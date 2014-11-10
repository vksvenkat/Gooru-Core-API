/////////////////////////////////////////////////////////////
// ContentIdexDaoImpl.java
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
/**
 * 
 */
package org.ednovo.gooru.infrastructure.persistence.hibernate.index;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.Resource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;

@Repository
@SuppressWarnings("unchecked")
public class ContentIndexDaoImpl extends IndexDaoImpl implements ContentIndexDao {

	private final String GET_COLLECTION_INFO_FOR_RESOURCE = "SELECT c.content_id, c.gooru_oid, l.lesson, l.goals, l.vocabulary, l.narration, l.notes, r.distinguish FROM learnguide l INNER JOIN resource r ON r.content_id = l.content_id  INNER JOIN content c on c.content_id = l.content_id WHERE c.content_id = :contentId";

	private final String GET_SCOLLECTION_INFO_FOR_RESOURCE = "SELECT distinct c.content_id, c.gooru_oid, r.title FROM collection_item  ci INNER JOIN content c ON c.content_id=ci.collection_content_id INNER JOIN resource r ON r.content_id=c.content_id  WHERE  ci.resource_content_id = :contentId and c.sharing='public'";

	private final String GET_COLLECTION_TAXONOMY_CODE_ID = "SELECT cc.code_id FROM content_classification cc WHERE cc.content_id = :contentId";

	private static final String GET_COLLECTION_ITEMS = "SELECT c.gooru_oid,r.type_name,r.title,c.sharing,ci.*,r.description,info.text,r.grade,r.category,r.url,rs.attribution,rs.domain_name FROM content c INNER JOIN resource r ON r.content_id = c.content_id INNER JOIN collection_item ci ON ci.resource_content_id = c.content_id LEFT JOIN resource_info info ON info.resource_info_id=r.resource_info_id LEFT JOIN resource_source rs ON rs.resource_source_id = r.resource_source_id where ci.collection_content_id=:contentId";

	private static final String GET_SEGMENTS_BY_QUIZ_ID = "SELECT name FROM assessment_segment WHERE assessment_id=:contentId";

	private static final String GET_COLLECTION_TITLE = "SELECT l.lesson FROM learnguide l INNER JOIN content c ON c.content_id = l.content_id WHERE  c.gooru_oid =:gooruOid";

	private static final String GET_COLLECTION_SEGMENTS_SQL = "SELECT s.title, s.segment_id from segment s WHERE s.resource_id=:contentId";

	private static final String GET_INSTANCE_BY_SEGMENT_SQL = "SELECT title , resource_id from resource_instance where segment_id IN (:segmentIds)";

	private static final String GET_QUIZ_BY_QUESTION = "SELECT c.gooru_oid FROM assessment_segment a INNER JOIN assessment_segment_question_assoc ass ON ( ass.segment_id = a.segment_id ) INNER JOIN content c ON (c.content_id = a.assessment_id ) INNER JOIN content ct ON ( ass.question_id = ct.content_id ) WHERE ct.gooru_oid = :gooruOid";

	private static final String GET_QUIZ_TITLE = "SELECT a.name FROM assessment a INNER JOIN  content c ON (c.content_id = a.assessment_id)  WHERE c.gooru_oid =:gooruOid";

	private static final String GET_RESOURCE_INFO = "SELECT ri.text, ri.tags, ri.num_of_pages FROM resource_info ri WHERE ri.resource_id= :contentId";

	private static final String GET_RESOURCE_INSTANCES = "SELECT ri.resource_instance_id, ri.title, ri.description, ri.narrative FROM resource_instance ri WHERE ri.resource_id= :contentId";

	private static final String CONTENT_ID = "contentId";

	private static final String GOORU_OID = "gooruOid";

	private static final String SEGMENT_IDS = "segmentIds";

	private static final String GET_COLLECTION_INFO = "SELECT l.lesson AS lesson,l.vocabulary AS vocabulary,l.goals AS goals,l.notes AS notes,l.narration AS narration,l.source AS source,l.collection_gooru_oid AS collectionGooruOid,l.assessment_gooru_oid AS assessmentGooruOid FROM content c INNER JOIN learnguide l ON l.content_id=c.content_id WHERE c.content_id=:contentId";

