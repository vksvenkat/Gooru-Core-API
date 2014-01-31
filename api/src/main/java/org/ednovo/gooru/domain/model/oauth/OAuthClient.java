/////////////////////////////////////////////////////////////
// OAuthClient.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.model.oauth;

import java.io.Serializable;

import javax.persistence.Entity;

import org.ednovo.gooru.core.api.model.User;

@Entity(name="oauthClient")
public class OAuthClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7173591300165688574L;
	
	private String oauthClientUId;
	
	private String clientId;
	
	private String clientName;
	
	private String description;
	
	private String clientSecret;
	
	private String userUid;
	
	private String scopes;
	
	private String grantTypes;
	
	private String authorities;
	
	private String redirectUris;
	
	private Integer accessTokenValiditySeconds;
	
	private Integer refreshTokenValiditySeconds;
	
	private User user;
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	
	public String getUserUid() {
		return userUid;
	}
	
	public void setOauthClientUId(String oauthClientUId) {
		this.oauthClientUId = oauthClientUId;
	}
	
	public String getOauthClientUId() {
		return oauthClientUId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setScopes(String scopes) {
		this.scopes = scopes;
	}

	public String getScopes() {
		return scopes;
	}

	public void setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getGrantTypes() {
		return grantTypes;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setRedirectUris(String redirectUris) {
		this.redirectUris = redirectUris;
	}

	public String getRedirectUris() {
		return redirectUris;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}


}
