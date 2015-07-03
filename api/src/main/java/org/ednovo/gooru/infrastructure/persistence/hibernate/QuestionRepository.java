package org.ednovo.gooru.infrastructure.persistence.hibernate;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;

public interface QuestionRepository extends BaseRepository {
	AssessmentQuestion getQuestion(String questionId);
}
