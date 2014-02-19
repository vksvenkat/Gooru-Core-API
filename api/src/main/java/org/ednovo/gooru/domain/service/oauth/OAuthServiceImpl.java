/////////////////////////////////////////////////////////////
// OAuthServiceImpl.java
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
package org.ednovo.gooru.domain.service.oauth;

import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.model.oauth.AuthorizationGrantType;
import org.ednovo.gooru.domain.model.oauth.GooruOAuthConsumerSecret;
import org.ednovo.gooru.domain.model.oauth.OAuthClient;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.auth.OAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class OAuthServiceImpl extends ServerValidationUtils implements OAuthService, ParameterProperties{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OAuthRepository oAuthRepository;
	
	@Override
	public User getUserByOAuthAccessToken(String accessToken) throws Exception {
		String clientId = oAuthRepository.findClientByAccessToken(accessToken);
		if(clientId == null){
			throw new RuntimeException("Client not found for given oauth token");
		}
		OAuthClient oAuthClient = oAuthRepository.findOAuthClientByClientId(clientId);
		User user = oAuthClient.getUser();
		if(user == null){
			throw new RuntimeException("User not found for given oauth token");
		}
		return user;
	}

	@Override
	public ActionResponseDTO<OAuthClient> createNewOAuthClient(OAuthClient oAuthClient) throws Exception {
		Errors errors = validateOAuthClient(oAuthClient);
		OAuthClient oAuthClientNew = new OAuthClient();
		if(!errors.hasErrors()){
			oAuthClientNew.setAccessTokenValiditySeconds(new Integer(86400));
			oAuthClientNew.setAuthorities("ROLE_CLIENT");
			StringBuilder grantType = new StringBuilder();
			if(oAuthClient.getGrantTypes() != null){
				String [] grantTypes = oAuthClient.getGrantTypes().split(",");
				for(int grantTypeCount = 0; grantTypeCount < grantTypes.length; grantTypeCount++){
					if(grantType.length() > 0){
						grantType.append(","); 
					}
					if(grantTypes[grantTypeCount].equals(AuthorizationGrantType.AUTHORIZATION_CODE.getAuthorizationGrantType())){
						grantType.append(AuthorizationGrantType.AUTHORIZATION_CODE.getAuthorizationGrantType());
					}
					else if(grantTypes[grantTypeCount].equals(AuthorizationGrantType.IMPLICIT.getAuthorizationGrantType())){
						grantType.append(AuthorizationGrantType.IMPLICIT.getAuthorizationGrantType());
					}
					else if(grantTypes[grantTypeCount].equals(AuthorizationGrantType.CLIENT_CREDENTIALS.getAuthorizationGrantType())){
						grantType.append(AuthorizationGrantType.CLIENT_CREDENTIALS.getAuthorizationGrantType());
					}
					else if(grantTypes[grantTypeCount].equals(AuthorizationGrantType.REFRESH_TOKEN.getAuthorizationGrantType())){
						grantType.append(AuthorizationGrantType.REFRESH_TOKEN.getAuthorizationGrantType());
					}
					else if(grantTypes[grantTypeCount].equals(AuthorizationGrantType.PASSWORD.getAuthorizationGrantType())){
						grantType.append(AuthorizationGrantType.PASSWORD.getAuthorizationGrantType());
					}
					
				}
			}else{
				grantType.append(AuthorizationGrantType.AUTHORIZATION_CODE.getAuthorizationGrantType() + ","+AuthorizationGrantType.REFRESH_TOKEN.getAuthorizationGrantType());
			}
			oAuthClientNew.setGrantTypes(grantType.toString());
			oAuthClientNew.setRedirectUris(oAuthClient.getRedirectUris());
			oAuthClientNew.setScopes("read");
			oAuthClientNew.setClientId(getRandomString(5));
			oAuthClientNew.setClientName(oAuthClient.getClientName());
			oAuthClientNew.setClientSecret(UUID.randomUUID().toString());
			oAuthClientNew.setDescription(oAuthClient.getDescription());
			oAuthClientNew.setUser(userRepository.findByGooruId(oAuthClient.getUserUid()));
			oAuthRepository.save(oAuthClientNew);
		}
		return new ActionResponseDTO<OAuthClient>(oAuthClientNew, errors);
	}

	@Override
	public ActionResponseDTO<OAuthClient> updateOAuthClient(OAuthClient oAuthClient) {
		rejectIfNull(oAuthClient, GL0056, "oAuthClient");
		OAuthClient exsitsOAuthClient = (OAuthClient) oAuthRepository.get(OAuthClient.class, oAuthClient.getOauthClientUId());
		rejectIfNull(exsitsOAuthClient, GL0056, "oAuthClient");
		if(oAuthClient.getClientName() != null){
			exsitsOAuthClient.setClientName(oAuthClient.getClientName());
		}
		if(oAuthClient.getDescription() != null){
			exsitsOAuthClient.setDescription(oAuthClient.getDescription());
		}
		if(oAuthClient.getRedirectUris() != null){
			exsitsOAuthClient.setRedirectUris(oAuthClient.getRedirectUris());
		}
		oAuthRepository.save(exsitsOAuthClient);
		final Errors errors = new BindException(OAuthClient.class, "oAuthClient");
		return new ActionResponseDTO<OAuthClient>(exsitsOAuthClient, errors);
	}

	@Override
	public List<OAuthClient> listOAuthClient(String gooruUId, int pageNo, int pageSize) throws Exception {
		return oAuthRepository.listOAuthClient(gooruUId, pageNo, pageSize);
	}

	@Override
	public void deleteOAuthClient(String clientUId, User apiCaller) throws Exception {
		OAuthClient oAuthClient = (OAuthClient) oAuthRepository.get(OAuthClient.class, clientUId);
		if(oAuthClient != null && oAuthClient.getUser().getPartyUid().equalsIgnoreCase(apiCaller.getPartyUid())){
			oAuthRepository.remove(oAuthClient);
			oAuthRepository.flush();
		}
	}

	@Override
	public ActionResponseDTO<OAuthClient> getOAuthClient(String clientUId) throws Exception {
		OAuthClient oAuthClient = (OAuthClient) oAuthRepository.get(OAuthClient.class, clientUId);
		final Errors errors = new BindException(OAuthClient.class, "oAuthClient");
		rejectIfNull(oAuthClient, GL0056, "oAuthClient");
		return new ActionResponseDTO<OAuthClient>(oAuthClient, errors);
	}
	
    private static String getRandomString(int length) {
        String randomStr = UUID.randomUUID().toString();
        while(randomStr.length() < length) {
            randomStr += UUID.randomUUID().toString();
        }
        return randomStr.substring(0, length);
     }

	private Errors validateOAuthClient(OAuthClient oAuthClient) throws Exception {
		final Errors errors = new BindException(oAuthClient, "oAuthClient");
		rejectIfNull(errors, oAuthClient, "userUid", GL0056, generateErrorMessage(GL0056, "userUid"));
		rejectIfNull(errors, oAuthClient, "clientName", GL0056, generateErrorMessage(GL0056, "clientName"));
		return errors;
	}
	
	private Errors validateGooruOAuthConsumerSecret(GooruOAuthConsumerSecret gooruOAuthConsumerSecret) throws Exception {
		final Errors errors = new BindException(gooruOAuthConsumerSecret, "oAuthClient");
		rejectIfNull(errors, gooruOAuthConsumerSecret, "consumerKey", GL0056, generateErrorMessage(GL0056, "consumerKey"));
		rejectIfNull(errors, gooruOAuthConsumerSecret, "consumerSecret", GL0056, generateErrorMessage(GL0056, "consumerSecret"));
		return errors;
	}
	
	@Override
	public ActionResponseDTO<GooruOAuthConsumerSecret> addConsumerSecret(GooruOAuthConsumerSecret consumerSecret, User apiCaller) throws Exception {
		Errors errors = validateGooruOAuthConsumerSecret(consumerSecret);
		GooruOAuthConsumerSecret newConsumerSecret = new GooruOAuthConsumerSecret();
		if(!errors.hasErrors()) {
			newConsumerSecret.setConsumerKey(getRandomString(10));
			newConsumerSecret.setOrganization(apiCaller.getOrganization());
			newConsumerSecret.setConsumerSecret(consumerSecret.getConsumerSecret());
			oAuthRepository.save(newConsumerSecret);
		}
		return new ActionResponseDTO<GooruOAuthConsumerSecret>(newConsumerSecret, errors);
	}

	@Override
	public void deleteConsumerSecret(String consumerKey)
			throws Exception {
		GooruOAuthConsumerSecret consumerSecret = (GooruOAuthConsumerSecret) oAuthRepository.get(GooruOAuthConsumerSecret.class, consumerKey);
		if(consumerSecret != null){
			oAuthRepository.remove(consumerSecret);
			oAuthRepository.flush();
		}
		
	}

	@Override
	public ActionResponseDTO<GooruOAuthConsumerSecret> getConsumerSecret(String consumerKey) throws Exception {
		GooruOAuthConsumerSecret gooruOAuthConsumerSecret = (GooruOAuthConsumerSecret) oAuthRepository.get(GooruOAuthConsumerSecret.class, consumerKey);
		final Errors errors = new BindException(GooruOAuthConsumerSecret.class, "OAuthConsumerSecret");
		rejectIfNull(gooruOAuthConsumerSecret, GL0056, "OAuthConsumerSecret");
		return new ActionResponseDTO<GooruOAuthConsumerSecret>(gooruOAuthConsumerSecret, errors);
	}

}
