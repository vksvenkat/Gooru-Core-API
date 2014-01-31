package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class TaskResourceAssoc  implements Serializable, Comparable<TaskResourceAssoc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983792695073216861L;


	private String taskResourceAssocUid;

	private Task task;

	private Resource resource;

	private Integer sequence;

	private Date associationDate;

	private User associatedBy;

	public Integer getSequence() {
		return sequence;
	}

	public Date getAssociationDate() {
		return associationDate;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setTaskResourceAssocUid(String taskResourceAssocUid) {
		this.taskResourceAssocUid = taskResourceAssocUid;
	}

	public String getTaskResourceAssocUid() {
		return taskResourceAssocUid;
	}

	@Override
	public int compareTo(TaskResourceAssoc otherItem) {
		if (otherItem != null && getSequence() != null && otherItem.getSequence() != null && !getTaskResourceAssocUid().equals(otherItem.getTaskResourceAssocUid())) {
			if (getSequence().equals(otherItem.getSequence())) {
				return 0;
			}
			return getSequence().compareTo(otherItem.getSequence());
		}
		return 0;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

}
