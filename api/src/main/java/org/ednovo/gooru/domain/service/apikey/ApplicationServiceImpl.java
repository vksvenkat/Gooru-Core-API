/////////////////////////////////////////////////////////////
// ApplicationServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.apikey;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.MethodFailureException;
import org.ednovo.gooru.core.exception.NotAllowedException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class ApplicationServiceImpl extends BaseServiceImpl implements ApplicationService,ParameterProperties,ConstantProperties {

	@Autowired
	private ApplicationRepository apiKeyRepository;

	@Autowired
	private PartyService partyService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public List<ApiKey> findApplicationByOrganization(String organizationUid){
		return apiKeyRepository.getApplicationByOrganization(organizationUid);
	}

	@Override
	public ActionResponseDTO<ApiKey> saveApplication(ApiKey apikey, User user ,String organizationUid, User apiCaller) throws Exception{
		Errors error = validateApiKey(apikey);
	    PartyCustomField partyCustomField = null;
		if (!error.hasErrors()) {
			if(apiCaller != null){
				 partyCustomField = partyService.getPartyCustomeField(apiCaller.getPartyUid(), ConstantProperties.ORG_ADMIN_KEY, apiCaller);				
			}else {
				 partyCustomField = partyService.getPartyCustomeField(user.getPartyUid(), ConstantProperties.ORG_ADMIN_KEY, user);
			}

			if(partyCustomField != null && partyCustomField.getOptionalValue() != null){
				Organization organization = null;
				 //If organization is passed from superadmin use it else set loggedin users organization details
                if(organizationUid != null){
                   organization = organizationService.getOrganizationById(organizationUid);
                }else{
                      organization = organizationService.getOrganizationById(partyCustomField.getOptionalValue());
                }
				
                if(organization == null){
					throw new NotFoundException("Organization not found !");
				}

				apikey.setActiveFlag(1);
				apikey.setSecretKey(UUID.randomUUID().toString());
				apikey.setKey(UUID.randomUUID().toString());
				apikey.setOrganization(organization);
				apikey.setLimit(-1);
				apikey.setDescription(apikey.getDescription());
				CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.DEVELOPMENT.getApplicationStatus());
				apikey.setStatus(type.getValue());
				apikey.setComment(apikey.getComment());
				apiKeyRepository.save(apikey);
			}
			else {
				throw new NotFoundException("Admin organization not found in custom fields");
			}
		}
		return new ActionResponseDTO<ApiKey>(apikey, error);
	}
	
	private Errors validateApiKey(ApiKey apiKey) {
		final Errors errors = new BindException(apiKey, API_KEY);
		rejectIfNull(errors, apiKey, APP_NAME, GL0056, generateErrorMessage(GL0056, APP_NAME));
		rejectIfNull(errors, apiKey, APP_URL, GL0056, generateErrorMessage(GL0056, APP_URL));
		return errors;
	}

	@Override
	public ActionResponseDTO<ApiKey> updateApplication(ApiKey apikey, User user)
			throws Exception {
		Errors error = validateApiKey(apikey);
		ApiKey existingApiKey = apiKeyRepository.getApplicationByAppKey(apikey.getKey());
		if (!error.hasErrors()) {
			if(apikey.getDescription() != null){
			 existingApiKey.setDescription(apikey.getDescription());
			}
			if(apikey.getAppName() != null){
			 existingApiKey.setAppName(apikey.getAppName());
			}
			if(apikey.getAppURL() != null){
			 existingApiKey.setAppURL(apikey.getAppURL());
			}
			existingApiKey.setLastUpdatedUserUid(user.getPartyUid());
			existingApiKey.setLastUpdatedDate(new Date(System.currentTimeMillis()));
			
			if(apikey.getStatus() != null){
			 existingApiKey.setStatus(apikey.getStatus());
			}
			if(apikey.getComment() != null){
			 existingApiKey.setComment(apikey.getComment());
			}
			apiKeyRepository.save(existingApiKey);
		}
		return new ActionResponseDTO<ApiKey>(existingApiKey, error);
	}
	
	@Override
	public ActionResponseDTO<ApiKey> createJira(ApiKey apiKey, String username,String password,String appName,String appKey)  {
		   
		   Errors error = validateApiKey(apiKey);
		   ApiKey existingApiKey = apiKeyRepository.getApplicationByAppKey(apiKey.getKey());
		   try{
			   Form form = new Form();
			   form.add("issuetype", ISSUE);
			   form.add("pid", PID);
			   form.add("summary", "Request to create Appkey for "+ appName +" in the Production");
			   form.add("description", "Request to create Appkey for development Application Name : "+ appName + " and development Application Key : "+ appKey + " in the Production");
			   form.add("components", COMPONENTS);
			   form.add("assignee", "-1");
			   form.add("reporter", JIRA_REPORTER);
	
			    ClientResource httpClient = new ClientResource("http://collab.ednovo.org/jira/secure/QuickCreateIssue.jspa?decorator=none");
			    Form headers = (Form)httpClient.getRequestAttributes().get("org.restlet.http.headers");
			    
			    if (headers == null) {
			        headers = new Form();
			        httpClient.getRequestAttributes().put("org.restlet.http.headers", headers);
			    }
			    headers.set("X-Atlassian-Token", "no-check");
			    ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
			    httpClient.setChallengeResponse(challengeResponse);
			    httpClient.post(form);
			    Response resp = httpClient.getResponse();
			    String text = resp.getEntity().getText();
			    String status = resp.getStatus().toString();
			    if (status.contains("200")){
			    	 CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.SUBMITTED_FOR_REVIEW.getApplicationStatus());
			 		 apiKey.setStatus(type.getValue());
			 		 apiKey.setKey(appKey);
			 		 apiKeyRepository.save(existingApiKey);
		 		}else{				   
		 			 throw new NotAllowedException(text);
			 			   
		 		}	
		   }catch(Exception e){
			   
			   throw new MethodFailureException(e.getMessage());   
		   }
		   
		return new ActionResponseDTO<ApiKey>(existingApiKey, error);	
	}
	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

}
