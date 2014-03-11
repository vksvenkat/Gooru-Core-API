/*******************************************************************************
 * SessionItem.java
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

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Resource;

public class SessionItem implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -203672596781106569L;


	private String sessionItemId;
	
	private Session session;
	
	private Resource resource;
	
	private CollectionItem collectionItem;
	
	private String attemptItemStatus;
	
	private Integer correctTrySequence;
	
	private Date startTime;

	private Date endTime;
	
	private Set<SessionItemAttemptTry> sessionItemAttemptTry;

	public void setSessionItemId(String sessionItemId) {
		this.sessionItemId = sessionItemId;
	}

	public String getSessionItemId() {
		return sessionItemId;
	}

	public void setsession(Session session) {
		this.session = session;
	}

	public Session getsession() {
		return session;
	}

	public void setCollectionItem(CollectionItem collectionItem) {
		this.collectionItem = collectionItem;
	}

	public CollectionItem getCollectionItem() {
		return collectionItem;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public void setAttemptItemStatus(String attemptItemStatus) {
		this.attemptItemStatus = attemptItemStatus;
	}

	public String getAttemptItemStatus() {
		return attemptItemStatus;
	}

	public void setCorrectTrySequence(Integer correctTrySequence) {
		this.correctTrySequence = correctTrySequence;
	}

	public Integer getCorrectTrySequence() {
		return correctTrySequence;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setSessionItemAttemptTry(Set<SessionItemAttemptTry> sessionItemAttemptTry) {
		this.sessionItemAttemptTry = sessionItemAttemptTry;
	}

	public Set<SessionItemAttemptTry> getSessionItemAttemptTry() {
		return sessionItemAttemptTry;
	}

}
