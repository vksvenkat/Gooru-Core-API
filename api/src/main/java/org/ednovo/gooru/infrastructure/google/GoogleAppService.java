/////////////////////////////////////////////////////////////
// GoogleAppService.java
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
package org.ednovo.gooru.infrastructure.google;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;

public class GoogleAppService implements ParameterProperties{
	
	@Autowired
	ConfigProperties configProperties;
	
	private GoogleService service;
	
	public boolean authorize(){		
		boolean isAuthorized = false;		
		GoogleOAuthParameters params = new GoogleOAuthParameters();
		params.setOAuthType(com.google.gdata.client.authn.oauth.OAuthParameters.OAuthType.TWO_LEGGED_OAUTH);
		
		params.setOAuthConsumerKey(configProperties.getGooruApp().get(GOORU_APP_KEY ));
		params.setOAuthConsumerSecret(configProperties.getGooruApp().get(GOORU_APP_SECRET));
		
		//params.setOAuthConsumerKey("554965803619.apps.googleusercontent.com");
		//params.setOAuthConsumerSecret("o0NeWPhXSgmaqfmDC78qXvfj");
		com.google.gdata.client.authn.oauth.OAuthSigner signer = new OAuthHmacSha1Signer();
		try {
			service.setOAuthCredentials(params, signer);
			isAuthorized = true;
		} catch (OAuthException e1) {
			isAuthorized = false;
			throw new RuntimeException("Error in authorization while scheduling classplan " , e1);
		}
		return isAuthorized;
	}

	public GoogleService getService() {
		return service;
	}

	public void setService(GoogleService service) {
		this.service = service;
	}

}
