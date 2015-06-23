package org.ednovo.gooru.core.api.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetaConstants {
	public static final Map<String, Object> COURSE_SUMMARY;
	static {
		Map<String, Object> courseSummary = new HashMap<String, Object>();
		courseSummary.put("unitCount", 0);
		courseSummary.put("lessonCount", 0);
		courseSummary.put("assessmentCount", 0);
		courseSummary.put("CollectionCount", 0);
		COURSE_SUMMARY = Collections.unmodifiableMap(courseSummary);
	}

	public static final Map<String, Object> UNIT_SUMMARY;
	static {
		Map<String, Object> unitSummary = new HashMap<String, Object>();
		unitSummary.put("lessonCount", 0);
		unitSummary.put("assessmentCount", 0);
		unitSummary.put("CollectionCount", 0);
		UNIT_SUMMARY = Collections.unmodifiableMap(unitSummary);
	}

	public static final Map<String, Object> LESSON_SUMMARY;
	static {
		Map<String, Object> lessonSummary = new HashMap<String, Object>();
		lessonSummary.put("assessmentCount", 0);
		lessonSummary.put("CollectionCount", 0);
		LESSON_SUMMARY = Collections.unmodifiableMap(lessonSummary);
	}

	public static final Map<String, Object> COLLECTION_SUMMARY;
	static {
		Map<String, Object> collectionSummary = new HashMap<String, Object>();
		collectionSummary.put("questionCount", 0);
		collectionSummary.put("resourceCount", 0);
		COLLECTION_SUMMARY = Collections.unmodifiableMap(collectionSummary);
	}
}
