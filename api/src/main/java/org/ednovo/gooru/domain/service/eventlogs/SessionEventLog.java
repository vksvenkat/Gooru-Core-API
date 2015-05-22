/////////////////////////////////////////////////////////////
// SessionEventLog.java
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

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionEventLog implements ParameterProperties, ConstantProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionEventLog.class);

	public void getEventLogs(SessionActivity sessionActivity, SessionActivityItem sessionActivityItem, String feedbackProviderUid) {
		try {
			SessionContextSupport.putLogParameter(EVENT_NAME, RESOURCE_USER_FEEDBACK);
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, sessionActivityItem.getContentGooruId());
			context.put(PARENT_GOORU_ID, sessionActivityItem.getParentGooruId());
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			payLoadObject = sessionActivityItem.getPayLoadObject() != null ? new JSONObject(sessionActivityItem.getPayLoadObject()) : new JSONObject();
			payLoadObject.put(TEXT, sessionActivityItem.getFeedbackText());
			payLoadObject.put(FEEDBACK_PROVIDER_UID, feedbackProviderUid);
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(_SESSION_ID, sessionActivity.getSessionActivityId());
			SessionContextSupport.putLogParameter(SESSION, session.toString());
			JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
			user.put(GOORU_UID, sessionActivity.getUser().getPartyUid());
			SessionContextSupport.putLogParameter(USER, user.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

}
