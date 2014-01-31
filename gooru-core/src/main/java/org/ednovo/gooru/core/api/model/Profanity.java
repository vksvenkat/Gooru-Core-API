package org.ednovo.gooru.core.api.model;

import java.util.List;

public class Profanity {

	private String callBackUrl;

	private String text;

	private int count;

	private String apiEndPoint;

	private String token;

	private List<String> expletive;

	private Boolean isFound;

	private String foundBy;

	public List<String> getExpletive() {
		return expletive;
	}

	public void setExpletive(List<String> expletive) {
		this.expletive = expletive;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setApiEndPoint(String apiEndPoint) {
		this.apiEndPoint = apiEndPoint;
	}

	public String getApiEndPoint() {
		return apiEndPoint;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}

	public boolean isFound() {
		return isFound;
	}

	public void setFoundBy(String foundBy) {
		this.foundBy = foundBy;
	}

	public String getFoundBy() {
		return foundBy;
	}
}
