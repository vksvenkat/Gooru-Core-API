package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = { "/v2/configSetting" })
public class ConfigSettingRestV2Controller extends BaseController {

	@Autowired
	private SettingService settingService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CONFIG_SETTINGS_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT)
	public void updateSettings(HttpServletRequest request,@RequestParam(value="organizationUid",required = true) String organizationUid,@RequestParam(value="name",required = true) String key,@RequestParam(value="value",required = true) String value, HttpServletResponse response) throws Exception {
		getSettingService().updateConfigSettingValue(organizationUid, key, value);
	}
	
	public SettingService getSettingService() {
		return settingService;
	}
}
