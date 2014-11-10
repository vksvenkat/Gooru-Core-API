package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Province implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4360205019474599215L;
	private String stateId;
	private String name;
	
	private Country country;

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
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