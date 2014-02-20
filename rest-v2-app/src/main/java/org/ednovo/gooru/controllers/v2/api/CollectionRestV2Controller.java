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
import java.util.HashMap;
import java.util.List;
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
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
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
@RequestMapping(value = { "/v2/collection" })
public class CollectionRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private RedisService redisService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createCollection(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().createCollection(this.buildCollectionFromInputParameters(getValue(COLLECTION, json), user), Boolean.parseBoolean(json != null && getValue(ADD_TO_SHELF, json) != null ? getValue(ADD_TO_SHELF, json) : FALSE),
				getValue(RESOURCE_ID, json), getValue(PARENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-create");
			SessionContextSupport.putLogParameter(GOORU_OID, responseDTO.getModel().getGooruOid());
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
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
		ActionResponseDTO<Collection> responseDTO = getCollectionService().updateCollection(this.buildCollectionFromInputParameters(getValue(COLLECTION, json), user), collectionId, getValue(OWNER_UID, json), getValue(CREATOR_UID, json), hasUnrestrictedContentAccess(),
				getValue(RELATED_CONTENT_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-update");
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) String collectionId, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = TRUE) boolean includeCollectionItem,
			@RequestParam(value = INLCLUDE_META_INFO, required = false, defaultValue = FALSE) boolean includeMetaInfo, @RequestParam(value = INCLUDE_COLLABORATOR, required = false, defaultValue = FALSE) boolean includeCollaborator,
			@RequestParam(value = INCLUDE_RELATED_CONTENT, required = false, defaultValue = FALSE) boolean includeRelatedContent, @RequestParam(value = "merge", required = false) String merge, @RequestParam(value = "requestContext", required = false, defaultValue="edit-play") String requestContext, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = null;
		String includes[] = null;
		if (requestContext != null && requestContext.equalsIgnoreCase("library")) {
			includes = (String[]) ArrayUtils.addAll(LIBRARY_RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
			includes = (String[]) ArrayUtils.addAll(includes, LIBRARY_COLLECTION_INCLUDE_FIELDS);
			final String cacheKey = "collection-data-" + requestContext + "-" + collectionId;
			String data = null;
			data = getRedisService().getValue(cacheKey);
			if (data == null) {
			  data = serialize(this.getCollectionService().getCollection(collectionId, new HashMap<String, Object>()), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, includes);
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
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);
			}
			if (includeRelatedContent) {
				includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation");
			}
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_TAGS);
		    collection = getCollectionService().getCollection(collectionId, includeMetaInfo, includeCollaborator, includeRelatedContent, user, merge);
		    return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}	
		
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-delete");
		getCollectionService().deleteCollection(collectionId);
		
		SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-delete");
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.POST)
	public ModelAndView createCollectionItem(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(getValue(RESOURCE_ID, json), collectionId, this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), user, CollectionType.COLLECTION.getCollectionType(), false);
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
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-item-update");
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItemId);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestParam(value = INCLUDE_ADDITIONAL_INFO, required = false, defaultValue = FALSE) boolean includeAdditionalInfo, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, includeAdditionalInfo, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_LIST })
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.GET)
	public ModelAndView getCollectionItems(@PathVariable(value = ID) String collectionId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) Boolean skipPagination, @RequestParam(value = ORDER_BY, defaultValue = DESC ,required = false) String orderBy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CollectionItem> collectionItems = this.getCollectionService().getCollectionItems(collectionId, offset, limit, skipPagination, orderBy);
		String includesDefault[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_ITEM_TAGS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_WORKSPACE);
		String includes[] = (String[]) ArrayUtils.addAll(includesDefault, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(getCollectionService().setCollectionItemMetaInfo(collectionItems), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollectionItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-item-delete");
		SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItemId);
		getCollectionService().deleteCollectionItem(collectionItemId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/reorder/{sequence}" }, method = RequestMethod.PUT)
	public ModelAndView reorderCollectionItemSequence(@PathVariable(value = ID) String collectionItemId, @PathVariable(value = SEQUENCE) int newSequence, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().reorderCollectionItem(collectionItemId, newSequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-item-re-order");
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, responseDTO.getModel().getCollectionItemId());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/copy" }, method = RequestMethod.PUT)
	public ModelAndView copyCollection(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		JSONObject json = requestData(data);
		if (getValue(SKIP_COLLECTION_ITEM, json) != null ? !Boolean.parseBoolean(getValue(SKIP_COLLECTION_ITEM, json)) : true) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		}
		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = getCollectionService().copyCollection(collectionId, this.buildCopyCollectionFromInputParameters(getValue(COLLECTION, json), user), json != null && getValue(ADD_TO_SHELF, json) != null ? Boolean.parseBoolean(getValue(ADD_TO_SHELF, json)) : false, json != null && getValue(PARENT_ID, json) != null ? getValue(PARENT_ID, json) : null, user);
		
		SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-copy");
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		SessionContextSupport.putLogParameter(COPY_COLLECTION_ID, collection.getGooruOid());
		return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@RequestMapping(value = { "/resource/moreinfo/{id}" }, method = RequestMethod.GET)
	public ModelAndView getResourceMoreInfo(@PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionService().getResourceMoreInfo(resourceId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}/copy/{cid}" }, method = RequestMethod.PUT)
	public ModelAndView copyCollectionItem(@PathVariable(value = ID) String collectionItemId, @PathVariable(value = CID) String collectionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		CollectionItem collectionItem = getCollectionService().copyCollectionItem(collectionItemId, collectionId);
		if (collectionItem != null) {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "scollection-item-copy");
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		return toModelAndViewWithIoFilter(collectionItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/move" }, method = RequestMethod.PUT)
	public ModelAndView moveCollectionToFolder(HttpServletRequest request, @RequestBody String data, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().moveCollectionToFolder(getValue(SOURCE_ID, json), json != null && getValue(TARGET_ID, json) != null ? getValue(TARGET_ID, json) : null , user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "move-collection-folder");
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serializeToJson(responseDTO.getModelData(), includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/resource" }, method = RequestMethod.POST)
	public ModelAndView createResourceWithCollectionItem(HttpServletRequest request, @PathVariable(ID) String collectionId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createResourceWithCollectionItem(collectionId, this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "resource-create-collection-item");
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
			SessionContextSupport.putLogParameter(RESOURCE_ID, responseDTO.getModel().getResource().getGooruOid());
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
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createQuestionWithCollectionItem(collectionId, data, user, getValue(MEDIA_FILE_NAME, json));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "question-create-collection-item");
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
			SessionContextSupport.putLogParameter(RESOURCE_ID, responseDTO.getModel().getResource().getGooruOid());
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_CREATE_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		return toModelAndView(serializeToJson(responseDTO.getModelData(), includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { "/{id}/workspace" }, method = RequestMethod.GET)
	public ModelAndView getMyWorkspace(@PathVariable(value = ID) String partyUid, HttpServletRequest request, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = FILTER_NAME, required = false, defaultValue = ALL) String filterName, @RequestParam(value = ORDER_BY, required = false, defaultValue = "desc") String orderBy, @RequestParam(value = SHARING, required = false) String sharing,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) boolean skipPagination, HttpServletResponse resHttpServletResponse) {
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(OFFSET_FIELD, offset + "");
		filters.put(LIMIT_FIELD, limit + "");
		filters.put(SKIP_PAGINATION, skipPagination ? YES : NO);
		filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
		filters.put(FILTER_NAME, filterName);
		if (sharing != null) {
			filters.put(SHARING, sharing);
		}
		filters.put(ORDER_BY, orderBy);
		List<CollectionItem> collectionItems = getCollectionService().setCollectionItemMetaInfo(getCollectionService().getMyCollectionItems(partyUid, filters, user));
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_WORKSPACE);
		return toModelAndView(serialize(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@RequestMapping(value = { "/{id}/isAdded" }, method = RequestMethod.GET)
	public ModelAndView isAlreadyCopied(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		return jsonmodel.addObject(MODEL, this.getCollectionService().resourceCopiedFrom(gooruOid, user.getGooruUId()));
	}
	
	@RequestMapping(value = { "/{id}/parents" }, method = RequestMethod.GET)
	public ModelAndView getCollectionParent(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toJsonModelAndView(this.getCollectionService().getParentCollection(gooruOid, user.getPartyUid()), true);
	}

	private Collection buildCollectionFromInputParameters(String data, User user) {
		Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		collection.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getCollectionService().getContentType(ContentType.RESOURCE);
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
						.getSharing() : Sharing.PUBLIC.getSharing());

		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setDistinguish(Short.valueOf("0"));
		collection.setRecordSource(NOT_ADDED);
		collection.setIsFeatured(0);
		collection.setLastUpdatedUserUid(user.getGooruUId());

		return collection;
	}

	private Resource buildResourceFromInputParameters(String data, User user) {
		Resource resource = JsonDeserializer.deserialize(data, Resource.class);
		resource.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getResourceService().getContentType(ContentType.RESOURCE);
		resource.setContentType(contentType);
		resource.setLastModified(new Date(System.currentTimeMillis()));
		resource.setCreatedOn(new Date(System.currentTimeMillis()));
		if (!hasUnrestrictedContentAccess()) {
			resource.setSharing(Sharing.PUBLIC.getSharing());
		} else {
			resource.setSharing(resource.getSharing() != null && (resource.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? resource
					.getSharing() : Sharing.PUBLIC.getSharing());
		}
		resource.setUser(user);
		resource.setOrganization(user.getPrimaryOrganization());
		resource.setCreator(user);
		resource.setDistinguish(Short.valueOf("0"));
		resource.setRecordSource(NOT_ADDED);
		resource.setIsFeatured(0);
		resource.setLastUpdatedUserUid(user.getGooruUId());

		return resource;
	}

	private Collection buildCopyCollectionFromInputParameters(String data, User user) {
		Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		return collection;
	}

	private CollectionItem buildCollectionItemFromInputParameters(String data, User user) {
		CollectionItem collectionItem = JsonDeserializer.deserialize(data, CollectionItem.class);
		return collectionItem;
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

}
