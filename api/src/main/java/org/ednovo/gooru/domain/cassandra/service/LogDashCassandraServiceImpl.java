package org.ednovo.gooru.domain.cassandra.service;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.factory.SearchCassandraKeyspaceFactory;
import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;

@Service
public class LogDashCassandraServiceImpl  implements LogDashCassandraService {
	
	protected static final Logger logger = LoggerFactory.getLogger(LogDashCassandraServiceImpl.class);

   private  Keyspace keyspace;
   
   private static final String LIVE_DASHBOARD ="live_dashboard";

   private static final ConsistencyLevel DEFAULT_CONSISTENCY_LEVEL = ConsistencyLevel.CL_QUORUM;
   
   private static final int MAX_RETRY = 3; 

  
   @Autowired
   private CassandraSettingService cassandraSettingService;
  
   @PostConstruct
   protected void init () {
	   keyspace = new SearchCassandraKeyspaceFactory(getCassandraSettingService()).getKeyspace();
	}


	@Override
	public Rows<String, String> readWithKeyListColumnList(Collection<String> ids, Collection<String> field, int retryCount) {
		ColumnFamily<String, String> CF_LIVE_DASHBOARD =  new ColumnFamily<String, String>(LIVE_DASHBOARD,StringSerializer.get(),StringSerializer.get());	
		Rows<String,String> result = null;
		try {
			result = this.getKeyspace().prepareQuery(CF_LIVE_DASHBOARD).setConsistencyLevel(DEFAULT_CONSISTENCY_LEVEL)
                    .getKeySlice(ids)
                    .withColumnSlice(field)
                    .execute()
                    .getResult();
			  
			} catch (ConnectionException e) {
				if (e instanceof ConnectionException) {
	            	if(retryCount < MAX_RETRY) {
	            		retryCount++;
	            		readWithKeyListColumnList(ids,field ,retryCount);
	            	}else{
	            		logger.error("Read failed after "+ MAX_RETRY + "retry for resources : "+ids+ " columns : " +field);
	            	}
	        	}
	        	else{
	        		logger.error("Read failed for resources : " + ids + "Exception : " + e);
	        	}
				 
		   }
		return result;		   	
		
	}
   
 
       
    public Keyspace getKeyspace() {
    	return keyspace;
    }
    
    public CassandraSettingService getCassandraSettingService() {
    	return cassandraSettingService;
    }
    



}
