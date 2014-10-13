/////////////////////////////////////////////////////////////
// LearnguideServiceImpl.java
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
package org.ednovo.gooru.domain.service.classplan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.MailAsyncExecutor;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.UserContentRelationshipUtil;
import org.ednovo.gooru.application.util.formatter.CollectionFormatter;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.ResourceType.Type;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc.RELATIONSHIP;
import org.ednovo.gooru.core.application.util.CollectionServiceUtil;
import org.ednovo.gooru.core.application.util.ImageUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.application.util.formatter.CollectionFo;
import org.ednovo.gooru.core.application.util.formatter.ResourceFo;
import org.ednovo.gooru.core.application.util.formatter.SegmentFo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.SessionActivityRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("learnguideService")
public class LearnguideServiceImpl extends OperationAuthorizer implements LearnguideService,ParameterProperties,ConstantProperties {

	@Autowired
	private RedisService redisService;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	@javax.annotation.Resource(name = "classplanConstants")
	private Properties classPlanConstants;

	@Autowired
	private MailAsyncExecutor mailAsyncExecutor;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	private SessionActivityRepository sessionActivityRepository;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	private static final Logger logger = LoggerFactory.getLogger(LearnguideServiceImpl.class);

	@Override
	public String updateCollectionImage(String gooruContentId, String fileName) throws IOException {
		Learnguide collection = this.getLearnguideRepository().findByContent(gooruContentId);
		resourceImageUtil.moveFileAndSendMsgToGenerateThumbnails(collection, fileName, false);
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);
		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		try {
			revisionHistoryService.createVersion(collection, COLLECTION_IMAGE_CHANGE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return collection.getOrganization().getNfsStorageArea().getAreaPath() + collection.getFolder() + "/" + collection.getThumbnail();
	}

	@Override
	public void deleteCollectionBulk(String collectionGooruOIds) {
		List<Resource> collectionResources = resourceRepository.findAllResourcesByGooruOId(collectionGooruOIds);
		List<Resource> removeCollectionList = new ArrayList<Resource>();
		if (collectionResources.size() > 0) {
			String removeContentUIds = "";
			int count = 0;
			for (Resource resource : collectionResources) {
				if (count > 0) {
					removeContentUIds += ",";
				}
				if (resource.getResourceType().getName().equals(ResourceType.Type.CLASSPLAN.getType())) {
					removeContentUIds += resource.getGooruOid();
					removeCollectionList.add(resource);
					count++;
				}
			}
			if (removeCollectionList.size() > 0) {
				this.baseRepository.removeAll(removeCollectionList);
				indexProcessor.index(removeContentUIds, IndexProcessor.INDEX, COLLECTION);
			}
		}
	}

	@Override
	public JSONObject updateContentSharingPermission(User user, String contentGooruOid, String sharing, String type) throws Exception {
		Content content = this.getContentRepository().findByContentGooruId(contentGooruOid);
		JSONObject responseJSON = new JSONObject();
		if (content != null) {
			if (sharing.trim().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || sharing.trim().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || sharing.trim().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
				if (type.trim().equalsIgnoreCase(COLLECTION)) {
					Learnguide collection = this.getLearnguideRepository().findByContent(contentGooruOid);
					if (collection != null) {
						if (!hasUnrestrictedContentAccess(user) && !hasPublishAccess(user) && sharing.trim().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
							responseJSON.put(STATUS, STATUS_403).put(MESSAGE, "Do not have permission to change sharing for this " + type);
						} else {
							if (sharing != null && sharing.equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
								Set<Segment> resourceSegments = collection.getResourceSegments();
								for (Segment segment : resourceSegments) {
									Set<ResourceInstance> resourceInstances = segment.getResourceInstances();
									for (ResourceInstance resourceInstance : resourceInstances) {
										SessionContextSupport.putLogParameter("sharing-" + resourceInstance.getResource().getGooruOid(), resourceInstance.getResource().getSharing() + " to " + sharing);
										resourceInstance.getResource().setSharing(sharing);
										this.getResourceService().updateResourceInstanceMetaData(resourceInstance.getResource(), user);
									}
								}
								collection.setRequestPending(0);
								this.saveCollectionSharing(sharing, collection);
								for (Segment segment : resourceSegments) {
									Set<ResourceInstance> resourceInstances = segment.getResourceInstances();
									for (ResourceInstance resourceInstance : resourceInstances) {
										this.getResourceService().replaceDuplicatePrivateResourceWithPublicResource(resourceInstance.getResource());
									}
								}
							} else {
								this.saveCollectionSharing(sharing, collection);
							}
							collectionUtil.deleteCollectionFromCache(collection.getGooruOid(), COLLECTION);
							indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);
							responseJSON.put(STATUS, STATUS_200).put(MESSAGE, "successfully updated the sharing to " + sharing);
						}
					} else {
						responseJSON.put(STATUS, STATUS_404).put(MESSAGE, type + " dosen't exist");
					}
				}
			} else {
				responseJSON.put(STATUS, STATUS_404).put(MESSAGE, "invaild sharing type");
			}
		} else {
			responseJSON.put(STATUS, STATUS_404).put(MESSAGE, type + " dosen't exist");
		}

		return responseJSON;
	}

