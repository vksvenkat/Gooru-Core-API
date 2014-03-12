/**
 * 
 */
package org.ednovo.gooru.cassandra.core.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.ednovo.gooru.cassandra.core.dao.CassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.CassandraDaoSupport;
import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.Keyspace;

/**
 * @author Search Team
 * 
 */
public abstract class SearchCassandraFactory extends CassandraFactory<SearchCassandraFactory> {

	protected static final Logger LOG = LoggerFactory.getLogger(SearchCassandraFactory.class);

	private SearchCassandraKeyspaceFactory keyspaceSupport;

	private final Map<String, CassandraColumnFamily> columnFamilies = new HashMap<String, CassandraColumnFamily>();
	
	private final Map<String, CassandraDaoSupport<CassandraColumnFamily>> daos = new HashMap<String, CassandraDaoSupport<CassandraColumnFamily>>();

	@PostConstruct
	public void init() {
		keyspaceSupport = new SearchCassandraKeyspaceFactory(getSettingService());
	}

	@PreDestroy
	public void cleanup() {
		keyspaceSupport.shutdown();
	}

	public boolean isInstantiated() {
		return keyspaceSupport.isInstantiated();
	}

	public Keyspace getKeyspace() {
		return keyspaceSupport.getKeyspace();
	}

	public void register(CassandraColumnFamily columnFamily) {
		columnFamily.init(getKeyspace());
		this.columnFamilies.put(columnFamily.getColumnFamilyName(), columnFamily);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends CassandraColumnFamily>void register(CassandraDaoSupport<T> acaDao) {
		acaDao.init();
		daos.put(acaDao.getColumnFamilyName(), (CassandraDaoSupport<CassandraColumnFamily>) acaDao);
	}
	
	public CassandraColumnFamily getColumnFamily(String name) {
		return columnFamilies.get(name);
	}
	
	public CassandraDaoSupport<CassandraColumnFamily> get(String name) {
		return daos.get(name);
	}

	public abstract CassandraSettingService getSettingService();

}