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

public abstract class InsightsCassandraFactory extends CassandraFactory<InsightsCassandraFactory>{


	protected static final Logger LOG = LoggerFactory.getLogger(SearchCassandraFactory.class);
	
	private InsightsCassandraKeyspaceFactory insightsKeyspaceSupport;

	private final Map<String, CassandraColumnFamily> columnFamilies = new HashMap<String, CassandraColumnFamily>();
	
	private final Map<String, CassandraDaoSupport<CassandraColumnFamily>> daos = new HashMap<String, CassandraDaoSupport<CassandraColumnFamily>>();

	
	@PostConstruct
	public void initialize() {
		insightsKeyspaceSupport = new InsightsCassandraKeyspaceFactory(getSettingService());
	}

	@PreDestroy
	public void cleanup() {
		insightsKeyspaceSupport.shutdown();
	}

	public boolean isInstantiated() {
		return insightsKeyspaceSupport.isInstantiated();
	}

	public Keyspace getKeyspace() {
		return insightsKeyspaceSupport.getKeyspace();
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
