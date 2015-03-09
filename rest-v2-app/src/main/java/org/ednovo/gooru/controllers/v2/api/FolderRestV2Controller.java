/////////////////////////////////////////////////////////////
//FolderRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.FolderService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/folder" })
public class FolderRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private FolderService folderService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createFolder(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().createCollection(this.buildCollectionFromInputParameters(data, user), Boolean.parseBoolean(json != null && getValue(ADD_TO_SHELF, json) != null ? getValue(ADD_TO_SHELF, json) : FALSE), getValue(RESOURCE_ID, json),
				getValue(PARENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = { RequestMethod.PUT })
	public ModelAndView updateFolder(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().updateCollection(this.buildUpadteCollectionFromInputParameters(data, user), collectionId, getValue(OWNER_UID, json), getValue(CREATOR_UID, json), hasUnrestrictedContentAccess(), getValue(RELATED_CONTENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_INCLUDE_FIELDS, ERROR_INCLUDE);
		if (getValue(TAXONOMY_SET, json) != null) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);
		}

		if (getValue(RELATED_CONTENT_ID, json) != null) {
			includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation.associateContent");
		}
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteFolder(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollection(collectionId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.POST)
	public ModelAndView createFolderItem(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(getValue(GOORU_OID, json), collectionId, this.buildCollectionItemFromInputParameters(data, user), user, CollectionType.COLLECTION.getCollectionType(), false);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateFolderItem(@PathVariable(value = ID) String collectionItemId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(data, user), collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ITEM_LIST })
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.GET)
	public ModelAndView getFolderItems(@PathVariable(value = ID) String collectionId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = ORDER_BY, required = false) String orderBy,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit, @RequestParam(value = SHARING, required = false, defaultValue = "private,public,anyonewithlink") String sharing, @RequestParam(value = COLLECTION_TYPE, required = false) String collectionType,
			@RequestParam(value = ITEM_LIMIT_FIELD, required = false, defaultValue = "4") Integer itemLimit, @RequestParam(value = FETCH_CHILDS, required = false, defaultValue = "false") boolean fetchChilds,
			@RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache, @RequestParam(value = EXCLUDE_TYPE, required = false) String excludeType, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getCollectionService().getFolderItemsWithCache(collectionId, limit, offset, sharing, collectionType, orderBy, itemLimit, fetchChilds, clearCache, user, excludeType));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollectionItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollectionItem(collectionItemId, user, true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/reorder/{sequence}" }, method = RequestMethod.PUT)
	public ModelAndView reorderCollectionItemSequence(@PathVariable(value = ID) String collectionItemId, @PathVariable(value = SEQUENCE) int newSequence, User user, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().reorderCollectionItem(collectionItemId, newSequence, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);

		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_MOVE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/move" }, method = RequestMethod.PUT)
	public ModelAndView moveCollectionToFolder(HttpServletRequest request, @RequestBody String data, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().moveCollectionToFolder(getValue(SOURCE_ID, json), json != null && getValue(TARGET_ID, json) != null ? getValue(TARGET_ID, json) : null, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_CREATE_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@RequestMapping(value = { "/{id}/workspace" }, method = RequestMethod.GET)
	public ModelAndView getMyWorkspace(@PathVariable(value = ID) String gooruUid, HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit,
			@RequestParam(value = SHARING, required = false, defaultValue = "private,public,anyonewithlink") String sharing, @RequestParam(value = COLLECTION_TYPE, required = false) String collectionType,
			@RequestParam(value = ITEM_LIMIT_FIELD, required = false, defaultValue = "4") Integer itemLimit, @RequestParam(value = FETCH_CHILDS, required = false, defaultValue = "false") boolean fetchChilds,
			@RequestParam(value = TOP_LEVEL_COLLECTION_TYPE, required = false) String topLevelCollectionType, @RequestParam(value = ORDER_BY, required = false) String orderBy, @RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache,
			@RequestParam(value = EXCLUDE_TYPE, required = false) String excludeType, HttpServletResponse resHttpServletResponse) {
		if (gooruUid.equalsIgnoreCase(MY)) {
			User user = (User) request.getAttribute(Constants.USER);
			gooruUid = user.getPartyUid();
		}
		Map<String, Object> content = null;
		final String cacheKey = V2_ORGANIZE_DATA + gooruUid + HYPHEN + offset + HYPHEN + limit + HYPHEN + sharing + HYPHEN + collectionType + HYPHEN + itemLimit + HYPHEN + fetchChilds + HYPHEN + topLevelCollectionType + HYPHEN + excludeType + HYPHEN + orderBy;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			content = new HashMap<String, Object>();
			content.put(SEARCH_RESULT, this.getCollectionService().getMyShelf(gooruUid, limit, offset, sharing, collectionType, itemLimit, fetchChilds, topLevelCollectionType, orderBy, excludeType));
			content.put(COUNT, this.getCollectionRepository().getMyShelfCount(gooruUid, sharing, collectionType, excludeType));
			data = serializeToJson(content, TOC_EXCLUDES, true, true);
			getRedisService().putValue(cacheKey, data, fetchChilds ? Constants.LIBRARY_CACHE_EXPIRY_TIME_IN_SEC : Constants.CACHE_EXPIRY_TIME_IN_SEC);
		}
		return toModelAndView(data);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@RequestMapping(value = { "" }, method = RequestMethod.GET)
	public ModelAndView getFolderList(HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit,
			@RequestParam(value = ID, required = false) String gooruOid, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = USER_NAME, required = false) String username, HttpServletResponse resHttpServletResponse) {
		return toJsonModelAndView(this.getCollectionService().getFolderList(limit, offset, gooruOid, title, username), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@RequestMapping(value = { "/{id}/toc" }, method = RequestMethod.GET)
	public ModelAndView getMyCollectionsToc(@PathVariable(value = ID) String gooruUid, HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "5") Integer limit,
			@RequestParam(value = SHARING, required = false, defaultValue = SHARINGS) String sharing, @RequestParam(value = COLLECTION_TYPE, required = false) String collectionType, @RequestParam(value = ORDER_BY, required = false) String orderBy,
			@RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache, @RequestParam(value = EXCLUDE_TYPE, required = false) String excludeType, HttpServletResponse resHttpServletResponse) {
		if (gooruUid.equalsIgnoreCase(MY)) {
			User user = (User) request.getAttribute(Constants.USER);
			gooruUid = user.getPartyUid();
		}
		return toModelAndView(this.getFolderService().getMyCollectionsToc(gooruUid, limit, offset, sharing, collectionType, orderBy, excludeType, clearCache));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@RequestMapping(value = { "/{id}/item/toc" }, method = RequestMethod.GET)
	public ModelAndView getFolderTocItems(@PathVariable(value = ID) String gooruOid, HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "5") Integer limit,
			@RequestParam(value = SHARING, required = false, defaultValue = SHARINGS) String sharing, @RequestParam(value = COLLECTION_TYPE, required = false) String collectionType, @RequestParam(value = ORDER_BY, required = false) String orderBy,
			@RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache, @RequestParam(value = EXCLUDE_TYPE, required = false) String excludeType, HttpServletResponse resHttpServletResponse) {
		return toModelAndView(this.getFolderService().getFolderTocItems(gooruOid, sharing, collectionType, orderBy, excludeType, clearCache));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getCollectionService().getCollection(collectionId, false, false, false, user, null, null, false, true), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, RESOURCE_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{cid}/item/{id}/next" }, method = RequestMethod.GET)
	public ModelAndView getNextCollectionItem(@PathVariable(value = CID) String collectionId, @RequestParam(value = EXCLUDE_TYPE, required = false) String excludeType, @PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndView(getFolderService().getNextCollectionItem(collectionItemId, excludeType), RESPONSE_FORMAT_JSON);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FOLDER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/node" }, method = RequestMethod.GET)
	public ModelAndView getFolderNode(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(getFolderService().getFolderNode(collectionId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, RESOURCE_INCLUDE_FIELDS);
	}

	private Collection buildCollectionFromInputParameters(String data, User user) {
		Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		collection.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getCollectionService().getContentType(ContentType.RESOURCE);
		collection.setContentType(contentType);
		collection.setCollectionType(ResourceType.Type.FOLDER.getType());
		collection.setResourceType(getCollectionService().getResourceType(ResourceType.Type.FOLDER.getType()));
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection.setSharing(Sharing.PRIVATE.getSharing());
		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setDistinguish(Short.valueOf("0"));
		collection.setRecordSource(NOT_ADDED);
		collection.setIsFeatured(0);
		collection.setLastUpdatedUserUid(user.getGooruUId());

		return collection;
	}

	private Collection buildUpadteCollectionFromInputParameters(String data, User user) {
		return JsonDeserializer.deserialize(data, Collection.class);
	}

	private CollectionItem buildCollectionItemFromInputParameters(String data, User user) {
		CollectionItem collectionItem = JsonDeserializer.deserialize(data, CollectionItem.class);
		return collectionItem;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public FolderService getFolderService() {
		return folderService;
	}

}
