/////////////////////////////////////////////////////////////
// SearchResult.java
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Tag;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.BaseUtil;

public class SearchResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] fragments;
	private String id;
	private long contentId;
	private String lesson;
	private String title;
	private String topic;
	private String unit;
	private String goals;
	private String subject;
	private String grade;
	private String sharing;
	private Integer userId;
	private String userFirstName;
	private String userLastName;
	private Short distinguish;
	private String gooruUId;
	private String collaborators;
	private String taxonomyLesson;
	private String taxonomySkills;
	private String folder;
	private String thumbnail;
	private String type;
	private Integer isFeatured;
	private Map<Integer, List<Code>> taxonomyMapByCode;
	private String collection_gooru_oid;
	private String assessment_gooru_oid;
	private String creatorId;
	private String creatorFirstname;
	private String creatorLastname;
	private String lastModified;
	private String usernameDisplay;
	private String creatornameDisplay;
	private Integer numberOfResources;
	private String taxonomyDataSet;
	private String collectionQuiz;
	private String network;
	private String assetURI;
	private String addDate;
	private Integer viewCount;
	private Integer subscriptionCount;
	private String contentOrganizationUid;
	private String contentOrganizationName;
	private String contentOrganizationCode;
	private Thumbnail thumbnails;
	private Map<String,String> customFields;
	private String segmentTiltlesAndOIds;
	private Integer resourceInstanceCount;
	private String tags;
	private Integer collaboratorCount;
	private String averageTime;
	private Integer votesUp;
	private Integer votesDown;
	private Set<Tag> tagSet;
	private String description;
	private String resultUId;
	private String category;
	private String batchId;

	public SearchResult () { 
		thumbnails = new Thumbnail();
	}
	public String getUsernameDisplay() {
		return usernameDisplay;
	}

	public void setUsernameDisplay(String usernameDisplay) {
		this.usernameDisplay = usernameDisplay;
	}

	public String getCreatornameDisplay() {
		return creatornameDisplay;
	}

	public void setCreatornameDisplay(String creatornameDisplay) {
		this.creatornameDisplay = creatornameDisplay;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorFirstname() {
		return creatorFirstname;
	}

	public void setCreatorFirstname(String creatorFirstname) {
		this.creatorFirstname = creatorFirstname;
	}

	public String getCreatorLastname() {
		return creatorLastname;
	}

	public void setCreatorLastname(String creatorLastname) {
		this.creatorLastname = creatorLastname;
	}

	public Map<Integer, List<Code>> getTaxonomyMapByCode() {
		return taxonomyMapByCode;
	}

	public void setTaxonomyMapByCode(Map<Integer, List<Code>> taxonomyMapByCode) {
		this.taxonomyMapByCode = taxonomyMapByCode;
	}

	public String getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(String collaborators) {
		this.collaborators = collaborators;
	}

	public String getGooruUId() {
		return gooruUId;
	}

	public void setGooruUId(String gooruUId) {
		this.gooruUId = gooruUId;
	}

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public Short getDistinguish() {
		return distinguish;
	}

	public void setDistinguish(Short distinguish) {
		this.distinguish = distinguish;
	}

	public String[] getFragments() {
		return fragments;
	}

	public void setFragments(String[] fragments) {
		this.fragments = fragments;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTaxonomyLesson(String taxonomyLesson) {
		this.taxonomyLesson = taxonomyLesson;
	}

	public String getTaxonomyLesson() {
		return taxonomyLesson;
	}

	public void setTaxonomySkills(String taxonomySkills) {
		this.taxonomySkills = taxonomySkills;
	}

	public String getTaxonomySkills() {
		return taxonomySkills;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getIsFeatured() {
		return isFeatured;
	}

	public void setIsFeatured(Integer isFeatured) {
		this.isFeatured = isFeatured;
	}

	public void setAssessment_gooru_oid(String assessment_gooru_oid) {
		this.assessment_gooru_oid = assessment_gooru_oid;
	}

	public String getAssessment_gooru_oid() {
		return assessment_gooru_oid;
	}

	public void setCollection_gooru_oid(String collection_gooru_oid) {
		this.collection_gooru_oid = collection_gooru_oid;
	}

	public String getCollection_gooru_oid() {
		return collection_gooru_oid;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public Integer getNumberOfResources() {
		return numberOfResources;
	}

	public void setNumberOfResources(Integer numberOfResources) {
		this.numberOfResources = numberOfResources;
	}

	public void setTaxonomyDataSet(String taxonomyDataSet) {
		this.taxonomyDataSet = taxonomyDataSet;
	}

	public String getTaxonomyDataSet() {
		return taxonomyDataSet;
	}

	public void setCollectionQuiz(String collectionQuiz) {
		this.collectionQuiz = collectionQuiz;
	}

	public String getCollectionQuiz() {
		return collectionQuiz;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNetwork() {
		return network;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public String getAssetURI() {
		if (UserGroupSupport.getUserOrganizationCdnDirectPath() != null) { 
			assetURI = UserGroupSupport.getUserOrganizationCdnDirectPath();
		}
		assetURI = BaseUtil.changeHttpsProtocol(assetURI);
		return assetURI;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public void setSubscriptionCount(Integer subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}

	public Integer getSubscriptionCount() {
		return subscriptionCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContentOrganizationUid() {
		return contentOrganizationUid;
	}

	public void setContentOrganizationUid(String contentOrganizationUid) {
		this.contentOrganizationUid = contentOrganizationUid;
	}

	public String getContentOrganizationName() {
		return contentOrganizationName;
	}

	public void setContentOrganizationName(String contentOrganizationName) {
		this.contentOrganizationName = contentOrganizationName;
	}

	public String getContentOrganizationCode() {
		return contentOrganizationCode;
	}

	public void setContentOrganizationCode(String contentOrganizationCode) {
		this.contentOrganizationCode = contentOrganizationCode;
	}
	
	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}

	public Thumbnail getThumbnails() {
		return thumbnails;
	}

	public Map<String,String> getCustomFields() {
		return customFields;
	}
	public void setCustomFields(Map<String,String> customFields) {
		this.customFields = customFields;
	}

	public class Thumbnail implements Serializable { 
		
		private static final long serialVersionUID = 5215352335276824742L;
		private String url;
		private String dimensions;
		private boolean isDefaultImage;
		public String getUrl() {
			if (getThumbnail() != null
					&& getThumbnail().contains("gooru-default")) {
				this.url = getAssetURI() + getThumbnail();
			} else {
				this.url = getAssetURI() + getFolder()
						+ getThumbnail();
			}
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getDimensions() {
			String resourceTypeName = getType();
			if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType())) {
				this.dimensions = ResourceImageUtil.COLLECTION_THUMBNAIL_SIZES;
			} else if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
				this.dimensions = ResourceImageUtil.QUIZ_THUMBNAIL_SIZES;
			} else {
				this.dimensions = ResourceImageUtil.RESOURCE_THUMBNAIL_SIZES;
			}
			return dimensions;
		}
		
		public void setDimensions(String dimensions) {
			this.dimensions = dimensions;
		}
		public boolean isDefaultImage() {
			if (getThumbnail() != null
					&& getThumbnail().contains("gooru-default")) {
				 this.isDefaultImage = true;
			} else { 
				this.isDefaultImage = false;
			}
			return isDefaultImage;
		}
		public void setDefaultImage(boolean isDefaultImage) {
			this.isDefaultImage = isDefaultImage;
		}	
	}

	public long getContentId() {
		return contentId;
	}
	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
	public String getSegmentTiltlesAndOIds() {
		return segmentTiltlesAndOIds;
	}
	public void setSegmentTiltlesAndOIds(String segmentTiltlesAndOIds) {
		this.segmentTiltlesAndOIds = segmentTiltlesAndOIds;
	}
	public Integer getResourceInstanceCount() {
		return resourceInstanceCount;
	}
	public void setResourceInstanceCount(Integer resourceInstanceCount) {
		this.resourceInstanceCount = resourceInstanceCount;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Integer getCollaboratorCount() {
		return collaboratorCount;
	}
	public void setCollaboratorCount(Integer collaboratorCount) {
		this.collaboratorCount = collaboratorCount;
	}
	public String getAverageTime() {
		return averageTime;
	}
	public void setAverageTime(String averageTime) {
		this.averageTime = averageTime;
	}
	public void setVotesUp(Integer votesUp) {
		this.votesUp = votesUp;
	}
	public Integer getVotesUp() {
		return votesUp;
	}
	public void setVotesDown(Integer votesDown) {
		this.votesDown = votesDown;
	}
	public Integer getVotesDown() {
		return votesDown;
	}
	public void setTagSet(Set<Tag> tagSet) {
		this.tagSet = tagSet;
	}
	public Set<Tag> getTagSet() {
		return tagSet;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public String getResultUId() {
		return resultUId;
	}
	public void setResultUId(String resultUId) {
		this.resultUId = resultUId;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory() {
		return category;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBatchId() {
		return batchId;
	}
	

}
