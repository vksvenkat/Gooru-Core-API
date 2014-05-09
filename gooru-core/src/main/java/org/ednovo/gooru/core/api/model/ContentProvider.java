package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentProvider implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760466116645806581L;
	
	private String contentProviderUid;
	
	private String contentProviderName;
	
	private String contentProviderType;

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

	public String getContentProviderType() {
		return contentProviderType;
	}

	public void setContentProviderType(String contentProviderType) {
		this.contentProviderType = contentProviderType;
	}

}
