/////////////////////////////////////////////////////////////
// TaskService.java
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

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AttachDTO;
import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.TaskAssoc;
import org.ednovo.gooru.core.api.model.TaskHistoryItem;
import org.ednovo.gooru.core.api.model.TaskResourceAssoc;
import org.ednovo.gooru.core.api.model.TaskUserAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.BaseService;

public interface TaskService extends BaseService {

	ActionResponseDTO<CollectionTaskAssoc> createCollectionTaskAssoc(CollectionTaskAssoc collectionTaskAssoc, User user) throws Exception;

	ActionResponseDTO<Task> createTask(Task task, String plannedEndDate, User user, AttachDTO attachDTO) throws Exception;

	ActionResponseDTO<Task> updateTask(String taskUid, Task task,String endDate, User user ) throws Exception;
	
	ActionResponseDTO<TaskAssoc> createTaskAssoc(TaskAssoc taskAssoc,String taskId, User user) throws Exception;
	
	TaskAssoc getTaskAssoc(String taskId);
	
	void deleteTaskAssoc(String taskAssocUid);
	
	Map<String,Object> getTasks(Integer offset, Integer limit, User user,Boolean skipPagination,String taskGooruOid , String classpageId);

	Task getTask(String taskUid);

	void deleteTask(String taskUid);

	CollectionTaskAssoc getCollectionTaskAssoc(String collectionId, String collectionTaskAssocId);
	
	List<Map<Object, Object>> getCollectionClasspageAssoc(String collectionId);
	
	void deleteCollectionAssocInAssignment(String collectionId);
	
	void deleteCollectionTaskAssoc(String collectionId, String collectionTaskAssocId);

	List<CollectionTaskAssoc> getCollectionTaskAssocs(String collectionId, String offset, String limit, String skipPagination, String orderBy);

	ActionResponseDTO<TaskResourceAssoc> createTaskResourceAssociation(TaskResourceAssoc taskResourceAssoc, User user, String taskUid) throws Exception;

	List<TaskUserAssoc> createTaskUserAssoc(String taskUId, String gooruUid,String associationType) throws Exception;

	void deleteTaskUserAssoc(String taskUid, String userUid);

	TaskResourceAssoc getTaskResourceAssociatedItem(String resourceAssocId);

	List<Resource> getTaskResourceAssociatedItems(String gooruOid, Integer offset, Integer limit, String skipPagination, String orderBy,String sharing);

	void deleteTaskResourceAssociatedItem(String taskUid, String taskResourceAssocUid);
	
	void deleteTaskResourceAssocItem(String taskUid, String resourceId);
	
	ActionResponseDTO<TaskResourceAssoc> reorderResourceAssociatedItem(String taskUid, String taskResourceAssocId, int sequence) throws Exception;
	
	void attachTaskToCollection(AttachDTO attachDTO, Task task, User user) throws Exception;
	
	List<TaskHistoryItem> getTaskHistory(String taskUid);
	
	Long getCollectionTaskCount(String collectionGooruOid);

}
