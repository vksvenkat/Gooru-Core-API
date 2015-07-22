/////////////////////////////////////////////////////////////
// CollectionServiceImpl.java
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
package org.ednovo.gooru.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.eventlogs.CollectionEventLog;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class CollectionServiceImpl extends ScollectionServiceImpl implements CollectionService {

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private CollectionEventLog collectionEventLog;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private StorageRepository storageRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private PartyService partyService;

	@Autowired
	private IndexHandler indexHandler;

	@Autowired
	private MongoQuestionsService mongoQuestionsService;

	final int zero = 0;

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionServiceImpl.class);

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(final String collectionId, final String data, final User user, final String mediaFileName, final String sourceReference) throws Exception {
		ActionResponseDTO<CollectionItem> response = null;
		final Collection collection = collectionRepository.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION), GL0056);
		}
		final AssessmentQuestion question = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		question.setSourceReference(sourceReference);
		question.setSharing(collection.getSharing());
		final ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(question, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel(), collection, null, null, user);
			boolean updateAssetInS3 = false;
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, question, ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					if (!(ResourceImageUtil.getYoutubeVideoId(questionImage) != null || questionImage.contains(YOUTUBE_URL))) {
						updateAssetInS3 = true;
					}
				}
			}
			/*
			 * The new generation questions have answers as images as well. We
			 * need to store these assets. Note that since they are going into
			 * Mongo, we are not maintaining the association in MySql. We shall
			 * just stash them and then make sure that they go till S3
			 */
			if (question.isQuestionNewGen()) {
				List<String> answerAssets = question.getMediaFiles();
				if (answerAssets != null && answerAssets.size() > 0) {
					updateAssetInS3 = true;
					for (String answerAsset : answerAssets) {
						this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), answerAsset, question, null);
					}
				}
			}

			if (updateAssetInS3) {
				try {
					this.getAsyncExecutor().updateResourceFileInS3(response.getModel().getResource().getFolder(), response.getModel().getResource().getOrganization().getNfsStorageArea().getInternalPath(), response.getModel().getResource().getGooruOid(), UserGroupSupport.getSessionToken());
				} catch (Exception e) {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error(e.getMessage());
					}
				}

			}

			response.getModel().setStandards(this.getStandards(responseDTO.getModel().getTaxonomySet(), false, null));
			if (question.isQuestionNewGen()) {
				mongoQuestionsService.createQuestion(question.getGooruOid(), data);
			}
		}
		try {
			this.getCollectionEventLog().getEventLogs(response.getModel(), false, true, user, response.getModel().getCollection().getCollectionType());
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
		}
		return response;

	}

	private ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(final CollectionItem collectionItem, final String data, final List<Integer> deleteAssets, final User user, final String mediaFileName) throws Exception {
		final AssessmentQuestion newQuestion = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		final Errors errors = validateUpdateCollectionItem(collectionItem);
		if (!errors.hasErrors()) {
			final AssessmentQuestion question = getAssessmentService().getQuestion(collectionItem.getContent().getGooruOid());
			if (question != null) {
				AssessmentQuestion assessmentQuestion = assessmentService.updateQuestion(newQuestion, deleteAssets, question.getGooruOid(), true, true).getModel();
				this.getResourceService().saveOrUpdateResourceTaxonomy(assessmentQuestion, newQuestion.getTaxonomySet());
				if (assessmentQuestion != null) {
					if (mediaFileName != null && mediaFileName.length() > 0) {
						String questionImage = this.assessmentService.updateQuizQuestionImage(assessmentQuestion.getGooruOid(), mediaFileName, question, ASSET_QUESTION);
						if (questionImage != null && questionImage.length() > 0) {
							if (ResourceImageUtil.getYoutubeVideoId(questionImage) != null || questionImage.contains(YOUTUBE_URL)) {
								assessmentQuestion = this.assessmentService.updateQuestionVideoAssest(assessmentQuestion.getGooruOid(), questionImage);
							} else {
								assessmentQuestion = this.assessmentService.updateQuestionAssest(assessmentQuestion.getGooruOid(), StringUtils.substringAfterLast(questionImage, "/"));
							}
						}
					}
					if (assessmentQuestion.isQuestionNewGen()) {
						List<String> mediaFilesToAdd = newQuestion.getMediaFiles();
						if (mediaFilesToAdd != null && mediaFilesToAdd.size() > 0) {
							for (String mediaFileToAdd : mediaFilesToAdd) {
								assessmentService.updateQuizQuestionImage(assessmentQuestion.getGooruOid(), mediaFileToAdd, assessmentQuestion, null);
							}
						}
					}
					// collectionItem.setQuestionInfo(assessmentQuestion);

					collectionItem.setStandards(this.getStandards(assessmentQuestion.getTaxonomySet(), false, null));
				}
				// Update the question in mongo now that transaction is almost
				// done
				mongoQuestionsService.updateQuestion(collectionItem.getContent().getGooruOid(), data);

				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
			}

		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, QUESTION), GL0056);
		}
		this.getCollectionEventLog().getEventLogs(collectionItem, false, false, user, false, true, data);
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(final String collectionItemId, final String data, final List<Integer> deleteAssets, final User user, final String mediaFileName) throws Exception {
		final CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM), GL0056);
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(final String collectionId, final String resourceId, final String data, final List<Integer> deleteAssets, final User user, final String mediaFileName) throws Exception {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemByResourceOid(collectionId, resourceId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM), GL0056);
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteQuestionWithCollectionItem(final String collectionId, final String resourceId) {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemByResourceOid(collectionId, resourceId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM), GL0056);
		}
		/*
		 * This method is just deleting the collection item entry but is not
		 * actually deleting the resource itself. Hence the question is still
		 * present and as such, we are NOT deleting it from Mongo db as well
		 */
		this.getCollectionRepository().remove(collectionItem);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> moveCollectionToFolder(final String sourceId, final String targetId, final User user) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = null;
		final Collection source = collectionRepository.getCollectionByGooruOid(sourceId, null);
		if (source == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION), GL0056);
		}
		if (source.getCollectionType().equalsIgnoreCase(FOLDER)) {
			throw new BadRequestException(generateErrorMessage(GL0007, _COLLECTION), GL0007);
		}
		final CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(source);
		final CollectionItem sourceCollectionItem = this.getCollectionRepository().findCollectionItemByGooruOid(sourceId, user.getPartyUid(), CLASSPAGE);
		if (sourceCollectionItem != null && sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		collectionItem.setEstimatedTime(sourceCollectionItem.getEstimatedTime());

		if (!user.getPartyUid().equalsIgnoreCase(collectionItem.getCollection().getUser().getPartyUid())) {
			final UserContentAssoc userContentAssocs = this.getCollaboratorRepository().findCollaboratorById(sourceId, user.getPartyUid());
			if (userContentAssocs != null) {
				collectionItem.setItemType(COLLABORATOR);
			}
		}

		if (targetId != null) {
			final Collection target = collectionRepository.getCollectionByGooruOid(targetId, null);
			if (target != null && !source.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) && !collectionItem.getItemType().equalsIgnoreCase(COLLABORATOR)) {
				target.setSharing(source.getSharing());
				this.getCollectionRepository().save(target);
			}
			responseDTO = this.createCollectionItem(sourceId, targetId, collectionItem, user, CollectionType.FOLDER.getCollectionType(), false);
		} else {
			responseDTO = this.createCollectionItem(sourceId, null, collectionItem, user, CollectionType.SHElf.getCollectionType(), false);
		}
		if (sourceCollectionItem != null) {
			deleteCollectionItem(sourceCollectionItem.getCollectionItemId(), user, true);
			updateFolderSharing(sourceCollectionItem.getCollection().getGooruOid());
			resetFolderVisibility(sourceCollectionItem.getCollection().getGooruOid(), user.getPartyUid());
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");

		try {
			this.getCollectionEventLog().getEventLogs(responseDTO.getModel(), true, false, user, responseDTO.getModel().getCollection().getCollectionType(), sourceCollectionItem);
		} catch (JSONException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
		}
		return responseDTO;
	}

	@Override
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(final String collectionId, final AssessmentQuestion assessmentQuestion, final User user, final String mediaFileName) throws Exception {

		ActionResponseDTO<CollectionItem> response = null;
		final Collection collection = collectionRepository.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION), GL0056);
		}
		/*
		 * NOTE: Not able to find where this API is called from using call
		 * hierarchy. So, instead of enabling it to handle new question types,
		 * trying to throw here. A bit drastic step, but need to understand if
		 * this is really dead code
		 */
		if (assessmentQuestion.isQuestionNewGen()) {
			LOGGER.error("createQuestionWithCollectionItem: This implementation does not handle new questions");
			throw new NotImplementedException("New question types are not handled");
		}
		final ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(assessmentQuestion, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel().getGooruOid(), collectionId, new CollectionItem(), user, CollectionType.COLLECTION.getCollectionType(), true);
			if (mediaFileName != null && mediaFileName.length() > 0) {
				final String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, assessmentQuestion, ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					// response.getModel().setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(),
					// StringUtils.substringAfterLast(questionImage, "/")));
				}
			}
		}
		return response;

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<CollectionItem> createCollectionItems(final List<String> collectionsIds, final String resourceId, final User user) throws Exception {
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(resourceId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION), GL0056);
		}
		List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
		for (final String collectionId : collectionsIds) {
			final Collection classPage = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
			if (classPage != null) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setCollection(classPage);
				// collectionItem.setResource(collection);
				collectionItem.setItemType(ADDED);
				collectionItem.setAssociatedUser(user);
				collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
				int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
				collectionItem.setItemSequence(sequence);
				this.getResourceRepository().save(collectionItem);
				collectionItems.add(collectionItem);
				SessionContextSupport.putLogParameter(EVENT_NAME, CLASSPAGE_CREATE_COLLECTION_TASK_ITEM);
				SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());
				SessionContextSupport.putLogParameter(GOORU_OID, classPage.getGooruOid());
				SessionContextSupport.putLogParameter(COLLECTION_ID, classPage.getGooruOid());
				SessionContextSupport.putLogParameter(RESOURCE_ID, collection.getGooruOid());
				SessionContextSupport.putLogParameter(COLLECTION_TYPE, collectionItem.getCollection().getCollectionType());
			}

		}
		return collectionItems;

	}

	@Override
	public List<Map<String, Object>> getMyShelf(String gooruUid, Integer limit, Integer offset, final String sharing, final String collectionType, Integer itemLimit, boolean fetchChildItem, final String topLevelCollectionType, final String orderBy, String excludeType) {
		if (!BaseUtil.isUuid(gooruUid)) {
			final User user = this.getUserRepository().getUserByUserName(gooruUid, true);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Map<String, Object>> folderList = this.getCollectionRepository().getFolder(null, gooruUid, limit, offset, sharing, topLevelCollectionType != null ? topLevelCollectionType : collectionType, fetchChildItem, orderBy, excludeType);
		int count = 0;
		List<Map<String, Object>> folderItems = new ArrayList<Map<String, Object>>();
		if (folderList != null && folderList.size() > 0) {
			for (Map<String, Object> collection : folderList) {
				final String typeName = String.valueOf(collection.get(TYPE));
				final String collectionGooruOid = String.valueOf(collection.get(GOORU_OID));
				Object imagePath = collection.get(IMAGE_PATH);
				if (imagePath != null) {
					final Map<String, Object> thumbnails = new HashMap<String, Object>();
					StringBuilder url = new StringBuilder(ConfigProperties.getBaseRepoUrl());
					url.append(imagePath);
					thumbnails.put(URL, url.toString());
					collection.put(THUMBNAILS, thumbnails);
				}
				if (fetchChildItem) {
					if (count == 0) {
						if (typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL)) {
							collection.put(COLLECTION_ITEMS, getCollectionItem(String.valueOf(collection.get(GOORU_OID)), sharing, String.valueOf(collection.get(SHARING)), itemLimit, fetchChildItem, orderBy, excludeType));
						} else if (typeName.equalsIgnoreCase(FOLDER)) {
							collection.put(COLLECTION_ITEMS, getFolderItem(collectionGooruOid, itemLimit, 0, sharing, collectionType, orderBy, itemLimit, fetchChildItem, null, excludeType));
						}
					}
				} else {
					if (typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL)) {
						collection.put(COLLECTION_ITEMS, getCollectionItem(String.valueOf(collection.get(GOORU_OID)), sharing, String.valueOf(collection.get(SHARING)), itemLimit, fetchChildItem, orderBy, excludeType));
					} else if (typeName.equalsIgnoreCase(FOLDER)) {
						collection.put(COLLECTION_ITEMS, getFolderItem(collectionGooruOid, itemLimit, 0, sharing, collectionType, orderBy, itemLimit, fetchChildItem, null, excludeType));
					}
				}
				collection.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(collection.get(GOORU_OID)), sharing, collectionType, excludeType));

				String contentSettings = String.valueOf(collection.get(DATA));
				if (contentSettings != null) {
					collection.put(SETTINGS, JsonDeserializer.deserialize(contentSettings, new TypeReference<Map<String, String>>() {
					}));
				}
				folderItems.add(collection);
				count++;
			}
		}
		return folderItems;
	}

	public List<Map<String, Object>> getCollectionItem(final String gooruOid, final String sharing, final String collectionType, Integer itemLimit, final boolean fetchChildItem, final String orderBy, final String excludeType) {
		List<Map<String, Object>> collectionItems = this.getCollectionRepository().getCollectionItem(gooruOid, 4, 0, sharing, orderBy, collectionType, fetchChildItem, ASC, false, excludeType);
		List<Map<String, Object>> folderItems = new ArrayList<Map<String, Object>>();
		if (collectionItems != null && collectionItems.size() > 0) {
			for (Map<String, Object> collectionItem : collectionItems) {
				final String typeName = String.valueOf(collectionItem.get(TYPE));
				final String resourceGooruOid = String.valueOf(collectionItem.get(GOORU_OID));
				final Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					String url = String.valueOf(collectionItem.get(URL));
					if (url != null) {
						thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(url) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(url) + "/1.jpg");
						collectionItem.put(THUMBNAILS, thumbnails);
					}
				} else {
					Object thumbnail = collectionItem.get(THUMBNAIL);
					if (thumbnail != null) {
						StringBuilder url = new StringBuilder(ConfigProperties.getBaseRepoUrl());
						url.append(collectionItem.get(FOLDER));
						url.append(thumbnail);
						thumbnails.put(URL, url.toString());
						collectionItem.put(THUMBNAILS, thumbnails);
					}
				}

				Object resourceFormatValue = collectionItem.get(VALUE);
				if (resourceFormatValue != null) {
					final Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, resourceFormatValue);
					resourceFormat.put(DISPLAY_NAME, collectionItem.get(DISPLAY_NAME));
					collectionItem.put(RESOURCEFORMAT, resourceFormat);
				}
				Map<String, Object> summary = new HashMap<String, Object>();
				Object average = collectionItem.get(AVERAGE);
				Object count = collectionItem.get(COUNT);
				summary.put(AVERAGE, average != null ? average : 0);
				summary.put(COUNT, count != null ? count : 0);
				collectionItem.put(RATINGS, summary);
				if (!fetchChildItem) {
					// need to set meta data
				}
				Object attribution = collectionItem.get(ATTRIBUTION);
				if (attribution != null) {
					final Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, attribution);
					resourceSource.put(DOMAIN_NAME, collectionItem.get(DOMAIN_NAME));
					collectionItem.put(RESOURCESOURCE, resourceSource);
				}

				final Resource resource = this.getResourceService().setContentProvider(resourceGooruOid);
				if (resource != null) {
					if (resource.getPublisher() != null && resource.getPublisher().size() > 0) {
						collectionItem.put(PUBLISHER, resource.getPublisher());
					}
					if (resource.getAggregator() != null && resource.getAggregator().size() > 0) {
						collectionItem.put(AGGREGATOR, resource.getAggregator());
					}
					if (resource.getHost() != null && resource.getHost().size() > 0) {
						collectionItem.put("host", resource.getHost());
					}
				}
				folderItems.add(collectionItem);
			}

		}
		return folderItems;
	}

	private Map<String, Object> setFolderItem(final Map<String, Object> folderItem, String sharing, String collectionType, Integer itemLimit, boolean fetchChildItem, String orderBy, String excludeType) {
		final String typeName = String.valueOf(folderItem.get(TYPE));
		final String collectionGooruOid = String.valueOf(folderItem.get(GOORU_OID));
		Object thumbnail = folderItem.get(IMAGE_PATH);
		if (thumbnail != null) {
			final Map<String, Object> thumbnails = new HashMap<String, Object>();
			StringBuilder url = new StringBuilder(ConfigProperties.getBaseRepoUrl());
			url.append(thumbnail);
			thumbnails.put(URL, url.toString());
			folderItem.put(THUMBNAILS, thumbnails);
		}

		if (fetchChildItem) {
			if (typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL)) {
				folderItem.put(COLLECTION_ITEMS, getCollectionItem(collectionGooruOid, sharing, collectionType, 4, fetchChildItem, orderBy, excludeType));
				folderItem.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(collectionGooruOid, sharing, collectionType, excludeType));
			} else if (typeName.equalsIgnoreCase(FOLDER)) {
				folderItem.put(COLLECTION_ITEMS, getFolderItem(collectionGooruOid, itemLimit, 0, sharing, collectionType, orderBy, itemLimit, fetchChildItem, null, excludeType));
				folderItem.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(collectionGooruOid, sharing, collectionType, excludeType));
			}
		}

		Object data = folderItem.get(DATA);
		if (data != null) {
			folderItem.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, String>>() {
			}));
			folderItem.remove(DATA);
		}
		return folderItem;
	}

	@Override
	public List<Map<String, Object>> getFolderItem(final String gooruOid, final Integer limit, Integer offset, final String sharing, final String collectionType, final String orderBy, final Integer itemLimit, final boolean fetchChildItem, final String sortOrder, final String excludeType) {
		List<Map<String, Object>> folderItems = this.getCollectionRepository().getFolder(gooruOid, null, limit, offset, sharing, collectionType, fetchChildItem, orderBy, excludeType);
		if (folderItems == null || folderItems.size() == 0) {
			folderItems = this.getCollectionRepository().getCollectionItem(gooruOid, 4, 0, sharing, orderBy, null, fetchChildItem, ASC, false, excludeType);
		}
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		if (folderItems != null) {
			for (Map<String, Object> folderItem : folderItems) {
				folderList.add(setFolderItem(folderItem, sharing, collectionType, itemLimit, fetchChildItem, orderBy, excludeType));
			}
		}
		return folderList;
	}

	@Override
	public Map<String, Object> getFolderList(final Integer limit, final Integer offset, final String gooruOid, final String title, final String username) {
		String gooruUid = null;
		if (username != null) {
			final User user = this.getUserService().getUserByUserName(username);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		final List<Object[]> result = this.getCollectionRepository().getFolderList(limit, offset, gooruOid, title, gooruUid);
		final List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		final Map<String, Object> content = new HashMap<String, Object>();
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				final Map<String, Object> folder = new HashMap<String, Object>();
				folder.put(GOORU_OID, object[0]);
				folder.put(TITLE, object[1]);
				folder.put(USER_NAME, object[2]);
				folder.put(CREATED_ON, object[3]);
				folder.put(LAST_MODIFIED, object[4]);
				folder.put(SHARING, object[5]);
				folderList.add(folder);
			}
			content.put(SEARCH_RESULT, folderList);
			content.put(COUNT, this.getCollectionRepository().getFolderListCount(gooruOid, title, gooruUid));
		}
		return content;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Code> getCollectionStandards(final Integer codeId, final String query, final Integer limit, final Integer offset, final User user) {
		final SearchResults<Code> result = new SearchResults<Code>();
		final List<Object[]> list = this.getTaxonomyRespository().getCollectionStandards(codeId, query, limit, offset);
		final List<Code> codeList = new ArrayList<Code>();
		for (Object[] object : list) {
			Code code = new Code();
			code.setCode((String) object[0]);
			code.setCodeId((Integer) object[1]);
			code.setLabel(((String) object[2]));
			code.setCodeUid((String) object[3]);
			codeList.add(code);
		}
		result.setSearchResults(codeList);
		return result;
	}

	private Errors validateUpdateCollectionItem(final CollectionItem collectionItem) throws Exception {
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		rejectIfNull(errors, collectionItem, COLLECTION_ITEM, GL0056, generateErrorMessage(GL0056, COLLECTION_ITEM));
		return errors;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Collection> getCollections(final Integer offset, final Integer limit, final User user, final String publishStatus) {
		final List<Collection> collections = this.getCollectionRepository().getCollectionsList(user, limit, offset,Constants.PUBLISH_STATUS.get(publishStatus));
		final SearchResults<Collection> result = new SearchResults<Collection>();
		result.setSearchResults(collections);
		result.setTotalHitCount(this.getCollectionRepository().getCollectionCount(Constants.PUBLISH_STATUS.get(publishStatus)));
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Collection> updateCollectionForPublish(final List<Map<String, String>> collection, final User user) throws Exception {

		final List<String> gooruOids = new ArrayList<String>();
		List<Collection> collections = new ArrayList<Collection>();
		final StringBuffer collectionIds = new StringBuffer();
		for (Map<String, String> map : collection) {
			gooruOids.add(map.get(GOORU_OID));
		}
		if (gooruOids.toString().trim().length() > 0) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (final Collection scollection : collections) {
					getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + scollection.getUser().getPartyUid() + "*");
					// TO DO
					if (scollection.getPublishStatusId() != null) {
						scollection.setPublishStatusId(Constants.PUBLISH_REVIEWED_STATUS_ID);
						collectionIds.append(scollection.getGooruOid());
						if (!scollection.getSharing().equalsIgnoreCase(PUBLIC)) {
							final UserSummary userSummary = this.getUserRepository().getSummaryByUid(scollection.getUser().getPartyUid());
							if (userSummary.getCollections() == null || userSummary.getCollections() == 0) {
								final PartyCustomField partyCustomField = new PartyCustomField(USER_META, SHOW_PROFILE_PAGE, TRUE);
								this.getPartyService().updatePartyCustomField(scollection.getUser().getPartyUid(), partyCustomField, scollection.getUser());
							}
							if (userSummary.getGooruUid() == null) {
								userSummary.setGooruUid(scollection.getUser().getPartyUid());
							}
							userSummary.setCollections((userSummary.getCollections() != null ? userSummary.getCollections() : 0) + 1);
							this.getUserRepository().save(userSummary);
						}
						scollection.setSharing(PUBLIC);
						resetFolderVisibility(scollection.getGooruOid(), scollection.getUser().getPartyUid());
						updateResourceSharing(PUBLIC, scollection);
						try {
							final String mailId = scollection.getUser().getIdentities().iterator().next().getExternalId();
							this.getMailHandler().sendAdminPortalMail(PUBLISH_COLLECTION, mailId, scollection.getUser().getFirstName(), scollection.getTitle(), scollection.getGooruOid());
						} catch (Exception e) {

						}
						if (collectionIds.toString().trim().length() > 0) {
							collectionIds.append(",");
						} else {
							throw new BadRequestException(generateErrorMessage(GL0089));
						}
					}

				}
			}
			this.getCollectionRepository().saveAll(collections);
			if (collectionIds.toString().trim().length() > 0) {
				indexHandler.setReIndexRequest(collectionIds.toString(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);
			}
		}
		return collections;

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Collection> updateCollectionForReject(final List<Map<String, String>> collection, final User user) throws Exception {

		final List<String> gooruOids = new ArrayList<String>();
		List<Collection> collections = new ArrayList<Collection>();
		final StringBuffer collectionIds = new StringBuffer();
		for (final Map<String, String> map : collection) {
			gooruOids.add(map.get(GOORU_OID));
		}
		if (gooruOids.toString().trim().length() > zero) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (final Collection scollection : collections) {
					// TO DO
					if (scollection.getPublishStatusId() != null) {
						scollection.setPublishStatusId(null);
						if (scollection.getSharing().equalsIgnoreCase(PUBLIC)) {
							UserSummary userSummary = this.getUserRepository().getSummaryByUid(scollection.getUser().getPartyUid());
							if (userSummary.getGooruUid() != null) {
								userSummary.setCollections(userSummary.getCollections() <= 0 ? 0 : (userSummary.getCollections() - 1));
								this.getUserRepository().save(userSummary);
							}
						}
						collectionIds.append(scollection.getGooruOid());

						if (collectionIds.toString().trim().length() > 0) {
							collectionIds.append(",");
						}
						scollection.setSharing(ANYONE_WITH_LINK);
						resetFolderVisibility(scollection.getGooruOid(), scollection.getUser().getPartyUid());
						updateResourceSharing(ANYONE_WITH_LINK, scollection);
					} else {
						throw new BadRequestException(generateErrorMessage(GL0091));

					}
				}
			}
			this.getCollectionRepository().saveAll(collections);
			if (collectionIds.toString().trim().length() > 0) {
				indexHandler.setReIndexRequest(collectionIds.toString(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);
			}
		}
		return collections;

	}

	@Override
	public String getFolderItemsWithCache(final String gooruOid, final Integer limit, final Integer offset, final String sharing, final String collectionType, final String orderBy, final Integer itemLimit, final boolean fetchChildItem, final boolean clearCache, final User user,
			final String excludeType) {
		Map<String, Object> content = null;
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		String data = null;
		if (collection != null) {
			final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + HYPHEN + gooruOid + HYPHEN + limit + HYPHEN + offset + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + orderBy + HYPHEN + itemLimit + HYPHEN + excludeType + HYPHEN + fetchChildItem;
			if (!clearCache) {
				data = redisService.getValue(cacheKey);
			}
			if (data == null) {
				content = new HashMap<String, Object>();
				content.put(SEARCH_RESULT, getFolderItem(gooruOid, limit, offset, sharing, collectionType, orderBy, itemLimit, fetchChildItem, collection.getCollectionType().equalsIgnoreCase(COLLECTION) ? ASC : DESC, excludeType));
				content.put(COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType, excludeType));
				if (!fetchChildItem && (collectionType == null || (collectionType != null && collectionType.equalsIgnoreCase(COLLECTION)))) {
					content.put(COLLECTION_COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType != null ? collectionType : COLLECTION, excludeType));
				}
				data = SerializerUtil.serializeToJson(content, TOC_EXCLUDES, true, true);
				redisService.putValue(cacheKey, data, fetchChildItem ? Constants.LIBRARY_CACHE_EXPIRY_TIME_IN_SEC : Constants.CACHE_EXPIRY_TIME_IN_SEC);
			}
		}
		return data;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Boolean resourceCopiedFrom(final String gooruOid, final String gooruUid) {
		Resource resource = collectionRepository.findResourceCopiedFrom(gooruOid, gooruUid);
		return resource != null ? true : false;
	}

	public StorageRepository getStorageRepository() {
		return storageRepository;
	}

	public TaxonomyRespository getTaxonomyRespository() {
		return taxonomyRespository;
	}

	public CollectionEventLog getCollectionEventLog() {
		return collectionEventLog;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}

	public PartyService getPartyService() {
		return partyService;
	}
}
