/////////////////////////////////////////////////////////////
//CommentRestV2Controller.java
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
/**
 * 
 */
package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Comment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
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
@RequestMapping(value = { "/v2/comment" })
public class CommentRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private CommentService commentService;
	
	private static final int ZERO = 0;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COMMENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createComment(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		
		final ActionResponseDTO<Comment> comment = getCommentService().createComment(this.buildCommentFromInputParameters(data), user );
		if (comment.getErrors().getErrorCount() > ZERO) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		final String includes[] = (String[]) ArrayUtils.addAll(COMMENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(comment.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COMMENT_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ModelAndView updateComment(@PathVariable(value = ID) final String commentUid, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final Comment comment = this.getCommentService().updateComment(commentUid, this.buildCommentFromInputParameters(data), user);
		final String includes[] = (String[]) ArrayUtils.addAll(COMMENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(comment, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COMMENT_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getComment(@PathVariable(value = ID) final String commentUid, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final String includes[] = (String[]) ArrayUtils.addAll(COMMENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getCommentService().getComment(commentUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COMMENT_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getComments(final HttpServletRequest request, @RequestParam(value = GOORU_OID, required = false) final String gooruOid,@RequestParam(value = "itemId", required = false) final String itemId,@RequestParam(value = _GOORU_UID, required = false) final String gooruUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "20") final Integer limit,@RequestParam(value=FETCH_TYPE,required=true,defaultValue="notdeleted") final String fetchType, final HttpServletResponse response) throws Exception {
		final String includes[] = (String[]) ArrayUtils.addAll(COMMENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getCommentService().getCommentsCount(itemId,gooruOid, gooruUid, limit, offset, fetchType), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COMMENT_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteComment(@PathVariable(value = ID) final String commentUid,@RequestParam(value=SOFT_DELETE,required=false,defaultValue=TRUE) final Boolean softdelete, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		this.getCommentService().deleteComment(commentUid, user,softdelete);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	public CommentService getCommentService() {
		return commentService;
	}

	private Comment buildCommentFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Comment.class);
	}

}
