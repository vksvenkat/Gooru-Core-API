/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "stas")
public class ResourceStasCo {

	@Column
	private String viewsCount;
	
	@Column
	private String subscriberCount;
	
	@Column
	private String rating;

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRating() {
		return rating;
	}

	public void setViewsCount(String viewsCount) {
		this.viewsCount = viewsCount;
	}

	public String getViewsCount() {
		return viewsCount;
	}

	public void setSubscriberCount(String subscriberCount) {
		this.subscriberCount = subscriberCount;
	}

	public String getSubscriberCount() {
		return subscriberCount;
	}

}