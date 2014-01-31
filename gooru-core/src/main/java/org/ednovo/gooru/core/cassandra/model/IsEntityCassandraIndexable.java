/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.util.Map;



/**
 * @author SearchTeam
 *
 */
public interface IsEntityCassandraIndexable extends IsCassandraIndexable {
	
	Map<String,String> getRiFields();

}
