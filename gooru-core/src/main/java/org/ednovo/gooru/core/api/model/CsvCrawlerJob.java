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
