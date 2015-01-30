package org.ednovo.gooru.core.api.model;

public class SearchIndexMeta {
	
	private String reIndexIds;
	
	private String action;
	
	private String sessionToken;
	
	private String type;
	
	private Boolean updateUserContent;
	
	private Boolean updateStatisticsData;

	public String getReIndexIds() {
		return reIndexIds;
	}

	public void setReIndexIds(String reIndexIds) {
		this.reIndexIds = reIndexIds;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUpdateUserContent(Boolean updateUserContent) {
		this.updateUserContent = updateUserContent;
	}

	public Boolean getUpdateUserContent() {
		return updateUserContent;
	}

	public void setUpdateStatisticsData(Boolean updateStatisticsData) {
		this.updateStatisticsData = updateStatisticsData;
	}

	public Boolean getUpdateStatisticsData() {
		return updateStatisticsData;
	}


}
