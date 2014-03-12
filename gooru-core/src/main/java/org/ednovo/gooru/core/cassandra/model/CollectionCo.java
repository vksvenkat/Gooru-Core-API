/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "collections")
public class CollectionCo {

	@Column
	private String taxonomyJson;

	@Column
	private String lesson;

	@Column
	private String classplanContent;
	
	@Column
	private String collectionResourceTitles;
	
	@Column
	private String collaboratorUIds;
	
	@Column
	private String goals;
	
	@Column
	private String source;
	
	@Column
	private String collectionGooruOid;
	
	@Column
	private String assessmentGooruOid;
	
	@Column
	private String collectionQuizName;
	
	@Column
	private List<Long> resourceContentIds;
	
	@Column
	private String segmentTiltlesAndOIds;
	
	@Column
	private String segmentTitles;

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getClassplanContent() {
		return classplanContent;
	}

	public void setClassplanContent(String classplanContent) {
		this.classplanContent = classplanContent;
	}

	public String getCollectionResourceTitles() {
		return collectionResourceTitles;
	}

	public void setCollectionResourceTitles(String collectionResourceTitles) {
		this.collectionResourceTitles = collectionResourceTitles;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCollectionGooruOid() {
		return collectionGooruOid;
	}

	public void setCollectionGooruOid(String collectionGooruOid) {
		this.collectionGooruOid = collectionGooruOid;
	}

	public String getAssessmentGooruOid() {
		return assessmentGooruOid;
	}

	public void setAssessmentGooruOid(String assessmentGooruOid) {
		this.assessmentGooruOid = assessmentGooruOid;
	}

	public String getCollectionQuizName() {
		return collectionQuizName;
	}

	public void setCollectionQuizName(String collectionQuizName) {
		this.collectionQuizName = collectionQuizName;
	}

	public void setResourceContentIds(List<Long> resourceContentIds) {
		this.resourceContentIds = resourceContentIds;
	}

	public List<Long> getResourceContentIds() {
		return resourceContentIds;
	}

	public void setSegmentTiltlesAndOIds(String segmentTiltlesAndOIds) {
		this.segmentTiltlesAndOIds = segmentTiltlesAndOIds;
	}

	public String getSegmentTiltlesAndOIds() {
		return segmentTiltlesAndOIds;
	}

	public String getTaxonomyJson() {
		return taxonomyJson;
	}

	public void setTaxonomyJson(String taxonomyJson) {
		this.taxonomyJson = taxonomyJson;
	}

	public String getSegmentTitles() {
		return segmentTitles;
	}

	public void setSegmentTitles(String segmentTitles) {
		this.segmentTitles = segmentTitles;
	}

	public String getCollaboratorUIds() {
		return collaboratorUIds;
	}

	public void setCollaboratorUIds(String collaboratorUIds) {
		this.collaboratorUIds = collaboratorUIds;
	}
}