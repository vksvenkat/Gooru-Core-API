/*******************************************************************************
 * QuestionSetQuestionAssoc.java
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

import org.ednovo.gooru.core.api.model.AssessmentQuestion;

public class QuestionSetQuestionAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2847963761401373528L;

	/**
	 * 
	 */
	

	private QuestionSet questionSet;

	private AssessmentQuestion question;

	private Integer sequence;

	public QuestionSetQuestionAssoc() {
		questionSet = new QuestionSet();
		question = new AssessmentQuestion();

	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public QuestionSet getQuestionSet() {
		return questionSet;
	}

	public void setQuestionSet(QuestionSet questionSet) {
		this.questionSet = questionSet;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

}
