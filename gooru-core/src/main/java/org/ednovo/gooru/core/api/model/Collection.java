/*******************************************************************************
 * Collection.java
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
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.application.util.CollectionMetaInfo;


public class Collection extends Resource implements Versionable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3271310636333972691L;
	
	
	private static final String INDEX_TYPE = "scollection";
	
	private String collectionType;
	private String narrationLink;
	private String notes;
	private String keyPoints;
	private String language;
	private String goals;
	private String estimatedTime;
	private Set<CollectionItem> collectionItems;
	private List<User> collaborators;
	private ContentAssociation contentAssociation;
	private Map<Integer, List<Code>> taxonomySetMapping;
	private Set<CollectionTaskAssoc> collectionTaskItems;
	private CollectionMetaInfo metaInfo;
	private String network;
	private CollectionItem collectionItem;
	private CustomTableValue buildType;
	private Boolean mailNotification;
    private Map<String,Object> lastModifiedUser;
	public Map<String, Object> getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(Map<String, Object> lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public Collection() {
		super();
	}

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

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public Set<CollectionItem> getCollectionItems() {
		return collectionItems;
	}

	public void setCollectionItems(Set<CollectionItem> collectionItems) {
		this.collectionItems = collectionItems;
	}

	public void setMetaInfo(CollectionMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public CollectionMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public ContentAssociation getContentAssociation() {
		return contentAssociation;
	}

	public void setContentAssociation(ContentAssociation contentAssociation) {
		this.contentAssociation = contentAssociation;
	}

	public void setTaxonomySetMapping(Map<Integer, List<Code>> taxonomySetMapping) {
		this.taxonomySetMapping = taxonomySetMapping;
	}

	public Map<Integer, List<Code>> getTaxonomySetMapping() {
		return taxonomySetMapping;
	}

	@Override
	public String getEntityId() {
		return getGooruOid();
	}

	public void setCollectionTaskItems(Set<CollectionTaskAssoc> collectionTaskItems) {
		this.collectionTaskItems = collectionTaskItems;
	}

	public Set<CollectionTaskAssoc> getCollectionTaskItems() {
		return collectionTaskItems;
	}
	
	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}

	public void setCollectionItem(CollectionItem collectionItem) {
		this.collectionItem = collectionItem;
	}

	public CollectionItem getCollectionItem() {
		return collectionItem;
	}

	public void setBuildType(CustomTableValue buildType) {
		this.buildType = buildType;
	}

	public CustomTableValue getBuildType() {
		return buildType;
	}
	
	public Boolean getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(Boolean mailNotification) {
		if(mailNotification == null){
			mailNotification = true;
		}
		this.mailNotification = mailNotification;
	}
}
