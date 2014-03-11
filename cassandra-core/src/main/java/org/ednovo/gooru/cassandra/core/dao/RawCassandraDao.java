/*******************************************************************************
 * RawCassandraDao.java
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
import java.util.Map;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author Search Team
 * 
 */
public interface RawCassandraDao extends CassandraDao {

	void save(String key, Map<String, Object> entity);
	
	void save(String rowKey, String column, String value);
	
	void save(String key,
			Map<String, Object> entity,
			String prefix,
			boolean reset);
	
	ColumnList<String> read(String rowKey);
	
	Rows<String,String> getAll();
	
	Rows<String,String> read(Collection<String> keys);
	
	void delete(String... ids);
	
	void delete(String rowKey, String column);
	
}
