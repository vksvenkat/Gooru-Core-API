/////////////////////////////////////////////////////////////
// TaskServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
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
package org.ednovo.gooru.domain.service.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AttachDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.TaskAssoc;
import org.ednovo.gooru.core.api.model.TaskHistory;
import org.ednovo.gooru.core.api.model.TaskHistoryItem;
import org.ednovo.gooru.core.api.model.TaskResourceAssoc;
import org.ednovo.gooru.core.api.model.TaskUserAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class TaskServiceImpl extends BaseServiceImpl implements TaskService, ParameterProperties {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	@javax.annotation.Resource(name = "userService")
	private UserService userService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ActionResponseDTO<Task> createTask(Task task, String endDate, User user, AttachDTO attachDTO) throws Exception {
		Date plannedEndDate = null;
		if (endDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			plannedEndDate = dateFormat.parse(endDate);
		}
		Errors errors = this.taskValidation(task, plannedEndDate);
		if (!errors.hasErrors()) {
			task.setGooruOid(UUID.randomUUID().toString());
			task.setSharing(Sharing.PRIVATE.getSharing());
			ContentType contentType = getCollectionService().getContentType(ContentType.TASK);
			task.setContentType(contentType);
			task.setCreator(user);
			task.setLastModified(new Date(System.currentTimeMillis()));
			task.setCreatedOn(new Date(System.currentTimeMillis()));
			task.setUser(user);
			task.setOrganization(user.getPrimaryOrganization());
			task.setLastUpdatedUserUid(user.getGooruUId());
			task.setCreatedOn(new Date());
			if (plannedEndDate != null) {
				task.setPlannedEndDate(plannedEndDate);
			}
			this.getTaskRepository().save(task);
			if (attachDTO != null) {
				rejectIfNull(attachDTO.getId(), GL0006,ID);
				rejectIfNull(attachDTO.getType(),GL0006,TYPE);
				attachTaskToCollection(attachDTO, task, user);
			}
		}

		return new ActionResponseDTO<Task>(task, errors);
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	@Override
	public ActionResponseDTO<CollectionTaskAssoc> createCollectionTaskAssoc(CollectionTaskAssoc collectionTaskAssoc, User user) throws Exception {
		Task task = this.getTaskRepository().getTask(collectionTaskAssoc.getTask().getGooruOid());
		Errors errors = null;
		if (task != null) {
			Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionTaskAssoc.getCollection().getGooruOid(), null);

			collectionTaskAssoc.setCollection(collection);
			collectionTaskAssoc.setTask(task);
			collectionTaskAssoc.setAssociationDate(new Date(System.currentTimeMillis()));
			collectionTaskAssoc.setAssociatedBy(user);
			int sequence = collectionTaskAssoc.getCollection().getCollectionTaskItems() != null ? collectionTaskAssoc.getCollection().getCollectionTaskItems().size() + 1 : 1;
			collectionTaskAssoc.setSequence(sequence);
			this.getTaskRepository().save(collectionTaskAssoc);
			errors = new BindException(collectionTaskAssoc, COLLECTION_TASK_ASSOC);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK), GL0056);
		}
		return new ActionResponseDTO<CollectionTaskAssoc>(collectionTaskAssoc, errors);
	}

	@Override
	public ActionResponseDTO<Task> updateTask(String gooruOid, Task newTask, String endDate, User user) throws Exception {
		Date plannedEndDate = null;
		if (endDate != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			plannedEndDate = dateFormat.parse(endDate);
		}
		Task task = this.getTaskRepository().getTask(gooruOid);
		Task oldTask = task.copy();

		Errors errors = this.updateTaskValidation(task, plannedEndDate);
		if (!errors.hasErrors()) {

			if (newTask.getTitle() != null) {
				task.setTitle(newTask.getTitle());
			}
			if (newTask.getDescription() != null) {
				task.setDescription(newTask.getDescription());
			}
			if (newTask.getStatus() != null) {
				task.setStatus(newTask.getStatus());
			}
			if (newTask.getEstimatedEffort() != null) {
				task.setEstimatedEffort(newTask.getEstimatedEffort());
			}
			if (plannedEndDate != null) {
				task.setPlannedEndDate(plannedEndDate);
			}
			task.setLastUpdatedUserUid(user.getUserUid());
			boolean addToTaskHistory = false;
			if (!EqualsBuilder.reflectionEquals(task, oldTask)) {
				addToTaskHistory = true;
			}
			this.getTaskRepository().save(task);

			if (addToTaskHistory) {
				createTaskHistory(oldTask, task, user);
			}

		}
		return new ActionResponseDTO<Task>(task, errors);
	}

	private void createTaskHistory(Task task, Task newTask, User user) {
		TaskHistory taskHistory = new TaskHistory();
		taskHistory.setCreatedDate(new Date());
		taskHistory.setTaskContentId(newTask);
		taskHistory.setUserUid(user);
		this.getTaskRepository().save(taskHistory);
		createTaskHistoryItem(task, newTask, taskHistory);
	}

	private void createTaskHistoryItem(Task task, Task newTask, TaskHistory taskHistory) {
		List<TaskHistoryItem> taskHistoryItems = new ArrayList<TaskHistoryItem>();
		if (task != null && newTask != null) {
			if (task.getTitle() != null && !task.getTitle().equals(newTask.getTitle()) && newTask.getTitle() != null) {
				taskHistoryItems.add(createTaskHistoryItemInstance(TITLE, null, task.getTitle(), taskHistory, null, newTask.getTitle()));
			}
			if (task.getDescription() != null && !task.getDescription().equals(newTask.getDescription()) && newTask.getDescription() != null) {
				taskHistoryItems.add(createTaskHistoryItemInstance(DESCRIPTION, null, task.getDescription(), taskHistory, null, newTask.getDescription()));
			}
			if (task.getStatus() != null && !task.getStatus().equals(newTask.getStatus()) && newTask.getStatus() != null) {
				taskHistoryItems.add(createTaskHistoryItemInstance(STATUS, null, task.getStatus(), taskHistory, null, newTask.getStatus()));
			}
			if (task.getEstimatedEffort() != null && !task.getEstimatedEffort().equals(newTask.getEstimatedEffort()) && newTask.getEstimatedEffort() != null) {
				taskHistoryItems.add(createTaskHistoryItemInstance(ESTIMATEDEFFORT, null, task.getEstimatedEffort().toString(), taskHistory, null, newTask.getEstimatedEffort().toString()));
			}
			if (task.getPlannedEndDate() != null && newTask.getPlannedEndDate() != null && (task.getPlannedEndDate().before(newTask.getPlannedEndDate()) || task.getPlannedEndDate().after(newTask.getPlannedEndDate()))) {
				taskHistoryItems.add(createTaskHistoryItemInstance(PLANNEDENDDATE, null, task.getPlannedEndDate().toString(), taskHistory, null, newTask.getPlannedEndDate().toString()));
			}
			this.getTaskRepository().saveAll(taskHistoryItems);
		}
	}

	private TaskHistoryItem createTaskHistoryItemInstance(String fieldName, String oldKey, String oldValue, TaskHistory taskHistory, String newKey, String newValue) {
		TaskHistoryItem taskHistoryItem = new TaskHistoryItem();
		taskHistoryItem.setFieldName(fieldName);
		taskHistoryItem.setTaskHistory(taskHistory);
		taskHistoryItem.setOldKey(oldKey);
		taskHistoryItem.setOldValue(oldValue);
		taskHistoryItem.setNewKey(newKey);
		taskHistoryItem.setNewValue(newValue);
		return taskHistoryItem;
	}

	@Override
	public Task getTask(String gooruOid) {
		return this.getTaskRepository().getTask(gooruOid);
	}

	@Override
	public Map<String, Object> getTasks(Integer limit, Integer offset, User user, Boolean skipPagination, String taskGooruOid, String classpageId) {
		if (userService.isContentAdmin(user)) {
			List<CollectionTaskAssoc> collectionTaskAssocs = this.getTaskRepository().getCollectionTaskAssoc(offset, limit, skipPagination, taskGooruOid, classpageId);
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(COLLECTION_TASKS, collectionTaskAssocs);
			result.put(TOTAL_COLLECTION_COUNT, this.getTaskRepository().getTaskResourceCount(taskGooruOid, null));
			result.put(TOTAL_HIT_COUNT, this.getTaskRepository().getTaskCollectionCount(taskGooruOid, classpageId));
			if (collectionTaskAssocs.size() > 0) {
				for (CollectionTaskAssoc collectionTaskAssoc : collectionTaskAssocs) {
					collectionTaskAssoc.setCollectionIds(this.getTaskRepository().getTaskResourceAssocs(collectionTaskAssoc.getTask().getGooruOid()));
				}
			}
			return result;
		} else {
			throw new UnauthorizedException("you don't have permission");
		}
	}

	@Override
	public void deleteTask(String gooruOid) {
		Task task = this.getTaskRepository().getTask(gooruOid);
		if (task != null) {
			this.getTaskRepository().remove(task);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK));
		}
	}

	@Override
	public void deleteCollectionTaskAssoc(String collectionId, String collectionTaskAssocId) {
		CollectionTaskAssoc collectionTaskAssoc = this.getTaskRepository().getCollectionTaskAssoc(collectionId, collectionTaskAssocId);
		if (collectionTaskAssoc != null) {
			this.getTaskRepository().remove(collectionTaskAssoc);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, COLLECTION_TASK_ASSOC));
		}
	}

	@Override
	public CollectionTaskAssoc getCollectionTaskAssoc(String collectionId, String collectionTaskAssocId) {
		return this.getTaskRepository().getCollectionTaskAssoc(collectionId, collectionTaskAssocId);
	}

	@Override
	public List<CollectionTaskAssoc> getCollectionTaskAssocs(String collectionId, String offset, String limit, String skipPagination, String orderBy) {
		return this.getTaskRepository().getCollectionTaskAssocs(collectionId, offset, limit, skipPagination, orderBy);
	}

	@Override
	public ActionResponseDTO<TaskResourceAssoc> createTaskResourceAssociation(TaskResourceAssoc taskResourceAssoc, User user, String gooruOid) throws Exception {
		Task task = this.getTaskRepository().getTask(gooruOid);
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(taskResourceAssoc.getResource().getGooruOid());
		Errors errors = this.taskResourceAssocValidation(taskResourceAssoc, task, resource);
		TaskResourceAssoc newTaskResourceAssoc = this.getTaskRepository().getTaskResourceAssocById(gooruOid, taskResourceAssoc.getResource().getGooruOid());
		if (newTaskResourceAssoc != null) {
			throw new RuntimeException("Resource already associated");
		}
		if (!errors.hasErrors()) {
			if (resource.getSharing().equals(Sharing.PRIVATE.getSharing())) {
				resource.setSharing(Sharing.ANYONEWITHLINK.getSharing());
				getCollectionService().updateResourceSharing(Sharing.ANYONEWITHLINK.getSharing(), (Collection) resource);
			}
			taskResourceAssoc.setAssociatedBy(user);
			taskResourceAssoc.setResource(resource);
			taskResourceAssoc.setTask(task);
			int sequence = taskResourceAssoc.getTask().getTaskResourceAssocs() != null ? taskResourceAssoc.getTask().getTaskResourceAssocs().size() + 1 : 1;
			taskResourceAssoc.setSequence(sequence);
			taskResourceAssoc.setAssociationDate(new Date());
			this.getTaskRepository().save(taskResourceAssoc);
		}
		return new ActionResponseDTO<TaskResourceAssoc>(taskResourceAssoc, errors);
	}

	@Override
	public TaskResourceAssoc getTaskResourceAssociatedItem(String resourceAssocId) {
		return this.getTaskRepository().getTaskResourceAssociatedItemId(null, resourceAssocId);
	}

	@Override
	public List<Resource> getTaskResourceAssociatedItems(String gooruOid, Integer offset, Integer limit, String skipPagination, String orderBy, String sharing) {
		return this.getTaskRepository().getTaskResourceAssociatedByTaskId(gooruOid, offset, limit, skipPagination, orderBy, sharing);
	}

	@Override
	public void deleteTaskResourceAssociatedItem(String taskUid, String taskResourceAssocUid) {
		TaskResourceAssoc taskResourceAssoc = this.getTaskRepository().getTaskResourceAssociatedItemId(taskUid, taskResourceAssocUid);
		if (taskResourceAssoc != null) {
			this.getTaskRepository().remove(taskResourceAssoc);
		} else {
			throw new RuntimeException(generateErrorMessage(GL0056, TASK_RESOURCE_ASSOC));
		}
	}

	@Override
	public void deleteTaskResourceAssocItem(String taskUid, String resourceId) {
		List<TaskResourceAssoc> taskResourceAssocs = this.getTaskRepository().getTaskResourceId(taskUid, resourceId);
		if (taskResourceAssocs.size() > 0) {
			for (TaskResourceAssoc taskResourceAssoc : taskResourceAssocs) {
				this.getTaskRepository().remove(taskResourceAssoc);
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK_RESOURCE_ASSOC), GL0056);
		}
	}

	@Override
	public ActionResponseDTO<TaskAssoc> createTaskAssoc(TaskAssoc taskAssoc, String gooruOid, User user) throws Exception {

		Task taskParent = this.getTaskRepository().getTask(gooruOid);
		Task taskDescendant = this.getTaskRepository().getTask(taskAssoc.getTaskDescendant().getGooruOid());
		Errors errors = this.taskAssocValidation(taskAssoc, taskParent, taskDescendant);
		if (!errors.hasErrors()) {
			taskAssoc.setTaskParent(taskParent);
			taskAssoc.setTaskDescendant(taskDescendant);
			this.getTaskRepository().save(taskAssoc);
		}
		return new ActionResponseDTO<TaskAssoc>(taskAssoc, errors);
	}

	@Override
	public TaskAssoc getTaskAssoc(String taskId) {

		return this.getTaskRepository().getTaskAssocByUid(taskId);
	}

	@Override
	public void deleteTaskAssoc(String taskAssocUid) {

		TaskAssoc taskAssoc = this.getTaskRepository().getTaskAssocByUid(taskAssocUid);
		if (taskAssoc != null) {
			this.getTaskRepository().remove(taskAssoc);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK_ASSOC), GL0056);
		}

	}

	@Override
	public void deleteTaskUserAssoc(String taskUid, String userUid) {
		TaskUserAssoc task = this.getTaskRepository().findByTaskUid(taskUid, userUid);
		if (task != null) {
			this.getTaskRepository().remove(task);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK_USER_ASSOC), GL0056);
		}

	}

	@Override
	public List<TaskUserAssoc> createTaskUserAssoc(String gooruOid, String userList, String associationType) {
		Task task = this.getTaskRepository().getTask(gooruOid);
		List<TaskUserAssoc> taskUserAssocList = new ArrayList<TaskUserAssoc>();
		if (task != null) {
			String[] gooruUid = userList.split(",");
			for (int i = 0; i < gooruUid.length; i++) {
				User user = this.getUserRepository().findByGooruId(gooruUid[i]);
				if (user != null) {
					TaskUserAssoc taskUserAssoc = new TaskUserAssoc();
					taskUserAssoc.setUser(user);
					taskUserAssoc.setTask(task);
					if (associationType != null && (associationType.equalsIgnoreCase(ASSIGNEE) || associationType.equalsIgnoreCase(CREATOR) || associationType.equalsIgnoreCase(REVIEWER))) {
						taskUserAssoc.setAssociationType(associationType);
					}
					taskUserAssocList.add(taskUserAssoc);
					this.getTaskRepository().save(taskUserAssoc);
				}
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, TASK), GL0056);
		}
		return taskUserAssocList;
	}

	@Override
	public ActionResponseDTO<TaskResourceAssoc> reorderResourceAssociatedItem(String taskUid, String taskResourceAssocId, int sequence) throws Exception {
		TaskResourceAssoc taskResourceAssoc = getTaskRepository().getTaskResourceAssociatedItemId(null, taskResourceAssocId);
		Errors errors = this.validateReorderTaskResourceItem(taskResourceAssoc);
		if (!errors.hasErrors()) {
			Task task = this.getTaskRepository().getTask(taskResourceAssoc.getTask().getGooruOid());

			Integer existTaskResourceItemSequence = taskResourceAssoc.getSequence();

			if (existTaskResourceItemSequence > sequence) {
				for (TaskResourceAssoc ci : task.getTaskResourceAssocs()) {

					if (ci.getSequence() >= sequence && ci.getSequence() <= existTaskResourceItemSequence && ci.getTaskResourceAssocUid().equalsIgnoreCase(taskResourceAssoc.getTaskResourceAssocUid())) {
							if (ci.getTaskResourceAssocUid().equalsIgnoreCase(taskResourceAssoc.getTaskResourceAssocUid())) {
								ci.setSequence(sequence);
							} else {
								ci.setSequence(ci.getSequence() + 1);
							}
					}
				}

			} else if (existTaskResourceItemSequence < sequence) {
				for (TaskResourceAssoc ci : task.getTaskResourceAssocs()) {
					if (ci.getSequence() <= sequence && existTaskResourceItemSequence <= ci.getSequence()) {
							if (ci.getTaskResourceAssocUid().equalsIgnoreCase(taskResourceAssoc.getTaskResourceAssocUid())) {
								if (task.getTaskResourceAssocs().size() < sequence) {
									ci.setSequence(task.getTaskResourceAssocs().size());
								} else {
									ci.setSequence(sequence);
								}
							} else {
								ci.setSequence(ci.getSequence() - 1);
							}
					}
				}
			}
			this.getCollectionRepository().save(task);
		}
		return new ActionResponseDTO<TaskResourceAssoc>(taskResourceAssoc, errors);
	}

	@Override
	public void attachTaskToCollection(AttachDTO attachDTO, Task task, User user) throws Exception {
		CollectionTaskAssoc collectionTaskAssoc = new CollectionTaskAssoc();
		collectionTaskAssoc.setTask(task);
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(attachDTO.getId(), null);
		if (attachDTO.getType() != null && collection != null && collection.getCollectionType().equalsIgnoreCase(attachDTO.getType())) {
			collectionTaskAssoc.setCollection(collection);
		}
		this.createCollectionTaskAssoc(collectionTaskAssoc, user);
	}

	@Override
	public void deleteCollectionAssocInAssignment(String collectionId) {
		this.getTaskRepository().deleteCollectionAssocInAssignment(collectionId);
	}

	private Errors validateReorderTaskResourceItem(TaskResourceAssoc taskResourceAssoc) throws Exception {
		final Errors errors = new BindException(taskResourceAssoc, TASK_RESOURCE_ASSOC);
		rejectIfNull(errors, taskResourceAssoc, TASK_RESOURCE_ASSOC, GL0056, generateErrorMessage(GL0056, TASK_RESOURCE_ASSOC));
		return errors;
	}

	private Errors taskAssocValidation(TaskAssoc taskAssoc, Task taskDescendant, Task taskParent) {
		Errors errors = new BindException(taskAssoc, TASK_ASSOC);
		rejectIfNull(errors, taskParent, TASK_PARENT, GL0056, generateErrorMessage(GL0056, TASK_PARENT));
		rejectIfNull(errors, taskDescendant, TASK_DESCENDANT, GL0056, generateErrorMessage(GL0056, TASK_DESCENDANT));
		return errors;
	}

	private Errors taskValidation(Task task, Date plannedEndDate) {
		Map<String, String> taskType = new HashMap<String, String>();
		taskType.put(ASSIGNMENT, TASK_TYPE);
		taskType.put(PROJECT, TASK_TYPE);
		taskType.put(MODULE, TASK_TYPE);
		Errors errors = new BindException(task, TASK);
		rejectIfNullOrEmpty(errors, task.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		rejectIfInvalidType(errors, task.getTypeName(), TYPE_NAME, GL0007, generateErrorMessage(GL0007, TYPE_NAME), taskType);
		return errors;
	}

	private Errors updateTaskValidation(Task task, Date plannedEndDate) {
		Errors errors = new BindException(task, TASK);
		rejectIfNull(errors, task, TASK, GL0056, generateErrorMessage(GL0056, TASK));
		return errors;
	}

	private Errors taskResourceAssocValidation(TaskResourceAssoc taskResourceAssoc, Task task, Resource resource) {
		Errors errors = new BindException(taskResourceAssoc, TASK_RESOURCE_ASSOC);
		rejectIfNull(errors, task, TASK, GL0056, generateErrorMessage(GL0056, TASK));
		rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
		return errors;
	}

	@Override
	public List<Map<Object, Object>> getCollectionClasspageAssoc(String collectionId) {
		return this.getTaskRepository().getCollectionClasspageAssoc(collectionId, null);

	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	@Override
	public List<TaskHistoryItem> getTaskHistory(String taskUid) {
		return this.getTaskRepository().getTaskHistory(taskUid);
	}

	@Override
	public Long getCollectionTaskCount(String collectionGooruOid) {
		return this.getTaskRepository().getCollectionTaskCount(collectionGooruOid);
	}


}
