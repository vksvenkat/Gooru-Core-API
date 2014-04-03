/////////////////////////////////////////////////////////////
// MailHandler.java
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
package org.ednovo.gooru.infrastructure.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.velocity.app.VelocityEngine;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.EventMapping;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.GooruMd5Util;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.EventService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.PartyRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.user.UserRepositoryHibernate;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class MailHandler extends ServerValidationUtils implements ConstantProperties, ParameterProperties {

	@Autowired
	private SettingService settingService;

	@Autowired
	@Resource(name = "serverConstants")
	private Properties serverConstants;

	@Autowired
	private UserRepositoryHibernate userRepositoryHibernate;

	@Autowired
	private LearnguideRepositoryHibernate learnGuideRepositoryHibernate;

	public static Logger logger = LoggerFactory.getLogger(MailHandler.class);

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private EventService eventService;

	@Autowired
	private CollectionRepository collectionRepository;
	
	@Autowired
	private ResourceRepository resourceRepository; 

	@Autowired
	private PartyRepository partyRepository;

	public static final String REQUEST_PUBLISHER = "requestPublisher";

	public static final String CONFIRM = "confirm";

	public static final String PASSWORD_RESET = "passwordReset";

	private static final String FROM = "Gooru Accounts";

	private static final String PASSWORD_CONFIRM_SUBJECT = "Gooru Password Change Confirmation";

	private static final String REGISTRATION = "Your Gooru account is confirmed!";

	private static final String COLLABORATOR_GREETING = "You've been added as a Collaborator!";

	private static final String FOLLOWER_GREETING = "You've a follower in Gooru!";

	private static final String UNFOLLOWER_GREETING = "You've lose a follower in Gooru!";

	public static final String REQUEST_PUBLISHER_SUBJECT = " user collection publish request on Gooru";

	private static final String S3_CSV_RESOURCE_IMPORT = "Gooru S3 Resource Importer Status";
	
	private static final String GOORU_BUTTON_CSS =  "background: none repeat scroll 0 0 #0F76BB;border: 0 none;color: white;font-size: 12px;height: 32px;width: 105px;-webkit-border-radius: 3px 3px 3px 3px;-moz-border-radius: 3px 3px 3px 3px;border-radius: 3px 3px 3px 3px;-ms-border-radius: 3px 3px 3px 3px;-kthml-border-radius: 3px 3px 3px 3px;cursor: pointer;position: relative;line-height: 32px;text-align:center;";
	
	private static final String GOORU_COLLECTION_LINK_CSS = "color: #ffffff;text-decoration: none;";
	
	private static final String GO_TO_COLLECTION_TEXT = "font-size: 13px; position: relative; top: 7px; left: 5px; font-family: arial;";

	private static final String SECRET_KEY = "sendmail";

	public void sendMailToResetPassword(String gooruUid, String password, Boolean flag, String gooruClassicUrl, String mailConfirmationUrl) throws Exception {

		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUid);
		String resetPasswordLink = null;
		String resetPasswordURL = null;
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		String userEmailId = identity.getExternalId();
		String resetToken = identity.getCredential().getToken();
		Map<String, Object> map = new HashMap<String, Object>();
		EventMapping eventMapping = null;
		gooruClassicUrl = gooruClassicUrl != null ? BaseUtil.changeHttpsProtocol(gooruClassicUrl) : null;
		mailConfirmationUrl = mailConfirmationUrl != null ? BaseUtil.changeHttpsProtocol(mailConfirmationUrl) : null;

		if (identity.getUser().getAccountTypeId() != null && identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
			Identity parentIdentity = this.getUserRepositoryHibernate().findUserByGooruId(identity.getUser().getParentUser().getGooruUId());
			eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.CHANGE_CHILD_ACCOUNT_PASSWORD.getEvent());
			if (flag) {
				eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.PASSWORD_CHANGED_CONFIRMATION_NOTIFICATION.getEvent());
			}
			if (parentIdentity != null) {
				userEmailId = parentIdentity.getExternalId();
			}
		} else {
			if (mailConfirmationUrl != null) {
				eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.CHANGE_GOORU_PARTNER_ACCOUNT_PASSWORD.getEvent());
			} else {
				eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.CHANGE_GOORU_ACCOUNT_PASSWORD.getEvent());
			}
		}
		map = eventMapData(eventMapping);

		if (mailConfirmationUrl != null) {
			resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">Click here to reset your password.</a>";
			resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword</a>";
		} else {
			if (gooruClassicUrl != null) {
				resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">Click here to reset your password.</a>";
				resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword</a>";
			} else {
				resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\" " + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "\">Click here to reset your password.</a>";
				resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\" " + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "\">" + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "</a>";
			}
		}
		map.put("resetPasswordLink", resetPasswordLink);
		map.put("resetPasswordURL", resetPasswordURL);
		map.put("serverpath", serverpath);
		map.put("firstName", identity.getFirstName());
		map.put("gooruUserName", identity.getUser().getUsername());
		map.put("gooruUserPassword", password);
		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("recipient", userEmailId);
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		sendMailViaRestApi(map);
	}

	public void sendMailToConfirmPasswordChanged(String gooruUid, String password, Boolean flag, String gooruClassicUrl, String mailConfirmationUrl) throws Exception {

		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUid);
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		String userEmailId = identity.getExternalId();
		String resetPasswordLink = null;
		String resetPasswordURL = null;
		String resetToken = identity.getCredential().getToken();
		Map<String, Object> map = new HashMap<String, Object>();
		EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.PASSWORD_CHANGED_CONFIRMATION_NOTIFICATION.getEvent());
		map = eventMapData(eventMapping);

		if (mailConfirmationUrl != null) {
			resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">Click here to reset your password.</a>";
			resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">" + mailConfirmationUrl + "?resetToken=" + resetToken + "&callback=changePassword</a>";
		} else {
			if (gooruClassicUrl != null) {
				resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">Click here to reset your password.</a>";
				resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword\" target=\"_blank\">" + gooruClassicUrl + "&resetToken=" + resetToken + "&callback=changePassword</a>";
			} else {
				resetPasswordLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\" " + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "\">Click here to reset your password.</a>";
				resetPasswordURL = "<a style=\"color: #1076bb;text-decoration: none;\" href=\" " + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "\">" + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "</a>";
			}
		}

		map.put("serverpath", serverpath);
		map.put("firstName", identity.getFirstName());
		map.put("gooruUserName", identity.getUser().getUsername());
		map.put("gooruUserPassword", password);
		map.put("resetPasswordLink", resetPasswordLink);
		map.put("resetPasswordURL", resetPasswordURL);
		map.put("gooruUserName", identity.getUser().getUsername());

		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("recipient", userEmailId);
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", PASSWORD_CONFIRM_SUBJECT);
		sendMailViaRestApi(map);
	}

	public void sendMailToConfirm(String gooruUid, String password, String accountType, String tokenId, String encodedDateOfBirth, String gooruClassicUrl, String mailConfirmationUrl, String userGrade, String userCourse) throws Exception {

		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUid);

		String userEmailId = identity.getExternalId();
		String parentExistingFlag = null;
		String userAccountType = "NonParent";
		String completeRegistration = null;
		String passwordResetLink = null;
		String registrationURL = null;
		String resetToken = null;
		Map<String, Object> model = new HashMap<String, Object>();
		mailConfirmationUrl = mailConfirmationUrl != null ? BaseUtil.changeHttpsProtocol(mailConfirmationUrl) : null;

		if (encodedDateOfBirth == null) {
			encodedDateOfBirth = "MTIzNDU2Nzg5"; // encoded data for '123456789'
		} else if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			encodedDateOfBirth = encodedDateOfBirth.replaceAll("d", "/");
		} else {
			encodedDateOfBirth = encodedDateOfBirth.replaceAll("/", "d");
		}

		if (identity.getCredential() != null && identity.getCredential().getToken() != null) {
			resetToken = identity.getCredential().getToken();
		}
		if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType())) {
			EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.PARANT_REGISTRATION_CONFIRMATION.getEvent());
			model = eventMapData(eventMapping);
			if (identity.getUser().getConfirmStatus() != null && identity.getUser().getConfirmStatus() == 1) {
				parentExistingFlag = "You'll need to create your account first to create your child account.";
			}
			if (gooruClassicUrl != null && serverpath != null) {
				completeRegistration = "<a style=\"color: #1076bb;\" href=\"" + gooruClassicUrl + "/#discover&gooruuid=" + identity.getUser().getGooruUId() + "&sessionid=" + tokenId + "&dob=" + encodedDateOfBirth + "&type=" + userAccountType
						+ "&callback=confirmUser\" target=\"_blank\">Complete Registration</a>";
				registrationURL = gooruClassicUrl + "/#discover&gooruuid=" + identity.getUser().getGooruUId() + "&sessionid=" + tokenId + "&dob=" + encodedDateOfBirth + "&type=" + userAccountType + "&callback=confirmUser";
			} else {
				completeRegistration = "<a style=\"color: #1076bb;\" href=\"" + serverpath + "/gooru/index.g#!/user/registration/" + identity.getUser().getGooruUId() + "/session/" + tokenId + "/" + encodedDateOfBirth + "/type/" + userAccountType + "\" target=\"_blank\">Complete Registration</a>";
				registrationURL = serverpath + "/gooru/index.g#!/user/registration/" + identity.getUser().getGooruUId() + "/session/" + tokenId + "/" + encodedDateOfBirth + "/type/" + userAccountType;
			}
			userAccountType = accountType;
		} else if (accountType != null && accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType())) {
			EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.CHILD_REGISTRATION_CONFIRMATION.getEvent());
			model = eventMapData(eventMapping);
			if (identity != null && identity.getUser().getParentUser() != null) {
				Identity parentIdentity = this.getUserRepositoryHibernate().findUserByGooruId(identity.getUser().getParentUser().getGooruUId());
				if (parentIdentity != null) {
					userEmailId = parentIdentity.getExternalId();
				}
			}
			if (gooruClassicUrl != null && serverpath != null) {
				gooruClassicUrl = BaseUtil.changeHttpsProtocol(gooruClassicUrl);
				passwordResetLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"" + gooruClassicUrl + "/#discover&amp;resetToken=" + resetToken + "&amp;callback=changePassword\" target=\"_blank\"> here</a>";
			} else {
				passwordResetLink = "<a style=\"color: #1076bb;text-decoration: none;\" href=\"  " + serverpath + "/gooru/index.g#!/change-password/" + resetToken + "\" target=\"_blank\"> here</a>";
			}
			model.put("passwordResetLink", passwordResetLink);
			userAccountType = accountType;
		} else {
			if (mailConfirmationUrl == null) {
				EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.NON_PARANT_REGISTRATION_CONFIRMATION.getEvent());
				model = eventMapData(eventMapping);
				if (gooruClassicUrl == null) {
					completeRegistration = "<a style=\"color: #1076bb;\" href=\"" + serverpath + "/gooru/index.g#!/user/registration/" + identity.getUser().getGooruUId() + "/session/" + tokenId + "/" + encodedDateOfBirth + "/type/" + userAccountType
							+ "\" target=\"_blank\">Click Here to Complete Registration.</a>";
				} else {
					completeRegistration = "<a style=\"color: #1076bb;\" href=\"" + gooruClassicUrl + "/#discover&gooruuid=" + identity.getUser().getGooruUId() + "&sessionid=" + tokenId + "&dob=" + encodedDateOfBirth + "&type=" + userAccountType
							+ "&callback=confirmUser\" target=\"_blank\">Click Here to Complete Registration.</a>";
				}
			} else {
				EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.PARTNER_PORTAL_USER_REGISTRATION_CONFIRMATION.getEvent());
				model = eventMapData(eventMapping);
				completeRegistration = "<a style=\"color: #1076bb;\" href=\"" + mailConfirmationUrl + "?gooruuid=" + identity.getUser().getGooruUId() + "&sessionid=" + tokenId + "&dob=" + encodedDateOfBirth + "&type=" + userAccountType
						+ "&callback=confirmUser\" target=\"_blank\">Click Here to Complete Registration.</a>";
			}
		}
		
		if(userGrade != null || userCourse != null ) {
			model.put("gradeCourseHeading", "<b>Grade(s) and Course(s) interested in: </b>");
		} else {
			model.put("gradeCourseHeading", "");
		}
		if (userGrade != null && userGrade.length() != 0) {
			model.put("userGrade","Grade "+userGrade+ ".");
		} else {
			model.put("userGrade", "");
		}
		if (userCourse != null && userCourse.length() != 0) {
			model.put("userCourse","Course "+ userCourse+ ".");
		} else {
			model.put("userCourse", "");
		}
		
		model.put("serverpath", serverpath);
		model.put("registrationURL", registrationURL);
		model.put("completeRegistration", completeRegistration);
		model.put("firstName", identity.getFirstName());
		model.put("userId", identity.getUser().getGooruUId());
		model.put("registerToken", identity.getUser().getRegisterToken());
		model.put("gooruUserName", identity.getUser().getUsername());
		model.put("gooruUserPassword", password);
		model.put("encodedDateOfBirth", encodedDateOfBirth);
		model.put("parentExistingFlag", parentExistingFlag);
		model.put("userAccountType", userAccountType);

		String organizationUid = identity.getUser().getOrganization().getPartyUid();

		model.put("htmlContent", generateMessage((String) model.get("htmlContent"), model));
		model.put("content", generateMessage((String) model.get("textContent"), model));
		model.put("recipient", userEmailId);
		model.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		model.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		model.put("fromName", FROM);

		logger.warn("Sending Registration confirmation for email " + userEmailId);
		sendMailViaRestApi(model);
	}

	public void sendMailToConfirmBulkUser(String gooruUid, String password, String accountType, String tokenId, String encodedDateOfBirth) throws Exception {

		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUid);

		String userEmailId = identity.getExternalId();

		String mailToConfirmType = "bulkUserConfirmHtml.vm";
		String mailToConfirmTypeText = "bulkUserConfirmText.vm";
		Boolean parentExistingFlag = false;
		String userAccountType = "NonParent";

		if (encodedDateOfBirth == null) {
			encodedDateOfBirth = "MTIzNDU2Nzg5"; // encoded data for '123456789'
		} else {
			encodedDateOfBirth = encodedDateOfBirth.replaceAll("/", "d");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("serverpath", serverpath);
		model.put("firstName", identity.getFirstName());
		model.put("userId", identity.getUser().getGooruUId());
		model.put("sessionId", tokenId);
		model.put("registerToken", identity.getUser().getRegisterToken());
		model.put("gooruUserName", identity.getUser().getUsername());
		model.put("gooruUserPassword", password);
		if (identity.getCredential() != null && identity.getCredential().getToken() != null) {
			model.put("resetToken", identity.getCredential().getToken());
		}
		model.put("encodedDateOfBirth", encodedDateOfBirth);
		model.put("parentExistingFlag", parentExistingFlag);
		model.put("userAccountType", userAccountType);

		String htmlResource = "";
		String resource = "";
		if (password != null && !password.equalsIgnoreCase("")) {
			htmlResource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailToConfirmType, model);
			resource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailToConfirmTypeText, model);
		} else {
			htmlResource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailToConfirmType, model);
			resource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, mailToConfirmTypeText, model);
		}
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		map.put("htmlContent", htmlResource);
		map.put("content", resource);
		map.put("recipient", userEmailId);
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", REGISTRATION);

		logger.warn("Sending Registration confirmation for email " + userEmailId);

		sendMailViaRestApi(map);
	}

	public void sendMailToRequestPublisher(Map<String, Object> model) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		User user = (User) model.get("user");
		String organizationUid = user.getOrganization().getPartyUid();
		String username = user.getFirstName() + " " + user.getLastName();

		String htmlContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "requestToPublisher.vm", model);
		String content = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "requestToPublisherText.vm", model);
		map.put("htmlContent", htmlContent);
		map.put("content", content);
		map.put("recipient", getConfigSetting(ConfigConstants.PUBLISHER, organizationUid));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", username + REQUEST_PUBLISHER_SUBJECT);
		sendMailViaRestApi(map);
	}

	public void sendCSVImportInfoMail(String htmlContent, String organizationUid) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("htmlContent", htmlContent);
		map.put("content", S3_CSV_RESOURCE_IMPORT);
		map.put("recipient", getConfigSetting(ConfigConstants.CONTENT_ADMIN, organizationUid));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", S3_CSV_RESOURCE_IMPORT);
		sendMailViaRestApi(map);
	}

	public void sendMailForCollaborator(String gooruUId, String senderUserName, String gooruOid, String collectionOrQuizTitle, String flag) throws Exception {
		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		String userEmailId = identity.getExternalId();
		String collaboratorMailHtml = "collaboratorMailHtml.vm";
		String collaboratorMailText = "collaboratorMailText.vm";
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("serverpath", serverpath);
		model.put("gooruUserName", identity.getUser().getUsername());
		model.put("collectionOrQuizTitle", collectionOrQuizTitle);
		model.put("collectionOrQuizId", gooruOid);
		model.put("Owner-Username", senderUserName);
		model.put("flag", flag);
		if (identity.getCredential() != null && identity.getCredential().getToken() != null) {
			model.put("resetToken", identity.getCredential().getToken());
		}
		String htmlResource = "";
		String resource = "";
		htmlResource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, collaboratorMailHtml, model);
		resource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, collaboratorMailText, model);
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		map.put("htmlContent", htmlResource);
		map.put("content", resource);
		map.put("recipient", userEmailId);
		map.put("username", getConfigSetting(ConfigConstants.MAIL_USERNAME, organizationUid));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("password", getConfigSetting(ConfigConstants.MAIL_PASSWORD, organizationUid));
		map.put("host", getConfigSetting(ConfigConstants.MAIL_SMTP_HOST, organizationUid));
		map.put("port", getConfigSetting(ConfigConstants.MAIL_SMTP_PORT, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", COLLABORATOR_GREETING);
		logger.warn("Sending Collaborator Greeting from email " + userEmailId);

		sendMailViaRestApi(map);

	}

	public void sendMailForFollowedOnUserOrGroup(String gooruUId) throws Exception {
		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		String userEmailId = identity.getExternalId();
		String followOnUserOrGroup = "followOnUserOrGroupHtml.vm";
		String followOnUserOrGroupMailHtmlText = "followOnUserOrGroupText.vm";
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("serverpath", serverpath);
		if (identity.getCredential() != null && identity.getCredential().getToken() != null) {
			model.put("resetToken", identity.getCredential().getToken());
		}

		String htmlResource = "";
		String resource = "";
		htmlResource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, followOnUserOrGroup, model);
		resource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, followOnUserOrGroupMailHtmlText, model);
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		map.put("htmlContent", htmlResource);
		map.put("content", resource);
		map.put("recipient", userEmailId);
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", FOLLOWER_GREETING);

		logger.warn("Sending FollowedOnUserOrGroup Greeting from email " + userEmailId);

		sendMailViaRestApi(map);

	}

	public void sendMailForUnFollowUserOrGroup(String gooruUId) throws Exception {
		final String serverpath = this.getServerConstants().getProperty("serverPath");
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		String userEmailId = identity.getExternalId();
		String followOnUserOrGroupMailHtml = "followOnUserOrGroupHtml.vm";
		String followOnUserOrGroupMailHtmlText = "followOnUserOrGroupText.vm";
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("serverpath", serverpath);
		if (identity.getCredential() != null && identity.getCredential().getToken() != null) {
			model.put("resetToken", identity.getCredential().getToken());
		}
		String htmlResource = "";
		String resource = "";
		htmlResource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, followOnUserOrGroupMailHtml, model);
		resource = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, followOnUserOrGroupMailHtmlText, model);
		String organizationUid = identity.getUser().getOrganization().getPartyUid();
		map.put("htmlContent", htmlResource);
		map.put("content", resource);
		map.put("recipient", userEmailId);
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, organizationUid));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, organizationUid));
		map.put("fromName", FROM);
		map.put("subject", UNFOLLOWER_GREETING);

		logger.warn("Sending FollowedOnUserOrGroup Greeting from email " + userEmailId);

		sendMailViaRestApi(map);

	}

	public void shareMailForContent(String toAddress, String fromAddress, String gooruUId, String subject, String message, String cfm, List<Map<String, String>> attachments) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String fromEmailAddress[] = fromAddress.split("@");
		String fromName = fromEmailAddress[0];
		String recipients = "";
		map.put("content", message);
		map.put("htmlContent", message);
		String  delimiters = "[,;]+";
	       String[] to = toAddress.split(delimiters);
	       for(int i= 0 ; i < to.length ; i++) {
	           recipients += recipients.length() == 0 ? to[i] : "," + to[i];
	       }
		map.put("recipient", recipients);
		map.put("from", fromAddress);
		if (cfm != null && cfm.equalsIgnoreCase("yes")) {
			map.put("cc", fromAddress);
		}
		if (attachments != null) {
			for (Map<String, String> attachment : attachments) {
				attachment.put("url", BaseUtil.changeToHttpProtocol(attachment.get("url")));
			}
			map.put("attachFiles", attachments);

		}
		map.put("fromName", fromName);
		map.put("subject", subject);

		logger.warn("Sending sharing content via Email " + toAddress);

		sendMailViaRestApi(map);

	}

	public void sendMail(String gooruUId, Map<String, Object> map) {
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		if (identity != null) {
		  map.put("user-name", identity.getUser().getUsernameDisplay());
		  map.put("recipient", identity.getExternalId());
		}
		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
		map.put("fromName", FROM);
		sendMailViaRestApi(map);
	}

	public void sendExternalIdConfirm(String gooruUId, Map<String, Object> map) {
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("recipient", map.get("oldEmailId") + "," + map.get("newMailId"));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, identity.getUser().getOrganization().getPartyUid()));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, identity.getUser().getOrganization().getPartyUid()));
		map.put("fromName", FROM);
		sendMailViaRestApi(map);
	}

	public void sendUserFirstCollectionCreate(String gooruUid, String accountTypeId, Map<String, Object> map) {

		PartyCustomField partyCustomField = this.getPartyRepository().getPartyCustomField(gooruUid, "user_first_collection_mail_send");
		User user = null;
		if (accountTypeId != null && accountTypeId.equals("2")) {
			user = this.getUserRepositoryHibernate().findByGooruId(gooruUid);
			gooruUid = user.getParentUser().getGooruUId();
		}
		if (partyCustomField != null && !Boolean.parseBoolean(partyCustomField.getOptionalValue())) {
			final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUid);
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(OFFSET_FIELD, "0");
			filters.put(LIMIT_FIELD, "2");
			filters.put(SKIP_PAGINATION, NO);
			filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
			List<CollectionItem> collectionItems = null;
			if (accountTypeId != null && accountTypeId.equals("2")) {
				collectionItems = this.getCollectionRepository().getMyCollectionItems(filters, user);
			} else {
				collectionItems = this.getCollectionRepository().getMyCollectionItems(filters, identity.getUser());
			}
			if (collectionItems != null && collectionItems.size() == 1) {
				map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
				map.put("content", generateMessage((String) map.get("textContent"), map));
				map.put("recipient", identity.getExternalId());
				map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, identity.getUser().getOrganization().getPartyUid()));
				map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, identity.getUser().getOrganization().getPartyUid()));
				map.put("fromName", FROM);
				sendMailViaRestApi(map);
				partyCustomField.setOptionalValue("true");
				this.getPartyRepository().save(partyCustomField);
			}
		}
	}

	public void sendUserInactiveMail(Map<String, Object> map) {
		Integer inactiveMailCount = this.getPartyRepository().getCountInActiveMailSendToday();
		Integer maxLimit = Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_USER_INACTIVE_MAIL_LIMIT, TaxonomyUtil.GOORU_ORG_UID));
		if (inactiveMailCount <= maxLimit) {
			Integer maxBatchSize = Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_USER_INACTIVE_MAIL_BATCH_SIZE, TaxonomyUtil.GOORU_ORG_UID));
			Integer inactiveUserCount = this.getUserRepositoryHibernate().getInactiveUsersCount();
			if (inactiveUserCount > 0) {
				Integer offset = Math.round(inactiveUserCount / maxBatchSize);
				Integer count = 0;
				for (int index = 0; index <= offset; index++) {
					List<Object[]> results = this.getUserRepositoryHibernate().getInactiveUsers(index, maxBatchSize);
					count += results.size();
					String userIds = "";
					String emailIds = "";
					for (Object[] object : results) {
						userIds += userIds.length() > 0 ? ", " : "";
						userIds += "'" + object[0] + "'";
						emailIds += emailIds.length() > 0 ? ", " : "";
						emailIds += object[1];
					}

					if (userIds.length() > 0 && emailIds.length() > 0) {
						map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
						map.put("content", generateMessage((String) map.get("textContent"), map));
						map.put("recipient", emailIds);
						map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
						map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
						map.put("fromName", FROM);
						this.getPartyRepository().updatePartyCustomFieldsInActiveMailKey(userIds);
						sendMailViaRestApi(map);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (count >= maxLimit) {
							break;
						}
					}
				}
			}
		}
	}

	private void sendMailViaRestApi(Map<String, Object> paramMap) {
		String url = settingService.getConfigSetting(ConfigConstants.GOORU_MAIL_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "send-mail";
		long expires = System.currentTimeMillis() + 1000 * 60 * 5;
		paramMap.put("expires", expires);
		paramMap.put("username", getConfigSetting(ConfigConstants.MAIL_USERNAME, TaxonomyUtil.GOORU_ORG_UID));
		paramMap.put("password", getConfigSetting(ConfigConstants.MAIL_PASSWORD, TaxonomyUtil.GOORU_ORG_UID));
		paramMap.put("host", getConfigSetting(ConfigConstants.MAIL_SMTP_HOST, TaxonomyUtil.GOORU_ORG_UID));
		paramMap.put("port", getConfigSetting(ConfigConstants.MAIL_SMTP_PORT, TaxonomyUtil.GOORU_ORG_UID));
		// paramMap.put("port", "587");
		try {
			paramMap.put("signature", new GooruMd5Util().signURLForClient(url, paramMap, SECRET_KEY, expires));
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject json = new JSONObject(paramMap);
		ClientResource clientResource = new ClientResource((settingService.getConfigSetting(ConfigConstants.GOORU_MAIL_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "send-mail"));
		clientResource.post(json.toString());
	}

	public void handleMailEvent(Map<String, String> data) {
		EventMapping eventMapping = this.getEventService().getTemplatesByEventName(data.get("eventType"));
		if (eventMapping != null) {
			Map<String, Object> map = eventMapData(eventMapping);
			if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.WELCOME_MAIL.getEvent())) {
				map.put("recipient", data.get("recipient"));
				sendMail(data.get("gooruUid"), map);
				
			} else if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.FIRST_COLLECTION.getEvent())) {
				sendUserFirstCollectionCreate(data.get("gooruUid"), data.get("accountTypeId"), map);
			} else 
			if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.SSO_CONFIRMATION_MAIL.getEvent())) {
				map.put("password", data.get("password"));
				sendMail(data.get("gooruUid"), map);
			} else if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.GOORU_EXTERNALID_CHANGE.getEvent())) {
				map.put("oldEmailId", data.get("oldEmailId"));
				map.put("newMailId", data.get("newMailId"));
				map.put("baseUrl", BaseUtil.changeHttpsProtocol(data.get("baseUrl")));
				sendExternalIdConfirm(data.get("gooruUid"), map);
			} else if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.CHILD_13_CONFIRMATION.getEvent())) {
				map.put("gooruUid", data.get("gooruUid"));
				map.put("parentEmailId", data.get("parentEmailId"));
				map.put("gooruUserName", data.get("gooruUserName"));
				sendChildConfirm(data.get("gooruUid"), map);
			} else if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.STUDENT_SEPARATION_CONFIRMATION.getEvent())) {
				map.put("gooruUid", data.get("gooruUid"));
				map.put("childEmailId", data.get("childEmailId"));
				sendStudentSeparationConfirm(data.get("gooruUid"), map);
			}
		}
	}

	public void sendChildConfirm(String gooruUId, Map<String, Object> map) {
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("recipient", map.get("parentEmailId"));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, identity.getUser().getOrganization().getPartyUid()));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, identity.getUser().getOrganization().getPartyUid()));
		map.put("fromName", FROM);
		sendMailViaRestApi(map);
	}

	public void sendStudentSeparationConfirm(String gooruUId, Map<String, Object> map) {
		final Identity identity = this.getUserRepositoryHibernate().findUserByGooruId(gooruUId);
		map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
		map.put("content", generateMessage((String) map.get("textContent"), map));
		map.put("recipient", map.get("childEmailId"));
		map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, identity.getUser().getOrganization().getPartyUid()));
		map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, identity.getUser().getOrganization().getPartyUid()));
		map.put("fromName", FROM);
		sendMailViaRestApi(map);
	}

	public void sendUserBirthDayMail(Map<String, Object> map) {
		Integer birthdayMailCount = this.getUserRepositoryHibernate().getUserBirthdayCount();
		String currentDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()).toString();
		if (birthdayMailCount != null) {
			Integer maxBatchSize = Integer.parseInt(getConfigSetting(ConfigConstants.GOORU_USER_BIRTHDAY_MAIL_BATCH_SIZE, TaxonomyUtil.GOORU_ORG_UID));

			Integer offset = Math.round(birthdayMailCount / maxBatchSize);
			Integer count = 0;
			for (int index = 0; index <= offset; index++) {
				List<Object[]> results = this.getUserRepositoryHibernate().listUserByBirthDay(index, maxBatchSize);
				count += results.size();
				String emailIds = "";
				String userIds = "";
				for (Object[] result : results) {
					if (!this.getPartyRepository().isUserBirthDayMailSentToday(result[1].toString(), currentDate)) {
						emailIds += emailIds.length() > 0 ? ", " : "";
						emailIds += result[0];
						userIds += userIds.length() > 0 ? ", " : "";
						userIds += "'"+result[1]+"'";
					}
				}
				
				if (userIds != null && userIds.length() != 0) {
					this.getPartyRepository().updatePartyCustomFieldsBirthDayMailKey(userIds);
				}

				if (emailIds.length() > 0) {
					map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
					map.put("content", generateMessage((String) map.get("textContent"), map));
					map.put("recipient", emailIds);
					map.put("sendRecipient", true);
					map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
					map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
					map.put("fromName", FROM);
					sendMailViaRestApi(map);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void sendChildUserBirthDayMail(String eventType) {
		Integer childBirthdayMailCount = this.getUserRepositoryHibernate().getChildUserBirthdayCount();
		if (childBirthdayMailCount != null) {
			List<Object[]> results = this.getUserRepositoryHibernate().listChildUserByBirthDay();
			EventMapping eventMapping = this.getEventService().getTemplatesByEventName(eventType);
			for (Object[] object : results) {
				Map<String, Object> map = eventMapData(eventMapping);
				if (eventMapping != null) {
					String childUserName = object[0] != null ? object[0].toString() : null;
					String parentEmailId = object[1] != null ? object[1].toString() : null;
					if (parentEmailId != null && childUserName != null) {
						map.put("childUserName", childUserName);
						map.put("parentEmailId", parentEmailId);
					}
					map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
					map.put("content", generateMessage((String) map.get("textContent"), map));
					map.put("recipient", map.get("parentEmailId"));
					map.put("sendRecipient", true);
					map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
					map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
					map.put("fromName", FROM);
					sendMailViaRestApi(map);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void sendEmailNotificationforComment(Map<String, String> commentData) {

		final String serverpath = this.getServerConstants().getProperty("serverPath");
		String goToCollectionLink = null;
		String collectionId = commentData.get("collectionId");
		Identity identity = null;
		EventMapping eventMapping = null;

		Map<String, Object> map = new HashMap<String, Object>();
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(collectionId, null);
		if (serverpath != null) {
			goToCollectionLink = "<div style = \" "+ GOORU_BUTTON_CSS +" \"><a style = \" "+ GOORU_COLLECTION_LINK_CSS +" \" href=\"" + serverpath + "/#preview-play&id=" + collectionId + "\"><div style = \" "+ GO_TO_COLLECTION_TEXT +" \">Go to Collection</div></a></div>";
		}
		if (collection != null) {
			if (collection.getUser().getAccountTypeId() != null && collection.getUser().getAccountTypeId().equals(Integer.parseInt("2")) && collection.getUser().getParentUser() != null) {
				identity = this.getUserRepositoryHibernate().findUserByGooruId(collection.getUser().getParentUser().getGooruUId());
				eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.COMMENT_ON_CHILD_COLLECTION.getEvent());
				map = eventMapData(eventMapping);
			} else if (collection.getUser() != null){
				identity = this.getUserRepositoryHibernate().findUserByGooruId(collection.getUser().getGooruUId());
				eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.COMMENT_ON_PARENT_COLLECTION.getEvent());
				map = eventMapData(eventMapping);
			}
			map.put("serverpath", serverpath);
			map.put("comment", commentData.get("commentText"));
			map.put("username", commentData.get("userName"));
			if (goToCollectionLink != null) {
				map.put("goToCollectionLink", goToCollectionLink);
			}
			map.put("collectionName", collection.getTitle());
			map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
			map.put("content", generateMessage((String) map.get("textContent"), map));
			map.put("recipient", identity.getExternalId());
			map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
			map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
			map.put("fromName", FROM);
			sendMailViaRestApi(map);
		}
	}
	
	public void sendMailToInviteCollaborator(Map<String,Object> collaboratorData) {
		
		final String serverpath = this.getServerConstants().getProperty("serverPath");
		Content content = (Content) collaboratorData.get("contentObject");
		if (content != null){
			EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.SEND_MAIL_TO_INVITE_COLLABORATOR.getEvent());
			Map<String, Object> map = new HashMap<String, Object>();
			map = eventMapData(eventMapping);
			map.put("serverpath",serverpath);
			org.ednovo.gooru.core.api.model.Resource resource = this.getResourceRepository().findResourceByContent(content.getGooruOid());
			if(resource != null) {
				map.put("collection-title", resource.getTitle());
			}
			if (content.getUser() != null){
				map.put("username",content.getUser().getUsername() );
			}
			map.put("collection-id", content.getGooruOid());
			
			map.put("recipient", collaboratorData.get("emailId"));
			map.put("htmlContent", generateMessage((String) map.get("htmlContent"), map));
			map.put("content", generateMessage((String) map.get("textContent"), map));
			map.put("from", getConfigSetting(ConfigConstants.MAIL_FROM, TaxonomyUtil.GOORU_ORG_UID));
			map.put("bcc", getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
			map.put("fromName", FROM);
			sendMailViaRestApi(map);
		}
	}
	
	public void sendMailToInviteUser(Map<String,String> inviteData, User user, String title, String gender, String noun,String inviteFrom, String inviteUser) {
		
		final String serverpath = this.getServerConstants().getProperty(SERVERPATH);
			EventMapping eventMapping = this.getEventService().getTemplatesByEventName(CustomProperties.EventMapping.SEND_MAIL_TO_INVITE_USER_CLASS.getEvent());
			Map<String, Object> map = eventMapData(eventMapping);
			map.put("serverpath",serverpath);
			map.put(TITLE, title);
			map.put(TEACHERNAME ,user.getUsername());
			map.put(MEMBERMAILID, inviteData.get(EMAIL_ID));
			map.put(GOORU_OID, inviteData.get(GOORU_OID));
			map.put(GENDER, gender);
			map.put(NOUN, noun);
			map.put(RECIPIENT, inviteData.get(EMAIL_ID));
			map.put(HTMLCONTENT, generateMessage((String) map.get(HTMLCONTENT), map));
			map.put(SUBJECT, "You’re invited to join "+ gender + user.getUsername()+"’s" + " class \""+title+"\"");
			map.put(CONTENT, generateMessage((String) map.get(TEXTCONTENT), map));
			map.put("from", inviteFrom);
			map.put(BCC, getConfigSetting(ConfigConstants.MAIL_BCC_SUPPORT, TaxonomyUtil.GOORU_ORG_UID));
			map.put(FROMNAME, inviteUser);
			sendMailViaRestApi(map);
	}

	public void handleMailEvent(String eventType) {
		EventMapping eventMapping = this.getEventService().getTemplatesByEventName(eventType);
		if (eventMapping != null) {
			Map<String, Object> map = eventMapData(eventMapping);
			/*if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.USER_IN_ACTIVE_MAIL.getEvent())) {
				sendUserInactiveMail(map);
			} else*/ 
			if (eventMapping.getEvent().getDisplayName().equalsIgnoreCase(CustomProperties.EventMapping.USER_BIRTHDAY_MAIL.getEvent())) {
				sendUserBirthDayMail(map);
			}
		}
	}

	private Map<String, Object> eventMapData(EventMapping eventMapping) {
		Map<String, Object> map = null;
		if (eventMapping.getData() != null) {
			map = JsonDeserializer.deserialize(eventMapping.getData(), new TypeReference<Map<String, Object>>() {
			});
		} else {
			map = new HashMap<String, Object>();
		}
		map.put("htmlContent", eventMapping.getTemplate().getHtmlContent());
		map.put("textContent", eventMapping.getTemplate().getTextContent());
		map.put("subject", eventMapping.getTemplate().getSubject());
		map.put("media-url", settingService.getConfigSetting(ConfigConstants.GOORU_MEDIA_END_POINT, 0, TaxonomyUtil.GOORU_ORG_UID));
		return map;
	}

	public EventService getEventService() {
		return eventService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public PartyRepository getPartyRepository() {
		return partyRepository;
	}

	private String getConfigSetting(String key, String organizationUid) {
		return settingService.getConfigSetting(key, organizationUid);
	}

	protected Properties getServerConstants() {
		return serverConstants;
	}

	protected UserRepositoryHibernate getUserRepositoryHibernate() {
		return userRepositoryHibernate;
	}

	protected LearnguideRepositoryHibernate getLearnguideRepositoryHibernate() {
		return learnGuideRepositoryHibernate;
	}
	
	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}
}
