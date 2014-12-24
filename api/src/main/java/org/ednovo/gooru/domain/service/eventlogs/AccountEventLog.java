package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class AccountEventLog implements ParameterProperties, ConstantProperties {

	public void getEventLogs(Identity identity, UserToken userToken, boolean login) throws JSONException {
		if (login) {
			SessionContextSupport.putLogParameter(EVENT_NAME, USER_LOGIN);
		} else {
			SessionContextSupport.putLogParameter(EVENT_NAME, USER_LOG_OUT);
		}
		final JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if (login) {
			eventLogType(LOGIN_TYPE, identity, context);
		} else {
			eventLogType(LOG_OUT_TYPE, identity, context);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		final JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		final JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(SESSIONTOKEN, userToken.getToken());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		final JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		if(login){
			user.put(GOORU_UID, identity != null && identity.getUser() != null ? identity.getUser().getPartyUid() : null );
		} else {
			user.put(GOORU_UID, userToken != null && userToken.getUser() != null ? userToken.getUser().getPartyUid() : null );
		}
		SessionContextSupport.putLogParameter(USER, user.toString());
	}
	
	public void eventLogType(String event, Identity identity, JSONObject context) throws JSONException{
		
		if (identity != null && identity.getLoginType() !=null && identity.getLoginType().equalsIgnoreCase(CREDENTIAL)) {
			context.put(event, GOORU);
		} else if (identity != null && identity.getLoginType() !=null) {
			context.put(event, identity.getLoginType());
		}
		
	}
	
}
