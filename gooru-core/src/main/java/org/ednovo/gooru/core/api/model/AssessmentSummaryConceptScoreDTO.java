package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class AssessmentSummaryConceptScoreDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6737798265216267174L;


	private String concept;
	private Double score;

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}
