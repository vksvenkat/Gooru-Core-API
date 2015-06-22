package org.ednovo.gooru.core.api.model;

public class RequestMappingUri {

	public static final String TAXONOMY_COURSE = "/taxonomycourse";

	public static final String SUBJECT = "/subject";

	public static final String SUBDOMAIN = "/subdomain";

	public static final String DOMAIN = "/domain";

	public static final String V3_CLASS = "/v3/class";

	public static final String SEPARATOR = "/";

	public static final String V3_COLLECTION = "/v3/collection";

	public static final String COURSE = "/v1/course";

	public static final String UNIT = "/v1/course/{courseId}/unit";

	public static final String LESSON = "/v1/course/{courseId}/unit/{id}/lesson";

	public static final String ID = "/{id}";

	public static final String LESSON_COLLECTION = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection";
	
	public static final String LESSON_COLLECTION_ID = "/v1/course/{courseId}/unit/{unitId}/lesson/{lessonId}/collection/{id}";

	public static final String TARGET_LESSON = "/course/{courseId}/targetUnit/{unitId}/targetLesson/{lessonId}";
	
	public static final String CREATE_QUESTION = "/id}/question";
	
	public static final String CREATE_RESOURCE = "/{id}/resource";
	
	public static final String UPDATE_RESOURCE = "/collectionId}/resource/{id}";
	
	public static final String UPDATE_QUESTION = "/collectionId}/question/{id}";
}
