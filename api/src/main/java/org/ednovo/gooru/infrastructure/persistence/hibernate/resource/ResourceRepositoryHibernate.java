/////////////////////////////////////////////////////////////
// ResourceRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.resource;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.LtiContentAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceSummary;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class ResourceRepositoryHibernate extends BaseRepositoryHibernate implements ResourceRepository, ConstantProperties, ParameterProperties {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String RESOURCE_TYPE = "resourceType";

	private static final String RESOURCE_TYPE_NAME = "resourceType.name";

	private static final String EXCLUDE_FOR_RESOURCES_STRING = "'gooru/classplan','gooru/classbook','assessment-quiz','assessment-exam','qb/response','gooru/studyshelf','gooru/notebook','qb/question','question','scollection'";

	private static final String UPDATE_RESOURCE_SOURCE_ID = "update resource set resource_source_id = %s where content_id = %s";

	private static final String GET_CONTENT_IDS = "SELECT gooru_oid AS gooruOid , content_id AS contentId from content WHERE gooru_oid IN (:gooruOids)";
	
	private static final String GET_CONTENT_ID = "SELECT content_id AS contentId from content WHERE gooru_oid =:gooruOid";
		

	@Override
	public ResourceSource findResourceByresourceSourceId(Integer resourceSourceId) {
		try {
			String hql = "SELECT r FROM ResourceSource r where r.resourceSourceId =? ";
			Query query = getSession().createQuery(hql).setParameter(0, resourceSourceId);
			List<ResourceSource> resources = list(query);
			return resources.size() == 0 ? null : resources.get(0);
		} catch (Exception ex) {
			return null;
		}
	}

	public List<Resource> findAllResourceBySourceId(Integer resourceSourceId) {
		String hql = "SELECT r FROM Resource r WHERE r.resourceSource.resourceSourceId =:resourceSourceId ";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceSourceId", resourceSourceId);
		query.setFirstResult(0);
		query.setMaxResults(5001);
		List<Resource> resourceList = list(query);
		return resourceList != null && resourceList.size() > 0 ? resourceList : null;
	}

	@Override
	public Resource findResourceByContentGooruId(String gooruOid) {
		List<Resource> resources = list(getSession().createQuery("SELECT r FROM Resource r  where r.gooruOid ='" + gooruOid + "' AND " + generateAuthQueryWithDataNew("r.")));
		return resources.size() == 0 ? null : resources.get(0);
	}


	@Override
	public Long getContentId(String contentGooruOid) {
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(GET_CONTENT_ID).addScalar(CONTENT_ID, StandardBasicTypes.LONG);
		query.setParameter(GOORU_OID, contentGooruOid);
		List<Long> results = list(query);

		return (results != null && results.size() > 0) ? results.get(0) : 0L;
	}
	
	@Override
	public List<Object[]> getContentIds(String gooruOids) {		
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(GET_CONTENT_IDS).setParameterList(GOORU_OIDS, gooruOids.split(COMMA));
		List<Object[]> contentIds= list(query);		 
		return contentIds; 
	}
	
	@Override
	public List<Resource> findWebResourcesForBlacklisting() {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Resource.class).add(Restrictions.isNull("hasFrameBreaker")).createAlias(RESOURCE_TYPE, RESOURCE_TYPE).add(Restrictions.eq(RESOURCE_TYPE_NAME, "resource/url"));
		addAuthCriterias(criteria);
		return criteria(criteria);
	}

	@Override
	public void updateWebResource(Long contentId, Integer status) {
		Boolean hasFrameBraker = null;
		Integer brokenStatus = null;
		if (status != null) {
			if (status.equals(200)) {
				hasFrameBraker = false;
				brokenStatus = 0;
			} else if (status.equals(-200)) {
				hasFrameBraker = true;
				brokenStatus = 1;
			} else {
				brokenStatus = status;
			}
		}
		Resource resource = (Resource) get(Resource.class, contentId);
		if (resource != null) {
			resource.setHasFrameBreaker(hasFrameBraker);
			resource.setBrokenStatus(brokenStatus);
			saveOrUpdate(resource);
		}
	}

	@Override
	public void saveOrUpdate(Resource resource) {

		if (resource.getLicense() == null) {
			resource.setLicense(License.OTHER);
		}
		if (StringUtils.isBlank(resource.getSharing())) {
			SessionContextSupport.putLogParameter("sharing-" + resource.getGooruOid(), resource.getSharing() + " to public");
			resource.setSharing("public");
		}
		if (resource.getContentType() == null) {
			ContentType ct = new ContentType();
			ct.setName(ContentType.RESOURCE);
			resource.setContentType(ct);
		}
		// set type by url:
		if (resource.getResourceType() == null) {

			ResourceType.Type type = ResourceType.Type.RESOURCE;
			try {
				if (StringUtils.isNotBlank(resource.getUrl())) {
					String url = resource.getUrl();
					if (url.endsWith(".pdf")) {
						type = ResourceType.Type.HANDOUTS;
					} else if ((new URL(url)).getHost().contains("youtube")) {
						type = ResourceType.Type.VIDEO;
					}
				}
			} catch (Exception e) {
				System.out.println("Exception : " + e);
			}
			resource.setResourceTypeByString(type.getType());
		}

		// set properties for new resource:
		if (resource.getGooruOid() == null) {
			resource.setGooruOid(UUID.randomUUID().toString());
		}
		if (resource.getCreatedOn() == null) {
			resource.setCreatedOn(new Date(System.currentTimeMillis()));
		}
		if (resource.getUser() == null) {
			User user = new User();
			user.setUserId(1);
			resource.setUser(user);
		}

		// update the last modified time:
		resource.setLastModified(new Date(System.currentTimeMillis()));

		super.saveOrUpdate(resource);
		// flush();
	}

	@Override
	public Textbook findTextbookByContentGooruId(String gooruContentId) {
		String hql = "SELECT textbook FROM Textbook textbook   WHERE textbook.gooruOid = '" + gooruContentId + "' AND " + generateAuthQueryWithDataNew("textbook.") + " ";
		Query query = getSession().createQuery(hql);
		List<Textbook> result = list(query);
		return (result.size() > 0) ? result.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Resource findWebResource(String url) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Resource.class, "resource").add(Restrictions.eq("url", url)).add(Restrictions.or(Restrictions.eq(RESOURCE_TYPE_NAME, ResourceType.Type.RESOURCE.getType()), Restrictions.eq(RESOURCE_TYPE_NAME, ResourceType.Type.VIDEO.getType())));
		List<Resource> result = addAuthCriterias(criteria).list();
		return (result.size() > 0) ? result.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Resource findByFileHash(String fileHash, String typeName, String url, String category) {

		if (fileHash != null && url != null && !fileHash.equals("") && !url.equals("")) {
			Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Resource.class).add(Restrictions.eq("fileHash", fileHash)).createAlias(RESOURCE_TYPE, RESOURCE_TYPE).add(Restrictions.eq(RESOURCE_TYPE_NAME, typeName)).add(Restrictions.eq("url", url));
			if (category != null && category.length() > 2) {
				criteria = criteria.add(Restrictions.eq("category", category));
			}
			List<Resource> resources = addAuthCriterias(criteria).list();
			return resources.size() > 0 ? resources.get(0) : null;
		}

		return null;
	}

	@Override
	public ResourceSource findResourceSource(String domainName) {
		String hql = "FROM ResourceSource rSource WHERE  rSource.domainName = '" + domainName + "'  and rSource.activeStatus = 1";
		Query query = getSession().createQuery(hql).setFetchSize(1);
		List<ResourceSource> resourceSources = list(query);
		return (resourceSources != null && resourceSources.size() > 0) ? resourceSources.get(0) : null;
	}

	@Override
	public void updateResourceSourceId(Long contentId, Integer resourceSourceId) {
		String updateResourceSourceId = format(UPDATE_RESOURCE_SOURCE_ID, resourceSourceId, contentId);
		this.getJdbcTemplate().update(updateResourceSourceId);

	}

	@Override
	public Resource getResourceByUrl(String url) {
		Query query = getSession().createQuery("SELECT r From Resource as r   WHERE r.url='" + url + "' AND  " + generateAuthQueryWithDataNew("r.") + " ");
		List<Resource> resources = list(query);
		return (resources.size() > 0) ? resources.get(0) : null;
	}

	@Override
	public List<ResourceSource> getSuggestAttribution(String keyword) {
		String hql = "FROM ResourceSource rss WHERE rss.attribution LIKE '%" + keyword + "%'";
		return list(getSession().createQuery(hql));
	}

	public ResourceSource getAttribution(String attribution) {
		String hql = "FROM ResourceSource rss WHERE rss.attribution = '" + attribution + "'";
		List<ResourceSource> resourceSource = list(getSession().createQuery(hql));
		return (resourceSource.size() > 0) ? resourceSource.get(0) : null;
	}

	@Override
	public Map<String, Object> findAllResourcesSource(Map<String, String> filters) {

		List<ResourceSource> resourceSourceList = list(getSession().createQuery("from ResourceSource"));

		Criteria criteria = getSession().createCriteria(ResourceSource.class);

		Map<String, Object> rsMap = new HashMap<String, Object>();

		rsMap.put("allResourceSource", resourceSourceList);

		if (filters != null) {
			Integer pageNum = 1;
			if (filters.containsKey(PAGE_NO)) {
				pageNum = Integer.parseInt(filters.get(PAGE_NO).trim());
			}
			Integer pageSize = 10;
			if (filters.containsKey(PAGE_SIZE)) {
				pageSize = Integer.parseInt(filters.get(PAGE_SIZE).trim());
			}

			if (filters.containsKey("attribution")) {
				criteria.add(Restrictions.like("attribution", "%" + filters.get("attribution").trim() + "%"));
			}

			if (filters.containsKey("domainName")) {
				criteria.add(Restrictions.like("domainName", "%" + filters.get("domainName").trim() + "%"));
			}
			criteria.add(Restrictions.eq("type", filters.get("type")));
			criteria.setFirstResult(((pageNum - 1) * pageSize));
			criteria.setMaxResults(pageSize);
			criteria.addOrder(Order.asc("resourceSourceId"));
		}
		rsMap.put("filteredResourcSource", criteria.list());
		return rsMap;
	}

	@Override
	public ResourceInfo findResourceInfo(String resourceGooruOid) {
		String hql = "Select info FROM ResourceInfo info left outer join info.resource r  WHERE r.gooruOid =:resourceGooruOid ";
		Query query = getSession().createQuery(hql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		List<ResourceInfo> infos = list(query);
		return infos.size() > 0 ? infos.get(0) : null;
	}

	@Override
	public void deleteResourceBulk(String contentIds) {
		try {
			String hql = "DELETE Resource resource  where resource.gooruOid IN(:gooruOIds) AND  " + generateAuthQuery("resource");
			Query query = getSession().createQuery(hql);
			query.setParameterList("gooruOIds", contentIds.split(","));
			addAuthParameters(query);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error("couldn't delete resource", e);
		}
	}

	@Override
	public List<Resource> findAllResourcesByGooruOId(String resourceGooruOIds) {
		String hql = "SELECT resource FROM Resource resource   WHERE resource.gooruOid IN(:gooruOIds) AND  " + generateAuthQuery("resource.");
		Query query = getSession().createQuery(hql);
		query.setParameterList("gooruOIds", resourceGooruOIds.split(","));
		addAuthParameters(query);
		return list(query);
	}

	@Override
	public Resource findResourceByUrl(String resourceUrl, String sharing, String userUid) {
		String videoId = ResourceImageUtil.getYoutubeVideoId(resourceUrl);
		String type = null;
		String hql = "SELECT resource FROM Resource resource   WHERE  " + generateAuthQuery("resource.");
		if (videoId != null) {
			resourceUrl = "%" + videoId + "%";
			type = ResourceType.Type.VIDEO.getType();
			hql += " and resource.url Like :resourceUrl";
		} else {
			type = ResourceType.Type.RESOURCE.getType();
			hql += " and resource.url = :resourceUrl";
		}

		if (userUid != null) {
			hql += " AND resource.user.partyUid =:userUid";
		}
		if (sharing != null) {
			hql += " AND resource.sharing = :sharing";
		}

		hql += " AND resource.resourceType.name =:type";
		Query query = getSession().createQuery(hql);
		query.setParameter("resourceUrl", resourceUrl);
		query.setParameter("type", type);
		if (sharing != null) {
			query.setParameter(SHARING, sharing);
		}
		if (userUid != null) {
			query.setParameter(USER_UID, userUid);
		}
		addAuthParameters(query);
		List<Resource> resourceList = list(query);
		return resourceList != null && resourceList.size() > 0 ? resourceList.get(0) : null;
	}


	@Override
	public ResourceInfo getResourcePageCount(String resourceId) {
		String hql = "SELECT r FROM ResourceInfo r   WHERE r.resource.gooruOid=:resourceId AND " + generateAuthQuery("r.resource.");
		Query query = getSession().createQuery(hql);
		query.setParameter("resourceId", resourceId);
		addAuthParameters(query);
		List<ResourceInfo> resourceInfo = list(query);
		return (resourceInfo != null && resourceInfo.size() > 0) ? resourceInfo.get(0) : null;
	}

	@Override
	public String shortenedUrlResourceCheck(String domainName, String domainType) {
		String hql = "SELECT r.type FROM ResourceSource r WHERE r.domainName=:domainName AND r.type=:domainType";
		Query query = getSession().createQuery(hql);
		query.setParameter("domainName", domainName);
		query.setParameter("domainType", domainType);
		List<String> urlType = list(query);
		return (urlType != null && urlType.size() > 0) ? urlType.get(0) : null;
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		String hql = "SELECT r.resource FROM ResourceInstance r";
		Query query = getSession().createQuery(hql);
		query.setFirstResult(pageSize * (pageNum - 1));
		query.setMaxResults(pageSize != null ? (pageSize > MAX_LIMIT ? MAX_LIMIT : pageSize) : pageSize);
		return list(query);
	}

	@Override
	public ResourceMetadataCo findResourceFeeds(String resourceGooruOid) {
		String hql = "Select rf FROM ResourceFeeds rf left outer join rf.resource r WHERE r.gooruOid =:resourceGooruOid AND " + generateOrgAuthQuery("r.");
		Query query = getSession().createQuery(hql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		addOrgAuthParameters(query);
		List<ResourceMetadataCo> resourceFeeds = list(query);

		return (resourceFeeds != null && resourceFeeds.size() > 0) ? resourceFeeds.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.resource.ResourceRepository#
	 * getResourceIndexFields(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getResourceFlatten(List<Long> contentIds) {

		String contentIdsJoined = StringUtils.join(contentIds, ",");
		String sql = "SELECT vrm.*, vrcd.*, vrsd.scollection_gooru_oids, vrcf.* from v_resource_meta vrm left join v_resource_coll_data vrcd on vrcd.content_id = vrm.id left join v_resource_scoll_data vrsd on vrsd.resource_id = vrm.id left outer join v_resource_cust_fields vrcf on vrcf.custom_fields_content_id = vrm.id where vrm.id in ("
		        + contentIdsJoined + ")";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.domain.model.resource.ResourceRepository#findResources
	 * (java.util.Map)
	 */

	@Override
	public List<Long> findResources(Map<String, String> filters) {
		String sql = "select r.content_id from resource r WHERE r.type_name not in (" + EXCLUDE_FOR_RESOURCES_STRING + ",'assessment-question')";
		int pageSize = Integer.parseInt(filters.get("pageSize"));
		int pageNo = Integer.parseInt(filters.get("pageNo"));
		String batchId = filters.get("batchId");
		if (batchId != null) {
			sql += " WHERE";
			sql += " r.batch_id= " + batchId;
		}
		sql += " LIMIT " + (pageSize * (pageNo - 1)) + " , " + pageSize;
		SQLQuery query = getSession().createSQLQuery(sql);
		query.addScalar("content_id", StandardBasicTypes.LONG);
		List<Long> results = list(query);
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.resource.ResourceRepository#
	 * getResourceFieldValueById(java.lang.String, long)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getResourceFieldValueById(String fields, String contentIds) {
		String sql = "SELECT content_id," + fields + " FROM resource WHERE content_id IN(" + contentIds + ")";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(Integer limit, Integer offset) {
		String hql = "SELECT r.resource FROM ResourceInstance r";
		Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(offset != null ? (offset > MAX_LIMIT ? MAX_LIMIT : offset) : offset);
		return list(query);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getPartyPermissions(long contentId) {
		String sql = "select cp.party_uid as uId,p.party_name as name,p.party_type as type FROM content_permission cp INNER JOIN party p on p.party_uid = cp.party_uid WHERE cp.content_id=:contentId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("contentId", contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getContentSubscription(long contentId) {
		String sql = "select a.resource_id as subscribed_content_id ,count(*) as subscriberCount from content c inner join annotation a on a.resource_id = c.content_id where a.type_name='subscription' and c.content_id=:contentId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("contentId", contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		@SuppressWarnings("rawtypes")
		List results = query.list();
		return (Map<String, Object>) ((results.size() > 0) ? results.get(0) : null);
	}

	@Override
	public Textbook findTextbookByContentGooruIdWithNewSession(String gooruOid) {
		String hql = "SELECT textbook FROM Textbook textbook JOIN textbook.securityGroups sg WHERE textbook.gooruOid = '" + gooruOid + "' AND textbook.organization.partyUid IN ( " + getUserOrganizationUidsAsString() + ")";
		List<Textbook> result = list(getSession().createQuery(hql));
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public void saveTextBook(final Long contentId, final String documentId, final String documentKey) {
		final String sql = "INSERT INTO textbook (content_id, document_id,document_key) values(?,?,?)";
		PreparedStatementCreator creator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement saveTextbook = con.prepareStatement(sql);
				saveTextbook.setLong(1, contentId);
				saveTextbook.setString(2, documentId);
				saveTextbook.setString(3, documentKey);
				return saveTextbook;
			}
		};
		getJdbcTemplate().update(creator);
	}

	@Override
	public License getLicenseByLicenseName(String licenseName) {
		String hql = "From License lic where lic.name =:licenseName";
		Query query = getSession().createQuery(hql).setParameter("licenseName", licenseName);
		return (License) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public Resource findResourceByContent(String gooruOid) {
		Query query = getSession().createQuery("SELECT r FROM Resource r  where r.gooruOid ='" + gooruOid + "'");
		List<Resource> resources = list(query);
		return resources.size() == 0 ? null : resources.get(0);
	}

	@Override
	public Resource findLtiResourceByContentGooruId(String gooruContentId) {
		Query query = getSession().createQuery("SELECT lti FROM LtiContentAssoc lti  where lti.contextId ='" + gooruContentId + "'");
		List<LtiContentAssoc> ltiContentAssoc = list(query);
		return ltiContentAssoc.size() == 0 ? null : ltiContentAssoc.get(0).getResource();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContentProvider> getResourceContentProvierList() {
		String hql = "From ContentProvider where activeFlag=1";
		Query query = getSession().createQuery(hql);
		query.setFirstResult(0);
		query.setMaxResults(200);
		List<ContentProvider> contentProviderList = (List<ContentProvider>) query.list();
		return (contentProviderList != null && contentProviderList.size() > 0) ? contentProviderList : null;
	}

	@Override
	public ResourceSummary getResourceSummaryById(String gooruOid) {
		String hql = "From ResourceSummary rs where rs.resourceGooruOid= '" + gooruOid + "'";
		Query query = getSession().createQuery(hql);
		return query.list().size() > 0 ? (ResourceSummary) query.list().get(0) : null;
	}


	@Override
	public List<Collection> getCollectionsByResourceId(String resourceId, String sharing, Integer limit, Integer offset) {
		String hql = "SELECT ci.collection FROM  CollectionItem ci  where ci.content.gooruOid=:resourceId";
		if (sharing != null) {
			hql += " and ci.collection.sharing in ('" + sharing.replace(",", "','") + "') ";
		}
		hql += " group by  ci.collection.user";
		Query query = getSession().createQuery(hql);
		query.setParameter(RESOURCE_ID, resourceId);
		query.setFirstResult(offset != null ? offset : OFFSET);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return list(query);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
