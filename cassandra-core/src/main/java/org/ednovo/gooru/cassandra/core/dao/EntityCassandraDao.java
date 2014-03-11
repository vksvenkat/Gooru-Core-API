/*******************************************************************************
 * EntityCassandraDao.java
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
import java.util.List;
import java.util.Map;

import com.netflix.astyanax.model.ColumnList;

/**
 * @author Search Team
 * 
 */
public interface EntityCassandraDao<M> extends CassandraDao {
	
	void save(M model);

	void save(Collection<M> models,
			Collection<String> modelKeys, boolean skipRiUpdate);
	
	Integer getColumnCount(String riFullKey);

	M read(String key);

	List<M> read(Collection<String> keys);
	
	void delete(Collection<String> keys);

	Map<String,Integer> getRowsColumnCount(String reverseIndexName);

	void save(String key,
			Map<String, Object> entity,
			String prefix,
			boolean reset);
	
	void save(List<Map<String, Object>> entity,
			String prefix,
			boolean reset);
	
	List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex,
			boolean reversed);
	
	List<M> getModels(int pageSize,
			String key,
			String startPoint,
			String reverseIndex);
	
	List<M> getAll();
	
	ColumnList<String> getColumns(String rowKey,
			Collection<String> fields);
	
}
