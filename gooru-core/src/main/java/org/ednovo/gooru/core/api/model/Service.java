package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Service implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2067064993860657983L;
	
	private String serviceKey;
	
	private String serviceDisplayName;
	
	private String status;
	
	private String version;

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getServiceDisplayName() {
		return serviceDisplayName;
	}

	public void setServiceDisplayName(String serviceDisplayName) {
		this.serviceDisplayName = serviceDisplayName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
 