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
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator.CollaboratorRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class CollaboratorServiceImpl extends BaseServiceImpl implements CollaboratorService, ParameterProperties, ConstantProperties {

	@Autowired
	private UserRepository userRepository;

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
	
	private Logger logger = LoggerFactory.getLogger(CollaboratorServiceImpl.class);

	@Override
	public List<Map<String, Object>> addCollaborator(List<String> email, String gooruOid, User apiCaller,boolean sendInvite) throws Exception {
		Content content = null;
		if (gooruOid != null) {
			content = getContentRepository().findContentByGooruId(gooruOid, true);
			if (content == null) {
				throw new NotFoundException("content not found");
			}
		} else {
			throw new BadCredentialsException("content required");
		}
		List<Map<String, Object>> collaborator = new ArrayList<Map<String, Object>>();
		if (email != null) {
			for (String mailId : email) {
				Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				ActionResponseDTO<CollectionItem> responseDto = new ActionResponseDTO<CollectionItem>();
				if (identity != null) {
					UserContentAssoc userContentAssocs = this.getCollaboratorRepository().findCollaboratorById(gooruOid, identity.getUser().getGooruUId());
					if (userContentAssocs == null) {
						UserContentAssoc userContentAssoc = new UserContentAssoc();
						userContentAssoc.setContent(content);
						userContentAssoc.setUser(identity.getUser());
						userContentAssoc.setAssociatedType(COLLABORATOR);
						userContentAssoc.setRelationship(COLABORATOR);
						userContentAssoc.setAssociatedBy(apiCaller);
						userContentAssoc.setLastActiveDate(new Date());
						userContentAssoc.setAssociationDate(new Date());
						this.userRepository.save(userContentAssoc);
						responseDto = this.getCollectionService().createCollectionItem(content.getGooruOid(), null, new CollectionItem(), identity.getUser(), COLLABORATOR, false);
						collaborator.add(setActiveCollaborator(userContentAssoc, ACTIVE));
						this.getContentService().createContentPermission(content, identity.getUser());
						try {
							getEventLogs(identity.getUser(), responseDto.getModel(), gooruOid, true, false);
						} catch (JSONException e) {
							e.printStackTrace();
						}	
						getAsyncExecutor().deleteFromCache("v2-organize-data-" + identity.getUser().getPartyUid() + "*");
						getAsyncExecutor().deleteFromCache("v2-organize-data-" + content.getUser().getPartyUid() + "*");
					} else {
						collaborator.add(setActiveCollaborator(userContentAssocs, ACTIVE));
					}

				} else {
					InviteUser inviteUsers = this.getInviteRepository().findInviteUserById(mailId, gooruOid,PENDING);
					if (inviteUsers == null) {
						InviteUser inviteUser = new InviteUser();
						inviteUser.setEmailId(mailId);
						inviteUser.setGooruOid(gooruOid);
						inviteUser.setCreatedDate(new Date());
						inviteUser.setInvitationType(COLLABORATOR);
						inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, PENDING));
						inviteUser.setAssociatedUser(apiCaller);
						this.getUserRepository().save(inviteUser);
						collaborator.add(setInviteCollaborator(inviteUser, PENDING));
					} else {
						collaborator.add(setInviteCollaborator(inviteUsers, PENDING));
					}
				}
				Map<String, Object> collaboratorData = new HashMap<String, Object>();
				collaboratorData.put(CONTENT_OBJ, content);
				collaboratorData.put(EMAIL_ID, mailId);
				if (sendInvite) {
					this.getMailAsyncExecutor().sendMailToInviteCollaborator(collaboratorData);
				}
			}
			try {
				indexProcessor.index(content.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}

		return collaborator;
	}

	private Map<String, Object> setInviteCollaborator(InviteUser inviteUser, String status) {
		Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.put(EMAIL_ID, inviteUser.getEmailId());
		listMap.put(GOORU_OID, inviteUser.getGooruOid());
		listMap.put(ASSOC_DATE, inviteUser.getCreatedDate());
		if (status != null) {
			listMap.put(STATUS, status);
		}
		return listMap;
	}

	private Map<String, Object> setActiveCollaborator(UserContentAssoc userContentAssoc, String status) {
		Map<String, Object> activeMap = new HashMap<String, Object>();
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
	public List<String> collaboratorSuggest(String text, String gooruUid) {

		return this.getCollaboratorRepository().collaboratorSuggest(text, gooruUid);
	}

	@Override
	public void deleteCollaborator(String gooruOid, List<String> email) {
		Content content = null;
		if (gooruOid != null) {
			content = getContentRepository().findContentByGooruId(gooruOid, true);
			if (content == null) {
				throw new NotFoundException("content not found");
			}
		} else {
			throw new BadCredentialsException("content required");
		}
		if (email != null) {
			for (String mailId : email) {
				Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				if (identity != null) {
					UserContentAssoc userContentAssoc = this.getCollaboratorRepository().findCollaboratorById(gooruOid, identity.getUser().getGooruUId());
					List<CollectionItem> collectionItems = this.getCollectionRepository().findCollectionByResource(gooruOid, identity.getUser().getGooruUId(), "collaborator");
					if (userContentAssoc != null) {
						this.getCollaboratorRepository().remove(userContentAssoc);
						if (collectionItems != null) {
						  this.getCollectionRepository().removeAll(collectionItems);
						}
						this.getContentService().deleteContentPermission(content, identity.getUser());
						List<CollectionItem> associations = this.getCollectionRepository().getCollectionItemByAssociation(gooruOid, identity.getUser().getGooruUId(),null);
						for (CollectionItem association : associations) {
							this.getCollectionService().deleteCollectionItem(association.getCollectionItemId(), identity.getUser());
						}
						try {
							getEventLogs(identity.getUser(), associations.get(0), gooruOid, false, true);
						} catch (JSONException e) {
							e.printStackTrace();
						}						
					}
					getAsyncExecutor().deleteFromCache("v2-organize-data-" + identity.getUser().getPartyUid() + "*");
					getAsyncExecutor().deleteFromCache("v2-organize-data-" + content.getUser().getPartyUid() + "*");
				} else {
					InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, gooruOid,PENDING);
					if (inviteUser != null) {
						this.getCollaboratorRepository().remove(inviteUser);
					}
				}
			}
			try {
				indexProcessor.index(content.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}

	}

	@Override
	public List<Map<String, Object>> getCollaborators(String gooruOid, String filterBy) {
		List<Map<String, Object>> collaborator = new ArrayList<Map<String, Object>>();

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
	public Map<String, List<Map<String, Object>>> getCollaboratorsByGroup(String gooruOid, String filterBy) {
		Map<String, List<Map<String, Object>>> collaboratorList = new HashMap<String, List<Map<String, Object>>>();
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
	public List<Map<String, Object>> getActiveCollaborator(String gooruOid) {
		List<Map<String, Object>> activeList = new ArrayList<Map<String, Object>>();
		List<UserContentAssoc> userContentAssocs = this.getCollaboratorRepository().getCollaboratorsById(gooruOid);
		if (userContentAssocs != null) {
			for (UserContentAssoc userContentAssoc : userContentAssocs) {
				activeList.add(this.setActiveCollaborator(userContentAssoc, ACTIVE));
			}
		}
		return activeList;
	}

	@Override
	public List<Map<String, Object>> getPendingCollaborator(String gooruOid) {
		List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUsersById(gooruOid);
		List<Map<String, Object>> pendingList = new ArrayList<Map<String, Object>>();
		if (inviteUsers != null) {
			for (InviteUser inviteUser : inviteUsers) {
				pendingList.add(this.setInviteCollaborator(inviteUser, PENDING));
			}
		}
		return pendingList;
	}

	@Override
	public void updateCollaboratorStatus(String mailId) throws Exception {
		List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUserByMail(mailId, COLLABORATOR);
		for (InviteUser inviteUser : inviteUsers) {
			inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, ACTIVE));
			inviteUser.setJoinedDate(new Date());
			this.getCollaboratorRepository().save(inviteUser);
			Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
			List<String> mail = new ArrayList<String>();
			mail.add(mailId);
			this.addCollaborator(mail, inviteUser.getGooruOid(), identity.getUser(),false);
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

	private void getEventLogs(User collaborator, CollectionItem collectionItem, String gooruOid, boolean isAdd, boolean isRemove) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_COLLABORATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) :  new JSONObject();
		context.put(SOURCE_GOORU_UID,gooruOid);
		context.put(TARGET_GOORU_UID,collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		context.put(TARGET_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		context.put(PARENT_GOORU_OID, collectionItem != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, gooruOid);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) :  new JSONObject();
		session.put(ORGANIZATION_UID, collaborator.getOrganizationUid());
		JSONObject newUser = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) :  new JSONObject();		
		newUser.put(GOORU_UID, collaborator.getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());	
		JSONObject user = SessionContextSupport.getLog().get(USER) != null ? new JSONObject(SessionContextSupport.getLog().get(USER).toString()) :  new JSONObject();
		SessionContextSupport.putLogParameter(USER, user.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) :  new JSONObject();
		if(isAdd){
			payLoadObject.put(MODE, ADD);
		} else if(isRemove){
			payLoadObject.put(MODE, DELETE);
		}
		payLoadObject.put(COLLABORATED_ID, collaborator != null ? collaborator.getPartyUid() : null);
		payLoadObject.put(ITEM_TYPE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getResourceType().getName() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
