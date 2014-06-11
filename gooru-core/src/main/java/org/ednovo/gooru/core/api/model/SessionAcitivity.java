package org.ednovo.gooru.core.api.model;

import java.util.Date;
import java.util.Set;


public class SessionAcitivity {
	private String sessionActivityUid;
	private User user;
	private Organization organization;
	private Integer activeFlag;
	private Date createdOn;
	private Set<SessionActivityItem> sessionActivityItems;

	public String getSessionActivityUid() {
		return sessionActivityUid;
	}

	public void setSessionActivityUid(String sessionActivityUid) {
		this.sessionActivityUid = sessionActivityUid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
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
