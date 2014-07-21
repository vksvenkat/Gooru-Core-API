package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class StatisticsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8920979831518583422L;

	private String gooruOid;
	
	private String resourceType;
	
	private Long views;
	
	private Long subscription;
	
	private Long voteUp;
	
	private Integer collabrator;
	
	private Long voteDown;

	public String getGooruOid() {
		return gooruOid;
	}
	
	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	public Long getViews() {
		return views;
	}
	
	public void setViews(Long views) {
		this.views = views;
	}
	
	public Long getSubscription() {
		return subscription;
	}
	
	public void setSubscription(Long subscription) {
		this.subscription = subscription;
	}
	
	public Long getVoteUp() {
		return voteUp;
	}
	
	public void setVoteUp(Long voteUp) {
		this.voteUp = voteUp;
	}
	
	public Integer getCollabrator() {
		return collabrator;
	}
	
	public void setCollabrator(Integer collabrator) {
		this.collabrator = collabrator;
	}
	
	public Long getVoteDown() {
		return voteDown;
	}
	
	public void setVoteDown(Long voteDown) {
		this.voteDown = voteDown;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public boolean isValid(){
		return views != null || voteDown != null || voteUp != null || collabrator != null || subscription != null;
	}
	
}
