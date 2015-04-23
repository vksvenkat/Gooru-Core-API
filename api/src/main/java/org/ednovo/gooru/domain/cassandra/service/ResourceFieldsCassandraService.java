package org.ednovo.gooru.domain.cassandra.service;

import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.cassandra.core.service.EntityCassandraService;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ResourceFieldsCio;

import com.netflix.astyanax.model.Rows;

public interface ResourceFieldsCassandraService  extends EntityCassandraService<String, ResourceFieldsCio> {

	void saveViews(String id);

	String getContentMeta(String id, String name);
	
	void updateIndexQueue(List<String> gooruOids, String rowKey, String columnPrefix);

	Rows<String, String> readIndexQueuedData(Integer limit);
	
	void deleteIndexQueue(String rowKey, Collection<String> columns);

	void updateQueueStatus(String columnName, String rowKey, String prefix);

}
