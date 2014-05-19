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
package org.ednovo.gooru.domain.service.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.Tag;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.tag.TagService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("v2Content")
public class ContentServiceImpl extends BaseServiceImpl implements ContentService, ParameterProperties {

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private TagService tagService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Override
	public List<Map<String, Object>> createTagAssoc(String gooruOid, List<String> labels, User apiCaller) {
		List<Map<String, Object>> contentTagAssocs = new ArrayList<Map<String, Object>>();
		Content content = this.contentRepository.findContentByGooruId(gooruOid);
		if (content == null) {
			throw new NotFoundException("content not found!!!");
		}
		deleteTagAssoc(gooruOid, labels, apiCaller);
		for (String label : labels) {
			Tag tag = this.tagRepository.findTagByLabel(label);
			if (tag == null) {
				tag = new Tag();
				tag.setLabel(label);
				tag = this.tagService.createTag(tag, apiCaller).getModel();
			}
			ContentTagAssoc contentTagAssocDb = this.contentRepository.getContentTagById(gooruOid, tag.getGooruOid(), apiCaller.getGooruUId());
			if (contentTagAssocDb == null) {
				ContentTagAssoc contentTagAssoc = new ContentTagAssoc();
				contentTagAssoc.setContentGooruOid(gooruOid);
				contentTagAssoc.setTagGooruOid(tag.getGooruOid());
				contentTagAssoc.setAssociatedUid(apiCaller.getGooruUId());
				contentTagAssoc.setAssociatedDate(new Date(System.currentTimeMillis()));
				this.getContentRepository().save(contentTagAssoc);
				tag.setContentCount(tag.getContentCount() != null ? tag.getContentCount() + 1 : 1);
				this.getContentRepository().save(tag);
				contentTagAssocs.add(setcontentTagAssoc(contentTagAssoc, tag.getLabel()));
			}

		}
		return contentTagAssocs;
	}

	private Map<String, Object> setcontentTagAssoc(ContentTagAssoc contentTagAssoc, String label) {
		Map<String, Object> contentTag = new HashMap<String, Object>();
		contentTag.put("label", label);
		contentTag.put("tagGooruOid", contentTagAssoc.getTagGooruOid());
		contentTag.put("associatedUid", contentTagAssoc.getAssociatedUid());
		contentTag.put("contentGooruOid", contentTagAssoc.getContentGooruOid());
		return contentTag;
	}

	@Override
	public void deleteTagAssoc(String gooruOid, List<String> labels, User apiCaller) {

		Content content = this.contentRepository.findContentByGooruId(gooruOid);
		if (content == null) {
			throw new NotFoundException("content not found!!!");
		}
		for (String label : labels) {
			Tag tag = this.tagRepository.findTagByLabel(label);
			if (tag != null) {
				ContentTagAssoc contentTagAssoc = this.contentRepository.getContentTagById(gooruOid, tag.getGooruOid(), apiCaller.getGooruUId());
				if (contentTagAssoc != null) {
					this.getContentRepository().remove(contentTagAssoc);
					tag.setContentCount(tag.getContentCount() - 1);
					this.getContentRepository().save(tag);
				}
			}
		}
		this.getContentRepository().flush();

	}

	@Override
	public List<Map<String, Object>> getContentTagAssoc(String gooruOid, User user) {
		List<Map<String, Object>> contentList = new ArrayList<Map<String, Object>>();
		List<ContentTagAssoc> contentTagAssocs = this.contentRepository.getContentTagByContent(gooruOid, user.getGooruUId());
		for (ContentTagAssoc contentTagAssoc : contentTagAssocs) {
			Tag tag = this.tagRepository.findTagByTagId(contentTagAssoc.getTagGooruOid());
			contentList.add(setcontentTagAssoc(contentTagAssoc, tag.getLabel()));
		}
		return contentList;
	}

	@Override
	public Content updateContent(String gooruOid, Content newContent) {
		Content content = this.getContentRepository().findContentByGooruId(gooruOid);
		if (content != null) {
			if (newContent.getStatusType() != null && newContent.getStatusType().getValue() != null) {
				CustomTableValue statusType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.CONTENT_STATUS_TYPE.getTable(), newContent.getStatusType().getValue());
				if (statusType != null) {
					content.setStatusType(statusType);
				}
			}
			if (newContent.getIsDeleted() != null) {
				content.setIsDeleted(newContent.getIsDeleted());
			}
			if (newContent.getSharing() != null && (newContent.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newContent.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newContent.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing()))) {
				content.setSharing(newContent.getSharing());
			}
			this.getContentRepository().save(content);
		}
		return content;
	}

	@Override
	public void createContentPermission(Content content, User user) {
		ContentPermission contentPermission = new ContentPermission();
		contentPermission.setContent(content);
		contentPermission.setParty(user);
		contentPermission.setPermission(EDIT);
		contentPermission.setValidFrom(new Date());
		this.getContentRepository().save(contentPermission);
	}

	@Override
	public void deleteContentPermission(Content content, User user) {
		List<ContentPermission> contentPermissions = this.getContentRepository().getContentPermission(content.getContentId(), user.getPartyUid());
		if (contentPermissions != null) {
			this.getContentRepository().removeAll(contentPermissions);
		}
	}

	@Override
	public List<String> getContentPermission(String gooruOid, User apiCaller) {
		List<String> permissions = new ArrayList<String>();
		if (apiCaller != null) {
			Content content = this.getContentRepository().findContentByGooruId(gooruOid, true);
			if (content != null) {
				for (ContentPermission userPermission : content.getContentPermissions()) {

					if (userPermission.getParty().getPartyUid().equals(apiCaller.getPartyUid())) {
						permissions.add(EDIT);
						break;
					}
				}
				if (apiCaller.getGooruUId().equals(content.getUser().getGooruUId()) || this.getUserService().isContentAdmin(apiCaller)) {
					permissions.add(EDIT);
					permissions.add(DELETE);
				}

				if (Sharing.PUBLIC.getSharing().equalsIgnoreCase(content.getSharing())) {
					permissions.add(VIEW);
				}

			}
		} else {
			permissions.add("permission denied");
		}
		return permissions;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public UserService getUserService() {
		return userService;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

}
