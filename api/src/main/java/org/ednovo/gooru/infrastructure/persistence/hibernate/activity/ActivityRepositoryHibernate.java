/////////////////////////////////////////////////////////////
// ActivityRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.activity;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.core.api.model.Activity;
import org.ednovo.gooru.core.api.model.ActivityLog;
import org.ednovo.gooru.core.api.model.ActivityStream;
import org.ednovo.gooru.core.api.model.ActivitySummary;
import org.ednovo.gooru.core.api.model.ActivityType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Repository;

@Repository("activityRepository")
public class ActivityRepositoryHibernate extends BaseRepositoryHibernate implements ActivityRepository {

	private JdbcTemplate jdbcTemplate;

	private UserRepository userRepository;

	private ContentRepository contentRepository;

	private static final String RETRIEVE_ACTIVITY = "Select s.gooru_oid, s.sharing, a.activity_id, a.user_id, a.type_name, a.content_id, a.created_date, a.description from activity a, activity_stream c, content s, resource r , user u where a.user_uid = c.user_uid and a.type_name = c.type_name and c.sharing = 'public' and a.user_uid !='%s' and s.content_id = a.content_id and r.content_id = a.content_id and r.type_name = '%s' and u.gooru_uid=a.user_uid  and (%s) order by a.created_date desc limit 5;";

	private static final String RETRIEVE_OTHER_ACTIVITY = "Select r.type_name as learnguide, s.gooru_oid, s.sharing, a.activity_id, a.user_id, a.type_name, a.content_id, a.created_date, a.description from activity a, activity_stream c, content s, resource r, user u  where a.user_uid = c.user_uid and a.type_name = c.type_name and c.sharing = 'public' and a.user_id ='%s' and s.content_id = a.content_id and r.content_id = a.content_id  and u.gooru_uid=a.user_uid and  (%s) order by a.created_date desc limit 5;";

