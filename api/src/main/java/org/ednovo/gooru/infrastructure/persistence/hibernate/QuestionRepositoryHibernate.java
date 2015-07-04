package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepositoryHibernate extends BaseRepositoryHibernate implements QuestionRepository, ConstantProperties, ParameterProperties {

	private static final String GET_QUESTION = "FROM AssessmentQuestion aq where aq.gooruOid=:qid";

	private static final String GET_QUESTION_ASSET = "SELECT questionAsset FROM AssessmentQuestionAssetAssoc questionAsset  WHERE questionAsset.assetKey =:key AND questionAsset.question.gooruOid =:qid";

	@Override
	public AssessmentQuestion getQuestion(String questionId) {
		Query query = getSession().createQuery(GET_QUESTION);
		query.setParameter(QUESTION_ID, questionId);
		List<AssessmentQuestion> assessmentQuestion = list(query);
		return (assessmentQuestion != null && assessmentQuestion.size() > 0) ? assessmentQuestion.get(0) : null;
	}

	@Override
	public AssessmentQuestionAssetAssoc getQuestionAsset(String key, String questionId) {
		Query query = getSession().createQuery(GET_QUESTION_ASSET);
		query.setParameter(KEY, key);
		query.setParameter(QUESTION_ID, questionId);
		List<AssessmentQuestionAssetAssoc> assessmentQuestionAssetAssoc = list(query);
		return (assessmentQuestionAssetAssoc != null && assessmentQuestionAssetAssoc.size() > 0) ? assessmentQuestionAssetAssoc.get(0) : null;
	}
}
