/////////////////////////////////////////////////////////////
// OAuthRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.auth;

import java.util.List;

import org.ednovo.gooru.core.api.model.OAuthClient;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface OAuthRepository extends BaseRepository {

	public Boolean checkTokenExists(String accessToken);

	public String findClientByAccessToken(String accessToken);
	
	public OAuthClient findOAuthClientByClientId(String clientId);
	
	public List<OAuthClient> listOAuthClient(String gooruUId, int pageNo, int pageSize);
	
	public OAuthClient findOAuthClientByclientSecret(String clientSecret);
	
	List<OAuthClient> listOAuthClientByOrganization(String organizationUId, Integer offset, Integer limit, String grantType);
	
	public Long getOauthClientCount(String organizationUId, String grantType);
	
	public OAuthClient findOAuthClientByOauthKey(String oauthKey);
	
	List<OAuthClient> findOAuthClientByApplicationKey(String apiKey);

	
}
