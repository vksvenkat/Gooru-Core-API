package org.ednovo.gooru.controllers.v2.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.apikey.ApplicationService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.OrganizationSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
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
@RequestMapping(value="/application")
public class ApplicationRestController extends BaseController implements ConstantProperties{

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private OrganizationSettingRepository organizationSettingRepository;
    
	@Autowired
	private ApplicationRepository apiKeyRepository;
	
	@Autowired
	private CustomTableRepository customTableRepository;
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APIKEY_ADD })
	@RequestMapping(method = RequestMethod.POST)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createApplication(@RequestBody String data,HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ApiKey apiKey = buildApiKeyFromInputParameters(getValue("apiKey", json));
		ActionResponseDTO<ApiKey> responseDTO = getApplicationService().saveApplication(apiKey, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APIKEY_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value="/{appKey}")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateApplication(@RequestBody String data,HttpServletRequest request, HttpServletResponse response, @PathVariable String appKey) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ApiKey apiKey = buildApiKeyFromInputParameters(getValue("apiKey", json));
		apiKey.setKey(appKey);
		ActionResponseDTO<ApiKey> responseDTO = getApplicationService().updateApplication(apiKey, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APIKEY_LIST })
	@RequestMapping(method = RequestMethod.GET, value="/list")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView listApplication(HttpServletRequest request, HttpServletResponse response, @RequestParam String organizationUid) throws Exception {
		return toModelAndViewWithIoFilter(getApplicationService().findApplicationByOrganization(organizationUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, APPLICATION_INCLUDES);
	}

	public void setApplicationService(ApplicationService apiKeyService) {
		this.applicationService = apiKeyService;
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}
	private ApiKey buildApiKeyFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, ApiKey.class);
	}
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APIKEY_UPDATE })
	@RequestMapping(method = RequestMethod.POST, value="/{appKey}/issue")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView createJiraIssue(@PathVariable String appKey,@RequestParam String appName ,@RequestParam String userName,HttpServletRequest request, HttpServletResponse response) throws  IOException{
		String result = null;
		String error = null;
		String issueKey = null;
		String issueId = null;
		User user = (User) request.getAttribute(Constants.USER);
		ApiKey apiKey = apiKeyRepository.getApplicationByAppKey(appKey);
		try{
		DefaultHttpClient httpClient = new DefaultHttpClient();     
		 String username = organizationSettingRepository.getOrganizationSetting(Constants.JIRA_USERNAME,TaxonomyUtil.GOORU_ORG_UID);
	     String password = organizationSettingRepository.getOrganizationSetting(Constants.JIRA_PASSWORD, TaxonomyUtil.GOORU_ORG_UID);
	     String auth = new String(org.apache.commons.codec.binary.Base64.encodeBase64((username+":"+password).getBytes()));
 		HttpPost postRequest = new HttpPost("http://collab.ednovo.org/jira/secure/QuickCreateIssue.jspa?decorator=none");
 		postRequest.addHeader("accept", "application/json");
 		postRequest.setHeader("X-Atlassian-Token", "no-check");
 		postRequest.setHeader("Authorization", "Basic "+auth);
	
 		 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
         nameValuePairs.add(new BasicNameValuePair("issuetype",ISSUE));
         nameValuePairs.add(new BasicNameValuePair("pid", PID));
        // nameValuePairs.add(new BasicNameValuePair("project", PID));
         nameValuePairs.add(new BasicNameValuePair("summary", "Request to create Appkey for "+ appName +" in the Production"));
         nameValuePairs.add(new BasicNameValuePair("description", "Request to create Appkey for development Application Name : "+ appName + " and development Application Key : "+ appKey + " in the Production"));
         //nameValuePairs.add(new BasicNameValuePair("assignee", userName));
         nameValuePairs.add(new BasicNameValuePair("components", COMPONENTS));
         postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

 		HttpResponse resp = httpClient.execute(postRequest);          		     
 		InputStream instream = resp.getEntity().getContent();
		result = convertStreamToString(instream);
		JSONObject jsonObject = new JSONObject(result);
		if (resp.getStatusLine().getStatusCode() != 200) {
			error = jsonObject.getString("errors");
			throw new RuntimeException(error);
 		}else{
 			issueKey = jsonObject.getString("issueKey");
 			issueId = jsonObject.getString("issueId");
 			CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.SUBMITTED_FOR_REVIEW.getApplicationStatus());
 			apiKey.setStatus(type.getValue());
 			apiKey.setKey(appKey);
 			ActionResponseDTO<ApiKey> responseDTO = getApplicationService().updateApplication(apiKey, user);
 			if (responseDTO.getErrors().getErrorCount() > 0) {
 				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
 			} else {
 				response.setStatus(HttpServletResponse.SC_OK);
 			}
 			String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);
 			return toModelAndViewWithIoFilter(apiKey, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
 		}
		}catch(Exception e){
		}
		 return toModelAndView(serialize(error, "json"));
	}
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_APIKEY_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value="/{appKey}/status")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView updateApplicationStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable String appKey) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		ApiKey apiKey = apiKeyRepository.getApplicationByAppKey(appKey);
		apiKey.setKey(appKey);
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.PRODUCTION.getApplicationStatus());
		apiKey.setStatus(type.getValue());
		ActionResponseDTO<ApiKey> responseDTO = getApplicationService().updateApplication(apiKey, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		String includes[] = (String[]) ArrayUtils.addAll(APPLICATION_INCLUDES, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}
	protected static String convertStreamToString(InputStream is) {
	
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	
	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}
	
}
