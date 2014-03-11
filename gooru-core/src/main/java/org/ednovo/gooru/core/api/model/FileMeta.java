/*******************************************************************************
 * FileMeta.java
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
