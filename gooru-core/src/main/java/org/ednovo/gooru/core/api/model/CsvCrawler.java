package org.ednovo.gooru.core.api.model;

import java.util.Date;

public class CsvCrawler {

	private String csvCrawlerId;

	private String title;

	private String url;

	private String subject;

	private String attribution;

	private String tags;

	private String description;

	private String thumbnail;

	private String type;

	private String grade;

	private String sourceFile;
	
	private String csvFileCode;

	private String content;
	
	private String importMode;

	private Date lastModified;

	private int uploadStatus;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getCsvCrawlerId() {
		return csvCrawlerId;
	}

	public void setCsvCrawlerId(String csvCrawlerId) {
		this.csvCrawlerId = csvCrawlerId;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public int getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCsvFileCode() {
		return csvFileCode;
	}

	public void setCsvFileCode(String csvFileCode) {
		this.csvFileCode = csvFileCode;
	}

	public void setImportMode(String importMode) {
		this.importMode = importMode;
	}

	public String getImportMode() {
		return importMode;
	}
	
}
