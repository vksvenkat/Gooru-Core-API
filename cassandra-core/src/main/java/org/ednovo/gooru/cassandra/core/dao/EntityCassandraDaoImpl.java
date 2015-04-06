package org.ednovo.gooru.cassandra.core.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ednovo.gooru.cassandra.core.factory.InsightsCassandraFactory;
import org.ednovo.gooru.cassandra.core.factory.SearchCassandraFactory;
import org.ednovo.gooru.core.cassandra.model.IsEntityCassandraIndexable;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.CqlResult;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.util.RangeBuilder;

/**
 * @author Search Team
 * 
 */
public class EntityCassandraDaoImpl<M extends IsEntityCassandraIndexable> extends CassandraDaoSupport<EntityCassandraColumnFamily<M>> implements EntityCassandraDao<M> {

	protected static final ConsistencyLevel DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevel.CL_QUORUM;
	
	private static final int MAX_RETRY = 3; 

	public EntityCassandraDaoImpl() {

	}

	public EntityCassandraDaoImpl(SearchCassandraFactory factory,
			String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}
	
	public EntityCassandraDaoImpl(InsightsCassandraFactory factory,
			String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	@Override
	public String read(String key,
			String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).setConsistencyLevel(ConsistencyLevel.CL_QUORUM).getKey(key).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getStringValue() : null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void save(M model) {
		ColumnList<String> preValues = preSaveAction(model.getIndexId());
		getCF().getEntityManager().put(model);
		postSaveAction(model.getIndexId(), model.getRiFields(), preValues);
	}

	@Override
	public M read(String key) {
		try {
			return key != null ? getCF().getEntityManager().get(key) : null;
		} catch (Exception ex) {
			LOG.error("Cassandra read error : " + ex.getMessage());
			return null;
		}
	}

	@Override
	public void save(Collection<M> models,
			Collection<String> modelKeys,
			boolean skipRiCheck) {
		Rows<String, String> preValues = null;
		if (!skipRiCheck) {
			preValues = preSaveAction(modelKeys);
		}
		int success = 0;
		while (success <= 0 ) {
			try {
				getCF().getEntityManager().put(models);
				success = 1; 
			} catch (Exception ex) {
				success = -1;
				if(success < -5) {
					getLog().error("Error saving to cassandra", ex);
					break;
				}
			}
		}
		postSaveAction(models, preValues);
	}

	@Override
	public List<M> read(Collection<String> keys) {
		return getCF().getEntityManager().get(keys);
	}

	@Override
	public void delete(Collection<String> keys) {
		preDeleteAction(keys);
		getCF().getEntityManager().delete(keys);
	}

	protected void postSaveAction(Collection<M> models,
			Rows<String, String> preValues) {
		if (isReverseIndexersInstantiated()) {
			MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
			for (M model : models) {
				Row<String, String> row = preValues != null ? preValues.getRow(model.getIndexId()) : null;
				ColumnList<String> preValue = row != null ? row.getColumns() : null;
				Iterator<Entry<String, String>> iterator = getCF().getRiColumnSettings().entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> entry = iterator.next();
					postSaveAction(model.getIndexId(), entry, preValue, model.getRiFields(), mutationBatch);
				}
			}
			try {
				mutationBatch.execute();
			} catch (ConnectionException e) {
				LOG.error("Post Cassandra Save Failed For :  - ", e);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex) {
		return getModels(pageSize, key, startPoint, reverseIndex, false);
	}

	@Override
	public List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex,
			boolean reversed) {
		try {
			RangeBuilder rangeBuilder = new RangeBuilder().setLimit(pageSize).setReversed(reversed);
			if (startPoint != null) {
				rangeBuilder = rangeBuilder.setStart(startPoint);
			}
			RowQuery<String, String> query = getFactory().getKeyspace().prepareQuery(getRiColumnFamily()).setConsistencyLevel(ConsistencyLevel.CL_QUORUM).getKey(reverseIndex + key).withColumnRange(rangeBuilder.build());
			Collection<String> columns;
			if (!(columns = query.execute().getResult().getColumnNames()).isEmpty()) {
				return getCF().getEntityManager().get(columns);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer getColumnCount(String riFullKey) {
		try {
			if (isReverseIndexersInstantiated()) {
				return getFactory().getKeyspace().prepareQuery(getRiColumnFamily()).getKey(riFullKey).getCount().execute().getResult();
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public Map<String, Integer> getRowsColumnCount(String riKeyPrefix) {
		Collection<String> keys = getRiKeys(riKeyPrefix);
		Map<String, Integer> rows = new HashMap<String, Integer>();
		for (String key : keys) {
			rows.put(key, getColumnCount(key));
		}
		return rows;
	}

	@Override
	public List<M> getAll() {
		return getCF().getEntityManager().getAll();
	}

	@Override
	public void save(List<Map<String, Object>> entities,
			String prefix,
			boolean reset) {
		Map<String, Map<String, String>> postValues = new HashMap<String, Map<String, String>>(entities.size());
		Rows<String, String> preValues = null;
		MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
		for (Map<String, Object> entity : entities) {
			if (entity != null) {
				String key = (String) entity.get("_id");
				ColumnListMutation<String> mutation = mutationBatch.withRow(getCF().getColumnFamily(), key);
				Map<String, String> riFields = new HashMap<String, String>(getCF().getRiColumnSettings().size());
				build(entity, mutation, prefix, riFields);
				postValues.put(key, riFields);
			}
		}
		if (reset) {
			delete(postValues.keySet());
		} else {
			preValues = preSaveAction(postValues.keySet());
		}
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
		postSaveAction(postValues, preValues);
	}

	@Override
	public ColumnFamily<String, String> getRiColumnFamily() {
		return getCF().getRiColumnFamily();
	}

	@Override
	public Map<String, String> readViewsCount(String rowKeys){
		Map<String,String> resultMap = null;
		String cql = "select key,value from resource where column1='stas.viewsCount' and key in ('"+rowKeys+ "')";
		try {
			OperationResult<CqlResult<String, String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).withCql(cql).execute();
			
			if(result != null){
				resultMap = new HashMap<String, String>();
				for (Row<String, String> row : result.getResult().getRows()) {
					
					ColumnList<String> columns = row.getColumns();
					
					resultMap.put(columns.getStringValue ("key", null), columns.getStringValue ("value",  null));
				}        
			}

		} catch (ConnectionException e) {
			getLog().error("Error reading viewscount", e.getMessage());
		}
		return resultMap;
		
	}

	@Override
	public void updateViewsCount(Map<String, String> viewsData) {
		MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch().setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL);

		for(String key : viewsData.keySet()){
			String value = viewsData.get(key);
			if(value != null){
				
				mutationBatch.withRow(getCF().getColumnFamily(), key).putColumn("stas.viewsCount", value);
			}
		}	
		try {
			mutationBatch.execute();
		} catch (Exception ex) {
			getLog().error("Error saving to cassandra", ex);
		}
	}
	
	@Override
	public Long readAsLong(String rowKey, String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getLongValue() : null;
		} catch (Exception e) {
			return 0L;
		}
	}

	@Override
	public Integer readAsInteger(String rowKey, String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getIntegerValue() : null;
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
    public Rows<String, String> readWithKeyListColumnList(Collection<String> keys,Collection<String> columnList, int retryCount){
        
    	Rows<String, String> result = null;
    	try {
              result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily())
                    .setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL)
                    .getKeySlice(keys)
                    .withColumnSlice(columnList)
                    .execute()
                    .getResult();
        } catch (Exception e) {
        	if(e instanceof ConnectionException){
            	if(retryCount < MAX_RETRY){
            		retryCount++;
            		readWithKeyListColumnList(keys,columnList ,retryCount);
            	}else{
            		LOG.error("Read failed after "+ MAX_RETRY + "retry for resources : "+keys+ " columns : " +columnList);
            	}
        	}
        	else{
        		LOG.error("Read failed for resources : " + keys + "Exception : " + e);
        	}
        }
    	
    	return result;
    }

}

