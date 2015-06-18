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

import java.io.File;
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
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.eventlogs.SessionEventLog;
import org.ednovo.gooru.domain.service.resource.CSVBuilderService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.session.SessionActivityRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	
	private final String CLASS_ID = "classId";

	private final String PATHWAY_ID = "pathwayId";

	@Override
	public ActionResponseDTO<SessionActivity> createSessionActivity(final SessionActivity sessionActivity, final User user) {

		final Errors errors = this.validateCreateSessionActivity(sessionActivity, sessionActivity.getContentGooruId());
		
		if (!errors.hasErrors()) {
			StringBuilder listOfGooruOids = new StringBuilder();
			listOfGooruOids.append(sessionActivity.getContentGooruId());
			if (sessionActivity.getClassGooruId() != null && sessionActivity.getUnitGooruId() != null && sessionActivity.getLessonGooruId() != null) {
				listOfGooruOids.append(COMMA).append(sessionActivity.getClassGooruId());
				listOfGooruOids.append(COMMA).append(sessionActivity.getUnitGooruId());
				listOfGooruOids.append(COMMA).append(sessionActivity.getLessonGooruId());
			}
			Map<Object, Object> contentIds = new HashMap<Object, Object>();
			List<Object[]> listOfcontentId = getResourceRepository().getContentIds(listOfGooruOids.toString());
			for (Object[] contentId : listOfcontentId) {
				contentIds.put(contentId[0], contentId[1]);
			}
			final Long collectionId = ((Number) contentIds.get(sessionActivity.getContentGooruId())).longValue();

			if (sessionActivity.getClassGooruId() != null) {
				sessionActivity.setClassContentId(((Number) contentIds.get(sessionActivity.getClassGooruId())).longValue());
				sessionActivity.setLessonContentId(((Number) contentIds.get(sessionActivity.getLessonGooruId())).longValue());
				sessionActivity.setUnitContentId(((Number) contentIds.get(sessionActivity.getUnitGooruId())).longValue());
				sessionActivity.setIsStudent(getResourceRepository().findUserIsStudent(sessionActivity.getClassContentId(), user.getGooruUId()));
				sessionActivity.setClassId(getResourceRepository().getNumericClassCode(sessionActivity.getClassContentId()));
				sessionActivity.setSequence(getSessionActivityRepository().getClassSessionActivityCount(collectionId, sessionActivity.getClassContentId(), sessionActivity.getUnitContentId(),
						sessionActivity.getLessonContentId(), user.getGooruUId()) + 1);
			} else {
				sessionActivity.setIsStudent(false);
				sessionActivity.setClassId(1L);
				sessionActivity.setSequence(getSessionActivityRepository().getCollectionSessionActivityCount(collectionId,user.getGooruUId()) + 1);
			}
			sessionActivity.setScore(0.0);
			sessionActivity.setScoreInPercentage(0.0);
			sessionActivity.setReaction(0);
			sessionActivity.setRating(0);
			sessionActivity.setTimeSpentInMillis(0L);
			sessionActivity.setType(sessionActivity.getType());
			sessionActivity.setStatus(SessionStatus.OPEN.getSessionStatus());
			sessionActivity.setCollectionId(collectionId);
			sessionActivity.setViewsInSession(1);
			sessionActivity.setStartTime(new Date(System.currentTimeMillis()));
			sessionActivity.setUser(user);
			this.getSessionActivityRepository().save(sessionActivity);
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
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
			 * Score calculation for assessment/collection.It can be update through API from FE.
			 */
			if (newSession.getScore() != null) {
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
			
			this.getSessionActivityRepository().save(sessionActivity);
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
	public SessionActivity getSessionActivity(final Long sessionActivityId) {
		SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivityById(sessionActivityId);
		rejectIfNull(sessionActivity, GL0056, SESSION);
		return sessionActivity;
	}

	@Override
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
		sessionActivityItem.setAttemptCount(sessionActivityItemAttemptTry.getTrySequence());
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
	public File exportClass(String classGooruId) {
		String query = getSessionActivityRepository().getExportConfig(EXPORT_CLASS_QUERY);
		List<Object[]> resultSet = getSessionActivityRepository().getClassReport(classGooruId, query);
		String headers = getSessionActivityRepository().getExportConfig(EXPORT_CLASS_HEADER);
		return csvBuilderService.generateCSVReport(resultSet, headers.split(COMMA), EXPORT_CLASS_FILENAME);
	}
	@Override
	public SessionActivityItem updateLastResourceSessionActivityItem(SessionActivityItem sessionActivityItem) {
		rejectIfNull(sessionActivityItem.getPayLoadObject(), GL0056, COLLECTION);
		Map<String, String> data = JsonDeserializer.deserialize(sessionActivityItem.getPayLoadObject(), new TypeReference<Map<String, String>>() {
		});
		String collectionGooruId = data.get(COLLECTION_ID);
		rejectIfNull(collectionGooruId, GL0056, COLLECTION);
		String userId = data.get(USER_ID);
		rejectIfNull(userId, GL0056, USER);
		String parentGooruId = data.get(PATHWAY_ID);
		if (StringUtils.isBlank(parentGooruId)) {
			parentGooruId = data.get(CLASS_ID);
		}
		final Long collectionId = this.getResourceRepository().getContentId(collectionGooruId);
		rejectIfNull(collectionId, GL0056, COLLECTION);
		final Long parentId = this.getResourceRepository().getContentId(parentGooruId);
		SessionActivity sessionActivity = this.getSessionActivityRepository().getLastSessionActivity(parentId, collectionId, userId);
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
}