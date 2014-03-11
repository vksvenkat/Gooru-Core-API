/*
*QuizRevisionHistoryDeserializer.java
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

/**
 * 
 */
package org.ednovo.gooru.domain.service.revision_history;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Search Team
 * 
 */
@Service
public class QuizRevisionHistoryDeserializer extends RevisionHistoryDeserializer<Assessment> implements QuizSerializerConstants {

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.QUIZ;
	}

	@Override
	protected Assessment deserialize(String data) {
		try {
			JSONObject assessmentJsonObject = new JSONObject(data);
			Assessment assessment = new Assessment();

			assessment.setContentId(Long.valueOf((String) get(assessmentJsonObject, ASSESSMENT_ID)));
			assessment.setImportCode((String) get(assessmentJsonObject, ASSESSMENT_IMPORT_CODE));
			assessment.setName((String) get(assessmentJsonObject, ASSESSMENT_NAME));
			assessment.setDescription((String) get(assessmentJsonObject, ASSESSMENT_DESCRIPTION));
			assessment.setMedium((String) get(assessmentJsonObject, ASSESSMENT_MEDIUM));
			assessment.setGrade((String) get(assessmentJsonObject, ASSESSMENT_GRADE));
			assessment.setLearningObjectives((String) get(assessmentJsonObject, ASSESSMENT_LEARNING_OBJECTIVES));
			assessment.setTimeToCompleteInSecs((Integer) get(assessmentJsonObject, ASSESSMENT_TIME_TO_COMPlETE_IN_SECS));
			assessment.setIsRandom((Boolean) get(assessmentJsonObject, ASSESSMENT_IS_RANDOM));
			assessment.setIsChoiceRandom((Boolean) get(assessmentJsonObject, ASSESSMENT_IS_CHOICE_RANDOM));
			assessment.setShowHints((Boolean) get(assessmentJsonObject, ASSESSMENT_SHOW_HINTS));
			assessment.setShowScore((Boolean) get(assessmentJsonObject, ASSESSMENT_SHOW_SCORE));
			assessment.setShowCorrectAnswer((Boolean) get(assessmentJsonObject, ASSESSMENT_SHOW_CORRECT_ANSWER));
			assessment.setQuestionCount((Integer) get(assessmentJsonObject, ASSESSMENT_QUESTION_COUNT));
			assessment.setSource((String) get(assessmentJsonObject, ASSESSMENT_SOURCE));
			assessment.setVocabulary((String) get(assessmentJsonObject, ASSESSMENT_VOCABULARY));
			assessment.setCollectionGooruOid((String) get(assessmentJsonObject, ASSESSMENT_COLLECTION_GOORU_OID));
			assessment.setQuizGooruOid((String) get(assessmentJsonObject, ASSESSMENT_QUIZ_GOORU_OID));

			JSONArray contentClassificationsJsonArray = (JSONArray) get(assessmentJsonObject, CONTENT_CLASSIFICATIONS);
			if (contentClassificationsJsonArray != null) {
				Set<Code> taxonomySet = new HashSet<Code>();
				for (int i = 0; i < contentClassificationsJsonArray.length(); i++) {
					JSONObject classificationJsonObject = (JSONObject) contentClassificationsJsonArray.get(i);
					Integer codeId = Integer.parseInt((String) get(classificationJsonObject, CONTENT_CLASSIFICATION_CODE_ID));
					Code code = taxonomyRespository.findCodeByCodeId(codeId);
					taxonomySet.add(code);
				}
				assessment.setTaxonomySet(taxonomySet);
			}

			getDeserializedResource(assessment, assessmentJsonObject);

			// assessmentService.getAssessmentMetaData(assessment);

			JSONArray collaborators = (JSONArray) get(assessmentJsonObject, COLLABORATORS);
			List<User> collaboratorsList = new ArrayList<User>();
			for (int j = 0; j < collaborators.length(); j++) {
				User collaborator = getUserRepository().findByGooruId((String) collaborators.get(j));
				if (collaborator != null) {
					collaboratorsList.add(collaborator);
				} else {
					logger.warn("collaborator does not found: " + (String) collaborators.get(j));
				}
			}
			if (collaboratorsList.size() > 0) {
				assessment.setCollaboratorList(collaboratorsList);
			}

			JSONArray segmentsJsonArray = (JSONArray) get(assessmentJsonObject, SEGMENTS);
			Set<AssessmentSegment> segments = new HashSet<AssessmentSegment>();
			for (int i = 0; i < segmentsJsonArray.length(); i++) {
				JSONObject segmentJSON = (JSONObject) segmentsJsonArray.get(i);
				logger.warn(segmentJSON.toString());
				AssessmentSegment assessmentSegment = new AssessmentSegment();
				assessmentSegment.setSegmentId(Integer.parseInt((String) get(segmentJSON, SEGMENT_ID)));
				assessmentSegment.setSegmentUId((String) get(segmentJSON, SEGMENT_UID));
				assessmentSegment.setName((String) get(segmentJSON, SEGMENT_NAME));
				assessmentSegment.setAssessment(assessment);
				assessmentSegment.setSequence((Integer) get(segmentJSON, SEGMENT_SEQUENCE));
				assessmentSegment.setTimeToCompleteInSecs((Integer) get(segmentJSON, SEGMENT_TIME_TO_COMPLETE_IN_SECS));

				// get assessment segment question assoc
				Set<AssessmentSegmentQuestionAssoc> segmentQuestions = new HashSet<AssessmentSegmentQuestionAssoc>();
				JSONArray segmentQuestionAssocJsonArray = (JSONArray) get(segmentJSON, SEGMENT_QUESTION_ASSOC);
				for (int j = 0; j < segmentQuestionAssocJsonArray.length(); j++) {
					JSONObject segmentquestionAssocJsonObject = (JSONObject) segmentQuestionAssocJsonArray.get(j);
					Long questionId = Long.valueOf((String) get(segmentquestionAssocJsonObject, SEGMENT_QUESTION_ASSOC_SEGMENT_QUESTION_ID));
					AssessmentQuestion assessmentQuestion = (AssessmentQuestion) assessmentRepository.get(AssessmentQuestion.class, questionId);
					AssessmentSegmentQuestionAssoc segmentQuestionAssoc = new AssessmentSegmentQuestionAssoc();
					segmentQuestionAssoc.setQuestion(assessmentQuestion);
					segmentQuestionAssoc.setSegment(assessmentSegment);
					segmentQuestionAssoc.setSequence((Integer) get(segmentquestionAssocJsonObject, SEGMENT_QUESTION_ASSOC_SEGMENT_SEQUENCE));
					segmentQuestions.add(segmentQuestionAssoc);
				}
				assessmentSegment.setSegmentQuestions(segmentQuestions);
				segments.add(assessmentSegment);
			}
			assessment.setSegments(segments);
			return assessment;
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
