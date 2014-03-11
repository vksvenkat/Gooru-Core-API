/*
*LearnguideRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.classplan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;


@Repository("classplanRepository")
public class LearnguideRepositoryHibernate extends BaseRepositoryHibernate implements LearnguideRepository {

	private static final String SHARE_TYPE = "shareType";

	private static final String GOORU_COLLECTION_ID = "gooruCollectionId";

	private static final String RESOURCE_TYPE = "resourceType";

	private static final String PAGE_SIZE = "pageSize";

	private static final String PAGE_NO = "pageNum";


	private static final String LIST_LEARNGUIDE_BY_USER_ID = "Select l.contentId,l.lesson, l.gooruOid from Learnguide l where l.user.partyUid = ? AND "+ generateOrgAuthQuery("l.") +" order by l.lesson";

	private static final String LIST_LEARNGUIDE_BY_USER_AND_LEARNGUIDE_TYPE = "Select l.contentId,l.lesson, l.gooruOid from Learnguide l where l.user.partyUid = ?  AND  "+ generateOrgAuthQuery("l.") +" AND l.resourceType.name = :type order by l.lesson";

	private static final String LIST_CLASSPLAN = "Select l.gooruOid,l.lesson from Learnguide l  where l.resourceType.name = ? AND "+ generateAuthQuery("l.") +"order by l.lesson";

	private static final String PAGE_START = "startAt";
	

	public List<Object> findByUser(User user, ResourceType.Type type) {
		String hql = LIST_LEARNGUIDE_BY_USER_ID;
		if (type != null) {
			hql = LIST_LEARNGUIDE_BY_USER_AND_LEARNGUIDE_TYPE;
		}

		Query query = getSession().createQuery(hql);
		query.setParameter(0, user.getPartyUid(), StandardBasicTypes.STRING);
		addOrgAuthParameters(query);
		if (type != null) {
			query.setParameter("type", type.getType());
		}
		return query.list();
	}

	public List<Learnguide> findAllByUserAndResource(User user, ResourceType.Type type, Map<String, String> filters) {
		Session session = getSession();
		Criteria criteria = session.createCriteria(Learnguide.class);
		if (filters != null) {
			Integer pageNum = 1;
			if (filters.containsKey(PAGE_NO)) {
				pageNum = Integer.parseInt(filters.get(PAGE_NO).trim());
			}
			Integer pageSize = 10;
			if (filters.containsKey(PAGE_SIZE)) {
				pageSize = Integer.parseInt(filters.get(PAGE_SIZE).trim());
			}
			
			criteria.setFirstResult((pageNum) - 1 * pageSize);
			criteria.setMaxResults(pageSize);
			addAuthCriterias(criteria);
		}
		return criteria.list();
	}

	public List<Object> findAllLearnguides(ResourceType.Type type) {
		Query query = getSession().createQuery(LIST_CLASSPLAN);
		if (type != null) {
			query.setParameter(0, type.getType(), StandardBasicTypes.STRING);
		}
		addAuthParameters(query);
		return query.list();
	}

	public List<Learnguide> findRecentlyModifiedLearnguides() {

		String hql = "SELECT l from Learnguide l  where l.lastModified >= :date AND "+generateAuthQuery("l.");

		Query q = getSession().createQuery(hql);

		Calendar current = Calendar.getInstance();
		current.add(Calendar.DATE, -1); // reducing by one day
		q.setCalendarDate("date", current);
		addAuthParameters(q);
		return q.list();

	}

	@Override
	public List<Learnguide> findRecentLearnguideByUser(User user, String sharing) {
		Session session = this.getSession();
		Criteria criteria = session.createCriteria(Learnguide.class);

		List<Learnguide> list = null;
		addOrgAuthCriterias(criteria);
		if (sharing.equals(Sharing.PUBLIC.getSharing())){
			list = criteria.add(Restrictions.eq("user", user)).add(Restrictions.eq("sharing", sharing)).setMaxResults(5).addOrder(Order.desc("createdOn")).list();
		}
		else{
			list = criteria.add(Restrictions.eq("user", user)).setMaxResults(5).addOrder(Order.desc("createdOn")).list();
		}

		return list;
	}

	@Override
	public Learnguide findByContent(String gooruContentId) {
		List<Learnguide> classplanList = find("SELECT l from Learnguide l  where l.gooruOid =? AND "+generateAuthQueryWithDataNew("l."),gooruContentId);
		return classplanList.size() == 0 ? null : classplanList.get(0);
	}

	@Override
	public List<User> findCollaborators(String gooruContentId, String userUid) {

		List<User> userList = new ArrayList<User>();
		String find_collaborators = "Select u.user_id, u.gooru_uid, u.firstname, u.lastname, i.external_id,u.username, u.organization_uid, u.primary_organization_uid from user u, content c , content_permission p, identity i where gooru_oid = '" + gooruContentId + "' and p.permission = 'edit' and u.gooru_uid = i.user_uid and c.content_id = p.content_id and u.gooru_uid = p.party_uid " ;
		if (userUid != null) {
			find_collaborators += " and p.party_uid = '" + userUid+ "'";
		}
		
		Session session = getSession();
		Query query = session.createSQLQuery(find_collaborators).addScalar("user_id", StandardBasicTypes.INTEGER).addScalar("gooru_uid", StandardBasicTypes.STRING).addScalar("firstname", StandardBasicTypes.STRING).addScalar("lastname", StandardBasicTypes.STRING)
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
			if(organizationUid == null){
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

	public List<Learnguide> findAllClassplans() {
		Criteria criteria = getSession().createCriteria(Learnguide.class);
		
		return addAuthCriterias(criteria).list();
	}

	@Override
	public List<Learnguide> listLearnguides(Map<String, String> filters) {
		Session session = getSession();

		Integer featured = null;

		if (filters.containsKey("featured")) {
			featured = Integer.valueOf(filters.get("featured"));
		}

		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 50;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}
		if (filters.get("accessType") != null && filters.get("accessType").equals("my")) {
			String userUid = filters.get("userId");
			String sql = "Select distinct c.content_id as contentId from learnguide c inner join resource cr on ( c.content_id = cr.content_id ";
			if (featured != null) {
				sql += " AND cr.is_featured =  " + featured + " ";
			}
			sql += " ) inner join content cc on c.content_id = cc.content_id left join annotation a on c.content_id = a.resource_id left join content ac on a.content_id = ac.content_id left join content_permission cps on cc.content_id = cps.content_id where ((cc.user_uid = '"+userUid+"' and cc.type_name != 'annotation') and "+generateAuthSqlQueryWithData("cc.") ;

			if (Boolean.parseBoolean(filters.get("excludeRating"))) {
				sql += " OR ( a.type_name='subscription' and ac.user_uid = '"+userUid+"')) ";
			}
			if (filters.containsKey("status")) {
				sql += " AND cc.sharing = '" + filters.get("status") + "' AND cc.creator_uid = '" + userUid + "' ";
			}

			if (filters.containsKey("myGroup") && userUid != null) {
				String myGroup = filters.get("myGroup");
				if (myGroup.equals("1")) {
					sql += " AND c.content_id = " + userUid;
				}
			}
			
			sql += "order by coalesce ( ac.last_modified , cc.last_modified ) desc limit " + pageSize * (pageNum - 1) + " , " + pageSize + "";
			
			Query query = session.createSQLQuery(sql).addScalar("contentId", StandardBasicTypes.LONG);
            Integer startAt = Integer.parseInt(filters.get(PAGE_START));
			if(startAt == 0 ) {
				startAt = ((pageNum - 1) * pageSize); 
			}   else { 
				startAt = startAt - 1;
			}

			query.setFirstResult(startAt);
			List<Long> contentIds = query.list();
			StringBuffer contentIdBuffer = new StringBuffer();
			for (Long contentId : contentIds) {
				if (contentIdBuffer.length() > 0) {
					contentIdBuffer.append(",'" + contentId + "'");
				} else {
					contentIdBuffer.append("'" + contentId + "'");
				}
			}
			if (contentIdBuffer.length() > 1) {
				List<Learnguide> myCollections = getSession().createQuery("FROM Learnguide l WHERE l.contentId IN (" + contentIdBuffer.toString() + ") ").list();
				List<Learnguide> resultCollections = new ArrayList<Learnguide>();
				for (Long contentId : contentIds) {
					for (Learnguide collection : myCollections) {
						if (collection.getContentId().equals(contentId)) {
							resultCollections.add(collection);
							break;
						}
					}
				}

				return resultCollections;
			} else {
				return new ArrayList<Learnguide>();
			}

		} else {
			String hql = "SELECT distinct(learnguide) FROM Learnguide learnguide LEFT JOIN learnguide.taxonomySet taxonomySet  ";
			
			if(filters.containsKey("standards") && filters.get("standards")!=null){
				hql+=" INNER JOIN taxonomySet.associatedCodes assocCodes";
			}
			
			hql+=" WHERE 1 =1 ";
			
			if (filters.containsKey(RESOURCE_TYPE)) {
				String resourceType = filters.get(RESOURCE_TYPE);
				if (resourceType != null) {
					hql += " AND learnguide.resourceType.name = '"+resourceType+"'";
				}
			}
			if (filters.containsKey("isLive")) {
				String isLive = filters.get("isLive");
				if (isLive != null) {
					hql += " AND learnguide.isLive = '"+isLive+"'";
				}
			}
			if (featured != null) {
				hql += " AND learnguide.isFeatured = '"+featured+"'";
			}
			if (filters.containsKey("gooruOid")) {
				String gooruOid = filters.get("gooruOid");
				if (gooruOid != null) {
					Criteria taxCriteria = session.createCriteria(Learnguide.class);
					taxCriteria.add(Restrictions.eq("gooruOid", gooruOid));
					List<Assessment> taxAssessments = taxCriteria.list();
					String codes = "";
					if (taxAssessments != null && taxAssessments.size() > 0) {
						for (Code code : taxAssessments.get(0).getTaxonomySet()) {
							if(codes.length() == 0 ) {
								codes += "'"+code.getCode()+"',";
							} else {
								codes += "'"+code.getCode()+"'";
							}
						}
						hql += " AND taxonomySet.code = '"+codes+"'";
					}
				}
			}
			if (filters.containsKey("taxonomyParentId")) {
				String taxonomyParentIdString = filters.get("taxonomyParentId");
				try {
					Integer taxonomyParentId = Integer.valueOf(taxonomyParentIdString);
					hql += " AND taxonomySet.parentId = '"+taxonomyParentId+"'";
				} catch (NumberFormatException e) {
					// We're really not doing anything here since we don't
					// handle the invalid taxonomyParentId's passed.
					// TODO Validate to make sure this is an integer
				}
			}
			
			if(filters.containsKey("standards") && filters.get("standards")!=null){
				String[] standards = filters.get("standards").split(",");
				StringBuilder includesStandards = new StringBuilder();
				for(String standard:standards){
					if(includesStandards.length() > 0){
						includesStandards.append(",");
					}
					includesStandards.append("'"+standard+"'");
				}
				hql+=" AND assocCodes.code IN ("+includesStandards+")";
			}
			
			hql +=  " AND "+generateAuthQueryWithDataNew("learnguide.");
			
			String orderBy = filters.get("orderBy");
			if (orderBy != null && orderBy.equalsIgnoreCase("lesson")) {
				hql += " ORDER BY taxonomySet.label ASC ";
			}
			Query query = getSession().createQuery(hql);
			query.setFirstResult(((pageNum - 1) * pageSize));
			query.setMaxResults(pageSize);
			return query.list();
		
		}
	}

	@Override
	public List<Learnguide> listPublishedCollections(String userGooruId) {

		String hql = "SELECT collection FROM Learnguide collection   WHERE collection.isLive = '1' AND ( collection.user.partyUid = '" + userGooruId + "' OR collection.creator.partyUid = '" + userGooruId + "' ) AND "+generateAuthQueryWithDataNew("collection.");

		return getSession().createQuery(hql).setMaxResults(30).list();
	}

	@Override
	public List<ResourceInstance> listCollectionResourceInstances(Map<String, String> filters) {

		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));

		String hql = "SELECT instance FROM Learnguide collection  join collection.resourceSegments segment join segment.resourceInstances instance WHERE  collection.gooruOid = '" + filters.get(GOORU_COLLECTION_ID) + "'";

		if (filters.containsKey(SHARE_TYPE)) {
			String type = filters.get(SHARE_TYPE);
			hql += " AND instance.resource.sharing = '" + type + "' ";
			if (type.equals("private")) {

				hql += " AND ( instance.title is null OR instance.title = '' OR instance.description is null OR instance.description = '' OR instance.resource.thumbnail is null OR instance.resource.thumbnail = '' ) AND " + generateAuthQueryWithDataNew("collection.");

			}
		}

		return getSession().createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();

	}

	@Override
	public List<ResourceInstance> listCollectionResourceInstance(Map<String, String> filters) {

		String hql = "SELECT instance FROM Learnguide collection  join collection.resourceSegments segment join  segment.resourceInstances instance WHERE  collection.gooruOid = '" + filters.get(GOORU_COLLECTION_ID)
				+ "' AND segment.type NOT IN('assessment', 'suggestedstudy', 'homework') ";
		if (filters.get("sharing") != null) {
			hql += " AND instance.resource.sharing = '" + filters.get("sharing") + "' AND "+generateAuthQueryWithDataNew("collection.");
		}
		if (filters.get("resourceGooruOid") != null) {
			hql += " AND instance.resource.gooruOid = '" + filters.get("resourceGooruOid") + "'";
		}

		hql += " order by segment.sequence";

		return getSession().createQuery(hql).list();

	}

	@Override
	public List<Segment> listCollectionSegments(Map<String, String> filters) {
		String hql = "SELECT segment FROM Learnguide collection    join collection.resourceSegments segment WHERE  collection.gooruOid = '" + filters.get(GOORU_COLLECTION_ID)
				+ "' AND segment.type NOT IN('assessment', 'suggestedstudy', 'homework') AND "+generateAuthQueryWithDataNew("collection.")+" order by segment.sequence";
		return getSession().createQuery(hql).list();
	}

	public Segment reorderResourceInstace(Resource resource, String segmentId, String resourceInstanceId, String newResourceInstancePos, String newSegmentId) throws Exception {
		Segment sourceSegment = null;
		Segment targetSegment = null;
		for (Segment segment : resource.getResourceSegments()) {
			if (segment.getSegmentId().equals(segmentId)) {
				sourceSegment = segment;
			} else if (segment.getSegmentId().equals(newSegmentId)) {
				targetSegment = segment;
			}
			if (sourceSegment != null && targetSegment != null) {
				// Both segments identified.
				break;
			}
		}
		if (sourceSegment != null) {
			if (targetSegment == null) {
				targetSegment = sourceSegment;
			}
			ResourceInstance resourceInstanceToMove = null;
			ResourceInstance toResourceInstance = null;
			int toPosition = 0;
			int fromPosition = 0;
			for (ResourceInstance resourceInstance : sourceSegment.getResourceInstances()) {
				if (resourceInstanceId.equals(resourceInstance.getResourceInstanceId()) && resourceInstanceToMove == null) {
					resourceInstanceToMove = resourceInstance;
					break;
				}
			}
			
			if (sourceSegment.equals(targetSegment) && sourceSegment.getResourceInstances().size() > 1) {
				for (ResourceInstance segmentResourceAssoc : sourceSegment.getResourceInstances()) {
					if (newResourceInstancePos.equals(segmentResourceAssoc.getResourceInstanceId())) {
						toResourceInstance = segmentResourceAssoc;
						fromPosition = resourceInstanceToMove.getSequence();
						toPosition = segmentResourceAssoc.getSequence();
						if (fromPosition < toPosition) {
							toResourceInstance.setSequence(toPosition - 1);
						} else if (fromPosition > toPosition) {
							toResourceInstance.setSequence(toPosition + 1);
						}
						resourceInstanceToMove.setSequence(toPosition);
						break;
					}
				}

				if (fromPosition != 0 && toPosition != 0) {
					for (ResourceInstance resourceInstance : sourceSegment.getResourceInstances()) {
						int sequence = resourceInstance.getSequence();
						if (((sequence > fromPosition && sequence < toPosition) || (sequence < fromPosition && sequence > toPosition)) && !resourceInstance.equals(toResourceInstance)) {
							if (fromPosition < toPosition) {
								resourceInstance.setSequence(sequence - 1);
							} else if (fromPosition > toPosition) {
								resourceInstance.setSequence(sequence + 1);
							}
						}
					}
				}
			} else {

				toPosition = 0;
				fromPosition = resourceInstanceToMove.getSequence();
				int lastPosition = 0;
				// identify target resource instance
				for (ResourceInstance resourceInstance : targetSegment.getResourceInstances()) {
					int resourceInstanceSequence = resourceInstance.getSequence();
					if (lastPosition < resourceInstanceSequence) {
						lastPosition = resourceInstanceSequence;
					}
					if (newResourceInstancePos.equals(resourceInstance.getResourceInstanceId())) {
						toPosition = resourceInstanceSequence;
						break;
					}
				}

				if (toPosition == 0 && newResourceInstancePos.equals("last")) {
					// The resource is probably dragged into last position.
					toPosition = targetSegment.getResourceInstances().size() + 1;
				}

				// Move sequence of all items beyond the target resource
				// instance(inclusive) upwards
				for (ResourceInstance segmentResourceAssoc : targetSegment.getResourceInstances()) {
					int sequence = segmentResourceAssoc.getSequence();
					if (sequence >= toPosition) {
						segmentResourceAssoc.setSequence(sequence + 1);
					}
				}

				// Insert the resource instance (update segment)
				resourceInstanceToMove.setSequence(toPosition);
				resourceInstanceToMove.setSegment(targetSegment);
				sourceSegment.getResourceInstances().remove(resourceInstanceToMove);
				targetSegment.getResourceInstances().add(resourceInstanceToMove);

				// update sequence of all items in the source

				for (ResourceInstance segmentResourceAssoc : sourceSegment.getResourceInstances()) {
					int sequence = segmentResourceAssoc.getSequence();
					getLogger().debug("ri:" + segmentResourceAssoc.getTitle() + ":" + sequence);
					if (sequence > fromPosition) {
						segmentResourceAssoc.setSequence(sequence - 1);
						getLogger().debug("ri:" + segmentResourceAssoc.getTitle() + ":" + sequence + ":to:" + (sequence - 1));
					}
				}
			}
			this.save(resource);
			return sourceSegment;
		}
		return null;
	}

	@Override
	public List<String> getAssessmentQuestionConcept(String keyword) {
		String hql = "SELECT DISTINCT rs.title FROM Learnguide lg JOIN lg.resourceSegments rs   WHERE rs.title LIKE '%" + keyword + "%' AND lg.distinguish=1 AND "+generateAuthQueryWithDataNew("lg.");
		return (List<String>) find(hql);
	}

	@Override
	public List<Learnguide> findByResource(String gooruResourceContentId, String sharing) {
		Session session = getSession();
		String hql = "SELECT DISTINCT collection FROM Learnguide collection   join collection.resourceSegments segment join segment.resourceInstances resourceInstance WHERE  resourceInstance.resource.gooruOid = '" + gooruResourceContentId
				+ "' AND collection.sharing = '"+ sharing +"' AND "+generateAuthQueryWithDataNew("collection.");
		return session.createQuery(hql).list();
	}

	@Override
	public List<String> getResourceInstanceIds(String gooruContentId) {
		// FIXME Add account filter
		String list_of_resource_ids = "SELECT ri.resource_instance_id FROM resource_instance AS ri INNER JOIN segment AS s ON ri.segment_id = s.segment_id INNER JOIN content c ON c.content_id = s.resource_id  WHERE c.gooru_oid = '" + gooruContentId
				+ "' and s.type_name NOT IN('assessment', 'suggestedstudy', 'homework') and "+generateAuthSqlQueryWithData("c.")+" order by s.sequence, ri.sequence";
		Session session = getSession();
		Query query = session.createSQLQuery(list_of_resource_ids);
		
		return query.list();
	}

	@Override
	public String findCollectionNameByGooruOid(String gooruOid) {
		String hql = "SELECT collection.lesson FROM Learnguide collection  WHERE  collection.gooruOid = '" + gooruOid + "' AND "+generateAuthQueryWithDataNew("collection.");
		List<String> result = (List<String>) find(hql);
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public List<Resource> listCollectionResources(Map<String, String> filters) {

		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));

		String hql = "SELECT DISTINCT (instance.resource) FROM Learnguide collection  join collection.resourceSegments segment join segment.resourceInstances instance WHERE  collection.gooruOid = '" + filters.get(GOORU_COLLECTION_ID)
				+ "' AND segment.type NOT IN('assessment', 'suggestedstudy', 'homework') AND "+generateAuthQueryWithDataNew("collection.");
		if (filters.get(SHARE_TYPE) != null) {
			if (filters.get(SHARE_TYPE).equals(Sharing.ANYONEWITHLINK.getSharing())) {
				hql += " AND ( instance.resource.sharing = '" + filters.get(SHARE_TYPE) + "' OR instance.resource.sharing = '" + Sharing.ANYONEWITHLINK.getSharing() + "' ) ";
			} else {
				hql += " AND instance.resource.sharing = '" + filters.get(SHARE_TYPE) + "' ";
			}
		}

		Session session = getSession();

		if (filters.get("fetchRecordType") != null && filters.get("fetchRecordType").equals("all")) {
			return session.createQuery(hql).list();
		}

		return session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
	}


	@Override
	public List<Learnguide> listAllCollectionsWithoutGroups(Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		String hql = "SELECT learnguide FROM Learnguide learnguide";
		Session session = getSession();
		List<Learnguide> learnguideList = session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
		return (learnguideList.size() > 0) ? learnguideList : null;
	}

	@Override
	public List<Learnguide>  findAllCollectionByResourceID(String resourceId) {
		// TODO Auto-generated method stub
		Session session = getSession();
		String hql = "SELECT collection FROM Learnguide collection   join collection.resourceSegments segment join segment.resourceInstances resourceInstance WHERE  resourceInstance.resource.gooruOid = '" + resourceId
		+ "' AND "+generateAuthQueryWithDataNew("collection.");
		return session.createQuery(hql).list();

	}


	@Override
	public List<Learnguide> getUserCollectionInfo(String gooruUId,	Map<String, String> filters) {
		Integer pageNum = Integer.parseInt(filters.get(PAGE_NO));
		Integer pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		String sharing = filters.get("sharing");
		String hql = "FROM Learnguide l where l.user.partyUid ='"+gooruUId+"' and  " + generateOrgAuthQueryWithData("l.");
		if(!(StringUtils.isEmpty(sharing))){
			hql += " AND l.sharing='"+sharing+"'";
		}
		Session session = getSession();
		List<Learnguide> learnguideList = session.createQuery(hql).setFirstResult(pageSize * (pageNum - 1)).setMaxResults(pageSize).list();
		return (learnguideList.size() > 0) ? learnguideList : null;
	}

	@Override
	public List<String> findAllCollaboratorByResourceID(String gooruUId, String searchText) {
		Session session = getSession();
		String sql = "select distinct u.username from user u inner join  user_perm up on(u.user_id=up.user_id) inner join content c on (c.content_id=up.content_id) inner join user user  on user.user_id=c.user_id where  user.gooru_uid='"+gooruUId+"' AND u.username = '" + searchText + "'";
		return session.createSQLQuery(sql).list();
		
	}

}
