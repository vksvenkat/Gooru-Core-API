package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Country implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1775302177846507373L;
	private String countryId;
	private String name;
	
	
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}