package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.User;


public class UserTagAssoc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3860412452108040017L;
	
	
	private User user;
	
	private String  tagGooruOid;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setTagGooruOid(String tagGooruOid) {
		this.tagGooruOid = tagGooruOid;
	}

	public String getTagGooruOid() {
		return tagGooruOid;
	}


}
