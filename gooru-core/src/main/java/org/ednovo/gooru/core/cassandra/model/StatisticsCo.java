/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "statistics")
public class StatisticsCo {

	@Column
	private String usedInCollectionCount;

	@Column
	private Integer usedInCollectionCountN;

	@Column
	private String usedInDistCollectionCount;

	@Column
	private Integer usedInDistCollectionCountN;

	@Column
	private Integer usedInSCollectionCountN;

	@Column
	private String usedInSCollectionCount;

	@Column
	private String viewsCount;

	@Column
	private String subscriberCount;

	@Column
	private String voteUp;

	@Column
	private String voteDown;

	@Column
	private String invalidResource;

	@Column
	private String hasNoThumbnail;

	@Column
	private Integer hasNoThumbnailN;

	@Column
	private String hasNoDescription;

	@Column
	private String hasFrameBreaker;

	@Column
	private Integer hasFrameBreakerN;
	
	@Column
	private Integer statusIsBroken;

	@Column
	private long copiedLevelCount;
	
	@Column
	private long copiedCount;

	public String getUsedInCollectionCount() {
		return usedInCollectionCount;
	}

	public void setUsedInCollectionCount(String usedInCollectionCount) {
		this.usedInCollectionCount = usedInCollectionCount;
	}

	public Integer getUsedInCollectionCountN() {
		return usedInCollectionCountN;
	}

	public void setUsedInCollectionCountN(Integer usedInCollectionCountN) {
		this.usedInCollectionCountN = usedInCollectionCountN;
	}

	public String getUsedInDistCollectionCount() {
		return usedInDistCollectionCount;
	}

	public void setUsedInDistCollectionCount(String usedInDistCollectionCount) {
		this.usedInDistCollectionCount = usedInDistCollectionCount;
	}

	public Integer getUsedInDistCollectionCountN() {
		return usedInDistCollectionCountN;
	}

	public void setUsedInDistCollectionCountN(Integer usedInDistCollectionCountN) {
		this.usedInDistCollectionCountN = usedInDistCollectionCountN;
	}

	public Integer getUsedInSCollectionCountN() {
		return usedInSCollectionCountN;
	}

	public void setUsedInSCollectionCountN(Integer usedInSCollectionCountN) {
		this.usedInSCollectionCountN = usedInSCollectionCountN;
	}

	public String getUsedInSCollectionCount() {
		return usedInSCollectionCount;
	}

	public void setUsedInSCollectionCount(String usedInSCollectionCount) {
		this.usedInSCollectionCount = usedInSCollectionCount;
	}

	public String getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(String viewsCount) {
		this.viewsCount = viewsCount;
	}

	public String getSubscriberCount() {
		return subscriberCount;
	}

	public void setSubscriberCount(String subscriberCount) {
		this.subscriberCount = subscriberCount;
	}

	public String getVoteUp() {
		return voteUp;
	}

	public void setVoteUp(String voteUp) {
		this.voteUp = voteUp;
	}

	public String getVoteDown() {
		return voteDown;
	}

	public void setVoteDown(String voteDown) {
		this.voteDown = voteDown;
	}

	public String getInvalidResource() {
		return invalidResource;
	}

	public void setInvalidResource(String invalidResource) {
		this.invalidResource = invalidResource;
	}

	public String getHasNoThumbnail() {
		return hasNoThumbnail;
	}

	public void setHasNoThumbnail(String hasNoThumbnail) {
		this.hasNoThumbnail = hasNoThumbnail;
	}

	public Integer getHasNoThumbnailN() {
		return hasNoThumbnailN;
	}

	public void setHasNoThumbnailN(Integer hasNoThumbnailN) {
		this.hasNoThumbnailN = hasNoThumbnailN;
	}

	public String getHasNoDescription() {
		return hasNoDescription;
	}

	public void setHasNoDescription(String hasNoDescription) {
		this.hasNoDescription = hasNoDescription;
	}

	public String getHasFrameBreaker() {
		return hasFrameBreaker;
	}

	public void setHasFrameBreaker(String hasFrameBreaker) {
		this.hasFrameBreaker = hasFrameBreaker;
	}

	public Integer getHasFrameBreakerN() {
		return hasFrameBreakerN;
	}

	public void setHasFrameBreakerN(Integer hasFrameBreakerN) {
		this.hasFrameBreakerN = hasFrameBreakerN;
	}

	public Integer getStatusIsBroken() {
		return statusIsBroken;
	}

	public void setStatusIsBroken(Integer statusIsBroken) {
		this.statusIsBroken = statusIsBroken;
	}

	public long getCopiedLevelCount(){
		return copiedLevelCount;
	}
	
	public void setCopiedLevelCount(long copiedLevelCount){
		this.copiedLevelCount = copiedLevelCount;
	}
	
	public long getCopiedCount(){
		return copiedCount;
	}
	
	public void setCopiedCount(long copiedCount){
		this.copiedCount = copiedCount;
	}
	

}