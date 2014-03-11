/*******************************************************************************
 * AssessmentAttemptItem.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;

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
