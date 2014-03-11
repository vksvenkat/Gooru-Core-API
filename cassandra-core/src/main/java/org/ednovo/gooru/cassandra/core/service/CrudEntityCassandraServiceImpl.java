/*******************************************************************************
 * CrudEntityCassandraServiceImpl.java
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
package org.ednovo.gooru.cassandra.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.cassandra.core.CassandraIndexSrcBuilder;
import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;

/**
 * @author SearchTeam
 * 
 */
public abstract class CrudEntityCassandraServiceImpl<S extends IsCassandraIndexable, M extends Serializable> extends EntityCassandraServiceImpl<M> {

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
					throw new RuntimeException("Content not exist : " + key);
				}
				CassandraIndexSrcBuilder<S, M> builder = CassandraIndexSrcBuilder.get(source.getIndexType());
				M modelCio = builder.build(source);
				if (modelCio != null) {
					models.add(modelCio);
				}
				modelKeys.add(key);
			}
			save(models, modelKeys);
			return models;
		}

		return null;
	}

	protected abstract S fetchSource(String key);
}
