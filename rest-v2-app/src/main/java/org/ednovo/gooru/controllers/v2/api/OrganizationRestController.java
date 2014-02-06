package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationSetting;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
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
@RequestMapping(value = { "/organization" })
public class OrganizationRestController extends BaseController implements ConstantProperties {

	@Autowired
	private OrganizationService organizationService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{accountUid}")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganization(HttpServletRequest request, HttpServletResponse response, @PathVariable(_ACCOUNT_UID) String accountUid) throws Exception {

		Organization account = getOrganizationService().getOrganizationById(accountUid);
		return toModelAndViewWithInFilter(account, FORMAT_JSON, ORGANIZATION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/account/list")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganizations(HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Organization> accounts = getOrganizationService().listAllOrganizations();
		return toModelAndViewWithInFilter(accounts, FORMAT_JSON, ORGANIZATION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Organization> responseDTO  = getOrganizationService().saveOrganization(buildOrganizationFromInputParameters(getValue(ORGANIZATION, json)), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(ORGANIZATION_INCLUDES_ADD, ERROR_INCLUDE);

		return toModelAndViewWithInFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value="/{organizationUid}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody String data, @PathVariable String organizationUid) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Organization> responseDTO  = getOrganizationService().updateOrganization(buildOrganizationFromInputParameters(getValue(ORGANIZATION, json)), organizationUid, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		String includes[] = (String[]) ArrayUtils.addAll(ORGANIZATION_INCLUDES_ADD, ERROR_INCLUDE);
		return toModelAndViewWithInFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value="{organizationUid}/setting")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateOrganizationSettings(HttpServletRequest request, HttpServletResponse response, @PathVariable String organizationUid, @RequestBody String data) throws Exception {
		JSONObject json = requestData(data);
		ActionResponseDTO<OrganizationSetting> responseDTO = getOrganizationService().saveOrUpdateOrganizationSetting(organizationUid, buildOrganizationSettingFromInputParameters(getValue(ORGANIZATION_SETTINGS, json)));
		return toModelAndViewWithInFilter(responseDTO.getModel(), RESPONSE_FORMAT_JSON, ORGANIZATION_SETTING_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value="/{organizationUid}/admin/permission")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateUserOrganization(HttpServletRequest request, HttpServletResponse response, @PathVariable String organizationUid, @RequestParam String gooruUid) throws Exception {
		User user = getOrganizationService().updateUserOrganization(organizationUid, gooruUid);
		return toModelAndViewWithInFilter(user, RESPONSE_FORMAT_JSON, USER_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_READ })
	@RequestMapping(method = RequestMethod.GET, value="{organizationUid}/setting")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganizationSettings(HttpServletRequest request, HttpServletResponse response, @PathVariable String organizationUid, @RequestParam String key) throws Exception {
		return toModelAndViewWithInFilter(getOrganizationService().getOrganizationSetting(organizationUid, key), RESPONSE_FORMAT_JSON, ORGANIZATION_SETTING_INCLUDE);
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	private Organization buildOrganizationFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Organization.class);
	}
	private OrganizationSetting buildOrganizationSettingFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, OrganizationSetting.class);
	}


}
