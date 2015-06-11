package org.ednovo.gooru.controllers.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.CustomSetting;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.customsetting.CustomSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/customSetting" })
public class CustomSettingRestController extends BaseController implements ConstantProperties {

	@Autowired
	private CustomSettingService customSettingService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CUSTOM_SETTING_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createCustomSetting(HttpServletRequest request, @RequestParam(value = KEY, required = true) String key, @RequestParam(value = VALUE, required = true) Boolean value, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = JSON, required = false) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, CREATE_CUSTOM_SETTING);
		CustomSetting customSetting = customSettingService.createCustomSetting(key, value);
		return toModelAndViewWithInFilter(customSetting, format, CUSTOM_SETTING_INCLUDES);
	}

}
