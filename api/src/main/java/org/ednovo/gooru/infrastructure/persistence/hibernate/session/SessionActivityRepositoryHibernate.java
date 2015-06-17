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
import java.util.Map;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class SessionActivityRepositoryHibernate extends BaseRepositoryHibernate implements SessionActivityRepository, ParameterProperties, ConstantProperties {


	private final String RETRIEVE_SESSION_ACTIVITY_BY_ID = "From SessionActivity s   where s.sessionActivityId=:sessionActivityId";

	private final String RETRIEVE_SESSION_ACTIVITY_ITEM_BY_ID = "From SessionActivityItem  si  where si.sessionActivityId=:sessionActivityId and si.resourceId=:resourceId";

	private final String SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT = "select count(1) as count from session_activity_item_attempt_try where session_activity_id=:sessionActivityId and resource_id=:resourceId ";

	private final String SESSION_ACTIVITY_RATING_COUNT = "select IFNULL(round(sum(rating)/count(1)), 0) as count from session_activity_item where session_activity_id =:sessionActivityId and rating <> 0";

	private final String SESSION_ACTIVITY_REACTION_COUNT = "select IFNULL(round(sum(reaction)/count(1)), 0) as count from session_activity_item where session_activity_id =:sessionActivityId and reaction <> 0";

	private final String COLLECTION_QUESTION_COUNT = "select count(1) as count from collection_item ci inner join assessment_question  q on q.question_id = ci.resource_content_id where q.type_name <> 'OE' and ci.collection_content_id=:collectionId";

	private final String SESSION_ACTIVITY_TOTAL_SCORE = "select IFNULL(sum(score), 0) as count from session_activity_item where session_activity_id =:sessionActivityId";
	
	private final String GET_CLASS_EXPORT_QUERY_FROM_CONFIG = "SELECT value from config_setting WHERE name=:name";

	private final String FIND_QUESTION = "From AssessmentQuestion q   where q.gooruOid=:gooruOid";
	
	private final String RETRIEVE_LAST_SESSION_ACTIVITY_BY_IDS = "From SessionActivity s   where s.parentId=:parentId and s.collectionId=:collectionId and s.user.partyUid=:userId order by s.sequence desc";
	
	private final String RETRIVE_INCOMPLETE_SESSION_ACTIVITY_ID = "SELECT sa.user_uid as userUid,sa.session_activity_id as sessionActivityId,so.gooru_oid as collectionGooruOid,sai.gooru_oid as resourceGooruOid from session_activity sa inner join session_activity_item si on sa.session_activity_id=si.session_activity_id inner join content so on so.content_id=sa.collection_id left join content sai on sai.content_id=si.resource_id where sa.status='open' and sa.user_uid=:userUid and so.gooru_oid =:collectionId order by si.start_time DESC";

	private final String RETRIVE_SESSION_COUNT = "select count(1) as count from session_activity where collection_id =:collectionId AND user_uid =:userId";
	
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
	public Integer getSessionActivityCount(Long collectionId, Long classContentId, Long unitContentId, Long lessonContentId, String gooruUId) {

		StringBuilder sqlQuery = new StringBuilder(RETRIVE_SESSION_COUNT);
		if (classContentId != null) {
			sqlQuery.append(" AND class_content_id=:classContentId");
		}
		if (unitContentId != null) {
			sqlQuery.append(" AND unit_content_id=:unitContentId");
		}
		if (lessonContentId != null) {
			sqlQuery.append(" AND lesson_content_id=:lessonContentId");
		}
		Query query = getSession().createSQLQuery(sqlQuery.toString()).addScalar(COUNT, StandardBasicTypes.INTEGER);

		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(USER_ID, gooruUId);

		if (classContentId != null) {
			query.setParameter(CLASS_CONTENT_ID, classContentId);
		}
		if (unitContentId != null) {
			query.setParameter(UNIT_CONTENT_ID, unitContentId);
		}
		if (lessonContentId != null) {
			query.setParameter(LESSON_CONTENT_ID, lessonContentId);
		}
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
		List<Object[]> result = list(query);
		return result;

	}

	@Override
	public AssessmentQuestion getQuestion(String gooruOid) {
		Query query = getSession().createQuery(FIND_QUESTION);
		query.setParameter(GOORU_OID, gooruOid);
		List<AssessmentQuestion> assessmentQuestions = list(query);
		return (assessmentQuestions.size() > 0) ? assessmentQuestions.get(0) : null;
	}

	@Override
	public SessionActivity getLastSessionActivity(Long parentId, Long contentId, String userUid) {
		Query query = getSession().createQuery(RETRIEVE_LAST_SESSION_ACTIVITY_BY_IDS);
		query.setParameter(PARENT_ID, parentId);
		query.setParameter(COLLECTION_ID, contentId);
		query.setParameter(USER_ID, userUid);
		query.setMaxResults(1);
		List<SessionActivity> sessionActivities = list(query);
		return (sessionActivities.size() > 0) ? sessionActivities.get(0) : null;
	}
	
	@Override
	public Map<String,Object> getSessionActivityByCollectionId(String gooruOid, String userUid)  {
		Query query = getSession().createSQLQuery(RETRIVE_INCOMPLETE_SESSION_ACTIVITY_ID);
		query.setParameter(COLLECTION_ID, gooruOid);
		query.setParameter(USER_UID, userUid);
		query.setMaxResults(1);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return (Map<String, Object>) query.list().get(0);
	}


}
