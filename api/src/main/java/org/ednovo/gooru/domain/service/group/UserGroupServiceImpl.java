/////////////////////////////////////////////////////////////
// UserGroupServiceImpl.java
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
package org.ednovo.gooru.domain.service.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("userGroupService")
public class UserGroupServiceImpl implements UserGroupService,ParameterProperties,ConstantProperties {

	@Autowired
	private UserRepository userRepository;
		
	@Autowired
	private ContentRepository contentRepository ;
	
	
	@Override
	public UserGroup createGroup(String name, String groupCode, String userGroupType, User apiCaller, String userMailIds)  {

		Map<String, String> errorsList = validateCreateGroup(name, groupCode, userGroupType);

		UserGroup userGroup = new UserGroup();
		
		UserGroupAssociation groupAssociation = new  UserGroupAssociation();
			

		if (errorsList.isEmpty()) {
						
			userGroup.setGroupName(name);
			if(groupCode == null)
			{
				groupCode = UUID.randomUUID().toString();
			}
			userGroup.setGroupCode(groupCode);
			userGroup.setUserGroupType(userGroupType);
			userGroup.setOrganization(apiCaller.getOrganization());
			userGroup.setActiveFlag(true);
			userGroup.setPartyName(GOORU);
			userGroup.setUserUid(apiCaller.getGooruUId());
			userGroup.setPartyType(GROUP);
			userGroup.setCreatedOn(new Date());
			this.getUserRepository().save(userGroup);
			
			groupAssociation.setIsGroupOwner(1);
			groupAssociation.setAssociationDate(new Date(System.currentTimeMillis()));
			groupAssociation.setUser(apiCaller);
			groupAssociation.setUserGroup(userGroup);
			this.getUserRepository().save(groupAssociation);				
		} 
		if(errorsList.isEmpty() && userMailIds != null && userGroup != null){
			addGroupMembers(userGroup,userMailIds);
		}
		
		return userGroup;
	}

	@Override
	public String removeUserGroup(String groupUid, User apiCaller) throws Exception {
							
		if (this.getUserRepository().getUserGroupOwnerByGooruUid(apiCaller.getGooruUId(), groupUid)) {
			this.getUserRepository().removeUserGroupByGroupUid(groupUid);
		} else {
			throw new AccessDeniedException("You don't have permission to do this action");
		}
		return "Deleted Successfully";
	}

	@Override
	public Map<String, String> validateCreateGroup(String name, String groupCode, String userGroupType)  {
		Map<String, String> errorList = new HashMap<String, String>();

		if (!isNotEmptyString(name)) {
			errorList.put(GROUP_NAME, "Group name cannot be null or empty");
		}

		if (!isNotEmptyString(userGroupType)) {
			errorList.put(USER_GROUP_TYPE, "User Group Type code cannot be null or empty");
		}
		if(isNotEmptyString(groupCode))		
		{
		UserGroup userGroup = this.getUserRepository().findUserGroupByGroupCode(groupCode);
			if(userGroup != null && userGroup.getGroupCode() != null){
				if (userGroup.getGroupCode().equalsIgnoreCase(groupCode)){
					errorList.put(GROUP_CODE, "Group Code Already Exists");
				}
			}
		}
		return errorList;
	}

	@Override
	public UserGroup findUserGroupByGroupCode(String groupCode) {
		return this.getUserRepository().findUserGroupByGroupCode(groupCode);
	}

	private Boolean isNotEmptyString(String field) {
		return StringUtils.hasLength(field);
	}

