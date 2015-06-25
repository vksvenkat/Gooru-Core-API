package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

public abstract class AbstractCollectionServiceImpl extends BaseServiceImpl implements AbstractCollectionService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private CustomTableRepository customTableRepository;

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

	@Override
	public List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset) {
		return getCollectionDao().getCollections(filters, limit, offset);
	}

	@Override
	public Map<String, Object> getCollection(String collectionId, String collectionType) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(GOORU_OID, collectionId);
		List<Map<String, Object>> collection = getCollectionDao().getCollections(filters, 1, 0);
		rejectIfNull(collection, GL0056, collectionType);
		return mergeMetaData(collection.get(0));
	}

	protected Map<String, Object> mergeMetaData(Map<String, Object> content) {
		Object data = content.get(META_DATA);
		Map<String, Object> metaData = null;
		if (data != null) {
			metaData = JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, Object>>() {
			});
			content.putAll(metaData);
		}
		content.remove(META_DATA);
		return content;
	}

	@Override
	public List<Map<String, Object>> getCollectionItem(String collectionId, String[] sharing, int limit, int offset) {
		return getCollectionDao().getCollectionItem(collectionId, sharing, limit, offset);
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

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

}
