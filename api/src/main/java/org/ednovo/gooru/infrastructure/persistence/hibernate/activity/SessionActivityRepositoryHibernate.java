/////////////////////////////////////////////////////////////
// SessionActivityRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.activity;

import java.util.List;

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class SessionActivityRepositoryHibernate extends BaseRepositoryHibernate implements SessionActivityRepository {

	@Override
	public SessionActivity getSessionActivity(String sessionActivityUid) {
		String hql = "FROM SessionActivity sessionActivity WHERE sessionActivity.sessionActivityUid = :sessionActivityUid AND  " + generateOrgAuthQuery("sessionActivity.");
		Query query = getSession().createQuery(hql);
		query.setParameter("sessionActivityUid", sessionActivityUid);
		addOrgAuthParameters(query);
		List<SessionActivity> sessionActivityList = (List<SessionActivity>) query.list();
		return sessionActivityList.size() > 0 ? sessionActivityList.get(0) : null;
	}

	@Override
	public List<SessionActivity> getUserSessionActivityList(String userUid) {
		String hql = "FROM SessionActivity sessionActivity WHERE sessionActivity.userUid=:userUid AND  " + generateOrgAuthQuery("sessionActivity.");
		Query query = getSession().createQuery(hql);
		query.setParameter("userUid", userUid);
		addOrgAuthParameters(query);
		List<SessionActivity> sessionActivityList = (List<SessionActivity>) query.list();
		return sessionActivityList.size() > 0 ? sessionActivityList : null;
	}

	@Override
	public SessionActivityItem getContentSessionActivityItem(String contentUid, String userUid, String status) {
		String hql = "FROM SessionActivityItem sessionActivityItem WHERE sessionActivityItem.sessionActivity.userUid=:userUid AND  " + generateOrgAuthQuery("sessionActivityItem.sessionActivity.")
				+ "  AND sessionActivityItem.contentUid =:contentUid AND sessionActivityItem.sessionActivity.status =:status  order by sessionActivityItem.createdOn desc";
		Query query = getSession().createQuery(hql).setMaxResults(1);
		query.setParameter("userUid", userUid);
		query.setParameter("contentUid", contentUid);
		query.setParameter("status", status);
		addOrgAuthParameters(query);
		return (SessionActivityItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<SessionActivityItem> getContentSessionActivityItemList(String contentUid, String userUid, String status) {
		String hql = "FROM SessionActivityItem sessionActivityItem WHERE sessionActivityItem.sessionActivity.userUid=:userUid AND  " + generateOrgAuthQuery("sessionActivityItem.sessionActivity.")
				+ "  AND sessionActivityItem.contentUid =:contentUid AND sessionActivityItem.sessionActivity.status =:status  order by sessionActivityItem.createdOn desc";
		Query query = getSession().createQuery(hql);
		query.setParameter("userUid", userUid);
		query.setParameter("contentUid", contentUid);
		query.setParameter("status", status);
		addOrgAuthParameters(query);
		return list(query);
	}

	@Override
	public SessionActivityItem getUserLastOpenSessionActivityItem(String userUid, String status) {
		String hql = "FROM SessionActivityItem sessionActivityItem WHERE sessionActivityItem.sessionActivity.userUid=:userUid AND  " + generateOrgAuthQuery("sessionActivityItem.sessionActivity.")
				+ " AND  sessionActivityItem.sessionActivity.status =:status  order by sessionActivityItem.createdOn desc";
		Query query = getSession().createQuery(hql).setMaxResults(1);
		query.setParameter("userUid", userUid);
		query.setParameter("status", status);
		addOrgAuthParameters(query);
		return (SessionActivityItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<SessionActivity> getContentSessionActivityList(String contentUid, String status) {
		String hql = "SELECT sessionActivityItem.sessionActivity  FROM SessionActivityItem sessionActivityItem WHERE sessionActivityItem.contentUid=:contentUid AND  sessionActivityItem.sessionActivity.status =:status AND " + generateOrgAuthQuery("sessionActivityItem.sessionActivity.");
		Query query = getSession().createQuery(hql);
		query.setParameter("contentUid", contentUid);
		query.setParameter("status", status);
		addOrgAuthParameters(query);
		List<SessionActivity> sessionActivityList = (List<SessionActivity>) query.list();
		return sessionActivityList.size() > 0 ? sessionActivityList : null;
	}

	@Override
	public List<SessionActivityItem> getSubContentSessionActivityItemList(String contentUid, String userUid, String status) {
		String hql = "FROM SessionActivityItem sessionActivityItem WHERE sessionActivityItem.sessionActivity.userUid=:userUid AND  " + generateOrgAuthQuery("sessionActivityItem.sessionActivity.")
				+ "  AND sessionActivityItem.contentUid =:contentUid AND sessionActivityItem.sessionActivity.status =:status  AND sessionActivityItem.subContentUid IS NOT NULL";
		Query query = getSession().createQuery(hql);
		query.setParameter("userUid", userUid);
		query.setParameter("contentUid", contentUid);
		query.setParameter("status", status);
		addOrgAuthParameters(query);
		return list(query);
	}

	@Override
	public Integer getStudiedResourceCount(String contentUid, String userUid, String status) {
		String sql = "SELECT count(distinct(sub_content_uid)) as count from session_activity_item sai inner join session_activity sa on sa.session_activity_uid = sai.session_activity_uid join content c on sai.content_uid = c.gooru_oid WHERE sai.content_uid ='" + contentUid + "' AND sa.user_uid = '"
				+ userUid + "' AND sa.status ='" + status + "' AND  " + generateOrgAuthSqlQueryWithData("sa.");
		Integer result = (Integer) getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();
		return result != null ? result : 0;
	}

}
