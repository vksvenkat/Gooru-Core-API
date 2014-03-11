/*******************************************************************************
 * TaxonomyCo.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
