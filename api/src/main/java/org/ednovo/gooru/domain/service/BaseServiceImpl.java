/////////////////////////////////////////////////////////////
// BaseServiceImpl.java
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

import org.ednovo.gooru.core.api.model.AnnotationType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.StatusType;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class BaseServiceImpl extends ServerValidationUtils implements BaseService {

	@Autowired
	protected IndexProcessor indexProcessor;

	@Autowired
	private UserService userService;

	@Autowired
	private BaseRepository baseRepository;
	
	@Autowired
	private ContentRepository contentRepository;

	
	@Override
	public ContentType getContentType(String type) {
		return (ContentType) this.getBaseRepository().get(ContentType.class, type);
	}

	@Override
	public ResourceType getResourceType(String type) {
		return (ResourceType) this.getBaseRepository().get(ResourceType.class, type);
	}

	@Override
	public AnnotationType getAnnotationType(String type) {
		return  (AnnotationType) baseRepository.get(AnnotationType.class, type);
	}
	
	@Override
	public StatusType getStatusType(String type) {
		return this.getContentRepository().getStatusType(type);
	}

	public IndexProcessor getIndexerMessenger() {
		return indexProcessor;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public UserService getUserService() {
		return userService;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

}
