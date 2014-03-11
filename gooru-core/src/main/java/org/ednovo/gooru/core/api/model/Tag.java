/*******************************************************************************
 * Tag.java
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


public class Tag extends Content implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8570184385077696374L;


	private String label;
	
	private CustomTableValue tagType;

	private CustomTableValue status;

	private Long contentCount;
	
	private Long userCount;
	
	private Integer synonymsCount;

	private String  wikiPostGooruOid;
	
	private String excerptPostGooruOid;


	

	public Long getContentCount() {
		return contentCount;
	}

	public void setContentCount(Long contentCount) {
		this.contentCount = contentCount;
	}

	public Long getUserCount() {
		return userCount;
	}

	public void setUserCount(Long userCount) {
		this.userCount = userCount;
	}

	public Integer getSynonymsCount() {
		return synonymsCount;
	}

	public void setSynonymsCount(Integer synonymsCount) {
		this.synonymsCount = synonymsCount;
	}

	public String getWikiPostGooruOid() {
		return wikiPostGooruOid;
	}

	public void setWikiPostGooruOid(String wikiPostGooruOid) {
		this.wikiPostGooruOid = wikiPostGooruOid;
	}

	public String getExcerptPostGooruOid() {
		return excerptPostGooruOid;
	}

	public void setExcerptPostGooruOid(String excerptPostGooruOid) {
		this.excerptPostGooruOid = excerptPostGooruOid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public CustomTableValue getTagType() {
		return tagType;
	}

	public void setTagType(CustomTableValue tagType) {
		this.tagType = tagType;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}


}