	@Override
	public JSONObject resetRequestPending(User user, String contentGooruOid, Integer pendingStatus) throws Exception {
		JSONObject responseJSON = new JSONObject();
		Learnguide collection = this.getLearnguideRepository().findByContent(contentGooruOid);
		if (collection != null) {
			if ((hasUnrestrictedContentAccess(user) || pendingStatus.equals(1))) {
				collection.setRequestPending(pendingStatus);
				responseJSON.put(STATUS, STATUS_200).put(MESSAGE, "updated collection request pending successfully!");
				this.getBaseRepository().save(collection);
				this.getCollectionUtil().deleteCollectionFromCache(collection.getGooruOid(), COLLECTION);
			} else {
				responseJSON.put(STATUS, STATUS_403).put(MESSAGE, "Do not have permission to change the publish request for this collection");
			}
		} else {
			responseJSON.put(STATUS, STATUS_404).put(MESSAGE, "collection dosen't exist");
		}
		return responseJSON;
	}


	@Override
	public JSONObject publishCollection(String action, User user, String contentGooruOid) throws Exception {
		JSONObject responseJSON = new JSONObject();
		if (action.equalsIgnoreCase(ACCEPT)) {
			return this.updateContentSharingPermission(user, contentGooruOid, Sharing.PUBLIC.getSharing(), COLLECTION);
		} else if (action.equalsIgnoreCase(DENY)) {
			responseJSON.put(STATUS, STATUS_200).put(MESSAGE, "publish the collection was cancelled successfully!");
		}
		this.resetRequestPending(user, contentGooruOid, 0);
		return responseJSON;
	}

	@Override
	public List<Object> findByUser(User user, Type type) {
		return this.getLearnguideRepository().findByUser(user, type);
	}

	@Override
	public List<Object> findAllLearnguides(Type type) {
		return this.getLearnguideRepository().findAllLearnguides(type);
	}

	@Override
	public List<Learnguide> findByResource(String gooruResourceContentId, String sharing) {
		return this.getLearnguideRepository().findByResource(gooruResourceContentId, sharing);
	}

	@Override
	public Learnguide findByContent(String gooruContentId) {
		return this.getLearnguideRepository().findByContent(gooruContentId);
	}

	@Override
	public List<User> findCollaborators(String gooruContentId) {
		return this.getLearnguideRepository().findCollaborators(gooruContentId, null);
	}

	@Override
	public List<Learnguide> findRecentLearnguideByUser(User user, String sharing) {
		return this.getLearnguideRepository().findRecentLearnguideByUser(user, sharing);
	}

	@Override
	public List<Learnguide> listLearnguides(Map<String, String> filters) {
		return this.getLearnguideRepository().listLearnguides(filters);
	}

	@Override
	public List<ResourceInstance> listCollectionResourceInstances(Map<String, String> filters) {
		return this.getLearnguideRepository().listCollectionResourceInstance(filters);
	}

	@Override
	public List<Learnguide> listPublishedCollections(String userGooruId) {
		return this.getLearnguideRepository().listPublishedCollections(userGooruId);
	}

