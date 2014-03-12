package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import org.ednovo.gooru.core.api.model.IndexableEntry;


public class SearchResultActivity implements IndexableEntry,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8205404722642577622L;
	/**
	 * 
	 */
	
	
	private long id;
	private String resultUId;
	private String userAction;
	private Date userActionTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getResultUId() {
		return resultUId;
	}
	public void setResultUId(String resultUId) {
		this.resultUId = resultUId;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public Date getUserActionTime() {
		return userActionTime;
	}
	public void setUserActionTime(Date userActionTime) {
		this.userActionTime = userActionTime;
	}
	/* (non-Javadoc)
	 * @see org.ednovo.gooru.domain.model.IndexableEntry#getEntryId()
	 */
	@Override
	public String getEntryId() {
		// TODO Auto-generated method stub
		return null;
	}
}
