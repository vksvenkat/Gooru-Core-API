/////////////////////////////////////////////////////////////
//DomainRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearningorg/
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
package org.ednovo.gooru.controllers.v1.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.DomainService;
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
@RequestMapping(value = { RequestMappingUri.DOMAIN })
public class DomainRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	public DomainService domainService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_ADD })
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createDomain(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Domain> responseDTO = getDomainService().createDomain(buildDomainFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseDTO.getModel().setUri(RequestMappingUri.DOMAIN + RequestMappingUri.SEPARATOR + responseDTO.getModel().getDomainId());
		}
		String includes[] = (String[]) ArrayUtils.addAll(CREATE_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_UPDATE })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public void updateDomain(@PathVariable(value = ID) Integer domainId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getDomainService().updateDomain(domainId, buildDomainFromInputParameters(data));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_READ })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getDomain(@PathVariable(value = ID) Integer DomainId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getDomainService().getDomain(DomainId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, DOMAIN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_READ })
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getDomains(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getDomainService().getDomains(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, DOMAIN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_DELETE })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteDomain(@PathVariable(value = ID) Integer DomainId, HttpServletRequest request, HttpServletResponse response) {
		getDomainService().deleteDomain(DomainId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	public DomainService getDomainService() {
		return domainService;
	}

	private Domain buildDomainFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Domain.class);
	}
}