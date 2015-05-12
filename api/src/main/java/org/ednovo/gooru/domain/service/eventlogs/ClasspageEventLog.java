/////////////////////////////////////////////////////////////
// ClasspageEventLog.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////


package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ClasspageEventLog implements ParameterProperties, ConstantProperties{
 

	private Object collectionItem;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClasspageEventLog.class);

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
		payLoadObject.put(_GROUP_UID, userGroup != null ? userGroup.getPartyUid() : null);
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
		payLoadObject.put(_GROUP_UID, userGroup != null ? userGroup.getPartyUid() : null);
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
			payLoadObject.put(_GROUP_UID, userGroupAssociation != null && userGroupAssociation.getUserGroup() != null ? userGroupAssociation.getUserGroup().getPartyUid() : null);
			payLoadObject.put(REMOVE_GOORU_UID, userGroupAssociation != null && userGroupAssociation.getUser() != null ? userGroupAssociation.getUser().getPartyUid() : null);
		}
		if (inviteUser != null && inviteUser.getInviteUid() != null) {
			payLoadObject.put(INVITED_USER_GOORU_UID, classpage != null && classpage.getUser() != null ? classpage.getUser().getPartyUid() : null);
		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(String classId, String pathwayGooruOid, User user, boolean isCreate, boolean isUpdate, String data) throws Exception{
	   try {
		if (isCreate) {
	            SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
	    } else if (isUpdate) {
	    	    SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
	    }
	    JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
	    context.put(CONTENT_GOORU_ID, pathwayGooruOid);
	    context.put(PARENT_GOORU_ID, classId);
	    SessionContextSupport.putLogParameter(CONTEXT, context.toString());
	    JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
	   if (isCreate) {
		   payLoadObject.put(MODE, CREATE);
	    } else if (isUpdate) {
		   payLoadObject.put(MODE, EDIT);	   
	    }
	    if (data != null) {
			payLoadObject.put(_ITEM_DATA, data);
	    }
	    payLoadObject.put(ITEM_TYPE,CLASSPAGE_PATHWAY);
	    SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	    JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
	    session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
	    SessionContextSupport.putLogParameter(SESSION, session.toString());
	} catch (Exception e) {
		LOGGER.error(_ERROR, e);
	}
	}
	
	public void getEventLogs(CollectionItem collectionItem, String pathwayId,User user, CollectionItem sourceItem, String collectionType) throws JSONException {
	    SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
	    JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
	    context.put(CONTENT_ITEM_ID, sourceItem.getCollectionItemId());
	    SessionContextSupport.putLogParameter(CONTEXT, context.toString());
	    JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
	    payLoadObject.put(MODE,REORDER);
	    payLoadObject.put(ITEM_SEQUENCE,collectionItem.getItemSequence());
		if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
			payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
		} else if (collectionItem != null && collectionItem.getResource() != null && collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.PATHWAY.getType())) {
			payLoadObject.put(ITEM_TYPE, CLASSPAGE_PATHWAY);
		} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
			if (collectionItem != null && collectionItem.getResource() != null) {
				String itemTypeName = collectionItem.getResource().getResourceType().getName();
				if (itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
					payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
				} else if (itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
				}
			}
		}
	    SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	    JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
	    session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
	    SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(String collectionId, CollectionItem collectionItem, String pathwayId,User user, CollectionItem sourceItem, CollectionItem targetItem) throws JSONException {
	    SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
	    JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
	    context.put(CONTENT_GOORU_ID, collectionId);
	    context.put(PARENT_GOORU_ID, pathwayId);
	    context.put(TARGET_ITEM_ID, targetItem != null ? targetItem.getCollectionItemId() : null);
	    context.put(CONTENT_ITEM_ID, targetItem != null ? targetItem.getCollectionItemId() : null);
	    context.put(SOURCE_ITEM_ID, sourceItem != null ? sourceItem.getCollectionItemId() : null);
	    context.put(SOURCE_GOORU_ID, collectionId);
	    context.put(TARGET_GOORU_ID, pathwayId);
	    SessionContextSupport.putLogParameter(CONTEXT, context.toString());
	    JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
	    payLoadObject.put(MODE,MOVE);
	    payLoadObject.put(ITEM_SEQUENCE,collectionItem.getItemSequence());
	    payLoadObject.put(ITEM_TYPE,PATHWAY_COLLECTION);    
	    SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	    JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
	    session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
	    SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	public void getEventLogs(Classpage classpage, InviteUser inviteUser,User user, List<String> emailIds) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, CLASSPAGE_USER_INVITE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, classpage != null ? classpage.getGooruOid() : null);
		context.put(INVITER_ID, classpage != null && classpage.getUser() != null ? classpage.getUser().getPartyUid() : null);
		context.put(INVITEE_EMAIL_ID, emailIds);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(CONTENT_ID, classpage != null ? classpage.getContentId() : null);
		payLoadObject.put(CLASS_CODE, classpage != null ? classpage.getClasspageCode() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	}
	
	
