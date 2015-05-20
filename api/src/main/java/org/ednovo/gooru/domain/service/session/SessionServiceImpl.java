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
import java.util.Map;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AttemptTryStatus;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.ModeType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityItemAttemptTry;
import org.ednovo.gooru.core.api.model.SessionItemFeedback;
import org.ednovo.gooru.core.api.model.SessionStatus;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.eventlogs.SessionEventLog;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.session.SessionRepository;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SessionServiceImpl extends BaseServiceImpl implements SessionService, ParameterProperties, ConstantProperties {

	@Autowired
	private SessionRepository sessionRepository;
	
	@Autowired
	private SessionEventLog sessionEventLog;
	
	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private UserRepository userRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionServiceImpl.class);

	@Override
	public ActionResponseDTO<SessionActivity> createSession(final SessionActivity sessionActivity, final User user) {
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(sessionActivity.getResource().getGooruOid());
		final Errors errors = this.validateCreateSession(sessionActivity, resource);
		if (!errors.hasErrors()) {
			int sessionSequence = 1;
			long parentId = 0L;
			boolean isStudent = false;
			long classId = 1L;
					
			if (sessionActivity.getParentGooruOid() != null) {
				 parentId = getResourceRepository().getContentId(sessionActivity.getParentGooruOid());
				 isStudent = getResourceRepository().findUserIsStudent(parentId, user.getGooruUId());
				 classId = getResourceRepository().getNumericClassCode(parentId);
			}
			sessionSequence = (sessionSequence + getResourceRepository().getSessionCount(resource.getContentId(), parentId, user.getGooruUId()));
			sessionActivity.setSequence(sessionSequence);
			sessionActivity.setParentId(parentId);
			sessionActivity.setClassId(classId);
			sessionActivity.setIsStudent(isStudent);
			sessionActivity.setScore(0.0);
			sessionActivity.setType(sessionActivity.getType());
			sessionActivity.setStatus(SessionStatus.OPEN.getSessionStatus());
			sessionActivity.setResource(resource);
			sessionActivity.setViewsInSession(1);
			sessionActivity.setTimeSpentInMillis(0);
			sessionActivity.setReaction(0);
			sessionActivity.setRating(0);
			sessionActivity.setStartTime(new Date(System.currentTimeMillis()));
			sessionActivity.setUser(user);
			this.getSessionRepository().save(sessionActivity);
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
	public SessionItemFeedback createSessionItemFeedback(final String sessionId, SessionItemFeedback sessionItemFeedback, final User user) {
		final User feedbackUser = this.getUserRepository().findByGooruId(sessionItemFeedback.getUser().getPartyUid());
		rejectIfNull(feedbackUser, GL0056, USER);
		final SessionItemFeedback sessionItemFeedbackUpdate = this.getSessionRepository().getSessionItemFeedback(sessionItemFeedback.getContentGooruOId(), feedbackUser.getGooruUId());
		if (sessionItemFeedbackUpdate == null) {
			sessionItemFeedback.setCreatedOn(new Date(System.currentTimeMillis()));
			sessionItemFeedback.setFreeText(sessionItemFeedback.getFreeText());
			sessionItemFeedback.setFeedbackProvidedBy(user);
			sessionItemFeedback.setSessionId(sessionId);
			sessionItemFeedback.setUser(feedbackUser);
			this.getSessionRepository().save(sessionItemFeedback);
		} else {
			sessionItemFeedbackUpdate.setFreeText(sessionItemFeedback.getFreeText());
			this.getSessionRepository().save(sessionItemFeedbackUpdate);
			sessionItemFeedback.setCreatedOn(new Date(System.currentTimeMillis()));
		}
		try {
			this.getSessionEventLog().getEventLogs(sessionItemFeedback, user);
		} catch (JSONException e) {
			LOGGER.debug("error"+e.getMessage());
		}
		return sessionItemFeedback;
	}
	
	@Override
	public ActionResponseDTO<SessionActivity> updateSession(final String sessionId, final SessionActivity newSession) {
		final SessionActivity sessionActivity = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(sessionActivity, GL0056, SESSION);
		final Errors errors = this.validateUpdateSession(sessionActivity, newSession);
		if (!errors.hasErrors()) {
			if (newSession.getStatus() != null && newSession.getStatus().equalsIgnoreCase(SessionStatus.ARCHIVE.getSessionStatus())) {
				sessionActivity.setStatus(newSession.getStatus());
				sessionActivity.setEndTime(new Date(System.currentTimeMillis()));
			}

			if (newSession.getScore() != null) {
				sessionActivity.setScore(newSession.getScore());
			}
			this.getSessionRepository().save(sessionActivity);
		}
		return new ActionResponseDTO<SessionActivity>(sessionActivity, errors);
	}

	@Override
	public SessionActivity getSession(final String sessionId) {
		SessionActivity sessionActivity = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(sessionActivity, GL0056, SESSION);
		return sessionActivity;
	}

	@Override
	public ActionResponseDTO<SessionActivityItem> createSessionItem(final SessionActivityItem sessionActivityItem, final String sessionId) {
		Errors errors = null;
		final SessionActivity sessionActivity = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(sessionActivity, GL0056, SESSION);
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(sessionActivityItem.getResource().getGooruOid());
		rejectIfNull(resource, GL0056, RESOURCE);
		/*if (sessionActivityItem.getSessionItemId() == null) {
			sessionActivityItem.setSessionItemId(UUID.randomUUID().toString());
		}*/
		errors = this.validateSessionItem(sessionActivity, sessionActivityItem, resource);
		if (!errors.hasErrors()) {
			final SessionActivityItem previousItem = this.getSessionRepository().getLastSessionItem(sessionId);
			if (previousItem != null) {
				previousItem.setEndTime(new Date(System.currentTimeMillis()));
				this.getSessionRepository().save(previousItem);
			}
			sessionActivityItem.setResource(resource);
			//sessionActivityItem.setSessionActivity(sessionActivity);
			/*if (sessionActivityItem.getCollectionItem() != null && sessionActivityItem.getCollectionItem().getCollectionItemId() != null) {
				final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(sessionActivityItem.getCollectionItem().getCollectionItemId());
				if (collectionItem != null) {
					sessionActivityItem.setCollectionItem(collectionItem);
				}
			}*/
			sessionActivityItem.setStartTime(new Date(System.currentTimeMillis()));

			this.getSessionRepository().save(sessionActivityItem);
		}
		return new ActionResponseDTO<SessionActivityItem>(sessionActivityItem, errors);
	}

	@Override
	public ActionResponseDTO<SessionActivityItem> updateSessionItem(final String sessionItemId, final SessionActivityItem newSessionItem) {
		final SessionActivityItem sessionActivityItem = this.getSessionRepository().findSessionItemById(sessionItemId);
		rejectIfNull(sessionActivityItem, GL0056, SESSION_ITEM);
		final Errors errors = this.validateUpdateSessionItem(sessionActivityItem);
		if (!errors.hasErrors()) {
			if (newSessionItem.getAnswerStatus() != null) {
				sessionActivityItem.setAnswerStatus(newSessionItem.getAnswerStatus());
			}
			if (newSessionItem.getAnswerOptionSequence() != null) {
				sessionActivityItem.setAnswerOptionSequence(newSessionItem.getAnswerOptionSequence());
			}
			if (newSessionItem.getEndTime() != null) {
				sessionActivityItem.setEndTime(newSessionItem.getEndTime());
			}
			this.getSessionRepository().save(sessionActivityItem);
		}
		return new ActionResponseDTO<SessionActivityItem>(sessionActivityItem, errors);
	}

	@Override
	public SessionActivityItemAttemptTry createSessionItemAttemptTry(final SessionActivityItemAttemptTry sessionActivityItemAttemptTry, final String sessionItemId) {
		
			final SessionActivityItem sessionActivityItem = this.getSessionRepository().findSessionItemById(sessionItemId);
			rejectIfNull(sessionActivityItem, GL0056, SESSION_ITEM);
			AssessmentQuestion question = null;
			if (sessionActivityItem.getResource().getResourceType() != null && sessionActivityItem.getResource().getResourceType().getName().equalsIgnoreCase(ASSESSMENT_QUESTION)) {
				question = this.assessmentService.getQuestion(sessionActivityItem.getResource().getGooruOid());
			}
			final Integer trySequence = this.getSessionRepository().getSessionItemAttemptTry(sessionItemId).size() + 1;
			if (question != null && (question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.FILL_IN_BLANKS.getName()) || question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.OPEN_ENDED.getName()) || question.getTypeName().equals(AssessmentQuestion.TYPE.SHORT_ANSWER.getName())
					|| question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.MULTIPLE_ANSWERS.getName()))) {
				rejectIfNull(sessionActivityItemAttemptTry.getAnswerText(), GL0006, ANSWER_TEXT);
			} else if (question != null && question.getTypeName().equals(AssessmentQuestion.TYPE.MATCH_THE_FOLLOWING.getName())) {
				rejectIfNull(sessionActivityItemAttemptTry.getAnswerText(), GL0006, ANSWER_TEXT);
				final String[] answerTexts = sessionActivityItemAttemptTry.getAnswerText().split(",");
				for (int i = 0; i < answerTexts.length; i++) {
					final AssessmentAnswer answers = this.getAssessmentRepository().getAssessmentAnswerById(Integer.parseInt(answerTexts[i]));
					if (answers.getMatchingAnswer().getAnswerId().equals(Integer.parseInt(answerTexts[i == 0 ? i + 1 : i - 1]))) {
						sessionActivityItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.CORRECT.getTryStatus());
						sessionActivityItem.setAnswerOptionSequence(trySequence);
						//final SessionActivity sessionActivity = sessionActivityItem.getSessionActivity();
						//sessionActivity.setScore(sessionActivityItem.getSessionActivity().getScore() + 1);
						//this.getSessionRepository().save(sessionActivity);
					} else {
						sessionActivityItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.WRONG.getTryStatus());
					}
				}

			} else {
				final AssessmentAnswer assessmentAnswer = this.getAssessmentRepository().getAssessmentAnswerById(sessionActivityItemAttemptTry.getAssessmentAnswer() != null ? sessionActivityItemAttemptTry.getAssessmentAnswer().getAnswerId() : null);
				rejectIfNull(assessmentAnswer, GL0006, ASSESSMENT_ANSWER);
				sessionActivityItemAttemptTry.setAssessmentAnswer(assessmentAnswer);
				if (assessmentAnswer.getIsCorrect()) {
					sessionActivityItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.CORRECT.getTryStatus());
					sessionActivityItem.setAnswerOptionSequence(trySequence);
				} else {
					sessionActivityItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.WRONG.getTryStatus());
				}
			}

			if (sessionActivityItemAttemptTry.getAttemptItemTryStatus() == null) {
				sessionActivityItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.SKIP.getTryStatus());
			} else {
				sessionActivityItemAttemptTry.setAttemptItemTryStatus(sessionActivityItemAttemptTry.getAttemptItemTryStatus());
			}
			sessionActivityItemAttemptTry.setAnsweredAtTime(new Date(System.currentTimeMillis()));
			sessionActivityItemAttemptTry.setTrySequence(trySequence);
			this.getSessionRepository().save(sessionActivityItemAttemptTry);
			this.getSessionRepository().save(sessionActivityItem);
		return sessionActivityItemAttemptTry;
	}

	private Errors validateUpdateSessionItem(SessionActivityItem sessionActivityItem) {
		final Errors errors = new BindException(sessionActivityItem, SESSION_ITEM);
		rejectIfNull(errors, sessionActivityItem, SESSION_ITEM, GL0056, generateErrorMessage(GL0056, SESSION_ITEM));
		return errors;
	}

	private Errors validateCreateSession(final SessionActivity sessionActivity, final Resource resource) {
		final Map<String, String> sessionMode = getSessionMode();
		final Errors errors = new BindException(sessionActivity, SESSION);
		rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
		rejectIfInvalidType(errors, sessionActivity.getMode(), MODE, GL0007, generateErrorMessage(GL0007, MODE), sessionMode);
		return errors;
	}

	private Errors validateUpdateSession(final SessionActivity sessionActivity, final SessionActivity newSession) {
		final Map<String, String> sessionStatus = getSessionStatus();
		final Errors errors = new BindException(sessionActivity, SESSION);
		rejectIfNull(errors, newSession, SESSION, GL0056, generateErrorMessage(GL0056, SESSION));
		rejectIfInvalidType(errors, newSession.getStatus(), STATUS, GL0007, generateErrorMessage(GL0007, STATUS), sessionStatus);
		return errors;
	}

	private Errors validateSessionItem(final SessionActivity sessionActivity, final SessionActivityItem sessionActivityItem, final Resource resource) {
		final Errors errors = new BindException(sessionActivityItem, SESSION_ITEM);
		rejectIfNull(errors, sessionActivity, SESSION, GL0056, generateErrorMessage(GL0056, SESSION));
		rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
		return errors;
	}

	private Map<String, String> getSessionStatus() {
		final Map<String, String> sessionStatus = new HashMap<String, String>();
		sessionStatus.put(SessionStatus.OPEN.getSessionStatus(), SESSION);
		sessionStatus.put(SessionStatus.ARCHIVE.getSessionStatus(), SESSION);
		return sessionStatus;
	}

	private Map<String, String> getSessionMode() {
		final Map<String, String> sessionMode = new HashMap<String, String>();
		sessionMode.put(ModeType.TEST.getModeType(), SESSION);
		sessionMode.put(ModeType.PLAY.getModeType(), SESSION);
		sessionMode.put(ModeType.PRACTICE.getModeType(), SESSION);
		return sessionMode;
	}

	public SessionEventLog getSessionEventLog() {
		return sessionEventLog;
	}

	public SessionRepository getSessionRepository() {
		return sessionRepository;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public AssessmentRepository getAssessmentRepository() {
		return assessmentRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}
	
}