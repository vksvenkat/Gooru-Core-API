/*******************************************************************************
 * TaskHistoryItem.java
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

public class TaskHistoryItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7839153683200765228L;

	private String taskHistoryItemUid;
	
	private TaskHistory taskHistory;
	
	private String fieldName;
	
	private String oldKey;
	
	private String oldValue;
	
	private String newKey;
	
	private String newValue;

	public void setTaskHistoryItemUid(String taskHistoryItemUid) {
		this.taskHistoryItemUid = taskHistoryItemUid;
	}

	public String getTaskHistoryItemUid() {
		return taskHistoryItemUid;
	}

	public void setTaskHistory(TaskHistory taskHistory) {
		this.taskHistory = taskHistory;
	}

	public TaskHistory getTaskHistory() {
		return taskHistory;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setOldKey(String oldKey) {
		this.oldKey = oldKey;
	}

	public String getOldKey() {
		return oldKey;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setNewKey(String newKey) {
		this.newKey = newKey;
	}

	public String getNewKey() {
		return newKey;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getNewValue() {
		return newValue;
	}



}
