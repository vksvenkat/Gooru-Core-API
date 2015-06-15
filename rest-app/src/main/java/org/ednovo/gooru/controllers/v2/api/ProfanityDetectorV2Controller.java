/////////////////////////////////////////////////////////////
//ProfanityDetectorV2Controller.java
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

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.Profanity;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.ProfanityCheckService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/v2/profanity")
public class ProfanityDetectorV2Controller extends SerializerUtil implements ParameterProperties, ConstantProperties {

	@Autowired
	private ProfanityCheckService profanityCheckService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PROFANITY_VALIDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView handleRequestInternal(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Profanity profanity = JsonDeserializer.deserialize(data, Profanity.class);
		if (profanity.getCallBackUrl() != null) {
			this.getProfanityCheckService().callBackprofanityWordCheck(profanity);
			return null;
		}
		return toModelAndViewWithIoFilter(getProfanityCheckService().profanityWordCheck(profanity), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, PROFANITY_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PROFANITY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView profanityCreate(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Profanity profanity = JsonDeserializer.deserialize(data, Profanity.class);
		
		return toModelAndViewWithIoFilter(this.profanityCheckService.profanityCreate(profanity), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, PROFANITY_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PROFANITY_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = {RequestMethod.DELETE,RequestMethod.PUT})
	public void profanityDelete(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Profanity profanity = JsonDeserializer.deserialize(data, Profanity.class);
		this.profanityCheckService.profanityDelete(profanity);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PROFANITY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView profanityList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndView(getProfanityCheckService().profanityList(), RESPONSE_FORMAT_JSON);

	}

	public ProfanityCheckService getProfanityCheckService() {
		return profanityCheckService;
	}
}
