/**
 * 
 */
package org.ednovo.gooru.cassandra.core.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.exception.CassandraException;
import org.ednovo.gooru.cassandra.core.factory.CassandraFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.common.base.Function;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.recipes.reader.AllRowsReader;

/**
 * @author SearchTeam
 * 
 */
public class CassandraDaoSupport<F extends CassandraColumnFamily> {
	
	protected static final Logger LOG = LoggerFactory.getLogger(CassandraDaoSupport.class);

	protected static final String SEPARATOR = "@";

	protected static final String EMPTY = "";

	protected static final String DOT = ".";
	
	private CassandraFactory<?> factory; 

	private F acaColumnFamily;

	private String columnFamilyName;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		acaColumnFamily = (F) getFactory().getColumnFamily(getColumnFamilyName());
	}
	
	public ColumnList<String> getColumns(String rowKey,
			Collection<String> fields) {
		if (fields != null && fields.size() > 0) {
			try {
				return getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).withColumnSlice(fields).execute().getResult();
			} catch (NotFoundException e) {
				throw new CassandraException(HttpStatus.NOT_FOUND, "Not Found Exception") ;
			} catch (ConnectionException e) {
				throw new CassandraException(HttpStatus.BAD_GATEWAY, "Unable to connect to cassandra cluster") ;
			}
		}
		return null;
	}

	public Rows<String, String> getColumns(Collection<String> keys, Collection<String> fields) {
		if (fields != null && fields.size() > 0) {
			try {
				return getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKeySlice(keys).withColumnSlice(fields).execute().getResult();
			} catch (NotFoundException e) {
				throw new CassandraException(HttpStatus.NOT_FOUND, "Not Found Exception") ;
			} catch (ConnectionException e) {
				throw new CassandraException(HttpStatus.BAD_GATEWAY, "Unable to connect to cassandra cluster") ;
			}
		}
		return null;
	}

	public void save(String key, Map<String, Object> entity) {
		save(key, entity, null, false);
	}

	public void save(String key, Map<String, Object> entity, String prefix, boolean reset) {
		if (reset) {
			delete(key);
		}
		if (entity != null) {
			MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
			ColumnListMutation<String> mutation = mutationBatch.withRow(getCF().getColumnFamily(), key);
			ColumnList<String> preValues = preSaveAction(key);
			Map<String, String> riColumns = null;
			if (isReverseIndexersInstantiated()) {
				riColumns = new HashMap<String, String>(getCF().getRiColumnSettings().size());
			}
			build(entity, mutation, prefix, riColumns);

			int success = 0;
			while (success <= 0) {
				try {
					mutationBatch.execute();
					success = 1;
				} catch (Exception ex) {
					success = -1;
					if (success < -5) {
						getLog().error("Error saving to cassandra", ex);
						break;
					}
				}
			}
			if (riColumns != null) {
				postSaveAction(key, riColumns, preValues);
			}
		}
	}

	public void delete(String id) {
		preDeleteAction(id);
		MutationBatch mb = getFactory().getKeyspace().prepareMutationBatch();
		mb.withRow(getCF().getColumnFamily(), id).delete();
		try {
			mb.execute();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	protected void preDeleteAction(Collection<String> rowKeys) {
		if ((isReverseIndexersInstantiated()) && (rowKeys != null)) {
			for (String rowKey : rowKeys) {
				preDeleteAction(rowKey);
			}

		}
	}

	protected void preDeleteAction(String... rowKeys) {
		if ((isReverseIndexersInstantiated()) && (rowKeys != null)) {
			for (String rowKey : rowKeys) {
				preDeleteAction(rowKey);
			}
		}

	}

	protected void preDeleteAction(String rowKey) {
		if (isReverseIndexersInstantiated()) {
			ColumnList<String> columnValues = getColumns(rowKey, getCF().getRiColumnSettings().values());
			Iterator<Entry<String, String>> iterator = getCF().getRiColumnSettings().entrySet().iterator();
			if (columnValues != null) {
				MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
				while (iterator.hasNext()) {
					Entry<String, String> entry = iterator.next();
					String columnName = entry.getValue();
					Column<String> column = columnValues.getColumnByName(columnName);
					if (column != null && column.hasValue()) {
						mutationBatch.withRow(getCF().getRiColumnFamily(), getRiKey(entry.getKey(), column.getStringValue())).deleteColumn(rowKey);
					}
				}
				try {
					mutationBatch.execute();
				} catch (Exception e) {
					LOG.error("Cassandra Save Failed For : '" + rowKey + "' - ", e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	protected void postSaveAction(Map<String, Map<String, String>> postValues, Rows<String, String> preValues) {
		if (isReverseIndexersInstantiated()) {
			MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
			Iterator<Entry<String, Map<String, String>>> entitiesIterator = postValues.entrySet().iterator();
			while (entitiesIterator.hasNext()) {
				Entry<String, Map<String, String>> entityEntry = entitiesIterator.next();
				Row<String, String> row = preValues.getRow(entityEntry.getKey());
				ColumnList<String> preValue = row.getColumns();
				Iterator<Entry<String, String>> iterator = getCF().getRiColumnSettings().entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> entry = iterator.next();
					postSaveAction(entityEntry.getKey(), entry, preValue, entityEntry.getValue(), mutationBatch);
				}
			}
			try {
				mutationBatch.execute();
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}
	}

	protected void postSaveAction(String key, Map<String, String> riColumns, ColumnList<String> preValues) {
		if (isReverseIndexersInstantiated()) {
			MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
			Iterator<Entry<String, String>> iterator = getCF().getRiColumnSettings().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				postSaveAction(key, entry, preValues, riColumns, mutationBatch);
			}
			try {
				mutationBatch.execute();
			} catch (Exception e) {
				LOG.error("Cassandra Save Failed For : '" + key + "' - ", e);
				throw new RuntimeException(e);
			}
		}
	}

	protected void postSaveAction(String key, Entry<String, String> entry, ColumnList<String> preValues, Map<String, String> riKeys, MutationBatch mutationBatch) {
		if (isReverseIndexersInstantiated() && riKeys.containsKey(entry.getValue())) {
			String postColumnValue = riKeys.get(entry.getValue());
			postSaveAction(preValues, entry, key, postColumnValue, mutationBatch);
		}
	}

	protected void postSaveAction(ColumnList<String> preValues, Entry<String, String> entry, String rowKey, String postColumnValue, MutationBatch mutationBatch) {
		Column<String> preColumn = preValues != null ? preValues.getColumnByName(entry.getValue()) : null;
		String preColumnValue = null;
		if (preColumn != null && preColumn.hasValue()) {
			preColumnValue = preColumn.getStringValue();
		}
		if (postColumnValue != null && !postColumnValue.trim().equals(EMPTY)) {
			if (preColumnValue == null) {
				mutationBatch.withRow(getCF().getRiColumnFamily(), getRiKey(entry.getKey(), postColumnValue)).putEmptyColumn(rowKey);
			} else if (preColumnValue != null && !preColumnValue.equals(postColumnValue)) {
				mutationBatch.withRow(getCF().getRiColumnFamily(), getRiKey(entry.getKey(), preColumnValue)).deleteColumn(rowKey);
				mutationBatch.withRow(getCF().getRiColumnFamily(), getRiKey(entry.getKey(), postColumnValue)).putEmptyColumn(rowKey);
			}
		} else if (preColumnValue != null && (postColumnValue == null || postColumnValue.trim().equals(EMPTY))) {
			mutationBatch.withRow(getCF().getRiColumnFamily(), getRiKey(entry.getKey(), preColumnValue)).deleteColumn(rowKey);
		}
	}

	public static String getRiKey(String prefix, String key) {
		return prefix + SEPARATOR + key;
	}

	public void build(Map<String, Object> entity, ColumnListMutation<String> mutation, String prefix, Map<String, String> riFields) {
		Iterator<Entry<String, Object>> iterator = entity.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();

			String key = (prefix != null && prefix.length() > 0) ? (prefix + DOT + entry.getKey()) : entry.getKey();
			Object value = entry.getValue();
			if (riFields != null && getCF().getRiColumnSettings() != null && getCF().getRiColumnSettings().keySet().contains(key)) {
				riFields.put(key, value != null ? value.toString() : null);
			}
			if (value == null) {
				mutation.putEmptyColumn(key);
			} else if (value instanceof Map) {
				build((Map<String, Object>) value, mutation, key, riFields);
			} else if (value instanceof List) {
				build((List<Object>) value, mutation, key);
			} else if (value instanceof String) {
				mutation.putColumn(key, (String) value);
			} else if (value instanceof Integer) {
				mutation.putColumn(key, (Integer) value);
			} else if (value instanceof Double) {
				mutation.putColumn(key, (Double) value);
			}
		}
	}

	protected void build(List<Object> entity, ColumnListMutation<String> mutation, String prefix) {
		if (entity != null) {
			for (Object entry : entity) {
				String key = (prefix != null && prefix.length() > 0) ? (prefix + DOT + entry.toString()) : entry.toString();
				mutation.putEmptyColumn(key);
			}
		}
	}

	public Collection<String> getRiKeys(final String riKeyPrefix) {
		try {
			final Collection<String> keys = new ArrayList<String>();
			new AllRowsReader.Builder<String, String>(getFactory().getKeyspace(), getCF().getRiColumnFamily()).withColumnRange(null, null, false, 0).forEachRow(new Function<Row<String, String>, Boolean>() {
				@Override
				public Boolean apply(Row<String, String> row) {
					if (row.getKey().startsWith(riKeyPrefix + SEPARATOR)) {
						keys.add(row.getKey());
					}
					return true;
				}
			}).build().call();
			return keys;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColumnList<String> preSaveAction(String rowKey) {
		return isReverseIndexersInstantiated() ? getColumns(rowKey, getCF().getRiColumnSettings().values()) : null;
	}

	protected Rows<String, String> preSaveAction(Collection<String> rowKeys) {
		return isReverseIndexersInstantiated() ? getColumns(rowKeys, getCF().getRiColumnSettings().values()) : null;
	}

	/**
	 * Read record querying for 
	 * @param columnFamilyName
	 * @param value = where condition value
	 * @return key
	 */
	public Rows<String, String> getRows(String field, String value) {

		if (field != null && value != null) {
			try {
				Rows<String, String> rows = getFactory().getKeyspace()
				.prepareQuery(getCF().getColumnFamily()).searchWithIndex()
				.addExpression()
				.whereColumn(field).equals().value(value)
				.execute().getResult();
				return rows;
			} catch (NotFoundException e) {
				throw new CassandraException(HttpStatus.NOT_FOUND, "Not Found Exception") ;
			} catch (ConnectionException e) {
				throw new CassandraException(HttpStatus.BAD_GATEWAY, "Unable to connect to cassandra cluster") ;
			}
		}
		return null;
	}
	
	public ColumnList<String> getColumns(String rowKey) {
		try {
			ColumnList<String> record = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).execute().getResult();
			return record;
		} catch (NotFoundException e) {
			throw new CassandraException(HttpStatus.NOT_FOUND, "Not Found Exception") ;
		} catch (ConnectionException e) {
			throw new CassandraException(HttpStatus.BAD_GATEWAY, "Unable to connect to cassandra cluster") ;
		}
	}
	
	public boolean isReverseIndexersInstantiated() {
		return getCF().getRiColumnSettings() != null && getCF().getRiColumnSettings().size() > 0;
	}

	public void setColumnFamilyName(String columnFamilyName) {
		this.columnFamilyName = columnFamilyName;
	}

	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	public Keyspace getKeyspace() {
		return getFactory().getKeyspace();
	}

	public final F getCF() {
		return acaColumnFamily;
	}

	public static Logger getLog() {
		return LOG;
	}

	public void setFactory(CassandraFactory<?> factory) {
		this.factory = factory;
	}

	public CassandraFactory<?> getFactory() {
		return factory;
	}

}
