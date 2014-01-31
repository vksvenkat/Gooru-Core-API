package org.ednovo.gooru.cassandra.core.factory;


/**
 * @author Search Team
 * 
 */
enum InsightsCassandraConstant {

	CQL_VERSION("insights.cassandra.cqlversion", "3.0.0"),

	VERSION("insights.cassandra.version", "1.2"),

	CLUSTER_NAME("insights.cassandra.clustername", "gooru-cluster"),

	SEED("insights.cassandra.seed", "localhost:9160"),

	KEYSPACE_NAME_SUFFIX("insights.cassandra.keyspacename", "search"),
	
	STRATEGY_OPTIONS("insights.cassandra.strategyOptions", "replication_factor=1"),
	
	STRATEGY_CLASS("insights.cassandra.strategyClass", "SimpleStrategy"),
	
	KEYSPACE_NAME_PREFIX("insights.cassandra.keyspacename.prefix", "gooru");
	
	/*KEYSPACES("cassandra.keyspaces","gooru_insights,gooru_search"),
	
	RETRY_EXPONENTIAL_BASE_MS("cassandra.retry.exponential.base.ms","10"),
	
	RETRY_MAX_ATTEMPTS("cassanrda.retry.max.attempts","3");*/

	String defaultValue;

	String key;

	private InsightsCassandraConstant(String key,
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