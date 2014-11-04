package org.ednovo.gooru.controllers.v2.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.user.UserService;
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
	private UserService userService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/list")
	public ModelAndView listRoles(
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String data = null;
		Map<String, Object> roles = null;
		if (data == null) {
			roles = new HashMap<String, Object>();
			roles.put(SEARCH_RESULT, this.getUserService().getAllRoles());
			Long roleCount = this.getUserService().allRolesCount();
			roles.put(COUNT, roleCount);
			data = serializeToJson(roles, true);
		}
		return toModelAndView(data);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{userUid}/list")
	public ModelAndView listUserRoles(
			@PathVariable(value = USER_UID) String userUid,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String data = null;
		Map<String, Object> roles = null;
		if (data == null) {
			roles = new HashMap<String, Object>();
			roles.put(SEARCH_RESULT,
					this.getUserService().findUserRoles(userUid));
			Long roleCount = this.getUserService().userRolesCount(userUid);
			roles.put(COUNT, roleCount);
			data = serializeToJson(roles, true);
		}
		return toModelAndView(data);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createRole(
			HttpServletRequest request,
			@RequestBody String data,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {
		UserRole role = JsonDeserializer.deserialize(data, UserRole.class);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		UserRole userRole = this.getUserService().createRole(role.getName(),
				role.getDescription(), apiCaller);
		return toModelAndView(userRole, format);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}/operation")
	public ModelAndView updateRoleOperation(
			HttpServletRequest request,
			@RequestParam(value = OPERATIONS) String operations,
			@PathVariable(ROLE_ID) Integer roleId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {
		List<RoleEntityOperation> roleEntityOperation = this.getUserService()
				.updateRoleOperation(roleId, operations);
		return toModelAndView(roleEntityOperation, format);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}/operation")
	public ModelAndView removeRoleOperation(
			HttpServletRequest request,
			@RequestParam(value = OPERATIONS) String operations,
			@PathVariable(ROLE_ID) Integer roleId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {

		String data = null;
		Map<String, Object> message = null;
		if(data== null){
			message = new HashMap<String, Object>();
			String isDeleted = this.getUserService().removeRoleOperation(roleId,
					operations);
			message.put(RESPONSE, isDeleted);
			data = serializeToJson(message, true);
		}
		return toModelAndView(data);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}")
	public ModelAndView updateRole(
			HttpServletRequest request,
			@RequestBody String data,
			@PathVariable(ROLE_ID) Short roleId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {

		UserRole role = JsonDeserializer.deserialize(data, UserRole.class);
		role.setRoleId(roleId);
		UserRole userRole = this.getUserService().updateRole(role);
		return toModelAndView(userRole, format);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}")
	public ModelAndView removeRole(
			HttpServletRequest request,
			@PathVariable(ROLE_ID) Short roleId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {
		
		String data = null;
		Map<String, Object> message = null;
		if(data== null){
			message = new HashMap<String, Object>();
			String isDeleted = this.getUserService().removeRole(roleId);
			message.put(RESPONSE, isDeleted);
			data = serializeToJson(message, true);
		}
		return toModelAndView(data);
	}

	public UserService getUserService() {
		return userService;
	}
}
