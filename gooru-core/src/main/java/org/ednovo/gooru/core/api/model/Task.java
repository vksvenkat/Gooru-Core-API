package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Content;


public class Task extends Content implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3873670928088767955L;

	private String title;
	
	private String description;
	
	private Date plannedStartDate;
	
	private Date plannedEndDate;
	
	private String status;
	
	private String typeName;
	
	private Double estimatedEffort;
	
	private Set<TaskResourceAssoc> taskResourceAssocs;
	
	
	public Task copy() {
		try {
			return (Task) this.clone();
		} catch (CloneNotSupportedException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public String getStatus() {
		return status;
	}

	public String getTypeName() {
		return typeName;
	}

	public Double getEstimatedEffort() {
		return estimatedEffort;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setEstimatedEffort(Double estimatedEffort) {
		this.estimatedEffort = estimatedEffort;
	}

	public void setTaskResourceAssocs(Set<TaskResourceAssoc> taskResourceAssocs) {
		this.taskResourceAssocs = taskResourceAssocs;
	}

	public Set<TaskResourceAssoc> getTaskResourceAssocs() {
		return taskResourceAssocs;
	}


}
