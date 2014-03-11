/*
*QuizRevisionHistorySerializer.java
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

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author Search Team
 * 
 */
@Service
public class QuizRevisionHistorySerializer extends RevisionHistorySerializer<Assessment> implements QuizSerializerConstants {

	@Override
	protected String serialize(Assessment entity) {

		JSONObject assessmentJsonObject = new JSONObject();
		try {
			assessmentJsonObject.put(ASSESSMENT_ID, entity.getContentId() + "");
			assessmentJsonObject.put(ASSESSMENT_IMPORT_CODE, entity.getImportCode());
			assessmentJsonObject.put(ASSESSMENT_NAME, entity.getName());
			assessmentJsonObject.put(ASSESSMENT_DESCRIPTION, entity.getDescription());
			assessmentJsonObject.put(ASSESSMENT_MEDIUM, entity.getMedium());
			assessmentJsonObject.put(ASSESSMENT_GRADE, entity.getGrade());
			assessmentJsonObject.put(ASSESSMENT_LEARNING_OBJECTIVES, entity.getLearningObjectives());
			assessmentJsonObject.put(ASSESSMENT_TIME_TO_COMPlETE_IN_SECS, entity.getTimeToCompleteInSecs());
			assessmentJsonObject.put(ASSESSMENT_IS_RANDOM, entity.getIsRandom());
			assessmentJsonObject.put(ASSESSMENT_IS_CHOICE_RANDOM, entity.getIsChoiceRandom());
			assessmentJsonObject.put(ASSESSMENT_SHOW_HINTS, entity.getShowHints());
			assessmentJsonObject.put(ASSESSMENT_SHOW_SCORE, entity.getShowScore());
			assessmentJsonObject.put(ASSESSMENT_SHOW_CORRECT_ANSWER, entity.getShowCorrectAnswer());
			assessmentJsonObject.put(ASSESSMENT_QUESTION_COUNT, entity.getQuestionCount());
			assessmentJsonObject.put(ASSESSMENT_SOURCE, entity.getSource());
			assessmentJsonObject.put(ASSESSMENT_VOCABULARY, entity.getVocabulary());
			assessmentJsonObject.put(ASSESSMENT_COLLECTION_GOORU_OID, entity.getCollectionGooruOid());
			assessmentJsonObject.put(ASSESSMENT_QUIZ_GOORU_OID, entity.getQuizGooruOid());
			putResourceObject(assessmentJsonObject, entity);

			assessmentJsonObject.put(COLLABORATORS, getCollaboratorJsonArray(entity.getCollaboratorList()));

			if (entity.getSegments() != null) {
				Iterator<AssessmentSegment> segmentsIterator = entity.getSegments().iterator();
				JSONArray assessmentSegmentsJsonArray = new JSONArray();
				while (segmentsIterator.hasNext()) {
					AssessmentSegment segment = segmentsIterator.next();
					JSONObject segmentJsonObject = new JSONObject();
					segmentJsonObject.put(SEGMENT_ID, segment.getSegmentId() + "");
					segmentJsonObject.put(SEGMENT_UID, segment.getSegmentUId());
					segmentJsonObject.put(SEGMENT_NAME, segment.getName());
					segmentJsonObject.put(SEGMENT_ASSESSMENT_ID, segment.getAssessment().getContentId() + "");
					segmentJsonObject.put(SEGMENT_SEQUENCE, segment.getSequence());
					segmentJsonObject.put(SEGMENT_TIME_TO_COMPLETE_IN_SECS, segment.getTimeToCompleteInSecs());
					if (segment.getSegmentQuestions() != null) {
						Iterator<AssessmentSegmentQuestionAssoc> segmentQuestionAssocs = segment.getSegmentQuestions().iterator();
						JSONArray segmentQuestionAssocJsonArray = new JSONArray();
						while (segmentQuestionAssocs.hasNext()) {
							JSONObject segmentQuestionAssocJsonObject = new JSONObject();
							AssessmentSegmentQuestionAssoc segmentQuestionAssoc = segmentQuestionAssocs.next();
							segmentQuestionAssocJsonObject.put(SEGMENT_QUESTION_ASSOC_SEGMENT_ID, segmentQuestionAssoc.getSegment().getSegmentId() + "");
							segmentQuestionAssocJsonObject.put(SEGMENT_QUESTION_ASSOC_SEGMENT_QUESTION_ID, segmentQuestionAssoc.getQuestion().getContentId() + "");
							segmentQuestionAssocJsonObject.put(SEGMENT_QUESTION_ASSOC_SEGMENT_SEQUENCE, segmentQuestionAssoc.getSequence());
							segmentQuestionAssocJsonArray.put(segmentQuestionAssocJsonObject);
						}
						segmentJsonObject.put(SEGMENT_QUESTION_ASSOC, segmentQuestionAssocJsonArray);
					}
					assessmentSegmentsJsonArray.put(segmentJsonObject);
				}
				assessmentJsonObject.put(SEGMENTS, assessmentSegmentsJsonArray);
			}
			return assessmentJsonObject.toString();
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.QUIZ;
	}

}
