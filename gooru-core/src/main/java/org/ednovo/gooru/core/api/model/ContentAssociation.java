/*******************************************************************************
 * ContentAssociation.java
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

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.User;


public class ContentAssociation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 589060187780621481L;
	

	private Long contentAssociationId;
	private Content content;
	private Content associateContent;
	private User user;
	private String typeOf;
	private Date modifiedDate;
	
	/**
	 * 
	 */
	public ContentAssociation() {
	}

	public void setContentAssociationId(Long contentAssociationId) {
		this.contentAssociationId = contentAssociationId;
	}
	public Long getContentAssociationId() {
		return contentAssociationId;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTypeOf() {
		return typeOf;
	}
	public void setTypeOf(String typeOf) {
		this.typeOf = typeOf;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public void setAssociateContent(Content associateContent) {
		this.associateContent = associateContent;
	}
	public Content getAssociateContent() {
		return associateContent;
	}
	
}
