/*
*AssessmentRepository.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.infrastructure.persistence.hibernate.assessment;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentAttemptTry;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface AssessmentRepository extends BaseRepository {

	String TAXONOMY_CODE = "taxonomyCode";

	String PAGE = "pageNum";

	String PAGE_SIZE = "pageSize";

	String QUESTION_GOORU_ID = "gooruOid";

	String BASIC_LISTING = "basicListing";

	String IS_CORRECT = "isCorrect";

	String QUESTION_TYPE_SA = "2";

	String ATTEMPT_ID = "attemptId";

	String SEGMENT_ID = "segmentId";

	String IS_LIVE = "isLive";

	String USER_ID = "userId";

	String ACCESS_TYPE = "accessType";

	List<Assessment> listAssessments(Map<String, String> filters);

	List<QuestionSet> listQuestionSets(Map<String, String> filters);

	List<AssessmentQuestion> getAssessmentQuestions(String gooruOAssessmentId);

	AssessmentQuestion getNextUnansweredQuestion(String gooruOAssessmentId, Integer attemptId);

	List<AssessmentQuestion> listQuestions(Map<String, String> filters);

	// boolean isAnswerUsed(Integer answerId);

	void saveAndFlush(Object object);

	Object getModel(Class<?> classModel, Serializable id);

	boolean getAttemptAnswerStatus(Integer answerId);

	AssessmentQuestionAssetAssoc getQuestionAsset(String assetKey, String gooruOAssessmentId);

	void deleteSegmentQuestion(AssessmentSegmentQuestionAssoc assessmentSegmentQuestion);

	void deleteQuestionSetQuestion(QuestionSetQuestionAssoc questionSetQuestion);

	void deleteQuestionAssets(int assetId);

	boolean isQuestionUsedInSegmentQuestion(String gooruOQuestionId);

	boolean isQuestionUsedInAttemptItem(String gooruOQuestionSetId);

	<T extends Serializable> T getByGooruOId(Class<T> modelClass, String gooruOId);

	Map<String, Object> getAssessmentAttemptsInfo(Integer attemptId, String gooruOAssessmentId, Integer studentId);

	Integer getAssessmentNotAttemptedQuestions(Integer attemptId, String gooruOAssessmentId);

	List<Object[]> getAssessmentAttemptQuestionSummary(Integer attemptId);

	AssessmentSegmentQuestionAssoc findSegmentQuestion(Integer segmentId, String gooruOQuestionId);

	Integer getAssessmentQuestionsCount(Long assessmentId);

	Assessment getAssessmentForSegment(Integer segmentId);

	void updateTimeForSegments(Long questionId);

	void updateTimeForAssessments(Long questionId);

	AssessmentQuestion findQuestionByImportCode(String code);

	Assessment findAssessmentByImportCode(String code);

	AssessmentQuestionAssetAssoc findQuestionAsset(String questionGooruOid, Integer assetId);

	List<AssessmentQuestion> getAssessmentQuestionsByAssessmentGooruOids(String gooruOAssessmentId);

	List<AssessmentAnswer> findAnswerByAssessmentQuestionId(Integer questionId);

	List<AssessmentQuestionAssetAssoc> getQuestionAssetByQuestionId(Integer questionId);

	List<Assessment> getAssessmentsListByAssessmentGooruOids(List<String> assessmentIds);

	List<Assessment> getAssessmentOfQuestion(String questionGooruOid);

	String findAssessmentNameByGooruOid(String gooruOid);

	Assessment findQuizContent(String quizGooruOid);

	Integer getCurrentTrySequence(Integer attemptItemId);

	List<AssessmentAttemptTry> findAssessmentAttemptsTryByAttemptItemId(Integer assessmentAttemptItemId);

	Assessment getAssessmentQuestion(String questionGooruOid);

	List<Assessment> listAllQuizsWithoutGroups(Map<String, String> filters);

	String getQuizUserScore(String gooruOAssessmentId, String studentId);

	List<String> getAssessmentByQuestion(String questionGooruOid);

	void deleteQuestionAssoc(String questionGooruOid);

	AssessmentAnswer getAssessmentAnswerById(Integer answerId);
}
