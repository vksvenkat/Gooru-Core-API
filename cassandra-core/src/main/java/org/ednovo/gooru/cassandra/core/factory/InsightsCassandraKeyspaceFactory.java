/*******************************************************************************
 * InsightsCassandraKeyspaceFactory.java
 *  gooru-cassandra-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package org.ednovo.gooru.cassandra.core.factory;

import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Cluster;
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
final class InsightsCassandraKeyspaceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(SearchCassandraFactory.class);

	private static final String CONNECTION_POOL = "MyConnectionPool";

	private Keyspace keyspace;

	private AstyanaxContext<Keyspace> astyanaxContext;
	
	private AstyanaxContext<Cluster> astyanaxClusterContext;
	
	private CassandraSettingService settingService;
		
	private boolean instantiated;
	
	public InsightsCassandraKeyspaceFactory(CassandraSettingService settingService) {
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
			
			strategyOption = getSetting(InsightsCassandraConstant.STRATEGY_OPTIONS);
			strategyClass = getSetting(InsightsCassandraConstant.STRATEGY_CLASS);
			
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
		
			clusterName = getSetting(InsightsCassandraConstant.CLUSTER_NAME);
			cqlVersion = getSetting(InsightsCassandraConstant.CQL_VERSION);
			version = getSetting(InsightsCassandraConstant.VERSION);
			seed = getSetting(InsightsCassandraConstant.SEED);
		
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
	
	protected AstyanaxContext<Cluster> getCassandraClusterContext(boolean isPreferredSearch) {
		String clusterName = "";
		String cqlVersion = "";
		String version = "";
		String seed = "";
		if(!isPreferredSearch){
			clusterName = getSetting(SearchCassandraConstant.CLUSTER_NAME);
			cqlVersion = getSetting(SearchCassandraConstant.CQL_VERSION);
			version = getSetting(SearchCassandraConstant.VERSION);
			seed = getSetting(SearchCassandraConstant.SEED);
		} else {
			clusterName = getSetting(InsightsCassandraConstant.CLUSTER_NAME);
			cqlVersion = getSetting(InsightsCassandraConstant.CQL_VERSION);
			version = getSetting(InsightsCassandraConstant.VERSION);
			seed = getSetting(InsightsCassandraConstant.SEED);
		}
		return new AstyanaxContext.Builder()
		.forCluster(clusterName)
		.withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
		.setDiscoveryType(NodeDiscoveryType.TOKEN_AWARE)
		.setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
		.setCqlVersion(cqlVersion)
		.setTargetCassandraVersion(version)
		)
		.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool")
		.setPort(9160)
		.setMaxConns(100)
		.setMaxConnsPerHost(100)
		.setMaxBlockedThreadsPerHost(100)
		.setSeeds(seed)
		)
		.withConnectionPoolMonitor(new CountingConnectionPoolMonitor()).buildCluster(ThriftFamilyFactory.getInstance());
		
	}
	
	protected String getKeyspaceName() {
		
		/*	String[] keyspaces = getSetting(InsightsCassandraConstant.KEYSPACES).split(",");
			ImmutableMap.Builder<String, Object> keyspaceMap = ImmutableMap.<String, Object> builder();
			for (String keyspace : keyspaces) {
				String[] params = keyspace.split("=");
				keyspaceMap.put(params[0], params[1]);
			}
			//keyspace.createKeyspace(ImmutableMap.<String, Object> builder().put("keyspacee", keyspaceMap.build()).put("strategy_class", strategyClass).build());
			*/
			String keyspacePrefix = "";
			String KeyspaceSuffix = "";

				keyspacePrefix = getSetting(InsightsCassandraConstant.KEYSPACE_NAME_PREFIX);
				KeyspaceSuffix = getSetting(InsightsCassandraConstant.KEYSPACE_NAME_SUFFIX);
			return keyspacePrefix + "_" + (KeyspaceSuffix.equals(keyspacePrefix) ? InsightsCassandraConstant.KEYSPACE_NAME_SUFFIX.getDefaultValue() : KeyspaceSuffix);
		}
	
	private String getSetting(SearchCassandraConstant constant) {
		String value = getSettingService() != null ? getSettingService().getSetting(constant.getKey()) : constant.getDefaultValue();
		return value != null && value.length() > 0 ? value : constant.getDefaultValue();
	}

	private String getSetting(InsightsCassandraConstant constant) {
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
