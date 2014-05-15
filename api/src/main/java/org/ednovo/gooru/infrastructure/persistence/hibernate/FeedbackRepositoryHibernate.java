/////////////////////////////////////////////////////////////
// FeedbackRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.StorageAccount;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepositoryHibernate extends BaseRepositoryHibernate implements FeedbackRepository {

	@Autowired
	 StorageRepository storageRepository;

	
	

	@Override
	public Feedback getFeedback(String feedbackId) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.gooruOid=:feedbackId";
		Query query = session.createQuery(hql);
		query.setParameter("feedbackId", feedbackId);
		addOrgAuthParameters(query);
		return (Feedback) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}
	
	@Override
	public List<Feedback> getFeedbacks(String feedbackIds,  String gooruUid) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.gooruOid IN (:feedbackIds)";
		Query query = session.createQuery(hql);
		query.setParameterList("feedbackIds", feedbackIds.split(","));
		addOrgAuthParameters(query);
		return query.list();
	}
	
	@Override
	public Feedback getFeedback(String feedbackId, String gooruUid) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.gooruOid=:feedbackId and feedback.creator.partyUid =:gooruUid ";
		Query query = session.createQuery(hql);
		query.setParameter("feedbackId", feedbackId);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		return (Feedback) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}
	
	@Override
	public Feedback getContentFeedback(String type, String assocGooruOid, String gooruUid) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.assocGooruOid=:assocGooruOid and feedback.creator.partyUid =:gooruUid and feedback.type.keyValue=:type";
		Query query = session.createQuery(hql);
		query.setParameter("assocGooruOid", assocGooruOid);
		query.setParameter("type", type);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		return (Feedback) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}
	
	@Override
	public List<Feedback> getContentFeedbacks(String type, String assocGooruOid, String creatorUid, String category, Integer limit, Integer offset,Boolean skipPagination) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.assocGooruOid=:assocGooruOid ";
		if (category != null)  {
			hql += " and feedback.category.keyValue=:category";
		}
		if (type != null) { 
			hql += " and feedback.type.keyValue=:type";
		}
		if (creatorUid != null) { 
			hql += " and feedback.creator.partyUid =:creatorUid";
		}
		Query query = session.createQuery(hql);
		query.setParameter("assocGooruOid", assocGooruOid);
		if (type != null) { 			
			query.setParameter("type", type);
		}
		if (creatorUid != null) { 
			query.setParameter("creatorUid", creatorUid);
		}
		if (category != null) { 
			query.setParameter("category", category);
		}
		addOrgAuthParameters(query);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	@Override
	public Long getContentFeedbacksCount(String type, String assocGooruOid, String creatorUid, String category) {
		String hql = "select count(*)  FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.assocGooruOid=:assocGooruOid ";
		if (category != null)  {
			hql += " and feedback.category.keyValue=:category";
		}
		if (type != null) { 
			hql += " and feedback.type.keyValue=:type";
		}
		if (creatorUid != null) { 
			hql += " and feedback.creator.partyUid =:creatorUid";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("assocGooruOid", assocGooruOid);
		if (type != null) { 			
			query.setParameter("type", type);
		}
		if (creatorUid != null) { 
			query.setParameter("creatorUid", creatorUid);
		}
		if (category != null) { 
			query.setParameter("category", category);
		}
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}
	
	@Override
	public List<Feedback> getUserFeedbacks(String type, String assocUserUid, String creatorUid, String category, Integer limit, Integer offset) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.assocUserUid=:assocUserUid  ";
		if (category != null)  {
			hql += " and feedback.category.keyValue=:category";
		}
		if (type != null) { 
			hql += " and feedback.type.keyValue=:type";
		}
		if (creatorUid != null) { 
			hql += " and feedback.creator.partyUid =:creatorUid";
		}
		Query query = session.createQuery(hql);
		query.setParameter("assocUserUid", assocUserUid);
		if (type != null) { 			
			query.setParameter("type", type);
		}
		if (creatorUid != null) { 
			query.setParameter("creatorUid", creatorUid);
		}
		if (category != null) { 
			query.setParameter("category", category);
		}
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}
	
	@Override
	public Feedback getUserFeedback(String type, String assocUserUid,  String gooruUid) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.assocUserUid=:assocUserUid and feedback.creator.partyUid =:gooruUid and feedback.type.keyValue=:type";
		Query query = session.createQuery(hql);
		query.setParameter("assocUserUid", assocUserUid);
		query.setParameter("type", type);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		return (Feedback) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Feedback> getFeedbacks(String feedbackTargetType, String feedbackType, String feedbackCreatorUid, Integer limit, Integer offset) {
		Session session = getSession();
		String hql = " FROM  Feedback feedback WHERE " +  generateOrgAuthQuery("feedback.") + " and feedback.type.keyValue=:feedbackType and  feedback.target.keyValue=:feedbackTargetType ";
		if (feedbackCreatorUid != null) {
			hql += " and feedback.creator.partyUid =:feedbackCreatorUid ";
		}
		Query query = session.createQuery(hql);
		query.setParameter("feedbackTargetType", feedbackTargetType);
		query.setParameter("feedbackType", feedbackType);
		if (feedbackCreatorUid != null) {
			query.setParameter("feedbackCreatorUid", feedbackCreatorUid);
		}
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	@Override
	public Map<String, Object> getUserFeedbackRating(String assocUserUid, String feedbackRatingType) {
		Session session = getSession();
		String sql = "select score, count(1) as count from feedback f inner join custom_table_value c on f.feedback_type_id = c.custom_table_value_id where f.assoc_user_uid=:assocUserUid and c.key_value=:feedbackRatingType and score is not null  group by score";
		Query query = session.createSQLQuery(sql).addScalar("score", StandardBasicTypes.INTEGER).
		addScalar("count", StandardBasicTypes.INTEGER)
		.setParameter("assocUserUid", assocUserUid).setParameter("feedbackRatingType", feedbackRatingType);		
		return getRating(query.list());
	}


	@Override
	public Map<String, Object> getContentFeedbackRating(String assocGooruOid,  String feedbackRatingType) {
		Session session = getSession();
		String sql = "select score, count(1) as count from feedback f inner join custom_table_value c on f.feedback_type_id = c.custom_table_value_id  where f.assoc_gooru_oid=:assocGooruOid and c.key_value=:feedbackRatingType and score is not null  group by score";
		Query query = session.createSQLQuery(sql).addScalar("score", StandardBasicTypes.INTEGER).
		addScalar("count", StandardBasicTypes.INTEGER)
		.setParameter("assocGooruOid", assocGooruOid).setParameter("feedbackRatingType", feedbackRatingType);		
		return getRating(query.list());
	}
	private Map<String, Object> getRating(List<Object[]> results) {
		Double sum = 0.0;
		Map<String, Object> rating = new HashMap<String, Object>();
		Map<Object, Object> value = new HashMap<Object, Object>();
		for (Object[] object : results) {
			value.put(object[0], object[1]);
			sum += ((Integer) object[0]);
		}
		rating.put("scores", value);
		rating.put("average", results.size() > 0 ? Double.parseDouble(new DecimalFormat("##.#").format(sum/results.size())) : sum);
		rating.put("count",sum);
		return rating; 
	}

	
	@Override
	public Map<String, Object> getContentFlags(Integer limit, Integer offset, Boolean skipPagination, String feedbackCategory, String type, String status, String reportedFlagType, String startDate, String endDate, String searchQuery, String description, String reportQuery) {
		Session session = getSession();
		String sql = "";
		String statusType = "";
		String flagType = "";
		if (status != null) {
			if (status.contains(",")) {
				status = status.replace(",", "','");
			}
			statusType = " and  cs.value in ('" + status + "') ";
		}
		if (reportedFlagType != null) {
			if (reportedFlagType.contains(",")) {
				reportedFlagType = reportedFlagType.replace(",", "','");
			}
			flagType = " and  ft.value in ('" + reportedFlagType + "') ";
		}

		if (type.equalsIgnoreCase("collection")) {
			sql = "select  title,concat(r.folder,r.thumbnail) as thumbnail,r.has_frame_breaker as hasFrameBreaker, cl.goals as description,  gooru_oid as gooruOid, cl.collection_type as category , CONVERT_TZ(c.created_on,@@session.time_zone,'US/Pacific') as createdOn, cs.value as value, group_concat(ft.value) as reportedFlag, c.user_uid  as userUid, fp.value as product,group_concat(f.feedback_uid) as reportId, f.creator_uid as reportCreator,CONVERT_TZ(f.created_date,@@session.time_zone,'US/Pacific') as reportCreatedOn, f.feedback_text as reportDescription, f.notes as notes, url as url, f.context_path as browserUrl, 'temp' as scount,c.sharing as sharing,group_concat(u.username) as reporterName, ru.username as resourceCreatorName from content c  inner join feedback f inner join custom_table_value cs on f.assoc_gooru_oid = c.gooru_oid and cs.custom_table_value_id = c.status_type inner join custom_table_value fp on f.product_id = fp.custom_table_value_id inner join custom_table_value ft on ft.custom_table_value_id = f.feedback_type_id inner join collection cl  on cl.content_id = c.content_id inner join resource r on c.content_id = r.content_id   inner join custom_table_value ss on f.feedback_category_id = ss.custom_table_value_id inner join custom_table ctab on ctab.custom_table_id = ft.custom_table_id inner join user u  on u.gooru_uid = f.creator_uid inner join user ru on ru.gooru_uid = c.user_uid where ctab.name = '"
					+ feedbackCategory + "' and  cs.value is not null  and cl.collection_type in ('collection')" + flagType + statusType + "";
		} else {

			sql = "select title ,concat(r.folder,r.thumbnail) as thumbnail,r.has_frame_breaker as hasFrameBreaker ,r.description,  gooru_oid as gooruOid, r.category as category , CONVERT_TZ(c.created_on,@@session.time_zone,'US/Pacific') as createdOn, cs.value as value, group_concat(ft.value) as reportedFlag, c.user_uid  as userUid, fp.value as product, group_concat(f.feedback_uid) as reportId, f.creator_uid as reportCreator,CONVERT_TZ(f.created_date,@@session.time_zone,'US/Pacific') as reportCreatedOn, f.feedback_text as reportDescription,f.notes as notes, url as url, f.context_path as browserUrl,(select concat(count(distinct(ci.collection_content_id)) , '~' ,group_concat(distinct(rc.title))) from collection_item ci inner join resource ri on ci.resource_content_id = ri.content_id inner join resource as rc on (rc.content_id = ci.collection_content_id)   where ri.content_id = r.content_id) as scount, c.sharing as sharing,group_concat(u.username) as reporterName, ru.username as resourceCreatorName from content c  inner join feedback f inner join custom_table_value cs on f.assoc_gooru_oid = c.gooru_oid and cs.custom_table_value_id = c.status_type inner join custom_table_value fp on f.product_id = fp.custom_table_value_id inner join custom_table_value ft on ft.custom_table_value_id = f.feedback_type_id inner join resource r  on r.content_id = c.content_id inner join custom_table_value ss on f.feedback_category_id = ss.custom_table_value_id  inner join custom_table ctab on ctab.custom_table_id = ft.custom_table_id inner join user u  on u.gooru_uid = f.creator_uid inner join user ru on ru.gooru_uid = c.user_uid where ctab.name = '"
					+ feedbackCategory + "' and  cs.value is not null  and r.type_name in ('resource/url','ppt/pptx', 'video/youtube', 'animation/swf', 'animation/kmz','textbook/scribd', 'assessment-question') " + statusType + "" + flagType + "  ";
		}

		if (startDate != null && endDate != null) {

			sql += "and DATE(f.created_date) BETWEEN '" + startDate + "' and '" + endDate + "'";
		} else if (startDate != null) {
			sql += " and DATE(f.created_date) = '" + startDate + "'";
		} else if (endDate != null) {
			sql += " and DATE(f.created_date) = '" + endDate + "'";
		}

		if (searchQuery != null) {
			sql += " and gooru_oid = '" + searchQuery + "' or title = '" + searchQuery + "' or f.feedback_text = '" + searchQuery + "' or f.notes = '" + searchQuery + "'";
		}

		if (description != null && type.equalsIgnoreCase("collection")) {
			sql += " and cl.goals = '" + description + "'";
		} else if (description != null && type.equalsIgnoreCase("resource")) {
			sql += " and r.description = '" + description + "'";
		}

		if (reportQuery != null) {
			sql += " and f.creator_uid = '" + reportQuery + "' or u.username = '" + reportQuery + "'";
		}

		sql += " group by f.creator_uid, f.assoc_gooru_oid";
		
		Query query = session.createSQLQuery(sql).addScalar("title", StandardBasicTypes.STRING).addScalar("description", StandardBasicTypes.STRING).addScalar("gooruOid", StandardBasicTypes.STRING).addScalar("category", StandardBasicTypes.STRING).addScalar("createdOn", StandardBasicTypes.STRING)
				.addScalar("value", StandardBasicTypes.STRING).addScalar("reportedFlag", StandardBasicTypes.STRING).addScalar("userUid", StandardBasicTypes.STRING).addScalar("product", StandardBasicTypes.STRING).addScalar("reportId", StandardBasicTypes.STRING)
				.addScalar("reportCreator", StandardBasicTypes.STRING).addScalar("reportCreatedOn", StandardBasicTypes.STRING).addScalar("reportDescription", StandardBasicTypes.STRING).addScalar("url", StandardBasicTypes.STRING).addScalar("browserUrl", StandardBasicTypes.STRING)
				.addScalar("scount", StandardBasicTypes.STRING).addScalar("sharing", StandardBasicTypes.STRING).addScalar("notes", StandardBasicTypes.STRING).addScalar("reporterName", StandardBasicTypes.STRING).addScalar("resourceCreatorName", StandardBasicTypes.STRING)
				.addScalar("thumbnail", StandardBasicTypes.STRING).addScalar("hasFrameBreaker", StandardBasicTypes.BOOLEAN);

		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return getFlags(query.list(), sql, type);
	}
	
	private Map<String, Object> getFlags(List<Object[]> results, String sql, String type) {
		List<Map<String, Object>> listFlag = new ArrayList<Map<String, Object>>();
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(StorageAccount.Type.NFS.getType());
		for (Object[] object : results) {
			Map<String, Object> flag = new HashMap<String, Object>();
			flag.put("title", object[0]);
			flag.put("description", object[1]);
			flag.put("gooruOid", object[2]);
			flag.put("category", object[3]);
			flag.put("createdOn", object[4]);
			flag.put("value", object[5]);
			flag.put("reportedFlag", object[6]);
			flag.put("userUid", object[7]);
			flag.put("product", object[8]);
			flag.put("reportId", object[9]);
			flag.put("reportCreator", object[10]);
			flag.put("reportCreatedOn", object[11]);
			flag.put("reportDescription", object[12]);
			flag.put("url", object[13]);
			flag.put("browserUrl", object[14]);
			flag.put("sharing", object[16]);
			flag.put("notes", object[17]);
			flag.put("reporterName", object[18]);
			flag.put("resourceCreatorName", object[19]);
			flag.put("thumbnail", object[20]);
			flag.put("hasFrameBreaker", object[21]);
			if (type == "resource") {
				String temp = (String) object[15];
				if (temp != null) {
					String[] scollection = temp.split("~");
					flag.put("scollectionCount", scollection[0]);
					flag.put("scollectionTitle", scollection[1]);
				} else {
					flag.put("scollectionCount", null);
					flag.put("scollectionTitle", null);
				}
			}
			if (flag.get("thumbnail") != null) {
				flag.put("thumbnail", storageArea.getAreaPath().concat(flag.get("thumbnail").toString()));
			}

			listFlag.add(flag);
		}

		Session session = getSession();
		sql = "select count(1) as totalCount from (" + sql + ") as flag";
		Query query = session.createSQLQuery(sql).addScalar("totalCount", StandardBasicTypes.INTEGER);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("searchResult", listFlag);
		result.put("totalCount", (Integer) query.list().get(0));
		return result;
	}
	
	@Override
	public Map<Object, Object> getContentFeedbackThumbs(String assocGooruOid, String feedbackRatingType) {
		Session session = getSession();
		String sql = "select sum(case when score is null then 0 when score > 0 then 1 else 0 end) as thumb_up, sum(case when score is null then 0 when score < 0 then 1 else 0 end) as thumb_down from feedback f inner join custom_table_value c on (f.feedback_type_id = c.custom_table_value_id) where f.assoc_gooru_oid =:assocGooruOid and c.key_value=:feedbackRatingType";
		Query query = session.createSQLQuery(sql).addScalar("thumb_up", StandardBasicTypes.INTEGER).
		addScalar("thumb_down", StandardBasicTypes.INTEGER)
		.setParameter("assocGooruOid", assocGooruOid).setParameter("feedbackRatingType", feedbackRatingType);	
		return getThumbsVotes((Object[]) query.list().get(0));
	}

	@Override
	public Map<Object, Object> getUserFeedbackThumbs(String assocUserUid, String feedbackRatingType) {
		Session session = getSession();
		String sql = "select sum(case when score is null then 0 when score > 0 then 1 else 0 end) as thumb_up, sum(case when score is null then 0 when score < 0 then 1 else 0 end) as thumb_down from feedback f inner join custom_table_value c on (f.feedback_type_id = c.custom_table_value_id)  where f.assoc_user_uid =:assocUserUid and c.key_value=:feedbackRatingType";
		Query query = session.createSQLQuery(sql).addScalar("thumb_up", StandardBasicTypes.INTEGER).
		addScalar("thumb_down", StandardBasicTypes.INTEGER)
		.setParameter("assocUserUid", assocUserUid).setParameter("feedbackRatingType", feedbackRatingType);
		return getThumbsVotes((Object[]) query.list().get(0));
	} 
	private Map<Object, Object> getThumbsVotes(Object[] object) {
		Map<Object, Object> thumbs = new HashMap<Object, Object>();
		thumbs.put("thumbUp", object[0] == null ? 0 : object[0]);
		thumbs .put("thumbDown", object[1] == null ? 0 : object[1]);
		return thumbs; 
	}

	@Override
	public  Integer getContentFeedbackAggregateByType(String assocGooruOid, String feedbackType) {
		Session session = getSession();
		String sql = "select count(1) as count from feedback f inner join custom_table_value c on (f.feedback_type_id = c.custom_table_value_id) where f.assoc_gooru_oid =:assocGooruOid and c.key_value=:feedbackType";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER)
		.setParameter("assocGooruOid", assocGooruOid).setParameter("feedbackType", feedbackType);
		return (Integer)query.list().get(0);
	}

	@Override
	public Integer getUserFeedbackAggregateByType(String assocUserUid, String  feedbackType) {
		Session session = getSession();
		String sql = "select count(1) as count from feedback f inner join custom_table_value c on f.feedback_type_id = c.custom_table_value_id where f.assoc_user_uid =:assocUserUid and c.key_value=:feedbackType";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER)
		.setParameter("assocUserUid", assocUserUid).setParameter("feedbackType", feedbackType);
		return (Integer)query.list().get(0);
	}

	@Override
	public Map<Object, Object> getUserFeedbackAverage(String assocUserUid, String feedbackCategory) {
		Session session = getSession();
		String sql = "select count(1) as count, t.value as name from feedback f inner join custom_table_value c on  c.custom_table_value_id = f.feedback_category_id inner join custom_table_value t on   t.custom_table_value_id = f.feedback_type_id  where  f.key_value =:feedbackCategory and assoc_user_uid =:assocUserUid  group by f.feedback_type_id";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).
		addScalar("name", StandardBasicTypes.STRING)
		.setParameter("assocUserUid", assocUserUid).setParameter("feedbackCategory", feedbackCategory);
		return getFeedbackAverage(query.list());
	}

	@Override
	public Map<Object, Object> getContentFeedbackAverage(String assocGooruOid, String feedbackCategory) {
		Session session = getSession();
		String sql = "select count(1) as count, t.value as name from feedback f inner join custom_table_value c on  c.custom_table_value_id = f.feedback_category_id inner join custom_table_value t on   t.custom_table_value_id = f.feedback_type_id  where  t.key_value =:feedbackCategory and assoc_gooru_oid =:assocGooruOid  group by f.feedback_type_id";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).
		addScalar("name", StandardBasicTypes.STRING)
		.setParameter("assocGooruOid", assocGooruOid).setParameter("feedbackCategory", feedbackCategory);
		return getFeedbackAverage(query.list());
	}
	
	private Map<Object, Object> getFeedbackAverage(List<Object[]> results) {
		Map<Object, Object> average = new HashMap<Object, Object>();
		for (Object[] object : results) {			
			average.put(object[1],  object[0]);
		}
		return average; 
	}
	
	private List<Map<Object, Object>> getFeedbackAggregate(List<Object[]> results) {
		List<Map<Object, Object>> listAggregate = new ArrayList<Map<Object,Object>>();
		for (Object[] object : results) {			
			Map<Object, Object> average = new HashMap<Object, Object>();
			average.put(object[1],  object[0]);
			average.put("CollectionId", object[2]);
			listAggregate.add(average);
		}
		return listAggregate; 
	}
	
	@Override
	public List<Map<Object, Object>> getContentFeedbackAggregate(String assocGooruOid, String feedbackCategory, Boolean flag) {
		Session session = getSession();
		String sql = "";
		if (flag) {
			sql = "select count(1) as count, t.value as name,cc.gooru_oid as collectionId from collection_item ci  inner join collection cn on (ci.collection_content_id = cn.content_id)  inner join content rc on (rc.content_id = ci.resource_content_id)  inner join feedback f on (rc.gooru_oid = f.assoc_gooru_oid) inner join content cc on cc.content_id = cn.content_id inner join custom_table_value c on  c.custom_table_value_id = f.feedback_category_id inner join  custom_table_value t  on  t.custom_table_value_id = f.feedback_type_id inner join  task_resource_assoc tc on tc.resource_content_id = ci.collection_content_id inner join content ct on ct.content_id = tc.task_content_id  where ct.gooru_oid =:assocGooruOid and c.key_value =:feedbackCategory group by f.feedback_type_id,cc.gooru_oid";
		} else {
			sql = "select count(1) as count, t.value as name,cc.gooru_oid as collectionId from collection_item ci  inner join collection cn on (ci.collection_content_id = cn.content_id)  inner join content rc on (rc.content_id = ci.resource_content_id)  inner join feedback f on (rc.gooru_oid = f.assoc_gooru_oid) inner join content cc on cc.content_id = cn.content_id inner join custom_table_value c on  c.custom_table_value_id = f.feedback_category_id inner join  custom_table_value t  on  t.custom_table_value_id = f.feedback_type_id  where cc.gooru_oid =:assocGooruOid and c.key_value =:feedbackCategory and cn.collection_type = 'collection' group by f.feedback_type_id";
		}
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).
		addScalar("name", StandardBasicTypes.STRING).addScalar("collectionId", StandardBasicTypes.STRING)
		.setParameter("assocGooruOid", assocGooruOid).setParameter("feedbackCategory", feedbackCategory);
		return getFeedbackAggregate(query.list());
	}

	@Override
	public List<CustomTableValue> getCustomValues(String type) {
		Session session = getSession();
		String hql = " FROM  CustomTableValue ct where ct.customTable.name=:type"; 
		Query query = session.createQuery(hql);
		query.setParameter("type", type);
		return query.list();
	}
	public StorageRepository getStorageRepository() {
		return storageRepository;
	}
	

}
