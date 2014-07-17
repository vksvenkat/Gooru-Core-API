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
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CollectionServiceImpl extends ScollectionServiceImpl implements CollectionService {

	@Autowired
	private CollectionRepository collectionRepository;

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

	@Override
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(String collectionId, String data, User user, String mediaFileName) throws Exception {
		ActionResponseDTO<CollectionItem> response = null;
		Collection collection = collectionRepository.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		AssessmentQuestion question = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		question.setSharing(collection.getSharing());
		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(question, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel(), collection,null,null,user);
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, question, ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					if (ResourceImageUtil.getYoutubeVideoId(questionImage) != null || questionImage.contains(YOUTUBE_URL)) {
						response.getModel().setQuestionInfo(this.assessmentService.updateQuestionVideoAssest(responseDTO.getModel().getGooruOid(), questionImage));
					} else {
						response.getModel().setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(), StringUtils.substringAfterLast(questionImage, "/")));
					}
				}
			}
			if (question.getDepthOfKnowledges() != null && question.getDepthOfKnowledges().size() > 0) {
				response.getModel().getResource().setDepthOfKnowledges(this.updateContentMeta(question.getDepthOfKnowledges(), question.getGooruOid(), user, "depth_of_knowledge"));
			} else {
				response.getModel().getResource().setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation("depth_of_knowledge"), question.getGooruOid(), "depth_of_knowledge"));
			}
			if (question.getEducationalUse() != null && question.getEducationalUse().size() > 0) {
				response.getModel().getResource().setEducationalUse(this.updateContentMeta(question.getEducationalUse(), question.getGooruOid(), user, "educational_use"));
			} else {
				response.getModel().getResource().setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation("educational_use"), question.getGooruOid(), "educational_use"));
			}
			response.getModel().setStandards(this.getStandards(responseDTO.getModel().getTaxonomySet(), false, null));
		}
		try {
			getEventLogs(response.getModel(), false, user, response.getModel().getCollection().getCollectionType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	@Override
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(String collectionItemId, String data, List<Integer> deleteAssets, User user, String mediaFileName) throws Exception {
		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		AssessmentQuestion newQuestion = getAssessmentService().buildQuestionFromInputParameters(data, user, true);
		Errors errors = validateUpdateCollectionItem(collectionItem);
		JSONObject ItemData = new JSONObject();
		ItemData.put("itemData", data);
		if (!errors.hasErrors()) {
			AssessmentQuestion question = getAssessmentService().getQuestion(collectionItem.getResource().getGooruOid());
			if (question != null) {
				ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.updateQuestion(newQuestion, deleteAssets, question.getGooruOid(), true, true);
				if (responseDTO.getModel() != null) {
					if (mediaFileName != null && mediaFileName.length() > 0) {
						String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, question, ASSET_QUESTION);
						if (questionImage != null && questionImage.length() > 0) {
							if (ResourceImageUtil.getYoutubeVideoId(questionImage) != null || questionImage.contains(YOUTUBE_URL)) {
								collectionItem.setQuestionInfo(this.assessmentService.updateQuestionVideoAssest(responseDTO.getModel().getGooruOid(), questionImage));
							} else {
								collectionItem.setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(), StringUtils.substringAfterLast(questionImage, "/")));
							}
						}
					}
					if (newQuestion.getDepthOfKnowledges() != null && newQuestion.getDepthOfKnowledges().size() > 0) {
						collectionItem.getResource().setDepthOfKnowledges(this.updateContentMeta(newQuestion.getDepthOfKnowledges(), responseDTO.getModel().getGooruOid(), user, "depth_of_knowledge"));
					} else {
						collectionItem.getResource().setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation("depth_of_knowledge"), responseDTO.getModel().getGooruOid(), "depth_of_knowledge"));
					}
					if (question.getEducationalUse() != null && question.getEducationalUse().size() > 0) {
						collectionItem.getResource().setEducationalUse(this.updateContentMeta(question.getEducationalUse(), responseDTO.getModel().getGooruOid(), user, "educational_use"));
					} else {
						collectionItem.getResource().setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation("educational_use"), responseDTO.getModel().getGooruOid(), "educational_use"));
					}
					collectionItem.setStandards(this.getStandards(responseDTO.getModel().getTaxonomySet(), false, null));
				}
				getAsyncExecutor().deleteFromCache("v2-organize-data-" + collectionItem.getCollection().getUser().getPartyUid() + "*");
			}

		} else {
			throw new NotFoundException("Question Not Found");
		}
		try {
			getEventLogs(collectionItem, ItemData, user);
		} catch(Exception e){
			e.printStackTrace();
		}

		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public ActionResponseDTO<CollectionItem> moveCollectionToFolder(String sourceId, String targetId, User user) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = null;
		Collection source = collectionRepository.getCollectionByGooruOid(sourceId, null);
		if (source == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "Collection"));
		}
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(source);
		CollectionItem sourceCollectionItem = this.getCollectionRepository().findCollectionItemByGooruOid(sourceId, user.getPartyUid());
		if (sourceCollectionItem != null && sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		if (!user.getPartyUid().equalsIgnoreCase(collectionItem.getCollection().getUser().getPartyUid())) {
			UserContentAssoc userContentAssocs = this.getCollaboratorRepository().findCollaboratorById(sourceId, user.getPartyUid());
			if (userContentAssocs != null) {
				collectionItem.setItemType(COLLABORATOR);
			}
		}

		if (sourceCollectionItem != null) {
			deleteCollectionItem(sourceCollectionItem.getCollectionItemId(), user);
		}
		if (targetId != null) {
			responseDTO = this.createCollectionItem(sourceId, targetId, collectionItem, user, CollectionType.FOLDER.getCollectionType(), false);
		} else {
			responseDTO = this.createCollectionItem(sourceId, null, collectionItem, user, CollectionType.SHElf.getCollectionType(), false);
		}
		getAsyncExecutor().deleteFromCache("v2-organize-data-" + collectionItem.getCollection().getUser().getPartyUid() + "*");
		getAsyncExecutor().deleteFromCache("v2-organize-data-" + user.getPartyUid() + "*");
		try {
			getEventLogs(responseDTO.getModel(), true, user, responseDTO.getModel().getCollection().getCollectionType());
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
			throw new NotFoundException(generateErrorMessage("GL0056", "Collection"));
		}
		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(assessmentQuestion, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel().getGooruOid(), collectionId, new CollectionItem(), user, CollectionType.COLLECTION.getCollectionType(), true);
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(), mediaFileName, assessmentQuestion, "asset-question");
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
	public List<Map<String, Object>> getMyShelf(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, Integer itemLimit, boolean fetchChildItem, String topLevelCollectionType) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		if (!BaseUtil.isUuid(gooruUid)) {
			User user = this.getUserService().getUserByUserName(gooruUid);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Object[]> result = this.getCollectionRepository().getMyFolder(gooruUid, limit, offset, sharing, topLevelCollectionType != null ? topLevelCollectionType : collectionType, fetchChildItem);
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
						collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem));
					}
				} else {
					collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem));
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
					collection.put("ideas", object[12]);
				}
				if (object[13] != null) {
					collection.put("questions", object[13]);
				}
				if (object[14] != null) {
					collection.put("performanceTasks", object[14]);
				}
				if (object[15] != null) {
					collection.put("collectionType", object[15]);
				}
				count++;
				folderList.add(collection);
			}
		}
		return folderList;
	}

	public List<Map<String, Object>> getFolderItem(String gooruOid, String sharing, String type, String collectionType, Integer itemLimit, boolean fetchChildItem) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, 0, false, sharing, type.equalsIgnoreCase(SCOLLECTION) ? SEQUENCE : null, collectionType, fetchChildItem);
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
				summary.put("average", object[16] != null ? object[16] : 0);
				summary.put("count", object[17] != null ? object[17] : 0);
				item.put("ratings", summary);
				if (!fetchChildItem) {
					if (String.valueOf(object[2]).equalsIgnoreCase("assessment-question")) {
						item.put("depthOfKnowledges", this.setContentMetaAssociation(this.getContentMetaAssociation("depth_of_knowledge"), String.valueOf(object[1]), "depth_of_knowledge"));
						item.put("educationalUse", this.setContentMetaAssociation(this.getContentMetaAssociation("educational_use"), String.valueOf(object[1]), "educational_use"));
					} else if (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION)) {
						item.put("depthOfKnowledges", this.setContentMetaAssociation(this.getContentMetaAssociation("depth_of_knowledge"), String.valueOf(object[1]), "depth_of_knowledge"));

						item.put("learningSkills", this.setContentMetaAssociation(this.getContentMetaAssociation("learning_and_innovation_skills"), String.valueOf(object[1]), "learning_and_innovation_skills"));

						item.put("audience", this.setContentMetaAssociation(this.getContentMetaAssociation("audience"), String.valueOf(object[1]), "audience"));

						item.put("instructionalMethod", this.setContentMetaAssociation(this.getContentMetaAssociation("instructional_method"), String.valueOf(object[1]), "instructional_method"));
					} else {
						item.put("educationalUse", this.setContentMetaAssociation(this.getContentMetaAssociation("educational_use"), String.valueOf(object[1]), "educational_use"));
						item.put("momentsOfLearning", this.setContentMetaAssociation(this.getContentMetaAssociation("moments_of_learning"), String.valueOf(object[1]), "moments_of_learning"));
					}
				}
				if (fetchChildItem && (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION) || String.valueOf(object[2]).equalsIgnoreCase(FOLDER))) {
					if (String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION)) {
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem));
						item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType));
					} else if ((String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION) || String.valueOf(object[2]).equalsIgnoreCase(FOLDER))) {
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem));
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
				if (object[12] != null) {
					item.put("ideas", object[12]);
				}
				if (object[13] != null) {
					item.put("questions", object[13]);
				}
				if (object[14] != null) {
					item.put("performanceTasks", object[14]);
				}
				if (object[18] != null) {
					item.put("collectionType", object[18]);
				}
				
				items.add(item);
			}

		}
		return items;
	}

	@Override
	public List<Map<String, Object>> getFolderItems(String gooruOid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, Integer itemLimit, boolean fetchChildItem) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, limit, offset, false, sharing, orderBy, collectionType, fetchChildItem);
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
				item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf((object[2])), collectionType, itemLimit, fetchChildItem));
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
				if (object[12] != null) {
					item.put("ideas", object[12]);
				}
				if (object[13] != null) {
					item.put("questions", object[13]);
				}
				if (object[14] != null) {
					item.put("performanceTasks", object[14]);
				}
				if (object[18] != null) {
					item.put("collectionType", object[18]);
				}
				items.add(item);
			}
		}
		return items;
	}

	@Override
	public Map<String, Object> getFolderList(Integer limit, Integer offset, String gooruOid, String title, String username, boolean skipPagination) {
		String gooruUid = null;
		if (username != null) { 
			User user = this.getUserService().getUserByUserName(gooruUid);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Object[]> result = this.getCollectionRepository().getFolderList(limit, offset, gooruOid, title, gooruUid, skipPagination);
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
				folderList.add(folder);
			}
			content.put(SEARCH_RESULT, folderList);
			content.put(COUNT, this.getCollectionRepository().getFolderListCount(gooruOid, title, gooruUid));
		}
		return content;
	}

	@Override
	public SearchResults<Code> getCollectionStandards(Integer codeId, String query, Integer limit, Integer offset, Boolean skipPagination, User user) {

		SearchResults<Code> result = new SearchResults<Code>();
		List<Object[]> list = this.getTaxonomyRespository().getCollectionStandards(codeId, query, limit, offset, skipPagination);
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
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED, COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		rejectIfNull(errors, collectionItem, COLLECTION_ITEM, "GL0056", generateErrorMessage(GL0056, COLLECTION_ITEM));
		rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		return errors;
	}

	@Override
	public List<CollectionItem> assignCollection(String classpageId, String collectionId, User user) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageId);
		rejectIfNull(classpage, GL0056, 404, generateErrorMessage(GL0056, CLASSPAGE));
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, generateErrorMessage(GL0056, COLLECTION));
		List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
		int sequence = classpage.getCollectionItems() != null ? classpage.getCollectionItems().size() + 1 : 1;
		if (collection.getCollectionType().equalsIgnoreCase(FOLDER)) {
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(SHARING, "public,anyonewithlink");
			filters.put(TYPE, COLLECTION);
			List<CollectionItem> folderCollectionItems = this.getCollectionRepository().getCollectionItems(collectionId, filters);
			for (CollectionItem collectionItem : folderCollectionItems) {
				collectionItems.add(createClasspageItem(classpage, collectionItem.getResource(), user, sequence++));
			}
		} else if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
			collectionItems.add(createClasspageItem(classpage, collection, user, sequence));
		}

		return collectionItems;
	}

	private CollectionItem createClasspageItem(Classpage classPage, Resource collection, User user, int sequence) {
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(classPage);
		collectionItem.setResource(collection);
		collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		collectionItem.setItemSequence(sequence);
		classPage.setItemCount(sequence);
		this.getResourceRepository().save(classPage);
		this.getResourceRepository().save(collectionItem);
		try {
			getEventLogs(collectionItem, false, user, collectionItem.getCollection().getCollectionType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return collectionItem;
	}

	@Override
	public SearchResults<Collection> getCollections(Integer offset, Integer limit, Boolean skipPagination, User user, String publishStatus) {

		List<Collection> collections = this.getCollectionRepository().getCollectionsList(user, limit, offset, skipPagination, publishStatus);
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
			gooruOids.add(map.get("gooruOid"));
		}
		if (gooruOids.toString().trim().length() > 0) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (Collection scollection : collections) {
					getAsyncExecutor().deleteFromCache("v2-organize-data-" + scollection.getUser().getPartyUid() + "*");
					if (scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
						scollection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue("publish_status", REVIEWED));
						collectionIds.append(scollection.getGooruOid());
						if (!scollection.getSharing().equalsIgnoreCase(PUBLIC)) {
							UserSummary userSummary = this.getUserRepository().getSummaryByUid(scollection.getUser().getPartyUid());
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

						if (collectionIds.toString().trim().length() > 0) {
							collectionIds.append(",");
						}
					} else {
						throw new BadCredentialsException("You do not have the permission");
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
			gooruOids.add(map.get("gooruOid"));
		}
		if (gooruOids.toString().trim().length() > 0) {
			collections = this.getCollectionRepository().getCollectionListByIds(gooruOids);
			if (userService.isSuperAdmin(user) || userService.isContentAdmin(user)) {
				for (Collection scollection : collections) {
					if (scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
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
						throw new BadCredentialsException("Please try again later");

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
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid);
		String data = null;
		if (collection != null) {
			final String cacheKey = "v2-organize-data-" + collection.getUser().getPartyUid() + "-" + gooruOid + "-" + limit + "-" + offset + "-" + sharing + "-" + collectionType + "-" + orderBy + "-" + itemLimit + "-" + fetchChildItem;
			if (!clearCache) {
				data = redisService.getValue(cacheKey);
			}
			if (data == null) {
				content = new HashMap<String, Object>();
				content.put(SEARCH_RESULT, getFolderItems(gooruOid, limit, offset, sharing, collectionType, orderBy, itemLimit, fetchChildItem));
				content.put(COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType));
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

	public void getEventLogs(CollectionItem collectionItem, boolean isMoveMode, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, "item.create");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context").toString()) : new JSONObject();
		context.put("parentGooruId", collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put("contentGooruId", collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		SessionContextSupport.putLogParameter("context", context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject").toString()) : new JSONObject();
		if (isMoveMode) {
			payLoadObject.put("mode", "move");
		} else {
			payLoadObject.put("mode", "add");
		}
		payLoadObject.put("itemSequence", collectionItem != null ? collectionItem.getItemSequence() : null);
		payLoadObject.put("ItemId", collectionItem != null ? collectionItem.getCollectionItemId() : null);
		if (collectionType != null && collectionItem != null) {
			if(collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())){
				if(collectionItem.getResource() != null){
					String typeName = collectionItem.getResource().getResourceType().getName();
					if(typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put("itemType", "shelf.collection");
					} else if(typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put("itemType", "shelf.folder");
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put("itemType", "collection.resource");
			} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
				if(collectionItem.getResource() != null){
					String itemTypeName = collectionItem.getResource().getResourceType().getName();
					if(itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put("itemType", "folder.folder");
					} else if(itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put("itemType", "folder.collection");
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put("itemType", "classpage.collection");
			}
		}
		payLoadObject.put("parentContentId", collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put("contentId", collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		payLoadObject.put("title", collectionItem != null && collectionItem.getResource() != null && collectionItem.getResource().getTitle() != null ? collectionItem.getResource().getTitle() : null);
		payLoadObject.put("description", collectionItem != null && collectionItem.getResource() != null  && collectionItem.getResource().getDescription() != null? collectionItem.getResource().getDescription() : null );
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session").toString()) : new JSONObject();
		session.put("organizationUId", user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter("session", session.toString());
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

}
