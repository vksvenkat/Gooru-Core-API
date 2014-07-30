/**
 * 
 */
package org.ednovo.gooru.cassandra.core.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.factory.InsightsCassandraFactory;
import org.ednovo.gooru.cassandra.core.factory.SearchCassandraFactory;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.hibernate.engine.jdbc.ColumnNameCache;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.util.RangeBuilder;
import com.yammer.metrics.core.HealthCheck.Result;

/**
 * @author Search Team
 * 
 */
public class RawCassandraDaoImpl extends CassandraDaoSupport<CassandraColumnFamily> implements RawCassandraDao {

	protected static final ConsistencyLevel DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevel.CL_QUORUM;
	
	private static Map<String, RawCassandraDaoImpl> coreCassandraDaos = new HashMap<String, RawCassandraDaoImpl>();

	public RawCassandraDaoImpl() {

	}

	public RawCassandraDaoImpl(SearchCassandraFactory factory, String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	public RawCassandraDaoImpl(InsightsCassandraFactory factory, String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	@PostConstruct
	public void init() {
		super.init();
		coreCassandraDaos.put(getCF().getColumnFamilyName(), this);
	}

	@Override
	public void delete(String... keys) {
		preDeleteAction(keys);
		try {
			MutationBatch batch = getFactory().getKeyspace().prepareMutationBatch();
			for (String rowKey : keys) {
				batch.withRow(getCF().getColumnFamily(), rowKey).delete();
			}
			batch.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ColumnList<String> read(String rowKey) {
		try {
			OperationResult<ColumnList<String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).execute();
			ColumnList<String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static RawCassandraDao getCoreCassandraDao(String columnFamilyName) {
		return coreCassandraDaos.get(columnFamilyName);
	}

	@Override
	public void save(String rowKey, String column, String value) {
		try {
			if (value != null) {
				getFactory().getKeyspace().prepareColumnMutation(getCF().getColumnFamily(), rowKey, column).putValue(value, null).execute();
			} else {
				getFactory().getKeyspace().prepareColumnMutation(getCF().getColumnFamily(), rowKey, column).putEmptyColumn(null).execute();
			}
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String read(String rowKey, String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getStringValue() : null;
		} catch (NotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String rowKey, String column) {
		try {
			MutationBatch batch = getFactory().getKeyspace().prepareMutationBatch();
			batch.withRow(getCF().getColumnFamily(), rowKey).deleteColumn(column);
			batch.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Rows<String, String> read(Collection<String> keys) {
		try {
			OperationResult<Rows<String, String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKeySlice(keys).execute();
			Rows<String, String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ColumnFamily<String, String> getRiColumnFamily() {
		return getCF().getRiColumnFamily();
	}

	@Override
	public Rows<String, String> getAll() {
		try {
			OperationResult<Rows<String, String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getAllRows().execute();
			Rows<String, String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addIndexQueueEntry(String key, String columnPrefix, List<String> gooruOids, boolean isUpdate) {
		MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch().setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL);
		ColumnListMutation<String> mutation = mutationBatch.withRow(getCF().getColumnFamily(), key);
		for(String gooruOid : gooruOids){
			if(isUpdate){
				gooruOid = gooruOid.replace("_", "");
			}
			mutation.putColumnIfNotNull(columnPrefix+gooruOid, gooruOid);
		}	
		try {
			mutationBatch.execute();
		} catch (Exception ex) {
			getLog().error("Error saving to cassandra", ex);
		}
  }
	
	@Override
	public ColumnList<String> readIndexQueuedData(String rowKey, Integer limit, String columnPrefix){
		OperationResult<ColumnList<String>> result = null;
		try {
			result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily())
			.getKey(rowKey)
			.withColumnRange(new RangeBuilder()
			    .setStart(columnPrefix +"\u00000")
			    .setEnd(columnPrefix +"\uffff")
			    .setLimit(limit).build())
			.execute();
		} catch (ConnectionException e) {
			getLog().error("Error reading index queue data", e);
		}
		return result != null ? result.getResult() : null;
	}
	
	
}
