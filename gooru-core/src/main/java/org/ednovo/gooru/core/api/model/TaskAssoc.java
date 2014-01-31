package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.ednovo.gooru.core.api.model.Task;

public class TaskAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3220124393263939483L;


	private String taskAssocUid;

	private Task taskParent;

	private Task taskDescendant;

	private Integer sequence;
	
	private String associationType;

	public String getTaskAssocUid() {
		return taskAssocUid;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setTaskAssocUid(String taskAssocUid) {
		this.taskAssocUid = taskAssocUid;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setTaskParent(Task taskParent) {
		this.taskParent = taskParent;
	}

	public Task getTaskParent() {
		return taskParent;
	}

	public void setTaskDescendant(Task taskDescendant) {
		this.taskDescendant = taskDescendant;
	}

	public Task getTaskDescendant() {
		return taskDescendant;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

	public String getAssociationType() {
		return associationType;
	}

}
