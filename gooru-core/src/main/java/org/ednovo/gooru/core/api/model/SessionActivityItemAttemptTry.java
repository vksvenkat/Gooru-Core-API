package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionActivityItemAttemptTry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9125991535274843683L;
	
	private Long sessionActivityId;
	
	private Resource resource;
	
	private AssessmentAnswer assessmentAnswer;
	
	private String answerText;
	
	private Integer trySequence;
	
	private String attemptItemTryStatus;
	
	private Date answeredAtTime;
	
	private Integer answerOptionSequence;

	public AssessmentAnswer getAssessmentAnswer() {
		return assessmentAnswer;
	}

	public void setAssessmentAnswer(AssessmentAnswer assessmentAnswer) {
		this.assessmentAnswer = assessmentAnswer;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public String getAttemptItemTryStatus() {
		return attemptItemTryStatus;
	}

	public void setAttemptItemTryStatus(String attemptItemTryStatus) {
		this.attemptItemTryStatus = attemptItemTryStatus;
	}

	public void setAnsweredAtTime(Date answeredAtTime) {
		this.answeredAtTime = answeredAtTime;
	}

	public Date getAnsweredAtTime() {
		return answeredAtTime;
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

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Long getSessionActivityId() {
		return sessionActivityId;
	}

	public void setSessionActivityId(Long sessionActivityId) {
		this.sessionActivityId = sessionActivityId;
	}
}
