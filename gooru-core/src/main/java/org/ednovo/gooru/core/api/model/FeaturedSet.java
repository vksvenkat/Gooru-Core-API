package org.ednovo.gooru.core.api.model;

import java.util.List;
import java.util.Set;

public class FeaturedSet extends OrganizationModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2412080401098106714L;

	private Integer featuredSetId;
	private String name;
	private String subjectCode;
	private Set<FeaturedSetItems> featuredSetItems;
	private boolean activeFlag = true;
	private List<Resource> resources;
	private List<Learnguide> collections;
	private List<AssessmentQuestion> questions;
	private Integer sequence;
	private String themeCode;
	private String displayName;
	private List<Collection> scollections;
	private CustomTableValue type;
	
	public Integer getFeaturedSetId() {
		return featuredSetId;
	}

	public void setFeaturedSetId(Integer featuredSetId) {
		this.featuredSetId = featuredSetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Set<FeaturedSetItems> getFeaturedSetItems() {
		return featuredSetItems;
	}

	public void setFeaturedSetItems(Set<FeaturedSetItems> featuredSetItems) {
		this.featuredSetItems = featuredSetItems;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public List<Learnguide> getCollections() {
		return collections;
	}

	public void setCollections(List<Learnguide> collections) {
		this.collections = collections;
	}

	public List<AssessmentQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AssessmentQuestion> questions) {
		this.questions = questions;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}

	public String getThemeCode() {
		return themeCode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setScollections(List<Collection> scollections) {
		this.scollections = scollections;
	}

	public List<Collection> getScollections() {
		return scollections;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectCode() {
		return subjectCode;
	}



}
