/////////////////////////////////////////////////////////////
// UserService.java
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
package org.ednovo.gooru.domain.service.user;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserCredential;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserToken;
import org.json.JSONException;
import org.springframework.util.MultiValueMap;


public interface UserService {

	User createUser(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Integer addedBySystem, String importCode, String accountType, String dateOfBirth, String userParentId, String gender,
			String childDOB, String source, String referenceUid, String role, String domainName) throws Exception;

	User createUser(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Integer addedBySystem, String importCode, String accountType, String dateOfBirth, String userParentId, String remoteEntityId,
			String gender, String childDOB, String source, String emailSSO, String referenceUid, String role) throws Exception;

	String encryptPassword(String password);

	List<User> getFollowedByUsers(String gooruUId,Integer offset, Integer limit);

	List<User> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit);

	UserRelationship followUser(User user, String gooruFollowOnUserId);

	boolean unFollowUser(String gooruUserId, String gooruFollowOnUserId);

	void signout(String sessionToken);

	User updateUserRole(String gooruUid, UserRoleType role);

	boolean hasResetTokenValid(String token);

	UserToken signIn(String username, String password, String apiKey, String sessionId, boolean isSsoLogin);

	User findByToken(String sessionToken);

	User findByIdentity(Identity identity);

	List<Identity> findAllIdentities();

	boolean findRegisteredUser(String emailId);

	Identity findByEmail(String emailId);

	Profile getProfile(User user);

	User findByGooruId(String gooruId);

	void registerUser(String emailId, String date);

	void updateAgeCheck(User user, String ageCheck);

	int findAgeCheck(User user);

	Identity findIdentityByResetToken(String resetToken);

	Identity findIdentityByRegisterToken(String registerToken);

	Identity findUserByGooruId(String gooruId);

	boolean checkUserAvailability(String keyword, CheckUser type, boolean isCollaboratorCheck);

	List<User> listUsers();

	User createUserWithValidation(String firstName, String lastName, String email, String password, String school, String username, Integer confirmStatus, String organizationCode, Boolean useGeneratedPassword, Boolean sendConfirmationMail, User apiCaller, String accountType, String dateOfBirth,
			String userParentId, String sessionId, String gender, String childDOB, String gooruClassicUrl, String referenceUid, String role, String pearsonEmailId, String domainName) throws Exception;

	User getUser(String gooruUId) throws Exception;

	Profile updateUserInfo(String gooruUId, MultiValueMap<String, String> data, User apiCaller, Boolean isDisableUser) throws Exception;

	User revokeUserRole(String gooruUId, String roles, User apiCaller) throws Exception;

	User grantUserRole(String gooruUId, String roles, User apiCaller) throws Exception;

	User findUserByImportCode(String userImportCode);

	String deleteUser(String gooruUId) throws Exception;

	UserRole findUserRoleByName(String name);

	UserRole findUserRoleByRoleId(Short roleId);

	UserRole createRole(String name, String description, User user) throws Exception;

	List<RoleEntityOperation> updateRoleOperation(Integer roleId, String operations) throws Exception;

	String removeRoleOperation(Integer roleId, String operations) throws Exception;

	List<RoleEntityOperation> getUserOperations(String roleNames) throws Exception;

	List<UserRole> findAllRoles();

	Map<String, String> validateUserAdd(String firstName, String lastName, String email, String password, String username, User user, String childDOB, String accountType, String dateOfBirth, String organizationCode) throws Exception;

	UserCredential getUserCredential(User user, String key, String skipCache, String sharedSecretKey);

	User getUserByUserName(String userName);

	UserToken createSessionToken(User user, String sessionId, ApiKey apiKey);

	Map<String, Object> getUserAvailability(String keyword, String type, boolean isCollaboratorCheck, String collectionId, User apiCaller);

	Map<String, Object> getRegisterUserInfo(String userId);

	String buildUserProfileImageUrl(User user);

	void validateUserLogin(String username, String password, boolean isSsoLogin);

	User updateUserConfirmStatus(String gooruUId, Integer confirmStatus, User apiCaller) throws Exception;

	Identity findByEmailIdOrUserName(String userName, Boolean isLoginRequest, Boolean fetchAllUser);

	void deactivateUser(Identity identity);

	void sendUserRegistrationConfirmationMail(String gooruUid, String accountType, String sessionId, String dateOfBirth, String gooruClassicUrl) throws Exception;

	boolean checkPasswordWithAlphaNumeric(String password);

	void validatePassword(String password, String userName);

	boolean checkUserFirstLogin(String userId);

	Boolean isAnonymous(User user);

	List<String> getUserPartyPermissions(User user);

	boolean checkUsernameStartAndEndWithSpecialCharacters(String username, boolean isStart);

	boolean checkLatinWordInUserName(String username);

	boolean checkUsernameIsRestricted(String username, String organizationUid);

	boolean containsWhiteSpace(String username);

	List<User> listUsers(Map<String, String> filters);

	User updateViewFlagStatus(String gooruUid, Integer viewFlag);

	Timestamp getSystemCurrentTime();

	UserToken partnerSignin(Map<String, Object> paramMap, String sessionId, String url, Long expires) throws Exception;

	Boolean isContentAdmin(User user);

	void validateUserOrganization(String organizationCode) throws Exception;

	List<User> findByIdentities(List<String> idList);

	Boolean checkCollaboratorsPermission(String collectionId, User collaborator, String contentType);

	UserToken loginAs(String sessionToken, String gooruUid, String apiKey, Boolean isReference) throws Exception;
	
	Integer calculateCurrentAge(String dateOfBirth);
	
	 Boolean isSuperAdmin(User user);
	 
	 public void getEventLogs(Identity identity, UserToken userToken) throws JSONException;
}
