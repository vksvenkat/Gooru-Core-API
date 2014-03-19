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

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.InviteService;
import org.ednovo.gooru.domain.service.ScollectionServiceImpl;
import org.ednovo.gooru.domain.service.group.UserGroupService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.task.TaskService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.UserGroupRepository;
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
	private UserGroupRepository userGroupRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserGroupService userGroupService;
	
	@Autowired
	private UserManagementService  userManagementService;
	
	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private InviteRepository inviteRepository;
	
	@Autowired
	private InviteService inviteService;



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
	public ActionResponseDTO<Classpage> createClasspage(Classpage newClasspage, CollectionItem newCollectionItem,
			String gooruOid, User user) throws Exception {
			Errors errors = validateClasspage(newClasspage);
			if (!errors.hasErrors()) {
				this.getCollectionRepository().save(newClasspage);
				this.getUserGroupService().createGroup(newClasspage.getTitle(), newClasspage.getClasspageCode(), "System", user, null);
				if (gooruOid != null && !gooruOid.isEmpty() && newCollectionItem != null) {
					CollectionItem collectionItem = new CollectionItem();
					collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
					collectionItem.setPlannedEndDate(newCollectionItem.getPlannedEndDate());
					collectionItem.setNarration(newCollectionItem.getNarration());
					collectionItem = this.createCollectionItem(gooruOid, newClasspage.getGooruOid(), collectionItem, newClasspage.getUser(), CollectionType.COLLECTION.getCollectionType(), false).getModel();
					Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
					collectionItems.add(collectionItem);
					newClasspage.setCollectionItems(collectionItems);
					this.getCollectionRepository().save(newClasspage);
				}
			}
			return new ActionResponseDTO<Classpage>(newClasspage, errors);
	}

	public ActionResponseDTO<Classpage> updateClasspage(Classpage newClasspage, String updateClasspageId , Boolean hasUnrestrictedContentAccess ) throws Exception {
		Classpage classpage = this.getClasspage(updateClasspageId, null,null);
		Errors errors = validateUpdateClasspage(classpage, newClasspage);
		if (!errors.hasErrors()) {
			if (newClasspage.getVocabulary() != null) {
				classpage.setVocabulary(newClasspage.getVocabulary());
			}

			if (newClasspage.getTitle() != null) {
				classpage.setTitle(newClasspage.getTitle());
				UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode());
				userGroup.setGroupName(newClasspage.getTitle());
				this.getUserRepository().save(userGroup);
			}
			if (newClasspage.getDescription() != null) {
				classpage.setDescription(newClasspage.getDescription());
			}
			if (newClasspage.getNarrationLink() != null) {
				classpage.setNarrationLink(newClasspage.getNarrationLink());
			}
			if (newClasspage.getEstimatedTime() != null) {
				classpage.setEstimatedTime(newClasspage.getEstimatedTime());
			}
			if (newClasspage.getNotes() != null) {
				classpage.setNotes(newClasspage.getNotes());
			}
			if (newClasspage.getGoals() != null) {
				classpage.setGoals(newClasspage.getGoals());
			}
			if (newClasspage.getKeyPoints() != null) {
				classpage.setGoals(newClasspage.getKeyPoints());
			}
			if (newClasspage.getLanguage() != null) {
				classpage.setLanguage(newClasspage.getLanguage());
			}
			if (newClasspage.getGrade() != null) {
				classpage.setGrade(newClasspage.getGrade());
			}
			if (newClasspage.getSharing() != null ){
				if (newClasspage.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newClasspage.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newClasspage.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
				classpage.setSharing(newClasspage.getSharing());
				}
			}
			if (newClasspage.getLastUpdatedUserUid() != null) {
				classpage.setLastUpdatedUserUid(newClasspage.getLastUpdatedUserUid());
			}
			
			if (hasUnrestrictedContentAccess) {
				if(newClasspage.getCreator() != null && newClasspage.getCreator().getPartyUid()!= null){
					User user = userService.findByGooruId(newClasspage.getCreator().getPartyUid());
					classpage.setCreator(user);						
				}
				
				if (newClasspage.getUser() != null && newClasspage.getUser().getPartyUid() != null) {
					User user = userService.findByGooruId(newClasspage.getUser().getPartyUid());		
					classpage.setUser(user);					
				}
		    }  
			
			this.getCollectionRepository().save(classpage);
		}
		return new ActionResponseDTO<Classpage>(classpage, errors);
	}
	
	@Override
	public Classpage getClasspage(String classpageCode, User user) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageCode);
		if (classpage == null) {
			throw new NotFoundException("Class not found");
		}
		return getClasspage(classpage.getGooruOid(), user, PERMISSIONS);
	}
	
	
	@Override
	public void deleteClasspage(String classpageId) {
		Classpage classpage = this.getClasspage(classpageId, null, null);
		if (classpage != null) {
			this.getUserRepository().remove(this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode()));
			this.getCollectionRepository().remove(Classpage.class, classpage.getContentId());
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, CLASSPAGE));
		}
	}

	@Override
	public List<Classpage> getClasspage(Map<String, String> filters, User user) {
		return this.getCollectionRepository().getClasspage(filters, user);
	}
	
	@Override
	public SearchResults<Classpage> getClasspages(Integer offset, Integer limit, Boolean skipPagination, User user ,String title, String author, String userName) {
		if (userService.isContentAdmin(user)) {
			List <Classpage> classpages = this.getCollectionRepository().getClasspages(offset, limit, skipPagination,title,author,userName);
			SearchResults<Classpage>  result = new SearchResults<Classpage>();
			result.setSearchResults(classpages);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCount(title,author,userName));
			return result;
		} else {
			throw new UnauthorizedException("user don't have permission ");
		}
	}

	@Override
	public Classpage getClasspage(String collectionId, User user, String merge) {
		Classpage classpage = this.getCollectionRepository().getClasspageByGooruOid(collectionId, null);
		if (classpage != null && merge != null) {
			Map<String, Object> permissions = new HashMap<String, Object>();
			Boolean isMember = false;
			InviteUser inviteUser = null;
			String mailId = null;
			UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(classpage.getClasspageCode());
			if (userGroup != null) {
				isMember = this.getUserRepository().getUserGroupMemebrByGroupUid(userGroup.getPartyUid(), user.getPartyUid()) != null ? true : false;
				if (user.getIdentities() != null) {
					mailId = user.getIdentities().iterator().next().getExternalId();
				}
				inviteUser = this.getInviteRepository().findInviteUserById(mailId, collectionId);
				if (!isMember && inviteUser == null && classpage.getSharing().equalsIgnoreCase(PRIVATE)) {
					this.getInviteRepository().save(this.getInviteService().createInviteUserObj(mailId, collectionId, CLASS, user));
					this.getInviteRepository().flush();
				}
			}
			if (merge.contains(PERMISSIONS)) {
				permissions.put(PERMISSIONS, this.getContentService().getContentPermission(collectionId, user));
			}
			permissions.put(ISMEMBER, isMember);
			classpage.setMeta(permissions);
		}
		return classpage;
	}
	
	@Override
	public ActionResponseDTO<CollectionItem> createClasspageItem(String assignmentGooruOid, String classpageGooruOid, CollectionItem collectionItem, User user, String type) throws Exception {
		Classpage classpage = null;
		if (type != null && type.equalsIgnoreCase(CollectionType.USER_CLASSPAGE.getCollectionType())) {
			if (classpageGooruOid != null) {
				classpage = this.getClasspage(classpageGooruOid, null,null);
			} else {
				classpage = this.getCollectionRepository().getUserShelfByClasspageGooruUid(user.getGooruUId(), CollectionType.USER_CLASSPAGE.getCollectionType());
			}
			if (classpage == null) {
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
			classpage = this.getClasspage(classpageGooruOid, null,null);
			collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		}

		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(assignmentGooruOid, classpage.getUser().getGooruUId());
		Errors errors = validateClasspageItem(classpage, collection, collectionItem);
		if (collection != null) {
			if (!errors.hasErrors()) {
				collectionItem.setCollection(classpage);
				collectionItem.setResource(collection);
				int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
				collectionItem.setItemSequence(sequence);
				this.getCollectionRepository().save(collectionItem);
			}
		} else {
			throw new Exception("invalid assignmentId -" + assignmentGooruOid);
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
	public List<Classpage> getMyClasspage(Integer offset, Integer limit,  User user, boolean skipPagination, String orderBy) {
		return this.getCollectionRepository().getMyClasspage(offset, limit,  user, skipPagination, orderBy);
	}
	
	@Override
	public Long getMyClasspageCount(String gooruUid) {
		return this.getCollectionRepository().getMyClasspageCount(gooruUid);
	}
	
	@Override
	public ActionResponseDTO<Classpage> createClasspage(Classpage classpage, String collectionId)  throws Exception {
		
		return this.createClasspage(classpage, classpage.getCollectionItem() , collectionId, classpage.getUser());
	}
	
	@Override
	public List<Map<String, Object>> classpageUserJoin(String code, List<String> mailIds, User apiCaller) throws Exception {
		List<Map<String, Object>> classpageMember = new ArrayList<Map<String, Object>>();
		UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(code);
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (userGroup != null && classpage != null) {
			for (String mailId : mailIds) {
				Identity identity = this.getUserRepository().findByEmailIdOrUserName(mailId, true, false);
				if (identity != null) {
					InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, classpage.getGooruOid());
					if(inviteUser != null) {
						inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, ACTIVE));
						inviteUser.setJoinedDate(new Date(System.currentTimeMillis()));
						this.getInviteRepository().save(inviteUser);
					}
					if (this.getUserRepository().getUserGroupMemebrByGroupUid(userGroup.getPartyUid(), identity.getUser().getPartyUid()) == null) {
						UserGroupAssociation groupAssociation = new UserGroupAssociation();
						groupAssociation.setIsGroupOwner(0);
						groupAssociation.setUser(identity.getUser());
						groupAssociation.setUserGroup(userGroup);
						this.getUserRepository().save(groupAssociation);
						classpageMember.add(setMemberResponse(groupAssociation,ACTIVE));
					}
				}
			}
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
						InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, classpage.getGooruOid());
						if(inviteUser != null) {
							this.getInviteRepository().remove(inviteUser);
						}
						this.getUserGroupRepository().remove(userGroupAssociation);
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
			throw new NotFoundException("Class not found!!!");
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
			throw new NotFoundException("Class not found!!!");
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
		member.put(EMAIL_ID, userGroupAssociation.getUser().getIdentities() != null ?userGroupAssociation.getUser().getIdentities().iterator().next().getExternalId() : null);
		member.put(_GOORU_UID, userGroupAssociation.getUser().getPartyUid());
		member.put(USER_NAME, userGroupAssociation.getUser().getUsername());
		member.put(PROFILE_IMG_URL, this.getUserManagementService().buildUserProfileImageUrl(userGroupAssociation.getUser()));
		member.put(STATUS, status);
		return member;
	}


	private Errors validateClasspage(Classpage classpage) throws Exception {
		final Errors errors = new BindException(classpage,CLASSPAGE);
		if (classpage != null) {
			rejectIfNullOrEmpty(errors, classpage.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	private Errors validateUpdateClasspage(Collection collection, Collection newCollection) throws Exception {
		final Errors errors = new BindException(collection, COLLECTION);
		rejectIfNull(errors, collection, "collection.all", GL0006, generateErrorMessage(GL0006, COLLECTION));
		return errors;
	}

	private Errors validateClasspageItem(Classpage classpage, Resource resource, CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED,COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, classpage, COLLECTION, GL0056, generateErrorMessage(GL0056, CLASSPAGE));
			rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
			rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		}
		return errors;
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


	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
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


}
