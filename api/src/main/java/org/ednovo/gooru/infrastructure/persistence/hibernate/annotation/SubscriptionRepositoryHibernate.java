/////////////////////////////////////////////////////////////
// SubscriptionRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.annotation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

//import org.ednovo.gooru.domain.model.user.User;

/**
 * @author Deepankar
 * 
 */

@Repository
public class SubscriptionRepositoryHibernate extends BaseRepositoryHibernate implements SubscriptionRepository {

	@Autowired
	private BaseRepository baseRepository;

	private static final String RETRIEVE_USERS = "SELECT c.created_on AS createdOn, u.gooru_uid AS cont_userId, au.firstname AS cont_firstname, au.lastname AS cont_lastname, au.gooru_uid AS scb_userId FROM annotation a INNER JOIN resource re ON a.resource_id = re.content_id INNER JOIN content c INNER JOIN content ac ON ac.content_id = a.content_id INNER JOIN user u ON re.content_id = c.content_id AND c.user_uid = u.gooru_uid INNER JOIN user au ON ac.user_uid = au.gooru_uid WHERE c.gooru_oid =  :gooruOid and a.type_name = 'subscription' and "
			+ generateOrgAuthSqlQuery("c.");
	private static final String RETRIEVE_SUBSCRIPTION_FOR_USER = "select table1.subCreated as createdOn, c.gooru_oid as lessonId, table1.res_id from content c inner join (select a.resource_id as res_id, c.created_on as subCreated from annotation a,  content c , user u where u.gooru_uid = :gooruUid and c.user_uid = u.gooru_uid and a.content_id =c.content_id and a.type_name = 'subscription' order by c.created_on desc) table1 on table1.res_id = c.content_id where "
			+ generateOrgAuthSqlQuery("c.") + "  limit 5";
	private static final String COUNT_SUBSCRIPTIONS = "select count(1) as total from content c where c.user_uid = :userId and c.content_id in (select r.content_id from annotation a, resource r where a.type_name = 'subscription' and a.resource_id = r.content_id ) and "
			+ generateOrgAuthSqlQuery("c.") + " group by c.user_uid";

	private static final String GET_SUBSCRIPTION_FOR_A_USER = "select a.content_id as contentid from content c , annotation a where c.user_uid = :userId and c.content_id = a.content_id and a.type_name = 'subscription' and a.resource_id in (select content_id from content where gooru_oid = :gooruOid) and "
			+ generateOrgAuthSqlQuery("c.");

	private static final String GET_SUBSCRIPTION_FOR_A_USER_SQL = "select a.content_id as contentid from content c , annotation a where c.user_uid = '%s' and c.content_id = a.content_id and a.type_name = 'subscription' and a.resource_id in (select content_id from content where gooru_oid = '%s')  and c.organization_uid in (%s) ";

	private static final String COUNT_SUBSCRIPTION_FOR_GOORUOID = "select count(1) as totalCount from content c inner join annotation a on a.resource_id = c.content_id where a.type_name='subscription' and c.gooru_oid= :gooruOid and " + generateOrgAuthSqlQuery("c.");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<HashMap<String, String>> findSubscribedUsers(String gooruOid) {
		Query query = getSession().createSQLQuery(RETRIEVE_USERS).addScalar("createdOn", StandardBasicTypes.STRING).addScalar("cont_userId", StandardBasicTypes.STRING).addScalar("cont_firstname", StandardBasicTypes.STRING).addScalar("cont_lastname", StandardBasicTypes.STRING)
				.addScalar("scb_userId", StandardBasicTypes.STRING).setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		List<Object[]> results = query.list();
		List<HashMap<String, String>> subscriptions = new ArrayList<HashMap<String, String>>();
		for (Object[] object : results) {
			HashMap<String, String> hMap = new HashMap<String, String>();
			hMap.put("subscribedOn", (String) object[0]);
			hMap.put("cont_userId", (String) object[1]);
			hMap.put("cont_firstname", (String) object[2]);
			hMap.put("cont_lastname", (String) object[3]);
			hMap.put("scb_userId", (String) object[4]);
			subscriptions.add(hMap);
		}
		return subscriptions;
	}

	public List<HashMap<String, String>> findSubscriptionsForUser(String gooruUid) {
		Query query = getSession().createSQLQuery(RETRIEVE_SUBSCRIPTION_FOR_USER).addScalar("createdOn", StandardBasicTypes.STRING).addScalar("lessonId", StandardBasicTypes.STRING).setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		List<Object[]> results = query.list();

		List<HashMap<String, String>> subscriptions = new ArrayList<HashMap<String, String>>();
		for (Object[] object : results) {
			HashMap<String, String> hMap = new HashMap<String, String>();
			hMap.put("subscribedOn", (String) object[0]);
			hMap.put("lessonId", (String) object[1]);
			subscriptions.add(hMap);
		}
		return subscriptions;
	}

	public Integer countSubscriptionsForUserContent(User user) {

		Query query = getSession().createSQLQuery(COUNT_SUBSCRIPTIONS).addScalar("total", StandardBasicTypes.INTEGER).setParameter("userId", user.getPartyUid());
		addOrgAuthParameters(query);
		List<Integer> subscriptionCounts = query.list();
		return (subscriptionCounts.size() > 0) ? subscriptionCounts.get(0) : 0;
	}

	public boolean hasUserSubscribedToUserContent(String userId, String gooruContentId) {

		Query query = getSession().createSQLQuery(GET_SUBSCRIPTION_FOR_A_USER).addScalar("contentid", StandardBasicTypes.INTEGER).setParameter("userId", userId).setParameter("gooruOid", gooruContentId);
		addOrgAuthParameters(query);
		List<Integer> contentids = query.list();
		if ((contentids != null) && (contentids.size() > 0)) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteSubscription(String userId, String gooruContentId) {

		String retrieveContentIds = DatabaseUtil.format(GET_SUBSCRIPTION_FOR_A_USER_SQL, userId, gooruContentId, getUserOrganizationUidsAsString());
		List<Long> contentids = this.getJdbcTemplate().query(retrieveContentIds, new RowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Long id = Long.parseLong(rs.getString("contentid"));
				return id;
			}
		});

		if (contentids != null) {
			Iterator<Long> iter = contentids.iterator();
			while (iter.hasNext()) {
				baseRepository.remove(Annotation.class, iter.next());
			}
		}
	}

	public Integer getSubscriptionCountForGooruOid(String contentGooruOid) {

		Query query = getSession().createSQLQuery(COUNT_SUBSCRIPTION_FOR_GOORUOID).addScalar("totalCount", StandardBasicTypes.INTEGER).setParameter("gooruOid", contentGooruOid);
		addOrgAuthParameters(query);
		List<Integer> subscriptionCounts = query.list();

		if ((subscriptionCounts != null) && (subscriptionCounts.size() > 0)) {
			return subscriptionCounts.get(0);
		} else {
			return new Integer(0);
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
