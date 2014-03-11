/*
*UserContentRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.user;

import java.util.List;

import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("userContentRepository")
public class UserContentRepositoryHibernate extends BaseRepositoryHibernate implements UserContentRepository {

	private static final String DELETE_CONTENT = "delete uca from user_content_assoc  uca  inner join content c on (c.content_id=uca.content_id) where c.content_id = '%s' and c.organization_uid in (%s)";
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public UserContentRepositoryHibernate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Learnguide> listCommonCollections(String userGooruId, String compareUserGooruId) {
		String hql = "FROM Learnguide collection WHERE exists (FROM UserContentAssoc userContentOne WHERE userContentOne.content.contentId = collection.contentId AND userContentOne.user.partyUid = '" + userGooruId
				+ "' ) AND exists (FROM UserContentAssoc userContentTwo WHERE userContentTwo.content.contentId = collection.contentId AND userContentTwo.user.gooruUId = '" + compareUserGooruId + "' ) AND "+generateOrgAuthQueryWithData("collection.");

		return find(hql);
	}

	@Override
	public List<UserContentAssoc> listContentUserRelations(String contentGooruId) {
		String hql = "FROM UserContentAssoc contentAssoc WHERE contentAssoc.content.gooruOid = '" + contentGooruId + "' AND "+generateOrgAuthQueryWithData("contentAssoc.content.");
		return find(hql);
	}

	@Override
	public UserContentAssoc getUserContentAssoc(String userGooruId, String contentGooruId, Integer relationshipId) {
		String hql = "FROM UserContentAssoc userContent WHERE userContent.user.partyUid = '" + userGooruId + "' AND userContent.content.gooruOid = '" + contentGooruId + "' AND userContent.relationshipId = '" + userGooruId + "' AND "+generateOrgAuthQueryWithData("userContent.content.");
		return getRecord(hql);
	}

	@Override
	public UserContentAssoc getUserContentAssoc(String userId, Long contentId, Integer relationshipId) {
		String hql = "FROM UserContentAssoc userContent WHERE userContent.user.partyUid = '" + userId + "' AND userContent.content.contentId = " + contentId + " AND userContent.relationshipId = '" + relationshipId +"' AND "+generateOrgAuthQueryWithData("userContent.content.");
		return getRecord(hql);
	}

	private UserContentAssoc getRecord(String hql) {
		List<UserContentAssoc> userContentAssocs = find(hql);
		return userContentAssocs.size() > 0 ? userContentAssocs.get(0) : null;
	}
	
	@Override
	public void deleteContentRelationShips(String contentId) {
	String deleteContent = DatabaseUtil.format(DELETE_CONTENT, contentId, getUserOrganizationUidsAsString());
	this.jdbcTemplate.execute(deleteContent);
	}

	@Override
	public void deleteUserContentRelationShip(UserContentAssoc userContentAssoc) {
		getSession().delete(userContentAssoc);
	}

}
