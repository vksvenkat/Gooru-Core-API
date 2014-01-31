/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "feed")
public class FeedCo {

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private Long favoriteCount;

	@Column
	private Long viewCount;

	@Column
	private Integer urlStatus;

	@Column
	private Double ratingAverage;

	@Column
	private Long likeCount;

	@Column
	private Long dislikeCount;

	@Column
	private Long durationInSec;
	
	@Column
	private String hasAdvertisement;
	
	@Column
	private String hasCopyright;
	
	@Column
	private String contentClarity;
	
	@Column 
	private String mediaClarity;
	
	@Column
	private String text;

	public Long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(Long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public Integer getUrlStatus() {
		return urlStatus;
	}

	public void setUrlStatus(Integer urlStatus) {
		this.urlStatus = urlStatus;
	}

	public Double getRatingAverage() {
		return ratingAverage;
	}

	public void setRatingAverage(Double ratingAverage) {
		this.ratingAverage = ratingAverage;
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

	public Long getDurationInSec() {
		return durationInSec;
	}

	public void setDurationInSec(Long durationInSec) {
		this.durationInSec = durationInSec;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}