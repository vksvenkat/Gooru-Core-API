package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.menu.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { "/v2/menu" })
@Controller
public class MenuRestV2Controller extends BaseController implements ConstantProperties{
	
	@Autowired
	private MenuService menuService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APPLICATION_READ })
	@RequestMapping(value = "/{userUid}/list", method = RequestMethod.GET)  
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
   	public ModelAndView getApikey(ModelMap model, HttpServletRequest request, HttpServletResponse response) { 

		String userUid = request.getParameter("userUid");
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getMenuService().getMenuByUserUid(userUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	public MenuService getMenuService() {
		return menuService;
	}

	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}
}
