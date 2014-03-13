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