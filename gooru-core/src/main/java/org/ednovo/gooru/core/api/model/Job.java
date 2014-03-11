/*******************************************************************************
 * Job.java
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

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.User;



public class Job extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4306366441283721824L;

	public static enum Status {
		INPROGRESS("Inprogress"), COMPLETED("Completed"), FAILED("Failed");

		private String status;

		Status(String status) {
			this.status = status;
		}

		public String getStatus() {
			return this.status;
		}
	}

	private Integer jobId;

	private JobType jobType;

	private String gooruOid;

	private User user;

	private String status;

	private Integer timeToComplete;

	private Long fileSize;

	public Job() {
		jobType = new JobType();
		user = new User();
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public Integer getTimeToComplete() {
		return timeToComplete;
	}

	public void setTimeToComplete(Integer timeToComplete) {
		this.timeToComplete = timeToComplete;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

}
