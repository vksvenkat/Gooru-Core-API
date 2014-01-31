/////////////////////////////////////////////////////////////
// ContentRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.content;

import java.util.Iterator;
import java.util.List;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.StatusType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContentRepositoryHibernate extends BaseRepositoryHibernate implements ContentRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Content findByContent(Long contentId) {
		List<Content> cc = getSession().createQuery("SELECT  c FROM Content c  WHERE c.contentId = ? AND  " + generateAuthQueryWithDataNew("c.")).setLong(0, contentId).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	public Content findByContentGooruId(String gooruContentId) {
		List<Content> cc = getSession().createQuery("SELECT c FROM Learnguide c   WHERE c.gooruOid = ? AND  " + generateAuthQueryWithDataNew("c.")).setString(0, gooruContentId).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	public Content findContentByGooruId(String gooruContentId) {
		return findContentByGooruId(gooruContentId, false);
	}

	@Override
	public Content findContentByGooruId(String gooruContentId, boolean fetchUser) {
		if (!fetchUser) {
			List<Content> cc = getSession().createQuery("SELECT c FROM Content c   WHERE c.gooruOid = ? AND " + generateAuthQueryWithDataNew("c.")).setString(0, gooruContentId).list();
			return cc.size() == 0 ? null : cc.get(0);
		} else {
			Criteria crit = getSession().createCriteria(Content.class);
			crit.setFetchMode("user", FetchMode.EAGER).setFetchMode("userPermSet", FetchMode.JOIN).add(Restrictions.eq("gooruOid", gooruContentId));
			Content content = (Content) crit.uniqueResult();

			return content;
		}
	}

	@Override
	public Resource findByResourceType(String typename, String url) {

		Criteria crit = getSession().createCriteria(Resource.class);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("resourceType"));
		proList.add(Projections.property("contentId"));
		proList.add(Projections.property("url"));
		proList.add(Projections.property("license"));
		crit.setProjection(proList);
		crit.add(Restrictions.eq("resourceType.name", typename));
		crit.add(Restrictions.eq("url", url));
		List resourceList = addAuthCriterias(crit).list();
		Resource resource = null;
		Iterator it = resourceList.iterator();
		while (it.hasNext()) {
			resource = new Resource();
			Object[] row = (Object[]) it.next();
			resource.setContentId((Long) row[1]);
			resource.setResourceType((ResourceType) row[0]);
			resource.setUrl((String) row[2]);
			resource.setLicense((License) row[3]);
		}
		return resource;
	}

	@Override
	public User findContentOwner(String gooruContentId) {

		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("user"));

		Criteria criteria = getSession().createCriteria(Content.class).setProjection(proList).add(Restrictions.eq("gooruOid", gooruContentId));
		List<Content> contents = addOrgAuthCriterias(criteria).list();
		return contents.size() == 0 ? null : contents.get(0).getUser();
	}

	@Override
	public void delete(String gooruContentId) {
		Content content = findByContentGooruId(gooruContentId);
		if (content != null && content instanceof Versionable) {
			try {
				getRevisionHistoryService().createVersion((Versionable) content, "Delete");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		getSession().delete(content);
	}

	@Override
	public ContentAssociation getCollectionAssocContent(String contentGooruOid) {
		String hql = "SELECT contentAssociation FROM ContentAssociation contentAssociation JOIN Content content  LEFT JOIN content.contentPermissions cps WHERE content.gooruOid = '" + contentGooruOid + "' AND  " + generateAuthQueryWithDataNew("content.");
		List<ContentAssociation> result = find(hql);
		return (result.size() > 0) ? null : result.get(0);
	}

	@Override
	public StatusType getStatusType(String name) {
		String hql = "FROM StatusType statusType  WHERE statusType.name = '" + name + "'";
		List<StatusType> result = find(hql);
		return (result.size() > 0) ? null : result.get(0);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Code getCodeByName(String name) {

		List<Code> cc = getSession().createQuery("SELECT c FROM Code c   WHERE c.label = ?  AND  " + generateAuthQueryWithDataNew("c.taxonomySet.")).setString(0, name).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	public Boolean checkContentPermission(Long contentId, String partyUid) {
		String hql = "FROM ContentPermission cp where cp.content.contentId=:contentId and cp.party.partyUid=:partyUid";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("contentId", contentId);
		query.setParameter("partyUid", partyUid);
		List<ContentPermission> permissions = query.list();
		return (permissions.size() > 0) ? true : false;
	}

	@Override
	public List getIdsByUserUId(String userUId, String typeName) {
		Session session = getSession();
		String sql = "SELECT c.content_id,c.gooru_oid, r.type_name FROM content c INNER JOIN resource r ON (r.content_id=c.content_id) WHERE c.user_uid = '" + userUId + "'";
		if (typeName != null) {
			sql += " and r.type_name in ('" + typeName + "')";
		}
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}

	@Override
	public List<Content> getContentByUserUId(String userUId) {
		Session session = getSession();
		String hql = "FROM Content content WHERE content.user.partyUid = '" + userUId + "'";
		Query query = session.createQuery(hql);
		return query.list();
	}

	@Override
	public void deleteContentByContentId(String contentId) {
		try {
			String hql = "DELETE Content content  where content.gooruOid = '" + contentId + "'";
			Session session = getSession();
			Query query = session.createQuery(hql);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error("couldn't delete content", e);
		}
	}

	@Override
	public ContentTagAssoc getContentTagById(String gooruOid, String tagGooruOid) {
		Session session = getSession();
		String hql = "select contentTagAssoc From ContentTagAssoc contentTagAssoc where contentTagAssoc.contentGooruOid='" + gooruOid + "'";
		if (tagGooruOid != null) {
			hql += "and contentTagAssoc.tagGooruOid='" + tagGooruOid + "'";
		}
		Query query = session.createQuery(hql);
		List<ContentTagAssoc> contentTagAssocs = query.list();
		return (contentTagAssocs.size() > 0) ? contentTagAssocs.get(0) : null;

	}

	@Override
	public List<ContentTagAssoc> getContentTagByContent(String gooruOid, Integer limit, Integer offset) {
		Session session = getSession();
		String hql = "select contentTagAssoc From ContentTagAssoc contentTagAssoc where contentTagAssoc.contentGooruOid='" + gooruOid + "'";
		Query query = session.createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	@Override
	public List<ContentPermission> getContentPermission(Long contentId, String partyUid) {
		String hql = "FROM ContentPermission cp where cp.content.contentId=:contentId and cp.party.partyUid=:partyUid";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("contentId", contentId);
		query.setParameter("partyUid", partyUid);
		return query.list();
	}

}
