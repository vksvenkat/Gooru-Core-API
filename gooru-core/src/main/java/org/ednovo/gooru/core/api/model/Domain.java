
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

public class Domain extends OrganizationModel implements Serializable {
	
	
	private static final long serialVersionUID = -1775302177846507373L;
	
	@Column
	private Short domainId;

	@Column
	private String name;

	@Column
	private String description;
	
	@Column
	private Short activeFlag;

	@Column
	private String imagePath;
	
	@Column
	private Short displaySequence;

	@Column
	private Date createdOn;
	
	@Column
	private Date lastModified;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(java.util.Date date) {
		this.createdOn = date;
	}
	
	public Short getDomainId() {
		return domainId;
	}

	public void setDomainId(Short domainId) {
		this.domainId = domainId;
	}
	
	public Short getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Short activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	
	public Short getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(Short displaySequence) {
		this.displaySequence = displaySequence;
	}

}
