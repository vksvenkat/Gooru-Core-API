/////////////////////////////////////////////////////////////
// PostRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Post;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryHibernate extends BaseRepositoryHibernate implements PostRepository, ConstantProperties, ParameterProperties {

	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public Post getPost(String gooruOid) {
		Session session = getSession();
		String hql = " FROM Post post WHERE  post.gooruOid=:gooruOid  and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("post."));
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		List<Post> post = query.list();
		return (post.size() > 0) ? post.get(0) : null;
	}

	@Override
	public List<Post> getPosts(String type, Integer limit, Integer offset) {
		String hql = "FROM Post post WHERE  ";
		if (type != null) {
			hql += "post.target.value='" + type + "' and ";
		}
		Session session = getSession();
		Query query = session.createQuery(hql + generateOrgAuthQuery("post."));
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();

	}

	@Override
	public List<Post> getUserPosts(String gooruUid, Integer limit, Integer offset, Boolean skipPagination) {
		String hql = "FROM Post post WHERE post.assocUserUid=:gooruUid and " + generateOrgAuthQuery("post.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public List<Post> getContentPosts(String gooruOid, Integer limit, Integer offset) {
		String hql = "FROM Post post WHERE post.assocGooruOid=:gooruOid and " + generateOrgAuthQuery("post.") + "and post.status.customTableValueId = "
				+ this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.POST_STATUS.getTable(), CustomProperties.PostStatus.ACTIVE.getPostStatus()).getCustomTableValueId();
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

}
