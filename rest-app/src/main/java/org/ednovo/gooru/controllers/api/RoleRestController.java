package org.ednovo.gooru.controllers.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.user.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
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
@RequestMapping(value = { "/role" })
public class RoleRestController extends BaseController {

	@Autowired
	private UserService userService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listRoles(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ROLE_LIST_ROLE);
		List<UserRole> roles = userService.findAllRoles();
		// roles.
		JSONObject json = new JSONObject();

		JSONArray roleList = new JSONArray();

		for (UserRole userRole : roles) {

			roleList.put(serializeToJsonObject(userRole));

		}
		json.put("roleList", roleList);

		return toModelAndView(json);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createRole(HttpServletRequest request, @RequestParam(value = NAME) String name, @RequestParam(value = DESCRIPTION) String description, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ROLE_CREATE_ROLE);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		UserRole userRole = userService.createRole(name, description, apiCaller);
		return toModelAndView(userRole, format);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{roleId}/operation")
	public ModelAndView updateRoleOperation(HttpServletRequest request, @RequestParam(value = OPERATIONS) String operations, @PathVariable(ROLE_ID) Integer roleId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ROLE_UPDATE_ROLE_OPERATIONS);
		List<RoleEntityOperation> roleEntityOperation = userService.updateRoleOperation(roleId, operations);
		return toModelAndView(roleEntityOperation, format);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ROLE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}/operation")
	public void  removeRoleOperation(HttpServletRequest request, @RequestParam(value = OPERATIONS) String operations, @PathVariable(ROLE_ID) Integer roleId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ROLE_REMOVE_ROLE_OPERATIONS);

		userService.removeRoleOperation(roleId, operations);

	}

}
