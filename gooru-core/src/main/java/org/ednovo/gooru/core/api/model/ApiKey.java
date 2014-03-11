/*******************************************************************************
 * ApiKey.java
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

import javax.persistence.Entity;
@Entity(name="apiKey")
public class ApiKey extends OrganizationModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2886634467039659836L;

	private Integer apiKeyId;

	private String key;

	private Integer limit;

	private Integer activeFlag;

	private Integer searchLimit;

	private String secretKey;
	
	private String appName;
	
	private String appURL;
	
	private String description;

	private CustomSetting customSetting;
	
	private Date lastUpdatedDate;
	
	private String lastUpdatedUserUid;
	
	private String status;
	
	private String comment;

	public ApiKey() {
	}

	public Integer getApiKeyId() {
		return apiKeyId;
	}

	public void setApiKeyId(Integer apiKeyId) {
		this.apiKeyId = apiKeyId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getSearchLimit() {
		return searchLimit;
	}

	public void setSearchLimit(Integer searchLimit) {
		this.searchLimit = searchLimit;
	}

	public CustomSetting getCustomSetting() {
		return customSetting;
	}

	public void setCustomSetting(CustomSetting customSetting) {
		this.customSetting = customSetting;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppURL(String appURL) {
		this.appURL = appURL;
	}

	public String getAppURL() {
		return appURL;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedUserUid(String lastUpdatedUserUid) {
		this.lastUpdatedUserUid = lastUpdatedUserUid;
	}

	public String getLastUpdatedUserUid() {
		return lastUpdatedUserUid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
