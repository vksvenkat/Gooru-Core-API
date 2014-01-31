package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class OrganizationTransModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6216032474734214537L;


	private String organizationCode;

	private String organizationName;

	private String organizationUid;

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

}
