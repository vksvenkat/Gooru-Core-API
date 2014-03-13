package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import org.ednovo.gooru.core.api.model.AssessmentAnswer;

public class AssessmentAttemptTry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -332375205806580279L;

	
	public static enum STATUS {

		CORRECT("Correct", 1), IN_CORRECT("Incorrect", 0);
		private String status;
		private int id;

		STATUS(String status, int id) {
			this.status = status;
			this.id = id;
		}

		public String getStatus() {
			return status;
		}

		public int getId() {
			return id;
		}
	}

	private Date answeredAtTime;

	private AssessmentAnswer answer;

	private String answerText;

	private Integer attemptTryStatus;

	private Integer trySequence;
	
	private String triedAnswerStatus;
	
	private AssessmentAttemptItem assessmentAttemptItem;

	public AssessmentAttemptItem getAssessmentAttemptItem() {
		return assessmentAttemptItem;
	}

	public void setAssessmentAttemptItem(AssessmentAttemptItem assessmentAttemptItem) {
		this.assessmentAttemptItem = assessmentAttemptItem;
	}

	public Date getAnsweredAtTime() {
		return answeredAtTime;
	}

	public void setAnsweredAtTime(Date answeredAtTime) {
		this.answeredAtTime = answeredAtTime;
	}

	public AssessmentAnswer getAnswer() {
		return answer;
	}

	public void setAnswer(AssessmentAnswer answer) {
		this.answer = answer;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public Integer getTrySequence() {
		return trySequence;
	}

	public void setTrySequence(Integer trySequence) {
		this.trySequence = trySequence;
	}

	public Integer getAttemptTryStatus() {
		return attemptTryStatus;
	}

	public void setAttemptTryStatus(Integer attemptTryStatus) {
		this.attemptTryStatus = attemptTryStatus;
		if(attemptTryStatus != null){
			if (attemptTryStatus == STATUS.CORRECT.getId()) {
				triedAnswerStatus = STATUS.CORRECT.getStatus();
			} else if (attemptTryStatus == STATUS.IN_CORRECT.getId()) {
				triedAnswerStatus = STATUS.IN_CORRECT.getStatus();
			}
		}
	}

	public String getTriedAnswerStatus() {
		return triedAnswerStatus;
	}

	public void setTriedAnswerStatus(String triedAnswerStatus) {
		this.triedAnswerStatus = triedAnswerStatus;
		if (triedAnswerStatus != null) {
			if (triedAnswerStatus.equals(STATUS.CORRECT.getStatus())) {
				attemptTryStatus = STATUS.CORRECT.getId();
			} else if (triedAnswerStatus.equals(STATUS.IN_CORRECT.getStatus())) {
				attemptTryStatus = STATUS.IN_CORRECT.getId();
			}
		}	
	}
}
