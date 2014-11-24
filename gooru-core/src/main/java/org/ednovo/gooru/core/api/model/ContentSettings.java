package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentSettings implements Serializable {

	private static final long serialVersionUID = 4169161021481625838L;

	private Content content;
	private String data;
	private String comment;
	private String mailNotification;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(String mailNotification) {
		this.mailNotification = mailNotification;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
