package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class ContentProviderAssociation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3872533003114192366L;
	
	private String contentProviderAssocUid;
	
	private ResourceSource resourceSourceId;
	
	private Date associatedDate;
	
	private String gooruOid;
	
	private User associatedBy;
	
	private ContentProvider contentProvider;

	public Date getAssociatedDate() {
		return associatedDate;
	}

	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	public ResourceSource getResourceSourceId() {
		return resourceSourceId;
	}

	public void setResourceSourceId(ResourceSource resourceSourceId) {
		this.resourceSourceId = resourceSourceId;
	}

	public String getContentProviderAssocUid() {
		return contentProviderAssocUid;
	}

	public void setContentProviderAssocUid(String contentProviderAssocUid) {
		this.contentProviderAssocUid = contentProviderAssocUid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public ContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setContentProvider(ContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

}
