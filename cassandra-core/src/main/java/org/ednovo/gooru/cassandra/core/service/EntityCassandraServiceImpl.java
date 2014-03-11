/*******************************************************************************
 * EntityCassandraServiceImpl.java
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.cassandra.core.dao.EntityCassandraDao;

import com.netflix.astyanax.model.ColumnList;

/**
 * @author SearchTeam
 * 
 */
public abstract class EntityCassandraServiceImpl<M extends Serializable> implements EntityCassandraService<String, M> {

	@Override
	public void delete(String id) {
		getCassandraDao().delete(id);
	}

	@Override
	public void delete(String... ids) {
		for (String id : ids) {
			delete(id);
		}
	}
	
	@Override
	public String get(String key,
			String column) {
		return getCassandraDao().read(key, column);
	}

	@Override
	public M read(String key) {
		return getCassandraDao().read(key);
	}

	@Override
	public List<M> read(List<String> keys) {
		return getCassandraDao().read(keys);
	}

	@Override
	public void save(Collection<M> models,
			Collection<String> modelKeys) {
		getCassandraDao().save(models, modelKeys, false);

	}

	@Override
	public List<M> getAll() {
		return getCassandraDao().getAll();
	}

	@Override
	public void save(String key,
			Map<String, Object> entity,
			String prefix,
			boolean reset) {
		getCassandraDao().save(key, entity, prefix, reset);
	}

	protected abstract EntityCassandraDao<M> getCassandraDao();

	@Override
	public void save(M model) {
		getCassandraDao().save(model);
	}

	@Override
	public ColumnList<String> getColumns(String rowKey,
			Collection<String> fields){
		return getCassandraDao().getColumns(rowKey, fields);
	}
	
}
