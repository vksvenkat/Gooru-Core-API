package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class TaskHistoryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7839153683200765228L;

	private String taskHistoryItemUid;
	
	private TaskHistory taskHistory;
	
	private String fieldName;
	
	private String oldKey;
	
	private String oldValue;
	
	private String newKey;
	
	private String newValue;

	public void setTaskHistoryItemUid(String taskHistoryItemUid) {
		this.taskHistoryItemUid = taskHistoryItemUid;
	}

	public String getTaskHistoryItemUid() {
		return taskHistoryItemUid;
	}

	public void setTaskHistory(TaskHistory taskHistory) {
		this.taskHistory = taskHistory;
	}

	public TaskHistory getTaskHistory() {
		return taskHistory;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setOldKey(String oldKey) {
		this.oldKey = oldKey;
	}

	public String getOldKey() {
		return oldKey;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}

	public String getNewKey() {
		return newKey;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getNewValue() {
		return newValue;
	}



}
