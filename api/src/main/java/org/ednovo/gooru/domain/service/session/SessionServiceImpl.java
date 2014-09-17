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
import org.ednovo.gooru.core.api.model.Session;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.SessionItem;
import org.ednovo.gooru.core.api.model.SessionItemAttemptTry;
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
	public ActionResponseDTO<Session> createSession(final Session session, final User user) {
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(session.getResource().getGooruOid());
		final Errors errors = this.validateCreateSession(session, resource);
		if (!errors.hasErrors()) {
			session.setScore(0.0);
			if (session.getSessionId() == null) {
				session.setSessionId(UUID.randomUUID().toString());
			}
			session.setStatus(SessionStatus.OPEN.getSessionStatus());
			session.setResource(resource);
			session.setStartTime(new Date(System.currentTimeMillis()));
			session.setUser(user);
			this.getSessionRepository().save(session);
		}
		return new ActionResponseDTO<Session>(session, errors);
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
	public ActionResponseDTO<Session> updateSession(final String sessionId, final Session newSession) {
		final Session session = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(session, GL0056, SESSION);
		final Errors errors = this.validateUpdateSession(session, newSession);
		if (!errors.hasErrors()) {
			if (newSession.getStatus() != null && newSession.getStatus().equalsIgnoreCase(SessionStatus.ARCHIVE.getSessionStatus())) {
				session.setStatus(newSession.getStatus());
				session.setEndTime(new Date(System.currentTimeMillis()));
			}

			if (newSession.getScore() != null) {
				session.setScore(newSession.getScore());
			}
			this.getSessionRepository().save(session);
		}
		return new ActionResponseDTO<Session>(session, errors);
	}

	@Override
	public Session getSession(final String sessionId) {
		Session session = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(session, GL0056, SESSION);
		return session;
	}

	@Override
	public ActionResponseDTO<SessionItem> createSessionItem(final SessionItem sessionItem, final String sessionId) {
		Errors errors = null;
		final Session session = this.getSessionRepository().findSessionById(sessionId);
		rejectIfNull(session, GL0056, SESSION);
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(sessionItem.getResource().getGooruOid());
		rejectIfNull(resource, GL0056, RESOURCE);
		if (sessionItem.getSessionItemId() == null) {
			sessionItem.setSessionItemId(UUID.randomUUID().toString());
		}
		errors = this.validateSessionItem(session, sessionItem, resource);
		if (!errors.hasErrors()) {
			final SessionItem previousItem = this.getSessionRepository().getLastSessionItem(sessionId);
			if (previousItem != null) {
				previousItem.setEndTime(new Date(System.currentTimeMillis()));
				this.getSessionRepository().save(previousItem);
			}
			sessionItem.setResource(resource);
			sessionItem.setsession(session);
			if (sessionItem.getCollectionItem() != null && sessionItem.getCollectionItem().getCollectionItemId() != null) {
				final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(sessionItem.getCollectionItem().getCollectionItemId());
				if (collectionItem != null) {
					sessionItem.setCollectionItem(collectionItem);
				}
			}
			sessionItem.setStartTime(new Date(System.currentTimeMillis()));

			this.getSessionRepository().save(sessionItem);
		}
		return new ActionResponseDTO<SessionItem>(sessionItem, errors);
	}

	@Override
	public ActionResponseDTO<SessionItem> updateSessionItem(final String sessionItemId, final SessionItem newSessionItem) {
		final SessionItem sessionItem = this.getSessionRepository().findSessionItemById(sessionItemId);
		rejectIfNull(sessionItem, GL0056, SESSION_ITEM);
		final Errors errors = this.validateUpdateSessionItem(sessionItem);
		if (!errors.hasErrors()) {
			if (newSessionItem.getAttemptItemStatus() != null) {
				sessionItem.setAttemptItemStatus(newSessionItem.getAttemptItemStatus());
			}
			if (newSessionItem.getCorrectTrySequence() != null) {
				sessionItem.setCorrectTrySequence(newSessionItem.getCorrectTrySequence());
			}
			if (newSessionItem.getEndTime() != null) {
				sessionItem.setEndTime(newSessionItem.getEndTime());
			}
			this.getSessionRepository().save(sessionItem);
		}
		return new ActionResponseDTO<SessionItem>(sessionItem, errors);
	}

	@Override
	public SessionItemAttemptTry createSessionItemAttemptTry(final SessionItemAttemptTry sessionItemAttemptTry, final String sessionItemId) {
		try{
			final SessionItem sessionItem = this.getSessionRepository().findSessionItemById(sessionItemId);
			rejectIfNull(sessionItem, GL0056, SESSION_ITEM);
			AssessmentQuestion question = new AssessmentQuestion();
			if (sessionItem.getResource().getResourceType() != null && sessionItem.getResource().getResourceType().getName().equalsIgnoreCase(ASSESSMENT_QUESTION)) {
				question = this.assessmentService.getQuestion(sessionItem.getResource().getGooruOid());
			}
			final Integer trySequence = this.getSessionRepository().getSessionItemAttemptTry(sessionItemId).size() + 1;
			if (question != null && question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.FILL_IN_BLANKS.getName()) || question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.OPEN_ENDED.getName()) || question.getTypeName().equals(AssessmentQuestion.TYPE.SHORT_ANSWER.getName())
					|| question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.MULTIPLE_ANSWERS.getName())) {
				rejectIfNull(sessionItemAttemptTry.getAnswerText(), GL0006, ANSWER_TEXT);
			} else if (question != null && question.getTypeName().equals(AssessmentQuestion.TYPE.MATCH_THE_FOLLOWING.getName())) {
				rejectIfNull(sessionItemAttemptTry.getAnswerText(), GL0006, ANSWER_TEXT);
				final String[] answerTexts = sessionItemAttemptTry.getAnswerText().split(",");
				for (int i = 0; i < answerTexts.length; i++) {
					final AssessmentAnswer answers = this.getAssessmentRepository().getAssessmentAnswerById(Integer.parseInt(answerTexts[i]));
					if (answers.getMatchingAnswer().getAnswerId().equals(Integer.parseInt(answerTexts[i == 0 ? i + 1 : i - 1]))) {
						sessionItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.CORRECT.getTryStatus());
						sessionItem.setCorrectTrySequence(trySequence);
						final Session session = sessionItem.getsession();
						session.setScore(sessionItem.getsession().getScore() + 1);
						this.getSessionRepository().save(session);
					} else {
						sessionItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.WRONG.getTryStatus());
					}
				}

			} else {
				final AssessmentAnswer assessmentAnswer = this.getAssessmentRepository().getAssessmentAnswerById(sessionItemAttemptTry.getAssessmentAnswer() != null ? sessionItemAttemptTry.getAssessmentAnswer().getAnswerId() : null);
				rejectIfNull(assessmentAnswer, GL0006, ASSESSMENT_ANSWER);
				sessionItemAttemptTry.setAssessmentAnswer(assessmentAnswer);
				if (assessmentAnswer.getIsCorrect()) {
					sessionItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.CORRECT.getTryStatus());
					sessionItem.setCorrectTrySequence(trySequence);
				} else {
					sessionItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.WRONG.getTryStatus());
				}
			}

			if (sessionItemAttemptTry.getAttemptItemTryStatus() == null) {
				sessionItemAttemptTry.setAttemptItemTryStatus(AttemptTryStatus.SKIP.getTryStatus());
			} else {
				sessionItemAttemptTry.setAttemptItemTryStatus(sessionItemAttemptTry.getAttemptItemTryStatus());
			}
			sessionItemAttemptTry.setSessionItem(sessionItem);
			sessionItemAttemptTry.setAnsweredAtTime(new Date(System.currentTimeMillis()));
			sessionItemAttemptTry.setTrySequence(trySequence);
			this.getSessionRepository().save(sessionItemAttemptTry);
			this.getSessionRepository().save(sessionItem);
		} catch(Exception e){
			SessionContextSupport.putLogParameter("sessionErrorLog", e.getMessage());
		}
		return sessionItemAttemptTry;
	}

	private Errors validateUpdateSessionItem(SessionItem sessionItem) {
		final Errors errors = new BindException(sessionItem, SESSION_ITEM);
		rejectIfNull(errors, sessionItem, SESSION_ITEM, GL0056, generateErrorMessage(GL0056, SESSION_ITEM));
		return errors;
	}

	private Errors validateCreateSession(final Session session, final Resource resource) {
		final Map<String, String> sessionMode = getSessionMode();
		final Errors errors = new BindException(session, SESSION);
		rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
		rejectIfInvalidType(errors, session.getMode(), MODE, GL0007, generateErrorMessage(GL0007, MODE), sessionMode);
		return errors;
	}

	private Errors validateUpdateSession(final Session session, final Session newSession) {
		final Map<String, String> sessionStatus = getSessionStatus();
		final Errors errors = new BindException(session, SESSION);
		rejectIfNull(errors, newSession, SESSION, GL0056, generateErrorMessage(GL0056, SESSION));
		rejectIfInvalidType(errors, newSession.getStatus(), STATUS, GL0007, generateErrorMessage(GL0007, STATUS), sessionStatus);
		return errors;
	}

	private Errors validateSessionItem(final Session session, final SessionItem sessionItem, final Resource resource) {
		final Errors errors = new BindException(sessionItem, SESSION_ITEM);
		rejectIfNull(errors, session, SESSION, GL0056, generateErrorMessage(GL0056, SESSION));
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