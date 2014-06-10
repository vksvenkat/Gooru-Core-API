/////////////////////////////////////////////////////////////
// UserGroupRepositoryHibernate.java
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

import java.math.BigInteger;
import java.util.List;

import org.ednovo.gooru.core.api.model.PartyPermission;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;


@Repository
public class UserGroupRepositoryHibernate extends BaseRepositoryHibernate implements UserGroupRepository {
	

	@Override
	@Cacheable("gooruCache")
	public UserGroup getDefaultGroupByOrganization(String organizationUid) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.organization.partyUid = '" + organizationUid + "' AND userGroup.activeFlag='1'";
		List<UserGroup> groups = find(hql);
		return groups.size() > 0 ? groups.get(0) : null;
	}

	@Override
	@Cacheable("gooruCache")
	public UserGroup getDefaultGroupByOrganizationCode(String organizationCode) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.organization.organizationCode = '" + organizationCode + "' AND userGroup.activeFlag='1'";
		List<UserGroup> groups = find(hql);
		return groups.size() > 0 ? groups.get(0) : null;
	}

	@Override
	@Cacheable("gooruCache")
	public UserGroup getGroup(String groupName, String organizationUid) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.name = '" + groupName + "' AND userGroup.activeFlag='1' AND userGroup.organization.partyUid = '" + organizationUid + "' ";
		List<UserGroup> groups = find(hql);
		return groups.size() > 0 ? groups.get(0) : null;
	}

	@Override
	public List<PartyPermission> getUserPartyPermissions(String userPartyUid) {
		String hql = "FROM PartyPermission pp WHERE pp.permittedParty.partyUid = :userPartyUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("userPartyUid", userPartyUid);
		return (List<PartyPermission>) query.list();
	}

	@Override
	public List<PartyPermission> getUserOrganizations(List<String> organizationIds) {
		String hql = "FROM PartyPermission pp WHERE pp.party.partyUid IN (:organizationIds)";
		Query query = getSession().createQuery(hql);
		query.setParameterList("organizationIds", organizationIds);
		return (List<PartyPermission>) query.list();
	}
	
	@Override
	public UserGroupAssociation getUserGroupAssociation(String gooruUid,
			String groupUid) {
		Session session = getSession();
		String hql = " FROM UserGroupAssociation userGroupAssociation WHERE  userGroupAssociation.user.partyUid=:gooruUid  and userGroupAssociation.userGroup.partyUid = :groupUid  and ";
		Query query = session.createQuery(hql
				+ generateOrgAuthQuery("userGroupAssociation.user."));
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("groupUid", groupUid);
		addOrgAuthParameters(query);
		return (UserGroupAssociation) ((query.list().size() > 0) ? query.list().get(0) : null);
	}
	
	@Override
	public List<UserGroupAssociation> getUserGroupAssociationByGroup(String groupUid) {
		Session session = getSession();
		String hql = " FROM UserGroupAssociation userGroupAssociation WHERE  userGroupAssociation.userGroup.partyUid = :groupUid  and isGroupOwner != 1 and ";
		Query query = session.createQuery(hql
				+ generateOrgAuthQuery("userGroupAssociation.user."));
		query.setParameter("groupUid", groupUid);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<String> classMemberSuggest(String queryText, String gooruUid) {
		String hql= "select distinct(external_id)  as mailId from classpage c inner join user_group u on u.user_group_code = c.classpage_code inner join content cc on cc.content_id = classpage_content_id  inner join  user_group_association ug on ug.user_group_uid = u.user_group_uid inner join identity i on i.user_uid = ug.gooru_uid  where cc.user_uid=:gooruUid  and external_id like '" + queryText + "%'";
		Query query = getSession().createSQLQuery(hql).addScalar("mailId",StandardBasicTypes.STRING);
		query.setParameter("gooruUid", gooruUid);
		return query.list();
	}
	
	@Override
	public List<Object[]> getMyStudy(String gooruUid, String mailId, String orderBy,Integer offset, Integer limit, boolean skipPagination, String type) {
	    String sql = "select * from ((select cc.gooru_oid,rr.title , class.classpage_code , 'active' as status, cc.created_on , cc.user_uid, u.firstname, u.lastname, u.username, rr.folder, rr.thumbnail, ccc.item_count, 'teach' as type from user u inner join content cc on (cc.user_uid = u.gooru_uid)  inner join classpage class on (cc.content_id = class.classpage_content_id) inner join resource rr  on (rr.content_id = cc.content_id) inner join collection_item ci on (cc.content_id = ci.resource_content_id) inner join collection c on (ci.collection_content_id = c.content_id) inner join resource r on (c.content_id = r.content_id) inner join content con on (r.content_id = con.content_id) inner join collection ccc on (ccc.content_id = rr.content_id)  where con.user_uid = '"+ gooruUid+"' and c.collection_type = 'user_classpage') union (select cc.gooru_oid , rr.title , c.classpage_code ,'active' as status, ug.association_date,cc.user_uid, uu.firstname, uu.lastname, uu.username, rr.folder, rr.thumbnail, ccc.item_count, 'study' as type  from classpage c inner join user_group u on u.user_group_code = c.classpage_code inner join content cc on cc.content_id = classpage_content_id  inner join  user_group_association ug on ug.user_group_uid = u.user_group_uid inner join resource rr on rr.content_id = cc.content_id inner join user uu on uu.gooru_uid = cc.user_uid inner join collection ccc on (ccc.content_id = rr.content_id) where  ug.gooru_uid= '"+ gooruUid+"' and ug.is_group_owner != 1)) as member  ";
	    if (type != null && !type.equalsIgnoreCase("teach-study"))  {
	    	sql  += " where type = '"+ type + "' ";
	    }
	    sql += " order by created_on ";
		if(orderBy.equalsIgnoreCase("desc") || orderBy.equalsIgnoreCase("asc")) {
			sql += orderBy;
		} else {
			sql += "desc";
		}
		
		Query query = getSession().createSQLQuery(sql);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public Long getMyStudyCount(String gooruUid, String mailId, String type) {
		String sql = "select count(1) from ((select cc.gooru_oid,rr.title , class.classpage_code , 'active' as status, cc.created_on , cc.user_uid, u.firstname, u.lastname, u.username, rr.folder, rr.thumbnail, ccc.item_count, 'teach' as type from user u inner join content cc on (cc.user_uid = u.gooru_uid)  inner join classpage class on (cc.content_id = class.classpage_content_id) inner join resource rr  on (rr.content_id = cc.content_id) inner join collection_item ci on (cc.content_id = ci.resource_content_id) inner join collection c on (ci.collection_content_id = c.content_id) inner join resource r on (c.content_id = r.content_id) inner join content con on (r.content_id = con.content_id) inner join collection ccc on (ccc.content_id = rr.content_id)  where con.user_uid = '"+ gooruUid+"' and c.collection_type = 'user_classpage') union (select cc.gooru_oid , rr.title , c.classpage_code ,'active' as status, ug.association_date,cc.user_uid, uu.firstname, uu.lastname, uu.username, rr.folder, rr.thumbnail, ccc.item_count, 'study' as type  from classpage c inner join user_group u on u.user_group_code = c.classpage_code inner join content cc on cc.content_id = classpage_content_id  inner join  user_group_association ug on ug.user_group_uid = u.user_group_uid inner join resource rr on rr.content_id = cc.content_id inner join user uu on uu.gooru_uid = cc.user_uid inner join collection ccc on (ccc.content_id = rr.content_id) where  ug.gooru_uid= '"+ gooruUid+"' and ug.is_group_owner != 1)) as member  ";
	    if (type != null && !type.equalsIgnoreCase("teach-study"))  {
	    	sql  += " where type = '"+ type + "' ";
	    }
		Query query = getSession().createSQLQuery(sql);
		return ((BigInteger)query.list().get(0)).longValue();
	}
	
	@Override
	public List<Object[]> getUserMemberList(String code, String gooruOid, Integer offset, Integer limit, Boolean skipPagination, String filterBy) {
		String sql= "select * from  ((select external_id, ui.username, ui.gooru_uid,  ug.association_date ,'active' as status   from classpage c inner join user_group u on u.user_group_code = c.classpage_code inner join content cc on cc.content_id = classpage_content_id  inner join  user_group_association ug on ug.user_group_uid = u.user_group_uid inner join identity i on i.user_uid = ug.gooru_uid inner join user ui on ui.gooru_uid = i.user_uid   where  c.classpage_code = '"+code+"' and ug.is_group_owner != 1) union (select email,  username ,gooru_uid, created_date, 'pending' as status from invite_user iu inner join custom_table_value ctv on ctv.custom_table_value_id = iu.status_id left join identity i on iu.email = i.external_id left join user user on user.gooru_uid = i.user_uid   where gooru_oid = '"+gooruOid+"' and ctv.value= 'pending')) as member";
		
		if(filterBy != null) {
			sql += " where status='"+ filterBy +"'";
		}
		
		Query query = getSession().createSQLQuery(sql);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	@Override
	public Long getUserMemberCount(String code, String gooruOid, String filterBy) {
		String sql= "select count(1) from  ((select external_id, ui.username, ui.gooru_uid, null as createdDate ,'active' as status   from classpage c inner join user_group u on u.user_group_code = c.classpage_code inner join content cc on cc.content_id = classpage_content_id  inner join  user_group_association ug on ug.user_group_uid = u.user_group_uid inner join identity i on i.user_uid = ug.gooru_uid inner join user ui on ui.gooru_uid = i.user_uid   where  c.classpage_code = '"+code+"' and ug.is_group_owner != 1) union (select email,  username ,gooru_uid, created_date, 'pending' as status from invite_user iu inner join custom_table_value ctv on ctv.custom_table_value_id = iu.status_id left join identity i on iu.email = i.external_id left join user user on user.gooru_uid = i.user_uid   where gooru_oid = '"+gooruOid+"' and ctv.value= 'pending')) as member ";
		
		if(filterBy != null) {
			sql += " where status='"+ filterBy +"'";
		}
		Query query = getSession().createSQLQuery(sql);
		return ((BigInteger)query.list().get(0)).longValue();
	}
	
}
