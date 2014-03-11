/*******************************************************************************
 * Identity.java
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

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class Identity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6030700900599262405L;

	
	private Integer identityId;
	private Short active = 1;
	private String firstName;
	private String lastName;
	private String externalId;
	private Date registeredOn;
	private Date deactivatedOn;
	private Date lastLogin;
	private String loginType;
	private Idp idp;	
	@JsonBackReference
	private User user;
	private Credential credential;
	private String accountCreatedType;
	private String ssoEmailId;
	
	public Integer getIdentityId() {
		return identityId;
	}
	public void setIdentityId(Integer identityId) {
		this.identityId = identityId;
	}
	public Short getActive() {
		return active;
	}
	public void setActive(Short active) {
		this.active = active;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String name) {
		this.firstName = name;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {		
		this.externalId = externalId;		
	}
	public Date getRegisteredOn() {
		return registeredOn;
	}
	public void setRegisteredOn(Date registeredOn) {
		this.registeredOn = registeredOn;
	}
	public Date getDeactivatedOn() {
		return deactivatedOn;
	}
	public void setDeactivatedOn(Date deactivatedOn) {
		this.deactivatedOn = deactivatedOn;
	}
	public Idp getIdp() {
		return idp;
	}
	public void setIdp(Idp idp) {
		this.idp = idp;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Credential getCredential() {
		return credential;
	}
	public void setCredential(Credential credential) {
		this.credential = credential;
	}
	
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setAccountCreatedType(String accountCreatedType) {
		this.accountCreatedType = accountCreatedType;
	}
	public String getAccountCreatedType() {
		return accountCreatedType;
	}
	public void setSsoEmailId(String ssoEmailId) {
		this.ssoEmailId = ssoEmailId;
	}
	public String getSsoEmailId() {
		return ssoEmailId;
	}
}
