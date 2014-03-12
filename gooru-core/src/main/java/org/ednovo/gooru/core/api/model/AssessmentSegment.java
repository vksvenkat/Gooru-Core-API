package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Set;

public class AssessmentSegment implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4008533317368282177L;


	private Integer segmentId;

	private String name;

	private Integer sequence;

	private Integer timeToCompleteInSecs;

	private Set<AssessmentSegmentQuestionAssoc> segmentQuestions;
	
	private  Assessment assessment;
	
	private String segmentUId;

	public AssessmentSegment() {

	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Integer segmentId) {
		this.segmentId = segmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimeToCompleteInSecs() {
		return timeToCompleteInSecs;
	}

	public void setTimeToCompleteInSecs(Integer timeToCompleteInSecs) {
		this.timeToCompleteInSecs = timeToCompleteInSecs;
	}

	public Set<AssessmentSegmentQuestionAssoc> getSegmentQuestions() {
		return segmentQuestions;
	}

	public void setSegmentQuestions(Set<AssessmentSegmentQuestionAssoc> segmentQuestions) {
		this.segmentQuestions = segmentQuestions;
	}

	public void setAssessment(Assessment assessment) {
		this.assessment = assessment;
	}

	public Assessment getAssessment() {
		return assessment;
	}

	public void setSegmentUId(String segmentUId) {
		this.segmentUId = segmentUId;
	}

	public String getSegmentUId() {
		return segmentUId;
	}
}