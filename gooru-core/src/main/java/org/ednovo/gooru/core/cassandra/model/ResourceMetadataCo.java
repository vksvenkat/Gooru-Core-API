/*******************************************************************************
 * ResourceMetadataCo.java
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
package org.ednovo.gooru.core.cassandra.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "resourceMetadata")
public class ResourceMetadataCo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3255773550245809347L;
	
	@Id
	private String id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private Long viewCount=0L;

	@Column
	private Long likeCount=0L;

	@Column
	private Long dislikeCount=0L;

	@Column
	private Long favoriteCount=0L;
	
	@Column
	private double ratingAverage; 
	
	@Column
	private Integer urlStatus=0;
	
	@Column
	private Long duration=0L;

	@Column
	private String hasNoThumbnails;
	
	@Column
	private String hasAdvertisement;
	
	@Column
	private String hasCopyright;
	
	@Column
	private String contentClarity;
	
	@Column 
	private String mediaClarity;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public Long getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(Long dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public Long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(Long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public double getRatingAverage() {
		return ratingAverage;
	}

	public void setRatingAverage(double ratingAverage) {
		this.ratingAverage = ratingAverage;
	}

	public Integer getUrlStatus() {
		return urlStatus;
	}

	public void setUrlStatus(Integer urlStatus) {
		this.urlStatus = urlStatus;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getHasNoThumbnails() {
		return hasNoThumbnails;
	}

	public void setHasNoThumbnails(String hasNoThumbnails) {
		this.hasNoThumbnails = hasNoThumbnails;
	}

	public String getHasAdvertisement() {
		return hasAdvertisement;
	}

	public void setHasAdvertisement(String hasAdvertisement) {
		this.hasAdvertisement = hasAdvertisement;
	}

	public String getHasCopyright() {
		return hasCopyright;
	}

	public void setHasCopyright(String hasCopyright) {
		this.hasCopyright = hasCopyright;
	}

	public String getContentClarity() {
		return contentClarity;
	}

	public void setContentClarity(String contentClarity) {
		this.contentClarity = contentClarity;
	}

	public String getMediaClarity() {
		return mediaClarity;
	}

	public void setMediaClarity(String mediaClarity) {
		this.mediaClarity = mediaClarity;
	}

}
