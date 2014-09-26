/////////////////////////////////////////////////////////////
// UserRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.EntityOperation;
import org.ednovo.gooru.core.api.model.Gender;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserClassification;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.api.model.UserSummary;

public interface UserRepository extends BaseRepository {

	List<User> findByRole(UserRole role);

	User findByToken(String sessionToken);

	User findByIdentity(Identity identity);

	List<User> findByIdentities(List<String> idList);

	List<Identity> findAllIdentities();

	boolean findRegisteredUser(String emailId);

	Identity findByEmail(String emailId);

	Profile getProfile(User user, boolean isSsoLogin);

	User findByGooruId(String gooruId);

	void registerUser(String emailId, String date);

	String checkUserStatus(String email, String code);

	void invite(String firstname, String lastname, String email, String school, String message, String date);

	void updateAgeCheck(User user, String ageCheck);

	int findAgeCheck(User user);

	List<User> getFollowedByUsers(String gooruUId, Integer offset, Integer limit);
	
	long getFollowedByUsersCount(String gooruUId);

	List<User> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit);
	
	long getFollowedOnUsersCount(String gooruUId);

	UserRelationship getActiveUserRelationship(String gooruUserId, String gooruFollowOnUserId);

	List<UserRoleAssoc> findUserRoleSet(User user);

	UserGroup findUserGroupById(String groupUid);

	Identity findIdentityByResetToken(String resetToken);

	Identity findIdentityByRegisterToken(String registerToken);

	Identity findUserByGooruId(String gooruId);

	boolean checkUserAvailability(String keyword, CheckUser type, boolean isCollaboratorCheck);

	List<User> listUsers();

	Gender getGenderByGenderId(String genderId);

	List<UserRole> findRolesByNames(String roles);

	List<User> findUserByIds(String ownerIds);

	UserRole findUserRoleByName(String name, String organizationUids);

	User findUserByImportCode(String importCode);

	UserRole findUserRoleByRoleId(Short roleId);

	EntityOperation findEntityOperation(String entityName, String operationName);

	RoleEntityOperation checkRoleEntity(Short roleId, Integer entityOperationId);

	List<RoleEntityOperation> getRoleEntityOperations(Short roleId);

	List<RoleEntityOperation> findEntityOperationByRole(String roleNames);

	List<UserRoleAssoc> getUserRoleByName(String roles, String userId);

	List<UserRole> findAllRoles();

	User getUserByUserName(String userName, boolean isLoginRequest);

	Identity findByEmailIdOrUserName(String userName, Boolean isLoginRequest, Boolean fetchAllUser);

	boolean checkUserFirstLogin(String userId);

	User getUserByUserId(Integer userId);

	UserGroup findUserGroupByGroupCode(String groupCode);

	List<User> findGroupUsers(String groupUid);

	String removeUserGroupByGroupUid(String groupUid);

	List<UserGroup> findAllGroups();

	String removeUserGroupMemebrByGroupUid(String groupUid, String gooruUids);

	boolean getUserGroupOwnerByGooruUid(String gooruUid, String groupUid);

	List<UserGroupAssociation> findGroupUserByIds(String ownerIds);

	List<User> listUsers(Map<String, String> filters);

	Party findPartyById(String partyUid);

	User findUserByPartyUid(String partyUid);

	User findUserWithoutOrganization(String username);

	Timestamp getSystemCurrentTime();

	UserClassification getUserClassification(String gooruUid, Integer classificationId, Integer codeId, String creatorUid, String grade);

	List<UserClassification> getUserClassifications(String gooruUid, Integer classificationId, Integer activeFlag);

	List<Object[]> getInactiveUsers(Integer offset, Integer limit);

	Integer getInactiveUsersCount();

	User findByGooruIdforSuperAdmin(String gooruId);

	Integer getUserTokenCount(String gooruUid);

	String getUserGrade(String userUid, Integer classificationId, Integer activeFlag);

	User findByReferenceUid(String referenceUid);

	Integer getUserBirthdayCount();

	List<Object[]> listUserByBirthDay(Integer offset, Integer limit);
	
	Integer getChildUserBirthdayCount();
	
	List<Object[]> listChildUserByBirthDay();
	
	UserGroupAssociation getUserGroupMemebrByGroupUid(String groupUid, String gooruUid);
	
	UserSummary getSummaryByUid(String gooruUid);
	
	User findByRemeberMeToken(String remeberMeToken);
	
	public Integer getChildAccountCount(String userUId);
	
	User findByIdentityLogin(Identity identity);	
		
	}

