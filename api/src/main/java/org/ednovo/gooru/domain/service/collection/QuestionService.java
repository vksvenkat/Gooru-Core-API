package org.ednovo.gooru.domain.service.collection;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.User;

public interface QuestionService extends AbstractResourceService {

	AssessmentQuestion createQuestion(String data, User user);

	AssessmentQuestion updateQuestion(String questionId, String data, User user);

	AssessmentQuestion getQuestion(String questionId);

	AssessmentQuestion copyQuestion(String questionId, User user);
}
