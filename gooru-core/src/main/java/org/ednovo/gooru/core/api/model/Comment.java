package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class Comment extends OrganizationModel implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -2331723976031708433L;

	private String comment;
	private String commentUid;
	private User commentorUid;
	private Date createdOn;
	private CustomTableValue status;
	private String gooruOid;
	private String itemId;
	private Boolean isDeleted;
	private Date lastModifiedOn;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCommentUid() {
		return commentUid;
	}
	public void setCommentUid(String commentUid) {
		this.commentUid = commentUid;
	}
	public User getCommentorUid() {
		return commentorUid;
	}
	public void setCommentorUid(User commentorUid) {
		this.commentorUid = commentorUid;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public CustomTableValue getStatus() {
		return status;
	}
	public void setStatus(CustomTableValue status) {
		this.status = status;
	}
	public String getGooruOid() {
		return gooruOid;
	}
	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean i) {
		this.isDeleted = i;
	}
	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return itemId;
	}

	
}
