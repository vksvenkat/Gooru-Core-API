/*******************************************************************************
 * SearchResultActivity.java
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

import org.ednovo.gooru.core.api.model.IndexableEntry;


public class SearchResultActivity implements IndexableEntry,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8205404722642577622L;
	/**
	 * 
	 */
	
	
	private long id;
	private String resultUId;
	private String userAction;
	private Date userActionTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getResultUId() {
		return resultUId;
	}
	public void setResultUId(String resultUId) {
		this.resultUId = resultUId;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public Date getUserActionTime() {
		return userActionTime;
	}
	public void setUserActionTime(Date userActionTime) {
		this.userActionTime = userActionTime;
	}
	/* (non-Javadoc)
	 * @see org.ednovo.gooru.domain.model.IndexableEntry#getEntryId()
	 */
	@Override
	public String getEntryId() {
		// TODO Auto-generated method stub
		return null;
	}
}
