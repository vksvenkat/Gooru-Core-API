package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class ContentAssociation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 589060187780621481L;
	

	private Long contentAssociationId;
	private Content content;
	private Content associateContent;
	private User user;
	private String typeOf;
	private Date modifiedDate;
	
	/**
	 * 
	 */
	public ContentAssociation() {
	}

	public void setContentAssociationId(Long contentAssociationId) {
		this.contentAssociationId = contentAssociationId;
	}
	public Long getContentAssociationId() {
		return contentAssociationId;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTypeOf() {
		return typeOf;
	}
	public void setTypeOf(String typeOf) {
		this.typeOf = typeOf;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public void setAssociateContent(Content associateContent) {
		this.associateContent = associateContent;
	}
	public Content getAssociateContent() {
		return associateContent;
	}
	
}
