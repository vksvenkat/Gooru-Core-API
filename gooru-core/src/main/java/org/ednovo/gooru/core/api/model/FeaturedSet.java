/*******************************************************************************
 * FeaturedSet.java
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
