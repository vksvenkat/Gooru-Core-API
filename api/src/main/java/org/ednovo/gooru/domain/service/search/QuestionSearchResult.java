/*
*QuestionSearchResult.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.search;

import java.util.HashSet;
import java.util.Set;


public class QuestionSearchResult extends SearchResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7871934464419763101L;
	private String questionText;
	private Set<Object> answers;
	private Set<Object> hints;
	private String questionType;
	private Long questionId;
	private String concept;
	private String source;
	private String explanation;
	private String groupedQuizNames;
	private String groupedQuizIds;
	private String gooruOid;

	public QuestionSearchResult() {
		answers = new HashSet<Object>();
		hints = new HashSet<Object>();
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}


	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getGroupedQuizNames() {
		return groupedQuizNames;
	}

	public void setGroupedQuizNames(String groupedQuizNames) {
		this.groupedQuizNames = groupedQuizNames;
	}

	public String getGroupedQuizIds() {
		return groupedQuizIds;
	}

	public void setGroupedQuizIds(String groupedQuizIds) {
		this.groupedQuizIds = groupedQuizIds;
	}

	public Set<Object> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Object> answers) {
		this.answers = answers;
	}

	public Set<Object> getHints() {
		return hints;
	}

	public void setHints(Set<Object> hints) {
		this.hints = hints;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

}
