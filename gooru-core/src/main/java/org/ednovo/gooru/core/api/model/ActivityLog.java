package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class ActivityLog implements Serializable {

	private static final long serialVersionUID = 2555372246589849600L;


	private Long activityLogId;
	private String eventId;
	private String eventName;
	private Date eventTime;
	private String type;
	private String userIp;
	private User user;
	private Content content;
	private Content parentContent;
	private String sessionToken;
	private String context;

	public Long getActivityLogId() {
		return activityLogId;
	}

	public void setActivityLogId(Long activityLogId) {
		this.activityLogId = activityLogId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventId() {
		return eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Content getParentContent() {
		return parentContent;
	}

	public void setParentContent(Content parentContent) {
		this.parentContent = parentContent;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

}
