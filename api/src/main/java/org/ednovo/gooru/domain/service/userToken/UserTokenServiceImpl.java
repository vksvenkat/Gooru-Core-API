/////////////////////////////////////////////////////////////
// UserTokenServiceImpl.java
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
package org.ednovo.gooru.domain.service.userToken;

import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userTokenService")
public class UserTokenServiceImpl implements UserTokenService,ParameterProperties {

	@Autowired
	private UserTokenRepository userTokenRepository;
	
	@Override
	public UserToken findByScope(String gooruUserId, String scope) {
		return userTokenRepository.findByScope(gooruUserId, scope);
	}

	@Override
	public UserToken findBySession(String sessionId) {
		return userTokenRepository.findBySession(sessionId);
	}

	@Override
	public UserToken findByToken(String sessionToken) {
		return userTokenRepository.findByToken(sessionToken);
	}

	@Override
	public UserToken getQuoteToken(String sessionToken, String gooruUserId) {
		
		UserToken userSessionToken = userTokenRepository.findByToken(sessionToken);
		UserToken quoteToken = null;

		if (userSessionToken != null) {
			User user = new User();
			user = userSessionToken.getUser();

			quoteToken = userTokenRepository.findByScope(gooruUserId, QUOTE);

			if (quoteToken == null) {
				quoteToken = new UserToken();
				quoteToken.setScope(QUOTE);
				quoteToken.setUser(user);
				quoteToken.setSessionId("");

				userTokenRepository.save(quoteToken);
			}
		}
		return quoteToken;
	}

	
}
