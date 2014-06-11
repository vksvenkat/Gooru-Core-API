package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Set;



public class Shelf extends OrganizationModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2761168938764287724L;


	private String shelfId;
	
	private String shelfParentId;
	
	private String shelfType;
	
	private String name;
		
	private boolean activeFlag;
	
	private Integer depth;
	
	private Set<Shelf> folders;
	
	private Set<ShelfItem> shelfItems;
	
	private String shelfCategory; 
	
	private boolean isDefaultFlag;
	
	private String userId;
	
	private boolean viewFlag;
	
	
	public String getShelfId() {
		return shelfId;
	}

	public void setShelfId(String shelfId) {
		this.shelfId = shelfId;
	}

	public String getShelfParentId() {
		return shelfParentId;
	}

	public void setShelfParentId(String shelfParentId) {
		this.shelfParentId = shelfParentId;
	}

	public String getShelfType() {
		return shelfType;
	}

	public void setShelfType(String shelfType) {
		this.shelfType = shelfType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setFolders(Set<Shelf> folders) {
		this.folders = folders;
	}

	public Set<Shelf> getFolders() {
		return folders;
	}

	public Set<ShelfItem> getShelfItems() {
		return shelfItems;
	}

	public void setShelfItems(Set<ShelfItem> shelfItems) {
		this.shelfItems = shelfItems;
	}

	public String getShelfCategory() {
		return shelfCategory;
	}

	public void setShelfCategory(String shelfCategory) {
		this.shelfCategory = shelfCategory;
	}

	public boolean isDefaultFlag() {
		return isDefaultFlag;
	}

	public void setDefaultFlag(boolean isDefaultFlag) {
		this.isDefaultFlag = isDefaultFlag;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public boolean isViewFlag() {
		return viewFlag;
	}

}
