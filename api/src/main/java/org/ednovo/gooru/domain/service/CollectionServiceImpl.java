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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.eventlogs.CollectionEventLog;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionServiceImpl.class);


	@Override
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(String collectionId, String data, User user, String mediaFileName, String sourceReference) throws Exception {
		ActionResponseDTO<CollectionItem> response = null;
		Collection collection = collectionRepository.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		AssessmentQuestion question = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		question.setSourceReference(sourceReference);
		question.setSharing(collection.getSharing());
		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(question, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel(), collection, null, null, user);
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, question, ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					if (ResourceImageUtil.getYoutubeVideoId(questionImage) != null || questionImage.contains(YOUTUBE_URL)) {
						response.getModel().setQuestionInfo(this.assessmentService.updateQuestionVideoAssest(responseDTO.getModel().getGooruOid(), questionImage));
					} else {
						response.getModel().setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(), StringUtils.substringAfterLast(questionImage, "/")));
						try {
							this.getAsyncExecutor().updateResourceFileInS3(response.getModel().getResource().getFolder(), response.getModel().getResource().getOrganization().getNfsStorageArea().getInternalPath(), response.getModel().getResource().getGooruOid(), UserGroupSupport.getSessionToken());
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
					}
				}
			}
			if (question.getDepthOfKnowledges() != null && question.getDepthOfKnowledges().size() > 0) {
				response.getModel().getResource().setDepthOfKnowledges(this.updateContentMeta(question.getDepthOfKnowledges(), responseDTO.getModel().getGooruOid(), user, DEPTH_OF_KNOWLEDGE));
			} else {
				response.getModel().getResource().setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), responseDTO.getModel().getGooruOid(), DEPTH_OF_KNOWLEDGE));
			}
			if (question.getEducationalUse() != null && question.getEducationalUse().size() > 0) {
				response.getModel().getResource().setEducationalUse(this.updateContentMeta(question.getEducationalUse(), responseDTO.getModel().getGooruOid(), user, EDUCATIONAL_USE));
			} else {
				response.getModel().getResource().setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), responseDTO.getModel().getGooruOid(), EDUCATIONAL_USE));
			}
			response.getModel().setStandards(this.getStandards(responseDTO.getModel().getTaxonomySet(), false, null));
			if (response.getModel().getCollection().getResourceType().getName().equalsIgnoreCase(SCOLLECTION) && response.getModel().getCollection().getClusterUid() != null && !response.getModel().getCollection().getClusterUid().equalsIgnoreCase(response.getModel().getCollection().getGooruOid())) {
				response.getModel().getCollection().setClusterUid(response.getModel().getCollection().getGooruOid());
				this.getCollectionRepository().save(response.getModel().getCollection());
			}
			getAsyncExecutor().deleteFromCache("v2-collection-data-"+ collectionId +"*");
		}
		try {
			this.getCollectionEventLog().getEventLogs(response.getModel(), false, user, response.getModel().getCollection().getCollectionType());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;

	}

	private ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(CollectionItem collectionItem, String data, List<Integer> deleteAssets, User user, String mediaFileName) throws Exception {
		AssessmentQuestion newQuestion = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		Errors errors = validateUpdateCollectionItem(collectionItem);
		final JSONObject itemData = new JSONObject();
		itemData.put(_ITEM_DATA, data);
		if (!errors.hasErrors()) {
			AssessmentQuestion question = getAssessmentService().getQuestion(collectionItem.getResource().getGooruOid());
			if (question != null) {
				AssessmentQuestion assessmentQuestion = assessmentService.updateQuestion(newQuestion, deleteAssets, question.getGooruOid(), true, true).getModel();
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
					collectionItem.setQuestionInfo(assessmentQuestion);
					if (newQuestion.getDepthOfKnowledges() != null && newQuestion.getDepthOfKnowledges().size() > 0) {
						collectionItem.getResource().setDepthOfKnowledges(this.updateContentMeta(newQuestion.getDepthOfKnowledges(), assessmentQuestion.getGooruOid(), user, DEPTH_OF_KNOWLEDGE));
					} else {
						collectionItem.getResource().setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), assessmentQuestion.getGooruOid(), DEPTH_OF_KNOWLEDGE));
					}
					if (question.getEducationalUse() != null && question.getEducationalUse().size() > 0) {
						collectionItem.getResource().setEducationalUse(this.updateContentMeta(question.getEducationalUse(), assessmentQuestion.getGooruOid(), user, EDUCATIONAL_USE));
					} else {
						collectionItem.getResource().setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), assessmentQuestion.getGooruOid(), EDUCATIONAL_USE));
					}
					collectionItem.setStandards(this.getStandards(assessmentQuestion.getTaxonomySet(), false, null));
				}
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
				getAsyncExecutor().deleteFromCache("v2-collection-data-"+ collectionItem.getCollection().getGooruOid() +"*");
			}

		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, "Question"));
		}
		try {
			this.collectionEventLog.getEventLogs(collectionItem, false,  false, user, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);

	}

	@Override
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(String collectionItemId, String data, List<Integer> deleteAssets, User user, String mediaFileName) throws Exception {
		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM));
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}
	
	@Override
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(String collectionId, String resourceId, String data, List<Integer> deleteAssets, User user, String mediaFileName) throws Exception {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemByResourceOid(collectionId, resourceId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM));
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}	
	
	@Override
	public void deleteQuestionWithCollectionItem(String collectionId, String resourceId) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemByResourceOid(collectionId,resourceId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM));
		}
	this.getCollectionRepository().remove(collectionItem);
	}

	@Override
	public ActionResponseDTO<CollectionItem> moveCollectionToFolder(String sourceId, String targetId, User user) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = null;
		Collection source = collectionRepository.getCollectionByGooruOid(sourceId, null);
		if (source == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(source);
		CollectionItem sourceCollectionItem = this.getCollectionRepository().findCollectionItemByGooruOid(sourceId, user.getPartyUid(), CLASSPAGE);
		if (sourceCollectionItem != null && sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		if (!user.getPartyUid().equalsIgnoreCase(collectionItem.getCollection().getUser().getPartyUid())) {
			UserContentAssoc userContentAssocs = this.getCollaboratorRepository().findCollaboratorById(sourceId, user.getPartyUid());
			if (userContentAssocs != null) {
				collectionItem.setItemType(COLLABORATOR);
			}
		}
		String collectionGooruOid = null;
		if (sourceCollectionItem != null) {
			collectionGooruOid = sourceCollectionItem.getCollection().getGooruOid();
			deleteCollectionItem(sourceCollectionItem.getCollectionItemId(), user);
		}

		if (targetId != null) {
			responseDTO = this.createCollectionItem(sourceId, targetId, collectionItem, user, CollectionType.FOLDER.getCollectionType(), false);
		} else {
			responseDTO = this.createCollectionItem(sourceId, null, collectionItem, user, CollectionType.SHElf.getCollectionType(), false);
		}
		if (collectionGooruOid != null) {
			updateFolderSharing(collectionGooruOid);
			List<String> parenFolders = this.getParentCollection(collectionGooruOid, user.getPartyUid(), false);
			for (String folderGooruOid : parenFolders) {
				updateFolderSharing(folderGooruOid);
			}
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");

		try {
			this.getCollectionEventLog().getEventLogs(responseDTO.getModel(), true, user, responseDTO.getModel().getCollection().getCollectionType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return responseDTO;
	}

	@Override
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(String collectionId, AssessmentQuestion assessmentQuestion, User user, String mediaFileName) throws Exception {

		ActionResponseDTO<CollectionItem> response = null;
		Collection collection = collectionRepository.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(assessmentQuestion, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel().getGooruOid(), collectionId, new CollectionItem(), user, CollectionType.COLLECTION.getCollectionType(), true);
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, assessmentQuestion, ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					response.getModel().setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(), StringUtils.substringAfterLast(questionImage, "/")));
				}
			}
		}
		return response;

	}

	@Override
	public List<CollectionItem> createCollectionItems(List<String> collectionsIds, String resourceId, User user) throws Exception {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(resourceId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION));
		}
		List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
		for (String collectionId : collectionsIds) {
			Collection classPage = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
			if (classPage != null) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setCollection(classPage);
				collectionItem.setResource(collection);
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
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
	public List<Map<String, Object>> getMyShelf(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, Integer itemLimit, boolean fetchChildItem, String topLevelCollectionType, String orderBy) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		if (!BaseUtil.isUuid(gooruUid)) {
			User user = this.getUserService().getUserByUserName(gooruUid);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Object[]> result = this.getCollectionRepository().getMyFolder(gooruUid, limit, offset, sharing, topLevelCollectionType != null ? topLevelCollectionType : collectionType, fetchChildItem, orderBy);
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		int count = 0;
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put(TITLE, object[0]);
				collection.put(GOORU_OID, object[1]);
				collection.put(TYPE, object[2]);
				Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (object[4] != null) {
					thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
				} else {
					thumbnails.put(URL, "");
				}
				collection.put(THUMBNAILS, thumbnails);
				if (fetchChildItem) {
					if (count == 0) {
						collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem, orderBy));
					}
				} else {
					collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem, orderBy));
				}
				collection.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType));
				collection.put(SHARING, object[5]);
				collection.put(COLLECTION_ITEM_ID, object[6]);
				if (object[7] != null) {
					collection.put(GOALS, object[7]);
				}

				if (object[8] != null) {
					Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, object[8]);
					resourceFormat.put(DISPLAY_NAME, object[9]);
					collection.put(RESOURCEFORMAT, resourceFormat);
				}

				if (object[10] != null) {
					Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					collection.put(RESOURCESOURCE, resourceSource);
				}
				if (object[12] != null) {
					collection.put(IDEAS, object[12]);
				}
				if (object[13] != null) {
					collection.put(QUESTIONS, object[13]);
				}
				if (object[14] != null) {
					collection.put(PERFORMANCE_TASKS, object[14]);
				}
				if (object[15] != null) {
					collection.put(COLLECTION_TYPE, object[15]);
				}
				collection.put(ITEM_SEQUENCE, object[16]);
				collection.put(PARENT_GOORU_OID, object[17]);
				count++;
				folderList.add(collection);
			}
		}
		return folderList;
	}

	public List<Map<String, Object>> getFolderItem(String gooruOid, String sharing, String type, String collectionType, Integer itemLimit, boolean fetchChildItem, String orderBy) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, 0, sharing, orderBy, collectionType, fetchChildItem);
		if (result != null && result.size() > 0) {

			for (Object[] object : result) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				String typeName = object[2].toString();
				Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					if (object[15] != null) {
						thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(object[15].toString()) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(object[15].toString()) + "/1.jpg");
					}
				} else {
					if (object[4] != null) {
						thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
					} else {
						thumbnails.put(URL, "");
					}
				}
				item.put(THUMBNAILS, thumbnails);
				if (object[5] != null) {
					Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, object[5]);
					resourceFormat.put(DISPLAY_NAME, object[6]);
					item.put(RESOURCEFORMAT, resourceFormat);
				}
				item.put(SHARING, object[7]);
				item.put(COLLECTION_ITEM_ID, object[8]);
				Map<String, Object> summary = new HashMap<String, Object>();
				summary.put(AVERAGE, object[16] != null ? object[16] : 0);
				summary.put(COUNT, object[17] != null ? object[17] : 0);
				item.put(RATINGS, summary);
				if (!fetchChildItem) {
					if (String.valueOf(object[2]).equalsIgnoreCase(ASSESSMENT_QUESTION)) {
						item.put(DEPTHOFKNOWLEDGES, this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), String.valueOf(object[1]), DEPTH_OF_KNOWLEDGE));
						item.put(_EDUCATIONAL_USE, this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), String.valueOf(object[1]), EDUCATIONAL_USE));
					} else if (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION)) {
						item.put(DEPTHOFKNOWLEDGES, this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), String.valueOf(object[1]), DEPTH_OF_KNOWLEDGE));

						item.put(LEARNING_SKILLS, this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS), String.valueOf(object[1]), LEARNING_AND_INNOVATION_SKILLS));

						item.put(AUDIENCE, this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), String.valueOf(object[1]), AUDIENCE));

						item.put(INSTRUCTIONALMETHOD, this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), String.valueOf(object[1]), INSTRUCTIONAL_METHOD));
					} else {
						item.put(_EDUCATIONAL_USE, this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), String.valueOf(object[1]), EDUCATIONAL_USE));
						item.put(MOMENTSOFLEARNING, this.setContentMetaAssociation(this.getContentMetaAssociation(MOMENTS_OF_LEARNING), String.valueOf(object[1]), MOMENTS_OF_LEARNING));
					}
				}
				if (fetchChildItem && (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION) || String.valueOf(object[2]).equalsIgnoreCase(FOLDER))) {
					if (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION)) {
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem, orderBy));
						item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType));
					} else if ((String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION) || String.valueOf(object[2]).equalsIgnoreCase(FOLDER))) {
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem, orderBy));
						item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType));
					}
				}
				if (object[9] != null) {
					item.put(GOALS, object[9]);
				}
				if (object[10] != null) {
					Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					item.put(RESOURCESOURCE, resourceSource);
				}
				Resource resource = this.getResourceService().setContentProvider(object[1].toString());
				if (resource != null) {
					if (resource.getPublisher() != null && resource.getPublisher().size() > 0) {
						item.put(PUBLISHER, resource.getPublisher());
					}
					if (resource.getAggregator() != null && resource.getAggregator().size() > 0) {
						item.put(AGGREGATOR, resource.getAggregator());
					}
					if (resource.getHost() != null && resource.getHost().size() > 0) {
						item.put("host", resource.getHost());
					}
				}

				if (object[12] != null) {
					item.put(IDEAS, object[12]);
				}
				if (object[13] != null) {
					item.put(QUESTIONS, object[13]);
				}
				if (object[14] != null) {
					item.put(PERFORMANCE_TASKS, object[14]);
				}
				if (object[18] != null) {
					item.put(COLLECTION_TYPE, object[18]);
				}
				item.put(ITEM_SEQUENCE, object[19]);

				item.put(PARENT_GOORU_OID, object[20]);

				items.add(item);
			}

		}
		return items;
	}

	@Override
	public List<Map<String, Object>> getFolderItems(String gooruOid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, Integer itemLimit, boolean fetchChildItem) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, limit, offset, sharing, orderBy, collectionType, fetchChildItem);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				String typeName = object[2].toString();
				Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					if (object[15] != null) {
						thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(object[15].toString()) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(object[15].toString()) + "/1.jpg");
					}
				} else {
					if (object[4] != null) {
						thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
					} else {
						thumbnails.put(URL, "");
					}
				}
				item.put(THUMBNAILS, thumbnails);
				if (object[5] != null) {
					Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, object[5]);
					resourceFormat.put(DISPLAY_NAME, object[6]);
					item.put(RESOURCEFORMAT, resourceFormat);
				}
				item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf((object[2])), collectionType, itemLimit, fetchChildItem, orderBy));
				item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType));
				item.put(SHARING, object[7]);
				item.put(COLLECTION_ITEM_ID, object[8]);
				if (object[9] != null) {
					item.put(GOALS, object[9]);
				}
				if (object[10] != null) {
					Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					item.put(RESOURCESOURCE, resourceSource);
				}
				Resource resource = this.getResourceService().setContentProvider(object[1].toString());
				if (resource != null) {
					if (resource.getPublisher() != null && resource.getPublisher().size() > 0) {
						item.put(PUBLISHER, resource.getPublisher());
					}
					if (resource.getAggregator() != null && resource.getAggregator().size() > 0) {
						item.put(AGGREGATOR, resource.getAggregator());
					}
					if (resource.getHost() != null && resource.getHost().size() > 0) {
						item.put("host", resource.getHost());
					}
				}
				if (object[12] != null) {
					item.put(IDEAS, object[12]);
				}
				if (object[13] != null) {
					item.put(QUESTIONS, object[13]);
				}
				if (object[14] != null) {
					item.put(PERFORMANCE_TASKS, object[14]);
				}
				if (object[18] != null) {
					item.put(COLLECTION_TYPE, object[18]);
				}
				item.put(ITEM_SEQUENCE, object[19]);
				item.put(PARENT_GOORU_OID, object[20]);
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public Map<String, Object> getFolderList(Integer limit, Integer offset, String gooruOid, String title, String username) {
		String gooruUid = null;
		if (username != null) {
			User user = this.getUserService().getUserByUserName(gooruUid);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Object[]> result = this.getCollectionRepository().getFolderList(limit, offset, gooruOid, title, gooruUid);
		List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		Map<String, Object> content = new HashMap<String, Object>();
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> folder = new HashMap<String, Object>();
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
	public SearchResults<Code> getCollectionStandards(Integer codeId, String query, Integer limit, Integer offset, User user) {

		SearchResults<Code> result = new SearchResults<Code>();
		List<Object[]> list = this.getTaxonomyRespository().getCollectionStandards(codeId, query, limit, offset);
		List<Code> codeList = new ArrayList<Code>();
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

	private Errors validateUpdateCollectionItem(CollectionItem collectionItem) throws Exception {
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		rejectIfNull(errors, collectionItem, COLLECTION_ITEM, GL0056, generateErrorMessage(GL0056, COLLECTION_ITEM));
		return errors;
	}

	@Override
	public List<CollectionItem> assignCollection(String classpageId, String collectionId, User user, String direction, String planedEndDate, Boolean isRequired, String minimumScore, String estimatedTime, Boolean showAnswerByQuestions, Boolean showAnswerEnd, Boolean showHints) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageId);
		rejectIfNull(classpage, GL0056, 404, generateErrorMessage(GL0056, CLASSPAGE));
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, generateErrorMessage(GL0056, COLLECTION));

		return classAssign(classpage, collection, user, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints);
	}

	@Override
	public List<CollectionItem> assignCollectionToPathway(String classpageId, String pathwayId, String collectionId, User user, String direction, String planedEndDate, Boolean isRequired, String minimumScore, String estimatedTime, Boolean showAnswerByQuestions, Boolean showAnswerEnd,
			Boolean showHints) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageId);
		rejectIfNull(classpage, GL0056, 404, generateErrorMessage(GL0056, CLASSPAGE));
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, generateErrorMessage(GL0056, COLLECTION));
		Collection pathway = this.getCollectionRepository().getCollectionByIdWithType(pathwayId, PATHWAY);
		rejectIfNull(pathway, GL0056, 404, generateErrorMessage(GL0056, PATHWAY));
		getAsyncExecutor().deleteFromCache("v2-class-data-" + classpage.getGooruOid() + "*");
		return classAssign(pathway, collection, user, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints);
	}

	public List<CollectionItem> classAssign(Collection classpage, Collection collection, User user, String direction, String planedEndDate, Boolean isRequired, String minimumScore, String estimatedTime, Boolean showAnswerByQuestions, Boolean showAnswerEnd, Boolean showHints) {

		List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
		int sequence = classpage.getCollectionItems() != null ? classpage.getCollectionItems().size() + 1 : 1;
		if (collection.getResourceType().getName().equalsIgnoreCase(FOLDER)) {
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(SHARING, "public,anyonewithlink");
			filters.put(TYPE, COLLECTION);
			List<CollectionItem> folderCollectionItems = this.getCollectionRepository().getCollectionItems(collection.getGooruOid(), filters);
			for (CollectionItem collectionItem : folderCollectionItems) {
				collectionItems.add(createClasspageItem(classpage, collectionItem.getResource(), user, sequence++, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints));
			}
		} else if (collection.getResourceType().getName().equalsIgnoreCase(SCOLLECTION)) {
			collectionItems.add(createClasspageItem(classpage, collection, user, sequence, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints));
		}
		getAsyncExecutor().deleteFromCache("v2-class-data-" + classpage.getGooruOid() + "*");

		return collectionItems;
	}

	private CollectionItem createClasspageItem(Collection classPage, Resource collection, User user, int sequence, String direction, String planedEndDate, Boolean isRequired, String minimumScore, String estimatedTime, Boolean showAnswerByQuestions, Boolean showAnswerEnd, Boolean showHints) {
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(classPage);
		collectionItem.setResource(collection);
		collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		collectionItem.setItemSequence(sequence);
		if (direction != null) {
			collectionItem.setNarration(direction);
		}
		if (isRequired != null) {
			collectionItem.setIsRequired(isRequired);
		}
		if (minimumScore != null) {
			collectionItem.setMinimumScore(minimumScore);
		}
		if (estimatedTime != null) {
			collectionItem.setEstimatedTime(estimatedTime);
		}
		if (showAnswerByQuestions != null) {
			collectionItem.setShowAnswerByQuestions(showAnswerByQuestions);
		}
		if (showAnswerEnd == null) {
			collectionItem.setShowAnswerEnd(showAnswerEnd);
		}
		if (showHints != null) {
			collectionItem.setShowHints(showHints);
		}
		if (planedEndDate != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				Date date = dateFormat.parse(planedEndDate);
				collectionItem.setPlannedEndDate(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		classPage.setItemCount(sequence);
		this.getResourceRepository().save(classPage);
		this.getResourceRepository().save(collectionItem);
		try {
			this.getCollectionEventLog().getEventLogs(collectionItem, false, user, collectionItem.getCollection().getCollectionType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectionItem;
	}

	@Override
	public SearchResults<Collection> getCollections(Integer offset, Integer limit, User user, String publishStatus) {

		List<Collection> collections = this.getCollectionRepository().getCollectionsList(user, limit, offset, publishStatus);
		SearchResults<Collection> result = new SearchResults<Collection>();
		result.setSearchResults(collections);
		result.setTotalHitCount(this.getCollectionRepository().getCollectionCount(publishStatus));
		return result;

	}

	@Override
	public List<Collection> updateCollectionForPublish(List<Map<String, String>> collection, User user) throws Exception {

		List<String> gooruOids = new ArrayList<String>();
		List<Collection> collections = new ArrayList<Collection>();
		StringBuffer collectionIds = new StringBuffer();
		for (Map<String, String> map : collection) {
			gooruOids.add(map.get(GOORU_OID));
		}
		if (gooruOids.toString().trim().length() > 0) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (Collection scollection : collections) {
					getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + scollection.getUser().getPartyUid() + "*");
					getAsyncExecutor().deleteFromCache("v2-collection-data-"+ scollection.getGooruOid() +"*");
					if (scollection.getPublishStatus() != null && scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
						scollection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, REVIEWED));
						collectionIds.append(scollection.getGooruOid());
						if (!scollection.getSharing().equalsIgnoreCase(PUBLIC)) {
							UserSummary userSummary = this.getUserRepository().getSummaryByUid(scollection.getUser().getPartyUid());
							if (userSummary.getCollections() == null || userSummary.getCollections() == 0) {
								PartyCustomField partyCustomField = new PartyCustomField(USER_META, SHOW_PROFILE_PAGE, TRUE);
								this.getPartyService().updatePartyCustomField(scollection.getUser().getPartyUid(), partyCustomField, scollection.getUser());
							}
							if (userSummary.getGooruUid() == null) {
								userSummary.setGooruUid(scollection.getUser().getPartyUid());
							}
							userSummary.setCollections((userSummary.getCollections() != null ? userSummary.getCollections() : 0) + 1);
							this.getUserRepository().save(userSummary);
							this.getUserRepository().flush();
						}
						scollection.setSharing(PUBLIC);
						List<String> parenFolders = this.getParentCollection(scollection.getGooruOid(), scollection.getUser().getPartyUid(), false);
						for (String folderGooruOid : parenFolders) {
							updateFolderSharing(folderGooruOid);
						}
						updateResourceSharing(PUBLIC, scollection);
						try {
							String mailId = scollection.getUser().getIdentities().iterator().next().getExternalId();
							this.getMailHandler().sendAdminPortalMail(PUBLISH_COLLECTION, mailId, scollection.getUser().getFirstName(), scollection.getTitle(), scollection.getGooruOid());
						} catch (Exception e) {

						}
						if (collectionIds.toString().trim().length() > 0) {
							collectionIds.append(",");
						} else {
							throw new BadRequestException(generateErrorMessage("GL0089"));
						}
					}

				}
			}
			this.getCollectionRepository().saveAll(collections);
			if (collectionIds.toString().trim().length() > 0) {
				indexProcessor.index(collectionIds.toString(), IndexProcessor.INDEX, SCOLLECTION);
			}
		}
		return collections;

	}

	@Override
	public List<Collection> updateCollectionForReject(List<Map<String, String>> collection, User user) throws Exception {

		List<String> gooruOids = new ArrayList<String>();
		List<Collection> collections = new ArrayList<Collection>();
		StringBuffer collectionIds = new StringBuffer();
		for (Map<String, String> map : collection) {
			gooruOids.add(map.get(GOORU_OID));
		}
		if (gooruOids.toString().trim().length() > 0) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (Collection scollection : collections) {
					if (scollection.getPublishStatus() != null && scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
						scollection.setPublishStatus(null);
						if (scollection.getSharing().equalsIgnoreCase(PUBLIC)) {
							UserSummary userSummary = this.getUserRepository().getSummaryByUid(scollection.getUser().getPartyUid());
							if (userSummary.getGooruUid() != null) {
								userSummary.setCollections(userSummary.getCollections() <= 0 ? 0 : (userSummary.getCollections() - 1));
								this.getUserRepository().save(userSummary);
								this.getUserRepository().flush();
							}
						}
						collectionIds.append(scollection.getGooruOid());

						if (collectionIds.toString().trim().length() > 0) {
							collectionIds.append(",");
						}
						scollection.setSharing(ANYONE_WITH_LINK);
						List<String> parenFolders = this.getParentCollection(scollection.getGooruOid(), scollection.getUser().getPartyUid(), false);
						for (String folderGooruOid : parenFolders) {
							updateFolderSharing(folderGooruOid);
						}
						updateResourceSharing(ANYONE_WITH_LINK, scollection);
					} else {
						throw new BadRequestException(generateErrorMessage("GL0091"));

					}
				}
			}
			this.getCollectionRepository().saveAll(collections);
			if (collectionIds.toString().trim().length() > 0) {
				indexProcessor.index(collectionIds.toString(), IndexProcessor.INDEX, SCOLLECTION);
			}
		}
		return collections;

	}

	@Override
	public String getFolderItemsWithCache(String gooruOid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, Integer itemLimit, boolean fetchChildItem, boolean clearCache, User user) {
		Map<String, Object> content = null;
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		String data = null;
		if (collection != null) {
			final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "-" + gooruOid + "-" + limit + "-" + offset + "-" + sharing + "-" + collectionType + "-" + orderBy + "-" + itemLimit + "-" + fetchChildItem;
			if (!clearCache) {
				data = redisService.getValue(cacheKey);
			}
			if (data == null) {
				content = new HashMap<String, Object>();
				content.put(SEARCH_RESULT, getFolderItems(gooruOid, limit, offset, sharing, collectionType, orderBy, itemLimit, fetchChildItem));
				content.put(COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType));
				if (!fetchChildItem && (collectionType == null || (collectionType != null && collectionType.equalsIgnoreCase(COLLECTION) || collectionType.equalsIgnoreCase(SCOLLECTION)))) {
					content.put(COLLECTION_COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType != null ? collectionType : COLLECTION));
				}
				data = SerializerUtil.serializeToJson(content, true);
				if (user != null && user.getUsername().equalsIgnoreCase(SAUSD)) {
					redisService.putValue(cacheKey, data);
				} else {
					redisService.putValue(cacheKey, data, 86400);
				}
			}
		}
		return data;
	}

	@Override
	public Boolean resourceCopiedFrom(String gooruOid, String gooruUid) {
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
