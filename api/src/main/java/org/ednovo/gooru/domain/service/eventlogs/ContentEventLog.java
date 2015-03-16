package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class ContentEventLog implements ParameterProperties, ConstantProperties {
	
	public  void getEventlogs( String gooruOid, User apiCaller) throws JSONException {
		
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, gooruOid);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_TAG);
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, apiCaller != null && apiCaller.getOrganization() != null ? apiCaller.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	
	}
	
}
	

