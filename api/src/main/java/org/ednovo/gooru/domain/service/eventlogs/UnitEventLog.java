package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;

import net.sf.json.JSONArray;

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
public class UnitEventLog implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(UnitEventLog.class);

	final static String CLASS_ITEM_MOVE = "class.item.move";

	private static final String CLASS_ITEM_DELETE = "class.item.delete";

	private static final String CLASS_GOORU_IDS = "classGooruIds";

	private static final String SHELF_COURSE_UNIT = "shelf.course.unit";

	private static final String CLASS_COURSE_UNIT = "class.course.unit";


	public void deleteEventLogs(String courseId, String unitId, User user) {
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
				SessionContextSupport.putLogParameter(EVENT_NAME, CLASS_ITEM_DELETE);
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

	public ClassRepository getClassRepository() {
		return classRepository;
	}

}
