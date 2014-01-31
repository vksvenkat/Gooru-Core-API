package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Gender implements Serializable{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8265351344261628804L;
	private String genderId;
	private String name;
	
	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}