/*******************************************************************************
 * Task.java
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

import org.ednovo.gooru.core.api.model.Content;


public class Task extends Content implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3873670928088767955L;

	private String title;
	
	private String description;
	
	private Date plannedStartDate;
	
	private Date plannedEndDate;
	
	private String status;
	
	private String typeName;
	
	private Double estimatedEffort;
	
	private Set<TaskResourceAssoc> taskResourceAssocs;
	
	
	public Task copy() {
		try {
			return (Task) this.clone();
		} catch (CloneNotSupportedException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getPlannedStartDate() {
		return plannedStartDate;
	}

	public Date getPlannedEndDate() {
		return plannedEndDate;
	}

	public String getStatus() {
		return status;
	}

	public String getTypeName() {
		return typeName;
	}

	public Double getEstimatedEffort() {
		return estimatedEffort;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPlannedStartDate(Date plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	public void setPlannedEndDate(Date plannedEndDate) {
		this.plannedEndDate = plannedEndDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setEstimatedEffort(Double estimatedEffort) {
		this.estimatedEffort = estimatedEffort;
	}

	public void setTaskResourceAssocs(Set<TaskResourceAssoc> taskResourceAssocs) {
		this.taskResourceAssocs = taskResourceAssocs;
	}

	public Set<TaskResourceAssoc> getTaskResourceAssocs() {
		return taskResourceAssocs;
	}


}
