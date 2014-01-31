/////////////////////////////////////////////////////////////
// SegmentServiceImpl.java
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
package org.ednovo.gooru.domain.service.segment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.LogUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CollectionServiceUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.SegmentRepository;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("segmentService")
public class SegmentServiceImpl implements SegmentService {

	private static final String COLLECTION = "collection";

	@Autowired
	private SegmentRepository segmentRepository;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private LearnguideRepository classplanRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private SettingService settingService;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	private static final Logger logger = LoggerFactory.getLogger(SegmentServiceImpl.class);

	@Override
	public ResourceInstance findSegmentResource(String segmentId, String gooruResourceId) {
		return segmentRepository.findSegmentResource(segmentId, gooruResourceId);
	}

	@Override
	public List<Segment> getSegments(String gooruContentId) {
		return segmentRepository.getSegments(gooruContentId);
	}

	@Override
	public List<ResourceInstance> listResourceInstances(String gooruContentId, String type) {
		return segmentRepository.listResourceInstances(gooruContentId, type);
	}

	@Override
	public List<ResourceInstance> listSegmentResourceInstances(String segmentId) {
		return segmentRepository.listSegmentResourceInstances(segmentId);
	}

	@Override
	public ResourceInstance getFirstResourceInstanceOfResource(String gooruContentId) {
		return segmentRepository.getFirstResourceInstanceOfResource(gooruContentId);
	}

	@Override
	public void updateSegment(String gooruContentId, String segmentId, String title, String duration, String type, String rendition, String description, String concept, String uploadedImageSrc, User user, Learnguide collection) {

		Segment updateSegment = null;

		for (Segment segment : collection.getResourceSegments()) {
			if (segment.getSegmentId().equals(segmentId)) {
				segment.setDuration(duration);
				segment.setSegmentId(segmentId);
				segment.setTitle(title);
				segment.setType(type);
				segment.setRenditionUrl(rendition);
				segment.setDescription(description);
				/*
				 * segment.setSequence(getResourceService().getResourceSegmentsCount
				 * (gooruContentId) + 1);
				 */
				segment.setConcept(concept);
				updateSegment = segment;
				break;
			}
		}
		classplanRepository.save(collection);
		try {
			revisionHistoryService.createVersion(collection, "SegmentUpdate");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (logger.isInfoEnabled()) {
			logger.info(LogUtil.getActivityLogStream(COLLECTION, user.toString(), updateSegment.toString(), LogUtil.SEGMENT_EDIT, ""));
		}
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, "collection");
		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		if (uploadedImageSrc != null && !uploadedImageSrc.equals("")) {

			String uploadedMediaFolder = "/" + Constants.UPLOADED_MEDIA_FOLDER + "/";
			Segment segment = resourceService.getSegment(segmentId);
			if (uploadedImageSrc.contains(uploadedMediaFolder) && segment != null) {
				String folder = collection.getFolder() + Constants.SEGMENT_FOLDER;
				String repoPath = collection.getOrganization().getNfsStorageArea().getInternalPath();
				String fileName = StringUtils.substringAfterLast(uploadedImageSrc, uploadedMediaFolder);
				String fileExtension = StringUtils.substringAfterLast(uploadedImageSrc, ".");
				String srcPath = repoPath + folder + "/";

				uploadedImageSrc = repoPath + uploadedMediaFolder + fileName;
				try {
					uploadedImageSrc = GooruImageUtil.moveImage(uploadedImageSrc, srcPath, segmentId);
					segment.setSegmentImage(Constants.SEGMENT_FOLDER + "/" + segmentId + "." + fileExtension);
					segmentRepository.save(segment);
					logger.error("segment image Source:" + uploadedImageSrc + " destination folder: " + srcPath);
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("sourceFilePath", uploadedImageSrc);
					param.put("targetFolderPath", srcPath);
					param.put("dimensions", ResourceImageUtil.RESOURCE_THUMBNAIL_SIZES);
					param.put("resourceGooruOid", collection.getGooruOid());
					param.put("apiEndPoint", settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID));
					RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image", Method.POST.getName());
				} catch (Exception e) {
					logger.error("uploading image faild: " + e);
				}
			}
		}

	}

	@Override
	public Segment createSegment(String gooruContentId, String format, String title, String rendition, String duration, String description, String type, Learnguide collection, User user) {

		Segment segment = new Segment();
		segment.setSegmentId(UUID.randomUUID().toString());
		segment.setDescription(description);
		segment.setDuration(duration);
		segment.setRenditionUrl(rendition);
		segment.setTitle(title);
		segment.setType(type);
		segment.setSequence(collection.getResourceSegments().size() + 1);
		collection.getResourceSegments().add(segment);
		CollectionServiceUtil.resetSegmentsSequence(collection);
		classplanRepository.save(collection);
		try {
			revisionHistoryService.createVersion(collection, "SegmentCreate");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (logger.isInfoEnabled()) {
			logger.info(LogUtil.getActivityLogStream(COLLECTION, user.toString(), segment.toString(), LogUtil.SEGMENT_ADD, ""));
		}
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, "collection");

		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		return segment;
	}

}
