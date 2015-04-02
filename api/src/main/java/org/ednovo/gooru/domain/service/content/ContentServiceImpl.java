/////////////////////////////////////////////////////////////
// ContentServiceImpl.java
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
package org.ednovo.gooru.domain.service.content;

import java.util.List;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.annotation.AnnotationService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.QuoteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("contentService")
public class ContentServiceImpl implements ContentService,ParameterProperties {

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private QuoteRepository quoteRepository;

	@Override
	public Content findByContent(Long contentId) {
		return contentRepository.findByContent(contentId);
	}

	@Override
	public Content findByContentGooruId(String gooruContentId) {
		return contentRepository.findByContentGooruId(gooruContentId);
	}

	@Override
	public void delete(String gooruContentId) {
		contentRepository.delete(gooruContentId);
	}

	@Override
	public Resource findByResourceType(String typeId, String url) {
		return contentRepository.findByResourceType(typeId, url);
	}

	@Override
	public Content findContentByGooruId(String gooruContentId) {
		return contentRepository.findContentByGooruId(gooruContentId);
	}

	@Override
	public Content findContentByGooruId(String gooruContentId, boolean fetchUser) {
		return contentRepository.findContentByGooruId(gooruContentId, fetchUser);
	}

	@Override
	public User findContentOwner(String gooruContentId) {
		return contentRepository.findContentOwner(gooruContentId);
	}

	@Override
	public ContentAssociation getCollectionAssocContent(String contentGooruOid) {
		return contentRepository.getCollectionAssocContent(contentGooruOid);
	}

	@Override
   public List<Object[]> getIdsByUserUId(String userUId, String typeName, Integer pageNo, Integer pageSize) {
           return contentRepository.getIdsByUserUId(userUId, typeName, pageNo, pageSize);
   }
}
