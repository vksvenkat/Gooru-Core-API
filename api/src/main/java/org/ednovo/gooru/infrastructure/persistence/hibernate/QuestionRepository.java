package org.ednovo.gooru.infrastructure.persistence.hibernate;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;

public interface QuestionRepository extends BaseRepository {
	AssessmentQuestion getQuestion(String questionId);

	AssessmentQuestionAssetAssoc getQuestionAsset(String key, String questionId);
}
