package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ApiActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037054629090587064L;

	private Integer apiActivityId;

	private ApiKey apiKey;

	private Integer count;

	public ApiActivity() {
	}

	public Integer getApiActivityId() {
		return apiActivityId;
	}

	public void setApiActivityId(Integer apiActivityId) {
		this.apiActivityId = apiActivityId;
	}

	public ApiKey getApiKey() {
		return apiKey;
	}

	public void setApiKey(ApiKey apiKey) {
		this.apiKey = apiKey;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}