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
