package org.ednovo.gooru.core.api.model;

import java.util.Set;

import org.ednovo.gooru.core.api.model.Resource;

public class QuestionSet extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7267883111878851302L;


	private Set<QuestionSetQuestionAssoc> questionSetQuestions;

	public QuestionSet() {

	}

	public Set<QuestionSetQuestionAssoc> getQuestionSetQuestions() {
		return questionSetQuestions;
	}

	public void setQuestionSetQuestions(Set<QuestionSetQuestionAssoc> questionSetQuestions) {
		this.questionSetQuestions = questionSetQuestions;
	}

}