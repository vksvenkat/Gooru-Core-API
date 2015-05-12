/////////////////////////////////////////////////////////////
// FeedbackEventLog.java
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

import org.ednovo.gooru.core.api.model.ContextDTO;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventLog implements ParameterProperties, ConstantProperties{
	
	public void getEventLogs(User feedbackUser, ContextDTO contextDTO, Feedback feedback, StringBuilder reactionType, Integer previous) throws Exception {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_RATE);
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, feedbackUser.getOrganizationUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		user.put(GOORU_UID, feedbackUser.getPartyUid());
		SessionContextSupport.putLogParameter(USER, user.toString());
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, feedback != null ? feedback.getAssocGooruOid() : null);
		context.put(PARENT_GOORU_ID, contextDTO != null ? contextDTO.getCollectionGooruId() : null);
		context.put(CONTENT_ITEM_ID, contextDTO != null ? contextDTO.getContentItemId() : null);
		context.put(PARENT_ITEM_ID, contextDTO != null ? contextDTO.getParentItemId() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(RATE, feedback != null ? feedback.getScore() : null);
		payLoadObject.put(PREVIOUS_RATE, previous);
		payLoadObject.put(TEXT, feedback != null ? feedback.getFreeText() : null);
		payLoadObject.put(FEEDBACK_PROVIDER_UID, feedbackUser.getPartyUid());
		payLoadObject.put(RATE_TYPE, ADD);
		payLoadObject.put(REACTION_TYPE, reactionType != null ? reactionType.toString() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}

}

	
