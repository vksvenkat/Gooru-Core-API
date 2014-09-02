/////////////////////////////////////////////////////////////
// ClasspageServiceImpl.java
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
package org.ednovo.gooru.domain.service.classpage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserCollectionItemAssoc;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.InviteService;
import org.ednovo.gooru.domain.service.ScollectionServiceImpl;
import org.ednovo.gooru.domain.service.eventlogs.ClasspageEventLog;
import org.ednovo.gooru.domain.service.eventlogs.CollectionEventLog;
import org.ednovo.gooru.domain.service.group.UserGroupService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.task.TaskService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.UserGroupRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class ClasspageServiceImpl extends ScollectionServiceImpl implements ClasspageService {

	@Autowired
	private TaskService taskService;

	@Autowired
	@javax.annotation.Resource(name = "userService")
	private UserService userService;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private ClasspageEventLog classpageEventlog;
	
	@Autowired
	private CollectionEventLog scollectionEventlog;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private InviteService inviteService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private StorageRepository storageRepository;
	
	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Override
	public ActionResponseDTO<Classpage> createClasspage(Classpage classpage, boolean addToUserClasspage, String assignmentId) throws Exception {
		Errors errors = validateClasspage(classpage);
		if (!errors.hasErrors()) {
			this.getCollectionRepository().save(classpage);
			if (assignmentId != null && !assignmentId.isEmpty()) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collectionItem = this.createClasspageItem(assignmentId, classpage.getGooruOid(), collectionItem, classpage.getUser(), CollectionType.CLASSPAGE.getCollectionType()).getModel();
				Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
				collectionItems.add(collectionItem);
				classpage.setCollectionItems(collectionItems);
			}
			if (addToUserClasspage) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				this.createClasspageItem(classpage.getGooruOid(), null, collectionItem, classpage.getUser(), CollectionType.USER_CLASSPAGE.getCollectionType());
			}
			this.getCollectionRepository().save(classpage);
		}
		return new ActionResponseDTO<Classpage>(classpage, errors);
	}

	@Override
	public ActionResponseDTO<Classpage> createClasspage(Classpage newClasspage, CollectionItem newCollectionItem, String gooruOid, User user, boolean addToMy) throws Exception {
		Errors errors = validateClasspage(newClasspage);
		if (!errors.hasErrors()) {
			this.getCollectionRepository().save(newClasspage);

			UserGroup userGroup = this.getUserGroupService().createGroup(newClasspage.getTitle(), newClasspage.getClasspageCode(),SYSTEM, user, null);
			if (gooruOid != null && !gooruOid.isEmpty() && newCollectionItem != null) {
				this.createClasspageItem(gooruOid, newClasspage.getGooruOid(), newCollectionItem, newClasspage.getUser(), CollectionType.USER_CLASSPAGE.getCollectionType());
				this.getCollectionRepository().save(newClasspage);
			}
			if (addToMy) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				this.createClasspageItem(newClasspage.getGooruOid(), null, collectionItem, newClasspage.getUser(), CollectionType.USER_CLASSPAGE.getCollectionType());
			}
			try {
				
				this.getClasspageEventlog().getEventLogs(newClasspage, user, userGroup, true, false);
			} catch(Exception e){
				e.printStackTrace();
			}
			
		}
		return new ActionResponseDTO<Classpage>(newClasspage, errors);
	}

	public ActionResponseDTO<Classpage> updateClasspage(Classpage newClasspage, String updateClasspageId, Boolean hasUnrestrictedContentAccess) throws Exception {
		Classpage classpage = this.getClasspage(updateClasspageId, null, null);
		Errors errors = validateUpdateClasspage(classpage, newClasspage);
		JSONObject itemData = new JSONObject();
		if (!errors.hasErrors()) {
			if (newClasspage.getVocabulary() != null) {
				itemData.put(VOCABULARY, newClasspage.getVocabulary());
				classpage.setVocabulary(newClasspage.getVocabulary());
			}

			if (newClasspage.getTitle() != null) {
				itemData.put(TITLE, newClasspage.getTitle());
				classpage.setTitle(newClasspage.getTitle());
				UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode());
				userGroup.setGroupName(newClasspage.getTitle());
				this.getUserRepository().save(userGroup);
			}
			if (newClasspage.getDescription() != null) {
				itemData.put(DESCRIPTION, newClasspage.getDescription());
				classpage.setDescription(newClasspage.getDescription());
			}
			if (newClasspage.getNarrationLink() != null) {
				itemData.put(NARRATION_LINK, newClasspage.getNarrationLink());
				classpage.setNarrationLink(newClasspage.getNarrationLink());
			}
			if (newClasspage.getEstimatedTime() != null) {
				itemData.put(ESTIMATED_TIME, newClasspage.getEstimatedTime());
				classpage.setEstimatedTime(newClasspage.getEstimatedTime());
			}
			if (newClasspage.getNotes() != null) {
				itemData.put(NOTES, newClasspage.getNotes());
				classpage.setNotes(newClasspage.getNotes());
			}
			if (newClasspage.getGoals() != null) {
				itemData.put(GOALS, newClasspage.getGoals());
				classpage.setGoals(newClasspage.getGoals());
			}
			if (newClasspage.getKeyPoints() != null) {
				itemData.put(KEYPOINTS, newClasspage.getKeyPoints());
				classpage.setGoals(newClasspage.getKeyPoints());
			}
			if (newClasspage.getLanguage() != null) {
				itemData.put(LANGUAGE, newClasspage.getLanguage());
				classpage.setLanguage(newClasspage.getLanguage());
			}
			if (newClasspage.getGrade() != null) {
				itemData.put(GRADE, newClasspage.getGrade());
				classpage.setGrade(newClasspage.getGrade());
			}
			if (newClasspage.getSharing() != null) {
				itemData.put(SHARING, newClasspage.getSharing());
				if (newClasspage.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newClasspage.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newClasspage.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
					classpage.setSharing(newClasspage.getSharing());
				}
			}
			if (newClasspage.getLastUpdatedUserUid() != null) {
				itemData.put(LAST_UPDATED_USER_UID, newClasspage.getLastUpdatedUserUid());
				classpage.setLastUpdatedUserUid(newClasspage.getLastUpdatedUserUid());
			}

			if (hasUnrestrictedContentAccess) {
				if (newClasspage.getCreator() != null && newClasspage.getCreator().getPartyUid() != null) {
					User user = userService.findByGooruId(newClasspage.getCreator().getPartyUid());
					classpage.setCreator(user);
				}

				if (newClasspage.getUser() != null && newClasspage.getUser().getPartyUid() != null) {
					User user = userService.findByGooruId(newClasspage.getUser().getPartyUid());
					classpage.setUser(user);
				}
			}

			this.getCollectionRepository().save(classpage);
			
			try{
				
				this.getScollectionEventlog().getEventLogs(classpage, itemData, classpage.getUser(), false, true);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return new ActionResponseDTO<Classpage>(classpage, errors);
	}

	@Override
	public Classpage getClasspage(String classpageCode, User user) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageCode);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "Class"));
		}
		return getClasspage(classpage.getGooruOid(), user, PERMISSIONS);
	}

	@Override
	public void deleteClasspage(String classpageId, User user) {
		Classpage classpage = this.getClasspage(classpageId, null, null);
		if (classpage != null) {
			if(this.getOperationAuthorizer().hasUnrestrictedContentAccess(classpageId, user)){
				UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode());
				if(userGroup != null){
					this.getUserRepository().remove(userGroup);
				}
				try {
					
					this.getClasspageEventlog().getEventLogs(classpage, classpage.getUser(), userGroup, false, true);
				} catch(Exception e){
					e.printStackTrace();
				}
				this.getCollectionRepository().remove(Classpage.class, classpage.getContentId());
			} else {
				throw new UnauthorizedException(generateErrorMessage("GL0085"));
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, CLASSPAGE));
		}
	}

	@Override
	public SearchResults<Classpage> getClasspages(Integer offset, Integer limit,  User user, String title, String author, String userName) {
		if (userService.isContentAdmin(user)) {
			List<Classpage> classpages = this.getCollectionRepository().getClasspages(offset, limit, title, author, userName);
			SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpages);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCount(title, author, userName));
			return result;
		} else {
			throw new UnauthorizedException(generateErrorMessage("GL0085"));
		}
	}

	@Override
	public Classpage getClasspage(String collectionId, User user, String merge) {
		Classpage classpage = this.getCollectionRepository().getClasspageByGooruOid(collectionId, null);
		if (classpage != null && merge != null) {
			Map<String, Object> permissions = new HashMap<String, Object>();
			Boolean isMember = false;
			String status = NOTINVITED;
			InviteUser inviteUser = null;
			String mailId = null;
			UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode());
			if (userGroup != null && !user.getGooruUId().equalsIgnoreCase(ANONYMOUS)) {
				isMember = this.getUserRepository().getUserGroupMemebrByGroupUid(userGroup.getPartyUid(), user.getPartyUid()) != null ? true : false;
				if (isMember) {
					status = ACTIVE;
				}
				if (user.getIdentities().size() > 0) {
					mailId = user.getIdentities().iterator().next().getExternalId();
				}
				if (classpage.getUser() != null && !classpage.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId())) {
					inviteUser = this.getInviteRepository().findInviteUserById(mailId, collectionId, PENDING);
					if (inviteUser != null) {
						status = PENDING;
					}
				}
			}
			if (merge.contains(PERMISSIONS)) {
				permissions.put(PERMISSIONS, this.getContentService().getContentPermission(collectionId, user));
			}
			permissions.put(STATUS, status);
			long member = this.getUserGroupRepository().getUserGroupAssociationCount(classpage.getClasspageCode());
			permissions.put(MEMBER_COUNT, member);
			classpage.setMeta(permissions);
		}
		return classpage;
	}

	@Override
	public ActionResponseDTO<CollectionItem> createClasspageItem(String assignmentGooruOid, String classpageGooruOid, CollectionItem collectionItem, User user, String type) throws Exception {
		Classpage classpage = null;
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(assignmentGooruOid, null);
		Errors errors = validateClasspageItem(collection, collectionItem);
		
		Date currentDate = new Date();
		
		if(collectionItem!= null && collectionItem.getPlannedEndDate() != null){
			Date plannedEndDate = collectionItem.getPlannedEndDate();
			if(currentDate.compareTo(plannedEndDate) > 0){
				throw new BadRequestException(generateErrorMessage("GL0086"));
			}
		}
		
		if(collection != null){
			if (type != null && type.equalsIgnoreCase(CollectionType.USER_CLASSPAGE.getCollectionType())) {
				if (classpageGooruOid != null) {
					classpage = this.getClasspage(classpageGooruOid, null, null);
				} else {
					classpage = this.getCollectionRepository().getUserShelfByClasspageGooruUid(user.getGooruUId(), CollectionType.USER_CLASSPAGE.getCollectionType());
				}
				if (classpage == null && type != null && type.equalsIgnoreCase(CollectionType.USER_CLASSPAGE.getCollectionType())) {
					classpage = new Classpage();
					classpage.setTitle(MY_CLASSPAGE);
					classpage.setCollectionType(CollectionType.USER_CLASSPAGE.getCollectionType());
					classpage.setClasspageCode(BaseUtil.base48Encode(7));
					classpage.setGooruOid(UUID.randomUUID().toString());
					ContentType contentType = (ContentType) this.getCollectionRepository().get(ContentType.class, ContentType.RESOURCE);
					classpage.setContentType(contentType);
					ResourceType resourceType = (ResourceType) this.getCollectionRepository().get(ResourceType.class, ResourceType.Type.CLASSPAGE.getType());
					classpage.setResourceType(resourceType);
					classpage.setLastModified(new Date(System.currentTimeMillis()));
					classpage.setCreatedOn(new Date(System.currentTimeMillis()));
					classpage.setSharing(Sharing.PRIVATE.getSharing());
					classpage.setUser(user);
					classpage.setOrganization(user.getPrimaryOrganization());
					classpage.setCreator(user);
					classpage.setDistinguish(Short.valueOf(ZERO));
					classpage.setRecordSource(NOT_ADDED);
					classpage.setIsFeatured(0);
					this.getCollectionRepository().save(classpage);
				}
				collectionItem.setItemType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
			} else {
				classpage = this.getClasspage(classpageGooruOid, null, null);
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
			}

			if (!errors.hasErrors()) {
				collectionItem.setCollection(classpage);
				collectionItem.setResource(collection);
				int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
				collectionItem.setItemSequence(sequence);
				collectionItem.getCollection().setItemCount(sequence);
				this.getCollectionRepository().save(collectionItem);
			}
			try{
				
				this.getClasspageEventlog().getEventLogs(collectionItem, true, user, collectionItem.getCollection().getCollectionType());
			} catch(Exception e){
				e.printStackTrace();
			}
		} else {
			throw new NotFoundException(generateErrorMessage("GL0087", assignmentGooruOid));
		}
		
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public ActionResponseDTO<Classpage> createClasspage(Classpage newclasspage, User user, boolean addToUserClasspage, String assignmentId) throws Exception {
		Errors errors = validateClasspage(newclasspage);
		if (!errors.hasErrors()) {
			this.getCollectionRepository().save(newclasspage);
			this.getResourceService().saveOrUpdateResourceTaxonomy(newclasspage, newclasspage.getTaxonomySet());
			if (assignmentId != null && !assignmentId.isEmpty()) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				collectionItem = this.createClasspageItem(assignmentId, newclasspage.getGooruOid(), collectionItem, newclasspage.getUser(), CollectionType.CLASSPAGE.getCollectionType()).getModel();
				Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
				collectionItems.add(collectionItem);
				newclasspage.setCollectionItems(collectionItems);
			}
			if (addToUserClasspage) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				this.createClasspageItem(newclasspage.getGooruOid(), null, collectionItem, newclasspage.getUser(), CollectionType.USER_CLASSPAGE.getCollectionType());
			}
			this.getCollectionRepository().save(newclasspage);
		}
		return new ActionResponseDTO<Classpage>(newclasspage, errors);

	}

	@Override
	public List<Classpage> getMyClasspage(Integer offset, Integer limit, User user,boolean skipPagination, String orderBy) {
		return this.getCollectionRepository().getMyClasspage(offset, limit, user, skipPagination, orderBy);
	}

	@Override
	public Long getMyClasspageCount(String gooruUid) {
		return this.getCollectionRepository().getMyClasspageCount(gooruUid);
	}

	@Override
	public ActionResponseDTO<Classpage> createClasspage(Classpage classpage, String collectionId, boolean addToMy) throws Exception {

		return this.createClasspage(classpage, classpage.getCollectionItem(), collectionId, classpage.getUser(), addToMy);
	}

	@Override
	public List<Map<String, Object>> classpageUserJoin(String code, List<String> mailIds, User apiCaller) throws Exception {
		List<Map<String, Object>> classpageMember = new ArrayList<Map<String, Object>>();
		UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(code);
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		InviteUser inviteUser = new InviteUser();
		if (userGroup != null && classpage != null) {
			for (String mailId : mailIds) {
				Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				if (identity != null) {
					inviteUser = this.getInviteRepository().findInviteUserById(mailId, classpage.getGooruOid(), null);
					if (inviteUser != null) {
						inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, ACTIVE));
						inviteUser.setJoinedDate(new Date(System.currentTimeMillis()));
						this.getInviteRepository().save(inviteUser);
					}
					if (this.getUserRepository().getUserGroupMemebrByGroupUid(userGroup.getPartyUid(), identity.getUser().getPartyUid()) == null) {
						UserGroupAssociation groupAssociation = new UserGroupAssociation();
						groupAssociation.setIsGroupOwner(0);
						groupAssociation.setUser(identity.getUser());
						groupAssociation.setAssociationDate(new Date(System.currentTimeMillis()));
						groupAssociation.setUserGroup(userGroup);
						classpage.setLastModified(new Date(System.currentTimeMillis()));
						this.getCollectionRepository().save(classpage);
						this.getUserRepository().save(groupAssociation);
						this.getUserRepository().flush();
						classpageMember.add(setMemberResponse(groupAssociation, ACTIVE));
					}
				}
				try {
				  this.getClasspageEventlog().getEventLogs(classpage, apiCaller, userGroup, inviteUser);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		} else {
			throw new NotFoundException(generateErrorMessage("GL0056","class"));
		}
		return classpageMember;
	}

	@Override
	public void classpageUserRemove(String code, List<String> mailIds, User apiCaller) throws Exception {
		UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(code);
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (userGroup != null && classpage != null) {
			for (String mailId : mailIds) {
				Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				if (identity != null) {
					UserGroupAssociation userGroupAssociation = this.getUserRepository().getUserGroupMemebrByGroupUid(userGroup.getPartyUid(), identity.getUser().getPartyUid());
					if (userGroupAssociation != null) {
						classpage.setLastModified(new Date(System.currentTimeMillis()));
						this.getCollectionRepository().save(classpage);
						this.getUserGroupRepository().remove(userGroupAssociation);
					}
					try {
						this.getClasspageEventlog().getEventLogs(classpage, userGroupAssociation, null);
					} catch(Exception e){
						e.printStackTrace();
					}
					
				}
				InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, classpage.getGooruOid(), null);
				if (inviteUser != null) {
					this.getInviteRepository().remove(inviteUser);
					try{
						this.getClasspageEventlog().getEventLogs(classpage, null, inviteUser);
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public List<Map<String, Object>> getClassMemberList(String code, String filterBy) {
		List<Map<String, Object>> classpageMember = new ArrayList<Map<String, Object>>();
		if (filterBy != null && filterBy.equalsIgnoreCase(ACTIVE)) {
			classpageMember.addAll(getActiveMemberList(code));
		} else if (filterBy != null && filterBy.equalsIgnoreCase(PENDING)) {
			classpageMember.addAll(getPendingMemberList(code));
		} else {
			classpageMember.addAll(getActiveMemberList(code));
			classpageMember.addAll(getPendingMemberList(code));
		}
		return classpageMember;
	}

	@Override
	public Map<String, List<Map<String, Object>>> getClassMemberListByGroup(String code, String filterBy) {
		Map<String, List<Map<String, Object>>> collaboratorList = new HashMap<String, List<Map<String, Object>>>();
		if (filterBy != null && filterBy.equalsIgnoreCase(ACTIVE)) {
			collaboratorList.put(ACTIVE, getActiveMemberList(code));
		} else if (filterBy != null && filterBy.equalsIgnoreCase(PENDING)) {
			collaboratorList.put(PENDING, getPendingMemberList(code));
		} else {
			collaboratorList.put(ACTIVE, getActiveMemberList(code));
			collaboratorList.put(PENDING, getPendingMemberList(code));
		}
		return collaboratorList;
	}

	private List<Map<String, Object>> getActiveMemberList(String code) {
		List<Map<String, Object>> activeList = new ArrayList<Map<String, Object>>();
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "Class"));
		}
		UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(code);
		List<UserGroupAssociation> userGroupAssociations = this.getUserGroupRepository().getUserGroupAssociationByGroup(userGroup.getPartyUid());
		for (UserGroupAssociation userGroupAssociation : userGroupAssociations) {
			if (userGroupAssociation.getIsGroupOwner() != 1) {
				activeList.add(this.setMemberResponse(userGroupAssociation, ACTIVE));
			}
		}
		return activeList;
	}

	private List<Map<String, Object>> getPendingMemberList(String code) {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056","Class"));
		}
		List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUsersById(classpage.getGooruOid());
		List<Map<String, Object>> pendingList = new ArrayList<Map<String, Object>>();
		if (inviteUsers != null) {
			for (InviteUser inviteUser : inviteUsers) {
				pendingList.add(this.setInviteMember(inviteUser, PENDING));
			}
		}
		return pendingList;
	}

	private Map<String, Object> setInviteMember(InviteUser inviteUser, String status) {
		Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.put(EMAIL_ID, inviteUser.getEmailId());
		listMap.put(GOORU_OID, inviteUser.getGooruOid());
		listMap.put(ASSOC_DATE, inviteUser.getCreatedDate());
		if (status != null) {
			listMap.put(STATUS, status);
		}
		return listMap;
	}

	private Map<String, Object> setMemberResponse(UserGroupAssociation userGroupAssociation, String status) {
		Map<String, Object> member = new HashMap<String, Object>();
		member.put(EMAIL_ID, userGroupAssociation.getUser().getIdentities() != null ? userGroupAssociation.getUser().getIdentities().iterator().next().getExternalId() : null);
		member.put(_GOORU_UID, userGroupAssociation.getUser().getPartyUid());
		member.put(USER_NAME, userGroupAssociation.getUser().getUsername());
		member.put(PROFILE_IMG_URL, this.getUserManagementService().buildUserProfileImageUrl(userGroupAssociation.getUser()));
		member.put(STATUS, status);
		return member;
	}

	@Override
	public SearchResults<Map<String, Object>> getMemberList(String code, Integer offset, Integer limit, String filterBy) {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "classpage"));
		}
		List<Object[]> results = this.getUserGroupRepository().getUserMemberList(code, classpage.getGooruOid(), offset, limit, filterBy);
		SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (Object[] object : results) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(EMAIL_ID, object[0]);
			result.put(USER_NAME, object[1]);
			result.put(_GOORU_UID, object[2]);
			result.put(ASSOC_DATE, object[3]);
			result.put(STATUS, object[4]);
			result.put(FIRST_NAME, object[5]);
			result.put(LAST_NAME, object[6]);
			if (object[2] != null) {
				result.put(PROFILE_IMG_URL, settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID) + "/" + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, TaxonomyUtil.GOORU_ORG_UID) + String.valueOf(object[2]) + ".png");
			}
			listMap.add(result);
		}
		searchResult.setSearchResults(listMap);
		searchResult.setTotalHitCount(this.getUserGroupRepository().getUserMemberCount(code, classpage.getGooruOid(), filterBy));
		return searchResult;
	}

	private Errors validateClasspage(Classpage classpage) throws Exception {
		final Errors errors = new BindException(classpage, CLASSPAGE);
		if (classpage != null) {
			rejectIfNullOrEmpty(errors, classpage.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	private Errors validateUpdateClasspage(Collection collection, Collection newCollection) throws Exception {
		final Errors errors = new BindException(collection, COLLECTION);
		rejectIfNull(errors, collection, COLLECTION_ALL, GL0006, generateErrorMessage(GL0006, COLLECTION));
		return errors;
	}

	private Errors validateClasspageItem(Resource resource, CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED, COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
			rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		}
		return errors;
	}

	@Override
	public List<String> classMemberSuggest(String queryText, String gooruUid) {
		return this.getUserGroupRepository().classMemberSuggest(queryText, gooruUid);
	}

	@Override
	public SearchResults<Map<String, Object>> getMyStudy(User user, String orderBy, Integer offset, Integer limit, String type) {
		if (user.getPartyUid().equalsIgnoreCase(ANONYMOUS)) {
			throw new NotFoundException(generateErrorMessage("GL0056","User"));
		}
		List<Object[]> results = this.getUserGroupRepository().getMyStudy(user.getPartyUid(), user.getIdentities() != null ? user.getIdentities().iterator().next().getExternalId() : null, orderBy, offset, limit, type);
		SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		searchResult.setSearchResults(this.setMyStudy(results));
		searchResult.setTotalHitCount(this.getUserGroupRepository().getMyStudyCount(user.getPartyUid(), user.getIdentities() != null ? user.getIdentities().iterator().next().getExternalId() : null, type));
		return searchResult;
	}

	@Override
	public List<Map<String, Object>> setMyStudy(List<Object[]> results) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (Object[] object : results) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(GOORU_OID, object[0]);
			result.put(TITLE, object[1]);
			result.put(CLASS_CODE, object[2]);
			result.put(STATUS, object[3]);
			result.put(CREATED_ON, object[4]);
			Map<String, Object> user = new HashMap<String, Object>();
			user.put(GOORU_UID, object[5]);
			user.put(FIRSTNAME, object[6]);
			user.put(LASTNAME, object[7]);
			user.put(USERNAME, object[8]);
			user.put(PROFILE_IMG_URL, settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID) + "/" + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, TaxonomyUtil.GOORU_ORG_UID) + String.valueOf(object[5]) + ".png");
			result.put(USER, user);

			StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
			Map<String, Object> thumbnails = new HashMap<String, Object>();
			if (object[10] != null) {
				thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[9]) + String.valueOf(object[10]));
			} else {
				thumbnails.put(URL, "");
			}
			result.put(THUMBNAILS, thumbnails);
			result.put(ITEM_COUNT, object[11] == null ? 0 : object[11]);
			long member = this.getUserGroupRepository().getUserGroupAssociationCount(String.valueOf(object[2]));
			result.put(MEMBER_COUNT, member);
			listMap.add(result);
		}
		return listMap;
	}

	@Override
	public CollectionItem updateAssignment(String collectionItemId, String status, User user) {
		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		rejectIfNull(collectionItem, GL0056, generateErrorMessage(GL0056, COLLECTION_ITEM));
		UserCollectionItemAssoc userCollectionItemAssoc = new UserCollectionItemAssoc();
		userCollectionItemAssoc.setCollectionItem(collectionItem);
		userCollectionItemAssoc.setUser(user);
		userCollectionItemAssoc.setLastModifiedOn(new Date());
		CustomTableValue statusType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.ASSIGNMENT_STATUS_TYPE.getTable(), status);
		userCollectionItemAssoc.setStatus(statusType);
		this.getCollectionRepository().save(userCollectionItemAssoc);
		userCollectionItemAssoc.getCollectionItem().setStatus(status);
		return userCollectionItemAssoc.getCollectionItem();
	}

	@Override
	public List<Map<String, Object>> getClasspageItems(String gooruOid, Integer limit, Integer offset, String userUid, String orderBy, boolean optimize, String status) {
		List<Object[]> results = this.getCollectionRepository().getClasspageItems(gooruOid, limit, offset, userUid, orderBy, status);
		List<Map<String, Object>> collectionItems = new ArrayList<Map<String, Object>>();
		for (Object[] object : results) {
			Map<String, Object> result = new HashMap<String, Object>();
			Map<String, Object> resource = new HashMap<String, Object>();
			if (!optimize)  {
				result.put(ASSOCIATION_DATE, object[0]);
				resource.put(FOLDER, object[7]);
				resource.put(SHARING, object[9]);
				Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (object[8] != null) {
					StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
					thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[7]) + String.valueOf(object[8]));
				} else {
					thumbnails.put(URL, "");
				}
				resource.put(THUMBNAILS, thumbnails);
				Map<String, Object> user = new HashMap<String, Object>();
				user.put(USERNAME, object[12]);
				user.put(GOORU_UID, object[13]);
				user.put(PROFILE_IMG_URL, settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID) + "/" + settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, TaxonomyUtil.GOORU_ORG_UID) + String.valueOf(object[13]) + ".png");
				resource.put(USER, user);
			}
			resource.put(GOALS, object[10]);
			resource.put(TITLE, object[6]);
			resource.put(GOORU_OID, object[5]);
			result.put(COLLECTION_ITEM_ID, object[1]);				
			result.put(ITEM_SEQUENCE, object[2]);
			result.put(NARRATION, object[3]);
			result.put(PLANNED_END_DATE, object[4]);
			result.put(STATUS, object[11]);
			result.put(RESOURCE, resource);
			collectionItems.add(result);
		}
		return collectionItems;
	}
	
	public List<Map<String, Object>> getClasspageAssoc(Integer offset, Integer limit, String classpageId, String collectionId, String title, String classCode, String collectionCreator) {
		String gooruUid = null;
		if (collectionCreator != null) {
			User user = this.getUserService().getUserByUserName(collectionCreator);
			if (user != null) {
				gooruUid = user.getPartyUid();
			}
		}
		List<Object[]> classpageAssocs = this.getCollectionRepository().getClasspageAssoc(offset, limit, classpageId, collectionId, gooruUid, title, classCode);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Object[] object : classpageAssocs) {
			Map<String, Object> results = new HashMap<String, Object>();
			results.put(CLASSPAGE_ID, object[0]);
			results.put(COLLECTION_ID, object[1]);
			results.put(COLLECTION_ITEM_ID, object[2]);
			results.put(ASSOC_COLLECTION_NO, object[3]);
			results.put(DIRECTION,object[4]);
			results.put(DUEDATE,object[5]);
			results.put(COLLECTION_CREATOR, object[6]);
			results.put(CREATED_DATE, object[7]);
			results.put(LAST_MODIFIED_DATE, object[8]);
			results.put(TITLE, object[9]);
			results.put(CLASSPAGE_CREATOR, object[10]);
			result.add(results);
		}
		Map<String, Object> resultCount = new HashMap<String, Object>();
		resultCount.put(TOTAL_HIT_COUNT, this.getCollectionRepository().getClasspageAssocCount(classpageId,collectionId,title,classCode,collectionCreator));
		result.add(resultCount);
		return result;
	}
	public CollectionEventLog getScollectionEventlog() {
		return scollectionEventlog;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public UserGroupRepository getUserGroupRepository() {
		return userGroupRepository;
	}

	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public void setOperationAuthorizer(OperationAuthorizer operationAuthorizer) {
		this.operationAuthorizer = operationAuthorizer;
	}

	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	public StorageRepository getStorageRepository() {
		return storageRepository;
	}

	public void setStorageRepository(StorageRepository storageRepository) {
		this.storageRepository = storageRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public InviteService getInviteService() {
		return inviteService;
	}
	
	public ClasspageEventLog getClasspageEventlog() {
		return classpageEventlog;
	}

}