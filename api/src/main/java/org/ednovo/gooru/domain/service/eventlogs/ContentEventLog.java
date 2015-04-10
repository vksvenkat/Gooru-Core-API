package org.ednovo.gooru.domain.service.eventlogs;

import java.util.Map;

import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ContentEventLog implements ParameterProperties, ConstantProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentEventLog.class);

	public void getEventlogs(String gooruOid, User apiCaller, boolean isAdd, boolean isRemove, Map<String, Object> contentTagAssoc) {
		
		try {
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(CONTENT_GOORU_ID, gooruOid);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_TAG);
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, apiCaller != null && apiCaller.getOrganization() != null ? apiCaller.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
	    if (isAdd) {
	    	payLoadObject.put(MODE, ADD);
	    } else if(isRemove) {
	    	payLoadObject.put(MODE, DELETE);
	    }
			payLoadObject.put(DATA, JsonSerializer.serializeToJson(contentTagAssoc));
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR , e);
		}
	}

}
	

