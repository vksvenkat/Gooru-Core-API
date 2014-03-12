/////////////////////////////////////////////////////////////
// SOAPSession.java
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

import java.net.URL;

import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapService;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapServiceService;
import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapServiceServiceLocator;

/**
 * This represents a SOAP session with JIRA including that state of being logged
 * in or not
 */
@Component
public class SOAPSession implements ConfigConstants,ParameterProperties {

	protected final Logger logger = LoggerFactory.getLogger(SOAPSession.class);

	private JiraSoapServiceService jiraSoapServiceLocator;
	private JiraSoapService jiraSoapService;
	private String token;
	private SOAPClient soapClient;

	public SOAPSession() {
		try {
			 jiraSoapServiceLocator = new JiraSoapServiceServiceLocator();
			 this.soapClient = new SOAPClient();
			URL webServicePort = null;
			if (soapClient != null && this.getSoapClient().getJiraConfig() != null && this.getSoapClient().getJiraConfig().get(URL) != null) {
				webServicePort = new URL(this.getSoapClient().getJiraConfig().get(URL));
			}

			if (webServicePort == null) {
				jiraSoapService = jiraSoapServiceLocator.getJirasoapserviceV2();
			} else {
				jiraSoapService = jiraSoapServiceLocator.getJirasoapserviceV2(webServicePort);
				logger.info("SOAP Session service endpoint at " + webServicePort.toExternalForm());
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while executing SOAP request", e);
		}

	}

	void login(String userName, String password) {

		logger.info("\tConnnecting via SOAP as : " + userName);

		try {
			token = getJiraSoapService().login(userName, password);
		} catch (Exception e) {
			throw new RuntimeException("Error while logging in", e);
		}

		logger.info("\tConnected");
	}

	public JiraSoapServiceService getJiraSoapServiceLocator() {
		return jiraSoapServiceLocator;
	}

	public void setJiraSoapServiceLocator(JiraSoapServiceService jiraSoapServiceLocator) {
		this.jiraSoapServiceLocator = jiraSoapServiceLocator;
	}

	public JiraSoapService getJiraSoapService() {
		return jiraSoapService;
	}

	public void setJiraSoapService(JiraSoapService jiraSoapService) {
		this.jiraSoapService = jiraSoapService;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SOAPClient getSoapClient() {
		return soapClient;
	}
}
