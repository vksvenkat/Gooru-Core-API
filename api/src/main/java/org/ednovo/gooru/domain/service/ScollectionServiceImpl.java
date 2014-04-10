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
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.StandardFo;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.CollectionMetaInfo;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.rating.RatingService;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentAssociationRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.question.CommentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyStoredProcedure;
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

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import flexjson.JSONDeserializer;

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
	protected LearnguideRepository learnguideRepository;

	@Autowired
	protected RatingService ratingService;

	@Autowired
	private TaxonomyStoredProcedure procedureExecutor;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private UserService userService;

	@Autowired
	protected AssessmentService assessmentService;

	@Autowired
	private ContentRepositoryHibernate contentRepositoryHibernate;

	@Autowired
	private ContentAssociationRepositoryHibernate contentAssociationRepositoryHibernate;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

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
	private CustomTableRepository customTableRepository;

	@Autowired
	private MailAsyncExecutor mailAsyncExecutor;

	@Autowired
	private AsyncExecutor asyncExecutor;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	Logger logger = LoggerFactory.getLogger(ScollectionServiceImpl.class);

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
			Collection parentCollection = collectionRepository.getCollectionByGooruOid(parentId, collection.getUser().getGooruUId());
			if (parentCollection != null) {
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), parentCollection.getGooruOid(), new CollectionItem(), collection.getUser(), CollectionType.FOLDER.getCollectionType(), false).getModel());
			}
			if (collection.getCollectionType().equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType()) && collection.getUser() != null) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("eventType", CustomProperties.EventMapping.FIRST_COLLECTION.getEvent());
				data.put("gooruUid", collection.getUser().getGooruUId());
				data.put("accountTypeId", collection.getUser().getAccountTypeId() != null ? collection.getUser().getAccountTypeId().toString() : null);
				// this.getMailAsyncExecutor().handleMailEvent(data);
				this.mailHandler.handleMailEvent(data);

			}
			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
			getAsyncExecutor().createVersion(collection, SCOLLECTION_CREATE, collection.getUser().getPartyUid());
		}

		return new ActionResponseDTO<Collection>(collection, errors);
	}

	public RatingService getRatingService() {
		return ratingService;
	}

	@Override
	public ActionResponseDTO<Collection> createCollection(Collection collection, boolean addToShelf, String resourceId, String parentId, User user) throws Exception {
		Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
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
			Collection parentCollection = collectionRepository.getCollectionByGooruOid(parentId, collection.getUser().getGooruUId());
			if (parentCollection != null) {
				collection.setCollectionItem(this.createCollectionItem(collection.getGooruOid(), parentCollection.getGooruOid(), new CollectionItem(), collection.getUser(), CollectionType.FOLDER.getCollectionType(), false).getModel());
			}

			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception ex) {
				logger.debug(ex.getMessage());
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
			getEventLogs(collection, user);
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
		if (newCollection.getUser() != null) {
			if (userService.isContentAdmin(newCollection.getUser())) {
				gooruUid = null;
			} else {
				gooruUid = newCollection.getUser().getGooruUId();
			}
		}
		Collection collection = this.getCollectionByGooruOid(updateCollectionId, gooruUid);
		Errors errors = validateUpdateCollection(collection, newCollection);
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
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyMapByCode(collection.getTaxonomySet(), taxonomyService));
			}

			if (newCollection.getVocabulary() != null) {
				collection.setVocabulary(newCollection.getVocabulary());
			}
			if (newCollection.getBuildType() != null && newCollection.getBuildType().getValue() != null) {
				if (newCollection.getBuildType().getValue() == WEB || newCollection.getBuildType().getValue() == IPAD) {
					collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), newCollection.getBuildType().getValue()));
				}
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
				resourceService.saveOrUpdateGrade(newCollection, collection);
			}

			if (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
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
				logger.debug(e.getMessage());
			}
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	public void deleteCollection(String collectionId) {
		Collection collection = this.getCollectionByGooruOid(collectionId, null);
		if (collection != null) {
			try {
				revisionHistoryService.createVersion(collection, SCOLLECTION_DELETE);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				indexProcessor.index(collection.getGooruOid(), IndexProcessor.DELETE, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
			this.getCollectionRepository().remove(Collection.class, collection.getContentId());
			List<CollectionItem> collectionItems = this.getCollectionRepository().getCollectionItemByAssociation(collectionId, null);
			if (collectionItems.size() > 0) {
				this.getCollectionRepository().removeAll(collectionItems);
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
	}

	@Override
	public List<Collection> getCollections(Map<String, String> filters, User user) {
		return this.getCollectionRepository().getCollections(filters, user);
	}

	@Override
	public ActionResponseDTO<CollectionItem> createCollectionItem(String resourceGooruOid, String collectionGooruOid, CollectionItem collectionItem, User user, String type, boolean isCreateQuestion) throws Exception {
		Collection collection = null;
		if (type != null && type.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
			collection = createMyShelfCollection(collectionGooruOid, collection, user);
			collectionItem.setItemType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
		} else if (type != null && type.equalsIgnoreCase(COLLABORATOR)) {
			collection = createMyShelfCollection(collectionGooruOid, collection, user);
			collectionItem.setItemType(COLLABORATOR);
		} else if (type != null && type.equalsIgnoreCase(CLASS)) {
			collection = createMyShelfCollection(collectionGooruOid, collection, user);
			collectionItem.setItemType(CLASS);
		} else {
			collection = this.getCollectionByGooruOid(collectionGooruOid, null);
			collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		}
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceGooruOid);
		Errors errors = validateCollectionItem(collection, resource, collectionItem);
		if (!errors.hasErrors()) {
			collectionItem.setCollection(collection);

			if (collectionItem.getCollection() != null) {
				collectionItem.getCollection().setLastUpdatedUserUid(user.getPartyUid());
				collectionItem.getCollection().setLastModified(new Date(System.currentTimeMillis()));
			}

			if (!isCreateQuestion && resource != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
				AssessmentQuestion assessmentQuestion = assessmentService.copyAssessmentQuestion(user, resource.getGooruOid());
				collectionItem.setResource(assessmentQuestion);
			} else {
				collectionItem.setResource(resource);
			}

			int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
			collectionItem.setItemSequence(sequence);
			this.getCollectionRepository().save(collectionItem);
			this.getCollectionRepository().flush();
			getEventLogs(collectionItem, false, user);

			try {
				indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		List<String> parenFolders = this.getParentCollection(resource.getGooruOid(), collection.getUser().getPartyUid(), false);
		for (String parentFolder : parenFolders) {
			updateFolderSharing(parentFolder);
		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	private Collection createMyShelfCollection(String collectionGooruOid, Collection collection, User user) {
		if (collectionGooruOid != null) {
			collection = this.getCollectionByGooruOid(collectionGooruOid, CollectionType.SHElf.getCollectionType());
		} else {
			collection = this.getCollectionRepository().getUserShelfByGooruUid(user.getGooruUId(), CollectionType.SHElf.getCollectionType());
		}
		if (collection == null) {
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
		if (!errors.hasErrors()) {
			if (newcollectionItem.getNarration() != null) {
				collectionItem.setNarration(newcollectionItem.getNarration());
			}
			if (newcollectionItem.getPlannedEndDate() != null) {
				collectionItem.setPlannedEndDate(newcollectionItem.getPlannedEndDate());
			}
			if (newcollectionItem.getNarrationType() != null) {
				collectionItem.setNarrationType(newcollectionItem.getNarrationType());
			}
			if (newcollectionItem.getStart() != null) {
				collectionItem.setStart(newcollectionItem.getStart());
			}
			if (newcollectionItem.getStop() != null) {
				collectionItem.setStop(newcollectionItem.getStop());
			}
			if (collectionItem.getResource() != null && collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
				collectionItem.getResource().setLastUpdatedUserUid(user.getPartyUid());
			}
			this.getCollectionRepository().save(collectionItem);
			this.getCollectionRepository().save(collectionItem.getCollection());
			try {
				if (collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
				} else {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
				}
				indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public List<CollectionItem> getCollectionItems(String collectionId, Map<String, String> filters) {
		return this.getCollectionRepository().getCollectionItems(collectionId, filters);
	}

	@Override
	public CollectionItem getCollectionItem(String collectionItemId, boolean includeAdditionalInfo, User user) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		if (collectionItem != null) {
			if (includeAdditionalInfo) {
				collectionItem = this.setCollectionItemMoreData(collectionItem);
				if (user != null) {
					Integer contentUserRating = this.getRatingService().getContentRatingForUser(user.getPartyUid(), collectionItem.getResource().getGooruOid());
					collectionItem.getResource().setUserRating(contentUserRating);
				}
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION_ITEM));
		}
		return collectionItem;
	}

	@Override
	public void deleteCollectionItem(String collectionItemId, User user) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		if (collectionItem != null) {
			try {
				getEventLogs(collectionItem, user);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			Collection collection = collectionItem.getCollection();
			this.getCollectionRepository().remove(CollectionItem.class, collectionItem.getCollectionItemId());

			collectionItem.getCollection().setLastUpdatedUserUid(user.getPartyUid());

			reOrderCollectionItems(collection, collectionItemId);
			try {
				if (collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.DELETE, SCOLLECTION);
				} else {
					indexProcessor.index(collectionItem.getResource().getGooruOid(), IndexProcessor.INDEX, RESOURCE);
				}
				indexProcessor.index(collectionItem.getCollection().getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
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

					if (ci.getItemSequence() >= newSequence) {
						if (ci.getItemSequence() <= existCollectionItemSequence) {
							if (ci.getCollectionItemId().equalsIgnoreCase(collectionItem.getCollectionItemId())) {
								ci.setItemSequence(newSequence);
							} else {
								ci.setItemSequence(ci.getItemSequence() + 1);
							}
						}
					}
				}

			} else if (existCollectionItemSequence < newSequence) {
				for (CollectionItem ci : collection.getCollectionItems()) {
					if (ci.getItemSequence() <= newSequence) {
						if (existCollectionItemSequence <= ci.getItemSequence()) {
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
			}
			this.getCollectionRepository().save(collection);
		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public Collection getCollection(String collectionId, boolean includeMetaInfo, boolean includeCollaborator, boolean isContentFlag, User user, String merge) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		boolean isCollaborator = this.getCollaboratorRepository().findCollaboratorById(collectionId, user.getGooruUId()) != null ? true : false;
		if (collection != null && (collection.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId()) || !collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || userService.isContentAdmin(user) || isCollaborator)) {
			if (includeMetaInfo) {
				this.setColletionMetaData(collection, user, merge, false);
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyMapByCode(collection.getTaxonomySet(), taxonomyService));
			}
			if (collection.getUser() != null) {
				collection.getUser().setProfileImageUrl(this.getUserService().buildUserProfileImageUrl(collection.getUser()));
			}
			if (isContentFlag) {
				collection.setContentAssociation(this.getContentAssociationRepositoryHibernate().getContentAssociationGooruOid(collectionId));
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
			if (merge != null) {
				Map<String, Object> permissions = new HashMap<String, Object>();
				if (merge.contains(PERMISSIONS)) {
					permissions.put(PERMISSIONS, this.getContentService().getContentPermission(collectionId, user));
				}
				if (merge.contains(REACTION_AGGREGATE)) {
					permissions.put(REACTION_AGGREGATE, this.getFeedbackService().getContentFeedbackAggregate(collectionId, REACTION));
				}
				if (merge.contains("commentCount")) {
					permissions.put("commentCount", this.getCommentRepository().getCommentCount(collection.getGooruOid(), null, "notdeleted"));
				}
				long collaboratorCount = this.getCollaboratorRepository().getCollaboratorsCountById(collectionId);
				permissions.put("collaboratorCount", collaboratorCount);
				permissions.put("isCollaborator", isCollaborator);
				collection.setMeta(permissions);
			}

		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		return collection;
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
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		if (collaboratorId != null) {
			List<String> collaboratorsList = Arrays.asList(collaboratorId.split("\\s*,\\s*"));
			for (User collaborator : getUserService().findByIdentities(collaboratorsList)) {
				if (getUserService().checkCollaboratorsPermission(collectionId, collaborator, SCOLLECTION)) {
					return collectionUtil.updateNewCollaborators(collection, collaboratorsList, user, "collection.collaborate", collaboratorOperation);
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
		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		return this.learnguideRepository.findCollaborators(collectionId, null);
	}

	protected Collection setColletionMetaData(Collection collection, User user, String merge, boolean ignoreUserTaxonomyPreference) {
		if (collection != null) {
			Set<String> acknowledgement = new HashSet<String>();
			CollectionMetaInfo collectionMetaInfo = new CollectionMetaInfo();
			collectionMetaInfo.setRating(this.getRatingService().findByContentObj(collection));
			collectionMetaInfo.setCourse(this.getCourse(collection.getTaxonomySet()));
			collectionMetaInfo.setStandards(this.getStandards(collection.getTaxonomySet(), ignoreUserTaxonomyPreference));
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
						resourcePermissions.put(REACTION, this.getFeedbackService().getContentFeedbacks(REACTION, null, collectionItem.getResource().getGooruOid(), collection.getUser().getPartyUid(), null, null, true));
						collectionItem.getResource().setMeta(resourcePermissions);
					}

					if (user != null) {
						Integer contentUserRating = this.getRatingService().getContentRatingForUser(user.getPartyUid(), collectionItem.getResource().getGooruOid());
						collectionItem.getResource().setUserRating(contentUserRating);
					}
					this.setCollectionItemMoreData(collectionItem);
				}
				collectionMetaInfo.setAcknowledgement(acknowledgement);
			}
			if (user != null) {
				Integer contentUserRating = this.getRatingService().getContentRatingForUser(user.getPartyUid(), collection.getGooruOid());
				collection.setUserRating(contentUserRating);
			}

		}
		return collection;
	}

	@Override
	public List<CollectionItem> setCollectionItemMetaInfo(List<CollectionItem> collectionItems) {
		if (collectionItems != null) {
			for (CollectionItem collectionItem : collectionItems) {
				if (collectionItem.getResource() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					Rating rating = this.getRatingService().findByContentObj(collectionItem.getResource());
					collectionItem.getResource().setVotesUp(rating != null ? rating.getVotesUp() : 0);
					collectionItem.setCourse(this.getCourse(collectionItem.getResource().getTaxonomySet()));
					collectionItem.setStandards(this.getStandards(collectionItem.getResource().getTaxonomySet(), false));
					List<CollectionItem> collectionItemCount = this.getCollectionItems(collectionItem.getResource().getGooruOid(), new HashMap<String, String>());
					collectionItem.setResourceCount(collectionItemCount.size());
				}
			}
		}
		return collectionItems;
	}

	private CollectionItem setCollectionItemMoreData(CollectionItem collectionItem) {
		if (collectionItem.getResource() != null) {
			collectionItem.setRating(this.getRatingService().findByContentObj(collectionItem.getResource()));
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

			collectionItem.setStandards(this.getStandards(collectionItem.getResource().getTaxonomySet(), false));
		}

		return collectionItem;
	}

	@Override
	public Set<String> getCourse(Set<Code> taxonomySet) {
		Set<String> course = null;
		if (taxonomySet != null) {
			course = new HashSet<String>();
			for (Code code : taxonomySet) {
				if (code.getDepth() == 2) {
					course.add(code.getLabel());
				}
			}
		}
		return course;
	}

	@Override
	public List<StandardFo> getStandards(Set<Code> taxonomySet, boolean ignoreUserTaxonomyPreference) {
		List<StandardFo> standards = null;
		if (taxonomySet != null) {
			standards = new ArrayList<StandardFo>();
			if (!ignoreUserTaxonomyPreference) {
				for (Code code : taxonomySet) {
					if (code.getRootNodeId() != null && UserGroupSupport.getTaxonomyPreference() != null && UserGroupSupport.getTaxonomyPreference().contains(code.getRootNodeId().toString())) {
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

		if (collection == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
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

		}
		if (isNotEmptyString(buildType)) {
			if (buildType.equalsIgnoreCase(WEB) || buildType.equalsIgnoreCase(IPAD)) {
				collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), buildType));
			}
		}
		if (isNotEmptyString(mediaType)) {
			collection.setMediaType(mediaType);
		}
		if (isNotEmptyString(title)) {
			collection.setTitle(title);
		}

		if (description != null) {
			collection.setGoals(description);
		}
		collection.setLastUpdatedUserUid(apiCallerUser.getPartyUid());

		if (isNotEmptyString(sharing)) {
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
			collection.setVocabulary(vocabulary);
		}
		if (data.containsKey(GRADE)) {
			saveOrUpdateCollectionGrade(grade, collection, false);
		}
		if (isNotEmptyString(narrationLink)) {
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
		this.setColletionMetaData(collection, null, null, true);
		this.getCollectionRepository().save(collection);
		this.getCollectionRepository().flush();
		try {
			indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
		} catch (Exception e) {
			logger.debug(e.getMessage());
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

		if (collectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION_ITEM));
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
			e.printStackTrace();
		}

		return collectionItem;
	}

	@Override
	public CollectionItem copyCollectionItem(String collectionItemId, String collectionId) throws Exception {
		CollectionItem sourceCollectionItem = this.getCollectionItem(collectionItemId, false, null);
		if (sourceCollectionItem == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION_ITEM));
		}
		CollectionItem destCollectionItem = new CollectionItem();
		Collection targetCollection = null;
		boolean hasSameCollection = false;
		if (collectionId != null) {
			targetCollection = this.getCollection(collectionId, false, false, false, sourceCollectionItem.getCollection().getUser(), null);
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
		return this.createResourceWithCollectionItem(collectionId, newResource, user);
	}

	@Override
	public CollectionItem buildCollectionItemFromInputParameters(String data, User user) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias(COLLECTION_ITEM, CollectionItem.class);
		CollectionItem collectionItem = (CollectionItem) xstream.fromXML(data);
		return collectionItem;
	}

	@Override
	public List<Collection> getMyCollection(Map<String, String> filters, User user) {
		return getCollectionRepository().getMyCollection(filters, user);
	}

	@Override
	public List<Collection> getMyCollection(String offset, String limit, String type, String filter, User user) {
		List<Collection> collection = this.getCollectionRepository().getMyCollection(offset, limit, type, filter, user);
		return collection;
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

	public void deleteCollectionTaxonomy(Collection collection, String taxonomyCode, boolean updateTaxonomyByCode) {
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
		colletionType.put(EBOOK, COLLECTION_TYPE);
		colletionType.put(FOLDER, COLLECTION_TYPE);
		colletionType.put(ASSIGNMENT, COLLECTION_TYPE);
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfInvalidType(errors, collection.getCollectionType(), COLLECTION_TYPE, GL0007, generateErrorMessage(GL0007, COLLECTION_TYPE), colletionType);
		}
		return errors;
	}

	private Errors validateUpdateCollection(Collection collection, Collection newCollection) throws Exception {
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
		if (newCollection.getUser() != null) {
			if (userService.isContentAdmin(newCollection.getUser())) {
				gooruUid = null;
			} else {
				gooruUid = newCollection.getUser().getGooruUId();
			}
		}
		Collection collection = this.getCollectionByGooruOid(updateCollectionId, gooruUid);
		Errors errors = validateUpdateCollection(collection, newCollection);
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
				resourceService.saveOrUpdateResourceTaxonomy(collection, newCollection.getTaxonomySet());
				collection.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyMapByCode(collection.getTaxonomySet(), taxonomyService));
			}

			if (newCollection.getVocabulary() != null) {
				collection.setVocabulary(newCollection.getVocabulary());
			}
			if (newCollection.getBuildType() != null && newCollection.getBuildType().getValue() != null) {
				if (newCollection.getBuildType().getValue().equalsIgnoreCase(WEB) || newCollection.getBuildType().getValue().equalsIgnoreCase(IPAD)) {
					collection.setBuildType(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.BUILD_TYPE.getTable(), newCollection.getBuildType().getValue()));
				}
			}
			if (newCollection.getMediaType() != null) {
				collection.setMediaType(newCollection.getMediaType());
			}
			if (newCollection.getTitle() != null) {
				collection.setTitle(newCollection.getTitle());
			}

			if (newCollection.getMailNotification() != null) {
				collection.setMailNotification(newCollection.getMailNotification());
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
				collection.setKeyPoints(newCollection.getKeyPoints());
			}
			if (newCollection.getLanguage() != null) {
				collection.setLanguage(newCollection.getLanguage());
			}
			if (newCollection.getGrade() != null) {
				resourceService.saveOrUpdateGrade(newCollection, collection);
			}

			if (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
				collection.setSharing(newCollection.getSharing());
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
				logger.debug(e.getMessage());
			}
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	public CollectionItem getCollectionItem(String collectionItemId, String includeAdditionalInfo, User user) {

		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		if (collectionItem != null) {
			if (includeAdditionalInfo.equalsIgnoreCase(TRUE)) {
				collectionItem = this.setCollectionItemMoreData(collectionItem);
				if (user != null) {
					Integer contentUserRating = this.getRatingService().getContentRatingForUser(user.getPartyUid(), collectionItem.getResource().getGooruOid());
					collectionItem.getResource().setUserRating(contentUserRating);
				}
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION_ITEM));
		}
		return collectionItem;
	}

	@Override
	public Collection copyCollection(String collectionId, Collection newCollection, boolean addToShelf, String parentId, User user) throws Exception {

		Collection sourceCollection = this.getCollection(collectionId, false, false, false, user, null);
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
			destCollection.setKeyPoints(sourceCollection.getKeyPoints());
			destCollection.setThumbnail(sourceCollection.getThumbnail());
			if (newCollection.getGrade() != null) {
				destCollection.setGrade(newCollection.getGrade());
			} else {
				destCollection.setGrade(sourceCollection.getGrade());
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
			} else {
				Set<Code> codes = new HashSet<Code>();
				for (Code code : sourceCollection.getTaxonomySet()) {
					if (code.getRootNodeId() != null && code.getRootNodeId().toString().equalsIgnoreCase(Code.GOORU_TAXONOMY_CODE_ID)) {
						codes.add(code);
					}
				}
				destCollection.setTaxonomySet(codes);
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
				Collection myCollection = createMyShelfCollection(null, null, user);
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
		return destCollection;
	}

	@Override
	public ActionResponseDTO<CollectionItem> createResourceWithCollectionItem(String collectionId, Resource newResource, User user) throws Exception {

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
					Set<Code> taxonomyCode = new HashSet<Code>();
					Iterator<Code> iter = collection.getTaxonomySet().iterator();
					while (iter.hasNext()) {
						Code code = iter.next();
						taxonomyCode.add(code);
					}
					Set<Code> originalTaxonomySet = resource.getTaxonomySet();
					if (originalTaxonomySet != null) {
						taxonomyCode.addAll(originalTaxonomySet);
					}
					resource.setTaxonomySet(taxonomyCode);
					this.getResourceService().saveOrUpdate(resource);
					this.getResourceService().updateYoutubeResourceFeeds(resource, false);
					this.getResourceService().saveOrUpdate(resource);
					this.getResourceService().mapSourceToResource(resource);
					if (newResource.getThumbnail() != null && newResource.getThumbnail().length() > 0) {
						try {
							this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}

					if (resource != null && resource.getContentId() != null) {
						try {
							indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}
				}

				if (newResource.getAttach() != null) {
					this.getResourceImageUtil().moveAttachment(newResource, resource);
				}
				collection.setLastUpdatedUserUid(user.getPartyUid());
				this.getResourceRepository().save(collection);

				response = createCollectionItem(resource, collection, user);

			} else {
				throw new NotFoundException("collection does not exist in the system, required collection to map the resource");
			}

		}
		return response;
	}

	public ActionResponseDTO<CollectionItem> createCollectionItem(Resource resource, Collection collection, User user) throws Exception {
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setCollection(collection);
		collectionItem.setResource(resource);
		collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
		collectionItem.setItemSequence(sequence);
		Errors errors = validateCollectionItem(collection, resource, collectionItem);
		this.getResourceRepository().save(collectionItem);
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_ADD);
		SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());
		SessionContextSupport.putLogParameter(GOORU_OID, resource.getGooruOid());
		SessionContextSupport.putLogParameter(COLLECTION_ID, collection.getGooruOid());
		SessionContextSupport.putLogParameter(RESOURCE_ID, resource.getGooruOid());
		SessionContextSupport.putLogParameter(COLLECTION_TYPE, collectionItem.getCollection().getCollectionType());
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public List<Collection> getMyCollection(String limit, String offset, String orderBy, String fetchType, String resourceType, boolean skipPagination, User user) {
		return getCollectionRepository().getMyCollection(Integer.parseInt(limit), Integer.parseInt(offset), orderBy, fetchType, resourceType, skipPagination, user);
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
	public List<CollectionItem> getCollectionItems(String collectionId, Integer offset, Integer limit, boolean skipPagination, String orderBy, String type) {
		return this.getCollectionRepository().getCollectionItems(collectionId, offset, limit, skipPagination, orderBy, "classpage");
	}

	@Override
	public Map<String, Object> getCollection(String gooruOid, Map<String, Object> collection) {
		Collection CollectionObj = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		collection.put("metaInfo", setMetaData(CollectionObj, true));
		collection.put("collectionItems", CollectionObj.getCollectionItems());
		collection.put("goals", CollectionObj.getGoals());
		collection.put("thumbnails", CollectionObj.getThumbnails());
		collection.put("gooruOid", CollectionObj.getGooruOid());
		collection.put("title", CollectionObj.getTitle());
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

	private CollectionMetaInfo setMetaData(Collection collection, boolean ignoreUserTaxonomyPreference) {
		CollectionMetaInfo collectionMetaInfo = null;
		if (collection != null && collection.getTaxonomySet() != null) {
			collectionMetaInfo = new CollectionMetaInfo();
			collectionMetaInfo.setStandards(this.getStandards(collection.getTaxonomySet(), ignoreUserTaxonomyPreference));
		}
		return collectionMetaInfo;
	}

	public void saveOrUpdateCollectionGrade(String newResourceGrade, Resource newResource, Boolean merge) {
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

	public void getEventLogs(Collection collection, User user) throws JSONException {

		SessionContextSupport.putLogParameter(EVENT_NAME, "collection.create");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context")) :  new JSONObject();
		context.put("contentGooruOId", collection.getGooruOid());

		SessionContextSupport.putLogParameter("context", context.toString());

		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject")) :  new JSONObject();
		payLoadObject.put("contentId", collection.getContentId());
		payLoadObject.put("title", collection.getTitle());
		payLoadObject.put("description", collection.getDescription());
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session")) :  new JSONObject();
		session.put("organizationUId", user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter("session", session.toString());
	}

	public void getEventLogs(CollectionItem collectionItem, boolean isCollectionItem, User user) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, "collection.create");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context")) :  new JSONObject();
		context.put("parentGooruId", collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		SessionContextSupport.putLogParameter("context", context.toString());

		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject")) :  new JSONObject();
		payLoadObject.put("mode", "assign");
		payLoadObject.put("itemSequence", collectionItem.getItemSequence());
		payLoadObject.put("collectionItemId", collectionItem.getCollectionItemId());
		payLoadObject.put("parentContentId", collectionItem.getCollection().getContentId());
		payLoadObject.put("title", collectionItem.getResource().getTitle());
		payLoadObject.put("description", collectionItem.getResource().getDescription());
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session")) :  new JSONObject();
		session.put("organizationUId", user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter("session", session.toString());
	}

	public void getEventLogs(CollectionItem collectionItem, User user) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, "collection.delete");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context")) :  new JSONObject();
		context.put("parentGooruId", collectionItem.getCollection().getGooruOid());
		context.put("contentGooruId", collectionItem.getResource().getGooruOid());
		SessionContextSupport.putLogParameter("context", context.toString());

		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject")) :  new JSONObject();
		payLoadObject.put("contentId", collectionItem.getResource().getContentId());
		payLoadObject.put("mode", "delete");
		payLoadObject.put("itemSequence", collectionItem.getItemSequence());
		payLoadObject.put("collectionItemId", collectionItem.getCollectionItemId());
		payLoadObject.put("parentContentId", collectionItem.getCollection().getContentId());
		payLoadObject.put("title", collectionItem.getResource().getTitle());
		payLoadObject.put("description", collectionItem.getResource().getDescription());
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session")) :  new JSONObject();
		session.put("organizationUId", user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter("session", session.toString());
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

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public TaxonomyStoredProcedure getProcedureExecutor() {
		return procedureExecutor;
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

	public ContentAssociationRepositoryHibernate getContentAssociationRepositoryHibernate() {
		return contentAssociationRepositoryHibernate;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setFeedbackService(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
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

	public void setCollaboratorRepository(CollaboratorRepository collaboratorRepository) {
		this.collaboratorRepository = collaboratorRepository;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

}
