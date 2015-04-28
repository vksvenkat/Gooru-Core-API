/**
 * 
 */
package org.ednovo.gooru.cassandra.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author SearchTeam
 * 
 */
public interface EntityCassandraService<K, M extends Serializable> {

	void delete(String... ids);

	M save(K id);

	List<M> save(K... ids);

	void save(M model);

	void save(Collection<M> models, Collection<String> modelKeys);

	void delete(K id);

	M read(K key);

	String get(String key, String column);

	List<M> read(List<K> keys);

	List<M> getAll();

	void save(String key, Map<String, Object> entity, String prefix, boolean reset);

	ColumnList<String> getColumns(String rowKey, Collection<String> fields);

	void updateViewsCount(Map<String, String> viewsData);

	Map<String, String> readViewsCount(String rowKeys);
	
	public Long getLong(String key, String column);
	
	public Rows<String, String> readWithKeyListColumnList(Collection<String> keys,Collection<String> columnList, int retryCount);

}
