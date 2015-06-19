/////////////////////////////////////////////////////////////
// FolderServiceImpl.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class FolderServiceImpl extends BaseServiceImpl implements FolderService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private CollectionService collectionService;

	@Override
	public SearchResults<Map<String, Object>> getMyCollectionsToc(String gooruUid, final Integer limit, final Integer offset, final String sharing, final String collectionType, final String orderBy, final String excludeType) {
		if (!BaseUtil.isUuid(gooruUid)) {
			final User user = this.getUserRepository().getUserByUserName(gooruUid, true);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		rejectIfNull(gooruUid, GL0056, 404, USER);
		final List<Map<String, Object>> folders = this.getCollectionRepository().getFolder(null, gooruUid, limit, offset, sharing, collectionType, true, orderBy, excludeType);
		if (folders != null && folders.size() > 0) {
			List<Map<String, Object>> folderList  =  new ArrayList<Map<String, Object>>();
			for (Map<String, Object> collection : folders) {
				final String typeName = String.valueOf(collection.get(TYPE));
				final String collectionGooruOid = String.valueOf(collection.get(GOORU_OID));
				final String data = String.valueOf(collection.get(DATA));
				if (data != null) {
					collection.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, String>>() {
					}));
				}
				if (typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL)) {
					collection.put(COLLECTION_ITEMS, getFolderTocItems(collectionGooruOid, limit, offset, sharing, collectionType, orderBy, ASC, excludeType));
				}
				folderList.add(collection);
			}
		}
		final SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		searchResult.setSearchResults(folders);
		searchResult.setTotalHitCount(this.getCollectionRepository().getFolderCount(null, gooruUid, sharing, collectionType, excludeType));
		return searchResult;
	}

	@Override
	public List<Map<String, Object>> getFolderTocItems(final String gooruOid, Integer limit, Integer offset, final String sharing, final String collectionType, final String orderBy, final String sortOrder, final String excludeType) {
		List<Map<String, Object>> collectionItems = this.getCollectionRepository().getFolder(gooruOid, null, limit, offset, sharing, collectionType, true, orderBy, excludeType);
		if (collectionItems == null || collectionItems.size() == 0) {
			collectionItems = this.getCollectionRepository().getCollectionItem(gooruOid, 4, 0, sharing, orderBy, collectionType, true, ASC, false, excludeType);
		}
		List<Map<String, Object>> collections = new ArrayList<Map<String, Object>>();
		if (collectionItems != null && collectionItems.size() > 0) {
			for (Map<String, Object> collection : collectionItems) {
				final String typeName = String.valueOf(collection.get(TYPE));
				final String collectionGooruOid = String.valueOf(collection.get(GOORU_OID));
				if (!(typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL) || typeName.equalsIgnoreCase(FOLDER))) {
					Object resourceFormatValue = collection.get(VALUE);
					if (resourceFormatValue != null) {
						final Map<String, Object> resourceFormat = new HashMap<String, Object>();
						resourceFormat.put(VALUE, resourceFormatValue);
						resourceFormat.put(DISPLAY_NAME, collection.get(DISPLAY_NAME));
						collection.put(RESOURCEFORMAT, resourceFormat);
					}
				}
				if (typeName.equalsIgnoreCase(COLLECTION) || typeName.equalsIgnoreCase(ASSESSMENT) || typeName.equalsIgnoreCase(ASSESSMENT_URL)) {
					collection.put(COLLECTION_ITEMS, getFolderTocItems(collectionGooruOid, limit, offset, sharing, collectionType, orderBy, ASC, excludeType));
				}
				final String data = String.valueOf(collection.get(DATA));
				if (data != null) {
					collection.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, String>>() {
					}));
				}
				collections.add(collection);
			}
		}
		return collections;

	}

	@Override
	public String getMyCollectionsToc(final String gooruUid, final Integer limit, final Integer offset, final String sharing, final String collectionType, final String orderBy, final String excludeType, final boolean clearCache) {
		final String cacheKey = V2_ORGANIZE_DATA + gooruUid + HYPHEN + offset + HYPHEN + limit + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + HYPHEN + orderBy + HYPHEN + excludeType + HYPHEN + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);

		}
		if (data == null) {
			data = SerializerUtil.serializeToJson(this.getMyCollectionsToc(gooruUid, limit, offset, sharing, collectionType, orderBy, excludeType), TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
	}

	@Override
	public String getFolderTocItems(final String gooruOid, Integer limit, Integer offset, final String sharing, final String collectionType, final String orderBy, final String excludeType, final boolean clearCache) {
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		rejectIfNull(collection, GL0056, 404, FOLDER);
		final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + HYPHEN + gooruOid + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + orderBy + HYPHEN + excludeType + HYPHEN + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(TITLE, collection.getTitle());
			item.put(IDEAS, collection.getIdeas());
			item.put(QUESTIONS, collection.getQuestions());
			item.put(PERFORMANCE_TASKS, collection.getPerformanceTasks());
			item.put(DESCRIPTION, collection.getGoals() != null ? collection.getGoals() : collection.getDescription());
			item.put(COLLECTION_TYPE, collection.getCollectionType());
			item.put(URL, collection.getUrl());
			item.put(COLLECTION_ITEMS, this.getFolderTocItems(gooruOid, limit, offset, sharing, collectionType, orderBy, collection.getContentType().getName().equalsIgnoreCase(SCOLLECTION) ? ASC : DESC, excludeType));
			data = SerializerUtil.serializeToJson(item, TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
	}

	@Override
	public List<Map<String, String>> getFolderNode(final String collectionId) {
		final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		rejectIfNull(collection, GL0056, 404, COLLECTION);
		return this.getCollectionService().getParentCollection(collectionId, null, true);
	}

	@Override
	public Map<String, Object> getNextCollectionItem(final String collectionItemId, final String excludeType, final String sharing, boolean excludeCollaboratorCollection) {
		final CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, 404, COLLECTION_ITEM);
		return getCollection(collectionItem.getCollection().getGooruOid(), collectionItem.getItemSequence(), excludeType, sharing, excludeCollaboratorCollection);
	}

	private Map<String, Object> getCollection(final String gooruOid, final Integer sequence, final String excludeType, final String sharing, boolean excludeCollaboratorCollection) {
		Map<String, Object> nextCollection = null;
		final CollectionItem nextCollectionItem = this.getCollectionRepository().getNextCollectionItemResource(gooruOid, sequence, excludeType, sharing, excludeCollaboratorCollection);
		if (nextCollection == null && nextCollectionItem != null && !nextCollectionItem.getResource().getContentType().getName().equalsIgnoreCase(FOLDER)) {
			final Collection collection = this.getCollectionRepository().getCollectionByGooruOid(nextCollectionItem.getResource().getGooruOid(), null);
			nextCollection = new HashMap<String, Object>();
			nextCollection.put(COLLECTION_ITEM_ID, nextCollectionItem.getCollectionItemId());
			nextCollection.put(TITLE, collection.getTitle());
			nextCollection.put(GOORU_OID, nextCollectionItem.getResource().getGooruOid());
			nextCollection.put(THUMBNAILS, collection.getThumbnails());
			nextCollection.put(COLLECTION_TYPE, collection.getCollectionType());
			final Long itemCount = this.getCollectionRepository().getCollectionItemCount(nextCollectionItem.getResource().getGooruOid(), null, null, null);
			final Long questionCount = this.getCollectionRepository().getCollectionItemCount(nextCollectionItem.getResource().getGooruOid(), null, ResourceType.Type.ASSESSMENT_QUESTION.getType(), null);
			nextCollection.put(RESOURCE_COUNT, itemCount - questionCount);
			nextCollection.put(QUESTION_COUNT, questionCount);
			return nextCollection;

		} else if (nextCollection == null && nextCollectionItem != null && nextCollectionItem.getResource().getContentType().getName().equalsIgnoreCase(FOLDER)) {
			final Long itemCount = this.getCollectionRepository().getCollectionItemCount(nextCollectionItem.getResource().getGooruOid(), null, null, null);
			return getCollection(nextCollectionItem.getResource().getGooruOid(), ((Number) (itemCount + 1)).intValue(), excludeType, sharing, excludeCollaboratorCollection);
		} else if (nextCollection == null && nextCollectionItem == null) {
			final CollectionItem parentCollectionItem = this.getCollectionRepository().getCollectionItemByResource(gooruOid);
			if (parentCollectionItem != null) {
				return getCollection(parentCollectionItem.getCollection().getGooruOid(), parentCollectionItem.getItemSequence(), excludeType, sharing, excludeCollaboratorCollection);
			}
		}

		return null;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}
}