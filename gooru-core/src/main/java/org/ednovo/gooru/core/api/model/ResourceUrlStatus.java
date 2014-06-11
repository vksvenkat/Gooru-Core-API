package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class ResourceUrlStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5087610400413710993L;


	private Long resourceUrlStatusId;

	private Resource resource;

	private Integer status;

	private int failedCount;

	private Date lastCheckedDate;
	
	private Date frameBreakerValidatedOn;

	public Long getResourceUrlStatusId() {
		return resourceUrlStatusId;
	}

	public void setResourceUrlStatusId(Long resourceUrlStatusId) {
		this.resourceUrlStatusId = resourceUrlStatusId;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public Date getLastCheckedDate() {
		return lastCheckedDate;
	}

	public void setLastCheckedDate(Date lastCheckedDate) {
		this.lastCheckedDate = lastCheckedDate;
	}

	public Date getFrameBreakerValidatedOn() {
		return frameBreakerValidatedOn;
	}

	public void setFrameBreakerValidatedOn(Date frameBreakerValidatedOn) {
		this.frameBreakerValidatedOn = frameBreakerValidatedOn;
	}
	
}
