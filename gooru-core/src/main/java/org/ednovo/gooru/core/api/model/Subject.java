package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Subject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6184925958268911061L;
	/**
	 * 
	 */

	private Short subjectId;
	private String name;
	
	
	public Short getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Short subjectId) {
		this.subjectId = subjectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
