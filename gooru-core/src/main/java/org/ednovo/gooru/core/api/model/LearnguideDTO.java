/*******************************************************************************
 * LearnguideDTO.java
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

/**
 * DTO object for transferring data to the view layer.
  */
public class LearnguideDTO {

	private Long contentId;
	private String topic;
	private String lesson;
	private String title;
	private String goals;
	private String folder;
	private Short distinguish;
	private String gooruOId;
	private String ratingCount;
	private String ratingAverage;
	private String subscriptionCount;
	private boolean isSubscribed;

	public String getGooruOId() {
		return gooruOId;
	}

	public void setGooruOId(String gooruOId) {
		this.gooruOId = gooruOId;
	}

	public String getTopic() {
		return topic;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Short getDistinguish() {
		return distinguish;
	}

	public void setDistinguish(Short distinguish) {
		this.distinguish = distinguish;
	}

	public void setRatingCount(String count) {
		this.ratingCount = count;
	}

	public String getRatingCount() {
		return this.ratingCount;
	}

	public void setRatingAverage(String average) {
		this.ratingAverage = average;
	}

	public String getRatingAverage() {
		return this.ratingAverage;
	}

	public void setSubscriptionCount(String count) {
		this.subscriptionCount = count;
	}

	public String getSubscriptionCount() {
		return this.subscriptionCount;
	}

	public void setIsSubscribed(boolean val) {
		this.isSubscribed = val;
	}

	public boolean getIsSubscribed() {
		return this.isSubscribed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
