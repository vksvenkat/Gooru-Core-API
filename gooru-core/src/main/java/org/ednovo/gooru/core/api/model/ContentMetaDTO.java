package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentMetaDTO implements Serializable {

	/**
	 * 
	 */
	public ContentMetaDTO() {

	}

	public ContentMetaDTO(String keyValue, String value, Boolean selected) {
		this.setKeyValue(keyValue);
		this.setValue(value);
		this.setSelected(selected);
	}

	private static final long serialVersionUID = -8689834743874678974L;

	private Boolean selected;

	private String value;

	private String keyValue;

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getKeyValue() {
		return keyValue;
	}

}
