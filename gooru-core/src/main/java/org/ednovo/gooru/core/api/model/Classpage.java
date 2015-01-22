package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Classpage extends Collection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360758388302848819L;

	private String classpageCode;

	public void setClasspageCode(String classpageCode) {
		this.classpageCode = classpageCode;
	}

	public String getClasspageCode() {
		return classpageCode;
	}

}
