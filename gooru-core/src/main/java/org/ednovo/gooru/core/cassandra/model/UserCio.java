/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.USER)
public class UserCio implements IsEntityCassandraIndexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5319665621281054134L;
	
	private static final String ORG_PARTY_UID = "organization.partyUid";
	
	private static final String PARTY_UID = "partyUid";
	
	@Id
	private String userUid;

	@Column
	private Integer userId;

	@Column
	private Integer accountTypeId;
		
	@Column
	private String displayname;

	@Column
	private String firstname;

	@Column
	private String lastname;

	@Column
	private String emailId;
	
	@Column
	private String userName;

	@Column
	private String accountId;
	
	@Column 
	private String confirmStatus;
	
	@Column
	private String userProfileImage;
	
	@Column
	private String roleSet;
	
	@Column 
	private List<String> roleIds;
	
	@Column
	private String grade;
	
	@Column 
	private String network;
	
	@Column
	private Date createdOn;
	
	@Column
	private List<Map<String, String>> partyPermissions;
	
	@Column
	private Map<String,String> organization;
	
	@Column
	private String versionUid;
	
	@Column
	private Date lastLogin;
	
	@Column
	private boolean isDeleted;
	
	@Column
	private String accountRegisterType;
	
	@Column
	private String aboutMe;
	
	@Column
	private String notes;
	
	@Column
	private String profileVisibility;
	
	@Column
	private Date lastModifiedOn;
	
	@Column
	private String metaJson;
	
	@Column 
	private Short active;
	
	@Column 
	private String parentAccountUserName;
	
	@Column 
	private Integer childAccountCount;
	

	public Short getActive() {
		return active;
	}

	public void setActive(Short active) {
		this.active = active;
	}

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
	
	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
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

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public String getUserProfileImage() {
		return userProfileImage;
	}

	public void setUserProfileImage(String userProfileImage) {
		this.userProfileImage = userProfileImage;
	}

	public String getRoleSet() {
		return roleSet;
	}

	public void setRoleSet(String roleSet) {
		this.roleSet = roleSet;
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

	public List<Map<String, String>> getPartyPermissions() {
		return partyPermissions;
	}

	public void setPartyPermissions(List<Map<String, String>> partyPermissions) {
		this.partyPermissions = partyPermissions;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setOrganization(Map<String,String> organization) {
		this.organization = organization;
	}

	public Map<String,String> getOrganization() {
		return organization;
	}

	@Override
	public String getIndexId() {
		return getUserUid();
	}

	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.USER;
	}

	public void setVersionUid(String versionUid) {
		this.versionUid = versionUid;
	}

	public String getVersionUid() {
		return versionUid;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogin() {
		return lastLogin;
	}
	
	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>(1);
		riFields.put(ORG_PARTY_UID, getOrganization().get(PARTY_UID));
		return riFields;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
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

	public Date getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(Date lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getMetaJson() {
		return metaJson;
	}

	public void setMetaJson(String metaJson) {
		this.metaJson = metaJson;
	}
	
	public String getParentAccountUserName() {
		return parentAccountUserName;
	}

	public void setParentAccountUserName(String parentAccountUserName) {
		this.parentAccountUserName = parentAccountUserName;
	}

	public Integer getChildAccountCount() {
		return childAccountCount;
	}

	public void setChildAccountCount(Integer childAccountCount) {
		this.childAccountCount = childAccountCount;
	}

}