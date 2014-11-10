package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class CollaboratorEventLog implements ParameterProperties, ConstantProperties {
	
	public void getEventLogs(User collaborator, CollectionItem collectionItem, String gooruOid, boolean isAdd, boolean isRemove) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_COLLABORATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) :  new JSONObject();
		context.put(SOURCE_GOORU_UID,gooruOid);
		context.put(TARGET_GOORU_UID,collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		context.put(TARGET_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		context.put(PARENT_GOORU_OID, collectionItem != null  && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, gooruOid);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) :  new JSONObject();
		session.put(ORGANIZATION_UID, collaborator != null ? collaborator.getOrganizationUid() : null);
		JSONObject newUser = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) :  new JSONObject();		
		newUser.put(GOORU_UID, collaborator != null ? collaborator.getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());	
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) :  new JSONObject();
		SessionContextSupport.putLogParameter(USER, user.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) :  new JSONObject();
		if(isAdd){
			payLoadObject.put(MODE, ADD);
		} else if(isRemove){
			payLoadObject.put(MODE, DELETE);
		}
		payLoadObject.put(COLLABORATED_ID, collaborator != null ? collaborator.getPartyUid() : null);
		payLoadObject.put(ITEM_TYPE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getResourceType().getName() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}
}
