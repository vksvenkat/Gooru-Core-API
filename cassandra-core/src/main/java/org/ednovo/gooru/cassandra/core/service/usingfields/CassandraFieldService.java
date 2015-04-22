package org.ednovo.gooru.cassandra.core.service.usingfields;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;

public interface CassandraFieldService {

	void save (String ...ids);
	
	Row<String,String>  fetchMultipleRows(String ...ids);
	
	ColumnList<String>  fetchSingleRow(String id);
	
}
