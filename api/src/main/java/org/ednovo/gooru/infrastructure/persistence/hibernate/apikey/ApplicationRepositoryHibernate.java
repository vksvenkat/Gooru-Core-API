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

import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationRepositoryHibernate extends BaseRepositoryHibernate implements ApplicationRepository {

	@Override
	public List<ApiKey> getApplicationByOrganization(String organizationUid) {
		int activeFlag= 1;
		String hql = "FROM ApiKey apiKey WHERE apiKey.organization.partyUid =:partyUid AND apiKey.activeFlag =:activeFlag";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", organizationUid);
		query.setParameter("activeFlag", activeFlag);
		List<ApiKey> list = query.list();
		return (list == null || list.size() == 0) ? null : list;
	}
	@Override
	public ApiKey getApplicationByAppKey(String appKey) {
		int activeFlag= 1;
		String hql = "FROM ApiKey apiKey WHERE apiKey.key =:appKey AND apiKey.activeFlag =:activeFlag";
		Query query = getSession().createQuery(hql);
		query.setParameter("appKey", appKey);
		query.setParameter("activeFlag", activeFlag);
		List<ApiKey> list = query.list();
		return (list == null || list.size() == 0) ? null : list.get(0);
	}

}
