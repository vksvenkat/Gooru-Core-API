package org.ednovo.gooru.controllers.v2.api;

import java.util.HashMap;
import java.util.Map;
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
public class RoleRestV2Controller extends BaseController implements
		ParameterProperties, ConstantProperties {

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private UserService userService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listRoles(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> roles = new HashMap<String, Object>();
		roles.put(SEARCH_RESULT, this.getUserManagementService().findAllRoles());
		roles.put(COUNT, this.getUserManagementService().allRolesCount());
		return toModelAndView(serializeToJson(roles, true));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{userUid}")
	public ModelAndView listUserRoles(
			@PathVariable(value = USER_UID) String userUid,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Map<String, Object> roles = new HashMap<String, Object>();
		roles.put(SEARCH_RESULT,
				this.getUserManagementService().findUserRoles(userUid));
		roles.put(COUNT, this.getUserManagementService()
				.userRolesCount(userUid));
		return toModelAndView(serializeToJson(roles, true));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createRole(@RequestBody String data,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<UserRole> responseDTO = getUserManagementService()
				.createNewRole(buildRoleFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		return toModelAndViewWithIoFilter(responseDTO.getModelData(),
				RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true,
				(String[]) ArrayUtils.addAll(ROLE_INCLUDES, ERROR_INCLUDE));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}/operation")
	public ModelAndView updateRoleOperation(HttpServletRequest request,
			@RequestParam(value = OPERATIONS) String operations,
			@PathVariable(ROLE_ID) Integer roleId, HttpServletResponse response)
			throws Exception {
		return toModelAndView(
				this.getUserService().updateRoleOperation(roleId, operations),
				RESPONSE_FORMAT_JSON);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}/operation")
	public ModelAndView removeRoleOperation(HttpServletRequest request,
			@RequestParam(value = OPERATIONS) String operations,
			@PathVariable(ROLE_ID) Integer roleId, HttpServletResponse response)
			throws Exception {
		return toModelAndView(
				this.getUserService().removeRoleOperation(roleId, operations),
				RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateRole(@RequestBody String data,
			HttpServletRequest request, HttpServletResponse response,
			@PathVariable(ROLE_ID) Integer roleId) throws Exception {
		return toModelAndView(
				this.getUserManagementService().updateRole(
						buildRoleFromInputParameters(data), roleId),
				RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}")
	public ModelAndView removeRole(HttpServletRequest request,
			@PathVariable(ROLE_ID) Integer roleId, HttpServletResponse response)
			throws Exception {

		return toModelAndView(this.getUserManagementService()
				.removeRole(roleId), RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/entity")
	public ModelAndView listEntityNames(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> roleOperations = new HashMap<String, Object>();
		roleOperations.put(SEARCH_RESULT, this.getUserManagementService()
				.findAllEntityNames());
		roleOperations.put(COUNT, this.getUserManagementService()
				.allEntityNamesCount());
		return toModelAndView(serializeToJson(roleOperations, true));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/entity/operations")
	public ModelAndView listOperationsByEntityName(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = ENTITY_NAME) String entityName)
			throws Exception {

		Map<String, Object> roleOperations = new HashMap<String, Object>();
		roleOperations.put(SEARCH_RESULT, this.getUserManagementService()
				.getOperationsByEntityName(entityName));
		roleOperations.put(COUNT, this.getUserManagementService()
				.getOperationCountByEntityName(entityName));
		return toModelAndView(serializeToJson(roleOperations, true));
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public UserService getUserService() {
		return userService;
	}

	private UserRole buildRoleFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, UserRole.class);
	}
}
