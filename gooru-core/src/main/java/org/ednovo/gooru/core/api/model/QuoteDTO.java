/*******************************************************************************
 * QuoteDTO.java
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

import org.ednovo.gooru.core.api.model.User;

public class QuoteDTO {
	
	private String title;
	private String text;
	private String url;
	private String tag;
	private String anchor;
	private String gooruOid;
	private String sharing;
	private String contextAnchorText;
	private Date createdOn;
	private User user;
	private String notesType;
	
	
	public String getNotesType() {
		return notesType;
	}
	public void setNotesType(String notesType) {
		this.notesType = notesType;
	}
	public String getContextAnchorText() {
		return contextAnchorText;
	}
	public void setContextAnchorText(String contextAnchorText) {
		this.contextAnchorText = contextAnchorText;
	}
	public String getSharing() {
		return sharing;
	}
	public void setSharing(String sharing) {
		this.sharing = sharing;
	}
	public String getGooruOid() {
		return gooruOid;
	}
	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
}
