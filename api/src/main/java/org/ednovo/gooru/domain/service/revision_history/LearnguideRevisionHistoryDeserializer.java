/*
*LearnguideRevisionHistoryDeserializer.java
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
import java.util.SortedSet;
import java.util.TreeSet;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Search Team
 * 
 */
@Service
public class LearnguideRevisionHistoryDeserializer extends RevisionHistoryDeserializer<Learnguide> implements LearnguideSerializerConstants {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private TaxonomyService taxonomyService;

	private static final Logger logger = LoggerFactory.getLogger(LearnguideRevisionHistoryDeserializer.class);

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.LEARNGUIDE;
	}

	@Override
	protected Learnguide deserialize(String data) {
		try {
			JSONObject learnguideJsonObject = new JSONObject(data);
			Learnguide learnguide = new Learnguide();
			learnguide.setContentId(Long.valueOf((String) get(learnguideJsonObject, LEARNGUIDE_CONTENT_ID)));
			learnguide.setLesson((String) get(learnguideJsonObject, LEARNGUIDE_LESSON));
			learnguide.setGoals((String) get(learnguideJsonObject, LEARNGUIDE_GOALS));
			learnguide.setFolder((String) get(learnguideJsonObject, LEARNGUIDE_FOLDER));
			learnguide.setGrade((String) get(learnguideJsonObject, LEARNGUIDE_GRADE));
			learnguide.setThumbnail((String) get(learnguideJsonObject, LEARNGUIDE_THUMBNAIL));
			learnguide.setType((String) get(learnguideJsonObject, LEARNGUIDE_TYPE));
			learnguide.setNotes((String) get(learnguideJsonObject, LEARNGUIDE_NOTES));
			learnguide.setDuration((String) get(learnguideJsonObject, LEARNGUIDE_DURATION));
			learnguide.setVocabulary((String) get(learnguideJsonObject, LEARNGUIDE_VOCABULARY));
			learnguide.setNarration((String) get(learnguideJsonObject, LEARNGUIDE_NARRATION));
			learnguide.setCurriculum((String) get(learnguideJsonObject, LEARNGUIDE_CURRICULUM));
			learnguide.setMedium((String) get(learnguideJsonObject, LEARNGUIDE_MEDIUM));
			learnguide.setCollectionGooruOid((String) get(learnguideJsonObject, LEARNGUIDE_COLLECTION_GOORU_OID));
			learnguide.setAssessmentGooruOid((String) get(learnguideJsonObject, LEARNGUIDE_ASSESSMENT_GOORU_OID));
			learnguide.setSource((String) get(learnguideJsonObject, LEARNGUIDE_SOURCE));
			learnguide.setRequestPending((Integer) get(learnguideJsonObject, LEARNGUIDE_REQUEST_PENDING));

			
			getDeserializedResource(learnguide, learnguideJsonObject);
			
			JSONArray contentClassificationsJsonArray = (JSONArray) get(learnguideJsonObject, CONTENT_CLASSIFICATIONS);
			if (contentClassificationsJsonArray != null) {
				Set<Code> taxonomySet = new HashSet<Code>();
				for (int i = 0; i < contentClassificationsJsonArray.length(); i++) {
					JSONObject classificationJsonObject = (JSONObject) contentClassificationsJsonArray.get(i);
					Integer codeId = Integer.parseInt((String) get(classificationJsonObject, CONTENT_CLASSIFICATION_CODE_ID));
					Code code = taxonomyRespository.findCodeByCodeId(codeId);
					taxonomySet.add(code);
				}
				learnguide.setTaxonomySet(taxonomySet);
			}

			learnguide.setTaxonomyMapByCode(TaxonomyUtil.getTaxonomyMapByCode(learnguide.getTaxonomySet(), taxonomyService));

			

			JSONArray collaborators = (JSONArray) get(learnguideJsonObject, COLLABORATORS);
			List<User> collaboratorsList = new ArrayList<User>();
			for (int j = 0; j < collaborators.length(); j++) {
				User collaborator = getUserRepository().findByGooruId((String) collaborators.get(j));
				if (collaborator != null) {
					collaboratorsList.add(collaborator);
				} else {
					logger.warn("collaborator does not found: " + (String) collaborators.get(j));
				}
			}
			if(collaboratorsList.size() > 0){
				learnguide.setCollaborators(collaboratorsList);
			}

			JSONArray segmentsJsonArray = (JSONArray) get(learnguideJsonObject, SEGMENTS);
			SortedSet<Segment> resourceSegments = new TreeSet<Segment>();
			List<ResourceInstance> allResourceInstances= new ArrayList<ResourceInstance>();
			if (segmentsJsonArray != null) {
				for (int i = 0; i < segmentsJsonArray.length(); i++) {
					JSONObject segmentJsonObject = (JSONObject) segmentsJsonArray.get(i);
					Segment segment = new Segment();
					segment.setSegmentId((String) get(segmentJsonObject, SEGMENT_SEGMENT_ID));
					segment.setTitle((String) get(segmentJsonObject, SEGMENT_TITLE));
					segment.setDescription((String) get(segmentJsonObject, SEGMENT_DESCRIPTION));
					segment.setType((String) get(segmentJsonObject, SEGMENT_TYPE_NAME));
					segment.setRenditionUrl((String) get(segmentJsonObject, SEGMENT_RENDITION_URL));
					segment.setDuration((String) get(segmentJsonObject, SEGMENT_DURATION));
					segment.setSequence((Integer) get(segmentJsonObject, SEGMENT_SEQUENCE));
					segment.setIsMeta((Integer) get(segmentJsonObject, SEGMENT_IS_META));
					segment.setConcept((String) get(segmentJsonObject, SEGMENT_CONCEPT));
					segment.setSegmentImage((String) get(segmentJsonObject, SEGMENT_SEGMENT_IMAGE));

					JSONArray resourceInstancesJsonArray = (JSONArray) get(segmentJsonObject, RESOURCE_INSTANCES);
					SortedSet<ResourceInstance> resourceInstances = new TreeSet<ResourceInstance>();
					
					if (resourceInstancesJsonArray != null) {
						for (int j = 0; j < resourceInstancesJsonArray.length(); j++) {
							JSONObject resourceInstanceJsonObject = (JSONObject) resourceInstancesJsonArray.get(j);
							ResourceInstance resourceInstance = new ResourceInstance();
							resourceInstance.setResourceInstanceId((String) get(resourceInstanceJsonObject, INSTANCE_RESOURCE_INSTANCE_ID));
							resourceInstance.setSegment(segment);
							Resource resource = (Resource) resourceRepository.get(Resource.class, Long.valueOf((String) get(resourceInstanceJsonObject, INSTANCE_RESOURCE_ID)));
							if (resource != null) {
								resourceInstance.setResource(resource);
							}
							resourceInstance.setTitle((String) get(resourceInstanceJsonObject, INSTANCE_TITLE));
							resourceInstance.setDescription((String) get(resourceInstanceJsonObject, INSTANCE_DESCRIPTION));
							resourceInstance.setStart((String) get(resourceInstanceJsonObject, INSTANCE_START));
							resourceInstance.setStop((String) get(resourceInstanceJsonObject, INSTANCE_STOP));
							resourceInstance.setNarrative((String) get(resourceInstanceJsonObject, INSTANCE_NARRATIVE));
							resourceInstance.setSequence((Integer) get(resourceInstanceJsonObject, INSTANCE_SEQUENCE));
							resourceInstances.add(resourceInstance);
						}
					}
					segment.setResourceInstances(resourceInstances);
					allResourceInstances.addAll(resourceInstances);
					resourceSegments.add(segment);
				}
			}
			learnguide.setResourceSegments(resourceSegments);
			
			learnguide.setResourceInstances(allResourceInstances);
			return learnguide;

		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
