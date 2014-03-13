/////////////////////////////////////////////////////////////
// TaskRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CollectionTaskAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Task;
import org.ednovo.gooru.core.api.model.TaskAssoc;
import org.ednovo.gooru.core.api.model.TaskHistoryItem;
import org.ednovo.gooru.core.api.model.TaskResourceAssoc;
import org.ednovo.gooru.core.api.model.TaskUserAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepositoryHibernate extends BaseRepositoryHibernate implements TaskRepository, ParameterProperties, ConstantProperties {

	@Override
	public Task getTask(String gooruOid) {
		Session session = getSession();
		String hql = " FROM Task task WHERE  task.gooruOid=:gooruOid  and task.contentType.name=:task and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("task."));
		query.setParameter("gooruOid", gooruOid);
		query.setParameter(TASK, TASK);
		addOrgAuthParameters(query);
		return (Task) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public Task getTask(String gooruOid, String gooruUid) {
		Session session = getSession();
		String hql = " FROM Task task WHERE  task.gooruOid=:gooruOid  and task.creator.gooruUId =:gooruUid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("task."));
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("gooruUid", gooruUid);
		addOrgAuthParameters(query);
		return (Task) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Task> getTasks(Integer offset, Integer limit, Boolean skipPagination) {
		Session session = getSession();
		String hql = " select collectionTaskAssoc FROM CollectionTaskAssoc collectionTaskAssoc  where " + generateOrgAuthQuery("task.");
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		if (skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public List<CollectionTaskAssoc> getCollectionTaskAssoc(Integer offset, Integer limit, Boolean skipPagination, String taskGooruOid, String classpageId) {
		Session session = getSession();
		String hql = " FROM CollectionTaskAssoc collectionTaskAssoc where " + generateOrgAuthQuery("collectionTaskAssoc.task.");
		if (taskGooruOid != null) {
			hql += " and collectionTaskAssoc.task.gooruOid='" + taskGooruOid + "'";
		}
		if (classpageId != null) {
			hql += " and collectionTaskAssoc.collection.gooruOid='" + classpageId + "'";
		}
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public Long getCollectionClasspageAssocCount(String collectionId) {
		Session session = getSession();
		String sql = "select count(*) as count from classpage cc inner join resource car on car.content_id = cc.classpage_content_id inner join content cor on cor.content_id = car.content_id   inner join collection_item ci on cc.classpage_content_id = ci.collection_content_id inner join content c on c.content_id = ci.collection_content_id inner join resource r on  r.content_id = ci.resource_content_id inner join content cr on cr.content_id = r.content_id where cr.gooru_oid = '"
				+ collectionId + "' and ci.associated_by_uid != cr.user_uid";
		
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		return (Long) query.list().get(0);
	}

	@Override
	public CollectionTaskAssoc getCollectionTaskAssoc(String collectionId, String collectionTaskAssocId) {
		Session session = getSession();
		String hql = " FROM CollectionTaskAssoc collectionTaskAssoc WHERE  collectionTaskAssoc.collection.gooruOid=:collectionId and collectionTaskAssoc.collectionTaskAssocUid=:collectionTaskAssocUid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("collectionTaskAssoc.collection."));
		query.setParameter("collectionId", collectionId);
		query.setParameter("collectionTaskAssocUid", collectionTaskAssocId);
		addOrgAuthParameters(query);
		return (CollectionTaskAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Map<Object, Object>> getCollectionClasspageAssoc(String collectionId, String gooruUid) {
		Session session = getSession();
		String sql = "select car.title as title, cor.gooru_oid as classpageId from classpage cc inner join resource car on car.content_id = cc.classpage_content_id inner join content cor on cor.content_id = car.content_id   inner join collection_item ci on cc.classpage_content_id = ci.collection_content_id inner join content c on c.content_id = ci.collection_content_id inner join resource r on  r.content_id = ci.resource_content_id inner join content cr on cr.content_id = r.content_id where cr.gooru_oid='"
				+ collectionId + "'";
		if (gooruUid != null) {
			sql += "and ci.associated_by_uid='" + gooruUid + "'";
		}
		Query query = session.createSQLQuery(sql).addScalar("title", StandardBasicTypes.STRING).addScalar("classpageId", StandardBasicTypes.STRING);
		return getClasspageData(query.list());
	}

	@Override
	public void deleteCollectionAssocInAssignment(String collectionId) {
		Session session = getSession();
		String sql = "delete ci.* from collection_item ci inner join collection c on c.content_id = ci.resource_content_id inner join content cc on cc.content_id = c.content_id inner join collection ccc on ccc.content_id = ci.collection_content_id  where cc.gooru_oid =:collectionId and ccc.collection_type = 'classpage'";
		Query query = session.createSQLQuery(sql).setParameter("collectionId", collectionId);
		query.executeUpdate();
	}

	private List<Map<Object, Object>> getClasspageData(List<Object[]> results) {
		List<Map<Object, Object>> listClasspageTitle = new ArrayList<Map<Object, Object>>();
		for (Object[] object : results) {
			Map<Object, Object> average = new HashMap<Object, Object>();
			average.put("classpageId", object[1]);
			average.put("title", object[0]);
			listClasspageTitle.add(average);
		}
		return listClasspageTitle;
	}

	@Override
	public List<CollectionTaskAssoc> getCollectionTaskAssocs(String collectionId, String offset, String limit, String skipPagination, String orderBy) {
		Integer startAt = (offset != null) ? Integer.parseInt(offset) : OFFSET;
		Integer pageSize = (limit != null) ? Integer.parseInt(limit) : LIMIT;
		Session session = getSession();
		String hql = " FROM CollectionTaskAssoc collectionTaskAssoc WHERE   collectionTaskAssoc.collection.gooruOid=:collectionId and " + generateOrgAuthQuery("collectionTaskAssoc.collection.");
		if (orderBy != null && orderBy.equalsIgnoreCase(TITLE)) {
			hql += " order by collectionTaskAssoc.task.title ";
		} else {
			hql += " order by collectionTaskAssoc.task.createdOn desc ";
		}
		Query query = session.createQuery(hql);
		query.setParameter("collectionId", collectionId);
		addOrgAuthParameters(query);
		if (skipPagination == null || (skipPagination != null && skipPagination.equalsIgnoreCase(NO))) {
			query.setFirstResult(startAt).setMaxResults(pageSize);
		}
		return query.list();
	}

	public TaskResourceAssoc getTaskResourceAssocById(String taskGooruOid, String gooruOid) {
		Session session = getSession();
		String hql = " FROM TaskResourceAssoc tra WHERE tra.task.gooruOid =:taskGooruOid and tra.resource.gooruOid =:gooruOid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("tra.task."));
		query.setParameter("taskGooruOid", taskGooruOid);
		query.setParameter("gooruOid", gooruOid);
		addOrgAuthParameters(query);
		return (TaskResourceAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<String> getTaskResourceAssocs(String taskGooruOid) {
		Session session = getSession();
		String hql = "select tra.resource.gooruOid FROM TaskResourceAssoc tra WHERE tra.task.gooruOid =:taskGooruOid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("tra.task."));
		query.setParameter("taskGooruOid", taskGooruOid);
		addOrgAuthParameters(query);
		return query.list();
	}

	public TaskUserAssoc findByTaskUid(String gooruOid, String userUid) {
		Session session = getSession();
		String hql = " FROM TaskUserAssoc tua WHERE tua.task.gooruOid =:gooruOid and tua.user.partyUid =:userUid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("tua.task."));
		query.setParameter("gooruOid", gooruOid);
		query.setParameter("userUid", userUid);
		addOrgAuthParameters(query);
		return (TaskUserAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public TaskResourceAssoc getTaskResourceAssociatedItemId(String gooruOid, String resourceAssocId) {
		Session session = getSession();
		String hql = " FROM TaskResourceAssoc tra WHERE tra.taskResourceAssocUid =:resourceAssocId and ";
		if (gooruOid != null) {
			hql += "tra.task.gooruOid =:gooruOid and ";
		}
		Query query = session.createQuery(hql + generateOrgAuthQuery("tra.task."));
		query.setParameter("resourceAssocId", resourceAssocId);
		if (gooruOid != null) {
			query.setParameter("gooruOid", gooruOid);
		}
		addOrgAuthParameters(query);
		return (TaskResourceAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<TaskResourceAssoc> getTaskResourceId(String gooruOid, String resourceId) {
		Session session = getSession();
		String hql = " FROM TaskResourceAssoc tra WHERE tra.resource.gooruOid in (:resourceId) and ";
		if (gooruOid != null) {
			hql += "tra.task.gooruOid =:gooruOid and ";
		}

		Query query = session.createQuery(hql + generateOrgAuthQuery("tra.task."));
		if (resourceId != null) {
			query.setParameterList("resourceId", resourceId.split(","));
		}
		if (gooruOid != null) {
			query.setParameter("gooruOid", gooruOid);
		}
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<Resource> getTaskResourceAssociatedByTaskId(String gooruOid, Integer offset, Integer limit, String skipPagination, String orderBy, String sharing) {
		Session session = getSession();
		String hql = "select distinct(taskResourceAssocs.resource)  FROM Task task inner join task.taskResourceAssocs taskResourceAssocs where task.gooruOid=:gooruOid and " + generateOrgAuthQuery("task.");
		if (sharing != null) {
			hql += " and taskResourceAssocs.resource.sharing in (:sharing)";
		}

		if (orderBy != null && orderBy.equalsIgnoreCase(DATE)) {
			hql += " order by taskResourceAssocs.resource.createdOn desc";
		}
		if (orderBy != null && orderBy.equalsIgnoreCase(TITLE)) {
			hql += " order by taskResourceAssocs.resource.title";
		}
		Query query = session.createQuery(hql);
		query.setParameter("gooruOid", gooruOid);
		if (sharing != null) {
			query.setParameterList("sharing", sharing.split(","));
		}
		addOrgAuthParameters(query);
		if (skipPagination != null && skipPagination.equalsIgnoreCase(NO)) {
			query.setFirstResult(offset).setMaxResults(limit);

		}
		return query.list();
	}

	@Override
	public TaskAssoc getTaskAssocByUid(String taskAssocUid) {
		Session session = getSession();
		String hql = " FROM TaskAssoc taskAssoc WHERE taskAssoc.taskAssocUid =:taskAssocUid and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("taskAssoc.taskParent."));
		query.setParameter("taskAssocUid", taskAssocUid);
		addOrgAuthParameters(query);
		return (TaskAssoc) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<TaskHistoryItem> getTaskHistory(String taskGooruOid) {
		Session session = getSession();
		String hql = "SELECT distinct(taskHistoryItems) FROM TaskHistory taskHistory inner join taskHistory.taskHistoryItems taskHistoryItems where taskHistory.taskContentId.gooruOid=:taskGooruOid  and ";
		Query query = session.createQuery(hql + generateOrgAuthQuery("taskHistory.taskContentId."));
		query.setParameter("taskGooruOid", taskGooruOid);
		addOrgAuthParameters(query);
		return query.list();
	}

	public Long getTaskResourceCount(String taskGooruOid, String sharing) {
		Session session = getSession();
		String hql = "select count(*)  FROM TaskResourceAssoc taskResourceAssoc where " + generateOrgAuthQuery("taskResourceAssoc.task.");
		if (taskGooruOid != null) {
			hql += " and taskResourceAssoc.task.gooruOid='" + taskGooruOid + "'";
		}
		if (sharing != null) {
			hql += " and taskResourceAssoc.resource.sharing in (:sharing)";
		}

		Query query = session.createQuery(hql);
		if (sharing != null) {
			query.setParameterList("sharing", sharing.split(","));
		}
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getTaskCollectionCount(String taskGooruOid, String classpageId) {
		Session session = getSession();
		String hql = "select count(*)  FROM CollectionTaskAssoc collectionTaskAssoc WHERE " + generateOrgAuthQuery("collectionTaskAssoc.task.");
		if (taskGooruOid != null) {
			hql += " and collectionTaskAssoc.task.gooruOid='" + taskGooruOid + "'";
		}
		if (classpageId != null) {
			hql += " and collectionTaskAssoc.collection.gooruOid='" + classpageId + "'";
		}
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

	@Override
	public Long getCollectionTaskCount(String collectionGooruOid) {
		Session session = getSession();
		String hql = "select count(*)  FROM CollectionTaskAssoc collectionTaskAssoc WHERE " + generateOrgAuthQuery("collectionTaskAssoc.task.");
		if (collectionGooruOid != null) {
			hql += " and collectionTaskAssoc.collection.gooruOid = '" + collectionGooruOid + "'";
		}
		Query query = session.createQuery(hql);
		addOrgAuthParameters(query);
		return (Long) query.list().get(0);
	}

}
