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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserCollectionItemAssoc;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
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
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.UserGroupRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClasspageServiceImpl extends ScollectionServiceImpl implements ClasspageService, ParameterProperties {

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

	@Autowired
	private CollectionRepository collectionRepository;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Classpage getClasspage(String classpageCode, User user) throws Exception {
		Classpage classpage = this.getCollectionRepository().getClasspageByCode(classpageCode);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, "Class"), GL0056);

		}
		return getClasspage(classpage.getGooruOid(), user, PERMISSIONS);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Classpage> getClasspages(Integer offset, Integer limit, User user, String title, String author, String userName) {
		if (userService.isContentAdmin(user)) {
			List<Classpage> classpages = this.getCollectionRepository().getClasspages(offset, limit, title, author, userName);
			SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpages);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCount(title, author, userName));
			return result;
		} else {
			throw new UnauthorizedException(generateErrorMessage("GL0085"), "GL0085");

		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy) {
		return this.getCollectionRepository().getMyClasspage(offset, limit, user, skipPagination, orderBy);
	}

	@Override
	public Long getMyClasspageCount(String gooruUid) {
		return this.getCollectionRepository().getMyClasspageCount(gooruUid);
	}

	@Override
	public List<Map<String, Object>> getClassMemberList(String code, String filterBy) {
		final List<Map<String, Object>> classpageMember = new ArrayList<Map<String, Object>>();
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
		final Map<String, List<Map<String, Object>>> collaboratorList = new HashMap<String, List<Map<String, Object>>>();
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

	private List<Map<String, Object>> getActiveMemberList(final String code) {
		final List<Map<String, Object>> activeList = new ArrayList<Map<String, Object>>();
		final Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "Class"), "GL0056");

		}
		final UserGroup userGroup = this.getUserGroupService().findUserGroupByGroupCode(code);
		final List<UserGroupAssociation> userGroupAssociations = this.getUserGroupRepository().getUserGroupAssociationByGroup(userGroup.getPartyUid());
		for (final UserGroupAssociation userGroupAssociation : userGroupAssociations) {
			if (userGroupAssociation.getIsGroupOwner() != _ONE) {
				activeList.add(this.setMemberResponse(userGroupAssociation, ACTIVE));
			}
		}
		return activeList;
	}

	private List<Map<String, Object>> getPendingMemberList(final String code) {
		final Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "Class"), "GL0056");

		}
		final List<InviteUser> inviteUsers = this.getInviteRepository().getInviteUsersById(classpage.getGooruOid());
		final List<Map<String, Object>> pendingList = new ArrayList<Map<String, Object>>();
		if (inviteUsers != null) {
			for (final InviteUser inviteUser : inviteUsers) {
				pendingList.add(this.setInviteMember(inviteUser, PENDING));
			}
		}
		return pendingList;
	}

	private Map<String, Object> setInviteMember(final InviteUser inviteUser, final String status) {
		final Map<String, Object> listMap = new HashMap<String, Object>();
		listMap.put(EMAIL_ID, inviteUser.getEmailId());
		listMap.put(GOORU_OID, inviteUser.getGooruOid());
		listMap.put(ASSOC_DATE, inviteUser.getCreatedDate());
		if (status != null) {
			listMap.put(STATUS, status);
		}
		return listMap;
	}

	private Map<String, Object> setMemberResponse(final UserGroupAssociation userGroupAssociation, final String status) {
		final Map<String, Object> member = new HashMap<String, Object>();
		String externalId = null;
		if (userGroupAssociation.getUser() != null && userGroupAssociation.getUser().getAccountTypeId() != null && (userGroupAssociation.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD))) {
			externalId = userGroupAssociation.getUser().getParentUser().getIdentities().iterator().next().getExternalId();
		} else {
			externalId = userGroupAssociation.getUser().getIdentities().iterator().next().getExternalId();
		}
		member.put(EMAIL_ID, externalId);
		member.put(_GOORU_UID, userGroupAssociation.getUser().getPartyUid());
		member.put(USER_NAME, userGroupAssociation.getUser().getUsername());
		member.put(PROFILE_IMG_URL, this.getUserManagementService().buildUserProfileImageUrl(userGroupAssociation.getUser()));
		member.put(STATUS, status);
		return member;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Map<String, Object>> getMemberList(String code, Integer offset, Integer limit, String filterBy) {
		final Classpage classpage = this.getCollectionRepository().getClasspageByCode(code);
		if (classpage == null) {
			throw new NotFoundException(generateErrorMessage("GL0056", "classpage"), "GL0056");

		}
		final List<Object[]> results = this.getUserGroupRepository().getUserMemberList(code, classpage.getGooruOid(), offset, limit, filterBy);
		final SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		final List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (Object[] object : results) {
			final Map<String, Object> result = new HashMap<String, Object>();
			result.put(EMAIL_ID, object[0]);
			result.put(USER_NAME, object[1]);
			result.put(_GOORU_UID, object[2]);
			result.put(ASSOC_DATE, object[3]);
			result.put(STATUS, object[4]);
			result.put(FIRST_NAME, object[5]);
			result.put(LAST_NAME, object[6]);
			if (object[2] != null) {
				result.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(object[2]) + ".png");
			}

			listMap.add(result);
		}
		searchResult.setSearchResults(listMap);
		searchResult.setTotalHitCount(this.getUserGroupRepository().getUserMemberCount(code, classpage.getGooruOid(), filterBy));
		return searchResult;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<String> classMemberSuggest(String queryText, String gooruUid) {
		return this.getUserGroupRepository().classMemberSuggest(queryText, gooruUid);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Map<String, Object>> getMyStudy(User user, String orderBy, Integer offset, Integer limit, String type, String itemType) {
		if (user.getPartyUid().equalsIgnoreCase(ANONYMOUS)) {
			throw new NotFoundException(generateErrorMessage("GL0056", "User"), "GL0056");

		}
		final List<Object[]> results = this.getUserGroupRepository().getMyStudy(user.getPartyUid(), user.getIdentities() != null ? user.getIdentities().iterator().next().getExternalId() : null, orderBy, offset, limit, type);
		final SearchResults<Map<String, Object>> searchResult = new SearchResults<Map<String, Object>>();
		searchResult.setSearchResults(this.setMyStudy(results, itemType));
		searchResult.setTotalHitCount(this.getUserGroupRepository().getMyStudyCount(user.getPartyUid(), user.getIdentities() != null ? user.getIdentities().iterator().next().getExternalId() : null, type));
		return searchResult;
	}

	@Override
	public List<Map<String, Object>> setMyStudy(List<Object[]> results, String itemType) {
		final List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
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
			user.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(object[5]) + ".png");
			result.put(USER, user);

			final StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
			final Map<String, Object> thumbnails = new HashMap<String, Object>();
			if (object[10] != null) {
				thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[9]) + String.valueOf(object[10]));
			} else {
				thumbnails.put(URL, "");
			}
			result.put(THUMBNAILS, thumbnails);
			result.put(ITEM_COUNT, this.getCollectionRepository().getClasspageCount(object[0].toString(), itemType));
			final long member = this.getUserGroupRepository().getUserGroupAssociationCount(String.valueOf(object[2]));
			result.put(MEMBER_COUNT, member);
			listMap.add(result);
		}
		return listMap;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getClasspageItems(String gooruOid, Integer limit, Integer offset, User apiCaller, String orderBy, boolean optimize, String status, String type) {
		final List<Object[]> results = this.getCollectionRepository().getClasspageItems(gooruOid, limit, offset, apiCaller.getPartyUid(), orderBy, status, type);
		final List<Map<String, Object>> collectionItems = new ArrayList<Map<String, Object>>();
		for (Object[] object : results) {
			final Map<String, Object> result = new HashMap<String, Object>();
			final Map<String, Object> resource = new HashMap<String, Object>();
			if (!optimize) {
				result.put(ASSOCIATION_DATE, object[0]);
				resource.put(FOLDER, object[7]);
				resource.put(SHARING, object[9]);
				final Map<String, Object> thumbnails = new HashMap<String, Object>();
				if (object[8] != null) {
					final StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
					thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[7]) + String.valueOf(object[8]));
				} else {
					thumbnails.put(URL, "");
				}
				resource.put(THUMBNAILS, thumbnails);
				final Map<String, Object> user = new HashMap<String, Object>();
				user.put(USERNAME, object[12]);
				user.put(GOORU_UID, object[13]);
				user.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(object[13]) + ".png");
				resource.put(USER, user);
				resource.put(COLLECTIONITEMS, getPathawyItemWithOutValidation(object[5].toString(), 0, 10, orderBy, apiCaller));
			}
			resource.put(ITEM_COUNT, this.getCollectionRepository().getCollectionItemsCount(object[5].toString(), null, CLASSPAGE));
			resource.put(GOALS, object[10]);
			resource.put(COLLECTION_TYPE, object[24]);
			resource.put(TITLE, object[6]);
			resource.put(TYPE_NAME, object[14]);
			resource.put(GOORU_OID, object[5]);
			result.put(COLLECTION_ITEM_ID, object[1]);
			result.put(ITEM_SEQUENCE, object[2]);
			result.put(NARRATION, object[3]);
			result.put(PLANNED_END_DATE, object[4]);
			result.put(STATUS, object[11]);
			result.put(IS_REQUIRED, object[15]);
			result.put(SHOW_ANSWER_BY_QUESTIONS, object[16]);
			result.put(SHOW_HINTS, object[17]);
			result.put(SHOW_ANSWER_END, object[18]);
			result.put(MINIMUM_SCORE, object[19]);
			result.put(ESTIMATED_TIME, object[20]);
			result.put("minimumScoreByUser", object[21]);
			result.put("assignmentCompleted", object[22]);
			result.put("timeStudying", object[23]);
			result.put(RESOURCE, resource);
			collectionItems.add(result);
		}
		return collectionItems;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getClasspageAssoc(Integer offset, Integer limit, String classpageId, String collectionId, String title, String collectionTitle, String classCode, String collectionCreator, String collectionItemId) {
		String gooruUid = null;
		if (collectionCreator != null) {
			final User user = this.getUserService().getUserByUserName(collectionCreator);
			if (user != null) {
				gooruUid = user.getPartyUid();
			}
		}
		final List<Object[]> classpageAssocs = this.getCollectionRepository().getClasspageAssoc(offset, limit, classpageId, collectionId, gooruUid, title, collectionTitle, classCode, collectionItemId);
		final Map<String, Object> resultCount = new HashMap<String, Object>();
		final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (Object[] object : classpageAssocs) {
			Map<String, Object> results = new HashMap<String, Object>();
			results.put(CLASSPAGE_ID, object[0]);
			results.put(COLLECTION_ID, object[1]);
			results.put(COLLECTION_ITEM_ID, object[2]);
			results.put(ASSOC_COLLECTION_NO, object[3]);
			results.put(DIRECTION, object[4]);
			results.put(DUEDATE, object[5]);
			results.put(COLLECTION_CREATOR, object[6]);
			results.put(CREATED_DATE, object[7]);
			results.put(LAST_MODIFIED_DATE, object[8]);
			results.put(TITLE, object[9]);
			results.put(COLLECTION_TITLE, object[10]);
			results.put(CLASSPAGE_CREATOR, object[11]);
			result.add(results);
		}
		resultCount.put(SEARCH_RESULT, result);
		resultCount.put(TOTAL_HIT_COUNT, this.getCollectionRepository().getClasspageAssocCount(classpageId, collectionId, gooruUid, title, collectionTitle, classCode, collectionItemId));
		return resultCount;
	}

	private List<CollectionItem> getPathawyItemWithOutValidation(final String pathwayId, final Integer offset, final Integer limit, final String orderBy, final User user) {
		final List<CollectionItem> collectionItems = this.getCollectionRepository().getCollectionItems(pathwayId, offset, limit, orderBy, CLASSPAGE);
		for (final CollectionItem collectionItem : collectionItems) {
			final UserCollectionItemAssoc userCollectionItemAssoc = this.getCollectionRepository().getUserCollectionItemAssoc(collectionItem.getCollectionItemId(), user.getPartyUid());
			if (userCollectionItemAssoc != null) {
				if (userCollectionItemAssoc.getStatus() != null) {
					collectionItem.setStatus(userCollectionItemAssoc.getStatus().getValue());
				}
			}
		}
		return collectionItems;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getParentDetails(final String collectionItemId) {
		final List<Object[]> result = this.getCollectionRepository().getParentDetails(collectionItemId);
		final Map<String, Object> items = new HashMap<String, Object>();
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				items.put("classGooruOid", object[0]);
				items.put("classTitle", object[1]);
				items.put("pathwayGooruOid", object[2]);
				items.put("pathwayTitle", object[3]);
				items.put("collectionGooruOid", object[4]);
				items.put("collectionTitle", object[5]);
				items.put("narration", object[6]);
				items.put("plannedEndDate", object[7]);
				items.put("isRequired", object[8]);
				items.put("minimumScore", object[9]);
			}
		}
		return items;
	}

	public CollectionEventLog getScollectionEventlog() {
		return scollectionEventlog;
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

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

}
