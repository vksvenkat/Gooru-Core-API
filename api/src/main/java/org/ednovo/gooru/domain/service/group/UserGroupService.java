/*
*UserGroupService.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.group;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;


public interface UserGroupService {

	UserGroup createGroup(String name, String groupCode, String userGroupType, User user, String userMailIds) throws Exception;

	String removeUserGroup(String groupUid, User user) throws Exception;

	UserGroup updateUserGroup(String groupUid, String name, String ownerFlag, String ownerId, Boolean activeFlag);

	Map<String, String> validateCreateGroup(String name, String groupUid, String userGroupType) throws Exception;

	UserGroup findUserGroupByGroupCode(String groupUid);

	List<UserGroupAssociation> manageGroupUsers(String groupCode, String userMailIds, String actionType);

	List<UserGroup> findAllGroups();

	List<User> getUserGroup(String groupUid) throws Exception;

	List<ContentPermission> contentShare(String contentId, User user, String partyUids, Boolean shareOtherOrganization, String OrganizationId) throws Exception;

}
