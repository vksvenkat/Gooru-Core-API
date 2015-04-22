/**
 * 
 */
package org.ednovo.gooru.cassandra.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.cassandra.core.CassandraIndexSrcBuilder;
import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;
import org.ednovo.gooru.core.exception.NotFoundException;


/**
 * @author SearchTeam
 * 
 */
public abstract class CrudEntityCassandraServiceImpl<S extends IsCassandraIndexable, M extends Serializable> extends EntityCassandraServiceImpl<M> {

	
	boolean isFieldIndexingIsEnabled=false;
	
	@Override
	public M save(String id) {
		S source = fetchSource(id);
		if (source == null) {
			throw new RuntimeException("Id : " + id + " doesn't exist in Cassandra ");
		}
		CassandraIndexSrcBuilder<S, M> builder = CassandraIndexSrcBuilder.get(source.getIndexType());
		M modelCio = builder.build(source);
		if (modelCio != null) {
			getCassandraDao().save(modelCio);
		}
		return modelCio;
	}

	@Override
	public List<M> save(String... ids) {
		if (ids != null) {
			List<M> models = new ArrayList<M>();
			Collection<String> modelKeys = new ArrayList<String>();
			for (String key : ids) {
				S source = fetchSource(key);
				if(source == null) {
					throw new NotFoundException("Content not exist : " + key);
				}
				
				if(source.getIndexType().equalsIgnoreCase("resource")||source.getIndexType().equalsIgnoreCase("question")&&isFieldIndexingIsEnabled) {
				CassandraIndexSrcBuilder<S, M> builder = CassandraIndexSrcBuilder.get("resource_fields");
				M modelCio = builder.build(source);
				if (modelCio != null) {
					models.add(modelCio);
				}
				}
				else {
				CassandraIndexSrcBuilder<S, M> builder = CassandraIndexSrcBuilder.get(source.getIndexType());
				M modelCio = builder.build(source);
				if (modelCio != null) {
					models.add(modelCio);
				}
				}	
				modelKeys.add(key);
			}
			save(models, modelKeys);
			//saveasrow(models,modelKeys);
			return models;
		}

		return null;
	}

	
	
	protected abstract S fetchSource(String key);
}
