package org.ednovo.gooru.domain.cassandra.service;

import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.cassandra.model.ResourceFieldsCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.domain.cassandra.ApiCassandraFactory;
import org.ednovo.gooru.infrastructure.persistence.hibernate.index.ContentIndexDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.astyanax.model.Rows;

@Service
public class ResourceFieldsCassandraServiceImpl extends ApiCrudEntityCassandraServiceImpl<Resource, ResourceFieldsCio> implements ResourceFieldsCassandraService , ConstantProperties{

	@Autowired
	private ContentIndexDao contentIndexDao;

	@Override
	protected Resource fetchSource(String key) {
		return contentIndexDao.findResourceByContentGooruId(key);
	}


	@Autowired
	private ApiCassandraFactory apiCassandraFactory;

	@Override
	public String getContentMeta(String id, String name) {
		return getDao().read(id, name);
	}
	
	private RawCassandraDao getDao() {
		return (RawCassandraDao) apiCassandraFactory.get(ColumnFamilyConstant.CONTENT_META);
	}

	private RawCassandraDao getDao(String columnFamilyName) {
		return (RawCassandraDao) apiCassandraFactory.get(columnFamilyName);
	}

	@Override
	String getDaoName() {
		return ColumnFamilyConstant.RESOURCE;
	}

	@Override
	public void updateIndexQueue(List<String> gooruOids, String rowKey, String prefix) {
		getDao(ColumnFamilyConstant.INDEX_QUEUE).addIndexQueueEntry(rowKey, prefix, gooruOids);
	}

	@Override
	public void updateQueueStatus(String columnName, String rowKey, String prefix) {
		getDao(ColumnFamilyConstant.INDEX_QUEUE).updateQueueStatus(columnName, rowKey, prefix);
	}

	@Override
	public Rows<String, String> readIndexQueuedData(Integer limit){
		return getDao().readIndexQueuedData(limit);
	}

	@Override
	public void deleteIndexQueue(String rowKey, Collection<String> columns) {
		getDao(ColumnFamilyConstant.INDEX_QUEUE).deleteIndexQueue(rowKey, columns);
	}
/*
	@Override
	public <String, ResourceFieldsCio> void saveViews(String id){
		
	}*/

	@Override
	public void saveViews(String id) {
		Long views = this.getContentMeta(id, VIEWS) != null  ? Long.parseLong(this.getContentMeta(id, VIEWS)) : 0L; 
		getDao().save(id, VIEWS, (views + 1) + "");	
	}


}


