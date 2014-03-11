/*******************************************************************************
 * Quote.java
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
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.TagType;

public class Quote extends Annotation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154603807013779771L;

	private String grade;
	private String title;
	private String topic;
	private String contextAnchor;
	private String contextAnchorText;

	public String getContextAnchor() {
		return contextAnchor;
	}

	public void setContextAnchor(String contextAnchor) {
		this.contextAnchor = contextAnchor;
	}

	public String getContextAnchorText() {
		return contextAnchorText;
	}

	public void setContextAnchorText(String contextAnchorText) {
		this.contextAnchorText = contextAnchorText;
	}

	private Content context;
	private License license;
	private TagType tagType;

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	@Override
	public String toString() {
		return "quote_id:";
	}

	public Content getContext() {
		return context;
	}

	public void setContext(Content resource) {
		this.context = resource;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License licenseName) {
		license = licenseName;
	}
}
