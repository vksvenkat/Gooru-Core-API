/////////////////////////////////////////////////////////////
// UserGroupRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.party;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.PartyPermission;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface UserGroupRepository extends BaseRepository {

	UserGroup getDefaultGroupByOrganization(String organizationUid);

	UserGroup getDefaultGroupByOrganizationCode(String organizationCode);

	UserGroup getGroup(String groupName, String orgainzationUid);

	List<PartyPermission> getUserPartyPermissions(String userPartyUid);

	List<PartyPermission> getUserOrganizations(List<String> organizationIds);
	
	UserGroupAssociation getUserGroupAssociation(String gooruUid, String groupUid);
	
	List<UserGroupAssociation> getUserGroupAssociationByGroup(String groupUid);
	
	List<String> classMemberSuggest(String queryText, String gooruUid);
	
	List<Object[]> getMyStudy(String gooruUid, String mailId, String orderBy,Integer offset, Integer limit, boolean skipPagination);
	
	Long getMyStudyCount(String gooruUid, String mailId);
	
	List<Object[]> getUserMemberList(String code, String gooruOid, Integer offset, Integer limit, Boolean skipPagination,String filterBy);
	
	Long getUserMemberCount(String code, String gooruOid,String filterBy);

}
