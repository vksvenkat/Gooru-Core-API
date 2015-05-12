/////////////////////////////////////////////////////////////
// UserEventlog.java
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

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class UserEventLog implements ParameterProperties, ConstantProperties{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEventLog.class);
	
	public void getEventLogs(boolean updateProfile, boolean visitProfile, User profileVisitor, JSONObject itemData, boolean isFollow, boolean isUnfollow) {
		
		try {
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
		} catch (Exception e) {
			LOGGER.error(_ERROR , e);
		}
	}
	

	public void getEventLogs(User newUser, String source, Identity newIdentity) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, USER_REG);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if (source != null) {
			context.put(REGISTER_TYPE, source);
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
		session.put(ORGANIZATION_UID, newUser != null && newUser.getOrganization() != null ? newUser.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		user.put(GOORU_UID, newUser != null ? newUser.getPartyUid() : null);
		SessionContextSupport.putLogParameter(USER, user.toString());

	}

}


