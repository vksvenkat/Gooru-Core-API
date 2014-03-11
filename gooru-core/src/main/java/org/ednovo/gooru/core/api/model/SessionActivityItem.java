/*******************************************************************************
 * SessionActivityItem.java
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

public class SessionActivityItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -419769806536998698L;


	private String sessionActivityItemUid;
	private SessionActivity sessionActivity;
	private String contentUid;
	private String subContentUid;
	private Integer questionAttemptId;
	private String contentType;
	private Date createdOn;
	
	public String getSessionActivityItemUid() {
		return sessionActivityItemUid;
	}

	public void setSessionActivityItemUid(String sessionActivityItemUid) {
		this.sessionActivityItemUid = sessionActivityItemUid;
	}

	public SessionActivity getSessionActivity() {
		return sessionActivity;
	}

	public void setSessionActivity(SessionActivity sessionActivity) {
		this.sessionActivity = sessionActivity;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setQuestionAttemptId(Integer questionAttemptId) {
		this.questionAttemptId = questionAttemptId;
	}

	public Integer getQuestionAttemptId() {
		return questionAttemptId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setContentUid(String contentUid) {
		this.contentUid = contentUid;
	}

	public String getContentUid() {
		return contentUid;
	}

	public void setSubContentUid(String subContentUid) {
		this.subContentUid = subContentUid;
	}

	public String getSubContentUid() {
		return subContentUid;
	}
}
