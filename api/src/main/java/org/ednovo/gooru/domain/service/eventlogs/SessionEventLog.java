package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.SessionItemFeedback;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component

public class SessionEventLog implements ParameterProperties, ConstantProperties{
	public void getEventLogs(SessionItemFeedback sessionItemFeedback, User feedbackProvider) throws JSONException {
        SessionContextSupport.putLogParameter(EVENT_NAME, RESOURCE_USER_FEEDBACK);
        JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) :  new JSONObject();
        context.put(CONTENT_GOORU_ID, sessionItemFeedback.getContentGooruOId());
        context.put(PARENT_GOORU_ID, sessionItemFeedback.getParentGooruOId());
        SessionContextSupport.putLogParameter(CONTEXT, context.toString());
        JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) :  new JSONObject();
        payLoadObject =  sessionItemFeedback.getPlayLoadObject() != null ? new JSONObject(sessionItemFeedback.getPlayLoadObject()) :  new JSONObject();
        payLoadObject.put(TEXT, sessionItemFeedback.getFreeText());
        payLoadObject.put(FEEDBACK_PROVIDER_UID, feedbackProvider.getPartyUid());
        SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
        JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) :  new JSONObject();
        session.put(_SESSION_ID, sessionItemFeedback.getSessionId());
        session.put(ORGANIZATION_UID, feedbackProvider.getOrganization().getPartyUid());
        SessionContextSupport.putLogParameter(SESSION, session.toString());    
        JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) :  new JSONObject();
        user.put(GOORU_UID, sessionItemFeedback.getUser().getPartyUid());
        SessionContextSupport.putLogParameter(USER, user.toString());
    }

}



