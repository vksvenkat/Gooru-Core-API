/////////////////////////////////////////////////////////////
// ProfanityCheckServiceImpl.java
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
package org.ednovo.gooru.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ednovo.gooru.core.api.model.Profanity;
import org.ednovo.gooru.core.application.util.UrlGenerator;
import org.ednovo.gooru.core.application.util.UrlToken;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.cassandra.service.BlackListWordCassandraService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class ProfanityCheckServiceImpl implements ProfanityCheckService, ConstantProperties {

	private static final Logger logger = LoggerFactory.getLogger(ProfanityCheckServiceImpl.class);

	private String webPurifyAPIKey;

	@Autowired
	private BlackListWordCassandraService blackListWordCassandraService;

	@Override
	public Profanity profanityWordCheck(Profanity profanity) {

		if (!getBlackListWordCassandraService().validate(profanity.getText())) {
			if (getWebPurifyAPIKey() == null) {
				setWebPurifyAPIKey(WEBPURIFY_API_KEY);
			}
			ClientResource client = new ClientResource(UrlGenerator.generateUrl(WEBPURIFY_API_END_POINT, UrlToken.GET_webpurify_PROFANITY, getWebPurifyAPIKey(), LIST_METHOD, RESPONSE_FORMAT, profanity.getText()));
			if (client.getStatus().isSuccess()) {
				String response = null;

				try {
					response = client.get().getText();
					JSONObject jsonResponse = new JSONObject(response);
					JSONObject jsonRsp = jsonResponse.getJSONObject("rsp");
					if (jsonRsp.getString("found").equals("0")) {
						profanity.setFound(false);
					} else {
						profanity.setFound(true);
						profanity.setFoundBy("webpurify");
						List<String> getWords = new ArrayList<String>();
						JSONArray length = jsonRsp.getJSONArray("expletive");
						for (int i = 0; i < length.length(); i++) {
							getWords.add(length.getString(i));
						}
						profanity.setExpletive(getWords);
						profanity.setCount(Integer.valueOf(jsonRsp.getString("found")));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				logger.error("failed to web purify call failed.");
				return null;
			}
		} else {
			profanity.setFound(true);
			profanity.setFoundBy("internal-list");
			profanity.setCount(1);

		}
		return profanity;
	}

	public List<String> profanityCreate(Profanity profanity) throws Exception {
		if (!getBlackListWordCassandraService().validate(profanity.getText())) {
			String[] badwords = profanity.getText().split(",");
			List<String> words = new ArrayList<String>();
			for (String badword : badwords) {
				words.add(badword);
			}
			getBlackListWordCassandraService().save(words);
			return words;
		} else {
			throw new BadCredentialsException(profanity.getText());
		}
	}

	public void profanityDelete(Profanity profanity) throws Exception {
		if (getBlackListWordCassandraService().validate(profanity.getText())) {
			getBlackListWordCassandraService().delete(profanity.getText());
		} else {
			throw new NotFoundException(profanity.getText());
		}
	}

	public Collection<String> profanityList() throws Exception {
		return getBlackListWordCassandraService().read();
	}

	public void callBackprofanityWordCheck(Profanity profanity) throws Exception {
		profanity = this.profanityWordCheck(profanity);
		if (profanity != null) {
			JSONObject response = new JSONObject();
			JSONObject status = new JSONObject();
			if (profanity.isFound()) {
				status.put("value", "abuse");
			} else {
				status.put("value", "active");
			}
			response.put("status", status);
			new ClientResource(profanity.getCallBackUrl()).put(response.toString());
		}
	}

	@Override
	public String getConfigSetting(String apiEndPoint, String key, String token) {
		ClientResource client = new ClientResource(UrlGenerator.generateUrl(apiEndPoint, UrlToken.GET_CONFIG_SETTING, key, token));
		String response = null;
		if (client.getStatus().isSuccess()) {
			try {
				response = client.get().getText();
			} catch (Exception e) {
				logger.error("failed to get the config setting");
			}
		}
		return response;
	}

	public void setWebPurifyAPIKey(String webPurifyAPIKey) {
		this.webPurifyAPIKey = webPurifyAPIKey;
	}

	public String getWebPurifyAPIKey() {
		return webPurifyAPIKey;
	}

	public BlackListWordCassandraService getBlackListWordCassandraService() {
		return blackListWordCassandraService;
	}

}
