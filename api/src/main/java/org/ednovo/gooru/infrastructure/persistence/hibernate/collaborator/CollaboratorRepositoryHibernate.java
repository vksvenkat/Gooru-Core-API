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

import java.util.List;

import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollaboratorRepositoryHibernate extends BaseRepositoryHibernate implements CollaboratorRepository {

	@Override
	public UserContentAssoc findCollaboratorById(String gooruOid, String gooruUid) {
		String hql= "from UserContentAssoc uc where uc.content.gooruOid=:gooruOid and uc.user.partyUid=:gooruUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("gooruUid", gooruUid);
		return (UserContentAssoc) ((query.list().size() > 0) ?query.list().get(0) : null);
	}

	@Override
	public InviteUser findInviteUserById(String mailId, String gooruOid) {
		String hql= "from InviteUser iu where iu.email=:mailId and iu.gooruOid=:gooruOid and iu.status.value=:pending";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("mailId", mailId);
		query.setParameter("pending", "pending");
		return (InviteUser) ((query.list().size() > 0) ?query.list().get(0) : null);
	}

	@Override
	public List<String> collaboratorSuggest(String text, String gooruUid) {
		String hql= "select external_id as mailId from  identity i inner join user_content_assoc uc on uc.user_uid = i.user_uid where uc.associated_by_uid=:gooruUid and i.external_id like '" +text+ "%'";
		Query query = getSession().createSQLQuery(hql).addScalar("mailId",StandardBasicTypes.STRING);
		query.setParameter("gooruUid", gooruUid);
		return query.list();
	}
	
	@Override
	public List<UserContentAssoc> getCollaboratorsById(String gooruOid) {
		String hql= "from UserContentAssoc uc where uc.content.gooruOid=:gooruOid ";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		return query.list().size() > 0 ?query.list() : null;
	}

	@Override
	public List<InviteUser> getInviteUsersById(String gooruOid) {
		String hql= "from InviteUser iu where  iu.gooruOid=:gooruOid and iu.status.value=:pending";
		Query query = getSession().createQuery(hql);
		query.setParameter("pending", "pending");
		query.setParameter("gooruOid", gooruOid);
		return query.list();
	}
	
	public Long getCollaboratorsCountById(String gooruOid) {
		String hql= "select count(*) from UserContentAssoc uc where uc.content.gooruOid=:gooruOid ";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		return (Long)query.list().get(0);
	}

	public Long getInviteUsersCountById(String gooruOid) {
		String hql= "select count(*) from InviteUser iu where  iu.gooruOid=:gooruOid and iu.status.value=:pending";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("pending", "pending");
		return (Long)query.list().get(0);
	}

	@Override
	public List<InviteUser> getInviteUserByMail(String mailId) {
		String hql= "from InviteUser iu where  iu.email=:mailId and iu.status.value=:pending";
		Query query = getSession().createQuery(hql);
		query.setParameter("mailId", mailId);
		query.setParameter("pending", "pending");
		return query.list();
	}

}
