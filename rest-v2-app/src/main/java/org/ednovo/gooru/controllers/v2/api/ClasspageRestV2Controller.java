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
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
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
@RequestMapping(value = { "/v2/classpage", "/v2/class" })
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
	public ModelAndView createClasspage(@RequestBody String data, @RequestParam(value = ADD_TO_SHELF, defaultValue = TRUE, required = false) boolean addToMy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Classpage> responseDTO = null;
		JSONObject json = requestData(data);
		if (getValue(CLASSPAGE, json) != null) {
			responseDTO = getClasspageService().createClasspage(this.buildClasspageFromInputParameters(getValue(CLASSPAGE, json), user), getValue(COLLECTION_ITEM, json) != null ? this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json)) : null, getValue(COLLECTION_ID, json),
					user, addToMy);
		} else {
			responseDTO = getClasspageService().createClasspage(this.buildClasspageFromInputParameters(data, user), getValue(COLLECTION_ID, json), addToMy);
		}
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public ModelAndView updateClasspage(@PathVariable(value = ID) String classpageId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);

		ActionResponseDTO<Classpage> responseDTO = getClasspageService().updateClasspage(this.buildClasspageForUpdateParameters(getValue(CLASSPAGE, json) != null ? getValue(CLASSPAGE, json) : data), classpageId, hasUnrestrictedContentAccess());
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(CLASSPAGE_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspage(@PathVariable(value = ID) String classpageId, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) boolean includeCollectionItem,
			@RequestParam(value = MERGE, required = false) String merge, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageId, user, merge), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getClasspages(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
		    @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = AUTHOR, required = false) String author,
			@RequestParam(value = USERNAME, required = false) String userName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		return toModelAndView(serialize(getClasspageService().getClasspages(offset, limit, user, title, author, userName), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
	public ModelAndView getClasspageByCode(@PathVariable(value = CLASSPAGE_CODE) String classpageCode, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) boolean includeCollectionItem,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageCode, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteClasspage(@PathVariable(value = ID) String classpageId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		getClasspageService().deleteClasspage(classpageId,user);
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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/assign/{cid}", method = RequestMethod.POST)
	public ModelAndView assignCollection(@PathVariable(value = ID) String classPageId, @RequestParam(value="direction", required=false ) String direction,@RequestParam(value="planedEndDate", required=false ) String planedEndDate,@PathVariable(value = CID) String collectionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<CollectionItem> collectionItems = getCollectionService().assignCollection(classPageId, collectionId, user, direction,planedEndDate);
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
		CollectionItem newCollectionItem = this.buildCollectionItemFromInputParameters(getValue(COLLECTION_ITEM, json));
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(newCollectionItem, collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			if (newCollectionItem.getStatus() != null) {
				getClasspageService().updateAssignment(collectionItemId, newCollectionItem.getStatus(), user);
			}
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "item/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspageItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, false, user, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/collection/{id}", method = RequestMethod.GET)
	public ModelAndView getCollectionClasspageAssoc(@PathVariable(value = ID) String collectionId, @RequestParam(value = GOORU_UID, required = false) String gooruUid, HttpServletRequest request, HttpServletResponse response) {
		return toJsonModelAndView(this.getTaskRepository().getCollectionClasspageAssoc(collectionId, gooruUid), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/collection/{id}/count", method = RequestMethod.GET)
	public ModelAndView getCollectionClasspageAssocCount(@PathVariable(value = ID) String collectionId, @RequestParam(value = GOORU_UID, required = false) String gooruUid, HttpServletRequest request, HttpServletResponse response) {
		return toJsonModelAndView(this.getTaskRepository().getCollectionClasspageAssocCount(collectionId), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/collection/{id}", method = RequestMethod.DELETE)
	public void deleteCollectionAssocInAssignment(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		this.getTaskService().deleteCollectionAssocInAssignment(collectionId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{cid}/item", method = RequestMethod.GET)
	public ModelAndView getClasspageItems(@PathVariable(value = COLLECTIONID) String classpageId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false) Integer limit,
		@RequestParam(value = ORDER_BY, defaultValue = PLANNED_END_DATE, required = false) String orderBy, @RequestParam(value = OPTIMIZE, required = false, defaultValue = FALSE) Boolean optimize, @RequestParam(value = STATUS, required = false) String status, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		User user = (User) request.getAttribute(Constants.USER);
		List<Map<String, Object>> collectionItems = this.getClasspageService().getClasspageItems(classpageId, limit != null ? limit : (optimize ? 30 : 5)  , offset, user.getPartyUid(), orderBy, optimize, status);
		String responseJson = null;
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(collectionItems);
		result.setTotalHitCount(this.getCollectionRepository().getClasspageCollectionCount(classpageId, status, user.getPartyUid(), orderBy));
		responseJson = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes);
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
	public ModelAndView classpageUserJoin(@PathVariable(value = CODE) String code, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		return toJsonModelAndView(this.getClasspageService().classpageUserJoin(code, JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}), apiCaller), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{code}/member/remove", method = RequestMethod.DELETE)
	public void classpageUserRemove(@PathVariable(value = CODE) String code, @RequestParam String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		this.getClasspageService().classpageUserRemove(code, JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}), apiCaller);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/member" }, method = RequestMethod.GET)
	public ModelAndView getClassMemberList(@PathVariable(ID) String code, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
		    @RequestParam(value = GROUP_BY_STATUS, defaultValue = "false", required = false) Boolean groupByStatus, @RequestParam(value = FILTER_BY, required = false) String filterBy,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return toModelAndView(serialize(this.getClasspageService().getMemberList(code, offset, limit, filterBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, CLASS_MEMBER_FIELDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/my/{type}" }, method = RequestMethod.GET)
	public ModelAndView getMyTeachAndStudy(@PathVariable(value = TYPE) String type, HttpServletRequest request, HttpServletResponse response,  @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = ORDER_BY, defaultValue = "desc", required = false) String orderBy) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		return toModelAndView(serialize(this.getClasspageService().getMyStudy(apiCaller, orderBy, offset, limit, type), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, STUDY_RESOURCE_FIELDS));
	}
	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView getMyClasspage(HttpServletRequest request, @RequestParam(value = DATA_OBJECT, required = false) String data, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") boolean skipPagination,
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = ORDER_BY, required = false, defaultValue = DESC) String orderBy,
			HttpServletResponse resHttpServletResponse) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<Classpage> classpage = this.getClasspageService().getMyClasspage(offset, limit, user, skipPagination,orderBy);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		if (!skipPagination) {
			SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpage);
			result.setTotalHitCount(this.getClasspageService().getMyClasspageCount(user.getGooruUId()));
			return toModelAndViewWithIoFilter(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		} else {
			return toModelAndViewWithIoFilter(getClasspageService().getMyClasspage(offset, limit, user, true,orderBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/member/suggest" }, method = RequestMethod.GET)
	public ModelAndView classMemberSuggest(@RequestParam(value = QUERY) String queryText, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getClasspageService().classMemberSuggest(queryText, user.getPartyUid()), RESPONSE_FORMAT_JSON);
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
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item", method = RequestMethod.GET)
	public ModelAndView getClasspageAssoc(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,@RequestParam(value = CLASSPAGE_ID, required = false) String classpageId ,
			@RequestParam(value = COLLECTION_ID, required = false) String collectionId,@RequestParam(value = TITLE, required = false) String title,@RequestParam(value = COLLECTION_TITLE, required = false) String collectionTitle, @RequestParam(value = CLASS_CODE, required = false) String classCode,@RequestParam(value = COLLECTION_CREATOR, required = false) String collectionCreator ){	
		return toJsonModelAndView(this.getClasspageService().getClasspageAssoc(offset, limit, classpageId,collectionId,title,collectionTitle,classCode,collectionCreator),true);
	}
	
	/********************pathway ***********************/
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/pathway", method = RequestMethod.POST)
	public ModelAndView createPathway(@RequestBody String data, @PathVariable(value= ID) String classId ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		Collection collection = this.getClasspageService().createPathway(classId,this.buildPathwayFromInputParameters(data, user),getValue(COLLECTION_ID, json), getValue(IS_REQUIRED, json) != null ? Boolean.parseBoolean(getValue(IS_REQUIRED, json)) : false);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(collection , RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{cid}/pathway/{id}", method = RequestMethod.PUT)
	public ModelAndView updatePathway(@RequestBody String data, @PathVariable(value= CID) String classId , @PathVariable(value= ID) String pathwayGooruOid ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Collection pathwayCollection = this.getClasspageService().updatePathway(pathwayGooruOid, this.buildUpadtePathwayCollectionFromInputParameters(data, user));
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(pathwayCollection , RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/pathway/{id}" }, method = RequestMethod.DELETE)
	public void deletePathway(@PathVariable(value = ID) String pathwayGooruOId, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		this.getClasspageService().deletePathway(pathwayGooruOId, user);
	}
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/pathway/{pid}", method = RequestMethod.GET)
	public ModelAndView getPathwayItems(@PathVariable(value= ID) String classId ,  @PathVariable(value= "pid") String pathId ,@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, 
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,@RequestParam(value = ORDER_BY, defaultValue = DESC ,required = false) String orderBy ,HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<CollectionItem> collectionItems = this.getClasspageService().getPathwayItems(classId,pathId,offset,limit,orderBy);
		String includesDefault[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_ITEM_TAGS);
		includesDefault = (String[]) ArrayUtils.addAll(includesDefault, COLLECTION_WORKSPACE);
		String includes[] = (String[]) ArrayUtils.addAll(includesDefault, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(getCollectionService().setCollectionItemMetaInfo(collectionItems, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/pathway/{pid}/assign/{cid}", method = RequestMethod.POST)
	public ModelAndView assignCollectionToPathway(@PathVariable(value = ID) String classPageId, @PathVariable(value= "pid") String pathwayId, @RequestParam(value="direction", required=false ) String direction,@RequestParam(value="planedEndDate", required=false ) String planedEndDate,@PathVariable(value = CID) String collectionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		List<CollectionItem> collectionItems = getCollectionService().assignCollectionToPathway(classPageId, pathwayId ,collectionId, user, direction,planedEndDate);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		return toModelAndViewWithIoFilter(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "{id}/pathway/{pid}/reorder/{sequence}" }, method = RequestMethod.PUT)
	public ModelAndView reorderPathwaySequence(@PathVariable(value = ID) String classId, @PathVariable(value= "pid") String pathwayId, @PathVariable(value = SEQUENCE) int newSequence, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = this.getClasspageService().reorderPathwaySequence(classId,pathwayId ,newSequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
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
		if (classpage.getSharing() != null && (classpage.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || classpage.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
			classpage.setSharing(classpage.getSharing());
		} else {
			classpage.setSharing(Sharing.PUBLIC.getSharing());
		}

		return classpage;
	}
	
	private Collection buildPathwayFromInputParameters(String data, User user) {
		Collection collection = JsonDeserializer.deserialize(data, Collection.class);
		collection.setGooruOid(UUID.randomUUID().toString());
		collection.setContentType(getCollectionService().getContentType(ContentType.RESOURCE));
		collection.setResourceType(getCollectionService().getResourceType(ResourceType.Type.PATHWAY.getType()));
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection.setUser(user);
		collection.setCollectionType(ResourceType.Type.PATHWAY.getType());
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setDistinguish(Short.valueOf("0"));
		collection.setRecordSource(NOT_ADDED);
		collection.setIsFeatured(0);
		collection.setLastUpdatedUserUid(user.getGooruUId());
		if (collection.getSharing() != null && (collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
			collection.setSharing(collection.getSharing());
		} else {
			collection.setSharing(Sharing.PUBLIC.getSharing());
		}

		return collection;
	}
	
	private Collection buildUpadtePathwayCollectionFromInputParameters(String data, User user) {
		return JsonDeserializer.deserialize(data, Collection.class);
	}

	private Classpage buildClasspageForUpdateParameters(String data) {
		return JsonDeserializer.deserialize(data, Classpage.class);
	}

	private CollectionItem buildCollectionItemFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, CollectionItem.class);
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
