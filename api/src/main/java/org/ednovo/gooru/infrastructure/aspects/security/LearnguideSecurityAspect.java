/////////////////////////////////////////////////////////////
// LearnguideSecurityAspect.java
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
package org.ednovo.gooru.infrastructure.aspects.security;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.PermissionType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

@Aspect
public class LearnguideSecurityAspect {

	private static final String CONTENT_ID = "contentId";

	@Autowired
	private LearnguideRepository classplanRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Before("execution(* org.ednovo.gooru.controllers.api.LearnguideRestController.delete*(..)) && args(gooruContentId,*,request,..)")
	public void doOwnerandAdminCheck(String gooruContentId, HttpServletRequest request) {

		User apiCaller = (User) request.getAttribute(Constants.USER);

		Learnguide collection = this.getClassplanRepository().findByContent(gooruContentId);
		if (collection != null) {
			SessionContextSupport.putLogParameter(CONTENT_ID, collection.getContentId());
			User contentOwner = collection.getUser();

			if (!(contentOwner.getUserId().equals(apiCaller.getUserId()) || getOperationAuthorizer().hasAuthorization(GooruOperationConstants.OPERATION_CONTENT_UNRESTRICTED))) {
				throw new AccessDeniedException("You are not authorized to perform this action.");
			} else {
				request.setAttribute(Constants.SEC_CLASSPLAN, collection);
			}
		}
	}

	@Pointcut("execution(* org.ednovo.gooru.controllers.api.LearnguideRestController.update*(..)) || " + "execution(* org.ednovo.gooru.controllers.api.SegmentRestController.update*(..)) " + "|| execution(* org.ednovo.gooru.controllers.api.SegmentRestController.create*(..))"
			+ "|| execution(* org.ednovo.gooru.controllers.api.SegmentRestController.delete*(..)) " + "|| execution(* org.ednovo.gooru.controllers.api.SegmentRestController.reorder*(..)) " + "|| execution(* org.ednovo.gooru.controllers.api.ResourceRestController.create*Resource(..)) "
			+ "|| execution(* org.ednovo.gooru.controllers.api.ResourceRestController.reorderResource(..))")
	public void classplanPointcut() {
	}

	@Before(" classplanPointcut() && args(request,gooruContentId,..)")
	public void doOwnerCollaboratorAccessCheck(HttpServletRequest request, String gooruContentId) {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		Learnguide collection = (Learnguide) this.getContentRepository().findByContentGooruId(gooruContentId);

		if (!getOperationAuthorizer().hasAuthorization(GooruOperationConstants.OPERATION_CONTENT_UNRESTRICTED)) {

			boolean isOwner = apiCaller.getGooruUId().equals(collection.getUser().getGooruUId());
			boolean isCollaborator = collection.hasPermissions(apiCaller, PermissionType.EDIT);
			boolean hasSubOrgPermission = hasSubOrgPermission(collection.getOrganization().getPartyUid());

			if (isOwner == false && isCollaborator == false && hasSubOrgPermission == false) {
				throw new AccessDeniedException("You are not authorized to perform this action");
			}
		}
		SessionContextSupport.putLogParameter(CONTENT_ID, collection.getContentId());
		request.setAttribute(Constants.SEC_CLASSPLAN, collection);
	}

	private boolean hasSubOrgPermission(String contentOrganizationId) {
		String[] subOrgUids = UserGroupSupport.getUserOrganizationUids();
		if (subOrgUids != null && subOrgUids.length > 0) {
			for (String userSuborganizationId : subOrgUids) {
				if (contentOrganizationId.equals(userSuborganizationId)) {
					return true;
				}
			}
		}
		return false;
	}

	@Pointcut("execution(* org.ednovo.gooru.controllers.api.ResourceRestController.deleteResource(..)) " + "|| execution(* org.ednovo.gooru.controllers.api.ResourceRestController.update*Resource(..)) ")
	public void resourcePointcut() {
	}

	@Before(" resourcePointcut() && args(request,gooruContentId,..)")
	public void doOwnerCollaboratorAccessCheckForResource(HttpServletRequest request, String gooruContentId) {
		User apiCaller = (User) request.getAttribute(Constants.USER);

		Content content = this.getContentRepository().findContentByGooruId(gooruContentId);

		if (!getOperationAuthorizer().hasAuthorization(GooruOperationConstants.OPERATION_CONTENT_UNRESTRICTED)) {

			boolean isOwner = apiCaller.getGooruUId().equals(content.getUser().getGooruUId());
			boolean isCollaborator = content.hasPermissions(apiCaller, PermissionType.EDIT);

			if (isOwner == false && isCollaborator == false) {
				throw new AccessDeniedException("You are not authorized to perform this action");
			}
		}
		SessionContextSupport.putLogParameter(CONTENT_ID, content.getContentId());
		request.setAttribute(Constants.SEC_CONTENT, content);
	}

	@AfterReturning("classplanPointcut() && args(request,gooruContentId,..)")
	public void updateContentLastModified(HttpServletRequest request, String gooruContentId) {
		// FIXME This logic isn't really right. the last modified date should
		// better be updated from within the Save method as we do 2 calls to
		// update, which is inefficient.
		Content content = (Learnguide) request.getAttribute(Constants.SEC_CONTENT);
		if (content != null) {
			SessionContextSupport.putLogParameter(CONTENT_ID, content.getContentId());
			content.setLastModified(new Date(System.currentTimeMillis()));
			this.getContentRepository().save(content);
		}
	}

	public LearnguideRepository getClassplanRepository() {
		return classplanRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public void setOperationAuthorizer(OperationAuthorizer operationAuthorizer) {
		this.operationAuthorizer = operationAuthorizer;
	}

}
