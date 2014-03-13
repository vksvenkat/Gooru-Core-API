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
