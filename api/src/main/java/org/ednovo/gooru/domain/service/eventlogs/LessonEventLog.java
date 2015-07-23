package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;

import net.sf.json.JSONArray;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LessonEventLog extends EventLog implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	public void lessonEventLogs(String courseId, String unitId, String lessonId, User user, Collection data, String action) {
		try {
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, lessonId);
			context.put(PARENT_GOORU_ID, unitId);
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			payLoadObject.put(TYPE, LESSON);
			payLoadObject.put(COURSE_GOORU_ID, courseId);
			payLoadObject.put(UNIT_GOORU_ID, unitId);
			payLoadObject.put(LESSON_GOORU_ID, lessonId);
			List<String> classUids = this.getClassRepository().getClassUid(courseId);
			if (!classUids.isEmpty()) {
				JSONArray newArray = new JSONArray();
				newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray);
				SessionContextSupport.putLogParameter(EVENT_NAME, action.equalsIgnoreCase(ADD)? ITEM_CREATE:CLASS_ITEM_DELETE);
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_LESSON);
			} else {
				SessionContextSupport.putLogParameter(EVENT_NAME, action.equalsIgnoreCase(ADD)? ITEM_CREATE:CLASS_ITEM_DELETE);
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_LESSON);
			}
			
			if(action.equalsIgnoreCase(ADD)){
				payLoadObject.put(MODE, ADD);
				payLoadObject.put(DATA, data);
			}
			else{
				payLoadObject.put(MODE, DELETE);
			}
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
			SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}

}
