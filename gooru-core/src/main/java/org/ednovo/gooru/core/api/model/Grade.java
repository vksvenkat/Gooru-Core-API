package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Grade implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5334767026378538572L;
	
	private Short gradeId;
	private String name;
	
	public Short getGradeId() {
		return gradeId;
	}
	public void setGradeId(Short gradeId) {
		this.gradeId = gradeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
