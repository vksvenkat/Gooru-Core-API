package org.ednovo.gooru.core.api.model;

public class RequestMappingUri {

	public static final String TAXONOMY_COURSE = "/v1/taxonomycourse";

	public static final String SUBJECT = "/v1/subject";

	public static final String SUBDOMAIN = "/v1/sub-domain";

	public static final String DOMAIN = "/v1/domain";

	public static final String V3_CLASS = "/v3/class";

	public static final String SEPARATOR = "/";

	public static final String META = "/v1/meta";

	public static final String V3_COLLECTION = "/v3/collection";

	public static final String COURSE = "/v1/course";

	public static final String UNIT = "/v1/course/{courseId}/unit";

	public static final String LESSON = "/v1/course/{courseId}/unit/{unitId}/lesson";

	public static final String ID = "/{id}";
	
	public static final String CLASS_MEMBER = "/{id}/member";

	public static final String LESSON_COLLECTION = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection";

	public static final String LESSON_COLLECTION_ID = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{id}";
	
	public static final String LESSON_COLLECTION_ITEM_ID = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{id}/item/{collectionItemId}";

	public static final String TARGET_LESSON = "/course/{courseId}/targetUnit/{unitId}/targetLesson/{lessonId}";

	public static final String CREATE_QUESTION = "/id}/question";

	public static final String CREATE_RESOURCE = "/{id}/resource";

	public static final String UPDATE_RESOURCE = "/collectionId}/resource/{id}";

	public static final String UPDATE_QUESTION = "/collectionId}/question/{id}";
	
	public static final String USER_COURSES = "/{id}/course";
	
	public static final String LESSON_COLLECTION_ITEM = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{id}/item";
	
	public static final String LESSON_CREATE_QUESTION = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/id}/question";

	public static final String LESSON_CREATE_RESOURCE = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{id}/resource";

	public static final String LESSON_UPDATE_RESOURCE = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{collectionId}/resource/{id}";

	public static final String LESSON_UPDATE_QUESTION = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{collectionId}/question/{id}";
	
	public static final String COURSES_CLASS = "/{id}/classes";
	

}
