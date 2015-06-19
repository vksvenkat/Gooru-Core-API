/////////////////////////////////////////////////////////////
// Course.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class TaxonomyCourse implements Serializable {

	private static final long serialVersionUID = -1775302177846507373L;
	
	public static final String REPO_PATH = "fc";

	private Integer courseId;

	private String name;

	private String description;

	private Integer subjectId;

	private String courseCode;

	private String grades;

	private Short activeFlag;

	private String imagePath;

	private User creatorUid;

	private Integer displaySequence;

	private Date createdOn;

	private Date lastModified;

	private String uri;

	private String mediaFilename;

	public String getMediaFilename() {
		return mediaFilename;
	}

	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}

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

	public Integer getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence) {
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

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Short getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Short activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
