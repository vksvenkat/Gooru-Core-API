package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentSubdomainAssoc implements Serializable {

	private static final long serialVersionUID = -248990366467402942L;

	private Content content;

	private Subdomain subdomain;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Subdomain getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(Subdomain subdomain) {
		this.subdomain = subdomain;
	}
}
