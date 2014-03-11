/*******************************************************************************
 * InsightsCassandraFactory.java
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
