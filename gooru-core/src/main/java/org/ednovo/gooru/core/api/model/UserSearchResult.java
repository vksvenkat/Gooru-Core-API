package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.User;

public class UserSearchResult {

	private User user;
	private String userId;
	private String firstName;
	private String lastName;
	private String userName;
	private String gooruUId;
	private String accountId;
	private String grade;
	private String network;
	private String emailId;
	private String createdOn;
	private String roleSet;
	private String confirmStatus;
	private String profileImageUrl;
	private String resultUId;
	private String lastLogin;
	private String isDeleted;
	private String accountRegisterType;
	private String aboutMe;
	private String notes;
	private String profileVisibility;
	
	public String getConfirmStatus() {
		return confirmStatus;
	}
	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getGooruUId() {
		return gooruUId;
	}
	public void setGooruUId(String gooruUId) {
		this.gooruUId = gooruUId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}	
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getRoleSet() {
		return roleSet;
	}
	public void setRoleSet(String roleSet) {
		this.roleSet = roleSet;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getResultUId() {
		return resultUId;
	}
	public void setResultUId(String resultUId) {
		this.resultUId = resultUId;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public String getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getAccountRegisterType() {
		return accountRegisterType;
	}
	public void setAccountRegisterType(String accountRegisterType) {
		this.accountRegisterType = accountRegisterType;
	}
	public String getAboutMe() {
		return aboutMe;
	}
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getProfileVisibility() {
		return profileVisibility;
	}
	public void setProfileVisibility(String profileVisibility) {
		this.profileVisibility = profileVisibility;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
