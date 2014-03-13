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
import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CsvCrawler;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.ResourceUrlStatus;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
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
public class ResourceRepositoryHibernate extends BaseRepositoryHibernate implements ResourceRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String CLOSING_BRACKET = ") ";

	private static final String RESOURCE_TYPE = "resourceType";

	private static final String RESOURCE_TYPE_NAME = "resourceType.name";

	private static final String UPDATE_VIEWS = "update resource r, content c set r.views_total = (r.views_total+1) where  c.gooru_oid= '%s' and c.content_id = r.content_id and %s";

	private static final String EXCLUDE_FOR_RESOURCES_STRING = "'gooru/classplan','gooru/classbook','assessment-quiz','assessment-exam','qb/response','gooru/studyshelf','gooru/notebook','qb/question','question','scollection'";

	private static final String UPDATE_RESOURCE_SOURCE_ID = "update resource set resource_source_id = %s where content_id = %s";

	private static final String UNORDERED_SEGMENTS = "select segment_id from resource_instance group by segment_id having count(distinct sequence) <> count(1)";

	private static final String PAGE_START = "startAt";

	@Override
	public ResourceSource findResourceByresourceSourceId(Integer resourceSourceId) {
		try {
			// FIXME
			List<ResourceSource> resources = find("SELECT r FROM ResourceSource r where r.resourceSourceId =? ", resourceSourceId);
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
		List<Resource> resourceList = (List<Resource>) query.list();
		return resourceList != null && resourceList.size() > 0 ? resourceList : null;
	}

	@Override
	public Resource findResourceByContentGooruId(String gooruOid) {
		List<Resource> resources = find("SELECT r FROM Resource r  where r.gooruOid ='" + gooruOid + "' AND " + generateAuthQueryWithDataNew("r."));
		return resources.size() == 0 ? null : resources.get(0);
	}

	@Override
	public int findViews(String contentGooruId) {
		Session session = getSessionFactory().getCurrentSession();
		String sql = "SELECT r.views FROM Resource r   WHERE r.gooruOid = '" + contentGooruId + "' AND  " + generateAuthQueryWithDataNew("r.");
		Query query = session.createQuery(sql);
		List<Long> results = query.list();

		return (results != null && results.size() > 0) ? Integer.valueOf(results.get(0) + "") : 0;
	}

	@Override
	public void incrementViews(String contentGooruId) {
		String updateViews = DatabaseUtil.format(UPDATE_VIEWS, contentGooruId, generateAuthSqlQueryWithData("c."));
		this.getJdbcTemplate().update(updateViews);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Resource> findWebResourcesForBlacklisting() {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Resource.class).add(Restrictions.isNull("hasFrameBreaker")).createAlias(RESOURCE_TYPE, RESOURCE_TYPE).add(Restrictions.eq(RESOURCE_TYPE_NAME, "resource/url"));
		addAuthCriterias(criteria);
		return criteria.list();
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
	public List<Resource> listResources(Map<String, String> filters) {

		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 50;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			try {
				pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
			} catch (Exception e) {
				pageSize = Integer.parseInt(StringUtils.substringBefore(filters.get(PAGE_SIZE), ",8"));
			}
		}
		String resourceType = filters.get(RESOURCE_TYPE);

		Integer featured = null;

		if (filters.containsKey("featured")) {
			featured = Integer.valueOf(filters.get("featured"));
		}
		String recordSource = filters.get("recordSource");

		String notResourceType = filters.get("not.resourceType");
		String excludes = "";
		if (notResourceType != null) {
			String[] excludeResources = notResourceType.split(",");

			for (String ex : excludeResources) {
				excludes += "'" + ex + "',";
			}
			excludes = excludes.substring(0, excludes.length() - 1);
		}

		String keyword = filters.get("keyword");
		if (filters.containsKey("accessType") && filters.get("accessType").equals("my")) {
			String userId = filters.get("userId");
			String includes = "";
			if (!resourceType.equals("all")) {
				String[] includeResources = resourceType.split(",");
				for (String ex : includeResources) {
					includes += "'" + ex + "',";
				}
				includes = includes.substring(0, includes.length() - 1);
			}
			String hql = "Select distinct(resource) FROM Resource resource  ,UserContentAssoc assoc   WHERE assoc.content.contentId = resource.contentId AND assoc.user.partyUid = '" + userId + "' AND resource.resourceType.name NOT IN (" + excludes + CLOSING_BRACKET;
			if (includes.length() > 1) {
				hql += " AND  resource.resourceType.name IN (" + includes + CLOSING_BRACKET;
			}

			if (featured != null) {
				hql += " AND resource.isFeatured =  " + featured + " ";
			}
			if (filters != null && filters.containsKey("tags")) {
				hql += " AND resource.tags = '" + filters.get("tags") + "'";
			}

			hql += " AND " + generateAuthQueryWithDataNew("resource.");

			// if (filters.containsKey("fetch_subscription_resource_alone") &&
			// filters.get("fetch_subscription_resource_alone").equals("1")) {
			if (filters.containsKey("fetchFromMyContent") && filters.get("fetchFromMyContent").equals("1")) {
				hql += " AND assoc.relationshipId = 2 ";
			} else {
				hql += " AND assoc.relationshipId = 6 ";
			}
			/*
			 * }else { hql +=
			 * " AND ( assoc.relationshipId = 6 OR assoc.relationshipId = 10 ) "
			 * ; }
			 */
			if (filters != null && !(StringUtils.isEmpty(keyword))) {
				hql += " AND resource.title like '%" + keyword + "%'";
			}
			hql += " ORDER BY assoc.lastActiveDate DESC";
			Query query = getSessionFactory().getCurrentSession().createQuery(hql);

			Integer startAt = Integer.parseInt(filters.get(PAGE_START));

			if (startAt == 0) {
				startAt = ((pageNum - 1) * pageSize);
			} else {
				startAt = startAt - 1;
			}

			query.setFirstResult(startAt);
			query.setMaxResults(pageSize);
			return query.list();
		}

		String hql = "SELECT distinct(resource) FROM Resource resource ";

		if (filters.containsKey("usedResource") && filters.get("usedResource") != null && filters.get("usedResource").equalsIgnoreCase("scollection")) {
			hql = "SELECT distinct(resource) FROM CollectionItem collectionItem INNER JOIN collectionItem.resource resource ";
		}

		hql += " LEFT JOIN resource.taxonomySet taxonomySet";

		if (filters.containsKey("standards") && filters.get("standards") != null) {
			hql += " INNER JOIN taxonomySet.associatedCodes assocCodes";
		}

		hql += " WHERE 1 =1 ";

		if (resourceType != null && !resourceType.equals("all")) {
			String[] includeResources = resourceType.split(",");
			StringBuilder includes = new StringBuilder();
			for (String ex : includeResources) {
				includes.append("'" + ex + "',");
			}
			String includeResourceTypes = includes.toString().substring(0, includes.toString().length() - 1);
			hql += " AND resource.resourceType.name IN (" + includeResourceTypes + ")";
		}
		String attributions = filters.get("attribution");
		if (attributions != null) {
			String[] includeMultipleAttribution = attributions.split(",");
			StringBuilder includesAttribution = new StringBuilder();
			for (String attribution : includeMultipleAttribution) {
				includesAttribution.append("'" + attribution + "',");
			}
			String includeResourcesSourceAttribution = includesAttribution.toString().substring(0, includesAttribution.toString().length() - 1);
			hql += " AND resource.resourceSource.attribution IN (" + includeResourcesSourceAttribution + ")";
		}
		if (featured != null) {
			hql += " AND resource.isFeatured = '" + featured + "'";
		}
		if (!StringUtils.isEmpty(recordSource)) {
			hql += " AND resource.recordSource = '" + recordSource + "'";
		}
		if (filters.containsKey("resourceSource")) {
			hql += " AND resource.resourceSource IS NULL ";
		}
		if (notResourceType != null && !notResourceType.isEmpty()) {
			hql += " AND resource.resourceType.name NOT IN ( " + EXCLUDE_FOR_RESOURCES_STRING + "," + excludes + " ) ";
		} else {
			hql += " AND resource.resourceType.name NOT IN ( " + EXCLUDE_FOR_RESOURCES_STRING + " ) ";
		}

		if (filters.containsKey("thumbnail")) {
			String thumbnailValue = filters.get("thumbnail");
			if (thumbnailValue.equalsIgnoreCase("null")) {
				hql += " AND resource.thumbnail IS NULL ";
				hql += " AND resource.title IS NULL ";
			}
		}
		String orderBy = filters.get("orderBy");
		if (filters.containsKey("taxonomyParentId")) {
			String taxonomyParentIdString = filters.get("taxonomyParentId");
			try {
				Integer taxonomyParentId = Integer.valueOf(taxonomyParentIdString);
				hql += " AND taxonomySet.parentId = '" + taxonomyParentId + "'";
			} catch (NumberFormatException ex) {
				// We're really not doing anything here since we don't
				// handle the invalid taxonomyParentId's passed.
				// TODO Validate to make sure this is an integer
			}
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

		hql += " AND " + generateAuthQueryWithDataNew("resource.");

		if (filters.containsKey("batchId") && filters.get("batchId") != null) {
			hql += " AND resource.batchId = '" + filters.get("batchId") + "'";
		}
		if (!filters.containsKey("pagination") || !filters.get("pagination").equals("disable")) {
			if (orderBy != null && orderBy.equalsIgnoreCase("lesson")) {
				hql += " ORDER BY taxonomySet.label ASC ";
			} else if (orderBy != null && orderBy.equalsIgnoreCase("lastModified")) {
				hql += " ORDER BY taxonomySet.lastModified DESC ";
			} else if (orderBy != null && orderBy.equalsIgnoreCase("mostViewed")) {
				hql += " ORDER BY taxonomySet.views DESC ";

			}
		}
		Query query = getSession().createQuery(hql);
		if (!filters.containsKey("pagination") || !filters.get("pagination").equals("disable")) {
			query.setFirstResult(pageSize * (pageNum - 1));
			query.setMaxResults(pageSize);
		}
		return query.list();
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
		List<Textbook> result = find(hql);
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public Resource findWebResource(String url) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Resource.class, "resource").add(Restrictions.eq("url", url)).add(Restrictions.or(Restrictions.eq(RESOURCE_TYPE_NAME, ResourceType.Type.RESOURCE.getType()), Restrictions.eq(RESOURCE_TYPE_NAME, ResourceType.Type.VIDEO.getType())));
		List<Resource> result = addAuthCriterias(criteria).list();
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public void retriveAndSetInstances(Resource resource) {

		List<ResourceInstance> resourceInstances = find("SELECT r From ResourceInstance as r  WHERE r.resource.contentId=" + resource.getContentId() + "AND " + generateAuthQueryWithDataNew("r.resource.") + "");
		if (resourceInstances != null) {
			resource.setResourceInstances(resourceInstances);
		}
	}

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
		List<ResourceSource> resourceSources = findFirstNRows("FROM ResourceSource rSource WHERE  rSource.domainName = '" + domainName + "'  and rSource.activeStatus = 1", 1);
		return (resourceSources != null && resourceSources.size() > 0) ? resourceSources.get(0) : null;
	}

	@Override
	public ResourceInstance findResourceInstanceByContentGooruId(String gooruOid) {
		List<ResourceInstance> resourceInstances = find("SELECT r From ResourceInstance as r  WHERE r.resource.gooruOid='" + gooruOid + "' AND " + generateAuthQueryWithDataNew("r.resource.") + " ");
		return (resourceInstances.size() > 0) ? resourceInstances.get(0) : null;
	}

	@Override
	public void updateResourceSourceId(Long contentId, Integer resourceSourceId) {
		String updateResourceSourceId = DatabaseUtil.format(UPDATE_RESOURCE_SOURCE_ID, resourceSourceId, contentId);
		this.getJdbcTemplate().update(updateResourceSourceId);

	}

	@Override
	public Resource getResourceByUrl(String url) {
		List<Resource> resources = find("SELECT r From Resource as r   WHERE r.url='" + url + "' AND  " + generateAuthQueryWithDataNew("r.") + " ");
		return (resources.size() > 0) ? resources.get(0) : null;
	}

	@Override
	public List<ResourceSource> getSuggestAttribution(String keyword) {
		String hql = "FROM ResourceSource rss WHERE rss.attribution LIKE '%" + keyword + "%'";
		return (List<ResourceSource>) find(hql);
	}

	public ResourceSource getAttribution(String attribution) {
		String hql = "FROM ResourceSource rss WHERE rss.attribution = '" + attribution + "'";
		List<ResourceSource> resourceSource = find(hql);
		return (resourceSource.size() > 0) ? resourceSource.get(0) : null;
	}

	@Override
	public Map<String, Object> findAllResourcesSource(Map<String, String> filters) {

		Session session = getSessionFactory().getCurrentSession();

		List<ResourceSource> resourceSourceList = find("from ResourceSource");

		Criteria criteria = session.createCriteria(ResourceSource.class);

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
	public List<Resource> findByContentIds(List<Long> contentIds) {
		if (contentIds != null) {
			StringBuilder strId = new StringBuilder();
			for (Long contentId : contentIds) {
				strId.append(" '").append(contentId).append("' , ");
			}
			return find("SELECT r FROM Resource r  WHERE r.gooruOid IN ( " + strId.substring(0, strId.length() - 2) + " ) AND " + generateAuthQueryWithDataNew("r.") + "");
		}
		return null;
	}

	@Override
	public void insertResourceUrlStatus() {
		String query = "insert into resource_url_status (resource_id) select content_id from resource res where not exists (select 1 from resource_url_status where resource_id = content_id) and res.type_name in ( 'video/youtube' , 'resource/url' )";
		getJdbcTemplate().execute(query);
	}

	@Override
	public List<String> getUnorderedInstanceSegments() {
		Session session = getSessionFactory().getCurrentSession();
		return session.createSQLQuery(UNORDERED_SEGMENTS).list();
	}

	@Override
	public List<ResourceInstance> getUnorderedInstances(String segmentId) {
		String hql = "SELECT instance FROM ResourceInstance instance  WHERE instance.segment.segmentId = :segmentId AND " + generateAuthQuery("instance.resource.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("segmentId", segmentId);
		addAuthParameters(query);
		return (List<ResourceInstance>) query.list();

	}

	@Override
	public ResourceInfo findResourceInfo(String resourceGooruOid) {
		String hql = "Select info FROM ResourceInfo info left outer join info.resource r  WHERE r.gooruOid =:resourceGooruOid ";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		List<ResourceInfo> infos = query.list();
		return infos.size() > 0 ? infos.get(0) : null;
	}

	
	@Override
	public void deleteResourceBulk(String contentIds) {
		// FIXME
		try {
			String hql = "DELETE Resource resource  where resource.gooruOid IN(:gooruOIds) AND  " + generateAuthQuery("resource");
			Session session = getSessionFactory().getCurrentSession();
			Query query = session.createQuery(hql);
			query.setParameterList("gooruOIds", contentIds.split(","));
			addAuthParameters(query);
			query.executeUpdate();
		} catch (Exception e) {
			getLogger().error("couldn't delete resource", e);
		}
	}

	@Override
	public String getContentIdsByGooruOIds(String resourceGooruOIds) {
		String hql = "SELECT resource.contentId FROM Resource resource   WHERE resource.gooruOid IN(:gooruOIds) AND  " + generateAuthQuery("resource.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameterList("gooruOIds", resourceGooruOIds.split(","));
		addAuthParameters(query);
		List<Long> resourceContentIds = query.list();
		String contentIds = "";
		int count = 0;
		for (Long contentId : resourceContentIds) {
			if (count > 0) {
				contentIds += ",";
			}
			contentIds += contentId.toString();
			count++;
		}
		return contentIds;
	}

	@Override
	public List<Resource> findAllResourcesByGooruOId(String resourceGooruOIds) {
		String hql = "SELECT resource FROM Resource resource   WHERE resource.gooruOid IN(:gooruOIds) AND  " + generateAuthQuery("resource.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameterList("gooruOIds", resourceGooruOIds.split(","));
		addAuthParameters(query);
		return (List<Resource>) query.list();
	}

	@Override
	public ResourceUrlStatus findResourceUrlStatusByGooruOId(String resourceGooruOId) {
		String hql = "SELECT urlStatus FROM ResourceUrlStatus urlStatus   WHERE urlStatus.resource.gooruOid = '" + resourceGooruOId + "' AND  " + generateAuthQueryWithDataNew("urlStatus.resource.");
		List<ResourceUrlStatus> resourceUrlStatus = find(hql);
		return resourceUrlStatus.size() > 0 ? resourceUrlStatus.get(0) : null;
	}

	@Override
	public List<ResourceInstance> findResourceInstances(String gooruOid, String userUid) {
		String hql = "SELECT r From ResourceInstance as r   WHERE r.resource.gooruOid=:gooruOid AND  " + generateAuthQuery("r.resource.");
		if (userUid != null) {
			hql += " AND r.resource.user.partyUid =:userUid";
		}
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		if (userUid != null) {
			query.setParameter("userUid", userUid);
		}
		addAuthParameters(query);
		return (List<ResourceInstance>) query.list();
	}

	@Override
	public Resource findResourceByUrl(String resourceUrl, String sharing, String userUid) {
		String videoId = ResourceImageUtil.getYoutubeVideoId(resourceUrl);
		String type = null;
		String hql = "SELECT resource FROM Resource resource   WHERE  resource.sharing = :sharing AND " + generateAuthQuery("resource.");
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

		hql += " AND resource.resourceType.name =:type";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceUrl", resourceUrl);
		query.setParameter("type", type);
		query.setParameter("sharing", sharing);
		if (userUid != null) {
			query.setParameter("userUid", userUid);
		}
		addAuthParameters(query);
		List<Resource> resourceList = (List<Resource>) query.list();
		return resourceList != null && resourceList.size() > 0 ? resourceList.get(0) : null;
	}

	@Override
	public List<Resource> getResourceListByUrl(String resourceUrl, String sharing, String userUid) {

		String hql = "SELECT resource FROM Resource resource   WHERE  resource.url = :resourceUrl AND resource.sharing = :sharing AND  " + generateAuthQuery("resource.");

		if (userUid != null) {
			hql += " AND resource.user.partyUid =:userUid";
		}
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceUrl", resourceUrl);
		query.setParameter("sharing", sharing);
		if (userUid != null) {
			query.setParameter("userUid", userUid);
		}
		addAuthParameters(query);
		List<Resource> resourceList = (List<Resource>) query.list();
		return resourceList != null && resourceList.size() > 0 ? resourceList : null;
	}

	@Override
	public List<Resource> listAllResourceWithoutGroups(Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		String hql = "SELECT resource FROM Resource resource   WHERE " + generateAuthQueryWithDataNew("resource.");
		Session session = getSessionFactory().getCurrentSession();
		List<Resource> resourceList = session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
		return (resourceList.size() > 0) ? resourceList : null;
	}

	@Override
	public Resource getResourceByResourceInstanceId(String resourceInstanceId) {

		String hql = "SELECT r.resource From ResourceInstance r  WHERE r.resourceInstanceId=:resourceInstanceId AND " + generateOrgAuthQuery("r.resource.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceInstanceId", resourceInstanceId);
		addOrgAuthParameters(query);
		List<Resource> resource = query.list();
		return (resource != null && resource.size() > 0) ? resource.get(0) : null;
	}

	@Override
	public List<String> findAllPublicResourceGooruOIds(Map<String, String> filters) {
		String hql = "SELECT r.gooruOid FROM Resource r  WHERE r.sharing='public' AND  " + generateAuthQueryWithDataNew("r.");
		Session session = getSession();
		int pageNum = 1;
		int pageSize = 100;
		if (filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		if (filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}
		List<String> gooruOIds = session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
		return (gooruOIds.size() > 0) ? gooruOIds : null;
	}

	@Override
	public ResourceInfo getResourcePageCount(String resourceId) {
		String hql = "SELECT r FROM ResourceInfo r   WHERE r.resource.gooruOid=:resourceId AND " + generateAuthQuery("r.resource.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceId", resourceId);
		addAuthParameters(query);
		List<ResourceInfo> resourceInfo = query.list();
		return (resourceInfo != null && resourceInfo.size() > 0) ? resourceInfo.get(0) : null;
	}

	@Override
	public String getResourceInstanceNarration(String resourceInstanceId) {
		String hql = "SELECT r.narrative FROM ResourceInstance r WHERE r.resourceInstanceId=:resourceInstanceId";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceInstanceId", resourceInstanceId);
		List<String> resourceInstance = query.list();
		return (resourceInstance != null && resourceInstance.size() > 0) ? resourceInstance.get(0) : null;
	}

	@Override
	public CsvCrawler getCsvCrawler(String url, String type) {
		List<CsvCrawler> csvCrawlers = getSessionFactory().getCurrentSession().createCriteria(CsvCrawler.class).add(Restrictions.eq("url", url)).add(Restrictions.eq("type", type)).list();
		return (csvCrawlers != null && csvCrawlers.size() > 0) ? csvCrawlers.get(0) : null;
	}

	@Override
	public void saveCsvCrawler(CsvCrawler csvCrawler) {
		save(csvCrawler);
	}

	@Override
	public boolean findIdIsValid(Class<?> modelClass, String id) {
		String hql = "SELECT 1 FROM " + modelClass.getSimpleName() + " model WHERE model.contentId = " + id + ")";

		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<Long> results = query.list();
		return (results != null && results.size() > 0) ? true : false;
	}

	@Override
	public String shortenedUrlResourceCheck(String domainName, String domainType) {
		String hql = "SELECT r.type FROM ResourceSource r WHERE r.domainName=:domainName AND r.type=:domainType";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("domainName", domainName);
		query.setParameter("domainType", domainType);
		List<String> urlType = query.list();
		return (urlType != null && urlType.size() > 0) ? urlType.get(0) : null;
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		String hql = "SELECT r.resource FROM ResourceInstance r";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<String> resourceInstance = query.list();
		return session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
	}

	@Override
	public ResourceMetadataCo findResourceFeeds(String resourceGooruOid) {
		String hql = "Select rf FROM ResourceFeeds rf left outer join rf.resource r WHERE r.gooruOid =:resourceGooruOid AND " + generateOrgAuthQuery("r.");
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		query.setParameter("resourceGooruOid", resourceGooruOid);
		addOrgAuthParameters(query);
		List<ResourceMetadataCo> resourceFeeds = query.list();

		return (resourceFeeds != null && resourceFeeds.size() > 0) ? resourceFeeds.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.resource.ResourceRepository#
	 * getResourceIndexFields(java.util.Map)
	 */
	@Override
	public List getResourceFlatten(List<Long> contentIds) {

		String contentIdsJoined = StringUtils.join(contentIds, ",");
				String sql = "SELECT vrm.*, vrcd.*, vrsd.scollection_gooru_oids, vrcf.* from v_resource_meta vrm left join v_resource_coll_data vrcd on vrcd.content_id = vrm.id left join v_resource_scoll_data vrsd on vrsd.resource_id = vrm.id left outer join v_resource_cust_fields vrcf on vrcf.custom_fields_content_id = vrm.id where vrm.id in ("
				+ contentIdsJoined + ")";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.resource.ResourceRepository#
	 * getResourceRatingSubscription(long)
	 */
	@Override
	public List getResourceRatingSubscription(long contentId) {
		String sql = "select * from v_rating_data where content_id=" + contentId;
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return results;
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
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		query.addScalar("content_id", StandardBasicTypes.LONG);
		List<Long> results = query.list();
		return query.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.resource.ResourceRepository#
	 * getResourceFieldValueById(java.lang.String, long)
	 */
	@Override
	public List getResourceFieldValueById(String fields, String contentIds) {
		String sql = "SELECT content_id," + fields + " FROM resource WHERE content_id IN(" + contentIds + ")";
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery(sql);
		// query.addScalar("content_id",StandardBasicTypes.LONG);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return results;
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(Integer limit, Integer offset) {
		String hql = "SELECT r.resource FROM ResourceInstance r";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		List<String> resourceInstance = query.list();
		return session.createQuery(hql).setFirstResult(limit * (offset - 1)).setMaxResults(limit).list();
	}

	@Override
	public Map<String, Object> getResourceCollectionInfo(long contentId) {
		String sql = "SELECT CONCAT_WS(\",\",GROUP_CONCAT(DISTINCT l.goals SEPARATOR \" , \") ,GROUP_CONCAT(DISTINCT l.vocabulary SEPARATOR\" , \"),GROUP_CONCAT(DISTINCT l.notes SEPARATOR \" , \"),GROUP_CONCAT(DISTINCT l.narration SEPARATOR \" , \")) as \"collection.classplanContent\",GROUP_CONCAT(DISTINCT l.lesson SEPARATOR \" , \") as \"collection.lesson\" FROM learnguide l WHERE l.content_id =:contentId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("contentId", contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return (Map<String, Object>) ((results.size() > 0) ? results.get(0) : null);
	}

	@Override
	public List<Map<String, Object>> getPartyPermissions(long contentId) {
		String sql = "select cp.party_uid as uId,p.party_name as name,p.party_type as type FROM content_permission cp INNER JOIN party p on p.party_uid = cp.party_uid WHERE cp.content_id=:contentId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("contentId", contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@Override
	public Map<String, Object> getContentSubscription(long contentId) {
		String sql = "select a.resource_id as subscribed_content_id ,count(*) as subscriberCount from content c inner join annotation a on a.resource_id = c.content_id where a.type_name='subscription' and c.content_id=:contentId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("contentId", contentId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List results = query.list();
		return (Map<String, Object>) ((results.size() > 0) ? results.get(0) : null);
	}

	@Override
	public List<Long> findValidContentIds(Class<?> modelClass, String ids) {
		String hql = "SELECT model.contentId  FROM " + modelClass.getSimpleName() + " model WHERE model.contentId in (" + ids + ")";
		Session session = getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		return query.list();

	}

	@Override
	public Textbook findTextbookByContentGooruIdWithNewSession(String gooruOid) {
		String hql = "SELECT textbook FROM Textbook textbook JOIN textbook.securityGroups sg WHERE textbook.gooruOid = '" + gooruOid + "' AND textbook.organization.partyUid IN ( " + getUserOrganizationUidsAsString() + ")";
		List<Textbook> result = getSession().createQuery(hql).list();
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
		return  (License)(query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public Resource findResourceByContent(String gooruOid) {
		List<Resource> resources = find("SELECT r FROM Resource r  where r.gooruOid ='" + gooruOid + "'");
		return resources.size() == 0 ? null : resources.get(0);
	}

}
