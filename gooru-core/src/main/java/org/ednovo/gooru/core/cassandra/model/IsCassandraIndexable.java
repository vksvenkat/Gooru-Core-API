/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.io.Serializable;

/**
 * 
 * @author SearchTeam
 */
public interface IsCassandraIndexable extends Serializable {
	
	String getIndexId();
	
	String getIndexType();
}
