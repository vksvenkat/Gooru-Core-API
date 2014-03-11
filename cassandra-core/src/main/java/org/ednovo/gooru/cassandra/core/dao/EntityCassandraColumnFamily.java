/*******************************************************************************
 * EntityCassandraColumnFamily.java
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
package org.ednovo.gooru.cassandra.core.dao;

import javax.persistence.Entity;

import org.ednovo.gooru.core.cassandra.model.ReverseIndexColumnSetting;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.entitystore.DefaultEntityManager;
import com.netflix.astyanax.entitystore.EntityManager;

/**
 * @author SearchTeam
 * 
 */
public class EntityCassandraColumnFamily<M> extends CassandraColumnFamily {

	private EntityManager<M, String> entityManager;

	private final Class<M> clazz;

	public EntityCassandraColumnFamily(Class<M> clazz) {
		super();
		this.clazz = clazz;
	}

	public EntityCassandraColumnFamily(Class<M> clazz,
			ReverseIndexColumnSetting riColumnSettings) {
		super(riColumnSettings);
		this.clazz = clazz;
	}

	@Override
	public void init(Keyspace keyspace) {
		setColumnFamilyName(initColumnFamilyName(clazz));
		super.init(keyspace);
		if (keyspace != null) {
			entityManager = new DefaultEntityManager.Builder<M, String>().withEntityType(clazz).withKeyspace(keyspace).build();
		} else {
			getLog().error("Cassandra Mapper for " + getColumnFamilyName() + " : FAILED");
		}
	}

	public EntityManager<M, String> getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager<M, String> entityManager) {
		this.entityManager = entityManager;
	}

	public final String initColumnFamilyName(Class<M> clazz) {
		Entity entityAnnotation = clazz.getAnnotation(Entity.class);
		if (entityAnnotation == null) {
			throw new IllegalArgumentException("class is NOT annotated with @java.persistence.Entity: " + clazz.getName());
		}
		return entityAnnotation.name().toLowerCase();
	}

}
