package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionItemFeedback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6886157895374174429L;

	private String sessionItemFeedbackUid;

	private String sessionId;

	private String contentGooruOId;

	private String contentItemId;

	private String parentGooruOId;

	private String parentItemId;

	private Date createdOn;

	private User feedbackProvidedBy;

	private User user;

	private String freeText;

	private String playLoadObject;

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getSessionItemFeedbackUid() {
		return sessionItemFeedbackUid;
	}

	public void setSessionItemFeedbackUid(String sessionItemFeedbackUid) {
		this.sessionItemFeedbackUid = sessionItemFeedbackUid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPlayLoadObject() {
		return playLoadObject;
	}

	public void setPlayLoadObject(String playLoadObject) {
		this.playLoadObject = playLoadObject;
	}

	public String getContentGooruOId() {
		return contentGooruOId;
	}

	public void setContentGooruOId(String contentGooruOId) {
		this.contentGooruOId = contentGooruOId;
	}

	public String getContentItemId() {
		return contentItemId;
	}

	public void setContentItemId(String contentItemId) {
		this.contentItemId = contentItemId;
	}

	public String getParentGooruOId() {
		return parentGooruOId;
	}

	public void setParentGooruOId(String parentGooruOId) {
		this.parentGooruOId = parentGooruOId;
	}

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public User getFeedbackProvidedBy() {
		return feedbackProvidedBy;
	}

	public void setFeedbackProvidedBy(User feedbackProvidedBy) {
		this.feedbackProvidedBy = feedbackProvidedBy;
	}
}
