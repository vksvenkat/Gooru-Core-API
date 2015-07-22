package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;
import net.sf.json.JSONArray;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassEventLog implements ConstantProperties, ParameterProperties {
	
	@Autowired
	private ClassRepository classRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassEventLog.class);

	public void getEventLogs(String courseId, String unitId, String lessonId, String collectionId, User user, String collectionType) throws Exception {
			try {
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, collectionId);
			context.put(PARENT_GOORU_ID, lessonId);
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			payLoadObject.put(MODE, DELETE);
			if (collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(TYPE, ASSESSMENT);
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())){
				payLoadObject.put(TYPE, COLLECTION);
			} else {
                payLoadObject.put(TYPE, ASSESSMENT_URL);
			}
			payLoadObject.put(COURSE_GOORU_ID, courseId);
			payLoadObject.put(UNIT_GOORU_ID, unitId);
			payLoadObject.put(LESSON_GOORU_ID, lessonId);
			List<String> classUids = this.getClassRepository().getClassUid(courseId);
			JSONArray newArray = new JSONArray();
			newArray.addAll(classUids);
			payLoadObject.put(CLASS_GOORU_IDS, newArray.toString());
			if (!classUids.isEmpty()) {
				SessionContextSupport.putLogParameter(EVENT_NAME,CLASS_ITEM_DELETE);
			} else {
				payLoadObject.remove(CLASS_GOORU_IDS);
				SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_ASSESSMENT);
			} else if(collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_ASSESSMENT);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_COLLECTION);
			} else if(collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())){
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_COLLECTION);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_ASSESSMENT_URL);
			} else if(collectionType.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())){
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_ASSESSMENT_URL);
			}
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
			SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}
		
			public void getEventLogs(String courseId, User user,String course) throws Exception {
				try {		
				JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
				context.put(CONTENT_GOORU_ID, courseId);
				context.put(PARENT_GOORU_ID, course); 
				SessionContextSupport.putLogParameter(CONTEXT, context.toString());
				JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
				payLoadObject.put(MODE, DELETE);
				payLoadObject.put(TYPE, COURSE);
				payLoadObject.put(COURSE_GOORU_ID, courseId);
				List<String> classUids = this.getClassRepository().getClassUid(courseId);
			    JSONArray newArray = new JSONArray();
			    newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray.toString());
			    if (!classUids.isEmpty()){
				   SessionContextSupport.putLogParameter(EVENT_NAME, CLASS_ITEM_DELETE);
			    } else {
			       payLoadObject.remove(CLASS_GOORU_IDS);
				   SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
				}
			    if(!classUids.isEmpty()){
					 payLoadObject.put(ITEM_TYPE, CLASS_COURSE);
				 } else {
					 payLoadObject.put(ITEM_TYPE, SHELF_COURSE);
				 }
				SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
				JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
				session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
				SessionContextSupport.putLogParameter(SESSION, session.toString());
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
	}
			
			public void getEventLogs(String courseId, String unitId, User user) throws Exception {
				try {			
				JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
				context.put(CONTENT_GOORU_ID, unitId);
				context.put(PARENT_GOORU_ID, courseId);
				SessionContextSupport.putLogParameter(CONTEXT, context.toString());
				JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
				payLoadObject.put(MODE, DELETE);
				payLoadObject.put(TYPE, UNIT);
				payLoadObject.put(COURSE_GOORU_ID, courseId);
				payLoadObject.put(UNIT_GOORU_ID, unitId);
				List<String> classUids = this.getClassRepository().getClassUid(courseId);
				JSONArray newArray = new JSONArray();
				newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray.toString());
				if (!classUids.isEmpty()) {
					SessionContextSupport.putLogParameter(EVENT_NAME,CLASS_ITEM_DELETE);
				} else {
					payLoadObject.remove(CLASS_GOORU_IDS);
					SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
				}
				if (!classUids.isEmpty()) {
					payLoadObject.put(ITEM_TYPE, CLASS_COURSE_UNIT);
				} else {
					payLoadObject.put(ITEM_TYPE, SHELF_COURSE_UNIT);
				}
				SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
				JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
				session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
				SessionContextSupport.putLogParameter(SESSION, session.toString());
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
       }
			
			public void getEventLogs(String courseId, String unitId, String lessonId, User user) throws Exception {
				try {		
				JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
				context.put(CONTENT_GOORU_ID, lessonId);
				context.put(PARENT_GOORU_ID, unitId);
				SessionContextSupport.putLogParameter(CONTEXT, context.toString());
				JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
				payLoadObject.put(MODE, DELETE);
				payLoadObject.put(TYPE, LESSON);
				payLoadObject.put(COURSE_GOORU_ID, courseId);
				payLoadObject.put(UNIT_GOORU_ID, unitId);
				payLoadObject.put(LESSON_GOORU_ID, lessonId);
				List<String> classUids = this.getClassRepository().getClassUid(courseId);
				JSONArray newArray = new JSONArray();
				newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray.toString());
				if (!classUids.isEmpty()) {
					SessionContextSupport.putLogParameter(EVENT_NAME,CLASS_ITEM_DELETE);
				} else {
					payLoadObject.remove(CLASS_GOORU_IDS);
					SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
				}
				if (!classUids.isEmpty()) {
					payLoadObject.put(ITEM_TYPE, CLASS_COURSE_LESSON);
				} else {
					payLoadObject.put(ITEM_TYPE, SHELF_COURSE_LESSON);
				}
				SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
				JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
				session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
				SessionContextSupport.putLogParameter(SESSION, session.toString());
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
        }
			
			public void getEventLogs(String collectionId, String resourceId,String userUid,String contentType) throws Exception {
				try {			
				JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
				context.put(CONTENT_GOORU_ID,resourceId );
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
				if(contentType.equalsIgnoreCase(QUESTION)){
					payLoadObject.put(ITEM_TYPE, SHELF_COURSE_QUESTION );
				} else {
					payLoadObject.put(ITEM_TYPE, SHELF_COURSE_RESOURCE);
				}
				SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
				JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
				session.put(ORGANIZATION_UID, userUid != null );
				SessionContextSupport.putLogParameter(SESSION, session.toString());
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
}

		public ClassRepository getClassRepository() {
			return classRepository;
		}
	
}
		
		
		
		
		
		
		
		
		
