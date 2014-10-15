
package org.ednovo.gooru.core.api.model;

public class Application extends Resource {

	private static final long serialVersionUID = -2886634467039659836L;

	private String apiKey;
	
	private Integer searchLimit;
	
	private Integer limit;

	private String secretKey;
			
	private CustomTableValue status;
	
	private String comment;
	
	private String ContactEmailId;

	public String getKey() {
		return getGooruOid();
	}

	public Integer getSearchLimit() {
		return searchLimit;
	}

	public void setSearchLimit(Integer searchLimit) {
		this.searchLimit = searchLimit;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getContactEmailId() {
		return ContactEmailId;
	}

	public void setContactEmailId(String contactEmailId) {
		ContactEmailId = contactEmailId;
	}

}