	@Override
	public UserGroup updateUserGroup(String groupUid, String name, String actionType, String ownerIds, Boolean activeFlag) {

		UserGroup userGroup = this.getUserRepository().findUserGroupById(groupUid);
	
		if (name != null) {
			userGroup.setGroupName(name);
		}
		
		if (activeFlag != null) {
			userGroup.setActiveFlag(true);
		}
		
		if(userGroup != null){
		this.getUserRepository().save(userGroup);
		}
		
		if (actionType != null && ownerIds != null) {
			List<UserGroupAssociation> groupOwners = this.getUserRepository().findGroupUserByIds(ownerIds);
				for (UserGroupAssociation userGroupAss : groupOwners) {
					if(actionType.equalsIgnoreCase(ADD_OWNER))
					{
						userGroupAss.setIsGroupOwner(1);
					} else if(actionType.equalsIgnoreCase(REMOVE_OWNER))
					{
						userGroupAss.setIsGroupOwner(0);	
					}
			  }
			this.getUserRepository().saveAll(groupOwners);
		}

		return userGroup;
	}

	

	@Override
	public List<UserGroupAssociation> manageGroupUsers(String groupCode, String userMailIds, String actionType) {
				
		UserGroup userGroup = new UserGroup();
		List<UserGroupAssociation> userGroupAssocList = new ArrayList<UserGroupAssociation> ();
		if(groupCode != null){
			userGroup = this.getUserRepository().findUserGroupByGroupCode(groupCode);
		}
					
		if (userGroup != null && actionType != null) {
			
			if(actionType.equalsIgnoreCase(ADD_OWNER))
			{
				userGroupAssocList = addGroupMembers(userGroup, userMailIds);
			}
			if(actionType.equalsIgnoreCase(REMOVE_OWNER))
			{
				String gooruUids=null;
				List<String> userMailIdList = Arrays.asList(userMailIds.split(","));
				List<User> users = this.getUserRepository().findByIdentities(userMailIdList);
				for (User user : users) {
					gooruUids = user.getGooruUId()+",";
				}
				
				this.getUserRepository().removeUserGroupMemebrByGroupUid(userGroup.getPartyUid(), gooruUids);
			}
		}

		return userGroupAssocList;
		
	}

	@Override
	public List<UserGroup> findAllGroups() {
		return this.getUserRepository().findAllGroups();
	}

	@Override
	public List<User> getUserGroup(String groupUid) throws Exception {
		
		List<User> users = new ArrayList<User>();
		
		if(groupUid != null)
		{
			users = this.getUserRepository().findGroupUsers(groupUid);
		}
		
		return users;
	}
	
	
	private List<UserGroupAssociation> addGroupMembers(UserGroup userGroup, String userMailIds)
	{		
		List<String> userMailIdList = Arrays.asList(userMailIds.split(","));
		List<User> users = this.getUserRepository().findByIdentities(userMailIdList);
		List<UserGroupAssociation> groupAssocList = new ArrayList<UserGroupAssociation> ();
		for (User user : users) {
			UserGroupAssociation groupAssociation = new UserGroupAssociation();
			groupAssociation.setUser(user);
			groupAssociation.setUserGroup(userGroup);
			groupAssociation.setIsGroupOwner(0);
			groupAssocList.add(groupAssociation);
	  }
	  this.getUserRepository().saveAll(groupAssocList);
	  return groupAssocList;
	}

	@Override
	public List<ContentPermission> contentShare(String contentId, User user, String partyUids, Boolean shareOtherOrganization, String organizationId) throws Exception {
		List<ContentPermission> contentPermissionList = new ArrayList<ContentPermission> ();
		Content content = this.getContentRepository().findByContentGooruId(contentId);
		Date date = new Date();
		List<String> partyUidList = Arrays.asList(partyUids.split(","));		
		for (String partyUid : partyUidList) {
			Party  party = this.getUserRepository().findPartyById(partyUid);
			
			if (content.getUser() != null && content.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId())) {
				
				if(!this.getContentRepository().checkContentPermission(content.getContentId(), partyUids)){
					ContentPermission contentPermission = new ContentPermission();
					contentPermission.setContent(content);
					contentPermission.setParty(party);
					contentPermission.setPermission(EDIT);
					contentPermission.setValidFrom(date);
					contentPermissionList.add(contentPermission);
				}
			}
			this.getUserRepository().saveAll(contentPermissionList);
		
		}
		return contentPermissionList;
}

	public Boolean userExistsInGroup(String userUid, String apiCallerUid){
		
		return false;
	}
	
	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}
					
}
