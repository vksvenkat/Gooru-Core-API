package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ServicePartyAssoc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727669846339775744L;
	
	private String ServicePartyAssocUid;
	
	private Service service;
	
	private String partyUid;
	
	private String partyType;
	
	private String serviceEndPoint;


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

	public void setServicePartyAssocUid(String servicePartyAssocUid) {
		ServicePartyAssocUid = servicePartyAssocUid;
	}

	public String getServicePartyAssocUid() {
		return ServicePartyAssocUid;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Service getService() {
		return service;
	}
	

}
