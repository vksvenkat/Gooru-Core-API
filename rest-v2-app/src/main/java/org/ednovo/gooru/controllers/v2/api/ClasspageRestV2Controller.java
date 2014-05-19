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
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
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
import org.ednovo.gooru.infrastructure.persistence.hibernate.task.TaskRepository;
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

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping(value = { "/v2/classpage" ,"/v2/class" })
public class ClasspageRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClasspageService classpageService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private CollectionRepository collectionRepository;
	
	@Autowired
	private TaskRepository taskRepository;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createClasspage(@RequestBody String data,@RequestParam (value= "addToShelf" , defaultValue= "true", required=false) boolean addToMy ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Classpage> responseDTO = null;
		JSONObject json = requestData(data);
		if(getValue(CLASSPAGE, json) != null) {
			responseDTO = getClasspageService().createClasspage(this.buildClasspageFromInputParameters(getValue(CLASSPAGE, json), user),getValue(COLLECTION_ITEM, json) != null ? this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)) : null, getValue(COLLECTION_ID, json), user,addToMy);
		} else {
			responseDTO = getClasspageService().createClasspage(this.buildClasspageFromInputParameters(data, user),getValue(COLLECTION_ID, json),addToMy);
		}
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS); 
		includes= (String[]) ArrayUtils.addAll(includes,COLLECTION_ITEM_INCLUDE_FILEDS);
		includes= (String[]) ArrayUtils.addAll(includes,CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public ModelAndView updateClasspage(@PathVariable(value = ID) String classpageId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		
		ActionResponseDTO<Classpage> responseDTO = getClasspageService().updateClasspage(this.buildClasspageForUpdateParameters(getValue(CLASSPAGE, json) != null ? getValue(CLASSPAGE, json) : data), classpageId, hasUnrestrictedContentAccess());
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-update");
			SessionContextSupport.putLogParameter(CLASSPAGE_ID, classpageId);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		String[] includes = (String[]) ArrayUtils.addAll(CLASSPAGE_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspage(@PathVariable(value = ID) String classpageId, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) boolean includeCollectionItem, @RequestParam(value = "merge", required = false) String merge, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageId, user, merge), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true ,includes);
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
	public ModelAndView getClasspageByCode(@PathVariable(value = CLASSPAGE_CODE) String classpageCode, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) boolean includeCollectionItem, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageCode,user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true,includes);
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
		ActionResponseDTO<CollectionItem> responseDTO = getClasspageService().createClasspageItem(getValue(COLLECTION_ID, json), classpageId, this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), user, CollectionType.CLASSPAGE.getCollectionType());
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			
			SessionContextSupport.putLogParameter(EVENT_NAME, "classpage-create-collection-task-item");
			SessionContextSupport.putLogParameter(CLASSPAGE_ID, classpageId);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, responseDTO.getModel().getCollection().getGooruOid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/collection/{id}/item", method = RequestMethod.POST)
	public ModelAndView createClasspageItems(@PathVariable(value = ID) String collectionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<CollectionItem> collectionItems = getCollectionService().createCollectionItems(JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}), collectionId, user);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		return toModelAndViewWithIoFilter(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/item/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateClasspageItem(@PathVariable(value = ID) String collectionItemId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)), collectionItemId, user);
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
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_CREATE_ITEM_INCLUDE_FILEDS);	
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, false, user, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/collection/{id}", method = RequestMethod.GET)
	public ModelAndView getCollectionClasspageAssoc(@PathVariable(value = ID) String collectionId, @RequestParam(value = GOORU_UID, required = false) String gooruUid, HttpServletRequest request, HttpServletResponse response) {
		return toJsonModelAndView(this.getTaskRepository().getCollectionClasspageAssoc(collectionId, gooruUid), true);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/collection/{id}/count", method = RequestMethod.GET)
	public ModelAndView getCollectionClasspageAssocCount(@PathVariable(value = ID) String collectionId, @RequestParam(value = GOORU_UID, required = false) String gooruUid, HttpServletRequest request, HttpServletResponse response) {
		return toJsonModelAndView(this.getTaskRepository().getCollectionClasspageAssocCount(collectionId), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_DELETE })
	@RequestMapping(value = "/collection/{id}", method = RequestMethod.DELETE)
	public void deleteCollectionAssocInAssignment(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		this.getTaskService().deleteCollectionAssocInAssignment(collectionId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/{cid}/item", method = RequestMethod.GET)
	public ModelAndView getClasspageItems(@PathVariable(value = COLLECTIONID) String classpageId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) Boolean skipPagination, @RequestParam(value = ORDER_BY, defaultValue = PLANNED_END_DATE ,required = false) String orderBy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		List<CollectionItem> collectionItems = this.getCollectionService().getCollectionItems(classpageId, offset , limit , skipPagination , orderBy, "classpage");
		String responseJson = null;
			SearchResults<CollectionItem> result = new SearchResults<CollectionItem>();
			result.setSearchResults(collectionItems);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCollectionCount(classpageId,"classpage"));
			responseJson = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL,includes);
		return toModelAndView(responseJson);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}", method = RequestMethod.DELETE)
	public void deleteClasspageItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getCollectionService().deleteCollectionItem(collectionItemId, user);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{code}/member/join", method = RequestMethod.POST)
	public ModelAndView classpageUserJoin(@PathVariable(value = CODE) String code,@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		return toJsonModelAndView(this.getClasspageService().classpageUserJoin(code,JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}),apiCaller), true);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{code}/member/remove", method = RequestMethod.DELETE)
	public void classpageUserRemove(@PathVariable(value = CODE) String code,@RequestParam String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		this.getClasspageService().classpageUserRemove(code,JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}),apiCaller);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/member" }, method = RequestMethod.GET)
	public ModelAndView getClassMemberList(@PathVariable(ID) String code,@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) Boolean skipPagination ,@RequestParam(value = "groupByStatus", defaultValue = "false", required = false) Boolean groupByStatus, @RequestParam(value = "filterBy", required = false) String filterBy, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return toModelAndView(serialize(this.getClasspageService().getMemberList(code, offset, limit, skipPagination,filterBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, CLASS_MEMBER_FIELDS));
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/my/study", "/my/teach-study" }, method = RequestMethod.GET)
	public ModelAndView getMyStudy( HttpServletRequest request, HttpServletResponse response,  @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue= "false") boolean skipPagination, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, 
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value= ORDER_BY, defaultValue="desc",required= false) String orderBy) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		
		return toModelAndView(this.getClasspageService().getMyStudy(apiCaller,orderBy, offset,  limit, skipPagination), RESPONSE_FORMAT_JSON);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView getMyClasspage(HttpServletRequest request, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue= "false") boolean skipPagination, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = ORDER_BY, required = false, defaultValue = DESC) String orderBy, HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			skipPagination = json != null && getValue(SKIP_PAGINATION, json) != null ? Boolean.parseBoolean(getValue(SKIP_PAGINATION, json)) : skipPagination;
		}
		List<Classpage> classpage = this.getClasspageService().getMyClasspage(offset,limit, user, skipPagination, orderBy);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		if (!skipPagination) {
			SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpage);
			result.setTotalHitCount(this.getClasspageService().getMyClasspageCount(user.getGooruUId()));
			return toModelAndViewWithIoFilter(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		} else {
			return toModelAndViewWithIoFilter(getClasspageService().getMyClasspage(offset, limit, user, true, orderBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL,true ,includes);
		}
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/member/suggest" }, method = RequestMethod.GET)
	public ModelAndView classMemberSuggest(@RequestParam(value = "query") String queryText, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getClasspageService().classMemberSuggest(queryText, user.getPartyUid()), RESPONSE_FORMAT_JSON);
	}

	private Classpage buildClasspageFromInputParameters(String data, User user) {
		Classpage classpage = JsonDeserializer.deserialize(data, Classpage.class);
		classpage.setGooruOid(UUID.randomUUID().toString());
		classpage.setClasspageCode(BaseUtil.base48Encode(7));
		classpage.setContentType(getCollectionService().getContentType(ContentType.RESOURCE));
		classpage.setResourceType(getCollectionService().getResourceType(ResourceType.Type.CLASSPAGE.getType()));
		classpage.setLastModified(new Date(System.currentTimeMillis()));
		classpage.setCreatedOn(new Date(System.currentTimeMillis()));
		
		classpage.setUser(user);
		classpage.setCollectionType(ResourceType.Type.CLASSPAGE.getType());
		classpage.setOrganization(user.getPrimaryOrganization());
		classpage.setCreator(user);
		classpage.setDistinguish(Short.valueOf("0"));
		classpage.setRecordSource(NOT_ADDED);
		classpage.setIsFeatured(0);
		classpage.setLastUpdatedUserUid(user.getGooruUId());
		if(classpage.getSharing() != null && (classpage.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || classpage.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
			classpage.setSharing(classpage.getSharing());
		} else {
			classpage.setSharing(Sharing.PRIVATE.getSharing());
		}
		
		return classpage;
	}
	
	private Classpage buildClasspageForUpdateParameters(String data) {
		return JsonDeserializer.deserialize(data, Classpage.class);
	}

	private CollectionItem buildCollectionItemFromInputParameters(String data) {
		
		return  JsonDeserializer.deserialize(data, CollectionItem.class);
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

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

}
