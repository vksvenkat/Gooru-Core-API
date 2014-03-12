package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Language implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 237791443114790640L;
	

	private String languageId;
	private String name;
	
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
