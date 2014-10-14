/////////////////////////////////////////////////////////////
// ApplicationRestV2Controller.java
// rest-v2-app
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
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.apikey.ApplicationService;
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
@RequestMapping(value = { "/v2/application" })
public class ApplicationRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private ApplicationService applicationService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APPLICATION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createApplication(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Application> responseDTO = getApplicationService().createApplication(buildApplicationFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APPLICATION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateApplication(@RequestBody String data, HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {
		Application responseDTO = getApplicationService().updateApplication(buildApplicationFromInputParameters(data), id);
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APPLICATION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getApplication(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getApplicationService().getApplication(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APPLICATION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView getApplications(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = ORGANIZATION_UID, required = false) String organizationUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getApplicationService().getApplications(organizationUid, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	private Application buildApplicationFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Application.class);
	}

}
