package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;

import net.sf.json.JSONArray;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollectionEventLog extends EventLog implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	public void collectionEventLog(String courseId, String unitId, String lessonId, String collectionId, User user, String collectionType, Collection data, String action) {
		try {
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, collectionId);
			context.put(PARENT_GOORU_ID, lessonId);
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			
			payLoadObject.put(COURSE_GOORU_ID, courseId);
			payLoadObject.put(UNIT_GOORU_ID, unitId);
			payLoadObject.put(LESSON_GOORU_ID, lessonId);
			List<String> classUids = this.getClassRepository().getClassUid(courseId);
			if (!classUids.isEmpty()) {
				JSONArray newArray = new JSONArray();
				newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray);
				SessionContextSupport.putLogParameter(EVENT_NAME, action.equalsIgnoreCase(ADD)? ITEM_CREATE:CLASS_ITEM_DELETE);
			} else {
				SessionContextSupport.putLogParameter(EVENT_NAME, action.equalsIgnoreCase(ADD)? ITEM_CREATE:CLASS_ITEM_DELETE);
			}
			
			if(action.equalsIgnoreCase(ADD)){
				payLoadObject.put(MODE, ADD);
				payLoadObject.put(DATA, data);
			}
			else{
				payLoadObject.put(MODE, DELETE);
			}
			if (collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(TYPE, ASSESSMENT);
				payLoadObject.put(ITEM_TYPE, (!classUids.isEmpty())? CLASS_COURSE_ASSESSMENT:SHELF_COURSE_ASSESSMENT);
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(TYPE, COLLECTION);
				payLoadObject.put(ITEM_TYPE, (!classUids.isEmpty())? CLASS_COURSE_COLLECTION:SHELF_COURSE_COLLECTION);
			} else if(collectionType.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())){
				payLoadObject.put(TYPE, ASSESSMENT_URL);
				payLoadObject.put(ITEM_TYPE, (!classUids.isEmpty())? CLASS_COURSE_ASSESSMENT_URL:SHELF_COURSE_ASSESSMENT_URL);
			}
			
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
			SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public void collectionItemEventLog(String collectionId, String resourceId, String userUid, String contentType, Object data, String action) {
		try {
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, resourceId);
			context.put(PARENT_GOORU_ID, collectionId);
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			payLoadObject.put(MODE, DELETE);
			if (contentType.equalsIgnoreCase(QUESTION)) {
				payLoadObject.put(TYPE, QUESTION);
			} else {
				payLoadObject.put(TYPE, RESOURCE);
			}
			if (contentType.equalsIgnoreCase(QUESTION)) {
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_QUESTION);
			} else {
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_RESOURCE);
			}
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(ORGANIZATION_UID, userUid != null);
			SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}

}
