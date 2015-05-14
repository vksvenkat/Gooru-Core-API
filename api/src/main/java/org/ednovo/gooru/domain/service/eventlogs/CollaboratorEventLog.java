/////////////////////////////////////////////////////////////
// CollaboratorEventLog.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
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
		context.put(PARENT_GOORU_ID, collectionItem != null  && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, gooruOid);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) :  new JSONObject();
		session.put(ORGANIZATION_UID, collaborator != null &&collaborator.getOrganization() != null ? collaborator.getOrganization().getPartyUid() : null);
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
