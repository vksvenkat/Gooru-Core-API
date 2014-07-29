/////////////////////////////////////////////////////////////
// ApiCassandraFactory.java
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
package org.ednovo.gooru.domain.cassandra;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.dao.CassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.EntityCassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.EntityCassandraDaoImpl;
import org.ednovo.gooru.cassandra.core.dao.RawCassandraDaoImpl;
import org.ednovo.gooru.cassandra.core.factory.SearchCassandraFactory;
import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.core.cassandra.model.ContentProviderCio;
import org.ednovo.gooru.core.cassandra.model.DomainCio;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ReverseIndexColumnSetting;
import org.ednovo.gooru.core.cassandra.model.TaxonomyCio;
import org.ednovo.gooru.core.cassandra.model.UserCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author SearchTeam
 * 
 */
@Component

public class ApiCassandraFactory extends SearchCassandraFactory {

	@Autowired
	private ConfigSettingRepository configSettingRepository;

	@Override
	@PostConstruct
	public final void init() {
		super.init();
		register(new CassandraColumnFamily(ColumnFamilyConstant.DATA_STORE));
		register(new CassandraColumnFamily(ColumnFamilyConstant.SEARCH_SETTING));
		register(new CassandraColumnFamily(ColumnFamilyConstant.CONTENT_META));
		register(new CassandraColumnFamily(ColumnFamilyConstant.CUSTOM_FILEDS_DATA));
		register(new CassandraColumnFamily(ColumnFamilyConstant.CUSTOM_FILEDS_DEFINITION));
		register(new EntityCassandraColumnFamily<DomainCio>(DomainCio.class, new ReverseIndexColumnSetting().putField("name", "id")));
		register(new EntityCassandraColumnFamily<ResourceCio>(ResourceCio.class, new ReverseIndexColumnSetting().putField("type","resourceType").putField("batch", "batchId").putField("categoy", "category").putField("resourceFormat", "resourceFormat").putField("instructional", "instructional")));
		register(new EntityCassandraColumnFamily<RevisionHistory>(RevisionHistory.class, new ReverseIndexColumnSetting().putField("entity", "entityName")));
		register(new EntityCassandraColumnFamily<TaxonomyCio>(TaxonomyCio.class, new ReverseIndexColumnSetting().putField("organization", "organization.partyUid")));
		register(new EntityCassandraColumnFamily<UserCio>(UserCio.class, new ReverseIndexColumnSetting().putField("organization", "organization.partyUid")));
		register(new EntityCassandraColumnFamily<ContentProviderCio>(ContentProviderCio.class, new ReverseIndexColumnSetting().putField("entity", "entityName")));
    	register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.DATA_STORE));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.SEARCH_SETTING));
		register(new EntityCassandraDaoImpl<ResourceCio>(this, ColumnFamilyConstant.RESOURCE));
		register(new EntityCassandraDaoImpl<TaxonomyCio>(this, ColumnFamilyConstant.TAXONOMY));
		register(new EntityCassandraDaoImpl<UserCio>(this, ColumnFamilyConstant.USER));
		register(new EntityCassandraDaoImpl<RevisionHistory>(this, ColumnFamilyConstant.REVISION_HISTORY));
		register(new EntityCassandraDaoImpl<DomainCio>(this, ColumnFamilyConstant.DOMAIN));
		register(new EntityCassandraDaoImpl<ContentProviderCio>(this, ColumnFamilyConstant.CONTENT_PROVIDER));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.CONTENT_META));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.CUSTOM_FILEDS_DATA));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.CUSTOM_FILEDS_DEFINITION));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.INDEX_QUEUE));
		
	}

	@Override
	public CassandraSettingService getSettingService() {
		return configSettingRepository;
	}

}
