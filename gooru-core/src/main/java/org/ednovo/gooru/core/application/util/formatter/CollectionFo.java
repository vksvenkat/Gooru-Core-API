/*******************************************************************************
 * CollectionFo.java
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
package org.ednovo.gooru.core.application.util.formatter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.Resource.Thumbnail;
import org.ednovo.gooru.core.api.model.User;

public class CollectionFo {

	private Map<Integer, List<Code>> taxonomySetMapping;

	private String gooruOid;

	private List<CodeType> taxonomyLevels;

	private Set<Code> taxonomySet;

	private Integer hasRequestPending;

	private String assetURI;

	private String userid;

	private String collaboratorsString;

	private User user;

	private User creator;

	private List<User> collaborators;

	private String grade;

	private List<String> subject;

	private List<String> unit;

	private List<String> topic;

	private List<String> lesson;

	private List<String> code;

	private RatingFo rating;

	private SubscriptionFo subscription;

	private List<String> description;

	private String title;

	private String type;

	private String nativeurl;

	private String resourceFolder;

	private String narrative;

	private String collectionGooruOid;

	private String assessmentGooruOid;

	private String linkedAssessmentTitle;

	private String linkedCollectionTitle;

	private String sharing;

	private String source;

	private Short distinguish;

	private Date lastModified;

	private Integer isFeatured;

	private List<SegmentFo> segments;

	private String goal;
	
	private String vocabulary;
	
	private String thumbnail;
	
	private Thumbnail thumbnails;
	
	private Integer segmentCount;
	
	private Integer collectionPageCount;
	
	private Integer quotedResourceCount;
		
	
	private Map<String, Map<String, Map<String, String>>> customFieldValues;
	
	public CollectionFo() {

		subscription = new SubscriptionFo();
	}

	public Map<Integer, List<Code>> getTaxonomySetMapping() {
		return taxonomySetMapping;
	}

	public void setTaxonomySetMapping(Map<Integer, List<Code>> taxonomySetMapping) {
		this.taxonomySetMapping = taxonomySetMapping;
	}

	public List<SegmentFo> getSegments() {
		return segments;
	}

	public void setSegments(List<SegmentFo> segments) {
		this.segments = segments;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public List<CodeType> getTaxonomyLevels() {
		return taxonomyLevels;
	}

	public void setTaxonomyLevels(List<CodeType> taxonomyLevels) {
		this.taxonomyLevels = taxonomyLevels;
	}

	public Set<Code> getTaxonomySet() {
		return taxonomySet;
	}

	public void setTaxonomySet(Set<Code> taxonomySet) {
		this.taxonomySet = taxonomySet;
	}
	
	public String getAssetURI() {
		return assetURI;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCollaboratorsString() {
		return collaboratorsString;
	}

	public void setCollaboratorsString(String collaboratorsString) {
		this.collaboratorsString = collaboratorsString;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(List<String> subject) {
		this.subject = subject;
	}

	public List<String> getUnit() {
		return unit;
	}

	public void setUnit(List<String> unit) {
		this.unit = unit;
	}

	public List<String> getTopic() {
		return topic;
	}

	public void setTopic(List<String> topic) {
		this.topic = topic;
	}

	public List<String> getLesson() {
		return lesson;
	}

	public void setLesson(List<String> lesson) {
		this.lesson = lesson;
	}

	public List<String> getCode() {
		return code;
	}

	public void setCode(List<String> code) {
		this.code = code;
	}

	public RatingFo getRating() {
		return rating;
	}

	public void setRating(RatingFo rating) {
		this.rating = rating;
	}

	public SubscriptionFo getSubscription() {
		return subscription;
	}

	public void setSubscription(SubscriptionFo subscription) {
		this.subscription = subscription;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNativeurl() {
		return nativeurl;
	}

	public void setNativeurl(String nativeurl) {
		this.nativeurl = nativeurl;
	}

	public String getResourceFolder() {
		return resourceFolder;
	}

	public void setResourceFolder(String resourceFolder) {
		this.resourceFolder = resourceFolder;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
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

	public String getLinkedAssessmentTitle() {
		return linkedAssessmentTitle;
	}

	public void setLinkedAssessmentTitle(String linkedAssessmentTitle) {
		this.linkedAssessmentTitle = linkedAssessmentTitle;
	}

	public String getLinkedCollectionTitle() {
		return linkedCollectionTitle;
	}

	public void setLinkedCollectionTitle(String linkedCollectionTitle) {
		this.linkedCollectionTitle = linkedCollectionTitle;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Short getDistinguish() {
		return distinguish;
	}

	public void setDistinguish(Short distinguish) {
		this.distinguish = distinguish;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Integer getIsFeatured() {
		return isFeatured;
	}

	public void setIsFeatured(Integer isFeatured) {
		this.isFeatured = isFeatured;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public class SubscriptionFo {
		private Integer subscriptioncount;
		private boolean isSubscribed;
		private List<HashMap<String, String>> subscriptions;

		public Integer getSubscriptioncount() {
			return subscriptioncount;
		}

		public void setSubscriptioncount(Integer subscriptioncount) {
			this.subscriptioncount = subscriptioncount;
		}

		public boolean getIsSubscribed() {
			return isSubscribed;
		}

		public void setIsSubscribed(boolean isSubscribed) {
			this.isSubscribed = isSubscribed;
		}

		public void setSubscriptions(List<HashMap<String, String>> subscriptions) {
			this.subscriptions = subscriptions;
		}

		public List<HashMap<String, String>> getSubscriptions() {
			return subscriptions;
		}

	}

	public void setHasRequestPending(Integer hasRequestPending) {
		this.hasRequestPending = hasRequestPending;
	}

	public Integer getHasRequestPending() {
		return hasRequestPending;
	}
	
	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}

	public Thumbnail getThumbnails() {
		return thumbnails;
	}

	public Map<String, Map<String, Map<String, String>>> getCustomFieldValues() {
		return customFieldValues;
	}

	public void setCustomFieldValues(Map<String, Map<String, Map<String, String>>> customFieldValues) {
		this.customFieldValues = customFieldValues;
	}

	public Integer getSegmentCount() {
		return segmentCount;
	}

	public void setSegmentCount(Integer segmentCount) {
		this.segmentCount = segmentCount;
	}
	
	public Integer getCollectionPageCount() {
		return collectionPageCount;
	}

	public void setCollectionPageCount(Integer collectionPageCount) {
		this.collectionPageCount = collectionPageCount;
	}

	public Integer getQuotedResourceCount() {
		return quotedResourceCount;
	}

	public void setQuotedResourceCount(Integer quotedResourceCount) {
		this.quotedResourceCount = quotedResourceCount;
	}


	

}
