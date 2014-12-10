package org.ednovo.gooru.controllers.v2.api;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.Role;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.menu.MenuService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { "/v2/menu" })
@Controller
public class MenuRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {
	
	@Autowired
	private MenuService menuService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createMenu(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {

		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Menu> responseDTO = getMenuService().createMenu(buildMenuFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(MENU_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_ITEM_INCLUDES);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateMenu(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {
		
		User user = (User) request.getAttribute(Constants.USER);
		Menu responseDTO = getMenuService().updateMenu(buildMenuFromInputParameters(data),user, id);
		String includes[] = (String[]) ArrayUtils.addAll(MENU_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_ITEM_INCLUDES);
		return toModelAndViewWithIoFilter(responseDTO, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "item/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateMenuItem(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {

		User user = (User) request.getAttribute(Constants.USER);
		MenuItem responseDTO = getMenuService().updateMenuItem(buildMenuItemFromInputParameters(data), id, user);
		String includes[] = (String[]) ArrayUtils.addAll(MENU_ITEM_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@RequestMapping(value = "/{id}",method = RequestMethod.GET)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getMenu(@PathVariable(value = ID) String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    
		String includes[] = (String[]) ArrayUtils.addAll(MENU_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_ITEM_INCLUDES);
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuById(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/item" }, method = RequestMethod.GET)
	public ModelAndView getMenuItems(@PathVariable(value = ID) String menuUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = ORDER_BY, defaultValue = DESC, required = false) String orderBy, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(MENU_ITEM_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_INCLUDES);
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuItems(menuUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/item/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getMenuItem(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(MENU_ITEM_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_INCLUDES);
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuItemById(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@RequestMapping(value = "", method = RequestMethod.GET)  
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
   	public ModelAndView getMenuByUserRole(@RequestParam (value = CHILDFLAG , required = false,defaultValue= "true")Boolean childFlag, ModelMap model, HttpServletRequest request, HttpServletResponse response) { 
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(MENU_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_ITEM_INCLUDES);
		
		return toModelAndViewWithIoFilter(this.getMenuService().getMenus(user,childFlag), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{menuUid}/role")
	public ModelAndView assignRoleByMenuUid(HttpServletRequest request,HttpServletResponse response,@PathVariable(MENU_UID) String menuUid, @RequestBody String data)throws Exception {

		return toModelAndViewWithIoFilter(getMenuService().assignRoleByMenuUid(this.buildRoleFromInputParameters(data).getRoleId(), menuUid), RESPONSE_FORMAT_JSON,EXCLUDE_ALL, true, (String[]) ArrayUtils.addAll(MENU_ROLE_ASSOC_INCLUDES,ERROR_INCLUDE));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{menuUid}/role")
	public void removeAssignedRoleByMenuUid(HttpServletRequest request,HttpServletResponse response,@PathVariable(MENU_UID) String menuUid, @RequestBody String data)throws Exception {
		
		this.getMenuService().removeAssignedRoleByMenuUid(this.buildRoleFromInputParameters(data).getRoleId(), menuUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteMenu(@PathVariable(value = ID) String menuUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		this.getMenuService().deleteMenu(menuUid,MENU);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/item/{id}")
	public void deleteMenuItem(@PathVariable(value = ID) String menuItemUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		this.getMenuService().deleteMenu(menuItemUid,ITEM);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	private Role buildRoleFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Role.class);
	}
	
	private Menu buildMenuFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Menu.class);
	}
	
	private MenuItem buildMenuItemFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, MenuItem.class);
	}

	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}
}
