/**
 * 
 */
package org.ednovo.gooru.cassandra.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author SearchTeam
 * 
 */
public abstract class CachedCassandraServiceImpl implements CachedCassandraService {

	@Override
	public void delete(String id) {
		ColumnList<String> entry = getCassandraDao().read(id);
		if (entry != null) {
			for (String columnName : entry.getColumnNames()) {
				getCacheService().deleteKey(getKeyPrefix() + id + columnName);
			}
			getCassandraDao().delete(id);
		}
	}

	@Override
	public void delete(String rowKey,
			String column) {
		getCassandraDao().delete(rowKey, column);
		try {
			getCacheService().deleteKey(getKeyPrefix() + rowKey + column);
		} catch (Exception ex) {
		}
	}

	@Override
	public void save(String rowKey,
			String column,
			String value) {
		getCassandraDao().save(rowKey, column, value);
		try {
			getCacheService().putValue(getKeyPrefix() + rowKey + column, value);
		} catch (Exception ex) {
		}
	}

	@Override
	public ColumnList<String> read(String key) {
		return getCassandraDao().read(key);
	}

	@Override
	public Rows<String, String> read(List<String> keys) {
		return getCassandraDao().read(keys);
	}

	@Override
	public String read(String rowKey,
			String column) {
		String value = null;
		try {
			value = getCacheService().getValue(getKeyPrefix() + rowKey + column);
		} catch (Exception ex) {
		}
		if (value == null) {
			value = getCassandraDao().read(rowKey, column);
			if (value != null) {
				try {
					getCacheService().putValue(getKeyPrefix() + rowKey + column, value);
				} catch (Exception ex) {
				}
			}
		}
		return value;
	}

	@Override
	public void save(String key,
			Map<String, Object> entity) {
		getCassandraDao().save(key, entity);
		if (entity != null) {
			Iterator<Map.Entry<String, Object>> iterator = entity.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				if (entry.getValue() instanceof String) {
					getCacheService().putValue(getKeyPrefix() + key + entry.getKey(), (String) entry.getValue());
				}
			}
		}
	}
	
	protected abstract RawCassandraDao getCassandraDao();
	
	protected abstract CassandraCacheService getCacheService();
	
	protected abstract String getKeyPrefix();

}
