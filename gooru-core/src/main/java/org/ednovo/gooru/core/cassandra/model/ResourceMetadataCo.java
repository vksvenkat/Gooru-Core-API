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
	
	private String thumbnail;
	
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

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

}