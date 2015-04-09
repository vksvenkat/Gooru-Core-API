package org.ednovo.gooru.web.filter;

import java.io.IOException;

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
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthorizationFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Autowired
	private DoAuthorization doAuthorization;

	private final String BEARER_TYPE = "Bearer";

	private final String SESSION_TOKEN = "sessionToken";

	private final String PIN_TOKEN = "pinToken";

	private final String API_KEY = "apiKey";

	private final String OAUTH_AUTHORIZATION = "OAuth-Authorization";

	@Override
	public void init(FilterConfig objFConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;

		MultiReadHttpServletRequest httpServletRequestWrapper = null;

		if (request.getContentType() != null && (request.getContentType().contains(MediaType.APPLICATION_JSON.getName()) || request.getContentType().contains("text/"))) {
			httpServletRequestWrapper = new MultiReadHttpServletRequest(request);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request URI: " + ((HttpServletRequest) request).getRequestURI());
		}

		// check the authentication object in security

		try {
			// check the authentication object in security
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String sessionToken = request.getParameter(SESSION_TOKEN);
			if (sessionToken == null || sessionToken.trim().length() == 0) {
				sessionToken = request.getHeader(SESSION_TOKEN);
			}
			String pinToken = request.getParameter(PIN_TOKEN);
			String apiKeyToken = request.getParameter(API_KEY);
			if (apiKeyToken == null || apiKeyToken.trim().length() == 0) {
				apiKeyToken = request.getHeader(API_KEY);
			}
			String oAuthToken = request.getHeader(OAUTH_AUTHORIZATION);

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

		chain.doFilter(httpServletRequestWrapper == null ? request : httpServletRequestWrapper, response);
	}

	@Override
	public void destroy() {
		SecurityContextHolder.clearContext();
	}

	public DoAuthorization getDoAuthorization() {
		return doAuthorization;
	}
}
