/*
*QuizSerializerConstants.java
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

package org.ednovo.gooru.domain.service.revision_history;

/**
 * @author Search Team
 * 
 */
public interface QuizSerializerConstants extends ResourceSerializerConstants {
	// Assessment
	String ASSESSMENT_ID = "assessment_id";
	String ASSESSMENT_GOORU_OID = "assessment_gooru_oid";
	String ASSESSMENT_IMPORT_CODE = "import_code";
	String ASSESSMENT_NAME = "name";
	String ASSESSMENT_DESCRIPTION = "description";
	String ASSESSMENT_MEDIUM = "medium";
	String ASSESSMENT_GRADE = "grade";
	String ASSESSMENT_LEARNING_OBJECTIVES = "learning_objectives";
	String ASSESSMENT_TIME_TO_COMPlETE_IN_SECS = "time_to_complete_in_secs";
	String ASSESSMENT_IS_RANDOM = "is_random";
	String ASSESSMENT_IS_CHOICE_RANDOM = "is_choice_random";
	String ASSESSMENT_SHOW_HINTS = "show_hints";
	String ASSESSMENT_SHOW_SCORE = "show_score";
	String ASSESSMENT_SHOW_CORRECT_ANSWER = "show_correct_answer";
	String ASSESSMENT_QUESTION_COUNT = "question_count";
	String ASSESSMENT_SOURCE = "source";
	String ASSESSMENT_VOCABULARY = "vocabulary";
	String ASSESSMENT_COLLECTION_GOORU_OID = "collection_gooru_oid";
	String ASSESSMENT_QUIZ_GOORU_OID = "quiz_gooru_oid";
	
	// Assessment Segment
	String SEGMENT_ID = "segment_id";
	String SEGMENT_UID = "segment_uid";
	String SEGMENT_NAME = "name";
	String SEGMENT_ASSESSMENT_ID = "assessment_id";
	String SEGMENT_SEQUENCE = "sequence";
	String SEGMENT_TIME_TO_COMPLETE_IN_SECS = "time_to_complete_in_secs";

	// Assessment segment question assoc
	String SEGMENT_QUESTION_ASSOC_SEGMENT_ID = "segment_id";
	String SEGMENT_QUESTION_ASSOC_SEGMENT_QUESTION_ID = "question_id";
	String SEGMENT_QUESTION_ASSOC_SEGMENT_SEQUENCE = "sequence";
	
	// other
	String SEGMENTS = "segments";
	String SEGMENT_QUESTION_ASSOC="assessment_segment_question_assoc";
}
