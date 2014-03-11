/*
*LearnguideSerializerConstants.java
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
public interface LearnguideSerializerConstants extends ResourceSerializerConstants{
	// Learnguide
	String LEARNGUIDE_CONTENT_ID = "content_id";
	String LEARNGUIDE_IMPORT_CODE = "import_code";
	String LEARNGUIDE_LESSON = "lesson";
	String LEARNGUIDE_GOALS = "goals";
	String LEARNGUIDE_FOLDER = "folder";
	String LEARNGUIDE_XML = "xml";
	String LEARNGUIDE_GRADE = "grade";
	String LEARNGUIDE_THUMBNAIL = "thumbnail";
	String LEARNGUIDE_TYPE = "type";
	String LEARNGUIDE_NOTES = "notes";
	String LEARNGUIDE_DURATION = "duration";
	String LEARNGUIDE_VOCABULARY = "vocabulary";
	String LEARNGUIDE_NARRATION = "narration";
	String LEARNGUIDE_CURRICULUM = "curriculum";
	String LEARNGUIDE_MIGRATED = "migrated";
	String LEARNGUIDE_MEDIUM = "medium";
	String LEARNGUIDE_COLLECTION_GOORU_OID = "collection_gooru_oid";
	String LEARNGUIDE_ASSESSMENT_GOORU_OID = "assessment_gooru_oid";
	String LEARNGUIDE_SOURCE = "source";
	String LEARNGUIDE_REQUEST_PENDING = "request_pending";

	// learnguide segment
	String SEGMENT_SEGMENT_ID = "segment_id";
	String SEGMENT_RESOURCE_ID = "resource_id";
	String SEGMENT_TITLE = "title";
	String SEGMENT_DESCRIPTION = "description";
	String SEGMENT_TYPE_NAME = "type_name";
	String SEGMENT_RENDITION_URL = "rendition_url";
	String SEGMENT_DURATION = "Duration";
	String SEGMENT_SEQUENCE = "Sequence";
	String SEGMENT_XML_SEGMENT_ID = "xml_segment_id";
	String SEGMENT_IS_META = "is_meta";
	String SEGMENT_CONCEPT = "concept";
	String SEGMENT_THUMBNAIL = "thumbnail";
	String SEGMENT_SEGMENT_IMAGE = "segment_image";

	// other
	String RESOURCE_INSTANCES="resourceInstances";
	String SEGMENTS = "segments";
}
