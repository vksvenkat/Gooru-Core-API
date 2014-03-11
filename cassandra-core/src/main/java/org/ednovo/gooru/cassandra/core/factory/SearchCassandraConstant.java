/*******************************************************************************
 * SearchCassandraConstant.java
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
	
	KEYSPACE_NAME_PREFIX("cassandra.keyspacename.prefix", "gooru");

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
