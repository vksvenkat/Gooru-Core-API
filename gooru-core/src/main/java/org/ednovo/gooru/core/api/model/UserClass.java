package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserClass extends UserGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6653728358644358796L;
	
	private String description;

	private String imagePath;

	private String grades;

	private Long courseContentId;

	private int minimumScore;

	private short visibility;

	private String uri;

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

	public short getVisibility() {
		return visibility;
	}

	public void setVisibility(short visibility) {
		this.visibility = visibility;
	}

	public int getMinimumScore() {
		return minimumScore;
	}

	public void setMinimumScore(int minimumScore) {
		this.minimumScore = minimumScore;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
