/////////////////////////////////////////////////////////////
//TaskManagementRestV2Controller.java
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AttachDTO;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.TaskAssoc;
import org.ednovo.gooru.core.api.model.TaskHistoryItem;
import org.ednovo.gooru.core.api.model.TaskResourceAssoc;
import org.ednovo.gooru.core.api.model.TaskUserAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.task.TaskService;
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

@Controller
@RequestMapping(value = { "/v2/project", "/v2/assignment", "/v2/module" })
public class TaskManagementRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "" }, method = RequestMethod.POST)
	public ModelAndView createTask(HttpServletRequest request, @RequestBody String data, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_CREATE_TASK);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Task> task = getTaskService().createTask(this.buildTaskFromInputParameters(getValue(TASK, json)), getValue(PLANNED_END_DATE, json) != null ? getValue(PLANNED_END_DATE, json) : null, user, this.buildAttachFromInputParameters(getValue(ATTACH_TO, json)));
		if (task.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_INCLUDES : includeFields, ERROR_INCLUDE);

		SessionContextSupport.putLogParameter(EVENTNAME, CREATE_TASK);
		SessionContextSupport.putLogParameter(TASK_UID, task.getModel().getGooruOid());
		SessionContextSupport.putLogParameter(CREATOR_UID, task.getModel().getCreatedOn() != null ? task.getModel().getCreator().getPartyUid() : null);
		SessionContextSupport.putLogParameter(TASK_CREATED_DATE, task.getModel().getCreatedOn());
		SessionContextSupport.putLogParameter(TASK_TITLE, task.getModel().getTitle());
		return toModelAndViewWithIoFilter(task.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = { "/{id}" })
	public ModelAndView updateTask(HttpServletRequest request, @PathVariable(ID) String gooruOid, @RequestBody String data, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_UPDATE_TASK);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Task> task = getTaskService().updateTask(gooruOid, this.buildTaskFromInputParameters(getValue(TASK, json)), getValue(PLANNED_END_DATE, json) != null ? getValue(PLANNED_END_DATE, json) : null, user);
		if (task.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_INCLUDES : includeFields, ERROR_INCLUDE);

		SessionContextSupport.putLogParameter(EVENTNAME, UPDATE_TASK);
		SessionContextSupport.putLogParameter(TASK_UID, task.getModel().getGooruOid());
		SessionContextSupport.putLogParameter(MODIFIED_USER, task.getModel().getLastUpdatedUserUid() != null ? task.getModel().getLastUpdatedUserUid() : null);
		SessionContextSupport.putLogParameter(LAST_MODIFIED_DATE, task.getModel().getLastModified());

		return toModelAndViewWithIoFilter(task.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}" })
	public ModelAndView getTask(@PathVariable(ID) String gooruOid, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_GET_TASK);
		Task task = this.getTaskService().getTask(gooruOid);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includes = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_INCLUDES : includeFields, ERROR_INCLUDE);
		SessionContextSupport.putLogParameter(GOORU_OID, gooruOid);
		return toModelAndViewWithIoFilter(task, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getTasks(@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit, @RequestParam(value = TASK_ID , required = false) String taksGooruOid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = FALSE) Boolean skipPagination, @RequestParam(value = CLASSPAGE_ID, required = false) String classpageId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getTaskService().getTasks(limit, offset, user, skipPagination, taksGooruOid, classpageId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLECTION_TASK_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = { "/{id}" })
	public void deleteTask(@PathVariable(ID) String gooruOid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getTaskService().deleteTask(gooruOid);
		SessionContextSupport.putLogParameter(EVENTNAME, DELETE_TASK);
		SessionContextSupport.putLogParameter(GOORU_OID, gooruOid);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = { "/{id}/item" })
	public ModelAndView createTaskResourceAssociation(HttpServletRequest request, @PathVariable(ID) String gooruOid, @RequestBody String data, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<TaskResourceAssoc> task = getTaskService().createTaskResourceAssociation(this.buildTaskResourceFromInputParameters(getValue(TASK_RESOURCE_ASSOC, json)), user, gooruOid);
		if (task.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_CREATE_RESOURCE_ASSOC_INCLUDES : includeFields, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);

		SessionContextSupport.putLogParameter(EVENTNAME, CREATE_TASK_RESOURCE_ASSOCIATION);
		SessionContextSupport.putLogParameter(TASK_ASSOCIATED_DATE, task.getModel().getAssociationDate());
		SessionContextSupport.putLogParameter(TASK_RESOURCE_ASSOCIATOR, task.getModel().getAssociatedBy() != null ? task.getModel().getAssociatedBy().getPartyUid() : null);
		SessionContextSupport.putLogParameter(TASK_RESOURCE_UID, task.getModel().getTaskResourceAssocUid());
		SessionContextSupport.putLogParameter(RESOURCE, task.getModel().getResource().getGooruOid());

		return toModelAndViewWithIoFilter(task.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{tid}/item/{id}" })
	public ModelAndView getTaskResourceAssociatedItem(@PathVariable(TID) String taskId, @PathVariable(ID) String resourceAssocId, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		TaskResourceAssoc taskResourceAssoc = this.getTaskService().getTaskResourceAssociatedItem(resourceAssocId);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includes = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_RESOURCE_ASSOC_INCLUDES : includeFields, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(taskResourceAssoc, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/item" })
	public ModelAndView getTaskResourceAssociatedItems(@PathVariable(ID) String gooruOid, @RequestParam(value = SHARING, required = false) String sharing, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includes = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_CREATE_RESOURCE_ASSOC_INCLUDES : includeFields, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, TASK_INCLUDES);
		List<Resource> taskResourceAssocs = this.getTaskService().getTaskResourceAssociatedItems(gooruOid, Integer.parseInt(json != null && getValue(OFFSET_FIELD, json) != null ? getValue(OFFSET_FIELD, json) : OFFSET.toString()),
				Integer.parseInt(json != null && getValue(LIMIT_FIELD, json) != null ? getValue(LIMIT_FIELD, json) : LIMIT.toString()), json != null ? getValue(SKIP_PAGINATION, json) : NO, json != null ? getValue(ORDER_BY, json) : DATE, sharing);
		String responseJson = null;
		if ((getValue(SKIP_PAGINATION, json) == null || (getValue(SKIP_PAGINATION, json) != null && getValue(SKIP_PAGINATION, json).equalsIgnoreCase(NO)))) {
			SearchResults<Resource> result = new SearchResults<Resource>();
			result.setSearchResults(taskResourceAssocs);
			result.setTotalHitCount(this.taskRepository.getTaskResourceCount(gooruOid, sharing));
			responseJson = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, includes));
		} else {
			responseJson = serialize(taskResourceAssocs, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, includes));
		}

		return toModelAndView(responseJson);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = { "/{tid}/item/{id}" })
	public void deleteTaskResourceAssociatedItem(@PathVariable(TID) String taskUid, @PathVariable(ID) String resourceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getTaskService().deleteTaskResourceAssocItem(taskUid, resourceId);
		SessionContextSupport.putLogParameter(EVENTNAME, DELETE_TASK_RESOURCE_ASSOCIATED_ITEM);
		SessionContextSupport.putLogParameter(TASK_UID, taskUid);
		SessionContextSupport.putLogParameter(RESOURCE_ASSOC_UID, resourceId);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = { "/{tid}/item/{id}/reorder/{sequence}" })
	public ModelAndView reorderTaskResourceAssociatedItem(@PathVariable(ID) String taskResourceAssocId, @PathVariable(TID) String taskUid, @PathVariable(value = SEQUENCE) int sequence, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionResponseDTO<TaskResourceAssoc> responseDTO = getTaskService().reorderResourceAssociatedItem(taskUid, taskResourceAssocId, sequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includes = (String[]) ArrayUtils.addAll(TASK_RESOURCE_ASSOC_INCLUDES, ERROR_INCLUDE);

		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = { "/{id}/associate/user/{uid}" })
	public ModelAndView createTaskUserAssoc(@PathVariable(ID) String gooruOid, @PathVariable(UID) String gooruUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		List<TaskUserAssoc> taskUserAssocList = getTaskService().createTaskUserAssoc(gooruOid, gooruUid, json != null ? getValue(ASSOCIATION_TYPE, json) : null);
		if (taskUserAssocList.size() < 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			for (TaskUserAssoc taskUserAssoc : taskUserAssocList) {
				SessionContextSupport.putLogParameter(EVENTNAME, CREATE_TASK_USER_ASSOC);
				SessionContextSupport.putLogParameter(USER_UID, taskUserAssoc.getTask().getUser().getGooruUId());
				SessionContextSupport.putLogParameter(GOORU_OID, taskUserAssoc.getTask().getGooruOid());
				SessionContextSupport.putLogParameter(CREATOR_UID, taskUserAssoc.getTask().getCreator() != null ? taskUserAssoc.getTask().getCreator().getPartyUid() : null);
				SessionContextSupport.putLogParameter(ASSOCIATION_TYPE, taskUserAssoc.getAssociationType());
				SessionContextSupport.putLogParameter(TASK_CREATED_DATE, taskUserAssoc.getTask().getCreatedOn());
			}
		}

		return toModelAndViewWithIoFilter(taskUserAssocList, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, TASK_USER_ASSOC_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = { "/{id}/associate/user/{uid}" })
	public void deleteTaskUserAssoc(@PathVariable(ID) String taskUid, @PathVariable(UID) String userUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getTaskService().deleteTaskUserAssoc(taskUid, userUid);
		SessionContextSupport.putLogParameter(EVENTNAME, DELETE_TASK_USER_ASSOC);
		SessionContextSupport.putLogParameter(TASK_UID, taskUid);
		SessionContextSupport.putLogParameter(USER_UID, userUid);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = { "/{id}/associate" })
	public ModelAndView createTaskAssoc(@PathVariable(ID) String taskId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_CREATE_TASK_ASSOC);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<TaskAssoc> responseDTO = getTaskService().createTaskAssoc(this.buildTaskAssocFromInputParameters(getValue(TASK_ASSOC, json)), taskId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_ASSOC_INCLUDES : includeFields, ERROR_INCLUDE);

		SessionContextSupport.putLogParameter(EVENTNAME, CREATE_TASK_ASSOC);
		SessionContextSupport.putLogParameter(TASK_ASSOC_UID, responseDTO.getModel().getTaskAssocUid());
		SessionContextSupport.putLogParameter(TASK_DESCENDANT_ID, responseDTO.getModel().getTaskDescendant() != null ? responseDTO.getModel().getTaskDescendant().getGooruOid() : null);
		SessionContextSupport.putLogParameter(TASK_PARENT_ID, responseDTO.getModel().getTaskParent() != null ? responseDTO.getModel().getTaskParent().getGooruOid() : null);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/associate" })
	public ModelAndView getAssociatedTasks(@PathVariable(ID) String taskUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_GET_TASK_ASSOC);
		TaskAssoc taskAssoc = this.getTaskService().getTaskAssoc(taskUid);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includes = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_ASSOC_INCLUDES : includeFields, ERROR_INCLUDE);
		SessionContextSupport.putLogParameter(TASK_UID, taskUid);
		return toModelAndViewWithIoFilter(taskAssoc, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = { "/associate/{id}" })
	public void deleteTaskAssoc(@PathVariable(ID) String taskAssocUid, HttpServletRequest request, HttpServletResponse response) {
		this.getTaskService().deleteTaskAssoc(taskAssocUid);
		SessionContextSupport.putLogParameter(EVENTNAME, DELETE_TASK_ASSOC);
		SessionContextSupport.putLogParameter(TASK_ASSOC_UID, taskAssocUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TASK_MANAGEMENT_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/taskhistory" })
	public ModelAndView getTaskHistory(@PathVariable(ID) String taskUid, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TASK_GET_TASK_HISTORY);
		List<TaskHistoryItem> taskHistoryItems = this.getTaskService().getTaskHistory(taskUid);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includes = (String[]) ArrayUtils.addAll(includeFields == null ? TASK_HISTORY_ITEM_INCLUDES : includeFields, ERROR_INCLUDE);
		SessionContextSupport.putLogParameter(GOORU_OID, taskUid);
		return toModelAndViewWithIoFilter(taskHistoryItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	private Task buildTaskFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Task.class);
	}

	private TaskResourceAssoc buildTaskResourceFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, TaskResourceAssoc.class);
	}

	private TaskAssoc buildTaskAssocFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, TaskAssoc.class);
	}

	private AttachDTO buildAttachFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, AttachDTO.class);
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

}
