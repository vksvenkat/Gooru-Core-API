package org.ednovo.gooru.core.api.model;

public class ClassCollectionSettings extends Content  {
	
	private static final long serialVersionUID = -3271310636333972691L;
	
	private Long classpageContentId;
	private Long collectionId;
	private String  value;
	
	public Long getClasspageContentId() {
		return classpageContentId;
	}
	public void setClasspageContentId(Long classpageContentId) {
		this.classpageContentId = classpageContentId;
	}
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
	

}
