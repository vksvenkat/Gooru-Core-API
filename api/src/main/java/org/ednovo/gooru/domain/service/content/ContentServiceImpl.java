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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.application.util.LogUtil;
import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.core.api.model.AnnotationType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Quote;
import org.ednovo.gooru.core.api.model.QuoteDTO;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.TagType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.StringUtil;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.annotation.AnnotationService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.QuoteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

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

	private static final Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);

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
	public List<QuoteDTO> createNote(String gooruUserId, String description, String url, String noteType, String grade, String title, String licenseName, String topic, String tagTypeName, String classplan, String contextAnchor, String contextAnchorText, User user) {
		AnnotationType annotationType = (AnnotationType) contentRepository.get(AnnotationType.class, noteType);
		ContentType contentTypeResource = (ContentType) contentRepository.get(ContentType.class, ContentType.RESOURCE);
		ContentType contentTypeAnnotation = (ContentType) contentRepository.get(ContentType.class, ContentType.ANNOTATION);

		TagType tagType = null;

		if (!tagTypeName.equals("")) {
			tagType = (TagType) contentRepository.get(TagType.class, tagTypeName);
		}

		Resource resource = null;
		Quote quote = new Quote();

		License license = null;
		if (licenseName != null) {
			license = (License) contentRepository.get(License.class, licenseName);
		}

		if (url != null && !url.equalsIgnoreCase("")) {

			resource = contentRepository.findByResourceType(ResourceType.Type.RESOURCE.getType(), url);

			ResourceType resourceType = (ResourceType) contentRepository.get(ResourceType.class, ResourceType.Type.RESOURCE.getType());

			if (resource == null) {

				UserRole contentAdmin = new UserRole();
				contentAdmin.setRoleId(UserRole.ROLE_CONTENT_ADMIN);

				User resourceUser = userRepository.findByRole(contentAdmin).get(0);

				resource = new Resource();
				resource.setUrl(url);

				resource.setResourceType(resourceType);
				resource.setLicense(license);

				if (description.length() <= 1000) {
					resource.setTitle(description);
				} else {
					resource.setTitle(description.substring(0, 999));
				}

				resource.setContentType(contentTypeResource);
				resource.setSharing(Sharing.PRIVATE.getSharing());
				resource.setGooruOid(UUID.randomUUID().toString());
				resource.setCreatedOn(new Date(System.currentTimeMillis()));
				resource.setLastModified(new Date(System.currentTimeMillis()));
				resource.setUser(resourceUser);
				resource.setRecordSource(Resource.RecordSource.QUOTED.getRecordSource());

				contentRepository.save(resource);
			}
		}

		quote.setTitle(title);
		quote.setGrade(grade);
		quote.setTopic(topic);
		quote.setLicense(license);

		quote.setAnnotationType(annotationType);

		quote.setGooruOid(UUID.randomUUID().toString());
		quote.setUser(user);
		quote.setSharing(Sharing.PRIVATE.getSharing());
		quote.setCreatedOn(new Date(System.currentTimeMillis()));
		quote.setLastModified(new Date(System.currentTimeMillis()));
		quote.setContentType(contentTypeAnnotation);
		quote.setAnchor(url);
		quote.setFreetext(description);
		quote.setResource(resource);
		quote.setTagType(tagType);
		quote.setContextAnchor(contextAnchor);
		quote.setContextAnchorText(contextAnchorText);

		if (classplan != null && !classplan.equals("")) {
			Content classplanResource = contentRepository.findContentByGooruId(classplan);
			quote.setContext(classplanResource);
		}

		contentRepository.save(quote);

		if (logger.isInfoEnabled()) {

			logger.info(LogUtil.getActivityLogStream(CLASS_PLAN, user.toString(), quote.toString() + quote.getGooruOid(), LogUtil.QUOTE_CREATE, ""));
		}

		List<QuoteDTO> quoteDTOList = new ArrayList<QuoteDTO>();
		QuoteDTO quoteDto = new QuoteDTO();
		quoteDto.setAnchor(quote.getAnchor());
		quoteDto.setGooruOid(quote.getGooruOid());
		quoteDto.setText(quote.getFreetext());

		if (quote.getTagType() == null) {
			quoteDto.setTag("");
		} else {
			quoteDto.setTag(quote.getTagType().getName());
		}

		if (quote.getResource() == null) {
			quoteDto.setUrl("");
		} else {
			quoteDto.setUrl(quote.getResource().getUrl());
		}
		quoteDto.setUser(quote.getUser());
		quoteDto.setSharing(quote.getSharing());
		quoteDto.setContextAnchorText(quote.getContextAnchorText());

		quoteDTOList.add(quoteDto);
		return quoteDTOList;
	}

	@Override
	public String createQuote(String gooruUserId, String description, String url, String pinToken, String sessionToken, String noteType, String grade, String title, String licenseName, String topic, String classplan, String tagTypeName, User user) {

		if (description != null) {
			description = StringUtil.stripSpecialCharacters(description);
		}
		if (grade != null) {
			grade = StringUtil.stripSpecialCharacters(grade);
		}
		if (topic != null) {
			topic = StringUtil.stripSpecialCharacters(topic);
		}

		UserToken userToken;
		if (pinToken != null) {
			userToken = userTokenRepository.findByToken(pinToken);
		} else {
			userToken = userTokenRepository.findByToken(sessionToken);
		}

		if (userToken == null) {
			throw new AccessDeniedException("You are not authorized to perform this action.");
		}

		Resource resource = null;
		License license = null;
		if (licenseName != null) {
			license = (License) contentRepository.get(License.class, licenseName);
		}

		if (url != null && !url.equalsIgnoreCase("")) {
			resource = resourceRepository.findWebResource(url);

			if (resource == null) {
				UserRole contentAdmin = new UserRole();
				contentAdmin.setRoleId(UserRole.ROLE_CONTENT_ADMIN);

				User resourceUser = userRepository.findByRole(contentAdmin).get(0);

				ResourceType resourceType = (ResourceType) contentRepository.get(ResourceType.class, ResourceType.Type.RESOURCE.getType());
				resource = new Resource();
				resource.setUrl(url);
				String category = WEB_SITE;
				if (url.contains(YOU_TUBE)) {
					category = VIDEO;
					resourceType = (ResourceType) contentRepository.get(ResourceType.class, ResourceType.Type.VIDEO.getType());
				}

				resource.setResourceType(resourceType);
				resource.setLicense(license);
				resource.setTitle(title);
				resource.setCategory(category);
				resource.setDescription(description);
				resource.setSharing(Sharing.PRIVATE.getSharing());
				resource.setUser(resourceUser);
				resource.setRecordSource(Resource.RecordSource.QUOTED.getRecordSource());

				Errors errors = new BindException(Resource.class, RESOURCE);
				resourceService.saveResource(resource, errors, false);
				resourceService.updateResourceInstanceMetaData(resource, user);
				// FIXME Handle possible validation errors
			}
		}

		TagType tagType = null;

		if (tagTypeName != null && !tagTypeName.equals("")) {
			tagType = (TagType) contentRepository.get(TagType.class, tagTypeName);
		}

		Quote quote = new Quote();
		quote.setTitle(title);
		quote.setGrade(grade);
		quote.setTopic(topic);
		quote.setLicense(license);
		quote.setAnchor(url);
		quote.setFreetext(description);
		quote.setUser(user);
		quote.setSharing(Sharing.PRIVATE.getSharing());
		quote.setResource(resource);
		quote.setTagType(tagType);

		if (classplan != null && !classplan.equals("")) {
			Content classplanResource = contentRepository.findContentByGooruId(classplan);
			quote.setContext(classplanResource);
		}

		Errors errors = new BindException(Annotation.class, "error");
		annotationService.create(quote, noteType, errors);

		// Auto-subscribe the user to the quoted resource
		Annotation annotation = new Annotation();
		annotation.setUser(user);
		annotation.setResource(resource);
		annotationService.create(annotation, SUBSCRIPTION, errors);

		if (logger.isInfoEnabled()) {
			logger.info(LogUtil.getActivityLogStream(CLASS_PLAN, user.toString(), quote.toString() + quote.getGooruOid(), LogUtil.QUOTE_CREATE, ""));
		}

		User apiCaller = userRepository.findByGooruId(user.getPartyUid());
		String externalId = null;
		if (apiCaller.getIdentities() != null) {
			for (Identity identity : apiCaller.getIdentities()) {
				if (identity.getExternalId() != null) {
					externalId = identity.getExternalId();
					break;
				}
			}
		}
		return externalId;

	}

	@Override
	public List<QuoteDTO> updateNote(String gooruUserId, String gooruContentId, String description, String url, String noteType, String grade, String title, String licenseName, String topic, String tagTypeName, String anchor, User user) {

		ContentType contentTypeResource = (ContentType) contentRepository.get(ContentType.class, ContentType.RESOURCE);
		TagType tagType = null;

		if (!tagTypeName.equals("")) {
			tagType = (TagType) contentRepository.get(TagType.class, tagTypeName);
		}

		Resource resource = null;
		// User user = null;
		Quote quote = null;

		License license = null;
		if (licenseName != null) {
			license = (License) contentRepository.get(License.class, licenseName);
		}

		// /user = userToken.getUser();
		if (url != null && !url.equalsIgnoreCase("")) {

			resource = contentRepository.findByResourceType(ResourceType.Type.RESOURCE.getType(), url);

			ResourceType resourceType = (ResourceType) contentRepository.get(ResourceType.class, ResourceType.Type.RESOURCE.getType());

			if (resource == null) {

				UserRole contentAdmin = new UserRole();
				contentAdmin.setRoleId(UserRole.ROLE_CONTENT_ADMIN);

				User resourceUser = userRepository.findByRole(contentAdmin).get(0);

				resource = new Resource();
				resource.setUrl(url);

				resource.setResourceType(resourceType);
				resource.setLicense(license);

				if (description.length() <= 1000) {
					resource.setTitle(description);
				} else {
					resource.setTitle(description.substring(0, 999));
				}

				resource.setContentType(contentTypeResource);
				resource.setSharing(Sharing.PRIVATE.getSharing());
				resource.setGooruOid(UUID.randomUUID().toString());
				resource.setCreatedOn(new Date(System.currentTimeMillis()));
				resource.setLastModified(new Date(System.currentTimeMillis()));
				resource.setUser(resourceUser);

				contentRepository.save(resource);
			}
		}

		quote = (Quote) contentRepository.findContentByGooruId(gooruContentId);

		quote.setTitle(title);
		quote.setGrade(grade);
		quote.setTopic(topic);
		quote.setLicense(license);

		quote.setLastModified(new Date(System.currentTimeMillis()));
		quote.setAnchor(url);
		quote.setFreetext(description);
		quote.setResource(resource);
		quote.setTagType(tagType);

		contentRepository.save(quote);

		if (logger.isInfoEnabled()) {

			// activity.info(LogUtil.getActivityLogStream("classplan",
			// user.toString(), annotation.getAnnotationType().toString() +
			// annotation.getGooruOid(), LogUtil.QUOTE_EDIT));
		}

		List<QuoteDTO> quoteDTOList = new ArrayList<QuoteDTO>();
		QuoteDTO quoteDto = new QuoteDTO();
		quoteDto.setAnchor(quote.getAnchor());
		quoteDto.setGooruOid(quote.getGooruOid());
		quoteDto.setText(quote.getFreetext());

		if (quote.getTagType() == null) {
			quoteDto.setTag("");
		} else {
			quoteDto.setTag(quote.getTagType().getName());
		}

		if (quote.getResource() == null) {
			quoteDto.setUrl("");
		} else {
			quoteDto.setUrl(quote.getResource().getUrl());
		}
		quoteDto.setUser(quote.getUser());
		quoteDto.setSharing(quote.getSharing());
		quoteDto.setContextAnchorText(quote.getContextAnchorText());

		quoteDTOList.add(quoteDto);
		return quoteDTOList;

	}

	@Override
	public Quote saveAnnotation(Quote annotation) {
		contentRepository.save(annotation);
		return null;
	}

	@Override
	public List<QuoteDTO> copyNote(String noteId, User apiCaller) {

		Quote annotation = (Quote) quoteRepository.findByContent(noteId);

		Quote quote = new Quote();
		quote.setAnchor(annotation.getAnchor());
		quote.setAnnotationType(annotation.getAnnotationType());
		quote.setResource(annotation.getResource());
		quote.setFreetext(annotation.getFreetext());

		quote.setContentType(annotation.getContentType());
		quote.setCreatedOn(new Date(System.currentTimeMillis()));
		quote.setGooruOid(UUID.randomUUID().toString());
		quote.setLastModified(new Date(System.currentTimeMillis()));
		quote.setUser(apiCaller);
		quote.setSharing(PRIVATE);
		quote.setContextAnchor(annotation.getContextAnchor());
		quote.setContextAnchorText(annotation.getContextAnchorText());

		quote.setContext(annotation.getContext());
		quote.setGrade(annotation.getGrade());
		quote.setTopic(annotation.getTopic());
		quote.setTitle(annotation.getTitle());
		quote.setLicense(annotation.getLicense());
		quote.setTagType(annotation.getTagType());

		contentRepository.save(quote);

		List<QuoteDTO> quoteDTOList = new ArrayList<QuoteDTO>();
		QuoteDTO quoteDto = new QuoteDTO();
		quoteDto.setAnchor(quote.getAnchor());
		quoteDto.setGooruOid(quote.getGooruOid());
		quoteDto.setText(quote.getFreetext());

		if (quote.getTagType() == null) {
			quoteDto.setTag("");
		} else {
			quoteDto.setTag(quote.getTagType().getName());
		}

		if (quote.getResource() == null) {
			quoteDto.setUrl("");
		} else {
			quoteDto.setUrl(quote.getResource().getUrl());
		}
		quoteDto.setUser(quote.getUser());
		quoteDto.setSharing(quote.getSharing());
		quoteDto.setContextAnchorText(quote.getContextAnchorText());

		quoteDTOList.add(quoteDto);
		return quoteDTOList;
	}

	@Override
	public void deleteNote(String noteId) {
		contentRepository.delete(noteId);
	}

	@Override
	public List getIdsByUserUId(String userUId, String typeName, Integer pageNo, Integer pageSize) {
		return contentRepository.getIdsByUserUId(userUId, typeName, pageNo, pageSize);
	}

}
