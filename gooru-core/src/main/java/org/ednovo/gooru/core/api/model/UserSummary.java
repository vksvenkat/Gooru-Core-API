package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserSummary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4233091747628971272L;
	
	private String gooruUid;
	
	private Double collections;
	
	private Double following;
	
	private Double followers;
	
	private Double tag;

	public String getGooruUid() {
		return gooruUid;
	}

	public void setGooruUid(String gooruUid) {
		this.gooruUid = gooruUid;
	}

	public Double getCollections() {
		return collections;
	}

	public void setCollections(Double collections) {
		this.collections = collections;
	}

	public Double getFollowing() {
		return following;
	}

	public void setFollowing(Double following) {
		this.following = following;
	}

	public Double getFollowers() {
		return followers;
	}

	public void setFollowers(Double followers) {
		this.followers = followers;
	}

	public Double getTag() {
		return tag;
	}

	public void setTag(Double tag) {
		this.tag = tag;
	}

}

