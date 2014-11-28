/////////////////////////////////////////////////////////////
// UserManagementServiceImpl.java
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
package org.ednovo.gooru.domain.service.userManagement;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.application.util.ProfileImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Credential;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.EntityOperation;
import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Idp;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationDomainAssoc;
import org.ednovo.gooru.core.api.model.PartyCategoryType;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserClassification;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotAllowedException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.CollaboratorService;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.eventlogs.UserEventlog;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.impl.UserServiceImpl;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.IdpRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.core.util.Base64Encoder;

@Service
public class UserManagementServiceImpl extends BaseServiceImpl implements UserManagementService, ParameterProperties, ConstantProperties {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserEventlog usereventlog;

	@Autowired
	private SettingService settingService;

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private PartyService partyService;

	@Autowired
	private IdpRepository idpRepository;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private ProfileImageUtil profileImageUtil;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private CollaboratorService collaboratorService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private InviteRepository inviteRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public Identity findUserByGooruId(String gooruId) {
		return this.getUserRepository().findUserByGooruId(gooruId);
	}

	@Override
	public Profile getProfile(User user) {
		return this.getUserRepository().getProfile(user, false);
	}

	@Override
	public SearchResults<Map<String, Object>> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit) {
		List<User> users = this.getUserRepository().getFollowedOnUsers(gooruUId, offset, limit);
		List<Map<String, Object>> usersObj = new ArrayList<Map<String, Object>>();
		for (User user : users) {
			usersObj.add(setUserObj(user));
		}
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(usersObj);
		result.setTotalHitCount(this.getUserRepository().getFollowedOnUsersCount(gooruUId));
		return result;
	}

	@Override
	public SearchResults<Map<String, Object>> getFollowedByUsers(String gooruUId, Integer offset, Integer limit) {
		List<User> users = this.getUserRepository().getFollowedByUsers(gooruUId, offset, limit);
		List<Map<String, Object>> usersObj = new ArrayList<Map<String, Object>>();
		for (User user : users) {
			usersObj.add(setUserObj(user));
		}
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(usersObj);
		result.setTotalHitCount(this.getUserRepository().getFollowedByUsersCount(gooruUId));
		return result;
	}

	@Override
	public User findByGooruId(String gooruId) {
		return userRepository.findByGooruId(gooruId);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	@Override
	public Profile getUserProfile(String gooruUid, Integer activeFlag) {
		User user = this.findByGooruId(gooruUid);
		if (user == null || user.getGooruUId().toLowerCase().contains(ANONYMOUS)) {
			throw new BadRequestException(generateErrorMessage(GL0056, USER));
		}
		Profile profile = this.getUserRepository().getProfile(user, false);
		String externalId = null;
		String profileImageUrl = this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, 0, TaxonomyUtil.GOORU_ORG_UID) + '/' + this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID).toString() + user.getGooruUId() + DOT_PNG;
		if (user.getAccountTypeId() != null && (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD))) {
			externalId = this.findUserByGooruId(user.getParentUser().getGooruUId()).getExternalId();
		} else {
			externalId = this.findUserByGooruId(user.getGooruUId()).getExternalId();
		}
		profile.getUser().setProfileImageUrl(profileImageUrl);
		profile.setExternalId(externalId);
		profile.getUser().setEmailId(externalId);
		profile.getUser().setMeta(userMeta(user));
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
		CustomTableValue gradeType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
		profile.setCourses(this.getUserRepository().getUserClassifications(gooruUid, type.getCustomTableValueId(), activeFlag));
		if (gradeType != null && gradeType.getCustomTableValueId() != null) {
			profile.setGrade(this.getUserRepository().getUserGrade(gooruUid, gradeType.getCustomTableValueId(), activeFlag));
		}
		try {
			this.getUsereventlog().getEventLogs(false, true, user, null, true, true);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e);
		}
		return profile;
	}

	@Override
	public Profile updateProfileInfo(Profile newProfile, String gooruUid, User apiCaller, String activeFlag, Boolean emailConfirmStatus, String showProfilePage, String accountType, String password) {
		User user = this.getUserRepository().findByGooruId(gooruUid);
		JSONObject itemData = new JSONObject();
		if (user == null) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}
		Profile profile = this.getUserService().getProfile(user);
		if (showProfilePage != null) {
			PartyCustomField partyCustomField = new PartyCustomField();
			partyCustomField.setOptionalValue(showProfilePage);
			partyCustomField.setOptionalKey(SHOW_PROFILE_PAGE);
			partyCustomField.setCategory(USER_META);
			this.getPartyService().updatePartyCustomField(user.getPartyUid(), partyCustomField, user);
		}
		if (profile != null) {
			Identity identity = this.getUserRepository().findUserByGooruId(gooruUid);
			if (password != null && gooruUid.equalsIgnoreCase(apiCaller.getGooruUId()) && identity.getCredential() != null) {
				this.getUserService().validatePassword(password, identity.getUser().getUsername());
				String encryptedPassword = this.encryptPassword(password);
				identity.getCredential().setPassword(encryptedPassword);
				this.getUserRepository().save(identity);
			}
			try {
				if (newProfile != null) {
					if (newProfile.getAboutMe() != null) {
						itemData.put(ABOUT_ME, newProfile.getAboutMe());
						profile.setAboutMe(newProfile.getAboutMe());
					}
					if (newProfile.getSubject() != null) {
						itemData.put(SUBJECT, newProfile.getSubject());
						profile.setSubject(newProfile.getSubject());
					}
					if (newProfile.getGrade() != null) {
						itemData.put(GRADE, newProfile.getGrade());
						profile.setGrade(addGrade(newProfile.getGrade(), user, apiCaller, activeFlag));
					}
					addCourse(newProfile.getCourses(), profile.getUser(), apiCaller, activeFlag);
					if (newProfile.getSchool() != null) {
						itemData.put(SCHOOL, newProfile.getSchool());
						profile.setSchool(newProfile.getSchool());
					}
					if (newProfile.getGender() != null && newProfile.getGender().getGenderId() != null) {
						itemData.put(GENDER_ID, newProfile.getGender().getGenderId());
						profile.setGender(this.getUserRepository().getGenderByGenderId(newProfile.getGender().getGenderId()));
					}
					if (newProfile.getUserType() != null) {
						itemData.put(_USER_TYPE, newProfile.getUserType());
						profile.setUserType(newProfile.getUserType());
					}
					if (newProfile.getNotes() != null) {
						itemData.put(NOTES, newProfile.getNotes());
						profile.setNotes(newProfile.getNotes());
					}
					if (identity != null) {
						if (user.getAccountTypeId() != null && user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
							user.setEmailId(user.getParentUser().getIdentities().iterator().next().getExternalId());
						} else {
							user.setEmailId(identity.getExternalId());
						}
					}
					if (newProfile.getUser() != null ) {
						if(newProfile.getUser().getActive() != null){
							identity.setActive(newProfile.getUser().getActive());
							user.setActive(newProfile.getUser().getActive());
							if(newProfile.getUser().getActive() == 0){
								this.getMailHandler().sendUserDisabledMail(gooruUid);
							}
							this.getUserRepository().save(identity);
						}
						if (identity != null && newProfile.getUser().getEmailId() != null && !newProfile.getUser().getEmailId().isEmpty()) {
							boolean emailAvailability = this.getUserRepository().checkUserAvailability(newProfile.getUser().getEmailId(), CheckUser.BYEMAILID, false);
							if (emailAvailability) {
								throw new BadRequestException(generateErrorMessage("GL0084" ,newProfile.getUser().getEmailId(),"Email id"));
							}
							if (emailConfirmStatus || (isContentAdmin(apiCaller) && !apiCaller.getPartyUid().equals(gooruUid))) {
								identity.setExternalId(newProfile.getUser().getEmailId());
								this.getUserRepository().save(identity);
								user.setEmailId(newProfile.getUser().getEmailId());
								if (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
									SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
									String date = dateFormat.format(profile.getDateOfBirth());
									Integer age = this.getUserService().calculateCurrentAge(date);
									if (age >= 13) {
										user.setAccountTypeId(UserAccountType.ACCOUNT_NON_PARENT);
										Map<String, String> childData = new HashMap<String, String>();
										Map<String, String> parentData = new HashMap<String, String>();
										parentData.put(_GOORU_UID, user.getGooruUId());
										parentData.put(EVENT_TYPE, CustomProperties.EventMapping.CHILD_13_CONFIRMATION.getEvent());
										parentData.put(GOORU_USER_NAME, user.getUsername());
										parentData.put(PARENT_EMAIL_ID, user.getParentUser().getIdentities().iterator().next().getExternalId());
										childData.put(_GOORU_UID, user.getGooruUId());
										childData.put(EVENT_TYPE, CustomProperties.EventMapping.STUDENT_SEPARATION_CONFIRMATION.getEvent());
										childData.put(CHILD_EMAIL_ID, identity.getExternalId());
										this.getMailHandler().handleMailEvent(childData);
										this.getMailHandler().handleMailEvent(parentData);
									}

								}

							} else {
								Map<String, String> dataMap = new HashMap<String, String>();
								dataMap.put(_GOORU_UID, user.getGooruUId());
								dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.GOORU_EXTERNALID_CHANGE.getEvent());
								if (user.getAccountTypeId() != null && user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
									dataMap.put(OLD_EMAIL_ID, user.getParentUser().getIdentities().iterator().next().getExternalId());
								} else {
									dataMap.put(OLD_EMAIL_ID, identity.getExternalId());
								}
								dataMap.put(NEW_MAIL_ID, newProfile.getUser().getEmailId());
								dataMap.put(BASE_URL, this.getSettingService().getConfigSetting(ConfigConstants.RESET_EMAIL_CONFIRM_RESTENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "&confirmStatus=true&newMailId=" + newProfile.getUser().getEmailId() + "&userId=" + user.getGooruUId());
								this.getMailHandler().handleMailEvent(dataMap);
							}
						}
						if (accountType != null) {
							if (accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
								user.setAccountTypeId(UserAccountType.ACCOUNT_PARENT);
							} else if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
								user.setAccountTypeId(UserAccountType.ACCOUNT_CHILD);
							} else if (accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType())) {
								user.setAccountTypeId(UserAccountType.ACCOUNT_NON_PARENT);
							}
						}
						if (newProfile.getUser().getFirstName() != null) {
							itemData.put(FIRST_NAME, newProfile.getUser().getFirstName());
							user.setFirstName(newProfile.getUser().getFirstName());
						}
						if (newProfile.getUser().getConfirmStatus() != null) {
							itemData.put(CONFIRM_STATUS, newProfile.getUser().getConfirmStatus());
							user.setConfirmStatus(newProfile.getUser().getConfirmStatus());
						}
						if (newProfile.getUser().getLastName() != null) {
							itemData.put(LAST_NAME, newProfile.getUser().getLastName());
							user.setLastName(newProfile.getUser().getLastName());
						}
						if (newProfile.getUser().getUsername() != null && !this.getUserRepository().checkUserAvailability(newProfile.getUser().getUsername(), CheckUser.BYUSERNAME, false)) {
							itemData.put(USERNAME, newProfile.getUser().getUsername());
							user.setUsername(newProfile.getUser().getUsername());
						}
					}
				}
			} catch (Exception e) {
				LOGGER.debug("Error" + e);
			}
			profile.setUser(user);
			this.getUserRepository().save(profile);
			PartyCustomField partyCustomField = this.getPartyService().getPartyCustomeField(profile.getUser().getPartyUid(), USER_CONFIRM_STATUS, profile.getUser());
			if (partyCustomField != null && !partyCustomField.getOptionalValue().equalsIgnoreCase(TRUE) && newProfile.getUser() != null && newProfile.getUser().getConfirmStatus() != null && newProfile.getUser().getConfirmStatus() == 1) {
				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put(GOORU_UID, profile.getUser().getPartyUid());
				dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
				if (profile.getUser().getAccountTypeId() != null && profile.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
					if (profile.getUser().getParentUser().getIdentities() != null) {
						dataMap.put(RECIPIENT, profile.getUser().getParentUser().getIdentities().iterator().next().getExternalId());
					}
				} else {
					if (profile.getUser().getIdentities() != null) {
						dataMap.put(RECIPIENT, profile.getUser().getIdentities().iterator().next().getExternalId());
					}
				}
				partyCustomField.setOptionalValue(TRUE);
				this.getUserRepository().save(partyCustomField);
				this.getMailHandler().handleMailEvent(dataMap);
			}
			CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			profile.setCourses(this.getUserRepository().getUserClassifications(gooruUid, type.getCustomTableValueId(), null));
		}
		try {
			this.getUsereventlog().getEventLogs(true, false, user, itemData, false, false);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e);
		}
		return profile;
	}

	private void addCourse(List<UserClassification> courses, User user, User apiCaller, String activeFlag) {
		if (courses != null) {
			CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			for (UserClassification course : courses) {
				if (course.getCode() != null && course.getCode().getCodeId() != null) {
					UserClassification existingCourse = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), course.getCode().getCodeId(), null, null);
					if (existingCourse == null) {
						Code code = this.getTaxonomyRespository().findCodeByCodeId(course.getCode().getCodeId());
						if (code != null && code.getDepth() == 2) {
							UserClassification userClassification = new UserClassification();
							userClassification.setCode(code);
							userClassification.setType(type);
							userClassification.setActiveFlag(activeFlag == null ? 1 : Integer.parseInt(activeFlag));
							userClassification.setUser(user);
							userClassification.setCreator(apiCaller);
							this.getUserRepository().save(userClassification);
							this.getUserRepository().flush();
						}
					} else {
						if (activeFlag != null) {
							existingCourse.setActiveFlag(Integer.parseInt(activeFlag));
							this.getUserRepository().save(existingCourse);
						}
					}
				}
			}
		}
	}

	private void deleteCourse(List<UserClassification> courses, User user, User apiCaller) {
		if (courses != null) {
			CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			for (UserClassification course : courses) {
				if (course.getCode() != null && course.getCode().getCodeId() != null) {
					UserClassification existingCourse = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), course.getCode().getCodeId(), apiCaller == null ? null : apiCaller.getGooruUId(), null);
					if (existingCourse != null) {
						this.getUserRepository().remove(existingCourse);
					}
				}
			}
		}
	}

	@Override
	public User createUserWithValidation(User newUser, String password, String school, Integer confirmStatus, Boolean useGeneratedPassword, Boolean sendConfirmationMail, User apiCaller, String accountType, String dateOfBirth, String userParentId, String sessionId, String gender, String childDOB,
			String gooruBaseUrl, Boolean token, HttpServletRequest request, String role, String mailConfirmationUrl) throws Exception {
		User user = new User();
		this.validateAddUser(newUser, apiCaller, childDOB, accountType, dateOfBirth, password);
		Boolean isAdminCreateUser = false;
		Integer addedBySystem = 0;
		if (apiCaller != null && isContentAdmin(apiCaller)) {
			addedBySystem = 1;
			isAdminCreateUser = true;
			if (useGeneratedPassword) {
				password = UUID.randomUUID().toString();
			}
			confirmStatus = 1;
		}
		List<InviteUser> inviteuser = null;
		if (accountType == null || !accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			inviteuser = this.getInviteRepository().getInviteUserByMail(newUser.getEmailId(), COLLABORATOR);
		}
		user = createUser(newUser, password, school, confirmStatus, addedBySystem, null, accountType, dateOfBirth, userParentId, gender, childDOB, null, request, role, mailConfirmationUrl);
		Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
		UserToken userToken = this.createSessionToken(user, sessionId, application);
		if (user != null && token) {
			Identity identity = this.findUserByGooruId(user.getGooruUId());
			if (identity != null) {
				user.setToken(identity.getCredential().getToken());
			}
		}
		if(user.getAccountTypeId() != UserAccountType.ACCOUNT_CHILD) {
			user.setEmailId(newUser.getEmailId());
		}
		if (user != null && sendConfirmationMail && (inviteuser == null || inviteuser.size() == 0)) {
			if (isAdminCreateUser) {
		        	this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), null, gooruBaseUrl, mailConfirmationUrl, null, null);
			} else {
				if (user.getAccountTypeId() == null || !user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
					this.getMailHandler().sendMailToConfirm(user.getGooruUId(), null, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, mailConfirmationUrl, null, null);
				}
			}
		}
		return user;
	}

	public String getUserCourse(String userId) {
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
		List<UserClassification> userCourses = this.getUserRepository().getUserClassifications(userId, type.getCustomTableValueId(), null);
		StringBuffer courses = new StringBuffer();
		if (userCourses != null) {
			for (UserClassification userCourse : userCourses) {
				courses = courses.append(courses.length() == 0 ? userCourse.getCode().getLabel() : "," + userCourse.getCode().getLabel());
			}
		}
		return courses.length() == 0 ? null : courses.toString();
	}

	@Override
	public User resendConfirmationMail(String gooruUid, User apiCaller, String sessionId, String gooruBaseUrl, String type) throws Exception {
		User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, "User"));
		}

		Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
		UserToken userToken = this.createSessionToken(user, sessionId, application);
		String password = user.getIdentities().iterator().next().getCredential() == null ? null : user.getIdentities().iterator().next().getCredential().getPassword();
		Identity identity = null;
		if (user.getAccountTypeId() != null) {
			identity = this.getUserService().findUserByGooruId(user.getGooruUId());
			if (user.getAccountTypeId() == UserAccountType.ACCOUNT_CHILD) {
				String encryptedPassword = this.encryptPassword(password);
				Credential credential = user.getIdentities().iterator().next().getCredential();
				credential.setPassword(encryptedPassword);
				identity.setCredential(credential);
				this.getUserRepository().save(identity);
			}
		}

		String accountType = null;
		if (user.getAccountTypeId() != null) {
			if (user.getAccountTypeId() == UserAccountType.ACCOUNT_CHILD) {
				accountType = UserAccountType.userAccount.CHILD.getType();
			} else if (user.getAccountTypeId() == UserAccountType.ACCOUNT_PARENT) {
				accountType = UserAccountType.userAccount.PARENT.getType();
			} else {
				accountType = UserAccountType.userAccount.NON_PARENT.getType();
			}
		}
		Profile profile = this.getUserService().getProfile(user);
		if (isContentAdmin(apiCaller)) {
			this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), null, gooruBaseUrl, null, null, null);
		} else {
			String dateOfBirth = "";
			if (profile.getDateOfBirth() != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				dateOfBirth = dateFormat.format(profile.getDateOfBirth());
			}
			if (user.getAccountTypeId() != null && type.equalsIgnoreCase(WELCOME)) {
				if (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
					CustomTableValue gradeType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
					String userGrade = this.getUserRepository().getUserGrade(user.getGooruUId(), gradeType.getCustomTableValueId(), null);
					this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, null, userGrade, getUserCourse(user.getGooruUId()));
				}
			} else {
				this.getMailHandler().sendMailToConfirm(user.getGooruUId(), null, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, null, null, null);
			}
		}
		return user;
	}

	private void validateAddUser(User user, User apicaller, String childDOB, String accountType, String dateOfBirth, String password) {
		if ((isNotEmptyString(childDOB)) && (isNotEmptyString(accountType)) && childDOB != null && !childDOB.equalsIgnoreCase(_NULL)) {
			Integer age = this.calculateCurrentAge(childDOB);
			if (age < 0) {
				throw new BadRequestException(generateErrorMessage("GL0059"));
			}
		}
		if ((isNotEmptyString(dateOfBirth)) && (isNotEmptyString(accountType)) && dateOfBirth != null && !dateOfBirth.equalsIgnoreCase(_NULL)) {
			Integer age = this.calculateCurrentAge(dateOfBirth);
			if (age < 0) {
				throw new BadRequestException(generateErrorMessage("GL0059"));
			}
		if (age < 13 && age >= 0 && (accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType()))) {
				throw new UnauthorizedException(generateErrorMessage("GL0060","13"));
			}
		}
		if (!isNotEmptyString(user.getFirstName())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "First name"));
		}

		if (!isNotEmptyString(user.getOrganization() != null ? user.getOrganization().getOrganizationCode() : null)) {
			throw new UnauthorizedException(generateErrorMessage("GL0061", "Organization code"));
		}
		if (!isNotEmptyString(user.getLastName())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Last name"));
		}
		if (!isNotEmptyString(user.getEmailId())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Email"));
		}
		if (!isNotEmptyString(password)) {
			if (apicaller != null && !isContentAdmin(apicaller)) {
				throw new BadRequestException(generateErrorMessage("GL0061", "Password"));
			} 
		} else if (password.length() < 5) {
			throw new BadRequestException(generateErrorMessage("GL0064", "5"));
		}
		if (!isNotEmptyString(user.getUsername())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Username"));
		} else if (user.getUsername().length() < 4) {
			throw new BadRequestException(generateErrorMessage("GL0065", "4"));
		}else if (user.getUsername().length() > 21) {
			throw new BadRequestException(generateErrorMessage("GL0100", "21"));
		}
		boolean usernameAvailability = this.getUserRepository().checkUserAvailability(user.getUsername(), CheckUser.BYUSERNAME, false);
		if (usernameAvailability) {
			throw new NotFoundException(generateErrorMessage("GL0084", user.getUsername(),"username"));
		}
		boolean emailidAvailability = this.getUserRepository().checkUserAvailability(user.getEmailId(), CheckUser.BYEMAILID, false);
		if (accountType != null) {
			if (emailidAvailability && (!accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()))) {
				throw new NotFoundException(generateErrorMessage("GL0062"));
			}
		} else {
			if (emailidAvailability) {
				throw new NotFoundException(generateErrorMessage("GL0062"));
			}
		}

	}

	@Override
	public void validateUserOrganization(String organizationCode, String superAdminToken) throws Exception {
		if (superAdminToken == null || !superAdminToken.equals(this.getSettingService().getOrganizationSetting(SUPER_ADMIN_TOKEN, TaxonomyUtil.GOORU_ORG_UID))) {
			Organization organization = this.getOrganizationService().getOrganizationByCode(organizationCode);
			if (organization == null) {
				throw new BadRequestException(generateErrorMessage("GL0066"));
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
				throw new AccessDeniedException(generateErrorMessage("GL0067"));
			}
		}
	}

	private Integer calculateCurrentAge(String dateOfBirth) {
		int years = -1;
		Date currentDate = new Date();
		Date userDateOfBirth = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		try {
			userDateOfBirth = simpleDateFormat.parse(dateOfBirth);
		} catch (ParseException e) {
			LOGGER.error("Error" + e.getMessage());
			throw new BadCredentialsException("Invalid date format. Expected format is MM/DD/YYY");
		}
		if (userDateOfBirth.getTime() < currentDate.getTime()) {
			long milliseconds = currentDate.getTime() - userDateOfBirth.getTime();
			years = (int) (milliseconds / (1000 * 60 * 60 * 24 * 365.25));
		}
		return years;
	}

	private Boolean isNotEmptyString(String field) {
		return StringUtils.hasLength(field);
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
			this.getUserTokenRepository().saveUserSession(sessionToken);
		} catch (Exception e) {
			LOGGER.error("Error" + e.getMessage());
		}
		Organization organization = null;
		if (sessionToken.getApplication() != null) {
			organization = sessionToken.getApplication().getOrganization();
		}
		this.getRedisService().addSessionEntry(sessionToken.getToken(), organization);
		return sessionToken;
	}

	@Override
	public User createUser(User newUser, String password, String school, Integer confirmStatus, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId, String remoteEntityId, String gender, String childDOB, String source, String emailSSO,
			HttpServletRequest request, String role, String mailConfirmationUrl) throws Exception {
		List<InviteUser> inviteuser = null;
		if (accountType == null || !accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
		 inviteuser = this.getInviteRepository().getInviteUserByMail(newUser.getEmailId(), COLLABORATOR);
		}
		if (inviteuser !=null && inviteuser.size() > 0) {
			confirmStatus = 1;
		}
		if (confirmStatus == null) {
			confirmStatus = 0;
		}
		if (newUser.getOrganization() != null && newUser.getOrganization().getOrganizationCode() != null && newUser.getOrganization().getOrganizationCode().length() > 0 && newUser.getOrganization().getOrganizationCode().equalsIgnoreCase(GLOBAL)) {
			confirmStatus = 1;
		}
		Identity identity = new Identity();
		if (emailSSO != null) {
			identity.setSsoEmailId(emailSSO);
		}
		if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			identity.setFirstName(newUser.getFirstName());
			identity.setLastName(newUser.getLastName());
			identity.setExternalId(newUser.getUsername());
			confirmStatus = 1;
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.CHILD.getType());
		} else {
			identity.setFirstName(newUser.getFirstName());
			identity.setLastName(newUser.getLastName());
			identity.setExternalId(newUser.getEmailId());
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.NORMAL.getType());
		}
		if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			identity.setAccountCreatedType(UserAccountType.accountCreatedType.GOOGLE_APP.getType());
		} else if (source != null) {
			identity.setAccountCreatedType(source);
		}
		String domain = newUser.getEmailId().substring(newUser.getEmailId().indexOf("@") + 1, newUser.getEmailId().length());
		Idp idp = null;
		if (remoteEntityId != null) {
			idp = this.getIdpRepository().findByName(remoteEntityId);
			if (idp == null) {
				idp = new Idp();
				idp.setName(remoteEntityId);
				this.getUserRepository().save(idp);
			}
		} else {
			idp = this.getIdpRepository().findByName(domain);
		}
		if (idp != null) {
			identity.setIdp(idp);
		}
		Organization organization = null;
		if (idp != null) {
			OrganizationDomainAssoc domainOrganizationAssoc = this.getIdpRepository().findByDomain(idp);
			if (domainOrganizationAssoc != null ) { 
				organization  = domainOrganizationAssoc.getOrganization();
			}
		}
		if (organization == null && newUser.getOrganization() != null && newUser.getOrganization().getOrganizationCode() != null) {
			organization = this.getOrganizationService().getOrganizationByCode(newUser.getOrganization().getOrganizationCode().toLowerCase());
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
			} else {
				userRole = userRepository.findUserRoleByName(UserRoleType.AUTHENTICATED_USER.getType(), null);
			}
		}
		user.setFirstName(newUser.getFirstName());
		user.setLastName(newUser.getLastName());
		// create a party
		user.setPartyName(organization.getPartyName());
		user.setPartyType(_USER);
		user.setCreatedOn(new Date(System.currentTimeMillis()));
		if (newUser.getUsername() == null) {
			user.setUsername(newUser.getEmailId());
		} else {
			user.setUsername(newUser.getUsername());
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
			}
		} else {
			user.setAccountTypeId(UserAccountType.ACCOUNT_NON_PARENT);
			accountType = UserAccountType.userAccount.NON_PARENT.getType();
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
				if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
					credential.setPassword(password);
				} else {
					credential.setPassword(encryptPassword(password));
				}
			}
		}
		Profile profile = new Profile();
		profile.setUser(user);
		profile.setSchool(school);
		if (role != null && (role.equalsIgnoreCase(UserRole.UserRoleType.STUDENT.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.PARENT.getType())  || role.equalsIgnoreCase(UserRole.UserRoleType.TEACHER.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.AUTHENTICATED_USER.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.OTHER.getType()))) {
			profile.setUserType(role);
		}
		if (dateOfBirth != null && accountType != null && !dateOfBirth.equalsIgnoreCase(_NULL)) {
			if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()) && userParentId != null) {
				if (dateOfBirth.equalsIgnoreCase("00/00/0000")) {
					profile.setDateOfBirth(this.getProfile(this.getUser(userParentId)).getChildDateOfBirth());
				} else {
					Integer age = this.calculateCurrentAge(dateOfBirth);
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
					Date date = dateFormat.parse(dateOfBirth);
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
				SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				Date date = dateFormat.parse(dateOfBirth);
				if (age >= 13 && accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType())) {
					profile.setDateOfBirth(date);
				}
			}
		}
		if (childDOB != null && !childDOB.equalsIgnoreCase(_NULL) && accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
			Integer age = this.calculateCurrentAge(childDOB);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
			Date date = dateFormat.parse(childDOB);
			if (age < 13 && age >= 0) {
				profile.setChildDateOfBirth(date);
			}
		}
		if (isNotEmptyString(gender)) {
			profile.setGender(this.getUserRepository().getGenderByGenderId(gender));
		}
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
		if (password != null && credential != null) {
			this.getUserRepository().save(credential);
		}
		identity.setCredential(credential);
		this.getUserRepository().save(identity);
		this.getPartyService().createUserDefaultCustomAttributes(user.getPartyUid(), user);
		this.getPartyService().createTaxonomyCustomAttributes(user.getPartyUid(), user);
		this.getUserRepository().flush();
		if (inviteuser != null && inviteuser.size() > 0 ) {
			this.getCollaboratorService().updateCollaboratorStatus(newUser.getEmailId(),user);
		}
		userCreatedDevice(user.getPartyUid(), request);
		PartyCustomField partyCustomField = this.getPartyService().getPartyCustomeField(profile.getUser().getPartyUid(), USER_CONFIRM_STATUS, identity.getUser());
		if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			Map<String, String> dataMap = new HashMap<String, String>();
			if (identity != null && identity.getUser() != null) {
				dataMap.put(GOORU_UID, identity.getUser().getPartyUid());
			}
			dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
			if (identity != null && identity.getExternalId() != null) {
				dataMap.put(RECIPIENT, identity.getExternalId());
			}
			partyCustomField.setOptionalValue(TRUE);
			this.getUserRepository().save(partyCustomField);
			this.getMailHandler().handleMailEvent(dataMap);
		}
		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
		try {
			this.getUsereventlog().getEventLogs(user, source, identity);
		} catch (JSONException e) {
			LOGGER.error("Error" + e.getMessage());
		}
		return user;
	}

	private void userCreatedDevice(String partyUid, HttpServletRequest request) {
		PartyCustomField partyCustomField = new PartyCustomField();
		partyCustomField.setCategory(USER_META);
		partyCustomField.setOptionalValue(request.getHeader(USER_AGENT));
		partyCustomField.setOptionalKey(GOORU_USER_CREATED_DEVICE);
		this.getPartyService().createPartyCustomField(partyUid, partyCustomField, null);
	}

	@Override
	public User getUser(String gooruUId) throws Exception {
		if (gooruUId == null || gooruUId.equalsIgnoreCase("")) {
			throw new BadRequestException("User id cannot be null or empty");
		}
		User user = getUserRepository().findByGooruId(gooruUId);
		if (user == null) {
			throw new BadRequestException("User not found");
		}
		user.setProfileImageUrl(buildUserProfileImageUrl(user));
		return user;
	}

	public static String getDefaultUserRoles(String organizationUid) {
		String roles = SettingService.getInstance().getOrganizationSetting(ConfigConstants.DEFAULT_USER_ROLES, organizationUid);
		return roles != null ? roles : null;
	}

	@Override
	public String buildUserProfileImageUrl(User user) {
		return this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, user.getOrganization().getPartyUid()) + "/" + this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_BUCKET, user.getOrganization().getPartyUid()) + user.getPartyUid() + ".png";
	}

	@Override
	public String encryptPassword(String password) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw new BadRequestException(generateErrorMessage("GL0068"), e);
		}
		try {
			messageDigest.update(password.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw new BadRequestException(generateErrorMessage("GL0069"), e);
		}
		byte raw[] = messageDigest.digest(); // step 4
		return new Base64Encoder().encode(raw); // step 5
	}

	@Override
	public User getUserByToken(String userToken) {
		if (userToken == null || userToken.equalsIgnoreCase("")) {
			throw new BadRequestException(generateErrorMessage("GL0061","User token"));
		}
		User user = getUserRepository().findByToken(userToken);
		if (user == null) {
			throw new BadRequestException(generateErrorMessage("GL0056","User"));
		}
		if (user != null && !user.getGooruUId().toLowerCase().contains(ANONYMOUS)) {
			user.setMeta(userMeta(user));
			if (user.getAccountTypeId() != null && user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				if (user.getParentUser().getIdentities() != null) {
					user.setEmailId(user.getParentUser().getIdentities().iterator().next().getExternalId());
				}
			} else {
				if (user.getIdentities() != null & user.getIdentities().size() > 0) {
					user.setEmailId(user.getIdentities().iterator().next().getExternalId());
				}
			}
		}
		try {
			this.getUsereventlog().getEventLogs(false, true, user, null, false, false);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e.getMessage());
		}
		return user;
	}

	@Override
	public User createUser(User user, String password, String school, Integer confirmStatus, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId, String gender, String childDOB, String source, HttpServletRequest request, String role,
			String mailConfirmationUrl) throws Exception {
		return createUser(user, password, school, confirmStatus, addedBySystem, userImportCode, accountType, dateOfBirth, userParentId, null, gender, childDOB, source, null, request, role, mailConfirmationUrl);
	}

	@Override
	public User resetPasswordRequest(String emailId, String gooruBaseUrl, User apicaller, String mailConfirmationUrl) throws Exception {
		Identity identity = new Identity();
		if (apicaller != null && !apicaller.getGooruUId().toLowerCase().contains(Constants.ANONYMOUS)) {
			identity = this.findUserByGooruId(apicaller.getGooruUId());
		} else {
			identity = this.getUserRepository().findByEmailIdOrUserName(emailId, true, false);
		}
		if (identity == null) {
			throw new NotFoundException(generateErrorMessage("GL0070"));
		}
		String token = UUID.randomUUID().toString();
		User user = this.userRepository.findByIdentity(identity);
		if (user == null) {
			throw new NotFoundException(generateErrorMessage("GL0071"));
		}
		if (user.getConfirmStatus() == 0) {
			throw new BadRequestException(generateErrorMessage("GL0072"));
		}
		Credential creds = identity.getCredential();
		if (creds == null && identity.getAccountCreatedType() != null && identity.getAccountCreatedType().equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			throw new BadRequestException(generateErrorMessage("GL0073"));
		}
		if (creds == null) {
			creds = new Credential();
			String password = UUID.randomUUID().toString();
			creds.setPassword(password);
			creds.setIdentity(identity);
		}
		creds.setToken(token);
		creds.setResetPasswordRequestDate(new Date(System.currentTimeMillis()));
		this.getUserRepository().save(creds);
		identity.setCredential(creds);
		user.setEmailId(emailId);
		this.getMailHandler().sendMailToResetPassword(user.getGooruUId(), null, false, gooruBaseUrl, mailConfirmationUrl);
		return user;
	}

	@Override
	public Identity resetCredential(String token, String gooruUid, String password, User apiCaller, String mailConfirmationUrl, Boolean isPartnerPortal) throws Exception {
		Identity identity = null;
		if (token != null) {
			if (this.getUserService().hasResetTokenValid(token)) {
				throw new BadRequestException(TOKEN_EXPIRED);
			}
			identity = this.getUserService().findIdentityByResetToken(token);
			if (identity.getUser().getUsername().equalsIgnoreCase(password)) {
				throw new BadRequestException(generateErrorMessage("GL0074"));
			}
		} else {
			if (this.isContentAdmin(apiCaller)) {
				identity = this.findUserByGooruId(gooruUid);
			} else {
				throw new BadRequestException(generateErrorMessage("GL0075"));
			}
		}
		boolean flag = false;
		String tokenIsExist = identity.getCredential().getToken();
		if (tokenIsExist != null && tokenIsExist.contains(RESET_TOKEN_SUFFIX)) {
			flag = true;
		}
		String newGenereatedToken = UUID.randomUUID().toString() + RESET_TOKEN_SUFFIX;
		String resetPasswordConfirmRestendpoint = this.getSettingService().getConfigSetting(ConfigConstants.RESET_PASSWORD_CONFIRM_RESTENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		this.getUserService().validatePassword(password, identity.getUser().getUsername());
		String encryptedPassword = this.encryptPassword(password);
		identity.setLastLogin(new Date(System.currentTimeMillis()));
		Credential credential = identity.getCredential();
		credential.setPassword(encryptedPassword);
		credential.setToken(newGenereatedToken);
		identity.setCredential(credential);
		this.getUserRepository().save(identity);
		if (!flag && !isPartnerPortal) {
			if (identity.getUser().getAccountTypeId() != null && identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_PARENT) || identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_NON_PARENT)) {
				this.getMailHandler().sendMailToConfirmPasswordChanged(identity.getUser().getGooruUId(), password, true, resetPasswordConfirmRestendpoint, mailConfirmationUrl);
			}
			if (identity.getUser().getAccountTypeId() != null && identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				this.getMailHandler().sendMailToResetPassword(identity.getUser().getGooruUId(), password, true, resetPasswordConfirmRestendpoint, mailConfirmationUrl);
			}
		}
		return identity;
	}

	@Override
	public Set<String> checkContentAccess(User authenticationUser, String goorContentId) {
		Set<String> permissions = new HashSet<String>();
		if (authenticationUser != null) {
			Content content = this.getContentRepository().findContentByGooruId(goorContentId, true);
			if (content != null) {
				Set<ContentPermission> contentPermissions = content.getContentPermissions();
				for (ContentPermission userPermission : contentPermissions) {
					if (userPermission.getParty().getPartyUid().equals(authenticationUser.getPartyUid())) {
						permissions.add(EDIT);
						break;
					}
				}
				if (authenticationUser.getGooruUId().equals(content.getUser().getGooruUId())) {
					permissions.add(EDIT);
					permissions.add(DELETE);
				}
				if (Sharing.PUBLIC.getSharing().equalsIgnoreCase(content.getSharing())) {
					permissions.add(VIEW);
				}
				if (isContentAdmin(authenticationUser)) {
					permissions.add(EDIT);
					permissions.add(DELETE);
				}
			} else {
				throw new NotFoundException();
			}
		}
		return permissions;
	}

	@Override
	public String getUserEmailFromIdentity(Set<Identity> identity) {
		Iterator<Identity> iter = identity.iterator();
		return iter.next().getExternalId();
	}

	@Override
	public void deleteUserMeta(String gooruUid, Profile newProfile, User apiCaller) {
		User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user != null && newProfile != null) {
			Profile profile = this.getUserService().getProfile(user);
			if (profile != null) {
				if (newProfile.getCourses() != null) {
					deleteCourse(newProfile.getCourses(), user, apiCaller);
				}
				if (newProfile.getGrade() != null && profile.getGrade() != null) {
					profile.setGrade(deleteGrade(newProfile.getGrade(), user, apiCaller));
					this.getUserRepository().save(profile);
				}
				indexProcessor.index(profile.getUser().getPartyUid(), IndexProcessor.INDEX, USER);
			}
		}

	}

	private String addGrade(String newGrade, User user, User apiCaller, String activeFlag) {
		List<String> newGradeList = Arrays.asList(newGrade.split(","));
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
		for (String grade : newGradeList) {
			UserClassification existingGrade = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), null, null, grade);
			if (existingGrade == null) {
				UserClassification userClassification = new UserClassification();
				userClassification.setGrade(grade);
				userClassification.setType(type);
				userClassification.setUser(user);
				userClassification.setActiveFlag(activeFlag != null ? Integer.parseInt(activeFlag) : 1);
				userClassification.setCreator(apiCaller);
				this.getUserRepository().save(userClassification);
				this.getUserRepository().flush();
			} else {
				if (activeFlag != null) {
					existingGrade.setActiveFlag(Integer.parseInt(activeFlag));
					this.getUserRepository().save(existingGrade);
				}
			}

		}
		return this.getUserRepository().getUserGrade(user.getGooruUId(), type.getCustomTableValueId(), null);
	}

	private String deleteGrade(String deleteGrade, User user, User apiCaller) {
		List<String> deleteGradeList = Arrays.asList(deleteGrade.split(","));
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
		for (String grade : deleteGradeList) {
			UserClassification existingCourse = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), null, apiCaller != null ? apiCaller.getGooruUId() : null, grade);
			if (existingCourse != null) {
				this.getUserRepository().remove(existingCourse);
			}
		}
		return this.getUserRepository().getUserGrade(user.getGooruUId(), type.getCustomTableValueId(), null);
	}

	@Override
	public void deleteUserContent(String gooruUid, String isDeleted, User apiCaller) {
		User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user != null && isContentAdmin(apiCaller) && isDeleted != null && isDeleted.equalsIgnoreCase(TRUE)) {
			user.setIsDeleted(true);
			List<Content> contents = this.getContentRepository().getContentByUserUId(gooruUid);
			List<ContentPermission> removeContentPermission = new ArrayList<ContentPermission>();
			List<Content> removeContentList = new ArrayList<Content>();
			String gooruOidAsString = "";
			for (Content content : contents) {
				if (content != null) {
					if (gooruOidAsString.length() > 0) {
						gooruOidAsString += ",";
					}
					gooruOidAsString += content.getGooruOid();
					if (content.getSharing().equalsIgnoreCase(PUBLIC)) {
						content.setCreator(apiCaller);
						this.getContentRepository().save(content);
					} else if (content.getSharing().equalsIgnoreCase(PRIVATE)) {
						Set<ContentPermission> contentPermissions = content.getContentPermissions();
						for (ContentPermission contentPermission : contentPermissions) {
							removeContentPermission.add(contentPermission);
						}
						removeContentList.add(content);
					}
				}
			}
			this.getContentRepository().removeAll(removeContentPermission);
			this.getContentRepository().removeAll(removeContentList);
			this.getUserRepository().save(user);
			if (gooruOidAsString.length() > 0) {
				indexProcessor.index(gooruOidAsString, IndexProcessor.INDEX, RESOURCE);
			}

		}
	}

	@Override
	public void deleteUserImageProfile(String userId) throws Exception {
		User user = findByGooruId(userId);
		if (user != null) {
			profileImageUtil.deleteS3Upload(this.getUserRepository().getProfile(user, false));
		} else {
			throw new FileNotFoundException(generateErrorMessage("GL0075","User"));
		}
	}

	@Override
	public void updateOrgAdminCustomField(String organizationUid, User user) throws Exception {
		Organization organization = this.getOrganizationService().getOrganizationById(organizationUid);
		if (organization != null) {
			PartyCustomField partyCustomField = new PartyCustomField();
			partyCustomField.setCategory(PartyCategoryType.USER_META.getpartyCategoryType());
			partyCustomField.setOptionalKey(ConstantProperties.ORG_ADMIN_KEY);
			partyCustomField.setOptionalValue(organizationUid);
			partyCustomField.setPartyUid(user.getPartyUid());
			partyService.createPartyCustomField(MY, partyCustomField, user);
		} else {
			throw new BadRequestException(generateErrorMessage("GL0076"));
		}
	}

	@Override
	public Map<String, Object> followUser(User user, String followOnUserId) {
		UserRelationship userRelationship = getUserRepository().getActiveUserRelationship(user.getPartyUid(), followOnUserId);
		User followOnUser = getUserRepository().findByGooruId(followOnUserId);
		if (userRelationship != null) {
			return this.setUserObj(followOnUser);
		}
		userRelationship = new UserRelationship();
		userRelationship.setUser(user);
		userRelationship.setFollowOnUser(followOnUser);
		userRelationship.setActivatedDate(new Date(System.currentTimeMillis()));
		userRelationship.setActiveFlag(true);
		getUserRepository().save(userRelationship);
		UserSummary userSummary = this.getUserRepository().getSummaryByUid(user.getPartyUid());
		UserSummary followOnUserSummary = this.getUserRepository().getSummaryByUid(followOnUserId);
		if (userSummary.getGooruUid() == null) {
			userSummary.setGooruUid(user.getPartyUid());
		}
		if (followOnUserSummary.getGooruUid() == null) {
			followOnUserSummary.setGooruUid(followOnUserId);
		}
		userSummary.setFollowing((userSummary.getFollowing() != null ? userSummary.getFollowing() : 0) + 1);
		followOnUserSummary.setFollowers((followOnUserSummary.getFollowers() != null ? followOnUserSummary.getFollowers() : 0) + 1);
		this.getUserRepository().save(userSummary);
		this.getUserRepository().save(followOnUserSummary);
		this.getUserRepository().flush();
		try {
			this.getUsereventlog().getEventLogs(false, false, null, null, true, false);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e.getMessage());
		}
		return this.setUserObj(followOnUser);
	}

	@Override
	public void unFollowUser(User user, String unFollowUserId) {
		UserRelationship userRelationship = getUserRepository().getActiveUserRelationship(user.getPartyUid(), unFollowUserId);
		if (userRelationship == null) {
			throw new BadRequestException(generateErrorMessage("GL0077"));
		} else {
			this.getUserRepository().remove(userRelationship);
			UserSummary userSummary = this.getUserRepository().getSummaryByUid(user.getPartyUid());
			UserSummary followOnUserSummary = this.getUserRepository().getSummaryByUid(unFollowUserId);
			userSummary.setFollowing(userSummary.getFollowing() - 1);
			followOnUserSummary.setFollowers(followOnUserSummary.getFollowers() - 1);
			this.getUserRepository().save(userSummary);
			this.getUserRepository().save(followOnUserSummary);
			this.getUserRepository().flush();
		}
		try {
			this.getUsereventlog().getEventLogs(false, false, null, null, false, true);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e.getMessage());
		}
	}
	
	private Map<String, Object> setUserObj(User user) {
		Map<String, Object> userObj = new HashMap<String, Object>();
		userObj.put(USER_NAME, user.getUsername());
		userObj.put(_GOORU_UID, user.getGooruUId());
		userObj.put(FIRST_NAME, user.getFirstName());
		userObj.put(LAST_NAME, user.getLastName());
		userObj.put(PROFILE_IMG_URL, buildUserProfileImageUrl(user));
		userObj.put(EMAIL_ID, user.getIdentities() != null ? user.getIdentities().iterator().next().getExternalId() : null);
		userObj.put(SUMMARY, getUserSummary(user.getPartyUid()));
		userObj.put(CUSTOM_FIELDS, user.getCustomFields());
		return userObj;
	}

	@Override
	public Map<String, Object> userMeta(User user) {
		Map<String, Object> meta = new HashMap<String, Object>();
		PartyCustomField partyCustomField = partyService.getPartyCustomeField(user.getPartyUid(), USER_TAXONOMY_ROOT_CODE, null);
		Map<String, Object> taxonomy = new HashMap<String, Object>();
		String taxonomyCode = null;
		if (partyCustomField != null && partyCustomField.getOptionalValue() != null && partyCustomField.getOptionalValue().length() > 0) {
			taxonomyCode = this.getTaxonomyRespository().getFindTaxonomyCodeList(partyCustomField.getOptionalValue());
		}
		if (taxonomyCode != null) {
			List<String> taxonomyCodeList = Arrays.asList(taxonomyCode.split(","));
			taxonomy.put(CODE, taxonomyCodeList);
		}
		if (partyCustomField != null && partyCustomField.getOptionalValue() != null && partyCustomField.getOptionalValue().length() > 0) {
			List<String> taxonomyCodeIdList = Arrays.asList(partyCustomField.getOptionalValue().split(","));
			taxonomy.put(CODE_ID, taxonomyCodeIdList);
		}
		PartyCustomField partyCustomFieldFeatured = partyService.getPartyCustomeField(user.getPartyUid(), IS_FEATURED_USER, null);
		if (partyCustomFieldFeatured != null && partyCustomFieldFeatured.getOptionalValue() != null && partyCustomFieldFeatured.getOptionalValue().length() > 0) {
			meta.put(FEATURED_USER, Boolean.parseBoolean(partyCustomFieldFeatured.getOptionalValue()));
		}
		meta.put(USER_TAX_PREFERENCE, taxonomy);
		meta.put(SUMMARY, getUserSummary(user.getPartyUid()));
		return meta;
	}

	@Override
	public User updateUserViewFlagStatus(String gooruUid, Integer viewFlag) {
		return this.getUserService().updateViewFlagStatus(gooruUid, viewFlag);
	}

	@Override
	public Map<String, Object> getUserSummary(String gooruUid) {
		UserSummary userSummary = this.getUserRepository().getSummaryByUid(gooruUid);
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put(COLLECTION, userSummary.getCollections() != null ? userSummary.getCollections() : 0);
		summary.put(TAGS, userSummary.getTag() != null ? userSummary.getTag() : 0);
		summary.put(FOLLOWING, userSummary.getFollowing() != null ? userSummary.getFollowing() : 0);
		summary.put(FOLLOWERS, userSummary.getFollowers() != null ? userSummary.getFollowers() : 0);
		return summary;
	}

	@Override
	public void resetEmailAddress(List<String> data) throws Exception {
		for (final String mailId : data) {
			Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
			String domainName = this.getSettingService().getConfigSetting(ConfigConstants.GOORU_USER_MAIL_RESET, 0, TaxonomyUtil.GOORU_ORG_UID);
			String[] mailAddress = mailId.split("@");
			if(domainName != null && identity != null) {
				String[] domains = domainName.split(",");
				for(String domain : domains) {
					if(mailAddress[1].equalsIgnoreCase(domain)) {
						   identity.setExternalId(mailAddress[1] + System.currentTimeMillis());
						   this.getUserRepository().save(identity);
						   this.getUserRepository().flush();
					   }
				}
			}else { 
				throw new BadRequestException("Requested domain not found");
			}
		}
	}
	
	@Override
	public Boolean isFollowedUser(String gooruUserId, User apiCaller) {
		return getUserRepository().getActiveUserRelationship(apiCaller.getPartyUid(), gooruUserId) != null ? true : false;
	}
	
	@Override
	public List<UserRole> findAllRoles() {
		return getUserRepository().findAllRoles();
	}

	@Override
	public Long allRolesCount() {
		
		return this.getUserRepository().countAllRoles();
	}
	
	@Override
	public List<UserRole> findUserRoles(String userUid) {
		
		return this.getUserRepository().findUserRoles(userUid);
	}
	
	@Override
	public Long userRolesCount(String userUid) {
		
		return this.getUserRepository().countUserRoles(userUid);
	}
	
	@Override
	public ActionResponseDTO<UserRole> createNewRole(UserRole role, User user) throws Exception{
		UserRole userRole = userRepository.findUserRoleByName(role.getName(),null);
		final Errors errors = validateCreateRole(role);		
		Organization gooruOrg = organizationService.getOrganizationById(TaxonomyUtil.GOORU_ORG_UID);
		Set<RoleEntityOperation> entityOperations = role.getRoleOperations();
	    Iterator<RoleEntityOperation> iter = entityOperations.iterator();
	    if (userRole != null && user.getOrganization().equals(gooruOrg)) {
	    	throw new BadRequestException(generateErrorMessage(GL0041,"Role "));
		} 
		else {		
			if (!errors.hasErrors()) {
				userRole = new UserRole();
			    userRole.setName(role.getName());
				userRole.setDescription(role.getDescription());
				getUserRepository().save(userRole);
				getUserRepository().flush();
			}
		}
		while (iter.hasNext()) {
	        RoleEntityOperation roleEntityOperation = (RoleEntityOperation) iter.next();
	        EntityOperation entityOperation = this.getEntityOperationByEntityOperationId(roleEntityOperation.getEntityOperation().getEntityOperationId());
	        roleEntityOperation.setUserRole(userRole);
	        roleEntityOperation.setEntityOperation(entityOperation);
	        getUserRepository().save(roleEntityOperation);
	    }
		 indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
		
		return new ActionResponseDTO<UserRole>(userRole, errors);
	}
	
	private Errors validateCreateRole(UserRole userRole) {
		final Errors errors = new BindException(userRole, "role");
		rejectIfNull(errors, userRole, NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}
	
	@Override
	public UserRole updateRole(UserRole role,Integer roleId) throws Exception {
		UserRole userRole = null;
		if (roleId != null) {
			userRole = userRepository.findUserRoleByRoleId(roleId);
		}
		rejectIfNull(userRole, GL0056, 404, "Role ");

		if (userRole != null) {
			if(role.getName()!=null){	
			userRole.setName(role.getName());
			}
			if(role.getDescription()!=null){
			userRole.setDescription(role.getDescription());
			}
			userRepository.save(userRole);
		}
		return userRole;
	}

	@Override
	public void removeRole(Integer roleId) throws Exception{

		UserRole userRole = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(userRole, GL0056, 404, "Role ");
		userRepository.remove(userRole);
	}
	
	@Override
	public EntityOperation getEntityOperationByEntityOperationId(Integer entityOperationId){
		return userRepository.getEntityOperationByEntityOperationId(entityOperationId);
	}
	
	@Override
	public List<EntityOperation> findAllEntityNames() {
		return getUserRepository().findAllEntityNames();
	}
	
	@Override
	public Long allEntityNamesCount() {
		
		return this.getUserRepository().countAllEntityNames();
	}
	
	@Override
	public List<EntityOperation> getOperationsByEntityName(String entityName) {
		return getUserRepository().findOperationsByEntityName(entityName);
	}	
	
	@Override
	public Long getOperationCountByEntityName(String entityName) {
		
		return this.getUserRepository().countOperationsByEntityName(entityName);
	}
	
	@Override
	public UserRoleAssoc assignRoleByUserUid(Integer roleId, String userUid)
			throws Exception {
		User user = userRepository.findUserByPartyUid(userUid);
		UserRole role = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(role, GL0010, 404, "Role ");
		UserRoleAssoc userRoleAssoc = userRepository.findUserRoleAssocEntryByRoleIdAndUserUid(roleId, userUid);
		if (userRoleAssoc != null) {
			throw new BadRequestException(generateErrorMessage(GL0041, "User role "));
		}
		userRoleAssoc = new UserRoleAssoc();
		userRoleAssoc.setUser(user);
		userRoleAssoc.setRole(role);
		getUserRepository().save(userRoleAssoc);
		getUserRepository().flush();
		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);
		return userRoleAssoc;
	}
	
	@Override
	public void removeAssignedRoleByUserUid(Integer roleId, String userUid)
			throws Exception {
		UserRoleAssoc userRoleAssoc = userRepository.findUserRoleAssocEntryByRoleIdAndUserUid(roleId, userUid);
		rejectIfNull(userRoleAssoc, GL0102,404, "Role ");
		getUserRepository().remove(userRoleAssoc);
	}
	
	public IdpRepository getIdpRepository() {
		return idpRepository;
	}

	public PartyService getPartyService() {
		return partyService;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public TaxonomyRespository getTaxonomyRespository() {
		return taxonomyRespository;
	}

	public ProfileImageUtil getProfileImageUtil() {
		return profileImageUtil;
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}

	public CollaboratorService getCollaboratorService() {
		return collaboratorService;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public UserTokenRepository getUserTokenRepository() {
		return userTokenRepository;
	}
	
	public UserEventlog getUsereventlog() {
		return usereventlog;
	}

	public ApplicationRepository getApplicationRepository() {
		return applicationRepository;
	}
}
	