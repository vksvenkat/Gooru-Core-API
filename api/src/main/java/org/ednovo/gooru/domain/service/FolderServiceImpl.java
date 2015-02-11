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
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl extends BaseServiceImpl implements FolderService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RedisService redisService;

	public final static String TOC = "toc";

	@Override
	public SearchResults<Map<String, Object>> getMyCollectionsToc(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy) {
		if (!BaseUtil.isUuid(gooruUid)) {
			User user = this.getUserRepository().getUserByUserName(gooruUid, true);
			gooruUid = user != null ? user.getPartyUid() : null;
		}
		List<Object[]> result = this.getCollectionRepository().getMyFolder(gooruUid, limit, offset, sharing, collectionType, true, orderBy);
		List<Map<String, Object>> folders = new ArrayList<Map<String, Object>>();
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put(TITLE, object[0]);
				collection.put(GOORU_OID, object[1]);
				collection.put(TYPE, object[2]);
				collection.put(IDEAS, object[12]);
				collection.put(QUESTIONS, object[13]);
				collection.put(PERFORMANCE_TASKS, object[14]);
				collection.put(COLLECTION_TYPE, object[15]);
				collection.put(COLLECTION_ITEM_ID, object[6]);
				if (object[2] != null && object[2].toString().equalsIgnoreCase(SCOLLECTION)) {
					collection.put(COLLECTION_ITEMS, getFolderTocItems(String.valueOf(object[1]), sharing, collectionType, orderBy, ASC));
				}
				folders.add(collection);
			}
		}
		SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		searchResult.setSearchResults(folders);
		searchResult.setTotalHitCount(this.getCollectionRepository().getMyShelfCount(gooruUid, sharing, collectionType));
		return searchResult;
	}

	@Override
	public List<Map<String, Object>> getFolderTocItems(String gooruOid, String sharing, String collectionType, String orderBy, String sortOrder) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getCollectionRepository().getCollectionItem(gooruOid, null, null, sharing, orderBy, collectionType, true, sortOrder, true);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(TITLE, object[0]);
				item.put(GOORU_OID, object[1]);
				item.put(TYPE, object[2]);
				if (object[2] != null && (!object[2].toString().equalsIgnoreCase(SCOLLECTION) && !object[2].toString().equalsIgnoreCase(FOLDER))) {
					if (object[5] != null) {
						Map<String, Object> resourceFormat = new HashMap<String, Object>();
						resourceFormat.put(VALUE, object[5]);
						resourceFormat.put(DISPLAY_NAME, object[6]);
						item.put(RESOURCEFORMAT, resourceFormat);
					}
				}
				if (object[2] != null && object[2].toString().equalsIgnoreCase(SCOLLECTION)) {
					item.put(COLLECTION_ITEMS, getFolderTocItems(String.valueOf(object[1]), sharing, collectionType, orderBy, ASC));
				}
				item.put(IDEAS, object[12]);
				item.put(QUESTIONS, object[13]);
				item.put(PERFORMANCE_TASKS, object[14]);
				item.put(COLLECTION_TYPE, object[18]);
				item.put(COLLECTION_ITEM_ID, object[8]);
				items.add(item);
			}
		}
		return items;

	}

	@Override
	public String getMyCollectionsToc(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, boolean clearCache) {
		final String cacheKey = V2_ORGANIZE_DATA + gooruUid + "-" + offset + "-" + limit + "-" + sharing + "-" + collectionType + "-" + "-" + orderBy + "-" + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);

		}
		if (data == null) {
			data = SerializerUtil.serializeToJson(this.getMyCollectionsToc(gooruUid, limit, offset, sharing, collectionType, orderBy), true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
	}

	@Override
	public String getFolderTocItems(String gooruOid, String sharing, String collectionType, String orderBy, boolean clearCache) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		rejectIfNull(collection, GL0056, 404, FOLDER);
		final String cacheKey = V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "-" + gooruOid + "-" + sharing + "-" + collectionType + "-" + orderBy + "-" + TOC;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			data = SerializerUtil.serializeToJson(this.getFolderTocItems(gooruOid, sharing, collectionType, orderBy, collection.getResourceType().getName().equalsIgnoreCase(SCOLLECTION) ? ASC : DESC), TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, 86400);
		}
		return data;
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

}