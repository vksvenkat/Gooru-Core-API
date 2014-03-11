/*******************************************************************************
 * EntityCassandraDaoImpl.java
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
package org.ednovo.gooru.cassandra.core.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import org.ednovo.gooru.cassandra.core.factory.InsightsCassandraFactory;
import org.ednovo.gooru.cassandra.core.factory.SearchCassandraFactory;
import org.ednovo.gooru.core.cassandra.model.IsEntityCassandraIndexable;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.util.RangeBuilder;

/**
 * @author Search Team
 * 
 */
public class EntityCassandraDaoImpl<M extends IsEntityCassandraIndexable> extends CassandraDaoSupport<EntityCassandraColumnFamily<M>> implements EntityCassandraDao<M> {

	public EntityCassandraDaoImpl() {

	}

	public EntityCassandraDaoImpl(SearchCassandraFactory factory,
			String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}
	
	public EntityCassandraDaoImpl(InsightsCassandraFactory factory,
			String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	@Override
	public String read(String key,
			String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(key).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getStringValue() : null;
		} catch (NotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(M model) {
		ColumnList<String> preValues = preSaveAction(model.getIndexId());
		getCF().getEntityManager().put(model);
		postSaveAction(model.getIndexId(), model.getRiFields(), preValues);
	}

	@Override
	public M read(String key) {
		try {
			return key != null ? getCF().getEntityManager().get(key) : null;
		} catch (PersistenceException ex) {
			return null;
		}
	}

	@Override
	public void save(Collection<M> models,
			Collection<String> modelKeys,
			boolean skipRiCheck) {
		Rows<String, String> preValues = null;
		if (!skipRiCheck) {
			preValues = preSaveAction(modelKeys);
		}
		int success = 0;
		while (success <= 0 ) {
			try {
				getCF().getEntityManager().put(models);
				success = 1; 
			} catch (Exception ex) {
				success = -1;
				if(success < -5) {
					getLog().error("Error saving to cassandra", ex);
					break;
				}
			}
		}
		postSaveAction(models, preValues);
	}

	@Override
	public List<M> read(Collection<String> keys) {
		return getCF().getEntityManager().get(keys);
	}

	@Override
	public void delete(Collection<String> keys) {
		preDeleteAction(keys);
		getCF().getEntityManager().delete(keys);
	}

	protected void postSaveAction(Collection<M> models,
			Rows<String, String> preValues) {
		if (isReverseIndexersInstantiated()) {
			MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
			for (M model : models) {
				Row<String, String> row = preValues != null ? preValues.getRow(model.getIndexId()) : null;
				ColumnList<String> preValue = row != null ? row.getColumns() : null;
				Iterator<Entry<String, String>> iterator = getCF().getRiColumnSettings().entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, String> entry = iterator.next();
					postSaveAction(model.getIndexId(), entry, preValue, model.getRiFields(), mutationBatch);
				}
			}
			try {
				mutationBatch.execute();
			} catch (ConnectionException e) {
				LOG.error("Post Cassandra Save Failed For :  - ", e);
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex) {
		return getModels(pageSize, key, startPoint, reverseIndex, false);
	}

	@Override
	public List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex,
			boolean reversed) {
		try {
			RangeBuilder rangeBuilder = new RangeBuilder().setLimit(pageSize).setReversed(reversed);
			if (startPoint != null) {
				rangeBuilder = rangeBuilder.setStart(startPoint);
			}
			RowQuery<String, String> query = getFactory().getKeyspace().prepareQuery(getRiColumnFamily()).getKey(reverseIndex + key).withColumnRange(rangeBuilder.build());
			Collection<String> columns;
			if (!(columns = query.execute().getResult().getColumnNames()).isEmpty()) {
				return getCF().getEntityManager().get(columns);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Integer getColumnCount(String riFullKey) {
		try {
			if (isReverseIndexersInstantiated()) {
				return getFactory().getKeyspace().prepareQuery(getRiColumnFamily()).getKey(riFullKey).getCount().execute().getResult();
			} else {
				return 0;
			}
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Integer> getRowsColumnCount(String riKeyPrefix) {
		Collection<String> keys = getRiKeys(riKeyPrefix);
		Map<String, Integer> rows = new HashMap<String, Integer>();
		for (String key : keys) {
			rows.put(key, getColumnCount(key));
		}
		return rows;
	}

	@Override
	public List<M> getAll() {
		return getCF().getEntityManager().getAll();
	}

	@Override
	public void save(List<Map<String, Object>> entities,
			String prefix,
			boolean reset) {
		Map<String, Map<String, String>> postValues = new HashMap<String, Map<String, String>>(entities.size());
		Rows<String, String> preValues = null;
		MutationBatch mutationBatch = getFactory().getKeyspace().prepareMutationBatch();
		for (Map<String, Object> entity : entities) {
			if (entity != null) {
				String key = (String) entity.get("_id");
				ColumnListMutation<String> mutation = mutationBatch.withRow(getCF().getColumnFamily(), key);
				Map<String, String> riFields = new HashMap<String, String>(getCF().getRiColumnSettings().size());
				build(entity, mutation, prefix, riFields);
				postValues.put(key, riFields);
			}
		}
		if (reset) {
			delete(postValues.keySet());
		} else {
			preValues = preSaveAction(postValues.keySet());
		}
		try {
			mutationBatch.execute();
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
		postSaveAction(postValues, preValues);
	}

	@Override
	public ColumnFamily<String, String> getRiColumnFamily() {
		return getCF().getRiColumnFamily();
	}

}
