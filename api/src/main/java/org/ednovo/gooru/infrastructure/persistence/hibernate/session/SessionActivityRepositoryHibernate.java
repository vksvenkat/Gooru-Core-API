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
import org.ednovo.gooru.core.api.model.UserActivityCollectionAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
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

	private final String COLLECTION_QUESTION_COUNT = "select count(1) as count from collection_item ci inner join assessment_question  q on q.question_id = ci.resource_content_id where q.type_name <> 'OE' and ci.collection_content_id=:collectionId";

	private final String SESSION_ACTIVITY_TOTAL_SCORE = "select IFNULL(sum(score), 0) as count from session_activity_item where session_activity_id =:sessionActivityId";
	
	private final String FIND_QUESTION = "From AssessmentQuestion q   where q.gooruOid=:gooruOid";
	
	private final String RETRIEVE_LAST_SESSION_ACTIVITY_BY_IDS = "From SessionActivity s where s.collectionId=:collectionId and s.user.partyUid=:userId AND s.class_id =:classId AND s.course_id=:courseId AND s.unit_content_id =:unitId AND s.lesson_content_id =:lessonId AND s.is_last_session = 1";
	
	private final String RETRIVE_INCOMPLETE_SESSION_ACTIVITY_ID = "SELECT sa.user_uid as userUid,sa.session_activity_id as sessionActivityId,so.gooru_oid as collectionGooruOid,sai.gooru_oid as resourceGooruOid from session_activity sa inner join session_activity_item si on sa.session_activity_id=si.session_activity_id inner join content so on so.content_id=sa.collection_id left join content sai on sai.content_id=si.resource_id where sa.status='open' and sa.user_uid=:userUid and so.gooru_oid =:collectionId order by si.start_time DESC";

	private final String RETRIVE_CLASS_SESSION_COUNT = "select count(1) as count from session_activity where collection_id =:collectionId AND user_uid =:userId AND class_content_id=:classContentId AND unit_content_id=:unitContentId AND lesson_content_id=:lessonContentId";
	
	private final String RETRIVE_SESSION_COUNT = "select count(1) as count from session_activity where collection_id =:collectionId AND user_uid =:userId AND class_content_id is null AND unit_content_id is null AND lesson_content_id is null" ;
	
	private final String UPDATE_LAST_SESSION = "UPDATE session_activity SET is_last_session = 0 WHERE collection_id =:collectionId AND user_uid =:userUid AND class_id=:classId AND unit_content_id=:unitContentId AND lesson_content_id=:lessonContentId AND course_id=:courseId AND is_last_session = 1";
		
	private final String GET_LESSON_SCORE = "SELECT (score_in_percentage) AS scoreInPerCentage FROM session_activity WHERE lesson_content_id =:lessonContentId AND is_last_session = 1";
	
	private final String GET_UNIT_SCORE = "SELECT (score_in_percentage) AS scoreInPerCentage FROM session_activity WHERE unit_content_id =:unitContentId AND is_last_session = 1";
	
	private final String RETRIEVE_USER_ACTIVITY_COLLECTION_ASSOC = "From UserActivityCollectionAssoc  uaca  where uaca.userUid=:userUid AND uaca.collectionId=:collectionId AND uaca.classContentId=:classContentId";
	
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
	public UserActivityCollectionAssoc getUserActivityCollectionAssoc(String userUid,Long classContentId, Long collectionId) {
		Query query = getSession().createQuery(RETRIEVE_USER_ACTIVITY_COLLECTION_ASSOC);
		query.setParameter(USER_UID, userUid);
		query.setParameter(CLASS_CONTENT_ID, classContentId);
		query.setParameter(COLLECTION_ID, collectionId);
		List<UserActivityCollectionAssoc> userActivityCollectionAssoc = list(query);
		return (userActivityCollectionAssoc.size() > 0) ? userActivityCollectionAssoc.get(0) : null;
	}
	
	@Override
	public Integer getClassSessionActivityCount(Long collectionId, Long classContentId, Long unitContentId, Long lessonContentId, String gooruUId) {
		Query query = getSession().createSQLQuery(RETRIVE_CLASS_SESSION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(USER_ID, gooruUId);
		query.setParameter(CLASS_CONTENT_ID, classContentId);
		query.setParameter(UNIT_CONTENT_ID, unitContentId);
		query.setParameter(LESSON_CONTENT_ID, lessonContentId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}
	
	@Override
	public Integer getCollectionSessionActivityCount(Long collectionId, String gooruUId) {
		Query query = getSession().createSQLQuery(RETRIVE_SESSION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(USER_ID, gooruUId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}
	
	@Override
	public Integer getSessionActivityItemAttemptCount(Long sessionActivityId, Long resourceId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_ITEM_ATTEMPT_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		query.setParameter(RESOURCE_ID, resourceId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}

	@Override
	public Integer getSessionActivityReactionCount(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_REACTION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}

	@Override
	public Integer getSessionActivityRatingCount(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_RATING_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}

	@Override
	public Integer getQuestionCount(Long collectionId) {
		Query query = getSession().createSQLQuery(COLLECTION_QUESTION_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, collectionId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}
	
	@Override
	public Double getLessonTotalScore(Long lessonId) {
		Query query = getSession().createSQLQuery(GET_LESSON_SCORE).addScalar(SCORE_IN_PERCENTAGE,StandardBasicTypes.DOUBLE);
		query.setParameter(LESSON_CONTENT_ID, lessonId);
		List<Double> results = list(query);
		return results.size() > 0 ? results.get(0) : 0.0;
	}
	
	@Override
	public Double getUnitTotalScore(Long unitId) {
		Query query = getSession().createSQLQuery(GET_UNIT_SCORE).addScalar(SCORE_IN_PERCENTAGE,StandardBasicTypes.DOUBLE);
		query.setParameter(UNIT_CONTENT_ID, unitId);
		List<Double> results = list(query);
		return results.size() > 0 ? results.get(0) : 0.0;
	}
	
	@Override
	public Integer getTotalScore(Long sessionActivityId) {
		Query query = getSession().createSQLQuery(SESSION_ACTIVITY_TOTAL_SCORE).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(SESSION_ACTIVITY_ID, sessionActivityId);
		List<Integer> results = list(query);
		return results.size() > 0 ? results.get(0) : 0;
	}

	@Override
	public AssessmentQuestion getQuestion(String gooruOid) {
		Query query = getSession().createQuery(FIND_QUESTION);
		query.setParameter(GOORU_OID, gooruOid);
		List<AssessmentQuestion> assessmentQuestions = list(query);
		return (assessmentQuestions.size() > 0) ? assessmentQuestions.get(0) : null;
	}

	@Override
	public SessionActivity getLastSessionActivity(Long classId,Long courseId,Long unitId,Long lessonId, Long contentId, String userUid) {
		Query query = getSession().createQuery(RETRIEVE_LAST_SESSION_ACTIVITY_BY_IDS);
		query.setParameter(CLASS_ID, classId);
		query.setParameter(COURSE_ID, courseId);
		query.setParameter(UNIT_ID, unitId);
		query.setParameter(LESSION_ID, lessonId);
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

	@Override
	public void updateOldSessions(SessionActivity sessionActivity){
		Query query = getSession().createSQLQuery(UPDATE_LAST_SESSION);
		query.setParameter(USER_UID, sessionActivity.getUser().getUserUid());
		query.setParameter(CLASS_ID, sessionActivity.getClassId());
		query.setParameter(COURSE_ID, sessionActivity.getCourseId());
		query.setParameter(UNIT_CONTENT_ID, sessionActivity.getUnitContentId());
		query.setParameter(LESSON_CONTENT_ID, sessionActivity.getLessonContentId());
		query.setParameter(COLLECTION_ID, sessionActivity.getCollectionId());
		query.executeUpdate();
	} 
	
}
