/*
 *JobRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.InviteUser;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class InviteRepositoryHibernate extends BaseRepositoryHibernate implements InviteRepository {
	@Override
	public InviteUser findInviteUserById(String mailId, String gooruOid, String status) {
		String hql = "from InviteUser iu where iu.emailId=:mailId and iu.gooruOid=:gooruOid  ";
		if(status != null) {
			hql += " and iu.status.value=:pending";
		}
		hql += " order by iu.createdDate desc";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("mailId", mailId);
		if(status != null) {
			query.setParameter("pending", status);
		} 
		return (InviteUser) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<InviteUser> getInviteUsersById(String gooruOid) {
		String hql = "from InviteUser iu where  iu.gooruOid=:gooruOid and iu.status.value=:pending order by createdDate desc";
		Query query = getSession().createQuery(hql);
		query.setParameter("pending", "pending");
		query.setParameter("gooruOid", gooruOid);
		return list(query);
	}

	@Override
	public List<InviteUser> getInviteUserByMail(String mailId, String inviteType) {
		String hql = "from InviteUser iu where  iu.emailId=:mailId and iu.status.value=:pending and iu.invitationType=:inviteType order by createdDate desc";
		Query query = getSession().createQuery(hql);
		query.setParameter("mailId", mailId);
		query.setParameter("pending", "pending");
		query.setParameter("inviteType", "collaborator");
		return list(query);
	}

	@Override
	public Long getInviteUsersCountById(String gooruOid) {
		String hql = "select count(*) from InviteUser iu where  iu.gooruOid=:gooruOid and iu.status.value=:pending";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("pending", "pending");
		return (Long) query.list().get(0);
	}

}
