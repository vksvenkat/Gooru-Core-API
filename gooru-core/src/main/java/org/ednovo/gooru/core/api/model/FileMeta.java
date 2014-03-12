package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import flexjson.JSON;

public class FileMeta implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2541308845572140654L;
	private String name;
	private long size;
	private String url;
	private String deleteUrl;
	private String deleteType;
	private String originalFilename;
	private byte[] fileData;
	private Integer statusCode;
	private String uploadImageSource;
	private String imageValidationMsg;

	public FileMeta(String filename, long size, String url) {
		this.name = filename;
		this.size = size;
		this.url = url;
		this.deleteUrl = url;
		this.deleteType = "DELETE";
	}

	public FileMeta() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDeleteUrl() {
		return deleteUrl;
	}

	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}

	public String getDeleteType() {
		return deleteType;
	}

	public void setDeleteType(String deleteType) {
		this.deleteType = deleteType;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	@JSON(include = false)
	public byte[] getFileData() {
		return fileData;
	}

	@JSON(include = false)
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public Integer getstatusCode() {
		return statusCode;
	}

	public void setstatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getUploadImageSource() {
		return uploadImageSource;
	}

	public void setUploadImageSource(String uploadImageSource) {
		this.uploadImageSource = uploadImageSource;
	}

	public String getImageValidationMsg() {
		return imageValidationMsg;
	}

	public void setImageValidationMsg(String imageValidationMsg) {
		this.imageValidationMsg = imageValidationMsg;
	}

	

}