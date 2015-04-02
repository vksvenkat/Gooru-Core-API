/////////////////////////////////////////////////////////////
// AssessmentService.java
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
package org.ednovo.gooru.domain.service.assessment;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;

public interface AssessmentService {
	
	AssessmentQuestion getQuestion(String gooruOQuestionId);

	QuestionSet getQuestionSet(String gooruOQuestionSetId);

	AssessmentQuestionAssetAssoc getQuestionAsset(String assetKey, String gooruOAssessmentId);

	List<AssessmentQuestion> getAssessmentQuestions(String gooruOAssessmentId);

	ActionResponseDTO<AssessmentQuestion> createQuestion(AssessmentQuestion question, boolean index) throws Exception;

	ActionResponseDTO<AssessmentQuestion> updateQuestion(AssessmentQuestion question, List<Integer> deleteAssets, String gooruOQuestionId, boolean copyToOriginal, boolean index) throws Exception;

	ActionResponseDTO<QuestionSet> createQuestionSet(QuestionSet questionSet) throws Exception;

	ActionResponseDTO<QuestionSet> updateQuestionSet(QuestionSet questionSet, String gooruOQuestionSetId) throws Exception;

	QuestionSetQuestionAssoc createQuestionSetQuestion(QuestionSetQuestionAssoc questionSetQuestion);

	boolean getAttemptAnswerStatus(Integer answerId);

	List<QuestionSet> listQuestionSets(Map<String, String> filters);

	int deleteQuestionSetQuestion(String gooruOQuestionSetId, String gooruOQuestionId, User caller);

	int deleteQuestionSet(String gooruOQuestionSetId, User caller);

	AssessmentQuestionAssetAssoc uploadQuestionAsset(String gooruQuestionId, AssessmentQuestionAssetAssoc questionAsset, boolean index) throws Exception;

	AssessmentQuestion copyAssessmentQuestion(User user, String gooruQuestionId) throws Exception;

	void updateQuetionInfo(String gooruOQuestionId, Integer segmentId);

	boolean assignAsset(String questionGooruId, Integer assetId, String assetKey);

	String updateQuizQuestionImage(String gooruContentId, String fileName, Resource resource, String assetKey) throws Exception;

	AssessmentQuestion updateQuestionAssest(String gooruQuestionId, String assetKeys) throws Exception;
	
	AssessmentQuestion updateQuestionVideoAssest(String gooruQuestionId, String assetKeys) throws Exception;

	void deleteQuestionAssest(String gooruQuestionId) throws Exception;

	void deleteQuizBulk(String gooruContentIds);

	String findAssessmentNameByGooruOId(String gooruOId);

	void deleteQuestionBulk(String gooruQuestionIds);
	
	AssessmentQuestion buildQuestionFromInputParameters(String jsonData, User user, boolean addFlag);
	
	int deleteQuestion(String gooruOQuestionId, User caller);
}
