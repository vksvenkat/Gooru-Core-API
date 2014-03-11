/*******************************************************************************
 * RawCassandraDaoImpl.java
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.factory.SearchCassandraFactory;
import org.ednovo.gooru.cassandra.core.factory.InsightsCassandraFactory;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author Search Team
 * 
 */
public class RawCassandraDaoImpl extends CassandraDaoSupport<CassandraColumnFamily> implements RawCassandraDao {

	private static Map<String, RawCassandraDaoImpl> coreCassandraDaos = new HashMap<String, RawCassandraDaoImpl>();

	public RawCassandraDaoImpl() {

	}

	public RawCassandraDaoImpl(SearchCassandraFactory factory, String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	public RawCassandraDaoImpl(InsightsCassandraFactory factory, String columnFamilyName) {
		setFactory(factory);
		setColumnFamilyName(columnFamilyName);
	}

	@PostConstruct
	public void init() {
		super.init();
		coreCassandraDaos.put(getCF().getColumnFamilyName(), this);
	}

	@Override
	public void delete(String... keys) {
		preDeleteAction(keys);
		try {
			MutationBatch batch = getFactory().getKeyspace().prepareMutationBatch();
			for (String rowKey : keys) {
				batch.withRow(getCF().getColumnFamily(), rowKey).delete();
			}
			batch.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ColumnList<String> read(String rowKey) {
		try {
			OperationResult<ColumnList<String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).execute();
			ColumnList<String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static RawCassandraDao getCoreCassandraDao(String columnFamilyName) {
		return coreCassandraDaos.get(columnFamilyName);
	}

	@Override
	public void save(String rowKey, String column, String value) {
		try {
			if (value != null) {
				getFactory().getKeyspace().prepareColumnMutation(getCF().getColumnFamily(), rowKey, column).putValue(value, null).execute();
			} else {
				getFactory().getKeyspace().prepareColumnMutation(getCF().getColumnFamily(), rowKey, column).putEmptyColumn(null).execute();
			}
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String read(String rowKey, String column) {
		try {
			Column<String> cfColumn = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKey(rowKey).getColumn(column).execute().getResult();
			return cfColumn != null && cfColumn.hasValue() ? cfColumn.getStringValue() : null;
		} catch (NotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(String rowKey, String column) {
		try {
			MutationBatch batch = getFactory().getKeyspace().prepareMutationBatch();
			batch.withRow(getCF().getColumnFamily(), rowKey).deleteColumn(column);
			batch.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Rows<String, String> read(Collection<String> keys) {
		try {
			OperationResult<Rows<String, String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getKeySlice(keys).execute();
			Rows<String, String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ColumnFamily<String, String> getRiColumnFamily() {
		return getCF().getRiColumnFamily();
	}

	@Override
	public Rows<String, String> getAll() {
		try {
			OperationResult<Rows<String, String>> result = getFactory().getKeyspace().prepareQuery(getCF().getColumnFamily()).getAllRows().execute();
			Rows<String, String> record = result.getResult();
			return record;
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}
}