	@Override
	public List<ResourceInstance> listCollectionResourceInstance(Map<String, String> filters) {
		return this.getLearnguideRepository().listCollectionResourceInstance(filters);
	}

	@Override
	public List<Resource> listCollectionResources(Map<String, String> filters) {
		return this.getLearnguideRepository().listCollectionResources(filters);
	}

	@Override
	public List<String> getResourceInstanceIds(String gooruContentId) {
		return this.getLearnguideRepository().getResourceInstanceIds(gooruContentId);
	}

	@Override
	public String findCollectionNameByGooruOid(String gooruOId) {
		return this.getLearnguideRepository().findCollectionNameByGooruOid(gooruOId);
	}

	@Override
	public List<Segment> listCollectionSegments(Map<String, String> filters) {
		return this.getLearnguideRepository().listCollectionSegments(filters);
	}

	@Override
	public Learnguide createNewCollection(String lesson, String grade, String[] taxonomyCode, User user, String type, Map<String, String> customFieldAndValueMap, String lessonObjectives) {

		Learnguide collection = new Learnguide();

		ContentType contentType = (ContentType) this.getBaseRepository().get(ContentType.class, ContentType.RESOURCE);
		License license = (License) this.getBaseRepository().get(License.class, License.OTHER);

		ResourceType resourceType = null;
		if (type.equalsIgnoreCase(CLASS_PLAN)) {
			resourceType = (ResourceType) this.getBaseRepository().get(ResourceType.class, ResourceType.Type.CLASSPLAN.getType());
		} else if (type.equalsIgnoreCase(CLASS_BOOK)) {
			resourceType = (ResourceType) this.getBaseRepository().get(ResourceType.class, ResourceType.Type.CLASSBOOK.getType());
		}

		collection.setType(type);
		collection.setLesson(lesson);
		collection.setTitle(lesson);
		collection.setResourceSegments(Learnguide.getSegmentsSkeleton());
		collection.setGrade(grade);
		collection.setContentType(contentType);
		collection.setGooruOid(UUID.randomUUID().toString());
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection.setSharing(Sharing.PRIVATE.getSharing());
		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setLicense(license);
		collection.setResourceType(resourceType);
		collection.setUrl("");
		collection.setDistinguish(Short.valueOf("0"));
		collection.setIsFeatured(0);
		collection.setNarration("");
		collection.setRequestPending(0);
		collection.setGoals(lessonObjectives);

		// Add taxonomy data
		List<Code> codeList = new ArrayList<Code>();
		if (taxonomyCode != null) {
			for (String codeId : taxonomyCode) {
				if (!codeId.equals("-")) {
					codeList.add((Code) this.getTaxonomyRepository().findCodeByTaxCode(codeId));
				}
			}
			if (codeList.size() != 0) {
				Set<Code> taxonomySet = new HashSet<Code>(codeList);

				collection.setTaxonomySet(taxonomySet);
			}
		}

		this.getLearnguideRepository().save(collection);

		// Save Resource Folder
		this.getLearnguideRepository().save(collection);

		this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist((Resource) collection);

		/*
		 * Commenting this line of code. Organization already saved in resource
		 * level in base class(saveOrUpdate)
		 */

		// s3ResourceApiHandler.updateOrganization(collection);

		UserContentRelationshipUtil.updateUserContentRelationship(collection, user, RELATIONSHIP.CREATE);


		final String cacheKey = "e.col.i-" + collection.getContentId().toString();
		getRedisService().putValue(cacheKey, JsonSerializer.serializeToJson(collection, true), RedisService.DEFAULT_PROFILE_EXP);
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);
		try {
			revisionHistoryService.createVersion(collection, NEW_COLLECTION);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return collection;
	}

