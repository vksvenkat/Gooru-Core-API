/*******************************************************************************
 * TagSynonyms.java
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

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.User;


public class TagSynonyms implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6715445251063919884L;

	private Integer tagSynonymsId;
	
	private Date createdOn;
	
	private Date approvalOn;
	
	private String targetTagName;
	
	private User creator;
	
	private String  tagContentGooruOid;
	
	private User approver;
	
	private CustomTableValue status;

	public Integer getTagSynonymsId() {
		return tagSynonymsId;
	}

	public void setTagSynonymsId(Integer tagSynonymsId) {
		this.tagSynonymsId = tagSynonymsId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getApprovalOn() {
		return approvalOn;
	}

	public void setApprovalOn(Date approvalOn) {
		this.approvalOn = approvalOn;
	}

	public String getTargetTagName() {
		return targetTagName;
	}

	public void setTargetTagName(String targetTagName) {
		this.targetTagName = targetTagName;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public void setTagContentGooruOid(String tagContentGooruOid) {
		this.tagContentGooruOid = tagContentGooruOid;
	}

	public String getTagContentGooruOid() {
		return tagContentGooruOid;
	}

}
