
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

public class Course extends OrganizationModel implements Serializable {
	
	
	private static final long serialVersionUID = -1775302177846507373L;
	
	@Id
	private Short courseId;

	@Column
	private String name;

	@Column
	private String description;
	
	@Id
	private Short subjectId;

	@Column
	private String courseCode;
	
	@Column
	private String grades;
	
	@Column
	private Short activeFlag;

	@Column
	private String imagePath;
	
	@Column
	private User creatorUid;
	
	@Column
	private String displaySequence;
	
	@Column
	private Date createdOn;
	
	@Column
	private Date lastModified;
 

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getGrades() {
		return grades;
	}

	public void setGrades(String grades) {
		this.grades = grades;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public User getCreatorUid() {
		return creatorUid;
	}

	public void setCreatorUid(User creatorUid) {
		this.creatorUid = creatorUid;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public String getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(String displaySequence) {
		this.displaySequence = displaySequence;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(java.util.Date date) {
		this.createdOn = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Short getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Short subjectId) {
		this.subjectId = subjectId;
	}
	
	public Short getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Short activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Short getCourseId() {
		return courseId;
	}

	public void setCourseId(Short courseId) {
		this.courseId = courseId;
	}

}
