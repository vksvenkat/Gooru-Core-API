/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "taxonomy")
public class TaxonomyCo {

	@Column
	private String subjects;

	@Column
	private String courses;

	@Column
	private String units;

	@Column
	private String topics;

	@Column
	private String lessons;
	
	@Column
	private Map<String,String> codeTypeId;
	
	@Column
	private Set<String> standards;
	
	@Column
	private String taxonomySkills;

	public TaxonomyCo() {

	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getCourses() {
		return courses;
	}

	public void setCourses(String courses) {
		this.courses = courses;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getLessons() {
		return lessons;
	}

	public void setLessons(String lessons) {
		this.lessons = lessons;
	}

	public Map<String, String> getCodeTypeId() {
		return codeTypeId;
	}

	public void setCodeTypeId(Map<String, String> codeTypeId) {
		this.codeTypeId = codeTypeId;
	}

	public Set<String> getStandards() {
		return standards;
	}

	public void setStandards(Set<String> standards) {
		this.standards = standards;
	}

	public String getTaxonomySkills() {
		return taxonomySkills;
	}

	public void setTaxonomySkills(String taxonomySkills) {
		this.taxonomySkills = taxonomySkills;
	}
}