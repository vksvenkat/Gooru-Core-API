package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UpdateViewsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -826509314300570206L;
	
	private String gooruOid;
	
	private Long views;

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	public Long getViews() {
		return views;
	}


}
