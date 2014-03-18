package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class InviteUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4627013229669491613L;

	private String inviteUid;
	private String email;
	private String gooruOid;
	private String invitationType;
	private Date createdDate;
	private Date joinedDate;
	private CustomTableValue status;
	private User associatedUser;

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setInvitationType(String invitationType) {
		this.invitationType = invitationType;
	}

	public String getInvitationType() {
		return invitationType;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setInviteUid(String inviteUid) {
		this.inviteUid = inviteUid;
	}

	public String getInviteUid() {
		return inviteUid;
	}

	public void setAssociatedUser(User associatedUser) {
		this.associatedUser = associatedUser;
	}

	public User getAssociatedUser() {
		return associatedUser;
	}


}
