/////////////////////////////////////////////////////////////
// AssessmentRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.assessment;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentAttemptTry;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AssessmentRepositoryHibernate extends BaseRepositoryHibernate implements AssessmentRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public AssessmentRepositoryHibernate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Assessment> listAssessments(Map<String, String> filters) {
		Session session = getSession();

		Integer pageNum = 1;
		if (filters != null && filters.containsKey(AssessmentRepository.PAGE)) {
			pageNum = Integer.parseInt(filters.get(AssessmentRepository.PAGE));
		}
		Integer pageSize = 50;
		if (filters != null && filters.containsKey(AssessmentRepository.PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(AssessmentRepository.PAGE_SIZE));
		}

		Integer featured = null;

		if (filters.containsKey("featured")) {
			featured = Integer.valueOf(filters.get("featured"));
		}

		String importCode = null;
		if (filters.containsKey("importCode") && filters.get("importCode") != null) {
			importCode = filters.get("importCode");
		}

		if (filters.containsKey(AssessmentRepository.ACCESS_TYPE) && filters.get(AssessmentRepository.ACCESS_TYPE).equals("my")) {

			String userId = filters.get("userId");
			String sql = "SELECT distinct c.assessment_id as contentId FROM assessment c INNER JOIN resource cr ON ( c.assessment_id = cr.content_id ";
			if (featured != null) {
				sql += " AND cr.is_featured =  " + featured + " ";
			}
			sql += ") INNER JOIN content cc on c.assessment_id = cc.content_id LEFT JOIN annotation a on c.assessment_id = a.resource_id LEFT JOIN content ac on a.content_id = ac.content_id  where (ac.user_uid = " + userId + " or cc.user_uid = " + userId + " ) ";
			sql += " and (a.type_name ='subscription' or  a.type_name is null ) ";
			if (importCode != null) {
				sql += " and c.import_code = '" + importCode + "' ";
			}

			sql += generateAuthSqlQueryWithData("cc.");
			sql += "order by coalesce ( ac.last_modified , cc.last_modified ) desc limit " + pageSize * (pageNum - 1) + " , " + pageSize + "";
			Query query = session.createSQLQuery(sql).addScalar("contentId", StandardBasicTypes.LONG);
			List<Long> contentIds = query.list();
			StringBuffer contentIdBuffer = new StringBuffer();
			for (Long contentId : contentIds) {
				if (contentIdBuffer.length() > 0) {
					contentIdBuffer.append(",'" + contentId + "'");
				} else {
					contentIdBuffer.append("'" + contentId + "'");
				}
			}

			if (contentIdBuffer.length() > 1) {

				List<Assessment> myQuiz = getSession().createQuery("SELECT assessment FROM Assessment assessment   WHERE assessment.contentId IN (" + contentIdBuffer.toString() + ") AND " + generateAuthQueryWithDataNew("assessment")).list();
				List<Assessment> resultQuiz = new ArrayList<Assessment>();
				for (Long contentId : contentIds) {
					for (Assessment quiz : myQuiz) {
						if (quiz.getContentId().equals(contentId)) {
							resultQuiz.add(quiz);
							break;
						}
					}
				}

				return resultQuiz;
			} else {
				return new ArrayList<Assessment>();
			}
		}
		Criteria criteria = session.createCriteria(Assessment.class);
		Criteria taxCriteria = session.createCriteria(Assessment.class);
		if (filters.containsKey(TAXONOMY_CODE)) {
			String taxonomyCode = filters.get(TAXONOMY_CODE);
			if (taxonomyCode != null) {
				criteria.createAlias("taxonomySet", "taxonomy");
				criteria.add(Restrictions.eq("taxonomy.code", taxonomyCode));
			}
		}
		if (filters.containsKey(SEGMENT_ID)) {
			Integer segmentId = Integer.parseInt(filters.get(SEGMENT_ID));
			if (segmentId != null) {
				criteria.createAlias("segments", "segment");
				criteria.add(Restrictions.eq("segment.segmentId", segmentId));
			}
		}
		if (featured != null) {
			criteria.add(Restrictions.eq("isFeatured", featured));
		}
		if (importCode != null) {
			criteria.add(Restrictions.eq("importCode", importCode));
		}
		if (filters.containsKey(IS_LIVE)) {
			String isLive = filters.get(IS_LIVE);
			if (isLive != null) {
				criteria.add(Restrictions.eq("isLive", isLive));
			}
		}
		if (filters.containsKey(QUESTION_GOORU_ID)) {
			String gooruOid = filters.get(QUESTION_GOORU_ID);
			if (gooruOid != null) {
				taxCriteria.add(Restrictions.eq("gooruOid", gooruOid));
				List<Assessment> taxAssessments = taxCriteria.list();
				List<String> codes = new ArrayList<String>();
				if (taxAssessments != null && taxAssessments.size() > 0) {
					for (Code code : taxAssessments.get(0).getTaxonomySet()) {
						codes.add(code.getCode());
					}
					criteria.createAlias("taxonomySet", "taxonomy");
					criteria.add(Restrictions.in("taxonomy.code", codes));
				}
			}
		}
		criteria.addOrder(Order.desc("questionCount"));
		criteria.setFirstResult(((pageNum - 1) * pageSize));
		criteria.setMaxResults(pageSize);
		addAuthCriterias(criteria);
		return criteria.list();
	}

	@Override
	public AssessmentQuestion getNextUnansweredQuestion(String gooruOAssessmentId, Integer attemptId) {
		String hql = "SELECT aquestion FROM AssessmentQuestion aquestion, Assessment assessment join assessment.segments as segment join segment.segmentQuestions segmentQuestion WHERE assessment.gooruOid = '" + gooruOAssessmentId + "' AND  " + generateAuthQueryWithDataNew("assessment.")
				+ "  AND aquestion = segmentQuestion.question AND aquestion NOT IN " + "(SELECT attemptItem.question FROM AssessmentAttempt attempt inner join attempt.attemptItems as attemptItem WHERE attempt.attemptId = " + attemptId + ") order by segment.sequence, segmentQuestion.sequence";
		return getRecord(hql);
	}

	@Override
	public List<AssessmentQuestion> listQuestions(Map<String, String> filters) {
		Session session = getSession();
		Criteria criteria = session.createCriteria(AssessmentQuestion.class);
		if (filters != null) {
			Integer pageNum = 1;
			if (filters != null && filters.containsKey(AssessmentRepository.PAGE)) {
				pageNum = Integer.parseInt(filters.get(AssessmentRepository.PAGE));
			}
			Integer pageSize = 10;
			if (filters != null && filters.containsKey(AssessmentRepository.PAGE_SIZE)) {
				pageSize = Integer.parseInt(filters.get(AssessmentRepository.PAGE_SIZE));
			}
			if (filters.containsKey("batchId") && filters.get("batchId") != null) {
				criteria.add(Restrictions.eq("batchId", filters.get("batchId")));
			}
			if (filters.containsKey("importCode") && filters.get("importCode") != null) {
				criteria.add(Restrictions.eq("importCode", filters.get("importCode")));
			}
			criteria.setFirstResult(((pageNum - 1) * pageSize));
			criteria.setMaxResults(pageSize);
			addAuthCriterias(criteria);
		}
		return criteria.list();
	}

	@Override
	public void saveAndFlush(Object object) {
		saveOrUpdate(object);
		getSession().flush();
	}

	@Override
	public boolean getAttemptAnswerStatus(Integer answerId) {
		String hql = "SELECT answer FROM AssessmentAnswer answer  WHERE answer.answerId = " + answerId + "AND answer.isCorrect = 1 AND  " + generateAuthQueryWithDataNew("answer.question.");
		List result = find(hql);
		if (result != null && result.size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteSegmentQuestion(AssessmentSegmentQuestionAssoc segmentQuestionAssoc) {
		this.delete(segmentQuestionAssoc);
	}

	/*
	 * @Override public Integer getDistinctAttemptQuestionCount(Map<String,
	 * String> filter) { String sql =
	 * "SELECT COUNT(DISTINCT attempt.student_id) FROM assessment_attempt_item attemptItem "
	 * +
	 * " INNER JOIN assessment_attempt attempt ON attemptItem.attempt_id = attempt.attempt_id  "
	 * +
	 * " INNER JOIN assessment_attempt fromAttempt ON attempt.attempt_id = fromAttempt.attempt_id "
	 * + " INNER JOIN content content ON content.gooru_oid = '" +
	 * filter.get(AssessmentRepository.QUESTION_GOORU_ID) + "'" +
	 * " INNER JOIN assessment_answer answer ON ( attemptItem.answer_id = answer.answer_id "
	 * ; if (filter.containsKey(AssessmentRepository.IS_CORRECT)) { sql +=
	 * " AND answer.is_correct = " +
	 * filter.get(AssessmentRepository.IS_CORRECT); } sql +=
	 * " ) WHERE attempt.mode = 1 AND attemptItem.question_id = content.content_id"
	 * ;
	 * 
	 * Session session = getSession(); List<BigInteger> results =
	 * session.createSQLQuery(sql).list(); return (results != null) ?
	 * (results.get(0)).intValue() : 0; }
	 */
	@Override
	public List<QuestionSet> listQuestionSets(Map<String, String> filters) {
		return getAll(QuestionSet.class);
	}

	@Override
	public void deleteQuestionSetQuestion(QuestionSetQuestionAssoc questionSetQuestionAssoc) {
		this.delete(get(QuestionSetQuestionAssoc.class, questionSetQuestionAssoc));
	}

	@Override
	public Object getModel(Class<?> classModel, Serializable id) {
		return get(classModel, id);
	}

	@Override
	public boolean isQuestionUsedInSegmentQuestion(String gooruQuestionId) {
		String hql = "SELECT segmentQuestion FROM AssessmentSegmentQuestionAssoc segmentQuestion  WHERE segmentQuestion.question.gooruOid = '" + gooruQuestionId + "' AND  " + generateAuthQueryWithDataNew("segmentQuestion.question.");
		List<AssessmentSegmentQuestionAssoc> result = this.find(hql);
		return (result == null || result.size() == 0) ? false : true;
	}

	@Override
	public boolean isQuestionUsedInAttemptItem(String gooruQuestionId) {
		String hql = "SELECT attemptItem FROM AssessmentAttemptItem attemptItem  WHERE attemptItem.question.gooruOid = '" + gooruQuestionId + "' AND  " + generateAuthQueryWithDataNew("attemptItem.question.");
		List<AssessmentSegmentQuestionAssoc> result = this.find(hql);
		return (result == null || result.size() == 0) ? false : true;
	}

	@Override
	public <T extends Serializable> T getByGooruOId(Class<T> modelClass, String gooruOId) {
		String hql = "SELECT distinct model FROM " + modelClass.getSimpleName() + " model  WHERE model.gooruOid = '" + gooruOId + "' AND  " + generateAuthQueryWithDataNew("model.");
		return (T) getRecord(hql);
	}

	@Override
	public List<AssessmentQuestion> getAssessmentQuestions(String gooruOAssessmentId) {
		String hql = "SELECT aquestion FROM AssessmentQuestion aquestion, Assessment assessment  join assessment.segments as assessmentSegment inner join assessmentSegment.segmentQuestions as segmentQuestion WHERE assessment.gooruOid  = '" + gooruOAssessmentId
				+ "' AND aquestion = segmentQuestion.question AND  " + generateAuthQueryWithDataNew("assessment.") + " order by assessmentSegment.sequence , segmentQuestion.sequence";
		return (List<AssessmentQuestion>) find(hql);
	}

	@Override
	public Map<String, Object> getAssessmentAttemptsInfo(Integer attemptId, String gooruOAssessmentId, Integer studentId) {
		Session session = this.getSession();
		String sql = "SELECT COUNT(1) as count, AVG(attempt.score) as avg FROM assessment_attempt attempt INNER JOIN content content ON content.gooru_oid = '" + gooruOAssessmentId
				+ "' INNER JOIN assessment assessment ON ( assessment.assessment_id = content.content_id AND assessment.assessment_id = attempt.assessment_id )  WHERE attempt.mode = 1 AND attempt.attempt_id != '" + attemptId + "' AND attempt.student_id != " + studentId + " AND "
				+ generateAuthSqlQueryWithData("content.");
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).addScalar("avg", StandardBasicTypes.DOUBLE);
		Object[] result = (Object[]) query.uniqueResult();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("otherAttempts", result[0]);
		resultMap.put("othersAvg", result[1]);
		releaseSession(session);
		return resultMap;
	}

	/*
	 * @Override public Integer getAttemptQuestionsInfo(Integer attemptId,
	 * Integer isCorrect) { // FIXME String hql =
	 * "FROM AssessmentAttempt attempt WHERE attempt.mode = 1 AND attempt.attemptId = "
	 * + attemptId + " AND "; if (isCorrect != null) { hql +=
	 * "( attempt.attemptItems.answer != NULL AND attempt.attemptItems.answer.isCorrect = "
	 * + isCorrect + ")"; } else { hql += "attempt.attemptItems.answer IS NULL";
	 * } List result = find(hql); return (result != null) ? result.size() : 0; }
	 */
	@Override
	public Integer getAssessmentNotAttemptedQuestions(Integer attemptId, String gooruOAssessmentId) {
		String hql = "SELECT question FROM AssessmentQuestion question ,Assessment assessment , AssessmentAttempt attempt  WHERE question IN assessment.segments.segmentQuestions.question AND  question NOT IN attempt.attemptItems.question AND assessment.gooruOid = '" + gooruOAssessmentId
				+ "' AND attempt.mode = 1 AND attempt.attemptId = " + attemptId + " AND " + generateAuthQueryWithDataNew("assessment.");
		List result = find(hql);
		return (result != null) ? result.size() : 0;
	}

	/*
	 * @Override public List<Object[]> getAssessmentAttemptByConcepts(Integer
	 * attemptId, String gooruOAssessmentId, Integer studentId) { Session
	 * session = this.getSession(); String sql =
	 * "SELECT COUNT(1) as maxScore, SUM(answer.is_correct) as studentScore,question.concept as concept FROM assessment_attempt attempt"
	 * +
	 * " INNER JOIN assessment_attempt_item item ON item.attempt_id = attempt.attempt_id AND attempt.attempt_id = '"
	 * + attemptId +
	 * "' INNER JOIN assessment_question question ON question.question_id = item.question_id"
	 * +
	 * " LEFT JOIN assessment_answer answer ON item.answer_id = answer.answer_id"
	 * + " INNER JOIN content content ON content.gooru_oid = '" +
	 * gooruOAssessmentId +
	 * "' INNER JOIN assessment assessment ON ( assessment.assessment_id = content.content_id AND assessment.assessment_id = attempt.assessment_id ) "
	 * + " GROUP BY question.concept";
	 * 
	 * Query query = session.createSQLQuery(sql).addScalar("maxScore",
	 * StandardBasicTypes.INTEGER).addScalar("studentScore",
	 * StandardBasicTypes.INTEGER).addScalar("concept",
	 * StandardBasicTypes.STRING); List<Object[]> result = query.list();
	 * releaseSession(session); return result; }
	 */
	@Override
	public void deleteQuestionAssets(int assetId) {
		String sql = "DELETE aqa FROM assessment_question_asset_assoc ";
		jdbcTemplate.execute(sql);
	}

	@Override
	public void deleteQuestionAssoc(String questionGooruOid) {
		String sql = "DELETE aa from assessment_question_asset_assoc aa inner join content c on c.content_id=aa.question_id where c.gooru_oid='" + questionGooruOid + "' and aa.asset_key='asset-question'";
		jdbcTemplate.execute(sql);
	}

	@Override
	public AssessmentQuestionAssetAssoc getQuestionAsset(String assetKey, String gooruOQuestionId) {
		String hql = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.assetKey = '" + assetKey + "' AND questionAsset.question.gooruOid = '" + gooruOQuestionId + "' AND " + generateAuthQueryWithDataNew("questionAsset.question.");
		return getRecord(hql);
	}

	private <T> T getRecord(String hql) {
		List<T> list = find(hql);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public AssessmentSegmentQuestionAssoc findSegmentQuestion(Integer segmentId, String gooruOQuestionId) {
		String hql = "SELECT questionAssoc FROM AssessmentSegmentQuestionAssoc questionAssoc  WHERE questionAssoc.question.gooruOid = '" + gooruOQuestionId + "' AND questionAssoc.segment.segmentId = " + segmentId + " AND " + generateAuthQueryWithDataNew("questionAssoc.question.");
		return getRecord(hql);
	}

	@Override
	public Integer getAssessmentQuestionsCount(Long assessmentId) {
		String sql = "SELECT COUNT(1) FROM assessment_question question INNER JOIN assessment_segment segment " + "INNER JOIN assessment_segment_question_assoc segmentQuestion ON ( segmentQuestion.segment_id = segment.segment_id AND segmentQuestion.question_id = question.question_id ) "
				+ "INNER JOIN assessment assessment ON assessment.assessment_id = segment.assessment_id " + "INNER JOIN content content ON content.content_id=question.question_id " + "WHERE assessment.assessment_id = '" + assessmentId + "' AND " + generateAuthSqlQueryWithData("content.");
		Session session = getSession();
		List<BigInteger> results = session.createSQLQuery(sql).list();
		return (results != null) ? (results.get(0)).intValue() : 0;
	}

	@Override
	public Assessment getAssessmentForSegment(Integer segmentId) {
		String hql = "SELECT assessment FROM Assessment assessment INNER JOIN assessment.segments segment  WHERE segment.segmentId = '" + segmentId + "' AND " + generateAuthQueryWithDataNew("assessment.");
		return getRecord(hql);
	}

	@Override
	public void updateTimeForSegments(Long questionId) {
		Session session = getSession();
		String sql = "UPDATE assessment_segment assessment_segment , content content  SET time_to_complete_in_secs = ( SELECT SUM(sumQuestion.time_to_complete_in_secs) FROM assessment_segment_question_assoc segmentQuestion INNER JOIN assessment_segment_question_assoc segmentQuestionAssoc ON segmentQuestion.segment_id = segmentQuestionAssoc.segment_id INNER JOIN assessment_question question ON ( segmentQuestionAssoc.question_id = question.question_id  AND segmentQuestionAssoc.question_id = "
				+ questionId
				+ " ) INNER JOIN assessment_question sumQuestion ON sumQuestion.question_id = segmentQuestion.question_id WHERE assessment_segment.segment_id = segmentQuestionAssoc.segment_id ) WHERE content.content_id=assessment_segment.assessment_id   AND "
				+ generateAuthSqlQueryWithData("content.");
		jdbcTemplate.execute(sql);
		releaseSession(session);
	}

	@Override
	public void updateTimeForAssessments(Long questionId) {
		Session session = getSession();
		String sql = "UPDATE assessment_segment  assessment_segment , content content SET time_to_complete_in_secs = ( SELECT SUM(sumQuestion.time_to_complete_in_secs) FROM assessment_segment_question_assoc segmentQuestion INNER JOIN assessment_segment_question_assoc segmentQuestionAssoc ON segmentQuestion.segment_id = segmentQuestionAssoc.segment_id INNER JOIN assessment_question question ON ( segmentQuestionAssoc.question_id = question.question_id  AND segmentQuestionAssoc.question_id = "
				+ questionId
				+ " ) INNER JOIN assessment_question sumQuestion ON sumQuestion.question_id = segmentQuestion.question_id WHERE assessment_segment.segment_id = segmentQuestionAssoc.segment_id ) WHERE content.content_id=assessment_segment.assessment_id  AND  "
				+ generateAuthSqlQueryWithData("content.");
		jdbcTemplate.execute(sql);
		releaseSession(session);
	}

	@Override
	public List<Object[]> getAssessmentAttemptQuestionSummary(Integer attemptId) {
		// FIXME
		Session session = this.getSession();
		String sql = "SELECT question.question_text as questionText , question.type as questionType, question.concept as concept, attemptItem.attempt_status as status, question.question_id as questionId, question.explanation as explanation, resource.folder as folder, storageArea.area_path as assetURI "
				+ " , content.gooru_oid as gooruOid ,  attemptItem.attempt_item_id as attemptItemId, attemptItem.correct_try_id as correctTrySequence  FROM assessment_attempt_item attemptItem "
				+ " INNER JOIN assessment_question question ON question.question_id = attemptItem.question_id "
				+ " INNER JOIN resource resource ON resource.content_id = question.question_id INNER JOIN content content ON resource.content_id = content.content_id  LEFT JOIN storage_area storageArea ON storageArea.storage_area_id = resource.storage_area_id  WHERE attemptItem.attempt_id = "
				+ attemptId + " AND " + generateAuthSqlQueryWithData("content.");

		Query query = session.createSQLQuery(sql).addScalar("questionText", StandardBasicTypes.STRING).addScalar("concept", StandardBasicTypes.STRING).addScalar("status", StandardBasicTypes.STRING).addScalar("questionId", StandardBasicTypes.INTEGER)
				.addScalar("explanation", StandardBasicTypes.STRING).addScalar("questionType", StandardBasicTypes.INTEGER).addScalar("folder", StandardBasicTypes.STRING).addScalar("assetURI", StandardBasicTypes.STRING).addScalar("gooruOid", StandardBasicTypes.STRING)
				.addScalar("attemptItemId", StandardBasicTypes.INTEGER).addScalar("correctTrySequence", StandardBasicTypes.INTEGER);
		List<Object[]> result = query.list();
		releaseSession(session);
		return result;
	}

	@Override
	public AssessmentQuestion findQuestionByImportCode(String code) {
		String hql = "SELECT question FROM AssessmentQuestion question WHERE question.importCode = '" + code + "' AND " + generateAuthQueryWithDataNew("question.");
		return getRecord(hql);
	}

	@Override
	public Assessment findAssessmentByImportCode(String code) {
		String hql = "SELECT assessment FROM Assessment assessment  WHERE assessment.importCode = '" + code + "' AND " + generateAuthQueryWithDataNew("assessment.");
		return getRecord(hql);
	}

	/*
	 * @Override public boolean isAnswerUsed(Integer answerId) { // FIXME String
	 * hql =
	 * "SELECT attempt FROM AssessmentAttempt attempt INNER JOIN attempt.attemptItems attemptItem WHERE attemptItem.answer.answerId = "
	 * + answerId; return find(hql).size() > 0; }
	 */
	@Override
	public AssessmentQuestionAssetAssoc findQuestionAsset(String questionGooruOid, Integer assetId) {
		String hql = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.question.gooruOid = '" + questionGooruOid + "' AND questionAsset.asset.assetId = " + assetId + " AND " + generateAuthQueryWithDataNew("questionAsset.question.");
		List<AssessmentQuestionAssetAssoc> questionAssets = find(hql);
		return questionAssets.size() > 0 ? questionAssets.get(0) : null;
	}

	@Override
	public List<AssessmentQuestion> getAssessmentQuestionsByAssessmentGooruOids(String gooruOAssessmentIds) {
		String hql = "SELECT question FROM AssessmentQuestion question  WHERE question.gooruOid IN(" + gooruOAssessmentIds + ")  AND " + generateAuthQueryWithDataNew("question.");
		return (List<AssessmentQuestion>) find(hql);
	}

	@Override
	public List<Assessment> getAssessmentsListByAssessmentGooruOids(List<String> assessmentIds) {
		Criteria criteria = getSession().createCriteria(Assessment.class).add(Restrictions.in("gooruOid", assessmentIds));
		List<Assessment> assessmentList = addAuthCriterias(criteria).list();
		return assessmentList.size() == 0 ? null : assessmentList;

	}

	@Override
	public List<AssessmentAnswer> findAnswerByAssessmentQuestionId(Integer questionId) {
		String hql = "SELECT question.answers FROM AssessmentQuestion question WHERE question.contentId =" + questionId + " AND " + generateAuthQueryWithDataNew("question.");
		return (List<AssessmentAnswer>) find(hql);
	}

	@Override
	public List<AssessmentQuestionAssetAssoc> getQuestionAssetByQuestionId(Integer questionId) {
		String hql = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.question.contentId = '" + questionId + "'  AND " + generateAuthQueryWithDataNew("questionAsset.question.");
		return (List<AssessmentQuestionAssetAssoc>) find(hql);
	}

	@Override
	public List<Assessment> getAssessmentOfQuestion(String questionGooruOid) {
		String hql = "SELECT assessment FROM AssessmentQuestion aquestion, Assessment assessment  join assessment.segments as assessmentSegment inner join assessmentSegment.segmentQuestions as segmentQuestion WHERE aquestion.gooruOid  = '" + questionGooruOid
				+ "' AND aquestion = segmentQuestion.question  AND  " + generateAuthQueryWithDataNew("assessment.");
		return (List<Assessment>) find(hql);
	}

	@Override
	public String findAssessmentNameByGooruOid(String gooruOid) {

		String sql = "SELECT a.name FROM assessment a INNER JOIN  content c ON (c.content_id = a.assessment_id)  WHERE c.gooru_oid = '" + gooruOid + "' AND " + generateAuthSqlQueryWithData("c.");
		Session session = getSession();
		List<String> result = session.createSQLQuery(sql).list();

		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public Assessment findQuizContent(String quizGooruOid) {
		String hql = "SELECT assessment FROM Assessment assessment  WHERE assessment.gooruOid = '" + quizGooruOid + "' AND " + generateAuthQueryWithDataNew("assessment.");
		List<Assessment> result = (List<Assessment>) find(hql);
		return ((result.size() > 0) ? result.get(0) : null);
	}

	@Override
	public Integer getCurrentTrySequence(Integer attemptItemId) {
		String sql = "SELECT MAX(attemptTry.try_sequence) as trySequence FROM assessment_attempt_try  attemptTry  INNER JOIN  assessment_answer answer ON (answer.answer_id = attemptTry.answer_id) INNER JOIN content content ON (answer.question_id=content.content_id)  WHERE attemptTry.attempt_item_id="
				+ attemptItemId + " AND " + generateAuthSqlQueryWithData("content.");
		Session session = getSession();
		Integer result = (Integer) session.createSQLQuery(sql).addScalar("trySequence", StandardBasicTypes.INTEGER).uniqueResult();
		return result != null ? result + 1 : 1;
	}

	@Override
	public List<AssessmentAttemptTry> findAssessmentAttemptsTryByAttemptItemId(Integer assessmentAttemptItemId) {
		String hql = "SELECT attemptTry FROM AssessmentAttemptTry attemptTry  WHERE attemptTry.assessmentAttemptItem.attemptItemId = " + assessmentAttemptItemId + " AND " + generateAuthQueryWithDataNew("attemptTry.answer.question.");
		return (List<AssessmentAttemptTry>) find(hql);
	}

	@Override
	public Assessment getAssessmentQuestion(String questionGooruOid) {
		String hql = "SELECT assessment FROM AssessmentQuestion aquestion, Assessment assessment  join assessment.segments as assessmentSegment inner join assessmentSegment.segmentQuestions as segmentQuestion WHERE aquestion.gooruOid  = '" + questionGooruOid
				+ "' AND aquestion = segmentQuestion.question  AND  " + generateAuthQueryWithDataNew("assessment.");
		List<Assessment> assessment = (List<Assessment>) find(hql);
		return (assessment.size() > 0) ? assessment.get(0) : null;
	}

	@Override
	public List<Assessment> listAllQuizsWithoutGroups(Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get("pageNum"));
		Integer pageSize = Integer.parseInt(filters.get("pageSize"));
		String hql = "SELECT assessment FROM Assessment assessment  WHERE  " + generateAuthQueryWithDataNew("assessment.");
		Session session = getSession();
		List<Assessment> assessmentList = session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
		return (assessmentList.size() > 0) ? assessmentList : null;
	}

	@Override
	public String getQuizUserScore(String gooruOAssessmentId, String studentId) {
		Session session = this.getSession();
		String sql = "SELECT Max(attempt.score) as maxScore FROM assessment_attempt attempt INNER JOIN content content ON content.gooru_oid = '" + gooruOAssessmentId
				+ "' INNER JOIN assessment assessment ON ( assessment.assessment_id = content.content_id AND assessment.assessment_id = attempt.assessment_id ) WHERE  attempt.student_uid = '" + studentId + "'  AND  " + generateOrgAuthSqlQueryWithData("content.");

		Query query = session.createSQLQuery(sql).addScalar("maxScore", StandardBasicTypes.INTEGER);
		Integer result = (Integer) query.uniqueResult();
		releaseSession(session);
		return result != null ? result.toString() : "0";
	}

	@Override
	public List<String> getAssessmentByQuestion(String questionGooruOid) {
		Session session = this.getSession();
		String sql = "SELECT c.gooru_oid FROM assessment_segment a INNER JOIN assessment_segment_question_assoc ass ON ( ass.segment_id = a.segment_id ) INNER JOIN content c ON (c.content_id = a.assessment_id ) INNER JOIN content ct ON ( ass.question_id = ct.content_id ) WHERE ct.gooru_oid = '"
				+ questionGooruOid + "'";
		Query query = session.createSQLQuery(sql);
		List<String> quizGooruOIds = query.list();
		return (quizGooruOIds.size() > 0) ? quizGooruOIds : null;
	}

	@Override
	public AssessmentAnswer getAssessmentAnswerById(Integer answerId) {
		Session session = getSession();
		Query query = session.createQuery("From AssessmentAnswer aa   where aa.answerId=:answerId ");
		query.setParameter("answerId", answerId);
		List<AssessmentAnswer> AssessmentAnswers = query.list();
		return (AssessmentAnswers.size() > 0) ? AssessmentAnswers.get(0) : null;
	}

}
