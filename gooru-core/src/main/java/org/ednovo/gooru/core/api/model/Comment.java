/*******************************************************************************
 * Comment.java
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


public class Comment extends OrganizationModel implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -2331723976031708433L;

	private String comment;
	private String commentUid;
	private User commentorUid;
	private Date createdOn;
	private CustomTableValue status;
	private String gooruOid;
	private Boolean isDeleted;
	private Date lastModifiedOn;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCommentUid() {
		return commentUid;
	}
	public void setCommentUid(String commentUid) {
		this.commentUid = commentUid;
	}
	public User getCommentorUid() {
		return commentorUid;
	}
	public void setCommentorUid(User commentorUid) {
		this.commentorUid = commentorUid;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public CustomTableValue getStatus() {
		return status;
	}
	public void setStatus(CustomTableValue status) {
		this.status = status;
	}
	public String getGooruOid() {
		return gooruOid;
	}
	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean i) {
		this.isDeleted = i;
	}
	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	
}
