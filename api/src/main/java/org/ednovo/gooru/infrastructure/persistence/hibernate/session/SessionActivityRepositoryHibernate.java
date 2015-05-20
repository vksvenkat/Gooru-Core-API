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
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class SessionActivityRepositoryHibernate extends BaseRepositoryHibernate implements SessionActivityRepository, ParameterProperties, ConstantProperties {

	private final String RETRIEVE_SESSION_ACTIVITY_BY_ID = "From SessionActivity s   where s.sessionActivityId=:sessionActivityId";

	private final String RETRIEVE_SESSION_ACTIVITY_ITEM_BY_ID = "From SessionActivityItem  si  where si.sessionActivityId=:sessionActivityId and si.resourceId=:resourceId";

	private final String SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT = "select count(1) as count from session_activity_item_attempt_try where session_activity_id=:sessionActivityId and resource_id=:resourceId ";

	private final String SESSION_ACTIVITY_RATING_COUNT = "select IFNULL(round(sum(rating)/count(1)), 0) as count from session_activity_item where session_activity_id =:sessionActivityId and rating <> 0";

	private final String SESSION_ACTIVITY_REACTION_COUNT = "select IFNULL(round(sum(reaction)/count(1)), 0) as count from session_activity_item where session_activity_id =:sessionActivityId and reaction <> 0";

	private final String COLLECTION_QUESTION_COUNT = "select count(1) as count from collection_item ci inner join assessment_question  q on q.question_id = ci.resource_content_id where ci.collection_content_id=:collectionId";

	private final String SESSION_ACTIVITY_TOTAL_SCORE = "select IFNULL(sum(score), 0) as count from session_activity_item where session_activity_id =:sessionActivityId";
	
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
		return (Integer) list(query).get(0);
	}

	@Override
	public Integer getSessionActivityItemAttemptCount(Long sessionActivityId, Long resourceId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		query.setParameter(RESOURCE_ID, resourceId);
		return (Integer) list(query).get(0);
	}

	@Override
	public Integer getSessionActivityReactionCount(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_REACTION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		return (Integer) list(query).get(0);
	}

	@Override
	public Integer getSessionActivityRatingCount(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_RATING_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		return (Integer) list(query).get(0);
	}

	@Override
	public Integer getQuestionCount(Long collectionId) {
		Query query = getSession().createSQLQuery(COLLECTION_QUESTION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, collectionId);
		return (Integer) list(query).get(0);
	}

	@Override
	public Integer getTotalScore(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_TOTAL_SCORE).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		return (Integer) list(query).get(0);
	}
}
