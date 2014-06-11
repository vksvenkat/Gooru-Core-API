package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "scollection")
public class SCollectionCo {

	@Column
	private String collectionType;
	
	@Column
	private String narrationLink;
	
	@Column
	private String notes;
	
	@Column
	private String keyPoints;

	@Column
	private String language;
	
	@Column
	private String goals;

	@Column
	private String estimatedTime;
	
	@Column
	private String network;
	
	@Column
	private String collaborators;

	@Column
	private String collectionItemCount;
	
	@Column
	private String collectionItemsJSON;
	
	@Column
	public String usedResourceGooruOidsJson;
	
	@Column
	public String questionCount;

	@Column
	public String resourceCount;
	
	@Column
	public String audience;
	
	@Column
	public String depthOfknowledge;
	
	@Column
	private String learningAndInovation;
    
	@Column
	private String instructionMethod;
		
	@Column
	private String languageObjective;
	
	public String getDepthOfknowledge() {
		return depthOfknowledge;
	}

	public void setDepthOfknowledge(String depthOfknowledge) {
		this.depthOfknowledge = depthOfknowledge;
	}

	public  String getAudience() {
		return audience;
	}

	public void setAudience(String list) {
		this.audience = list;
	}

	public String getLearningAndInovation() {
		return learningAndInovation;
	}

	public void setLearningAndInovation(String learningAndInovation) {
		this.learningAndInovation = learningAndInovation;
	}

	public String getInstructionMethod() {
		return instructionMethod;
	}

	public void setInstructionMethod(String instructionMethod) {
		this.instructionMethod = instructionMethod;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public String getCollectionType() {
		return collectionType;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNetwork() {
		return network;
	}

	public void setCollaborators(String collaborators) {
		this.collaborators = collaborators;
	}

	public String getCollaborators() {
		return collaborators;
	}

	public String getCollectionItemCount() {
		return collectionItemCount;
	}

	public void setCollectionItemCount(String collectionItemCount) {
		this.collectionItemCount = collectionItemCount;
	}

	public String getCollectionItemsJSON() {
		return collectionItemsJSON;
	}

	public void setCollectionItemsJSON(String collectionItemsJSON) {
		this.collectionItemsJSON = collectionItemsJSON;
	}

	public String getUsedResourceGooruOidsJson() {
		return usedResourceGooruOidsJson;
	}

	public void setUsedResourceGooruOidsJson(String usedResourceGooruOidsJson) {
		this.usedResourceGooruOidsJson = usedResourceGooruOidsJson;
	}
	
	public String getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(String questionCount) {
		this.questionCount = questionCount;
	}

	public String getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(String resourceCount) {
		this.resourceCount = resourceCount;
	}

	public void setLanguageObjective(String languageObjective) {
		this.languageObjective = languageObjective;
	}

	public String getLanguageObjective() {
		return languageObjective;
	}
	
}
