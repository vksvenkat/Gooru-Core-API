/*
*EventRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Event;
import org.ednovo.gooru.core.api.model.EventMapping;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepositoryHibernate extends BaseRepositoryHibernate implements EventRepository {

	@Override
	public Event getEvent(String id) {
		Session session = getSession();
		String hql = " FROM  Event event WHERE  event.gooruOid=:id";
		Query query = session.createQuery(hql);
		query.setParameter("id", id);
		return (Event) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public Event getEventByName(String name) {
		Session session = getSession();
		String hql = " FROM  Event event WHERE  event.name=:name";
		Query query = session.createQuery(hql);
		query.setParameter("name", name);
		return (Event) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Event> getEvents() {
		Session session = getSession();
		String hql = " FROM  Event event";
		Query query = session.createQuery(hql);
		return query.list();
	}

	@Override
	public EventMapping getEventMapping(String eventUid, String templateUid) {
		Session session = getSession();
		String hql = " FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.template.gooruOid=:templateUid and eventMapping.event.gooruOid=:eventUid";
		Query query = session.createQuery(hql);
		query.setParameter("eventUid", eventUid);
		query.setParameter("templateUid", templateUid);
		addOrgAuthParameters(query);
		return (EventMapping) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<EventMapping> getTemplatesByEvent(String eventUid) {
		Session session = getSession();
		String hql = " FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.event.gooruOid=:eventUid";
		Query query = session.createQuery(hql);
		query.setParameter("eventUid", eventUid);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public EventMapping getEventMappingByType(String eventType) {
		Session session = getSession();
		String hql = " FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.event.displayName=:eventType and eventMapping.status.value='active'";
		Query query = session.createQuery(hql);
		query.setParameter("eventType", eventType);
		addOrgAuthParameters(query);
		return (EventMapping) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

}
