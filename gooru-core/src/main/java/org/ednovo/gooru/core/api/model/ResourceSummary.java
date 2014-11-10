package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ResourceSummary implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 8077158217695385596L;
	
	private String resourceGooruOid;
	
	private Long ratingStarAvg;
	
	private Double ratingStarCount;
	
	private Long reviewCount;


	public void setRatingStarAvg(Long ratingStarAvg) {
		this.ratingStarAvg = ratingStarAvg;
	}

	public Long getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Long reviewCount) {
		this.reviewCount = reviewCount;
	}

	public Long getRatingStarAvg() {
		return ratingStarAvg;
	}

	public void setRatingStarCount(Double ratingStarCount) {
		this.ratingStarCount = ratingStarCount;
	}

	public Double getRatingStarCount() {
		return ratingStarCount;
	}

	public void setResourceGooruOid(String resourceGooruOid) {
		this.resourceGooruOid = resourceGooruOid;
	}

	public String getResourceGooruOid() {
		return resourceGooruOid;
	}

}
