/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "stas")
public class ResourceStasCo {

	@Column
	private Long viewsCount;
	
	@Column
	private Long subscriberCount;
	
	@Column
	private String rating;

	public Long getViewsCount() {
		return viewsCount;
	}

	public void setViewsCount(Long viewsCount) {
		this.viewsCount = viewsCount;
	}

	public Long getSubscriberCount() {
		return subscriberCount;
	}

	public void setSubscriberCount(Long subscriberCount) {
		this.subscriberCount = subscriberCount;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRating() {
		return rating;
	}

}