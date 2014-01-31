/////////////////////////////////////////////////////////////
// AssessmentSearchResult.java
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

import java.util.ArrayList;
import java.util.List;

public class AssessmentSearchResult extends SearchResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2370334674589355676L;
	private String name;
	private List<String> segments;
	private long contentId;
	private String type;
	private Integer views;
	private Integer ratings;
	private Integer subscribers;
	private String learningObjectives;
	private String collaboratorEmails;
	private String collaboratorUserIds;
	private String userFirstName;
	@SuppressWarnings("unused")
	private String userLastName;
	private String usernameDisplay;
	private String quizCollection;
	private String userEmailId;
	private String creatorEmailId;
	private String vocabulary;

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getQuizCollection() {
		return quizCollection;
	}

	public void setQuizCollection(String quizCollection) {
		this.quizCollection = quizCollection;
	}

	public AssessmentSearchResult() {
		segments = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSegments() {
		return segments;
	}

	public void setSegments(List<String> segments) {
		this.segments = segments;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public Integer getRatings() {
		return ratings;
	}

	public void setRatings(Integer ratings) {
		this.ratings = ratings;
	}

	public Integer getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(Integer subscribers) {
		this.subscribers = subscribers;
	}

	public String getLearningObjectives() {
		return learningObjectives;
	}

	public void setLearningObjectives(String learningObjectives) {
		this.learningObjectives = learningObjectives;
	}

	public String getCollaboratorEmails() {
		return collaboratorEmails;
	}

	public void setCollaboratorEmails(String collaboratorEmails) {
		this.collaboratorEmails = collaboratorEmails;
	}

	public String getCollaboratorUserIds() {
		return collaboratorUserIds;
	}

	public void setCollaboratorUserIds(String collaboratorUserIds) {
		this.collaboratorUserIds = collaboratorUserIds;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userFirstName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUsernameDisplay() {
		return usernameDisplay;
	}

	public void setUsernameDisplay(String usernameDisplay) {
		this.usernameDisplay = usernameDisplay;
	}

	public String getUserEmailId() {
		return userEmailId;
	}

	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}

	public String getCreatorEmailId() {
		return creatorEmailId;
	}

	public void setCreatorEmailId(String creatorEmailId) {
		this.creatorEmailId = creatorEmailId;
	}

}
