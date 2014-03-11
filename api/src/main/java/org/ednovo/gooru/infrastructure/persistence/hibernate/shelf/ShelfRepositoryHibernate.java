/*
*ShelfRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.shelf;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.Shelf;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;


@Repository
public class ShelfRepositoryHibernate extends BaseRepositoryHibernate implements ShelfRepository {

	private String RETIREVE_SHELF_BY_SHELFID = "From Shelf s  where s.shelfId=:shelfId and  " + generateOrgAuthQuery("s.");

	private String RETIREVE_ALL_SHELF_BY_USER = "From Shelf s   where s.userId=:userId and  " + generateOrgAuthQuery("s.") + "  and s.shelfType=:shelfType";

	private String RETIREVE_ALL_SHELF_BY_USER_AND_ID = "From ShelfItem si where si.shelf.userId=:userId and si.resource.contentId=:contentId";

	private String RETIREVE_SHELF_BY_NAME = "From Shelf s   where s.name=:shelfName and s.userId=:userId and   " + generateOrgAuthQuery("s.");

	private String RETIREVE_SUB_SHELF = "From Shelf s  where s.shelfParentId=:shelfParentId and s.userId=:userId and   " + generateOrgAuthQuery("s.");

	private String RETIREVE_DEFAULT_SHELF_BY_USER = "From Shelf s   where  s.userId=:userId  and s.defaultFlag=:defaultFlag";

	private String RETIREVE_DEFAULT_SHELF_NAMES = "Select s.name From Shelf s   where  s.userId=:userId and  " + generateOrgAuthQuery("s.") + "  and s.name NOT IN (:name)";

	private String RETIREVE_SHELF_BY_NAME_AND_EXCLUDE_BY_ID = "From Shelf s   where s.name=:shelfName and s.userId=:userId and  " + generateOrgAuthQuery("s.") + "  and s.shelfId !=:shelfId";

	private String RETIREVE_SHELF_SUBSCRIBE_USER_LIST = "Select shelfItems From ShelfItem shelfItems join shelfItems.resource resource   where resource.gooruOid=:gooruOid and shelfItems.addedType=:addedType  and  " + generateOrgAuthQuery("shelfItems.shelf.");

	
	private String RETIREVE_SHELF_SUBSCRIBE_USER = "From ShelfItem shelfItems   where shelfItems.resource.gooruOid=:gooruOid  and shelfItems.shelf.userId=:gooruUid  and  " + generateOrgAuthQuery("shelfItems.shelf.");

	@Override
	public Shelf findShelfByShelfId(String shelfId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SHELF_BY_SHELFID);
		query.setParameter("shelfId", shelfId);
		addOrgAuthParameters(query);
		List<Shelf> shelfs = query.list();
		return (shelfs.size() > 0) ? shelfs.get(0) : null;
	}

	@Override
	public List<Shelf> listShelf(Map<String, String> parameters) {

		StringBuilder hql = new StringBuilder("SELECT distinct(shelf) FROM Shelf shelf ");
		hql.append(" where  " + generateOrgAuthQueryWithData("shelf."));

		if (parameters.containsKey("shelfUid")) {
			String shelfUid = parameters.get("shelfUid");
			hql.append(" AND shelf.shelfId='").append(shelfUid).append("'");
		} else {
			hql.append(" AND shelf.shelfParentId IS NULL");
		}

		if (parameters.containsKey("userId")) {
			String userId = parameters.get("userId");
			hql.append(" AND shelf.userId='").append(userId).append("'");
		}
		if (parameters.containsKey("fetchAll") && parameters.get("fetchAll").equalsIgnoreCase("0")) {
			hql.append(" AND shelf.activeFlag=1 ");
		}
		hql.append(" order by shelf.name");

		List<Shelf> shelfs = getSession().createQuery(hql.toString()).list();

		return shelfs;
	}

	@Override
	public List<ShelfItem> listShelfItem(Map<String, String> parameters) {

		Integer pageNum = 1;
		if (parameters != null && parameters.containsKey("pageNum")) {
			pageNum = Integer.parseInt(parameters.get("pageNum"));
		}
		Integer pageSize = 10;
		if (parameters != null && parameters.containsKey("pageSize")) {
			try {
				pageSize = Integer.parseInt(parameters.get("pageSize"));
			} catch (Exception e) {
				pageSize = Integer.parseInt(StringUtils.substringBefore(parameters.get("pageSize"), ",8"));
			}
		}
		Integer startAt = (parameters.containsKey("startAt") && parameters.get("startAt") != null) ? Integer.parseInt(parameters.get("startAt")) : 0;
		if (pageSize > 30) {
			pageSize = 30;
		}
		StringBuilder hql = new StringBuilder("SELECT shelfItem FROM ShelfItem shelfItem ");
		hql.append(" where " + generateOrgAuthQueryWithData("shelfItem.shelf."));

		if (parameters.containsKey("shelfUid") && parameters.get("shelfUid") != null) {
			String shelfUid = parameters.get("shelfUid");
			hql.append(" AND shelfItem.shelf.shelfId='").append(shelfUid).append("'");
		}

		if (parameters.containsKey("userId")) {
			String userId = parameters.get("userId");
			hql.append(" AND shelfItem.shelf.userId='").append(userId).append("'");
		}

		if (parameters.containsKey("resourceType")) {
			String resourceType = parameters.get("resourceType");
			String type = parameters.get("type");
			if (!resourceType.equalsIgnoreCase("all")) {
				if (resourceType.equalsIgnoreCase("resource")) {
					hql.append(" AND shelfItem.resource.resourceType.name NOT IN ( ").append(type).append(") ");
				} else {
					hql.append(" AND shelfItem.resource.resourceType.name  IN ( ").append(type).append(") ");
				}

			}
		}
		hql.append(" AND shelfItem.shelf.activeFlag=1");
		if (parameters.containsKey("addedType")) {
			String addedType = parameters.get("addedType");
			hql.append(" AND shelfItem.addedType = '").append(addedType).append("'");
		}

		String orderBy = parameters.get("orderBy");
		if (orderBy != null) {
			if (orderBy.equalsIgnoreCase("recent")) {
				hql.append(" order by shelfItem.lastActivityOn desc");
			} else if (orderBy.equalsIgnoreCase("title")) {
				hql.append(" order by shelfItem.resource.title");
			}
		}
		List<ShelfItem> shelfs = null;
		if (parameters.containsKey("skipPagination") && parameters.get("skipPagination") != null && parameters.get("skipPagination").equalsIgnoreCase("1")) {
			shelfs = getSession().createQuery(hql.toString()).list();
		} else {
			if (startAt == 0) {
				startAt = ((pageNum - 1) * pageSize);
			} else {
				startAt = startAt - 1;
			}
			shelfs = getSession().createQuery(hql.toString()).setFirstResult(startAt).setMaxResults(pageSize).list();
		}
		return shelfs;

	}

	@Override
	public List<Shelf> findAllShelfByUser(String gooruUId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_ALL_SHELF_BY_USER);
		query.setParameter("userId", gooruUId);
		query.setParameter("shelfType", "Shelf");
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<ShelfItem> findAllShelfByUserAndId(String userId, Long contentId) {
		Query query = getSession().createQuery(RETIREVE_ALL_SHELF_BY_USER_AND_ID);
		query.setParameter("userId", userId);
		query.setParameter("contentId", contentId);
		return query.list();
	}

	@Override
	public Shelf findShelfByName(String name, String gooruUId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SHELF_BY_NAME);
		query.setParameter("shelfName", name);
		query.setParameter("userId", gooruUId);
		addOrgAuthParameters(query);
		List<Shelf> shelfs = query.list();
		return (shelfs.size() > 0) ? shelfs.get(0) : null;
	}

	@Override
	public List<Shelf> findSubShelfByShelfId(String shelfId, String gooruUId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SUB_SHELF);
		query.setParameter("shelfParentId", shelfId);
		query.setParameter("userId", gooruUId);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public Shelf updateShelfItem(String sourceShelfId, String targetShelfId, Long contentId) {
		Session session = getSession();
		String hql = "UPDATE ShelfItem si set si.shelf.shelfId=:targetShelfId where si.resource.contentId =:contentId and si.shelf.shelfId=:sourceShelfId";
		Query query = session.createQuery(hql);
		query.setParameter("targetShelfId", targetShelfId);
		query.setParameter("contentId", contentId);
		query.setParameter("sourceShelfId", sourceShelfId);
		query.executeUpdate();
		return this.findShelfByShelfId(targetShelfId);
	}

	@Override
	public int deleteShelfEntry(String shelfId, Long contentId) {
		String hql = "DELETE FROM ShelfItem s  WHERE s.shelf.shelfId =:shelfId and s.resource.contentId = :contentId";
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("shelfId", shelfId);
		query.setParameter("contentId", contentId);
		return query.executeUpdate();
	}

	@Override
	public Shelf getDefaultShelf(String gooruUId) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_DEFAULT_SHELF_BY_USER);
		query.setParameter("userId", gooruUId);
		query.setParameter("defaultFlag", true);
		List<Shelf> shelfs = query.list();
		return (shelfs.size() > 0) ? shelfs.get(0) : null;
	}

	@Override
	public List<String> getShelfNames(String gooruUId, List<String> suggest) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_DEFAULT_SHELF_NAMES);
		query.setParameter("userId", gooruUId);
		query.setParameterList("name", suggest);
		addOrgAuthParameters(query);
		List<String> shelfName = query.list();
		return (shelfName.size() > 0) ? shelfName : null;
	}

	@Override
	public Shelf findShelfByNameExcludeById(String name, String shelfId, String gooruUid) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SHELF_BY_NAME_AND_EXCLUDE_BY_ID);
		query.setParameter("shelfName", name);
		query.setParameter("userId", gooruUid);
		query.setParameter("shelfId", shelfId);
		addOrgAuthParameters(query);
		List<Shelf> shelfs = query.list();
		return (shelfs.size() > 0) ? shelfs.get(0) : null;
	}

	@Override
	public List<ShelfItem> getShelfSubscribeUserList(String gooruOid) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SHELF_SUBSCRIBE_USER_LIST);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("addedType", ShelfType.AddedType.SUBSCRIBED.getAddedType());
		addOrgAuthParameters(query);
		List<ShelfItem> shelfItem = query.list();
		return (shelfItem.size() > 0) ? shelfItem : null;
	}

	@Override
	public List<ShelfItem> getShelfContentByUser(String gooruUid, String gooruOid) {
		Session session = getSession();
		Query query = session.createQuery(RETIREVE_SHELF_SUBSCRIBE_USER);
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		List<ShelfItem> shelfItem = query.list();
		return (shelfItem.size() > 0) ? shelfItem : null;
	}

	@Override
	public void updateShelfFolderNameForNewUser(User user) {
		Session session = getSession();
		String hql = "update Shelf s  set s.name=:folderName where s.userId=:userId and s.name like '%s Favorites' and " + generateOrgAuthQuery("s.");
		Query query = session.createQuery(hql);
		query.setParameter("folderName", user.getUsername() + Constants.SHELF_DEFAULT_NAME);
		query.setParameter("userId", user.getPartyUid());
		addOrgAuthParameters(query);
		query.executeUpdate();

	}

}
