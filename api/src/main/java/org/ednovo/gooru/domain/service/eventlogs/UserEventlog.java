package org.ednovo.gooru.domain.service.eventlogs;

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserAccountType.accountCreatedType;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class UserEventlog implements ParameterProperties, ConstantProperties{
	
	public void getEventLogs(boolean updateProfile, boolean visitProfile, User profileVisitor, JSONObject itemData, boolean isFollow, boolean isUnfollow) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, PROFILE_ACTION);
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(USER, user.toString());
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();

		if (updateProfile) {
			context.put("url", "/profile/edit");
		} else if (visitProfile) {
			context.put("url", "/profile/visit");
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (updateProfile) {
			payLoadObject.put(ACTION_TYPE, EDIT);
		} else if (visitProfile) {
			payLoadObject.put(ACTION_TYPE, VISIT);
			payLoadObject.put(VISIT_UID, profileVisitor != null ? profileVisitor.getPartyUid() : null);
		} else if (isFollow) {
			payLoadObject.put(ACTION_TYPE, FOLLOW);

		} else if (isUnfollow) {
			payLoadObject.put(ACTION_TYPE, UN_FOLLOW);
		}
		if (itemData != null) {
			payLoadObject.put("itemData", itemData.toString());

		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}

	public void getEventLogs(User newUser, String source, Identity newIdentity) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, USER_REG);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			context.put(REGISTER_TYPE, accountCreatedType.GOOGLE_APP.getType());
		} else if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.SSO.getType())) {
			context.put(REGISTER_TYPE, accountCreatedType.SSO.getType());
		} else {
			context.put(REGISTER_TYPE, GOORU);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (newIdentity != null && newIdentity.getIdp() != null) {

			payLoadObject.put(IDP_NAME, newIdentity.getIdp().getName());
		} else {
			payLoadObject.put(IDP_NAME, GOORU_API);
		}
		Iterator<Identity> iter = newUser.getIdentities().iterator();
		if (iter != null && iter.hasNext()) {
			Identity identity = iter.next();
			payLoadObject.put(CREATED_TYPE, identity != null ? identity.getAccountCreatedType() : null);
		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, newUser != null && newUser.getOrganization() != null ? newUser.getOrganization().getOrganizationUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		user.put(GOORU_UID, newUser != null ? newUser.getPartyUid() : null);
		SessionContextSupport.putLogParameter(USER, user.toString());

	}

}


