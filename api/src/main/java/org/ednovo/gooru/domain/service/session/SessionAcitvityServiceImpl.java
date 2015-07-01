/////////////////////////////////////////////////////////////
// SessionServiceImpl.java
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
package org.ednovo.gooru.domain.service.session;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AttemptTryStatus;
import org.ednovo.gooru.core.api.model.ModeType;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityItemAttemptTry;
import org.ednovo.gooru.core.api.model.SessionStatus;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserActivityCollectionAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.eventlogs.SessionEventLog;
import org.ednovo.gooru.domain.service.resource.CSVBuilderService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.session.SessionActivityRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class SessionAcitvityServiceImpl extends BaseServiceImpl implements SessionActivityService, ParameterProperties, ConstantProperties {

	@Autowired
	private SessionActivityRepository sessionActivityRepository;

	@Autowired
	private SessionEventLog sessionEventLog;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private CSVBuilderService csvBuilderService;
	
	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private ClassRepositoryHibernate classRepositoryHibernate;


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<SessionActivity> createSessionActivity(final SessionActivity sessionActivity, final User user) {

		final Errors errors = this.validateCreateSessionActivity(sessionActivity, sessionActivity.getContentGooruId());

		if (!errors.hasErrors()) {

			sessionActivity.setUser(user);
			sessionActivity.setScore(0.0);
			sessionActivity.setScoreInPercentage(0.0);
			sessionActivity.setReaction(0);
			sessionActivity.setRating(0);
			sessionActivity.setTimeSpentInMillis(0L);
			sessionActivity.setType(sessionActivity.getType());
			sessionActivity.setStatus(SessionStatus.OPEN.getSessionStatus());
			sessionActivity.setViewsInSession(1);
			sessionActivity.setStartTime(new Date(System.currentTimeMillis()));

			if (sessionActivity.getCourseGooruId() != null && sessionActivity.getUnitGooruId() != null && sessionActivity.getLessonGooruId() != null) {

				Map<Object, Object> contentIds = this.getContentIds(sessionActivity.getContentGooruId(), sessionActivity.getLessonGooruId(), sessionActivity.getUnitGooruId(),
						sessionActivity.getCourseGooruId());

				final Long collectionId = ((Number) contentIds.get(sessionActivity.getContentGooruId())).longValue();
				sessionActivity.setCollectionId(collectionId);
				sessionActivity.setCourseId(((Number) contentIds.get(sessionActivity.getCourseGooruId())).longValue());
				sessionActivity.setLessonContentId(((Number) contentIds.get(sessionActivity.getLessonGooruId())).longValue());
				sessionActivity.setUnitContentId(((Number) contentIds.get(sessionActivity.getUnitGooruId())).longValue());

				Integer perviousSessionCount = getSessionActivityRepository().getClassSessionActivityCount(collectionId, sessionActivity.getClassId(), sessionActivity.getUnitContentId(),
						sessionActivity.getLessonContentId(), user.getGooruUId());
				if (perviousSessionCount > 0) {
					this.getSessionActivityRepository().updateOldSessions(sessionActivity);
				}
				sessionActivity.setSequence(perviousSessionCount + 1);
				sessionActivity.setIsLastSession(true);
				Map<String, Object> classMap = this.getClassRepositoryHibernate().findStudentAndClassId(sessionActivity.getClassGooruId(), user.getGooruUId());
				if (classMap != null) {
					sessionActivity.setClassId(((Number) classMap.get(CLASS_ID)).longValue());
					sessionActivity.setIsStudent(((Boolean) classMap.get(IS_STUDENT)).booleanValue());
				}

			} else {
				final Long collectionId = getResourceRepository().getContentId(sessionActivity.getContentGooruId());
				sessionActivity.setIsStudent(false);
				sessionActivity.setClassId(0L);
				sessionActivity.setSequence(getSessionActivityRepository().getCollectionSessionActivityCount(collectionId, user.getGooruUId()) + 1);
				sessionActivity.setCollectionId(collectionId);
			}
			this.getSessionActivityRepository().save(sessionActivity);
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<SessionActivity> updateSessionActivity(final Long sessionActivityId, final SessionActivity newSession) {
		final SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivityById(sessionActivityId);
		rejectIfNull(sessionActivity, GL0056, SESSION_ACTIVITY);
		final Errors errors = this.validateUpdateSessionActivity(sessionActivity, newSession);
		if (!errors.hasErrors()) {
			if (newSession.getStatus() != null && newSession.getStatus().equalsIgnoreCase(SessionStatus.ARCHIVE.getSessionStatus())) {
				sessionActivity.setStatus(newSession.getStatus());
				sessionActivity.setEndTime(new Date(System.currentTimeMillis()));
				sessionActivity.setRating(this.getSessionActivityRepository().getSessionActivityRatingCount(sessionActivityId));
				sessionActivity.setReaction(this.getSessionActivityRepository().getSessionActivityReactionCount(sessionActivityId));
			}
			/**
			 * Time spent calculation for assessment/collection.It can be update through API from FE.
			 */
			if(newSession.getTimeSpentInMillis() != null){
				sessionActivity.setTimeSpentInMillis(newSession.getTimeSpentInMillis());
			}else{
				Long timeSpentInMillis = sessionActivity.getEndTime().getTime() - sessionActivity.getStartTime().getTime();
				sessionActivity.setTimeSpentInMillis(timeSpentInMillis);
			}
			
			if(newSession.getViewsInSession() != null){
				sessionActivity.setViewsInSession(newSession.getViewsInSession());
			}
			
			/**
			 * Score calculation for assessment/collection.It can be update through API from FE.
			 */
			if (newSession.getScore() != null && newSession.getScoreInPercentage() != null) {
				sessionActivity.setScore(newSession.getScore());
				sessionActivity.setScoreInPercentage(newSession.getScoreInPercentage());
			}else{
				Integer questionCount = this.getSessionActivityRepository().getQuestionCount(sessionActivity.getCollectionId());
				if (questionCount > 0) {
					Integer totalScore = this.getSessionActivityRepository().getTotalScore(sessionActivityId);
					Double scoreInPrecentage =  (double) (100 * totalScore / questionCount);
					sessionActivity.setScore(Double.valueOf(totalScore));
					sessionActivity.setScoreInPercentage(scoreInPrecentage);
				}
			}
			this.getSessionActivityRepository().save(sessionActivity);

			if (sessionActivity.getCourseId() != null && sessionActivity.getUnitContentId() != null && sessionActivity.getLessonContentId() != null) {
				double unitTotalScore =  this.getSessionActivityRepository().getUnitTotalScore(sessionActivity.getUnitContentId());
				int lessonCount =  this.getCollectionDao().getCollectionItemCount(sessionActivity.getUnitContentId(), LESSON);
				double unitScoreInPercentage = 0.0;
				if(lessonCount != 0){
					unitScoreInPercentage = (unitTotalScore/lessonCount);
				}
				
				UserActivityCollectionAssoc userActivityUnitAssoc = this.getSessionActivityRepository().getUserActivityCollectionAssoc(sessionActivity.getUser().getGooruUId(), sessionActivity.getClassId(), sessionActivity.getUnitContentId());
				this.createOrUpdateUserActivityCollectionAssoc(userActivityUnitAssoc, sessionActivity, sessionActivity.getUnitContentId(), unitTotalScore, unitScoreInPercentage);
				
				double lessonTotalScore =  this.getSessionActivityRepository().getLessonTotalScore(sessionActivity.getLessonContentId());
				int collectionCount =  this.getCollectionDao().getCollectionItemCount(sessionActivity.getUnitContentId(), COLLECTION);
				double lessonScoreInPercentage = 0.0;
				if(collectionCount != 0){
					lessonScoreInPercentage = (lessonTotalScore/collectionCount);
				}
				
				UserActivityCollectionAssoc userActivityLessonAssoc = this.getSessionActivityRepository().getUserActivityCollectionAssoc(sessionActivity.getUser().getGooruUId(), sessionActivity.getClassId(), sessionActivity.getLessonContentId());
				this.createOrUpdateUserActivityCollectionAssoc(userActivityLessonAssoc, sessionActivity, sessionActivity.getLessonContentId(), lessonTotalScore, lessonScoreInPercentage);
				
				UserActivityCollectionAssoc userActivityCollectionAssoc = this.getSessionActivityRepository().getUserActivityCollectionAssoc(sessionActivity.getUser().getGooruUId(), sessionActivity.getClassId(), sessionActivity.getCollectionId());
				this.createOrUpdateUserActivityCollectionAssoc(userActivityCollectionAssoc, sessionActivity, sessionActivity.getCollectionId(), sessionActivity.getScore(), sessionActivity.getScoreInPercentage());
			}
			
			
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SessionActivity getSessionActivity(final Long sessionActivityId) {
		SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivityById(sessionActivityId);
		rejectIfNull(sessionActivity, GL0056, SESSION);
		return sessionActivity;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SessionActivityItem createOrUpdateSessionActivityItem(final SessionActivityItem newSessionActivityItem, final Long sessionActivityId) {
		final Long resourceId = this.getResourceRepository().getContentId(newSessionActivityItem.getContentGooruId());
		rejectIfNull(resourceId, GL0056, RESOURCE);
		SessionActivityItem sessionActivityItem = this.getSessionActivityRepository().getSessionActivityItem(sessionActivityId, resourceId);
		if (sessionActivityItem == null) {
			sessionActivityItem = new SessionActivityItem();
			sessionActivityItem.setSessionActivityId(sessionActivityId);
			sessionActivityItem.setResourceId(resourceId);
			sessionActivityItem.setQuestionType(newSessionActivityItem.getQuestionType());
			sessionActivityItem.setStartTime(new Date(System.currentTimeMillis()));
			sessionActivityItem.setViewsInSession(1);
			sessionActivityItem.setRating(0);
			sessionActivityItem.setReaction(0);
			sessionActivityItem.setAttemptCount(0);
			sessionActivityItem.setScore(0.0);
			sessionActivityItem.setTimeSpentInMillis(0L);
			SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivityById(sessionActivityId);
			rejectIfNull(sessionActivity, GL0056, SESSION_ACTIVITY);
			sessionActivityItem.setClassId(sessionActivity.getClassId());
		} else {
			if (newSessionActivityItem.getStatus() != null && newSessionActivityItem.getStatus().equalsIgnoreCase(SessionStatus.OPEN.getSessionStatus())) {
				sessionActivityItem.setStartTime(new Date(System.currentTimeMillis()));
				sessionActivityItem.setViewsInSession(sessionActivityItem.getViewsInSession() + 1);
			} else if (newSessionActivityItem.getStatus() != null && newSessionActivityItem.getStatus().equalsIgnoreCase(SessionStatus.ARCHIVE.getSessionStatus())) {
				sessionActivityItem.setEndTime(new Date(System.currentTimeMillis()));
				if(newSessionActivityItem.getTimeSpentInMillis() != null){
					sessionActivityItem.setTimeSpentInMillis(newSessionActivityItem.getTimeSpentInMillis());
				}else{
					Long timeSpentInMillis = sessionActivityItem.getEndTime().getTime() - sessionActivityItem.getStartTime().getTime();
					sessionActivityItem.setTimeSpentInMillis(sessionActivityItem.getTimeSpentInMillis() + timeSpentInMillis);
				}
				if(newSessionActivityItem.getViewsInSession() != null){
					sessionActivityItem.setViewsInSession(newSessionActivityItem.getViewsInSession());
				}
			}

			if (newSessionActivityItem.getFeedbackText() != null) {
				sessionActivityItem.setFeedbackText(newSessionActivityItem.getFeedbackText());
				sessionActivityItem.setFeedbackProvidedTime(new Date(System.currentTimeMillis()));
				sessionActivityItem.setFeedbackProvidedUserUid(newSessionActivityItem.getFeedbackProvidedUserUid());
				sessionActivityItem.setContentGooruId(newSessionActivityItem.getContentGooruId());
				sessionActivityItem.setParentGooruId(newSessionActivityItem.getParentGooruId());
				sessionActivityItem.setPayLoadObject(newSessionActivityItem.getPayLoadObject());
				SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivityById(sessionActivityId);
				this.getSessionEventLog().getEventLogs(sessionActivity, sessionActivityItem, newSessionActivityItem.getFeedbackProvidedUserUid());
			}

			if (newSessionActivityItem.getRating() != null) {
				sessionActivityItem.setRating(newSessionActivityItem.getRating());
			}
			if (newSessionActivityItem.getReaction() != null) {
				sessionActivityItem.setReaction(newSessionActivityItem.getReaction());
			}
			
		}
		this.getSessionActivityRepository().save(sessionActivityItem);

		return sessionActivityItem;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SessionActivityItemAttemptTry createSessionActivityItemAttemptTry(final SessionActivityItemAttemptTry sessionActivityItemAttemptTry, final Long sessionActivityId) {
		AssessmentQuestion question = this.assessmentService.getQuestion(sessionActivityItemAttemptTry.getContentGooruId());
		rejectIfNull(question, GL0056, QUESTION);
		final SessionActivityItem sessionActivityItem = this.getSessionActivityRepository().getSessionActivityItem(sessionActivityId, question.getContentId());
		rejectIfNull(sessionActivityItem, GL0056, SESSION_ACTIVITY_ITEM);
		sessionActivityItemAttemptTry.setSessionActivityId(sessionActivityId);
		sessionActivityItemAttemptTry.setResourceId(question.getContentId());
		sessionActivityItemAttemptTry.setTrySequence((this.getSessionActivityRepository().getSessionActivityItemAttemptCount(sessionActivityId, question.getContentId()) + 1));
		sessionActivityItemAttemptTry.setStartTime(sessionActivityItem.getStartTime());
		sessionActivityItemAttemptTry.setEndTime(new Date(System.currentTimeMillis()));
		this.getSessionActivityRepository().save(sessionActivityItemAttemptTry);
		sessionActivityItem.setAnswerId(sessionActivityItemAttemptTry.getAnswerId());
		sessionActivityItem.setAnswerOptionSequence(sessionActivityItemAttemptTry.getAnswerOptionSequence());
		sessionActivityItem.setAnswerStatus(sessionActivityItemAttemptTry.getAnswerStatus());
		sessionActivityItem.setAttemptCount((sessionActivityItem.getAttemptCount() == null ? 0 : sessionActivityItem.getAttemptCount() + 1));
		sessionActivityItem.setAnswerText(sessionActivityItemAttemptTry.getAnswerText());
		if (StringUtils.isNotBlank(sessionActivityItem.getAnswerStatus()) && sessionActivityItem.getQuestionType() != null && !sessionActivityItem.getQuestionType().equalsIgnoreCase(AssessmentQuestion.TYPE.OPEN_ENDED.getName())  && !sessionActivityItem.getAnswerStatus().contains(AttemptTryStatus.WRONG.getTryStatus()) && !sessionActivityItem.getAnswerStatus().contains(AttemptTryStatus.SKIPPED.getTryStatus())) {
			sessionActivityItem.setScore(1.0);
		} else {
			sessionActivityItem.setScore(0.0);
		}
		this.getSessionActivityRepository().save(sessionActivityItem);
		return sessionActivityItemAttemptTry;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SessionActivityItem updateLastResourceSessionActivityItem(SessionActivityItem sessionActivityItem) {
		rejectIfNull(sessionActivityItem.getPayLoadObject(), GL0056, COLLECTION);
		Map<String, String> data = JsonDeserializer.deserialize(sessionActivityItem.getPayLoadObject(), new TypeReference<Map<String, String>>() {
		});
		String collectionGooruId = data.get(CONTENT_GOORU_ID);
		rejectIfNull(collectionGooruId, GL0056, COLLECTION);
		String userId = data.get(USER_ID);
		rejectIfNull(userId, GL0056, USER);
		Map<Object, Object> contentIds = this.getContentIds(data.get(CONTENT_GOORU_ID),data.get(COURSE_GOORU_ID),data.get(UNIT_GOORU_ID),data.get(LESSON_GOORU_ID));
		final Long classId = this.getClassRepositoryHibernate().getClassId(data.get(CLASS_GOORU_ID));
		SessionActivity sessionActivity = this.getSessionActivityRepository().getLastSessionActivity(classId,((Number) contentIds.get(data.get(COURSE_GOORU_ID))).longValue(),((Number) contentIds.get(data.get(UNIT_GOORU_ID))).longValue(),((Number) contentIds.get(data.get(LESSON_GOORU_ID))).longValue(), ((Number) contentIds.get(data.get(CONTENT_GOORU_ID))).longValue(), userId);
		rejectIfNull(sessionActivity, GL0056, SESSION_ACTIVITY);
		return createOrUpdateSessionActivityItem(sessionActivityItem, sessionActivity.getSessionActivityId());
	}

	private Errors validateCreateSessionActivity(final SessionActivity sessionActivity, final String collectionGooruId) {
		final Map<Object, String> sessionMode = getSessionMode();
		final Errors errors = new BindException(sessionActivity, SESSION_ACTIVITY);
		rejectIfNull(errors, collectionGooruId, COLLECTION, GL0056, generateErrorMessage(GL0056, COLLECTION));
		rejectIfInvalidType(errors, sessionActivity.getMode(), MODE, GL0007, generateErrorMessage(GL0007, MODE), sessionMode);
		return errors;
	}

	private Errors validateUpdateSessionActivity(final SessionActivity sessionActivity, final SessionActivity newSession) {
		final Map<Object, String> sessionStatus = getSessionStatus();
		final Errors errors = new BindException(sessionActivity, SESSION);
		rejectIfNull(errors, newSession, SESSION, GL0056, generateErrorMessage(GL0056, SESSION_ACTIVITY));
		rejectIfInvalidType(errors, newSession.getStatus(), STATUS, GL0007, generateErrorMessage(GL0007, STATUS), sessionStatus);
		return errors;
	}

	private Map<Object, String> getSessionStatus() {
		final Map<Object, String> sessionStatus = new HashMap<Object, String>();
		sessionStatus.put(SessionStatus.OPEN.getSessionStatus(), SESSION);
		sessionStatus.put(SessionStatus.ARCHIVE.getSessionStatus(), SESSION);
		return sessionStatus;
	}

	private Map<Object, String> getSessionMode() {
		final Map<Object, String> sessionMode = new HashMap<Object, String>();
		sessionMode.put(ModeType.TEST.getModeType(), SESSION);
		sessionMode.put(ModeType.PLAY.getModeType(), SESSION);
		sessionMode.put(ModeType.PRACTICE.getModeType(), SESSION);
		return sessionMode;
	}
	
	private void createOrUpdateUserActivityCollectionAssoc(UserActivityCollectionAssoc userActivityCollectionAssoc ,SessionActivity sessionActivity,Long collectionId,Double score,Double scoreInPercentage) {
		if(userActivityCollectionAssoc == null){ 
			userActivityCollectionAssoc = new UserActivityCollectionAssoc();
		}
		userActivityCollectionAssoc.setClassContentId(sessionActivity.getClassId());
		userActivityCollectionAssoc.setUserUid(sessionActivity.getUser().getGooruUId());
		userActivityCollectionAssoc.setCollectionId(collectionId);
		userActivityCollectionAssoc.setScoreInPercentage(scoreInPercentage);
		userActivityCollectionAssoc.setScore(score);
		userActivityCollectionAssoc.setCollectionAttemptCount(userActivityCollectionAssoc.getCollectionAttemptCount() == null? 1 : (userActivityCollectionAssoc.getCollectionAttemptCount() + 1));
		userActivityCollectionAssoc.setLastAccessedTime(new Date());
		userActivityCollectionAssoc.setTotalTimeSpentInMillis(userActivityCollectionAssoc.getTotalTimeSpentInMillis() == null ? sessionActivity.getTimeSpentInMillis() : (userActivityCollectionAssoc.getTotalTimeSpentInMillis() + sessionActivity.getTimeSpentInMillis()));
		userActivityCollectionAssoc.setViewsInSession(userActivityCollectionAssoc.getViewsInSession() == null ? sessionActivity.getViewsInSession() : (userActivityCollectionAssoc.getViewsInSession() + sessionActivity.getViewsInSession()));
		this.getSessionActivityRepository().save(userActivityCollectionAssoc);
	}

	private Map<Object, Object> getContentIds(String... gooruOids) {
		Map<Object, Object> contentIds = new HashMap<Object, Object>();
		
		StringBuilder listOfGooruOids = new StringBuilder();
		for (String gooruOid : gooruOids) {
			listOfGooruOids.append(listOfGooruOids.length() > 0 ? COMMA : EMPTY);
			listOfGooruOids.append(gooruOid);
		}
		List<Object[]> listOfcontentId = getResourceRepository().getContentIds(listOfGooruOids.toString());
		for (Object[] contentId : listOfcontentId) {
			contentIds.put(contentId[0], contentId[1]);
		}
		
		return contentIds;
	}
	@Override
	public Map<String, Object> getInCompleteSessionActivity(final String gooruOid, final String user) {
		return this.getSessionActivityRepository().getSessionActivityByCollectionId(gooruOid,user);
	}
	public SessionEventLog getSessionEventLog() {
		return sessionEventLog;
	}

	public SessionActivityRepository getSessionActivityRepository() {
		return sessionActivityRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}
	
	public CollectionDao getCollectionDao() {
		return collectionDao;
	}
	public ClassRepositoryHibernate getClassRepositoryHibernate() {
		return classRepositoryHibernate;
	}
}