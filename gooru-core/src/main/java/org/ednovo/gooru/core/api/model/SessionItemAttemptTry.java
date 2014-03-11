/*******************************************************************************
 * SessionItemAttemptTry.java
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
	

}
