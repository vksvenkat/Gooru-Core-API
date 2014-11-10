package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserClassification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -349689314340064916L;
	private User user;
	private CustomTableValue type;
	private Code code;
	private String classificationId;
	private User creator;
	private Integer activeFlag;
	private String grade;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public void setClassificationId(String classificationId) {
		this.classificationId = classificationId;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getCreator() {
		return creator;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGrade() {
		return grade;
	}

}
