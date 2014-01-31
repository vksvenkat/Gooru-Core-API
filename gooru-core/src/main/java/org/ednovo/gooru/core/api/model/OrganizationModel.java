package org.ednovo.gooru.core.api.model;

import org.codehaus.jackson.map.annotate.JsonFilter;

import com.fasterxml.jackson.annotation.JsonBackReference;

@JsonFilter("organizationModel")
public class OrganizationModel implements OrganizationWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2856612172682693561L;

	@JsonBackReference
	protected Organization organization;

	@Override
	public Organization getOrganization() {
		return organization;
	}

	@Override
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
