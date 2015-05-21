package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionActivityItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -203672596781106569L;

	private Long sessionActivityId;

	private Long classId;

	private Integer viewsInSession;

	private Long timeSpentInMillis;

	private Integer reaction;

	private Integer attemptCount;

	private Integer rating;

	private Double score;

	private String questionType;

	private Integer answerId;

	private Long resourceId;

	private String contentGooruId;
	
	private String answerStatus;

	private String answerText;

	private String feedbackProvidedUserUid;

	private Date feedbackProvidedTime;

	private String feedbackText;

	private Integer answerOptionSequence;

	private Date startTime;

	private Date endTime;
	
	private String status;
	
	private String payLoadObject; 

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Integer getViewsInSession() {
		return viewsInSession;
	}

	public void setViewsInSession(Integer viewsInSession) {
		this.viewsInSession = viewsInSession;
	}

	public Integer getReaction() {
		return reaction;
	}

	public void setReaction(Integer reaction) {
		this.reaction = reaction;
	}

	public Integer getAttemptCount() {
		return attemptCount;
	}

	public void setAttemptCount(Integer attemptCount) {
		this.attemptCount = attemptCount;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Integer getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}

	public String getAnswerStatus() {
		return answerStatus;
	}

	public void setAnswerStatus(String answerStatus) {
		this.answerStatus = answerStatus;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public String getFeedbackProvidedUserUid() {
		return feedbackProvidedUserUid;
	}

	public void setFeedbackProvidedUserUid(String feedbackProvidedUserUid) {
		this.feedbackProvidedUserUid = feedbackProvidedUserUid;
	}

	public Date getFeedbackProvidedTime() {
		return feedbackProvidedTime;
	}

	public void setFeedbackProvidedTime(Date feedbackProvidedTime) {
		this.feedbackProvidedTime = feedbackProvidedTime;
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(String feedbackText) {
		this.feedbackText = feedbackText;
	}

	public Integer getAnswerOptionSequence() {
		return answerOptionSequence;
	}

	public void setAnswerOptionSequence(Integer answerOptionSequence) {
		this.answerOptionSequence = answerOptionSequence;
	}

	public Long getSessionActivityId() {
		return sessionActivityId;
	}

	public void setSessionActivityId(Long sessionActivityId) {
		this.sessionActivityId = sessionActivityId;
	}

	public String getContentGooruId() {
		return contentGooruId;
	}

	public void setContentGooruId(String contentGooruId) {
		this.contentGooruId = contentGooruId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getTimeSpentInMillis() {
		return timeSpentInMillis;
	}

	public void setTimeSpentInMillis(Long timeSpentInMillis) {
		this.timeSpentInMillis = timeSpentInMillis;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getPayLoadObject() {
		return payLoadObject;
	}

	public void setPayLoadObject(String payLoadObject) {
		this.payLoadObject = payLoadObject;
	}

}
