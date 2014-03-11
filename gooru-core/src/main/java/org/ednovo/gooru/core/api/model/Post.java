/*******************************************************************************
 * Post.java
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

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.CustomTableValue;


public class Post extends Content implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2120041760899270990L;
	private String title;
	private String freeText;
	private CustomTableValue type;
	private String assocGooruOid;
	private String assocUserUid;
	private CustomTableValue target;
	private CustomTableValue status;
	

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String postTitle) {
		this.title = postTitle;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String postText) {
		this.freeText = postText;
	}

	public String getAssocGooruOid() {
		return assocGooruOid;
	}

	public void setAssocGooruOid(String assocGooruOid) {
		this.assocGooruOid = assocGooruOid;
	}

	public String getAssocUserUid() {
		return assocUserUid;
	}

	public void setAssocUserUid(String assocUserUid) {
		this.assocUserUid = assocUserUid;
	}

	public void setTarget(CustomTableValue target) {
		this.target = target;
	}

	public CustomTableValue getTarget() {
		return target;
	}

	public void setType(CustomTableValue postType) {
		this.type = postType;
	}

	public CustomTableValue getType() {
		return type;
	}

}
