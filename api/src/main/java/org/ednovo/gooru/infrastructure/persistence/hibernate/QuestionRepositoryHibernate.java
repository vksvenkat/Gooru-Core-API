package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionRepositoryHibernate extends BaseRepositoryHibernate implements QuestionRepository, ConstantProperties, ParameterProperties {

	private static final String GET_QUESTION = "FROM AssesmentQuestion aq where aq.gooruOid=:questionId";

	@Override
	public AssessmentQuestion getQuestion(String questionId) {
		Query query = getSession().createQuery(GET_QUESTION);
		query.setParameter(QUESTION_ID, questionId);
		List<AssessmentQuestion> assessmentQuestion = list(query);
		return (assessmentQuestion != null && assessmentQuestion.size() > 0) ? assessmentQuestion.get(0) : null;
	}
}
