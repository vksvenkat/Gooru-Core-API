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

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityItemAttemptTry;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.session.SessionActivityService;
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
@RequestMapping(value = { "/v2/session" })
public class SessionActivityRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private SessionActivityService sessionActivityService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createSession(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<SessionActivity> sessionActivity = getSessionActivityService().createSessionActivity(this.buildSessionActivityFromInputParameters(data), user);
		if (sessionActivity.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(SESSION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(sessionActivity.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateSession(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<SessionActivity> sessionActivity = getSessionActivityService().updateSessionActivity(sessionActivityId, this.buildSessionActivityFromInputParameters(data));
		if (sessionActivity.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String includes[] = (String[]) ArrayUtils.addAll(SESSION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(sessionActivity.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getSession(@PathVariable(ID) final Long sessionActivityId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(SESSION_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getSessionActivityService().getSessionActivity(sessionActivityId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, value = "/{id}/item")
	public ModelAndView createOrUpdateSessionItem(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final SessionActivityItem sessionActivityItem = getSessionActivityService().createOrUpdateSessionActivityItem(this.buildSessionActivityItemFromInputParameters(data), sessionActivityId);
		response.setStatus(HttpServletResponse.SC_CREATED);
		String includes[] = (String[]) ArrayUtils.addAll(SESSION_ITEM_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(sessionActivityItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/attempt")
	public ModelAndView createSessionItemAttemptTry(@RequestBody String data, @PathVariable(ID) Long sessionActivityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SessionActivityItemAttemptTry sessionActivityItemAttemptTry = getSessionActivityService().createSessionActivityItemAttemptTry(this.buildSessionItemAttemptFromInputParameters(data), sessionActivityId);
		if (sessionActivityItemAttemptTry == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return toModelAndViewWithIoFilter(sessionActivityItemAttemptTry, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, SESSION_ITEM_ATTEMPT_INCLUDES);
	}

	private SessionActivity buildSessionActivityFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionActivity.class);
	}

	private SessionActivityItem buildSessionActivityItemFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionActivityItem.class);
	}

	private SessionActivityItemAttemptTry buildSessionItemAttemptFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionActivityItemAttemptTry.class);
	}

	public SessionActivityService getSessionActivityService() {
		return sessionActivityService;
	}
}
