package org.ednovo.gooru.cassandra.core.factory;

import org.ednovo.gooru.cassandra.core.dao.CassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.CassandraDaoSupport;

import com.netflix.astyanax.Keyspace;

public interface CassandraKeyspace {
	
	public boolean isInstantiated();

	public Keyspace getKeyspace();
	
	public CassandraDaoSupport<CassandraColumnFamily> get(String name);
	
	public CassandraColumnFamily getColumnFamily(String name);
	
}
