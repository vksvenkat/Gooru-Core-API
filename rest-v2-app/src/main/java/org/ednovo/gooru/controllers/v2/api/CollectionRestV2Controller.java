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
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
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
	
	@Autowired
	private IndexProcessor indexProcessor;
	
	@Autowired 
	private IndexHandler indexHandler;
	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createCollection(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final ActionResponseDTO<Collection> responseDTO = getCollectionService().createCollection(this.buildCollectionFromInputParameters(getValue(COLLECTION, json), user, request), Boolean.parseBoolean(json != null && getValue(ADD_TO_SHELF, json) != null ? getValue(ADD_TO_SHELF, json) : FALSE),
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
	public ModelAndView updateCollection(@PathVariable(value = ID) final String collectionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final ActionResponseDTO<Collection> responseDTO = getCollectionService().updateCollection(this.buildCopyCollectionFromInputParameters(getValue(COLLECTION, json)), collectionId, getValue(OWNER_UID, json), getValue(CREATOR_UID, json), hasUnrestrictedContentAccess(),
				getValue(RELATED_CONTENT_ID, json), user, data);
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
	public ModelAndView getCollection(@PathVariable(value = ID) final String collectionId, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = TRUE) final boolean includeCollectionItem,
			@RequestParam(value = INLCLUDE_META_INFO, required = false, defaultValue = FALSE) boolean includeMetaInfo, @RequestParam(value = INCLUDE_COLLABORATOR, required = false, defaultValue = FALSE) boolean includeCollaborator,
			@RequestParam(value = IS_GAT, required = false, defaultValue = FALSE) boolean isGat, @RequestParam(value = CLEAR_CACHE, required = false, defaultValue = FALSE) boolean clearCache,
			@RequestParam(value = INCLUDE_RELATED_CONTENT, required = false, defaultValue = FALSE) boolean includeRelatedContent, @RequestParam(value = MERGE, required = false) String merge, @RequestParam(value = REQ_CONTEXT, required = false, defaultValue = "edit-play") String requestContext,
			@RequestParam(value = ROOT_NODE_ID, required = false) String rootNodeId, @RequestParam(value = INCLUDE_CONTENT_PROVIDER, required = false, defaultValue = TRUE) boolean includeContentProvider, @RequestParam(value = INCLUDE_CUSTOM_FIELDS, required = false, defaultValue = TRUE) boolean includeCustomFields, HttpServletRequest request, HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		String includes[] = null;
		if (requestContext != null && requestContext.equalsIgnoreCase("library")) {
			includes = (String[]) ArrayUtils.addAll(LIBRARY_RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
			includes = (String[]) ArrayUtils.addAll(includes, LIBRARY_COLLECTION_INCLUDE_FIELDS);
			final String cacheKey = COLLECTION_DATA + requestContext + "-" + collectionId + "-" + rootNodeId;
			String data = null;
			data = getRedisService().getValue(cacheKey);
			if (data == null) {
				data = serialize(this.getCollectionService().getCollection(collectionId, new HashMap<String, Object>(), rootNodeId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, includes);
				getRedisService().putValue(cacheKey, data, Constants.CACHE_EXPIRY_TIME_IN_SEC);
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
			return toModelAndViewWithIoFilter(getCollectionService().getCollection(collectionId, includeMetaInfo, includeCollaborator, includeRelatedContent, user, merge, rootNodeId, isGat, true, includeContentProvider, includeCustomFields), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = ID) final String collectionId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollection(collectionId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.POST)
	public ModelAndView createCollectionItem(@PathVariable(value = ID) final String collectionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(getValue(RESOURCE_ID, json), collectionId, this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), user, CollectionType.COLLECTION.getCollectionType(), false);
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
	public ModelAndView updateCollectionItem(@PathVariable(value = ID) final String collectionItemId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), collectionItemId, user, data);
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
	public ModelAndView getCollectionItem(@PathVariable(value = ID) final String collectionItemId, @RequestParam(value = INCLUDE_ADDITIONAL_INFO, required = false, defaultValue = FALSE) final boolean includeAdditionalInfo, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, includeAdditionalInfo, user, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.GET)
	public ModelAndView getCollectionItems(@PathVariable(value = ID) final String collectionId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit,
			@RequestParam(value = ORDER_BY, defaultValue = DESC, required = false) final String orderBy, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final List<CollectionItem> collectionItems = this.getCollectionService().getCollectionItems(collectionId, offset, limit, orderBy, "collection");
		String includesDefault[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_ITEM_TAGS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_WORKSPACE);
		String includes[] = (String[]) ArrayUtils.addAll(includesDefault, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(getCollectionService().setCollectionItemMetaInfo(collectionItems, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollectionItem(@PathVariable(value = ID) final String collectionItemId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollectionItem(collectionItemId, user, true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/reorder/{sequence}" }, method = RequestMethod.PUT)
	public ModelAndView reorderCollectionItemSequence(@PathVariable(value = ID) final String collectionItemId, @PathVariable(value = SEQUENCE) int newSequence, final User user , final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().reorderCollectionItem(collectionItemId, newSequence, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_COPY })
	@RequestMapping(value = { "/{id}/copy" }, method = RequestMethod.PUT)
	public ModelAndView copyCollection(@PathVariable(value = ID) final String collectionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		final JSONObject json = requestData(data);
		if (getValue(SKIP_COLLECTION_ITEM, json) != null ? !Boolean.parseBoolean(getValue(SKIP_COLLECTION_ITEM, json)) : true) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		}
		final User user = (User) request.getAttribute(Constants.USER);
		final Collection collection = getCollectionService().copyCollection(collectionId, this.buildCopyCollectionFromInputParameters(getValue(COLLECTION, json)), json != null && getValue(ADD_TO_SHELF, json) != null ? Boolean.parseBoolean(getValue(ADD_TO_SHELF, json)) : false,
				json != null && getValue(PARENT_ID, json) != null ? getValue(PARENT_ID, json) : null, user);
		indexHandler.setReIndexRequest(collection.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);				
		return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/resource/moreinfo/{id}" }, method = RequestMethod.GET)
	public ModelAndView getResourceMoreInfo(@PathVariable(value = ID) final String resourceId, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionService().getResourceMoreInfo(resourceId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_COPY })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/copy/{cid}" }, method = RequestMethod.PUT)
	public ModelAndView copyCollectionItem(@PathVariable(value = ID) final String collectionItemId, @PathVariable(value = CID) final String collectionId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final CollectionItem collectionItem = getCollectionService().copyCollectionItem(collectionItemId, collectionId);
		return toModelAndViewWithIoFilter(collectionItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_MOVE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/move" }, method = RequestMethod.PUT)
	public ModelAndView moveCollectionToFolder(final HttpServletRequest request, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);

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
	public ModelAndView createResourceWithCollectionItem(final HttpServletRequest request, @PathVariable(ID) final String collectionId, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		final User user = (User) request.getAttribute(Constants.USER);
		System.out.println(collectionId);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createResourceWithCollectionItem(collectionId, this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), getValue(START, json), getValue(STOP, json),
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
	public ModelAndView updateResourceWithCollectionItem(final HttpServletRequest request, @PathVariable(ID) final String collectionItemId, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateResourceWithCollectionItem(collectionItemId, this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), getValue(RESOURCE_TAGS, json) == null ? null : buildResourceTags(getValue(RESOURCE_TAGS, json)),
				user, data);
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
	public ModelAndView createQuestionWithCollectionItem(@PathVariable(value = ID) final String collectionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createQuestionWithCollectionItem(collectionId, data, user, getValue(MEDIA_FILE_NAME, json), getCollectionType(request));
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
	public ModelAndView updateQuestionWithCollectionItem(final HttpServletRequest request, @PathVariable(ID) final String collectionItemId, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateQuestionWithCollectionItem(collectionItemId, data, parseJSONArray(getValue(DELETE_ASSETS, json)), user, getValue(MEDIA_FILE_NAME, json));
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
	public ModelAndView updateQuestionWithCollectionResourceItem(final HttpServletRequest request, @PathVariable(value = CID) final String collectionId, @PathVariable(ID) final String resourceId, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateQuestionWithCollectionItem(collectionId, resourceId, data, parseJSONArray(getValue(DELETE_ASSETS, json)), user, getValue(MEDIA_FILE_NAME, json));
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
	public void deleteQuestionWithCollectionItem(@PathVariable(value = CID) final String collectionId, @PathVariable(value = ID) final String resourceId, final HttpServletRequest request, final HttpServletResponse response) {
		this.getCollectionService().deleteQuestionWithCollectionItem(collectionId, resourceId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/workspace" }, method = RequestMethod.GET)
	public ModelAndView getMyWorkspace(@PathVariable(value = ID) final String partyUid, final HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit,
			@RequestParam(value = FILTER_NAME, required = false, defaultValue = ALL) final String filterName, @RequestParam(value = ORDER_BY, required = false, defaultValue = "desc") final String orderBy, @RequestParam(value = SHARING, required = false) final String sharing, final HttpServletResponse resHttpServletResponse) {
		final User user = (User) request.getAttribute(Constants.USER);
		final Map<String, String> filters = new HashMap<String, String>();
		filters.put(OFFSET_FIELD, offset + "");
		filters.put(LIMIT_FIELD, limit + "");
		filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
		filters.put(FILTER_NAME, filterName);
		if (sharing != null) {
			filters.put(SHARING, sharing);
		}
		filters.put(ORDER_BY, orderBy);
		final List<CollectionItem> collectionItems = getCollectionService().setCollectionItemMetaInfo(getCollectionService().getMyCollectionItems(partyUid, filters, user), null);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_WORKSPACE);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		return toModelAndView(serialize(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/isAdded" }, method = RequestMethod.GET)
	public ModelAndView isAlreadyCopied(final HttpServletRequest request, @PathVariable(value = ID) final String gooruOid, final HttpServletResponse resHttpServletResponse) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		return jsonmodel.addObject(MODEL, this.getCollectionService().resourceCopiedFrom(gooruOid, user.getGooruUId()));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/parents" }, method = RequestMethod.GET)
	public ModelAndView getCollectionParent(final HttpServletRequest request, @PathVariable(value = ID) final String gooruOid, final HttpServletResponse resHttpServletResponse) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		return toJsonModelAndView(this.getCollectionService().getParentCollection(gooruOid, user.getPartyUid(), true), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/standards" }, method = RequestMethod.GET)
	public ModelAndView getCollectionStandards(final HttpServletRequest request, @RequestParam(value = ID, required = false) final Integer codeId, @RequestParam(value = QUERY, required = false) final String query, final HttpServletResponse resHttpServletResponse,
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getCollectionService().getCollectionStandards(codeId, query, limit, offset, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_STANDARDS_INCLUDES);
	}

	private Collection buildCollectionFromInputParameters(final String data, final User user, final HttpServletRequest request) {
		final Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		collection.setGooruOid(UUID.randomUUID().toString());
		final ContentType contentType = getCollectionService().getContentType(ContentType.RESOURCE);
		collection.setContentType(contentType);
		ResourceType resourceType = getCollectionService().getResourceType(ResourceType.Type.SCOLLECTION.getType());
		if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
			resourceType = getCollectionService().getResourceType(ResourceType.Type.FOLDER.getType());
		}

		collection.setResourceType(resourceType);
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));

		collection
				.setSharing(collection.getSharing() != null && (collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? collection
						.getSharing() : Sharing.ANYONEWITHLINK.getSharing());

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

	private Resource buildResourceFromInputParameters(final String data, final User user) {
		return JsonDeserializer.deserialize(data, Resource.class);
	}

	private Collection buildCopyCollectionFromInputParameters(final String data) {

		return JsonDeserializer.deserialize(data, Collection.class);
	}

	private CollectionItem buildCollectionItemFromInputParameters(final String data) {

		return JsonDeserializer.deserialize(data, CollectionItem.class);
	}

	private List<Integer> parseJSONArray(final String arrayData) throws Exception {

		final List<Integer> list = new ArrayList<Integer>();
		if (arrayData != null && arrayData.length() > 2) {
			final JSONArray jsonArray = new JSONArray(arrayData);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add((Integer) jsonArray.get(i));
			}
		}
		return list;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/list/status" }, method = RequestMethod.GET)
	public ModelAndView getCollectionListForPublish(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit,
			@RequestParam(value = PUBLISH_STATUS, required = false) final String publishStatus, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getCollectionService().getCollections(offset, limit, user, publishStatus), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/publish/collections" }, method = { RequestMethod.PUT })
	public ModelAndView updateCollectionForPublish(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final List<Map<String, String>> collection = buildUpdatesPublishStatusFromInputParameters(data);
		return toModelAndViewWithIoFilter(getCollectionService().updateCollectionForPublish(collection, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/reject/collections" }, method = { RequestMethod.PUT })
	public ModelAndView updateCollectionForRejection(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final List<Map<String, String>> collection = buildUpdatesPublishStatusFromInputParameters(data);
		return toModelAndViewWithIoFilter(getCollectionService().updateCollectionForReject(collection, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/bulk")
	public void deleteBulkCollections(@RequestParam final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		this.getCollectionService().deleteBulkCollections(JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}));
	}

	private List<Map<String, String>> buildUpdatesPublishStatusFromInputParameters(final String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<Map<String, String>>>() {
		});
	}

	private List<String> buildResourceTags(final String data) {
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

	private String getCollectionType(final HttpServletRequest request) {
		String type = null;
		if (request.getRequestURL() != null && request.getRequestURL().toString().contains(ASSESSMENT)) {
			type = ASSESSMENT;
		} else if (request.getRequestURL() != null && request.getRequestURL().toString().contains(COLLECTION)) {
			type = COLLECTION;
		}
		return type;
	}

}
