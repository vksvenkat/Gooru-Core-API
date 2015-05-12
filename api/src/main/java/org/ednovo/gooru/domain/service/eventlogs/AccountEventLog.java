/////////////////////////////////////////////////////////////
// AccountEventLog.java
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
		SessionContextSupport.putLogParameter(EVENT_NAME, login ? USER_LOGIN : USER_LOG_OUT);
		final JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();

		if (identity != null && identity.getLoginType() != null) {
			context.put(login ? LOGIN_TYPE : LOG_OUT_TYPE, identity.getLoginType().equalsIgnoreCase(CREDENTIAL) ? GOORU : identity.getLoginType());
		}

		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		final JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		final JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(SESSIONTOKEN, userToken.getToken());
		session.put(API_KEY, userToken.getApplication().getKey());
		session.put(ORGANIZATION_UID,userToken.getUser().getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		final JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		if (login) {
			user.put(GOORU_UID, identity != null && identity.getUser() != null ? identity.getUser().getPartyUid() : null);
		} else {
			user.put(GOORU_UID, userToken != null && userToken.getUser() != null ? userToken.getUser().getPartyUid() : null);
		}
		SessionContextSupport.putLogParameter(USER, user.toString());
	}
}
