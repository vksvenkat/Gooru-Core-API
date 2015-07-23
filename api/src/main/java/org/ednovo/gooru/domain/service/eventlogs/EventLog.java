package org.ednovo.gooru.domain.service.eventlogs;

import java.util.List;

import net.sf.json.JSONArray;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EventLog implements ConstantProperties, ParameterProperties{

	@Autowired
	private ClassRepository classRepository;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(CourseEventLog.class);
	
	public final static String CLASS_ITEM_MOVE = "class.item.move";

	public static final String CLASS_ITEM_DELETE = "class.item.delete";
	
	public static final String CLASS_GOORU_IDS = "classGooruIds";
	
	//lesson
	public static final String SHELF_COURSE_LESSON = "shelf.course.unit.lesson";

	public static final String CLASS_COURSE_LESSON = "class.course.unit.lesson";
	
	//unit
	public static final String SHELF_COURSE_UNIT = "shelf.course.unit";

	public static final String CLASS_COURSE_UNIT = "class.course.unit";
	
	//course
	public static final String CLASS_COURSE = "class.course";

	public static final String SHELF_COURSE = "shelf.course";
	
	//collection
	public static final String SHELF_COURSE_COLLECTION = "shelf.course.unit.lesson.collection";

	public static final String CLASS_COURSE_COLLECTION = "class.course.unit.lesson.collection";

	public static final String SHELF_COURSE_ASSESSMENT = "shelf.course.unit.lesson.assessment";

	public static final String CLASS_COURSE_ASSESSMENT = "class.course.unit.lesson.assessment";

	public static final String SHELF_COURSE_ASSESSMENT_URL = "shelf.course.unit.lesson.assessment-url";

	public static final String CLASS_COURSE_ASSESSMENT_URL = "class.course.unit.lesson.assessment-url";

	public static final String SHELF_COURSE_RESOURCE = "shelf.course.unit.lesson.collection.resource";

	public static final String SHELF_COURSE_QUESTION = "shelf.course.unit.lesson.collection.question";


	public JSONObject payLoadObject(String courseId,List<String> classUids, JSONObject payLoadObject, String action, Collection data){
		
		try{
			payLoadObject.put(TYPE, COURSE);
			payLoadObject.put(COURSE_GOORU_ID, courseId);
			
			
			if (!classUids.isEmpty()) {
				JSONArray newArray = new JSONArray();
				newArray.addAll(classUids);
				payLoadObject.put(CLASS_GOORU_IDS, newArray.toString());
				SessionContextSupport.putLogParameter(EVENT_NAME, (action.equalsIgnoreCase(ADD)? ITEM_CREATE:CLASS_ITEM_DELETE));
			} else {
				SessionContextSupport.putLogParameter(EVENT_NAME, action.equalsIgnoreCase(ADD)? ITEM_CREATE:ITEM_DELETE);
			}
			
			if(action.equalsIgnoreCase(ADD)){
				payLoadObject.put(MODE, ADD);
				payLoadObject.put(DATA, data);
			}
			else if(action.equalsIgnoreCase(DELETE)){
				payLoadObject.put(MODE, DELETE);
			}
			
			return payLoadObject;
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
		return null;
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}
}
