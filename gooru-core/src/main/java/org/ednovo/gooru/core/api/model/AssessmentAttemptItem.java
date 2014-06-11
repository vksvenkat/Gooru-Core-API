package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class AssessmentAttemptItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3049384594736271030L;


	public static enum STATUS {

		CORRECT("Correct", 1), IN_CORRECT("Incorrect", 2), NOT_ATTEMPTED(
				"Not Attempted", 0);

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

	private Integer attemptItemId;

	private AssessmentQuestion question;

	private Date presentedAtTime;

	private Integer attemptStatus;

	private String statusName;
	
	private Integer correctTryId;
	
	public AssessmentAttemptItem() {
		question = new AssessmentQuestion();
	}

	public Integer getAttemptItemId() {
		return attemptItemId;
	}

	public void setAttemptItemId(Integer attemptItemId) {
		this.attemptItemId = attemptItemId;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

	public Date getPresentedAtTime() {
		return presentedAtTime;
	}

	public void setPresentedAtTime(Date presentedAtTime) {
		this.presentedAtTime = presentedAtTime;
	}

	public Integer getAttemptStatus() {
		return attemptStatus;
	}

	public void setAttemptStatus(Integer attemptStatus) {
		this.attemptStatus = attemptStatus;
		if (attemptStatus != null) {
			if (attemptStatus == STATUS.CORRECT.getId()) {
				statusName = STATUS.CORRECT.getStatus();
			} else if (attemptStatus == STATUS.IN_CORRECT.getId()) {
				statusName = STATUS.IN_CORRECT.getStatus();
			} else if (attemptStatus == STATUS.NOT_ATTEMPTED.getId()) {
				statusName = STATUS.NOT_ATTEMPTED.getStatus();
			}
		}
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
		if (statusName != null) {
			if (statusName.equals(STATUS.CORRECT.getStatus())) {
				attemptStatus = STATUS.CORRECT.getId();
			} else if (statusName.equals(STATUS.IN_CORRECT.getStatus())) {
				attemptStatus = STATUS.IN_CORRECT.getId();
			} else if (statusName.equals(STATUS.NOT_ATTEMPTED.getStatus())) {
				attemptStatus = STATUS.NOT_ATTEMPTED.getId();
			}
		}

	}

	public Integer getCorrectTryId() {
		return correctTryId;
	}

	public void setCorrectTryId(Integer correctTryId) {
		this.correctTryId = correctTryId;
	}

}