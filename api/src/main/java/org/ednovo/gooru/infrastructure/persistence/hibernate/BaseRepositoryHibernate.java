/////////////////////////////////////////////////////////////
// BaseRepositoryHibernate.java
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.core.security.AuthenticationDo;
import org.ednovo.gooru.domain.service.authentication.AccountServiceImpl;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class serves as the Base class for all other Daos - namely to hold
 * common methods that they might all use. Can be used for standard CRUD
 * operations.</p>
 */
public class BaseRepositoryHibernate extends AbstractRepositoryHibernate implements BaseRepository {

	@javax.annotation.Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;
	
	@javax.annotation.Resource(name = "sessionFactoryReadOnly")
	private SessionFactory sessionFactoryReadOnly;
	
	@Autowired
	private RevisionHistoryService revisionHistoryService;
	
	@Autowired
	private RedisService redisService; 
	
	private static final String SESSION_TOKEN_KEY = "authenticate_";


	@Override
	public void remove(Class clazz, Serializable id) {
		if (clazz.isAssignableFrom(Versionable.class)) {
			try {
				getRevisionHistoryService().createVersion(id.toString(), "Delete");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		delete(get(clazz, id));
	}

	@Override
	public void removeAll(Collection entities) {
		if (entities != null) {
			Iterator iterator = entities.iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof Versionable) {
					try {
						getRevisionHistoryService().createVersion((Versionable) object, "BulkDelete");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		deleteAll(entities);
	}
	
	@Override
	public AuthenticationDo getAuthenticationDo(String token) {
		String data = getRedisService().get(SESSION_TOKEN_KEY + token);
		AuthenticationDo  authentication = new AuthenticationDo();
		if (data != null) {
			authentication = JsonDeserializer.deserialize(data, AuthenticationDo.class);
		}
		return authentication;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
		public RevisionHistoryService getRevisionHistoryService() {
		return revisionHistoryService;
	}

	public void setRevisionHistoryService(RevisionHistoryService revisionHistoryService) {
		this.revisionHistoryService = revisionHistoryService;
	}
	
	public SessionFactory getSessionFactoryReadOnly() {
		return sessionFactoryReadOnly;
	}

	public RedisService getRedisService() {
		return redisService;
	}
}
