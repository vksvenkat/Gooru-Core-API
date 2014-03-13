package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.List;

public class ConverterDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7215391960356365839L;

	private List<String> resourcePaths;

	private String sourcePath;

	private String gooruContentId;

	private String segmentId;

	private Integer jobId;

	private String learnGuideString;

	private String operationType;

	private long startTime;

	private String fileType = "png";

	private ResourceDTO resource;

	private String resourcePath;

	private boolean success;

	public ConverterDTO() {

	}

	public List<String> getResourcePaths() {
		return resourcePaths;
	}

	public void setResourcePaths(List<String> resourcePaths) {
		this.resourcePaths = resourcePaths;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getGooruContentId() {
		return gooruContentId;
	}

	public void setGooruContentId(String gooruContentId) {
		this.gooruContentId = gooruContentId;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getLearnGuideString() {
		return learnGuideString;
	}

	public void setLearnGuideString(String learnGuideString) {
		this.learnGuideString = learnGuideString;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public ResourceDTO getResource() {
		return resource;
	}

	public void setResource(ResourceDTO resource) {
		this.resource = resource;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileType() {
		return fileType;
	}

}
