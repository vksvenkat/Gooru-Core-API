package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.group.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/userGroup" })
public class UserGroupRestController extends BaseController implements ConstantProperties {

	@Autowired
	private UserGroupService userGroupService;

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param name
	 *            required - The name of user group.
	 * @param userGroupType
	 *            required - The Type of user group.
	 * @param groupCode
	 *            optional - The group code to use filter
	 * @param userMailIds
	 *            optional - associates the group members to the respective
	 *            group
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param response
	 * @return userGroup in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST })
	public ModelAndView createUserGroup(HttpServletRequest request, @RequestParam(value = NAME, required = true) String name, @RequestParam(value = USER_GROUP_TYPE, required = true) String userGroupType, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = GROUP_CODE, required = false) String groupCode, @RequestParam(value = USER_MAIL_IDS, required = false) String userMailIds, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON, required = false) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE,GROUP_CREATE_GROUP);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		UserGroup userGroup = userGroupService.createGroup(name, groupCode, userGroupType, apiCaller, userMailIds);
		if (userGroup != null) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_ADD);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GROUP_TITLE, name);
		return toModelAndView(serializeToJsonObject(userGroup, true, GROUP_INCLUDES));
	}

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param name
	 *            required - The name of user group.
	 * @param actionType
	 *            optional - The action responds with respect to user request.
	 * @param ownerId
	 *            optional - The owner Id for update its owner.
	 * @param activeFlag
	 *            optional - The activeFlag used for group status
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return userGroup in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = GROUP_UID )
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{groupUid}")
	public ModelAndView updateGroup(@PathVariable String groupUid, @RequestParam(value = NAME, required = false) String name, @RequestParam(value = ACTION_TYPE, required = false) String actionType, @RequestParam(value = OWNER_ID, required = false) String ownerId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = ACTIVE_FLAG, required = false) Boolean activeFlag, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, GROUP_UPDATE_GROUP);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		UserGroup userGroup = userGroupService.updateUserGroup(groupUid, name, actionType, ownerId, activeFlag);
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_UPDATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GROUP_TITLE, name);
		return toModelAndViewWithInFilter(userGroup, format, GROUP_INCLUDES);

	}

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param name
	 *            required - The name of user group.
	 * @param groupUid
	 *            required - The ID of the user group
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return Deleted Successfully in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_DELETE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = GROUP_UID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{groupUid}")
	public ModelAndView removeGroup(HttpServletRequest request, @PathVariable String groupUid, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, GROUP_REMOVE_GROUP);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		String isDeleted = userGroupService.removeUserGroup(groupUid, apiCaller);
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_REMOVE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		return toModelAndView(isDeleted, format);
	}

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param name
	 *            required - The name of user group.
	 * @param groupUid
	 *            required - The ID of the user group.
	 * @param userMailIds
	 *            required - associates the group members to the respective
	 *            group
	 * @param actionType
	 *            required - The action responds with respect to user request.
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return userGroupAssoc in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{groupCode}/user")
	public ModelAndView manageGroupUsers(@PathVariable String groupCode, @RequestParam(value = USER_MAIL_IDS, required = true) String userMailIds, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = ACTION_TYPE, required = true) String actionType,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, GROUP_MANAGE_GROUP_USERS);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<UserGroupAssociation> userGroupAssocList = userGroupService.manageGroupUsers(groupCode, userMailIds, actionType);
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_UPDATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GROUP_TITLE, actionType);
		return toModelAndView(userGroupAssocList, format);

	}

	/**
	 * @param format
	 * 
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return groups in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET })
	public ModelAndView listGroups(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON, required = false) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, GROUP_LIST_GROUP);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<UserGroup> groups = userGroupService.findAllGroups();
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_LIST);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		return toModelAndViewWithInFilter(groups, format, GROUP_INCLUDES);
	}

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param groupUid
	 *            required - The ID of the user group
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return users in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/{groupUid}")
	public ModelAndView getUserGroup(HttpServletRequest request, @PathVariable String groupUid, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, GROUP_USER_GROUP);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<User> users = userGroupService.getUserGroup(groupUid);
		SessionContextSupport.putLogParameter(OPERATION_NAME, GROUP_LIST);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		return toModelAndView(users, format);
	}

	/**
	 * @param format
	 *            Format of the variable. Today, only JSON is supported.
	 * @param contentId
	 *            required - The ID of the Content
	 * @param partyUids
	 *            required - The IDs of the user group(s)
	 * @param sessionToken
	 *            required - Session token of the user
	 * @param request
	 * @param responsecontent
	 * @return permission in the required format.
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_GROUP_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/content/{contentId}/share")
	public ModelAndView contentShare(HttpServletRequest request, @PathVariable String contentId, @RequestParam(value = PARTY_UIDS, required = true) String partyUids, @RequestParam(value = SHARE_OTHER_ORGANIZATION, required = false) Boolean shareOtherOrganization,
			@RequestParam(value = _ORGANIZATION_ID, required = false) String OrganizationId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, GROUP_CONTENT_SHARE);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<ContentPermission> permissionList = userGroupService.contentShare(contentId, apiCaller, partyUids, shareOtherOrganization, OrganizationId);
		SessionContextSupport.putLogParameter(OPERATION_NAME, CONTENT_SHARE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		return toModelAndView(permissionList, format);
	}

}
