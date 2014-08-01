package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.ContextDTO;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventLog implements ParameterProperties, ConstantProperties{
	
	public void getEventLogs(User feedbackUser, ContextDTO contextDTO, Feedback feedback, StringBuilder reactionType) throws JSONException {
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
		payLoadObject.put(TEXT, feedback != null ? feedback.getFreeText() : null);
		payLoadObject.put(FEEDBACK_PROVIDER_UID, feedbackUser.getPartyUid());
		payLoadObject.put(RATE_TYPE, ADD);
		payLoadObject.put(REACTION_TYPE, reactionType != null ? reactionType.toString() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}

}

	
