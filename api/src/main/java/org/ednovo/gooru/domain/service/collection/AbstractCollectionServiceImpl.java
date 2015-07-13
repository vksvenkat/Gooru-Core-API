package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentClassification;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.ContentTaxonomyCourseAssoc;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.TaxonomyCourseRepository;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentClassificationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

public abstract class AbstractCollectionServiceImpl extends BaseServiceImpl implements AbstractCollectionService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private ContentClassificationRepository contentClassificationRepository;

	@Autowired
	private IndexHandler indexHandler;

	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Autowired
	private TaxonomyCourseRepository taxonomyCourseRepository;

	@Autowired
	private SettingService settingService;

	protected final static String TAXONOMY_COURSE = "taxonomyCourse";

	protected final static String DEPTHOF_KNOWLEDGE = "depthOfKnowledge";

	public Collection createCollection(Collection collection, User user) {
		collection.setGooruOid(UUID.randomUUID().toString());
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection
				.setSharing(collection.getSharing() != null && (collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? collection
						.getSharing() : Sharing.ANYONEWITHLINK.getSharing());
		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setLastUpdatedUserUid(user.getGooruUId());
		collection.setContentType(this.getContentType(collection.getCollectionType()));
		collection.setIsRepresentative(1);
		collection.setDistinguish((short) 0);
		collection.setClusterUid(collection.getGooruOid());
		getCollectionDao().save(collection);
		return collection;
	}

	@Override
	public Collection createCollection(Collection collection, Collection parentCollection, User user) {
		collection = this.createCollection(collection, user);
		CollectionItem collectionItem = new CollectionItem();
		collectionItem.setItemType(ADDED);
		createCollectionItem(collectionItem, parentCollection, collection, user);
		return collection;
	}

	@Override
	public Collection updateCollection(Collection collection, Collection newCollection, User user) {
		rejectIfNull(collection, GL0056, _COLLECTION);
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
		if (newCollection.getUrl() != null) {
			collection.setUrl(newCollection.getUrl());
		}

		if (newCollection.getKeyPoints() != null) {
			collection.setKeyPoints(newCollection.getKeyPoints());
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
		if (newCollection.getMailNotification() != null) {
			collection.setMailNotification(newCollection.getMailNotification());
		}
		if (newCollection.getMailNotification() != null) {
			collection.setMailNotification(newCollection.getMailNotification());
		}
		collection.setLastUpdatedUserUid(user.getGooruUId());
		getCollectionDao().save(collection);
		return collection;

	}

	public void resetSequence(String parentGooruOid, String gooruOid) {
		CollectionItem itemSequence = this.getCollectionDao().getCollectionItem(parentGooruOid, gooruOid);
		int sequence = itemSequence.getItemSequence();
		List<CollectionItem> resetCollectionSequence = this.getCollectionDao().getCollectionItems(parentGooruOid, sequence, itemSequence.getContent().getContentType().getName());
		if (resetCollectionSequence != null) {
			for (CollectionItem collectionItem : resetCollectionSequence) {
				collectionItem.setItemSequence(sequence++);
			}
			this.getCollectionDao().saveAll(resetCollectionSequence);
		}
	}

	public void resetSequence(Collection parentCollection, String gooruOid, Integer newSequence) {
		int max = this.getCollectionDao().getCollectionItemMaxSequence(parentCollection.getContentId());
		reject((max > newSequence), GL0007, 404, ITEM_SEQUENCE);
		CollectionItem collectionItem = this.getCollectionDao().getCollectionItem(parentCollection.getGooruOid(), gooruOid);
		if (collectionItem != null) {
			List<CollectionItem> resetCollectionSequence = null;
			int displaySequence;
			int oldSequence = collectionItem.getItemSequence();
			if (newSequence > oldSequence) {
				resetCollectionSequence = this.getCollectionDao().getCollectionItems(collectionItem.getCollection().getGooruOid(), oldSequence, newSequence, collectionItem.getContent().getContentType().getName());
				displaySequence = oldSequence;
			} else {
				resetCollectionSequence = this.getCollectionDao().getCollectionItems(collectionItem.getCollection().getGooruOid(), newSequence, oldSequence, collectionItem.getContent().getContentType().getName());
				displaySequence = newSequence + 1;
			}
			if (resetCollectionSequence != null) {
				for (CollectionItem collectionSequence : resetCollectionSequence) {
					if (collectionSequence.getContent().getGooruOid() != gooruOid) {
						collectionSequence.setItemSequence(displaySequence++);
					} else if (collectionSequence.getContent().getGooruOid().equalsIgnoreCase(gooruOid)) {
						collectionSequence.setItemSequence(newSequence);
					}
				}
				this.getCollectionDao().saveAll(resetCollectionSequence);
			}
		}
	}

	@Override
	public List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset) {
		return getCollectionDao().getCollections(filters, limit, offset);
	}

	@Override
	public Map<String, Object> getCollection(String collectionId, String collectionType) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(GOORU_OID, collectionId);
		List<Map<String, Object>> collection = getCollectionDao().getCollections(filters, 1, 0);
		rejectIfNull((collection == null || collection.size() == 0 ? null : collection), GL0056, collectionType);
		return mergeMetaData(collection.get(0));
	}

	protected Map<String, Object> mergeMetaData(Map<String, Object> content) {
		Object data = content.get(META_DATA);
		if (data != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, Object>>() {
			});
			content.putAll(metaData);
		}
		Object settings = content.get(DATA);
		if (settings != null) {
			Map<String, Object> setting = JsonDeserializer.deserialize(String.valueOf(settings), new TypeReference<Map<String, Object>>() {
			});
			content.put(SETTINGS, setting);
		}
		Object buildType = content.get(BUILD_TYPE);
		if (buildType != null) {
			content.put(BUILD_TYPE, Constants.BUILD_TYPE.get(((Number) buildType).shortValue()));
		}
		Object thumbnail = content.get(IMAGE_PATH);
		if (thumbnail != null) {
			content.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
		}
		Object publishStatus = content.get(PUBLISH_STATUS);
		if (publishStatus != null) {
			content.put(PUBLISH_STATUS, Constants.PUBLISH_STATUS.get(((Number) publishStatus).shortValue()));
		}
		content.put(USER, setUser(content.get(GOORU_UID), content.get(USER_NAME)));
		content.remove(DATA);
		content.remove(META_DATA);
		content.remove(IMAGE_PATH);
		content.remove(GOORU_UID);
		content.remove(USER_NAME);
		return content;
	}

	@Override
	public void deleteCollection(String collectionId) {
		Collection collection = getCollectionDao().getCollection(collectionId);
		rejectIfNull(collection, GL0056, COLLECTION);
		getCollectionDao().remove(collection);
	}

	@Override
	public CollectionItem createCollectionItem(CollectionItem collectionItem, Collection parentContent, Content content, User user) {
		int sequence = getCollectionDao().getCollectionItemMaxSequence(parentContent.getContentId()) + 1;
		collectionItem.setCollection(parentContent);
		collectionItem.setContent(content);
		collectionItem.setAssociatedUser(user);
		collectionItem.setAssociationDate(new Date(System.currentTimeMillis()));
		collectionItem.setItemSequence(sequence);
		this.getCollectionDao().save(collectionItem);
		return collectionItem;
	}

	@Override
	public void createContentMeta(Content content, Map<String, Object> data) {
		ContentMeta contentMeta = new ContentMeta();
		contentMeta.setContent(content);
		contentMeta.setMetaData(SerializerUtil.serialize(data));
		this.getCollectionDao().save(contentMeta);
	}

	@Override
	public void updateContentMeta(ContentMeta contentMeta, Map<String, Object> data) {
		if (contentMeta != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(contentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
			});
			metaData.putAll(data);
			contentMeta.setMetaData(SerializerUtil.serialize(metaData));
			this.getCollectionDao().save(contentMeta);
		}
	}

	@Override
	public List<Map<String, Object>> updateContentMetaAssoc(Content content, User user, String key, List<Integer> metaIds) {
		this.getContentRepository().deleteContentMetaAssoc(content.getContentId(), key);
		List<Map<String, Object>> metaValues = null;
		if (metaIds != null && metaIds.size() > 0) {
			metaValues = new ArrayList<Map<String, Object>>();
			List<CustomTableValue> values = this.getCustomTableRepository().getCustomValues(metaIds);
			List<ContentMetaAssociation> metaDatas = new ArrayList<ContentMetaAssociation>();
			if (values != null) {
				for (CustomTableValue value : values) {
					ContentMetaAssociation contentMetaAssoc = new ContentMetaAssociation();
					contentMetaAssoc.setContent(content);
					contentMetaAssoc.setCreatedOn(new Date(System.currentTimeMillis()));
					contentMetaAssoc.setTypeId(value);
					contentMetaAssoc.setUser(user);
					metaDatas.add(contentMetaAssoc);
					Map<String, Object> metaValue = new HashMap<String, Object>();
					metaValue.put(ID, value.getCustomTableValueId());
					metaValue.put(NAME, value.getDisplayName());
					metaValues.add(metaValue);
				}
				this.getContentRepository().saveAll(metaDatas);
			}
		}
		return metaValues;
	}

	@SuppressWarnings("unchecked")
	public void deleteValidation(Long contentId, String collectionType) {
		ContentMeta contentMeta = this.getContentRepository().getContentMeta(contentId);
		Map<String, Object> metaData = JsonDeserializer.deserialize(contentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
		});
		Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
		int assessmentCount = ((Number) summary.get(MetaConstants.ASSESSMENT_COUNT)).intValue();
		int collectionCount = ((Number) summary.get(MetaConstants.COLLECTION_COUNT)).intValue();
		reject((assessmentCount == 0 && collectionCount == 0), GL0110, 400, collectionType);
	}

	public List<Map<String, Object>> updateContentCode(Content content, List<Integer> codeIds, Short typeId) {
		this.getContentClassificationRepository().deleteContentClassification(content.getContentId(), typeId);
		List<Map<String, Object>> codes = null;
		if (codeIds != null && codeIds.size() > 0) {
			List<Code> assocCodes = this.getContentClassificationRepository().getCodes(codeIds);
			if (assocCodes != null && assocCodes.size() > 0) {
				codes = new ArrayList<Map<String, Object>>();
				List<ContentClassification> contentClassifications = new ArrayList<ContentClassification>();
				for (Code code : assocCodes) {
					ContentClassification contentClassification = new ContentClassification();
					contentClassification.setContent(content);
					contentClassification.setCode(code);
					contentClassification.setTypeId(typeId);
					contentClassifications.add(contentClassification);
					Map<String, Object> assocCode = new HashMap<String, Object>();
					assocCode.put(ID, code.getCodeId());
					assocCode.put(ROOT_NODE_ID, code.getRootNodeId());
					assocCode.put(CODE, code.getCode());
					codes.add(assocCode);
				}
				this.getContentRepository().saveAll(contentClassifications);
			}
		}
		return codes;
	}

	public List<Map<String, Object>> updateTaxonomyCourse(Content content, List<Integer> taxonomyCourseIds) {
		this.getContentRepository().deleteContentTaxonomyCourseAssoc(content.getContentId());
		List<Map<String, Object>> courses = null;
		if (taxonomyCourseIds != null && taxonomyCourseIds.size() > 0) {
			List<TaxonomyCourse> taxonomyCourses = this.getTaxonomyCourseRepository().getTaxonomyCourses(taxonomyCourseIds);
			if (taxonomyCourses != null && taxonomyCourses.size() > 0) {
				courses = new ArrayList<Map<String, Object>>();
				List<ContentTaxonomyCourseAssoc> contentTaxonomyCourseAssocs = new ArrayList<ContentTaxonomyCourseAssoc>();
				for (TaxonomyCourse taxonomyCourse : taxonomyCourses) {
					Map<String, Object> course = new HashMap<String, Object>();
					course.put(ID, taxonomyCourse.getCourseId());
					course.put(SUBJECT_ID, taxonomyCourse.getSubjectId());
					course.put(NAME, taxonomyCourse.getName());
					courses.add(course);
					ContentTaxonomyCourseAssoc contentTaxonomyCourseAssoc = new ContentTaxonomyCourseAssoc();
					contentTaxonomyCourseAssoc.setContent(content);
					contentTaxonomyCourseAssoc.setTaxonomyCourse(taxonomyCourse);
					contentTaxonomyCourseAssocs.add(contentTaxonomyCourseAssoc);
				}
				this.getContentRepository().saveAll(contentTaxonomyCourseAssocs);
			}
		}
		return courses;
	}

	protected Map<String, Object> setUser(Object userUid, Object username) {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put(GOORU_UID, userUid);
		user.put(USER_NAME, username);
		user.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(getSettingService().getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(user.get(GOORU_UID)) + ".png");
		return user;
	}

	protected void updateMetaDataSummary(Long courseId, Long unitId, Long lessonId, String collectionType, String action) {
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
	protected void updateSummaryMeta(String collectionType, ContentMeta contentMeta, String action) {
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

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public ContentClassificationRepository getContentClassificationRepository() {
		return contentClassificationRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public IndexHandler getIndexHandler() {
		return indexHandler;
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public TaxonomyCourseRepository getTaxonomyCourseRepository() {
		return taxonomyCourseRepository;
	}

	public SettingService getSettingService() {
		return settingService;
	}

}
