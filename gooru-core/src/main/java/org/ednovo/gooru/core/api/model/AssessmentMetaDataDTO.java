package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.User;


public class AssessmentMetaDataDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3181891009769907985L;

	private List<String> grades = new ArrayList<String>();

	private List<String> subjects = new ArrayList<String>();

	private List<String> units = new ArrayList<String>();

	private List<String> topics = new ArrayList<String>();

	private List<String> lessons = new ArrayList<String>();

	private List<String> curriculumCodes = new ArrayList<String>();

	private List<String> curriculumDescs = new ArrayList<String>();

	private String collaboratorsString;

	private List<User> collaborators;

	private String title;
	
	private Map<Integer,List<Code>> taxonomyMapByCode;

	private List<CodeType> taxonomyLevels;

	public AssessmentMetaDataDTO() {
		
		taxonomyMapByCode = new HashMap<Integer, List<Code>>();
		collaborators = new ArrayList<User>();
	}

	public List<String> getGrades() {
		return grades;
	}

	public void setGrades(List<String> grades) {
		this.grades = grades;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	public List<String> getTopics() {
		return topics;
	}

	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	public List<String> getLessons() {
		return lessons;
	}

	public void setLessons(List<String> lessons) {
		this.lessons = lessons;
	}

	public List<String> getCurriculumDescs() {
		return curriculumDescs;
	}

	public void setCurriculumDescs(List<String> curriculumDescs) {
		this.curriculumDescs = curriculumDescs;
	}

	public List<String> getCurriculumCodes() {
		return curriculumCodes;
	}

	public void setCurriculumCodes(List<String> curriculumCodes) {
		this.curriculumCodes = curriculumCodes;
	}

	public String getCollaboratorsString() {
		return collaboratorsString;
	}

	public void setCollaboratorsString(String collaboratorsString) {
		this.collaboratorsString = collaboratorsString;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<Integer, List<Code>> getTaxonomyMapByCode() {
		return taxonomyMapByCode;
	}

	public void setTaxonomyMapByCode(Map<Integer, List<Code>> taxonomyMapByCode) {
		this.taxonomyMapByCode = taxonomyMapByCode;
	}

	public void setTaxonomyLevels(List<CodeType> taxonomyLevels) {
		this.taxonomyLevels = taxonomyLevels;
	}

	public List<CodeType> getTaxonomyLevels() {
		return taxonomyLevels;
	}
}
