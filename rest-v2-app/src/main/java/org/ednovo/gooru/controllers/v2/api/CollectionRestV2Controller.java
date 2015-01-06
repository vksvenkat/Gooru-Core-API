/////////////////////////////////////////////////////////////
//CollectionRestV2Controller.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jettison.json.JSONArray;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping(value = { "/v2/collection/", "/v2/assessment" })
public class CollectionRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private ConversionService conversionService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createCollection(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().createCollection(this.buildCollectionFromInputParameters(getValue(COLLECTION, json), user, request), Boolean.parseBoolean(json != null && getValue(ADD_TO_SHELF, json) != null ? getValue(ADD_TO_SHELF, json) : FALSE),
				getValue(RESOURCE_ID, json), getValue(PARENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = { RequestMethod.PUT })
	public ModelAndView updateCollection(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().updateCollection(this.buildCopyCollectionFromInputParameters(getValue(COLLECTION, json)), collectionId, getValue(OWNER_UID, json), getValue(CREATOR_UID, json), hasUnrestrictedContentAccess(),
				getValue(RELATED_CONTENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_INCLUDE_FIELDS, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);

		if (getValue(RELATED_CONTENT_ID, json) != null) {
			includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation.associateContent");
		}
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) String collectionId, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = TRUE) boolean includeCollectionItem,
			@RequestParam(value = INLCLUDE_META_INFO, required = false, defaultValue = FALSE) boolean includeMetaInfo, @RequestParam(value = INCLUDE_COLLABORATOR, required = false, defaultValue = FALSE) boolean includeCollaborator,
			@RequestParam(value = IS_GAT, required = false, defaultValue = FALSE) boolean isGat, @RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache,
			@RequestParam(value = INCLUDE_RELATED_CONTENT, required = false, defaultValue = FALSE) boolean includeRelatedContent, @RequestParam(value = MERGE, required = false) String merge, @RequestParam(value = REQ_CONTEXT, required = false, defaultValue = "edit-play") String requestContext,
			@RequestParam(value = ROOT_NODE_ID, required = false) String rootNodeId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String collection = null;
		String includes[] = null;
		if (requestContext != null && requestContext.equalsIgnoreCase("library")) {
			includes = (String[]) ArrayUtils.addAll(LIBRARY_RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
			includes = (String[]) ArrayUtils.addAll(includes, LIBRARY_COLLECTION_INCLUDE_FIELDS);
			final String cacheKey = COLLECTION_DATA + requestContext + "-" + collectionId + "-" + rootNodeId;
			String data = null;
			data = getRedisService().getValue(cacheKey);
			if (data == null) {
				data = serialize(this.getCollectionService().getCollection(collectionId, new HashMap<String, Object>(), rootNodeId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, includes);
				getRedisService().putValue(cacheKey, data, 86400);
			}
			return toModelAndView(data);
		} else {
			includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
			if (includeCollectionItem) {
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
			}
			if (includeMetaInfo) {
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
			}
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);

			if (includeRelatedContent) {
				includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation");
			}
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_TAGS);
			return toModelAndViewWithIoFilter(getCollectionService().getCollection(collectionId, includeMetaInfo, includeCollaborator, includeRelatedContent, user, merge, rootNodeId, isGat, false), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollection(collectionId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.POST)
	public ModelAndView createCollectionItem(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(getValue(RESOURCE_ID, json), collectionId, this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), user, CollectionType.COLLECTION.getCollectionType(), false);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestParam(value = INCLUDE_ADDITIONAL_INFO, required = false, defaultValue = FALSE) boolean includeAdditionalInfo, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, includeAdditionalInfo, user, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.GET)
	public ModelAndView getCollectionItems(@PathVariable(value = ID) String collectionId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = ORDER_BY, defaultValue = DESC, required = false) String orderBy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CollectionItem> collectionItems = this.getCollectionService().getCollectionItems(collectionId, offset, limit, orderBy, "collection");
		String includesDefault[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_ITEM_TAGS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_WORKSPACE);
		String includes[] = (String[]) ArrayUtils.addAll(includesDefault, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(getCollectionService().setCollectionItemMetaInfo(collectionItems, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollectionItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollectionItem(collectionItemId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/reorder/{sequence}" }, method = RequestMethod.PUT)
	public ModelAndView reorderCollectionItemSequence(@PathVariable(value = ID) String collectionItemId, @PathVariable(value = SEQUENCE) int newSequence, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().reorderCollectionItem(collectionItemId, newSequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_COPY })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/copy" }, method = RequestMethod.PUT)
	public ModelAndView copyCollection(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		JSONObject json = requestData(data);
		if (getValue(SKIP_COLLECTION_ITEM, json) != null ? !Boolean.parseBoolean(getValue(SKIP_COLLECTION_ITEM, json)) : true) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		}
		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = getCollectionService().copyCollection(collectionId, this.buildCopyCollectionFromInputParameters(getValue(COLLECTION, json)), json != null && getValue(ADD_TO_SHELF, json) != null ? Boolean.parseBoolean(getValue(ADD_TO_SHELF, json)) : false,
				json != null && getValue(PARENT_ID, json) != null ? getValue(PARENT_ID, json) : null, user);

		return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/resource/moreinfo/{id}" }, method = RequestMethod.GET)
	public ModelAndView getResourceMoreInfo(@PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionService().getResourceMoreInfo(resourceId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_COPY })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/copy/{cid}" }, method = RequestMethod.PUT)
	public ModelAndView copyCollectionItem(@PathVariable(value = ID) String collectionItemId, @PathVariable(value = CID) String collectionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		CollectionItem collectionItem = getCollectionService().copyCollectionItem(collectionItemId, collectionId);
		return toModelAndViewWithIoFilter(collectionItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_MOVE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/move" }, method = RequestMethod.PUT)
	public ModelAndView moveCollectionToFolder(HttpServletRequest request, @RequestBody String data, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().moveCollectionToFolder(getValue(SOURCE_ID, json), json != null && getValue(TARGET_ID, json) != null ? getValue(TARGET_ID, json) : null, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serializeToJson(responseDTO.getModelData(), includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/resource" }, method = RequestMethod.POST)
	public ModelAndView createResourceWithCollectionItem(HttpServletRequest request, @PathVariable(ID) String collectionId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createResourceWithCollectionItem(collectionId, this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), getValue(START, json), getValue(STOP, json),
				getValue(RESOURCE_TAGS, json) == null ? null : buildResourceTags(getValue(RESOURCE_TAGS, json)), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/resource/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateResourceWithCollectionItem(HttpServletRequest request, @PathVariable(ID) String collectionItemId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateResourceWithCollectionItem(collectionItemId, this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), getValue(RESOURCE_TAGS, json) == null ? null : buildResourceTags(getValue(RESOURCE_TAGS, json)),
				user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/question" }, method = RequestMethod.POST)
	public ModelAndView createQuestionWithCollectionItem(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createQuestionWithCollectionItem(collectionId, data, user, getValue(MEDIA_FILE_NAME, json), getCollectionType(request));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/question/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateQuestionWithCollectionItem(HttpServletRequest request, @PathVariable(ID) String collectionItemId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateQuestionWithCollectionItem(collectionItemId, data, parseJSONArray(getValue(DELETE_ASSETS, json)), user, getValue(MEDIA_FILE_NAME, json));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_CREATE_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "{cid}/question/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateQuestionWithCollectionResourceItem(HttpServletRequest request, @PathVariable(value = CID) String collectionId, @PathVariable(ID) String resourceId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateQuestionWithCollectionItem(collectionId, resourceId, data, parseJSONArray(getValue(DELETE_ASSETS, json)), user, getValue(MEDIA_FILE_NAME, json));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_CREATE_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "{cid}/question/{id}" }, method = RequestMethod.DELETE)
	public void deleteQuestionWithCollectionItem(@PathVariable(value = CID) String collectionId, @PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) {
		this.getCollectionService().deleteQuestionWithCollectionItem(collectionId, resourceId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/workspace" }, method = RequestMethod.GET)
	public ModelAndView getMyWorkspace(@PathVariable(value = ID) String partyUid, HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = FILTER_NAME, required = false, defaultValue = ALL) String filterName, @RequestParam(value = ORDER_BY, required = false, defaultValue = "desc") String orderBy, @RequestParam(value = SHARING, required = false) String sharing, HttpServletResponse resHttpServletResponse) {
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(OFFSET_FIELD, offset + "");
		filters.put(LIMIT_FIELD, limit + "");
		filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
		filters.put(FILTER_NAME, filterName);
		if (sharing != null) {
			filters.put(SHARING, sharing);
		}
		filters.put(ORDER_BY, orderBy);
		List<CollectionItem> collectionItems = getCollectionService().setCollectionItemMetaInfo(getCollectionService().getMyCollectionItems(partyUid, filters, user), null);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_WORKSPACE);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		return toModelAndView(serialize(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/isAdded" }, method = RequestMethod.GET)
	public ModelAndView isAlreadyCopied(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		return jsonmodel.addObject(MODEL, this.getCollectionService().resourceCopiedFrom(gooruOid, user.getGooruUId()));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/parents" }, method = RequestMethod.GET)
	public ModelAndView getCollectionParent(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toJsonModelAndView(this.getCollectionService().getParentCollection(gooruOid, user.getPartyUid(), true), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/standards" }, method = RequestMethod.GET)
	public ModelAndView getCollectionStandards(HttpServletRequest request, @RequestParam(value = ID, required = false) Integer codeId, @RequestParam(value = QUERY, required = false) String query, HttpServletResponse resHttpServletResponse,
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getCollectionService().getCollectionStandards(codeId, query, limit, offset, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_STANDARDS_INCLUDES);
	}

	private Collection buildCollectionFromInputParameters(String data, User user, HttpServletRequest request) {
		Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		collection.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getCollectionService().getContentType(ContentType.RESOURCE);
		collection.setContentType(contentType);
		ResourceType resourceType = null;
		if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
			resourceType = getCollectionService().getResourceType(ResourceType.Type.FOLDER.getType());
		} else if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
			resourceType = getCollectionService().getResourceType(ResourceType.Type.SCOLLECTION.getType());
		}else if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.ASSESSMENT.getType())) {
			resourceType = getCollectionService().getResourceType(ResourceType.Type.SCOLLECTION.getType());
		}
		collection.setResourceType(resourceType);
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection.setSharing(collection.getSharing() != null && (collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? collection.getSharing() : Sharing.ANYONEWITHLINK.getSharing());
		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setDistinguish(Short.valueOf("0"));
		collection.setRecordSource(NOT_ADDED);
		collection.setIsFeatured(0);
		collection.setLastUpdatedUserUid(user.getGooruUId());
		if (collection.getCollectionType() == null) {
			collection.setCollectionType(getCollectionType(request));
		}

		return collection;
	}

	private Resource buildResourceFromInputParameters(String data, User user) {
		return JsonDeserializer.deserialize(data, Resource.class);
	}

	private Collection buildCopyCollectionFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, Collection.class);
	}

	private CollectionItem buildCollectionItemFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, CollectionItem.class);
	}

	private List<Integer> parseJSONArray(String arrayData) throws Exception {

		List<Integer> list = new ArrayList<Integer>();
		if (arrayData != null && arrayData.length() > 2) {
			JSONArray jsonArray = new JSONArray(arrayData);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add((Integer) jsonArray.get(i));
			}
		}
		return list;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/list/status" }, method = RequestMethod.GET)
	public ModelAndView getCollectionListForPublish(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = PUBLISH_STATUS, required = false) String publishStatus, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(COLLECTION_ITEM_INCLUDE_FILEDS, COLLECTION_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(getCollectionService().getCollections(offset, limit, user, publishStatus), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/publish/collections" }, method = { RequestMethod.PUT })
	public ModelAndView updateCollectionForPublish(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<Map<String, String>> collection = buildUpdatesPublishStatusFromInputParameters(data);
		return toModelAndViewWithIoFilter(getCollectionService().updateCollectionForPublish(collection, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/reject/collections" }, method = { RequestMethod.PUT })
	public ModelAndView updateCollectionForRejection(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<Map<String, String>> collection = buildUpdatesPublishStatusFromInputParameters(data);
		return toModelAndViewWithIoFilter(getCollectionService().updateCollectionForReject(collection, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/bulk")
	public void deleteBulkCollections(@RequestParam String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getCollectionService().deleteBulkCollections(JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}));
	}

	private List<Map<String, String>> buildUpdatesPublishStatusFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<Map<String, String>>>() {
		});
	}

	private List<String> buildResourceTags(String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		});
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	private String getCollectionType(HttpServletRequest request) {
		String type = null;
		if (request.getRequestURL() != null && request.getRequestURL().toString().contains(ASSESSMENT)) {
			type = ASSESSMENT;
		} else if (request.getRequestURL() != null && request.getRequestURL().toString().contains(COLLECTION)) {
			type = COLLECTION;
		}
		return type;
	}

}
