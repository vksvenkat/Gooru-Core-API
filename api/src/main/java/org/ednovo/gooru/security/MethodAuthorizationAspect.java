/////////////////////////////////////////////////////////////
// MethodAuthorizationAspect.java
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
package org.ednovo.gooru.security;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.user.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Aspect
public class MethodAuthorizationAspect extends OperationAuthorizer {

	private static final Logger logger = LoggerFactory.getLogger(MethodAuthorizationAspect.class);

	@Autowired
	private ParameterNameDiscoverer parameterNameDiscoverer;

	@Autowired
	private UserServiceImpl userService;

	@Around("accessCheckPointcut() && @annotation(authorizeOperations) && @annotation(requestMapping)")
	public Object operationsAuthorization(ProceedingJoinPoint pjp, AuthorizeOperations authorizeOperations, RequestMapping requestMapping) throws Throwable {

		// Check method access
		boolean permitted = hasOperationsAuthority(authorizeOperations, pjp);

		// Check party access
		if (authorizeOperations.partyOperations() != null && authorizeOperations.partyOperations().length > 0) {
			permitted = hasPartyAuthorization(authorizeOperations, pjp, requestMapping);
		}

		if (permitted) {
			return pjp.proceed();
		} else {
			throw new AccessDeniedException("Permission Denied");
		}
	}

	@Pointcut("execution(* org.ednovo.gooru.controllers.*.*RestController.*(..)) || " + "execution(* org.ednovo.gooru.controllers.*.*.*RestV2Controller.*(..))) ")
	public void accessCheckPointcut() {
	}
	
	public boolean hasOperationsAuthority(AuthorizeOperations authorizeOperations, ProceedingJoinPoint pjp) {
		if (authorizeOperations.operations().length == 0) {
			return true;
		}

		GooruAuthenticationToken authenticationContext = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		if (authenticationContext != null && authenticationContext.getErrorMessage() != null) {
			if (authenticationContext.getErrorCode() == 403) {
				throw new AccessDeniedException(authenticationContext.getErrorMessage());
			} else { 
				throw new RuntimeException(authenticationContext.getErrorMessage());
			}
		}
		if (authenticationContext == null) {
			throw new AccessDeniedException("Invalid Session Token");
		}
		if (hasAuthorization(authorizeOperations)) {
			return true;
		}
		logger.error("Permission Denied For : " + authenticationContext.getPrincipal() + " To Access : " + pjp.getSignature().getName());
		return false;
	}

	public boolean hasPartyAuthorization(AuthorizeOperations authorizeOperations, ProceedingJoinPoint pjp, RequestMapping requestMapping) {

		GooruAuthenticationToken authenticationContext = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

		if (authenticationContext != null) {
			boolean partyOperationCheck = false;
			String apiCallerPermission = null;
			List<String> partyPermissions = authenticationContext.getUserCredential().getPartyOperations();
			List<String> accessblePermissions = Arrays.asList(authorizeOperations.partyOperations());
			if (partyPermissions != null && accessblePermissions != null) {
				for (String partyPermission : partyPermissions) {
					if (accessblePermissions.contains(partyPermission)) {
						partyOperationCheck = true;
						apiCallerPermission = partyPermission;
					}
				}
			}
			final Signature signature = pjp.getStaticPart().getSignature();
			if (signature instanceof MethodSignature) {
				final MethodSignature ms = (MethodSignature) signature;
				String[] paramNames = parameterNameDiscoverer.getParameterNames(ms.getMethod());
				Object[] paramValues = pjp.getArgs();
				String partyUidName = authorizeOperations.partyUId();
				String partyUid = "";
				if (paramNames != null && paramValues != null) {
					for (int paramNameIndex = 0; paramNameIndex < paramNames.length; paramNameIndex++) {
						String paramName = paramNames[paramNameIndex];
						if (paramName instanceof String) {
							if (paramName.equals(partyUidName)) {
								if (paramValues[paramNameIndex] != null) {
									partyUid = (String) paramValues[paramNameIndex];
								}
							}
						}
					}
				}
				if (!partyUid.isEmpty()) {
					String[] permittedParties = authenticationContext.getUserCredential().getPartyPermits();
					List<String> permittedPartiesList = Arrays.asList(permittedParties);
					String apiCallerOrgUid = authenticationContext.getUserCredential().getOrganizationUid();
					String userUid = authenticationContext.getUserCredential().getUserUid();
					User user = userService.findByGooruId(partyUid);
					User apiCaller = userService.findByGooruId(userUid);
					RequestMethod[] requestMethods = requestMapping.method();

					if (partyUid.equals(userUid)) {
						for (RequestMethod requestMethod : requestMethods) {
							if (requestMethod.equals(RequestMethod.DELETE)) {
								return false;
							}
						}
						return true;
					} else if (user != null && partyOperationCheck && (permittedPartiesList.contains(partyUid) || permittedPartiesList.contains(user.getOrganization().getPartyUid()))) {
						if (user.getOrganization().getPartyUid().equals(apiCallerOrgUid)) {
							if (apiCallerPermission.equalsIgnoreCase(GooruOperationConstants.GROUP_ADMIN) && user.getUserGroup().equals(apiCaller.getUserGroup())) {
								return true;
							} else if (apiCallerPermission.equalsIgnoreCase(GooruOperationConstants.ORG_ADMIN)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
