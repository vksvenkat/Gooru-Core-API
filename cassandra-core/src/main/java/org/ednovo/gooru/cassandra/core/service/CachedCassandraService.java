/**
 * 
 */
package org.ednovo.gooru.cassandra.core.service;

import java.util.List;
import java.util.Map;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;


/**
 * @author SearchTeam
 * 
 */
public interface CachedCassandraService {
	
	void delete(String id);
	
	void delete(String rowKey, String column);
	
	ColumnList<String> read(String id);
	
	Rows<String, String> read(List<String> keys);
	
	String read(String rowKey, String column);
	
	void save(String rowKey, String column, String value);
	
	void save(String key,
			Map<String, Object> entity);

}
