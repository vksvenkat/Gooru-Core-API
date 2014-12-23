package org.ednovo.gooru.core.api.model;

import java.io.Serializable;



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
	
	private String jobUid;
	
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
	
	public String getJobUid() {
		return jobUid;
	}

	public void setJobUid(String jobUid) {
		this.jobUid = jobUid;
	}
	
}