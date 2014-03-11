/*******************************************************************************
 * SessionActivity.java
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

import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.OrganizationModel;


public class SessionActivity extends OrganizationModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6998586171468786682L;


	private String sessionActivityUid;
	private String userUid;
	private String status;
	private Date createdOn;
	private Set<SessionActivityItem> sessionActivityItems;
	public String getSessionActivityUid() {
		return sessionActivityUid;
	}
	public void setSessionActivityUid(String sessionActivityUid) {
		this.sessionActivityUid = sessionActivityUid;
	}
	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Set<SessionActivityItem> getSessionActivityItems() {
		return sessionActivityItems;
	}
	public void setSessionActivityItems(Set<SessionActivityItem> sessionActivityItems) {
		this.sessionActivityItems = sessionActivityItems;
	}
	
}


