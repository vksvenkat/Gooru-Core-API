package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class UserCollectionItemAssoc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -306690946964358328L;
	
	private User user;
	
	private CollectionItem collectionItem;
	
	private CustomTableValue status;
	
	private Date lastModifiedOn;
	
	private String minimumScore;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CollectionItem getCollectionItem() {
		return collectionItem;
	}

	public void setCollectionItem(CollectionItem collectionItem) {
		this.collectionItem = collectionItem;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public void setMinimumScore(String minimumScore) {
		this.minimumScore = minimumScore;
	}

	public String getMinimumScore() {
		return minimumScore;
	}

}
