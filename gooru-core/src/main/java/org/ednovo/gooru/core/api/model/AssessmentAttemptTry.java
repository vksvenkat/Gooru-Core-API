/*******************************************************************************
 * AssessmentAttemptTry.java
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
