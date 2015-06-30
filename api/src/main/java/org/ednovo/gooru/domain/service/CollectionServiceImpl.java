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

import org.apache.commons.lang.NotImplementedException;
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
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
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
	public ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(
			final String collectionId, final String data, final User user,
			final String mediaFileName, final String sourceReference)
			throws Exception {
		ActionResponseDTO<CollectionItem> response = null;
		final Collection collection = collectionRepository
				.getCollectionByGooruOid(collectionId, null);
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056,
					_COLLECTION), GL0056);
		}
		final AssessmentQuestion question = getAssessmentService()
				.buildQuestionFromInputParameters(data, user, true);
		question.setSourceReference(sourceReference);
		question.setSharing(collection.getSharing());
		final ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService
				.createQuestion(question, true);
		if (responseDTO.getModel() != null) {
			response = this.createCollectionItem(responseDTO.getModel(),
					collection, null, null, user);
            boolean updateAssetInS3 = false;
			if (mediaFileName != null && mediaFileName.length() > 0) {
				String questionImage = this.assessmentService
						.updateQuizQuestionImage(responseDTO.getModel()
								.getGooruOid(), mediaFileName, question,
								ASSET_QUESTION);
				if (questionImage != null && questionImage.length() > 0) {
					if (ResourceImageUtil.getYoutubeVideoId(questionImage) != null
							|| questionImage.contains(YOUTUBE_URL)) {
						response.getModel().setQuestionInfo(
								this.assessmentService
										.updateQuestionVideoAssest(responseDTO
												.getModel().getGooruOid(),
												questionImage));
					} else {
                        response.getModel().setQuestionInfo(
                                this.assessmentService.updateQuestionAssest(
                                        responseDTO.getModel().getGooruOid(),
                                        StringUtils.substringAfterLast(
                                                questionImage, "/")));
                        updateAssetInS3 = true;
                    }
				}
            }
            /* The new generation questions have answers as images as well. We need to
             * store these assets. Note that since they are going into Mongo, we are not
             * maintaining the association in MySql. We shall just stash them and then
             * make sure that they go till S3
             */
            if (question.isQuestionNewGen()) {
                List<String> answerAssets = question.getMediaFiles();
                if (answerAssets != null && answerAssets.size() > 0) {
                    updateAssetInS3 = true;
                    for (String answerAsset : answerAssets) {
                        this.assessmentService.updateQuizQuestionImage(responseDTO.getModel().getGooruOid(),
                                answerAsset, question, null);
                    }
                }
            }

            if (updateAssetInS3) {
                try {
                    this.getAsyncExecutor().updateResourceFileInS3(
                            response.getModel().getResource()
                                    .getFolder(),
                            response.getModel().getResource()
                                    .getOrganization()
                                    .getNfsStorageArea()
                                    .getInternalPath(),
                            response.getModel().getResource()
                                    .getGooruOid(),
                            UserGroupSupport.getSessionToken());
                } catch (Exception e) {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error(e.getMessage());
                    }
                }

            }

			if (question.getDepthOfKnowledges() != null
					&& question.getDepthOfKnowledges().size() > 0) {
				response.getModel()
						.getResource()
						.setDepthOfKnowledges(
								this.updateContentMeta(
										question.getDepthOfKnowledges(),
										responseDTO.getModel().getGooruOid(),
										user, DEPTH_OF_KNOWLEDGE));
			} else {
				response.getModel()
						.getResource()
						.setDepthOfKnowledges(
								this.setContentMetaAssociation(
										this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE),
										responseDTO.getModel().getGooruOid(),
										DEPTH_OF_KNOWLEDGE));
			}
			if (question.getEducationalUse() != null
					&& question.getEducationalUse().size() > 0) {
				response.getModel()
						.getResource()
						.setEducationalUse(
								this.updateContentMeta(
										question.getEducationalUse(),
										responseDTO.getModel().getGooruOid(),
										user, EDUCATIONAL_USE));
			} else {
				response.getModel()
						.getResource()
						.setEducationalUse(
								this.setContentMetaAssociation(
										this.getContentMetaAssociation(EDUCATIONAL_USE),
										responseDTO.getModel().getGooruOid(),
										EDUCATIONAL_USE));
			}
			response.getModel().setStandards(
					this.getStandards(responseDTO.getModel().getTaxonomySet(),
							false, null));
			response.getModel()
					.getResource()
					.setSkills(
							getSkills(responseDTO.getModel().getTaxonomySet()));
			if (question.isQuestionNewGen()) {
				mongoQuestionsService.createQuestion(question.getGooruOid(),
						data);

			}
			if (response.getModel().getCollection().getResourceType().getName()
					.equalsIgnoreCase(SCOLLECTION)
					&& response.getModel().getCollection().getClusterUid() != null
					&& !response
							.getModel()
							.getCollection()
							.getClusterUid()
							.equalsIgnoreCase(
									response.getModel().getCollection()
											.getGooruOid())) {
				response.getModel()
						.getCollection()
						.setClusterUid(
								response.getModel().getCollection()
										.getGooruOid());
				this.getCollectionRepository().save(
						response.getModel().getCollection());
			}
		}
		try {
			this.getCollectionEventLog().getEventLogs(response.getModel(),
					false, true, user,
					response.getModel().getCollection().getCollectionType());
		} catch (Exception e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage());
			}
		}
		return response;

	}

	private ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(
			final CollectionItem collectionItem, final String data,
			final List<Integer> deleteAssets, final User user,
			final String mediaFileName) throws Exception {
		final AssessmentQuestion newQuestion = getAssessmentService()
				.buildQuestionFromInputParameters(data, user, true);
		final Errors errors = validateUpdateCollectionItem(collectionItem);
		if (!errors.hasErrors()) {
			final AssessmentQuestion question = getAssessmentService()
					.getQuestion(collectionItem.getResource().getGooruOid());
			if (question != null) {
				AssessmentQuestion assessmentQuestion = assessmentService
						.updateQuestion(newQuestion, deleteAssets,
								question.getGooruOid(), true, true).getModel();
				this.getResourceService().saveOrUpdateResourceTaxonomy(
						assessmentQuestion, newQuestion.getTaxonomySet());
				if (assessmentQuestion != null) {
					if (mediaFileName != null && mediaFileName.length() > 0) {
						String questionImage = this.assessmentService
								.updateQuizQuestionImage(
										assessmentQuestion.getGooruOid(),
										mediaFileName, question, ASSET_QUESTION);
						if (questionImage != null && questionImage.length() > 0) {
							if (ResourceImageUtil
									.getYoutubeVideoId(questionImage) != null
									|| questionImage.contains(YOUTUBE_URL)) {
								assessmentQuestion = this.assessmentService
										.updateQuestionVideoAssest(
												assessmentQuestion
														.getGooruOid(),
												questionImage);
							} else {
								assessmentQuestion = this.assessmentService
										.updateQuestionAssest(
												assessmentQuestion
														.getGooruOid(),
												StringUtils.substringAfterLast(
														questionImage, "/"));
							}
						}
                        if (assessmentQuestion.isQuestionNewGen()) {
                            List<String> mediaFilesToAdd = newQuestion.getMediaFiles();
                            if (mediaFilesToAdd != null && mediaFilesToAdd.size() > 0) {
                                for (String mediaFileToAdd : mediaFilesToAdd) {
                                    assessmentService.updateQuizQuestionImage(
                                            assessmentQuestion.getGooruOid(),
                                            mediaFileToAdd,
                                            assessmentQuestion,
                                            null
                                    );
                                }
                            }
                        }
                    }
					collectionItem.setQuestionInfo(assessmentQuestion);
					if (newQuestion.getDepthOfKnowledges() != null
							&& newQuestion.getDepthOfKnowledges().size() > 0) {
						collectionItem.getResource().setDepthOfKnowledges(
								this.updateContentMeta(
										newQuestion.getDepthOfKnowledges(),
										assessmentQuestion.getGooruOid(), user,
										DEPTH_OF_KNOWLEDGE));
					} else {
						collectionItem
								.getResource()
								.setDepthOfKnowledges(
										this.setContentMetaAssociation(
												this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE),
												assessmentQuestion
														.getGooruOid(),
												DEPTH_OF_KNOWLEDGE));
					}
					if (question.getEducationalUse() != null
							&& question.getEducationalUse().size() > 0) {
						collectionItem.getResource().setEducationalUse(
								this.updateContentMeta(
										question.getEducationalUse(),
										assessmentQuestion.getGooruOid(), user,
										EDUCATIONAL_USE));
					} else {
						collectionItem
								.getResource()
								.setEducationalUse(
										this.setContentMetaAssociation(
												this.getContentMetaAssociation(EDUCATIONAL_USE),
												assessmentQuestion
														.getGooruOid(),
												EDUCATIONAL_USE));
					}
					collectionItem.getResource().setSkills(
							getSkills(collectionItem.getResource()
									.getTaxonomySet()));
					collectionItem.setStandards(this.getStandards(
							assessmentQuestion.getTaxonomySet(), false, null));
				}
				// Update the question in mongo now that transaction is almost done
				mongoQuestionsService.updateQuestion(collectionItem.getResource().getGooruOid(), data);
				
				getAsyncExecutor().deleteFromCache(
						V2_ORGANIZE_DATA
								+ collectionItem.getCollection().getUser()
										.getPartyUid() + "*");
			}

		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, QUESTION),
					GL0056);
		}
		this.getCollectionEventLog().getEventLogs(collectionItem, false, false,
				user, false, true, data);
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);

	}

	@Override
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(final String collectionItemId, final String data, final List<Integer> deleteAssets, final User user, final String mediaFileName) throws Exception {
		final CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM), GL0056);
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}

	@Override
	public ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(final String collectionId, final String resourceId, final String data, final List<Integer> deleteAssets, final User user, final String mediaFileName) throws Exception {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemByResourceOid(collectionId, resourceId);
		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_ITEM), GL0056);
		}
		return updateQuestionWithCollectionItem(collectionItem, data, deleteAssets, user, mediaFileName);
	}

	@Override
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
	public ActionResponseDTO<CollectionItem> moveCollectionToFolder(final String sourceId, final String targetId, final User user) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = null;
		final Collection source = collectionRepository.getCollectionByGooruOid(sourceId, null);
		if (source == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION), GL0056);
		}
		if (source.getCollectionType().equalsIgnoreCase(FOLDER)){
			throw new BadRequestException(generateErrorMessage(GL0007, _COLLECTION), GL0007);
		}
		final CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(source);
		final CollectionItem sourceCollectionItem = this.getCollectionRepository().findCollectionItemByGooruOid(sourceId, user.getPartyUid(), CLASSPAGE);
		if (sourceCollectionItem != null && sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		collectionItem.setPlannedEndDate(sourceCollectionItem.getPlannedEndDate());
		collectionItem.setEstimatedTime(sourceCollectionItem.getEstimatedTime());
		collectionItem.setAssignmentCompleted(sourceCollectionItem.getAssignmentCompleted());

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
			if(LOGGER.isErrorEnabled()) {
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
		 * NOTE: Not able to find where this API is called from using call hierarchy. 
		 * So, instead of enabling it to handle new question types, trying to throw
		 * here. A bit drastic step, but need to understand if this is really dead
		 * code
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
					response.getModel().setQuestionInfo(this.assessmentService.updateQuestionAssest(responseDTO.getModel().getGooruOid(), StringUtils.substringAfterLast(questionImage, "/")));
				}
			}
		}
		return response;

	}

	@Override
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
				collectionItem.setResource(collection);
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
		final StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		if (!BaseUtil.isUuid(gooruUid)) {
			final User user = this.getUserRepository().getUserByUserName(gooruUid, true);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		final List<Object[]> result = this.getCollectionRepository().getMyFolder(gooruUid, limit, offset, sharing, topLevelCollectionType != null ? topLevelCollectionType : collectionType, fetchChildItem, orderBy, excludeType);
		final List<Map<String, Object>> folderList = new ArrayList<Map<String, Object>>();
		int count = 0;
		if (result != null && result.size() > 0) {
			for (final Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put(TITLE, object[0]);
				collection.put(GOORU_OID, object[1]);
				collection.put(TYPE, object[2]);
				final Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (object[4] != null) {
					if (object[17] != null && Boolean.parseBoolean(object[17].toString())) {
						thumbnails.put(URL, storageArea.getS3Path() + String.valueOf(object[3]) + String.valueOf(object[4]));
					} else {
						thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
					}
				} else {
					thumbnails.put(URL, "");
				}
				collection.put(THUMBNAILS, thumbnails);
				if (fetchChildItem) {
					if (count == 0) {
						collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem, orderBy, excludeType));
					}
				} else {
					collection.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, itemLimit, fetchChildItem, orderBy, excludeType));
				}
				collection.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType, excludeType));
				collection.put(SHARING, object[5]);
				collection.put(COLLECTION_ITEM_ID, object[6]);
				collection.put(GOALS, object[7]);

				if (object[8] != null) {
					final Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, object[8]);
					resourceFormat.put(DISPLAY_NAME, object[9]);
					collection.put(RESOURCEFORMAT, resourceFormat);
				}

				if (object[10] != null) {
					final Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					collection.put(RESOURCESOURCE, resourceSource);
				}
				collection.put(IDEAS, object[12]);
				collection.put(QUESTIONS, object[13]);
				collection.put(PERFORMANCE_TASKS, object[14]);
				collection.put(COLLECTION_TYPE, object[15]);
				collection.put(ITEM_SEQUENCE, object[16]);
				collection.put(PARENT_GOORU_OID, object[17]);
				collection.put(URL, object[20]);
				if (object[21] != null) {
						collection.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(object[21]), new TypeReference<Map<String, String>>() {
					}));
				}
				count++;
				folderList.add(collection);
			}
		}
		return folderList;
	}

	public List<Map<String, Object>> getFolderItem(final String gooruOid, final String sharing, final String type, final String collectionType, Integer itemLimit, final boolean fetchChildItem, final String orderBy, final String excludeType) {
		final StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		final List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		final List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, 0, sharing, orderBy, collectionType, fetchChildItem, type.equalsIgnoreCase(SCOLLECTION) ? ASC : DESC, false, excludeType);
		if (result != null && result.size() > 0) {

			for (final Object[] object : result) {
				final Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				final String typeName = object[2].toString();
				final Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					if (object[15] != null) {
						thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(object[15].toString()) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(object[15].toString()) + "/1.jpg");
					}
				} else {
					if (object[4] != null) {
						if (object[21] != null && Boolean.parseBoolean(object[21].toString())) {
							thumbnails.put(URL, storageArea.getS3Path() + String.valueOf(object[3]) + String.valueOf(object[4]));
						} else {
							thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
						}
					} else {
						thumbnails.put(URL, "");
					}
				}
				item.put(THUMBNAILS, thumbnails);
				if (object[5] != null) {
					final Map<String, Object> resourceFormat = new HashMap<String, Object>();
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
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem, orderBy, excludeType));
						item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType, excludeType));
					} else if ((String.valueOf(object[2]).equalsIgnoreCase(SCOLLECTION) || String.valueOf(object[2]).equalsIgnoreCase(FOLDER))) {
						item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf(object[2]), collectionType, type.equalsIgnoreCase(SCOLLECTION) ? 4 : itemLimit, fetchChildItem, orderBy, excludeType));
						item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType, excludeType));
					}
				}
				item.put(GOALS, object[9]);
				if (object[10] != null) {
					final Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					item.put(RESOURCESOURCE, resourceSource);
				}
				final Resource resource = this.getResourceService().setContentProvider(object[1].toString());
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

				item.put(IDEAS, object[12]);
				item.put(QUESTIONS, object[13]);
				item.put(PERFORMANCE_TASKS, object[14]);
				item.put(COLLECTION_TYPE, object[18]);
				item.put(ITEM_SEQUENCE, object[19]);

				item.put(PARENT_GOORU_OID, object[20]);
				item.put(URL, object[15]);
				items.add(item);
				if (object[23] != null) {
					item.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(object[23]), new TypeReference<Map<String, String>>() {
					}));
				}
			}

		}
		return items;
	}

	@Override
	public List<Map<String, Object>> getFolderItems(final String gooruOid, final Integer limit, Integer offset, final String sharing, final String collectionType, final String orderBy, final Integer itemLimit, final boolean fetchChildItem, final String sortOrder, final String excludeType) {
		final StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		final List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		final List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, limit, offset, sharing, orderBy, collectionType, fetchChildItem, sortOrder, false, excludeType);
		if (result != null && result.size() > 0) {
			for (final Object[] object : result) {
				final Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				final String typeName = object[2].toString();
				final Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					if (object[15] != null) {
						thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(object[15].toString()) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(object[15].toString()) + "/1.jpg");
					}
				} else {
					if (object[4] != null) {
						if (object[21] != null && Boolean.parseBoolean(object[21].toString())) {
							thumbnails.put(URL, storageArea.getS3Path() + String.valueOf(object[3]) + String.valueOf(object[4]));
						} else {
							thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
						}
					} else {
						thumbnails.put(URL, "");
					}
				}
				item.put(THUMBNAILS, thumbnails);
				if (object[5] != null) {
					final Map<String, Object> resourceFormat = new HashMap<String, Object>();
					resourceFormat.put(VALUE, object[5]);
					resourceFormat.put(DISPLAY_NAME, object[6]);
					item.put(RESOURCEFORMAT, resourceFormat);
				}
				item.put(COLLECTION_ITEMS, getFolderItem(String.valueOf(object[1]), sharing, String.valueOf((object[2])), collectionType, itemLimit, fetchChildItem, orderBy, excludeType));
				item.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[1]), sharing, collectionType, excludeType));
				item.put(SHARING, object[7]);
				item.put(COLLECTION_ITEM_ID, object[8]);
				if (object[9] != null) {
					item.put(GOALS, object[9]);
				}
				if (object[10] != null) {
					final Map<String, Object> resourceSource = new HashMap<String, Object>();
					resourceSource.put(ATTRIBUTION, object[10]);
					resourceSource.put(DOMAIN_NAME, object[11]);
					item.put(RESOURCESOURCE, resourceSource);
				}
				final Resource resource = this.getResourceService().setContentProvider(object[1].toString());
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
				item.put(IDEAS, object[12]);
				item.put(QUESTIONS, object[13]);
				item.put(PERFORMANCE_TASKS, object[14]);
				item.put(COLLECTION_TYPE, object[18]);
				item.put(ITEM_SEQUENCE, object[19]);
				item.put(PARENT_GOORU_OID, object[20]);
				item.put(URL, object[15]);
				if (object[23] != null) {
					item.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(object[23]), new TypeReference<Map<String, String>>() {
					}));
				}
				items.add(item);
			}
		}
		return items;
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
	public List<CollectionItem> assignCollection(final String classpageId, final String collectionId, final User user, final String direction, final String planedEndDate, final Boolean isRequired, final String minimumScore, final String estimatedTime, final Boolean showAnswerByQuestions, final Boolean showAnswerEnd, final Boolean showHints) throws Exception {
		final Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageId);
		rejectIfNull(classpage, GL0056, 404, generateErrorMessage(GL0056, CLASSPAGE));
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, generateErrorMessage(GL0056, COLLECTION));

		return classAssign(classpage, collection, user, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints);
	}

	@Override
	public List<CollectionItem> assignCollectionToPathway(final String classpageId, final String pathwayId, final String collectionId, final User user, final String direction, final String planedEndDate, final Boolean isRequired, final String minimumScore, final String estimatedTime, final Boolean showAnswerByQuestions, final Boolean showAnswerEnd,
			final Boolean showHints) throws Exception {
		final Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageId);
		rejectIfNull(classpage, GL0056, 404, generateErrorMessage(GL0056, CLASSPAGE));
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, generateErrorMessage(GL0056, COLLECTION));
		final Collection pathway = this.getCollectionRepository().getCollectionByIdWithType(pathwayId, PATHWAY);
		rejectIfNull(pathway, GL0056, 404, generateErrorMessage(GL0056, PATHWAY));
		getAsyncExecutor().deleteFromCache("v2-class-data-" + classpage.getGooruOid() + "*");
		return classAssign(pathway, collection, user, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints);
	}

	public List<CollectionItem> classAssign(final Collection classpage, final Collection collection, final User user, final String direction, final String planedEndDate, final Boolean isRequired, final String minimumScore, final String estimatedTime, final Boolean showAnswerByQuestions, final Boolean showAnswerEnd, final Boolean showHints) {

		final List<CollectionItem> collectionItems = new ArrayList<CollectionItem>();
		int sequence = classpage.getCollectionItems() != null ? classpage.getCollectionItems().size() + 1 : 1;
		if (collection.getResourceType().getName().equalsIgnoreCase(FOLDER)) {
			final Map<String, String> filters = new HashMap<String, String>();
			filters.put(SHARING, "public,anyonewithlink");
			filters.put(TYPE, COLLECTION);
			final List<CollectionItem> folderCollectionItems = this.getCollectionRepository().getCollectionItems(collection.getGooruOid(), filters);
			for (CollectionItem collectionItem : folderCollectionItems) {
				collectionItems.add(createClasspageItem(classpage, collectionItem.getResource(), user, sequence++, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints));
			}
		} else if (collection.getResourceType().getName().equalsIgnoreCase(SCOLLECTION)) {
			collectionItems.add(createClasspageItem(classpage, collection, user, sequence, direction, planedEndDate, isRequired, minimumScore, estimatedTime, showAnswerByQuestions, showAnswerEnd, showHints));
		}
		getAsyncExecutor().deleteFromCache("v2-class-data-" + classpage.getGooruOid() + "*");

		return collectionItems;
	}

	private CollectionItem createClasspageItem(final Collection classPage, final Resource collection, final User user, final int sequence, final String direction, final String planedEndDate, final Boolean isRequired, final String minimumScore, final String estimatedTime, final Boolean showAnswerByQuestions, final Boolean showAnswerEnd, final Boolean showHints) {
		final CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(classPage);
		collectionItem.setResource(collection);
		collectionItem.setItemType(ADDED);
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
				final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				final Date date = dateFormat.parse(planedEndDate);
				collectionItem.setPlannedEndDate(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		classPage.setItemCount(sequence);
		this.getResourceRepository().save(classPage);
		this.getResourceRepository().save(collectionItem);
		try {
			this.getCollectionEventLog().getEventLogs(collectionItem, false, false, user, collectionItem.getCollection().getCollectionType());
		} catch (Exception e) {
			if(LOGGER.isErrorEnabled()){
				LOGGER.error(e.getMessage());
			}
		}
		return collectionItem;
	}

	@Override
	public SearchResults<Collection> getCollections(final Integer offset, final Integer limit, final User user, final String publishStatus) {

		final List<Collection> collections = this.getCollectionRepository().getCollectionsList(user, limit, offset, CustomProperties.Table.PUBLISH_STATUS.getTable() + UNDER_SCORE + publishStatus);
		final SearchResults<Collection> result = new SearchResults<Collection>();
		result.setSearchResults(collections);
		result.setTotalHitCount(this.getCollectionRepository().getCollectionCount(CustomProperties.Table.PUBLISH_STATUS.getTable() + UNDER_SCORE + publishStatus));
		return result;

	}

	@Override
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
					if (scollection.getPublishStatus() != null && scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
						scollection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, REVIEWED));
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
							throw new BadRequestException(generateErrorMessage("GL0089"));
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
					if (scollection.getPublishStatus() != null && scollection.getPublishStatus().getValue().equalsIgnoreCase(PENDING)) {
						scollection.setPublishStatus(null);
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
						throw new BadRequestException(generateErrorMessage("GL0091"));

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
	public String getFolderItemsWithCache(final String gooruOid, final Integer limit, final Integer offset, final String sharing, final String collectionType, final String orderBy, final Integer itemLimit, final boolean fetchChildItem, final boolean clearCache, final User user, final String excludeType) {
		Map<String, Object> content = null;
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		String data = null;
		if (collection != null) {
			final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + HYPHEN + gooruOid + HYPHEN + limit + HYPHEN + offset + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + orderBy + HYPHEN + itemLimit + HYPHEN + excludeType +  HYPHEN +  fetchChildItem;
			if (!clearCache) {
				data = redisService.getValue(cacheKey);
			}
			if (data == null) {
				content = new HashMap<String, Object>();
				content.put(SEARCH_RESULT, getFolderItems(gooruOid, limit, offset, sharing, collectionType, orderBy, itemLimit, fetchChildItem, collection.getCollectionType().equalsIgnoreCase(COLLECTION) ? ASC : DESC, excludeType));
				content.put(COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType, excludeType));
				if (!fetchChildItem && (collectionType == null || (collectionType != null && collectionType.equalsIgnoreCase(COLLECTION) || collectionType.equalsIgnoreCase(SCOLLECTION)))) {
					content.put(COLLECTION_COUNT, this.getCollectionRepository().getCollectionItemCount(gooruOid, sharing, collectionType != null ? collectionType : COLLECTION, excludeType));
				}
				data = SerializerUtil.serializeToJson(content, TOC_EXCLUDES, true, true);
				redisService.putValue(cacheKey, data, fetchChildItem ? Constants.LIBRARY_CACHE_EXPIRY_TIME_IN_SEC : Constants.CACHE_EXPIRY_TIME_IN_SEC);
			}
		}
		return data;
	}

	@Override
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
