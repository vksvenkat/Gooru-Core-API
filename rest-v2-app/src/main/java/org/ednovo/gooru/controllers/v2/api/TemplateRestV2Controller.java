/////////////////////////////////////////////////////////////
//TemplateRestV2Controller.java
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
import org.ednovo.gooru.core.api.model.Template;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.TemplateService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/template" })
public class TemplateRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private TemplateService templateService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TEMPLATE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "")
	public ModelAndView createTemplate(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Template template = getTemplateService().createTemplate(this.buildTemplateFromInputParameters(data), user);
		response.setStatus(HttpServletResponse.SC_CREATED);
		String includes[] = (String[]) ArrayUtils.addAll(TEMPLATES_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(template, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TEMPLATE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{id}")
	public ModelAndView updateTemplate(@PathVariable(value = ID) String id, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Template template = getTemplateService().updateTemplate(id, this.buildTemplateFromInputParameters(data));
		String includes[] = (String[]) ArrayUtils.addAll(TEMPLATES_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(template, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TEMPLATE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getTemplate(@PathVariable(value = ID) String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(TEMPLATES_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getTemplateService().getTemplate(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TEMPLATE_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "")
	public ModelAndView getTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(TEMPLATES_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getTemplateService().getTemplates(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	private Template buildTemplateFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Template.class);
	}

	public TemplateService getTemplateService() {
		return templateService;
	}
}
