/////////////////////////////////////////////////////////////
// ResourceCassandraServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
/**
 * 
 */
package org.ednovo.gooru.domain.cassandra.service;

import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.domain.cassandra.ApiCassandraFactory;
import org.ednovo.gooru.infrastructure.persistence.hibernate.index.ContentIndexDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Rows;

/**
 * @author SearchTeam
 * 
 */
@Service
public class ResourceCassandraServiceImpl extends ApiCrudEntityCassandraServiceImpl<Resource, ResourceCio> implements ResourceCassandraService , ConstantProperties{

	@Autowired
	private ContentIndexDao contentIndexDao;

	@Override
	protected Resource fetchSource(String key) {
		return contentIndexDao.findResourceByContentGooruId(key);
	}

	@Autowired
	private ApiCassandraFactory apiCassandraFactory;

	@Override
	public void saveViews(String id) {
		Long views = this.getContentMeta(id, VIEWS) != null  ? Long.parseLong(this.getContentMeta(id, VIEWS)) : 0L; 
		getDao().save(id, VIEWS, (views + 1) + "");	
	}
	
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
	public void updateIndexQueue(List<String> gooruOids, String rowKey, String columnPrefix, boolean isUpdate) {
		getDao(ColumnFamilyConstant.INDEX_QUEUE).addIndexQueueEntry(rowKey, columnPrefix, gooruOids, isUpdate);
	}
	
	@Override
	public Rows<String, String> readIndexQueuedData(Integer limit, String columnPrefix){
		return getDao().readIndexQueuedData(limit, columnPrefix);
	}

	@Override
	public void deleteIndexQueue(String rowKey, Collection<String> columns) {
		getDao(ColumnFamilyConstant.INDEX_QUEUE).deleteIndexQueue(rowKey, columns);
	}

}
