package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class ContentTagAssoc implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1111252614388191922L;

	private String contentGooruOid;
	
	private String tagGooruOid;
	
	private String associatedUid;
	
	private Date associatedDate;

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

	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	public Date getAssociatedDate() {
		return associatedDate;
	}

	public void setAssociatedUid(String associatedUid) {
		this.associatedUid = associatedUid;
	}

	public String getAssociatedUid() {
		return associatedUid;
	}


}
