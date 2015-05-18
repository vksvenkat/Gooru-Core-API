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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityItemAttemptTry;
import org.ednovo.gooru.core.api.model.SessionItemFeedback;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;


@Repository
public class SessionRepositoryHibernate extends BaseRepositoryHibernate implements SessionRepository {

	private final String RETRIEVE_SESSION_BY_ID = "From Session s   where s.sessionId=:sessionId and " + generateOrgAuthQuery("s.");
	
	private final String RETRIEVE_SESSION_ITEM_BY_ID = "select si From SessionItem si  join si.session s  where si.sessionItemId=:sessionItemId and " + generateOrgAuthQuery("s.");
	
	private final String RETRIEVE_ITEM_BY_ID_USING_DATE = " select si From SessionItem si join  si.session  s where s.sessionId=:sessionId and " + generateOrgAuthQuery("s.") + "order by si.startTime desc";
	
	private final String RETRIEVE_SESSION_ITEM_ATTEMPT_TRY = "select at From SessionItemAttemptTry at  join at.sessionItem si join si.session s   where si.sessionItemId=:sessionItemId and " + generateOrgAuthQuery("s.");
	
	@Override
	public org.ednovo.gooru.core.api.model.SessionActivity findSessionById(String sessionId) {
		Session session = getSession();
		Query query = session.createQuery(RETRIEVE_SESSION_BY_ID);
		query.setParameter("sessionId", sessionId);
		addOrgAuthParameters(query);
		List<org.ednovo.gooru.core.api.model.SessionActivity> sessionActivities = list(query);
		return (sessionActivities.size() > 0) ? sessionActivities.get(0) : null;
	}

	@Override
	public SessionActivityItem findSessionItemById(String sessionItemId) {
		Session session = getSession();
		Query query = session.createQuery(RETRIEVE_SESSION_ITEM_BY_ID);
		query.setParameter("sessionItemId", sessionItemId);
		addOrgAuthParameters(query);
		List<SessionActivityItem> sessionActivityItems = list(query);
		return (sessionActivityItems.size() > 0) ? sessionActivityItems.get(0) : null;
	}

	@Override
	public List<SessionActivityItemAttemptTry> getSessionItemAttemptTry(String sessionItemId) {
		Session session = getSession();
		Query query = session.createQuery(RETRIEVE_SESSION_ITEM_ATTEMPT_TRY);
		query.setParameter("sessionItemId", sessionItemId);
		addOrgAuthParameters(query);
		return list(query);
	}

	@Override
	public SessionActivityItem getLastSessionItem(String sessionId) {
		Session session = getSession();
		Query query = session.createQuery(RETRIEVE_ITEM_BY_ID_USING_DATE);
		query.setParameter("sessionId", sessionId);
		addOrgAuthParameters(query);
		List<SessionActivityItem> sessionActivityItems = list(query);
		return (sessionActivityItems.size() > 0) ? sessionActivityItems.get(0) : null;
	}
	
	@Override
	public Map<String, Object> getQuizSummary(String sessionId, Integer trySequence, String questionType, Long quizContentId) {
		Session session = getSession();
		String sql = "select count(aq.question_id) as attemptedQuestionCount, " +
						"sum(case sit.attempt_item_try_status when 'correct' then 1 else 0 end ) as correctAnswerCount, " +
						"sum(case sit.attempt_item_try_status when 'wrong' then 1 else 0 end ) as wrongAnswerCount " +
						"from session s " +
						"inner join session_item si on (si.session_id = s.session_id) " +
						"inner join session_item_attempt_try sit on (sit.session_item_id = si.session_item_id) " +
						"inner join assessment_question aq on (aq.question_id = si.resource_id) " +
						"where si.session_id = :sessionId and try_sequence = :trySequence and s.status = 'archive'  " +
						"and aq.type in (:questionType)";
		if (quizContentId != null && quizContentId != 0) {
		    sql += " and s.collection_id = :quizId";
		}
		Query query = session.createSQLQuery(sql).addScalar("attemptedQuestionCount", StandardBasicTypes.INTEGER)
		.addScalar("correctAnswerCount", StandardBasicTypes.INTEGER).addScalar("wrongAnswerCount", StandardBasicTypes.INTEGER)
		.setParameter("sessionId", sessionId).setParameter("trySequence", trySequence)
		.setParameter("questionType", questionType);
		if (quizContentId != null && quizContentId != 0) {
			query.setParameter("quizId", quizContentId);
		}		
		return getQuizSummary((Object[]) query.list().get(0));
	} 
	private Map<String, Object> getQuizSummary(Object[] object) {
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("attemptedQuestionCount", object[0] != null ? object[0] : 0);
		summary.put("correctAnswerCount", object[1] != null ? object[1] : 0);
		summary.put("wrongAnswerCount", object[2] != null ? object[2] : 0);
		return summary; 
	}
	
        public String getQuestionStatus(String sessionId, Integer trySequence, String questionType, Long quizContentId, String questionId){
                Session session = getSession();
                String sql = "select attempt_item_try_status from session s " +
                                "inner join session_item si on si.session_id = s.session_id " +
                                "inner join session_item_attempt_try sit on sit.session_item_id = si.session_item_id " +
                                "inner join assessment_question aq on (aq.question_id = si.resource_id) " +
                                "inner join content c on c.content_id = aq.question_id " +
                                "where s.status = 'archive' and si.session_id = :sessionId " +
                                "and c.gooru_oid = :questionId and try_sequence = :trySequence " +
                                                "and aq.type in (:questionType)";
                if (quizContentId != null && quizContentId != 0) {
                    sql += " and s.collection_id = :quizContentId";
                }
                Query query = session.createSQLQuery(sql).setParameter("sessionId", sessionId).setParameter("trySequence", trySequence)
                .setParameter("questionType", questionType).setParameter("questionId", questionId);
                if (quizContentId != null && quizContentId != 0) {
                        query.setParameter("quizContentId", quizContentId);
                }
                return query.list().size() > 0 ? (String)query.list().get(0) : null;
        }

		@Override
		public SessionItemFeedback getSessionItemFeedback(String gooruOid, String gooruUid) {
			String hql = "from SessionItemFeedback si where si.contentGooruOId = '"+ gooruOid+"' and user.partyUid = '" +gooruUid+ "'";
			Query query = getSession().createQuery(hql);
			return query.list().size() > 0 ? (SessionItemFeedback)query.list().get(0) : null;
		}	

}
