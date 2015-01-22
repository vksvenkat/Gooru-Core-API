/////////////////////////////////////////////////////////////
// CommentServiceImpl.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.MailAsyncExecutor;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Comment;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.PartyRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.question.CommentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CommentServiceImpl extends BaseServiceImpl implements CommentService, ParameterProperties, ConstantProperties {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private MailAsyncExecutor mailAsyncExecuter;

	@Autowired
	private PartyRepository partyRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CollectionRepository collectionRepository;
	
	@Autowired 
	private ContentService contentService;

	@Override
	public ActionResponseDTO<Comment> createComment(Comment comment, User user) {
		final Errors error = validateComment(comment);

		if (!error.hasErrors()) {
			comment.setCreatedOn(new Date());
			comment.setStatus(getCustomTableRepository().getCustomTableValue(CustomProperties.Table.COMMNET_STATUS.getTable(), CustomProperties.CommentStatus.ACTIVE.getCommentStatus()));
			comment.setCommentorUid(user);
			comment.setOrganization(user.getPrimaryOrganization());
			this.getCommentRepository().save(comment);
			if (comment.getItemId() == null) {
				commentMailNotification(comment, user);
			}
		}
		return new ActionResponseDTO<Comment>(comment, error);
	}

	@Override
	public Comment updateComment(String commentUid, Comment newComment, User user) {
		Comment comment = this.getComment(commentUid);
		if (comment != null) {
			if (newComment.getComment() != null) {
				comment.setComment(newComment.getComment());
			}
			comment.setLastModifiedOn(new Date());
			CustomTableValue customTableValue = null;
			if (newComment.getStatus() != null) {
				customTableValue = (newComment.getStatus() != null && newComment.getStatus().getValue() == ABUSE) ? getCustomTableRepository().getCustomTableValue(CustomProperties.Table.COMMNET_STATUS.getTable(), CustomProperties.CommentStatus.ABUSE.getCommentStatus()) : getCustomTableRepository()
						.getCustomTableValue(CustomProperties.Table.COMMNET_STATUS.getTable(), newComment.getStatus().getValue());
			} else {
				customTableValue = getCustomTableRepository().getCustomTableValue(CustomProperties.Table.COMMNET_STATUS.getTable(), CustomProperties.CommentStatus.ACTIVE.getCommentStatus());
			}
			rejectIfNull(customTableValue, GL0007, COMMENT__STATUS);
			comment.setStatus(customTableValue);
			this.getCommentRepository().save(comment);
			//commentMailNotification(comment, user);
		}
		return comment;
	}

	@Override
	public Comment getComment(String commentUid) {
		Comment comment = this.getCommentRepository().getComment(commentUid);
		if (comment == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, COMMENT), GL0056);
		}
		return comment;
	}

	@Override
	public List<Comment> getComments(String itemId,String gooruOid, String gooruUid, Integer limit, Integer offset, String fetchType) {
		return this.getCommentRepository().getComments(itemId,gooruOid, gooruUid, limit, offset, fetchType);
	}

	@Override
	public SearchResults<Comment> getCommentsCount(String itemId,String gooruOid, String gooruUid, Integer limit, Integer offset, String fetchType) {
		List<Comment> comments = this.getCommentRepository().getComments(itemId,gooruOid, gooruUid, limit, offset, fetchType);
		SearchResults<Comment> result = new SearchResults<Comment>();
		result.setSearchResults(comments);
		result.setTotalHitCount(this.getCommentRepository().getCommentCount(itemId,gooruOid, gooruUid, fetchType));
		return result;
	}

	@Override
	public void deleteComment(String commentUid, User user, Boolean softdelete) {
		Comment comment = this.getCommentRepository().getComment(commentUid);
		rejectIfNull(comment, COMMENT, GL0057, generateErrorMessage(GL0057, COMMENT));
		Content content = this.getResourceRepository().findResourceByContent(comment.getGooruOid());
		List<String> contentPermissions = contentService.getContentPermission(content, user);
		boolean hasPermission = false;
		for (String contentPermission : contentPermissions) {
			if (contentPermission.equalsIgnoreCase(EDIT)) {
				hasPermission = true;
				break;
			}
		}
		if (!hasPermission && content != null && content.getUser().getPartyUid().equalsIgnoreCase(user.getPartyUid())) { 
			hasPermission = true;
		} else if (comment.getCommentorUid().getPartyUid().equalsIgnoreCase(user.getPartyUid())) { 
			hasPermission = true;
		}
		
		if (hasPermission) {
			if (softdelete) {
				comment.setIsDeleted(true);
				this.getCommentRepository().save(comment);
			} else {
				this.getCommentRepository().remove(comment);
			}
		} else  {
			throw new AccessDeniedException("Permission denied ");
		} 

	}
	
	@Override
	public Boolean isContentOwner(String commentUid, User user) {
		Boolean isContentOwner = false;
		final Comment comment = this.getCommentRepository().getComment(commentUid);
		if (comment != null) {
			Collection collection = this.getCollectionRepository().getCollectionByGooruOid(comment.getGooruOid(), null);
			if(collection != null && collection.getUser()!= null && collection.getUser().equals(user)){
					isContentOwner = true;
			}
		}
		return isContentOwner;
	}
	
	public void commentMailNotification(Comment comment, User user) {
		Map<String, String> commentData = new HashMap<String, String>();
		if (comment.getComment() != null) {

			commentData.put(COMMENT_TEXT, comment.getComment());
		}
		if (user.getUsername() != null) {
			commentData.put(USERNAME, user.getUsername());
		}
		if (comment.getGooruOid() != null) {
			commentData.put(COLLECTION_ID, comment.getGooruOid());
		}
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(comment.getGooruOid(), null);
		if (collection != null) {
			PartyCustomField partyCustomField = this.getPartyRepository().getPartyCustomField(collection.getUser().getGooruUId(), COLLECTION_COMMENT_EMAIL_NOTIFICATION);
			if (!collection.getUser().getPartyUid().equalsIgnoreCase(user.getPartyUid()) && partyCustomField != null && partyCustomField.getOptionalValue().equals(TRUE) && collection.getMailNotification()) {
				this.getMailAsyncExecuter().sendEmailNotificationforComment(commentData);
			}
		}
	}

	private Errors validateComment(Comment comment) {
		final Errors errors = new BindException(comment, COMMENT);
		rejectIfNull(errors, comment, COMMENT, GL0056, generateErrorMessage(GL0056, COMMENT));
		rejectIfNullOrEmpty(errors, comment.getComment(), COMMENT, GL0006, generateErrorMessage(GL0006, COMMENT));
		return errors;
	}

	public CommentRepository getCommentRepository() {
		return commentRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public UserService getUserService() {
		return userService;
	}

	public MailAsyncExecutor getMailAsyncExecuter() {
		return mailAsyncExecuter;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public PartyRepository getPartyRepository() {
		return partyRepository;
	}

}
