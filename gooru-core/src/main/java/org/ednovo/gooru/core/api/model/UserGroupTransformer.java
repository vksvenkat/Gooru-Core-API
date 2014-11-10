package org.ednovo.gooru.core.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.transformer.ObjectTransformer;

public class UserGroupTransformer extends ObjectTransformer {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupTransformer.class);
	
	@Override
	public void transform(Object object) {
		UserGroup sg = (UserGroup) object;
		if (sg != null) {

			UserGroupTransModel sgModel = new UserGroupTransModel();
			sgModel.setName(sg.getPartyName());
			sgModel.setPartyUid(sg.getPartyUid());
			sgModel.setGroupUid(sg.getPartyUid());
			sgModel.setGroupName(sg.getGroupName());
			sgModel.setGroupCode(sg.getGroupCode());
			sgModel.setOrganization(sg.getOrganization());
			sgModel.setUserUid(sg.getUserUid());
						
			getContext().transform(sgModel);

		} else {
			LOGGER.error("Serialization failed for user group transformer");
			getContext().write(null);
		}
	}
}
