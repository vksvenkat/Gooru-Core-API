package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Province implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4360205019474599215L;
	private String provinceId;
	private String name;
	
	private Country country;

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
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