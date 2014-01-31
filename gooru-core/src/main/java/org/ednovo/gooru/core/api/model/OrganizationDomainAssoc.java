package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class OrganizationDomainAssoc extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2837330304486624576L;
	
	private Idp domain;

	public void setDomain(Idp domain) {
		this.domain = domain;
	}

	public Idp getDomain() {
		return domain;
	}

}
