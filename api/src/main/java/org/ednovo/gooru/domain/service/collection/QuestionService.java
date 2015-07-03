package org.ednovo.gooru.domain.service.collection;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.User;

public interface QuestionService extends AbstractResourceService {

	AssessmentQuestion createQuestion(AssessmentQuestion question, User user);

	void updateQuestion(String questionId, AssessmentQuestion assessmentQuestion, User user);
}
