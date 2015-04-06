/////////////////////////////////////////////////////////////
// CollaboratorServiceImpl.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.MailAsyncExecutor;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.eventlogs.CollaboratorEventLog;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollaboratorServiceImpl extends BaseServiceImpl implements CollaboratorService, ParameterProperties, ConstantProperties {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CollaboratorEventLog collaboratorEventLog;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private MailAsyncExecutor mailAsyncExecutor;
	
	@Autowired
	private InviteRepository inviteRepository;
	
	@Autowired
	private AsyncExecutor asyncExecutor;
	
	@Autowired
	private IndexHandler indexHandler;
	
	private final Logger LOGGER = LoggerFactory.getLogger(CollaboratorServiceImpl.class);

	@Override
	public List<Map<String, Object>> addCollaborator(final List<String> email, final String gooruOid, final User apiCaller, final boolean sendInvite) throws Exception {
		Content content = null;
		if (gooruOid != null) {
			content = getContentRepository().findContentByGooruId(gooruOid, true);
			if (content == null) {
				throw new NotFoundException(generateErrorMessage("GL0056", "content"), GL0056);
			}
		} else {
			throw new BadRequestException(generateErrorMessage("GL0088"), "GL0088");
		}
		final List<Map<String, Object>> collaborator = new ArrayList<Map<String,Object>>();
		if (email != null) {
			for (final String mailId : email) {
				final Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				collaborator.add(addCollaborator(mailId, gooruOid, apiCaller, sendInvite, content, identity == null ? null : identity.getUser()));
			}
			try {
				indexHandler.setReIndexRequest(content.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);						
			} catch (Exception e) {
				LOGGER.debug("error" + e.getMessage());
			}
		}

		return collaborator;
	}

	private Map<String, Object> addCollaborator(final String mailId, final String gooruOid, final User apiCaller, final boolean sendInvite, final Content content, final User user) throws Exception {
		Map<String, Object> collaborator = new HashMap<String, Object>();
		ActionResponseDTO<CollectionItem> responseDto = new ActionResponseDTO<CollectionItem>();
		if (user != null) {
			final UserContentAssoc userContentAssocs = this.getCollaboratorRepository().findCollaboratorById(gooruOid, user.getGooruUId());
			if (userContentAssocs == null) {
				final UserContentAssoc userContentAssoc = new UserContentAssoc();
				userContentAssoc.setContent(content);
				userContentAssoc.setUser(user);
				userContentAssoc.setAssociatedType(COLLABORATOR);
				userContentAssoc.setRelationship(COLABORATOR);
				userContentAssoc.setAssociatedBy(apiCaller);
				userContentAssoc.setLastActiveDate(new Date());
				userContentAssoc.setAssociationDate(new Date());
				this.userRepository.save(userContentAssoc);
				responseDto = this.getCollectionService().createCollectionItem(content.getGooruOid(), null, new CollectionItem(), user, COLLABORATOR, false);
				collaborator= setActiveCollaborator(userContentAssoc, ACTIVE);
				this.getContentService().createContentPermission(content, user);
				try {
					this.getCollaboratorEventLog().getEventLogs(user, responseDto.getModel(), gooruOid, true, false);
				} catch (JSONException e) {
					LOGGER.debug("error" + e.getMessage());
				}
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + user.getPartyUid() + "*");
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + content.getUser().getPartyUid() + "*");
			} else {
				collaborator = setActiveCollaborator(userContentAssocs, ACTIVE);
			}

		} else {
			final InviteUser inviteUsers = this.getInviteRepository().findInviteUserById(mailId, gooruOid, PENDING);
			if (inviteUsers == null) {
				final InviteUser inviteUser = new InviteUser();
				inviteUser.setEmailId(mailId);
				inviteUser.setGooruOid(gooruOid);
				inviteUser.setCreatedDate(new Date());
				inviteUser.setInvitationType(COLLABORATOR);
				inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, PENDING));
				inviteUser.setAssociatedUser(apiCaller);
				this.getUserRepository().save(inviteUser);
				collaborator = setInviteCollaborator(inviteUser, PENDING);
			} else {
				collaborator = setInviteCollaborator(inviteUsers, PENDING);
			}
		}
		final Map<String, Object> collaboratorData = new HashMap<String, Object>();
		collaboratorData.put(CONTENT_OBJ, content);
		collaboratorData.put(EMAIL_ID, mailId);
		if (sendInvite) {
			this.getMailAsyncExecutor().sendMailToInviteCollaborator(collaboratorData);
		}

		return collaborator;
	}
	
	private Map<String, Object> setInviteCollaborator(final InviteUser inviteUser, final String status) {
		final Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.put(EMAIL_ID, inviteUser.getEmailId());
		listMap.put(GOORU_OID, inviteUser.getGooruOid());
		listMap.put(ASSOC_DATE, inviteUser.getCreatedDate());
		if (status != null) {
			listMap.put(STATUS, status);
		}
		return listMap;
	}

	private Map<String, Object> setActiveCollaborator(final UserContentAssoc userContentAssoc, final String status) {
		final Map<String, Object> activeMap = new HashMap<String, Object>();
		activeMap.put(EMAIL_ID, userContentAssoc.getUser().getIdentities() != null ? userContentAssoc.getUser().getIdentities().iterator().next().getExternalId() : null);
		activeMap.put(_GOORU_UID, userContentAssoc.getUser().getGooruUId());
		activeMap.put(USER_NAME, userContentAssoc.getUser().getUsername());
		activeMap.put(GOORU_OID, userContentAssoc.getContent().getGooruOid());
		activeMap.put(ASSOC_DATE, userContentAssoc.getAssociationDate());
		activeMap.put(PROFILE_IMG_URL , this.getUserManagementService().buildUserProfileImageUrl(userContentAssoc.getUser()));
		if (status != null) {
			activeMap.put(STATUS, status);
		}
		return activeMap;
	}

	@Override
	public List<String> collaboratorSuggest(final String text, final String gooruUid) {

		return this.getCollaboratorRepository().collaboratorSuggest(text, gooruUid);
	}

	@Override
	public void deleteCollaborator(final String gooruOid, final List<String> email) {
		Content content = null;
		if (gooruOid != null) {
			content = getContentRepository().findContentByGooruId(gooruOid, true);
			if (content == null) {
				throw new NotFoundException(generateErrorMessage("GL0056", "content"), GL0056);
			}
		} else {
			throw new BadRequestException(generateErrorMessage("GL0088"), "GL0088");
		}
		if (email != null) {
			for (final String mailId : email) {
				final Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				if (identity != null) {
					final UserContentAssoc userContentAssoc = this.getCollaboratorRepository().findCollaboratorById(gooruOid, identity.getUser().getGooruUId());
					final List<CollectionItem> collectionItems = this.getCollectionRepository().findCollectionByResource(gooruOid, identity.getUser().getGooruUId(), COLLABORATOR);
					if (userContentAssoc != null) {
						this.getCollaboratorRepository().remove(userContentAssoc);
						if (collectionItems != null) {
						  this.getCollectionRepository().removeAll(collectionItems);
						}
						this.getContentService().deleteContentPermission(content, identity.getUser());
						final List<CollectionItem> associations = this.getCollectionRepository().getCollectionItemByAssociation(gooruOid, identity.getUser().getGooruUId(),null);
						
						try {
							this.getCollaboratorEventLog().getEventLogs(identity.getUser(), associations.size() > 0 ? associations.get(0) : null, gooruOid, false, true);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						for (CollectionItem association : associations) {
							this.getCollectionService().deleteCollectionItem(association.getCollectionItemId(), identity.getUser(), false);
						}
					}
					getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + identity.getUser().getPartyUid() + "*");
					getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + content.getUser().getPartyUid() + "*");
				} else {
					final InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, gooruOid,PENDING);
					if (inviteUser != null) {
						this.getCollaboratorRepository().remove(inviteUser);
					}
				}
			}
			try {
				indexHandler.setReIndexRequest(content.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);						
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
		}

	}

	@Override
	public List<Map<String, Object>> getCollaborators(final String gooruOid, final String filterBy) {
		final List<Map<String, Object>> collaborator = new ArrayList<Map<String, Object>>();

		if (filterBy != null && filterBy.equalsIgnoreCase(ACTIVE)) {
			collaborator.addAll(getActiveCollaborator(gooruOid));
		} else if (filterBy != null && filterBy.equalsIgnoreCase(PENDING)) {
			collaborator.addAll(getPendingCollaborator(gooruOid));
		} else {
			collaborator.addAll(getActiveCollaborator(gooruOid));
			collaborator.addAll(getPendingCollaborator(gooruOid));
		}
		return collaborator;
	}

	@Override
	public Map<String, List<Map<String, Object>>> getCollaboratorsByGroup(final String gooruOid, final String filterBy) {
		final Map<String, List<Map<String, Object>>> collaboratorList = new HashMap<String, List<Map<String, Object>>>();
		if (filterBy != null && filterBy.equalsIgnoreCase(ACTIVE)) {
			collaboratorList.put(ACTIVE, getActiveCollaborator(gooruOid));
		} else if (filterBy != null && filterBy.equalsIgnoreCase(PENDING)) {
			collaboratorList.put(PENDING, getPendingCollaborator(gooruOid));
		} else {
			collaboratorList.put(ACTIVE, getActiveCollaborator(gooruOid));
			collaboratorList.put(PENDING, getPendingCollaborator(gooruOid));
		}
		return collaboratorList;
	}

	@Override
	public List<Map<String, Object>> getActiveCollaborator(final String gooruOid) {
		final List<Map<String, Object>> activeList = new ArrayList<Map<String, Object>>();
		final List<UserContentAssoc> userContentAssocs = this.getCollaboratorRepository().getCollaboratorsById(gooruOid);
		if (userContentAssocs != null) {
			for (final UserContentAssoc userContentAssoc : userContentAssocs) {
				activeList.add(this.setActiveCollaborator(userContentAssoc, ACTIVE));
			}
		}
		return activeList;
	}

	@Override
	public List<Map<String, Object>> getPendingCollaborator(final String gooruOid) {
		final List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUsersById(gooruOid);
		final List<Map<String, Object>> pendingList = new ArrayList<Map<String, Object>>();
		if (inviteUsers != null) {
			for (final InviteUser inviteUser : inviteUsers) {
				pendingList.add(this.setInviteCollaborator(inviteUser, PENDING));
			}
		}
		return pendingList;
	}

	@Override
	public void updateCollaboratorStatus(final String mailId, final User user) throws Exception {
		final List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUserByMail(mailId, COLLABORATOR);
		final CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, ACTIVE);
		final List<InviteUser> inviteUserList = new ArrayList<InviteUser>();
		for (final InviteUser inviteUser : inviteUsers) {
			inviteUser.setStatus(status);
			inviteUser.setJoinedDate(new Date());
			inviteUserList.add(inviteUser);
			final Content content = getContentRepository().findContentByGooruId(inviteUser.getGooruOid(), true);
			if (content != null) {
				this.addCollaborator(mailId, inviteUser.getGooruOid(), user, false, content, user);
			}
		}
		if (inviteUserList.size() > 0) {
			this.getCollaboratorRepository().saveAll(inviteUserList);
		}

	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public CollaboratorRepository getCollaboratorRepository() {
		return collaboratorRepository;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public MailAsyncExecutor getMailAsyncExecutor() {
		return mailAsyncExecutor;
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}
	
	public CollaboratorEventLog getCollaboratorEventLog() {
		return collaboratorEventLog;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
