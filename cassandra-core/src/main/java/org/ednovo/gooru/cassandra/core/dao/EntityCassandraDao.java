package org.ednovo.gooru.cassandra.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.netflix.astyanax.model.ColumnList;

/**
 * @author Search Team
 * 
 */
public interface EntityCassandraDao<M> extends CassandraDao {
	
	void save(M model);

	void save(Collection<M> models,
			Collection<String> modelKeys, boolean skipRiUpdate);
	
	Integer getColumnCount(String riFullKey);

	M read(String key);

	List<M> read(Collection<String> keys);
	
	void delete(Collection<String> keys);

	Map<String,Integer> getRowsColumnCount(String reverseIndexName);

	void save(String key,
			Map<String, Object> entity,
			String prefix,
			boolean reset);
	
	void save(List<Map<String, Object>> entity,
			String prefix,
			boolean reset);
	
	List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex,
			boolean reversed);
	
	List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex);
	
	List<M> getAll();
	
	ColumnList<String> getColumns(String rowKey,
			Collection<String> fields);
	
}
