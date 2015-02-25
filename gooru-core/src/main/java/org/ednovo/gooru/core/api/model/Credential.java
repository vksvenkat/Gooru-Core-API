package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("credential")
public class Credential implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8921194097519990459L;

	private Integer id; // same as identity id in database. 
	@JsonBackReference
	private Identity identity;
	private String password;
	private String token;
	private Date resetPasswordRequestDate;
	private String passwordEncryptType;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getResetPasswordRequestDate() {
		return resetPasswordRequestDate;
	}
	public void setResetPasswordRequestDate(Date resetPasswordRequestDate) {
		this.resetPasswordRequestDate = resetPasswordRequestDate;
	}
	
	public Identity getIdentity() {
		return identity;
	}
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPasswordEncryptType() {
		return passwordEncryptType;
	}
	public void setPasswordEncryptType(String passwordEncryptType) {
		this.passwordEncryptType = passwordEncryptType;
	}
	
	
}
