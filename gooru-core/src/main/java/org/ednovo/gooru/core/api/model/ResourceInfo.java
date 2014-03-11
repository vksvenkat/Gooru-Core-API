/*******************************************************************************
 * ResourceInfo.java
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

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonFilter;

import flexjson.JSON;

@JsonFilter("resourceInfo")
@Entity(name = "resourceInfo")
public class ResourceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1351807396836836051L;


	private Long resourceInfoId;

	private Resource resource;

	private long viewCount;

	private long subscribeCount;

	private int voteUp;

	private int voteDown;

	@Column
	private String text;

	@Column
	private String tags;

	@Column
	private Integer numOfPages;

	private Date lastUpdated;
	
	@JSON(include=false)
	public Long getResourceInfoId() {
		return resourceInfoId;
	}

	public void setResourceInfoId(Long resourceInfoId) {
		this.resourceInfoId = resourceInfoId;
	}

	@JSON(include=false)
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@JSON(include=false)
	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	@JSON(include=false)
	public long getSubscribeCount() {
		return subscribeCount;
	}

	public void setSubscribeCount(long subscribeCount) {
		this.subscribeCount = subscribeCount;
	}

	@JSON(include=false)
	public int getVoteUp() {
		return voteUp;
	}

	public void setVoteUp(int voteUp) {
		this.voteUp = voteUp;
	}

	@JSON(include=false)
	public int getVoteDown() {
		return voteDown;
	}

	public void setVoteDown(int voteDown) {
		this.voteDown = voteDown;
	}

	@JSON(include=true)
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@JSON(include=false)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Integer getNumOfPages() {
		return numOfPages;
	}

	public void setNumOfPages(Integer numOfPages) {
		this.numOfPages = numOfPages;
	}

}
