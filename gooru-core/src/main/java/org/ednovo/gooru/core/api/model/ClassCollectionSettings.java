package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ClassCollectionSettings implements Serializable {

	private static final long serialVersionUID = -3271310636333972691L;

	private Long classId;
	private Long lessonId;
	private Long collectionId;
	private String value;

	public Long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(Long collectionId) {
		this.collectionId = collectionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public Long getLessonId() {
		return lessonId;
	}

	public void setLessonId(Long lessonId) {
		this.lessonId = lessonId;
	}

}
