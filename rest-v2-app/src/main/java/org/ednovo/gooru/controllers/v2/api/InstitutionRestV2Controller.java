/////////////////////////////////////////////////////////////
//AccountRestV2Controller.java
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

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.authentication.AccountService;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
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
@RequestMapping(value = { "/v2/school", "/v2/school-district" })
public class InstitutionRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	@Resource(name = "serverConstants")
	private Properties serverConstants;
	
	@Autowired
	private OrganizationService organizationService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Organization> responseDTO = getOrganizationService().saveOrganization(buildOrganizationFromInputParameters(data, request), user, request);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(INSTITUTION_INCLUDES_ADD, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody String data, @PathVariable String id) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Organization> responseDTO = getOrganizationService().updateOrganization(buildOrganizationFromInputParameters(data, request), id, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		String includes[] = (String[]) ArrayUtils.addAll(INSTITUTION_INCLUDES_ADD, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.GET, value = "")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganization(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(INSTITUTION_INCLUDES_ADD, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getOrganizationService().getOrganizations(getInstitutionType(request), null, null, offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/{type}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getOrganizationByParent(HttpServletRequest request, HttpServletResponse response, @PathVariable String type, @PathVariable String id, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(INSTITUTION_INCLUDES_ADD, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getOrganizationService().getOrganizations(type, id, null, offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ORGANIZATION_UPDATE })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/user")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getUserByOrganization(HttpServletRequest request, HttpServletResponse response, @PathVariable String id, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(INSTITUTION_INCLUDES_ADD, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, USER_INCLUDES);
		return toModelAndViewWithIoFilter(this.getOrganizationService().getUsersByOrganization(getInstitutionType(request), id, id, offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	private Organization buildOrganizationFromInputParameters(String data, HttpServletRequest request) {
		Organization organization = JsonDeserializer.deserialize(data, Organization.class);
		if (getInstitutionType(request) != null) {
			CustomTableValue type = new CustomTableValue();
			type.setValue(getInstitutionType(request));
			organization.setType(type);
		}
		return organization;
	}

	private String getInstitutionType(HttpServletRequest request) {
		String type = null;
		if (request != null && request.getRequestURL() != null) {
			if (request.getRequestURL().toString().contains(CustomProperties.InstitutionType.DISTRICT.getInstitutionType())) {
				type = CustomProperties.InstitutionType.SCHOOL_DISTRICT.getInstitutionType();
			} else if (request.getRequestURL().toString().contains(CustomProperties.InstitutionType.SCHOOL.getInstitutionType())) {
				type = CustomProperties.InstitutionType.SCHOOL.getInstitutionType();
			}
		}
		return type;
	}
}
