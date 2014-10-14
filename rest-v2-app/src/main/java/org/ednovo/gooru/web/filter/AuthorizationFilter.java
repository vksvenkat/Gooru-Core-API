/////////////////////////////////////////////////////////////
//AuthorizationFilter.java
//rest-v2-app
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
package org.ednovo.gooru.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.security.DoAuthorization;
import org.ednovo.gooru.security.MultiReadHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Autowired
	private DoAuthorization doAuthorization;

	private static final String BEARER_TYPE = "Bearer";

	@Override
	public void init(FilterConfig objFConfig) throws ServletException {
		// Does nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;
		MultiReadHttpServletRequest httpServletRequestWrapper = new MultiReadHttpServletRequest(request);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Request URI: " + ((HttpServletRequest) request).getRequestURI());
		}

		// check the authentication object in security

		try {
			// check the authentication object in security
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sessionToken = request.getParameter("sessionToken");
			String pinToken = request.getParameter("pinToken");
			String apiKeyToken = request.getParameter("apiKey");
			String oAuthToken = request.getHeader("OAuth-Authorization");

			if (oAuthToken != null) {
				if (oAuthToken.contains(BEARER_TYPE)) {
					oAuthToken = StringUtils.substringAfterLast(oAuthToken, BEARER_TYPE).trim();
				} else {
					oAuthToken = null;
				}
			}

			getDoAuthorization().doFilter(sessionToken, pinToken, apiKeyToken, request, response, auth, oAuthToken);

		} catch (Exception ex) {
			int errorCode = 500;
			if (ex instanceof AccessDeniedException) {
				errorCode = 403;
			}
			Authentication auth = new GooruAuthenticationToken(ex, null, ex.getMessage(), errorCode);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(httpServletRequestWrapper, response);
	}

	@Override
	public void destroy() {
		SecurityContextHolder.clearContext();
	}

	public DoAuthorization getDoAuthorization() {
		return doAuthorization;
	}
}
