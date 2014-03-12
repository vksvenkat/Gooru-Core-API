package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;

public class UserGroupAssociation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7821133985195541453L;

	/**
	 * 
	 */


	private User user;

	private UserGroup userGroup;

	private Integer isGroupOwner;

	public UserGroupAssociation() {
		user = new User();
		userGroup = new UserGroup();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public Integer getIsGroupOwner() {
		return isGroupOwner;
	}

	public void setIsGroupOwner(Integer isGroupOwner) {
		this.isGroupOwner = isGroupOwner;
	}

	
	
}
