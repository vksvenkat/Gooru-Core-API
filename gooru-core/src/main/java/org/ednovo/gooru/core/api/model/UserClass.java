package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6653728358644358796L;

	private Long classId;

	private String title;

	private String description;

	private String imagePath;

	private String grades;

	private UserGroup userGroup;

	private Long courseContentId;

	private Integer minimumScore;

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getGrades() {
		return grades;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}

	public Long getCourseContentId() {
		return courseContentId;
	}

	public void setCourseContentId(Long courseContentId) {
		this.courseContentId = courseContentId;
	}

	public Integer getMinimumScore() {
		return minimumScore;
	}

	public void setMinimumScore(Integer minimumScore) {
		this.minimumScore = minimumScore;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

}
