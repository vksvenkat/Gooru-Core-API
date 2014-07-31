/////////////////////////////////////////////////////////////
// ScollectionServiceImpl.java
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.MailAsyncExecutor;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.ContentMetaDTO;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceSummary;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.StandardFo;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ResourceMetaInfo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentAssociationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.question.CommentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class ScollectionServiceImpl extends BaseServiceImpl implements ScollectionService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private UserService userService;

	@Autowired
	protected AssessmentService assessmentService;

	@Autowired
	private ContentRepositoryHibernate contentRepositoryHibernate;

	@Autowired
	private ContentAssociationRepository contentAssociationRepository;

	@Autowired
	protected TaxonomyService taxonomyService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@Autowired
	private FeedbackService feedbackService;

 	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private MailAsyncExecutor mailAsyncExecutor;

	@Autowired
	private AsyncExecutor asyncExecutor;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private CustomFieldsService customFieldsService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OperationAuthorizer operationAuthorizer;

	private static final Logger LOGGER = LoggerFactory.getLogger(ScollectionServiceImpl.class);

	@Override
	public ActionResponseDTO<Collection> createCollection(Collection collection, boolean addToShelf, String resourceId, String taxonomyCode, boolean updateTaxonomyByCode, String parentId) throws Exception {
		Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			if (taxonomyCode != null) {
				addCollectionTaxonomy(collection, taxonomyCode, updateTaxonomyByCode);

			}
			collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), collection.getBuildType() == null ? WEB : collection.getBuildType().getValue() != null ? collection.getBuildType().getValue() : WEB));

			if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
				collection.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, FOLDER));
			} else {
				collection.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, SCOLLECTION));
			}

			this.getCollectionRepository().save(collection);
			if (resourceId != null && !resourceId.isEmpty()) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collectionItem = this.createCollectionItem(resourceId, collection.getGooruOid(), collectionItem, collection.getUser(), CollectionType.COLLECTION.getCollectionType(), false).getModel();
				Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
				collectionItems.add(collectionItem);
				collection.setCollectionItems(collectionItems);
			}

			if (addToShelf) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), null, collectionItem, collection.getUser(), CollectionType.SHElf.getCollectionType(), false).getModel());
			}
			this.getCollectionRepository().save(collection);
			if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
				if (collection.getDepthOfKnowledges() != null && collection.getDepthOfKnowledges().size() > 0) {
					collection.setDepthOfKnowledges(this.updateContentMeta(collection.getDepthOfKnowledges(), collection.getGooruOid(), collection.getUser(), DEPTH_OF_KNOWLEDGE));
				} else {
					collection.setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), collection.getGooruOid(), DEPTH_OF_KNOWLEDGE));
				}

				if (collection.getLearningSkills() != null && collection.getLearningSkills().size() > 0) {
					collection.setLearningSkills(this.updateContentMeta(collection.getLearningSkills(), collection.getGooruOid(), collection.getUser(), LEARNING_AND_INNOVATION_SKILLS));
				} else {
					collection.setLearningSkills(this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS), collection.getGooruOid(), LEARNING_AND_INNOVATION_SKILLS));
				}

				if (collection.getAudience() != null && collection.getAudience().size() > 0) {
					collection.setAudience(this.updateContentMeta(collection.getAudience(), collection.getGooruOid(), collection.getUser(), AUDIENCE));
				} else {
					collection.setAudience(this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), collection.getGooruOid(), AUDIENCE));
				}

				if (collection.getInstructionalMethod() != null && collection.getInstructionalMethod().size() > 0) {
					collection.setInstructionalMethod(this.updateContentMeta(collection.getInstructionalMethod(), collection.getGooruOid(), collection.getUser(), INSTRUCTIONAL_METHOD));
				} else {
					collection.setInstructionalMethod(this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), collection.getGooruOid(), INSTRUCTIONAL_METHOD));
				}
			}

			Collection parentCollection = collectionRepository.getCollectionByGooruOid(parentId, collection.getUser().getGooruUId());
			if (parentCollection != null) {
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), parentCollection.getGooruOid(), new CollectionItem(), collection.getUser(), CollectionType.FOLDER.getCollectionType(), false).getModel());
			}
			collection.setClusterUid(collection.getGooruOid());
			collection.setIsRepresentative(1);
			this.getCollectionRepository().save(collection);
			
			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				getAsyncExecutor().createVersion(collection, SCOLLECTION_CREATE, collection.getUser().getPartyUid());
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
			
		}
		
		try {
			getEventLogs(collection, null, collection.getUser(), true, false);
		} catch(Exception e){
			LOGGER.error("Error"+ e.getMessage());
		}

		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	public ActionResponseDTO<Collection> createCollection(Collection collection, boolean addToShelf, String resourceId, String parentId, User user) throws Exception {
		final Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), collection.getBuildType() == null ? WEB : collection.getBuildType().getValue() != null ? collection.getBuildType().getValue() : WEB));
			if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
				collection.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, FOLDER));
			} else {
				collection.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, SCOLLECTION));
			}
			if (collection.getSharing() != null && collection.getSharing().equalsIgnoreCase(PUBLIC)) {
				collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, PENDING));
				collection.setSharing(Sharing.ANYONEWITHLINK.getSharing());
			}
			this.getCollectionRepository().save(collection);

			if (resourceId != null && !resourceId.isEmpty()) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collectionItem = this.createCollectionItem(resourceId, collection.getGooruOid(), collectionItem, collection.getUser(), CollectionType.COLLECTION.getCollectionType(), false).getModel();
				Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
				collectionItems.add(collectionItem);
				collection.setCollectionItems(collectionItems);
			}
			if (addToShelf) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), null, collectionItem, collection.getUser(), CollectionType.SHElf.getCollectionType(), false).getModel());
			}
			this.getCollectionRepository().save(collection);
			if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
				if (collection.getDepthOfKnowledges() != null && collection.getDepthOfKnowledges().size() > 0) {
					collection.setDepthOfKnowledges(this.updateContentMeta(collection.getDepthOfKnowledges(), collection.getGooruOid(), collection.getUser(), DEPTH_OF_KNOWLEDGE));
				} else {
					collection.setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), collection.getGooruOid(), DEPTH_OF_KNOWLEDGE));
				}

				if (collection.getLearningSkills() != null && collection.getLearningSkills().size() > 0) {
					collection.setLearningSkills(this.updateContentMeta(collection.getLearningSkills(), collection.getGooruOid(), collection.getUser(), LEARNING_AND_INNOVATION_SKILLS ));
				} else {
					collection.setLearningSkills(this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS ), collection.getGooruOid(), LEARNING_AND_INNOVATION_SKILLS ));
				}

				if (collection.getAudience() != null && collection.getAudience().size() > 0) {
					collection.setAudience(this.updateContentMeta(collection.getAudience(), collection.getGooruOid(), collection.getUser(), AUDIENCE));
				} else {
					collection.setAudience(this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), collection.getGooruOid(), AUDIENCE));
				}

				if (collection.getInstructionalMethod() != null && collection.getInstructionalMethod().size() > 0) {
					collection.setInstructionalMethod(this.updateContentMeta(collection.getInstructionalMethod(), collection.getGooruOid(), collection.getUser(), INSTRUCTIONAL_METHOD));
				} else {
					collection.setInstructionalMethod(this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), collection.getGooruOid(), INSTRUCTIONAL_METHOD));
				}
			}
			if (collection.getSharing().equalsIgnoreCase(PUBLIC)) {
				UserSummary userSummary = this.getUserRepository().getSummaryByUid(user.getPartyUid());
				if (userSummary.getGooruUid() == null) {
					userSummary.setGooruUid(user.getPartyUid());
				}
				userSummary.setCollections((userSummary.getCollections() != null ? userSummary.getCollections() : 0) + 1);
				this.getUserRepository().save(userSummary);
				this.getUserRepository().flush();
			}

			Collection parentCollection = collectionRepository.getCollectionByGooruOid(parentId, collection.getUser().getGooruUId());
			if (parentCollection != null) {
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), parentCollection.getGooruOid(), new CollectionItem(), collection.getUser(), CollectionType.FOLDER.getCollectionType(), false).getModel());
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + parentCollection.getUser().getPartyUid() + "*");
			}

			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception ex) {
				LOGGER.debug(ex.getMessage());
			}
			if (collection.getCollectionType().equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType()) && collection.getUser() != null) {
				Map<String, String> data = new HashMap<String, String>();
				data.put(EVENT_TYPE, CustomProperties.EventMapping.FIRST_COLLECTION.getEvent());
				data.put(_GOORU_UID, collection.getUser().getGooruUId());
				data.put(ACCOUNT_TYPE_ID, collection.getUser().getAccountTypeId() != null ? collection.getUser().getAccountTypeId().toString() : null);
				// this.getMailAsyncExecutor().handleMailEvent(data);
				this.mailHandler.handleMailEvent(data);

			}
			getAsyncExecutor().createVersion(collection, SCOLLECTION_CREATE, user.getPartyUid());
			
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
			try {
				getEventLogs(collection.getCollectionItem(), true, false, user, collection.getCollectionItem().getCollection().getCollectionType());
			} catch(Exception e){
				LOGGER.debug(e.getMessage());
			}
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	protected void addCollectionTaxonomy(Collection collection, String taxonomyCode, boolean updateTaxonomyByCode) {
		String[] taxonomyCodes = taxonomyCode.split(",");
		Set<Code> codes = collection.getTaxonomySet();
		for (String codeId : taxonomyCodes) {
			Code newCode = null;
			if (updateTaxonomyByCode) {
				newCode = (Code) this.getTaxonomyRepository().findCodeByTaxCode(codeId);
			} else {
				newCode = (Code) this.getTaxonomyRepository().findCodeByCodeId(Integer.parseInt(codeId));
			}
			if (newCode != null) {
				if (codes != null && codes.size() > 0) {
					boolean isExisting = false;
					for (Code code : codes) {
						if (code.getCodeId().equals(newCode.getCodeId())) {
							isExisting = true;
							break;
						}
					}
					if (!isExisting) {
						codes.add(newCode);
					}
				} else {
					codes = new HashSet<Code>();
					codes.add(newCode);
				}
			}
		}
		collection.setTaxonomySet(codes);
	}

	public ActionResponseDTO<Collection> updateCollection(Collection newCollection, String updateCollectionId, String taxonomyCode, String ownerUId, String creatorUId, boolean hasUnrestrictedContentAccess, String relatedContentId, boolean updateTaxonomyByCode, User apiCallerUser) throws Exception {
		String gooruUid = null;
		if (newCollection.getUser() != null && !userService.isContentAdmin(newCollection.getUser())) {
			gooruUid = newCollection.getUser().getGooruUId();
		}
		Collection collection = this.getCollectionByGooruOid(updateCollectionId, gooruUid);
		Errors errors = validateUpdateCollection(collection);
		if (!errors.hasErrors()) {

			if (relatedContentId != null) {
				Content assocContent = this.getContentRepositoryHibernate().findContentByGooruId(relatedContentId);
				Content content = this.getContentRepositoryHibernate().findContentByGooruId(updateCollectionId);

				if (assocContent != null && content != null) {
					ContentAssociation contentAssoc = new ContentAssociation();
					contentAssoc.setAssociateContent(assocContent);
					contentAssoc.setContent(content);
					contentAssoc.setModifiedDate(new Date());
					contentAssoc.setUser(collection.getUser());
					contentAssoc.setTypeOf(RELATED_CONTENT);
					this.getContentRepositoryHibernate().save(contentAssoc);
					collection.setContentAssociation(contentAssoc);
				}

			}

			if (taxonomyCode != null) {
				addCollectionTaxonomy(collection, taxonomyCode, updateTaxonomyByCode);
				this.getCollectionRepository().save(collection);
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyByCode(collection.getTaxonomySet(), taxonomyService));
			}

			if (newCollection.getVocabulary() != null) {
				collection.setVocabulary(newCollection.getVocabulary());
			}
			if ((newCollection.getBuildType() != null && newCollection.getBuildType().getValue() != null) && (newCollection.getBuildType().getValue() == WEB || newCollection.getBuildType().getValue() == IPAD)) {
				collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), newCollection.getBuildType().getValue()));
			}

			if (newCollection.getTitle() != null) {
				collection.setTitle(newCollection.getTitle());
			}
			if (newCollection.getDescription() != null) {
				collection.setDescription(newCollection.getDescription());
			}
			if (newCollection.getNarrationLink() != null) {
				collection.setNarrationLink(newCollection.getNarrationLink());
			}
			if (newCollection.getEstimatedTime() != null) {
				collection.setEstimatedTime(newCollection.getEstimatedTime());
			}
			if (newCollection.getNotes() != null) {
				collection.setNotes(newCollection.getNotes());
			}
			if (newCollection.getGoals() != null) {
				collection.setGoals(newCollection.getGoals());
			}
			if (newCollection.getKeyPoints() != null) {
				collection.setGoals(newCollection.getKeyPoints());
			}
			if (newCollection.getLanguage() != null) {
				collection.setLanguage(newCollection.getLanguage());
			}
			if (newCollection.getGrade() != null) {
				collection.setGrade(newCollection.getGrade());
			}
			if (newCollection.getLanguageObjective() != null) {
				collection.setLanguageObjective(newCollection.getLanguageObjective());
			}
			if (newCollection.getIdeas() != null) {
				collection.setIdeas(newCollection.getIdeas());
			}
			if (newCollection.getQuestions() != null) {
				collection.setQuestions(newCollection.getQuestions());
			}
			if (newCollection.getPerformanceTasks() != null) {
				collection.setPerformanceTasks(newCollection.getPerformanceTasks());
			}
			if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
				if (newCollection.getDepthOfKnowledges() != null && newCollection.getDepthOfKnowledges().size() > 0) {
					collection.setDepthOfKnowledges(this.updateContentMeta(newCollection.getDepthOfKnowledges(), updateCollectionId, apiCallerUser, DEPTH_OF_KNOWLEDGE));
				} else {
					collection.setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), updateCollectionId, DEPTH_OF_KNOWLEDGE));
				}

				if (newCollection.getLearningSkills() != null && newCollection.getLearningSkills().size() > 0) {
					collection.setLearningSkills(this.updateContentMeta(newCollection.getLearningSkills(), updateCollectionId, apiCallerUser, LEARNING_AND_INNOVATION_SKILLS ));
				} else {
					collection.setLearningSkills(this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS ), updateCollectionId, LEARNING_AND_INNOVATION_SKILLS ));
				}

				if (newCollection.getAudience() != null && newCollection.getAudience().size() > 0) {
					collection.setAudience(this.updateContentMeta(newCollection.getAudience(), updateCollectionId, apiCallerUser, AUDIENCE));
				} else {
					collection.setAudience(this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), updateCollectionId, AUDIENCE));
				}

				if (newCollection.getInstructionalMethod() != null && newCollection.getInstructionalMethod().size() > 0) {
					collection.setInstructionalMethod(this.updateContentMeta(newCollection.getInstructionalMethod(), updateCollectionId, apiCallerUser, INSTRUCTIONAL_METHOD));
				} else {
					collection.setInstructionalMethod(this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), updateCollectionId, INSTRUCTIONAL_METHOD));
				}
			}
			if (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {

				if (!newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
					collection.setPublishStatus(null);
				}
				if (newCollection.getSharing().equalsIgnoreCase(PUBLIC) && !userService.isContentAdmin(apiCallerUser)) {
					collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, PENDING));
					newCollection.setSharing(collection.getSharing());
				}
				if (newCollection.getSharing().equalsIgnoreCase(PUBLIC) && userService.isContentAdmin(apiCallerUser)) {
					collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, REVIEWED));
				}

				if (collection.getSharing().equalsIgnoreCase(PUBLIC) && newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
					UserSummary userSummary = this.getUserRepository().getSummaryByUid(apiCallerUser.getPartyUid());
					userSummary.setCollections(userSummary.getCollections() <= 0 ? 0 :  (userSummary.getCollections() - 1));
					this.getUserRepository().save(userSummary);
					this.getUserRepository().flush();
				} else if (!collection.getSharing().equalsIgnoreCase(PUBLIC) && newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
					UserSummary userSummary = this.getUserRepository().getSummaryByUid(apiCallerUser.getPartyUid());
					if (userSummary.getGooruUid() == null) {
						userSummary.setGooruUid(apiCallerUser.getPartyUid());
					}
					userSummary.setCollections((userSummary.getCollections() != null ? userSummary.getCollections() : 0) + 1);
					this.getUserRepository().save(userSummary);
					this.getUserRepository().flush();
				}
				collection.setSharing(newCollection.getSharing());

				this.getCollectionRepository().save(collection);
				this.getCollectionRepository().flush();
				List<String> parenFolders = this.getParentCollection(collection.getGooruOid(), apiCallerUser.getPartyUid(), false);
				for (String folderGooruOid : parenFolders) {
					updateFolderSharing(folderGooruOid);
				}
				updateResourceSharing(newCollection.getSharing(), collection);
			}

			if (hasUnrestrictedContentAccess) {
				if (creatorUId != null) {
					User user = getUserService().findByGooruId(creatorUId);
					collection.setCreator(user);
				}
				if (ownerUId != null) {
					User user = getUserService().findByGooruId(ownerUId);
					collection.setUser(user);
				}
				if (newCollection.getNetwork() != null) {
					collection.setNetwork(newCollection.getNetwork());
				}
			}

			if (newCollection.getMailNotification() != null) {
				collection.setMailNotification(newCollection.getMailNotification());
			}

			this.getCollectionRepository().save(collection);
			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	public List<ContentMetaDTO> updateContentMeta(List<ContentMetaDTO> newDepthOfKnowledges, String collectionId, User apiCaller, String type) {
		for (ContentMetaDTO newMeta : newDepthOfKnowledges) {
			if (this.getCustomTableRepository().getValueByDisplayName(newMeta.getValue(), type) != null) {
				ContentMetaAssociation contentMetaAssociation = this.getCollectionRepository().getContentMetaByValue(newMeta.getValue(), collectionId);
				if (contentMetaAssociation == null && newMeta.getSelected()) {
					contentMetaAssociation = new ContentMetaAssociation();
					contentMetaAssociation.setValue(newMeta.getValue());
					contentMetaAssociation.setAssociationType(this.getCustomTableRepository().getCustomTableValue("content_association_type", type));
					contentMetaAssociation.setUser(apiCaller);
					contentMetaAssociation.setContent(this.getContentRepositoryHibernate().findContentByGooruId(collectionId));
					contentMetaAssociation.setAssociatedDate(new Date(System.currentTimeMillis()));
					this.getCollectionRepository().save(contentMetaAssociation);
				} else {
					if (contentMetaAssociation != null && !newMeta.getSelected()) {
						this.getCollectionRepository().remove(contentMetaAssociation);
					}
				}
			}
		}

		return this.setContentMetaAssociation(this.getContentMetaAssociation(type), collectionId, type);
	}

	@Override
	public void deleteCollection(String collectionId, User user) {
		Collection collection = this.getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, _COLLECTION);
			if(this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId, user)){
				try {
					revisionHistoryService.createVersion(collection, SCOLLECTION_DELETE);
				} catch (Exception ex) {
					LOGGER.debug(ex.getMessage());
				}
				try {
					indexProcessor.index(collection.getGooruOid(), IndexProcessor.DELETE, SCOLLECTION);
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
				}

				List<CollectionItem> collectionItems = this.getCollectionRepository().getCollectionItemByAssociation(collectionId, null, null);
				try {
					getEventLogs(collection.getCollectionItem(), user, collection.getCollectionType());
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
				}

				for (CollectionItem item : collectionItems) {
					this.deleteCollectionItem(item.getCollectionItemId(), user);
					if (item.getAssociatedUser() != null && !item.getAssociatedUser().getPartyUid().equals(user.getPartyUid())) {
						getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + item.getAssociatedUser().getPartyUid() + "*");
					}
				}
				if (collection != null && collection.getUser() != null && collection.getSharing().equalsIgnoreCase(PUBLIC)) {
					UserSummary userSummary = this.getUserRepository().getSummaryByUid(collection.getUser().getPartyUid());
					if (userSummary != null && userSummary.getCollections() != null) {
						userSummary.setCollections(userSummary.getCollections() <= 0 ? 0 : (userSummary.getCollections() - 1));
						this.getUserRepository().save(userSummary);
					}
					this.getUserRepository().flush();
				}
				this.getCollectionRepository().remove(Collection.class, collection.getContentId());
				for (CollectionItem item : collectionItems) {
					Collection parentCollection = item.getCollection();
					if (parentCollection.getCollectionType().equals(FOLDER)) {
						updateFolderSharing(parentCollection.getGooruOid());
					}
				}
			} else {
				throw new UnauthorizedException("user don't have permission ");
			}
			
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");

	}
	
	@Override
	public List<Collection> getCollections(Map<String, String> filters, User user) {
		return this.getCollectionRepository().getCollections(filters, user);
	}

	@Override
	public ActionResponseDTO<CollectionItem> createCollectionItem(String resourceGooruOid, String collectionGooruOid, CollectionItem collectionItem, User user, String type, boolean isCreateQuestion) throws Exception {
		Collection collection = this.createMyShelfCollection(collectionGooruOid, user, type, collectionItem);
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceGooruOid);
		Errors errors = validateCollectionItem(collection, resource, collectionItem);
		if (!errors.hasErrors()) {
			collectionItem.setCollection(collection);

			if (collectionItem.getCollection() != null) {
				collectionItem.getCollection().setLastUpdatedUserUid(user.getPartyUid());
			}

			if (!isCreateQuestion && resource != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
				AssessmentQuestion assessmentQuestion = assessmentService.copyAssessmentQuestion(user, resource.getGooruOid());
				collectionItem.setResource(assessmentQuestion);
			} else {
				collectionItem.setResource(resource);
			}

			int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
			collectionItem.setItemSequence(sequence);
			collectionItem.getCollection().setItemCount(sequence);
			this.getCollectionRepository().save(collectionItem);
			this.getCollectionRepository().flush();
			try{
				getEventLogs(collectionItem, false, true, user, collectionItem.getCollection().getCollectionType());
			} catch(Exception e){
				LOGGER.debug(e.getMessage());
			}

			try {
				indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				if (collectionItem.getResource().getResourceType() != null && !collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(SCOLLECTION) && !collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(FOLDER)
						&& !collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(CLASSPAGE)) {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
				}

			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			List<String> parenFolders = this.getParentCollection(resource.getGooruOid(), collection.getUser().getPartyUid(), false);
			for (String parentFolder : parenFolders) {
				updateFolderSharing(parentFolder);
			}
			if (collectionItem.getCollection().getResourceType().getName().equalsIgnoreCase(SCOLLECTION) && !collectionItem.getCollection().getClusterUid().equalsIgnoreCase(collectionItem.getCollection().getGooruOid())) { 
				collectionItem.getCollection().setClusterUid(collectionItem.getCollection().getGooruOid());
				this.getCollectionRepository().save(collectionItem.getCollection());
			}
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		}

		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	private Collection createMyShelfCollection(String collectionGooruOid, User user, String type, CollectionItem collectionItem) {
		Collection collection = null;
		if (type != null && type.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
			collectionItem.setItemType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
		} else if (type != null && type.equalsIgnoreCase(COLLABORATOR)) {
			collectionItem.setItemType(COLLABORATOR);
		} else if (type != null && type.equalsIgnoreCase(CLASS)) {
			collectionItem.setItemType(CLASS);
		} else {
			collection = this.getCollectionByGooruOid(collectionGooruOid, null);
			if (collectionItem != null && collectionItem.getItemType() == null) { 
			  collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
			}
		}
		if (collectionGooruOid != null) {
			collection = this.getCollectionByGooruOid(collectionGooruOid, null);
		} else {
			collection = this.getCollectionRepository().getUserShelfByGooruUid(user.getGooruUId(), CollectionType.SHElf.getCollectionType());
		}
		if (collection == null && type != null && type.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
			collection = new Collection();
			collection.setTitle(MY_SHELF);
			collection.setCollectionType(CollectionType.SHElf.getCollectionType());
			collection.setGooruOid(UUID.randomUUID().toString());
			ContentType contentType = (ContentType) this.getCollectionRepository().get(ContentType.class, ContentType.RESOURCE);
			collection.setContentType(contentType);
			ResourceType resourceType = (ResourceType) this.getCollectionRepository().get(ResourceType.class, ResourceType.Type.SCOLLECTION.getType());
			collection.setResourceType(resourceType);
			collection.setLastModified(new Date(System.currentTimeMillis()));
			collection.setCreatedOn(new Date(System.currentTimeMillis()));
			collection.setSharing(Sharing.PRIVATE.getSharing());
			collection.setUser(user);
			collection.setOrganization(user.getPrimaryOrganization());
			collection.setCreator(user);
			collection.setDistinguish(Short.valueOf(ZERO));
			collection.setRecordSource(NOT_ADDED);
			collection.setIsFeatured(0);
			this.getCollectionRepository().save(collection);
		}
		return collection;
	}

	@Override
	public ActionResponseDTO<CollectionItem> updateCollectionItem(CollectionItem newcollectionItem, String collectionItemId, User user) throws Exception {
		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		Errors errors = validateUpdateCollectionItem(newcollectionItem);
		JSONObject itemData = new JSONObject();
		
		if (!errors.hasErrors()) {
			if (newcollectionItem.getNarration() != null) {
				collectionItem.setNarration(newcollectionItem.getNarration());
				itemData.put(NARRATION, newcollectionItem.getNarration());
			}
			if (newcollectionItem.getPlannedEndDate() != null) {
				collectionItem.setPlannedEndDate(newcollectionItem.getPlannedEndDate());
				itemData.put(PLANNED_END_DATE, newcollectionItem.getPlannedEndDate());
			}
			if (newcollectionItem.getNarrationType() != null) {
				collectionItem.setNarrationType(newcollectionItem.getNarrationType());
				itemData.put(NARRATION_TYPE, newcollectionItem.getNarrationType());
			}
			if (newcollectionItem.getStart() != null) {
				collectionItem.setStart(newcollectionItem.getStart());
				itemData.put(START, newcollectionItem.getStart());
			}
			if (newcollectionItem.getStop() != null) {
				collectionItem.setStop(newcollectionItem.getStop());
				itemData.put(STOP, newcollectionItem.getStop());
			}
			if (collectionItem.getResource() != null && collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
				collectionItem.getResource().setLastUpdatedUserUid(user.getPartyUid());
			}
			this.getCollectionRepository().save(collectionItem);
			this.getCollectionRepository().save(collectionItem.getCollection());
			try {
				getEventLogs(collectionItem, itemData, user);
				if (collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				} else {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
				}
				indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
		}
		
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public List<CollectionItem> getCollectionItems(String collectionId, Map<String, String> filters) {
		return this.getCollectionRepository().getCollectionItems(collectionId, filters);
	}

	@Override
	public CollectionItem getCollectionItem(String collectionItemId, boolean includeAdditionalInfo, User user, String rootNodeId) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, _COLLECTION_ITEM);
		if (includeAdditionalInfo) {
			collectionItem = this.setCollectionItemMoreData(collectionItem, rootNodeId);
		}
		return collectionItem;
	}

	@Override
	public void deleteCollectionItem(String collectionItemId, User user) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		if (collectionItem != null && collectionItem.getResource() != null) {
				try {
					getEventLogs(collectionItem, user, collectionItem.getCollection().getCollectionType());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				Collection collection = collectionItem.getCollection();
				this.getCollectionRepository().remove(CollectionItem.class, collectionItem.getCollectionItemId());

				collectionItem.getCollection().setLastUpdatedUserUid(user.getPartyUid());
				collectionItem.getCollection().setLastModified(new Date(System.currentTimeMillis()));
				collectionItem.getCollection().setItemCount((collectionItem.getCollection().getItemCount() == null || (collectionItem.getCollection().getItemCount() != null && collectionItem.getCollection().getItemCount() == 0)) ? 0 : collectionItem.getCollection().getItemCount() - 1);
				reOrderCollectionItems(collection, collectionItemId);
				try {
					if (collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.DELETE, SCOLLECTION);
					} else {
						indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
					}
					indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
				}
				if (collectionItem.getCollection().getResourceType().getName().equalsIgnoreCase(SCOLLECTION) && !collectionItem.getCollection().getClusterUid().equalsIgnoreCase(collectionItem.getCollection().getGooruOid())) { 
					collectionItem.getCollection().setClusterUid(collectionItem.getCollection().getGooruOid());
					this.getCollectionRepository().save(collectionItem.getCollection());
				}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION_ITEM));

		}
	}
	
	@Override
	public ActionResponseDTO<CollectionItem> reorderCollectionItem(String collectionItemId, int newSequence) throws Exception {
		CollectionItem collectionItem = getCollectionRepository().getCollectionItemById(collectionItemId);
		Errors errors = validateReorderCollectionItem(collectionItem);
		if (!errors.hasErrors()) {
			Collection collection = getCollectionRepository().getCollectionByGooruOid(collectionItem.getCollection().getGooruOid(), null);

			Integer existCollectionItemSequence = collectionItem.getItemSequence();

			if (existCollectionItemSequence > newSequence) {
				for (CollectionItem ci : collection.getCollectionItems()) {

					if (ci.getItemSequence() >= newSequence && ci.getItemSequence() <= existCollectionItemSequence) {
						if (ci.getCollectionItemId().equalsIgnoreCase(collectionItem.getCollectionItemId())) {
							ci.setItemSequence(newSequence);
						} else {
							ci.setItemSequence(ci.getItemSequence() + 1);
						}
					}
				}

			} else if (existCollectionItemSequence < newSequence) {
				for (CollectionItem ci : collection.getCollectionItems()) {
					if (ci.getItemSequence() <= newSequence && existCollectionItemSequence <= ci.getItemSequence()) {
						if (ci.getCollectionItemId().equalsIgnoreCase(collectionItem.getCollectionItemId())) {
							if (collection.getCollectionItems().size() < newSequence) {
								ci.setItemSequence(collection.getCollectionItems().size());
							} else {
								ci.setItemSequence(newSequence);
							}
						} else {
							ci.setItemSequence(ci.getItemSequence() - 1);
						}
					}
				}
			}
			this.getCollectionRepository().save(collection);
		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public Collection getCollection(String collectionId, boolean includeMetaInfo, boolean includeCollaborator, boolean isContentFlag, User user, String merge, String rootNodeId, boolean isGat) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		boolean isCollaborator = this.getCollaboratorRepository().findCollaboratorById(collectionId, user.getGooruUId()) != null ? true : false;
		if (collection != null && (collection.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId()) || !collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || userService.isContentAdmin(user) || isCollaborator)) {
			if (includeMetaInfo) {
				this.setColletionMetaData(collection, user, merge, false, rootNodeId);
			}
			if (isGat) {
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyByCode(collection.getTaxonomySet(), taxonomyService));
			}
			if (collection.getUser() != null) {
				collection.getUser().setProfileImageUrl(this.getUserService().buildUserProfileImageUrl(collection.getUser()));
			}
			if (isContentFlag) {
				collection.setContentAssociation(this.contentAssociationRepository.getContentAssociationGooruOid(collectionId));
			}
			if (collection.getUser().getIdentities() != null) {
				Identity identity = collection.getUser().getIdentities().iterator().next();
				collection.getUser().setEmailId(identity.getExternalId());
			}
			User lastUserModified = this.getUserService().findByGooruId(collection.getLastUpdatedUserUid());
			Map<String, Object> lastUserModifiedMap = new HashMap<String, Object>();
			if (lastUserModified != null) {
				lastUserModifiedMap.put(USER_NAME, lastUserModified.getUsername());
				lastUserModifiedMap.put(GOORU_UID, lastUserModified.getGooruUId());
			}
			collection.setLastModifiedUser(lastUserModifiedMap);
			for (CollectionItem collectionItem : collection.getCollectionItems()) {
				if (collectionItem.getResource().getResourceType().getName().equalsIgnoreCase("assessment-question")) {
					collectionItem.getResource().setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), collectionItem.getResource().getGooruOid(), DEPTH_OF_KNOWLEDGE));
				} else {
					collectionItem.getResource().setMomentsOfLearning(this.setContentMetaAssociation(this.getContentMetaAssociation(MOMENTS_OF_LEARNING), collectionItem.getResource().getGooruOid(), MOMENTS_OF_LEARNING));
				}
				collectionItem.getResource().setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), collectionItem.getResource().getGooruOid(), EDUCATIONAL_USE));
				collectionItem.getResource().setRatings(this.setRatingsObj(this.getResourceRepository().getResourceSummaryById(collectionItem.getResource().getGooruOid())));
				collectionItem.getResource().setCustomFieldValues(this.getCustomFieldsService().getCustomFieldsValuesOfResource(collectionItem.getResource().getGooruOid()));
				collectionItem.setResource(getResourceService().setContentProvider(collectionItem.getResource()));
			}
			if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
				collection.setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), collectionId, DEPTH_OF_KNOWLEDGE));

				collection.setLearningSkills(this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS), collectionId, LEARNING_AND_INNOVATION_SKILLS));

				collection.setAudience(this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), collectionId, AUDIENCE));

				collection.setInstructionalMethod(this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), collectionId, INSTRUCTIONAL_METHOD));
			}
			if (merge != null) {
				Map<String, Object> permissions = new HashMap<String, Object>();
				if (merge.contains(PERMISSIONS)) {
					permissions.put(PERMISSIONS, this.getContentService().getContentPermission(collectionId, user));
				}
				if (merge.contains(REACTION_AGGREGATE)) {
					permissions.put(REACTION_AGGREGATE, this.getFeedbackService().getContentFeedbackAggregate(collectionId, REACTION));
				}
				if (merge.contains(COMMENT_COUNT)) {
					permissions.put(COMMENT_COUNT, this.getCommentRepository().getCommentCount(collection.getGooruOid(), null, "notdeleted"));
				}
				long collaboratorCount = this.getCollaboratorRepository().getCollaboratorsCountById(collectionId);
				permissions.put(COLLABORATOR_COUNT, collaboratorCount);
				permissions.put( IS_COLLABORATOR, isCollaborator);
				collection.setMeta(permissions);
				collection.setCustomFieldValues(this.getCustomFieldsService().getCustomFieldsValuesOfResource(collection.getGooruOid()));
			}

		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		return collection;
	}
	
	@Override
	public Map<String, Object> setRatingsObj(ResourceSummary resourceSummary) {
		Map<String, Object> ratings = new HashMap<String, Object>();
		if (resourceSummary != null) {
			ratings.put(AVERAGE, resourceSummary.getRatingStarAvg());
			ratings.put(COUNT, resourceSummary.getRatingStarCount());
			ratings.put("reviewCount", resourceSummary.getReviewCount());
		}
		return ratings;
	}

	@Override
	public List<ContentMetaDTO> getContentMetaAssociation(String type) {
		List<ContentMetaDTO> depthOfKnowledges = new ArrayList<ContentMetaDTO>();
		String cacheKey = "content_meta_association_type_" + type;
		String data = redisService.getValue(cacheKey);
		if (data == null) {
			List<CustomTableValue> customTableValues = this.getCustomTableRepository().getCustomTableValues(type);
			for (CustomTableValue customTableValue : customTableValues) {
				ContentMetaDTO depthOfknowledge = new ContentMetaDTO();
				depthOfknowledge.setValue(customTableValue.getDisplayName());
				depthOfknowledge.setSelected(false);
				depthOfKnowledges.add(depthOfknowledge);
			}
			redisService.putValue(cacheKey, JsonSerializer.serialize(depthOfKnowledges, FORMAT_JSON));
		} else {
			depthOfKnowledges = JsonDeserializer.deserialize(data, new TypeReference<List<ContentMetaDTO>>() {
			});
		}
		return depthOfKnowledges;
	}

	@Override
	public List<ContentMetaDTO> setContentMetaAssociation(List<ContentMetaDTO> depthOfKnowledges, String collectionId, String type) {
		List<ContentMetaAssociation> metaAssociations = this.getCollectionRepository().getContentMetaById(collectionId, type);
		for (ContentMetaAssociation contentMetaAssociation : metaAssociations) {
			for (ContentMetaDTO depthOfKnowledge : depthOfKnowledges) {
				if (depthOfKnowledge.getValue().equalsIgnoreCase(contentMetaAssociation.getValue())) {
					depthOfKnowledge.setSelected(true);
				}
			}
		}
		return depthOfKnowledges;

	}

	@Override
	public Collection copyCollection(String collectionId, String title, boolean addToShelf, User user, String taxonomyCode, String grade, String parentId) throws Exception {
		Collection newCollection = new Collection();
		newCollection.setTitle(title);
		newCollection.setUser(user);
		newCollection.setGrade(grade);
		if (taxonomyCode != null) {
			addCollectionTaxonomy(newCollection, taxonomyCode, false);
		}
		return this.copyCollection(collectionId, newCollection, addToShelf, parentId, user);
	}

	@Override
	public User addCollaborator(String collectionId, User user, String collaboratorId, String collaboratorOperation) {
		Collection collection = this.getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, _COLLECTION);
		if (collaboratorId != null) {
			List<String> collaboratorsList = Arrays.asList(collaboratorId.split("\\s*,\\s*"));
			for (User collaborator : getUserService().findByIdentities(collaboratorsList)) {
				if (getUserService().checkCollaboratorsPermission(collectionId, collaborator, SCOLLECTION)) {
					return collectionUtil.updateNewCollaborators(collection, collaboratorsList, user, COLLECTION_COLLABORATE, collaboratorOperation);
				} else {
					throw new NotFoundException("Invalid Collaborator");
				}
			}
		}

		return null;
	}

	@Override
	public List<User> getCollaborators(String collectionId) {
		Collection collection = this.getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, _COLLECTION);
		return this.learnguideRepository.findCollaborators(collectionId, null);
	}

	private Collection setColletionMetaData(Collection collection, User user, String merge, boolean ignoreUserTaxonomyPreference, String rootNodeId) {
		if (collection != null) {
			Set<String> acknowledgement = new HashSet<String>();
			ResourceMetaInfo collectionMetaInfo = new ResourceMetaInfo();
			collectionMetaInfo.setCourse(this.getCourse(collection.getTaxonomySet()));
			collectionMetaInfo.setStandards(this.getStandards(collection.getTaxonomySet(), ignoreUserTaxonomyPreference, rootNodeId));
			if (collection.getVocabulary() != null) {
				collectionMetaInfo.setVocabulary(Arrays.asList(collection.getVocabulary().split("\\s*,\\s*")));
			}
			collection.setMetaInfo(collectionMetaInfo);
			if (collection.getCollectionItems() != null) {
				for (CollectionItem collectionItem : collection.getCollectionItems()) {
					if (collectionItem.getResource() != null && collectionItem.getResource().getResourceSource() != null && collectionItem.getResource().getResourceSource().getAttribution() != null) {
						acknowledgement.add(collectionItem.getResource().getResourceSource().getAttribution());

						if (collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
							String duration = getResourceCassandraService().get(collectionItem.getResource().getGooruOid(), RESOURCE_METADATA_DURATION);
							if (duration != null) {
								collectionItem.getResource().setDurationInSec(duration);
							}
						}
					}
					if ((merge != null && merge.contains(REACTION)) && (collectionItem.getResource() != null)) {
						Map<String, Object> resourcePermissions = new HashMap<String, Object>();
						resourcePermissions.put(REACTION, this.getFeedbackService().getContentFeedbacks(REACTION, null, collectionItem.getResource().getGooruOid(), collection.getUser().getPartyUid(), null, null, null));
						collectionItem.getResource().setMeta(resourcePermissions);
					}

					this.setCollectionItemMoreData(collectionItem, rootNodeId);
				}
				collectionMetaInfo.setAcknowledgement(acknowledgement);
			}

		}
		return collection;
	}

	@Override
	public List<CollectionItem> setCollectionItemMetaInfo(List<CollectionItem> collectionItems, String rootNodeId) {
		if (collectionItems != null) {
			for (CollectionItem collectionItem : collectionItems) {
				if (collectionItem.getResource() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					collectionItem.setCourse(this.getCourse(collectionItem.getResource().getTaxonomySet()));
					collectionItem.setStandards(this.getStandards(collectionItem.getResource().getTaxonomySet(), false, rootNodeId));
					List<CollectionItem> collectionItemCount = this.getCollectionItems(collectionItem.getResource().getGooruOid(), new HashMap<String, String>());
					collectionItem.setResourceCount(collectionItemCount.size());
				}
			}
		}
		return collectionItems;
	}

	private CollectionItem setCollectionItemMoreData(CollectionItem collectionItem, String rootNodeId) {
		if (collectionItem.getResource() != null) {
			if (collectionItem.getResource().getResourceType().getName().equals(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
				collectionItem.setQuestionInfo(this.getAssessmentService().getQuestion(collectionItem.getResource().getGooruOid()));
			}
			if (collectionItem.getResource().getResourceType().getName().equals(ResourceType.Type.TEXTBOOK.getType())) {
				Textbook textbook = this.getResourceRepository().findTextbookByContentGooruId(collectionItem.getResource().getGooruOid());
				if (textbook != null) {
					collectionItem.setDocumentid(textbook.getDocumentId());
					collectionItem.setDocumentkey(textbook.getDocumentKey());
				}
			}

			collectionItem.setStandards(this.getStandards(collectionItem.getResource().getTaxonomySet(), false, rootNodeId));
		}

		return collectionItem;
	}

	@Override
	public Set<String> getCourse(Set<Code> taxonomySet) {
		Set<String> course = null;
		if (taxonomySet != null) {
			course = new HashSet<String>();
			for (Code code : taxonomySet) {
				if (code.getDepth() == 2 && code.getRootNodeId() != null && code.getRootNodeId().toString().equalsIgnoreCase(Code.GOORU_TAXONOMY_CODE_ID)) {
					course.add(code.getLabel());
				}
			}
		}
		return course;
	}

	@Override
	public List<StandardFo> getStandards(Set<Code> taxonomySet, boolean ignoreUserTaxonomyPreference, String rootNodeId) {
		List<StandardFo> standards = null;
		if (taxonomySet != null) {
			standards = new ArrayList<StandardFo>();
			if (!ignoreUserTaxonomyPreference) {
				String taxonomyPreference = rootNodeId != null && !rootNodeId.equalsIgnoreCase(Code.GOORU_TAXONOMY_CODE_ID) ? rootNodeId : UserGroupSupport.getTaxonomyPreference();
				for (Code code : taxonomySet) {
					if (code.getRootNodeId() != null && taxonomyPreference != null && taxonomyPreference.contains(code.getRootNodeId().toString())) {
						standards.add(getStandards(code));
					}
				}
			} else {
				for (Code code : taxonomySet) {
					if (code.getRootNodeId() != null && !code.getRootNodeId().toString().equalsIgnoreCase(Code.GOORU_TAXONOMY_CODE_ID)) {
						standards.add(getStandards(code));
					}
				}
			}

		}
		return standards;
	}

	private StandardFo getStandards(Code code) {
		StandardFo standard = new StandardFo();
		if (code.getCommonCoreDotNotation() != null && !code.getCommonCoreDotNotation().equals("")) {
			standard.setCode(code.getCommonCoreDotNotation().replace(".--", " "));
		} else if (code.getdisplayCode() != null && !code.getdisplayCode().equals("")) {
			standard.setCode(code.getdisplayCode().replace(".--", " "));
		}
		if (code.getLabel() != null && !code.getLabel().equals("")) {
			standard.setDescription(code.getLabel());
		} else {
			standard.setDescription(BLANK + code.getCode());
		}
		standard.setCodeId(code.getCodeId());
		return standard;
	}

	@Override
	public List<Collection> getResourceMoreInfo(String resourceGooruOid) {
		return this.getCollectionRepository().getCollectionsByResourceId(resourceGooruOid);
	}

	@Override
	public Collection getCollectionByGooruOid(String gooruOid, String gooruUid) {
		return getCollectionRepository().getCollectionByGooruOid(gooruOid, gooruUid);
	}

	@Override
	public CollectionItem getCollectionItemById(String collectionItemId) {
		return getCollectionRepository().getCollectionItemById(collectionItemId);
	}

	@Override
	public Collection updateCollectionMetadata(String collectionId, String creatorUId, String ownerUId, boolean hasUnrestrictedContentAccess, MultiValueMap<String, String> data, User apiCallerUser) {
		Collection collection = this.getCollectionByGooruOid(collectionId, null);
		
		JSONObject jsonItemdata = new JSONObject();
		rejectIfNull(collection, GL0056, _COLLECTION);
		Boolean taxonomyByCode = false;
		String taxonomyCode = data.getFirst(TAXONOMY_CODE);
		String title = data.getFirst(TITLE);
		String description = data.getFirst(DESCRIPTION);
		String grade = data.getFirst(GRADE);
		String sharing = data.getFirst(SHARING);
		String narrationLink = data.getFirst(NARRATION_LINK);
		String vocabulary = data.getFirst(VOCABULARY);
		String updateTaxonomyByCode = data.getFirst(UPDATE_TAXONOMY_BY_CODE);
		String action = data.getFirst(ACTION);
		String mediaType = data.getFirst(MEDIA_TYPE);
		String buildType = data.getFirst(BUILD_TYPE);

		if (isNotEmptyString(updateTaxonomyByCode) && updateTaxonomyByCode.equalsIgnoreCase(TRUE)) {
			taxonomyByCode = true;
		}

		if (isNotEmptyString(taxonomyCode)) {
			if (isNotEmptyString(action) && action.equalsIgnoreCase(DELETE)) {
				deleteCollectionTaxonomy(collection, taxonomyCode, taxonomyByCode);
			} else {
				addCollectionTaxonomy(collection, taxonomyCode, taxonomyByCode);
			}
			this.getCollectionRepository().save(collection);
			try {
				jsonItemdata.put( TAXONOMY_CODE , taxonomyCode);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		if (isNotEmptyString(buildType)) {
			try {
				jsonItemdata.put(BUILD_TYPE, buildType);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (buildType.equalsIgnoreCase(WEB) || buildType.equalsIgnoreCase(IPAD)) {
				collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), buildType));
			}
		}
		if (isNotEmptyString(mediaType)) {
			try {
				jsonItemdata.put(MEDIA_TYPE, mediaType);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setMediaType(mediaType);
		}
		if (isNotEmptyString(title)) {
			try {
				jsonItemdata.put(TITLE, title);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setTitle(title);
		}

		if (description != null) {
			try {
				jsonItemdata.put(DESCRIPTION, description);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setGoals(description);
		}
		collection.setLastUpdatedUserUid(apiCallerUser.getPartyUid());

		if (isNotEmptyString(sharing)) {
			try {
				jsonItemdata.put(SHARING, sharing);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setSharing(sharing);
			this.getCollectionRepository().save(collection);
			this.getCollectionRepository().flush();
			List<String> parenGooruOid = this.getParentCollection(collection.getGooruOid(), apiCallerUser.getPartyUid(), false);
			for (String folderGooruOid : parenGooruOid) {
				updateFolderSharing(folderGooruOid);
			}
			updateResourceSharing(sharing, collection);
		}

		if (isNotEmptyString(vocabulary)) {
			try {
				jsonItemdata.put(VOCABULARY, vocabulary);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setVocabulary(vocabulary);
		}
		if (data.containsKey(GRADE)) {
			try {
				jsonItemdata.put(GRADE, grade);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			saveOrUpdateCollectionGrade(grade, collection, false);
		}
		if (isNotEmptyString(narrationLink)) {
			try {
				jsonItemdata.put(NARRATION_LINK, narrationLink);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			collection.setNarrationLink(narrationLink);
		}

		if (hasUnrestrictedContentAccess) {
			if (creatorUId != null) {
				User user = getUserService().findByGooruId(creatorUId);
				collection.setCreator(user);
			}
			if (ownerUId != null) {
				User user = getUserService().findByGooruId(ownerUId);
				collection.setUser(user);
			}
		}
		this.setColletionMetaData(collection, null, null, true, null);
		this.getCollectionRepository().save(collection);
		this.getCollectionRepository().flush();
		try {
			indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
		try {
			getEventLogs(collection, jsonItemdata, apiCallerUser, false, true);
		} catch(Exception e){
			e.printStackTrace();
		}
		return collection;
	}

	private Boolean isNotEmptyString(String field) {
		return StringUtils.hasLength(field);
	}

	public void updateResourceSharing(String sharing, Collection collection) {
		Iterator<CollectionItem> iterator = collection.getCollectionItems().iterator();
		while (iterator.hasNext()) {
			CollectionItem collectionItem = iterator.next();
			if (!collectionItem.getResource().getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
				collectionItem.getResource().setSharing(sharing);
				this.getCollectionRepository().save(collectionItem);
			}
		}
	}

	@Override
	public CollectionItem updateCollectionItemMetadata(String collectionItemId, MultiValueMap<String, String> data, User apiCaller) {

		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);

		rejectIfNull(collectionItem, GL0056, _COLLECTION_ITEM);
		
		JSONObject jsonItemdata = new JSONObject();
		
		try {
			jsonItemdata.put(_ITEM_DATA , data);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		String narration = data.getFirst(NARRATION);
		String narrationType = data.getFirst(NARRATION_TYPE);
		String start = data.getFirst(START);
		String stop = data.getFirst(STOP);

		if (data.containsKey(NARRATION)) {
			collectionItem.setNarration(narration);
		}
		if (isNotEmptyString(narrationType)) {
			collectionItem.setNarrationType(narrationType);
		}
		if (data.containsKey(START)) {
			collectionItem.setStart(start);
		}
		if (data.containsKey(STOP)) {
			collectionItem.setStop(stop);
		}
		if (collectionItem.getResource() != null && collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
			collectionItem.getResource().setLastUpdatedUserUid(apiCaller.getPartyUid());
		}
		this.getCollectionRepository().save(collectionItem);
		try {
			indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
			indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
		} catch (Exception e) {
			LOGGER.debug("Error"+e.getMessage());
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		
		try {
			getEventLogs(collectionItem, jsonItemdata, apiCaller);
		} catch (JSONException e) {
			LOGGER.debug("Error"+e.getMessage());
		}
		return collectionItem;
	}

	@Override
	public CollectionItem copyCollectionItem(String collectionItemId, String collectionId) throws Exception {
		CollectionItem sourceCollectionItem = this.getCollectionItem(collectionItemId, false, null, null);
		rejectIfNull(sourceCollectionItem, GL0056, _COLLECTION_ITEM);
		
		CollectionItem destCollectionItem = new CollectionItem();
		Collection targetCollection = null;
		boolean hasSameCollection = false;
		if (collectionId != null) {
			targetCollection = this.getCollection(collectionId, false, false, false, sourceCollectionItem.getCollection().getUser(), null, null, false);
		}
		if (targetCollection == null) {
			targetCollection = sourceCollectionItem.getCollection();
		}
		if (targetCollection.getGooruOid().equalsIgnoreCase(sourceCollectionItem.getCollection().getGooruOid())) {
			hasSameCollection = true;
		}
		if (sourceCollectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
			AssessmentQuestion assessmentQuestion = assessmentService.copyAssessmentQuestion(sourceCollectionItem.getCollection().getUser(), sourceCollectionItem.getResource().getGooruOid());
			destCollectionItem.setResource(assessmentQuestion);
		} else {
			destCollectionItem.setResource(sourceCollectionItem.getResource());
			destCollectionItem.getResource().setCopiedResourceId(sourceCollectionItem.getCollectionItemId());
		}
		destCollectionItem.setItemType(sourceCollectionItem.getItemType());
		if (hasSameCollection) {
			resetCollectionItemSequence(sourceCollectionItem.getCollectionItemId(), targetCollection);
		}

		int sequence = targetCollection.getCollectionItems().size() > 0 ? hasSameCollection ? (sourceCollectionItem.getItemSequence() + 1) : (targetCollection.getCollectionItems().size() + 1) : 1;

		destCollectionItem.setCollection(targetCollection);
		destCollectionItem.setItemSequence(sequence);
		destCollectionItem.setNarration(sourceCollectionItem.getNarration());
		destCollectionItem.setNarrationType(sourceCollectionItem.getNarrationType());
		destCollectionItem.setStart(destCollectionItem.getStart());
		destCollectionItem.setStop(sourceCollectionItem.getStop());
		SessionContextSupport.putLogParameter(COLLECTION_ID, destCollectionItem.getCollection().getGooruOid());
		SessionContextSupport.putLogParameter(RESOURCE_ID, destCollectionItem.getResource().getGooruOid());
		this.getCollectionRepository().save(destCollectionItem);
		try{
			if(destCollectionItem != null){
				getEventLogs(destCollectionItem, false, false, destCollectionItem.getCollection() != null && destCollectionItem.getCollection().getUser() != null ?  destCollectionItem.getCollection().getUser() : null, destCollectionItem.getCollection() != null ? destCollectionItem.getCollection().getCollectionType() : null);
			}
		}catch(Exception e){
			LOGGER.debug("Error"+e.getMessage());
		}
		return destCollectionItem;
	}

	private void resetCollectionItemSequence(String collectionItemId, Collection collection) {
		int itemSequence = 1;
		int count = 1;
		for (CollectionItem item : collection.getCollectionItems()) {
			item.setItemSequence(count++);
		}

		for (CollectionItem item : collection.getCollectionItems()) {
			if (item.getCollectionItemId().equals(collectionItemId)) {
				itemSequence = item.getItemSequence() + 1;
				continue;
			}
			if (itemSequence != 1) {
				itemSequence++;
				item.setItemSequence(itemSequence);
			}
		}

		this.getCollectionRepository().save(collection);
	}

	@Override
	public ActionResponseDTO<CollectionItem> createResourceWithCollectionItem(String collectionId, String title, String description, String url, String start, String stop, String thumbnailImgSrc, String resourceType, String category, User user) throws Exception {
		Resource newResource = new Resource();
		newResource.setTitle(title);
		newResource.setDescription(description);
		newResource.setUrl(url);
		newResource.setThumbnail(thumbnailImgSrc);
		newResource.setCategory(category);
		return this.createResourceWithCollectionItem(collectionId, newResource, start, stop,null ,user);
	}

	@Override
	public CollectionItem buildCollectionItemFromInputParameters(String data, User user) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias(COLLECTION_ITEM, CollectionItem.class);
		return (CollectionItem) xstream.fromXML(data);
	}

	@Override
	public List<Collection> getMyCollection(Map<String, String> filters, User user) {
		return getCollectionRepository().getMyCollection(filters, user);
	}

	@Override
	public List<Collection> getMyCollection(String offset, String limit, String type, String filter, User user) {
		return this.getCollectionRepository().getMyCollection(offset, limit, type, filter, user);
	}

	protected void reOrderCollectionItems(Collection collection, String collectionItemId) {
		int resetSequence = 1;
		Set<CollectionItem> items = collection.getCollectionItems();
		for (CollectionItem item : items) {
			if (item.getCollectionItemId().equals(collectionItemId)) {
				items.remove(item);
				break;
			}
		}
		for (CollectionItem item : items) {
			item.setItemSequence(resetSequence++);
		}
		this.getCollectionRepository().saveAll(items);
	}

	private void deleteCollectionTaxonomy(Collection collection, String taxonomyCode, boolean updateTaxonomyByCode) {
		String[] taxonomyCodes = taxonomyCode.split(",");
		Set<Code> codes = collection.getTaxonomySet();
		Set<Code> removeCodes = new HashSet<Code>();
		for (String codeId : taxonomyCodes) {
			Code removeCode = null;
			if (updateTaxonomyByCode) {
				removeCode = (Code) this.getTaxonomyRepository().findCodeByTaxCode(codeId);
			} else {
				removeCode = (Code) this.getTaxonomyRepository().findCodeByCodeId(Integer.parseInt(codeId));
			}
			if (removeCode != null) {
				for (Code code : codes) {
					if (code.getCodeId().equals(removeCode.getCodeId())) {
						removeCodes.add(removeCode);
					}
				}

			}
			if (removeCodes.size() > 0) {
				codes.removeAll(removeCodes);
			}
		}
		collection.setTaxonomySet(codes);
	}

	private Errors validateCollection(Collection collection) throws Exception {
		Map<String, String> colletionType = new HashMap<String, String>();
		colletionType.put(LESSON, COLLECTION_TYPE);
		colletionType.put(SHELF, COLLECTION_TYPE);
		colletionType.put(COLLECTION, COLLECTION_TYPE);
		colletionType.put(QUIZ, COLLECTION_TYPE);
		colletionType.put(FOLDER, COLLECTION_TYPE);
		colletionType.put(ASSIGNMENT, COLLECTION_TYPE);
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfInvalidType(errors, collection.getCollectionType(), COLLECTION_TYPE, GL0007, generateErrorMessage(GL0007, COLLECTION_TYPE), colletionType);
		}
		return errors;
	}

	private Errors validateUpdateCollection(Collection collection) throws Exception {
		final Errors errors = new BindException(collection, COLLECTION);
		rejectIfNull(errors, collection, COLLECTION, GL0006, generateErrorMessage(GL0006, COLLECTION));
		return errors;
	}

	private Errors validateCollectionItem(Collection collection, Resource resource, CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED, COLLECTION_ITEM_TYPE);
		itemType.put(COLLABORATOR, COLLECTION_ITEM_TYPE);
		itemType.put("class", COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, collection, COLLECTION, GL0056, generateErrorMessage(GL0056, COLLECTION));
			rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
			rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		}
		return errors;
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

	private Errors validateReorderCollectionItem(CollectionItem collectionItem) throws Exception {
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, collectionItem, COLLECTION_ITEM, GL0056, generateErrorMessage(GL0056, COLLECTION_ITEM));
		}
		return errors;
	}

	@Override
	public List<CollectionItem> getCollectionItemByResourceId(Long resourceId) {
		return collectionRepository.getCollectionItemByResourceId(resourceId);

	}

	@Override
	public ActionResponseDTO<Collection> updateCollection(Collection newCollection, String updateCollectionId, String ownerUId, String creatorUId, boolean hasUnrestrictedContentAccess, String relatedContentId, User updateUser) throws Exception {
		String gooruUid = null;
		if (newCollection.getUser() != null && !userService.isContentAdmin(updateUser)) {
			gooruUid = newCollection.getUser().getGooruUId();
		} 
		Collection collection = this.getCollectionByGooruOid(updateCollectionId, gooruUid);
		Errors errors = validateUpdateCollection(collection);
		JSONObject itemData = new JSONObject();
		if (!errors.hasErrors()) {

			if (relatedContentId != null) {
				Content assocContent = this.getContentRepositoryHibernate().findContentByGooruId(relatedContentId);
				Content content = this.getContentRepositoryHibernate().findContentByGooruId(updateCollectionId);

				if (assocContent != null && content != null) {
					ContentAssociation contentAssoc = new ContentAssociation();
					contentAssoc.setAssociateContent(assocContent);
					contentAssoc.setContent(content);
					contentAssoc.setModifiedDate(new Date());
					contentAssoc.setUser(collection.getUser());
					contentAssoc.setTypeOf(RELATED_CONTENT);
					this.getContentRepositoryHibernate().save(contentAssoc);
					collection.setContentAssociation(contentAssoc);
				}

			}

			if (newCollection.getTaxonomySet() != null) {
				itemData.put(TAXONOMY_SET, newCollection.getTaxonomySet());
				resourceService.saveOrUpdateResourceTaxonomy(collection, newCollection.getTaxonomySet());
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyByCode(collection.getTaxonomySet(), taxonomyService));
			}

			if (newCollection.getVocabulary() != null) {
				itemData.put(VOCABULARY, newCollection.getVocabulary());
				collection.setVocabulary(newCollection.getVocabulary());
			}
			if (newCollection.getBuildType() != null && newCollection.getBuildType().getValue() != null) {
				itemData.put(BUILD_TYPE, newCollection.getBuildType().getValue());
				if (newCollection.getBuildType().getValue().equalsIgnoreCase(WEB) || newCollection.getBuildType().getValue().equalsIgnoreCase(IPAD)) {
					collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), newCollection.getBuildType().getValue()));
				}
			}
			if (newCollection.getPublishStatus() != null && newCollection.getPublishStatus().getValue() != null) {
				itemData.put(PUBLISH_STATUS, newCollection.getPublishStatus().getValue());
				if (newCollection.getPublishStatus().getValue().equalsIgnoreCase(REVIEWED)) {
					collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, newCollection.getPublishStatus().getValue()));
				}
			}
			if (newCollection.getMediaType() != null) {
				itemData.put(MEDIA_TYPE, newCollection.getMediaType());
				collection.setMediaType(newCollection.getMediaType());
			}
			if (newCollection.getTitle() != null) {
				itemData.put(TITLE, newCollection.getTitle());
				collection.setTitle(newCollection.getTitle());
			}

			if (newCollection.getMailNotification() != null) {
				itemData.put(MAIL_NOTIFICATION, newCollection.getMailNotification());
				collection.setMailNotification(newCollection.getMailNotification());
			}
			if (newCollection.getDescription() != null) {
				itemData.put(DESCRIPTION, newCollection.getDescription());
				collection.setDescription(newCollection.getDescription());
			}
			if (newCollection.getNarrationLink() != null) {
				itemData.put(NARRATION_LINK, newCollection.getNarrationLink());
				collection.setNarrationLink(newCollection.getNarrationLink());
			}
			if (newCollection.getEstimatedTime() != null) {
				itemData.put(ESTIMATED_TIME, newCollection.getEstimatedTime());
				collection.setEstimatedTime(newCollection.getEstimatedTime());
			}
			if (newCollection.getNotes() != null) {
				itemData.put(NOTES, newCollection.getNotes());
				collection.setNotes(newCollection.getNotes());
			}
			if (newCollection.getGoals() != null) {
				itemData.put(GOALS, newCollection.getGoals());
				collection.setGoals(newCollection.getGoals());
			}
			if (newCollection.getKeyPoints() != null) {
				itemData.put(KEYPOINTS, newCollection.getKeyPoints());
				collection.setKeyPoints(newCollection.getKeyPoints());
			}
			if (newCollection.getLanguage() != null) {
				itemData.put(LANGUAGE, newCollection.getLanguage());
				collection.setLanguage(newCollection.getLanguage());
			}
			if (newCollection.getGrade() != null) {
				itemData.put(GRADE, newCollection.getGrade());
				collection.setGrade(newCollection.getGrade());
			}
			if (newCollection.getLanguageObjective() != null) {
				itemData.put(LANGUAGE_OBJECTIVE, newCollection.getLanguageObjective());
				collection.setLanguageObjective(newCollection.getLanguageObjective());
			}
			if (newCollection.getIdeas() != null) {
				itemData.put(IDEAS, newCollection.getIdeas());
				collection.setIdeas(newCollection.getIdeas());
			}
			if (newCollection.getQuestions() != null) {
				itemData.put(QUESTIONS, newCollection.getQuestions());
				collection.setQuestions(newCollection.getQuestions());
			}
			if (newCollection.getPerformanceTasks() != null) {
				itemData.put(PERFORMANCE_TASKS, newCollection.getPerformanceTasks());
				collection.setPerformanceTasks(newCollection.getPerformanceTasks());
			}
			if (collection.getCollectionType().equalsIgnoreCase(COLLECTION)) {
				if (newCollection.getDepthOfKnowledges() != null && newCollection.getDepthOfKnowledges().size() > 0) {
					itemData.put(DEPTHOFKNOWLEDGES, newCollection.getDepthOfKnowledges());
					collection.setDepthOfKnowledges(this.updateContentMeta(newCollection.getDepthOfKnowledges(), updateCollectionId, updateUser, DEPTH_OF_KNOWLEDGE));
				} else {
					collection.setDepthOfKnowledges(this.setContentMetaAssociation(this.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), updateCollectionId, DEPTH_OF_KNOWLEDGE));
				}

				if (newCollection.getLearningSkills() != null && newCollection.getLearningSkills().size() > 0) {
					itemData.put(LEARNING_SKILLS, newCollection.getLearningSkills());
					collection.setLearningSkills(this.updateContentMeta(newCollection.getLearningSkills(), updateCollectionId, updateUser, LEARNING_AND_INNOVATION_SKILLS));
				} else {
					collection.setLearningSkills(this.setContentMetaAssociation(this.getContentMetaAssociation(LEARNING_AND_INNOVATION_SKILLS), updateCollectionId, LEARNING_AND_INNOVATION_SKILLS));
				}

				if (newCollection.getAudience() != null && newCollection.getAudience().size() > 0) {
					itemData.put(AUDIENCE, newCollection.getAudience());
					collection.setAudience(this.updateContentMeta(newCollection.getAudience(), updateCollectionId, updateUser, AUDIENCE));
				} else {
					collection.setAudience(this.setContentMetaAssociation(this.getContentMetaAssociation(AUDIENCE), updateCollectionId, AUDIENCE));
				}

				if (newCollection.getInstructionalMethod() != null && newCollection.getInstructionalMethod().size() > 0) {
					itemData.put(INSTRUCTIONALMETHOD, newCollection.getInstructionalMethod());
					collection.setInstructionalMethod(this.updateContentMeta(newCollection.getInstructionalMethod(), updateCollectionId, updateUser, INSTRUCTIONAL_METHOD));
				} else {
					collection.setInstructionalMethod(this.setContentMetaAssociation(this.getContentMetaAssociation(INSTRUCTIONAL_METHOD), updateCollectionId, INSTRUCTIONAL_METHOD));
				}
			}
			if (newCollection.getSharing() != null && (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing()))) {
				
				itemData.put(SHARING, newCollection.getSharing());

				if (!newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
					collection.setPublishStatus(null);
				}
				if (newCollection.getSharing().equalsIgnoreCase(PUBLIC) && !userService.isContentAdmin(updateUser)) {
					collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, PENDING));
					newCollection.setSharing(collection.getSharing());
				}
				if (newCollection.getSharing().equalsIgnoreCase(PUBLIC) && userService.isContentAdmin(updateUser)) {
					collection.setPublishStatus(this.getCustomTableRepository().getCustomTableValue(_PUBLISH_STATUS, REVIEWED));
				}
				if (collection.getSharing().equalsIgnoreCase(PUBLIC) && (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing()))) {
					UserSummary userSummary = this.getUserRepository().getSummaryByUid(collection.getUser().getPartyUid());
					if (userSummary.getGooruUid() != null) {
						userSummary.setCollections(userSummary.getCollections() <= 0 ? 0 : (userSummary.getCollections() - 1));
						this.getUserRepository().save(userSummary);
						this.getUserRepository().flush();
					}
				} else if (hasUnrestrictedContentAccess &&  !collection.getSharing().equalsIgnoreCase(PUBLIC) && newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
					UserSummary userSummary = this.getUserRepository().getSummaryByUid(collection.getUser().getPartyUid());
					if (userSummary.getGooruUid() == null) {
						userSummary.setGooruUid(collection.getUser().getPartyUid());
					}
					userSummary.setCollections((userSummary.getCollections() != null ? userSummary.getCollections() : 0) + 1);
					this.getUserRepository().save(userSummary);
					this.getUserRepository().flush();
				}
				if (newCollection.getSharing().equalsIgnoreCase(PRIVATE)) {
					List<CollectionItem> associations = this.getCollectionRepository().getCollectionItemByAssociation(collection.getGooruOid(), null, CLASSPAGE);
					for (CollectionItem item : associations) {
						this.deleteCollectionItem(item.getCollectionItemId(), updateUser);
					}
				}
				collection.setSharing(newCollection.getSharing());
				List<String> parenFolders = this.getParentCollection(collection.getGooruOid(), updateUser.getPartyUid(), false);
				for (String folderGooruOid : parenFolders) {
					updateFolderSharing(folderGooruOid);
				}
				updateResourceSharing(newCollection.getSharing(), collection);
			}

			collection.setLastUpdatedUserUid(updateUser.getPartyUid());

			if (hasUnrestrictedContentAccess) {
				if (creatorUId != null) {
					User user = getUserService().findByGooruId(creatorUId);
					collection.setCreator(user);
				}
				if (ownerUId != null) {
					User user = getUserService().findByGooruId(ownerUId);
					collection.setUser(user);
				}
				if (newCollection.getNetwork() != null) {
					collection.setNetwork(newCollection.getNetwork());
				}
			}

			this.getCollectionRepository().save(collection);
			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
		}
		
		try{
			getEventLogs(collection, itemData, collection.getUser(), false, true);
		}catch(Exception e){
			LOGGER.debug(e.getMessage());
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	public CollectionItem getCollectionItem(String collectionItemId, String includeAdditionalInfo, User user, String rootNodeId) {

		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, _COLLECTION_ITEM);
		if (includeAdditionalInfo.equalsIgnoreCase(TRUE)) {
			collectionItem = this.setCollectionItemMoreData(collectionItem, rootNodeId);
		}
		return collectionItem;
	}

	@Override
	public Collection copyCollection(String collectionId, Collection newCollection, boolean addToShelf, String parentId, User user) throws Exception {

		Collection sourceCollection = this.getCollection(collectionId, false, false, false, user, null, null, false);
		Collection destCollection = null;
		if (sourceCollection != null) {
			destCollection = new Collection();
			if (newCollection.getTitle() != null) {
				destCollection.setTitle(newCollection.getTitle());
			} else {
				destCollection.setTitle(sourceCollection.getTitle());
			}
			destCollection.setGoals(sourceCollection.getGoals());
			destCollection.setCopiedResourceId(sourceCollection.getGooruOid());
			destCollection.setCollectionType(sourceCollection.getCollectionType());
			destCollection.setResourceFormat(sourceCollection.getResourceFormat());
			destCollection.setDescription(sourceCollection.getDescription());
			destCollection.setNotes(sourceCollection.getNotes());
			destCollection.setLanguage(sourceCollection.getLanguage());
			destCollection.setThumbnail(sourceCollection.getThumbnail());
			if (newCollection.getGrade() != null) {
				destCollection.setGrade(newCollection.getGrade());
			}
			destCollection.setEstimatedTime(sourceCollection.getEstimatedTime());
			destCollection.setNarrationLink(sourceCollection.getNarrationLink());
			destCollection.setGooruOid(UUID.randomUUID().toString());
			destCollection.setContentType(sourceCollection.getContentType());
			destCollection.setResourceType(sourceCollection.getResourceType());
			destCollection.setLastModified(new Date(System.currentTimeMillis()));
			destCollection.setCreatedOn(new Date(System.currentTimeMillis()));
			if (newCollection != null && newCollection.getSharing() != null) {
				destCollection.setSharing(newCollection.getSharing());
			} else {
				destCollection.setSharing(addToShelf ? Sharing.ANYONEWITHLINK.getSharing() : sourceCollection.getSharing());
			}
			destCollection.setUser(user);
			destCollection.setOrganization(sourceCollection.getOrganization());
			destCollection.setCreator(sourceCollection.getCreator());
			destCollection.setDistinguish(sourceCollection.getDistinguish());
			destCollection.setIsFeatured(sourceCollection.getIsFeatured());
			SessionContextSupport.putLogParameter(SOURCE_COLLECTION_ID, sourceCollection.getGooruOid());
			SessionContextSupport.putLogParameter(TARGET_COLLECTION_ID, destCollection.getGooruOid());
			this.getCollectionRepository().save(destCollection);
			if (newCollection.getTaxonomySet() != null && newCollection.getTaxonomySet().size() > 0) {
				resourceService.saveOrUpdateResourceTaxonomy(destCollection, new HashSet<Code>(newCollection.getTaxonomySet()));
			}
			this.getCollectionRepository().save(destCollection);
			Iterator<CollectionItem> sourceItemIterator = sourceCollection.getCollectionItems().iterator();
			Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
			while (sourceItemIterator.hasNext()) {
				CollectionItem sourceItem = sourceItemIterator.next();
				CollectionItem destItem = new CollectionItem();
				if (sourceItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
					AssessmentQuestion assessmentQuestion = assessmentService.copyAssessmentQuestion(user, sourceItem.getResource().getGooruOid());
					destItem.setResource(assessmentQuestion);
				} else {
					destItem.setResource(sourceItem.getResource());
				}
				destItem.getResource().setCopiedResourceId(sourceItem.getCollectionItemId());
				destItem.setItemType(sourceItem.getItemType());
				destItem.setItemSequence(sourceItem.getItemSequence());
				destItem.setNarration(sourceItem.getNarration());
				destItem.setNarrationType(sourceItem.getNarrationType());
				destItem.setStart(sourceItem.getStart());
				destItem.setStop(sourceItem.getStop());
				destItem.setCollection(destCollection);
				this.getCollectionRepository().save(destItem);
				collectionItems.add(destItem);
			}
			destCollection.setCollectionItems(collectionItems);
			this.getCollectionRepository().save(destCollection);
			getAsyncExecutor().copyResourceFolder(sourceCollection, destCollection);
			if (addToShelf) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
				Collection myCollection = createMyShelfCollection(null, user,  CollectionType.SHElf.getCollectionType(), collectionItem);
				collectionItem.setCollection(myCollection);
				collectionItem.setResource(destCollection);
				int sequence = myCollection.getCollectionItems() != null ? myCollection.getCollectionItems().size() + 1 : 1;
				collectionItem.setItemSequence(sequence);
				collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
				this.getCollectionRepository().save(collectionItem);
			}

		}
		if (parentId != null) {
			Collection parentCollection = collectionRepository.getCollectionByGooruOid(parentId, user.getPartyUid());
			if (parentCollection != null) {
				destCollection.setCollectionItem(this.createCollectionItem(destCollection.getGooruOid(), parentCollection.getGooruOid(), new CollectionItem(), destCollection.getUser(), CollectionType.FOLDER.getCollectionType(), false).getModel());
			}
		}
		this.getCollectionRepository().save(destCollection);
		destCollection.setClusterUid(sourceCollection.getGooruOid());
		destCollection.setIsRepresentative(0);
		this.getCollectionRepository().save(destCollection);
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + destCollection.getUser().getPartyUid() + "*");
		
		try {
			if(destCollection != null){
				indexProcessor.index(destCollection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				getEventLogs(destCollection, null, user, false, false);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return destCollection;
	}

	@Override
	public ActionResponseDTO<CollectionItem> createResourceWithCollectionItem(String collectionId, Resource newResource,String start,String stop ,List<String> tags,User user) throws Exception {

		ActionResponseDTO<CollectionItem> response = null;
		ResourceSource resourceSource = null;
		String domainName = null;
		if (collectionId != null) {
			if (newResource.getUrl() != null && getResourceService().shortenedUrlResourceCheck(newResource.getUrl())) {
				throw new Exception("Cannot able to upload shortened URL resource.");
			}
			Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
			if (collection != null) {
				Resource resource = null;
				if (newResource.getUrl() != null && !newResource.getUrl().isEmpty() && newResource.getAttach() == null) {
					resource = this.getResourceRepository().findResourceByUrl(newResource.getUrl(), Sharing.PUBLIC.getSharing(), null);
					if (resource == null) {
						resource = this.getResourceRepository().findResourceByUrl(newResource.getUrl(), Sharing.PRIVATE.getSharing(), user.getGooruUId());
					}
				}
				if (resource != null && resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
					throw new AccessDeniedException("This is public resource, do  not have premission to edit this resource, edit via GAT");
				}

				String sharing = collection.getSharing();
				String title = newResource.getTitle().length() > 1000 ? newResource.getTitle().substring(0, 1000) : newResource.getTitle();
				if (resource == null) {
					resource = new Resource();
					resource.setGooruOid(UUID.randomUUID().toString());
					resource.setUser(user);
					resource.setTitle(title);
					if (newResource.getCategory() != null) {
						resource.setCategory(newResource.getCategory().toLowerCase());
					}
					if (newResource.getInstructional() != null) {
						CustomTableValue resourceCategory = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, newResource.getInstructional().getValue());
						resource.setInstructional(resourceCategory);
					}
					if (newResource.getResourceFormat() != null) {
						CustomTableValue resourcetype = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
						resource.setResourceFormat(resourcetype);
					}
					resource.setDescription(newResource.getDescription());
					License license = new License();
					license.setName(OTHER);
					resource.setLicense(license);
					resource.setRecordSource(Resource.RecordSource.COLLECTION.getRecordSource());
					ResourceType resourceTypeDo = new ResourceType();
					resource.setResourceType(resourceTypeDo);

					if (newResource.getAttach() != null && newResource.getAttach().getFilename() != null) {
						String fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(newResource.getAttach().getFilename(), ".");
						if (fileExtension.contains(PDF)) {
							resourceTypeDo.setName(ResourceType.Type.HANDOUTS.getType());
						} else {
							resourceTypeDo.setName(ResourceType.Type.IMAGE.getType());
						}
						resource.setUrl(newResource.getAttach().getFilename());
						resource.setIsOer(true);
					} else {
						resource.setUrl(newResource.getUrl());
						resourceTypeDo.setName(ResourceImageUtil.getYoutubeVideoId(newResource.getUrl()) != null ? ResourceType.Type.VIDEO.getType() : ResourceType.Type.RESOURCE.getType());
					}
					resource.setSharing(sharing);
					domainName = getDomainName(newResource.getUrl());
					resourceSource = this.getResourceRepository().findResourceSource(domainName);
					if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
						resource.setHasFrameBreaker(true);
					} else if ((newResource.getUrl() != null && newResource.getUrl().contains(YOUTUBE_URL) && ResourceImageUtil.getYoutubeVideoId(newResource.getUrl()) == null)) {
						resource.setHasFrameBreaker(true);
					} else {
						resource.setHasFrameBreaker(false);
					}

					this.getResourceService().saveOrUpdate(resource);
					resourceService.saveOrUpdateResourceTaxonomy(resource, newResource.getTaxonomySet());
					this.getResourceService().updateYoutubeResourceFeeds(resource, false);
					this.getResourceService().saveOrUpdate(resource);
					this.getResourceService().mapSourceToResource(resource);

					if (newResource.getMomentsOfLearning() != null && newResource.getMomentsOfLearning().size() > 0) {
						resource.setMomentsOfLearning(this.updateContentMeta(newResource.getMomentsOfLearning(), resource.getGooruOid(), user, MOMENTS_OF_LEARNING));
					} else {
						resource.setMomentsOfLearning(this.setContentMetaAssociation(this.getContentMetaAssociation(MOMENTS_OF_LEARNING), resource.getGooruOid(), MOMENTS_OF_LEARNING));
					}
					if (newResource.getEducationalUse() != null && newResource.getEducationalUse().size() > 0) {
						resource.setEducationalUse(this.updateContentMeta(newResource.getEducationalUse(), resource.getGooruOid(), user, EDUCATIONAL_USE));
					} else {
						resource.setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), resource.getGooruOid(), EDUCATIONAL_USE));
					}
					if(newResource.getHost() != null && newResource.getHost().size() > 0) {
						resource.setHost(this.getResourceService().updateContentProvider(resource.getGooruOid(), newResource.getHost(), user, "host"));
					}
					if(tags != null && tags.size() > 0) {
						resource.setResourceTags(this.getContentService().createTagAssoc(resource.getGooruOid(), tags, user));
					}
					try {
						this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
					} catch (Exception e) {
						LOGGER.debug(e.getMessage());
					}

					if (resource != null && resource.getContentId() != null) {
						try {
							indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
						} catch (Exception e) {
							LOGGER.debug(e.getMessage());
						}
					}
				}

				if (newResource.getAttach() != null) {
					this.getResourceImageUtil().moveAttachment(newResource, resource);
				}
				collection.setLastUpdatedUserUid(user.getPartyUid());
				this.getResourceRepository().save(collection);

				response = createCollectionItem(resource, collection, start, stop, user);

				response.getModel().setStandards(this.getStandards(resource.getTaxonomySet(), false, null));

			} else {
				throw new NotFoundException("collection does not exist in the system, required collection to map the resource");
			}
			if (response.getModel().getCollection().getResourceType().getName().equalsIgnoreCase(SCOLLECTION) && !response.getModel().getCollection().getClusterUid().equalsIgnoreCase(response.getModel().getCollection().getGooruOid())) { 
				response.getModel().getCollection().setClusterUid(response.getModel().getCollection().getGooruOid());
				this.getCollectionRepository().save(response.getModel().getCollection());
			}
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + response.getModel().getCollection().getUser().getPartyUid() + "*");
		}
		try{
			getEventLogs(response.getModel(), true, false, user, response.getModel().getCollection().getCollectionType() );
		} catch(Exception e){
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public ActionResponseDTO<CollectionItem> updateResourceWithCollectionItem(String collectionItemId, Resource newResource, List<String> tags,User user) throws Exception {

		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		Errors errors = validateUpdateCollectionItem(collectionItem);
		JSONObject itemData = new JSONObject();
		if (!errors.hasErrors()) {

			Resource resource = null;

			if (collectionItem != null && collectionItem.getResource() != null) {
				resource = this.getResourceService().findResourceByContentGooruId(collectionItem.getResource().getGooruOid());
			}
			rejectIfNull(resource, GL0056, RESOURCE);
			if (newResource.getTitle() != null) {
				resource.setTitle(newResource.getTitle());
				itemData.put(TITLE, newResource.getTitle());
			}
			if (newResource.getDescription() != null) {
				resource.setDescription(newResource.getDescription());
				itemData.put(DESCRIPTION, newResource.getDescription());
			}
			if (newResource.getCategory() != null) {
				resource.setCategory(newResource.getCategory().toLowerCase());
				itemData.put(DESCRIPTION, newResource.getCategory().toLowerCase());
			}
			if (newResource.getInstructional() != null) {
				CustomTableValue resourceCategory = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, newResource.getInstructional().getValue());
				resource.setInstructional(resourceCategory);
				itemData.put(INSTRUCTIONAL, resourceCategory);
			}
			if (newResource.getResourceFormat() != null) {
				CustomTableValue resourcetype = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				resource.setResourceFormat(resourcetype);
				itemData.put(RESOURCEFORMAT, resourcetype);
			}
			if (newResource.getSharing() != null) {
				resource.setSharing(newResource.getSharing());
				itemData.put(SHARING, newResource.getSharing());
			}

			if (newResource.getAttach() != null && newResource.getAttach().getFilename() != null) {
				String fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(newResource.getAttach().getFilename(), ".");
				ResourceType resourceTypeDo = new ResourceType();
				resource.setResourceType(resourceTypeDo);
				if (fileExtension.contains(PDF)) {
					resourceTypeDo.setName(ResourceType.Type.HANDOUTS.getType());
				} else {
					resourceTypeDo.setName(ResourceType.Type.IMAGE.getType());
				}
				resource.setUrl(newResource.getAttach().getFilename());
				itemData.put(URL, newResource.getAttach().getFilename());
			}

			this.getResourceService().saveOrUpdate(resource);

			resourceService.saveOrUpdateResourceTaxonomy(resource, newResource.getTaxonomySet());

			if (newResource.getMomentsOfLearning() != null && newResource.getMomentsOfLearning().size() > 0) {
				resource.setMomentsOfLearning(this.updateContentMeta(newResource.getMomentsOfLearning(), resource.getGooruOid(), user, MOMENTS_OF_LEARNING));
			} else {
				resource.setMomentsOfLearning(this.setContentMetaAssociation(this.getContentMetaAssociation(MOMENTS_OF_LEARNING), resource.getGooruOid(), MOMENTS_OF_LEARNING));
			}
			if (newResource.getEducationalUse() != null && newResource.getEducationalUse().size() > 0) {
				resource.setEducationalUse(this.updateContentMeta(newResource.getEducationalUse(), resource.getGooruOid(), user, EDUCATIONAL_USE));
			} else {
				resource.setEducationalUse(this.setContentMetaAssociation(this.getContentMetaAssociation(EDUCATIONAL_USE), resource.getGooruOid(), EDUCATIONAL_USE));
			}
			
			if(tags != null && tags.size() > 0) {
				resource.setResourceTags(this.getContentService().createTagAssoc(resource.getGooruOid(), tags, user));
			}

			this.getResourceService().saveOrUpdate(resource);

			if (newResource.getThumbnail() != null && newResource.getThumbnail().length() > 0) {
				try {
					this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
				} catch (Exception e) {
					LOGGER.debug(e.getMessage());
				}
				itemData.put(THUMBNAIL, newResource.getThumbnail());
			}
			if (newResource.getAttach() != null) {
				this.getResourceImageUtil().moveAttachment(newResource, resource);
			}
			this.getResourceService().saveOrUpdate(resource);
			collectionItem.setResource(resource);
			this.getCollectionRepository().save(collectionItem);
			collectionItem.setStandards(this.getStandards(resource.getTaxonomySet(), false, null));
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		}
		try {
			getEventLogs(collectionItem, itemData, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	public ActionResponseDTO<CollectionItem> createCollectionItem(Resource resource, Collection collection, String start, String stop, User user ) throws Exception {
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(collection);
		collectionItem.setResource(resource);
		collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
		collectionItem.setItemSequence(sequence);
		collectionItem.getCollection().setItemCount(sequence);
		collectionItem.setStart(start);
		collectionItem.setStop(stop);
		Errors errors = validateCollectionItem(collection, resource, collectionItem);
		this.getResourceRepository().save(collectionItem);
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collectionItem.getCollection().getUser().getPartyUid() + "*");
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public List<Collection> getMyCollection(String limit, String offset, String orderBy, String fetchType, String resourceType, User user) {
		return getCollectionRepository().getMyCollection(Integer.parseInt(limit), Integer.parseInt(offset), orderBy, fetchType, resourceType, user);
	}

	@Override
	public List<CollectionItem> getMyCollectionItems(String partyUid, Map<String, String> filters, User user) {
		User party = null;
		if (partyUid != null && !partyUid.equalsIgnoreCase(MY)) {
			party = userService.findByGooruId(partyUid);
			if (party != null) {
				filters.put(SHARING, PUBLIC);
			}
		}
		return getCollectionRepository().getMyCollectionItems(filters, party != null ? party : user);
	}

	@Override

	public List<CollectionItem> getCollectionItems(String collectionId, Integer offset, Integer limit, String orderBy, String type) {
		return this.getCollectionRepository().getCollectionItems(collectionId, offset, limit, orderBy, "classpage");
	}

	@Override
	public Map<String, Object> getCollection(String gooruOid, Map<String, Object> collection, String rootNodeId) {
		Collection collectionObj = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		collection.put(METAINFO, setMetaData(collectionObj, false, rootNodeId));
		Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
		for (CollectionItem collectionItem : collectionObj.getCollectionItems()) {
			collectionItem.getResource().setRatings(this.setRatingsObj(this.getResourceRepository().getResourceSummaryById(collectionItem.getResource().getGooruOid())));
			collectionItems.add(collectionItem);
		}
		collection.put(COLLECTIONITEMS, collectionItems);
		collection.put(GOALS, collectionObj.getGoals());
		collection.put(THUMBNAILS, collectionObj.getThumbnails());
		collection.put(GOORU_OID, collectionObj.getGooruOid());
		collection.put(TITLE, collectionObj.getTitle());
		return collection;
	}

	@Override
	public List<String> getParentCollection(String collectionGooruOid, String gooruUid, boolean reverse) {
		List<String> parentIds = new ArrayList<String>();
		getCollection(collectionGooruOid, gooruUid, parentIds);

		if (reverse) {
			return parentIds.size() > 0 ? Lists.reverse(parentIds) : parentIds;
		} else {
			return parentIds;
		}
	}

	private List<String> getCollection(String collectionGooruOid, String gooruUid, List<String> parentIds) {
		String gooruOid = this.getCollectionRepository().getParentCollection(collectionGooruOid, gooruUid);
		if (gooruOid != null) {
			parentIds.add(gooruOid);
			getCollection(gooruOid, gooruUid, parentIds);
		}
		return parentIds;

	}

	@Override
	public void updateFolderSharing(String gooruOid) {
		Resource collection = this.getResourceRepository().findResourceByContent(gooruOid);
		if (collection != null) {
			if (this.getCollectionRepository().getPublicCollectionCount(collection.getGooruOid(), PUBLIC) > 0) {
				collection.setSharing(Sharing.PUBLIC.getSharing());
			} else if (this.getCollectionRepository().getPublicCollectionCount(collection.getGooruOid(), Sharing.ANYONEWITHLINK.getSharing()) > 0) {
				collection.setSharing(Sharing.ANYONEWITHLINK.getSharing());
			} else {
				collection.setSharing(Sharing.PRIVATE.getSharing());
			}
			this.getCollectionRepository().save(collection);
			this.getCollectionRepository().flush();
		}

	}

	private ResourceMetaInfo setMetaData(Collection collection, boolean ignoreUserTaxonomyPreference, String rootNodeId) {
		ResourceMetaInfo collectionMetaInfo = null;
		if (collection != null && collection.getTaxonomySet() != null) {
			collectionMetaInfo = new ResourceMetaInfo();
			collectionMetaInfo.setStandards(this.getStandards(collection.getTaxonomySet(), ignoreUserTaxonomyPreference, rootNodeId));
		}
		return collectionMetaInfo;
	}

	private void saveOrUpdateCollectionGrade(String newResourceGrade, Resource newResource, Boolean merge) {
		if (newResource.getGrade() != null && merge) {
			String grade = newResource.getGrade();
			String resourceGrade = newResourceGrade;
			List<String> newResourceGrades = Arrays.asList(grade.split(","));
			if (resourceGrade != null) {
				List<String> resourceGrades = Arrays.asList(resourceGrade.split(","));
				if (resourceGrades != null) {
					for (String newGrade : resourceGrades) {
						if (!newResourceGrades.contains(newGrade) && newGrade.length() > 0) {
							grade += "," + newGrade;
						}
					}
				}
			}
			newResource.setGrade(grade);
		} else {
			newResource.setGrade(newResourceGrade);
		}
		this.getResourceRepository().save(newResource);
	}

	private String getDomainName(String resourceUrl) {
		String domainName = "";
		if (resourceUrl != null && !resourceUrl.isEmpty()) {
			if (resourceUrl.contains("http://")) {
				domainName = resourceUrl.split("http://")[1];
			} else if (resourceUrl.contains("http://")) {
				domainName = resourceUrl.split("www.")[1];
			} else if (resourceUrl.contains("https://")) {
				domainName = resourceUrl.split("https://")[1];
			}
			if (domainName.contains("www.")) {
				domainName = domainName.split("www.")[1];
			}
			if (domainName.contains("/")) {
				domainName = domainName.split("/")[0];
			}
		}
		return domainName;
	}

	public void getEventLogs(Collection collection, JSONObject itemData, User user, boolean isCreate, boolean isUpdate) throws JSONException {
		if(isCreate){
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		} else if(isUpdate){
			SessionContextSupport.putLogParameter(EVENT_NAME,  ITEM_EDIT);
		}
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if(collection != null ){
			context.put(CONTENT_GOORU_ID, collection != null && collection.getCollectionItem() != null && collection.getCollectionItem().getResource() != null ? collection.getCollectionItem().getResource().getGooruOid() : null);
			context.put(CONTENT_ITEM_ID, collection != null && collection.getCollectionItem() != null ? collection.getCollectionItem().getCollectionItemId() : null);
			context.put(PARENT_GOORU_ID, collection != null ? collection.getGooruOid() : null);
			context.put( PARENT_ITEM_ID, collection != null && collection.getCollectionItem() != null ? collection.getCollectionItem().getCollectionItemId() : null);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if(isCreate){
			payLoadObject.put(MODE,   CREATE);
		} else if(isUpdate){
			payLoadObject.put(MODE, EDIT);
		} else {
			payLoadObject.put(MODE, _COPY);
		}
		payLoadObject.put(ITEM_TYPE, collection != null ? collection.getCollectionType()  : null);
		payLoadObject.put(_ITEM_DATA , itemData != null ? itemData.toString() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put( ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

	@Override
	public void getEventLogs(CollectionItem collectionItem, boolean isCreate, boolean isAdd, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if(isCreate){
			payLoadObject.put(MODE,   CREATE);
		} else if(isAdd){
			payLoadObject.put(MODE, ADD);
		} else {
			payLoadObject.put(MODE, _COPY);
		}
		payLoadObject.put(ITEM_SEQUENCE, collectionItem != null ? collectionItem.getItemSequence() : null);
		payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		if (collectionType != null && collectionItem != null) {
			if(collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())){
				if(collectionItem != null && collectionItem.getResource() != null){
					String typeName = collectionItem.getResource().getResourceType().getName();
					if(typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
					} else if(typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
			} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
				if(collectionItem != null && collectionItem.getResource() != null){
					String itemTypeName = collectionItem.getResource().getResourceType().getName();
					if(itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
					} else if(itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
			}
		}
		payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put(CONTENTID , collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		payLoadObject.put(TITLE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getTitle() : null);
		payLoadObject.put(DESCRIPTION, collectionItem != null && collectionItem.getResource() != null ?  collectionItem.getResource().getDescription() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put( ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void getEventLogs(CollectionItem collectionItem , JSONObject itemData, User user) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME,  ITEM_EDIT);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if(collectionItem != null ){
			context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
			context.put(CONTENT_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
			context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
			context.put( PARENT_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(ITEM_TYPE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getResourceType().getName() : null);
		payLoadObject.put(_ITEM_DATA , itemData != null ? itemData.toString() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put( ORGANIZATION_UID, user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

	@Override
	public void getEventLogs(CollectionItem collectionItem, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (collectionType != null && collectionItem != null) {
			if(collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())){
				if(collectionItem.getResource() != null){
					String typeName = collectionItem.getResource().getResourceType().getName();
					if(typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
					} else if(typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
			} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
				if(collectionItem.getResource() != null){
					String itemTypeName = collectionItem.getResource().getResourceType().getName();
					if(itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())){
						payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
					} else if(itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())){
						payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
			}
		}
		payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put(CONTENTID , collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user!= null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}
	
	public void deleteBulkCollections(List<String> gooruOids){
		List<Collection> collections = collectionRepository.getCollectionListByIds(gooruOids);
		StringBuffer removeContentIds = new StringBuffer();
		for (Collection collection : collections) {
			removeContentIds.append(collection.getGooruOid());
		}
		this.collectionRepository.removeAll(collections);
		indexProcessor.index(removeContentIds.toString(), IndexProcessor.DELETE, SCOLLECTION);
	}
	
	public boolean isResourceType(Resource resource){
		boolean isResourceType = false;
		if(!resource.getResourceType().equals(ResourceType.Type.SCOLLECTION.getType()) && !resource.getResourceType().equals(ResourceType.Type.CLASSPAGE.getType()) && !resource.getResourceType().equals(ResourceType.Type.FOLDER.getType())){
			isResourceType = true;
		}
		return isResourceType;
	}
	

	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public AssessmentService getAssessmentService() {
		return assessmentService;
	}

	public ContentRepositoryHibernate getContentRepositoryHibernate() {
		return contentRepositoryHibernate;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public MailAsyncExecutor getMailAsyncExecutor() {
		return mailAsyncExecutor;
	}

	public CommentRepository getCommentRepository() {
		return commentRepository;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

	public CustomFieldsService getCustomFieldsService() {
		return customFieldsService;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

}