	@Override
	public void deleteCollectionThumbnail(String gooruContentId) throws Exception {
		Learnguide collection = this.getLearnguideRepository().findByContent(gooruContentId);

		File collectionDir = new File(collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder());

		if (collectionDir.exists()) {
			String prevFileName = collection.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(collectionDir.getPath() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
					s3ResourceApiHandler.deleteResourceFile(collection, collection.getThumbnail());
				}
			}

			collection.setThumbnail(null);

			this.getLearnguideRepository().save(collection);

			// Remove the collection from cache
			collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);
			indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);
		}

	}

	@Override
	public String updateCollectionThumbnail(String gooruContentId, String fileName, String imageURL, Map<String, Object> formField) throws Exception {
		boolean isHasSlash = StringUtils.contains(fileName, '\\');

		if (isHasSlash) {
			fileName = StringUtils.substringAfterLast(fileName, Character.toString('\\'));
		}

		Learnguide collection = this.getLearnguideRepository().findByContent(gooruContentId);
		boolean buildThumbnail = false;

		if (imageURL != null && imageURL.length() > 0) {
			String resourceImageFile = collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder() + "/" + fileName;
			String prevFileName = collection.getThumbnail();
			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
				}
				s3ResourceApiHandler.deleteResourceFile(collection, collection.getThumbnail());
			}
			ImageUtil.downloadAndSaveFile(imageURL, resourceImageFile);
			collection.setThumbnail(fileName);
			buildThumbnail = true;
			logger.info("Thumbnail downloader:Resource " + collection.getGooruOid() + " didn't have image. downloading into " + resourceImageFile);
		} else {
			File classplanDir = new File(collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder());

			if (!classplanDir.exists()) {
				classplanDir.mkdirs();
			}

			Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

			byte[] fileData = null;

			// expecting only one file in the request right now
			for (byte[] fileContent : files.values()) {
				fileData = fileContent;
			}
			if (fileData != null && fileData.length > 0) {

				String prevFileName = collection.getThumbnail();

				if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
					File prevFile = new File(collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder() + "/" + prevFileName);
					if (prevFile.exists()) {
						prevFile.delete();
					}
				}

				File file = new File(collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder() + "/" + fileName);

				OutputStream out = new FileOutputStream(file);
				out.write(fileData);
				out.close();

				collection.setThumbnail(fileName);
				buildThumbnail = true;
			}
		}

		this.getLearnguideRepository().save(collection);
		if (buildThumbnail) {
			resourceImageUtil.sendMsgToGenerateThumbnails(collection);
		}
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		return collection.getFolder() + "/" + fileName;
	}

	@Override
	public Learnguide copyCollection(String gooruContentId, String collectionTitle, User user, boolean isClassplan, String segmentIds, String targetCollectionId) throws Exception {
		logger.info("Copy Classplan Step 0: " + System.currentTimeMillis());

		Learnguide sourceCollection = (Learnguide) this.getLearnguideRepository().findByContent(gooruContentId);
		String lesson = sourceCollection.getLesson();
		Learnguide targetCollection = null;

		if (targetCollectionId == null) {
			targetCollection = new Learnguide();
			targetCollection.setUser(user);
			targetCollection.setOrganization(user.getOrganization());
			targetCollection.setContentType(sourceCollection.getContentType());
			targetCollection.setCreatedOn(new Date(System.currentTimeMillis()));
			targetCollection.setLastModified(new Date(System.currentTimeMillis()));
			targetCollection.setGooruOid(UUID.randomUUID().toString());
			targetCollection.setSharing(Sharing.PRIVATE.getSharing());
			targetCollection.setType(sourceCollection.getType());
			targetCollection.setNarration(sourceCollection.getNarration());
			targetCollection.setAssessmentLink(sourceCollection.getAssessmentLink());
			targetCollection.setAssessmentGooruOid(sourceCollection.getAssessmentGooruOid());
			targetCollection.setCollectionLink(sourceCollection.getCollectionLink());
			targetCollection.setCollectionGooruOid(sourceCollection.getCollectionGooruOid());
			targetCollection.setVocabulary(sourceCollection.getVocabulary());
			targetCollection.setMedium(sourceCollection.getMedium());
			targetCollection.setDuration(targetCollection.getDuration());
			targetCollection.setTitle(sourceCollection.getTitle());
			targetCollection.setNotes(sourceCollection.getNotes());
			targetCollection.setGoals(sourceCollection.getGoals());
			targetCollection.setDistinguish(new Short("0"));
			targetCollection.setLicense(sourceCollection.getLicense());
			targetCollection.setResourceType(sourceCollection.getResourceType());
			targetCollection.setUrl(sourceCollection.getUrl());
			targetCollection.setCreator(sourceCollection.getCreator());
			targetCollection.setThumbnail(sourceCollection.getThumbnail());
			targetCollection.setRequestPending(0);

			String newLesson = "";
			if (collectionTitle != null) {
				newLesson = collectionTitle;
			} else {
				newLesson = "Copy - " + sourceCollection.getLesson();
			}

			if (newLesson.length() > 256) {
				newLesson = newLesson.substring(0, 255);
			}
			targetCollection.setLesson(newLesson);
			targetCollection.setTitle(newLesson);

			Set<Code> taxonomy = new HashSet<Code>();
			taxonomy.addAll(sourceCollection.getTaxonomySet());

			targetCollection.setTaxonomySet(taxonomy);
		} else {
			targetCollection = (Learnguide) this.getLearnguideRepository().findByContent(targetCollectionId);
		}
		if (sourceCollection.getResourceSegments() != null) {
			Set<Segment> segments = new TreeSet<Segment>();
			int sequence = 0;
			if ((targetCollectionId != null) && (targetCollection == null)) {
				throw new NotFoundException(generateErrorMessage(GL0056, TARGET_COLLECTION));
			}

			if (targetCollection.getResourceSegments() != null) {
				sequence = targetCollection.getResourceSegments().size();
			}
			for (Segment segment : sourceCollection.getResourceSegments()) {

				Segment newSegment = new Segment();
				if (segmentIds != null) {
					String[] segmentIdsArr = segmentIds.split(",");
					for (String segmentId : segmentIdsArr) {
						if (segmentId.equals(segment.getSegmentId())) {
							sequence++;
							newSegment = copySegments(segment, sequence);
						}
					}
				} else {
					newSegment = copySegments(segment, segment.getSequence());
				}
				if (newSegment.getSegmentId() != null) {
					segments.add(newSegment);
				}

			}
			if (targetCollectionId == null) {
				targetCollection.setResourceSegments(segments);
			} else {
				targetCollection.getResourceSegments().addAll(segments);
			}
		}

		// Step 3 - Retrieved the content classification object from database
		// and populate the new object
		this.getLearnguideRepository().save(targetCollection);

		// track copied collection source
		ContentAssociation contentAssociation = new ContentAssociation();
		contentAssociation.setAssociateContent(targetCollection);
		contentAssociation.setContent(sourceCollection);
		contentAssociation.setModifiedDate(new Date(System.currentTimeMillis()));
		contentAssociation.setTypeOf(Constants.COPIED_COLLECTION);
		contentAssociation.setUser(user);
		this.getLearnguideRepository().save(contentAssociation);

		for (Segment segment : targetCollection.getResourceSegments()) {
			if (segment.getResourceInstances() != null) {
				CollectionServiceUtil.resetInstancesSequence(segment);
				for (ResourceInstance resourceInstance : segment.getResourceInstances()) {
					resourceService.saveResourceInstance(resourceInstance);
				}
			}
		}

		logger.info("Copy Classplan Step 2: " + System.currentTimeMillis());

		this.getResourceManager().copyResourceRepository(sourceCollection, targetCollection);

		logger.info("Copy Classplan Step 3: " + System.currentTimeMillis());
		indexProcessor.index(targetCollection.getGooruOid(), IndexProcessor.INDEX, COLLECTION );

		this.s3ResourceApiHandler.uploadS3Resource(targetCollection);

		try {
			revisionHistoryService.createVersion(targetCollection, COPY_COLLECTION);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return targetCollection;
	}

	private Segment copySegments(Segment segment, Integer sequence) {
		Segment newSegment = new Segment();
		newSegment.setDescription(segment.getDescription());
		newSegment.setDuration(segment.getDuration());
		newSegment.setRenditionUrl(segment.getRenditionUrl());
		newSegment.setSequence(sequence);
		newSegment.setTitle(segment.getTitle());
		newSegment.setType(segment.getType());
		newSegment.setSegmentImage(segment.getSegmentImage());
		newSegment.setSegmentId(UUID.randomUUID().toString());
		if (segment.getResourceInstances() != null) {
			Set<ResourceInstance> resourceInstances = new TreeSet<ResourceInstance>();
			for (ResourceInstance resourceInstance : segment.getResourceInstances()) {
				ResourceInstance newResourceInstance = new ResourceInstance();
				newResourceInstance.setResourceInstanceId(UUID.randomUUID().toString());
				newResourceInstance.setSegment(newSegment);
				newResourceInstance.setResource(resourceInstance.getResource());
				newResourceInstance.setSequence(resourceInstance.getSequence());
				newResourceInstance.setStart(resourceInstance.getStart());
				newResourceInstance.setStop(resourceInstance.getStop());
				newResourceInstance.setNarrative(resourceInstance.getNarrative());
				newResourceInstance.setDescription(resourceInstance.getDescription());
				newResourceInstance.setTitle(resourceInstance.getTitle());
				resourceInstances.add(newResourceInstance);
			}
			newSegment.setResourceInstances(resourceInstances);
		}
		return newSegment;
	}

	@Override
	public void updateImages(String numberOfImages) throws Exception {
		Integer numberOfImagesToDownload = Integer.parseInt(numberOfImages);
		int processedCount = 0;
		List<Learnguide> classplanList = this.getLearnguideRepository().findAllClassplans();
		for (Learnguide learnguide : classplanList) {

			if (processedCount >= numberOfImagesToDownload) {
				break;
			}

			if (learnguide.getThumbnail() == null) {
				try {
					File classplanDir = new File(learnguide.getOrganization().getNfsStorageArea().getInternalPath() + learnguide.getFolder());

					if (!classplanDir.exists()) {
						classplanDir.mkdir();
					}

					String lesson = learnguide.getLesson();

					int success = downloadCollectionThumbnail(learnguide, lesson);
					if (success == 1) {
						processedCount++;
					} else {
						for (Code code : learnguide.getTaxonomySet()) {
							success = downloadCollectionThumbnail(learnguide, code.getLabel());
							if (success == 1) {
								processedCount++;
								break;
							}
						}
					}

				} catch (Exception e) {
					logger.warn("Thumbnail downloader:Collection " + learnguide.getGooruOid() + " had a problem" + ExceptionUtils.getFullStackTrace(e));
				}
			} else {
				// Identify and re-scale image
			}
		}
		logger.info("Thumbnail downloader:Finished processing");
	}

	private int downloadCollectionThumbnail(Learnguide learnguide, String query) {
		String imageURLInfo = ImageUtil.getThumbnailUrlByQuery(query, MEDIUM, WIDE);
		if (imageURLInfo != null && !imageURLInfo.isEmpty()) {
			String parts[] = imageURLInfo.split("\\|");
			if (parts.length < 3) {
				return 0;
			}
			String extension = "." + parts[0];
			String imageURL = parts[1];
			String imageThumbnailURL = parts[2];
			String fileName = learnguide.getGooruOid() + extension;

			String resourceImageFile = learnguide.getOrganization().getNfsStorageArea().getInternalPath() + learnguide.getFolder() + "/" + fileName;
			logger.info("Thumbnail downloader:Collection " + learnguide.getGooruOid() + " didn't have image. downloading from " + resourceImageFile);
			ImageUtil.downloadAndSaveFile(imageURL, resourceImageFile);

			String resourceImageThumbnailFile = learnguide.getOrganization().getNfsStorageArea().getInternalPath() + learnguide.getFolder() + "/" + "thumb_" + fileName;
			ImageUtil.downloadAndSaveFile(imageThumbnailURL, resourceImageThumbnailFile);
			learnguide.setThumbnail(fileName);

			resourceImageUtil.sendMsgToGenerateThumbnails(learnguide);

			this.getLearnguideRepository().save(learnguide);
			indexProcessor.index(learnguide.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

			return 1;
		}
		return 0;
	}

	private void saveCollectionSharing(String sharing, Learnguide collection) {
		collection.setSharing(sharing);
		this.getBaseRepository().save(collection);
	}

	@Override
	public JSONObject getContentSessionActivity(String gooruContentId, String gooruUid) throws JSONException {
		JSONObject sessionActivityJson = new JSONObject();
		SessionActivityItem sessionActivityItem = this.getSessionActivityRepository().getContentSessionActivityItem(gooruContentId, gooruUid, SessionActivityType.Status.OPEN.getStatus());
		if (sessionActivityItem != null && sessionActivityItem.getSubContentUid() != null) {
			sessionActivityJson.put(LAST_PLAYED_RESOURCE_INSTANCE_ID, sessionActivityItem.getSubContentUid());
			List<SessionActivityItem> sessionActivityItemList = this.getSessionActivityRepository().getSubContentSessionActivityItemList(gooruContentId, gooruUid, SessionActivityType.Status.OPEN.getStatus());
			int studiedResourceCount = sessionActivityItemList != null ? sessionActivityItemList.size() : 0;
			sessionActivityJson.put(STUDIED_RESOURCE_COUNT, studiedResourceCount);
		}
		return sessionActivityJson;
	}

	public LearnguideRepository getLearnguideRepository() {
		return learnguideRepository;
	}

	public void setLearnguideRepository(LearnguideRepository learnguideRepository) {
		this.learnguideRepository = learnguideRepository;
	}

	public Properties getClassPlanConstants() {
		return classPlanConstants;
	}

	public void setClassPlanConstants(Properties classPlanConstants) {
		this.classPlanConstants = classPlanConstants;
	}

	public void setContentRepository(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public void setBaseRepository(BaseRepository baseRepository) {
		this.baseRepository = baseRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public void setTaxonomyRepository(TaxonomyRespository taxonomyRepository) {
		this.taxonomyRepository = taxonomyRepository;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public CollectionUtil getCollectionUtil() {
		return collectionUtil;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	@Override
	public void saveCollection(Learnguide collection) {
		baseRepository.save(collection);

	}

	public SessionActivityRepository getSessionActivityRepository() {
		return sessionActivityRepository;
	}

	@Override
	public List<Learnguide> getCollectionsOfResource(String resourceId) {
		return this.getLearnguideRepository().findAllCollectionByResourceID(resourceId);
	}

	@Override
	public List<CollectionFo> getUserCollectionInfo(String gooruUId, Map<String, String> filters) {
		List<CollectionFo> collections = new ArrayList<CollectionFo>();
		List<Learnguide> learnguides = getLearnguideRepository().getUserCollectionInfo(gooruUId, filters);

		if (learnguides != null && (Boolean.parseBoolean(filters.get(SKP_SKELETON_SEG)))) {
			for (Learnguide collection : learnguides) {
				CollectionFo collectionFo = new CollectionFo();
				collectionFo.setGooruOid(collection.getGooruOid());
				collectionFo.setTitle(collection.getLesson());
				collectionFo.setSegments(CollectionFormatter.getInstance().getSegments(collection.getResourceSegments(), false, collection, null, 1, null, true));
				collections.add(collectionFo);
			}
		}
		return collections;
	}

	@Override
	public Map<String, Integer> getCollectionPageCount(List<SegmentFo> segments) {
		int pageCount = 0;
		int quoteResourceCount = 0;
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (SegmentFo segment : segments) {
			for (ResourceFo resource : segment.getResources()) {
				if (resource.getType().equalsIgnoreCase(EXAM_PDF) || resource.getType().equalsIgnoreCase(PPT_PPTX) || resource.getType().equalsIgnoreCase(TXT_BK_SCRIBD) || resource.getType().equalsIgnoreCase(HANDOUTS)) {
					ResourceInfo resources = resourceService.getResourcePageCount(resource.getId());
					if ((resources != null) && (resources.getNumOfPages() != null)) {
						pageCount = pageCount + resources.getNumOfPages();
					}

				} else {
					pageCount++;
				}
				if (!StringUtils.isEmpty(resource.getRecordSource())) {
					if (resource.getRecordSource().equalsIgnoreCase(Resource.RecordSource.QUOTED.getRecordSource())) {
						quoteResourceCount++;
					}
				}
			}
		}

		countMap.put(COLLECTION_PAGE_COUNT , pageCount);
		countMap.put(QUOTED_RESOURCE_COUNT, quoteResourceCount);
		return countMap;
	}

	@Override
	public List<String> sendRequestForGetCollaborators(String gooruUId, String searchText) {
		return this.getLearnguideRepository().findAllCollaboratorByResourceID(gooruUId, searchText);
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public MailAsyncExecutor getMailAsyncExecutor() {
		return mailAsyncExecutor;
	}

}
