package org.ednovo.gooru.core.security;

import org.ednovo.gooru.core.api.model.UserCredential;
import org.ednovo.gooru.core.api.model.UserToken;

public class AuthenticationDo {

	private UserToken userToken;

	private UserCredential userCredential;
	
	public UserToken getUserToken() {
		return userToken;
	}

	public void setUserToken(UserToken userToken) {
		this.userToken = userToken;
	}

	public void setUserCredential(UserCredential userCredential) {
		this.userCredential = userCredential;
	}

	public UserCredential getUserCredential() {
		return userCredential;
	}

	
}
