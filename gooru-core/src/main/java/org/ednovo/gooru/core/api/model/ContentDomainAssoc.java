package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentDomainAssoc implements Serializable {

	private static final long serialVersionUID = -248990366467402942L;

	private Content content;

	private Domain domain;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}
}
