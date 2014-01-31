package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.User;


public class TaskHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5254419540945110174L;

	private String taskHistoryUid;
	
	private Task taskContentId;
	
	private User userUid;
	
	private Date createdDate;
	
	private Set<TaskHistoryItem> taskHistoryItems;
 	
	public void setTaskHistoryUid(String taskHistoryUid) {
		this.taskHistoryUid = taskHistoryUid;
	}

	public String getTaskHistoryUid() {
		return taskHistoryUid;
	}

	public void setUserUid(User userUid) {
		this.userUid = userUid;
	}

	public User getUserUid() {
		return userUid;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setTaskContentId(Task taskContentId) {
		this.taskContentId = taskContentId;
	}

	public Task getTaskContentId() {
		return taskContentId;
	}

	public void setTaskHistoryItems(Set<TaskHistoryItem> taskHistoryItems) {
		this.taskHistoryItems = taskHistoryItems;
	}

	public Set<TaskHistoryItem> getTaskHistoryItems() {
		return taskHistoryItems;
	}
	

}
