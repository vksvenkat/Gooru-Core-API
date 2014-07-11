/////////////////////////////////////////////////////////////
//PostRestV2Controller.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Post;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.PostService;
import org.ednovo.gooru.domain.service.comment.CommentService;
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

@Controller
@RequestMapping(value = { "/v2/post", "/v2/review", "/v2/response", "/v2/question-board", "/v2/note" })
public class PostRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private CommentService commentService;

	@Autowired
	private PostService postService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "")
	public ModelAndView createPost(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Post post = this.getPostService().createPost(this.buildPostFromInputParameters(data, request), user);
		SessionContextSupport.putLogParameter(EVENT_NAME, CREATE_POST);
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		return toModelAndViewWithIoFilter(post, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, POST_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updatePost(@RequestBody String data, @PathVariable(value = ID) String postId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Post post = this.getPostService().updatePost(postId, this.buildPostFromInputParameters(data, request));
		SessionContextSupport.putLogParameter(EVENT_NAME,UPDATE_POST);
		SessionContextSupport.putLogParameter(POST_ID, postId);
		return toModelAndViewWithIoFilter(post, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, POST_INCLUDE_FIELDS);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getPost(HttpServletRequest request, @PathVariable(value = ID) String postId, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(this.getPostService().getPost(postId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, POST_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "")
	public ModelAndView getPosts(@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getPostService().getPosts(user, getPostType(request), limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, POST_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deletePost(@PathVariable(value = ID) String postId, HttpServletRequest request, HttpServletResponse response) throws Exception {

		this.getPostService().deletePost(postId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_POST_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/comment")
	public ModelAndView getPostComments(HttpServletRequest request, @PathVariable(value = ID) String gooruOid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") Integer limit,
			HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(this.getCommentService().getComments(gooruOid, null, limit, offset,"notdelted"), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, POST_INCLUDE_FIELDS);
	}

	private String getPostType(HttpServletRequest request) {
		if (request.getPathInfo() != null) {
			String path = request.getPathInfo();
			return path.substring(path.lastIndexOf('/') + 1);
		}
		return null;
	}

	private Post buildPostFromInputParameters(String data, HttpServletRequest request) {
		Post post = JsonDeserializer.deserialize(data, Post.class);
		CustomTableValue postType = new CustomTableValue();
		postType.setValue(getPostType(request));
		post.setType(postType);
		return post;
	}

	public PostService getPostService() {
		return postService;
	}

	public CommentService getCommentService() {
		return commentService;
	}

}
