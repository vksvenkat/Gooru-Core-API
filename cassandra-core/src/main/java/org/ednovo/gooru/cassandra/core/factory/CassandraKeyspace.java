package org.ednovo.gooru.cassandra.core.factory;

import org.ednovo.gooru.cassandra.core.dao.CassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.CassandraDaoSupport;

import com.netflix.astyanax.Keyspace;

public interface CassandraKeyspace {
	
	boolean isInstantiated();

	Keyspace getKeyspace();
	
	CassandraDaoSupport<CassandraColumnFamily> get(String name);
	
	CassandraColumnFamily getColumnFamily(String name);
	
}
