package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.group.UserGroupService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
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


@Controller
@RequestMapping(value = { "/v2/role" })
public class RoleRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserGroupService userGroupService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getRoles(@RequestParam(value = USER_UID, required = false) final String userUid,@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		
		return toModelAndViewWithIoFilter(getUserManagementService().getRoles(offset,limit,userUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ROLE_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createRole(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<UserRole> responseDTO = getUserManagementService().createNewRole(buildRoleFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		return toModelAndViewWithIoFilter(responseDTO.getModelData(),RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true,(String[]) ArrayUtils.addAll(ROLE_INCLUDES, ERROR_INCLUDE));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}/operation")
	public ModelAndView updateRoleOperation(final HttpServletRequest request,@RequestParam(value = OPERATIONS) final String operations,@PathVariable(ROLE_ID) final Integer roleId, final HttpServletResponse response) throws Exception {

		return toModelAndView(this.getUserService().updateRoleOperation(roleId, operations),RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}/operation")
	public void removeRoleOperation(final HttpServletRequest request,@RequestParam(value = OPERATIONS) final String operations,@PathVariable(ROLE_ID) final Integer roleId, final HttpServletResponse response) throws Exception {
		
		this.getUserService().removeRoleOperation(roleId, operations);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateRole(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response,@PathVariable(ROLE_ID) final Integer roleId) throws Exception {

		return toModelAndViewWithIoFilter(this.getUserManagementService().updateRole(buildRoleFromInputParameters(data), roleId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_ROLE_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}")
	public void removeRole(final HttpServletRequest request,@PathVariable(ROLE_ID) final Integer roleId, final HttpServletResponse response) throws Exception {

		this.getUserManagementService().removeRole(roleId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/entity")
	public ModelAndView getEntity(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final Integer limit, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		return toModelAndViewWithIoFilter(this.getUserManagementService().findAllEntityNames(offset,limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ENTITY_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/entity/operations")
	public ModelAndView getEntityOperations(final HttpServletRequest request, final HttpServletResponse response,@RequestParam(value = ENTITY_NAME) final String entityName) throws Exception {

		return toModelAndViewWithIoFilter(this.getUserManagementService().getOperationsByEntityName(entityName), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ENTITY_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/group")
	public ModelAndView getGroupRole(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		
		return toModelAndView(serialize(this.getUserGroupService().findAllGroups(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, (String[]) ArrayUtils.addAll(ERROR_INCLUDE, USER_GROUP_INCLUDES)));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{roleId}")
	public ModelAndView getRoleByRoleId(@PathVariable(ROLE_ID) final Integer roleId, final HttpServletRequest request,	final HttpServletResponse response) throws Exception {
		
		return toModelAndViewWithIoFilter(getUserManagementService().getRoleByRoleId(roleId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_ROLE_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{roleId}/operation")
	public ModelAndView getRoleOperationsByRoleId(@PathVariable(ROLE_ID) final Integer roleId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		
		return toModelAndViewWithIoFilter(getUserManagementService().getRoleOperationsByRoleId(roleId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ROLE_OPERATIONS_INCLUDES);
	}
	
	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public UserService getUserService() {
		return userService;
	}
	
	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	private UserRole buildRoleFromInputParameters(final String data) {
		return JsonDeserializer.deserialize(data, UserRole.class);
	}
}
