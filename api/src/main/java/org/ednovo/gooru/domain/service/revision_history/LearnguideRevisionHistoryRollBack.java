/////////////////////////////////////////////////////////////
// LearnguideRevisionHistoryRollBack.java
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Search Team
 * 
 */

@Service
public class LearnguideRevisionHistoryRollBack extends RevisionHistoryRollBack<Learnguide> implements ParameterProperties {

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private IndexProcessor indexProcessor;

	private static final Logger logger = LoggerFactory.getLogger(LearnguideRevisionHistoryRollBack.class);

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.LEARNGUIDE;
	}

	@Override
	protected Learnguide rollback(Learnguide entity, RevisionHistory history) {
		Learnguide learnguide = learnguideRepository.findByContent(entity.getGooruOid());
		String revisionImagePath = "";
		String revisionFileNamePrefix = "";
		if (learnguide != null) {
			deleteResourceInstances(learnguide.getResourceSegments());
			this.merge(entity, learnguide);
		} else {
			entity.setContentId(null);
			revisionImagePath = entity.getOrganization().getNfsStorageArea().getInternalPath() + entity.getFolder() + entity.getThumbnail();
			revisionFileNamePrefix = StringUtils.substringBeforeLast(entity.getThumbnail(), ".");
			learnguide = entity;
			learnguide.setFolder(null);
			deleteResourceInstances(learnguide.getResourceSegments());
		}
		entity.setRevisionHistoryUid(history.getRevisionHistoryUid());

		learnguideRepository.save(learnguide);
		moveCollectionImage(learnguide, revisionImagePath, revisionFileNamePrefix);
		this.resourceImageUtil.setDefaultThumbnailImageIfFileNotExist((Resource) learnguide);
		saveResourceInstances(entity.getResourceInstances());

		collectionUtil.deleteCollectionFromCache(learnguide.getGooruOid(), COLLECTION);
		indexProcessor.index(learnguide.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

		return learnguide;
	}

	private Learnguide moveCollectionImage(Learnguide newLearnguide, String revisionImagePath, String revisionFileNamePrefix) {

		String destFolderPath = newLearnguide.getOrganization().getNfsStorageArea().getInternalPath() + newLearnguide.getFolder();
		try {
			GooruImageUtil.copyImage(revisionImagePath, destFolderPath, revisionFileNamePrefix);
			resourceImageUtil.sendMsgToGenerateThumbnails(newLearnguide);
		} catch (IOException e) {
			logger.error("Image move : " + e);
		}
		return newLearnguide;
	}

	private void deleteResourceInstances(Set<Segment> resourceSegments) {
		Iterator<Segment> newSegmentsIterator = resourceSegments.iterator();
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		while (newSegmentsIterator.hasNext()) {
			Segment segment = newSegmentsIterator.next();
			Iterator<ResourceInstance> newInstanceIterator = segment.getResourceInstances().iterator();
			while (newInstanceIterator.hasNext()) {
				ResourceInstance instance = newInstanceIterator.next();
				instances.add(instance);
			}
		}
		if (instances.size() > 0) {
			resourceRepository.removeAll(instances);
		}
	}

	@Override
	protected Learnguide merge(Learnguide revisionEntity, Learnguide existingEntity) {
		existingEntity.setLesson(revisionEntity.getLesson());
		existingEntity.setGoals(revisionEntity.getGoals());
		existingEntity.setFolder(revisionEntity.getFolder());
		existingEntity.setGrade(revisionEntity.getGrade());
		existingEntity.setThumbnail(revisionEntity.getThumbnail());
		existingEntity.setType(revisionEntity.getType());
		existingEntity.setNotes(revisionEntity.getNotes());
		existingEntity.setDuration(revisionEntity.getDuration());
		existingEntity.setVocabulary(revisionEntity.getVocabulary());
		existingEntity.setNarration(revisionEntity.getNarration());
		existingEntity.setCurriculum(revisionEntity.getCurriculum());
		existingEntity.setMedium(revisionEntity.getMedium());
		existingEntity.setCollectionGooruOid(revisionEntity.getCollectionGooruOid());
		existingEntity.setAssessmentGooruOid(revisionEntity.getAssessmentGooruOid());
		existingEntity.setSource(revisionEntity.getSource());
		existingEntity.setRequestPending(revisionEntity.getRequestPending());
		existingEntity.setTaxonomySet(revisionEntity.getTaxonomySet());
		existingEntity.setCollaborators(revisionEntity.getCollaborators());
		mergeSegments(existingEntity.getResourceSegments(), (revisionEntity.getResourceSegments()));
		mergeResource(revisionEntity, existingEntity);
		return existingEntity;
	}

	private void saveResourceInstances(List<ResourceInstance> revisionResourceInstances) {

		Iterator<ResourceInstance> revisionInstanceIterator = revisionResourceInstances.iterator();
		while (revisionInstanceIterator.hasNext()) {
			ResourceInstance revisionInstance = revisionInstanceIterator.next();
			ResourceInstance resourceInstance = (ResourceInstance) resourceRepository.get(ResourceInstance.class, revisionInstance.getResourceInstanceId());
			if (resourceInstance != null) {
				resourceInstance.setSegment(revisionInstance.getSegment());
				resourceInstance.setTitle(revisionInstance.getTitle());
				resourceInstance.setDescription(revisionInstance.getDescription());
				resourceInstance.setStart(revisionInstance.getStart());
				resourceInstance.setStop(revisionInstance.getStop());
				resourceInstance.setNarrative(revisionInstance.getNarrative());
				resourceInstance.setSequence(revisionInstance.getSequence());
				resourceRepository.save(resourceInstance);
			} else {
				resourceRepository.save(revisionInstance);
			}
		}
	}

	private void mergeSegments(Set<Segment> existingSegments, Set<Segment> revisionSegments) {

		SortedSet<Segment> removeExistssegments = new TreeSet<Segment>();

		for (Segment existSegment : existingSegments) {
			boolean segmentExists = false;
			for (Segment revisionSegment : revisionSegments) {
				if (revisionSegment.getSegmentId().equalsIgnoreCase(existSegment.getSegmentId())) {
					// update exists
					existSegment.setTitle(revisionSegment.getTitle());
					existSegment.setDescription(revisionSegment.getDescription());
					existSegment.setType(revisionSegment.getType());
					existSegment.setRenditionUrl(revisionSegment.getRenditionUrl());
					existSegment.setDuration(revisionSegment.getDuration());
					existSegment.setSequence(revisionSegment.getSequence());
					existSegment.setIsMeta(revisionSegment.getIsMeta());
					existSegment.setConcept(revisionSegment.getConcept());
					existSegment.setSegmentImage(revisionSegment.getSegmentImage());

					existSegment.setResourceInstances(revisionSegment.getResourceInstances());
					segmentExists = true;
					revisionSegments.remove(revisionSegment);
					break;
				}
			}
			if (!segmentExists) {
				removeExistssegments.add(existSegment);
			}
		}

		existingSegments.removeAll(removeExistssegments);
		learnguideRepository.removeAll(removeExistssegments);
		existingSegments.addAll(revisionSegments);

	}
}
