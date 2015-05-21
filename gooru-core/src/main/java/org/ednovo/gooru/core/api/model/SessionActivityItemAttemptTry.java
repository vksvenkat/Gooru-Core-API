package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionActivityItemAttemptTry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9125991535274843683L;

	private Long sessionActivityId;

	private Long resourceId;

	private String contentGooruId;

	private Integer answerId;

	private String answerText;

	private Integer trySequence;

	private String answerStatus;

	private Date startTime;
	
	private Date endTime;

	private Integer answerOptionSequence;

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public void setTrySequence(Integer trySequence) {
		this.trySequence = trySequence;
	}

	public Integer getTrySequence() {
		return trySequence;
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

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getContentGooruId() {
		return contentGooruId;
	}

	public void setContentGooruId(String contentGooruId) {
		this.contentGooruId = contentGooruId;
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
