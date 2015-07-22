/////////////////////////////////////////////////////////////
// SubdomainRestController.java
// rest-app
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
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
package org.ednovo.gooru.controllers.v1.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.subdomain.SubdomainService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { RequestMappingUri.SUBDOMAIN })
public class SubdomainRestController extends BaseController implements ConstantProperties {

	@Autowired
	private SubdomainService subdomainService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SUBDOMAIN_ADD })
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createSubdomain(HttpServletRequest request, HttpServletResponse response, @RequestBody String data)   {
		User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<Subdomain> responseDTO = this.getSubdomainService().createSubdomain(buildSubdomainFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseDTO.getModel().setUri(RequestMappingUri.SUBDOMAIN + RequestMappingUri.SEPARATOR + responseDTO.getModel().getSubdomainId());
		}
		String includes[] = (String[]) ArrayUtils.addAll(CREATE_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SUBDOMAIN_READ })
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getSubdomains(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletResponse response, HttpServletRequest request)   {
		return toModelAndViewWithIoFilter(this.getSubdomainService().getSubdomains(limit, offset), FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SUBDOMAIN_READ })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.GET)
	public ModelAndView getSubdomain(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) Integer SubdomainId)   {
		return toModelAndViewWithIoFilter(this.getSubdomainService().getSubdomain(SubdomainId), FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SUBDOMAIN_DELETE })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.DELETE)
	public void deleteSubdomain(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) Integer subDomaintId)   {
		this.getSubdomainService().deleteSubdomain(subDomaintId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SUBDOMAIN_READ })
	@RequestMapping(value = RequestMappingUri.SUBDOMAIN_STANDARDS, method = RequestMethod.GET)
	public ModelAndView getSubdomainStandards(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) Integer subdomainId)   {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getSubdomainService().getSubdomainStandards(subdomainId, user), FORMAT_JSON, EXCLUDE, true, "*");
	}

	private Subdomain buildSubdomainFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Subdomain.class);
	}

	public SubdomainService getSubdomainService() {
		return subdomainService;
	}
}
