/////////////////////////////////////////////////////////////
// AccountServiceImpl.java
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
package org.ednovo.gooru.domain.service.authentication;

import java.io.File;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.Credential;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.core.security.AuthenticationDo;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.eventlogs.AccountEventLog;
import org.ednovo.gooru.domain.service.eventlogs.UserEventLog;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.userToken.UserTokenService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.OrganizationSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.goorucore.application.serializer.ExcludeNullTransformer;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import flexjson.JSONSerializer;

@Service
public class AccountServiceImpl extends ServerValidationUtils implements AccountService, ParameterProperties, ConstantProperties {

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private AccountEventLog accountEventlog;

	@Autowired
	private RedisService redisService;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private OrganizationSettingRepository organizationSettingRepository;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private ConfigSettingRepository configSettingRepository;

	@Autowired
	private PartyService partyService;

	@Autowired
	@Resource(name = "userTokenService")
	private UserTokenService userTokenService;

	@Autowired
	private UserService userService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private UserEventLog usereventlog;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private IndexHandler indexHandler;

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	private static final String SESSION_TOKEN_KEY = "authenticate_";

	private final String SESSION_TOKEN = "sessionToken";

	@Override
	public UserToken createSessionToken(final User user, final String apiKey, final HttpServletRequest request) throws Exception {
		final Application application = this.getApplicationRepository().getApplication(apiKey);
		rejectIfNull(application, GL0056, 404, APPLICATION);
		final UserToken sessionToken = new UserToken();
		final String apiEndPoint = getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		sessionToken.setScope(SESSION);
		sessionToken.setUser(user);
		sessionToken.setSessionId(request.getSession().getId());
		sessionToken.setApplication(application);
		sessionToken.setCreatedOn(new Date(System.currentTimeMillis()));
		sessionToken.setRestEndPoint(apiEndPoint);

		try {
			userTokenRepository.save(sessionToken);
		} catch (Exception e) {
			LOGGER.error("Error" + e.getMessage());
		}
		Organization organization = null;
		if (sessionToken.getApplication() != null) {
			organization = sessionToken.getApplication().getOrganization();
		}
		redisService.addSessionEntry(sessionToken.getToken(), organization);
		request.getSession().setAttribute(Constants.USER, sessionToken.getUser());
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());
		return sessionToken;
	}

	@Override
	public ActionResponseDTO<UserToken> logIn(final String username, final String password, final boolean isSsoLogin, final HttpServletRequest request) throws Exception {
		final UserToken userToken = new UserToken();
		final Errors errors = new BindException(userToken, SESSIONTOKEN);
		final String apiEndPoint = getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		
		if (!errors.hasErrors()) {
			if (username == null) {
				throw new BadRequestException(generateErrorMessage(GL0061, "Username"), GL0061);
			}
			if (password == null) {
				throw new BadRequestException(generateErrorMessage(GL0061, "Password"), GL0061);
			}
			
			String apiKey = request.getParameter(API_KEY);
			rejectIfNull(apiKey, GL0056, API_KEY);
			final Application application  = this.getApplicationRepository().getApplication(apiKey);
			
			// APIKEY domain white listing verification based on request referrer and host headers.
			verifyApikeyDomains(request, application);
			
			Identity identity = new Identity();
			identity.setExternalId(username);

			identity = this.getUserRepository().findByEmailIdOrUserName(username, true, true);
			if (identity == null) {
				throw new UnauthorizedException(generateErrorMessage(GL0078), GL0078);
			}
			identity.setLoginType(CREDENTIAL);

			if (identity.getActive() == 0) {
				throw new UnauthorizedException(generateErrorMessage(GL0079), GL0079);
			}
			
			final User user = this.getUserRepository().findByIdentityLogin(identity);
					
			if (!isSsoLogin) {
				if (identity.getCredential() == null && identity.getAccountCreatedType() != null && !identity.getAccountCreatedType().equalsIgnoreCase(CREDENTIAL)) {
					throw new UnauthorizedException(generateErrorMessage(GL0105, identity.getAccountCreatedType()), GL0105 + Constants.ACCOUNT_TYPES.get(identity.getAccountCreatedType()));
				}
				if (identity.getCredential() == null) {
					throw new UnauthorizedException(generateErrorMessage(GL0078), GL0078);
				}

				final String encryptedPassword;
				final Credential credential = identity.getCredential();
				if (credential != null && credential.getPasswordEncryptType() != null && credential.getPasswordEncryptType().equalsIgnoreCase(CustomProperties.PasswordEncryptType.MD5.getPasswordEncryptType())) {
					encryptedPassword = BaseUtil.encryptPassword(password);
				} else {
					encryptedPassword = this.getUserService().encryptPassword(password);
				}

				if (user == null || !(encryptedPassword.equals(identity.getCredential().getPassword()) || password.equals(identity.getCredential().getPassword()))) {
					throw new UnauthorizedException(generateErrorMessage(GL0081), GL0081);
				}
				if (credential != null && credential.getPasswordEncryptType() != null && credential.getPasswordEncryptType().equalsIgnoreCase(CustomProperties.PasswordEncryptType.MD5.getPasswordEncryptType())) {
					credential.setPassword(this.getUserService().encryptPassword(password));
					credential.setPasswordEncryptType(CustomProperties.PasswordEncryptType.SHA.getPasswordEncryptType());
				}

			}

			if (user.getConfirmStatus() == 0) {
				final PartyCustomField userDevice = getPartyService().getPartyCustomeField(user.getPartyUid(), GOORU_USER_CREATED_DEVICE, null);
				final Integer tokenCount = this.getUserRepository().getUserTokenCount(user.getGooruUId());
				if (userDevice == null || userDevice.getOptionalValue().indexOf(MOBILE) == -1) {
					if (-1 != Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_WEB_LOGIN_WITHOUT_CONFIRMATION_LIMIT, 0, TaxonomyUtil.GOORU_ORG_UID))) {
						throw new BadRequestException(generateErrorMessage(GL0072), GL0072);
					}
				} else {
					if (tokenCount >= Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_IPAD_LOGIN_WITHOUT_CONFIRMATION_LIMIT, 0, TaxonomyUtil.GOORU_ORG_UID))) {
						throw new BadRequestException(generateErrorMessage(GL0072), GL0072);
					}
				}
			}

			userToken.setUser(user);
			userToken.setSessionId(request.getSession().getId());
			userToken.setScope(SESSION);
			userToken.setCreatedOn(new Date(System.currentTimeMillis()));
			userToken.setApplication(application);
			userToken.setRestEndPoint(apiEndPoint);
			boolean firstLogin = false;
			if (user.getIdentities().size() > 0 && user.getIdentities().iterator().next().getLastLogin() == null) {
				firstLogin = true;
			}
			userToken.setFirstLogin(firstLogin);
			userToken.getUser().setMeta(this.getUserManagementService().userMeta(user));

			final Profile profile = getPartyService().getUserDateOfBirth(user.getPartyUid(), user);

			if (profile.getUserType() != null) {
				userToken.setUserRole(profile.getUserType());
			}
			if (profile != null && profile.getDateOfBirth() != null) {
				userToken.setDateOfBirth(profile.getDateOfBirth().toString());
			}

			identity.setLastLogin(new Date(System.currentTimeMillis()));
			this.getUserRepository().save(identity);
			this.getUserTokenRepository().save(userToken);

			Organization organization = null;
			if (user != null && user.getOrganization() != null) {
				organization = user.getOrganization();
			}
			try {
				if (userToken != null) {
					final AuthenticationDo authentication = new AuthenticationDo();
					authentication.setUserToken(userToken);
					authentication.setUserCredential(userService.getUserCredential(user, userToken.getToken(), null, null));
					getRedisService().put(
							SESSION_TOKEN_KEY + userToken.getToken(),
							new JSONSerializer().transform(new ExcludeNullTransformer(), void.class)
									.include(new String[] { "*.operationAuthorities", "*.userRoleSet", "*.partyOperations", "*.subOrganizationUids", "*.orgPermits", "*.partyPermits", "*.customFields", "*.identities", "*.partyPermissions.*" })
									.exclude(new String[] { "*.class", "*.school", "*.schoolDistrict", "*.status", "*.meta" }).serialize(authentication), Constants.AUTHENTICATION_CACHE_EXPIRY_TIME_IN_SEC);
				}
			} catch (Exception e) {
				LOGGER.error("Failed to  put  value from redis server {}", e);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Authorize User: First Name-" + user.getFirstName() + "; Last Name-" + user.getLastName() + "; Email-" + user.getUserId());
			}
			redisService.addSessionEntry(userToken.getToken(), organization);

			final User newUser = (User) BeanUtils.cloneBean(userToken.getUser());

			if (newUser.getAccountTypeId() != null && newUser.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				newUser.setEmailId(newUser.getParentUser().getIdentities() != null ? newUser.getParentUser().getIdentities().iterator().next().getExternalId() : null);
			} else {
				newUser.setEmailId(identity.getExternalId());
			}
			newUser.setUserRoleSet(newUser.getUserRoleSet());
			newUser.setProfileImageUrl(BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, 0, TaxonomyUtil.GOORU_ORG_UID)) + '/' + newUser.getGooruUId() + DOT_PNG);
			userToken.setUser(newUser);
			request.getSession().setAttribute(Constants.USER, newUser);
			request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getToken());
			if (userToken.getApplication() != null) {
				request.getSession().setAttribute(Constants.APPLICATION_KEY, userToken.getApplication().getKey());
			}
			try {
				this.getAccountEventlog().getEventLogs(identity, userToken, true);
			} catch (Exception e) {
				LOGGER.debug("error" + e.getMessage());
			}
			indexHandler.setReIndexRequest(user.getPartyUid(), IndexProcessor.INDEX, USER, userToken.getToken(), false, false);

		}

		return new ActionResponseDTO<UserToken>(userToken, errors);
	}

	@Override
	public void logOut(final String sessionToken) {
		UserToken userToken = this.getUserTokenRepository().findByToken(sessionToken);
		if (userToken != null) {
			try {
				this.getAccountEventlog().getEventLogs(userToken.getUser().getIdentities() != null ? userToken.getUser().getIdentities().iterator().next() : null, userToken, false);
				this.getUserTokenRepository().remove(userToken);
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
			this.redisService.delete(SESSION_TOKEN_KEY + userToken.getToken());
		}
	}
	
	public void verifyApikeyDomains(HttpServletRequest request, Application application) {
		
		boolean isValidReferrer = false;
		
		String hostName = null;
		String registeredRefererDomains = null;
		
		if (request.getHeader(HOST) != null){
			hostName = request.getHeader(HOST);
		}else if (request.getHeader(REFERER) != null){
			hostName = request.getHeader(REFERER);
		}
		LOGGER.info("Host Name" +hostName);

		if (hostName != null && application != null){			
			String hostNameArr [] = hostName.split(REGX_DOT);
			StringBuffer mainDomainName = null;
			if(hostNameArr.length == 2){
				mainDomainName = new StringBuffer(hostNameArr[0]);
				mainDomainName.append(DOT).append(hostNameArr[1]);
			} else {
				mainDomainName = new StringBuffer(hostNameArr[1]);
				mainDomainName.append(DOT).append(hostNameArr[2]);
			}
			registeredRefererDomains = application.getRefererDomains();
			
			if(registeredRefererDomains != null ){				
				String registeredRefererDomainArr [] = registeredRefererDomains.split(COMMA);
				for (String refererDomain : registeredRefererDomainArr) {
					if(refererDomain.equalsIgnoreCase(mainDomainName.toString())){
						isValidReferrer = true;
						break;						
					}
				}
			}
		}
		
		if (registeredRefererDomains != null && isValidReferrer == false){
			throw new AccessDeniedException(generateErrorMessage("GL0109"));
		}
	}

	@Override
	public String getConfigSetting(final String key, final int securityLevel, final String organizationUid) {
		return configSettingRepository.getConfigSetting(key, securityLevel, organizationUid);
	}

	@Override
	public UserToken loginAs(final String gooruUid, final HttpServletRequest request) throws Exception {
		UserToken userToken = null;
		if (gooruUid != null) {
			if (gooruUid.equalsIgnoreCase(ANONYMOUS)) {
				final String apiKey = request.getHeader(Constants.GOORU_API_KEY) != null ? request.getHeader(Constants.GOORU_API_KEY) : request.getParameter(API_KEY);
				final Application application = this.getApplicationRepository().getApplication(apiKey);
				rejectIfNull(application, GL0007, API_KEY);
				final Organization org = application.getOrganization();
				final String partyUid = org.getPartyUid();
				final String anonymousUid = organizationSettingRepository.getOrganizationSetting(Constants.ANONYMOUS, partyUid);
				final User user = this.getUserService().findByGooruId(anonymousUid);
				userToken = this.createSessionToken(user, apiKey, request);
			} else {
				final String sessionToken = request.getHeader(Constants.GOORU_SESSION_TOKEN) != null ? request.getHeader(Constants.GOORU_SESSION_TOKEN) : request.getParameter(SESSION_TOKEN);
				final User loggedInUser = this.getUserRepository().findByToken(sessionToken);
				rejectIfNull(loggedInUser, GL0056, SESSIONTOKEN);
				if (this.getUserService().isContentAdmin(loggedInUser)) {
					final User user = this.getUserRepository().findByGooruId(gooruUid);
					rejectIfNull(user, GL0056, USER);
					if (!this.getUserService().isContentAdmin(user)) {
						final Application userApiKey = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
						userToken = this.createSessionToken(user, userApiKey.getKey(), request);
					} else {
						throw new BadRequestException(generateErrorMessage(GL0042, _USER), GL0042);
					}
				} else {
					throw new BadRequestException(generateErrorMessage(GL0043, _USER), GL0042);
				}

			}
		}
		request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getToken());
		if (userToken != null && userToken.getApplication() != null) {
			request.getSession().setAttribute(Constants.APPLICATION_KEY, userToken.getApplication().getKey());
		}

		return userToken;
	}

	@Override
	public User userAuthentication(User newUser, final String secretKey, final String apiKey, final String source, final HttpServletRequest request) {
		if (secretKey == null || !secretKey.equalsIgnoreCase(settingService.getConfigSetting(ConfigConstants.GOORU_AUTHENTICATION_SECERT_KEY, 0, TaxonomyUtil.GOORU_ORG_UID))) {
			throw new UnauthorizedException(generateErrorMessage("GL0082", "secret") + secretKey, "GL0082");
		}
		boolean registerUser = false;
		final Identity identity = new Identity();
		identity.setExternalId(newUser.getEmailId());
		User userIdentity = this.getUserService().findByIdentity(identity);
		UserToken sessionToken = null;
		if (userIdentity == null) {
			try {
				if (newUser.getUsername() == null) {
					newUser.setFirstName(StringUtils.remove(newUser.getFirstName(), " "));
					newUser.setUsername(newUser.getFirstName());
					if (newUser.getLastName() != null && newUser.getLastName().length() > 0) {
						newUser.setUsername(newUser.getUsername() + newUser.getLastName().substring(0, 1));
					}
					final User user = this.getUserRepository().findUserWithoutOrganization(newUser.getUsername());
					if (user != null && user.getUsername().equalsIgnoreCase(newUser.getUsername())) {
						final Random randomNumber = new Random();
						newUser.setUsername(newUser.getUsername() + randomNumber.nextInt(1000));
					}
				}
				userIdentity = this.getUserManagementService().createUser(newUser, null, null, 1, 0, null, null, null, null, null, null, null, source, null, request, null, null);
				registerUser = true;
			} catch (Exception e) {
				LOGGER.error("Error : " + e);
			}
		}
		Identity newIdentity = null;
		if (userIdentity.getIdentities() != null && userIdentity.getIdentities().size() > 0) {
			newIdentity = userIdentity.getIdentities().iterator().next();
			if (newIdentity != null) {
				newIdentity.setLoginType(source);
				newIdentity.setLastLogin(new Date(System.currentTimeMillis()));
				this.getUserRepository().save(newIdentity);
			}
		}
		if (sessionToken == null) {
			final Application application = this.getApplicationRepository().getApplication(apiKey);
			rejectIfNull(application, GL0056, 404, APPLICATION);
			sessionToken = this.getUserManagementService().createSessionToken(userIdentity, request.getSession().getId(), application);
		}
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());
		if (!registerUser) {
			try {
				this.getAccountEventlog().getEventLogs(newIdentity, sessionToken, true);
				indexHandler.setReIndexRequest(userIdentity.getPartyUid(), IndexProcessor.INDEX, USER, sessionToken.getToken(), false, false);

			} catch (JSONException e) {
				LOGGER.error("Error : " + e);
			}
		}
		try {
			newUser = (User) BeanUtils.cloneBean(userIdentity);
		} catch (Exception e) {
			LOGGER.error("Error : " + e);
		}
		request.getSession().setAttribute(Constants.USER, newUser);
		newUser.setToken(sessionToken.getToken());
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());
		if (sessionToken.getApplication() != null) {
			request.getSession().setAttribute(Constants.APPLICATION_KEY, sessionToken.getApplication().getKey());
		}
		return newUser;
	}

	@Override
	public ActionResponseDTO<UserToken> switchSession(final String sessionToken) throws Exception {
		final UserToken userToken = userTokenRepository.findByToken(sessionToken);
		return new ActionResponseDTO<UserToken>(userToken, new BindException(userToken, SESSIONTOKEN));
	}

	public AccountEventLog getAccountEventlog() {
		return accountEventlog;
	}

	public UserTokenRepository getUserTokenRepository() {
		return userTokenRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public PartyService getPartyService() {
		return partyService;
	}

	public UserTokenService getUserTokenService() {
		return userTokenService;
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public UserService getUserService() {
		return userService;
	}

	public ApplicationRepository getApplicationRepository() {
		return applicationRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public UserEventLog getUsereventlog() {
		return usereventlog;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public OrganizationSettingRepository getOrganizationSettingRepository() {
		return organizationSettingRepository;
	}

	public IndexProcessor getIndexProcessor() {
		return indexProcessor;
	}

	public ConfigSettingRepository getConfigSettingRepository() {
		return configSettingRepository;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

}
