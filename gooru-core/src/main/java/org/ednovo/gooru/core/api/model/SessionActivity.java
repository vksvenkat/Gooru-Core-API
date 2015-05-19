package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.GeneratedValue;

public class SessionActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2181056325925130088L;
	
	private Long sessionActivityId;

	private Long classId;

	private Long parentId;
	
	private String parentGooruOid;
	
	private Integer sequence;
	
	private Integer viewsInSession;
	
	private Integer timeSpentInMillis;
	
	private Integer reaction;
	
	private Integer rating;
	
	private Boolean isStudent;
	
	private String type;
	
	private Resource resource;

	private Double score;

	private String mode;

	private Date startTime;

	private Date endTime;

	private String status;

	private User user;
	
	private Set<SessionActivityItem> sessionActivityItems;

	public String getParentGooruOid() {
		return parentGooruOid;
	}

	public void setParentGooruOid(String parentGooruOid) {
		this.parentGooruOid = parentGooruOid;
	}

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

	public void setSessionItems(Set<SessionActivityItem> sessionActivityItems) {
		this.sessionActivityItems = sessionActivityItems;
	}

	public Set<SessionActivityItem> getSessionItems() {
		return sessionActivityItems;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}


	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public Integer getTimeSpentInMillis() {
		return timeSpentInMillis;
	}

	public void setTimeSpentInMillis(Integer timeSpentInMillis) {
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
}
