/*******************************************************************************
 * TaskAssoc.java
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

import org.ednovo.gooru.core.api.model.Task;

public class TaskAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3220124393263939483L;


	private String taskAssocUid;

	private Task taskParent;

	private Task taskDescendant;

	private Integer sequence;
	
	private String associationType;

	public String getTaskAssocUid() {
		return taskAssocUid;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setTaskAssocUid(String taskAssocUid) {
		this.taskAssocUid = taskAssocUid;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setTaskParent(Task taskParent) {
		this.taskParent = taskParent;
	}

	public Task getTaskParent() {
		return taskParent;
	}

	public void setTaskDescendant(Task taskDescendant) {
		this.taskDescendant = taskDescendant;
	}

	public Task getTaskDescendant() {
		return taskDescendant;
	}

	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}

	public String getAssociationType() {
		return associationType;
	}

}
