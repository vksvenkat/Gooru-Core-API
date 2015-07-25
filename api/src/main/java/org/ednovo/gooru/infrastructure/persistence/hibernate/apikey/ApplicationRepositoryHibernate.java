/////////////////////////////////////////////////////////////
// ApplicationRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.apikey;

import java.util.List;

import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.ApplicationItem;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationRepositoryHibernate extends BaseRepositoryHibernate implements ApplicationRepository, ParameterProperties, ConstantProperties {

	private static final String APPLICATION_STATUS_ACTIVE = CustomProperties.Table.APPLICATION_STATUS.getTable()+"_"+CustomProperties.ApplicationStatus.ACTIVE.getApplicationStatus();
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Application> getApplications(String organizationUid,String gooruUid, Integer offset, Integer limit) {
		String hql = "FROM Application app WHERE  1=1";
		if (organizationUid != null) {
			hql += " AND app.organization.partyUid =:partyUid";
		}
		if (gooruUid != null) {
			hql += " AND app.user.partyUid =:gooruUid";
		}
		hql += " AND app.status.keyValue =:type and app.resourceType.name = '" + ResourceType.Type.APPLICATION.getType()+"' ORDER BY app.lastModified desc";
		Query query = getSession().createQuery(hql);
		if (organizationUid != null) {
			query.setParameter("partyUid", organizationUid);
		}
		if (gooruUid != null) {
			query.setParameter("gooruUid", gooruUid);
		}
		query.setParameter("type", APPLICATION_STATUS_ACTIVE);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return (List<Application>) query.list();
	}

	@Override
	@Cacheable("gooruCache")
	public Application getApplication(String apiKey) {
		String hql = "FROM Application app WHERE app.key=:apiKey AND app.status.keyValue =:type";
		Query query = getSession().createQuery(hql);
		query.setParameter("apiKey", apiKey);
		query.setParameter("type", APPLICATION_STATUS_ACTIVE);
		return (Application) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	public Long getApplicationCount(String organizationUid , String gooruUid) {
		String hql = "SELECT count(*) FROM Application app WHERE 1=1";
		if (organizationUid != null)  {
			hql += " AND app.organization.partyUid =:organizationUid";
		}
		if (gooruUid != null) {
			hql += " AND app.user.partyUid =:gooruUid";
		}
		hql += " AND app.status.keyValue =:type and app.resourceType.name = '" + ResourceType.Type.APPLICATION.getType()+"'";	
		Query query = getSession().createQuery(hql);
		if (organizationUid != null)  {
			query.setParameter("organizationUid", organizationUid);
		}
		if (gooruUid != null)  {
			query.setParameter("gooruUid", gooruUid);
		}
		query.setParameter("type", APPLICATION_STATUS_ACTIVE);
		return (Long) query.list().get(0);
	}

	@Override
	@Cacheable("gooruCache")
	public Application getApplicationByOrganization(String organizationUid) {
		String hql = "FROM Application app WHERE app.organization.partyUid =:organizationUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("organizationUid", organizationUid);
		return (Application) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public ApplicationItem getApplicationItem(String applicationItemId,String apikey) {
		String hql = "FROM ApplicationItem appItem WHERE appItem.applicationItemUid=:applicationItemId AND appItem.application.status.keyValue=:type";
		Query query = getSession().createQuery(hql);
		query.setParameter("applicationItemId", applicationItemId);
		query.setParameter("type", APPLICATION_STATUS_ACTIVE);
		return (ApplicationItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public List<ApplicationItem> getApplicationItemByApiKey(String apiKey) {
		String hql = "FROM ApplicationItem appItem WHERE appItem.application.key=:apiKey AND appItem.application.status.keyValue=:type";
		Query query = getSession().createQuery(hql);
		query.setParameter("apiKey", apiKey);
		query.setParameter("type", APPLICATION_STATUS_ACTIVE);
		List<ApplicationItem> applicationItems = list(query);
		return applicationItems.size() > 0 ? applicationItems : null;
	}

}