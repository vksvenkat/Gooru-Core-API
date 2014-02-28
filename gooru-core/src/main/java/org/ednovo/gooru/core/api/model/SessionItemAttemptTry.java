package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;

import org.ednovo.gooru.core.api.model.AssessmentAnswer;

public class SessionItemAttemptTry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9125991535274843683L;

	
	private String sessionItemAttemptTryId;
	
	private SessionItem sessionItem;
	
	private AssessmentAnswer assessmentAnswer;
	
	private String answerText;
	
	private Integer trySequence;
	
	private String attemptItemTryStatus;
	
	private Date answeredAtTime;

	public SessionItem getSessionItem() {
		return sessionItem;
	}

	public void setSessionItem(SessionItem sessionItem) {
		this.sessionItem = sessionItem;
	}

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

	public void setSessionItemAttemptTryId(String sessionItemAttemptTryId) {
		this.sessionItemAttemptTryId = sessionItemAttemptTryId;
	}

	public String getSessionItemAttemptTryId() {
		return sessionItemAttemptTryId;
	}
	
	public static void main(String a[]) { 
		SessionItemAttemptTry  eventData = new SessionItemAttemptTry();
        Method[] methods = eventData.getClass().getMethods();
        for (Method method : methods) { 
                System.out.println(method.getName());
                
        }
}

}
