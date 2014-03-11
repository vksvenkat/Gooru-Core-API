/*******************************************************************************
 * TaskResourceAssoc.java
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

public class TaskResourceAssoc  implements Serializable, Comparable<TaskResourceAssoc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983792695073216861L;


	private String taskResourceAssocUid;

	private Task task;

	private Resource resource;

	private Integer sequence;

	private Date associationDate;

	private User associatedBy;

	public Integer getSequence() {
		return sequence;
	}

	public Date getAssociationDate() {
		return associationDate;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setTaskResourceAssocUid(String taskResourceAssocUid) {
		this.taskResourceAssocUid = taskResourceAssocUid;
	}

	public String getTaskResourceAssocUid() {
		return taskResourceAssocUid;
	}

	@Override
	public int compareTo(TaskResourceAssoc otherItem) {
		if (otherItem != null && getSequence() != null && otherItem.getSequence() != null && !getTaskResourceAssocUid().equals(otherItem.getTaskResourceAssocUid())) {
			if (getSequence().equals(otherItem.getSequence())) {
				return 0;
			}
			return getSequence().compareTo(otherItem.getSequence());
		}
		return 0;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

}
