package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ServicePartyAssoc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727669846339775744L;
	
	private String serviceKey;
	
	private String partyUid;
	
	private String partyType;
	
	private String serviceEndPoint;

	public String getServiceKey() {
		return serviceKey;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getPartyUid() {
		return partyUid;
	}

	public void setPartyUid(String partyUid) {
		this.partyUid = partyUid;
	}

	public String getPartyType() {
		return partyType;
	}

	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}

	public String getServiceEndPoint() {
		return serviceEndPoint;
	}

	public void setServiceEndPoint(String serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}
	

}