	private static final String GET_SCOLLECTION_INFO = "SELECT c.collection_type AS 'scollection.collectionType', c.narration_link AS 'scollection.narrationLink', c.notes AS 'scollection.notes', c.key_points AS 'scollection.keyPoints', c.language AS 'scollection.language', c.goals AS 'scollection.goals', c.estimated_time AS 'scollection.estimatedTime', c.network AS 'scollection.network', r.tags, r.text FROM collection c INNER JOIN resource r ON r.content_id =c.content_id WHERE c.content_id =:contentId";

	private static final String GET_QUIZ_INFO = "select a.name,a.vocabulary As 'quiz.vocabulary', a.learning_objectives as 'quiz.learningObjectives', a.source as 'quiz.source', a.collection_gooru_oid as 'quiz.collectionGooruOid', a.quiz_gooru_oid as 'quiz.quizGooruOid', a.import_code as 'quiz.importCode', group_concat(seg.name SEPARATOR ' ~~ ') as segmentTitles, l.lesson as 'quiz.quizCollectionName' FROM assessment a left join assessment_segment seg on seg.assessment_id = a.assessment_id left JOIN content cc ON cc.gooru_oid = a.collection_gooru_oid left JOIN learnguide l on l.content_id = cc.content_id WHERE a.assessment_id=:contentId";

	private static final String GET_QUESTION_AND_HINT = "select group_concat(aa.answer_text SEPARATOR ' ~~ ') AS 'question.answerTexts',group_concat(ah.hint_text SEPARATOR ' ~~ ') as 'question.hintTexts' FROM assessment_answer aa left join assessment_hint ah on ah.question_id = aa.question_id WHERE aa.question_id=:contentId";

	private static final String GET_ASSETS = "select a.name,aqaa.asset_key from assessment_question_asset_assoc aqaa inner join asset a on aqaa.asset_id = a.asset_id where aqaa.question_id=:contentId";

	private static final String GET_RATING_BY_CONTENT_ID = "select sum((case when isnull(`ra`.`score`) then 0 when (`ra`.`score` > 0) then 1 else 0 end)) AS `voteUp`,sum((case when isnull(`ra`.`score`) then 0 when (`ra`.`score` < 0) then 1 else 0 end)) AS `voteDown` from (`annotation` `a` join `rating` `ra` on((`ra`.`content_id` = `a`.`content_id`))) WHERE a.resource_id=:contentId group by `a`.`resource_id`";

	private static final String GET_SUBSCRIPTION_BY_CONTENT_ID = "select count(0) AS `subscriberCount` from (`content` `c` join `annotation` `a` on((`a`.`resource_id` = `c`.`content_id`))) where (`a`.`type_name` = 'subscription') AND a.resource_id=:contentId group by `a`.`resource_id`";

	private final String GET_SCOLLECTION_ITEM_IDS_BY_RESOURCE_ID = "SELECT collection_item_id FROM collection_item WHERE resource_content_id = :contentId";

	private static final String MAX_SUBSCRIBERS_COUNT = "select max(totalCount) as maxSubscribers from (select count(1) as totalCount from content c inner join annotation a on a.resource_id = c.content_id where a.type_name='subscription' group by a.resource_id) a";
	
	private static final String MAX_RESOURCE_USED_IN_COLLECTION_COUNT = "select count(distinct ci.collection_content_id) as maxUsedInCollectionCount from content col inner join collection co on col.content_id = co.content_id inner join collection_item ci on ci.collection_content_id = co.content_id  inner join content res on res.content_id = ci.resource_content_id where co.collection_type = 'collection' and ci.collection_content_id != ci.resource_content_id and res.sharing in ('anyonewithlink', 'public') and col.sharing in ('anyonewithlink', 'public')  group by resource_content_id order by count(distinct ci.collection_content_id) desc limit 1";

    private static final String RESOURCE_MAX_VIEW = "select views_total as max_views from resource where type_name in ('animation/kmz','animation/swf','assessment-question','exam/pdf','handouts','image/png','ppt/pptx','qb/question','qb/response','question','resource/url','textbook/scribd','video/youtube') order by views_total desc limit 1";
    
	private static final String COLLECTION_MAX_VIEW = "select views_total as max_views from resource where type_name = 'scollection' order by views_total desc limit 1";

	private static final String UPPERBOUND_QUESTION_RESOURCE_IN_COLLECTION = "select count(ci.resource_content_id) as upperbound_question_count from resource c inner join collection_item ci on ci.collection_content_id = c.content_id inner join content cr on cr.content_id = ci.resource_content_id inner join resource r on r.content_id = cr.content_id where c.type_name = 'scollection' and r.resource_format_id = 104 group by c.content_id order by count(ci.resource_content_id) desc limit 1";
	
