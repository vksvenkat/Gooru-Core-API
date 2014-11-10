package org.ednovo.gooru.core.api.model;

import java.util.Date;

public class QuoteDTO {
	
	private String title;
	private String text;
	private String url;
	private String tag;
	private String anchor;
	private String gooruOid;
	private String sharing;
	private String contextAnchorText;
	private Date createdOn;
	private User user;
	private String notesType;
	
	
	public String getNotesType() {
		return notesType;
	}
	public void setNotesType(String notesType) {
		this.notesType = notesType;
	}
	public String getContextAnchorText() {
		return contextAnchorText;
	}
	public void setContextAnchorText(String contextAnchorText) {
		this.contextAnchorText = contextAnchorText;
	}
	public String getSharing() {
		return sharing;
	}
	public void setSharing(String sharing) {
		this.sharing = sharing;
	}
	public String getGooruOid() {
		return gooruOid;
	}
	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
}
