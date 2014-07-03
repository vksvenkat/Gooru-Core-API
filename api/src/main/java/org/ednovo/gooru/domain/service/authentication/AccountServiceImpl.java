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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ActivityStream;
import org.ednovo.gooru.core.api.model.ActivityType;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserAccountType.accountCreatedType;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.apitracker.ApiTrackerService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.userToken.UserTokenService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.OrganizationSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.ActivityRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class AccountServiceImpl extends ServerValidationUtils implements AccountService, ParameterProperties, ConstantProperties {

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private ApiTrackerService apiTrackerService;

	@Autowired
	private OrganizationSettingRepository organizationSettingRepository;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ActivityRepository activityRepository;

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

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Override
	public UserToken createSessionToken(User user, String apiKey, HttpServletRequest request) throws Exception {

		ApiKey apiKeyObj = apiTrackerService.getApiKey(apiKey);
		UserToken sessionToken = new UserToken();
		String apiEndPoint = getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		sessionToken.setToken(UUID.randomUUID().toString());
		sessionToken.setScope(SESSION);
		sessionToken.setUser(user);
		sessionToken.setSessionId(request.getSession().getId());
		sessionToken.setApiKey(apiKeyObj);
		sessionToken.setCreatedOn(new Date(System.currentTimeMillis()));
		sessionToken.setRestEndPoint(apiEndPoint);
		try {
			userTokenRepository.saveUserSession(sessionToken);
		} catch (Exception e) {
			logger.error("Error" + e.getMessage());
		}
		Organization organization = null;
		if (sessionToken.getApiKey() != null) {
			organization = sessionToken.getApiKey().getOrganization();
		}
		redisService.addSessionEntry(sessionToken.getToken(), organization);
		request.getSession().setAttribute(Constants.USER, sessionToken.getUser());
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());
		return sessionToken;
	}

	@Override
	public ActionResponseDTO<UserToken> logIn(String username, String password, String apiKeyId, boolean isSsoLogin, HttpServletRequest request) throws Exception {

		ApiKey apiKey = apiTrackerService.getApiKey(apiKeyId);

		UserToken userToken = new UserToken();
		Errors errors = validateApiKey(apiKey, userToken);
		String apiEndPoint = getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		if (!errors.hasErrors()) {
			if (username == null) {
				throw new BadCredentialsException("Username cannot be null or empty.");
			}

			if (password == null) {
				throw new BadCredentialsException("Password cannot be null or empty.");
			}

			Identity identity = new Identity();
			identity.setExternalId(username);

			identity = this.getUserRepository().findByEmailIdOrUserName(username, true, true);

			if (identity == null) {
				throw new BadCredentialsException("Please double-check your email address and password, and then try logging in again.");
			}
			if (identity.getUser().getIsDeleted() == true) {
				throw new BadCredentialsException("error : User has been deleted.");
			}
			identity.setLoginType(CREDENTIAL);

		//	Date deactivateOn = identity.getDeactivatedOn();

		//	if (deactivateOn != null && deactivateOn.before(new Date(System.currentTimeMillis()))) {
			if (identity.getActive() == 0) {
				throw new UnauthorizedException("The user has been deactivated from the system.\nPlease contact Gooru Administrator.");
			}

			User user = this.getUserRepository().findByIdentity(identity);
			if (!isSsoLogin) {
				if (identity.getCredential() == null) {
					throw new BadCredentialsException("Please double check your email ID and password and try again.");
				}
				String encryptedPassword = userService.encryptPassword(password);
				if (user == null || !(encryptedPassword.equals(identity.getCredential().getPassword()) || password.equals(identity.getCredential().getPassword()))) {

					throw new BadCredentialsException("Please double-check your password and try signing in again.");
				}

			}

			if (user.getConfirmStatus() == 0) {
				PartyCustomField userDevice = getPartyService().getPartyCustomeField(user.getPartyUid(), GOORU_USER_CREATED_DEVICE, null);
				Integer tokenCount = this.getUserRepository().getUserTokenCount(user.getGooruUId());
				if (userDevice == null || userDevice.getOptionalValue().indexOf(MOBILE) == -1) {
					if (-1 != Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_WEB_LOGIN_WITHOUT_CONFIRMATION_LIMIT, 0, TaxonomyUtil.GOORU_ORG_UID))) {
						throw new BadCredentialsException("We sent you a confirmation email with instructions on how to complete your Gooru registration. Please check your email, and then try again. Didn’t receive a confirmation email? Please contact us at support@goorulearning.org");
					}
				} else {
					if (tokenCount >= Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_IPAD_LOGIN_WITHOUT_CONFIRMATION_LIMIT, 0, TaxonomyUtil.GOORU_ORG_UID))) {
						throw new BadCredentialsException("We sent you a confirmation email with instructions on how to complete your Gooru registration. Please check your email, and then try again. Didn’t receive a confirmation email? Please contact us at support@goorulearning.org");
					}
				}
			}
			
			userToken.setUser(user);
			userToken.setSessionId(request.getSession().getId());
			userToken.setScope(SESSION);
			userToken.setCreatedOn(new Date(System.currentTimeMillis()));
			userToken.setApiKey(apiKey);
			userToken.setRestEndPoint(apiEndPoint);
			userToken.setFirstLogin(userRepository.checkUserFirstLogin(user.getPartyUid()));
			userToken.getUser().setMeta(userManagementService.userMeta(user));
			
			Profile profile = getPartyService().getUserDateOfBirth(user.getPartyUid(), user);
			if(profile.getUserType() != null){
				userToken.setUserRole(profile.getUserType());
			}
			if(profile != null && profile.getDateOfBirth() != null){
				userToken.setDateOfBirth(profile.getDateOfBirth().toString());
			}

			identity.setLastLogin(new Date(System.currentTimeMillis()));
			this.getUserRepository().save(identity);
			this.getUserTokenRepository().save(userToken);
			
			Organization organization = null;
