/////////////////////////////////////////////////////////////
// SessionRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.session;

import java.util.List;

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class SessionActivityRepositoryHibernate extends BaseRepositoryHibernate implements SessionActivityRepository, ParameterProperties, ConstantProperties {

	private final String RETRIEVE_SESSION_ACTIVITY_BY_ID = "From SessionActivity s   where s.sessionActivityId=:sessionActivityId";

	private final String RETRIEVE_SESSION_ACTIVITY_ITEM_BY_ID = "From SessionActivityItem  si  where si.sessionActivityId=:sessionActivityId and si.resourceId=:resourceId";

	private final String SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT = "select count(1) as count from session_activity_item_attempt_try where session_activity_id=:sessionActivityId and resource_id=:resourceId ";
	
	private final String GET_CLASS_EXPORT_QUERY_FROM_CONFIG = "SELECT value from config_setting WHERE name=:name";
	@Override
	public SessionActivity getSessionActivityById(Long sessionActivityId) {
		Query query = getSession().createQuery(RETRIEVE_SESSION_ACTIVITY_BY_ID);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		List<SessionActivity> sessionActivities = list(query);
		return (sessionActivities.size() > 0) ? sessionActivities.get(0) : null;
	}

	@Override
	public SessionActivityItem getSessionActivityItem(Long sessionActivityId, Long resourceId) {
		Query query = getSession().createQuery(RETRIEVE_SESSION_ACTIVITY_ITEM_BY_ID);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		query.setParameter(RESOURCE_ID, resourceId);
		List<SessionActivityItem> sessionActivityItems = list(query);
		return (sessionActivityItems.size() > 0) ? sessionActivityItems.get(0) : null;
	}

	@Override
	public Integer getSessionActivityCount(Long collectionId, Long parentId, String gooruUId) {
		String sql = " select count(1) as count from session_activity where collection_id =" + collectionId + " AND user_uid = '" + gooruUId + "'";
		if (parentId != null) {
			sql += " AND parent_id=" + parentId;
		}
		Query query = getSession().createSQLQuery(sql).addScalar(COUNT, StandardBasicTypes.INTEGER);
		List<Integer> results = list(query);
		return (results != null && results.size() > 0) ? results.get(0) : 0;
	}

	@Override
	public Integer getSessionActivityItemAttemptCount(Long sessionActivityId, Long resourceId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		query.setParameter(RESOURCE_ID, resourceId);
		List<Integer> results = list(query);
		return (results != null && results.size() > 0) ? results.get(0) : 0;
	}
	
	@Override
	public String getExportConfig(String key) {
		Query query = getSession().createSQLQuery(GET_CLASS_EXPORT_QUERY_FROM_CONFIG)
		.addScalar(VALUE, StandardBasicTypes.STRING);
		query.setParameter(NAME, key);
		List<String> results = list(query);
		return (results != null && results.size() > 0) ? results.get(0) : null;
	}
	
	@Override
	public List<Object[]> getClassReport(String classGooruId,String sql) {
		Session session = getSession();
		Query query = session.createSQLQuery(sql);
		query.setParameter(CLASS_GOORU_ID, classGooruId);
		List<Object[]> result = query.list();
		return result;

	}

}
