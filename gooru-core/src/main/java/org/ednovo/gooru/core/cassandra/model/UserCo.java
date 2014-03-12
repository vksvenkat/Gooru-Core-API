/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.USER)
public class UserCo {

	@Column
	private String userUid;

	@Column
	private Integer userId;

	@Column
	private String displayname;

	@Column
	private String firstname;

	@Column
	private String lastname;

	@Column
	private String organizationUid;
	
	@Column
	private String emailId;
	
	@Column
	private String userName;
	
	@Column
	private String profileVisibility;

	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public String getProfileVisibility() {
		return profileVisibility;
	}

	public void setProfileVisibility(String profileVisibility) {
		this.profileVisibility = profileVisibility;
	}

}