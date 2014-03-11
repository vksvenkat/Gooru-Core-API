/*******************************************************************************
 * InviteUser.java
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

public class InviteUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4627013229669491613L;

	private String inviteUid;
	private String email;
	private String gooruOid;
	private String invitationType;
	private Date createdDate;
	private Date joinedDate;
	private CustomTableValue status;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setInvitationType(String invitationType) {
		this.invitationType = invitationType;
	}

	public String getInvitationType() {
		return invitationType;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setInviteUid(String inviteUid) {
		this.inviteUid = inviteUid;
	}

	public String getInviteUid() {
		return inviteUid;
	}


}
