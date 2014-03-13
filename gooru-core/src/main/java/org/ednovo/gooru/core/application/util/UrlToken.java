package org.ednovo.gooru.core.application.util;



public enum UrlToken {

	GET_CONFIG_SETTING("/config-setting/{0}/value?sessionToken={1}&format=raw"),
	GET_webpurify_PROFANITY("?api_key={0}&method={1}&format={2}&text={3}");

	private String url;

	private UrlToken(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
