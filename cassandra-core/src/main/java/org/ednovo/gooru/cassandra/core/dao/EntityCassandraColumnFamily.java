/**
 * 
 */
package org.ednovo.gooru.cassandra.core.dao;

import javax.persistence.Entity;

import org.ednovo.gooru.cassandra.custom.entity.GooruDefaultEntityManager;
import org.ednovo.gooru.core.cassandra.model.ReverseIndexColumnSetting;

import com.netflix.astyanax.Keyspace;
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
			entityManager = new GooruDefaultEntityManager.Builder<M, String>().withEntityType(clazz).withKeyspace(keyspace).build();
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
