
/*******************************************************************************
 * UserTransModel.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;


import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import flexjson.JSON;

public class UserTransModel extends OrganizationModel implements IndexableEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1605240548956490842L;
	private Integer userId;
	private String gooruUId;
	private String partyUid;
	private String firstName;
	private String lastName;
	private String username;
	private String emailId = "";
	private Integer confirmStatus;
	private String registerToken;
	private UserRole userRole;
	private Set<Identity> identities;
	private Set<Content> contentSet;
	private Set<UserRoleAssoc> userRoleSet;
	private String userRoleSetString;
	private User parentUser;
	private Integer accountTypeId;
	private String profileImageUrl;
	private Integer viewFlag;
	private String createdOn;
	private String loginType;
	private String accountCreatedType;
	private Boolean isDeleted;
	private Set<PartyCustomField> customFields;
	private Map<String, Object> meta;
	private String organizationName;
	private Short active;
	private String token;
	private Date registeredOn;

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof UserTransModel) {

			final UserTransModel other = (UserTransModel) obj;

			if (this.partyUid.equals(other.partyUid)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((partyUid == null) ? 0 : partyUid.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "gooru_uid:" + partyUid;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Set<Content> getContentSet() {
		return contentSet;
	}

	public void setContentSet(Set<Content> contentSet) {
		this.contentSet = contentSet;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getGooruUId() {
		return gooruUId;
	}

	@JSON(include = false)
	public Set<Identity> getIdentities() {
		return identities;
	}

	public void setIdentities(Set<Identity> identities) {
		this.identities = identities;
	}

	public void setGooruUId(String gooruId) {
		this.gooruUId = gooruId;
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

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	@JSON(include = false)
	public Set<UserRoleAssoc> getUserRoleSet() {
		return userRoleSet;
	}

	public void setUserRoleSet(Set<UserRoleAssoc> userRoleSet) {
		this.userRoleSet = userRoleSet;

		userRoleSetString = "";
		if (userRoleSet != null) {
			for (UserRoleAssoc userRoleAssoc : userRoleSet) {
				if (!userRoleSetString.isEmpty()) {
					userRoleSetString += ",";
				}
				userRoleSetString += userRoleAssoc.getRole().getName();
			}
		}
	}

	public String getUserRoleSetString() {
		return this.userRoleSetString;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public String getUsernameDisplay() {
		String usernameDisplay = username;
		if (username == null || username.isEmpty()) {
			String firstName = "";
			if (this.getFirstName() != null) {
				firstName = this.getFirstName();
				firstName = StringUtils.remove(firstName, " ");
			}
			String lastName = "";
			if (this.getLastName() != null) {
				lastName = this.getLastName();
			}

			usernameDisplay = firstName;
			if (lastName.length() > 0) {
				usernameDisplay = usernameDisplay + lastName.substring(0, 1);
			}
			if (usernameDisplay.length() > 20) {
				usernameDisplay = usernameDisplay.substring(0, 20);
			}
		}
		return usernameDisplay;
	}

	public void setRegisterToken(String registerToken) {
		this.registerToken = registerToken;
	}

	public String getRegisterToken() {
		return registerToken;
	}

	public void setConfirmStatus(Integer confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Integer getConfirmStatus() {
		return confirmStatus;
	}

	@Override
	public String getEntryId() {
		String id = null;
		if (userId != null) {
			id = userId.toString();
		}
		return id;
	}

	public User getParentUser() {
		return parentUser;
	}

	public void setParentUser(User parentUser) {
		this.parentUser = parentUser;
	}

	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getPartyUid() {
		return partyUid;
	}

	public void setPartyUid(String partyUid) {
		this.partyUid = partyUid;
	}

	public void setViewFlag(Integer viewFlag) {
		this.viewFlag = viewFlag;
	}

	public Integer getViewFlag() {
		return viewFlag;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public String getLoginType() {
		return loginType;
	}

	public String getAccountCreatedType() {
		return accountCreatedType;
	}

	public void setAccountCreatedType(String accountCreatedType) {
		this.accountCreatedType = accountCreatedType;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setCustomFields(Set<PartyCustomField> customFields) {
		this.customFields = customFields;
	}

	public Set<PartyCustomField> getCustomFields() {
		return customFields;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public void setActive(Short active) {
		this.active = active;
	}

	public Short getActive() {
		return active;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getRegisteredOn() {
		return registeredOn;
	}

	public void setRegisteredOn(Date registeredOn) {
		this.registeredOn = registeredOn;
	}

}
