/////////////////////////////////////////////////////////////
// OrganizationSettingRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.OrganizationSetting;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationSettingRepositoryHibernate extends BaseRepositoryHibernate implements OrganizationSettingRepository {

	@Override
	@Cacheable("persistent")
	public Map<String, String> getOrganizationSettings(String organizationUid) {
		Map<String, String> settings = new HashMap<String, String>();
		Query query = getSessionReadOnly().createSQLQuery(GET_ORGAIZATION_SETTINGS).addScalar(NAME, StandardBasicTypes.STRING).addScalar(VALUE, StandardBasicTypes.STRING);
		query.setParameter(ORG_UID_PARAM, organizationUid);
		List<Object[]> results = query.list();
		for (Object[] object : results) {
			settings.put((String) object[0], (String) object[1]);
		}
		return settings;
	}
	
	@Override
	@Cacheable("persistent")
	public Map<String, String> getOrganizationExpireTime(String name) {
		Map<String, String> settings = new HashMap<String, String>();
		Query query = getSessionReadOnly().createSQLQuery(GET_ORGANIZATION_EXPIRE_TIME).addScalar("organization_uid", StandardBasicTypes.STRING).addScalar(VALUE, StandardBasicTypes.STRING);
		query.setParameter(NAME, name);
		List<Object[]> results = query.list();
		for (Object[] object : results) {
			settings.put((String) object[0], (String) object[1]);
		}
		return settings;
	}

	@Override
	@Cacheable("persistent")
	public String getOrganizationSetting(String key, String organizationUid) {
		Query query = getSessionReadOnly().createSQLQuery(GET_ORGANIZATION_SETTING).addScalar(VALUE, StandardBasicTypes.STRING);
		query.setParameter(ORG_UID_PARAM, organizationUid);
		query.setParameter(NAME, key);
		List<String> results = query.list();
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		return null;
	}

	@Override
	@Cacheable("persistent")
	public OrganizationSetting getOrganizationSettings(String organizationUid, String configKey) throws Exception {
		String hql = "FROM OrganizationSetting orgSetting where orgSetting.organization.partyUid = :organizationUid and orgSetting.key=:name";
		Query query = getSessionReadOnly().createQuery(hql);
		query.setParameter(ORG_UID_PARAM, organizationUid);
		query.setParameter(NAME, configKey);
		if(query.list() != null && query.list().size() > 0){
			return (OrganizationSetting) query.list().get(0);
		}
		return null;
	}

	@Override
	@Cacheable("persistent")
	public OrganizationSetting listOrgSetting(String organizationUid, String configKey) throws Exception {
		String hql = "FROM OrganizationSetting orgSetting where orgSetting.key=:name";
		if(organizationUid != null){
			hql += " and orgSetting.organization.partyUid = :organizationUid " ;
		}
		Query query = getSessionReadOnly().createQuery(hql);
		
		if(organizationUid != null){
			query.setParameter(ORG_UID_PARAM, organizationUid);
		}
		query.setParameter(NAME, configKey);
		if(query.list() != null && query.list().size() > 0){
			return (OrganizationSetting) query.list().get(0);
		}
		return null;
	}
	
}
