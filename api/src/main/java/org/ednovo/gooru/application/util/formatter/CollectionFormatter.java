/////////////////////////////////////////////////////////////
// CollectionFormatter.java
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
package org.ednovo.gooru.application.util.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Question;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceInstanceComparator;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.formatter.CollectionFo;
import org.ednovo.gooru.core.application.util.formatter.InfoFo;
import org.ednovo.gooru.core.application.util.formatter.QuestionFo;
import org.ednovo.gooru.core.application.util.formatter.ResourceFo;
import org.ednovo.gooru.core.application.util.formatter.SegmentFo;
import org.ednovo.gooru.core.application.util.formatter.TextbookFo;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class CollectionFormatter {
	
	private static CollectionFormatter instance;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionFormatter.class);

	public static enum SKELETON_SEGMENT {
		ASSESSMENT("assessment"), SUGGESTED_STUDY("suggestedstudy"), HOMEWORK("homework");

		private String value;

		SKELETON_SEGMENT(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private CollectionUtil collectionUtil;
	
	@Autowired
	private CustomFieldsService customFieldService;

	
	public CollectionFormatter() {
		instance = this;
	}

	private ResourceFo getResourceInstance(ResourceInstance resourceInstance) {

		try {

			ResourceFo resourceFo = null;

			Resource resource = resourceInstance.getResource();
			
			if (resource.getResourceType().getName().equals(ResourceType.Type.QUIZ.getType()) && !(resource instanceof Question)) {
				resource = getResourceService().findResourceByContentGooruId(resource.getGooruOid());
				resourceFo = new QuestionFo();
			} else if (resource.getResourceType().getName().equals(ResourceType.Type.TEXTBOOK.getType()) && !(resource instanceof Textbook)) {
				resource = getResourceService().findTextbookByContentGooruId(resource.getGooruOid());
				resourceFo = new TextbookFo();
				((TextbookFo) resourceFo).setDocumentid(((Textbook) resource).getDocumentId());
				((TextbookFo) resourceFo).setDocumentkey(((Textbook) resource).getDocumentKey());
			} else {
				resourceFo = new ResourceFo();
			}
			
			resourceFo.getInstructionnotes().setStart(resourceInstance.getStart());
			resourceFo.getInstructionnotes().setStop(resourceInstance.getStop());
			resourceFo.getInstructionnotes().setInstruction(resourceInstance.getNarrative());
			
			String brokenResource = "0";
			if (resource.getBrokenStatus() != null && resource.getBrokenStatus() != 0) {
				brokenResource = "1";
			}
			String hasFrameBreaker = "0";
			if (resource.getHasFrameBreaker() != null && resource.getHasFrameBreaker().booleanValue()) {
				hasFrameBreaker = "1";
			}
			resourceFo.getResourcestatus().setStatusIsBroken(brokenResource);
			resourceFo.getResourcestatus().setStatusIsFrameBreaker(hasFrameBreaker);
			resourceFo.setThumbnails(resource.getThumbnails());
			if (resource.getThumbnail() == null || resource.getThumbnail().equals("")) {
				final String resourceTypeName = resource.getResourceType().getName();
				if (resourceTypeName.equals(ResourceType.Type.PRESENTATION.getType()) || resourceTypeName.equals(ResourceType.Type.HANDOUTS.getType()) || resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
					resourceFo.setThumbnail("slides/slides1.jpg");
				} else {
					resourceFo.setThumbnail(resource.getGooruOid()+".jpg");
				}
			}
			resourceFo.setThumbnail(resource.getThumbnail());
			resourceFo.setResourceInstanceId(resourceInstance.getResourceInstanceId());
			resourceFo.setDescription(resourceInstance.getDescription());
			resourceFo.setLabel(resourceInstance.getTitle());
			resourceFo.setType(resource.getResourceType().getName());
			resourceFo.setTypeDesc(resource.getResourceType().getDescription());
			resourceFo.setSharing(resource.getSharing());
			resourceFo.setResourcefolder(resource.getFolder());
			resourceFo.setAssetURI(resource.getAssetURI());
			resourceFo.setNativeurl(resource.getUrl());
			resourceFo.setId(resource.getGooruOid());
			resourceFo.setCategory(resource.getCategory());
			resourceFo.setRecordSource(resource.getRecordSource());
			if(resource.getResourceInfo() != null) {
				resourceFo.getResourceInfo().setNumOfPages(resource.getResourceInfo().getNumOfPages());
			}
			
			return resourceFo;

		} catch (Exception exception) {
			return null;
		}
	}

	protected List<ResourceFo> getResourceInstances(Segment segment, String mediaTypeFilter) {
		List<ResourceFo> resources = new ArrayList<ResourceFo>();
		if(segment == null || segment.getResourceInstances() == null) {
			return resources;
		}
		
		// FIXME Move this all to one place, and use some kind of a mapper utility for serialization
		List<ResourceInstance> resourceInstanceList = new ArrayList<ResourceInstance>(segment.getResourceInstances());
		Collections.sort(resourceInstanceList, new ResourceInstanceComparator());
		
		for (ResourceInstance resourceInstance : resourceInstanceList) {
			ResourceFo resource = getResourceInstance(resourceInstance);
			if (resource != null) {
				resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getId()));
				if (resource != null && (mediaTypeFilter == null || !mediaTypeFilter.equalsIgnoreCase(resourceInstance.getResource().getMediaType()))) {
					resources.add(resource);
				}
			} else {
				LOGGER.error("Invalid Resource Instance - " + resourceInstance.getResourceInstanceId());
			}
		}
		return resources;
	}

	public List<SegmentFo> getSegments(Set<Segment> segments, boolean retriveSkeletons, Learnguide collection, String mediaTypeFilter, Integer segmentStart, Integer segmentStop, Boolean skipResources) {
		List<SegmentFo> segmentFos = new ArrayList<SegmentFo>();
		
		Integer count = 1;
		if(segmentStop == null || segmentStop == 0){
			segmentStop = segments.size();
		}
		
		boolean startAddSegment = false;
		for (Segment segment : segments) {
			if(count <= segmentStop){
				boolean add = true;
				if (!retriveSkeletons) {
					for (SKELETON_SEGMENT skeleton : SKELETON_SEGMENT.values()) {
						if (segment.getType().equals(skeleton.getValue())) {
							add = false;
							count--;
							break;
						}
					}
				}
				if(count.equals(segmentStart) && !startAddSegment && add){
					startAddSegment = true;
				}
				if (startAddSegment) {
					segmentFos.add(getSegment(segment,collection, mediaTypeFilter, skipResources));
				}
			}
			count++;
		}	
		return segmentFos;
	}

	private SegmentFo getSegment(Segment segment,Resource resource, String mediaTypeFilter, Boolean skipResources) {

		SegmentFo segmentFo = new SegmentFo();
		segmentFo.setId(segment.getSegmentId());
		segmentFo.setConcept(segment.getConcept());
		segmentFo.setTitle(segment.getTitle());
		segmentFo.setDescription(segment.getDescription());
		segmentFo.setDuration(segment.getDuration());
		segmentFo.setType(segment.getType());
		segmentFo.setNativeurl(segment.getRenditionUrl());
		if(!skipResources){
			List<ResourceFo> resourceFo = getResourceInstances(segment, mediaTypeFilter);
			segmentFo.setResources(resourceFo);
			segmentFo.setResourceCount(resourceFo.size());
		}
		//set absolute path for segment image
		collectionUtil.setSegmentImageAbsolutePath(resource,segment);
		segmentFo.setSegmentImage(segment.getSegmentImage());
		return segmentFo;
	}
	
	public CollectionFo buildCollection(Learnguide collection){
		CollectionFo classplanFo = new CollectionFo();
		if (collection != null) {
			classplanFo.setUser(collection.getUser());
			classplanFo.setCreator(collection.getCreator());
			classplanFo.setTaxonomySet(collection.getTaxonomySet());
			classplanFo.setGooruOid(collection.getGooruOid());
			classplanFo.setAssetURI(collection.getAssetURI());
			classplanFo.setDistinguish(collection.getDistinguish() != null ? collection.getDistinguish() : Short.valueOf("0"));
			classplanFo.setSharing(collection.getSharing());
			classplanFo.setSource(collection.getSource());
			classplanFo.setLastModified(collection.getLastModified());
			classplanFo.setIsFeatured(collection.getIsFeatured() != null ? collection.getIsFeatured() :  0);
			classplanFo.setLinkedAssessmentTitle(collection.getLinkedAssessmentTitle());
			classplanFo.setLinkedCollectionTitle(collection.getLinkedCollectionTitle());
			classplanFo.setAssessmentGooruOid(collection.getAssessmentGooruOid());
			classplanFo.setCollectionGooruOid(collection.getCollectionGooruOid());
			classplanFo.setNarrative(collection.getNarration());
			classplanFo.setResourceFolder(collection.getFolder());
			classplanFo.setNativeurl(collection.getUrl());
			classplanFo.setThumbnail(collection.getThumbnail());
			classplanFo.setTitle(collection.getTitle());
			String currentUserOrgUid = UserGroupSupport.getUserOrganizationUid();
			if (collection.getOrganization() != null && ( currentUserOrgUid == null || collection.getOrganization().getPartyUid().equals(currentUserOrgUid))) {
				classplanFo.setUserid(collection.getUser().getGooruUId());
			}
			classplanFo.setCollaboratorsString(collection.collaboratorsInAString());
			classplanFo.setCustomFieldValues(collection.getCustomFieldValues());
			classplanFo.setHasRequestPending(collection.getRequestPending());
			classplanFo.setThumbnails(collection.getThumbnails());
		}
		return classplanFo;
	}

	public InfoFo getLearnguideInfo(Learnguide learnguide) {
		return new InfoFo(learnguide);
	}

	public static CollectionFormatter getInstance() {
		return instance;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

}
