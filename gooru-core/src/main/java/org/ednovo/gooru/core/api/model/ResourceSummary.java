package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ResourceSummary implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 8077158217695385596L;
	
	private String resourceSummaryUid;
	
	private String gooruOid;
	
	private Long ratingStarAvg;
	
	private Double ratingStarCount;

	public void setResourceSummaryUid(String resourceSummaryUid) {
		this.resourceSummaryUid = resourceSummaryUid;
	}

	public String getResourceSummaryUid() {
		return resourceSummaryUid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setRatingStarAvg(Long ratingStarAvg) {
		this.ratingStarAvg = ratingStarAvg;
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

}
