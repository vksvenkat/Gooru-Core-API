/**
 * 
 */
package org.ednovo.gooru.cassandra.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * @author SearchTeam
 * 
 */
public abstract class CassandraIndexSrcBuilder<I extends Serializable, O> implements IsIndexSrcBuilder<I, O> {

	private static final Map<String, CassandraIndexSrcBuilder<?, ?>> indexBuilders = new HashMap<String, CassandraIndexSrcBuilder<?, ?>>();
	
	@PostConstruct
	protected void init() {
		indexBuilders.put(getName(), this);
	}
	
	@SuppressWarnings("unchecked")
	public static <I extends Serializable, O> CassandraIndexSrcBuilder<I, O> get(String builderKey) {
		return (CassandraIndexSrcBuilder<I, O>) indexBuilders.get(builderKey);
	}
	
}
