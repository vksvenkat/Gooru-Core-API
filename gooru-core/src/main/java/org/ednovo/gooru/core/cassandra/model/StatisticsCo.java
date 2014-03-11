/*******************************************************************************
 * StatisticsCo.java
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

}
