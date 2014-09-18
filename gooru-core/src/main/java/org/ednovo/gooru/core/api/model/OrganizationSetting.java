package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import javax.persistence.Entity;



@Entity(name="organizationSetting")
public class OrganizationSetting extends OrganizationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9059046085899109918L;
	
	private int organizationSettingId;
	
	private String key;
	
	private String value;

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setOrganizationSettingId(int organizationSettingId) {
		this.organizationSettingId = organizationSettingId;
	}

	public int getOrganizationSettingId() {
		return organizationSettingId;
	}

}
