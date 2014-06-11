/////////////////////////////////////////////////////////////
//ContentRestV2Controller.java
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
package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.FeedbackService;
import org.ednovo.gooru.domain.service.PostService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping("/v2/content")
public class ContentRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	@Resource(name = "v2Content")
	private ContentService contentService;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private PostService postService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/{id}/tag")
	public ModelAndView createContentTagAssoc(@PathVariable(value = ID) String gooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		
		return toModelAndView(this.contentService.createTagAssoc(gooruOid, JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}),apiCaller), FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{id}/tag")
	public void deleteContentTagAssoc(@PathVariable(value = ID) String gooruOid,@RequestParam String data, HttpServletRequest request, HttpServletResponse response) {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		
		this.getContentService().deleteTagAssoc(gooruOid, JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}),apiCaller);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ})
	@RequestMapping(method = { RequestMethod.GET }, value = "/{id}/tag")
	public ModelAndView getContentTagAssoc(@PathVariable(value = ID) String gooruOid, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, HttpServletRequest request,
			HttpServletResponse response) {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		
		return toModelAndView(this.getContentService().getContentTagAssoc(gooruOid, apiCaller), FORMAT_JSON);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@RequestMapping(method = { RequestMethod.GET }, value = "/tag/{id}")
	public ModelAndView getUserContentTagList(@PathVariable(value = ID) String gooruUid, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, HttpServletRequest request,
			HttpServletResponse response, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") Boolean skipPagination) {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		if(gooruUid.equalsIgnoreCase(MY)) {
			gooruUid = apiCaller.getPartyUid();
		}
		
		return toModelAndView(serialize(this.getContentService().getUserContentTagList(gooruUid,limit,offset,skipPagination), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, USER_CONTENT_TAGS_INCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = { "/{id}/post", "/{id}/review", "/{id}/response", "/{id}/question-board", "/{id}/note" })
	public ModelAndView getContentPosts(@PathVariable(value = ID) String gooruOid, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, HttpServletRequest request,
			HttpServletResponse response) {

		return toModelAndViewWithIoFilter(this.getPostService().getContentPosts(gooruOid, limit, offset, getPostType(request)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COMMENT_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FEEDBACK_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating/{type}", "/{id}/report/{type}", "/{id}/flag/{type}", "/{id}/reaction/{type}" })
	public ModelAndView getContentFeedbacks(HttpServletRequest request, @PathVariable(value = ID) String assocContentUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit, @RequestParam(value = ORDER_BY, required = false) String orderBy, @RequestParam(value = CREATOR_UID, required = false) String creatorUid, @PathVariable(value = TYPE) String feedbackType, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(FEEDBACK_INCLUDE_FIELDS, ERROR_INCLUDE);
		if (feedbackType.equalsIgnoreCase(AVERAGE)) {
			return toJsonModelAndView(this.getFeedbackService().getContentFeedbackAverage(assocContentUid, getFeedbackCategory(request)), true);
		} else if (feedbackType.equalsIgnoreCase(AGGREGATE)) {
			return toJsonModelAndView(this.getFeedbackService().getContentFeedbackAggregate(assocContentUid, getFeedbackCategory(request)), true);
		}
		return toModelAndViewWithIoFilter(this.getFeedbackService().getContentFeedbacks(getFeedbackCategory(request), feedbackType, assocContentUid, creatorUid, limit, offset, false,orderBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_FEEDBACK_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating", "/{id}/report", "/{id}/flag", "/{id}/reaction" })
	public ModelAndView getContentFeedbacksByCategory(HttpServletRequest request, @PathVariable(value = ID) String assocContentUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") Boolean skipPagination, @RequestParam(value = ORDER_BY, required = false) String orderBy, @RequestParam(value = CREATOR_UID, required = false) String creatorUid,
			HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(FEEDBACK_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getFeedbackService().getContentFeedbacks(getFeedbackCategory(request), null, assocContentUid, creatorUid, limit, offset, skipPagination, orderBy).getSearchResults(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/rating/{type}/count", "/{id}/report/{type}/count", "/{id}/flag/{type}/count", "/{id}/reaction/{type}/count" })
	public ModelAndView getContentFeedback(HttpServletRequest request, @PathVariable(value = TYPE) String type, @PathVariable(value = ID) String assocGooruOid, HttpServletResponse response) throws Exception {
		String category = getFeedbackCategory(request);
		if (category.contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && type.contains(CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType())) {
			return toJsonModelAndView(this.getFeedbackService().getContentFeedbackStarRating(assocGooruOid), true);
		} else if (category.contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && type.contains(CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType())) {
			return toJsonModelAndView(this.getFeedbackService().getContentFeedbackThumbRating(assocGooruOid), true);
		} else {
			return toJsonModelAndView(this.getFeedbackService().getContentFeedbackAggregateByType(assocGooruOid, type), true);
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = { RequestMethod.PUT })
	public ModelAndView updateContent(@PathVariable(value = ID) String gooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {

		Content content = this.getContentService().updateContent(gooruOid, this.buildContentFromInputParameters(data));

		return toModelAndView(serialize(content, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CONTENT_INCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/{id}/check-access")
	public ModelAndView checkContentAccess(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, HttpServletResponse response) throws Exception {

		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndView(serialize(this.getContentService().getContentPermission(gooruOid, apiCaller), RESPONSE_FORMAT_JSON));
	}

	private String getFeedbackCategory(HttpServletRequest request) {
		String category = null;
		if (request != null && request.getRequestURL() != null) {
			if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.RATING.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.FLAG.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.FLAG.getFeedbackCategory();
			} else if (request.getRequestURL().toString().contains(CustomProperties.FeedbackCategory.REACTION.getFeedbackCategory())) {
				category = CustomProperties.FeedbackCategory.REACTION.getFeedbackCategory();
			}
		}
		ServerValidationUtils.rejectIfNull(category, GL0007, " request path ");
		return category;
	}

	private String getPostType(HttpServletRequest request) {
		if (request.getPathInfo() != null) {
			String path = request.getPathInfo();
			return path.substring(path.lastIndexOf('/') + 1);
		}
		return null;
	}

	private Content buildContentFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Content.class);
	}

	public ContentService getContentService() {
		return contentService;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

	public PostService getPostService() {
		return postService;
	}
}
