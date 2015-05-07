/**
 * 
 */
package org.ednovo.gooru.cassandra.core.factory;

import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * @author SearchTeam
 *
 */
final class SearchCassandraKeyspaceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SearchCassandraFactory.class);

	private static final String CONNECTION_POOL = "MyConnectionPool";

	private Keyspace keyspace;

	private AstyanaxContext<Keyspace> astyanaxContext;
		
	private CassandraSettingService settingService;
		
	private boolean instantiated;
	
	public SearchCassandraKeyspaceFactory(CassandraSettingService settingService) {
		this.settingService = settingService;
		try {
			this.astyanaxContext = getCassandraContext();
			this.astyanaxContext.start();
			this.keyspace = this.astyanaxContext.getClient();
			createKeySpaceIfNotExist();
			this.keyspace.describeKeyspace();
			instantiated = true;
		} catch (Throwable e) {
			LOG.error("Could not connect to cassandra : ", e);
		}
	}
	
	protected void createKeySpaceIfNotExist() throws ConnectionException {
		try {
			this.keyspace.describeKeyspace();
		} catch (Exception ex) {
			String strategyOption = "";
			String strategyClass = "";			
			
			strategyOption = getSetting(SearchCassandraConstant.STRATEGY_OPTIONS);
			strategyClass = getSetting(SearchCassandraConstant.STRATEGY_CLASS);
			
			String[] strategyOptions = strategyOption.split(",");
			ImmutableMap.Builder<String, Object> strategyMap = ImmutableMap.<String, Object> builder();
			for (String strategy : strategyOptions) {
				String[] params = strategy.split("=");
				strategyMap.put(params[0], params[1]);
			}
			keyspace.createKeyspace(ImmutableMap.<String, Object> builder().put("strategy_options", strategyMap.build()).put("strategy_class", strategyClass).build());
			LOG.warn("Cassandra Keyspace : " + keyspace.getKeyspaceName() + " doesn't exist, Created!");
		}
	}
	
	protected AstyanaxContext<Keyspace> getCassandraContext() {
		String clusterName = "";
		String cqlVersion = "";
		String version = "";
		String seed = "";
		
			clusterName = getSetting(SearchCassandraConstant.CLUSTER_NAME);
			cqlVersion = getSetting(SearchCassandraConstant.CQL_VERSION);
			version = getSetting(SearchCassandraConstant.VERSION);
			seed = getSetting(SearchCassandraConstant.SEED);
		
		return new AstyanaxContext.Builder()
				.forCluster(clusterName)
				.forKeyspace(getKeyspaceName())
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl()
						.setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
						.setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
						.setCqlVersion(cqlVersion)
						.setTargetCassandraVersion(version))
				.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(CONNECTION_POOL)
				.setMaxConnsPerHost(100)
				.setSeeds(seed))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
				.buildKeyspace(ThriftFamilyFactory.getInstance());

	}
	
	protected String getKeyspaceName() {

			String keyspacePrefix = "";
			String KeyspaceSuffix = "";

				keyspacePrefix = getSetting(SearchCassandraConstant.KEYSPACE_NAME_PREFIX);
				KeyspaceSuffix = getSetting(SearchCassandraConstant.KEYSPACE_NAME_SUFFIX);
			return keyspacePrefix + "_" + (KeyspaceSuffix.equals(keyspacePrefix) ? SearchCassandraConstant.KEYSPACE_NAME_SUFFIX.getDefaultValue() : KeyspaceSuffix);
		}

	private String getSetting(SearchCassandraConstant constant) {
		String value = getSettingService() != null ? getSettingService().getSetting(constant.getKey()) : constant.getDefaultValue();
		return value != null && value.length() > 0 ? value : constant.getDefaultValue();
	}
	
	public boolean isInstantiated() {
		return instantiated;
	}
	
	public Keyspace getKeyspace() {
		return keyspace;
	}
	
	public CassandraSettingService getSettingService() {
		return settingService;
	}
	
	public void shutdown() {
		if (this.astyanaxContext != null) {
			this.astyanaxContext.shutdown();
		}
	}

}
