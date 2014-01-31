/////////////////////////////////////////////////////////////
// LearnguideRevisionHistorySerializer.java
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
package org.ednovo.gooru.domain.service.revision_history;

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.Segment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author Search Team, RajaMani
 * 
 */
@Service
public class LearnguideRevisionHistorySerializer extends RevisionHistorySerializer<Learnguide> implements LearnguideSerializerConstants {

	@Override
	protected String serialize(Learnguide entity) {
		JSONObject collectionJsonObject = new JSONObject();
		try {
			collectionJsonObject.put(LEARNGUIDE_CONTENT_ID, entity.getContentId() + "");
			// collectionJson.put(LEARNGUIDE_IMPORT_CODE,entity
			collectionJsonObject.put(LEARNGUIDE_LESSON, entity.getLesson());
			collectionJsonObject.put(LEARNGUIDE_GOALS, entity.getGoals());
			collectionJsonObject.put(LEARNGUIDE_FOLDER, entity.getFolder());
			collectionJsonObject.put(LEARNGUIDE_GRADE, entity.getGrade());
			collectionJsonObject.put(LEARNGUIDE_THUMBNAIL, entity.getThumbnail());
			collectionJsonObject.put(LEARNGUIDE_TYPE, entity.getType());
			collectionJsonObject.put(LEARNGUIDE_NOTES, entity.getNotes());
			collectionJsonObject.put(LEARNGUIDE_DURATION, entity.getDuration());
			collectionJsonObject.put(LEARNGUIDE_VOCABULARY, entity.getVocabulary());
			collectionJsonObject.put(LEARNGUIDE_NARRATION, entity.getNarration());
			collectionJsonObject.put(LEARNGUIDE_CURRICULUM, entity.getCurriculum());
			collectionJsonObject.put(LEARNGUIDE_MEDIUM, entity.getMedium());
			collectionJsonObject.put(LEARNGUIDE_COLLECTION_GOORU_OID, entity.getCollectionGooruOid());
			collectionJsonObject.put(LEARNGUIDE_ASSESSMENT_GOORU_OID, entity.getAssessmentGooruOid());
			collectionJsonObject.put(LEARNGUIDE_SOURCE, entity.getSource());
			collectionJsonObject.put(LEARNGUIDE_REQUEST_PENDING, entity.getRequestPending());

			putResourceObject(collectionJsonObject, entity);

			collectionJsonObject.put(COLLABORATORS, getCollaboratorJsonArray(entity.getCollaborators()));
			if (entity.getResourceSegments() != null) {
				Iterator<Segment> segments = entity.getResourceSegments().iterator();
				JSONArray segmentsJsonArray = new JSONArray();
				while (segments.hasNext()) {
					Segment segment = segments.next();
					JSONObject segmentJsonObject = new JSONObject();
					segmentJsonObject.put(SEGMENT_SEGMENT_ID, segment.getSegmentId() + "");
					segmentJsonObject.put(SEGMENT_TITLE, segment.getTitle());
					segmentJsonObject.put(SEGMENT_DESCRIPTION, segment.getDescription());
					segmentJsonObject.put(SEGMENT_TYPE_NAME, segment.getType());
					segmentJsonObject.put(SEGMENT_RENDITION_URL, segment.getRenditionUrl());
					segmentJsonObject.put(SEGMENT_DURATION, segment.getDuration());
					segmentJsonObject.put(SEGMENT_SEQUENCE, segment.getSequence());
					segmentJsonObject.put(SEGMENT_XML_SEGMENT_ID, segment.getXmlSegmentId());
					segmentJsonObject.put(SEGMENT_IS_META, segment.getIsMeta());
					segmentJsonObject.put(SEGMENT_CONCEPT, segment.getConcept());
					segmentJsonObject.put(SEGMENT_SEGMENT_IMAGE, segment.getSegmentImage());
					if (segment.getResourceInstances() != null) {
						Iterator<ResourceInstance> resourceInstances = segment.getResourceInstances().iterator();
						JSONArray resourceInstanceJsonArray = new JSONArray();
						while (resourceInstances.hasNext()) {
							ResourceInstance resourceInstance = resourceInstances.next();
							JSONObject resourceInstanceJsonObject = new JSONObject();
							resourceInstanceJsonObject.put(INSTANCE_RESOURCE_INSTANCE_ID, resourceInstance.getResourceInstanceId() + "");
							resourceInstanceJsonObject.put(INSTANCE_SEGMENT_ID, resourceInstance.getSegment().getSegmentId());
							resourceInstanceJsonObject.put(INSTANCE_RESOURCE_ID, resourceInstance.getResource().getContentId() + "");
							resourceInstanceJsonObject.put(INSTANCE_TITLE, resourceInstance.getTitle());
							resourceInstanceJsonObject.put(INSTANCE_DESCRIPTION, resourceInstance.getDescription());
							resourceInstanceJsonObject.put(INSTANCE_START, resourceInstance.getStart());
							resourceInstanceJsonObject.put(INSTANCE_STOP, resourceInstance.getStop());
							resourceInstanceJsonObject.put(INSTANCE_NARRATIVE, resourceInstance.getNarrative());
							resourceInstanceJsonObject.put(INSTANCE_SEQUENCE, resourceInstance.getSequence());
							resourceInstanceJsonArray.put(resourceInstanceJsonObject);
						}
						segmentJsonObject.put(RESOURCE_INSTANCES, resourceInstanceJsonArray);
					}
					segmentsJsonArray.put(segmentJsonObject);
				}
				collectionJsonObject.put(SEGMENTS, segmentsJsonArray);
			}
			return collectionJsonObject.toString();

		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.LEARNGUIDE;
	}

}
