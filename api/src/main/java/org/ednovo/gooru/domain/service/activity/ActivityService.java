/////////////////////////////////////////////////////////////
// ActivityService.java
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
package org.ednovo.gooru.domain.service.activity;

import java.util.List;

import org.ednovo.gooru.core.api.model.Activity;
import org.ednovo.gooru.core.api.model.ActivityLog;
import org.ednovo.gooru.core.api.model.ActivityStream;
import org.ednovo.gooru.core.api.model.ActivitySummary;
import org.ednovo.gooru.core.api.model.User;

public interface ActivityService {

	List<Activity> findActivities(User user, String type);

	List<Activity> findMyActivities(User user);

	List<ActivityStream> findActivityStreamByUser(User user);

	ActivityStream findActivityStreamByType(ActivityStream activityStream);

	List<Activity> findOthersActivities(User user);

	void saveActivity(String userId, String contentGooruId, String activityName, String description);

	void insertActivityLog(String eventId, String eventName, String type, String userIp, Integer userId, String contentGooruOid, String parentGooruOid, String context, String sessionToken);

	ActivityLog findActivityLogByEventId(String eventId);

	ActivitySummary findActivitySummaryByEventId(String eventId, String userIp);

	List<ActivityLog> findActivitiesLog();

	void createSummariesOfActivityLogs();

	void updateActivitySummaries(Integer withInHours);

	void createResourceInfoOfResources();

	void updateViewCountsOfResourceInfos();

	void updateSubscriptionCountsOfResourceInfos();

	void updateActivitySettings(User user, String sharing, String activityType);

	void updateBulkActivitySettings(User user, String sharing, String activityType);
}
