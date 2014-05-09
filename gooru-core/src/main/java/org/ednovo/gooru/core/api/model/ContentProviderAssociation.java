package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class ContentProviderAssociation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3872533003114192366L;
	
	private Integer resourceSourceId;
	
	private Date associatedDate;
	
	private BigInteger ContentId;
	
	private String associatedByUid;
	
	private String contentProviderUid;

	public Integer getResourceSourceId() {
		return resourceSourceId;
	}

	public void setResourceSourceId(Integer resourceSourceId) {
		this.resourceSourceId = resourceSourceId;
	}

	public Date getAssociatedDate() {
		return associatedDate;
	}

	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	public BigInteger getContentId() {
		return ContentId;
	}

	public void setContentId(BigInteger contentId) {
		ContentId = contentId;
	}

	public String getAssociatedByUid() {
		return associatedByUid;
	}

	public void setAssociatedByUid(String associatedByUid) {
		this.associatedByUid = associatedByUid;
	}

	public String getContentProviderUid() {
		return contentProviderUid;
	}

	public void setContentProviderUid(String contentProviderUid) {
		this.contentProviderUid = contentProviderUid;
	}
	
	

}
