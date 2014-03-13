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
