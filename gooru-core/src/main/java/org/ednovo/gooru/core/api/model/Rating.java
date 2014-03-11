/*******************************************************************************
 * Rating.java
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

public class Rating extends Annotation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6764612121583791123L;

	private Double score;

	private Integer count;
	
	private Double average;
	
	private Integer votesUp;
	
	private Integer votesDown;
	
	private Double votes;
	
	private Integer previousContentUserRating;

	private Integer point;

	private String type;

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getVotesUp() {
		return votesUp;
	}

	public void setVotesUp(Integer votesUp) {
		this.votesUp = votesUp;
	}

	public Integer getVotesDown() {
		return votesDown;
	}

	public void setVotesDown(Integer votesDown) {
		this.votesDown = votesDown;
	}

	public Double getVotes() {
		return votes;
	}

	public void setVotes(Double votes) {
		this.votes = votes;
	}

	public void setPreviousContentUserRating(Integer previousContentUserRating) {
		this.previousContentUserRating = previousContentUserRating;
	}

	public Integer getPreviousContentUserRating() {
		return previousContentUserRating;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
