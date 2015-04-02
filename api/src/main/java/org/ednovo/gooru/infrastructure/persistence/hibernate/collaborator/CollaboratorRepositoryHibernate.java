/////////////////////////////////////////////////////////////
// CollaboratorRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.collaborator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollaboratorRepositoryHibernate extends BaseRepositoryHibernate implements CollaboratorRepository {

	@Override
	public UserContentAssoc findCollaboratorById(String gooruOid, String gooruUid) {
		String hql = "from UserContentAssoc uc where uc.content.gooruOid=:gooruOid and uc.user.partyUid=:gooruUid order by uc.associationDate desc";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("gooruUid", gooruUid);
		return (UserContentAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<String> collaboratorSuggest(String text, String gooruUid) {
		String hql = "select external_id as mailId from  identity i inner join user_content_assoc uc on uc.user_uid = i.user_uid where uc.associated_by_uid=:gooruUid and i.external_id like '" + text.replace("'", "\\") + "%'";
		Query query = getSession().createSQLQuery(hql).addScalar("mailId", StandardBasicTypes.STRING);
		query.setParameter("gooruUid", gooruUid);
		return query.list();
	}

	@Override
	public List<UserContentAssoc> getCollaboratorsById(String gooruOid) {
		String hql = "from UserContentAssoc uc where uc.content.gooruOid=:gooruOid ";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		return query.list().size() > 0 ? query.list() : null;
	}

	public Long getCollaboratorsCountById(String gooruOid) {
		String hql = "select count(*) from UserContentAssoc uc where uc.content.gooruOid=:gooruOid ";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		return (Long) query.list().get(0);
	}

	@Override
	public List<User> findCollaborators(String gooruContentId, String userUid) {

		List<User> userList = new ArrayList<User>();
		String findCollaborators = "Select u.user_id, u.gooru_uid, u.firstname, u.lastname, i.external_id,u.username, u.organization_uid, u.primary_organization_uid from user u, content c , content_permission p, identity i where gooru_oid = '" + gooruContentId
				+ "' and p.permission = 'edit' and u.gooru_uid = i.user_uid and c.content_id = p.content_id and u.gooru_uid = p.party_uid ";
		if (userUid != null) {
			findCollaborators += " and p.party_uid = '" + userUid + "'";
		}

		Session session = getSession();
		Query query = session.createSQLQuery(findCollaborators).addScalar("user_id", StandardBasicTypes.INTEGER).addScalar("gooru_uid", StandardBasicTypes.STRING).addScalar("firstname", StandardBasicTypes.STRING).addScalar("lastname", StandardBasicTypes.STRING)
				.addScalar("external_id", StandardBasicTypes.STRING).addScalar("username", StandardBasicTypes.STRING).addScalar("organization_uid", StandardBasicTypes.STRING).addScalar("primary_organization_uid", StandardBasicTypes.STRING);

		List<Object[]> results = query.list();

		for (Object[] object : results) {
			Set<Identity> idSet = new HashSet<Identity>();
			User user = new User();
			Identity id = new Identity();

			user.setPartyUid((String) object[1]);
			user.setUserId((Integer) object[0]);
			user.setGooruUId((String) object[1]);
			user.setFirstName((String) object[2]);
			user.setLastName((String) object[3]);
			id.setExternalId((String) object[4]);
			user.setUsername((String) object[5]);
			String organizationUid = (String) object[6];
			if (organizationUid == null) {
				organizationUid = (String) object[7];
			}
			Organization organization = new Organization();
			organization.setPartyUid(organizationUid);
			user.setOrganization(organization);

			idSet.add(id);

			user.setIdentities(idSet);
			user.setEmailId(id.getExternalId());
			userList.add(user);
		}
		return userList;
	}
}
