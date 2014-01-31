package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class TrackActivity extends OrganizationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8068624977782192272L;

	private String activityId;
	
	private String activityType;
	
	private Date startTime;
	
	private Date endTime;
	
	private User user;
	
	private TrackActivity parentId;
	
	private Task task;

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setParentId(TrackActivity parentId) {
		this.parentId = parentId;
	}

	public TrackActivity getParentId() {
		return parentId;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}
}
