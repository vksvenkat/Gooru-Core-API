/////////////////////////////////////////////////////////////
// RevisionHistoryServiceImpl.java
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
package org.ednovo.gooru.domain.service.revision_history;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.dao.EntityCassandraDao;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.domain.cassandra.ApiCassandraFactory;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.revision_history.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles the versioning of entities
 * 
 * @author Search Team
 * 
 */
@Service("revisionHistoryService")
public class RevisionHistoryServiceImpl implements RevisionHistoryService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private ApiCassandraFactory apiCassandraFactory;

	private EntityCassandraDao<RevisionHistory> revisionHistoryCassandraDao;

	@PostConstruct
	protected void init() {
		revisionHistoryCassandraDao = (EntityCassandraDao<RevisionHistory>) apiCassandraFactory.get(ColumnFamilyConstant.REVISION_HISTORY);
	}

	private static final Logger logger = LoggerFactory.getLogger(RevisionHistoryServiceImpl.class);

	@Override
	public RevisionHistory createVersion(String entityId, String onEvent) throws Exception {
		Resource resource = getResourceRepository().findResourceByContentGooruId(entityId);
		if (resource instanceof Versionable) {
			return createVersion((Versionable) resource, onEvent);
		} else {
			throw new Exception("Invalid Entity Type");
		}
	}

	@Override
	public RevisionHistory createVersion(Versionable versionable, String onEvent) throws Exception {
		RevisionHistoryType type = null;
		if (versionable == null) {
			throw new Exception("Versionable cannot be null");
		}
		if (versionable instanceof Learnguide) {
			type = RevisionHistoryType.LEARNGUIDE;
		} else if (versionable instanceof Assessment) {
			type = RevisionHistoryType.QUIZ;
		} else if (versionable instanceof Collection) {
			type = RevisionHistoryType.COLLECTION;
		} else {
			throw new Exception("Entity Not Supported For Versioning");
		}
		return createVersion(versionable, type, onEvent,null);
	}
	
	@Override
	public RevisionHistory createVersion(Versionable versionable, String onEvent,String gooruUid) throws Exception {
		RevisionHistoryType type = null;
		if (versionable == null) {
			throw new Exception("Versionable cannot be null");
		}
		if (versionable instanceof Learnguide) {
			type = RevisionHistoryType.LEARNGUIDE;
		} else if (versionable instanceof Assessment) {
			type = RevisionHistoryType.QUIZ;
		} else if (versionable instanceof Collection) {
			type = RevisionHistoryType.COLLECTION;
		} else {
			throw new Exception("Entity Not Supported For Versioning");
		}
		return createVersion(versionable, type, onEvent,gooruUid);
	}
	
	@Override
	public final RevisionHistory createVersion(Versionable entity, RevisionHistoryType type, String onEvent,String gooruUid) {
		RevisionHistory history = new RevisionHistory();
		history.setData(RevisionHistorySerializer.getSerializer(type.getValue()).serialize(entity));
		history.setTime(new Date());
		if(gooruUid != null) {
			history.setUserUid(gooruUid);
		} else {
			history.setUserUid(UserGroupSupport.getUserCredential().getUserUid());
		}
		history.setOnEvent(onEvent);
		history.setEntityUid(entity.getEntityId());
		history.setEntityName(type.getValue());
		history.setRevisionHistoryUid(UUID.randomUUID().toString());
		revisionHistoryCassandraDao.save(history);
		return history;
	}

	@Override
	public final Versionable rollbackByRevision(String revisionHistoryUid) {
		RevisionHistory revisionHistory = revisionHistoryCassandraDao.read(revisionHistoryUid);
		if (revisionHistory != null) {
			try {
				createVersion(revisionHistory.getEntityUid(), "PreBackup-" + revisionHistoryUid);
			} catch (Exception ex) {
				logger.error("Cannot create pre-backup : " + ex.getMessage());
			}
			Versionable versionable = RevisionHistoryDeserializer.getDeserializer(revisionHistory.getEntityName()).deserialize(revisionHistory.getData());
			return RevisionHistoryRollBack.getRollBack(revisionHistory.getEntityName()).rollback(versionable, revisionHistory);
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public final Versionable rollbackByLastKnownHistory(String entityUid) {
		RevisionHistory revisionHistory = null;
		if (revisionHistory == null) {
			throw new RuntimeException("Yet to be implemented.");
		}
		try {
			createVersion(revisionHistory.getEntityUid(), "PreBackup-" + revisionHistory.getRevisionHistoryUid());
		} catch (Exception ex) {
			logger.error("Cannot create pre-backup : " + ex.getMessage());
		}
		return RevisionHistoryDeserializer.getDeserializer(revisionHistory.getEntityName()).deserialize(revisionHistory.getData());
	}

	@Override
	public PageWrapper<RevisionHistory> listRevisionHistory(Map<String, Object> parameters) {
		// FIXME
		return null;
	}

	@Override
	public RevisionHistory getRevisionHistory(String revisionHistoryUid) {
		return revisionHistoryCassandraDao.read(revisionHistoryUid);
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

}
