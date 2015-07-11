package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ClassCollectionSettings implements Serializable {

	private static final long serialVersionUID = -3271310636333972691L;

	private Long classId;
	private Long collectionId;
	private String value;
	private Boolean visibility;
	private Short scoreTypeId;

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

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Short getScoreTypeId() {
		return scoreTypeId;
	}

	public void setScoreTypeId(Short scoreTypeId) {
		this.scoreTypeId = scoreTypeId;
	}

}
