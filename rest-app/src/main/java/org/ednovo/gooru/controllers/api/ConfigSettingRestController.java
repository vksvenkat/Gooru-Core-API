package org.ednovo.gooru.controllers.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Controller
@RequestMapping(value = { "/configSetting", "" })
public class ConfigSettingRestController extends BaseController {

	@Autowired
	private SettingService settingService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CONFIG_SETTINGS_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/config-settings")
	public ModelAndView getConfigSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		XStream stream = new XStream(new DomDriver());
		Map<String, String> configSettings = settingService.getConfigSettings(apiCaller.getOrganization().getPartyUid());
		jsonmodel.addObject(MODEL, stream.toXML(configSettings));
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CONFIG_SETTINGS_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/config-setting/{key}/value")
	public ModelAndView getConfigSetting(HttpServletRequest request, HttpServletResponse response, @PathVariable(KEY) String key, @RequestParam(value = FORMAT, required = false) String format) throws Exception {

		if (key.equals(ConfigConstants.MAIL_FROM) || key.equals(ConfigConstants.MAIL_PASSWORD) || key.equals(ConfigConstants.MAIL_USERNAME) || key.equals(ConfigConstants.PUBLISHER)) {
			throw new AccessDeniedException("Permission denied to fetch the value of key : " + key);
		}
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		XStream stream = new XStream(new DomDriver());
		if (format == null) {
			jsonmodel.addObject(MODEL, stream.toXML(settingService.getConfigSetting(key, apiCaller.getOrganization().getPartyUid())));
		} else {
			jsonmodel.addObject(MODEL, settingService.getConfigSetting(key, apiCaller.getOrganization().getPartyUid()));
		}
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CONFIG_SETTINGS_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/config-settings/reset")
	public void resetMailHandlerConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
		settingService.resetConfigSettings();
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CONFIG_SETTINGS_UPDATE })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value="/{key}")
	public void updateConfigSettingValues(HttpServletRequest request, HttpServletResponse response, @PathVariable String key, @RequestParam (value=CONFIG_VALUE) String configValue) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		settingService.updateConfigSettingValue(apiCaller.getOrganization().getPartyUid(), key, configValue);
	}

}
