/*******************************************************************************
 * UserRelationship.java
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

import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.User;


public class UserRelationship extends OrganizationModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1878026632439748759L;
	private Integer userRelationshipId;
	private User user;
	private Party followOnUser;
	private Date activatedDate;
	private Date deactivatedDate;
	private Boolean activeFlag = true;

	public Integer getUserRelationshipId() {
		return userRelationshipId;
	}

	public void setUserRelationshipId(Integer userRelationshipId) {
		this.userRelationshipId = userRelationshipId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Party getFollowOnUser() {
		return followOnUser;
	}

	public void setFollowOnUser(Party followOnUser) {
		this.followOnUser = followOnUser;
	}

	public Date getActivatedDate() {
		return activatedDate;
	}

	public void setActivatedDate(Date activatedDate) {
		this.activatedDate = activatedDate;
	}

	public Date getDeactivatedDate() {
		return deactivatedDate;
	}

	public void setDeactivatedDate(Date deactivatedDate) {
		this.deactivatedDate = deactivatedDate;
	}

	public Boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

}