	private static final String UPPERBOUND_OTHER_RESOURCE_IN_COLLECTION = "select count(ci.resource_content_id) as upperbound_resource_count from resource c inner join collection_item ci on ci.collection_content_id = c.content_id inner join content cr on cr.content_id = ci.resource_content_id inner join resource r on r.content_id = cr.content_id where c.type_name = 'scollection' and r.resource_format_id in (100, 101, 102, 103, 105, 106) group by c.content_id order by count(ci.resource_content_id) desc limit 1";
	
	@Override
	public List<Object[]> getCollectionSegments(Long contentId) {
		return createSQLQuery(GET_COLLECTION_SEGMENTS_SQL).setParameter(CONTENT_ID, contentId).list();
	}

	@Override
	public List<Object[]> getResourceInstances(Set<String> segmentIds) {
		return createSQLQuery(GET_INSTANCE_BY_SEGMENT_SQL).setParameterList(SEGMENT_IDS, segmentIds).list();
	}

	@Override
	public List<String> getQuizSegments(Long contentId) {
		return createSQLQuery(GET_SEGMENTS_BY_QUIZ_ID).setParameter(CONTENT_ID, contentId).list();
	}

	@Override
	public String getCollectionTitle(String gooruOid) {
		List<String> list = createSQLQuery(GET_COLLECTION_TITLE).setParameter(GOORU_OID, gooruOid).list();
		return list.size() > 0 ? (String) list.get(0) : null;
	}

	@Override
	public List<String> getQuestionQuiz(String gooruOid) {
		return (List<String>) createSQLQuery(GET_QUIZ_BY_QUESTION).setParameter(GOORU_OID, gooruOid).list();
	}

