package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class EventMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8945989654492560094L;
	private Event event;
	private Template template;
	private String data;
	private User associatedBy;
	private CustomTableValue status;
	private Date createdDate;
	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setAssociatedBy(User associatedBy) {
		this.associatedBy = associatedBy;
	}

	public User getAssociatedBy() {
		return associatedBy;
	}

}