	private static final String RETRIEVE_MY_ACTIVITY = "Select r.type_name as learnguide, s.gooru_oid, s.sharing, a.activity_id, a.user_id, a.type_name, a.content_id, a.created_date, a.description from activity a, activity_stream c, content s, resource r, user u  where a.user_uid = c.user_uid and a.type_name = c.type_name and a.user_uid ='%s' and s.content_id = a.content_id and r.content_id = a.content_id and u.gooru_uid=a.user_uid and (%s) order by a.created_date desc limit 5;";

	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityRepositoryHibernate.class);

	@Autowired
	public ActivityRepositoryHibernate(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate, UserRepository userRepository, ContentRepository contentRepository) {
		super();
		setSessionFactory(sessionFactory);
		setJdbcTemplate(jdbcTemplate);
		setContentRepository(contentRepository);
		setUserRepository(userRepository);
	}

	@Override
	public List<Activity> findActivities(User user, String type) {

		String retrieveSegment = "";
		// FIXME: temporary for retriving only one result from each user:
		String selectOneResultFromEachUser = "Select s.gooru_oid, s.sharing, a.activity_id, a.user_id, a.type_name, a.content_id, a.created_date, a.description, a.user_uid from activity a, activity_stream c, content s, activity_type at , user u where a.user_uid = c.user_uid and a.type_name = c.type_name and c.sharing = 'public' and s.sharing = 'public' and s.content_id = a.content_id and at.name = a.type_name and at.active_flag = 1  and u.gooru_uid=a.user_uid and  "
				+ generateOrgAuthSqlQueryWithData("u.") + "  order by a.created_date desc limit 300;";

		if (type.equalsIgnoreCase("all")) {
			// retrieveSegment = DatabaseUtil.format(RETRIEVE_ACTIVITY_ALL,
			// user.getUserId());
			retrieveSegment = DatabaseUtil.format(selectOneResultFromEachUser);
		} else {
			retrieveSegment = DatabaseUtil.format(RETRIEVE_ACTIVITY, user.getPartyUid(), type, generateOrgAuthSqlQueryWithData("u."));
		}

		List<Activity> annotations = this.getJdbcTemplate().query(retrieveSegment, new RowMapper() {
			private Activity activity = new Activity();

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					activity = new Activity();
					activity.setActivityId(rs.getLong("activity_id"));
					activity.setDescription(rs.getString("description"));

					User user = new User();
					// user.setUserId(new Integer(rs.getString("user_id")));
					user = (User) get(User.class, (rs.getString("user_uid")));

					activity.setUser(user);

					ActivityType activityType = new ActivityType();
					// activityType.setName(rs.getString("type_name"));
					activityType = (ActivityType) get(ActivityType.class, rs.getString("type_name"));

					activity.setActivityType(activityType);

					activity.setCreatedOn(rs.getTimestamp("created_date"));

					// if(rs.getString("gooru_oid") != 0) {
					Content content = new Content();
					content.setGooruOid((rs.getString("gooru_oid")));
					content.setSharing(rs.getString("sharing"));
					// content = (Content) get(Content.class,
					// rs.getLong("gooru_oid"));
					activity.setContent(content);

					return activity;
				} catch (ObjectRetrievalFailureException ex) {
					LOGGER.error("Error while getting activity", ex);
				}
				return activity;
			}
		});

		return annotations;
	}

	@Override
	public List<Activity> findMyActivities(final User user) {

		String retrieveSegment = DatabaseUtil.format(RETRIEVE_MY_ACTIVITY, user.getPartyUid(), generateOrgAuthSqlQueryWithData("u."));

		List<Activity> annotations = this.getJdbcTemplate().query(retrieveSegment, new RowMapper() {
			private Activity activity = new Activity();

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					activity = new Activity();
					activity.setActivityId(rs.getLong("activity_id"));
					activity.setDescription(rs.getString("description"));
					activity.setUser(user);
					activity.setCreatedOn(rs.getTimestamp("created_date"));

					ActivityType activityType = new ActivityType();
					activityType = (ActivityType) get(ActivityType.class, rs.getString("type_name"));
					activity.setActivityType(activityType);

					// if(rs.getString("gooru_oid") != 0) {
					Content content = new Content();
					content.setGooruOid((rs.getString("gooru_oid")));
					content.setSharing(rs.getString("sharing"));

					ContentType ct = new ContentType();
					ct.setName(rs.getString("learnguide"));
					content.setContentType(ct);
					// content = (Content) get(Content.class,
					// rs.getLong("gooru_oid"));
					activity.setContent(content);

					return activity;
				} catch (ObjectRetrievalFailureException ex) {
					LOGGER.error("Error while getting activity", ex);
				}
				return activity;
			}
		});

		return annotations;
	}

	@Override
	public List<Activity> findOthersActivities(final User user) {

		String retrieveSegment = DatabaseUtil.format(RETRIEVE_OTHER_ACTIVITY, user.getPartyUid(), generateOrgAuthSqlQueryWithData("u."));

		List<Activity> annotations = this.getJdbcTemplate().query(retrieveSegment, new RowMapper() {
			private Activity activity = new Activity();

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					activity = new Activity();
					activity.setActivityId(rs.getLong("activity_id"));
					activity.setDescription(rs.getString("description"));
					activity.setUser(user);
					activity.setCreatedOn(rs.getTimestamp("created_date"));

					ActivityType activityType = new ActivityType();
					activityType = (ActivityType) get(ActivityType.class, rs.getString("type_name"));
					activity.setActivityType(activityType);

					// if(rs.getString("gooru_oid") != 0) {
					Content content = new Content();
					content.setGooruOid((rs.getString("gooru_oid")));
					content.setSharing(rs.getString("sharing"));

					ContentType ct = new ContentType();
					ct.setName(rs.getString("learnguide"));
					content.setContentType(ct);
					// content = (Content) get(Content.class,
					// rs.getLong("gooru_oid"));
					activity.setContent(content);

					return activity;
				} catch (ObjectRetrievalFailureException ex) {
					LOGGER.error("Error while getting activity", ex);
				}
				return activity;
			}
		});

		return annotations;
	}

	@Override
	public void saveActivity(String userId, String contentGooruId, String activityName, String description) {

		User user = this.getUserRepository().findByGooruId(userId);

		Content content = this.getContentRepository().findContentByGooruId(contentGooruId);
		ActivityType type = new ActivityType();
		type.setName(activityName);

		Activity activity = new Activity();
		activity.setActivityType(type);
		activity.setContent(content);
		activity.setUser(user);
		activity.setDescription(description);
		activity.setCreatedOn(new Date(System.currentTimeMillis()));

		getSession().saveOrUpdate(activity);

	}

	@Override
	public List<ActivityStream> findActivityStreamByUser(User user) {
		Criteria criteria = getSession().createCriteria(ActivityStream.class).createAlias("user", "user").add(Expression.eq("user.userId", user.getUserId()));
		List<ActivityStream> activities = addOrgAuthCriterias(criteria, "user.").list();
		return activities.size() == 0 ? null : activities;
	}

	@Override
	public ActivityStream findActivityStreamByType(ActivityStream activityStream) {

		return null;
	}

	@Override
	public ActivityLog findActivityLogByEventId(String eventId) {
		String hql = "FROM ActivityLog log  WHERE log.eventId='" + eventId + "' and  " + generateOrgAuthQueryWithData("log.user.");
		List<ActivityLog> activityLogs = this.find(hql);
		return (activityLogs.size() > 0) ? activityLogs.get(0) : null;
	}

	@Override
	public List<ActivityLog> findActivitiesLog() {
		String hql = "FROM ActivityLog log WHERE  " + generateOrgAuthQueryWithData("log.user.");
		return this.find(hql);
	}

	@Override
	public void insertActivityLog(String eventId, String eventName, String type, String userIp, Integer userId, String contentGooruOid, String parentGooruOid, String context, String sessionToken) {
		String sql = " INSERT INTO activity_log SELECT  null , '" + eventId + "' , '" + eventName + "' , now() , '" + type + "' , '" + userIp + "', " + userId + " , con.content_id AS content_id  , par.content_id AS parent_content_id , '" + context + "', '" + sessionToken + "' , null "
				+ " FROM `user` user LEFT OUTER JOIN `content` con ON con.gooru_oid = " + (contentGooruOid != null ? " '" + contentGooruOid + "' " : "null") + " LEFT OUTER JOIN content par ON par.gooru_oid = " + (parentGooruOid != null ? " '" + parentGooruOid + "' " : "null")
				+ " WHERE user.user_id = " + userId + " AND " + generateOrgAuthSqlQueryWithData("user.");
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void createSummariesOfActivityLogs() {
		String sql = "INSERT INTO activity_summary SELECT null , event_id , event_name , event_time , null , null, user_ip , user_id , content_id , parent_content_id , context , session_token , now(), null  FROM activity_log log  join user u  WHERE NOT EXISTS (SELECT summary.event_id FROM activity_summary summary WHERE summary.event_id = log.event_id )  AND log.type = 'start' AND u.gooru_uid=log.user_uid  AND  "
				+ generateOrgAuthSqlQueryWithData("u.");
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void updateActivitySummaries(Integer withInHours) {
		String sql = "UPDATE activity_summary summary INNER JOIN activity_log startLog ON (summary.elapsed_time IS NULL AND summary.event_id = startLog.event_id AND (( startLog.content_id IS NULL AND  summary.content_id IS NULL ) OR (startLog.content_id = summary.content_id)) AND summary.user_id = startLog.user_id ) INNER JOIN activity_log stopLog ON ( startLog.type = 'start' AND stopLog.type = 'stop' AND startLog.event_id = stopLog.event_id AND (( startLog.content_id IS NULL AND  stopLog.content_id IS NULL ) OR (startLog.content_id = stopLog.content_id)) AND stopLog.user_id = startLog.user_id ) JOIN user user ON (user.gooru_uid=startLog.user_uid)  SET summary.elapsed_time = ( stopLog.event_time - startLog.event_time ) , summary.end_time = stopLog.event_time WHERE  "
				+ generateOrgAuthSqlQueryWithData("user.");
		if (withInHours != null && withInHours > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, -withInHours);
			sql += " WHERE '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()) + "' < summary.created_time ";
		}
		getJdbcTemplate().execute(sql);
	}

	@Override
	public ActivitySummary findActivitySummaryByEventId(String eventId, String userIp) {
		String hql = "FROM ActivitySummary summary  WHERE summary.eventId='" + eventId + "'AND " + generateOrgAuthQueryWithData("summary.user.");
		if (userIp != null) {
			hql += " AND summary.userIp = '" + userIp + "'";
		}
		List<ActivitySummary> activitySummary = this.find(hql);
		return (activitySummary.size() > 0) ? activitySummary.get(0) : null;
	}

	@Override
	public void createResourceInfoOfResources() {
		String sql = "INSERT INTO resource_info SELECT null , summary.content_id , 0 , 0 , now(), null FROM activity_summary summary INNER JOIN content sum_con ON ( sum_con.content_id = summary.content_id AND sum_con.type_name = 'resource' )  JOIN user user ON(user.gooru_uid=summary.user_uid) WHERE NOT EXISTS ( SELECT resource_id FROM resource_info WHERE resource_info.resource_id = summary.content_id ) ";
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void updateViewCountsOfResourceInfos() {
		String sql = "UPDATE resource_info info SET info.view_count = (SELECT count(1) FROM activity_summary summary WHERE summary.content_id = info.resource_id )";
		getJdbcTemplate().execute(sql);
	}

	@Override
	public void updateSubscriptionCountsOfResourceInfos() {
		String sql = "UPDATE resource_info info SET info.subscribe_count = (select count(1) from content c where c.content_id = info.resource_id and c.content_id in (select r.content_id from annotation a, resource r where a.type_name = 'subscription' and a.resource_id = r.content_id ))";
		getJdbcTemplate().execute(sql);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public void setContentRepository(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

}
