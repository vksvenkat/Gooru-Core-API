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
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
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
	private CollectionRepository collectionRepository;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	private final static String COLLECTION_IMAGE_DIMENSION = "160x120,75x56,120x90,80x60,800x600";

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createCollection(User user, Collection collection) {
		if (collection.getBuildTypeId() != null) {
			collection.setBuildTypeId(Constants.BUILD_WEB_TYPE_ID);
		}
		final Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			Collection parentCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
			if (parentCollection == null) {
				parentCollection = new Collection();
				parentCollection.setCollectionType(CollectionType.SHElf.getCollectionType());
				parentCollection.setTitle(CollectionType.SHElf.getCollectionType());
				super.createCollection(parentCollection, user);
				createCollection(user, collection, parentCollection);
			}
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCollection(String courseId, String unitId, String lessonId, String collectionId, User user) {
		Collection collection = this.getCollectionDao().getCollection(collectionId);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		reject(this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId, user), GL0099, 403, COLLECTION);
		Collection lesson = getCollectionDao().getCollectionByType(lessonId, LESSON);
		rejectIfNull(lesson, GL0056, 404, LESSON);
		Collection course = getCollectionDao().getCollectionByType(courseId, COURSE);
		rejectIfNull(course, GL0056, 404, COURSE);
		Collection unit = getCollectionDao().getCollectionByType(unitId, UNIT);
		rejectIfNull(unit, GL0056, 404, UNIT);
		this.resetSequence(lessonId, collection.getGooruOid());
		this.deleteCollection(collectionId);
		this.updateMetaDataSummary(course.getContentId(), unit.getContentId(), lesson.getContentId(), collection.getCollectionType(), DELETE);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createCollection(String courseId, String unitId, String lessonId, User user, Collection collection) {
		final Errors errors = validateCollection(collection);
		if (!errors.hasErrors()) {
			Collection course = getCollectionDao().getCollectionByType(courseId, COURSE);
			rejectIfNull(course, GL0056, 404, COURSE);
			Collection unit = getCollectionDao().getCollectionByType(unitId, UNIT);
			rejectIfNull(unit, GL0056, 404, UNIT);
			Collection lesson = getCollectionDao().getCollectionByType(lessonId, LESSON);
			rejectIfNull(lesson, GL0056, 404, LESSON);
			createCollection(user, collection, lesson);
			Map<String, Object> data = generateCollectionMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.COLLECTION_SUMMARY);
			createContentMeta(collection, data);
			updateMetaDataSummary(course.getContentId(), unit.getContentId(), lesson.getContentId(), collection.getCollectionType(), ADD);
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCollection(String collectionId, Collection newCollection, User user) {
		boolean hasUnrestrictedContentAccess = this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId, user);
		// TO-Do add validation for collection type and collaborator validation
		Collection collection = getCollectionDao().getCollection(collectionId);
		if (newCollection.getSharing() != null && (newCollection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newCollection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing()))) {
			if (!newCollection.getSharing().equalsIgnoreCase(PUBLIC)) {
				collection.setPublishStatusId(null);
			}
			collection.setSharing(newCollection.getSharing());
		}
		if (newCollection.getSettings() != null) {
			updateCollectionSettings(collection, newCollection);
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
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, 404, _COLLECTION_ITEM);
		Collection collection = this.getCollectionDao().getCollectionByType(collectionId, COLLECTION);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
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
			this.resetSequence(collection, collectionItem.getContent().getGooruOid(), newCollectionItem.getPosition());
		}
		this.getCollectionRepository().save(collectionItem);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<CollectionItem> createResource(String collectionId, CollectionItem collectionItem, User user) {
		Resource resource = collectionItem.getResource();
		final Errors errors = validateResource(resource);
		if (!errors.hasErrors()) {
			Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION);
			rejectIfNull(collection, GL0056, 404, COLLECTION);
			resource.setSharing(collection.getSharing());
			resource = getResourceBoService().createResource(resource, user);
			collectionItem.setItemType(ADDED);
			collectionItem = createCollectionItem(collectionItem, collection, resource, user);
			updateCollectionMetaDataSummary(collection.getContentId(), RESOURCE);
			Map<String, Object> data = generateResourceMetaData(resource, collectionItem.getResource(), user);
			createContentMeta(resource, data);

		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateResource(String collectionId, String collectionResourceItemId, CollectionItem newCollectionItem, User user) {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionResourceItemId);
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
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		AssessmentQuestion question = getQuestionService().createQuestion(data, user);
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setItemType(ADDED);
		collectionItem = createCollectionItem(collectionItem, collection, question, user);
		collectionItem.setQuestion(question);
		collectionItem.setTitle(question.getTitle());
		updateCollectionMetaDataSummary(collection.getContentId(), QUESTION);
		Map<String, Object> metaData = generateQuestionMetaData(question, question, user);
		createContentMeta(question, metaData);
		return collectionItem;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateQuestion(String collectionId, String collectionQuestionItemId, String data, User user) {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionQuestionItemId);
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
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		Resource resource = getResourceBoService().getResource(resourceId);
		rejectIfNull(resource, GL0056, 404, RESOURCE);
		updateCollectionMetaDataSummary(collection.getContentId(), QUESTION);
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setItemType(ADDED);
		return createCollectionItem(collectionItem, collection, resource, user);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CollectionItem addQuestion(String collectionId, String questionId, User user) {
		Collection collection = getCollectionDao().getCollectionByType(collectionId, COLLECTION);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		AssessmentQuestion question = this.getQuestionService().copyQuestion(questionId, user);
		rejectIfNull(question, GL0056, 404, QUESTION);
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setItemType(ADDED);
		collectionItem = createCollectionItem(collectionItem, collection, question, user);
		Map<String, Object> metaData = generateQuestionMetaData(question, question, user);
		createContentMeta(question, metaData);
		return collectionItem;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getCollection(String collectionId, String collectionType, User user, boolean includeItems) {
		Map<String, Object> collection = super.getCollection(collectionId, collectionType);
		StringBuilder key = new StringBuilder(ALL_);
		key.append(collection.get(GOORU_OID));
		collection.put(VIEWS, getDashboardCassandraService().readAsLong(key.toString(), COUNT_VIEWS));
		if (includeItems) {
			collection.put(COLLECTION_ITEMS, this.getCollectionItems(collectionId, MAX_LIMIT, 0));
		}
		final boolean isCollaborator = this.getCollaboratorRepository().findCollaboratorById(collectionId, user.getGooruUId()) != null ? true : false;
		collection.put(PERMISSIONS, getContentService().getContentPermission(collectionId, user));
		collection.put(IS_COLLABORATOR, isCollaborator);
		return collection;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		String[] collectionTypes = collectionType.split(COMMA);
		filters.put(COLLECTION_TYPE, collectionTypes);
		filters.put(PARENT_GOORU_OID, lessonId);
		List<Map<String, Object>> results = this.getCollections(filters, limit, offset);
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
	public void moveCollectionToLesson(String courseId, String unitId, String lessonId, String collectionId, User user) {
		CollectionItem sourceCollectionItem = getCollectionDao().getCollectionItemById(collectionId, user);
		rejectIfNull(sourceCollectionItem, GL0056, 404, COLLECTION);
		// need to put validation for collaborator
		// reject(this.getOperationAuthorizer().hasUnrestrictedContentAccess(collectionId,
		// user), GL0099, 403, COLLECTION);
		Collection lesson = this.getCollectionDao().getCollectionByType(lessonId, LESSON);
		rejectIfNull(lesson, GL0056, 404, LESSON);
		Collection unit = this.getCollectionDao().getCollectionByType(unitId, UNIT);
		rejectIfNull(unit, GL0056, 404, UNIT);
		Collection course = this.getCollectionDao().getCollectionByType(courseId, COURSE);
		rejectIfNull(course, GL0056, 404, COURSE);
		CollectionItem collectionItem = new CollectionItem();
		if (sourceCollectionItem.getItemType() != null) {
			collectionItem.setItemType(sourceCollectionItem.getItemType());
		}
		String collectionType = getParentCollection(sourceCollectionItem.getContent().getContentId(), sourceCollectionItem.getContent().getContentType().getName(), collectionId);
		if (collectionType.equalsIgnoreCase(SHELF) || collectionType.equals(FOLDER)) {
			this.createCollectionItem(collectionItem, lesson, sourceCollectionItem.getContent(), user);
		} else {
			this.getCollectionDao().deleteCollectionItem(sourceCollectionItem.getContent().getContentId());
			this.createCollectionItem(collectionItem, lesson, sourceCollectionItem.getContent(), user);
			this.updateMetaDataSummary(course.getContentId(), unit.getContentId(), lesson.getContentId(), sourceCollectionItem.getContent().getContentType().getName(), ADD);
		}
	}

	private String getParentCollection(Long collectionId, String collectionType, String gooruOid) {
		CollectionItem lesson = this.getCollectionDao().getParentCollection(collectionId);
		if (lesson.getCollection().getCollectionType().equalsIgnoreCase(LESSON)) {
			CollectionItem unit = this.getCollectionDao().getParentCollection(lesson.getCollection().getContentId());
			CollectionItem course = this.getCollectionDao().getParentCollection(unit.getCollection().getContentId());
			this.updateMetaDataSummary(course.getCollection().getContentId(), unit.getCollection().getContentId(), lesson.getCollection().getContentId(), collectionType, DELETE);
			this.resetSequence(lesson.getCollection().getGooruOid(), gooruOid);
		}
		return lesson.getCollection().getCollectionType();
	}

	private void updateMetaDataSummary(Long courseId, Long unitId, Long lessonId, String collectionType, String action) {
		ContentMeta unitContentMeta = this.getContentRepository().getContentMeta(unitId);
		ContentMeta courseContentMeta = this.getContentRepository().getContentMeta(courseId);
		ContentMeta lessonContentMeta = this.getContentRepository().getContentMeta(lessonId);
		if (lessonContentMeta != null) {
			updateSummaryMeta(collectionType, lessonContentMeta, action);
		}
		if (unitContentMeta != null) {
			updateSummaryMeta(collectionType, unitContentMeta, action);
		}
		if (courseContentMeta != null) {
			updateSummaryMeta(collectionType, courseContentMeta, action);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateSummaryMeta(String collectionType, ContentMeta contentMeta, String action) {
		Map<String, Object> metaData = JsonDeserializer.deserialize(contentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
		if (collectionType.equalsIgnoreCase(CollectionType.ASSESSMENT.getCollectionType())) {
			int assessmentCount = ((Number) summary.get(MetaConstants.ASSESSMENT_COUNT)).intValue();
			if (action.equalsIgnoreCase(DELETE)) {
				assessmentCount -= 1;
			} else if (action.equalsIgnoreCase(ADD)) {
				assessmentCount += 1;
			}
			summary.put(MetaConstants.ASSESSMENT_COUNT, assessmentCount);
		}
		if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
			int collectionCount = ((Number) summary.get(MetaConstants.COLLECTION_COUNT)).intValue();
			if (action.equalsIgnoreCase(DELETE)) {
				collectionCount -= 1;
			} else if (action.equalsIgnoreCase(ADD)) {
				collectionCount += 1;
			}
			summary.put(MetaConstants.COLLECTION_COUNT, collectionCount);
		}
		metaData.put(SUMMARY, summary);
		updateContentMeta(contentMeta, metaData);
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
			content.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
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
				content.putAll(JsonDeserializer.deserialize(json, new TypeReference<Map<String, Object>>() {
				}));
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

	private Collection createCollection(User user, Collection collection, Collection parentCollection) {
		createCollection(collection, parentCollection, user);
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
		return collection;
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
	private void updateCollectionMetaDataSummary(Long collectionId, String type) {
		ContentMeta lessonContentMeta = this.getContentRepository().getContentMeta(collectionId);
		if (lessonContentMeta != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(lessonContentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
			});
			Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
			if (type.equalsIgnoreCase(RESOURCE)) {
				int resourceCount = ((Number) summary.get(MetaConstants.RESOURCE_COUNT)).intValue() + 1;
				summary.put(MetaConstants.RESOURCE_COUNT, resourceCount);
			}
			if (type.equalsIgnoreCase(QUESTION)) {
				int questionCount = ((Number) summary.get(MetaConstants.QUESTION_COUNT)).intValue() + 1;
				summary.put(MetaConstants.QUESTION_COUNT, questionCount);
			}
			metaData.put(SUMMARY, summary);
			updateContentMeta(lessonContentMeta, metaData);
		}
	}

	private Errors validateResource(final Resource resource) {
		final Errors errors = new BindException(resource, RESOURCE);
		if (resource != null) {
			rejectIfNullOrEmpty(errors, resource.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	private Errors validateQuestion(final AssessmentQuestion quetsion) {
		final Errors errors = new BindException(quetsion, QUESTION);
		if (quetsion != null) {
			rejectIfNullOrEmpty(errors, quetsion.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfNullOrEmpty(errors, quetsion.getQuestionText(), QUESTION_TEXT, GL0006, generateErrorMessage(GL0006, QUESTION_TEXT));
		}
		return errors;
	}

	private Errors validateCollection(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfInvalidType(errors, collection.getCollectionType(), COLLECTION_TYPE, GL0007, generateErrorMessage(GL0007, COLLECTION_TYPE), Constants.COLLECTION_TYPES);
			if (collection.getPublishStatusId() != null) {
				rejectIfInvalidType(errors, collection.getPublishStatusId(), PUBLISH_STATUS, GL0007, generateErrorMessage(GL0007, PUBLISH_STATUS), Constants.PUBLISH_STATUS);
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

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
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

}
