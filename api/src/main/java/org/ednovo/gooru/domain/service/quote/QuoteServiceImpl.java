/////////////////////////////////////////////////////////////
// QuoteServiceImpl.java
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
package org.ednovo.gooru.domain.service.quote;

import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.utils.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Quote;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.StringUtil;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.annotation.AnnotationService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.QuoteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service("quoteService")
public class QuoteServiceImpl implements QuoteService,ParameterProperties {
	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private ResourceService resourceService;


	@Autowired
	private QuoteRepository quoteRepository;

	@Autowired
	private AnnotationService annotationService;

	@Autowired
	private ResourceImageUtil resourceImageUtil;
	
	@Autowired
	private IndexHandler indexHandler;

	@Override
	public JSONObject createQuote(String title, String description, String url, String category, String shelfId, String licenseName, String pinToken, String sessionToken, User user) throws Exception {

		JSONObject resultJson = new JSONObject();
		URL urlObj = new URL(url);
		String urlAuth = urlObj.getAuthority();
		String urlArr[] = urlAuth.split("\\.");
		String urlHost = urlArr[0];
		if (urlHost.equals(DEVBS1) || urlHost.equals("pearsonbluesky")) {
			throw new RuntimeException("You can't add a BlueSky URL as a resource.");
		}

		if (StringUtils.isEmpty(description.trim())) {
			throw new RuntimeException("Description should not be blank.");
		}
		description = StringUtil.stripSpecialCharacters(description);
		UserToken userToken;
		if (pinToken != null) {
			userToken = this.userTokenRepository.findByToken(pinToken);
		} else {
			userToken = this.userTokenRepository.findByToken(sessionToken);
		}

		if (userToken == null) {
			throw new AccessDeniedException("You are not authorized to perform this action.");
		}

		Resource resource = null;
		/* JSONObject resultJson = new JSONObject(); */
		resource = this.resourceRepository.findWebResource(url);
		if (resource != null) {
			resultJson.put(STATUS, STATUS_200);
			resultJson.put(MESSAGE, "It looks like this resource already exists in \nGooru! We've gone ahead and added it to \nyour Shelf.");
		} else {
			resource = new Resource();
			resource.setGooruOid(UUID.randomUUID().toString());
			ResourceType resourceType = null;
			if (this.getYoutubeVideoId(url) != null) {
				if (category == null) {
					category = VIDEO;
				}
				resourceType = (ResourceType) baseRepository.get(ResourceType.class, ResourceType.Type.VIDEO.getType());
			} else {
				if (category == null) {
					category = WEB_SITE;
				}
				resourceType = (ResourceType) this.baseRepository.get(ResourceType.class, ResourceType.Type.RESOURCE.getType());
			}

			resource.setResourceType(resourceType);
			resource.setUrl(url);
			resource.setTitle(title);
			resource.setCategory(category);
			resource.setDescription(description);
			resource.setSharing(Sharing.PRIVATE.getSharing());
			resource.setUser(user);
			resource.setRecordSource(Resource.RecordSource.QUOTED.getRecordSource());

			License license = null;
			if (licenseName != null) {
				license = (License) this.baseRepository.get(License.class, licenseName);
			}
			resource.setLicense(license);
			Errors errors = new BindException(Resource.class, RESOURCE);
			this.resourceService.saveResource(resource, errors, false);
			// this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource,
			// url);
			this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist(resource);
			this.resourceService.updateResourceInstanceMetaData(resource, user);
			Quote quote = new Quote();
			quote.setTitle(title);
			quote.setLicense(license);
			quote.setAnchor(url);
			quote.setFreetext(description);
			quote.setUser(user);
			quote.setSharing(Sharing.PRIVATE.getSharing());
			quote.setResource(resource);

			annotationService.create(quote, QUOTE, errors);
			// Auto-subscribe the user to the quoted resource
			Annotation annotation = new Annotation();
			annotation.setUser(user);
			annotation.setResource(resource);
			annotationService.create(annotation, SUBSCRIPTION, errors);

			resultJson.put(STATUS, STATUS_200);
			resultJson.put(MESSAGE, "This resource has been added to your Shelf");
			if (resource != null && resource.getContentId() != null) {
				indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);						
			}
		}

		return resultJson;
	}

	private String getYoutubeVideoId(String url) {
		String pattern = "youtu(?:\\.be|be\\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]+)";
		String videoId = null;
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(url);
		while (matcher.find()) {
			videoId = matcher.group(1);
		}
		return videoId;
	}

	@Override
	public Integer findNotesCount(String tag, User user) {
		return quoteRepository.findNotesCount(tag, user);
	}

	@Override
	public List<Quote> findNotes(String tag, User user, int start, int stop) {
		return quoteRepository.findNotes(tag, user, start, stop);
	}

	@Override
	public List<Quote> findNotes(Content context, String mode, User user, int count) {
		return quoteRepository.findNotes(context, mode, user, count);
	}

	@Override
	public List<Quote> findByUser(String userId) {
		return quoteRepository.findByUser(userId);
	}

	@Override
	public Quote findByContent(String gooruContentId) {
		return quoteRepository.findByContent(gooruContentId);
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}
}
