package org.ednovo.gooru.core.api.model;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class GooruAuthenticationToken extends UsernamePasswordAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1226821636654781499L;
	/**
	 * 
	 */
	

	private UserCredential userCredential;
	
    private String errorMessage;
    
    private int  errorCode;
    
	public GooruAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> credentials, String errorMessage, int errorCode) { 
		super(principal, "", credentials);
		this.errorMessage = errorMessage;
		this.setErrorCode(errorCode);
	}
	
	public GooruAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> credentials, UserCredential userCredential) {
		super(principal, "", credentials);
		this.userCredential = userCredential;
		if (userCredential.getUserUid() == null) {
			this.userCredential.setUserUid((String) principal);
		}
	}

	public UserCredential getUserCredential() {
		return userCredential;
	}

	public void setUserCredential(UserCredential userCredential) {
		this.userCredential = userCredential;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
