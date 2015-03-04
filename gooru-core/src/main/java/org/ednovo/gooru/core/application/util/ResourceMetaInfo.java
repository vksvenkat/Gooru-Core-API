package org.ednovo.gooru.core.application.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.StandardFo;

public class ResourceMetaInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2670811444584590511L;

	private List<String> vocabulary;
	private Set<String> course;
	private List<StandardFo> standards;
	private Rating rating;
	private Set<String> acknowledgement;
	private Set<Map<String, Object>> skills;

	/**
	 * 
	 */
	public ResourceMetaInfo() {
	}

	public List<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(List<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Set<String> getCourse() {
		return course;
	}

	public void setCourse(Set<String> course) {
		this.course = course;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Rating getRating() {
		return rating;
	}

	public void setStandards(List<StandardFo> standards) {
		this.standards = standards;
	}

	public List<StandardFo> getStandards() {
		return standards;
	}

	public void setAcknowledgement(Set<String> acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public Set<String> getAcknowledgement() {
		return acknowledgement;
	}

	public Set<Map<String, Object>> getSkills() {
		return skills;
	}

	public void setSkills(Set<Map<String, Object>> skills) {
		this.skills = skills;
	}

}
