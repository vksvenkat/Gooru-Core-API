package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;

public class AssessmentSegmentQuestionAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3543831538455656303L;


	private AssessmentSegment segment;

	private AssessmentQuestion question;

	private Integer sequence;

	public AssessmentSegmentQuestionAssoc() {
		segment = new AssessmentSegment();
		question = new AssessmentQuestion();
	}

	public AssessmentSegment getSegment() {
		return segment;
	}

	public void setSegment(AssessmentSegment segment) {
		this.segment = segment;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

}