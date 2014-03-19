package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class LtiContentAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5741682961442166533L;
	private Long ltiId;
	private String contextId;
	private User user;
	private Resource resource;
	
	private Date associationDate;

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Date getAssociationDate() {
		return associationDate;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}

	public Long getLtiId() {
		return ltiId;
	}

	public void setLtiId(Long ltiId) {
		this.ltiId = ltiId;
	}

}
