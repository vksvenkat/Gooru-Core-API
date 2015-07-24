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
public class CollectionEventLog implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionEventLog.class);

	final static String CLASS_ITEM_MOVE = "class.item.move";

	private static final String CLASS_ITEM_DELETE = "class.item.delete";

	private static final String CLASS_GOORU_IDS = "classGooruIds";

	private static final String SHELF_COURSE_COLLECTION = "shelf.course.unit.lesson.collection";

	private static final String CLASS_COURSE_COLLECTION = "class.course.unit.lesson.collection";

	private static final String SHELF_COURSE_ASSESSMENT = "shelf.course.unit.lesson.assessment";

	private static final String CLASS_COURSE_ASSESSMENT = "class.course.unit.lesson.assessment";

	private static final String SHELF_COURSE_ASSESSMENT_URL = "shelf.course.unit.lesson.assessment-url";

	private static final String CLASS_COURSE_ASSESSMENT_URL = "class.course.unit.lesson.assessment-url";

	private static final String SHELF_COURSE_RESOURCE = "shelf.course.unit.lesson.collection.resource";

	private static final String SHELF_COURSE_QUESTION = "shelf.course.unit.lesson.collection.question";

	public void deleteCollectionEventLog(String courseId, String unitId, String lessonId, String collectionId, User user, String collectionType) {
		try {
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(CONTENT_GOORU_ID, collectionId);
			context.put(PARENT_GOORU_ID, lessonId);
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
			payLoadObject.put(MODE, DELETE);
			if (collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(TYPE, ASSESSMENT);
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
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
				SessionContextSupport.putLogParameter(EVENT_NAME, CLASS_ITEM_DELETE);
			} else {
				payLoadObject.remove(CLASS_GOORU_IDS);
				SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_ASSESSMENT);
			} else if (collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_ASSESSMENT);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_COLLECTION);
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, SHELF_COURSE_COLLECTION);
			}
			if (!classUids.isEmpty() && collectionType.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())) {
				payLoadObject.put(ITEM_TYPE, CLASS_COURSE_ASSESSMENT_URL);
			} else if (collectionType.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())) {
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

	public void deleteCollectionItemEventLog(String collectionId, String resourceId, String userUid, String contentType) {
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
