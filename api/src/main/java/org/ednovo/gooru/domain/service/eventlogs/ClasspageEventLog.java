package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ClasspageEventLog implements ParameterProperties, ConstantProperties{
 

	public void getEventLogs(Classpage classpage, User user, UserGroup userGroup, boolean isCreate, boolean isDelete) throws JSONException {
		if(isCreate){
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		} else if(isDelete){
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
		}
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, classpage != null ? classpage.getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if(isCreate){
			payLoadObject.put(MODE, CREATE);
		}
		payLoadObject.put(ITEM_TYPE, ResourceType.Type.CLASSPAGE.getType());
		payLoadObject.put(GROUP_UID, userGroup != null ? userGroup.getPartyUid() : null);
		payLoadObject.put(CONTENT_ID, classpage != null ? classpage.getContentId() : null);
		payLoadObject.put(CLASS_CODE, classpage != null ? classpage.getClasspageCode() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(CollectionItem collectionItem, boolean isCollectionItem, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(MODE, ADD);
		payLoadObject.put(ITEM_SEQUENCE, collectionItem != null ? collectionItem.getItemSequence() : null);
		payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		if (collectionType != null && collectionItem != null) {
			if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
			}
		}
		payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null  && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put(CONTENT_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		payLoadObject.put(TITLE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getTitle() : null);
		payLoadObject.put(DESCRIPTION, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getDescription() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

	public void getEventLogs(Classpage classpage, User user, UserGroup userGroup, InviteUser inviteUser) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, CLASSPAGE_USER_ADD);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, classpage != null ? classpage.getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
	    payLoadObject.put(INVITED_USER_GOORU_UID, classpage != null && classpage.getUser() != null ? classpage.getUser().getPartyUid() : null);
		payLoadObject.put(CONTENT_ID, classpage != null ? classpage.getContentId() : null);
		payLoadObject.put(CLASS_CODE, classpage != null ? classpage.getClasspageCode() : null);
		payLoadObject.put(GROUP_UID, userGroup != null ? userGroup.getPartyUid() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(Classpage classpage, UserGroupAssociation userGroupAssociation, InviteUser inviteUser) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, CLASSPAGE_USER_REMOVE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, classpage != null ? classpage.getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if(userGroupAssociation != null){
			payLoadObject.put(GROUP_UID, userGroupAssociation != null && userGroupAssociation.getUserGroup() != null ? userGroupAssociation.getUserGroup().getPartyUid() : null);
			payLoadObject.put(REMOVE_GOORU_UID, userGroupAssociation != null && userGroupAssociation.getUser() != null ? userGroupAssociation.getUser().getPartyUid() : null);
		}
		if (inviteUser != null && inviteUser.getInviteUid() != null) {
			payLoadObject.put(INVITED_USER_GOORU_UID, classpage != null && classpage.getUser() != null ? classpage.getUser().getPartyUid() : null);
		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

}


