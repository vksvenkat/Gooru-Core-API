/*******************************************************************************
 * SCollectionCo.java
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
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "scollection")
public class SCollectionCo {

	@Column
	private String collectionType;
	
	@Column
	private String narrationLink;
	
	@Column
	private String notes;
	
	@Column
	private String keyPoints;

	@Column
	private String language;
	
	@Column
	private String goals;

	@Column
	private String estimatedTime;
	
	@Column
	private String network;
	
	@Column
	private String collaborators;

	@Column
	private String collectionItemCount;
	
	@Column
	private String collectionItemsJSON;
	
	@Column
	public String usedResourceGooruOidsJson;
	
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

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public String getCollectionType() {
		return collectionType;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getNetwork() {
		return network;
	}

	public void setCollaborators(String collaborators) {
		this.collaborators = collaborators;
	}

	public String getCollaborators() {
		return collaborators;
	}

	public String getCollectionItemCount() {
		return collectionItemCount;
	}

	public void setCollectionItemCount(String collectionItemCount) {
		this.collectionItemCount = collectionItemCount;
	}

	public String getCollectionItemsJSON() {
		return collectionItemsJSON;
	}

	public void setCollectionItemsJSON(String collectionItemsJSON) {
		this.collectionItemsJSON = collectionItemsJSON;
	}

	public String getUsedResourceGooruOidsJson() {
		return usedResourceGooruOidsJson;
	}

	public void setUsedResourceGooruOidsJson(String usedResourceGooruOidsJson) {
		this.usedResourceGooruOidsJson = usedResourceGooruOidsJson;
	}
}
