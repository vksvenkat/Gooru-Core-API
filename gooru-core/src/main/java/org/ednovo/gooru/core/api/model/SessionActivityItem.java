package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class SessionActivityItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -419769806536998698L;


	private String sessionActivityItemUid;
	private SessionActivity sessionActivity;
	private String contentUid;
	private String subContentUid;
	private Integer questionAttemptId;
	private String contentType;
	private Date createdOn;
	
	public String getSessionActivityItemUid() {
		return sessionActivityItemUid;
	}

	public void setSessionActivityItemUid(String sessionActivityItemUid) {
		this.sessionActivityItemUid = sessionActivityItemUid;
	}

	public SessionActivity getSessionActivity() {
		return sessionActivity;
	}

	public void setSessionActivity(SessionActivity sessionActivity) {
		this.sessionActivity = sessionActivity;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setQuestionAttemptId(Integer questionAttemptId) {
		this.questionAttemptId = questionAttemptId;
	}

	public Integer getQuestionAttemptId() {
		return questionAttemptId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setContentUid(String contentUid) {
		this.contentUid = contentUid;
	}

	public String getContentUid() {
		return contentUid;
	}

	public void setSubContentUid(String subContentUid) {
		this.subContentUid = subContentUid;
	}

	public String getSubContentUid() {
		return subContentUid;
	}
}
