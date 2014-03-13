/**
 * 
 */
package org.ednovo.gooru.cassandra.core.service;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author SearchTeam
 * 
 */
public abstract class CassandraServiceImpl implements CassandraService {

	@Override
	public void delete(String id) {
		getCassandraDao().delete(id);
	}

	@Override
	public void delete(String... ids) {
		getCassandraDao().delete(ids);
	}

	@Override
	public void delete(String rowKey, String column) {
		getCassandraDao().delete(rowKey, column);
	}

	@Override
	public void save(String rowKey, String column, String value) {
		getCassandraDao().save(rowKey, column, value);
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
	public String read(String rowKey, String column) {
		return getCassandraDao().read(rowKey, column);
	}

	@Override
	public void save(String key, Map<String, Object> entity, String prefix, boolean reset) {
		getCassandraDao().save(key, entity, prefix, reset);
	}

	protected abstract RawCassandraDao getCassandraDao();

	@Override
	public void save(String key, Map<String, Object> entity) {
		getCassandraDao().save(key, entity);

	}

	@Override
	public Rows<String, String> getAll() {
		return getCassandraDao().getAll();
	}

}
