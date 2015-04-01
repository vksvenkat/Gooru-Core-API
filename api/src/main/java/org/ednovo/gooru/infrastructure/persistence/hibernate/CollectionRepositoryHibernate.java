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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assignment;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserCollectionItemAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionRepositoryHibernate extends BaseRepositoryHibernate implements CollectionRepository, ConstantProperties, ParameterProperties {

	private static final String PAGE_SIZE = "pageSize";

	private static final String PAGE_NO = "pageNum";
	
	private static final String  COLLECTION_ITEM_BY_RESOURCE = "FROM CollectionItem collectionItem WHERE  collectionItem.resource.gooruOid=:resourceId";
	
	private static final String COLLECTION_ITEM_BY_SEQUENCE = "FROM CollectionItem collectionItem WHERE collectionItem.collection.gooruOid=:collectionId and collectionItem.itemSequence>:itemSequence order by collectionItem.itemSequence asc";
	
	private static final String GET_COLLECTION_BY_RESOURCE_OID = "Select distinct(collectionItem.collection) FROM  CollectionItem  collectionItem where collectionItem.resource.gooruOid=:resourceId";
	
	private static final String GET_COLLECTION_ITEM_BY_RESOURCE_OID = "FROM CollectionItem collectionItem WHERE  collectionItem.collection.gooruOid=:collectionId and collectionItem.resource.gooruOid=:resourceId  and " + generateOrgAuthQuery("collectionItem.collection.");

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getCollections(final Map<String, String> filters, final User user) {
		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 10;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String hql = "SELECT collection FROM Collection collection ";

		if (filters.containsKey(STANDARDS) && filters.get(STANDARDS) != null) {
			hql += " LEFT JOIN collection.taxonomySet taxonomySet INNER JOIN taxonomySet.associatedCodes assocCodes";
		}

		hql += " WHERE " + generateOrgAuthQuery("collection.");
		if (filters != null && filters.get(Constants.FETCH_TYPE) != null && filters.get(Constants.FETCH_TYPE).equalsIgnoreCase(MY) && user != null) {
			hql += " and collection.resourceType.name = '" + ResourceType.Type.SCOLLECTION.getType() + "' and collection.user.partyUid = '" + user.getGooruUId() + "'";
		}

		if (filters.containsKey(STANDARDS) && filters.get(STANDARDS) != null) {
			String[] standards = filters.get(STANDARDS).split(",");
			StringBuilder includesStandards = new StringBuilder();
			for (String standard : standards) {
				if (includesStandards.length() > 0) {
					includesStandards.append(",");
				}
				includesStandards.append("'" + standard + "'");
			}
			hql += " AND assocCodes.code IN (" + includesStandards + ")";
		}
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Classpage> getClasspage(final Map<String, String> filters, final User user) {
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
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> getCollectionItems(final String collectionId, final Map<String, String> filters) {
		String hql = "select collectionItems  FROM Collection collection inner join collection.collectionItems collectionItems where collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");

		if (filters.containsKey(TYPE) && filters.get(TYPE).equalsIgnoreCase(CLASSPAGE)) {
			hql += " and collectionItems.resource.sharing in ('public','anyonewithlink') ";
		}

		if (filters != null && filters.get(SHARING) != null) {
			String sharing = filters.get(SHARING);
			if (filters.get(SHARING).contains(",")) {
				sharing = sharing.replace(",", "','");
			}
			hql += " and collectionItems.resource.sharing in ('" + sharing + "') ";
		}

		if (filters.containsKey(TYPE) && filters.get(TYPE).equalsIgnoreCase(COLLECTION)) {
			hql += " and collectionItems.resource.resourceType.name = 'scollection' ";
		}

		if (filters.containsKey(ORDER_BY) && filters.get(ORDER_BY).equalsIgnoreCase(TITLE)) {
			hql += " order by collectionItems.resource.title";
		} else {
			hql += " order by collectionItems.associationDate desc";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, collectionId);
		addOrgAuthParameters(query);
		Integer pageNo = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNo = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = null;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}
		query.setFirstResult((pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : MAX_LIMIT) * (pageNo - 1));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : MAX_LIMIT);
		return query.list();
	}

	@Override
	public Collection getCollectionByGooruOid(final String gooruOid, final String gooruUid) {
		String hql = " FROM Collection collection WHERE  collection.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " collection.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("collection."));
		query.setParameter(GOORU_OID, gooruOid);
		addOrgAuthParameters(query);
		return (query.list().size() > 0) ? (Collection) query.list().get(0) : null;
	}

	@Override
	public Collection getCollectionByIdWithType(final String gooruOid, final String type) {
		String hql = " FROM Collection collection WHERE  collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");
		if (type != null) {
			hql += " and collection.resourceType.name=:type ";
		}
		Query query = getSession().createQuery(hql);
		if (type != null) {
			query.setParameter(TYPE, type);
		}
		query.setParameter(GOORU_OID, gooruOid);
		addOrgAuthParameters(query);
		return (query.list().size() > 0) ? (Collection) query.list().get(0) : null;
	}

	@Override
	public Classpage getClasspageByGooruOid(final String gooruOid, final String gooruUid) {
		String hql = " FROM Classpage classpage WHERE  classpage.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " classpage.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter(GOORU_OID, gooruOid);
		addOrgAuthParameters(query);
		return (query.list().size() > 0) ? (Classpage) query.list().get(0) : null;
	}

	@Override
	public Classpage getClasspageByCode(final String classpageCode) {
		String hql = " FROM Classpage classpage WHERE  (classpage.classpageCode=:classpageCode or classpage.gooruOid=:classpageCode) and ";
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter("classpageCode", classpageCode);
		addOrgAuthParameters(query);
		return (Classpage) ((query.list().size() != 0) ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getUserShelfByGooruUid(final String gooruUid, final String type) {
		String hql = " FROM Collection collection WHERE  collection.user.partyUid=:gooruUid  and collection.collectionType=:type and ";
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("collection."));
		query.setParameter(_GOORU_UID, gooruUid);
		query.setParameter(TYPE, type);
		addOrgAuthParameters(query);
		List<Collection> collections = query.list();
		return (collections.size() != 0) ? collections.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Classpage getUserShelfByClasspageGooruUid(final String gooruUid, final String type) {
		String hql = " FROM Classpage classpage WHERE  classpage.user.partyUid=:gooruUid  and classpage.collectionType=:type and ";
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("classpage."));
		query.setParameter(_GOORU_UID, gooruUid);
		query.setParameter(TYPE, type);
		addOrgAuthParameters(query);
		List<Classpage> classpage = query.list();
		return (classpage.size() != 0) ? classpage.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CollectionItem getCollectionItemById(final String collectionItemId) {
		Query query = getSession().createQuery("FROM CollectionItem collectionItem WHERE  collectionItem.collectionItemId=:collectionItemId  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("collectionItemId", collectionItemId);
		addOrgAuthParameters(query);
		List<CollectionItem> collectionItems = query.list();
		return (collectionItems.size() != 0) ? collectionItems.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getCollectionsByResourceId(final String resourceGooruOid) {
		Query query = getSession().createQuery("Select collection FROM CollectionItem collectionItem WHERE  collectionItem.resource.gooruOid=:resourceGooruOid  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("resourceGooruOid", resourceGooruOid);
		addOrgAuthParameters(query);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> getCollectionItemByAssociation(final String resourceGooruOid, final String gooruUid, final String type) {
		String sql = "FROM CollectionItem collectionItem WHERE  collectionItem.resource.gooruOid=:resourceGooruOid  and  " + generateOrgAuthQuery("collectionItem.collection.");
		String collectionType = "";
		if (gooruUid != null) {
			sql += " and collectionItem.associatedUser.partyUid=:gooruUid";
		}

		if (type != null) {
			collectionType = type.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : type;
			sql += " and collectionItem.collection.resourceType.name=:collectionType";
		}
		Query query = getSession().createQuery(sql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		if (gooruUid != null) {
			query.setParameter(_GOORU_UID, gooruUid);
		}
		if (type != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
		}
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<CollectionItem> getCollectionItemByParentId(final String collectionGooruOid, final String gooruUid, final String type) {
		String sql = "FROM CollectionItem collectionItem WHERE  collectionItem.collection.gooruOid=:collectionGooruOid  and  " + generateOrgAuthQuery("collectionItem.collection.");
		String collectionType = "";
		if (gooruUid != null) {
			sql += " and collectionItem.associatedUser.partyUid=:gooruUid";
		}
		if (type != null) {
			collectionType = type.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : type;
			sql += " and collectionItem.collection.resourceType.name=:collectionType";
		}
		Query query = getSession().createQuery(sql);
		query.setParameter("collectionGooruOid", collectionGooruOid);
		if (gooruUid != null) {
			query.setParameter(_GOORU_UID, gooruUid);
		}
		if (type != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
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
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCollectionGooruOidsByResourceId(long contentId) {
		String sql = "select c.gooru_oid from resource_used_collection_oid c WHERE c.resource_id=" + contentId;
		SQLQuery query = getSession().createSQLQuery(sql);
		return query.list();
	}

	@Override
	public Assignment getAssignmentByGooruOid(final String gooruOid, final String gooruUid) {
		String hql = " FROM Assignment assignment WHERE  assignment.gooruOid=:gooruOid  and ";
		if (gooruUid != null) {
			hql += " assignment.user.partyUid='" + gooruUid + "' and ";
		}
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("assignment."));
		query.setParameter(GOORU_OID, gooruOid);
		addOrgAuthParameters(query);
		return ((query.list().size() > 0) ? (Assignment) query.list().get(0) : null);
	}

	@Override
	public Assignment getAssignmentUserShelfByGooruUid(final String gooruUid, final String type) {
		String hql = " FROM Assignment assignment WHERE  assignment.user.partyUid=:gooruUid  and assignment.collectionType=:type and ";
		Query query = getSession().createQuery(hql + generateOrgAuthQuery("assignment."));
		query.setParameter(_GOORU_UID, gooruUid);
		query.setParameter(TYPE, type);
		addOrgAuthParameters(query);
		return ((query.list().size() > 0) ? (Assignment) query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Assignment> getAssignments(final Map<String, String> filters, final User user) {
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
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getMyCollection(final Map<String, String> filters, final User user) {
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
		if (filters != null && filters.containsKey(ORDER_BY)) {
			orderBy = filters.get(ORDER_BY);
		}

		if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase(ASC) && !orderBy.equalsIgnoreCase(DESC))) {
			orderBy = DESC;
		}

		String type = "";
		if (filters != null && filters.containsKey(Constants.FETCH_TYPE)) {
			String fetchType = filters.get(Constants.FETCH_TYPE).equalsIgnoreCase(COLLECTION) ? SCOLLECTION : filters.get(Constants.FETCH_TYPE);
			type = " collection.resourceType.name = '" + fetchType + "' and ";
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

		Query query = getSession().createQuery(hql);
		query.setFirstResult(((pageNum - 1) * pageSize));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getMyCollection(final String offset, final String limit, final String type, final String filter, final User user) {
		Integer startAt = (offset != null) ? Integer.parseInt(offset) : OFFSET;
		Integer pageSize = (limit != null) ? Integer.parseInt(limit) : LIMIT;
		String resourceType = "";
		String collectionType = "";
		if (type != null && !type.equalsIgnoreCase(ALL)) {
			collectionType = type.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : type;
			collectionType = " collection.resourceType.name = '" + type + "' and ";
		}

		if (filter != null) {
			resourceType = " collectionItems.resource.resourceType.name = '" + filter + "' and ";
		}
		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + collectionType + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId() + "'  order by collectionItems.resource.createdOn desc";
		return getSession().createQuery(hql).setFirstResult(startAt).setMaxResults(pageSize).list();
	}

	@Override
	public Quiz getQuiz(final String gooruOid, final String gooruUid, final String type) {
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
		Query query = getSession().createQuery(hql);
		if (gooruOid != null) {
			query.setParameter(GOORU_OID, gooruOid);
		}
		if (gooruUid != null) {
			query.setParameter(_GOORU_UID, gooruUid);
		}
		if (type != null) {
			query.setParameter(TYPE, type);
		}
		addOrgAuthParameters(query);
		return (query.list().size() > 0) ? (Quiz) query.list().get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getQuizList(String gooruOid, final String gooruUid, final String type) {
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
		Query query = getSession().createQuery(hql);
		if (gooruUid != null) {
			query.setParameter(_GOORU_UID, gooruUid);
		}
		if (type != null) {
			query.setParameter(TYPE, type);
		}
		addOrgAuthParameters(query);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getQuizzes(final Integer limit, final Integer offset) {
		final String hql = "FROM Quiz quiz WHERE " + generateOrgAuthQuery("quiz.");
		final Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult(offset != null ? offset : OFFSET);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Quiz> getMyQuizzes(final Integer limit, final Integer offset, final String gooruUid, final String orderBy) {
		final String hql = "select collectionItems.resource  FROM Quiz quiz inner join quiz.collectionItems collectionItems WHERE   quiz.user.partyUid = '" + gooruUid + "' and quiz.collectionType = '" + CollectionType.USER_QUIZ + "' order by collectionItems.itemSequence " + orderBy;
		final Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> getCollectionItemByResourceId(final Long resourceId) {
		final Query query = getSession().createQuery("FROM CollectionItem collectionItem WHERE  collectionItem.resource.contentId=:resourceId  and " + generateOrgAuthQuery("collectionItem.collection."));
		query.setParameter("resourceId", resourceId);
		addOrgAuthParameters(query);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Classpage> getMyClasspage(final Integer offset, final Integer limit, final User user, final boolean skipPagination, final String orderBy) {
		final String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE   collection.user.partyUid = '" + user.getGooruUId() + "' and collection.collectionType = '" + CollectionType.USER_CLASSPAGE.getCollectionType()
				+ "'  order by collectionItems.resource.createdOn " + orderBy;
		Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getMyCollection(final Integer limit, final Integer offset, String orderBy, String fetchType, final String filterName, final User user) {
		if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase(ASC) && !orderBy.equalsIgnoreCase(DESC))) {
			orderBy = DESC;
		}

		String type = "";
		if (fetchType != null && fetchType.length() > 0) {
			fetchType = fetchType.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : fetchType;
			type = " collection.resourceType.name = '" + fetchType + "' and ";
		}
		String resourceType = "";
		if (filterName != null && filterName.length() > 0 && (filterName.equalsIgnoreCase(FOLDER) || filterName.equalsIgnoreCase(SCOLLECTION))) {
			resourceType = " collectionItems.resource.resourceType.name = '" + resourceType + "' and ";
		}
		String hql = "select collectionItems.resource  FROM Collection collection inner join collection.collectionItems collectionItems WHERE  " + type + " " + resourceType + " collection.user.partyUid = '" + user.getGooruUId() + "'  order by collectionItems.resource.createdOn desc";
		Query query = getSession().createQuery(hql);

		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> getMyCollectionItems(final Map<String, String> filters, final User user) {
		if (filters == null || user == null) {
			return null;
		}
		Integer offset = 0;
		Integer limit = 0;

		if (filters != null && filters.containsKey(OFFSET_FIELD)) {
			offset = Integer.parseInt(filters.get(OFFSET_FIELD));
		}

		if (filters != null && filters.containsKey(LIMIT_FIELD)) {
			limit = Integer.parseInt(filters.get(LIMIT_FIELD));
		}

		String orderBy = "";
		if (filters != null && filters.containsKey(ORDER_BY)) {
			orderBy = filters.get(ORDER_BY);
		}

		String type = "";

		if (filters != null && filters.containsKey(Constants.FETCH_TYPE)) {
			String fetchType = filters.get(Constants.FETCH_TYPE).equalsIgnoreCase(COLLECTION) ? SCOLLECTION : filters.get(Constants.FETCH_TYPE);
			type = " collection.resourceType.name = '" + fetchType + "' and ";
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
			if (orderBy.length() == 0 || (!orderBy.equalsIgnoreCase(ASC) && !orderBy.equalsIgnoreCase(DESC))) {
				orderBy = DESC;
			}
			hql += " order by collectionItems.associationDate " + orderBy;
		}

		Query query = getSession().createQuery(hql);

		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> getCollectionItems(final String collectionId, final Integer offset, final Integer limit, final String orderBy, final String type) {
		String hql = "select collectionItems  FROM Collection collection inner join collection.collectionItems collectionItems where collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");
		if (type != null && type.equalsIgnoreCase("classpage")) {

			hql += " and collectionItems.resource.sharing in('public','anyonewithlink') ";
		}

		if (orderBy != null && (!orderBy.equals(PLANNED_END_DATE) && !orderBy.equals(SEQUENCE))) {
			hql += " order by collectionItems.associationDate desc ";
		} else if (orderBy != null && orderBy.equals(PLANNED_END_DATE)) {
			hql += "order by IFNULL(collectionItems.plannedEndDate, (SUBSTRING(now(), 1, 4) + 1000)) asc ";
		} else {
			hql += " order by collectionItems.itemSequence";
		}

		Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, collectionId);
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public Long getCollectionItemsCount(final String collectionId, final String orderBy, final String type) {
		String hql = "select count(*)  FROM Collection collection inner join collection.collectionItems collectionItems where collection.gooruOid=:gooruOid and " + generateOrgAuthQuery("collection.");
		if (type != null && type.equalsIgnoreCase("classpage")) {
			hql += " and collectionItems.resource.sharing in('public','anyonewithlink') ";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, collectionId);
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Resource findResourceCopiedFrom(String gooruOid, String gooruUid) {
		List<Resource> resources = find("SELECT r FROM Resource r  where r.copiedResourceId ='" + gooruOid + "' AND r.user.partyUid ='" + gooruUid + "' AND " + generateAuthQueryWithDataNew("r."));
		return resources.size() == 0 ? null : resources.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Classpage> getClasspages(final Integer offset, final Integer limit, final String title, final String author, final String userName) {
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
		Query query = getSession().createQuery(hql);
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
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public Long getClasspageCount(final String gooruOid, final String itemType) {
		String sql = "select count(1) as count from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id   "
				+ "where  c.sharing in ('public', 'anyonewithlink') and rc.gooru_oid='" + gooruOid + "'";

		if (itemType != null) {
			sql += " and r.type_name ='" + itemType + "'";
		} else {
			sql += " and r.type_name != 'pathway'";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getClasspageCollectionCount(final String classpageGooruOid, final String status, final String userUid, final String orderBy, final String type) {
		String sql = "select count(1) as count from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id left join user_collection_item_assoc uc on uc.collection_item_uid = ci.collection_item_id and uc.user_uid = '"
				+ userUid + "' left join custom_table_value ct on ct.custom_table_value_id = uc.status inner join user uu on uu.gooru_uid = c.user_uid  where  c.sharing in ('public', 'anyonewithlink') ";
		sql += " and rc.gooru_oid='" + classpageGooruOid + "'  ";

		if (type != null) {
			sql += " and r.type_name ='" + type + "'";
		} else {
			sql += " and r.type_name != 'pathway'";
		}

		if (status != null) {
			sql += " and IFNULL(ct.value, 'open') = '" + status + "' ";
		}
		if (orderBy != null && (orderBy.equalsIgnoreCase(DUE_DATE) || orderBy.equalsIgnoreCase(DUE_DATE_EARLIEST))) {
			sql += " and ci.planned_end_date IS NOT NULL ";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getClasspageCount(final String title, final String author, final String userName) {
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
		Query query = getSession().createQuery(hql);
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
	public Long getMyClasspageCount(final String gooruUid) {
		final String hql = "select count(collectionItems.resource)  FROM Collection collection inner join collection.collectionItems collectionItems WHERE   collection.user.partyUid = '" + gooruUid + "' and collection.collectionType = '" + CollectionType.USER_CLASSPAGE.getCollectionType()
				+ "'  order by collectionItems.resource.createdOn desc";
		Query query = getSession().createQuery(hql);
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getMyFolder(final String gooruUid, final Integer limit, final Integer offset, final String sharing, String collectionType, boolean fetchChildItem, String orderBy, String excludeType) {
		String sql = "select re.title, cr.gooru_oid, re.type_name, re.folder, re.thumbnail, cr.sharing, ci.collection_item_id, co.goals, ct.value, ct.display_name, rs.attribution, rs.domain_name , co.ideas, co.questions,co.performance_tasks, co.collection_type, ci.item_sequence, cc.gooru_oid as parentGooruOid, re.s3_upload_flag as s3UploadFlag, re.description, re.url, cs.data from  resource r inner join collection c on c.content_id = r.content_id inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join resource re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id left join content_settings cs on cs.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid  left join collection co on co.content_id = re.content_id left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id left join resource_source rs on rs.resource_source_id = r.resource_source_id  where c.collection_type = 'shelf' and  cr.sharing in ('"
				+ sharing.replace(",", "','") + "') ";
		sql += " and cc.user_uid = '" + gooruUid + "' ";
		if (excludeType != null) { 
			sql += " and co.collection_type not in ('"+ excludeType.replace(",", "','") + "')";
		}
		if (collectionType != null) {
			collectionType = collectionType.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : collectionType;
			sql += " and re.type_name =:collectionType ";
		}
		if (fetchChildItem) {
			sql += " and ci.item_type != 'collaborator' ";
		}
		if (orderBy != null && orderBy.equalsIgnoreCase(SEQUENCE)) {
			sql += " order by ci.item_sequence desc ";
		} else {
			sql += " order by ci.association_date desc ";
		}
		Query query = getSession().createSQLQuery(sql);
		if (collectionType != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getCollectionItem(final String gooruOid, Integer limit, Integer offset, String sharing, String orderBy, String collectionType, boolean fetchChildItem, String sequenceOrder, boolean fecthAll, String excludeType) {
		String sql = "select r.title, c.gooru_oid, r.type_name, r.folder, r.thumbnail, ct.value, ct.display_name, c.sharing, ci.collection_item_id, co.goals, rs.attribution, rs.domain_name, co.ideas, co.questions, co.performance_tasks, r.url ,rsummary.rating_star_avg, rsummary.rating_star_count, co.collection_type, ci.item_sequence, rc.gooru_oid as parentGooruOid, r.s3_upload_flag as s3UploadFlag, r.description, cs.data  from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join content_settings cs on cs.content_id = r.content_id left join collection co on co.content_id = r.content_id left join resource_source rs on rs.resource_source_id = r.resource_source_id left join resource_summary rsummary on   c.gooru_oid = rsummary.resource_gooru_oid where  c.sharing in ('"
				+ sharing.replace(",", "','") + "') and rc.gooru_oid=:gooruOid";
		if (collectionType != null) {
			collectionType = collectionType.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : collectionType;
			sql += " and r.type_name =:collectionType ";
		}
		if (excludeType != null) { 
			sql += " and co.collection_type not in ('"+ excludeType.replace(",", "','") + "')";
		}
		if (fetchChildItem) {
			sql += " and ci.item_type != 'collaborator' ";
		}
		if (orderBy != null && orderBy.equalsIgnoreCase(SEQUENCE)) {
			sql += " order by ci.item_sequence " + sequenceOrder;
		} else {
			sql += " order by ci.association_date desc";
		}
		Query query = getSession().createSQLQuery(sql);
		query.setParameter(GOORU_OID, gooruOid);
		if (collectionType != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
		}
		if (fecthAll) {
			limit = MAX_LIMIT;
			offset = 0;
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public Long getMyShelfCount(final String gooruUid, final String sharing, String collectionType, final String excludeType) {
		String sql = "select count(1) as count from  resource r inner join collection c on c.content_id = r.content_id inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join resource re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid left join collection co on co.content_id = re.content_id  where c.collection_type = 'shelf' and cr.sharing in ('"
				+ sharing.replace(",", "','") + "') and cc.user_uid=:gooruUid";
		if (collectionType != null) {
			collectionType = collectionType.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : collectionType;
			sql += " and re.type_name =:collectionType ";
		}
		if (excludeType != null) { 
			sql += " and co.collection_type not in ('"+ excludeType.replace(",", "','") + "')";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter(_GOORU_UID, gooruUid);
		if (collectionType != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
		}
		return (Long) query.list().get(0);
	}

	@Override
	public Long getCollectionItemCount(final String gooruOid, final String sharing, String collectionType, String excludeType) {
		String sql = "select count(1) as count from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id where rc.gooru_oid=:gooruOid ";
		if (sharing != null) {		
			sql += " and c.sharing in ('" + sharing.replace(",", "','") + "') ";
		}
		if (collectionType != null) {
			collectionType = collectionType.equalsIgnoreCase(COLLECTION) ? SCOLLECTION : collectionType;
			sql += " and r.type_name =:collectionType ";
		}
		if (excludeType != null) { 
			sql += " and co.collection_type not in ('"+ excludeType.replace(",", "','") + "')";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter(GOORU_OID, gooruOid);
		if (collectionType != null) {
			query.setParameter(COLLECTION_TYPE, collectionType);
		}
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CollectionItem> findCollectionByResource(final String gooruOid, final String gooruUid, final String type) {
		String hql = "FROM CollectionItem collectionItems where collectionItems.resource.gooruOid=:gooruOid ";
		if (gooruUid != null) {
			hql += " and collectionItems.collection.user.partyUid=:gooruUid ";
		}
		if (type != null) {
			hql += " and collectionItems.itemType=:type";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, gooruOid);
		if (gooruUid != null) {
			query.setParameter(_GOORU_UID, gooruUid);
		}
		if (type != null) {
			query.setParameter(TYPE, type);
		}
		return query.list();
	}

	@Override
	public CollectionItem findCollectionItemByGooruOid(final String gooruOid, final String gooruUid, final String type) {

		String hql = "FROM CollectionItem collectionItems where collectionItems.resource.gooruOid=:gooruOid and collectionItems.collection.user.partyUid=:gooruUid";
		if (type != null) {
			hql += " and collectionItems.collection.collectionType !=:type";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(_GOORU_UID, gooruUid);
		if (type != null) {
			query.setParameter(TYPE, type);
		}
		query.setMaxResults(1);
		return (CollectionItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public Object[] getParentCollection(final String collectionGooruOid, final String gooruUid) {
		String hql = "select cc.gooru_oid, cor.title  as gooruOid  from collection_item ci inner join resource r on r.content_id = ci.resource_content_id inner join content cr on cr.content_id = r.content_id inner join content cc on cc.content_id = ci.collection_content_id inner join collection co on  co.content_id = ci.collection_content_id inner join resource cor on cor.content_id = co.content_id   where cr.gooru_oid='"
				+ collectionGooruOid + "'and co.collection_type = 'folder'  and ci.item_type != 'collaborator' ";
		if (gooruUid != null) {
			hql += "and  cc.user_uid ='" + gooruUid + "'";
		}
		Query query = getSession().createSQLQuery(hql);
		return (Object[]) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public Long getPublicCollectionCount(final String gooruOid, final String sharing) {
		final String sql = "select count(1) as count  from collection_item  ci inner join resource r  on r.content_id = ci.resource_content_id inner join content c on c.content_id = ci.resource_content_id inner join content cc on cc.content_id = ci.collection_content_id  where cc.gooru_oid =:gooruOid and c.sharing in  ('"
				+ sharing + "') and (r.type_name = 'folder' or r.type_name = 'scollection') and ci.item_type != 'collaborator' ";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		query.setParameter(GOORU_OID, gooruOid);
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getCollectionListByIds(final List<String> collectionIds) {
		final String hql = " FROM Collection c  WHERE c.gooruOid IN ( :collectionIds )";
		Query query = getSession().createQuery(hql);
		query.setParameterList("collectionIds", collectionIds);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getFolderList(final Integer limit, final Integer offset, final String gooruOid, final String title, final String gooruUid) {
		String sql = "select cc.gooru_oid as gooruOid, r.title as title, u.username as username, cc.created_on as createdOn, cc.last_modified as lastModified, cc.sharing as sharing from resource r inner join collection c on  r.content_id = c.content_id inner join content cc on c.content_id = cc.content_id inner join user u on cc.user_uid = u.gooru_uid where c.collection_type = 'folder'";
		if (gooruOid != null) {
			sql += " and cc.gooru_oid = '" + gooruOid + "'";
		}
		if (title != null) {
			sql += " and r.title = '" + title + "'";
		}
		if (gooruUid != null) {
			sql += " and u.gooru_uid = '" + gooruUid + "'";
		}
		Query query = getSession().createSQLQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public Long getFolderListCount(final String gooruOid, final String title, final String gooruUid) {
		String sql = "select count(1) as count from resource r inner join collection c on  r.content_id = c.content_id inner join content cc on c.content_id = cc.content_id inner join user u on cc.user_uid = u.gooru_uid where c.collection_type = 'folder'";
		if (gooruOid != null) {
			sql += " and cc.gooru_oid = '" + gooruOid + "'";
		}
		if (title != null) {
			sql += " and r.title = '" + title + "'";
		}
		if (gooruUid != null) {
			sql += " and u.gooru_uid = '" + gooruUid + "'";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContentMetaAssociation> getContentMetaById(final String gooruOid, final String type) {
		final String hql = "From ContentMetaAssociation ci where ci.content.gooruOid =:gooruOid and ci.associationType.value=:type";
		final Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(TYPE, type);
		return query.list();
	}

	@Override
	public ContentMetaAssociation getContentMetaByValue(final String value, final String gooruOid) {
		final String hql = "From ContentMetaAssociation ci where ci.content.gooruOid =:gooruOid and ci.value =:value";
		final Query query = getSession().createQuery(hql);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(VALUE, value);
		query.setMaxResults(1);
		return (ContentMetaAssociation) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public UserCollectionItemAssoc getUserCollectionItemAssoc(final String collectionItemId, final String userUid) {
		final String hql = "From UserCollectionItemAssoc ci where ci.collectionItem.collectionItemId =:collectionItemId and ci.user.partyUid =:userUid";
		final Query query = getSession().createQuery(hql);
		query.setParameter("collectionItemId", collectionItemId);
		query.setParameter("userUid", userUid);
		query.setMaxResults(1);
		return (UserCollectionItemAssoc) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getClasspageItems(final String gooruOid, final Integer limit, final Integer offset, final String userUid, final String orderBy, final String status, String type) {
		String sql = "select association_date,ci.collection_item_id,item_sequence,narration,planned_end_date,c.gooru_oid,r.title, r.folder, r.thumbnail, c.sharing, co.goals, IFNULL(ct.value, 'open') as status, uu.username, uu.gooru_uid, r.type_name,ci.is_required , ci.show_answer_by_questions, ci.show_hints, ci.show_answer_end ,ci.minimum_score, ci.estimated_time,uc.minimum_score as user, uc.assignment_completed , uc.time_studying, co.collection_type from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id left join user_collection_item_assoc uc on uc.collection_item_uid = ci.collection_item_id and uc.user_uid = '"
				+ userUid + "' left join custom_table_value ct on ct.custom_table_value_id = uc.status inner join user uu on uu.gooru_uid = c.user_uid  where  c.sharing in ('public', 'anyonewithlink') ";
		sql += " and rc.gooru_oid='" + gooruOid + "'  ";
		if (status != null) {
			sql += " and IFNULL(ct.value, 'open') = '" + status + "' ";
		}
		if (type != null) {
			sql += " and r.type_name ='" + type + "'";
		} else {
			sql += " and r.type_name != 'pathway'";
		}
		if (orderBy != null && orderBy.equalsIgnoreCase(RECENT)) {
			sql += " order by ci.association_date desc, item_sequence  desc ";
		} else if (orderBy != null && orderBy.equalsIgnoreCase(SEQUENCE_DESC)) {
			sql += " order by ci.item_sequence desc ";
		} else if (orderBy != null && orderBy.equalsIgnoreCase(DUE_DATE)) {
			sql += " and ci.planned_end_date IS NOT NULL order by ci.planned_end_date asc ";
		} else if (orderBy != null && orderBy.equalsIgnoreCase(DUE_DATE_EARLIEST)) {
			sql += " and ci.planned_end_date IS NOT NULL order by ci.planned_end_date desc";
		} else {
			sql += " order by ci.item_sequence asc ";
		}
		Query query = getSession().createSQLQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Collection> getCollectionsList(final User user, final Integer limit, final Integer offset, final String publishStatus) {
		String hql = " FROM Collection collection   WHERE  collection.resourceType.name=:type and " + generateOrgAuthQuery("collection.");
		if (publishStatus != null) {
			hql += " and collection.publishStatus IS NOT NULL and  collection.collectionType in ('collection', 'assessment', 'quiz') and  collection.publishStatus.keyValue =:pending order by collection.lastModified desc";
		}

		Query query = getSession().createQuery(hql);
		if (publishStatus != null) {
			query.setParameter(PENDING, publishStatus);
		}
		query.setParameter(TYPE, SCOLLECTION);
		addOrgAuthParameters(query);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list().size() > 0 ? query.list() : null;
	}

	@Override
	public Long getCollectionCount(final String publishStatus) {
		final String sql = "SELECT count(1) as count from  collection c inner join custom_table_value ct on ct.custom_table_value_id = c.publish_status_id  where c.publish_status_id is not null and ct.key_value = '" + publishStatus + "' and c.collection_type in ('collection', 'quiz', 'assessment')";
		final Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		return (Long) query.list().get(0);
	}

	@Override
	public List<Object[]> getClasspageAssoc(final Integer offset, final Integer limit, final String classpageId, final String collectionId, final String gooruUid, final String title, final String collectionTitle, final String classCode, final String collectionItemId) {
		String sql = "select  cc.gooru_oid as classpageId, cr.gooru_oid as collectionId, ci.collection_item_id as collectionItemId, ci.item_sequence as assocCollectionNo,ci.narration as direction,ci.planned_end_date as dueDate, usr.username as collectionCreator,cr.created_on as createdDate,cr.last_modified as lastModified, r.title as title, res.title as collectionTitle, u.username as creator from classpage cp inner join resource r on r.content_id = cp.classpage_content_id inner join content cc on cc.content_id = r.content_id inner join collection_item ci on cp.classpage_content_id = ci.collection_content_id inner join user u on cc.creator_uid = u.gooru_uid inner join content ct on ct.content_id = ci.collection_content_id inner join resource res on res.content_id = ci.resource_content_id inner join content cr on cr.content_id = res.content_id inner join user usr on cr.creator_uid = usr.gooru_uid where "
				+ generateAuthSqlQueryWithData("cr.");

		if (classpageId != null) {
			sql += " and cc.gooru_oid = '" + classpageId + "'";
		}
		if (collectionId != null) {
			sql += " and cr.gooru_oid = '" + collectionId + "'";
		}
		if (classCode != null) {
			sql += " and cp.classpage_code = '" + classCode + "'";
		}
		if (title != null) {
			sql += " and r.title = '" + title + "'";
		}
		if (collectionTitle != null) {
			sql += " and res.title = '" + collectionTitle + "'";
		}
		if (gooruUid != null) {
			sql += "and usr.gooru_uid = '" + gooruUid + "'";
		}
		if (collectionItemId != null) {
			sql += " and ci.collection_item_id = '" + collectionItemId + "'";
		}
		Query query = getSession().createSQLQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public BigInteger getClasspageAssocCount(final String classpageId, final String collectionId, final String gooruUid, final String title, final String collectionTitle, final String classCode, final String collectionItemId) {
		String sql = "select  count(*) from classpage cp inner join resource r on r.content_id = cp.classpage_content_id inner join content cc on cc.content_id = r.content_id inner join collection_item ci on cp.classpage_content_id = ci.collection_content_id inner join user u on cc.creator_uid = u.gooru_uid inner join content ct on ct.content_id = ci.collection_content_id inner join resource res on res.content_id = ci.resource_content_id inner join content cr on cr.content_id = res.content_id inner join user usr on cr.creator_uid = usr.gooru_uid where "
				+ generateAuthSqlQueryWithData("cr.");
		if (classpageId != null) {
			sql += " and cc.gooru_oid = '" + classpageId + "'";
		}
		if (collectionId != null) {
			sql += " and cr.gooru_oid = '" + collectionId + "'";
		}
		if (classCode != null) {
			sql += " and cp.classpage_code = '" + classCode + "'";
		}
		if (title != null) {
			sql += " and r.title = '" + title + "'";
		}
		if (collectionTitle != null) {
			sql += " and res.title = '" + collectionTitle + "'";
		}
		if (gooruUid != null) {
			sql += "and usr.gooru_uid = '" + gooruUid + "'";
		}
		if (collectionItemId != null) {
			sql += " and ci.collection_item_id = '" + collectionItemId + "'";
		}
		Query query = getSession().createSQLQuery(sql);
		return (BigInteger) query.list().get(0);
	}

	@Override
	public List<Object[]> getParentDetails(final String collectionItemId) {
		final String sql = "select cc.gooru_oid as classId, rc.title as classTitle, cp.gooru_oid as pathwayId, rp.title as pathwayTitle, cs.gooru_oid as assignmentId, rs.title as assignmentTitle,cii.narration,cii.planned_end_date,cii.is_required,cii.minimum_score from content cc inner join resource rc on (rc.content_id = cc.content_id) inner join collection_item ci on (ci.collection_content_id = rc.content_id) inner join content cp on (cp.content_id = ci.resource_content_id) inner join resource rp on (rp.content_id = cp.content_id) inner join collection_item cii on (cii.collection_content_id = rp.content_id) inner join content cs on (cs.content_id = cii.resource_content_id) inner join resource rs on (cs.content_id = rs.content_id) where cii.collection_item_id ='"
				+ collectionItemId + "'";
		Query query = getSession().createSQLQuery(sql);
		return query.list();
	}

	@Override
	public CollectionItem getCollectionItemByResourceOid(final String collectionId, final String resourceId) {
		final Query query = getSession().createQuery(GET_COLLECTION_ITEM_BY_RESOURCE_OID);
		query.setParameter("resourceId", resourceId);
		query.setParameter("collectionId", collectionId);
		addOrgAuthParameters(query);
		List<CollectionItem> collectionItems = query.list();
		return (collectionItems.size() != 0) ? collectionItems.get(0) : null;
	}

	@Override
	public List<Collection> getCollectionByResourceOid(final String resourceId) {
		Query query = getSession().createQuery(GET_COLLECTION_BY_RESOURCE_OID);
		query.setParameter("resourceId", resourceId);
		return query.list();
	}

	@Override
	public CollectionItem getNextCollectionItemResource(final String collectionId, final int sequence, final String excludeType) {
		String hql = "FROM CollectionItem collectionItem WHERE  collectionItem.collection.gooruOid=:collectionId and collectionItem.itemSequence<:itemSequence ";
		if (excludeType != null) { 
			hql += " and collectionItem.collection.collectionType not in ('"+ excludeType.replace(",", "','") + "')";
		}
		hql += " order by collectionItem.itemSequence desc";
		Query query = getSession().createQuery(hql);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(ITEM_SEQUENCE, sequence);
		query.setMaxResults(1);
		return (CollectionItem) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public CollectionItem getCollectionItemByResource(final String resourceId) {
		final Query query = getSession().createQuery(COLLECTION_ITEM_BY_RESOURCE);
		query.setParameter("resourceId", resourceId);
		return (CollectionItem) ((query.list().size() > 0) ? query.list().get(0) : null);
	}
	
	@Override
	public List<CollectionItem> getCollectionItemsByResource(final String resourceId) {
		final Query query = getSession().createQuery(COLLECTION_ITEM_BY_RESOURCE);
		query.setParameter("resourceId", resourceId);
		return query.list();
	}

	@Override
	public List<CollectionItem> getResetSequenceCollectionItems(final String collectionId, final int sequence) {
		Query query = getSession().createQuery(COLLECTION_ITEM_BY_SEQUENCE);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(ITEM_SEQUENCE, sequence);
		return query.list();
	}
	
}
