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
	
	private String gooruOid;
	
	private Date associatedDate;
	
	private User associatedBy;
	
	private User  user;
	
	private String freeText;

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public Date getAssociatedDate() {
		return associatedDate;
	}

	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
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
}
