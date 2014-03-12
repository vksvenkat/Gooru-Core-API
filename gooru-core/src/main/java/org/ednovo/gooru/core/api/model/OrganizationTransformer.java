package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.Organization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.transformer.ObjectTransformer;

public class OrganizationTransformer extends ObjectTransformer {

	private static final Logger logger = LoggerFactory.getLogger(OrganizationTransformer.class);
	
	@Override
	public void transform(Object object) {
		Organization organization = (Organization) object;
		if (organization != null) {

			OrganizationTransModel organizationModel = new OrganizationTransModel();
			organizationModel.setOrganizationCode(organization.getOrganizationCode());
			organizationModel.setOrganizationUid(organization.getPartyUid());
			organizationModel.setOrganizationName(organization.getPartyName());

			getContext().transform(organizationModel);

		} else {
			logger.error("Serialization failed for organization transformer");
			getContext().write(null);
		}
	}
}
