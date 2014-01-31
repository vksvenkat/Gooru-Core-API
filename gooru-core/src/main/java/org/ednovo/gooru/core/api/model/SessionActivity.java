package org.ednovo.gooru.core.api.model;

import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.OrganizationModel;


public class SessionActivity extends OrganizationModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6998586171468786682L;


	private String sessionActivityUid;
	private String userUid;
	private String status;
	private Date createdOn;
	private Set<SessionActivityItem> sessionActivityItems;
	public String getSessionActivityUid() {
		return sessionActivityUid;
	}
	public void setSessionActivityUid(String sessionActivityUid) {
		this.sessionActivityUid = sessionActivityUid;
	}
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Set<SessionActivityItem> getSessionActivityItems() {
		return sessionActivityItems;
	}
	public void setSessionActivityItems(Set<SessionActivityItem> sessionActivityItems) {
		this.sessionActivityItems = sessionActivityItems;
	}
	
}


