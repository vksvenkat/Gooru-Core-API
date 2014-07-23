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
import org.ednovo.gooru.core.api.model.Session;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.SessionItem;
import org.ednovo.gooru.core.api.model.SessionItemAttemptTry;
import org.ednovo.gooru.core.api.model.SessionItemFeedback;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.session.SessionService;
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
@RequestMapping(value = { "/v2/session" })
public class SessionRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private SessionService sessionService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createSession(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TAG_ADD_RESOURCE);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Session> session = getSessionService().createSession(this.buildSessionFromInputParameters(getValue(SESSION, json)), user);
		if (session.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "create-session");
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? SESSION_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(session.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/item/feedback", method = RequestMethod.POST)
	public ModelAndView createSessionItemFeedback(@PathVariable(ID) String sessionId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		SessionItemFeedback sessionItemFeedback = getSessionService().createSessionItemFeedback(sessionId, this.buildSessionItemFeedbackFromInputParameters(data), user);
		
		return toModelAndViewWithIoFilter(sessionItemFeedback, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, SESSION_ITEM_FEEDBACK_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateSession(@RequestBody String data, @PathVariable(ID) String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TAG_ADD_RESOURCE);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Session> session = getSessionService().updateSession(sessionId, this.buildSessionFromInputParameters(getValue(SESSION, json)));
		if (session.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "update-session");
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? SESSION_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(session.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getSession(@PathVariable(ID) String sessionId, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Session session = this.getSessionService().getSession(sessionId);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String includes[] = (String[]) ArrayUtils.addAll(SESSION_INCLUDES, SESSION_ITEM_INCLUDES);
		includes = (String[]) ArrayUtils.addAll(includes, SESSION_ITEM_ATTEMPT_INCLUDES);
		includes = (String[]) ArrayUtils.addAll(includeFields == null ? includes : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(session, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/item")
	public ModelAndView createSessionItem(@RequestBody String data, @PathVariable(ID) String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TAG_ADD_RESOURCE);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<SessionItem> sessionItem = getSessionService().createSessionItem(this.buildSessionItemFromInputParameters(getValue(SESSION_ITEM, json)), sessionId);
		if (sessionItem.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "create-session-item");
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? SESSION_ITEM_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(sessionItem.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_V2_SESSION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{sid}/item/{id}/attempt")
	public ModelAndView createSessionItemAttemptTry(@RequestBody String data, @PathVariable(ID) String sessionItemId, @PathVariable(SESSION_ID) String sessionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, TAG_ADD_RESOURCE);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		SessionItemAttemptTry sessionItemAttemptTry = getSessionService().createSessionItemAttemptTry(this.buildSessionItemAttemptFromInputParameters(getValue(SESSION_ITEM_ATTEMPT_TRY, json)), sessionItemId);
		if (sessionItemAttemptTry == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "update-session-item");
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		return toModelAndViewWithIoFilter(sessionItemAttemptTry, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, SESSION_ITEM_ATTEMPT_INCLUDES);
	}

	private Session buildSessionFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, Session.class);
	}

	private SessionItem buildSessionItemFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionItem.class);
	}

	private SessionItemFeedback buildSessionItemFeedbackFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionItemFeedback.class);
	}

	private SessionItemAttemptTry buildSessionItemAttemptFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, SessionItemAttemptTry.class);
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

}
