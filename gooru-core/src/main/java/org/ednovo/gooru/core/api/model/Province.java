package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Province implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4360205019474599215L;
	private String stateCode;
	private String stateUid;
	private String name;
	private Country country;
	
	public String getStateUid() {
		return stateUid;
	}

	public void setStateUid(String stateUid) {
		this.stateUid = stateUid;
	}
	
	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}


}