
package org.ednovo.gooru.core.api.model;

import java.util.List;

public class Application extends Resource {

	private static final long serialVersionUID = -2886634467039659836L;

	private String key;
	
	private Integer searchLimit;
	
	private Integer limit;

	private String secretKey;
			
	private CustomTableValue status;
	
	private String comment;
	
	private String ContactEmailId;
	
	private ApplicationItem applicationItem;
	
	private OAuthClient oauthClient;
	
    private List<OAuthClient> oauthClients;
    
    private List<ApplicationItem> applicationItems;
    
    private String refererDomains;

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

	public String getContactEmailId() {
		return ContactEmailId;
	}

	public void setContactEmailId(String contactEmailId) {
		ContactEmailId = contactEmailId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ApplicationItem getApplicationItem() {
		return applicationItem;
	}

	public void setApplicationItem(ApplicationItem applicationItem) {
		this.applicationItem = applicationItem;
	}

	public OAuthClient getOauthClient() {
		return oauthClient;
	}

	public void setOauthClient(OAuthClient oauthClient) {
		this.oauthClient = oauthClient;
	}

	public List<OAuthClient> getOauthClients() {
		return oauthClients;
	}

	public void setOauthClients(List<OAuthClient> oauthClients) {
		this.oauthClients = oauthClients;
	}

	public List<ApplicationItem> getApplicationItems() {
		return applicationItems;
	}

	public void setApplicationItems(List<ApplicationItem> applicationItems) {
		this.applicationItems = applicationItems;
	}

	public String getRefererDomains() {
		return refererDomains;
	}

	public void setRefererDomains(String refererDomains) {
		this.refererDomains = refererDomains;
	}


}