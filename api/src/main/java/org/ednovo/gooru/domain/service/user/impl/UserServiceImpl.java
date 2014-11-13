/////////////////////////////////////////////////////////////
// UserServiceImpl.java
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
package org.ednovo.gooru.domain.service.user.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Credential;
import org.ednovo.gooru.core.api.model.EntityOperation;
import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Idp;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationDomainAssoc;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.PartyPermission;
import org.ednovo.gooru.core.api.model.PermissionType;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserAccountType.accountCreatedType;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserCredential;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.GooruMd5Util;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UserNotConfirmedException;
import org.ednovo.gooru.domain.service.CollaboratorService;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.IdpRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.UserGroupRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import com.thoughtworks.xstream.core.util.Base64Encoder;

@Service("userService")
public class UserServiceImpl extends ServerValidationUtils implements UserService,ParameterProperties,ConstantProperties {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IdpRepository idpRepository;

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private UserGroupRepository groupRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private SettingService settingService;

	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private PartyService partyService;

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private ResourceRepository resourceRepository;
	
	@Autowired
	private InviteRepository inviteRepository;

	@Autowired 
	private TaxonomyRespository taxonomyRespository;
	
	@Autowired
	private CollaboratorService collaboratorService;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public User createUser(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId,
			String remoteEntityId, String gender, String childDOB, String source, String emailSSO, String referenceUid, String role) throws Exception {
		List<InviteUser> inviteuser = this.getInviteRepository().getInviteUserByMail(email, COLLABORATOR);
		if(inviteuser.size() > 0) {
			confirmStatus = 1;
		}
		boolean confirmedUser = false;
		if (confirmStatus != null && confirmStatus == 1) {
			confirmedUser = true;
		}
		if (confirmStatus == null) {
			confirmStatus = 0;
		}
		if (organizationCode != null && organizationCode.length() > 0 && organizationCode.equalsIgnoreCase(GLOBAL)) {
			confirmStatus = 1;
		}
		
		String domain = email.substring(email.indexOf("@") + 1, email.length());

		Idp idp = null;
		Organization organization = null;

		if (remoteEntityId != null) {
			idp = this.getIdpRepository().findByName(remoteEntityId);
			if(idp != null){
				organization = setOrganizationByDomain(idp, organization, organizationCode);
			}
			else {
				idp = new Idp();
				idp.setName(remoteEntityId);
				userRepository.save(idp);
			}
		} else {
			idp = this.getIdpRepository().findByName(domain);
			if(idp != null){
				organization = setOrganizationByDomain(idp, organization, organizationCode);
			}
			else if(organizationCode != null){
				organization = organizationService.getOrganizationByCode(organizationCode.toLowerCase());
			}
		}

		if (organization == null) {
			organization = organizationService.getOrganizationByCode(GOORU);
		}

		Identity identity = new Identity();
		if (emailSSO != null) {
			identity.setSsoEmailId(emailSSO);
		}
		if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			identity.setFirstName(firstName);
			identity.setLastName(lastName);
			identity.setExternalId(username);
			confirmStatus = 1;
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.CHILD.getType());
			identity.setLoginType(CREDENDTIAL);
		} else {
			identity.setFirstName(firstName);
			identity.setLastName(lastName);
			identity.setExternalId(email);
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.NORMAL.getType());
			identity.setLoginType(CREDENDTIAL);
		}

		if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.GOOGLE_APP.getType());
		} else if (source != null) {
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.SSO.getType());
		}

		/*
		 * Step I - Create a user object from the received credentials
		 */
		Set<Identity> identities = new HashSet<Identity>();
		identities.add(identity);

		User user = new User();
		user.setIdentities(identities);
		user.setViewFlag(0);

		/*
		 * Assuming Teacher Role for all users. In future, it will be a
		 * parameter to the function
		 */

		UserRole userRole = null;
		if (organization != null) {
			String roles = getDefaultUserRoles(organization.getPartyUid());
			if (roles != null) {
				String[] roleArray = roles.split(",");
				userRole = userRepository.findUserRoleByName(roleArray[0], organization.getPartyUid());
				if (roleArray.length > 1) {
					userRole = userRepository.findUserRoleByName(roleArray[1], organization.getPartyUid());
				}
			}
		}

		if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			user.setFirstName(firstName);
			user.setLastName(lastName);
		} else {
			user.setFirstName(firstName);
			user.setLastName(lastName);
		}

		if (referenceUid != null) {
			user.setReferenceUid(referenceUid);
		}
		// create a party
		user.setPartyName(organization.getPartyName());
		user.setPartyType(USER);
		user.setCreatedOn(new Date(System.currentTimeMillis()));

		if (username == null) {
			user.setUsername(email);
		} else {
			user.setUsername(username);
		}
		user.setConfirmStatus(confirmStatus);
		user.setRegisterToken(UUID.randomUUID().toString());
		user.setAddedBySystem(addedBySystem);
		user.setImportCode(userImportCode);

		if (accountType != null) {
			if (accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
				user.setAccountTypeId(UserAccountType.ACCOUNT_PARENT);
			} else if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
				user.setAccountTypeId(UserAccountType.ACCOUNT_CHILD);
				if (userParentId != null) {
					this.getUser(userParentId).setAccountTypeId(UserAccountType.ACCOUNT_PARENT);
					user.setParentUser(this.getUser(userParentId));
				}
			} else {
				user.setAccountTypeId(UserAccountType.ACCOUNT_NON_PARENT);
			}
		}

		// check if the idp exists in the database. If not, create it.
		/*
		 * if (idp == null) { idp = new Idp(); idp.setName(domain);
		 * identity.setIdp(idp);
		 * 
		 * this.getUserRepository().save(idp); }
		 */

		if (idp != null) {
			identity.setIdp(idp);
		}
		identity.setRegisteredOn(new Date(System.currentTimeMillis()));
		identity.setUser(user);
		Credential credential = null;
		if (source == null || !source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			credential = new Credential();
			credential.setIdentity(identity);
			String token = UUID.randomUUID().toString();
			credential.setToken(token);
			credential.setResetPasswordRequestDate(new Date(System.currentTimeMillis()));
			if (password != null) {
				credential.setPassword(encryptPassword(password));
			} else if (confirmedUser) {
				password = BaseUtil.base48Encode(7);
				credential.setPassword(encryptPassword(password));
			}
		}

		/*
		 * Step III - create profile for user.
		 */

		Profile profile = new Profile();
		profile.setUser(user);
		profile.setSchool(school);
		if (role != null) {
			if (role.equalsIgnoreCase(UserRole.UserRoleType.STUDENT.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.TEACHER.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.AUTHENTICATED_USER.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.OTHER.getType())) {
				profile.setUserType(role);
			}
		}

		if (dateOfBirth != null && accountType != null && !dateOfBirth.equalsIgnoreCase("null")) {
			if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()) && userParentId != null) {
				if (dateOfBirth.equalsIgnoreCase("00/00/0000")) {
					profile.setDateOfBirth(this.getProfile(this.getUser(userParentId)).getChildDateOfBirth());
				} else {
					Integer age = this.calculateCurrentAge(dateOfBirth);
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
					final Date date = dateFormat.parse(dateOfBirth);
					if (age < 13 && age >= 0) {
						profile.setDateOfBirth(date);
						User parentUser = profile.getUser().getParentUser();
						Profile parentProfile = this.getUserRepository().getProfile(getUser(parentUser.getGooruUId()), false);
						parentProfile.setChildDateOfBirth(date);
						this.getUserRepository().save(parentProfile);

					}
				}
			} else {
				Integer age = this.calculateCurrentAge(dateOfBirth);
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date date = dateFormat.parse(dateOfBirth);
				if (age >= 13 && accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType())) {
					profile.setDateOfBirth(date);
				}
			}
		}
		if (childDOB != null && !childDOB.equalsIgnoreCase("null") && accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
			Integer age = this.calculateCurrentAge(childDOB);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = dateFormat.parse(childDOB);
			if (age < 13 && age >= 0) {
				profile.setChildDateOfBirth(date);
			}
		}

		if (isNotEmptyString(gender)) {
			profile.setGender(this.getUserRepository().getGenderByGenderId(gender));
		}
		/*
		 * Step IV - Persist the user object in the database.
		 */
		user.setOrganization(organization);

		// Fix me
		user.setPrimaryOrganization(organization);

		user.setUserGroup(null);

		this.getUserRepository().save(profile);

		// Associate user role

		if (userRole != null) {
			UserRoleAssoc userRoleAssoc = new UserRoleAssoc();
			userRoleAssoc.setRole(userRole);
			userRoleAssoc.setUser(user);
			this.getUserRepository().save(userRoleAssoc);
			user.setUserRoleSet(new HashSet<UserRoleAssoc>());
			user.getUserRoleSet().add(userRoleAssoc);
		}

		user.setUserUid(user.getPartyUid());
		this.getUserRepository().save(user);

		/*
		 * If password is not null, persist a credential object.
		 */

		if (password != null) {
			this.getUserRepository().save(credential);
		}

		identity.setCredential(credential);
		this.getUserRepository().save(identity);
		
		if(inviteuser.size() > 0) {
			this.getCollaboratorService().updateCollaboratorStatus(email);
		}

		this.getPartyService().createUserDefaultCustomAttributes(user.getPartyUid(), user);

		this.getPartyService().createTaxonomyCustomAttributes(user.getPartyUid(), user);

		this.getUserRepository().flush();

		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);

		/*if (identity.getIdp() != null) {
			SessionContextSupport.putLogParameter(IDP_NAME, identity.getIdp().getName());
		} else {
			SessionContextSupport.putLogParameter(IDP_NAME, GOORU_API);
		}*/
		
		try {
			getEventLogs(user, source, identity);
		}catch(Exception e){
			e.printStackTrace();
		}

		return user;

	}

	private Organization setOrganizationByDomain(Idp idp, Organization organization, String organizationCode){
		OrganizationDomainAssoc domainOrganizationAssoc = this.getIdpRepository().findByDomain(idp);
		if(domainOrganizationAssoc != null){
			organization = domainOrganizationAssoc.getOrganization();
		}
		else if (organizationCode != null) {
			organization = organizationService.getOrganizationByCode(organizationCode.toLowerCase());
		}
		return organization;
	}
	/*
	 * Creates a user object in the database.
	 */
	@Override
	public User createUser(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId, String gender,
			String childDOB, String source, String referenceUid, String role, String domainName) throws Exception {
		return createUser(firstName, lastName, email, password, school, username, confirmStatus, organizationCode, addedBySystem, userImportCode, accountType, dateOfBirth, userParentId, domainName, gender, childDOB, source, null, referenceUid, role);
	}

	public static String getDefaultUserRoles(String organizationUid) {
		String roles = SettingService.getInstance().getOrganizationSetting(ConfigConstants.DEFAULT_USER_ROLES, organizationUid);
		return roles != null ? roles : DEFAULT_ROLES;
	}

	@Override
	public String encryptPassword(String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw new BadCredentialsException("Error while authenticating user - No algorithm exists. ", e);
		}
		try {
			md.update(password.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw new BadCredentialsException("Error while authenticating user - ", e);
		}
		byte raw[] = md.digest(); // step 4
		String hash = (new Base64Encoder()).encode(raw); // step 5

		return hash; // step 6
	}

	@Override
	public User createUserWithValidation(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Boolean useGeneratedPassword, Boolean sendConfirmationMail, User apiCaller, String accountType,
			String dateOfBirth, String userParentId, String sessionId, String gender, String childDOB, String gooruClassicUrl, String referenceUid, String role, String pearsonEmailId, String domainName) throws Exception {

		Boolean isAdminCreateUser = false;
		Integer addedBySystem = 0;
		if (apiCaller != null) {
			if (isContentAdmin(apiCaller)) {
				addedBySystem = 1;
				isAdminCreateUser = true;
				if (useGeneratedPassword) {
					password = UUID.randomUUID().toString();
				}

				confirmStatus = 1;
			}
		}
		List<InviteUser> inviteuser = this.getInviteRepository().getInviteUserByMail(email, COLLABORATOR);
		
		User user = createUser(firstName, lastName, email, password, school, username, confirmStatus, organizationCode, addedBySystem, null, accountType, dateOfBirth, userParentId, gender, childDOB, null, referenceUid, role,  domainName);
		Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
		UserToken userToken = this.createSessionToken(user, sessionId, application);

		if (isNotEmptyString(pearsonEmailId)) {
			user.setEmailId(pearsonEmailId);
			userRepository.save(user);
		}
		userRepository.flush();
		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER, userToken != null ? userToken.getToken() : null);

		if (user != null && sendConfirmationMail && inviteuser.size() <= 0) {
			if (isAdminCreateUser) {
				this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), null, gooruClassicUrl,null,null,null);
			} else {
				if (user.getAccountTypeId() == null || !user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
					this.getMailHandler().sendMailToConfirm(user.getGooruUId(), null, accountType, userToken.getToken(), dateOfBirth, gooruClassicUrl,null,null,null);
				} 
			}
		}

		return user;
	}

	@Override
	public Map<String, String> validateUserAdd(String firstName, String lastName, String email, String password, final String username, User user, String childDOB, String accountType, String dateOfBirth, String organizationCode) throws JSONException {
		Map<String, String> errorList = new HashMap<String, String>();

		if ((isNotEmptyString(childDOB)) && (isNotEmptyString(accountType)) && childDOB != null && !childDOB.equalsIgnoreCase("null")) {
			Integer age = this.calculateCurrentAge(childDOB);
			if (age < 0) {
				errorList.put(DOB, "Future date will not be as a data of birth");
			}
		}

		if ((isNotEmptyString(dateOfBirth)) && (isNotEmptyString(accountType)) && dateOfBirth != null && !dateOfBirth.equalsIgnoreCase("null")) {
			Integer age = this.calculateCurrentAge(dateOfBirth);
			if (age < 0) {
				errorList.put(DOB, "Future date will not be as a data of birth");
			}
			if (age < 13 && age >= 0 && (accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType()))) {
				errorList.put(DOB, "You are below 13 , please register with parent emailId");
			}
		}

		if (!isNotEmptyString(firstName)) {
			errorList.put(FIRST_NAME, "First name cannot be null or empty");
		}

	
		if (!isNotEmptyString(lastName)) {
			errorList.put(LAST_NAME, "Last name cannot be null or empty");
		}

		if (!isNotEmptyString(email)) {
			errorList.put(EMAIL_ID, "Email cannot be null or empty");
		}

		if (!isNotEmptyString(password)) {
			if (user != null) {
				if (!isContentAdmin(user)) {
					errorList.put(PWD, "Password cannot be null or empty");
				}
			} else {
				errorList.put(PWD, "Password cannot be null or empty");
			}

		} else if (password.length() < 5) {
			errorList.put(PWD, "Password should be atleast 5 characters");
		}

		if (!isNotEmptyString(username)) {
			errorList.put(USERNAME, "Username cannot be null or empty");
		} else if (username.length() < 5) {
			errorList.put(USERNAME, "Username should be atleast 5 characters");
		}

		boolean usernameAvailability = this.getUserRepository().checkUserAvailability(username, CheckUser.BYUSERNAME, false);

		if (usernameAvailability) {
			errorList.put(USERNAME, "Someone already has taken " + username + "!.Please pick another username.");
		}

		boolean emailidAvailability = this.getUserRepository().checkUserAvailability(email, CheckUser.BYEMAILID, false);

		if (emailidAvailability) {
			errorList.put(EMAIL, "The email address specified already exists within Gooru. Please use sign-in to log in to your existing account.");
		}

		return errorList;
	}

	@Override
	public boolean containsWhiteSpace(String username) {
		if (username != null) {
			for (int i = 0; i < username.length(); i++) {
				if (Character.isWhitespace(username.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public User getUser(String gooruUId) throws Exception {

		if (gooruUId == null || gooruUId.equalsIgnoreCase("")) {
			throw new BadCredentialsException("User id cannot be null or empty");
		}

		User user = getUserRepository().findByGooruId(gooruUId);
		if (user == null) {
			throw new BadCredentialsException("User not found");
		}
		user.setProfileImageUrl(buildUserProfileImageUrl(user));

		return user;
	}

	@Override
	public List<RoleEntityOperation> getUserOperations(String roleNames) throws Exception {
		return this.getUserRepository().findEntityOperationByRole(roleNames);
	}

	@Override
	public Profile updateUserInfo(String gooruUId, MultiValueMap<String, String> data, User apiCaller, Boolean isDisableUser) throws Exception {

		if (gooruUId == null || gooruUId.equalsIgnoreCase("")) {
			throw new BadCredentialsException("User Id cannot be null or empty");
		}

		if ((!apiCaller.getGooruUId().equals(gooruUId)) && (!isContentAdmin(apiCaller))) {
			throw new AccessDeniedException("You are not authorized to perform this action");
		}

		String firstName = data.getFirst(FIRST_NAME);
		String lastName = data.getFirst(LAST_NAME);
		String gender = data.getFirst(GENDER);
		String birthDate = data.getFirst(BIRTH_DATE);
		String birthMonth = data.getFirst(BIRTH_MONTH);
		String birthYear = data.getFirst(BIRTH_YEAR);
		String aboutMe = data.getFirst(ABOUT_ME);
		String highestDegree = data.getFirst(HIGHEST_DEGREE);
		String graduation = data.getFirst(GRADUATION);
		String postGraduation = data.getFirst(POST_GRADUATION);
		String highSchool = data.getFirst(HIGH__SCHOOL);
		String website = data.getFirst(WEBSITE);
		String facebook = data.getFirst(FACE_BOOK);
		String twitter = data.getFirst(TWITTER);
		String email = data.getFirst(EMAIL);
		String subject = data.getFirst(SUBJECT);
		String grade = data.getFirst(GRADE);
		String school = data.getFirst(SCHOOL);
		String username = data.getFirst(USER_NAME);
		String teachingExperience = data.getFirst(TEACHING_EXP);
		String teachingIn = data.getFirst(TEACHING_IN);
		String teachingMethodology = data.getFirst(TEACHING_METHODOLOGY);
		String dateOfBirth = data.getFirst(DATEOFBIRTH);
		String password = data.getFirst(PASSWORD);
		String userType = data.getFirst(USER_ROLE);

		Identity identity = this.getUserRepository().findUserByGooruId(gooruUId);

		Profile profile = this.getUserRepository().getProfile(getUser(gooruUId), false);

		User user = profile.getUser();
		boolean sendWelcomeMail = false;
		if ((user != null) && (user.getConfirmStatus() == 0)) {
			sendWelcomeMail = true;
			user.setConfirmStatus(1);
		}

		if (isNotEmptyString(firstName)) {
			user.setFirstName(firstName);
		}
		if (isNotEmptyString(lastName)) {
			user.setLastName(lastName);
		}

		if (isNotEmptyString(gender)) {
			profile.setGender(this.getUserRepository().getGenderByGenderId(gender));
		}

		if (isNotEmptyString(dateOfBirth)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = dateFormat.parse(dateOfBirth);
			profile.setDateOfBirth(date);

		}

		// if(isContentAdmin(apiCaller)){
		if (isNotEmptyString(birthDate)) {
			profile.setBirthDate(Integer.parseInt(birthDate));
		}

		if (isNotEmptyString(birthMonth)) {
			profile.setBirthMonth((Integer.parseInt(birthMonth)));
		}

		if (isNotEmptyString(birthYear)) {
			profile.setBirthYear(Integer.parseInt(birthYear));
		}

		if (isNotEmptyString(username)) {

			usernameValidation(username, user.getOrganization().getPartyUid());

			if (isNotEmptyString(password)) {
				validatePassword(password, username);
			}

			boolean usernameAvailability = this.getUserRepository().checkUserAvailability(username, CheckUser.BYUSERNAME, false);
			if (usernameAvailability) {
				throw new BadCredentialsException("Someone already has taken " + username + "!.Please pick another username.");
			} else {
				user.setUsername(username);
			}
		}

		Boolean saveIdentity = false;

		if (isNotEmptyString(firstName)) {
			identity.setFirstName(firstName);
		}

		if (isNotEmptyString(lastName)) {
			identity.setLastName(lastName);
		}

		if (isNotEmptyString(email)) {

			boolean emailAvailability = this.getUserRepository().checkUserAvailability(email, CheckUser.BYEMAILID, false);

			if (emailAvailability) {
				throw new BadCredentialsException("Someone already has taken " + email + "!.Please pick another email.");
			}

			identity.setExternalId(email);
			saveIdentity = true;

		}

		if (isDisableUser) {
			identity.setActive(Short.parseShort(ZERO));
			user.setConfirmStatus(0);
			saveIdentity = true;
		}

		if (saveIdentity) {
			this.getUserRepository().save(identity);
		}
		// }

		// set password for parent or non parent

		Credential creds = identity.getCredential();
		if (creds != null) {
			creds.setIdentity(identity);
		}
		if (password != null) {
			creds.setPassword(encryptPassword(password));
			this.getUserRepository().save(creds);
		}

		if (isNotEmptyString(aboutMe)) {
			profile.setAboutMe(aboutMe);
		}
		if (isNotEmptyString(highestDegree)) {
			profile.setHighestDegree(highestDegree);
		}
		if (isNotEmptyString(graduation)) {
			profile.setGraduation(graduation);
		}
		if (isNotEmptyString(postGraduation)) {
			profile.setPostGraduation(postGraduation);
		}
		if (isNotEmptyString(highSchool)) {
			profile.setHighSchool(highSchool);
		}
		if (isNotEmptyString(website)) {
			profile.setWebsite(website);
		}
		if (isNotEmptyString(facebook)) {
			profile.setFacebook(facebook);
		}
		if (isNotEmptyString(twitter)) {
			profile.setTwitter(twitter);
		}
		if (isNotEmptyString(subject)) {
			profile.setSubject(subject);
		}
		if (isNotEmptyString(grade)) {
			profile.setGrade(grade);
		}
		if (isNotEmptyString(school)) {
			profile.setSchool(school);
		}
		if (isNotEmptyString(teachingExperience)) {
			profile.setTeachingExperience(teachingExperience);
		}
		if (isNotEmptyString(teachingIn)) {
			profile.setTeachingIn(teachingIn);
		}
		if (isNotEmptyString(teachingMethodology)) {
			profile.setTeachingMethodology(teachingMethodology);
		}
		if (isNotEmptyString(userType)) {
			profile.setUserType(userType);
		}

		profile.setUser(user);

		this.getUserRepository().save(profile);
		
		PartyCustomField partyCustomField = this.getPartyService().getPartyCustomeField(profile.getUser().getPartyUid(), "user_confirm_status", profile.getUser());
		
			if(partyCustomField != null && !partyCustomField.getOptionalValue().equalsIgnoreCase("true")) {
					Map<String, String> dataMap = new HashMap<String, String>();
					dataMap.put(GOORU_UID, profile.getUser().getPartyUid());
					dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
					if (profile.getUser().getAccountTypeId() != null && profile.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) { 
						if(profile.getUser().getParentUser().getIdentities() != null){
							dataMap.put("recipient", profile.getUser().getParentUser().getIdentities().iterator().next().getExternalId());
						}
					} else {
						if(profile.getUser().getIdentities() != null){
							dataMap.put("recipient", profile.getUser().getIdentities().iterator().next().getExternalId());
						}
					}
					partyCustomField.setOptionalValue("true");
					this.getUserRepository().save(partyCustomField);
					this.getMailHandler().handleMailEvent(dataMap);
			}
		
		if (user != null && identity.getAccountCreatedType() != null && identity.getAccountCreatedType().equalsIgnoreCase(UserAccountType.accountCreatedType.SSO.getType()) && user.getViewFlag() == 0) {
			password = BaseUtil.base48Encode(7);
			creds.setPassword(encryptPassword(password));
			this.getUserRepository().save(creds);
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.SSO_CONFIRMATION_MAIL.getEvent());
			dataMap.put(GOORU_UID, user.getGooruUId());
			dataMap.put(PASSWORD, password);
			this.getMailHandler().handleMailEvent(dataMap);
		}

		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);

		return profile;

	}

	@Override
	public User revokeUserRole(final String gooruUId, String roles, User apiCaller) throws Exception {
		User user = null;
		if (isNotEmptyString(gooruUId)) {
			if (isContentAdmin(apiCaller)) {
				List<UserRoleAssoc> userRoleAssoc = this.getUserRepository().getUserRoleByName(roles, gooruUId);
				userRepository.removeAll(userRoleAssoc);
				userRepository.flush();
				user = userRepository.findByGooruId(gooruUId);
				indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
			} else {
				throw new BadCredentialsException("You are not authorized to perform this action");
			}

		} else {
			throw new BadCredentialsException("Gooru user Id cannot be null or empty");
		}

		return getUser(gooruUId);
	}

	@Override
	public User grantUserRole(String gooruUId, String roles, User apiCaller) throws Exception {
		if (isContentAdmin(apiCaller)) {
			User user = getUser(gooruUId);
			Set<UserRoleAssoc> roleSet = new HashSet<UserRoleAssoc>();
			final List<UserRole> userRoles = this.getUserRepository().findRolesByNames(roles);
			Set<UserRoleAssoc> currentRoles = user.getUserRoleSet();
			if (userRoles != null) {
				for (UserRole userRole : userRoles) {
					if (!currentRoles.contains(userRole)) {
						UserRoleAssoc userRoleAssoc = new UserRoleAssoc();
						userRoleAssoc.setRole(userRole);
						userRoleAssoc.setUser(user);
						currentRoles.add(userRoleAssoc);
						roleSet.add(userRoleAssoc);
					}
				}
			}
			if (roleSet.size() > 0) {
				this.getUserRepository().saveAll(roleSet);
			}
			user.setUserRoleSet(currentRoles);
			userRepository.save(user);
			indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
			return user;
		} else {
			throw new BadCredentialsException("You are not authorized to perform this action");
		}
	}

	@Override
	public List<UserRole> findAllRoles() {
		return getUserRepository().findAllRoles();
	}	
	
	@Override
	public Boolean isContentAdmin(User user) {
		Boolean isAdminUser = false;
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				if (userRoleAssoc.getRole().getName().equalsIgnoreCase(UserRoleType.CONTENT_ADMIN.getType())) {
					isAdminUser = true;
					break;
				}
			}
		}
		return isAdminUser;
	}

	public Boolean isAnonymous(User user) {
		Boolean isAnonymousUser = false;
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				if (userRoleAssoc.getRole().getName().equalsIgnoreCase(UserRoleType.ANONYMOUS.getType())) {
					isAnonymousUser = true;
					break;
				}
			}
		}
		return isAnonymousUser;
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


	private Boolean isNotEmptyString(String field) {
		return StringUtils.hasLength(field);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public IdpRepository getIdpRepository() {
		return idpRepository;
	}

	public void setIdpRepository(IdpRepository idpRepository) {
		this.idpRepository = idpRepository;
	}

	public UserTokenRepository getUserTokenRepository() {
		return userTokenRepository;
	}

	public void setUserTokenRepository(UserTokenRepository userTokenRepository) {
		this.userTokenRepository = userTokenRepository;
	}

	@Override
	public List<User> getFollowedByUsers(String gooruUId, Integer offset, Integer limit) {
		return getUserRepository().getFollowedByUsers(gooruUId,offset,limit);
	}

	@Override
	public List<User> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit) {
		return getUserRepository().getFollowedOnUsers(gooruUId,offset,limit);
	}

	@Override
	public void signout(String sessionToken) {
		UserToken userToken = this.getUserTokenRepository().findByToken(sessionToken);

		if (userToken != null) {
			userToken.setScope(EXPIRED);
			this.getUserTokenRepository().save(userToken);
		}
	}

	@Override
	public User updateUserRole(String gooruUid, UserRoleType role) {

		User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user == null) {
			return null;
		}
		Profile profile = this.getUserRepository().getProfile(user, false);
		profile.setIsPublisherRequestPending(0);
		UserRoleAssoc usrRoleAssoc = new UserRoleAssoc();
		Set<UserRoleAssoc> existingUserRoleAssoc = user.getUserRoleSet();
		if (!getOperationAuthorizer().hasRole(UserRole.ROLE_PUBLISHER, user)) {
			UserRole usrRole = new UserRole();
			usrRole.setRoleId(UserRole.ROLE_PUBLISHER);
			usrRole.setName(role.getType());
			usrRole.setDescription(role.getType());
			usrRoleAssoc.setRole(usrRole);
			usrRoleAssoc.setUser(user);
			if (existingUserRoleAssoc != null) {
				existingUserRoleAssoc = new HashSet<UserRoleAssoc>();
			}
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				existingUserRoleAssoc.add(userRoleAssoc);
			}
			existingUserRoleAssoc.add(usrRoleAssoc);
		}
		user.setUserRoleSet(existingUserRoleAssoc);
		this.getUserRepository().save(user);
		this.getUserRepository().save(profile);
		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
		return user;
	}

	@Override
	public boolean hasResetTokenValid(String token) {
		Identity identity = this.getUserRepository().findIdentityByResetToken(token);
		boolean resetTokenInvalid = false;
		if (identity != null) {
			double resetHoursDifferent = (new Date(System.currentTimeMillis()).getTime() - identity.getCredential().getResetPasswordRequestDate().getTime()) / (60 * 60 * 1000);
			if (resetHoursDifferent > 24) {
				resetTokenInvalid = true;
			}
		} else {
			resetTokenInvalid = true;
		}
		return resetTokenInvalid;
	}

	@Override
	public UserToken signIn(String username, String password, String apikeyId, String sessionId, boolean isSsoLogin) {

		Application application = this.getApplicationRepository().getApplication(apikeyId);
		if (username == null) {
			throw new BadCredentialsException("error:Username cannot be null or empty.");
		}

		if (password == null) {
			throw new BadCredentialsException("error:Password cannot be null or empty.");
		}

		Identity identity = new Identity();
		identity.setExternalId(username);

		identity = this.getUserRepository().findByEmailIdOrUserName(username, true, true);

		if (identity == null) {
			throw new BadCredentialsException("error:Please double-check your email address and password, and then try logging in again.");
		}
		if (identity.getUser().getIsDeleted() == true) {

			throw new DisabledException("error : User has been deleted.");
		}
		identity.setLoginType(CREDENTIAL);

		Date deactivateOn = identity.getDeactivatedOn();

		if (deactivateOn != null && deactivateOn.before(new Date(System.currentTimeMillis()))) {
			throw new DisabledException("error: The user has been deactivated from the system.\nPlease contact Gooru Administrator.");
		}

		User user = this.getUserRepository().findByIdentity(identity);

		if (!isSsoLogin) {
			if (identity.getCredential() == null) {
				throw new BadCredentialsException("error:Please double check your email ID and password and try again.");
			}
			String encryptedPassword = encryptPassword(password);
			if (user == null || !(encryptedPassword.equals(identity.getCredential().getPassword()))) {

				throw new BadCredentialsException("error:Please double-check your password and try signing in again.");
			}

		}

		if (user.getConfirmStatus() == 0) {
			throw new UserNotConfirmedException("error:We sent you a confirmation email with instructions on how to complete your Gooru registration. Please check your email, and then try again. Didn’t receive a confirmation email? Please contact us at support@goorulearning.org");
		}

		UserToken userToken = new UserToken();
		userToken.setUser(user);
		userToken.setSessionId(sessionId);
		userToken.setScope(SESSION);
		userToken.setCreatedOn(new Date(System.currentTimeMillis()));
		userToken.setApplication(application);
		userToken.setFirstLogin(userRepository.checkUserFirstLogin(user.getPartyUid()));

		identity.setLastLogin(new Date(System.currentTimeMillis()));
		this.getUserRepository().save(identity);
		this.getUserTokenRepository().save(userToken);
		Organization organization = null;
		if (userToken.getApplication() != null) {
			organization = userToken.getApplication().getOrganization();
		}

		redisService.addSessionEntry(userToken.getToken(), organization);

		if (identity.getIdp() != null) {
			SessionContextSupport.putLogParameter(IDP_NAME, identity.getIdp().getName());
		} else {
			SessionContextSupport.putLogParameter(IDP_NAME, GOORU_API);
		}

		return userToken;
	}

	@Override
	public void validateUserLogin(String username, String password, boolean isSsoLogin) {

		Identity identity = new Identity();
		identity.setExternalId(username);

		identity = this.getUserRepository().findByEmailIdOrUserName(username, true, false);

		if (identity == null) {
			throw new BadCredentialsException("error:Please double-check your email address and password, and then try logging in again.");
		}

		Date deactivateOn = identity.getDeactivatedOn();

		if (deactivateOn != null && deactivateOn.before(new Date(System.currentTimeMillis()))) {
			throw new BadCredentialsException("error: The user has been deactivated from the system.\nPlease contact Gooru Administrator.");
		}

		User user = this.getUserRepository().findByIdentity(identity);

		if (!isSsoLogin) {
			if (identity.getCredential() == null) {
				throw new BadCredentialsException("error:Please double check your email ID and password and try again.");
			}
			String encryptedPassword = encryptPassword(password);
			if (user == null || !(encryptedPassword.equals(identity.getCredential().getPassword()))) {

				throw new BadCredentialsException("error:Please double-check your password and try signing in again.");
			}

			if (user.getConfirmStatus() == 0) {
				throw new BadCredentialsException("error:We sent you a confirmation email with instructions on how to complete your Gooru registration. Please check your email, and then try again. Didn’t receive a confirmation email? Please contact us at support@goorulearning.org");
			}
		}

	}

	@Override
	public User findByToken(String sessionToken) {
		return userRepository.findByToken(sessionToken);
	}

	@Override
	public User findByIdentity(Identity identity) {
		return userRepository.findByIdentity(identity);
	}

	@Override
	public List<Identity> findAllIdentities() {
		return userRepository.findAllIdentities();
	}

	@Override
	public boolean findRegisteredUser(String emailId) {
		return userRepository.findRegisteredUser(emailId);
	}

	@Override
	public Identity findByEmail(String emailId) {
		return userRepository.findByEmail(emailId);
	}

	@Override
	public Profile getProfile(User user) {
		return userRepository.getProfile(user, false);
	}

	@Override
	public User findByGooruId(String gooruId) {
		return userRepository.findByGooruId(gooruId);
	}

	@Override
	public void registerUser(String emailId, String date) {
		userRepository.registerUser(emailId, date);
	}

	@Override
	public void updateAgeCheck(User user, String ageCheck) {
		userRepository.updateAgeCheck(user, ageCheck);
	}

	@Override
	public int findAgeCheck(User user) {
		return userRepository.findAgeCheck(user);
	}

	@Override
	public Identity findIdentityByResetToken(String resetToken) {
		return userRepository.findIdentityByResetToken(resetToken);
	}

	@Override
	public Identity findIdentityByRegisterToken(String registerToken) {
		return userRepository.findIdentityByRegisterToken(registerToken);
	}

	@Override
	public Identity findUserByGooruId(String gooruId) {
		return userRepository.findUserByGooruId(gooruId);
	}

	@Override
	public boolean checkUserAvailability(String keyword, CheckUser type, boolean isCollaboratorCheck) {
		return userRepository.checkUserAvailability(keyword, type, isCollaboratorCheck);
	}

	private void getUserParties(PartyPermission partyPermission, Map<String, String> permittedParties, List<String> userParties, List<String> userOrgs, List<String> partyPriveliges) {
		String partyUid = partyPermission.getParty().getPartyUid();
		if (partyPermission.getValidFrom() != null && (!permittedParties.containsKey(partyUid) || !permittedParties.get(partyUid).equals(PermissionType.VIEW.getType()))) {
			permittedParties.put(partyUid, partyPermission.getPermission());
		}
		if (!partyPriveliges.contains(partyPermission.getPermission())) {
			partyPriveliges.add(partyPermission.getPermission());
		}

		if (!userParties.contains(partyUid)) {
			userParties.add(partyUid);
		}
		if (!userOrgs.contains(partyUid)) {
			userOrgs.add(partyUid);
		}
		List<PartyPermission> partyPermissions = groupRepository.getUserPartyPermissions(partyUid);
		if (partyPermissions.size() > 0) {
			for (PartyPermission subPartyPermission : partyPermissions) {
				getUserParties(subPartyPermission, permittedParties, userParties, userOrgs, partyPriveliges);
			}
		}
	}

	private void getSubOrganizations(List<String> subOrgs, List<String> organizationIds) {
		List<String> orgIds = new ArrayList<String>();
		for (PartyPermission partyPermission : groupRepository.getUserOrganizations(organizationIds)) {
			if (partyPermission.getPermittedParty().getPartyType().equalsIgnoreCase(ORGANIZATION)) {
				if (!subOrgs.contains(partyPermission.getPermittedParty().getPartyUid())) {
					subOrgs.add(partyPermission.getPermittedParty().getPartyUid());
					orgIds.add(partyPermission.getPermittedParty().getPartyUid());
				}
			}
		}
		if (orgIds.size() > 0) {
			getSubOrganizations(subOrgs, orgIds);
		}
	}

	@Override
	public List<String> getUserPartyPermissions(User user) {
		List<String> userParties = new ArrayList<String>();
		List<String> userOrgs = new ArrayList<String>();
		List<String> partyPrivileges = new ArrayList<String>();
		Map<String, String> permittedParties = new HashMap<String, String>();
		PartyPermission partyPermission = new PartyPermission();
		partyPermission.setParty(user);
		getUserParties(partyPermission, permittedParties, userParties, userOrgs, partyPrivileges);
		return userParties;
	}

	@Cacheable("gooruCache")
	private UserCredential getUserCredentialCached(User user, String key, String sharedSecretKey) {
		return getUserCredential(user, key, sharedSecretKey);
	}

	private UserCredential getUserCredential(User user,final  String key, String sharedSecretKey) {
		String userCredentailKey = "user-credential:" + ((key != null && !key.equalsIgnoreCase(NA)) ? key : user.getGooruUId());
		List<String> authorities = new ArrayList<String>();
		if (user != null && user.getUserRoleSet() != null && user.getUserRoleSet().size() > 0) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				for (RoleEntityOperation roleEntityOperation : userRoleAssoc.getRole().getRoleOperations()) {
					EntityOperation entityOperation = roleEntityOperation.getEntityOperation();
					String authority = entityOperation.getEntityName() + GooruOperationConstants.ENTITY_ACTION_SEPARATOR + entityOperation.getOperationName();
					if (!authorities.contains(authority)) {
						authorities.add(authority);
					}
				}
			}
		}
		List<String> userParties = new ArrayList<String>();
		List<String> partyPrivileges = new ArrayList<String>();
		List<String> userOrgs = new ArrayList<String>();
		List<String> organizationIdList = new ArrayList<String>();
		List<String> userSuborgs = new ArrayList<String>();
		organizationIdList.add(user.getOrganization().getPartyUid());
		Map<String, String> permittedParties = new HashMap<String, String>();
		PartyPermission partyPermission = new PartyPermission();
		partyPermission.setParty(user);

		getSubOrganizations(userSuborgs, organizationIdList);
		getUserParties(partyPermission, permittedParties, userParties, userOrgs, partyPrivileges);
		UserCredential userCredential = new UserCredential();
		userOrgs.addAll(userSuborgs);

		String isAdminContentAccess = settingService.getOrganizationSetting(ConfigConstants.ADMIN_CONTENT_ACCESS, user.getOrganization().getPartyUid());
		if (isAdminContentAccess != null && isAdminContentAccess.equalsIgnoreCase(ONE) && isContentAdmin(user)) {
			userCredential.setIsAdminAccessContent(true);
		} else {
			userCredential.setIsAdminAccessContent(false);
		}
		userCredential.setOrganizationUid(user.getOrganization().getPartyUid());
		userCredential.setPrimaryOrganizatoinUid(user.getPrimaryOrganization().getPartyUid());
		if (user.getUserGroup() != null) {
			userCredential.setDefaultGroupUid(user.getUserGroup().getPartyUid());
		}
		userParties.add(userCredential.getOrganizationUid());
		userOrgs.add(userCredential.getOrganizationUid());

		// Given Access for Gooru content since it's open
		String isGooruContentAccess = settingService.getOrganizationSetting(ConfigConstants.ACCESS_GOORU_CONTENT, user.getOrganization().getPartyUid());

		if (isGooruContentAccess != null && isGooruContentAccess.equalsIgnoreCase(ONE)) {
			Organization gooruOrganization = organizationService.getOrganizationByCode(GOORU);
			if (gooruOrganization != null && !userOrgs.contains(gooruOrganization.getPartyUid())) {
				userOrgs.add(gooruOrganization.getPartyUid());
			}
		}

		String[] userPartiesAsArray = StringUtils.toStringArray(userParties);
		String[] userOrgsAsArray = StringUtils.toStringArray(userOrgs);
		String[] userSubOrgsArray = StringUtils.toStringArray(userSuborgs);
		userCredential.setPartyPermits(userPartiesAsArray);
		userCredential.setOrgPermits(userOrgsAsArray);
		userCredential.setPartyPermitsAsString("'" + org.apache.commons.lang.StringUtils.join(userPartiesAsArray, "','") + "'");
		userCredential.setOrgPermitsAsString("'" + org.apache.commons.lang.StringUtils.join(userOrgsAsArray, "','") + "'");
		userCredential.setSubOrganizationUidsString("'" + org.apache.commons.lang.StringUtils.join(userSubOrgsArray, "','") + "'");
		userCredential.setKey(userCredentailKey);
		userCredential.setUserUid(user.getPartyUid());
		userCredential.setToken(key);
		userCredential.setOperationAuthorities(authorities);
		userCredential.setOrganizationNfsInternalPath(user.getOrganization().getNfsStorageArea().getInternalPath());
		userCredential.setOrganizationNfsRealPath(user.getOrganization().getNfsStorageArea().getAreaPath());
		userCredential.setPartyOperations(partyPrivileges);
		userCredential.setSubOrganizationUids(userSuborgs);
		userCredential.setOrganizationCdnDirectPath(user.getOrganization().getNfsStorageArea().getCdnDirectPath());
		userCredential.setSharedSecretKey(sharedSecretKey);
		userCredential.setProfileAssetURI(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, user.getOrganization().getPartyUid()) + "/" + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, user.getOrganization().getPartyUid()));
		String storedSecret = settingService.getOrganizationSetting(ConstantProperties.SUPER_ADMIN_TOKEN, TaxonomyUtil.GOORU_ORG_UID);
		userCredential.setStoredSecretKey(storedSecret);
		UserToken userToken = userTokenRepository.findByToken(key);
		if (userToken != null && userToken.getApplication() != null) {
			userCredential.setApiKeySearchLimit(userToken.getApplication().getSearchLimit());
		}
		PartyCustomField partyCustomFieldTax = partyService.getPartyCustomeField(user.getPartyUid(), USER_TAXONOMY_ROOT_CODE, null);
		if (partyCustomFieldTax != null) {
			userCredential.setTaxonomyPreference(partyCustomFieldTax.getOptionalValue());
		}
		return userCredential;

	}

	@Override
	public UserCredential getUserCredential(User user, String key, String skipCache, String sharedSecretKey) {
		if (skipCache == null || skipCache.equals(ZERO)) {
			return getUserCredentialCached(user, key, sharedSecretKey);
		} else {
			return getUserCredential(user, key, sharedSecretKey);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<User> listUsers() {
		List<User> users = userRepository.listUsers();
		if (users != null) {
			for (User user : users) {
				user.setProfileImageUrl(this.buildUserProfileImageUrl(user));
			}
		}

		return users;
	}

	@Override
	public User findUserByImportCode(String userImportCode) {
		return userRepository.findUserByImportCode(userImportCode);
	}

	public String deleteUser(String gooruUId) throws Exception {

		String userDeleteMsg = "failed to delete";
		User user = findByGooruId(gooruUId);
		if (user != null) {
			userRepository.remove(User.class, user.getPartyUid());
			userDeleteMsg = "deleted successfully";
			indexProcessor.index(user.getPartyUid(), IndexProcessor.DELETE, USER);
		} else {
			throw new Exception("user not found");
		}

		return userDeleteMsg;

	}

	@Override
	public UserRole createRole(String name, String description, User user) throws Exception {

		UserRole userRole = findUserRoleByName(name);
		Organization gooruOrg = organizationService.getOrganizationById(TaxonomyUtil.GOORU_ORG_UID);
		if (userRole != null && user.getOrganization().equals(gooruOrg)) {
			throw new Exception("user role already exists");
		} else {
			userRole = new UserRole();
			userRole.setName(name);
			userRole.setDescription(description);
			//userRole.setOrganization(user.getOrganization());
			userRole.setOrganization(gooruOrg);
			userRepository.save(userRole);
		}
		return userRole;
	}
	
	@Override
	public UserRole findUserRoleByName(final String name) {
		return userRepository.findUserRoleByName(name, null);

	}

	@Override
	public List<RoleEntityOperation> updateRoleOperation(Integer roleId, String operations) throws Exception {
		UserRole userRole = null;
		RoleEntityOperation roleEntityOperation = null;
		List<RoleEntityOperation> roleEntityOperations = new ArrayList<RoleEntityOperation>();
		if (roleId != null) {
			userRole = findUserRoleByRoleId(roleId);
		}
		if (userRole == null) {
			throw new NotFoundException("user role not exists");
		}

		if (operations != null) {
			String[] operationsArr = operations.split(",");
			for (String operation : operationsArr) {
				String[] entityOperationArr = operation.split("\\.");
				String entityName = entityOperationArr[0];
				String operationName = entityOperationArr[1];
				final EntityOperation entityOperation = userRepository.findEntityOperation(entityName, operationName);
				if (entityOperation != null) {
					roleEntityOperation = userRepository.checkRoleEntity(roleId, entityOperation.getEntityOperationId());
					if (roleEntityOperation != null) {
						throw new NotFoundException("entity operation exists for the role");
					} else {
						roleEntityOperation = new RoleEntityOperation();
						roleEntityOperation.setUserRole(userRole);
						roleEntityOperation.setEntityOperation(entityOperation);
						roleEntityOperations.add(roleEntityOperation);
					}

				} else {
					throw new Exception("entity operation not exists");
				}
			}
			if (roleEntityOperations.size() > 0) {
				userRepository.saveAll(roleEntityOperations);
				roleEntityOperations = userRepository.getRoleEntityOperations(roleId);
			}

		}

		return roleEntityOperations;
	}

	@Override
	public UserRole findUserRoleByRoleId(Integer roleId) {

		return userRepository.findUserRoleByRoleId(roleId);
	}

	@Override
	public void removeRoleOperation(Integer roleId, String operations) throws Exception{

		UserRole userRole = null;
		RoleEntityOperation roleEntityOperation = null;
		List<RoleEntityOperation> roleEntityOperations = new ArrayList<RoleEntityOperation>();
		if (roleId != null) {
			userRole = findUserRoleByRoleId(roleId);
		}
		if (userRole == null) {
			throw new NotFoundException("User role not exists");
		}

		if (operations != null) {
			String[] operationsArr = operations.split(",");
			for (String operation : operationsArr) {
				String[] entityOperationArr = operation.split("\\.");
				String entityName = entityOperationArr[0];
				String operationName = entityOperationArr[1];
				EntityOperation entityOperation = userRepository.findEntityOperation(entityName, operationName);
				if (entityOperation != null) {
					roleEntityOperation = userRepository.checkRoleEntity(roleId, entityOperation.getEntityOperationId());
					if (roleEntityOperation != null) {
						roleEntityOperations.add(roleEntityOperation);
					}
				} else {
					throw new NotFoundException("Entity operation not exists");
				}
			}
			if (roleEntityOperations.size() > 0) {
				userRepository.removeAll(roleEntityOperations);
			}

		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public UserToken createSessionToken(User user, String sessionId, Application application) {
		UserToken sessionToken = new UserToken();
		sessionToken.setToken(UUID.randomUUID().toString());
		sessionToken.setScope(SESSION);
		sessionToken.setUser(user);
		sessionToken.setSessionId(sessionId);
		sessionToken.setApplication(application);
		sessionToken.setCreatedOn(new Date(System.currentTimeMillis()));
		try {
			userTokenRepository.saveUserSession(sessionToken);
		} catch (Exception e) {
			LOGGER.error("Error" + e.getMessage());
		}
		Organization organization = null;
		if (sessionToken.getApplication() != null) {
			organization = sessionToken.getApplication().getOrganization();
		}
		redisService.addSessionEntry(sessionToken.getToken(), organization);
		return sessionToken;
	}

	@Override
	public User getUserByUserName(String userName) {
		return userRepository.getUserByUserName(userName, false);
	}

	@Override
	public Map<String, Object> getUserAvailability(String keyword, String type, boolean isCollaboratorCheck, String resourceId, User apiCaller) {

		Map<String, Object> userInfo = new HashMap<String, Object>();

		boolean availability = false;
		Integer confirmStatus = 0;
		String gooruUId = null;
		String externalId = null;
		String userName = null;
		User user = null;
		if (type.equalsIgnoreCase(CheckUser.BYUSERNAME.getCheckUser())) {
			availability = this.getUserRepository().checkUserAvailability(keyword, CheckUser.BYUSERNAME, isCollaboratorCheck);
			if (availability) {
				Identity identity = this.findByEmailIdOrUserName(keyword, true, false);
				if (identity != null) {
					user = this.findByIdentity(identity);
					externalId = identity.getExternalId();
					userName = user.getUsername();
					if (resourceId != null) {
						Resource resource = resourceRepository.findResourceByContentGooruId(resourceId);
						String contentType = null;
						if (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType())) {
							contentType = LEARN_GUIDE;
						} else if (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
							contentType = ASSESSMENT;
						}
						availability = checkCollaboratorsPermission(resourceId, user, contentType);
					}
				} else {
					LOGGER.debug("User identity not exisit !");
					availability = false;
				}
			}
		} else if (type.equalsIgnoreCase(CheckUser.BYEMAILID.getCheckUser())) {
			availability = this.getUserRepository().checkUserAvailability(keyword, CheckUser.BYEMAILID, isCollaboratorCheck);
			if (availability) {
				Identity identity = this.findByEmail(keyword);
				user = identity.getUser();
			}
		}
		if (user != null) {
			confirmStatus = user.getConfirmStatus();
			gooruUId = user.getGooruUId();
		}
		userInfo.put(AVAILABILITY, availability);
		userInfo.put(GOORU_UID, gooruUId);
		userInfo.put(CONFIRM_STATUS, confirmStatus);
		userInfo.put(EXTERNAL_ID, externalId);
		userInfo.put(USERNAME, userName);
		userInfo.put(COLLABORATOR_CHECK, isCollaboratorCheck);

		return userInfo;
	}

	@Override
	public Boolean checkCollaboratorsPermission(String resourceId, User collaborator, String contentType) {
		Boolean hasPermission = false;
		if (resourceId != null) {
			List<PartyPermission> collaboratorPermissions = groupRepository.getUserPartyPermissions(collaborator.getPartyUid());
			if (contentType.equalsIgnoreCase(LEARN_GUIDE)) {
				Learnguide learnguide = learnguideRepository.findByContent(resourceId);
				for (Segment segment : learnguide.getResourceSegments()) {
					for (ResourceInstance resourceInstance : segment.getResourceInstances()) {
						if (!hasContentAccessPermission(collaboratorPermissions, collaborator, resourceInstance.getResource())) {
							hasPermission = false;
							LOGGER.debug("User organization and resource organization doesn't match !");
						} else {
							hasPermission = true;
						}
					}
				}
			} else if (contentType.equalsIgnoreCase(SCOLLECTION)) {
				// Collection collection = (Collection)
				// collectionRepository.get(Collection.class, resourceId);
				Collection collection = collectionRepository.getCollectionByGooruOid(resourceId, null);
				for (CollectionItem collectionItem : collection.getCollectionItems()) {
					if (collectionItem.getCollection() != null) {
						for (CollectionItem collectionItem2 : collectionItem.getCollection().getCollectionItems()) {
							if (!hasContentAccessPermission(collaboratorPermissions, collaborator, collectionItem2.getResource())) {
								hasPermission = false;
								LOGGER.debug("User organization and resource organization doesn't match !");
							} else {
								hasPermission = true;
							}
						}
					} else {
						if (!hasContentAccessPermission(collaboratorPermissions, collaborator, collectionItem.getResource())) {
							hasPermission = false;
							LOGGER.debug("User organization and resource organization doesn't match !");
						} else {
							hasPermission = true;
						}
					}
				}
			} else if (contentType.equalsIgnoreCase(ASSESSMENT)) {
				Assessment assessment = assessmentRepository.getByGooruOId(Assessment.class, resourceId);
				for (AssessmentSegment segment : assessment.getSegments()) {
					for (AssessmentSegmentQuestionAssoc aQuestionAssoc : segment.getSegmentQuestions()) {
						if (!hasContentAccessPermission(collaboratorPermissions, collaborator, aQuestionAssoc.getQuestion())) {
							hasPermission = false;
							LOGGER.debug("User organization and resource organization doesn't match !");
						} else {
							hasPermission = true;
						}
					}
				}
			}
		}
		return hasPermission;
	}

	private Boolean hasContentAccessPermission(List<PartyPermission> collaboratorPermissions, User user, Resource resource) {
		Boolean hasPermission = false;
		if ((user.getPrimaryOrganization().getPartyUid().equalsIgnoreCase(resource.getOrganization().getPartyUid())) || (user.getOrganization().getPartyUid().equalsIgnoreCase(resource.getOrganization().getPartyUid()))) {
			// System.out.println("********** orgs permission ***********");
			hasPermission = true;
		} else if (collaboratorPermissions != null && collaboratorPermissions.size() > 0) {
			for (PartyPermission partyPermission : collaboratorPermissions) {
				if (partyPermission.getParty().getPartyUid().equalsIgnoreCase(resource.getOrganization().getPartyUid())) {
					// System.out.println("********** orgs permission collaborator ***********");
					hasPermission = true;
				}
			}
		}
		/*
		 * else if(userCredential != null &&
		 * userCredential.getSubOrganizationUids().size() > 0){ for(String
		 * userOrganizationUid : userCredential.getSubOrganizationUids()) {
		 * if(userOrganizationUid
		 * .equalsIgnoreCase(user.getOrganization().getPartyUid())) {
		 * System.out.println("**********Sub orgs permission ***********");
		 * hasPermission = true; } } }
		 */
		return hasPermission;
	}

	@Override
	public Map<String, Object> getRegisterUserInfo(String userId) {

		Map<String, Object> registerUserInfo = new HashMap<String, Object>();

		String emailId = null;
		String accountType = "none";
		Boolean availability = false;
		Integer accountTypeId = null;
		Integer confirmStatus = 0;
		if (userId != null) {
			Identity identity = this.findUserByGooruId(userId);
			if (identity != null) {
				if (identity.getUser() != null) {
					availability = true;
				}
				emailId = identity.getExternalId();
				accountTypeId = identity.getUser().getAccountTypeId();
				confirmStatus = identity.getUser().getConfirmStatus();
				if (accountTypeId != null) {
					if (accountTypeId.equals(UserAccountType.ACCOUNT_PARENT)) {
						accountType = UserAccountType.userAccount.PARENT.getType();
					} else if (accountTypeId.equals(UserAccountType.ACCOUNT_CHILD)) {
						accountType = UserAccountType.userAccount.CHILD.getType();
					} else {
						accountType = UserAccountType.userAccount.NON_PARENT.getType();
					}
				}
			}
		}

		registerUserInfo.put(EMAIL_ID, emailId);
		registerUserInfo.put(ACCOUNTTYPE, accountType);
		registerUserInfo.put(AVAILABILITY, availability);
		registerUserInfo.put(CONFIRM_STATUS, confirmStatus);
		return registerUserInfo;
	}

	@Override
	public Integer calculateCurrentAge(String dateOfBirth) {

		int years = -1;
		Date currentDate = new Date();
		Date userDateOfBirth = null;
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		try {
			userDateOfBirth = simpleDateFormat.parse(dateOfBirth);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (userDateOfBirth.getTime() < currentDate.getTime()) {
			long milliseconds = currentDate.getTime() - userDateOfBirth.getTime();
			years = (int) (milliseconds / (1000 * 60 * 60 * 24 * 365.25));
		}
		return years;
	}

	@Override
	public User updateUserConfirmStatus(String gooruUserId, Integer confirmStatus, User apiCaller) throws Exception {

		if (isContentAdmin(apiCaller)) {
			User user = userRepository.findByGooruId(gooruUserId);
			user.setConfirmStatus(confirmStatus);
			userRepository.save(user);
			indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
			return user;
		} else {
			throw new Exception("You are not permitted to do this action.");
		}
	}

	@Override
	public User updateViewFlagStatus(String gooruUid, Integer viewFlag) {
		User user = userRepository.findByGooruId(gooruUid);
		if (user != null) {
			user.setViewFlag(viewFlag);
			userRepository.save(user);
			indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
		}
		return user;
	}

	@Override
	public String buildUserProfileImageUrl(User user) {
		return settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, user.getOrganization().getPartyUid()) + "/" + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, user.getOrganization().getPartyUid()) + user.getPartyUid() + ".png";
	}

	@Override
	public Identity findByEmailIdOrUserName(String userName, Boolean isLoginRequest, Boolean fetchAllUser) {
		return userRepository.findByEmailIdOrUserName(userName, isLoginRequest, fetchAllUser);
	}

	@Override
	public void deactivateUser(Identity identity) {
		identity.setDeactivatedOn(new Date(System.currentTimeMillis()));
		identity.setActive(Short.parseShort(ZERO));
		identity.getUser().setConfirmStatus(0);
		this.getUserRepository().save(identity);
		indexProcessor.index(identity.getUser().getPartyUid(), IndexProcessor.INDEX, USER);
	}

	@Override
	public void sendUserRegistrationConfirmationMail(String gooruUid, String accountType, String sessionId, String dateOfBirth, String gooruClassicUrl) throws Exception {
		User user = this.findByGooruId(gooruUid);
		if (user != null) {
			Application application = this.getApplicationRepository().getApplication(user.getOrganization().getPartyUid());
			UserToken userToken = this.createSessionToken(user, sessionId, application);
			this.getMailHandler().sendMailToConfirm(gooruUid, null, accountType, userToken.getToken(), dateOfBirth, gooruClassicUrl,null,null,null);
		}
	}

	@Override
	public boolean checkUserFirstLogin(String userId) {
		return userRepository.checkUserFirstLogin(userId);

	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public void setOperationAuthorizer(OperationAuthorizer operationAuthorizer) {
		this.operationAuthorizer = operationAuthorizer;
	}

	@Override
	public boolean checkPasswordWithAlphaNumeric(final String password) {
		int letterSize = 0;
		int digitSize = 0;
		for (int i = 0; i < password.length(); i++) {
			if (Character.isDigit(password.charAt(i))) {
				digitSize = digitSize + 1;
			} else if (Character.isLetter(password.charAt(i))) {
				letterSize = letterSize + 1;
			}
		}
		if ((digitSize == 0) || (letterSize == 0)) {
			return true;
		} else {
			return false;
		}
	}

	private void usernameValidation(String username, String orgnaizationUid) {
		if (username.length() < 5) {
			throw new BadCredentialsException("Username should be atleast 5 characters");
		}

		else if (username.length() > 20) {
			throw new BadCredentialsException("Username should be within 20 characters");
		}

		else if (username.charAt(0) >= '0' && username.charAt(0) <= '9' || checkUsernameStartAndEndWithSpecialCharacters(username, true)) {
			throw new BadCredentialsException("Username must begin with a letter");
		}

		else if (containsWhiteSpace(username)) {
			throw new BadCredentialsException("Username should not contain spaces");
		} else if (checkLatinWordInUserName(username)) {
			throw new BadCredentialsException("Username must contain only latin letters or digits.");
		}

		else if (checkUsernameStartAndEndWithSpecialCharacters(username, false)) {
			throw new BadCredentialsException("Username must end with a letter or digit");
		}

		else if (checkUsernameIsRestricted(username, orgnaizationUid)) {
			throw new BadCredentialsException("Username should not give the impression that the account has permissions ");
		}
	}

	@Override
	public void validatePassword(final String password, String userName) {
		if (password.length() < 5) {
			throw new BadCredentialsException("Password should be atleast 5 characters");
		}

		else if (password.length() > 14) {
			throw new BadCredentialsException("Password should be within 14 characters");
		}

		else if (checkPasswordWithAlphaNumeric(password)) {
			throw new BadCredentialsException("Password should contain atleast one letter and one digit");
		}
		if ((isNotEmptyString(userName)) && (userName.equalsIgnoreCase(password))) {
			throw new BadCredentialsException("Password should not be same with the Username");
		}

	}

	@Override
	public boolean checkLatinWordInUserName(String username) {
		boolean valid = username.matches("^[a-zA-Z0-9_.-]*$");
		if (!valid) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean checkUsernameStartAndEndWithSpecialCharacters(String username, boolean isStart) {

		if (isStart) {
			Pattern pattern = Pattern.compile("(^\\p{Punct})");
			Matcher matcher = pattern.matcher(username);
			boolean check = matcher.find();
			if (check) {
				return true;
			} else {
				return false;
			}
		} else {
			Pattern pattern = Pattern.compile("(\\p{Punct}$)");
			Matcher matcher = pattern.matcher(username);
			boolean check = matcher.find();
			if (check) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean checkUsernameIsRestricted(String username, String organizationUid) {
		int valid = 0;
		String restrictions = settingService.getConfigSetting(ConfigConstants.USER_NAME_RESTRICTIONS, 0, organizationUid);
		if (isNotEmptyString(restrictions)) {
			String[] userNameRestrictionsList = restrictions.split("\\,");
			for (int index = 0; index < userNameRestrictionsList.length; index++) {
				if (username.contains(userNameRestrictionsList[index])) {
					valid = 1;
					break;
				}
			}
		}
		if (valid == 1) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public List<User> listUsers(Map<String, String> filters) {
		return userRepository.listUsers(filters);
	}

	@Override
	public Timestamp getSystemCurrentTime() {
		return userRepository.getSystemCurrentTime();
	}

	@Override
	public UserToken partnerSignin(Map<String, Object> paramMap, String sessionId, String url, Long expires) throws Exception {
		UserToken userToken = null;
		Application application = this.getApplicationRepository().getApplication(paramMap.get(API_KEY).toString());
		if (application == null) {
			throw new BadCredentialsException("error:Invalid API Key.");
		} else {
			Long start = System.currentTimeMillis();
			if (start <= expires) {
				String signature = paramMap.get(SIGNATURE).toString();
				paramMap.put(EXPIRE, expires);
				String computedSignature = new GooruMd5Util().verifySignatureFromURL(url, paramMap, application.getSecretKey());
				if (signature.equals(computedSignature)) {
					final String emailId = paramMap.get(EMAIL_ID).toString();
					String password = paramMap.get(PASSWORD).toString();
					String apiKey = paramMap.get(API_KEY).toString();
					if (paramMap.get(EMAIL_ID).toString() != null && checkUserAvailability(emailId, CheckUser.BYEMAILID, false)) {
						userToken = signIn(emailId, password, apiKey, sessionId, false);
					} else {
						String firstName = emailId.substring(0, emailId.indexOf("@"));
						createUser(firstName, firstName, emailId, password, "", null, 1, application.getOrganization().getOrganizationCode(), 0, null, null, null, null, null, null, null, null, null, null);
						userToken = signIn(emailId, password, apiKey, sessionId, false);
					}

				} else {
					throw new BadCredentialsException("erro: invalid MD5 format");
				}
			} else {
				throw new BadCredentialsException("error: Login time is expired.");
			}
		}
		return userToken;
	}

	@Override
	public void validateUserOrganization(String organizationCode) throws Exception {
		Organization organization = organizationService.getOrganizationByCode(organizationCode);
		if (organization == null) {
			throw new Exception("Given organization doesn't exists !");
		}

		Boolean hasPermission = false;
		GooruAuthenticationToken authenticationContext = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		String[] partyPermits = authenticationContext.getUserCredential().getPartyPermits();
		for (String permittedPartyUid : partyPermits) {
			if (permittedPartyUid.equals(organization.getPartyUid())) {
				hasPermission = true;
			}
		}
		if (!hasPermission) {
			throw new AccessDeniedException("Permission denied for given organization");
		}
	}

	public PartyService getPartyService() {
		return partyService;
	}

	@Override
	public List<User> findByIdentities(List<String> idList) {
		return userRepository.findByIdentities(idList);
	}

	@Override
	public UserToken loginAs(String sessionToken, String gooruUid, String apiKey, Boolean isReference) throws Exception {
		UserToken userToken = new UserToken();
		if (gooruUid != null && sessionToken != null) {
			User loggedInUser = this.getUserRepository().findByToken(sessionToken);
			if (loggedInUser != null) {
				if (isContentAdmin(loggedInUser)) {
					User user = null;
					if (isReference) {
						user = this.getUserRepository().findByReferenceUid(gooruUid);
					} else {
						user = this.getUserRepository().findByGooruId(gooruUid);
					}
					if (user != null) {
						Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
						userToken = this.createSessionToken(user, sessionToken, application);
					}
				} else {
					throw new BadCredentialsException("error:This User doesn't have a permission to login as another user.");
				}
			}
		} else {
			throw new BadCredentialsException("error:GooruId/Session Token cannot be null or empty.");
		}
		return userToken;
	}
	
	public void getEventLogs(User newUser, String source, Identity newIdentity) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, "user.register");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context").toString()) :  new JSONObject();
		if(source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			context.put("registerType", accountCreatedType.GOOGLE_APP.getType());			
		}else if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.SSO.getType())) {
			context.put("registerType", accountCreatedType.SSO.getType());
		}else {
			context.put("registerType", "Gooru");
		}
		SessionContextSupport.putLogParameter("context", context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject").toString()) :  new JSONObject();
		if (newIdentity != null && newIdentity.getIdp() != null) {
			payLoadObject.put(IDP_NAME, newIdentity.getIdp().getName());
		} else {
			payLoadObject.put(IDP_NAME, GOORU_API);
		}
		Iterator<Identity> iter = newUser.getIdentities().iterator();
		if (iter != null && iter.hasNext()) {
			Identity identity = iter.next();
			payLoadObject.put(CREATED_TYPE, identity != null ? identity.getAccountCreatedType() : null);
		}
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		final JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session").toString()) :  new JSONObject();
		session.put("organizationUId", newUser != null ? newUser.getOrganizationUid() : null);
		SessionContextSupport.putLogParameter("session", session.toString());	
		JSONObject user = SessionContextSupport.getLog().get("user") != null ? new JSONObject(SessionContextSupport.getLog().get("user").toString()) :  new JSONObject();
		user.put("gooruUId", newUser != null ? newUser.getPartyUid() : null);
		SessionContextSupport.putLogParameter("user", user.toString());
	}
	
	@Override
	public void getEventLogs(Identity identity, final UserToken userToken) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, "user.login");
		JSONObject context = SessionContextSupport.getLog().get("context") != null ? new JSONObject(SessionContextSupport.getLog().get("context").toString()) : new JSONObject();
		if(identity != null && identity.getLoginType().equalsIgnoreCase("Credential")) {
			context.put("LogInType", "Gooru");
		}else if (identity != null && identity.getLoginType().equalsIgnoreCase("Apps")) {
			context.put("LogInType", accountCreatedType.GOOGLE_APP.getType());	
		}else {
			context.put("LogInType", accountCreatedType.SSO.getType());
		}
		SessionContextSupport.putLogParameter("context", context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get("payLoadObject") != null ? new JSONObject(SessionContextSupport.getLog().get("payLoadObject").toString()) : new JSONObject();
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session").toString()) : new JSONObject();
		session.put("sessionToken", userToken.getToken());
		SessionContextSupport.putLogParameter("session", session.toString());
		JSONObject user = SessionContextSupport.getLog().get("user") != null ? new JSONObject(SessionContextSupport.getLog().get("user").toString()) : new JSONObject();
		user.put("gooruUId", identity != null && identity.getUser() != null ? identity.getUser().getPartyUid() : null );
		SessionContextSupport.putLogParameter("user", user.toString());
	}
		
	public TaxonomyRespository getTaxonomyRespository() {
		return taxonomyRespository;
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}

	public void setInviteRepository(InviteRepository inviteRepository) {
		this.inviteRepository = inviteRepository;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public CollaboratorService getCollaboratorService() {
		return collaboratorService;
	}
	
	public Integer getChildAccountCount(String userUId){
		return userRepository.getChildAccountCount(userUId);
	}

	public ApplicationRepository getApplicationRepository() {
		return applicationRepository;
	}

}
