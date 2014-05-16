package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionItemFeedback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6886157895374174429L;

	private String sessionItemFeedbackId;
	
	private Session session;
	
	private String gooruOid;
	
	private Date associatedDate;
	
	private User associatedBy;
	
	private User feedbackUser;
	
	private String freeText;

	public String getSessionItemFeedbackId() {
		return sessionItemFeedbackId;
	}

	public void setSessionItemFeedbackId(String sessionItemFeedbackId) {
		this.sessionItemFeedbackId = sessionItemFeedbackId;
	}

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

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getFeedbackUser() {
		return feedbackUser;
	}

	public void setFeedbackUser(User feedbackUser) {
		this.feedbackUser = feedbackUser;
	}

}
