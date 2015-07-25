package org.ednovo.gooru.domain.service.collection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.ContentSettings;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.eventlogs.CollectionEventLog;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

import flexjson.JSONSerializer;

@Service
public class CollectionBoServiceImpl extends AbstractResourceServiceImpl implements CollectionBoService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@Autowired
	private ResourceBoService resourceBoService;

	@Autowired
	private UserService userService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private CollectionEventLog collectionEventLog;

	private final static String COLLECTION_IMAGE_DIMENSION = "160x120,75x56,120x90,80x60,800x600";

	private final static String LAST_USER_MODIFIED = "lastUserModified";

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCollection(String courseId, String unitId, String lessonId, String collectionId, User user) {
		CollectionItem collection = this.getCollectionDao().getCollectionItem(lessonId, collectionId, user.getPartyUid());
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		reject(this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId, user), GL0099, 403, COLLECTION);
		Collection lesson = getCollectionDao().getCollectionByType(lessonId, LESSON_TYPE);
		rejectIfNull(lesson, GL0056, 404, LESSON);
		Collection course = getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		rejectIfNull(course, GL0056, 404, COURSE);
		Collection unit = getCollectionDao().getCollectionByType(unitId, UNIT_TYPE);
		rejectIfNull(unit, GL0056, 404, UNIT);
		this.resetSequence(lessonId, collection.getCollectionItemId(), user.getPartyUid(), COLLECTION);
		this.deleteCollection(collectionId);
		this.updateContentMetaDataSummary(lesson.getContentId(), collection.getContent().getContentType().getName(), DELETE);
		collection.getContent().setIsDeleted((short) 1);
		this.getCollectionDao().save(collection);
		getCollectionEventLog().collectionEventLog(courseId, unitId, lessonId, collection, user, null, DELETE);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createCollection(String courseId, String unitId, String lessonId, User user, Collection collection) {
		final Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			Collection course = getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
			rejectIfNull(course, GL0056, 404, COURSE);
			Collection unit = getCollectionDao().getCollectionByType(unitId, UNIT_TYPE);
			rejectIfNull(unit, GL0056, 404, UNIT);
			Collection lesson = getCollectionDao().getCollectionByType(lessonId, LESSON_TYPE);
			rejectIfNull(lesson, GL0056, 404, LESSON);
			CollectionItem newCollection = createCollection(user, collection, lesson);
			getCollectionEventLog().collectionEventLog(courseId, unitId, lessonId, newCollection, user, collection, ADD);
			Map<String, Object> data = generateCollectionMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.COLLECTION_SUMMARY);
			createContentMeta(collection, data);
			updateContentMetaDataSummary(lesson.getContentId(), collection.getCollectionType(), ADD);

		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createCollection(String folderId, User user, Collection collection) {
		if (collection.getBuildTypeId() != null) {
			collection.setBuildTypeId(Constants.BUILD_WEB_TYPE_ID);
		}
		final Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			Collection targetCollection = null;
			if (folderId != null) {
				targetCollection = this.getCollectionDao().getCollectionByType(folderId, FOLDER_TYPE);
				rejectIfNull(targetCollection, GL0056, 404, FOLDER);

			} else {
				targetCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
				if (targetCollection == null) {
					targetCollection = new Collection();
					targetCollection.setCollectionType(CollectionType.SHElf.getCollectionType());
					targetCollection.setTitle(CollectionType.SHElf.getCollectionType());
					super.createCollection(targetCollection, user);
				}
			}
			createCollection(user, collection, targetCollection);
			getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCollection(String parentId, String collectionId, Collection newCollection, User user) {
		boolean hasUnrestrictedContentAccess = this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId, user);
		// TO-Do add validation for collection type and collaborator validation
		Collection collection = getCollectionDao().getCollection(collectionId);
		if (newCollection.getSharing() != null && (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing()))) {
			if (!newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
				collection.setPublishStatusId(null);
			}
			if (!collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType()) && newCollection.getSharing().equalsIgnoreCase(PUBLIC) && !userService.isContentAdmin(user)) {
				collection.setPublishStatusId(Constants.PUBLISH_PENDING_STATUS_ID);
				newCollection.setSharing(collection.getSharing());
			}
			if (collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType()) || newCollection.getSharing().equalsIgnoreCase(PUBLIC) && userService.isContentAdmin(user)) {
				collection.setPublishStatusId(Constants.PUBLISH_REVIEWED_STATUS_ID);
			}
			collection.setSharing(newCollection.getSharing());
		}
		if (newCollection.getSettings() != null) {
			updateCollectionSettings(collection, newCollection);
		}
		if (newCollection.getLanguageObjective() != null) {
			collection.setLanguageObjective(newCollection.getLanguageObjective());
		}
		if (hasUnrestrictedContentAccess) {
			if (newCollection.getCreator() != null && newCollection.getCreator().getPartyUid() != null) {
				User creatorUser = getUserService().findByGooruId(newCollection.getCreator().getPartyUid());
				collection.setCreator(creatorUser);
			}
			if (newCollection.getUser() != null && newCollection.getUser().getPartyUid() != null) {
				User ownerUser = getUserService().findByGooruId(newCollection.getUser().getPartyUid());
				collection.setUser(ownerUser);
			}
			if (newCollection.getNetwork() != null) {
				collection.setNetwork(newCollection.getNetwork());
			}
		}
		if (newCollection.getPosition() != null) {
			CollectionItem parentCollectionItem = this.getCollectionDao().getCollectionItemById(collectionId, user);
			if (parentId == null) {
				parentId = parentCollectionItem.getCollection().getGooruOid();
			}
			Collection parentCollection = getCollectionDao().getCollectionByUser(parentId, user.getPartyUid());
			this.resetSequence(parentCollection, parentCollectionItem.getCollectionItemId(), newCollection.getPosition(), user.getPartyUid(), COLLECTION);
		}
		if (newCollection.getMediaFilename() != null) {
			String folderPath = Collection.buildResourceFolder(collection.getContentId());
			this.getGooruImageUtil().imageUpload(newCollection.getMediaFilename(), folderPath, COLLECTION_IMAGE_DIMENSION);
			StringBuilder basePath = new StringBuilder(folderPath);
			basePath.append(File.separator).append(newCollection.getMediaFilename());
			collection.setImagePath(basePath.toString());
		}
		updateCollection(collection, newCollection, user);
		Map<String, Object> data = generateCollectionMetaData(collection, newCollection, user);
		if (data != null && data.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collection.getContentId());
			updateContentMeta(contentMeta, data);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCollectionItem(String collectionId, final String collectionItemId, CollectionItem newCollectionItem, User user) {
		final CollectionItem collectionItem = this.getCollectionDao().getCollectionItem(collectionItemId);
		rejectIfNull(collectionItem, GL0056, 404, _COLLECTION_ITEM);
		if (newCollectionItem.getNarration() != null) {
			collectionItem.setNarration(newCollectionItem.getNarration());
		}
		if (newCollectionItem.getStart() != null) {
			collectionItem.setStart(newCollectionItem.getStart());
		}
		if (newCollectionItem.getStop() != null) {
			collectionItem.setStop(newCollectionItem.getStop());
		}
		if (newCollectionItem.getPosition() != null) {
			Collection parentCollection = getCollectionDao().getCollectionByUser(collectionId, user.getPartyUid());
			this.resetSequence(parentCollection, collectionItem.getCollectionItemId(), newCollectionItem.getPosition(), user.getPartyUid(), COLLECTION_ITEM);
		}
		this.getCollectionDao().save(collectionItem);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> createResource(String collectionId, CollectionItem collectionItem, User user) {
		Resource resource = collectionItem.getResource();
		final Errors errors = validateResource(resource);
		if (!errors.hasErrors()) {
			Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
			rejectIfNull(collection, GL0056, 404, COLLECTION);
			resource.setSharing(collection.getSharing());
			resource = getResourceBoService().createResource(resource, user);
			collectionItem.setItemType(ADDED);
			collectionItem = createCollectionItem(collectionItem, collection, resource, user);
			getCollectionEventLog().collectionItemEventLog(collectionId, collectionItem, user.getPartyUid(), RESOURCE, collectionItem, ADD);
			updateCollectionMetaDataSummary(collection.getContentId(), RESOURCE, ADD);
			Map<String, Object> data = generateResourceMetaData(resource, collectionItem.getResource(), user);
			createContentMeta(resource, data);

		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateResource(String collectionId, String collectionResourceItemId, CollectionItem newCollectionItem, User user) {
		final CollectionItem collectionItem = this.getCollectionDao().getCollectionItem(collectionResourceItemId);
		rejectIfNull(collectionItem, GL0056, 404, _COLLECTION_ITEM);
		this.getResourceBoService().updateResource(collectionItem.getContent().getGooruOid(), newCollectionItem.getResource(), user);
		Map<String, Object> data = generateResourceMetaData(collectionItem.getContent(), newCollectionItem.getResource(), user);
		if (data != null && data.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collectionItem.getContent().getContentId());
			updateContentMeta(contentMeta, data);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CollectionItem createQuestion(String collectionId, String data, User user) {
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		AssessmentQuestion question = getQuestionService().createQuestion(data, user);
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setItemType(ADDED);
		collectionItem = createCollectionItem(collectionItem, collection, question, user);
		getCollectionEventLog().collectionItemEventLog(collectionId, collectionItem, user.getPartyUid(), QUESTION, data, ADD);
		collectionItem.setQuestion(question);
		collectionItem.setTitle(question.getTitle());
		updateCollectionMetaDataSummary(collection.getContentId(), QUESTION, ADD);
		Map<String, Object> metaData = generateQuestionMetaData(question, question, user);
		createContentMeta(question, metaData);
		return collectionItem;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateQuestion(String collectionId, String collectionQuestionItemId, String data, User user) {
		final CollectionItem collectionItem = this.getCollectionDao().getCollectionItem(collectionQuestionItemId);
		rejectIfNull(collectionItem, GL0056, 404, _COLLECTION_ITEM);
		AssessmentQuestion newAssessmentQuestion = this.getQuestionService().updateQuestion(collectionItem.getContent().getGooruOid(), data, user);
		Map<String, Object> metaData = generateQuestionMetaData(collectionItem.getContent(), newAssessmentQuestion, user);
		if (metaData != null && metaData.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collectionItem.getContent().getContentId());
			updateContentMeta(contentMeta, metaData);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CollectionItem addResource(String collectionId, String resourceId, User user) {
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		Resource resource = getResourceBoService().getResource(resourceId);
		rejectIfNull(resource, GL0056, 404, RESOURCE);
		reject(!resource.getContentType().getName().equalsIgnoreCase(QUESTION), GL0056, 404, RESOURCE);
		updateCollectionMetaDataSummary(collection.getContentId(), RESOURCE, ADD);
		CollectionItem collectionItem = new CollectionItem();
		getCollectionEventLog().collectionItemEventLog(collectionId, collectionItem, user.getPartyUid(), RESOURCE, null, ADD);
		collectionItem.setItemType(ADDED);
		return createCollectionItem(collectionItem, collection, resource, user);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CollectionItem addQuestion(String collectionId, String questionId, User user) {
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		AssessmentQuestion question = this.getQuestionService().getQuestion(questionId);
		rejectIfNull(question, GL0056, 404, QUESTION);
		AssessmentQuestion copyQuestion = this.getQuestionService().copyQuestion(question, user);
		CollectionItem collectionItem = new CollectionItem();
		getCollectionEventLog().collectionItemEventLog(collectionId, collectionItem, user.getPartyUid(), QUESTION, null, ADD);
		collectionItem.setItemType(ADDED);
		collectionItem = createCollectionItem(collectionItem, collection, copyQuestion, user);
		updateCollectionMetaDataSummary(collection.getContentId(), QUESTION, ADD);
		Map<String, Object> metaData = generateQuestionMetaData(copyQuestion, copyQuestion, user);
		createContentMeta(copyQuestion, metaData);
		return collectionItem;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getCollection(String collectionId,String collectionType, User user, boolean includeItems, boolean includeLastModifiedUser) {
		Map<String, Object> collection = super.getCollection(collectionId, collectionType);
		StringBuilder key = new StringBuilder(ALL_);
		key.append(collection.get(GOORU_OID));
		collection.put(VIEWS, getDashboardCassandraService().readAsLong(key.toString(), COUNT_VIEWS));
		if (includeItems) {
			collection.put(COLLECTION_ITEMS, this.getCollectionItems(collectionId, MAX_LIMIT, 0));
		}
		if (includeLastModifiedUser) {
			Object lastModifiedUserUid = collection.get(LAST_MODIFIED_USER_UID);
			if (lastModifiedUserUid != null) {
				collection.put(LAST_USER_MODIFIED, getLastCollectionModifyUser(String.valueOf(lastModifiedUserUid)));
			}
		}
		collection.remove(LAST_MODIFIED_USER_UID);
		final boolean isCollaborator = this.getCollaboratorRepository().findCollaboratorById(collectionId, user.getPartyUid()) != null ? true : false;
		collection.put(PERMISSIONS, getContentService().getContentPermission(collectionId, user));
		collection.put(IS_COLLABORATOR, isCollaborator);
		return collection;
	}

	private Map<String, Object> getLastCollectionModifyUser(String userUid) {
		Map<String, Object> lastUserModifiedMap = null;
		final User lastUserModified = this.getUserService().findByGooruId(userUid);
		if (lastUserModified != null) {
			lastUserModifiedMap = new HashMap<String, Object>();
			lastUserModifiedMap.put(USER_NAME, lastUserModified.getUsername());
			lastUserModifiedMap.put(GOORU_UID, lastUserModified.getGooruUId());
		}
		return lastUserModifiedMap;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		String[] collectionTypes = collectionType.split(COMMA);
		filters.put(COLLECTION_TYPE, collectionTypes);
		filters.put(PARENT_GOORU_OID, lessonId);
		List<Map<String, Object>> results = this.getCollections(filters,limit, offset);
		List<Map<String, Object>> collections = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> collection : results) {
			collections.add(mergeMetaData(collection));
		}
		return collections;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getCollectionItems(String collectionId, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_ID, collectionId);
		List<Map<String, Object>> collectionItems = this.getCollectionDao().getCollectionItem(filters, limit, offset);
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> item : collectionItems) {
			StringBuilder key = new StringBuilder(ALL_);
			key.append(item.get(GOORU_OID));
			item.put(VIEWS, getDashboardCassandraService().readAsLong(key.toString(), COUNT_VIEWS));
			items.add(mergeCollectionItemMetaData(item));
		}
		return items;
	}

	@Override
	public Map<String, Object> getCollectionItem(String collectionId, String collectionItemId) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_ID, collectionId);
		filters.put(COLLECTION_ITEM_ID, collectionItemId);
		List<Map<String, Object>> collectionItems = this.getCollectionDao().getCollectionItem(filters, 1, 0);
		rejectIfNull(((collectionItems == null || collectionItems.size() == 0) ? null : collectionItems.get(0)), GL0056, _COLLECTION_ITEM);
		return mergeCollectionItemMetaData(collectionItems.get(0));
	}

	// Validation to check for the collection already played, should not be
	// moved
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void moveCollection(String courseId, String unitId, String lessonId, String collectionId, User user) {
		Collection lesson = this.getCollectionDao().getCollectionByType(lessonId, LESSON_TYPE);
		rejectIfNull(lesson, GL0056, 404, LESSON);
		Collection unit = this.getCollectionDao().getCollectionByType(unitId, UNIT_TYPE);
		rejectIfNull(unit, GL0056, 404, UNIT);
		Collection course = this.getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		rejectIfNull(course, GL0056, 404, COURSE);
		String collectionType = moveCollection(collectionId, lesson, user);
		if (collectionType != null) {
			updateContentMetaDataSummary(lesson.getContentId(), collectionType, ADD);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void moveCollection(String folderId, String collectionId, User user) {
		Collection targetCollection = null;
		if (folderId != null) {
			targetCollection = this.getCollectionDao().getCollectionByType(folderId, FOLDER_TYPE);
			rejectIfNull(targetCollection, GL0056, 404, FOLDER);
		} else {
			targetCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
			if (targetCollection == null) {
				targetCollection = new Collection();
				targetCollection.setCollectionType(CollectionType.SHElf.getCollectionType());
				targetCollection.setTitle(CollectionType.SHElf.getCollectionType());
				super.createCollection(targetCollection, user);
			}
		}
		moveCollection(collectionId, targetCollection, user);
	}

	private String moveCollection(String collectionId, Collection targetCollection, User user) {
		CollectionItem sourceCollectionItem = getCollectionDao().getCollectionItemById(collectionId, user);
		rejectIfNull(sourceCollectionItem, GL0056, 404, COLLECTION);
		// need to put validation for collaborator
		CollectionItem collectionItem = new CollectionItem();
		if (sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		String collectionType = getParentCollection(sourceCollectionItem.getContent().getContentId(), sourceCollectionItem.getContent().getContentType().getName(), collectionId, targetCollection);
		String contentType = null;
		if (collectionType.equalsIgnoreCase(LESSON)) {
			contentType = sourceCollectionItem.getContent().getContentType().getName();
		}
		createCollectionItem(collectionItem, targetCollection, sourceCollectionItem.getContent(), user);
		resetSequence(sourceCollectionItem.getCollection().getGooruOid(), sourceCollectionItem.getCollectionItemId(), user.getPartyUid(), COLLECTION);
		getCollectionDao().remove(sourceCollectionItem);
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");
		return contentType;
	}

	private String getParentCollection(Long collectionId, String collectionType, String gooruOid, Collection targetCollection) {
		CollectionItem lesson = this.getCollectionDao().getParentCollection(collectionId);
		reject(!(lesson.getCollection().getGooruOid().equalsIgnoreCase(targetCollection.getGooruOid())), GL0111, 404, lesson.getCollection().getCollectionType());
		if (lesson.getCollection().getCollectionType().equalsIgnoreCase(LESSON)) {
			updateContentMetaDataSummary(lesson.getCollection().getContentId(), collectionType, DELETE);
		}
		return targetCollection.getCollectionType();
	}

	private Map<String, Object> generateCollectionMetaData(Collection collection, Collection newCollection, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (newCollection.getStandardIds() != null) {
			List<Map<String, Object>> standards = updateContentCode(collection, newCollection.getStandardIds(), MetaConstants.CONTENT_CLASSIFICATION_STANDARD_TYPE_ID);
			data.put(STANDARDS, standards);
		}

		if (newCollection.getSkillIds() != null) {
			List<Map<String, Object>> skills = updateContentCode(collection, newCollection.getSkillIds(), MetaConstants.CONTENT_CLASSIFICATION_SKILLS_TYPE_ID);
			data.put(SKILLS, skills);
		}
		if (newCollection.getAudienceIds() != null) {
			List<Map<String, Object>> audiences = updateContentMetaAssoc(collection, user, AUDIENCE, newCollection.getAudienceIds());
			data.put(AUDIENCE, audiences);
		}
		if (newCollection.getDepthOfKnowledgeIds() != null) {
			List<Map<String, Object>> depthOfKnowledge = updateContentMetaAssoc(collection, user, DEPTH_OF_KNOWLEDGE, newCollection.getDepthOfKnowledgeIds());
			data.put(DEPTHOF_KNOWLEDGE, depthOfKnowledge);
		}
		if (newCollection.getTaxonomyCourseIds() != null) {
			List<Map<String, Object>> taxonomyCourse = updateTaxonomyCourse(collection, newCollection.getTaxonomyCourseIds());
			data.put(TAXONOMY_COURSE, taxonomyCourse);
		}
		return data;
	}

	private void updateCollectionSettings(Collection collection, Collection newCollection) {
		ContentSettings contentSettings = null;
		final Map<String, String> settings = new HashMap<String, String>();
		if (collection.getContentSettings() != null && collection.getContentSettings().size() > 0) {
			contentSettings = collection.getContentSettings().iterator().next();
			final Map<String, String> contentSettingsMap = JsonDeserializer.deserialize(contentSettings.getData(), new TypeReference<Map<String, String>>() {
			});
			settings.putAll(contentSettingsMap);
		}
		settings.putAll(newCollection.getSettings());
		newCollection.setSettings(settings);
		ContentSettings contentSetting = contentSettings == null ? new ContentSettings() : contentSettings;
		contentSetting.setContent(collection);
		contentSetting.setData(new JSONSerializer().exclude(EXCLUDE).serialize(newCollection.getSettings()));
		this.getCollectionDao().save(contentSetting);
	}

	private Map<String, Object> mergeCollectionItemMetaData(Map<String, Object> content) {
		Object data = content.get(META_DATA);
		if (data != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, Object>>() {
			});
			content.putAll(metaData);
		}
		Map<String, Object> resourceFormat = new HashMap<String, Object>();
		resourceFormat.put(VALUE, content.get(VALUE));
		resourceFormat.put(DISPLAY_NAME, content.get(DISPLAY_NAME));
		content.put(RESOURCEFORMAT, resourceFormat);
		Object ratingAverage = content.get(AVERAGE);
		String typeName = (String) content.get(RESOURCE_TYPE);
		Map<String, Object> resourceType = new HashMap<String, Object>();
		resourceType.put(NAME, typeName);
		content.put(RESOURCE_TYPE, resourceType);
		if (ratingAverage != null) {
			Map<String, Object> rating = new HashMap<String, Object>();
			rating.put(AVERAGE, content.get(AVERAGE));
			rating.put(COUNT, content.get(COUNT));
			content.put(RATING, rating);
		}

		Object thumbnail = content.get(THUMBNAIL);
		if (thumbnail != null) {
			StringBuilder imagePath = new StringBuilder();
			imagePath.append(content.get(FOLDER)).append(thumbnail);
			content.put(THUMBNAILS, GooruImageUtil.getThumbnails(imagePath.toString()));
		}
		if (typeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
			// To-Do, need fix later, by getting answer and hints details
			// without querying the assessment object
			String gooruOid = (String) content.get(GOORU_OID);
			AssessmentQuestion assessmentQuestion = this.getQuestionService().getQuestion(gooruOid);
			if (assessmentQuestion != null && !assessmentQuestion.isQuestionNewGen()) {
				content.put(ANSWERS, assessmentQuestion.getAnswers());
				content.put(HINTS, assessmentQuestion.getHints());
			} else {
				String json = getMongoQuestionsService().getQuestionByIdWithJsonAdjustments(gooruOid);
				if (json != null) {
					content.putAll(JsonDeserializer.deserialize(json, new TypeReference<Map<String, Object>>() {
					}));
				}
			}
		}
		content.put(USER, setUser(content.get(GOORU_UID), content.get(USER_NAME)));
		content.put(ASSET_URI, ConfigProperties.getBaseRepoUrl());
		content.remove(THUMBNAIL);
		content.remove(META_DATA);
		content.remove(VALUE);
		content.remove(DISPLAY_NAME);
		content.remove(AVERAGE);
		content.remove(COUNT);
		content.remove(GOORU_UID);
		content.remove(USER_NAME);
		return content;
	}

	private CollectionItem createCollection(User user, Collection collection, Collection parentCollection) {
		CollectionItem collectionItem = createCollection(collection, parentCollection, user);
		if (collection.getSharing() != null && !collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType()) && collection.getSharing().equalsIgnoreCase(PUBLIC)) {
			collection.setSharing(Sharing.ANYONEWITHLINK.getSharing());
		}
		if (collection.getSharing() != null && !collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType()) && collection.getSharing().equalsIgnoreCase(PUBLIC)) {
			collection.setPublishStatusId(Constants.PUBLISH_PENDING_STATUS_ID);
			collection.setSharing(Sharing.ANYONEWITHLINK.getSharing());
		}
		createCollectionSettings(collection);
		if (!collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_URL.getType())) {
			getIndexHandler().setReIndexRequest(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);
		}
		return collectionItem;
	}

	private void createCollectionSettings(Collection collection) {
		final ContentSettings contentSetting = new ContentSettings();
		if (collection.getSettings() == null || collection.getSettings().size() == 0) {
			collection.setSettings(Constants.COLLECTION_DEFAULT_SETTINGS);
		}
		contentSetting.setContent(collection);
		contentSetting.setData(new JSONSerializer().exclude(EXCLUDE).serialize(collection.getSettings()));
		getCollectionDao().save(contentSetting);
	}

	@SuppressWarnings("unchecked")
	private void updateCollectionMetaDataSummary(Long collectionId, String type, String action) {
		ContentMeta lessonContentMeta = this.getContentRepository().getContentMeta(collectionId);
		if (lessonContentMeta != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(lessonContentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
			});
			Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
			if (summary != null && summary.size() > 0) {
				int resourceCount = ((Number) summary.get(MetaConstants.RESOURCE_COUNT)).intValue();
				int questionCount = ((Number) summary.get(MetaConstants.QUESTION_COUNT)).intValue();
				if (type.equalsIgnoreCase(RESOURCE)) {
					summary.put(MetaConstants.RESOURCE_COUNT, action.equalsIgnoreCase(ADD) ? (resourceCount + 1) : (resourceCount - 1));
				}
				if (type.equalsIgnoreCase(QUESTION)) {
					summary.put(MetaConstants.QUESTION_COUNT, action.equalsIgnoreCase(ADD) ? (questionCount + 1) : (questionCount - 1));
				}
				metaData.put(SUMMARY, summary);
				updateContentMeta(lessonContentMeta, metaData);
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCollectionItem(String collectionId, String collectionItemId, String userUid) {
		CollectionItem collectionItem = this.getCollectionDao().getCollectionItem(collectionItemId);
		rejectIfNull(collectionItem, GL0056, 404, _COLLECTION_ITEM);
		Resource resource = this.getResourceBoService().getResource(collectionItem.getContent().getGooruOid());
		rejectIfNull(resource, GL0056, 404, RESOURCE);
		String contentType = resource.getContentType().getName();
		Long collectionContentId = collectionItem.getCollection().getContentId();
		this.resetSequence(collectionId, collectionItem.getCollectionItemId(), userUid, COLLECTION_ITEM);
		getCollectionEventLog().collectionItemEventLog(collectionId, collectionItem, userUid, contentType, null, DELETE);
		if (contentType.equalsIgnoreCase(QUESTION)) {
			getCollectionDao().remove(resource);
		} else {
			getCollectionDao().remove(collectionItem);
		}
		updateCollectionMetaDataSummary(collectionContentId, RESOURCE, contentType);
	}

	private Errors validateResource(final Resource resource) {
		final Errors errors = new BindException(resource, RESOURCE);
		if (resource != null) {
			rejectIfNullOrEmpty(errors, resource.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	private Errors validateCollection(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfInvalidType(errors, collection.getCollectionType(), COLLECTION_TYPE, GL0007, generateErrorMessage(GL0007, COLLECTION_TYPE), Constants.COLLECTION_TYPES);
			if (collection.getPublishStatusId() != null) {
				reject(Constants.PUBLISH_STATUS.containsValue(collection.getPublishStatusId()), GL0007, 400, PUBLISH_STATUS);
			}
		}
		return errors;
	}

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public GooruImageUtil getGooruImageUtil() {
		return gooruImageUtil;
	}

	public ResourceBoService getResourceBoService() {
		return resourceBoService;
	}

	public QuestionService getQuestionService() {
		return questionService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

	public CollectionEventLog getCollectionEventLog() {
		return collectionEventLog;
	}

}
