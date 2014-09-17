/////////////////////////////////////////////////////////////
//OganizationRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////

package org.ednovo.gooru.controllers.v2.api;

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
@RequestMapping(value = { "/v2/organization" })
public class OrganizationRestV2Controller extends BaseController implements ConstantProperties {
	
	@Autowired
	private OrganizationService organizationService;

	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{organizationUid}")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganization(HttpServletRequest request, HttpServletResponse response, @PathVariable(_ORGANIZATION_UID) String accountUid) throws Exception {
		Organization account = getOrganizationService().getOrganizationById(accountUid);
		return toModelAndViewWithIoFilter(account, FORMAT_JSON, EXCLUDE_ALL, true, ORGANIZATION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/account/list")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganizations(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, 
			@RequestParam (value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit) throws Exception {
		return toModelAndViewWithIoFilter(getOrganizationService().listAllOrganizations( offset, limit), FORMAT_JSON, EXCLUDE_ALL, true, ORGANIZATION_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Organization> responseDTO  = getOrganizationService().saveOrganization(buildOrganizationFromInputParameters(getValue(ORGANIZATION, json)), user, request);
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
		return toModelAndViewWithIoFilter(responseDTO.getModel(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ORGANIZATION_SETTING_INCLUDE);
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
		return toModelAndViewWithIoFilter(getOrganizationService().getOrganizationSetting(organizationUid, key), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, ORGANIZATION_SETTING_INCLUDE);
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
