/////////////////////////////////////////////////////////////
// CommentRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.question;

import java.util.List;

import org.ednovo.gooru.core.api.model.Comment;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryHibernate extends BaseRepositoryHibernate implements CommentRepository, ParameterProperties, ConstantProperties {

	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public Comment getComment(String commentUid) {
		Session session = getSession();
		String hql = " FROM  Comment comment WHERE  comment.commentUid=:commentUid and (comment.isDeleted != 1 or comment.isDeleted is null) and " + generateOrgAuthQuery("comment.");
		Query query = session.createQuery(hql);
		query.setParameter("commentUid", commentUid);
		addOrgAuthParameters(query);
		return (Comment) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Comment> getComments(String gooruOid, String gooruUid, Integer limit, Integer offset,String fetchType) {
		Session session = getSession();
		String hql = " FROM  Comment comment WHERE " + generateOrgAuthQuery("comment.");
		if (gooruOid != null) {	
			hql += " and comment.gooruOid = '" + gooruOid + "'";	
		}
			
		if (gooruUid != null) {
			hql += " and comment.commentorUid = '" + gooruUid + "'";
		} else {
			hql += " and comment.status.keyValue = 'comment_status_active'";
		}
		if (fetchType.equalsIgnoreCase("deleted")) {
			hql += " and comment.isDeleted = 1";
		} else if(fetchType.equalsIgnoreCase("notdeleted")) {
			hql += " and (comment.isDeleted != 1 or comment.isDeleted is null)";
		}
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(offset).setMaxResults(limit);
		return query.list();
	}
	
	@Override
	public Long getCommentCount(String gooruOid,String commentorUid,String fetchType) {
		Session session = getSession();
		String hql = "select count(*)  FROM Comment comment where "+ generateOrgAuthQuery("comment.");
		if (gooruOid != null) {	
			hql += " and comment.gooruOid = '" + gooruOid + "'";	
		}
		if (commentorUid != null) {
			hql += " and comment.commentorUid = '" + commentorUid + "'";
		} else {
			hql += " and comment.status.customTableValueId = " + this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.COMMNET_STATUS.getTable(), CustomProperties.CommentStatus.ACTIVE.getCommentStatus()).getCustomTableValueId();
		}
		if (fetchType.equalsIgnoreCase("deleted")) {
			hql += " and comment.isDeleted = 1";
		} else if(fetchType.equalsIgnoreCase("notdeleted")) {
			hql += " and (comment.isDeleted != 1 or comment.isDeleted is null)";
		}
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@Override
	public Comment findCommentByUser(String commentUid, String commentorUid) {
		Session session = getSession();
		String hql = " FROM  Comment comment WHERE  comment.commentUid=:commentUid and comment.commentorUid.partyUid=:commentorUid and " + generateOrgAuthQuery("comment.");
		Query query = session.createQuery(hql);
		query.setParameter("commentUid", commentUid);
		query.setParameter("commentorUid", commentorUid);
		addOrgAuthParameters(query);
		return (Comment) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

}
