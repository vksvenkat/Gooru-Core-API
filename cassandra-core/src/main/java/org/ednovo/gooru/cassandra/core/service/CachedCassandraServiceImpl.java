/*******************************************************************************
 * CachedCassandraServiceImpl.java
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author SearchTeam
 * 
 */
public abstract class CachedCassandraServiceImpl implements CachedCassandraService {

	@Override
	public void delete(String id) {
		ColumnList<String> entry = getCassandraDao().read(id);
		if (entry != null) {
			for (String columnName : entry.getColumnNames()) {
				getCacheService().deleteKey(getKeyPrefix() + id + columnName);
			}
			getCassandraDao().delete(id);
		}
	}

	@Override
	public void delete(String rowKey,
			String column) {
		getCassandraDao().delete(rowKey, column);
		try {
			getCacheService().deleteKey(getKeyPrefix() + rowKey + column);
		} catch (Exception ex) {
		}
	}

	@Override
	public void save(String rowKey,
			String column,
			String value) {
		getCassandraDao().save(rowKey, column, value);
		try {
			getCacheService().putValue(getKeyPrefix() + rowKey + column, value);
		} catch (Exception ex) {
		}
	}

	@Override
	public ColumnList<String> read(String key) {
		return getCassandraDao().read(key);
	}

	@Override
	public Rows<String, String> read(List<String> keys) {
		return getCassandraDao().read(keys);
	}

	@Override
	public String read(String rowKey,
			String column) {
		String value = null;
		try {
			value = getCacheService().getValue(getKeyPrefix() + rowKey + column);
		} catch (Exception ex) {
		}
		if (value == null) {
			value = getCassandraDao().read(rowKey, column);
			if (value != null) {
				try {
					getCacheService().putValue(getKeyPrefix() + rowKey + column, value);
				} catch (Exception ex) {
				}
			}
		}
		return value;
	}

	@Override
	public void save(String key,
			Map<String, Object> entity) {
		getCassandraDao().save(key, entity);
		if (entity != null) {
			Iterator<Map.Entry<String, Object>> iterator = entity.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, Object> entry = iterator.next();
				if (entry.getValue() instanceof String) {
					getCacheService().putValue(getKeyPrefix() + key + entry.getKey(), (String) entry.getValue());
				}
			}
		}
	}
	
	protected abstract RawCassandraDao getCassandraDao();
	
	protected abstract CassandraCacheService getCacheService();
	
	protected abstract String getKeyPrefix();

}
