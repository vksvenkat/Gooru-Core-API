/*******************************************************************************
 * CsvCrawlerJob.java
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

import java.util.Date;

public class CsvCrawlerJob {

	private String csvCrawlerJobId;
	private String fileNames;
	private Date createdOn;
	private Date startTime;
	private Date endTime;
	private Integer resourceCount;
	private Integer newResourceCount;
	private Integer status;
	private String destServer;
	private String crawlerName;
	private String log;

	public String getCsvCrawlerJobId() {
		return csvCrawlerJobId;
	}

	public void setCsvCrawlerJobId(String csvCrawlerJobId) {
		this.csvCrawlerJobId = csvCrawlerJobId;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getResourceCount() {
		return resourceCount;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public void setResourceCount(Integer resourceCount) {
		this.resourceCount = resourceCount;
	}

	public Integer getNewResourceCount() {
		return newResourceCount;
	}

	public void setNewResourceCount(Integer newResourceCount) {
		this.newResourceCount = newResourceCount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDestServer() {
		return destServer;
	}

	public void setDestServer(String destServer) {
		this.destServer = destServer;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getCrawlerName() {
		return crawlerName;
	}

	public void setCrawlerName(String crawlerName) {
		this.crawlerName = crawlerName;
	}

}
