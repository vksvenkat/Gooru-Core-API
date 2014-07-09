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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.authentication.AccountService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
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
@RequestMapping(value = { "/v2/account" })
public class AccountRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	@Resource(name = "serverConstants")
	private Properties serverConstants;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/login")
	public ModelAndView login(@RequestParam(value = API_KEY, required = true) String apiKey, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_LOGIN);
		JSONObject json = requestData(data);
		ActionResponseDTO<UserToken> responseDTO = null;
		responseDTO = this.getAccountService().logIn(getValue(USER_NAME, json), getValue(PASSWORD, json), apiKey, false, request);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			SessionContextSupport.putLogParameter(EVENT_NAME, USER_LOGIN);
		}
		String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));

	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/switch-session")
	public ModelAndView swithSession(@RequestParam(value = SESSIONTOKEN, required = true) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_SWITCH_SESSION);
		ActionResponseDTO<UserToken> responseDTO = null;
		responseDTO = this.getAccountService().switchSession(sessionToken);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			SessionContextSupport.putLogParameter(EVENT_NAME, USER_SIGN_IN);
			SessionContextSupport.putLogParameter(CURRENT_SESSION_TOKEN, responseDTO.getModel().getToken());
			SessionContextSupport.putLogParameter(GOORU_UID, responseDTO.getModel().getUser().getPartyUid());
		}
		String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModel(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNOUT })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, GOORU_LOG_OUT);
		getAccountService().logOut(sessionToken);
		request.getSession().invalidate();
		RequestUtil.deleteCookie(request, response, GOORU_SESSION_TOKEN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/loginas/{id}")
	public ModelAndView loginAs(@PathVariable(value = ID) String gooruUid, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = API_KEY, required = false) String apiKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_LOGIN_AS);
		ActionResponseDTO<UserToken> userToken = this.getAccountService().loginAs(sessionToken, gooruUid, request, apiKey);
		if (userToken.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			SessionContextSupport.putLogParameter(EVENT_NAME, _USER_LOGIN_AS);
			SessionContextSupport.putLogParameter(CURRENT_SESSION_TOKEN, userToken.getModel().getToken());
			SessionContextSupport.putLogParameter(GOORU_UID, userToken.getModel().getUser().getPartyUid());
			request.getSession().setAttribute(Constants.USER, userToken.getModel().getUser());
			request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getModel().getToken());
		}
		String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, ERROR_INCLUDE);
		return toModelAndView(serialize(userToken.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/authenticate")
	public ModelAndView authenticateUser(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		SessionContextSupport.putLogParameter(EVENT_NAME, USER_AUTHENTICATE);
		SessionContextSupport.putLogParameter(SECERT_KEY, getValue(SECERT_KEY, json));
		SessionContextSupport.putLogParameter(API_KEY, getValue(API_KEY, json));
		User user = this.getAccountService().userAuthentication(buildUserFromInputParameters(data), getValue(SECERT_KEY, json), getValue(API_KEY, json), UserAccountType.accountCreatedType.GOOGLE_APP.getType(), request);
		if (user.getIdentities() != null) {
			Identity identity = user.getIdentities().iterator().next();
			if (identity.getActive() == 0) {
				Map<String, Object> redirectObj = new HashMap<String, Object>();
				redirectObj.put(ACTIVE, 0);
				 return toModelAndView(serialize(redirectObj, JSON));
			}
		}
		return toModelAndViewWithIoFilter(user, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, USER_INCLUDES);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	private User buildUserFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, User.class);
	}

	public Properties getServerConstants() {
		return serverConstants;
	}

}
