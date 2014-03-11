/*******************************************************************************
 * AssessmentAttemptSummaryDTO.java
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
import java.util.List;
import java.util.Map;

public class AssessmentAttemptSummaryDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1630954979050974383L;

	private Integer score;

	private Integer correctAnswerCount;

	private Integer totalQuestions;

	private Integer correctAnswersPercentage;

	private List<AttemptQuestionDTO> questionData;

	private Map<String,Object> socialAttemptScore;
	
	private Map<String, Integer> conceptsScore;
	
	public Map<String, Object> getSocialAttemptScore() {
		return socialAttemptScore;
	}

	public void setSocialAttemptScore(Map<String, Object> socialAttemptScore) {
		this.socialAttemptScore = socialAttemptScore;
	}

	public List<AttemptQuestionDTO> getQuestionData() {
		return questionData;
	}

	public void setQuestionData(List<AttemptQuestionDTO> questionData) {
		this.questionData = questionData;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getCorrectAnswerCount() {
		return correctAnswerCount;
	}

	public void setCorrectAnswerCount(Integer correctAnswerCount) {
		this.correctAnswerCount = correctAnswerCount;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getCorrectAnswersPercentage() {
		return correctAnswersPercentage;
	}

	public void setCorrectAnswersPercentage(Integer correctAnswersPercentage) {
		this.correctAnswersPercentage = correctAnswersPercentage;
	}

	public Map<String, Integer> getConceptsScore() {
		return conceptsScore;
	}

	public void setConceptsScore(Map<String, Integer> conceptsScore) {
		this.conceptsScore = conceptsScore;
	}
}
