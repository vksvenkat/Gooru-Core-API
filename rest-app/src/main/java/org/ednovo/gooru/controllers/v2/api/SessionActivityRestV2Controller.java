/////////////////////////////////////////////////////////////
//SessionRestV2Controller.java
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

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

// This session controller will get deprecated 
@Deprecated
@Controller
@RequestMapping(value = { "/v2/session" })
public class SessionActivityRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createSession(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateSession(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getSession(@PathVariable(ID) final Long sessionActivityId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return null;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, value = "/{id}/item")
	public ModelAndView createOrUpdateSessionItem(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/attempt")
	public ModelAndView createSessionItemAttemptTry(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/incomplete-session")
	public ModelAndView getInCompleteSession(@PathVariable(ID) final String gooruOid, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return null;

	}
}
