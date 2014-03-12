/////////////////////////////////////////////////////////////
// AnnotationServiceImpl.java
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
package org.ednovo.gooru.domain.service.annotation;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.UserContentRelationshipUtil;
import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.core.api.model.AnnotationType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.UserContentAssoc.RELATIONSHIP;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.SubscriptionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class AnnotationServiceImpl implements AnnotationService,ParameterProperties {
	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private CollectionUtil collectionUtil;

	@Override
	public void create(Annotation annotation, String type, Errors errors) {
		if (annotation.getUser() == null) {
			errors.reject(USER, INVALID_USER);
		}
		if (annotation.getResource() == null || annotation.getResource().getGooruOid() == null) {
			errors.reject(RESOURCE, INVALID_RESOURCE);
		}
		if (type != null && type.equalsIgnoreCase(SUBSCRIPTION)) {
			boolean hasUserSubscribedToUserContent = this.getSubscriptionRepository().hasUserSubscribedToUserContent(annotation.getUser().getPartyUid(), annotation.getResource().getGooruOid());
			if (hasUserSubscribedToUserContent) {
				errors.reject(GET_GOORU_OID, ALREADY_SUBSCRIBED);
			}
		}
		if (errors.hasErrors()) {
			return;
		}

		AnnotationType annotationType = (AnnotationType) this.getBaseRepository().get(AnnotationType.class, type);
		ContentType contentTypeAnnotation = (ContentType) this.getBaseRepository().get(ContentType.class, ContentType.ANNOTATION);

		annotation.setGooruOid(UUID.randomUUID().toString());
		annotation.setAnnotationType(annotationType);
		annotation.setContentType(contentTypeAnnotation);
		annotation.setSharing(StringUtils.defaultIfEmpty(annotation.getSharing(), Sharing.PRIVATE.getSharing()));
		annotation.setCreatedOn(new Date(System.currentTimeMillis()));
		annotation.setLastModified(new Date(System.currentTimeMillis()));

		this.getContentRepository().save(annotation);

		UserContentRelationshipUtil.updateUserContentRelationship(annotation.getResource(), annotation.getUser(), (type.equals(AnnotationType.Type.SUBSCRIPTION.getType())) ? RELATIONSHIP.SUBSCRIBE : RELATIONSHIP.QUOTE);

	}

	@Override
	public Object getSubscriptionsForContent(String gooruContentId) throws JSONException {
		List<HashMap<String, String>> subscriptionsMap = this.collectionUtil.getSubscribtionUserList(gooruContentId);
		Iterator<HashMap<String, String>> subcriptionIterator = subscriptionsMap.iterator();
		JSONArray subcriptions = new JSONArray();
		JSONObject subcriptionJson = new JSONObject();

		while (subcriptionIterator.hasNext()) {
			HashMap<String, String> hMap = subcriptionIterator.next();
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(SUBSCRIBE_USER_ID, hMap.get(SCB_USER_ID)).put(SUBSCRIBED_ON, hMap.get(SUBSCRIBED_ON)).put(CONTENT_USER_ID, hMap.get(CONT_USER_ID)).put(CONT_FIRST_NAME, hMap.get(CONT_FIRSTNAME)).put(CONT_LAST_NAME, hMap.get(CONT_LASTNAME));
			subcriptions.put(jsonObj);
		}
		subcriptionJson.put(SUBSCRIPTIONS, subcriptions);
		return subcriptionJson;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public void setContentRepository(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public void setBaseRepository(BaseRepository baseRepository) {
		this.baseRepository = baseRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	public SubscriptionRepository getSubscriptionRepository() {
		return subscriptionRepository;
	}

	public void setSubscriptionRepository(SubscriptionRepository subscriptionRepository) {
		this.subscriptionRepository = subscriptionRepository;
	}

	@Override
	public boolean hasUserSubscribedToUserContent(String userId, String gooruContentId) {
		return getSubscriptionRepository().hasUserSubscribedToUserContent(userId, gooruContentId);
	}

}
