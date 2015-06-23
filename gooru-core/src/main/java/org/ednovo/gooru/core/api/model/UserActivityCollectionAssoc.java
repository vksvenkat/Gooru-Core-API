package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserActivityCollectionAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7666934386633532053L;

	private String userUid;

	private Long classContentId;

	private Long collectionId;

	private String evidence;

	private Long totalTimeSpentInMillis;
	
	private Double scoreInPercentage;

	private Double score;

	private Integer collectionAttemptCount;

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public Long getTotalTimeSpentInMillis() {
		return totalTimeSpentInMillis;
	}

	public void setTotalTimeSpentInMillis(Long totalTimeSpentInMillis) {
		this.totalTimeSpentInMillis = totalTimeSpentInMillis;
	}

	public Double getScoreInPercentage() {
		return scoreInPercentage;
	}

	public void setScoreInPercentage(Double scoreInPercentage) {
		this.scoreInPercentage = scoreInPercentage;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Integer getCollectionAttemptCount() {
		return collectionAttemptCount;
	}

	public void setCollectionAttemptCount(Integer collectionAttemptCount) {
		this.collectionAttemptCount = collectionAttemptCount;
	}

	public Long getClassContentId() {
		return classContentId;
	}

	public void setClassContentId(Long classContentId) {
		this.classContentId = classContentId;
	}
	
}
