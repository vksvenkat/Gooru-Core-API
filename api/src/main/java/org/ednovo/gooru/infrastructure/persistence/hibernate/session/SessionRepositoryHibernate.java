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

import org.ednovo.gooru.core.api.model.SessionItem;
import org.ednovo.gooru.core.api.model.SessionItemAttemptTry;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;


@Repository
public class SessionRepositoryHibernate extends BaseRepositoryHibernate implements SessionRepository {

	private final String RETIREVE_SESSION_BY_ID = "From Session s   where s.sessionId=:sessionId and " + generateOrgAuthQuery("s.");
	
	private final String RETIREVE_SESSION_ITEM_BY_ID = "From SessionItem si   where si.sessionItemId=:sessionItemId and " + generateOrgAuthQuery("si.session.");
	
	private final String RETIREVE_ITEM_BY_ID_USING_DATE = "From SessionItem si   where si.session.sessionId=:sessionId and " + generateOrgAuthQuery("si.session.") + "order by si.startTime desc";
	
	private final String RETIREVE_SESSION_ITEM_ATTEMPT_TRY = "From SessionItemAttemptTry at   where at.sessionItem.sessionItemId=:sessionItemId and " + generateOrgAuthQuery("at.sessionItem.session.");

	@Override
	public org.ednovo.gooru.core.api.model.Session findSessionById(String sessionId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SESSION_BY_ID);
		query.setParameter("sessionId", sessionId);
		addOrgAuthParameters(query);
		List<org.ednovo.gooru.core.api.model.Session> sessions = query.list();
		return (sessions.size() > 0) ? sessions.get(0) : null;
	}

	@Override
	public SessionItem findSessionItemById(String sessionItemId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SESSION_ITEM_BY_ID);
		query.setParameter("sessionItemId", sessionItemId);
		addOrgAuthParameters(query);
		List<SessionItem> sessionItems = query.list();
		return (sessionItems.size() > 0) ? sessionItems.get(0) : null;
	}

	@Override
	public List<SessionItemAttemptTry> getSessionItemAttemptTry(String sessionItemId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SESSION_ITEM_ATTEMPT_TRY);
		query.setParameter("sessionItemId", sessionItemId);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public SessionItem getLastSessionItem(String sessionId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_ITEM_BY_ID_USING_DATE);
		query.setParameter("sessionId", sessionId);
		addOrgAuthParameters(query);
		List<SessionItem> sessionItems = query.list();
		return (sessionItems.size() > 0) ? sessionItems.get(0) : null;
	}

}
