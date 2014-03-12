package org.ednovo.gooru.core.application.util.formatter;

import java.io.Serializable;
import java.util.List;

public class FilterSubjectFo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6647008258393283409L;
	
	private List<String> subject;
	
	private List<String> gradeLevel;
	
	private List<String> category;
	
	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(List<String> subject) {
		this.subject = subject;
	}

	public List<String> getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(List<String> gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	
}
