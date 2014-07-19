/////////////////////////////////////////////////////////////
// PostServiceImpl.java
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Post;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.cassandra.service.BlackListWordCassandraService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.PostRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends BaseServiceImpl implements PostService, ConstantProperties, ParameterProperties {

	@Autowired
	CollectionService collectionService;

	@Autowired
	ContentRepository contentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserService userService;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private BlackListWordCassandraService blackListWordCassandraService;

	@Override
	public Post createPost(Post post, User user) {
		rejectIfNull(post.getFreeText(), GL0006, CONTENT_TEXT);
		rejectIfNull(post.getType(), GL0006, TYPE);
		CustomTableValue customTableValue = getBlackListWordCassandraService().validate(post.getFreeText()) ? getCustomTableRepository().getCustomTableValue(CustomProperties.Table.POST_STATUS.getTable(), CustomProperties.PostStatus.ABUSE.getPostStatus()) : getCustomTableRepository()
				.getCustomTableValue(CustomProperties.Table.POST_STATUS.getTable(), CustomProperties.PostStatus.ACTIVE.getPostStatus());
		rejectIfNull(customTableValue, GL0007, POST_STATUS);
		post.setStatus(customTableValue);
		CustomTableValue type = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.POST_TYPE.getTable(), post.getType().getValue());
		rejectIfNull(type, GL0007, type.getValue() + TYPE);
		if (post.getTarget() != null && post.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.USER.getTarget())) {
			rejectIfNull(post.getAssocUserUid(), GL0006,  _USER );
			User assocUser = this.getUserRepository().findByGooruId(post.getAssocUserUid());
			rejectIfNull(assocUser, GL0056, _USER);
			post.setTarget(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.TARGET.getTable(), CustomProperties.Target.USER.getTarget()));
		}
		if (post.getTarget() != null && post.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.CONTENT.getTarget())) {
			rejectIfNull(post.getAssocGooruOid(), GL0006,CONTENT);
			Content assocontent = this.getContentRepository().findContentByGooruId(post.getAssocGooruOid());
			rejectIfNull(assocontent, GL0056, _CONTENT);
			post.setTarget(this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.TARGET.getTable(), CustomProperties.Target.CONTENT.getTarget()));
		}
		post.setType(type);
		post.setGooruOid(UUID.randomUUID().toString());
		post.setSharing(Sharing.PRIVATE.getSharing());
		ContentType contentType = getCollectionService().getContentType(ContentType.POST);
		post.setContentType(contentType);
		post.setCreator(user);
		post.setUser(user);
		post.setOrganization(user.getPrimaryOrganization());
		post.setCreatedOn(new Date());
		this.getPostRepository().save(post);
		return post;
	}

	@Override
	public Post updatePost(String postId, Post newPost) {
		Post post = this.getPostRepository().getPost(postId);
		rejectIfNull(post, GL0056, newPost.getType().getValue());
		if (newPost.getFreeText() != null) {
			post.setFreeText(newPost.getFreeText());
		}
		if (newPost.getTitle() != null) {
			post.setTitle(newPost.getTitle());
		}
		this.getPostRepository().save(post);
		return post;
	}

	@Override
	public Post getPost(String postId) {
		Post post = this.getPostRepository().getPost(postId);
		rejectIfNull(post, GL0056);
		return post;
	}

	@Override
	public List<Post> getPosts(User user, String type, Integer limit, Integer offset) {
		if (userService.isContentAdmin(user)) {
			return this.getPostRepository().getPosts(type, limit, offset);
		}
		throw new UnauthorizedException("Dont have a permission");
	}

	@Override
	public List<Post> getUserPosts(String gooruUid, Integer limit, Integer offset, String type) {
		rejectIfNull(type, GL0006, TYPE);
		CustomTableValue Usertype = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.POST_TYPE.getTable(), type);
		rejectIfNull(type, GL0007, Usertype.getValue() + TYPE);
		return this.getPostRepository().getUserPosts(gooruUid, limit, offset);
	}

	@Override
	public List<Post> getContentPosts(String gooruOid, Integer limit, Integer offset, String type) {
		rejectIfNull(type, GL0006, TYPE);
		CustomTableValue contentType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.POST_TYPE.getTable(), type);
		rejectIfNull(type, GL0007, contentType.getValue() + TYPE);
		return this.getPostRepository().getContentPosts(gooruOid, limit, offset);
	}

	@Override
	public void deletePost(String postId) {
		Post post = this.getPost(postId);
		rejectIfNull(post, GL0056);
		this.getPostRepository().remove(post);
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public PostRepository getPostRepository() {
		return postRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public BlackListWordCassandraService getBlackListWordCassandraService() {
		return blackListWordCassandraService;
	}

}
