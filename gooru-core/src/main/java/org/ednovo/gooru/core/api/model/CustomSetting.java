package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class CustomSetting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3831080407525564131L;

	private Integer customId;

	private String key;

	private Integer value;

	public Integer getCustomId() {
		return customId;
	}

	public void setCustomId(Integer customId) {
		this.customId = customId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
