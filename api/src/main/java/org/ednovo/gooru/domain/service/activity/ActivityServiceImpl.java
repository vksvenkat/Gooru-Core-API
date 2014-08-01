/////////////////////////////////////////////////////////////
// ActivityServiceImpl.java
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

import java.util.ArrayList;
import java.util.List;

import org.ednovo.gooru.core.api.model.Activity;
import org.ednovo.gooru.core.api.model.ActivityLog;
import org.ednovo.gooru.core.api.model.ActivityStream;
import org.ednovo.gooru.core.api.model.ActivitySummary;
import org.ednovo.gooru.core.api.model.ActivityType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private ActivityRepository activityRepository;

	@Override
	public List<Activity> findActivities(final User user, final String type) {
		return activityRepository.findActivities(user, type);
	}

	@Override
	public List<Activity> findMyActivities(final User user) {
		return activityRepository.findMyActivities(user);
	}

	@Override
	public List<ActivityStream> findActivityStreamByUser(final User user) {
		return activityRepository.findActivityStreamByUser(user);
	}

	@Override
	public ActivityStream findActivityStreamByType(final ActivityStream activityStream) {
		return activityRepository.findActivityStreamByType(activityStream);
	}

	@Override
	public List<Activity> findOthersActivities(final User user) {
		return activityRepository.findOthersActivities(user);
	}

	@Override
	public void saveActivity(final String userId, final String contentGooruId, final String activityName, final String description) {
		activityRepository.saveActivity(userId, contentGooruId, activityName, description);

	}

	@Override
	public void insertActivityLog(final String eventId, final String eventName, final String type, final String userIp, final Integer userId, final String contentGooruOid, String parentGooruOid, final String context, final String sessionToken) {
		activityRepository.insertActivityLog(eventId, eventName, type, userIp, userId, contentGooruOid, parentGooruOid, context, sessionToken);
	}

	@Override
	public ActivityLog findActivityLogByEventId(final String eventId) {
		return activityRepository.findActivityLogByEventId(eventId);
	}

	@Override
	public ActivitySummary findActivitySummaryByEventId(final String eventId, final String userIp) {
		return activityRepository.findActivitySummaryByEventId(eventId, userIp);

	}

	@Override
	public List<ActivityLog> findActivitiesLog() {
		return activityRepository.findActivitiesLog();

	}

	@Override
	public void createSummariesOfActivityLogs() {
		activityRepository.createSummariesOfActivityLogs();

	}

	@Override
	public void updateActivitySummaries(final Integer withInHours) {
		activityRepository.updateActivitySummaries(withInHours);
	}

	@Override
	public void createResourceInfoOfResources() {
		activityRepository.createResourceInfoOfResources();

	}

	@Override
	public void updateViewCountsOfResourceInfos() {
		activityRepository.updateViewCountsOfResourceInfos();
	}

	@Override
	public void updateSubscriptionCountsOfResourceInfos() {
		activityRepository.updateSubscriptionCountsOfResourceInfos();
	}

	@Override
	public void updateActivitySettings(final User user, final String sharing, final String activityType) {
		ActivityType type = new ActivityType();
		type.setName(activityType);

		ActivityStream stream = new ActivityStream();
		stream.setActivityType(type);
		stream.setUser(user);

		stream = findActivityStreamByType(stream);
		stream.setSharing(sharing);

		activityRepository.save(stream);
	}

	@Override
	public void updateBulkActivitySettings(final User user, final String sharing, final String activityType) {

		String[] activityTypeArray = activityType.split(",");
		String[] sharingArray = sharing.split(",");
		final List<ActivityStream> activities = new ArrayList<ActivityStream>();

		for (int activityIndex = 0; activityIndex < activityTypeArray.length; activityIndex++) {
			ActivityType type = new ActivityType();
			type.setName(activityTypeArray[activityIndex]);

			ActivityStream stream = new ActivityStream();
			stream.setActivityType(type);
			stream.setUser(user);

			stream = findActivityStreamByType(stream);
			if (stream == null) {
				ActivityStream streamNew = new ActivityStream();
				streamNew.setActivityType(type);
				streamNew.setUser(user);
				streamNew.setSharing(sharingArray[activityIndex]);
				activities.add(streamNew);
			} else {
				stream.setSharing(sharingArray[activityIndex]);
				activities.add(stream);
			}

		}
		activityRepository.saveAll(activities);
	}
}
