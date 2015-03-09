package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Country implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1775302177846507373L;
	private String countryCode;
	private String countryUid;
	private String name;
	
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryUid() {
		return countryUid;
	}
	public void setCountryUid(String countryUid) {
		this.countryUid = countryUid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}