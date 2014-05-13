package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentMetaDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8689834743874678974L;
	
	private Boolean selected;
	
	private String value;


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

}
