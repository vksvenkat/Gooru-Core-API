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

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.model.oauth.AuthorizationGrantType;
import org.ednovo.gooru.core.api.model.OAuthClient;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.auth.OAuthRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class OAuthServiceImpl extends ServerValidationUtils implements OAuthService, ParameterProperties {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OAuthRepository oAuthRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Override
	public User getUserByOAuthAccessToken(String accessToken) throws Exception {
		String clientId = oAuthRepository.findClientByAccessToken(accessToken);
		if (clientId == null) {
			throw new NotFoundException("Client not found for given oauth token");
		}
		OAuthClient oAuthClient = oAuthRepository.findOAuthClientByOAuthKey(clientId);
		User user = oAuthClient.getUser();
		if (user == null) {
			throw new NotFoundException("User not found for given oauth token");
		}
		return user;
	}

	@Override
	public ActionResponseDTO<OAuthClient> createOAuthClient(OAuthClient oAuthClient, User apiCaller) throws Exception {
		Errors errors = validateOAuthClient(oAuthClient);
		if (!errors.hasErrors()) {
			oAuthClient.setGooruOid(UUID.randomUUID().toString());
			oAuthClient.setAccessTokenValiditySeconds(new Integer(86400));
			oAuthClient.setAuthorities(ROLE_CLIENT);
			if (oAuthClient.getSecretKey() == null) {
				oAuthClient.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
			}
			if (oAuthClient.getKey() == null) {
				oAuthClient.setKey(UUID.randomUUID().toString().replaceAll("-", ""));				
			}
			if (oAuthClient.getGrantTypes() == null) {
				oAuthClient.setGrantTypes(AuthorizationGrantType.AUTHORIZATION_CODE.getAuthorizationGrantType() + "," + AuthorizationGrantType.REFRESH_TOKEN.getAuthorizationGrantType() + "," + AuthorizationGrantType.CLIENT_CREDENTIALS.getAuthorizationGrantType());
				oAuthClient.setResourceType((ResourceType) this.getOAuthRepository().get(ResourceType.class, ResourceType.Type.OAUTH.getType()));
			}else{
				oAuthClient.setGrantTypes(LTI);
				oAuthClient.setResourceType((ResourceType) this.getOAuthRepository().get(ResourceType.class, ResourceType.Type.LTI.getType()));
			}
			rejectIfNull(oAuthClient.getApplication(), GL0006, "Application key ");
			rejectIfNull(oAuthClient.getApplication().getKey(), GL0006, "Application key ");
			Application application = this.getApplicationRepository().getApplication(oAuthClient.getApplication().getKey());
			rejectIfNull(application, GL0007, "Application key ");
			oAuthClient.setApplication(application);
			CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.ACTIVE.getApplicationStatus());
			oAuthClient.setStatus(status);
			oAuthClient.setScopes(READ);
			oAuthClient.setContentType((ContentType) this.getOAuthRepository().get(ContentType.class, RESOURCE));			
			oAuthClient.setLastModified(new Date(System.currentTimeMillis()));
			oAuthClient.setCreatedOn(new Date(System.currentTimeMillis()));
			oAuthClient.setUser(apiCaller);
			oAuthClient.setOrganization(apiCaller.getPrimaryOrganization());
			oAuthClient.setIsFeatured(0);
			oAuthClient.setCreator(apiCaller);
			oAuthClient.setRecordSource(NOT_ADDED);
			oAuthClient.setLastUpdatedUserUid(apiCaller.getGooruUId());
			oAuthClient.setSharing(Sharing.PRIVATE.getSharing());
			oAuthRepository.save(oAuthClient);
		}
		return new ActionResponseDTO<OAuthClient>(oAuthClient, errors);
	}

	@Override
	public ActionResponseDTO<OAuthClient> updateOAuthClient(OAuthClient oAuthClient, String id) {
		rejectIfNull(oAuthClient, GL0056, "oAuthClient");
		OAuthClient exsitsOAuthClient = (OAuthClient) oAuthRepository.findOAuthClientByOAuthKey(id);
		rejectIfNull(exsitsOAuthClient, GL0056, "oAuthClient");
		if (oAuthClient.getRedirectUrl() != null) {
			exsitsOAuthClient.setRedirectUrl(oAuthClient.getRedirectUrl());
		}
		if (exsitsOAuthClient.getResourceType()!= null && exsitsOAuthClient.getResourceType().getName().equalsIgnoreCase(LTI)) {
			if (oAuthClient.getKey() != null) {
				exsitsOAuthClient.setKey(oAuthClient.getKey());
			}
			if (oAuthClient.getSecretKey() != null) {
				exsitsOAuthClient.setSecretKey(oAuthClient.getSecretKey());
			}
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
		if (oAuthClient != null && oAuthClient.getUser().getPartyUid().equalsIgnoreCase(apiCaller.getPartyUid())) {
			oAuthRepository.remove(oAuthClient);
			oAuthRepository.flush();
		}
	}

	@Override
	public OAuthClient getOAuthClientByClientSecret(String clientSecret) throws Exception {
		return oAuthRepository.findOAuthClientByclientSecret(clientSecret);
	}

	@Override
	public ActionResponseDTO<OAuthClient> getOAuthClient(String oauthKey) throws Exception {
		OAuthClient oAuthClient = (OAuthClient) oAuthRepository.findOAuthClientByOAuthKey(oauthKey);
		final Errors errors = new BindException(OAuthClient.class, "oAuthClient");
		rejectIfNull(oAuthClient, GL0056, "oAuthClient");
		return new ActionResponseDTO<OAuthClient>(oAuthClient, errors);
	}

	@Override
	public SearchResults<OAuthClient> listOAuthClientByOrganization(String organizationUId, Integer offset, Integer limit, String grantType) throws Exception {

		List<OAuthClient> oAuthClient = this.getOAuthRepository().listOAuthClientByOrganization(organizationUId, offset, limit, grantType);
		SearchResults<OAuthClient> result = new SearchResults<OAuthClient>();
		result.setSearchResults(oAuthClient);
		result.setTotalHitCount(this.getOAuthRepository().getOauthClientCount(organizationUId, grantType));
		return result;

	}


	private Errors validateOAuthClient(OAuthClient oAuthClient) throws Exception {
		final Errors errors = new BindException(oAuthClient, OAUTH_CLIENT);
		rejectIfNull(errors, oAuthClient, "userUid", GL0056, generateErrorMessage(GL0056, "userUid"));
		rejectIfNull(errors, oAuthClient, "clientName", GL0056, generateErrorMessage(GL0056, "clientName"));
		return errors;
	}

	@Override
	public Boolean isSuperAdmin(User user) {
		Boolean isSuperAdmin = false;
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				if (userRoleAssoc.getRole().getName().equalsIgnoreCase(UserRoleType.SUPER_ADMIN.getType())) {
					isSuperAdmin = true;
					break;
				}
			}
		}

		return isSuperAdmin;
	}

	@Override
	public List<OAuthClient> getOAuthClientByApiKey(String apiKey) throws Exception {
		return oAuthRepository.findOAuthClientByApplicationKey(apiKey);
	}

	@Override
	public void deleteOAuthClientByOAuthKey(String oauthKey) throws Exception{
		OAuthClient oAuthClient = oAuthRepository.findOAuthClientByOAuthKey(oauthKey);
		rejectIfNull(oAuthClient, GL0056,404, OAUTH_CLIENT);
		getApplicationRepository().remove(oAuthClient);
	}
	
	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public OAuthRepository getOAuthRepository() {
		return oAuthRepository;
	}

	public ApplicationRepository getApplicationRepository() {
		return applicationRepository;
	}

}
