/////////////////////////////////////////////////////////////
// HibernateLogger.java
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
/**
 * 
 */
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Search Team
 * 
 */
public class HibernateLogger implements PostDeleteEventListener {
	
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4565236643778853926L;

	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateLogger.class);

	@PostConstruct
	public void register() {
		EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
		registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.hibernate.event.spi.PreDeleteEventListener#onPreDelete(org.hibernate
	 * .event.spi.PreDeleteEvent)
	 */
	@Override
	public void onPostDelete(PostDeleteEvent event) {
		final Serializable entityId = event.getPersister().hasIdentifierProperty() ? event.getPersister().getIdentifier(event.getEntity(), event.getSession()) : null;
		final String entityName = event.getPersister().getEntityName();
		final Date transTime = new Date();
		LOGGER.debug("{ \"operation\" : \"DELETE\"  \"entity\" : \"" + entityId + "\" \"type\" : \"" + entityName + "\" \"time\" : \"" + new SimpleDateFormat().format(transTime) +"\" \"userId\" : \"" + UserGroupSupport.getCurrentUserUid() + "\" \"eventName\" : \"" + UserGroupSupport.getLog().get("eventName")+ "\"");
	}

	@Override
	public boolean requiresPostCommitHanding(EntityPersister arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
