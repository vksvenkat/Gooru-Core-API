/*******************************************************************************
 * AssessmentHint.java
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

public class AssessmentHint implements Serializable,Comparable<AssessmentHint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5773944979028571352L;


	private Integer hintId;

	private String hintText;

	private Integer sequence;
	
	private AssessmentQuestion question;

	public AssessmentHint() {
	}

	public Integer getHintId() {
		return hintId;
	}

	public void setHintId(Integer hintId) {
		this.hintId = hintId;
	}

	public String getHintText() {
		return hintText;
	}

	public void setHintText(String hintText) {
		this.hintText = hintText;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}
	
	@Override
	public int compareTo(AssessmentHint assessmentHint) {

		if (assessmentHint != null && getSequence() != null && assessmentHint.getSequence() != null) {
			if (getSequence().equals(assessmentHint.getSequence())) {
				return 0;
			}
			return getSequence().compareTo(assessmentHint.getSequence());
		}
		return 0;
	}
	
}
