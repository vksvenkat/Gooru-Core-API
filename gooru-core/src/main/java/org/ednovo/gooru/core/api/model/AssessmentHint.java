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
