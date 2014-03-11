/*******************************************************************************
 * AttemptQuestionDTO.java
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

import java.util.List;

import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentAttemptTry;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;

public class AttemptQuestionDTO {

	private String questionText;

	private String answer;

	private Integer type;

	private String correctAnswer;

	private Integer isCorrect;

	private String questionStatus;

	private String concept;

	private String assetURI;

	private String folder;

	private String correctlyAnsweredPercentage;

	private List<AssessmentAnswer> answers;

	private AssessmentQuestionAssetAssoc asset;

	private String explanation;
	
	private String gooruOid;
	
	private List<AssessmentAttemptTry> assessmentAttemptsTry;
	
	private Integer correctTrySequence;

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionStatus() {
		return questionStatus;
	}

	public void setQuestionStatus(String questionStatus) {
		if (questionStatus.equals("1")) {
			questionStatus = "Correct";
		} else if (questionStatus.equals("2")) {
			questionStatus = "Incorrect";
		} else if (questionStatus.equals("0")) {
			questionStatus = "Not Attempted";
		}
		this.questionStatus = questionStatus;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public int getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Integer isCorrect) {
		if (isCorrect == null) {
			isCorrect = 0;
		}
		this.isCorrect = isCorrect;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getCorrectlyAnsweredPercentage() {
		return correctlyAnsweredPercentage;
	}

	public void setCorrectlyAnsweredPercentage(String correctlyAnsweredPercentage) {
		this.correctlyAnsweredPercentage = correctlyAnsweredPercentage;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setAnswers(List<AssessmentAnswer> answers) {
		this.answers = answers;
	}

	public List<AssessmentAnswer> getAnswers() {
		return answers;
	}

	public void setAsset(AssessmentQuestionAssetAssoc asset) {
		this.asset = asset;
	}

	public AssessmentQuestionAssetAssoc getAsset() {
		return asset;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAssetURI() {
		return assetURI;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public List<AssessmentAttemptTry> getAssessmentAttemptsTry() {
		return assessmentAttemptsTry;
	}

	public void setAssessmentAttemptsTry(List<AssessmentAttemptTry> assessmentAttemptsTry) {
		this.assessmentAttemptsTry = assessmentAttemptsTry;
	}

	public Integer getCorrectTrySequence() {
		return correctTrySequence;
	}

	public void setCorrectTrySequence(Integer correctTrySequence) {
		this.correctTrySequence = correctTrySequence;
	}

}
