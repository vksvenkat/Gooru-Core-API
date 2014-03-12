package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class CustomTableValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4427206959203260874L;

	private Integer customTableValueId;

	private CustomTable customTable;

	private String value;
	
	private String displayName;
	
	private String keyValue;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setCustomTableValueId(Integer customTableValueId) {
		this.customTableValueId = customTableValueId;
	}

	public Integer getCustomTableValueId() {
		return customTableValueId;
	}

	public void setCustomTable(CustomTable customTable) {
		this.customTable = customTable;
	}

	public CustomTable getCustomTable() {
		return customTable;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getKeyValue() {
		return keyValue;
	}


}
