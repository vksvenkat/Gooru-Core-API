package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class SessionItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -203672596781106569L;


	private String sessionItemId;
	
	private Session session;
	
	private Resource resource;
	
	private CollectionItem collectionItem;
	
	private String attemptItemStatus;
	
	private Integer correctTrySequence;
	
	private Date startTime;

	private Date endTime;
	
	private Set<SessionItemAttemptTry> sessionItemAttemptTry;

	public void setSessionItemId(String sessionItemId) {
		this.sessionItemId = sessionItemId;
	}

	public String getSessionItemId() {
		return sessionItemId;
	}

	public void setsession(Session session) {
		this.session = session;
	}

	public Session getsession() {
		return session;
	}

	public void setCollectionItem(CollectionItem collectionItem) {
		this.collectionItem = collectionItem;
	}

	public CollectionItem getCollectionItem() {
		return collectionItem;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setAttemptItemStatus(String attemptItemStatus) {
		this.attemptItemStatus = attemptItemStatus;
	}

	public String getAttemptItemStatus() {
		return attemptItemStatus;
	}

	public void setCorrectTrySequence(Integer correctTrySequence) {
		this.correctTrySequence = correctTrySequence;
	}

	public Integer getCorrectTrySequence() {
		return correctTrySequence;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setSessionItemAttemptTry(Set<SessionItemAttemptTry> sessionItemAttemptTry) {
		this.sessionItemAttemptTry = sessionItemAttemptTry;
	}

	public Set<SessionItemAttemptTry> getSessionItemAttemptTry() {
		return sessionItemAttemptTry;
	}

}
