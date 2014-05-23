package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentProvider  extends OrganizationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760466116645806581L;
	
	private String contentProviderUid;
	
	private String contentProviderName;
	
	private CustomTableValue contentProviderType;
	
	private boolean activeFlag;

	public String getContentProviderUid() {
		return contentProviderUid;
	}

	public void setContentProviderUid(String contentProviderUid) {
		this.contentProviderUid = contentProviderUid;
	}

	public String getContentProviderName() {
		return contentProviderName;
	}

	public void setContentProviderName(String contentProviderName) {
		this.contentProviderName = contentProviderName;
	}

	public CustomTableValue getContentProviderType() {
		return contentProviderType;
	}

	public void setContentProviderType(CustomTableValue contentProviderType) {
		this.contentProviderType = contentProviderType;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

}
