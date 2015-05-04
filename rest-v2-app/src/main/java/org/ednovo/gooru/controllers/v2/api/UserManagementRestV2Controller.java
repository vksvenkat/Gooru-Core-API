/////////////////////////////////////////////////////////////
//UserManagementRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.io.FileNotFoundException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserTagAssoc;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.FeedbackService;
import org.ednovo.gooru.domain.service.PostService;
import org.ednovo.gooru.domain.service.tag.TagService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;


@Controller
@RequestMapping(value = { "/v2/user" })
public class UserManagementRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private PostService postService;

	@Autowired
	private TagService tagService;

	@Autowired
	private IndexProcessor indexProcessor;
	
	@Autowired 
	private IndexHandler indexHandler;
	
	@Autowired
	@Resource(name = "userService")
	private UserService userService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "")
	public ModelAndView createUser(final HttpServletRequest request, @RequestBody final String data, @RequestParam(value = SESSIONTOKEN, required = false) final String sessionToken, @RequestParam(value = SHARED_SECRETKEY, required = false) final String sharedSecretKey, @RequestParam(value = ORG_ADMIN, required = false, defaultValue="false") final Boolean orgAdmin, @RequestParam(value = ADMIN_ORGANIZATION_UID, required = false) final String adminOrganizationUid, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		final User creator = this.buildUserFromInputParameters((getValue(USER, json)));
		final String sessionId = request.getSession().getId();

		String dateOfBirth = getValue(DATEOFBIRTH, json);
		final User apiCaller = (User) request.getAttribute(Constants.USER);
		if (getValue(CHILDFLAG, json) != null ? Boolean.parseBoolean(getValue(CHILDFLAG, json)) : false) {
			dateOfBirth = dateOfBirth != null ? dateOfBirth.replace("d", "/") : dateOfBirth;
		}

		// Check user organization permission
		if (creator.getOrganization() != null && creator.getOrganization().getOrganizationCode() != null && apiCaller != null && !creator.getOrganization().getOrganizationCode().equalsIgnoreCase(GOORU)) {
			this.getUserManagementService().validateUserOrganization(creator.getOrganization().getOrganizationCode(), sharedSecretKey);
		}

		final User user = this.getUserManagementService().createUserWithValidation(creator, getValue(PASSWORD, json), null, getValue(CONFIRM_STATUS, json) != null ? Integer.parseInt(getValue(CONFIRM_STATUS, json)) : null, getValue(USEGENERATEDPASSWORD, json) != null ? Boolean.parseBoolean(getValue(USEGENERATEDPASSWORD, json)) : false,
				getValue(SENDCONFIRMATIONMAIL, json) != null ? Boolean.parseBoolean(getValue(SENDCONFIRMATIONMAIL, json)) : true, apiCaller, getValue(ACCOUNTTYPE, json), dateOfBirth, getValue(USERPARENTID, json), sessionId, getValue(GENDER, json), getValue(CHILDDOB, json),
				getValue(GOORU_BASE_URL, json), getValue(TOKEN, json) != null ? Boolean.parseBoolean(getValue(TOKEN, json)) : false, request, getValue(ROLE, json), getValue(MAIL_CONFIRMATION_URL, json) != null ? getValue(MAIL_CONFIRMATION_URL, json) : null );
		if (user != null) {
			if(orgAdmin && adminOrganizationUid != null){
				this.getUserManagementService().updateOrgAdminCustomField(adminOrganizationUid, user);
			}
       		response.setStatus(HttpServletResponse.SC_CREATED);
			indexHandler.setReIndexRequest(user.getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);					
		}

		return toModelAndViewWithIoFilter(user, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateUser(@RequestBody String data, @PathVariable(value = ID) String gooruUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final User apicaller = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		Boolean emailConfirmStatus = false;

		if (getValue(EMAIL_CONFIRM_STATUS, json) != null && getValue(EMAIL_CONFIRM_STATUS, json).equalsIgnoreCase(TRUE)) {
			emailConfirmStatus = true;
		}
		final Profile profile = this.getUserManagementService().updateProfileInfo(getValue(PROFILE, json) != null ? this.buildProfileFromInputParameters(getValue(PROFILE, json)) : null, gooruUid, apicaller, getValue(USER_META_ACTIVE_FLAG, json), emailConfirmStatus, getValue(_SHOW_PROFILE_PAGE, json),getValue(ACCOUNTTYPE, json),getValue(PASSWORD, json));

		return toModelAndView(serialize(profile, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_PROFILE_INCUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getUser(@PathVariable(value = ID) final String userId, @RequestParam(value = USER_META_ACTIVE_FLAG, required = false) final Integer activeFlag, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		return toModelAndViewWithIoFilter(getUserManagementService().getUserProfile(userId, activeFlag), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_PROFILE_INCUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_CONFIRM_MAIL })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/sendmail/{id}")
	public ModelAndView resendConfirmationMail(@PathVariable(value = ID) final String gooruUid, @RequestBody final String data , final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User apicaller = (User) request.getAttribute(Constants.USER);
		final String sessionId = request.getSession().getId();
		final JSONObject json = requestData(data);
		return toModelAndViewWithIoFilter(getUserManagementService().resendConfirmationMail(gooruUid,apicaller,sessionId,getValue(GOORU_BASE_URL, json),getValue(TYPE, json) != null ? getValue(TYPE, json) : CONFIRMATION), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CHECK_IF_USER_EXISTS })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/{type}/availability")
	public ModelAndView getUserAvailability(final HttpServletRequest request, @RequestParam(value = KEYWORD) final String keyword, @RequestParam(value = COLLECTION_ID, required = false) final String collectionId,@RequestParam(value = IS_COLLABORATOR_CHK, defaultValue = FALSE, required = false) final boolean isCollaboratorCheck ,@PathVariable(TYPE) final String type, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_CHECK_USERNAMEOREMAILID_AVAILABILITY);
		final User user = (User) request.getAttribute(Constants.USER);

		return toModelAndViewWithIoFilter(this.getUserService().getUserAvailability(keyword, type.equals(USER_NAME) ? CheckUser.BYUSERNAME.getCheckUser() : type.equals(EMAIL_ID) ? CheckUser.BYEMAILID.getCheckUser() : null, isCollaboratorCheck, collectionId, user),RESPONSE_FORMAT_JSON,EXCLUDE_ALL,true,AVAILABILITY_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE_PASSWORD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/reset-password/request")
	public ModelAndView forgotPassword(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FORGET_PASSWORD);
		User apicaller = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		User user = this.getUserManagementService().resetPasswordRequest(getValue(EMAIL_ID, json), getValue(GOORU_BASE_URL, json), apicaller,getValue(MAIL_CONFIRMATION_URL, json) != null ? getValue(MAIL_CONFIRMATION_URL, json) : null);

		return toModelAndViewWithIoFilter(user, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, RESET_PASSWORD_REQUEST_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE_PASSWORD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/reset-password")
	public ModelAndView resetCredential(final HttpServletRequest request, @RequestBody final String data, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE,RESET_CREDENTIAL);
		final User apicaller = (User) request.getAttribute(Constants.USER);
		final JSONObject json = requestData(data);
		final Identity identity = this.getUserManagementService().resetCredential(getValue(TOKEN, json), getValue(GOORU_UID, json), getValue(PASSWORD, json), apicaller, getValue(MAIL_CONFIRMATION_URL, json) != null ? getValue(MAIL_CONFIRMATION_URL, json) : null,  getValue(IS_PARTNER_PORTAL, json) != null ? Boolean.parseBoolean(getValue(IS_PARTNER_PORTAL, json)) : false);
		final String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, RESET_PASSWORD_INCLUDES);
		if (identity != null) {
			indexHandler.setReIndexRequest(identity.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);		
		}
		return toModelAndViewWithIoFilter(identity, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SESSION_CHECK })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/check-reset-token")
	public ModelAndView checkResetToken(final HttpServletRequest request, @RequestParam final String resetToken, final HttpServletResponse response) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		if(resetToken != null){
			if (this.getUserService().hasResetTokenValid(resetToken)) {
				jsonmodel.addObject(MODEL, jsonObj.put(IS_VALID_TOKEN, FALSE));
			} else {
				jsonmodel.addObject(MODEL, jsonObj.put(IS_VALID_TOKEN, TRUE));
			}
		} else {
			throw new FileNotFoundException("token not found!!!");
		}
		return jsonmodel;
	}
	
	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/post", "/{id}/review", "/{id}/response", "/{id}/question-board", "/{id}/note" })
	public ModelAndView getUserPosts(final HttpServletRequest request, @PathVariable(value = ID) final String gooruUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") final Integer limit,
			final HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(this.getPostService().getUserPosts(gooruUid, limit, offset, getPostType(request)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COMMENT_INCLUDES);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating/{type}", "/{id}/report/{type}", "/{id}/flag/{type}", "/{id}/reaction/{type}" })
	public ModelAndView getUserFeedbacks(final HttpServletRequest request, @PathVariable(value = ID) final String assocUserUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") final Integer limit,
			@RequestParam(value = CREATOR_UID, required = false) final String creatorUid, @PathVariable(value = TYPE) final String feedbackType, final HttpServletResponse response) throws Exception {
		final String includes[] = (String[]) ArrayUtils.addAll(FEEDBACK_INCLUDE_FIELDS, ERROR_INCLUDE);
		if (feedbackType.equalsIgnoreCase(AVERAGE)) {
			return toJsonModelAndView(this.getFeedbackService().getUserFeedbackAverage(assocUserUid, getFeedbackCategory(request)), true);
		}
		return toModelAndViewWithIoFilter(this.getFeedbackService().getUserFeedbacks(getFeedbackCategory(request), feedbackType, assocUserUid, creatorUid, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating", "/{id}/report", "/{id}/flag", "/{id}/reaction" })
	public ModelAndView getUserFeedbackByCategory(final HttpServletRequest request, @PathVariable(value = ID) final String assocUserUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") final Integer limit, @RequestParam(value = CREATOR_UID, required = false) final String creatorUid, final HttpServletResponse response) throws Exception {
		final String includes[] = (String[]) ArrayUtils.addAll(FEEDBACK_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getFeedbackService().getUserFeedbacks(getFeedbackCategory(request), null, assocUserUid, creatorUid, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/{id}/tag/{tid}")
	public ModelAndView createUserTagAssoc(@PathVariable(value = ID) final String gooruUid, @PathVariable(value = TID) final String tagGooruOid, final HttpServletRequest request, final HttpServletResponse response) {
		final UserTagAssoc userTagAssoc = this.getTagService().createUserTagAssoc(gooruUid, tagGooruOid);
		final String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, USER_ASSOC_INCLUDES);
		if (userTagAssoc != null) {
			indexHandler.setReIndexRequest(userTagAssoc.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);					
		}
		return toModelAndViewWithIoFilter(userTagAssoc, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating/{type}/count", "/{id}/report/{type}/count", "/{id}/flag/{type}/count", "/{id}/reaction/{type}/count" })
	public ModelAndView getUserFeeback(final HttpServletRequest request, @PathVariable(value = ID) final String assocUserUid, @PathVariable(value = TYPE) final String type, final HttpServletResponse response) throws Exception {
		final String category = getFeedbackCategory(request);
		if (category.contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && type.contains(CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType())) {
			return toJsonModelAndView(this.getFeedbackService().getUserFeedbackStarRating(assocUserUid), true);
		} else if (category.contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && type.contains(CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType())) {
			return toJsonModelAndView(this.getFeedbackService().getUserFeedbackThumbRating(assocUserUid), true);
		} else {
			return toJsonModelAndView(this.getFeedbackService().getUserFeedbackAggregateByType(assocUserUid, type), true);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{id}/tag/{tid}")
	public void deleteUserTagAssoc(@PathVariable(value = ID) final String gooruOid, @PathVariable(value = TID) final String tagGooruOid, final HttpServletRequest request, final HttpServletResponse response) {
		this.getTagService().deleteUserTagAssoc(gooruOid, tagGooruOid);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/{id}/tag")
	public ModelAndView getContentTagAssoc(@PathVariable(value = ID) final String gooruUid, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, final HttpServletRequest request,
			 final HttpServletResponse response) {
		final List<UserTagAssoc> userTagAssoc = this.getTagService().getUserTagAssoc( gooruUid, limit, offset);
		final String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, USER_ASSOC_INCLUDES);
		return toModelAndViewWithIoFilter(userTagAssoc, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{id}/meta")
	public void updateUserMeta(@PathVariable(value = ID) final String gooruUid, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		final User apicaller = (User) request.getAttribute(Constants.USER);
		this.getUserManagementService().deleteUserMeta(gooruUid, this.buildProfileFromInputParameters(data), apicaller);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{id}")
	public void deleteSoftUser(@PathVariable(value = ID) final String gooruUid, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_DELETE_USER);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		this.getUserManagementService().deleteUserContent(gooruUid, getValue(IS_DELETED, json), apiCaller);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/profile/picture")
	public void updateProfilePicture(@PathVariable(value = ID) final String userId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_DELETE_PROFILE_PICTURE);
		this.getUserManagementService().deleteUserImageProfile(userId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/token/{userToken}")
	public ModelAndView getUserByToken(@PathVariable(value = USER_TOKEN) final String userToken, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndView(serialize(this.getUserManagementService().getUserByToken(userToken), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_PROFILE_INCUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/content/{id}/access")
	public ModelAndView checkContentAccess(final HttpServletRequest request, @PathVariable(value = ID) final String gooruContentId, final HttpServletResponse response) throws Exception {

		final User authenticatedUser = (User) request.getAttribute(Constants.USER);
		SessionContextSupport.putLogParameter(EVENT_NAME, CHECK_CONTENT_PERMISSION);
		SessionContextSupport.putLogParameter(GOORU_CONTENT_ID, gooruContentId);
		SessionContextSupport.putLogParameter(USER_ID, authenticatedUser.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, authenticatedUser.getPartyUid());
		return toJsonModelAndView(this.getUserManagementService().checkContentAccess(authenticatedUser, gooruContentId), true);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/follow/{id}")
	public ModelAndView followUser(@PathVariable(value = ID) final String followOnUserId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOW);
		final User user = (User) request.getAttribute(Constants.USER);
		final String[] includes = (String[]) ArrayUtils.addAll(FOLLOW_USER_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getUserManagementService().followUser(user, followOnUserId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/unfollow/{id}")
	public void unFollowUser(@PathVariable(value = ID) final String unFollowUserId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User apiCaller = (User) request.getAttribute(Constants.USER);
		this.getUserManagementService().unFollowUser(apiCaller, unFollowUserId);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/followers")
	public ModelAndView getFollowedByUsers(@PathVariable(value = ID) final String gooruUserId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit,
		  final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOWERS_LIST);
		final String[] includes = (String[]) ArrayUtils.addAll(FOLLOWED_BY_USERS_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getUserManagementService().getFollowedByUsers(gooruUserId,offset,limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/following")
	public ModelAndView getFollowedOnUsers(@PathVariable(value = ID) final String gooruUserId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOWING_LIST);
		final String[] includes = (String[]) ArrayUtils.addAll(FOLLOWED_BY_USERS_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getUserManagementService().getFollowedOnUsers(gooruUserId,offset,limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/isfollow")
	public ModelAndView isAlredayFollowed(@PathVariable(value = ID) final String gooruUserId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		
		final User apiCaller = (User) request.getAttribute(Constants.USER);
		final ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		return jsonmodel.addObject(MODEL, this.getUserManagementService().isFollowedUser(gooruUserId, apiCaller));
	}
	
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = GOORU_UID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{id}/view-flag")
	public ModelAndView updateUserViewFlag(@PathVariable(value = ID) final String gooruUId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		return toModelAndView(serializeToJsonWithExcludes(this.getUserManagementService().updateUserViewFlagStatus(gooruUId,Integer.parseInt(getValue(VIEW_FLAG,json))), USER_FLAG_EXCLUDE_FIELDS));
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_EMAIL_RESET })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "reset/account")
	public void resetEmailAddress(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		this.getUserManagementService().resetEmailAddress(JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{userUid}/role")
	public ModelAndView assignRoleByUserUid(final HttpServletRequest request,final HttpServletResponse response,@PathVariable(USER_UID) final String userUid, @RequestBody String data) throws Exception {

		return toModelAndViewWithIoFilter(this.getUserManagementService().assignRoleByUserUid(this.buildRoleFromInputParameters(data).getRoleId(), userUid), RESPONSE_FORMAT_JSON,EXCLUDE_ALL, true, (String[]) ArrayUtils.addAll(USER_ROLE_ASSOC_INCLUDES, ERROR_INCLUDE));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = {RequestMethod.DELETE, RequestMethod.PUT}, value = "/{userUid}/role")
	public void removeAssignedRoleByUserUid(final HttpServletRequest request,final HttpServletResponse response,@PathVariable(USER_UID) final String userUid, @RequestBody final String data) throws Exception {

		this.getUserManagementService().removeAssignedRoleByUserUid(this.buildRoleFromInputParameters(data).getRoleId(), userUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private String getFeedbackCategory(final HttpServletRequest request) {
		String category = null;
		if (request != null && request.getRequestURL() != null) {
			if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.RATING.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.FLAG.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.FLAG.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.REACTION.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.REACTION.getFeedbackCategory();
			}
		}
		ServerValidationUtils.rejectIfNull(category, GL0007, REQUEST_PATH);
		return category;
	}

	private String getPostType(final HttpServletRequest request) {
		if (request.getPathInfo() != null) {
			final String path = request.getPathInfo();
			return path.substring(path.lastIndexOf('/') + 1);
		}
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/category")
	public ModelAndView getUserCategory( final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User apiCaller = (User) request.getAttribute(Constants.USER);
		final String includesDefault[] = (String[]) ArrayUtils.addAll(CUSTOM_VALUE_INCLUDE_FIELDS, CUSTOM_VALUE_INCLUDE);
		final String[] includes = (String[]) ArrayUtils.addAll(includesDefault, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getUserManagementService().getUserCategory(apiCaller), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	
	}
	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_CONFIRM_MAIL })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/bulkuser")
	public ModelAndView createBulkUser(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final JSONObject json = requestData(data);
		String path = getValue(PATH, json);
		this.getUserManagementService().createBulkUser(path);
		return null;
	}
	
	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public PostService getPostService() {
		return postService;
	}

	public TagService getTagService() {
		return tagService;
	}

	private Profile buildProfileFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, Profile.class);
	}

	private User buildUserFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, User.class);
	}

	public UserService getUserService() {
		return userService;
	}
	
	private UserRole buildRoleFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, UserRole.class);
	}
}
