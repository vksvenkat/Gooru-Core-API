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

	private Short minimumScore;

	private Boolean visibility;

	private String uri;
	
	private Long classId;

	private String courseGooruOid;
	
	private String mediaFilename;
	
	public static final String IMAGE_DIMENSION = "800x600";
	
	public static final String REPO_PATH = "fclass";
	
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

	public Short getMinimumScore() {
		return minimumScore;
	}

	public void setMinimumScore(Short minimumScore) {
		this.minimumScore = minimumScore;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}
	public String getCourseGooruOid() {
		return courseGooruOid;
	}

	public void setCourseGooruOid(String courseGooruOid) {
		this.courseGooruOid = courseGooruOid;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public String getMediaFilename() {
		return mediaFilename;
	}

	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}
}
