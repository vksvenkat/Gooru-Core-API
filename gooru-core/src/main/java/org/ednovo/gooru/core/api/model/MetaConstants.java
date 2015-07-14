package org.ednovo.gooru.core.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetaConstants {
	public static final Map<String, Object> COURSE_SUMMARY;
	public static final String UNIT_COUNT = "unitCount";
	public static final String LESSON_COUNT = "lessonCount";
	public static final String ASSESSMENT_COUNT = "assessmentCount";
	public static final String COLLECTION_COUNT = "collectionCount";
	public static final String QUESTION_COUNT = "questionCount";
	public static final String RESOURCE_COUNT = "resourceCount";
	public static final Short CONTENT_CLASSIFICATION_STANDARD_TYPE_ID = 1;
	public static final Short CONTENT_CLASSIFICATION_SKILLS_TYPE_ID = 2;

	static {
		Map<String, Object> courseSummary = new HashMap<String, Object>();
		courseSummary.put(UNIT_COUNT, 0);
		courseSummary.put(LESSON_COUNT, 0);
		courseSummary.put(ASSESSMENT_COUNT, 0);
		courseSummary.put(COLLECTION_COUNT, 0);
		COURSE_SUMMARY = Collections.unmodifiableMap(courseSummary);
	}

	public static final Map<String, Object> UNIT_SUMMARY;
	static {
		Map<String, Object> unitSummary = new HashMap<String, Object>();
		unitSummary.put(LESSON_COUNT, 0);
		unitSummary.put(ASSESSMENT_COUNT, 0);
		unitSummary.put(COLLECTION_COUNT, 0);
		UNIT_SUMMARY = Collections.unmodifiableMap(unitSummary);
	}

	public static final Map<String, Object> LESSON_SUMMARY;
	static {
		Map<String, Object> lessonSummary = new HashMap<String, Object>();
		lessonSummary.put(ASSESSMENT_COUNT, 0);
		lessonSummary.put(COLLECTION_COUNT, 0);
		LESSON_SUMMARY = Collections.unmodifiableMap(lessonSummary);
	}

	public static final Map<String, Object> COLLECTION_SUMMARY;
	static {
		Map<String, Object> collectionSummary = new HashMap<String, Object>();
		collectionSummary.put(QUESTION_COUNT, 0);
		collectionSummary.put(RESOURCE_COUNT, 0);
		COLLECTION_SUMMARY = Collections.unmodifiableMap(collectionSummary);
	}

	public static final Map<Short, Object> CONTENT_CLASSIFICATION_TYPE;
	static {
		Map<Short, Object> contentClassificationType = new HashMap<Short, Object>();
		contentClassificationType.put((short) 1, "standard");
		contentClassificationType.put((short) 2, "21st century skills");
		contentClassificationType.put((short) 3, "Learning targets");
		CONTENT_CLASSIFICATION_TYPE = Collections.unmodifiableMap(contentClassificationType);
	}
}
