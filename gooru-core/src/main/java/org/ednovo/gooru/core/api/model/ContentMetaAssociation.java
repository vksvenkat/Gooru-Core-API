package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class ContentMetaAssociation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4802999965130804819L;

	private Content content;

	private CustomTableValue typeId;

	private User user;

	private Date createdOn;

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

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public CustomTableValue getTypeId() {
		return typeId;
	}

	public void setTypeId(CustomTableValue typeId) {
		this.typeId = typeId;
	}

}
