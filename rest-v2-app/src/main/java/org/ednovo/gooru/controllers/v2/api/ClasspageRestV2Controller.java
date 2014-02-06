/////////////////////////////////////////////////////////////
//ClasspageRestV2Controller.java
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
/**
 * 
 */
package org.ednovo.gooru.controllers.v2.api;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.classpage.ClasspageService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.task.TaskService;
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
@RequestMapping(value = { "/v2/classpage" })
public class ClasspageRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClasspageService classpageService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private CollectionRepository collectionRepository;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createClasspage(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Classpage> responseDTO = getClasspageService().createClasspage(this.buildClasspageFromInputParameters(getValue(CLASSPAGE, json), user),this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), getValue(COLLECTION_ID, json), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-create");
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS); 
		includes= (String[]) ArrayUtils.addAll(includes,COLLECTION_ITEM_INCLUDE_FILEDS);
		includes= (String[]) ArrayUtils.addAll(includes,CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public ModelAndView updateClasspage(@PathVariable(value = ID) String classpageId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		Classpage newClasspage = this.buildClasspageUpdateFromInputParameters(getValue(CLASSPAGE, json));
		ActionResponseDTO<Classpage> responseDTO = getClasspageService().updateClasspage(newClasspage, classpageId, hasUnrestrictedContentAccess());
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-update");
			SessionContextSupport.putLogParameter(CLASSPAGE_ID, classpageId);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		String[] includes = (String[]) ArrayUtils.addAll(CLASSPAGE_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspage(@PathVariable(value = ID) String classpageId, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = "merge", required = false) String merge, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageId, user, merge), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getClasspages(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) Boolean skipPagination, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = AUTHOR, required = false) String author,
			@RequestParam(value = USERNAME, required = false) String userName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		return toModelAndView(serialize(getClasspageService().getClasspages(offset, limit, skipPagination, user, title, author, userName), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
	public ModelAndView getClasspageByCode(@PathVariable(value = CLASSPAGE_CODE) String classpageCode, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageCode,user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteClasspage(@PathVariable(value = ID) String classpageId, HttpServletRequest request, HttpServletResponse response) {
		getClasspageService().deleteClasspage(classpageId);
		
		SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-delete");
		SessionContextSupport.putLogParameter(CLASSPAGE_ID, classpageId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/item", method = RequestMethod.POST)
	public ModelAndView createClasspageItem(@PathVariable(value = ID) String classpageId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(getValue(COLLECTION_ID, json), classpageId, this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), user, CollectionType.COLLECTION.getCollectionType(), false);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-create-collection-task-item");
			SessionContextSupport.putLogParameter(CLASSPAGE_ID, classpageId);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, responseDTO.getModel().getCollection().getGooruOid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS); 
		includes = (String[]) ArrayUtils.addAll(COLLECTION_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateClasspageItem(@PathVariable(value = ID) String collectionItemId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-item-update");
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItemId);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "item/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspageItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, false, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/collection/{id}", method = RequestMethod.GET)
	public ModelAndView getCollectionClasspageAssoc(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		return toJsonModelAndView(this.getTaskService().getCollectionClasspageAssoc(collectionId), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_DELETE })
	@RequestMapping(value = "/assignment/collection/{id}", method = RequestMethod.DELETE)
	public void deleteCollectionAssocInAssignment(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		this.getTaskService().deleteCollectionAssocInAssignment(collectionId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/{cid}/item", method = RequestMethod.GET)
	public ModelAndView getClasspageItems(@PathVariable(value = COLLECTIONID) String classpageId, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = ORDER_BY, defaultValue = DESC ,required = false) String orderBy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		List<CollectionItem> collectionItems = this.getCollectionService().getCollectionItems(classpageId, json != null ? Integer.parseInt(getValue(OFFSET_FIELD, json)) : 0, json != null ? Integer.parseInt(getValue(LIMIT_FIELD, json)) : 20, json != null ? Boolean.parseBoolean(getValue(SKIP_PAGINATION, json)) : false, orderBy);
		String responseJson = null;
			SearchResults<CollectionItem> result = new SearchResults<CollectionItem>();
			result.setSearchResults(collectionItems);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCollectionCount(classpageId));
			responseJson = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL,includes);
		return toModelAndView(responseJson);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}", method = RequestMethod.DELETE)
	public void deleteClasspageItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-delete-classpage-item");
		SessionContextSupport.putLogParameter(CLASSPAGE_ITEM_ID, collectionItemId);
		getCollectionService().deleteCollectionItem(collectionItemId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView getMyClasspage(HttpServletRequest request, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = null;
		String[] includes = null;
		if (data != null && !data.isEmpty()) {
			json = requestData(data);
			includes = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		Boolean skipPagination = Boolean.parseBoolean((json != null && getValue(SKIP_PAGINATION, json) != null ? getValue(SKIP_PAGINATION, json) : FALSE));
		List<Classpage> classpage = getClasspageService().getMyClasspage(Integer.parseInt(json != null && getValue(OFFSET_FIELD, json) != null ? getValue(OFFSET_FIELD, json) : OFFSET.toString()),
				Integer.parseInt(json != null && getValue(LIMIT_FIELD, json) != null ? getValue(LIMIT_FIELD, json) : LIMIT.toString()), user, skipPagination, json != null && getValue(ORDER_BY, json) != null ? getValue(ORDER_BY, json) : "desc");
		includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		String responseJson = null;
		if (!skipPagination) {
			SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpage);
			result.setTotalHitCount(this.getClasspageService().getMyClasspageCount(user.getGooruUId()));
			responseJson = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
		} else {
			responseJson = serialize(
					getClasspageService().getMyClasspage(Integer.parseInt(json != null && getValue(OFFSET_FIELD, json) != null ? getValue(OFFSET_FIELD, json) : OFFSET.toString()), Integer.parseInt(json != null && getValue(LIMIT_FIELD, json) != null ? getValue(LIMIT_FIELD, json) : LIMIT.toString()),
							user, true, json != null && getValue(ORDER_BY, json) != null ? getValue(ORDER_BY, json) : "desc"), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
		}
		return toModelAndView(responseJson);
	}

	private Classpage buildClasspageFromInputParameters(String data, User user) {
		Classpage classpage = JsonDeserializer.deserialize(data, Classpage.class);
		classpage.setGooruOid(UUID.randomUUID().toString());
		classpage.setClasspageCode(BaseUtil.base48Encode(7));
		classpage.setContentType(getCollectionService().getContentType(ContentType.RESOURCE));
		classpage.setResourceType(getCollectionService().getResourceType(ResourceType.Type.CLASSPAGE.getType()));
		classpage.setLastModified(new Date(System.currentTimeMillis()));
		classpage.setCreatedOn(new Date(System.currentTimeMillis()));
		classpage.setSharing(Sharing.ANYONEWITHLINK.getSharing());
		classpage.setUser(user);
		classpage.setOrganization(user.getPrimaryOrganization());
		classpage.setCreator(user);
		classpage.setDistinguish(Short.valueOf("0"));
		classpage.setRecordSource(NOT_ADDED);
		classpage.setIsFeatured(0);
		classpage.setLastUpdatedUserUid(user.getGooruUId());
		if (!hasUnrestrictedContentAccess()) {
			classpage.setSharing(Sharing.PUBLIC.getSharing());
		} else {
			classpage
					.setSharing(classpage.getSharing() != null && (classpage.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || classpage.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || classpage.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? classpage
							.getSharing() : Sharing.PUBLIC.getSharing());
		}
		return classpage;
	}

	private CollectionTaskAssoc buildCollectionTaskItemFromInputParameters(String data, String assignmentId, Classpage classpage) {
		CollectionTaskAssoc collectionTaskAssoc = data != null ? JsonDeserializer.deserialize(data, CollectionTaskAssoc.class) : new CollectionTaskAssoc();
		Task task = new Task();
		task.setGooruOid(assignmentId);
		collectionTaskAssoc.setTask(task);
		collectionTaskAssoc.setCollection(classpage);
		return collectionTaskAssoc;
	}
	
	private CollectionItem buildCollectionItemFromInputParameters(String data, User user) {
		CollectionItem collectionItem = JsonDeserializer.deserialize(data, CollectionItem.class);
		return collectionItem;
	}

	private Classpage buildClasspageUpdateFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, Classpage.class);
	}

	public ClasspageService getClasspageService() {
		return classpageService;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public void setCollectionRepository(CollectionRepository collectionRepository) {
		this.collectionRepository = collectionRepository;
	}

}
