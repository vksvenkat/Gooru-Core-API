/*******************************************************************************
 * ResourceMetaData.java
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

import com.fasterxml.jackson.annotation.JsonBackReference;

public class ResourceMetaData  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3517417646072932821L;

	private String metaKey;
	private String metaContent;
	private Integer resourceMetaId;
	@JsonBackReference
	private Resource resource;
	
	public String getMetaKey() {
		return metaKey;
	}
	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}
	public String getMetaContent() {
		return metaContent;
	}
	public void setMetaContent(String metaContent) {
		this.metaContent = metaContent;
	}
	public Integer getResourceMetaId() {
		return resourceMetaId;
	}
	public void setResourceMetaId(Integer resourceMetaId) {
		this.resourceMetaId = resourceMetaId;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
