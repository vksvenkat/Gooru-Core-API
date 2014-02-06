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
import org.ednovo.gooru.domain.model.oauth.OAuthClient;
import org.ednovo.gooru.domain.service.oauth.OAuthService;
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
@RequestMapping(value = { "/oauth" })
public class OAuthRestController extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private OAuthService oAuthService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/client")
	public ModelAndView createOAuthClient(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.create");
		JSONObject json = requestData(data);
		OAuthClient oAuthClient = buildOAuthClientFromInputParameters(getValue("oauthClient", json));
		ActionResponseDTO<OAuthClient> responseDTO = null;
		responseDTO = oAuthService.createNewOAuthClient(oAuthClient);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Register");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getOauthClientUId());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(responseDTO.getModel(), RESPONSE_FORMAT_JSON, EXCLUDE, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/client")
	public ModelAndView updateOAuthClient(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.update");
		JSONObject json = requestData(data);
		OAuthClient oAuthClient = buildOAuthClientFromInputParameters(getValue("oauthClient", json));
		ActionResponseDTO<OAuthClient> responseDTO = null;
		responseDTO = oAuthService.updateOAuthClient(oAuthClient);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Update");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getOauthClientUId());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);

		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/client/{clientUId}")
	public ModelAndView getOAuthClient(@PathVariable String clientUId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.read");
		ActionResponseDTO<OAuthClient> responseDTO = null;
		responseDTO = oAuthService.getOAuthClient(clientUId);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Read");
			SessionContextSupport.putLogParameter("OAuthClientId", responseDTO.getModel().getOauthClientUId());
		}
		String [] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, OAUTH_CLIENT_INCLUDES);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_OAUTH_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/client/{clientUId}")
	public void deleteOAuthClient(@PathVariable String clientUId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "oauthclient.delete");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		oAuthService.deleteOAuthClient(clientUId, apiCaller);
		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, "OauthClient-Delete");
		SessionContextSupport.putLogParameter("DeletedOAuthClientId", clientUId);
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

	private OAuthClient buildOAuthClientFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, OAuthClient.class);
	}

}
