package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class City implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1767890553132273348L;
	private String cityCode;
	private String cityUid;
	private String name;
	private Country country;
	private Province province;
	
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	public String getCityUid() {
		return cityUid;
	}
	public void setCityUid(String cityUid) {
		this.cityUid = cityUid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}