package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class AssessmentAnswer implements Serializable,Comparable<AssessmentAnswer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3000256378255711993L;


	private Integer answerId;

	private String answerText;
	
	private String answerType;

	private Boolean isCorrect;

	private Integer sequence;
	
	private String unit;

	private AssessmentAnswer matchingAnswer;
	
	private AssessmentQuestion question;
	
	private String answerExplanation;
	
	private String answerHint;
	
	private String answerGroupCode;
	
	private Integer matchingSequence;

	public AssessmentAnswer() {

	}

	public Integer getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	
	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	
	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public AssessmentAnswer getMatchingAnswer() {
		return matchingAnswer;
	}

	public void setMatchingAnswer(AssessmentAnswer matchingAnswer) {
		this.matchingAnswer = matchingAnswer;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}
	
	@Override
	public int compareTo(AssessmentAnswer assessmentAnswer) {

		if (assessmentAnswer != null && getSequence() != null && assessmentAnswer.getSequence() != null ) {
			if (getSequence().equals(assessmentAnswer.getSequence())) {
				return 0;
			}
			return getSequence().compareTo(assessmentAnswer.getSequence());
		}
		return 0;
	}

	public void setAnswerExplanation(String answerExplanation) {
		this.answerExplanation = answerExplanation;
	}

	public String getAnswerExplanation() {
		return answerExplanation;
	}

	public void setAnswerHint(String answerHint) {
		this.answerHint = answerHint;
	}

	public String getAnswerHint() {
		return answerHint;
	}

	public void setAnswerGroupCode(String answerGroupCode) {
		this.answerGroupCode = answerGroupCode;
	}

	public String getAnswerGroupCode() {
		return answerGroupCode;
	}

	public void setMatchingSequence(Integer matchingSequence) {
		this.matchingSequence = matchingSequence;
	}

	public Integer getMatchingSequence() {
		return matchingSequence;
	}

	
}
