/////////////////////////////////////////////////////////////
// CollectionRepositoryHibernate.java
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
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assignment;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionRepositoryHibernate extends BaseRepositoryHibernate implements CollectionRepository, ConstantProperties, ParameterProperties {

	private static final String PAGE_SIZE = "pageSize";

	private static final String PAGE_NO = "pageNum";

	@Override
	public List<Collection> getCollections(Map<String, String> filters, User user) {
		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 10;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String hql = "SELECT collection FROM Collection collection ";

		if (filters.containsKey("standards") && filters.get("standards") != null) {
			hql += " LEFT JOIN collection.taxonomySet taxonomySet INNER JOIN taxonomySet.associatedCodes assocCodes";
		}

		hql += " WHERE " + generateOrgAuthQuery("collection.");
		if (filters != null && filters.get(Constants.FETCH_TYPE) != null && filters.get(Constants.FETCH_TYPE).equalsIgnoreCase("my") && user != null) {
			hql += " and collection.collectionType = '" + CollectionType.COLLECTION.getCollectionType() + "' and collection.user.partyUid = '" + user.getGooruUId() + "'";
		}

		if (filters.containsKey("standards") && filters.get("standards") != null) {
			String[] standards = filters.get("standards").split(",");
			StringBuilder includesStandards = new StringBuilder();
			for (String standard : standards) {
				if (includesStandards.length() > 0) {
					includesStandards.append(",");
				}
				includesStandards.append("'" + standard + "'");
			}
			hql += " AND assocCodes.code IN (" + includesStandards + ")";
		}

		Session session = getSession();
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize);
		return query.list();
	}

	@Override
	public List<Classpage> getClasspage(Map<String, String> filters, User user) {
		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 10;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String hql = "FROM Classpage classpage WHERE " + generateOrgAuthQuery("classpage.");
		if (filters != null && filters.get(Constants.FETCH_TYPE) != null && filters.get(Constants.FETCH_TYPE).equalsIgnoreCase("my") && user != null) {
			hql += " and classpage.collectionType = '" + CollectionType.CLASSPAGE.getCollectionType() + "' and classpage.user.partyUid = '" + user.getGooruUId() + "'";
		}
		Session session = getSession();
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize);
		return query.list();
	}

	@Override
	public List<CollectionItem> getCollectionItems(String collectionId, Map<String, String> filters) {
		Session session = getSession();
		String hql = "select collectionItems  FROM Collection collection inner join collection.collectionItems collectionItems where collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");
		
		if (filters.containsKey(TYPE) && filters.get(TYPE).equalsIgnoreCase(CLASSPAGE)) {
			hql +=	" and collectionItems.resource.sharing in ('public','anyonewithlink') " ;
		}
		
		if (filters != null && filters.get(SHARING) != null) {
			String sharing = filters.get(SHARING);
			if (filters.get(SHARING).contains(",")) {
				sharing = sharing.replace(",", "','");
			}
			hql += " and collectionItems.resource.sharing in ('" + sharing + "') ";
		}
		
		if (filters.containsKey(ORDER_BY) && filters.get(ORDER_BY).equalsIgnoreCase(TITLE)) {
			hql += " order by collectionItems.resource.title";
		} else {
			hql += " order by collectionItems.resource.createdOn desc";
		}
		Query query = session.createQuery(hql);
		query.setParameter("gooruOid", collectionId);
		addOrgAuthParameters(query);
		if (filters.containsKey(SKIP_PAGINATION) && filters.get(SKIP_PAGINATION).equalsIgnoreCase(NO)) {
			int pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
			int pageNo = Integer.parseInt(filters.get(PAGE_NO));
			query.setFirstResult(pageSize * (pageNo - 1)).setMaxResults(pageSize);

		}
		return query.list();
	}

	@Override
	public Collection getCollectionByGooruOid(String gooruOid, String gooruUid) {
		Session session = getSession();
		String hql = " FROM Collection collection WHERE  collection.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " collection.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = session.createQuery(hql + generateOrgAuthQuery("collection."));
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		List<Collection> collections = query.list();
		return (collections.size() > 0) ? collections.get(0) : null;
	}

	@Override
	public Classpage getClasspageByGooruOid(String gooruOid, String gooruUid) {
		Session session = getSession();
		String hql = " FROM Classpage classpage WHERE  classpage.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " classpage.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = session.createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		List<Classpage> classpage = query.list();
		return (classpage.size() > 0) ? classpage.get(0) : null;
	}

	@Override
	public Classpage getClasspageByCode(String classpageCode) {
		Session session = getSession();
		String hql = " FROM Classpage classpage WHERE  (classpage.classpageCode=:classpageCode or classpage.gooruOid=:classpageCode) and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter("classpageCode", classpageCode);
		addOrgAuthParameters(query);
		return (Classpage) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public Collection getUserShelfByGooruUid(String gooruUid, String type) {
		Session session = getSession();
		String hql = " FROM Collection collection WHERE  collection.user.partyUid=:gooruUid  and collection.collectionType=:type and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("collection."));
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("type", type);
		addOrgAuthParameters(query);
		List<Collection> collections = query.list();
		return (collections.size() > 0) ? collections.get(0) : null;
	}

	@Override
	public Classpage getUserShelfByClasspageGooruUid(String gooruUid, String type) {
		Session session = getSession();
		String hql = " FROM Classpage classpage WHERE  classpage.user.partyUid=:gooruUid  and classpage.collectionType=:type and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("type", type);
		addOrgAuthParameters(query);
		List<Classpage> classpage = query.list();
		return (classpage.size() > 0) ? classpage.get(0) : null;
	}

	@Override
	public CollectionItem getCollectionItemById(String collectionItemId) {
		Session session = getSession();
		Query query = session.createQuery("FROM CollectionItem collectionItem WHERE  collectionItem.collectionItemId=:collectionItemId  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("collectionItemId", collectionItemId);
		addOrgAuthParameters(query);
		List<CollectionItem> collectionItems = query.list();
		return (collectionItems.size() > 0) ? collectionItems.get(0) : null;
	}

	@Override
	public List<Collection> getCollectionsByResourceId(String resourceGooruOid) {
		Session session = getSession();
		Query query = session.createQuery("Select collection FROM CollectionItem collectionItem WHERE  collectionItem.resource.gooruOid=:resourceGooruOid  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("resourceGooruOid", resourceGooruOid);
		addOrgAuthParameters(query);
		return query.list();
	}
	
	@Override
	public List<CollectionItem> getCollectionItemByAssociation(String resourceGooruOid, String gooruUid) {
		Session session = getSession();
		String sql = "FROM CollectionItem collectionItem WHERE  collectionItem.resource.gooruOid=:resourceGooruOid  and  " + generateOrgAuthQuery("collectionItem.collection.");
		if (gooruUid != null) {
			sql += " and collectionItem.associatedUser.partyUid=:gooruUid";
		}
		Query query = session.createQuery(sql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		if (gooruUid != null) {
			query.setParameter("gooruUid", gooruUid);
		}
		addOrgAuthParameters(query);
		return query.list();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository
	 * #getCollectionGooruOidsByResourceId(long)
	 */
	@Override
	public List<String> getCollectionGooruOidsByResourceId(long contentId) {
		Session session = getSession();
		String sql = "select c.gooru_oid from resource_used_collection_oid c WHERE c.resource_id=" + contentId;
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}

	@Override
	public Assignment getAssignmentByGooruOid(String gooruOid, String gooruUid) {
		Session session = getSession();
		String hql = " FROM Assignment assignment WHERE  assignment.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " assignment.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = session.createQuery(hql + generateOrgAuthQuery("assignment."));
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		List<Assignment> assignments = query.list();
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}

	@Override
	public Assignment getAssignmentUserShelfByGooruUid(String gooruUid, String type) {
		Session session = getSession();
		String hql = " FROM Assignment assignment WHERE  assignment.user.partyUid=:gooruUid  and assignment.collectionType=:type and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("assignment."));
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("type", type);
		addOrgAuthParameters(query);
		List<Assignment> assignments = query.list();
		return (assignments.size() > 0) ? assignments.get(0) : null;
	}

	@Override
	public List<Assignment> getAssignments(Map<String, String> filters, User user) {
		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 10;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String hql = "FROM Assignment assignment WHERE " + generateOrgAuthQuery("assignment.");
		if (filters != null && filters.get(Constants.FETCH_TYPE) != null && filters.get(Constants.FETCH_TYPE).equalsIgnoreCase("my") && user != null) {
			hql += " and assignment.collectionType = '" + CollectionType.ASSIGNMENT.getCollectionType() + "' and assignment.user.partyUid = '" + user.getGooruUId() + "'";
		}
		Session session = getSession();
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize);
		return query.list();
	}

	@Override
	public List<Collection> getMyCollection(Map<String, String> filters, User user) {
		if (filters == null || user == null) {
			return null;
		}
		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 20;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String orderBy = "";
		if (filters != null && filters.containsKey("orderBy")) {
			orderBy = filters.get("orderBy");
		}

		if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase("asc") && !orderBy.equalsIgnoreCase("desc"))) {
			orderBy = "desc";
		}

		String type = "";
		if (filters != null && filters.containsKey(Constants.FETCH_TYPE)) {
			type = " collection.collectionType = '" + filters.get(Constants.FETCH_TYPE) + "' and ";
		}

		String sharingType = "";
		if (filters != null && filters.get(SHARING) != null) {
			String sharing = filters.get(SHARING);
			if (filters.get(SHARING).contains(",")) {
				sharing = sharing.replace(",", "','");
			}
			sharingType = " collectionItems.resource.sharing in ('" + sharing + "') and ";
		}

		String resourceType = "";

		if (filters != null && filters.containsKey("filterName") && (filters.get("filterName").equalsIgnoreCase("folder") || filters.get("filterName").equalsIgnoreCase("collection"))) {
			String typeName = filters.get("filterName");
			if (typeName.equalsIgnoreCase(COLLECTION)) {
				typeName = "scollection";
			}
			resourceType = " collectionItems.resource.resourceType.name =  '" + typeName + "' and ";
		}

		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + type + "  " + sharingType + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId()
				+ "'  order by collectionItems.resource.createdOn desc";

		Session session = getSession();
		Query query = session.createQuery(hql);

		if (filters.containsKey(SKIP_PAGINATION) && filters.get(SKIP_PAGINATION).equalsIgnoreCase(NO)) {
			query.setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize);

		}
		return query.list();
	}

	@Override
	public List<Collection> getMyCollection(String offset, String limit, String type, String filter, User user) {
		Integer startAt = (offset != null) ? Integer.parseInt(offset) : OFFSET;
		Integer pageSize = (limit != null) ? Integer.parseInt(limit) : LIMIT;
		if (type != null && !type.equalsIgnoreCase("all")) {
			type = " collection.collectionType = '" + type + "' and ";
		} else {
			type = " ";
		}

		String resourceType = "";

		if (filter != null) {
			resourceType = " collectionItems.resource.resourceType.name = '" + filter + "' and ";
		}

		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + type + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId() + "'  order by collectionItems.resource.createdOn desc";

		Session session = getSession();
		List<Collection> collections = session.createQuery(hql).setFirstResult(startAt).setMaxResults(pageSize).list();
		return (collections != null && collections.size() > 0) ? collections : null;
	}

	@Override
	public Quiz getQuiz(String gooruOid, String gooruUid, String type) {
		Session session = getSession();
		String hql = " FROM Quiz quiz WHERE   " + generateOrgAuthQuery("quiz.");
		if (gooruOid != null) {
			hql += " and  quiz.gooruOid=:gooruOid ";
		}
		if (gooruUid != null) {
			hql += " and quiz.user.partyUid=:gooruUid ";
		}
		if (type != null) {
			hql += " and quiz.collectionType=:type ";
		}
		Query query = session.createQuery(hql);
		if (gooruOid != null) {
			query.setParameter("gooruOid", gooruOid);
		}
		if (gooruUid != null) {
			query.setParameter("gooruUid", gooruUid);
		}
		if (type != null) {
			query.setParameter("type", type);
		}
		addOrgAuthParameters(query);
		List<Quiz> quizs = query.list();
		return (quizs.size() > 0) ? quizs.get(0) : null;
	}

	@Override
	public List<Quiz> getQuizList(String gooruOid, String gooruUid, String type) {
		Session session = getSession();
		String hql = " FROM Quiz quiz WHERE   " + generateOrgAuthQuery("quiz.");
		if (gooruOid != null) {
			if (gooruOid.contains(",")) {
				gooruOid = gooruOid.replace(",", "','");
			}
			hql += " and  quiz.gooruOid in ('" + gooruOid + "')  ";
		}
		if (gooruUid != null) {
			hql += " and quiz.user.partyUid=:gooruUid ";
		}
		if (type != null) {
			hql += " and quiz.collectionType=:type ";
		}
		Query query = session.createQuery(hql);
		if (gooruUid != null) {
			query.setParameter("gooruUid", gooruUid);
		}
		if (type != null) {
			query.setParameter("type", type);
		}
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<Quiz> getQuizzes(Integer limit, Integer offset) {
		String hql = "FROM Quiz quiz WHERE " + generateOrgAuthQuery("quiz.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(offset != null ? offset : OFFSET);
		query.setMaxResults(limit != null ? limit : LIMIT);
		return query.list();
	}

	@Override
	public List<Quiz> getMyQuizzes(Integer limit, Integer offset, String gooruUid, boolean skipPagination, String orderBy) {
		String hql = "select collectionItems.resource  FROM Quiz quiz inner join quiz.collectionItems collectionItems WHERE   quiz.user.partyUid = '" + gooruUid + "' and quiz.collectionType = '" + CollectionType.USER_QUIZ + "' order by collectionItems.itemSequence " + orderBy;
		Session session = getSession();
		Query query = session.createQuery(hql);
		if (!skipPagination) {
			query.setFirstResult(offset != null ? offset : OFFSET);
			query.setMaxResults(limit != null ? limit : LIMIT);
		}
		return query.list();
	}

	@Override
	public List<CollectionItem> getCollectionItemByResourceId(Long resourceId) {
		Session session = getSession();
		Query query = session.createQuery("FROM CollectionItem collectionItem WHERE  collectionItem.resource.contentId=:resourceId  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("resourceId", resourceId);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy) {
		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE   collection.user.partyUid = '" + user.getGooruUId() + "' and collection.collectionType = '" + CollectionType.USER_CLASSPAGE.getCollectionType()
				+ "'  order by collectionItems.resource.createdOn desc";
		Session session = getSession();
		Query query = session.createQuery(hql);
		if (!skipPagination) {
			query.setFirstResult(offset != null ? offset : OFFSET);
			query.setMaxResults(limit != null ? limit : LIMIT);
		}
		return query.list();
	}

	@Override
	public List<Collection> getMyCollection(Integer limit, Integer offset, String orderBy, String fetchType, String filterName, boolean skipPagination, User user) {

		if (orderBy != null && orderBy.length() > 0) {
			orderBy = orderBy;
		}

		if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase("asc") && !orderBy.equalsIgnoreCase("desc"))) {
			orderBy = "desc";
		}

		String type = "";
		if (fetchType != null && fetchType.length() > 0) {
			type = " collection.collectionType = '" + fetchType + "' and ";
		}

		String resourceType = "";
		if (filterName != null && filterName.length() > 0 && (filterName.equalsIgnoreCase("folder") || filterName.equalsIgnoreCase("scollection"))) {
			resourceType = " collectionItems.resource.resourceType.name = '" + resourceType + "' and ";
		}

		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + type + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId() + "'  order by collectionItems.resource.createdOn desc";

		Session session = getSession();
		Query query = session.createQuery(hql);

		if (!skipPagination) {
			query.setFirstResult(offset != null ? offset : OFFSET);
			query.setMaxResults(limit != null ? limit : LIMIT);
		}
		return query.list();
	}

	@Override
	public List<CollectionItem> getMyCollectionItems(Map<String, String> filters, User user) {
		if (filters == null || user == null) {
			return null;
		}
		Integer pageNum = 1;
		Integer pageSize = 20;
		Integer offset = 0;
		Integer limit = 0;

		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		if (filters != null && filters.containsKey(OFFSET_FIELD)) {
			offset = Integer.parseInt(filters.get(OFFSET_FIELD));
		}

		if (filters != null && filters.containsKey(LIMIT_FIELD)) {
			limit = Integer.parseInt(filters.get(LIMIT_FIELD));
		}

		String orderBy = "";
		if (filters != null && filters.containsKey("orderBy")) {
			orderBy = filters.get("orderBy");
		}

		String type = "";

		if (filters != null && filters.containsKey(Constants.FETCH_TYPE)) {
			type = " collection.collectionType = '" + filters.get(Constants.FETCH_TYPE) + "' and ";
		}

		if (filters != null && filters.containsKey(SHARING)) {
			String sharing = filters.get(SHARING);
			if (filters.get(SHARING).contains(",")) {
				sharing = sharing.replace(",", "','");
			}
			type += " collectionItems.resource.sharing in ( '" + sharing + "' ) and ";
		}

		String resourceType = "";

		if (filters != null && filters.containsKey("filterName") && (filters.get("filterName").equalsIgnoreCase("folder") || filters.get("filterName").equalsIgnoreCase("scollection"))) {
			resourceType = " collectionItems.resource.resourceType.name = '" + filters.get("filterName") + "' and ";
		}

		String hql = "select collectionItems FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + type + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId() + "' ";

		if (orderBy != null && orderBy.equalsIgnoreCase(SEQUENCE)) {
			hql += " order by collectionItems.itemSequence";
		} else {
			if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase("asc") && !orderBy.equalsIgnoreCase("desc"))) {
				orderBy = "desc";
			}
			hql += " order by collectionItems.resource.createdOn " + orderBy;
		}

		Session session = getSession();
		Query query = session.createQuery(hql);

		if (filters.containsKey(SKIP_PAGINATION) && filters.get(SKIP_PAGINATION).equalsIgnoreCase(NO)) {
			if (filters != null && filters.containsKey(OFFSET_FIELD)) {
				query.setFirstResult(offset).setMaxResults(limit);
			} else {
				query.setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize);
			}

		}
		return query.list();
	}

	@Override
	public List<CollectionItem> getCollectionItems(String collectionId, Integer offset, Integer limit, boolean skipPagination, String orderBy, String type) {
		Session session = getSession();
		String hql = "select collectionItems  FROM Collection collection inner join collection.collectionItems collectionItems where collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");
		
		if(type != null && type.equalsIgnoreCase("classpage")) {
			hql +=	" and collectionItems.resource.sharing in('public','anyonewithlink') " ;
		}
		
		if (!orderBy.equals(PLANNED_END_DATE)) {
			hql += "order by collectionItems.associationDate desc ";
		} else {
			hql += "order by collectionItems.plannedEndDate desc ";
		}
		
		Query query = session.createQuery(hql);
		query.setParameter("gooruOid", collectionId);
		addOrgAuthParameters(query);

		if (!skipPagination) {
			query.setFirstResult((offset != null ? offset : OFFSET));
			query.setMaxResults((limit != null ? limit : LIMIT));
		}
		return query.list();
	}

	@Override
	public Resource findResourceCopiedFrom(String gooruOid, String gooruUid) {
		List<Resource> resources = find("SELECT r FROM Resource r  where r.copiedResourceId ='" + gooruOid + "' AND r.user.partyUid ='" + gooruUid + "' AND " + generateAuthQueryWithDataNew("r."));
		return resources.size() == 0 ? null : resources.get(0);
	}

	@Override
	public List<Classpage> getClasspages(Integer offset, Integer limit, Boolean skipPagination, String title, String author, String userName) {
		Session session = getSession();
		String hql = "select classpage  FROM Classpage classpage where " + generateOrgAuthQuery("classpage.");

		if (title != null) {
			hql += " and classpage.title like :title ";
		}
		if (userName != null) {
			hql += " and classpage.user.username =:userName ";
		}
		if (author != null) {
			hql += " and (classpage.user.username =:author or classpage.user.firstName =:author or classpage.user.lastName =:author) ";
		}

		Query query = session.createQuery(hql);
		if (title != null) {
			query.setParameter("title", "%" + title + "%");
		}

		if (userName != null) {
			query.setParameter("userName", userName);
		}

		if (author != null) {
			query.setParameter("author", author);
		}
		addOrgAuthParameters(query);

		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}

		return query.list();
	}
	
	@Override
	public Long getClasspageCollectionCount(String classpageGooruOid, String type) {
		
		Session session = getSession();
		String hql = "select count(*)  FROM CollectionItem collectionItem where " + generateOrgAuthQuery("collectionItem.collection.");
		if (classpageGooruOid != null) {
			hql += " and collectionItem.collection.gooruOid=:classpageGooruOid ";
		}
		if(type != null && type.equalsIgnoreCase("classpage")) {
			hql +=	" and collectionItem.resource.sharing in('public','anyonewithlink') " ;
		}
		Query query = session.createQuery(hql);
		if (classpageGooruOid != null) {
			query.setParameter("classpageGooruOid", classpageGooruOid );
		}
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getClasspageCount(String title, String author, String userName) {
		Session session = getSession();
		String hql = "select count(*)  FROM Classpage classpage where " + generateOrgAuthQuery("classpage.");
		if (title != null) {
			hql += " and classpage.title like :title ";
		}
		if (userName != null) {
			hql += " and classpage.user.username =:userName ";
		}
		if (author != null) {
			hql += " and (classpage.user.username =:author or classpage.user.firstName =:author or classpage.user.lastName =:author) ";
		}

		Query query = session.createQuery(hql);
		if (title != null) {
			query.setParameter("title", "%" + title + "%");
		}

		if (userName != null) {
			query.setParameter("userName", userName);
		}

		if (author != null) {
			query.setParameter("author", author);
		}
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getMyClasspageCount(String gooruUid) {
		String hql = "select count(collectionItems.resource)  FROM Collection collection inner join collection.collectionItems collectionItems WHERE   collection.user.partyUid = '" + gooruUid + "' and collection.collectionType = '" + CollectionType.USER_CLASSPAGE.getCollectionType()
				+ "'  order by collectionItems.resource.createdOn desc";
		Session session = getSession();
		Query query = session.createQuery(hql);
		return (Long) query.list().get(0);
	}

	@Override

	public List<Object[]> getMyFolder(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType) {
		String sql = "select re.title, cr.gooru_oid, re.type_name, re.folder, re.thumbnail, cr.sharing, ci.collection_item_id  from  resource r inner join collection c on c.content_id = r.content_id inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join resource re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid  where c.collection_type = 'shelf' and  cr.sharing in ('" + sharing.replace(",", "','")+ "') and cc.user_uid=:gooruUid ";						
		if (collectionType!= null)  {
			sql += " and cr.type_name=:collectionType ";
		}
		sql += "order by ci.item_sequence ";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("gooruUid", gooruUid);
		if (collectionType != null) {
			query.setParameter("collectionType", collectionType);
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	@Override
	public List<Object[]> getCollectionItem(String gooruOid, Integer limit, Integer offset, boolean skipPagination, String sharing, String orderBy, String collectionType) {
		String sql = "select r.title, c.gooru_oid, r.type_name, r.folder, r.thumbnail, ct.value, ct.display_name, c.sharing, ci.collection_item_id from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id where  c.sharing in ('"
				+ sharing.replace(",", "','") + "') and rc.gooru_oid=:gooruOid  ";

		if (collectionType != null) {
			sql += " and c.type_name =:collectionType ";
		}

		if (orderBy != null && orderBy.equalsIgnoreCase("folder")) {
			sql += " order by ci.item_sequence asc";
		} else {
			sql += " order by rc.created_on desc";
		}

		Query query = getSession().createSQLQuery(sql);
		query.setParameter("gooruOid", gooruOid);
		if (collectionType != null) {
			query.setParameter("collectionType", collectionType);
		}
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public Long getMyShelfCount(String gooruUid, String sharing, String collectionType) {
		String sql = "select count(1) as count from  resource r inner join collection c on c.content_id = r.content_id inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join resource re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid  where c.collection_type = 'shelf' and cr.sharing in ('" + sharing.replace(",", "','")+ "') and cc.user_uid=:gooruUid";

		if (collectionType != null) {
			sql += " and cc.type_name =:collectionType ";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter("gooruUid", gooruUid);
		if (collectionType != null) {
			query.setParameter("collectionType", collectionType);
		}
		return (Long)query.list().get(0);
	}

	@Override
	public Long getCollectionItemCount(String gooruOid, String sharing, String collectionType) {	
		String sql = "select count(1) as count from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id where rc.gooru_oid=:gooruOid and c.sharing in ('" + sharing.replace(",", "','")+ "')";
		if (collectionType != null) {
			sql += " and c.type_name =:collectionType ";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter("gooruOid", gooruOid);
		if (collectionType != null) {
			query.setParameter("collectionType", collectionType);
		}
		return (Long)query.list().get(0);
	}
	
	@Override
	public List<CollectionItem> findCollectionByResource(String gooruOid, String gooruUid, String type) {
		String hql = "FROM CollectionItem collectionItems where collectionItems.resource.gooruOid=:gooruOid ";
		if (gooruUid != null) { 
			hql += " and collectionItems.collection.user.partyUid=:gooruUid ";
		}
		if (type != null) { 
			hql += " and collectionItems.itemType=:type";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		if (gooruUid != null) {
		  query.setParameter("gooruUid", gooruUid);
		}
		if (type != null) {
		  query.setParameter("type", type);
		}
		return query.list();
	}

	@Override
	public CollectionItem findCollectionItemByGooruOid(String gooruOid,String gooruUid) {
		String hql = "FROM CollectionItem collectionItems where collectionItems.resource.gooruOid=:gooruOid and collectionItems.collection.user.partyUid=:gooruUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("gooruUid", gooruUid);
		return (CollectionItem)(query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public String getParentCollection(String collectionGooruOid, String gooruUid) {
		String hql = "select cc.gooru_oid  as gooruOid  from collection_item ci inner join resource r on r.content_id = ci.resource_content_id inner join content cr on cr.content_id = r.content_id inner join content cc on cc.content_id = ci.collection_content_id inner join collection co on  co.content_id = ci.collection_content_id  where cr.gooru_oid='"+collectionGooruOid+"' and cc.user_uid ='"+gooruUid+"' and co.collection_type = 'folder'";
		Query query = getSession().createSQLQuery(hql);
		return query.list().size() > 0 ? (String)query.list().get(0) : null;
	}
	
	public Long getPublicCollectionCount(String gooruOid) {
		String sql = "select count(1) as count  from collection_item  ci inner join resource r  on r.content_id = ci.resource_content_id inner join content c on c.content_id = ci.resource_content_id inner join content cc on cc.content_id = ci.collection_content_id  where cc.gooru_oid =:gooruOid and c.sharing in  ('public') and (r.type_name = 'folder' or r.type_name = 'scollection')";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter("gooruOid", gooruOid);
		return (Long) query.list().get(0);
	}
}
