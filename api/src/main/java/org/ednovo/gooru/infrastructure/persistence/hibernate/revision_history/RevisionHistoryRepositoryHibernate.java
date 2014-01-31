/////////////////////////////////////////////////////////////
// RevisionHistoryRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.revision_history;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * @author Search Team
 * 
 */
@Repository
public class RevisionHistoryRepositoryHibernate implements RevisionHistoryRepository {

	@Resource(name = "revisionHistorySessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public PageWrapper<RevisionHistory> listRevisionHistory(Map<String, Object> parameters) {
		String hql = "FROM RevisionHistory history WHERE  1=1 ";

		if (parameters.containsKey("entityUid")) {
			hql += " AND history.entityUid = '" + parameters.get("entityUid") + "'";
		}
		if (parameters.containsKey("userUid")) {
			hql += " AND history.userUid = '" + parameters.get("userUid") + "'";
		}
		if (parameters.containsKey("onEvent")) {
			hql += " AND history.onEvent = '" + parameters.get("onEvent") + "'";
		}

		Query query = getSession().createQuery(hql);
		Integer pageNum = (Integer) parameters.get("pageNum");
		if (pageNum == null) {
			pageNum = 1;
		}
		Integer pageSize = (Integer) parameters.get("pageSize");
		if (pageSize == null) {
			pageSize = 10;
		}
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		PageWrapper<RevisionHistory> revisionWrapper = new PageWrapper<RevisionHistory>();
		revisionWrapper.setDataList(query.list());
		revisionWrapper.setPageNum(pageNum);
		revisionWrapper.setPageSize(pageSize);
		return revisionWrapper;
	}

	@Override
	public void save(RevisionHistory revisionHistory) {
		getSession().save(revisionHistory);
	}

	@Override
	public RevisionHistory get(String revisionHistoryUid) {
		return getRecord(getSession().createCriteria(RevisionHistory.class).add(Restrictions.idEq(revisionHistoryUid)).list());
	}

	@Override
	public RevisionHistory getLastKnownHistory(String entityId) {
		return getRecord(getSession().createCriteria(RevisionHistory.class).add(Restrictions.eq("entityUid", entityId)).addOrder(Order.desc("time")).list());
	}

	private <T> T getRecord(List<?> list) {
		return list.size() > 0 ? (T) list.get(0) : null;
	}

	private Session getSession() {
		return getSession();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