	@Override
	public String getQuizTitle(String gooruOid) {
		List<String> result = createSQLQuery(GET_QUIZ_TITLE).setParameter(GOORU_OID, gooruOid).list();
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public List<Object[]> getCollectionItems(Long contentId) {
		return createSQLQuery(GET_COLLECTION_ITEMS).setParameter(CONTENT_ID, contentId).list();
	}

	@Override
	public List<Object[]> getResourceCollections(Long contentId) {
		return createSQLQuery(GET_COLLECTION_INFO_FOR_RESOURCE).setLong(CONTENT_ID, contentId).list();
	}

	@Override
	public List<Object[]> getResourceSCollections(Long contentId) {
		return createSQLQuery(GET_SCOLLECTION_INFO_FOR_RESOURCE).setLong(CONTENT_ID, contentId).list();
	}

	@Override
	public List<Integer> getCollectionTaxonomyIds(Long contentId) {
		return createSQLQuery(GET_COLLECTION_TAXONOMY_CODE_ID).setLong(CONTENT_ID, contentId).list();
	}

	private Query createSQLQuery(String query) {
		return getSessionFactory().getCurrentSession().createSQLQuery(query);
	}

	@Override
	public List<Object[]> getResourceInstances(Long contentId) {
		return createSQLQuery(GET_RESOURCE_INSTANCES).setLong(CONTENT_ID, contentId).list();
	}

	@Override
	public Object[] getResourceInfo(Long contentId) {
		List<Object[]> list = createSQLQuery(GET_RESOURCE_INFO).setLong(CONTENT_ID, contentId).list();
		return list.size() > 0 ? (Object[]) list.get(0) : null;
	}

	@Override
	public Object[] getCollectionInfo(long contentId) {
		List<Object[]> list = createSQLQuery(GET_COLLECTION_INFO).setLong(CONTENT_ID, contentId).list();
		return list.size() > 0 ? (Object[]) list.get(0) : null;
	}

	@Override
	public Object[] getSCollectionInfo(long contentId) {
		List<Object[]> list = createSQLQuery(GET_SCOLLECTION_INFO).setLong(CONTENT_ID, contentId).list();
		return list.size() > 0 ? (Object[]) list.get(0) : null;
	}

	@Override
	public Map<String, Object> getQuizInfo(long contentId) {
		Query query = createSQLQuery(GET_QUIZ_INFO).setLong(CONTENT_ID, contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return (results.size() > 0 ? (Map<String, Object>) results.get(0) : null);
	}

	@Override
	public Object[] getStorageArea(String organizationUid, Boolean s3UploadFlag) {
		String sql = "SELECT sa.cdn_path,sa.area_path From organization o INNER JOIN storage_area sa ON ";
		if (!s3UploadFlag) {
			sql += " o.nfs_storage_area_id = sa.storage_area_id";
		} else {
			sql += " o.s3_storage_area_id = sa.storage_area_id";
		}
		sql += " WHERE o.organization_uid='" + organizationUid + "'";
		List<Object[]> list = createSQLQuery(sql).list();
		return list.size() > 0 ? (Object[]) list.get(0) : null;
	}

	@Override
	public Map<String, Object> getAnswerAndHint(long contentId) {
		Query query = createSQLQuery(GET_QUESTION_AND_HINT).setLong(CONTENT_ID, contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return (results.size() > 0 ? (Map<String, Object>) results.get(0) : null);
	}

	@Override
	public List<Object[]> getAssets(long contentId) {
		List<Object[]> list = createSQLQuery(GET_ASSETS).setLong(CONTENT_ID, contentId).list();
		return list;
	}

	@Override
	public Object[] getRatingByContentId(long contentId) {
		List<Object[]> list = createSQLQuery(GET_RATING_BY_CONTENT_ID).setLong(CONTENT_ID, contentId).list();
		return list.size() > 0 ? (Object[]) list.get(0) : null;
	}

	@Override
	public Long getSubscriptionCountByContentId(long contentId) {
		SQLQuery query = getSessionFactory().getCurrentSession().createSQLQuery(GET_SUBSCRIPTION_BY_CONTENT_ID);
		query.addScalar("subscriberCount", StandardBasicTypes.LONG);
		query.setLong(CONTENT_ID, contentId);
		List<Long> list = query.list();
		return (list.size() > 0 ? list.get(0) : 0);
	}

	@Override
	public Resource findResourceByContentGooruId(String gooruOid) {
		List<Resource> resources = getSessionFactory().getCurrentSession().createQuery("SELECT r FROM Resource r  where r.gooruOid ='" + gooruOid + "' and r.resourceType.name not in ('classpage', 'folder', 'gooru/classbook', 'gooru/classplan', 'shelf', 'assignment', 'quiz', 'assessment-quiz', 'gooru/notebook', 'gooru/studyshelf', 'assessment-exam')").list();
		return resources.size() == 0 ? null : resources.get(0);
	}

	@Override
	public List<String> getCollectionItemIdsByResourceId(Long contentId) {
		return createSQLQuery(GET_SCOLLECTION_ITEM_IDS_BY_RESOURCE_ID).setLong(CONTENT_ID, contentId).list();
	}
	
	@Override
	public List<Object[]> getContentProviderAssoc(long contentId) {
		String sql = "SELECT cp.type, cp.name, cp.content_provider_uid from content_provider cp INNER JOIN content_provider_assoc cpa on cp.content_provider_uid=cpa.content_provider_uid WHERE content_id = :contentId";
		List<Object[]> list = createSQLQuery(sql).setLong(CONTENT_ID, contentId).list();
		return list;
	}

	@Override
	public ContentProvider  getContentProviderlist(String contentProviderId) {
		String sql="SELECT cp FROM ContentProvider cp WHERE cp.contentProviderUid='"+contentProviderId+"'";
		    List<ContentProvider> contentProvider =getSessionFactory().getCurrentSession().createQuery(sql).list();
		return contentProvider.size()== 0 ? null:contentProvider.get(0);
	}
	
	@Override
	public Integer getMaximumSubscribersCount() {
        Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(MAX_SUBSCRIBERS_COUNT).addScalar("maxSubscribers", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public Integer getMaximumUsedInCollectionCount() {
        Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(MAX_RESOURCE_USED_IN_COLLECTION_COUNT).addScalar("maxUsedInCollectionCount", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public Integer getResourceMaximumView() {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(RESOURCE_MAX_VIEW).addScalar("max_views", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public Integer getCollectionMaximumView() {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(COLLECTION_MAX_VIEW).addScalar("max_views", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public Integer getUpperboundQuestionCountInCollection() {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(UPPERBOUND_QUESTION_RESOURCE_IN_COLLECTION).addScalar("upperbound_question_count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public Integer getUpperboundResourceCountInCollection() {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(UPPERBOUND_OTHER_RESOURCE_IN_COLLECTION).addScalar("upperbound_resource_count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
}
