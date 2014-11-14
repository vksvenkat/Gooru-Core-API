package org.ednovo.gooru.cassandra.core.factory;

/**
 * @author Search Team
 * 
 */
enum SearchCassandraConstant {

	CQL_VERSION("cassandra.cqlversion", "3.0.0"),

	VERSION("cassandra.version", "1.2"),

	CLUSTER_NAME("cassandra.clustername", "gooru-cluster"),

	SEED("cassandra.seed", "localhost:9160"),

	KEYSPACE_NAME_SUFFIX("cassandra.keyspacename", "local"),
	
	STRATEGY_OPTIONS("cassandra.strategyOptions", "replication_factor=1"),
	
	STRATEGY_CLASS("cassandra.strategyClass", "SimpleStrategy"),
	
	KEYSPACE_NAME_PREFIX("cassandra.keyspacename.prefix", "event");

	String defaultValue;

	String key;

	private SearchCassandraConstant(String key,
			String defaultValue) {
		this.defaultValue = defaultValue;
		this.key = key;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

}