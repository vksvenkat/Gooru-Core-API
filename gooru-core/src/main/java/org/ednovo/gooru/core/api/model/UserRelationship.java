package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.User;


public class UserRelationship extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1878026632439748759L;
	private Integer userRelationshipId;
	private User user;
	private Party followOnUser;
	private Date activatedDate;
	private Date deactivatedDate;
	private Boolean activeFlag = true;

	public Integer getUserRelationshipId() {
		return userRelationshipId;
	}

	public void setUserRelationshipId(Integer userRelationshipId) {
		this.userRelationshipId = userRelationshipId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Party getFollowOnUser() {
		return followOnUser;
	}

	public void setFollowOnUser(Party followOnUser) {
		this.followOnUser = followOnUser;
	}

	public Date getActivatedDate() {
		return activatedDate;
	}

	public void setActivatedDate(Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	public Date getDeactivatedDate() {
		return deactivatedDate;
	}

	public void setDeactivatedDate(Date deactivatedDate) {
		this.deactivatedDate = deactivatedDate;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

}