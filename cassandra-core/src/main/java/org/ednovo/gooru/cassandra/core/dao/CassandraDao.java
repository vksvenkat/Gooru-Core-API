/**
 * 
 */
package org.ednovo.gooru.cassandra.core.dao;

import java.util.Collection;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.model.ColumnFamily;

/**
 * @author Search Team
 * 
 */
public interface CassandraDao {

	String read(String rowKey, String column);
	
	void delete(String rowKey);
	
	Collection<String> getRiKeys(String reverseIndexName);
	
	ColumnFamily<String, String> getRiColumnFamily();
	
	Keyspace getKeyspace();
	

}
