/////////////////////////////////////////////////////////////
// TaskRunnerDaemon.java
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
package org.ednovo.gooru.application.util;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskRunnerDaemon extends Thread implements ParameterProperties {

	@Autowired
	private SettingService settingService;

	@Autowired
	ConfigProperties configProperties;

	private static final Logger logger = LoggerFactory.getLogger(TaskRunnerDaemon.class);

	private long runIndex = 0;
	private boolean stopRequested;
	private Properties configConstants;
	private String restEndPoint;
	private String tomcatUsername;
	private String tomcatPassword;

	public TaskRunnerDaemon(Properties configConstants) {
		this.configConstants = configConstants;
		this.restEndPoint = settingService.getConfigSetting(ConfigConstants.GOORU_SERVICES_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
		tomcatUsername = configProperties.getTomCat().get(USER_NAME);
		tomcatPassword = configProperties.getTomCat().get(PASSWORD);
		this.setDaemon(true);
	}

	public void run() {

	}

	public void runBatch(String urlPath, Map<String, String> configOptions) {
		runBatch(urlPath, configOptions, false);
	}

	public void runBatch(String urlPath, Map<String, String> configOptions, boolean recursive) {
		while (!stopRequested) {
			try {
				logger.debug("Starting run:" + runIndex);
				runUrlTask(urlPath, configOptions);
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			runIndex++;
		}
		logger.info("TaskRunnerDaemon: Stop requested, stopping at run " + runIndex);
	}

	private void runUrlTask(String urlPath, Map<String, String> configOptions) {
		Representation representation = null;
		ClientResource resource = null;

		String resourceUrl = "";
		try {

			restEndPoint = configOptions.get(REST_END_POINT);
			tomcatUsername = configOptions.get(TOMCAT_USER_NAME);
			tomcatPassword = configOptions.get(TOMCAT_PASSWORD);
			configOptions.remove(REST_END_POINT);
			configOptions.remove(TOMCAT_USER_NAME);
			configOptions.remove(TOMCAT_PASSWORD);

			Set<String> keySet = configOptions.keySet();
			StringBuilder params = new StringBuilder();
			for (String key : keySet) {
				params.append(key);
				params.append("=");
				params.append(configOptions.get(key));
				params.append("&");
			}

			// Create the client resource
			// resource = new ClientResource(restAPIEndPoint +
			// "/learnguide/migrate");
			resourceUrl = restEndPoint + urlPath + "?" + params.toString();
			resource = new ClientResource(resourceUrl);

			if (!tomcatUsername.equals(NA)) {
				ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, tomcatUsername, tomcatPassword);
				resource.setChallengeResponse(challengeResponse);
			}
			resource.setRetryDelay(120000);
			resource.setRetryAttempts(1);
			representation = resource.get();

		} catch (Exception exception) {
			logger.warn("UrlRunner exception while trying to call " + resourceUrl + ": ", exception);
		} finally {
			try {
				if (representation != null) {
					representation.release();
				}
				if (resource != null) {
					resource.release();
				}
			} catch (Exception x) {
			}
		}
	}

	public void stopTask() {
		this.stopRequested = true;
	}

}
