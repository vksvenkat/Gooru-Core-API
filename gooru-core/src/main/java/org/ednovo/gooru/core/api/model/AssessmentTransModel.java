package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class AssessmentTransModel  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8405912273414860351L;
	
	private String importCode;

	private String name;

	private String description;

	private Integer questionCount;

	private Integer timeToCompleteInSecs;

	private Boolean isRandom;

	private Boolean isChoiceRandom;

	private Boolean showHints;

	private Boolean showScore;

	private Boolean showCorrectAnswer;

	private String grade;

	private String medium;

	private String learningObjectives;

	private Set<AssessmentSegment> segments;

	private Set<AssessmentAttempt> attempts;

	private AssessmentMetaDataDTO metaData;
	
	private String collaborators;
	
	private List<User> collaboratorList;
	
	private String creatorGooruUserId;

	private String ownerGooruUserId;
	
	private String source;
	
	private String taxonomyContentData;
	
	private String linkedCollectionTitle;
	
	private String linkedAssessmentTitle;

	public String getCollectionGooruOid() {
		return collectionGooruOid;
	}

	public void setCollectionGooruOid(String collectionGooruOid) {
		this.collectionGooruOid = collectionGooruOid;
	}

	private String vocabulary;
	
	private String collectionGooruOid;
	
	private String quizGooruOid;

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(Integer questionCount) {
		this.questionCount = questionCount;
	}

	public Integer getTimeToCompleteInSecs() {
		return timeToCompleteInSecs;
	}

	public void setTimeToCompleteInSecs(Integer timeToCompleteInSecs) {
		this.timeToCompleteInSecs = timeToCompleteInSecs;
	}

	public Boolean getIsRandom() {
		return isRandom;
	}

	public void setIsRandom(Boolean isRandom) {
		this.isRandom = isRandom;
	}

	public Boolean getIsChoiceRandom() {
		return isChoiceRandom;
	}

	public void setIsChoiceRandom(Boolean isChoiceRandom) {
		this.isChoiceRandom = isChoiceRandom;
	}

	public Boolean getShowHints() {
		return showHints;
	}

	public void setShowHints(Boolean showHints) {
		this.showHints = showHints;
	}

	public Boolean getShowScore() {
		return showScore;
	}

	public void setShowScore(Boolean showScore) {
		this.showScore = showScore;
	}

	public Boolean getShowCorrectAnswer() {
		return showCorrectAnswer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setShowCorrectAnswer(Boolean showCorrectAnswer) {
		this.showCorrectAnswer = showCorrectAnswer;
	}

	public Set<AssessmentSegment> getSegments() {
		return segments;
	}

	public void setSegments(Set<AssessmentSegment> segments) {
		this.segments = segments;
	}

	public Set<AssessmentAttempt> getAttempts() {
		return attempts;
	}

	public void setAttempts(Set<AssessmentAttempt> attempts) {
		this.attempts = attempts;
	}

	public AssessmentMetaDataDTO getMetaData() {
		return metaData;
	}

	public void setMetaData(AssessmentMetaDataDTO metaData) {
		this.metaData = metaData;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getLearningObjectives() {
		return learningObjectives;
	}

	public void setLearningObjectives(String learningObjectives) {
		this.learningObjectives = learningObjectives;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCollaborators(){
		return collaborators;
	}
	
	public void setCollaborators(String collaborators){
		this.collaborators = collaborators;
	}

	public String getCreatorGooruUserId() {
		return creatorGooruUserId;
	}

	public void setCreatorGooruUserId(String creatorGooruUserId) {
		this.creatorGooruUserId = creatorGooruUserId;
	}

	public String getOwnerGooruUserId() {
		return ownerGooruUserId;
	}

	public void setOwnerGooruUserId(String ownerGooruUserId) {
		this.ownerGooruUserId = ownerGooruUserId;
	}

    public String getSource() {
		return source;
	}
	
	public void setSource(String source){
		this.source = source;
	}
	public List<User> getCollaboratorList() {
		return collaboratorList;
	}

	public void setCollaboratorList(List<User> collaboratorList) {
		this.collaboratorList = collaboratorList;
	}

	public String getTaxonomyContentData() {
		return taxonomyContentData;
	}

	public void setTaxonomyContentData(String taxonomyContentData) {
		this.taxonomyContentData = taxonomyContentData;
	}

	public String getQuizGooruOid() {
		return quizGooruOid;
	}

	public void setQuizGooruOid(String quizGooruOid) {
		this.quizGooruOid = quizGooruOid;
	}

	public String getLinkedCollectionTitle() {
		return linkedCollectionTitle;
	}

	public void setLinkedCollectionTitle(String linkedCollectionTitle) {
		this.linkedCollectionTitle = linkedCollectionTitle;
	}

	public String getLinkedAssessmentTitle() {
		return linkedAssessmentTitle;
	}

	public void setLinkedAssessmentTitle(String linkedAssessmentTitle) {
		this.linkedAssessmentTitle = linkedAssessmentTitle;
	}



}
