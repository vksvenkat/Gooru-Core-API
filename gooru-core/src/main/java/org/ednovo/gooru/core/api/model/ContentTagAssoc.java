package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentTagAssoc implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1111252614388191922L;

	private String contentGooruOid;
	
	private String tagGooruOid;

	public void setContentGooruOid(String contentGooruOid) {
		this.contentGooruOid = contentGooruOid;
	}

	public String getContentGooruOid() {
		return contentGooruOid;
	}

	public void setTagGooruOid(String tagGooruOid) {
		this.tagGooruOid = tagGooruOid;
	}

	public String getTagGooruOid() {
		return tagGooruOid;
	}


}
