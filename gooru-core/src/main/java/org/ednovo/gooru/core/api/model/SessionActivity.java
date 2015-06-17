package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2181056325925130088L;

	private Long sessionActivityId;

	private Long classId;

	private Integer sequence;

	private Integer viewsInSession;

	private Long timeSpentInMillis;

	private Integer reaction;

	private Integer rating;

	private Boolean isStudent;

	private String type;

	private Long collectionId;

	private String contentGooruId;

	private Double score;

	private String mode;

	private Date startTime;

	private Date endTime;

	private String status;

	private User user;

	private Long classContentId;
	
	private Long unitContentId;
	
	private Long lessonContentId;
	
	private String classGooruId;
	
	private String unitGooruId;
	
	private String lessonGooruId;
	
	private Double scoreInPercentage;
	
	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Double getScore() {
		return score;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getViewsInSession() {
		return viewsInSession;
	}

	public void setViewsInSession(Integer viewsInSession) {
		this.viewsInSession = viewsInSession;
	}

	public Long getTimeSpentInMillis() {
		return timeSpentInMillis;
	}

	public void setTimeSpentInMillis(Long timeSpentInMillis) {
		this.timeSpentInMillis = timeSpentInMillis;
	}

	public Integer getReaction() {
		return reaction;
	}

	public void setReaction(Integer reaction) {
		this.reaction = reaction;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Boolean getIsStudent() {
		return isStudent;
	}

	public void setIsStudent(Boolean isStudent) {
		this.isStudent = isStudent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSessionActivityId() {
		return sessionActivityId;
	}

	public void setSessionActivityId(Long sessionActivityId) {
		this.sessionActivityId = sessionActivityId;
	}

	public String getContentGooruId() {
		return contentGooruId;
	}

	public void setContentGooruId(String contentGooruId) {
		this.contentGooruId = contentGooruId;
	}

	public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	public Long getClassContentId() {
		return classContentId;
	}

	public void setClassContentId(Long classContentId) {
		this.classContentId = classContentId;
	}

	public Long getUnitContentId() {
		return unitContentId;
	}

	public void setUnitContentId(Long unitContentId) {
		this.unitContentId = unitContentId;
	}

	public Long getLessonContentId() {
		return lessonContentId;
	}

	public void setLessonContentId(Long lessonContentId) {
		this.lessonContentId = lessonContentId;
	}

	public String getClassGooruId() {
		return classGooruId;
	}

	public void setClassGooruId(String classGooruId) {
		this.classGooruId = classGooruId;
	}

	public String getUnitGooruId() {
		return unitGooruId;
	}

	public void setUnitGooruId(String unitGooruId) {
		this.unitGooruId = unitGooruId;
	}

	public String getLessonGooruId() {
		return lessonGooruId;
	}

	public void setLessonGooruId(String lessonGooruId) {
		this.lessonGooruId = lessonGooruId;
	}

	public Double getScoreInPercentage() {
		return scoreInPercentage;
	}

	public void setScoreInPercentage(Double scoreInPercentage) {
		this.scoreInPercentage = scoreInPercentage;
	}

}
