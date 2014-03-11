/*******************************************************************************
 * ConverterDTO.java
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
