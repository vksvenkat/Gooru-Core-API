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