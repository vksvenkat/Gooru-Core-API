package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class ContentMetaAssociation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4802999965130804819L;
	
	private String contentMetaAssociationId;
	
	private Content content;
	
	private CustomTableValue associationType;
	
	private CustomTableValue value;
	
	private User user;
	
	private Date associatedDate;
	
	public String getContentMetaAssociationId() {
		return contentMetaAssociationId;
	}

	public void setContentMetaAssociationId(String contentMetaAssociationId) {
		this.contentMetaAssociationId = contentMetaAssociationId;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public CustomTableValue getAssociationType() {
		return associationType;
	}

	public void setAssociationType(CustomTableValue associationType) {
		this.associationType = associationType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getAssociatedDate() {
		return associatedDate;
	}

	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}

	public void setValue(CustomTableValue value) {
		this.value = value;
	}

	public CustomTableValue getValue() {
		return value;
	}
	

}
