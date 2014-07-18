/////////////////////////////////////////////////////////////
// LogUtil.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.application.util;

import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.ActivityRepository;
import org.hibernate.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

	public static final String CLASSPLAN_CREATE = "classplan.create";
	public static final String CLASSPLAN_DELETE = "classplan.delete";
	public static final String CLASSPLAN_EDIT = "classplan.edit";
	public static final String CLASSPLAN_COPY = "classplan.copy";
	public static final String CLASSPLAN_SEARCH = "classplan.search";
	public static final String ASSESSMENT_CREATE = "assessment.create";
	public static final String ASSESSMENT_DELETE = "assessment.delete";
	public static final String ASSESSMENT_EDIT = "assessment.edit";
	public static final String ASSESSMENT_SEARCH = "assessment.search";
	public static final String QUESTION_ADD = "question.add";
	public static final String QUESTION_EDIT = "question.edit";
	public static final String QUESTION_DELETE = "question.delete";
	public static final String QUESTION_SEARCH = "question.search";

	public static final String SEGMENT_ADD = "segment.add";
	public static final String SEGMENT_REMOVE = "segment.remove";
	public static final String SEGMENT_EDIT = "segment.edit";
	public static final String RESOURCE_ADD = "resource.add";
	public static final String RESOURCE_EDIT = "resource.edit";
	public static final String RESOURCE_REMOVE = "resource.remove";
	public static final String CLASSPLAN_VIEW = "classplan.teach";
	public static final String SEGMENT_VIEW = "segment.view";
	public static final String RESOURCE_PLAY = "resource.play";
	public static final String SEGMENT_CLOSE = "segment.close";
	public static final String CLASSPLAN_CLOSE = "classplan.close";
	public static final String RESOURCE_STOP = "resource.stop";
	public static final String QUOTE_CREATE = "quote.create";
	public static final String QUOTE_EDIT = "quote.edit";

	public static final String CLASSBOOK_CREATE = "classbook.create";
	public static final String CLASSBOOK_DELETE = "classbook.delete";
	public static final String CLASSBOOK_EDIT = "classbook.edit";
	public static final String CLASSBOOK_COPY = "classbook.copy";
	public static final String CLASSBOOK_SEARCH = "classbook.search";
	public static final String CLASSBOOK_STUDY = "classbook.study";

	private static ActivityRepository activityRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

	public static final String getActivityLogStream(String application, String subject, String object, String predicate, String description) {
		if (predicate.equals(CLASSBOOK_COPY) || predicate.equals(CLASSBOOK_CREATE) || predicate.equals(CLASSPLAN_CREATE) || predicate.equals(CLASSPLAN_COPY) || predicate.equals(RESOURCE_ADD) || predicate.equals(QUOTE_CREATE) || predicate.equals(ASSESSMENT_CREATE) || predicate.equals(QUESTION_ADD)) {

			String userId = subject.split(":")[1];
			String contentGooruId = object.split(":")[1];

			try {
				LogUtil.getActivityRepository().saveActivity(userId, contentGooruId, predicate, description);
			} catch (Exception e) {
				LOGGER.error("Error while saving activity " + e);
			}

		}

		return "application:" + application + "#" + subject + "#" + object + "#" + "event:" + predicate + "#descption:" + description;
	}

	public static final String getApplicationLogStream(String application, String message) {
		return "application:" + application + "  | " + message;
	}

	public static void setActivityRepository(ActivityRepository activityRepository) {
		LogUtil.activityRepository = activityRepository;
	}

	public static ActivityRepository getActivityRepository() {
		return activityRepository;
	}
}
