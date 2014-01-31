/////////////////////////////////////////////////////////////
// CollectionSearchResult.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.search;

import java.util.Collections;
import java.util.Set;

import org.ednovo.gooru.core.api.model.CollectionItem;

public class CollectionSearchResult extends SearchResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8699668067262807882L;
	private String collectionType;
	private String narrationLink;
	private String notes;
	private String keyPoints;
	private String language;
	private String goals;
	private String grade;
	private Integer collectionItemCount;
	private Set<CollectionItem> collectionItems = Collections.emptySet();

	private String lastModifiedBy;

	private boolean profileUserVisibility;

	public String getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Set<CollectionItem> getCollectionItems() {
		return collectionItems;
	}

	public void setCollectionItems(Set<CollectionItem> collectionItems) {
		this.collectionItems = collectionItems;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public boolean isProfileUserVisibility() {
		return profileUserVisibility;
	}

	public void setProfileUserVisibility(boolean profileUserVisibility) {
		this.profileUserVisibility = profileUserVisibility;
	}

	public Integer getCollectionItemCount() {
		return collectionItemCount;
	}

	public void setCollectionItemCount(Integer collectionItemCount) {
		this.collectionItemCount = collectionItemCount;
	}

}
