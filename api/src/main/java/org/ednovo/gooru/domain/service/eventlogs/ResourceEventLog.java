package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ResourceEventLog implements ParameterProperties, ConstantProperties{

	public void getEventLogs(Resource resource, boolean isCreate, boolean isAdd, User user) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if(isCreate){
			payLoadObject.put(MODE, CREATE);
		} else if(isAdd){
			payLoadObject.put(MODE, ADD);
		}
		
		if(resource != null){
			payLoadObject.put(ITEM_TYPE, resource.getResourceType().getName());
		}
		
		payLoadObject.put(TITLE, resource != null && resource.getTitle() != null ? resource.getTitle() : null);
		payLoadObject.put(DESCRIPTION, resource != null && resource.getDescription() != null ? resource.getDescription() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(Resource resource , JSONObject itemData, User user) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, resource != null ? resource.getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(MODE, EDIT);
		payLoadObject.put(ITEM_TYPE, resource != null ? resource.getResourceType().getName() : null);
		payLoadObject.put(_ITEM_DATA, itemData != null ? itemData.toString() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

}
