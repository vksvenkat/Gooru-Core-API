package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.User;


public class ActivityStream implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4547664876328803434L;
	/**
	 * 
	 */

	private Long activityStreamId;
	private User user;
	private ActivityType activityType;
	private String sharing;
	
	public Long getActivityStreamId() {
		return activityStreamId;
	}
	public void setActivityStreamId(Long activityStreamId) {
		this.activityStreamId = activityStreamId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	public String getSharing() {
		return sharing;
	}
	public void setSharing(String sharing) {
		this.sharing = sharing;
	}
}
