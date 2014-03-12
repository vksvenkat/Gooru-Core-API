package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.Resource;


public class ShelfItem extends OrganizationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1117655899231660702L;


	private String shelfItemId;
	
	private Shelf shelf;
	
	private Resource resource;
	
	private String addedType;
	
	private Date createdOn;
	
	private Date lastActivityOn;
	
	public String getShelfItemId() {
		return shelfItemId;
	}

	public void setShelfItemId(String shelfItemId) {
		this.shelfItemId = shelfItemId;
	}
	
	public String getAddedType() {
		return addedType;
	}

	public void setAddedType(String addedType) {
		this.addedType = addedType;
	}

	public Shelf getShelf() {
		return shelf;
	}

	public void setShelf(Shelf shelf) {
		this.shelf = shelf;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setLastActivityOn(Date lastActivityOn) {
		this.lastActivityOn = lastActivityOn;
	}

	public Date getLastActivityOn() {
		return lastActivityOn;
	}
}
