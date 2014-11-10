/////////////////////////////////////////////////////////////
// UserManagementService.java
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
package org.ednovo.gooru.domain.service.userManagement;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.domain.service.BaseService;
import org.ednovo.gooru.domain.service.search.SearchResults;


public interface UserManagementService extends BaseService {

	Set<String> checkContentAccess(User authenticationUser, String contentgooruId);
	
	Map<String, Object> userMeta(User user);

	User findByGooruId(String gooruId);

	Profile getProfile(User user);

	Profile getUserProfile(String gooruUid, Integer activeFlag);

	Identity findUserByGooruId(String gooruId);

	User getUserByToken(String userToken);

	SearchResults<Map<String, Object>> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit);
	
	Boolean isFollowedUser(String gooruUserId, User apiCaller);
	
	SearchResults<Map<String, Object>> getFollowedByUsers(String gooruUserId, Integer offset, Integer limit);

	Profile updateProfileInfo(Profile profile, String gooruUid, User apiCaller, String activeFlag, Boolean emailConfirmStatus, String showProfilePage,String accountType,String password);

	void validateUserOrganization(String organizationCode, String superAdminToken) throws Exception;

	User createUserWithValidation(User user, String password, String school, Integer confirmStatus, Boolean useGeneratedPassword, Boolean sendConfirmationMail, User apiCaller, String accountType, String dateOfBirth, String userParentId, String sessionId, String gender, String childDOB,
			String gooruClassicUrl, Boolean token, HttpServletRequest resRequest, String role, String mailConfirmationUrl) throws Exception;

	Boolean isContentAdmin(User user);

	UserToken createSessionToken(User user, String sessionId, Application application);

	User createUser(User user, String password, String school, Integer confirmStatus, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId, String remoteEntityId, String gender, String childDOB, String source, String emailSSO,
			HttpServletRequest resRequest, String role, String mailConfirmationUrl) throws Exception;

	User getUser(String gooruUId) throws Exception;

	String buildUserProfileImageUrl(User user);

	String encryptPassword(String password);

	User createUser(User user, String password, String school, Integer confirmStatus, Integer addedBySystem, String userImportCode, String accountType, String dateOfBirth, String userParentId, String gender, String childDOB, String source, HttpServletRequest resRequest, String role, String mailConfirmationUrl)
			throws Exception;

	User resetPasswordRequest(String emailId, String gooruClassicUrl, User apicaller,String mailConfirmationUrl) throws Exception;

	Identity resetCredential(String token, String gooruUid, String password, User apicaller, String mailConfirmationUrl,Boolean isPartnerPortal) throws Exception;

	String getUserEmailFromIdentity(Set<Identity> identity);

	void deleteUserMeta(String gooruUid, Profile newProfile, User apicaller);

	void deleteUserContent(String gooruUid, String newUser, User apiCaller);

	void deleteUserImageProfile(String userId) throws Exception;

	User resendConfirmationMail(String gooruUid,User apicaller,String sessionId,String gooruBaseUrl,String type) throws Exception;
	
	void updateOrgAdminCustomField(String organizationUid, User user) throws Exception;
	
	User updateUserViewFlagStatus(String gooruUid, Integer viewFlag);
	
	 Map<String, Object> followUser(User user, String followOnUserId);
	 
	 void  unFollowUser(User user, String unFollowUserId);
	 
	 Map<String, Object> getUserSummary(String gooruUid);

}
