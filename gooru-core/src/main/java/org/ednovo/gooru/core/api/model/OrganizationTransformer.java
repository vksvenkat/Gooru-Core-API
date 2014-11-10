package org.ednovo.gooru.core.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.transformer.ObjectTransformer;

public class OrganizationTransformer extends ObjectTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationTransformer.class);
	
	@Override
	public void transform(Object object) {
		Organization organization = (Organization) object;
		if (organization != null) {

			OrganizationTransModel organizationModel = new OrganizationTransModel();
			organizationModel.setOrganizationCode(organization.getOrganizationCode());
			organizationModel.setOrganizationUid(organization.getPartyUid());
			organizationModel.setOrganizationName(organization.getPartyName());
			organizationModel.setId(organization.getPartyUid());
			organizationModel.setName(organization.getPartyName());
			organizationModel.setParentId(organization.getParentId());
			getContext().transform(organizationModel);

		} else {
			LOGGER.error("Serialization failed for organization transformer");
			getContext().write(null);
		}
	}
}
