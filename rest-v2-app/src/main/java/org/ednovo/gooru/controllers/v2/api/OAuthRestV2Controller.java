/////////////////////////////////////////////////////////////
// OAuthRestV2Controller.java
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


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.core.api.model.OAuthClient;
import org.ednovo.gooru.domain.service.oauth.OAuthService;
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
@RequestMapping( value = { "/v2/oauth" } )

public class OAuthRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {
	
	@Autowired
	private OAuthService oAuthService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/client")
	public ModelAndView createOAuthClient(@RequestBody String data,  HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<OAuthClient> responseDTO = getOAuthService().createOAuthClient(buildOAuthClientFromInputParameters(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Register");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getKey());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(responseDTO.getModel(), RESPONSE_FORMAT_JSON, EXCLUDE, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/client/{id}")
	public ModelAndView updateOAuthClient(@PathVariable String id,HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.update");
		ActionResponseDTO<OAuthClient> responseDTO = oAuthService.updateOAuthClient(buildOAuthClientFromInputParameters(data),id);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Update");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getKey());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);

		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/client/{oauthKey}")
	public ModelAndView getOAuthClient(@PathVariable String oauthKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.read");
		ActionResponseDTO<OAuthClient> responseDTO = oAuthService.getOAuthClient(oauthKey);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Read");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getKey());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/client")
	public ModelAndView listOAuthClient(@RequestParam String gooruUId, HttpServletRequest request, HttpServletResponse response , @RequestParam(required = false , defaultValue= "0") int pageNo ,@RequestParam(required=false, defaultValue="20") int pageSize ) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.list");
		List<OAuthClient> OAuthClients = oAuthService.listOAuthClient(gooruUId, pageNo, pageSize);
		
		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-list");
		SessionContextSupport.putLogParameter("UserUid", gooruUId);
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(OAuthClients, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/client/list")
	public ModelAndView listOAuthClientByOrganization(@RequestParam String organizationUId, HttpServletRequest request, HttpServletResponse response , 
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, 
			@RequestParam (value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.list-by-organization");
		SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-list-byOrganization");
		SessionContextSupport.putLogParameter("organizationUId", organizationUId);
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(this.getOAuthService().listOAuthClientByOrganization(organizationUId, offset, limit, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{id}")
	public void deleteOAuthClientByOAuthKey(@PathVariable(value = ID) String oauthKey, HttpServletRequest request, HttpServletResponse response) throws Exception {

		User apiCaller = (User) request.getAttribute(Constants.USER);
		this.getOAuthService().deleteOAuthClientByOAuthKey(oauthKey,apiCaller);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private OAuthClient buildOAuthClientFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, OAuthClient.class);
	}
	
	public OAuthService getOAuthService() {
		return oAuthService;
	}


}
