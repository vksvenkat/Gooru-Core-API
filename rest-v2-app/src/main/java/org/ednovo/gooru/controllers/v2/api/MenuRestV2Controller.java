package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
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
public class MenuRestV2Controller extends BaseController implements ConstantProperties{
	
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
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuItems(menuUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/item/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getMenuItem(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(MENU_ITEM_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuItemById(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MENU_READ })
	@RequestMapping(value = "", method = RequestMethod.GET)  
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
   	public ModelAndView getMenuByUserRole(ModelMap model, HttpServletRequest request, HttpServletResponse response) { 
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(MENU_INCLUDES, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, MENU_ITEM_INCLUDES);
		
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuByUserUid(user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

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
