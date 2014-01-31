package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationWrapper;
import org.ednovo.gooru.core.api.model.Party;

public class Network extends Party implements OrganizationWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2247265221959499344L;


	private boolean approvedFlag;
	private Organization organization;

	public boolean isApprovedFlag() {
		return approvedFlag;
	}

	public void setApprovedFlag(boolean approvedFlag) {
		this.approvedFlag = approvedFlag;
	}

	@Override
	public Organization getOrganization() {
		return organization;
	}

	@Override
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}