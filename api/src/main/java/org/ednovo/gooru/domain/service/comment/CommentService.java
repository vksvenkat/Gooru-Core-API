/////////////////////////////////////////////////////////////
// CommentService.java
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
package org.ednovo.gooru.domain.service.comment;

import java.util.List;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Comment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.BaseService;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface CommentService extends BaseService {

	ActionResponseDTO<Comment> createComment(Comment comment, User user);

	Comment updateComment(String commentUid, Comment newComment, User user);

	Comment getComment(String commentUid);

	List<Comment> getComments(String itemId, String gooruOid, String gooruUid, Integer limit, Integer offset,String fetchType);
	
	SearchResults<Comment> getCommentsCount(String itemId,String gooruOid, String gooruUid, Integer limit, Integer offset,String fetchType);

	void deleteComment(String commentUid, User user,Boolean softdelete);
	
	Boolean isContentOwner(String commentUid, User user);
}
