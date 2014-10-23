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
	
	private static String BEARER_TYPE = "Bearer";
	
	@Override
	public void init(FilterConfig objFConfig) throws ServletException {
		// Does nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		HttpServletResponse response = (HttpServletResponse) res;
		
		MultiReadHttpServletRequest httpServletRequestWrapper = null;
		
		if (request.getContentType() != null && (request.getContentType().contains(MediaType.APPLICATION_JSON.getName()) || request.getContentType().contains("text/"))) {
				httpServletRequestWrapper = new MultiReadHttpServletRequest(request);
		}

		if (logger.isInfoEnabled()) {
			logger.info("Request URI: " + ((HttpServletRequest) request).getRequestURI());
		}

		// check the authentication object in security

	       try {
               // check the authentication object in security
               Authentication auth = SecurityContextHolder.getContext().getAuthentication();
               String sessionToken = request.getParameter("sessionToken");
               String pinToken = request.getParameter("pinToken");
               String apiKeyToken = request.getParameter("apiKey");
               String oAuthToken = request.getHeader("OAuth-Authorization");
               
               if(oAuthToken != null){
            	   if(oAuthToken.contains(BEARER_TYPE)){
            		   oAuthToken = StringUtils.substringAfterLast(oAuthToken, BEARER_TYPE).trim();
            	   }
            	   else{
            		   oAuthToken = null;
            	   }
               }

               getDoAuthorization().doFilter(sessionToken, pinToken, apiKeyToken, request, response, auth, oAuthToken);

       } catch (Exception ex) {
               int  errorCode = 500;
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
