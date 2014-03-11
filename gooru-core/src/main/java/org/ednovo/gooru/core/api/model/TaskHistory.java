/*******************************************************************************
 * TaskHistory.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.User;


public class TaskHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5254419540945110174L;

	private String taskHistoryUid;
	
	private Task taskContentId;
	
	private User userUid;
	
	private Date createdDate;
	
	private Set<TaskHistoryItem> taskHistoryItems;
 	
	public void setTaskHistoryUid(String taskHistoryUid) {
		this.taskHistoryUid = taskHistoryUid;
	}

	public String getTaskHistoryUid() {
		return taskHistoryUid;
	}

	public void setUserUid(User userUid) {
		this.userUid = userUid;
	}

	public User getUserUid() {
		return userUid;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setTaskContentId(Task taskContentId) {
		this.taskContentId = taskContentId;
	}

	public Task getTaskContentId() {
		return taskContentId;
	}

	public void setTaskHistoryItems(Set<TaskHistoryItem> taskHistoryItems) {
		this.taskHistoryItems = taskHistoryItems;
	}

	public Set<TaskHistoryItem> getTaskHistoryItems() {
		return taskHistoryItems;
	}
	

}