/*			if (userToken.getApiKey() != null) {
				organization = userToken.getApiKey().getOrganization();
			}
*/
			if(user != null && user.getOrganization() != null){
				organization = user.getOrganization();
			}
			
			redisService.addSessionEntry(userToken.getToken(), organization);
			List<ActivityStream> activityStreams = new ArrayList<ActivityStream>();

			List<ActivityType> types = this.getUserRepository().getAll(ActivityType.class);

			for (ActivityType type : types) {
				ActivityStream stream = new ActivityStream();
				stream.setActivityType(type);
				stream.setUser(user);

				stream = activityRepository.findActivityStreamByType(stream);

				if (stream == null) {
					stream = new ActivityStream();
					stream.setActivityType(type);
					stream.setUser(user);
					stream.setSharing(PUBLIC);
					activityStreams.add(stream);
				}
			}
			
			this.getUserRepository().saveAll(activityStreams);
			User newUser = (User) BeanUtils.cloneBean(userToken.getUser());
			if (newUser.getAccountTypeId() != null && newUser.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				newUser.setEmailId(newUser.getParentUser().getIdentities() != null ? newUser.getParentUser().getIdentities().iterator().next().getExternalId() : null);
			} else {
				newUser.setEmailId(identity.getExternalId());
			}
			newUser.setUserRoleSet(newUser.getUserRoleSet());
			newUser.setProfileImageUrl(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, 0, TaxonomyUtil.GOORU_ORG_UID) + '/' + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID).toString() +  newUser.getGooruUId() + DOT_PNG);
			userToken.setUser(newUser);
			request.getSession().setAttribute(Constants.USER, newUser);
			request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getToken());
			try{
				getEventLogs(identity,userToken);
			} catch(Exception e){
				e.printStackTrace();
			}
			indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER, false);
			
		}
		return new ActionResponseDTO<UserToken>(userToken, errors);
	}

	@Override
	public void logOut(String sessionToken) {
		UserToken userToken = this.getUserTokenRepository().findByToken(sessionToken);

		if (userToken != null) {
			userToken.setScope(EXPIRED);
			this.getUserTokenRepository().save(userToken);
			this.redisService.deleteKey(SESSION_TOKEN_KEY + sessionToken);
		}
	}

	@Override
	public String getConfigSetting(String key, int securityLevel, String organizationUid) {
		return configSettingRepository.getConfigSetting(key, securityLevel, organizationUid);
	}

	@Override
	public ActionResponseDTO<UserToken> loginAs(String sessionToken, String gooruUid, HttpServletRequest request, String apiKey) throws Exception {
		UserToken userToken = new UserToken();
		Errors errors = null;
		if (gooruUid != null) {
			if (gooruUid.equalsIgnoreCase(ANONYMOUS)) {
				ApiKey apiKeyObj = apiTrackerService.getApiKey(apiKey);
				errors = this.validateApiKey(apiKeyObj, userToken);
				if (!errors.hasErrors()) {
					Organization org = apiKeyObj.getOrganization();
					String partyUid = org.getPartyUid();
					String anonymousUid = organizationSettingRepository.getOrganizationSetting(Constants.ANONYMOUS, partyUid);
					User user = userService.findByGooruId(anonymousUid);
					userToken = this.createSessionToken(user, apiKey, request);
				}
			} else {
				User loggedInUser = this.getUserRepository().findByToken(sessionToken);
				errors = this.validateLoginAsUser(userToken, loggedInUser);
				if (!errors.hasErrors()) {
					if (userService.isContentAdmin(loggedInUser)) {
						User user = this.getUserRepository().findByGooruId(gooruUid);
						errors = this.validateLoginAsUser(userToken, user);
						if (!errors.hasErrors()) {
							if (!userService.isContentAdmin(user)) {
								ApiKey userApiKey = apiTrackerService.findApiKeyByOrganization(user.getOrganization().getPartyUid());
								userToken = this.createSessionToken(user, userApiKey.getKey(), request);
							} else {
								throw new BadCredentialsException(generateErrorMessage(GL0042, _USER));
							}
						}
					} else {
						throw new BadCredentialsException(generateErrorMessage(GL0043, _USER));
					}
				}
			}
		}
		return new ActionResponseDTO<UserToken>(userToken, errors);
	}

	@Override
	public User userAuthentication(User newUser, String secretKey, String apiKey, String source, HttpServletRequest request) {
		if (secretKey == null || !secretKey.equalsIgnoreCase(settingService.getConfigSetting(ConfigConstants.GOORU_AUTHENTICATION_SECERT_KEY, 0, TaxonomyUtil.GOORU_ORG_UID))) {
			throw new UnauthorizedException("Invalid authentication request with secret key: " + secretKey);
		}
		Identity identity = new Identity();
		identity.setExternalId(newUser.getEmailId());
		User userIdentity = this.getUserService().findByIdentity(identity);
		UserToken sessionToken = null;
		if (newUser.getUsername() == null) {
			newUser.setFirstName(StringUtils.remove(newUser.getFirstName(), " "));
			newUser.setUsername(newUser.getFirstName());
			if (newUser.getLastName() != null && newUser.getLastName().length() > 0) {
				newUser.setUsername(newUser.getUsername() + newUser.getLastName().substring(0, 1));
			}
			User user = this.getUserRepository().findUserWithoutOrganization(newUser.getUsername());
			if (user != null && user.getUsername().equals(newUser.getUsername())) {
				Random randomNumber = new Random();
				newUser.setUsername(newUser.getUsername() + randomNumber.nextInt(1000));
			}
		}

		if (userIdentity == null) {
			try {
				boolean usernameAvailability = this.getUserRepository().checkUserAvailability(newUser.getUsername(), CheckUser.BYUSERNAME, false);

				if (usernameAvailability) {
					throw new NotFoundException("Someone already has taken " + newUser.getUsername() + "!.Please pick another username.");
				}
				userIdentity = this.getUserManagementService().createUser(newUser, null, null, 1, 0, null, null, null, null, null, null, null, source, null, request, null,null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sessionToken = this.getUserTokenService().findBySession(request.getSession().getId());

		if (sessionToken == null) {
			sessionToken = this.getUserManagementService().createSessionToken(userIdentity, request.getSession().getId(), apiTrackerService.getApiKey(apiKey));
		}
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());
		try {
			newUser = (User) BeanUtils.cloneBean(userIdentity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute(Constants.USER, newUser);
		return newUser;
	}

	@Override
	public ActionResponseDTO<UserToken> switchSession(String sessionToken) throws Exception {
		UserToken userToken = userTokenRepository.findByToken(sessionToken);
		return new ActionResponseDTO<UserToken>(userToken, new BindException(userToken, SESSIONTOKEN));
	}
	
	public void getEventLogs(Identity identity, UserToken userToken) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, USER_LOGIN);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if(identity != null && identity.getLoginType().equalsIgnoreCase(CREDENTIAL)) {
			context.put( LOGIN_TYPE, GOORU);
		}else if (identity != null && identity.getLoginType().equalsIgnoreCase(APPS)) {
			context.put( LOGIN_TYPE, accountCreatedType.GOOGLE_APP.getType());	
		}else {
			context.put( LOGIN_TYPE, accountCreatedType.SSO.getType());
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(SESSIONTOKEN, userToken.getToken());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) : new JSONObject();
		user.put(_GOORU_UID, identity != null && identity.getUser() != null ? identity.getUser().getPartyUid() : null );
		SessionContextSupport.putLogParameter(USER, user.toString());
	}
	
	private Errors validateLoginAsUser(UserToken userToken, User user) {
		final Errors errors = new BindException(userToken, SESSIONTOKEN);
		rejectIfNull(errors, user, USER, GL0056, generateErrorMessage(GL0056, USER));
		return errors;
	}

	private Errors validateApiKey(ApiKey apiKey, UserToken sessToken) throws Exception {
		final Errors errors = new BindException(sessToken, SESSIONTOKEN);
		rejectIfNull(errors, apiKey, API_KEY, GL0056, generateErrorMessage(GL0056, API_KEY));
		return errors;
	}

	public UserTokenRepository getUserTokenRepository() {
		return userTokenRepository;
	}

	public ApiTrackerService getApiTrackerService() {
		return apiTrackerService;
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

}
