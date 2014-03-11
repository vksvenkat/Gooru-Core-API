/*******************************************************************************
 * AssessmentSegment.java
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
