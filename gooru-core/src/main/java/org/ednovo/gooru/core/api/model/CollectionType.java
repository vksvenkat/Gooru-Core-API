package org.ednovo.gooru.core.api.model;

public enum CollectionType {
	SHElf("shelf"),
	LESSON("lesson"),
	COLLECTION("collection"),
	FOLDER("folder"),
	EBOOK("ebook"),
	CLASSPAGE("classpage"),
	USER_CLASSPAGE("user_classpage"),
	ASSIGNMENT("assignment"),
	QUIZ("quiz"),
	USER_QUIZ("user_quiz"),
	STORY("story");
	
	private String collectionType;
	
	private CollectionType(String collectionType){
		this.collectionType=collectionType;
	}
	
	public String getCollectionType(){
		return this.collectionType;
	}
}
