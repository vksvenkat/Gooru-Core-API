/////////////////////////////////////////////////////////////
// SOAPClient.java
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
package org.ednovo.gooru.infrastructure.jira;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import com.atlassian.jira.rpc.exception.RemotePermissionException;
import com.atlassian.jira.rpc.exception.RemoteValidationException;
import com.atlassian.jira.rpc.soap.beans.RemoteComponent;
import com.atlassian.jira.rpc.soap.beans.RemoteCustomFieldValue;
import com.atlassian.jira.rpc.soap.beans.RemoteIssue;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapService;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class SOAPClient implements ConfigConstants,ParameterProperties{

	@Autowired
	private SettingService settingService;
	
	private static final String JIRA_ERROR = "Error during jira issue creation";

	private static final Logger LOGGER = LoggerFactory.getLogger(SOAPClient.class);

	private String username;
	private String password;
	private String projectKey;
	private String issueTypeId;
	private String priorityId;
	private String assignee;
	private String customFieldEmailId;
	private String componentId;
	protected Map<String, String> jiraConfig;
	
	@PostConstruct
	public void init() { 
		String data = settingService.getConfigSetting(JIRA_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		try {
			jiraConfig = JsonDeserializer.deserialize(data, new TypeReference<Map<String, String>>() {
			});
			this.username = jiraConfig.get(USER_NAME);
			this.password = jiraConfig.get(PASSWORD);
			this.projectKey = jiraConfig.get(PROJECT_KEY);
			this.issueTypeId = jiraConfig.get(ISSUE_TYPE);
			this.priorityId = jiraConfig.get(PRIORITY_ID);
			this.componentId = jiraConfig.get(COMPONENT_ID);
			
		} catch(Exception e) {
			LOGGER.info("Failed to initialize jira config");
		}
	}

	public String getAuthenticationToken(SOAPSession soapSession) {
		String authToken = null;
		soapSession.login(username, password);
		authToken = soapSession.getToken();
		return authToken;
	}

	public String createIssue(SOAPSession soapSession, Map<Integer,String> customFieldValues , Map<String,String> standardJiraFields) {
		
		String authToken = getAuthenticationToken(soapSession);
		JiraSoapService jiraSoapService = soapSession.getJiraSoapService();
		
		//Get the custom Fields
		Map<Integer,String> customFieldKeys = new HashMap<Integer,String>();
		customFieldKeys.put(1,this.customFieldEmailId);
		
		// Create the issue
		RemoteIssue issue = new RemoteIssue();
		issue.setProject(projectKey);
		issue.setType(issueTypeId);
		issue.setAssignee(assignee);
		
		
		issue.setSummary(standardJiraFields.get(SUMMARY));
		issue.setDescription(standardJiraFields.get(DESCRIPTION));
		issue.setEnvironment(standardJiraFields.get(ENVIRONMENT));
		issue.setPriority(priorityId);
		issue.setDuedate(Calendar.getInstance());
		
		RemoteComponent component = new RemoteComponent();
		component.setId(componentId);
		issue.setComponents(new RemoteComponent[] { component });
		
		// Add custom fields 
		if(customFieldValues != null){
			RemoteCustomFieldValue[] arrCustomFieldValues = new RemoteCustomFieldValue[customFieldKeys.size()];
			for(int i=1;i<=customFieldKeys.size();i++){
				String[] strArray = {customFieldValues.get(i)};
				arrCustomFieldValues[i-1] = new RemoteCustomFieldValue(customFieldKeys.get(i),"",strArray);
			}
			issue.setCustomFieldValues(arrCustomFieldValues);
		}
		
		// Run the create issue code
		RemoteIssue returnedIssue=null;
		try {
			returnedIssue = jiraSoapService.createIssue(authToken, issue);
		} catch (RemotePermissionException e) {			
			LOGGER.error(JIRA_ERROR,e);
		} catch (RemoteValidationException e) {
			LOGGER.error(JIRA_ERROR,e);
		} catch (RemoteAuthenticationException e) {
			LOGGER.error(JIRA_ERROR,e);
		} catch (com.atlassian.jira.rpc.exception.RemoteException e) {
			LOGGER.error(JIRA_ERROR,e);
		} catch (RemoteException e) {
			LOGGER.error(JIRA_ERROR,e);
		} catch (Exception e) {
			LOGGER.error(JIRA_ERROR,e);
		}
		final String issueKey = returnedIssue.getKey();		
		
		return issueKey;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public String getIssueTypeId() {
		return issueTypeId;
	}

	public String getPriorityId() {
		return priorityId;
	}

	public String getAssignee() {
		return assignee;
	}

	public String getCustomFieldEmailId() {
		return customFieldEmailId;
	}

	public void setCustomFieldEmailId(String customFieldEmailId) {
		this.customFieldEmailId = customFieldEmailId;
	}
	
	public String getComponentId() {
		return componentId;
	}

	public Map<String, String> getJiraConfig() {
		return jiraConfig;
	}

}
