package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Menu implements Serializable {

	private static final long serialVersionUID = 3421674099506300520L;

	private String menuUid;
	private String name;
	private String description;
	private String url;
	private String iconUrl;
	private String creatorUid;
	private Date createdOn;
	private Date lastModified;
	private Boolean isActive = true;
	private MenuItem menuItem;
	private List<MenuItem> menuItems;
	private Set<MenuRoleAssoc> menuRoleAssocs;
	

	public String getMenuUid() {
		return menuUid;
	}

	public void setMenuUid(String menuUid) {
		this.menuUid = menuUid;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getCreatorUid() {
		return creatorUid;
	}

	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem 
			menuItem) {
		this.menuItem = menuItem;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public Set<MenuRoleAssoc> getMenuRoleAssocs() {
		return menuRoleAssocs;
	}

	public void setMenuRoleAssocs(Set<MenuRoleAssoc> menuRoleAssocs) {
		this.menuRoleAssocs = menuRoleAssocs;
	}


}
