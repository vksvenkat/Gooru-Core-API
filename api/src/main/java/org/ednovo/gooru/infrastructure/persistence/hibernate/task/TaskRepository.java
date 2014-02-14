/////////////////////////////////////////////////////////////
// TaskRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.task;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.TaskAssoc;
import org.ednovo.gooru.core.api.model.TaskHistoryItem;
import org.ednovo.gooru.core.api.model.TaskResourceAssoc;
import org.ednovo.gooru.core.api.model.TaskUserAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface TaskRepository extends BaseRepository {

	Task getTask(String taskUid);

	Task getTask(String taskUid, String gooruUid);

	List<Task> getTasks(Integer offset, Integer limit, Boolean skipPagination);

	CollectionTaskAssoc getCollectionTaskAssoc(String collectionId, String collectionTaskAssocId);
	
	List<Map<Object, Object>> getCollectionClasspageAssoc(String collectionId, String gooruUid);
	
	void deleteCollectionAssocInAssignment(String collectionId);
	
	List<CollectionTaskAssoc> getCollectionTaskAssocs(String collectionId, String offset, String limit, String skipPagination, String orderBy);
	
	TaskResourceAssoc getTaskResourceAssocById(String taskUid,String gooruOid);

	TaskUserAssoc findByTaskUid(String taskUid,String userUid);

	TaskResourceAssoc getTaskResourceAssociatedItemId(String taskUid,String resourceAssocId);
	
	List<TaskResourceAssoc> getTaskResourceId(String taskUid,String resourceId);
	
	List<Resource> getTaskResourceAssociatedByTaskId(String gooruOid, Integer offset, Integer limit, String skipPagination, String orderBy,String sharing);

	TaskAssoc getTaskAssocByUid(String taskAssocUid);
	
	List<TaskHistoryItem> getTaskHistory(String taskUid);
	
	Long getTaskResourceCount(String taskGooruOid,String sharing);
	
	Long getTaskCollectionCount(String taskGooruOid , String classpageId );
	
	Long getCollectionTaskCount(String collectionGooruOid);

	List<CollectionTaskAssoc> getCollectionTaskAssoc(Integer offset, Integer limit, Boolean skipPagination,String taskGooruOid , String classpageId);

	List<String> getTaskResourceAssocs(String taskGooruOid);
	
	
	
}

