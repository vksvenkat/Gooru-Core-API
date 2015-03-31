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
import org.ednovo.gooru.core.api.model.Province;
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
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.CollaboratorService;
import org.ednovo.gooru.domain.service.CountryService;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.eventlogs.UserEventlog;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.impl.UserServiceImpl;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
	
	@Autowired
	private CountryService countryService;

	@Autowired
	private IndexHandler indexHandler;

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
	public SearchResults<Map<String, Object>> getFollowedOnUsers(final String gooruUId, Integer offset, Integer limit) {
		final List<User> users = this.getUserRepository().getFollowedOnUsers(gooruUId, offset, limit);
		final List<Map<String, Object>> usersObj = new ArrayList<Map<String, Object>>();
		for (User user : users) {
			usersObj.add(setUserObj(user));
		}
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(usersObj);
		result.setTotalHitCount(this.getUserRepository().getFollowedOnUsersCount(gooruUId));
		return result;
	}

	@Override
	public SearchResults<Map<String, Object>> getFollowedByUsers(final String gooruUId, Integer offset, Integer limit) {
		final List<User> users = this.getUserRepository().getFollowedByUsers(gooruUId, offset, limit);
		final List<Map<String, Object>> usersObj = new ArrayList<Map<String, Object>>();
		for (User user : users) {
			usersObj.add(setUserObj(user));
		}
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(usersObj);
		result.setTotalHitCount(this.getUserRepository().getFollowedByUsersCount(gooruUId));
		return result;
	}

	@Override
	public User findByGooruId(final String gooruId) {
		return userRepository.findByGooruId(gooruId);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	@Override
	public Profile getUserProfile(final String gooruUid, final Integer activeFlag) {
		final User user = this.findByGooruId(gooruUid);
		if (user == null || user.getGooruUId().toLowerCase().contains(ANONYMOUS)) {
			throw new BadRequestException(generateErrorMessage(GL0056, USER));
		}
		final Profile profile = this.getUserRepository().getProfile(user, false);
		String externalId = null;
		final String profileImageUrl = this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, 0, TaxonomyUtil.GOORU_ORG_UID) + '/' + this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID).toString() + user.getGooruUId() + DOT_PNG;
		if (user.getAccountTypeId() != null && (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD))) {
			externalId = this.findUserByGooruId(user.getParentUser().getGooruUId()).getExternalId();
		} else {
			externalId = this.findUserByGooruId(user.getGooruUId()).getExternalId();
		}
		profile.getUser().setProfileImageUrl(profileImageUrl);
		profile.setExternalId(externalId);
		profile.getUser().setEmailId(externalId);
		profile.getUser().setMeta(userMeta(user));
		final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
		final CustomTableValue gradeType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
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
	public Profile updateProfileInfo(final Profile newProfile, final String gooruUid, final User apiCaller, final String activeFlag, final Boolean emailConfirmStatus, final String showProfilePage, final String accountType, final String password) {
		final User user = this.getUserRepository().findByGooruId(gooruUid);
		Boolean reindexUserContent = false;
		final JSONObject itemData = new JSONObject();		
		if (user == null) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}
		final Profile profile = this.getUserService().getProfile(user);
		if (showProfilePage != null) {
			final PartyCustomField partyCustomField = new PartyCustomField(USER_META, SHOW_PROFILE_PAGE, showProfilePage);
			this.getPartyService().updatePartyCustomField(user.getPartyUid(), partyCustomField, user);
			reindexUserContent = true;
		}
		if (profile != null) {
			final Identity identity = this.getUserRepository().findUserByGooruId(gooruUid);
			if (password != null && gooruUid.equalsIgnoreCase(apiCaller.getGooruUId()) && identity.getCredential() != null) {
				this.getUserService().validatePassword(password, identity.getUser().getUsername());
				final String encryptedPassword = this.encryptPassword(password);
				identity.getCredential().setPasswordEncryptType(CustomProperties.PasswordEncryptType.SHA.getPasswordEncryptType());
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
					if (newProfile.getUser() != null) {
						if (newProfile.getUser().getActive() != null) {
							identity.setActive(newProfile.getUser().getActive());
							user.setActive(newProfile.getUser().getActive());
							if (newProfile.getUser().getActive() == 0) {
								this.getMailHandler().sendUserDisabledMail(gooruUid);
							}
							this.getUserRepository().save(identity);
						}
						if (identity != null && newProfile.getUser().getEmailId() != null && !newProfile.getUser().getEmailId().isEmpty()) {
							final boolean emailAvailability = this.getUserRepository().checkUserAvailability(newProfile.getUser().getEmailId(), CheckUser.BYEMAILID, false);
							if (emailAvailability) {
								throw new BadRequestException(generateErrorMessage("GL0084", newProfile.getUser().getEmailId(), "Email id"));
							}
							if (emailConfirmStatus || (isContentAdmin(apiCaller) && !apiCaller.getPartyUid().equals(gooruUid))) {
								identity.setExternalId(newProfile.getUser().getEmailId());
								this.getUserRepository().save(identity);
								user.setEmailId(newProfile.getUser().getEmailId());
								if (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
									final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
									final String date = dateFormat.format(profile.getDateOfBirth());
									final Integer age = this.getUserService().calculateCurrentAge(date);
									if (age >= 13) {
										user.setAccountTypeId(UserAccountType.ACCOUNT_NON_PARENT);
										final Map<String, String> childData = new HashMap<String, String>();
										final Map<String, String> parentData = new HashMap<String, String>();
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
								final Map<String, String> dataMap = new HashMap<String, String>();
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
							reindexUserContent = true;
						}
						if (newProfile.getUser().getConfirmStatus() != null) {
							itemData.put(CONFIRM_STATUS, newProfile.getUser().getConfirmStatus());
							user.setConfirmStatus(newProfile.getUser().getConfirmStatus());
						}
						if (newProfile.getUser().getLastName() != null) {
							itemData.put(LAST_NAME, newProfile.getUser().getLastName());
							user.setLastName(newProfile.getUser().getLastName());
							reindexUserContent = true;
						}
						if (newProfile.getUser().getUsername() != null && !this.getUserRepository().checkUserAvailability(newProfile.getUser().getUsername(), CheckUser.BYUSERNAME, false)) {
							itemData.put(USERNAME, newProfile.getUser().getUsername());
							user.setUsername(newProfile.getUser().getUsername());
							reindexUserContent = true;
						}
						if (newProfile.getUser() != null && newProfile.getUser().getSchool() != null && newProfile.getUser().getSchool().getPartyUid() != null) {
							Organization school = this.getOrganizationService().getOrganizationById(newProfile.getUser().getSchool().getPartyUid());
							rejectIfNull(school, GL0056, 404, SCHOOL);
							user.setSchool(school);
						}

						if (newProfile.getUser() != null && newProfile.getUser().getSchoolDistrict() != null && newProfile.getUser().getSchoolDistrict().getPartyUid() != null) {
							Organization district = this.getOrganizationService().getOrganizationById(newProfile.getUser().getSchoolDistrict().getPartyUid());
						    rejectIfNull(district, GL0056, 404, DISTRICT);
						    user.setSchoolDistrict(district);
						}

						if (newProfile.getUser() != null && newProfile.getUser().getStateProvince() != null && newProfile.getUser().getStateProvince().getStateUid() != null) {
							Province state = this.getCountryService().getState(newProfile.getUser().getStateProvince().getStateUid());
							rejectIfNull(state, GL0056, 404, PROVINCE);
						    user.setStateProvince(state);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.debug("Error" + e);
			}
			profile.setUser(user);
			this.getUserRepository().save(profile);
			final PartyCustomField partyCustomField = this.getPartyService().getPartyCustomeField(profile.getUser().getPartyUid(), USER_CONFIRM_STATUS, profile.getUser());
			if (partyCustomField == null && newProfile.getUser() != null && newProfile.getUser().getConfirmStatus() != null && newProfile.getUser().getConfirmStatus() == 1) {
				final Map<String, String> dataMap = new HashMap<String, String>();
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
				this.getUserRepository().save(new PartyCustomField(profile.getUser().getPartyUid(), USER_META, USER_CONFIRM_STATUS, TRUE));
				this.getMailHandler().handleMailEvent(dataMap);
			}
			final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			profile.setCourses(this.getUserRepository().getUserClassifications(gooruUid, type.getCustomTableValueId(), null));
		}
		try {
			this.getUsereventlog().getEventLogs(true, false, user, itemData, false, false);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e);
		}

		if (profile != null) {
			indexHandler.setReIndexRequest(profile.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, reindexUserContent, false);
		}

		return profile;
	}

	private void addCourse(final List<UserClassification> courses, final User user, final User apiCaller, final String activeFlag) {
		if (courses != null) {
			final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			for (UserClassification course : courses) {
				if (course.getCode() != null && course.getCode().getCodeId() != null) {
					final UserClassification existingCourse = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), course.getCode().getCodeId(), null, null);
					if (existingCourse == null) {
						final Code code = this.getTaxonomyRespository().findCodeByCodeId(course.getCode().getCodeId());
						if (code != null && code.getDepth() == 2) {
							UserClassification userClassification = new UserClassification();
							userClassification.setCode(code);
							userClassification.setType(type);
							userClassification.setActiveFlag(activeFlag == null ? 1 : Integer.parseInt(activeFlag));
							userClassification.setUser(user);
							userClassification.setCreator(apiCaller);
							this.getUserRepository().save(userClassification);
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

	private void deleteCourse(final List<UserClassification> courses, final User user, final User apiCaller) {
		if (courses != null && courses.size() > 0) {
			final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
			for (final UserClassification course : courses) {
				if (course.getCode() != null && course.getCode().getCodeId() != null && type != null) {
					final UserClassification existingCourse = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), course.getCode().getCodeId(), apiCaller == null ? null : apiCaller.getGooruUId(), null);
					if (existingCourse != null) {
						this.getUserRepository().remove(existingCourse);
					}
				}
			}
		}
	}

	@Override
	public User createUserWithValidation( User newUser, String password, final String school, Integer confirmStatus, final Boolean useGeneratedPassword, final Boolean sendConfirmationMail, final User apiCaller, final String accountType, final String dateOfBirth, final String userParentId, final String sessionId, final String gender, final String childDOB,
			final String gooruBaseUrl, final Boolean token, final HttpServletRequest request, final String role, final String mailConfirmationUrl) throws Exception {
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
		final Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
		final UserToken userToken = this.createSessionToken(user, sessionId, application);
		if (user != null && token) {
			final Identity identity = this.findUserByGooruId(user.getGooruUId());
			if (identity != null) {
				user.setToken(identity.getCredential().getToken());
			}
		}
		if (user.getAccountTypeId() != UserAccountType.ACCOUNT_CHILD) {
			user.setEmailId(newUser.getEmailId());
		}
		if (user != null && sendConfirmationMail && (inviteuser == null || inviteuser.size() == 0)) {
			try {
				if (isAdminCreateUser) {
					this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), null, gooruBaseUrl, mailConfirmationUrl, null, null);
				} else {
					if (user.getAccountTypeId() == null || !user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
						this.getMailHandler().sendMailToConfirm(user.getGooruUId(), null, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, mailConfirmationUrl, null, null);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Sending confirmation mail failed" + e);
			}

		}
		return user;
	}

	public String getUserCourse(final String userId) {
		final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.COURSE.getUserClassificationType());
		final List<UserClassification> userCourses = this.getUserRepository().getUserClassifications(userId, type.getCustomTableValueId(), null);
		StringBuffer courses = new StringBuffer();
		if (userCourses != null) {
			for (final UserClassification userCourse : userCourses) {
				courses = courses.append(courses.length() == 0 ? userCourse.getCode().getLabel() : "," + userCourse.getCode().getLabel());
			}
		}
		return courses.length() == 0 ? null : courses.toString();
	}

	@Override
	public User resendConfirmationMail(final String gooruUid, final User apiCaller, final String sessionId, final String gooruBaseUrl, final String type) throws Exception {
		final User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, "User"), GL0056);
		}

		final Application application = this.getApplicationRepository().getApplicationByOrganization(user.getOrganization().getPartyUid());
		final UserToken userToken = this.createSessionToken(user, sessionId, application);
		final String password = user.getIdentities().iterator().next().getCredential() == null ? null : user.getIdentities().iterator().next().getCredential().getPassword();
		Identity identity = null;
		if (user.getAccountTypeId() != null) {
			identity = this.getUserService().findUserByGooruId(user.getGooruUId());
			if (user.getAccountTypeId() == UserAccountType.ACCOUNT_CHILD) {
				final String encryptedPassword = this.encryptPassword(password);
				final Credential credential = user.getIdentities().iterator().next().getCredential();
				credential.setPassword(encryptedPassword);
				credential.setPasswordEncryptType(CustomProperties.PasswordEncryptType.SHA.getPasswordEncryptType());
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
				final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				dateOfBirth = dateFormat.format(profile.getDateOfBirth());
			}
			if (user.getAccountTypeId() != null && type.equalsIgnoreCase(WELCOME)) {
				if (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
					final CustomTableValue gradeType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
					final String userGrade = this.getUserRepository().getUserGrade(user.getGooruUId(), gradeType.getCustomTableValueId(), null);
					this.getMailHandler().sendMailToConfirm(user.getGooruUId(), password, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, null, userGrade, getUserCourse(user.getGooruUId()));
				}
			} else {
				this.getMailHandler().sendMailToConfirm(user.getGooruUId(), null, accountType, userToken.getToken(), dateOfBirth, gooruBaseUrl, null, null, null);
			}
		}
		return user;
	}

	private void validateAddUser(final User user, final User apicaller, final String childDOB, final String accountType, final String dateOfBirth, final String password) {
		if ((isNotEmptyString(childDOB)) && (isNotEmptyString(accountType)) && childDOB != null && !childDOB.equalsIgnoreCase(_NULL)) {
			final Integer age = this.calculateCurrentAge(childDOB);
			if (age < 0) {
				throw new BadRequestException(generateErrorMessage("GL0059"), "GL0059");
			}
		}
		if ((isNotEmptyString(dateOfBirth)) && (isNotEmptyString(accountType)) && dateOfBirth != null && !dateOfBirth.equalsIgnoreCase(_NULL)) {
			final Integer age = this.calculateCurrentAge(dateOfBirth);
			if (age < 0) {
				throw new BadRequestException(generateErrorMessage("GL0059"), "GL0059");
			}
			if (age < 13 && age >= 0 && (accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType()))) {
				throw new UnauthorizedException(generateErrorMessage("GL0060", "13"), "GL0060");
			}
		}
		if (!isNotEmptyString(user.getFirstName())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "First name"), "GL0061");
		}

		if (!isNotEmptyString(user.getOrganization() != null ? user.getOrganization().getOrganizationCode() : null)) {
			throw new UnauthorizedException(generateErrorMessage("GL0061", "Organization code"), "GL0061");
		}
		if (!isNotEmptyString(user.getLastName())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Last name"), "GL0061");
		}
		if (!isNotEmptyString(user.getEmailId())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Email"), "GL0061");
		}
		if (!isNotEmptyString(password)) {
			if (apicaller != null && !isContentAdmin(apicaller)) {
				throw new BadRequestException(generateErrorMessage("GL0061", "Password"), "GL0061");
			}
		} else if (password.length() < 5) {
			throw new BadRequestException(generateErrorMessage("GL0064", "5"), "GL0064");
		}
		if (!isNotEmptyString(user.getUsername())) {
			throw new BadRequestException(generateErrorMessage("GL0061", "Username"), "GL0061");
		} else if (user.getUsername().length() < 4) {
			throw new BadRequestException(generateErrorMessage("GL0065", "4"), "GL0065");
		} else if (user.getUsername().length() > 21) {
			throw new BadRequestException(generateErrorMessage("GL0100", "21"), "GL0100");
		}
		boolean usernameAvailability = this.getUserRepository().checkUserAvailability(user.getUsername(), CheckUser.BYUSERNAME, false);
		if (usernameAvailability) {
			throw new NotFoundException(generateErrorMessage("GL0084", user.getUsername(), "username"), "GL0084");
		}
		boolean emailidAvailability = this.getUserRepository().checkUserAvailability(user.getEmailId(), CheckUser.BYEMAILID, false);
		if (accountType != null) {
			if (emailidAvailability && (!accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()))) {
				throw new NotFoundException(generateErrorMessage("GL0062"), "GL0062");
			}
		} else {
			if (emailidAvailability) {
				throw new NotFoundException(generateErrorMessage("GL0062"), "GL0062");
			}
		}

	}

	@Override
	public void validateUserOrganization(final String organizationCode, final String superAdminToken) throws Exception {
		if (superAdminToken == null || !superAdminToken.equals(this.getSettingService().getOrganizationSetting(SUPER_ADMIN_TOKEN, TaxonomyUtil.GOORU_ORG_UID))) {
			final Organization organization = this.getOrganizationService().getOrganizationByCode(organizationCode);
			if (organization == null) {
				throw new BadRequestException(generateErrorMessage("GL0066"), "GL0066");
			}
			Boolean hasPermission = false;
			final GooruAuthenticationToken authenticationContext = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
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

	private Integer calculateCurrentAge(final String dateOfBirth) {
		int years = -1;
		Date currentDate = new Date();
		Date userDateOfBirth = null;
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		try {
			userDateOfBirth = simpleDateFormat.parse(dateOfBirth);
		} catch (ParseException e) {
			LOGGER.error("Error" + e.getMessage());
			throw new BadRequestException("Invalid date format. Expected format is MM/DD/YYY");
		}
		if (userDateOfBirth.getTime() < currentDate.getTime()) {
			long milliseconds = currentDate.getTime() - userDateOfBirth.getTime();
			years = (int) (milliseconds / (1000 * 60 * 60 * 24 * 365.25));
		}
		return years;
	}

	private Boolean isNotEmptyString(final String field) {
		return StringUtils.hasLength(field);
	}

	@Override
	public Boolean isContentAdmin(final User user) {
		Boolean isAdminUser = false;
		if (user.getUserRoleSet() != null) {
			for (final UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				if (userRoleAssoc.getRole().getName().equalsIgnoreCase(UserRoleType.CONTENT_ADMIN.getType())) {
					isAdminUser = true;
					break;
				}
			}
		}
		return isAdminUser;
	}

	@Override
	public UserToken createSessionToken(final User user, final String sessionId, Application application) {
		UserToken sessionToken = new UserToken();
		sessionToken.setScope(SESSION);
		sessionToken.setUser(user);
		sessionToken.setSessionId(sessionId);
		sessionToken.setApplication(application);
		sessionToken.setCreatedOn(new Date(System.currentTimeMillis()));
		try {
			this.getUserTokenRepository().save(sessionToken);
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
	public User createUser(final User newUser, final String password, final String school, Integer confirmStatus, final Integer addedBySystem, final String userImportCode, String accountType, final String dateOfBirth, final String userParentId, final String remoteEntityId, final String gender, final String childDOB, final String source, final String emailSSO,
			final HttpServletRequest request, final String role, final String mailConfirmationUrl) throws Exception {
		List<InviteUser> inviteuser = null;
		if (accountType == null || !accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			inviteuser = this.getInviteRepository().getInviteUserByMail(newUser.getEmailId(), COLLABORATOR);
		}
		if (confirmStatus == null) {
			confirmStatus = 0;
		}
		if ((inviteuser != null && inviteuser.size() > 0) || (newUser.getOrganization() != null && newUser.getOrganization().getOrganizationCode() != null && newUser.getOrganization().getOrganizationCode().length() > 0 && newUser.getOrganization().getOrganizationCode().equalsIgnoreCase(GLOBAL))) {
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
		if (source != null) {
			identity.setAccountCreatedType(source);
		}
		final String domain = newUser.getEmailId().substring(newUser.getEmailId().indexOf("@") + 1, newUser.getEmailId().length());
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
			final OrganizationDomainAssoc domainOrganizationAssoc = this.getIdpRepository().findByDomain(idp);
			if (domainOrganizationAssoc != null) {
				organization = domainOrganizationAssoc.getOrganization();
			}
		}
		if (organization == null && newUser.getOrganization() != null && newUser.getOrganization().getOrganizationCode() != null) {
			organization = this.getOrganizationService().getOrganizationByCode(newUser.getOrganization().getOrganizationCode().toLowerCase());
		}

		/*
		 * Step I - Create a user object from the received credentials
		 */
		final Set<Identity> identities = new HashSet<Identity>();
		identities.add(identity);
		final User user = new User();
		user.setIdentities(identities);
		user.setViewFlag(0);
		/*
		 * Assuming Teacher Role for all users. In future, it will be a
		 * parameter to the function
		 */
		UserRole userRole = null;
		if (organization != null) {
			final String roles = getDefaultUserRoles(organization.getPartyUid());
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
			credential.setPasswordEncryptType(CustomProperties.PasswordEncryptType.SHA.getPasswordEncryptType());
			credential.setResetPasswordRequestDate(new Date(System.currentTimeMillis()));
			if (password != null) {
				if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
					credential.setPassword(password);
				} else {
					credential.setPassword(this.encryptPassword(password));
				}
			}
		}
		final Profile profile = new Profile();
		profile.setUser(user);
		profile.setSchool(school);
		if (role != null
				&& (role.equalsIgnoreCase(UserRole.UserRoleType.STUDENT.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.PARENT.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.TEACHER.getType()) || role.equalsIgnoreCase(UserRole.UserRoleType.AUTHENTICATED_USER.getType()) || role
						.equalsIgnoreCase(UserRole.UserRoleType.OTHER.getType()))) {
			profile.setUserType(role);
		}
		if (dateOfBirth != null && accountType != null && !dateOfBirth.equalsIgnoreCase(_NULL)) {
			if (accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()) && userParentId != null) {
				if (dateOfBirth.equalsIgnoreCase("00/00/0000")) {
					profile.setDateOfBirth(this.getProfile(this.getUser(userParentId)).getChildDateOfBirth());
				} else {
					final Integer age = this.calculateCurrentAge(dateOfBirth);
					SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
					final Date date = dateFormat.parse(dateOfBirth);
					if (age < 13 && age >= 0) {
						profile.setDateOfBirth(date);
						final User parentUser = profile.getUser().getParentUser();
						final Profile parentProfile = this.getUserRepository().getProfile(getUser(parentUser.getGooruUId()), false);
						parentProfile.setChildDateOfBirth(date);
						this.getUserRepository().save(parentProfile);
					}
				}
			} else {
				final Integer age = this.calculateCurrentAge(dateOfBirth);
				final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
				final Date date = dateFormat.parse(dateOfBirth);
				if (age >= 13 && accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType())) {
					profile.setDateOfBirth(date);
				}
			}
		}
		if (childDOB != null && !childDOB.equalsIgnoreCase(_NULL) && accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
			final Integer age = this.calculateCurrentAge(childDOB);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
			final Date date = dateFormat.parse(childDOB);
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
			final UserRoleAssoc userRoleAssoc = new UserRoleAssoc();
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
	
		if (inviteuser != null && inviteuser.size() > 0) {
			this.getCollaboratorService().updateCollaboratorStatus(newUser.getEmailId(), user);
		}
		userCreatedDevice(user.getPartyUid(), request);
		this.getUserRepository().save(new PartyCustomField(user.getPartyUid(), USER_META, SHOW_PROFILE_PAGE, FALSE));
		if (source != null && source.equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			final Map<String, String> dataMap = new HashMap<String, String>();
			if (identity != null && identity.getUser() != null) {
				dataMap.put(GOORU_UID, identity.getUser().getPartyUid());
			}
			dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
			if (identity != null && identity.getExternalId() != null) {
				dataMap.put(RECIPIENT, identity.getExternalId());
			}
			this.getUserRepository().save(new PartyCustomField(user.getPartyUid(), USER_META, USER_CONFIRM_STATUS, TRUE));
			try {
				this.getMailHandler().handleMailEvent(dataMap);
			} catch (Exception e) {
				LOGGER.error("Error : " + e);
			}
		}
		indexHandler.setReIndexRequest(user.getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);
		try {
			this.getUsereventlog().getEventLogs(user, source, identity);
		} catch (JSONException e) {
			LOGGER.error("Error" + e.getMessage());
		}
		return user;
	}

	private void userCreatedDevice(final String partyUid, final HttpServletRequest request) {
		final PartyCustomField partyCustomField = new PartyCustomField(USER_META, GOORU_USER_CREATED_DEVICE, request.getHeader(USER_AGENT));
		this.getPartyService().createPartyCustomField(partyUid, partyCustomField, null);
	}

	@Override
	public User getUser(final String gooruUId) throws Exception {
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

	public static String getDefaultUserRoles(final String organizationUid) {
		final String roles = SettingService.getInstance().getOrganizationSetting(ConfigConstants.DEFAULT_USER_ROLES, organizationUid);
		return roles != null ? roles : null;
	}

	@Override
	public String buildUserProfileImageUrl(final User user) {
		return this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, user.getOrganization().getPartyUid()) + "/" + this.getSettingService().getConfigSetting(ConfigConstants.PROFILE_BUCKET, user.getOrganization().getPartyUid()) + user.getPartyUid() + ".png";
	}

	@Override
	public String encryptPassword(final String password) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw new BadRequestException(generateErrorMessage("GL0068"), "GL0068");
		}
		try {
			messageDigest.update(password.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw new BadRequestException(generateErrorMessage("GL0069"), "GL0069");
		}
		byte raw[] = messageDigest.digest(); // step 4
		return new Base64Encoder().encode(raw); // step 5
	}

	@Override
	public User getUserByToken(final String userToken) {
		if (userToken == null || userToken.equalsIgnoreCase("")) {
			throw new BadRequestException(generateErrorMessage("GL0061", "User token"), "GL0061");
		}
		final User user = getUserRepository().findByToken(userToken);
		if (user == null) {
			throw new BadRequestException(generateErrorMessage("GL0056", "User"), GL0056);
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
		return user;
	}

	@Override
	public User createUser(final User user, final String password, final String school, final Integer confirmStatus, final Integer addedBySystem, final String userImportCode, final String accountType, final String dateOfBirth, final String userParentId, final String gender, final String childDOB, final String source, final HttpServletRequest request, final String role,
			final String mailConfirmationUrl) throws Exception {
		return createUser(user, password, school, confirmStatus, addedBySystem, userImportCode, accountType, dateOfBirth, userParentId, null, gender, childDOB, source, null, request, role, mailConfirmationUrl);
	}

	@Override
	public User resetPasswordRequest(final String emailId, final String gooruBaseUrl, final User apicaller, final String mailConfirmationUrl) throws Exception {
		Identity identity = new Identity();
		if (apicaller != null && !apicaller.getGooruUId().toLowerCase().contains(Constants.ANONYMOUS)) {
			identity = this.findUserByGooruId(apicaller.getGooruUId());
		} else {
			identity = this.getUserRepository().findByEmailIdOrUserName(emailId, true, false);
		}
		if (identity == null) {
			throw new NotFoundException(generateErrorMessage("GL0070"), "GL0070");
		}
		String token = UUID.randomUUID().toString();
		final User user = this.userRepository.findByIdentity(identity);
		if (user == null) {
			throw new NotFoundException(generateErrorMessage("GL0071"), "GL0071");
		}
		if (user.getConfirmStatus() == 0) {
			throw new BadRequestException(generateErrorMessage("GL0072"), "GL0072");
		}
		Credential creds = identity.getCredential();
		if (creds == null && identity.getAccountCreatedType() != null && identity.getAccountCreatedType().equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			throw new BadRequestException(generateErrorMessage("GL0073"), "GL0073");
		}
		if (creds == null) {
			creds = new Credential();
			final String password = UUID.randomUUID().toString();
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
	public Identity resetCredential(final String token, final String gooruUid, final String password, final User apiCaller, final String mailConfirmationUrl, final Boolean isPartnerPortal) throws Exception {
		Identity identity = null;
		if (token != null) {
			if (this.getUserService().hasResetTokenValid(token)) {
				throw new BadRequestException(TOKEN_EXPIRED);
			}
			identity = this.getUserService().findIdentityByResetToken(token);
			if (identity.getUser().getUsername().equalsIgnoreCase(password)) {
				throw new BadRequestException(generateErrorMessage("GL0074"), "GL0074");
			}
		} else {
			if (this.isContentAdmin(apiCaller)) {
				identity = this.findUserByGooruId(gooruUid);
			} else {
				throw new BadRequestException(generateErrorMessage("GL0075"), "GL0075");
			}
		}
		boolean flag = false;
		final String tokenIsExist = identity.getCredential().getToken();
		if (tokenIsExist != null && tokenIsExist.contains(RESET_TOKEN_SUFFIX)) {
			flag = true;
		}
		final String newGenereatedToken = UUID.randomUUID().toString() + RESET_TOKEN_SUFFIX;
		final String resetPasswordConfirmRestendpoint = this.getSettingService().getConfigSetting(ConfigConstants.RESET_PASSWORD_CONFIRM_RESTENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		this.getUserService().validatePassword(password, identity.getUser().getUsername());
		final String encryptedPassword = this.encryptPassword(password);
		identity.setLastLogin(new Date(System.currentTimeMillis()));
		final Credential credential = identity.getCredential();
		credential.setPassword(encryptedPassword);
		credential.setPasswordEncryptType(CustomProperties.PasswordEncryptType.SHA.getPasswordEncryptType());
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
	public Set<String> checkContentAccess(final User authenticationUser, final String goorContentId) {
		final Set<String> permissions = new HashSet<String>();
		if (authenticationUser != null) {
			final Content content = this.getContentRepository().findContentByGooruId(goorContentId, true);
			if (content != null) {
				final Set<ContentPermission> contentPermissions = content.getContentPermissions();
				for (final ContentPermission userPermission : contentPermissions) {
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
	public String getUserEmailFromIdentity(final Set<Identity> identity) {
		final Iterator<Identity> iter = identity.iterator();
		return iter.next().getExternalId();
	}

	@Override
	public void deleteUserMeta(final String gooruUid, final Profile newProfile, final User apiCaller) {
		final User user = this.getUserRepository().findByGooruId(gooruUid);
		if (user != null && newProfile != null) {
			final Profile profile = this.getUserService().getProfile(user);
			if (profile != null) {
				if (newProfile.getCourses() != null && newProfile.getCourses().size() > 0) {
					deleteCourse(newProfile.getCourses(), user, apiCaller);
				}
				if (newProfile.getGrade() != null && profile.getGrade() != null) {
					profile.setGrade(deleteGrade(newProfile.getGrade(), user, apiCaller));
					this.getUserRepository().save(profile);
				}
				indexHandler.setReIndexRequest(profile.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);
			}
		}

	}

	private String addGrade(final String newGrade, final User user, final User apiCaller, final String activeFlag) {
		final List<String> newGradeList = Arrays.asList(newGrade.split(","));
		final StringBuilder newGrades = new StringBuilder();
		final StringBuilder totalGrades = new StringBuilder();
		final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
		final List<UserClassification> userClassificationList = new ArrayList<UserClassification>();
		for (final String grade : newGradeList) {
			if (!grade.isEmpty()) {
				final UserClassification existingGrade = this.getUserRepository().getUserClassification(user.getGooruUId(), type.getCustomTableValueId(), null, null, grade);
				if (existingGrade == null) {
					final UserClassification userClassification = new UserClassification();
					userClassification.setGrade(grade);
					userClassification.setType(type);
					userClassification.setUser(user);
					userClassification.setActiveFlag(activeFlag != null ? Integer.parseInt(activeFlag) : 1);
					userClassification.setCreator(apiCaller);
					userClassificationList.add(userClassification);
					if (newGrades.length() > 0) {
						newGrades.append(",");
					}
					newGrades.append(grade);
				} else {
					if (activeFlag != null) {
						existingGrade.setActiveFlag(Integer.parseInt(activeFlag));
						userClassificationList.add(existingGrade);
					}
				}
			}
		}
		this.getUserRepository().saveAll(userClassificationList);
		totalGrades.append(this.getUserRepository().getUserGrade(user.getGooruUId(), type.getCustomTableValueId(), null));
		if (newGrades != null && newGrades.length() > 0) {
			totalGrades.append(",");
			totalGrades.append(newGrades.toString());
		}
		return totalGrades.toString();
	}

	private String deleteGrade(final String deleteGrade, final User user, final User apiCaller) {
		final CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.USER_CLASSIFICATION_TYPE.getTable(), CustomProperties.UserClassificationType.GRADE.getUserClassificationType());
		this.getUserRepository().deleteUserClassificationByGrade(apiCaller.getPartyUid(), deleteGrade);
		return this.getUserRepository().getUserGrade(user.getGooruUId(), type.getCustomTableValueId(), null);
	}

	@Override
	public void deleteUserContent(final String gooruUid, final String isDeleted, final User apiCaller) {
		final User user = this.getUserRepository().findByGooruId(gooruUid);
		if ((user != null && isDeleted != null && isDeleted.equalsIgnoreCase(TRUE))) {
			if (isContentAdmin(apiCaller) || user == apiCaller) {
				user.setIsDeleted(true);
				if (user.getIdentities() != null) {
					Identity identity = user.getIdentities().iterator().next();
					identity.setExternalId(identity.getExternalId() + System.currentTimeMillis());
					this.getUserRepository().save(identity);
				}
				final List<Content> contents = this.getContentRepository().getContentByUserUId(gooruUid);
				final List<ContentPermission> removeContentPermission = new ArrayList<ContentPermission>();
				final List<Content> removeContentList = new ArrayList<Content>();
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
							final Set<ContentPermission> contentPermissions = content.getContentPermissions();
							for (final ContentPermission contentPermission : contentPermissions) {
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
					indexHandler.setReIndexRequest(gooruOidAsString, IndexProcessor.INDEX, RESOURCE, null, false, false);
				}
			} else {
				throw new UnauthorizedException(generateErrorMessage("GL0085"));

			}
		}
	}

	@Override
	public void deleteUserImageProfile(final String userId) throws Exception {
		final User user = findByGooruId(userId);
		if (user != null) {
			profileImageUtil.deleteS3Upload(this.getUserRepository().getProfile(user, false));
		} else {
			throw new FileNotFoundException(generateErrorMessage("GL0075", "User"));
		}
	}

	@Override
	public void updateOrgAdminCustomField(final String organizationUid, final User user) throws Exception {
		final Organization organization = this.getOrganizationService().getOrganizationById(organizationUid);
		if (organization != null) {
			final PartyCustomField partyCustomField = new PartyCustomField();
			partyCustomField.setCategory(PartyCategoryType.USER_META.getpartyCategoryType());
			partyCustomField.setOptionalKey(ConstantProperties.ORG_ADMIN_KEY);
			partyCustomField.setOptionalValue(organizationUid);
			partyCustomField.setPartyUid(user.getPartyUid());
			partyService.createPartyCustomField(MY, partyCustomField, user);
		} else {
			throw new BadRequestException(generateErrorMessage("GL0076"), "GL0076");
		}
	}

	@Override
	public Map<String, Object> followUser(final User user, final String followOnUserId) {
		UserRelationship userRelationship = getUserRepository().getActiveUserRelationship(user.getPartyUid(), followOnUserId);
		final User followOnUser = getUserRepository().findByGooruId(followOnUserId);
		if (userRelationship != null) {
			return this.setUserObj(followOnUser);
		}
		userRelationship = new UserRelationship();
		userRelationship.setUser(user);
		userRelationship.setFollowOnUser(followOnUser);
		userRelationship.setActivatedDate(new Date(System.currentTimeMillis()));
		userRelationship.setActiveFlag(true);
		getUserRepository().save(userRelationship);
		final UserSummary userSummary = this.getUserRepository().getSummaryByUid(user.getPartyUid());
		final UserSummary followOnUserSummary = this.getUserRepository().getSummaryByUid(followOnUserId);
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
		try {
			this.getUsereventlog().getEventLogs(false, false, null, null, true, false);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e.getMessage());
		}
		return this.setUserObj(followOnUser);
	}

	@Override
	public void unFollowUser(final User user, final String unFollowUserId) {
		final UserRelationship userRelationship = getUserRepository().getActiveUserRelationship(user.getPartyUid(), unFollowUserId);
		if (userRelationship == null) {
			throw new BadRequestException(generateErrorMessage("GL0077"), "GL0077");
		} else {
			this.getUserRepository().remove(userRelationship);
			final UserSummary userSummary = this.getUserRepository().getSummaryByUid(user.getPartyUid());
			final UserSummary followOnUserSummary = this.getUserRepository().getSummaryByUid(unFollowUserId);
			userSummary.setFollowing(userSummary.getFollowing() - 1);
			followOnUserSummary.setFollowers(followOnUserSummary.getFollowers() - 1);
			this.getUserRepository().save(userSummary);
			this.getUserRepository().save(followOnUserSummary);
		}
		try {
			this.getUsereventlog().getEventLogs(false, false, null, null, false, true);
		} catch (JSONException e) {
			LOGGER.debug("Error" + e.getMessage());
		}
	}

	private Map<String, Object> setUserObj(final User user) {
		final Map<String, Object> userObj = new HashMap<String, Object>();
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
	public Map<String, Object> userMeta(final User user) {
		final Map<String, Object> meta = new HashMap<String, Object>();
		final PartyCustomField partyCustomField = partyService.getPartyCustomeField(user.getPartyUid(), USER_TAXONOMY_ROOT_CODE, null);
		final Map<String, Object> taxonomy = new HashMap<String, Object>();
		final String taxonomyCodeIds = (partyCustomField != null && partyCustomField.getOptionalValue() != null && partyCustomField.getOptionalValue().length() > 0) ? partyCustomField.getOptionalValue() : this.getTaxonomyRespository().getFindTaxonomyList(
				settingService.getConfigSetting(ConfigConstants.GOORU_EXCLUDE_TAXONOMY_PREFERENCE, 0, user.getOrganization().getPartyUid()));

		if (taxonomyCodeIds != null) {
			final String taxonomyCode = this.getTaxonomyRespository().getFindTaxonomyCodeList(taxonomyCodeIds);
			if (taxonomyCode != null) {
				final List<String> taxonomyCodeList = Arrays.asList(taxonomyCode.split(","));
				taxonomy.put(CODE, taxonomyCodeList);
				List<String> taxonomyCodeIdList = Arrays.asList(taxonomyCodeIds.split(","));
				taxonomy.put(CODE_ID, taxonomyCodeIdList);
			}
		}
		final PartyCustomField partyCustomFieldFeatured = partyService.getPartyCustomeField(user.getPartyUid(), IS_FEATURED_USER, null);
		if (partyCustomFieldFeatured != null && partyCustomFieldFeatured.getOptionalValue() != null && partyCustomFieldFeatured.getOptionalValue().length() > 0) {
			meta.put(FEATURED_USER, Boolean.parseBoolean(partyCustomFieldFeatured.getOptionalValue()));
		}
		meta.put(USER_TAX_PREFERENCE, taxonomy);
		meta.put(SUMMARY, getUserSummary(user.getPartyUid()));
		return meta;
	}

	@Override
	public User updateUserViewFlagStatus(final String gooruUid, final Integer viewFlag) {
		return this.getUserService().updateViewFlagStatus(gooruUid, viewFlag);
	}

	@Override
	public Map<String, Object> getUserSummary(final String gooruUid) {
		final UserSummary userSummary = this.getUserRepository().getSummaryByUid(gooruUid);
		final Map<String, Object> summary = new HashMap<String, Object>();
		summary.put(COLLECTION, userSummary.getCollections() != null ? userSummary.getCollections() : 0);
		summary.put(TAGS, userSummary.getTag() != null ? userSummary.getTag() : 0);
		summary.put(FOLLOWING, userSummary.getFollowing() != null ? userSummary.getFollowing() : 0);
		summary.put(FOLLOWERS, userSummary.getFollowers() != null ? userSummary.getFollowers() : 0);
		return summary;
	}

	@Override
	public void resetEmailAddress(final List<String> data) throws Exception {
		for (final String mailId : data) {
			final Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
			final String domainName = this.getSettingService().getConfigSetting(ConfigConstants.GOORU_USER_MAIL_RESET, 0, TaxonomyUtil.GOORU_ORG_UID);
			String[] mailAddress = mailId.split("@");
			if (domainName != null && identity != null) {
				String[] domains = domainName.split(",");
				for (final String domain : domains) {
					if (mailAddress[1].equalsIgnoreCase(domain)) {
						identity.setExternalId(mailAddress[1] + System.currentTimeMillis());
						this.getUserRepository().save(identity);
					}
				}
			} else {
				throw new BadRequestException("Requested domain not found");
			}
		}
	}

	@Override
	public Boolean isFollowedUser(final String gooruUserId, final User apiCaller) {
		return getUserRepository().getActiveUserRelationship(apiCaller.getPartyUid(), gooruUserId) != null ? true : false;
	}

	@Override
	public SearchResults<UserRole> getRoles(final Integer offset, final Integer limit, final String userUid) {
		final SearchResults<UserRole> result = new SearchResults<UserRole>();
		result.setSearchResults(this.getUserRepository().getRoles(offset, limit, userUid));
		result.setTotalHitCount(this.getUserRepository().countRoles(userUid));
		return result;
	}

	@Override
	public ActionResponseDTO<UserRole> createNewRole(final UserRole role, final User user) throws Exception {
		UserRole userRole = userRepository.findUserRoleByName(role.getName(), null);
		final Errors errors = validateCreateRole(role);
		final Organization gooruOrg = organizationService.getOrganizationById(TaxonomyUtil.GOORU_ORG_UID);
		final Set<RoleEntityOperation> entityOperations = role.getRoleOperations();
		final Iterator<RoleEntityOperation> iter = entityOperations.iterator();
		if (userRole != null && user.getOrganization().equals(gooruOrg)) {
			throw new BadRequestException(generateErrorMessage(GL0041, ROLE));
		} else {
			if (!errors.hasErrors()) {
				userRole = new UserRole();
				userRole.setName(role.getName());
				userRole.setDescription(role.getDescription());
				getUserRepository().save(userRole);
			}
		}
		while (iter.hasNext()) {
			final RoleEntityOperation roleEntityOperation = (RoleEntityOperation) iter.next();
			final EntityOperation entityOperation = this.getEntityOperationByEntityOperationId(roleEntityOperation.getEntityOperation().getEntityOperationId());
			roleEntityOperation.setUserRole(userRole);
			roleEntityOperation.setEntityOperation(entityOperation);
			getUserRepository().save(roleEntityOperation);
		}
		indexHandler.setReIndexRequest(user.getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);
		return new ActionResponseDTO<UserRole>(userRole, errors);
	}

	private Errors validateCreateRole(final UserRole userRole) {
		final Errors errors = new BindException(userRole, "role");
		rejectIfNull(errors, userRole, NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}

	@Override
	public UserRole updateRole(final UserRole role, final Integer roleId) throws Exception {
		rejectIfNull(role, GL0056, ROLE);
		final UserRole userRole = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(userRole, GL0056, 404, ROLE);
		if (role.getName() != null) {
			userRole.setName(role.getName());
		}
		if (role.getDescription() != null) {
			userRole.setDescription(role.getDescription());
		}
		userRepository.save(userRole);
		return userRole;
	}

	@Override
	public void removeRole(final Integer roleId) throws Exception {

		final UserRole userRole = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(userRole, GL0056, 404, ROLE);
		userRepository.remove(userRole);
	}

	@Override
	public EntityOperation getEntityOperationByEntityOperationId(final Integer entityOperationId) {
		return userRepository.getEntityOperationByEntityOperationId(entityOperationId);
	}

	@Override
	public SearchResults<EntityOperation> findAllEntityNames(final Integer offset, final Integer limit) {
		final SearchResults<EntityOperation> result = new SearchResults<EntityOperation>();
		result.setSearchResults(this.getUserRepository().findAllEntityNames(offset, limit));
		result.setTotalHitCount(this.getUserRepository().countAllEntityNames());
		return result;
	}

	@Override
	public List<EntityOperation> getOperationsByEntityName(final String entityName) {
		return this.getUserRepository().findOperationsByEntityName(entityName);
	}

	@Override
	public UserRoleAssoc assignRoleByUserUid(final Integer roleId, final String userUid) throws Exception {
		final User user = userRepository.findUserByPartyUid(userUid);
		final UserRole role = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(role, GL0056, 404, ROLE);
		UserRoleAssoc userRoleAssoc = userRepository.findUserRoleAssocEntryByRoleIdAndUserUid(roleId, userUid);
		rejectIfAlreadyExist(userRoleAssoc, GL0103, USER);
		userRoleAssoc = new UserRoleAssoc();
		userRoleAssoc.setUser(user);
		userRoleAssoc.setRole(role);
		getUserRepository().save(userRoleAssoc);
		indexHandler.setReIndexRequest(user.getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);
		return userRoleAssoc;
	}

	@Override
	public void removeAssignedRoleByUserUid(final Integer roleId, final String userUid) throws Exception {
		final UserRoleAssoc userRoleAssoc = userRepository.findUserRoleAssocEntryByRoleIdAndUserUid(roleId, userUid);
		rejectIfNull(userRoleAssoc, GL0102, 404, USER);
		getUserRepository().remove(userRoleAssoc);
		indexHandler.setReIndexRequest(userRoleAssoc.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);
	}

	@Override
	public UserRole getRoleByRoleId(final Integer roleId) {
		final UserRole userRole = userRepository.findUserRoleByRoleId(roleId);
		rejectIfNull(userRole, GL0056, 404, ROLE);
		return userRole;
	}

	@Override
	public List<RoleEntityOperation> getRoleOperationsByRoleId(final Integer roleId) {
		final UserRole role = this.getUserRepository().findUserRoleByRoleId(roleId);
		rejectIfNull(role, GL0056, 404, ROLE);
		return this.getUserRepository().findRoleOperationsByRoleId(roleId);
	}

	@Override
	public List<CustomTableValue> getUserCategory(final User apiCaller) {
		return this.getCustomTableRepository().getCustomValues(USER_CATEGORY);
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
	
	public CountryService getCountryService(){
		return countryService;
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
