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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
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
	public <T extends Serializable> T getByGooruOId(Class<T> modelClass, String gooruOId) {
		String hql = "SELECT distinct model FROM " + modelClass.getSimpleName() + " model  WHERE model.gooruOid = '" + gooruOId + "' AND  " + generateAuthQueryWithDataNew("model.");
		return  getRecord(hql);
	}

	@Override
	public List<AssessmentQuestion> getAssessmentQuestions(String gooruOAssessmentId) {
		String hql = "SELECT aquestion FROM AssessmentQuestion aquestion, Assessment assessment  join assessment.segments as assessmentSegment inner join assessmentSegment.segmentQuestions as segmentQuestion WHERE assessment.gooruOid  = '" + gooruOAssessmentId
				+ "' AND aquestion = segmentQuestion.question AND  " + generateAuthQueryWithDataNew("assessment.") + " order by assessmentSegment.sequence , segmentQuestion.sequence";
		Query query = getSession().createQuery(hql);
		return list(query);
	}

	@Override
	public Map<String, Object> getAssessmentAttemptsInfo(Integer attemptId, String gooruOAssessmentId, Integer studentId) {
		String sql = "SELECT COUNT(1) as count, AVG(attempt.score) as avg FROM assessment_attempt attempt INNER JOIN content content ON content.gooru_oid = '" + gooruOAssessmentId
				+ "' INNER JOIN assessment assessment ON ( assessment.assessment_id = content.content_id AND assessment.assessment_id = attempt.assessment_id )  WHERE attempt.mode = 1 AND attempt.attempt_id != '" + attemptId + "' AND attempt.student_id != " + studentId + " AND "
				+ generateAuthSqlQueryWithData("content.");
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER).addScalar("avg", StandardBasicTypes.DOUBLE);
		Object[] result = (Object[]) query.uniqueResult();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("otherAttempts", result[0]);
		resultMap.put("othersAvg", result[1]);
		return resultMap;
	}

	
	@Override
	public Integer getAssessmentNotAttemptedQuestions(Integer attemptId, String gooruOAssessmentId) {
		String hql = "SELECT question FROM AssessmentQuestion question ,Assessment assessment , AssessmentAttempt attempt  WHERE question IN assessment.segments.segmentQuestions.question AND  question NOT IN attempt.attemptItems.question AND assessment.gooruOid = '" + gooruOAssessmentId
				+ "' AND attempt.mode = 1 AND attempt.attemptId = " + attemptId + " AND " + generateAuthQueryWithDataNew("assessment.");
		List result = find(hql);
		return (result != null) ? result.size() : 0;
	}

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
		Query query = getSession().createQuery(hql);
		List<T> list = list(query);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public Integer getAssessmentQuestionsCount(Long assessmentId) {
		String sql = "SELECT COUNT(1) FROM assessment_question question INNER JOIN assessment_segment segment " + "INNER JOIN assessment_segment_question_assoc segmentQuestion ON ( segmentQuestion.segment_id = segment.segment_id AND segmentQuestion.question_id = question.question_id ) "
				+ "INNER JOIN assessment assessment ON assessment.assessment_id = segment.assessment_id " + "INNER JOIN content content ON content.content_id=question.question_id " + "WHERE assessment.assessment_id = '" + assessmentId + "' AND " + generateAuthSqlQueryWithData("content.");
		Session session = getSession();
		List<BigInteger> results = list(session.createSQLQuery(sql));
		return (results != null) ? (results.get(0)).intValue() : 0;
	}

	@Override
	public void updateTimeForSegments(Long questionId) {
		String sql = "UPDATE assessment_segment assessment_segment , content content  SET time_to_complete_in_secs = ( SELECT SUM(sumQuestion.time_to_complete_in_secs) FROM assessment_segment_question_assoc segmentQuestion INNER JOIN assessment_segment_question_assoc segmentQuestionAssoc ON segmentQuestion.segment_id = segmentQuestionAssoc.segment_id INNER JOIN assessment_question question ON ( segmentQuestionAssoc.question_id = question.question_id  AND segmentQuestionAssoc.question_id = "
				+ questionId
				+ " ) INNER JOIN assessment_question sumQuestion ON sumQuestion.question_id = segmentQuestion.question_id WHERE assessment_segment.segment_id = segmentQuestionAssoc.segment_id ) WHERE content.content_id=assessment_segment.assessment_id   AND "
				+ generateAuthSqlQueryWithData("content.");
		jdbcTemplate.execute(sql);
	}

	@Override
	public void updateTimeForAssessments(Long questionId) {
		String sql = "UPDATE assessment_segment  assessment_segment , content content SET time_to_complete_in_secs = ( SELECT SUM(sumQuestion.time_to_complete_in_secs) FROM assessment_segment_question_assoc segmentQuestion INNER JOIN assessment_segment_question_assoc segmentQuestionAssoc ON segmentQuestion.segment_id = segmentQuestionAssoc.segment_id INNER JOIN assessment_question question ON ( segmentQuestionAssoc.question_id = question.question_id  AND segmentQuestionAssoc.question_id = "
				+ questionId
				+ " ) INNER JOIN assessment_question sumQuestion ON sumQuestion.question_id = segmentQuestion.question_id WHERE assessment_segment.segment_id = segmentQuestionAssoc.segment_id ) WHERE content.content_id=assessment_segment.assessment_id  AND  "
				+ generateAuthSqlQueryWithData("content.");
		jdbcTemplate.execute(sql);
	}

	@Override
	public List<Object[]> getAssessmentAttemptQuestionSummary(Integer attemptId) {	
		String sql = "SELECT question.question_text as questionText , question.type as questionType, question.concept as concept, attemptItem.attempt_status as status, question.question_id as questionId, question.explanation as explanation, resource.folder as folder, storageArea.area_path as assetURI "
				+ " , content.gooru_oid as gooruOid ,  attemptItem.attempt_item_id as attemptItemId, attemptItem.correct_try_id as correctTrySequence  FROM assessment_attempt_item attemptItem "
				+ " INNER JOIN assessment_question question ON question.question_id = attemptItem.question_id "
				+ " INNER JOIN resource resource ON resource.content_id = question.question_id INNER JOIN content content ON resource.content_id = content.content_id  LEFT JOIN storage_area storageArea ON storageArea.storage_area_id = resource.storage_area_id  WHERE attemptItem.attempt_id = "
				+ attemptId + " AND " + generateAuthSqlQueryWithData("content.");

		Query query = getSession().createSQLQuery(sql).addScalar("questionText", StandardBasicTypes.STRING).addScalar("concept", StandardBasicTypes.STRING).addScalar("status", StandardBasicTypes.STRING).addScalar("questionId", StandardBasicTypes.INTEGER)
				.addScalar("explanation", StandardBasicTypes.STRING).addScalar("questionType", StandardBasicTypes.INTEGER).addScalar("folder", StandardBasicTypes.STRING).addScalar("assetURI", StandardBasicTypes.STRING).addScalar("gooruOid", StandardBasicTypes.STRING)
				.addScalar("attemptItemId", StandardBasicTypes.INTEGER).addScalar("correctTrySequence", StandardBasicTypes.INTEGER);
		List<Object[]> result = arrayList(query);
		return result;
	}

	@Override
	public AssessmentQuestion findQuestionByImportCode(String code) {
		String hql = "SELECT question FROM AssessmentQuestion question WHERE question.importCode = '" + code + "' AND " + generateAuthQueryWithDataNew("question.");
		return getRecord(hql);
	}

	@Override
	public AssessmentQuestionAssetAssoc findQuestionAsset(String questionGooruOid, Integer assetId) {
		String hql = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.question.gooruOid = '" + questionGooruOid + "' AND questionAsset.asset.assetId = " + assetId + " AND " + generateAuthQueryWithDataNew("questionAsset.question.");
		Query query = getSession().createQuery(hql);
		List<AssessmentQuestionAssetAssoc> questionAssets = list(query);
		return questionAssets.size() > 0 ? questionAssets.get(0) : null;
	}

	@Override
	public List<AssessmentQuestion> getAssessmentQuestionsByAssessmentGooruOids(String gooruOAssessmentIds) {
		String hql = "SELECT question FROM AssessmentQuestion question  WHERE question.gooruOid IN(" + gooruOAssessmentIds + ")  AND " + generateAuthQueryWithDataNew("question.");
		Query query = getSession().createQuery(hql);
		return list(query);
	}

	@Override
	public List<AssessmentAnswer> findAnswerByAssessmentQuestionId(Integer questionId) {
		String hql = "SELECT question.answers FROM AssessmentQuestion question WHERE question.contentId =" + questionId + " AND " + generateAuthQueryWithDataNew("question.");
		Query query = getSession().createQuery(hql);
		return list(query);
	}

	@Override
	public List<AssessmentQuestionAssetAssoc> getQuestionAssetByQuestionId(Integer questionId) {
		String hql = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.question.contentId = '" + questionId + "'  AND " + generateAuthQueryWithDataNew("questionAsset.question.");
		Query query = getSession().createQuery(hql);
		return list(query);
	}


	@Override
	public String findAssessmentNameByGooruOid(String gooruOid) {

		String sql = "SELECT a.name FROM assessment a INNER JOIN  content c ON (c.content_id = a.assessment_id)  WHERE c.gooru_oid = '" + gooruOid + "' AND " + generateAuthSqlQueryWithData("c.");
		Session session = getSession();
		List<String> result = list(session.createSQLQuery(sql));

		return (result.size() > 0) ? result.get(0) : null;
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
	public String getQuizUserScore(String gooruOAssessmentId, String studentId) {
		String sql = "SELECT Max(attempt.score) as maxScore FROM assessment_attempt attempt INNER JOIN content content ON content.gooru_oid = '" + gooruOAssessmentId
				+ "' INNER JOIN assessment assessment ON ( assessment.assessment_id = content.content_id AND assessment.assessment_id = attempt.assessment_id ) WHERE  attempt.student_uid = '" + studentId + "'  AND  " + generateOrgAuthSqlQueryWithData("content.");

		Query query = getSession().createSQLQuery(sql).addScalar("maxScore", StandardBasicTypes.INTEGER);
		Integer result = (Integer) query.uniqueResult();
		return result != null ? result.toString() : "0";
	}

	@Override
	public List<String> getAssessmentByQuestion(String questionGooruOid) {
		Session session = this.getSession();
		String sql = "SELECT c.gooru_oid FROM assessment_segment a INNER JOIN assessment_segment_question_assoc ass ON ( ass.segment_id = a.segment_id ) INNER JOIN content c ON (c.content_id = a.assessment_id ) INNER JOIN content ct ON ( ass.question_id = ct.content_id ) WHERE ct.gooru_oid = '"
				+ questionGooruOid + "'";
		Query query = session.createSQLQuery(sql);
		return list(query);
	}

	@Override
	public AssessmentAnswer getAssessmentAnswerById(Integer answerId) {
		Session session = getSession();
		Query query = session.createQuery("From AssessmentAnswer aa   where aa.answerId=:answerId ");
		query.setParameter("answerId", answerId);
		return (AssessmentAnswer) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

}
