/////////////////////////////////////////////////////////////
// EventRepositoryHibernate.java
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

import java.util.List;

import org.ednovo.gooru.core.api.model.Event;
import org.ednovo.gooru.core.api.model.EventMapping;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepositoryHibernate extends BaseRepositoryHibernate implements EventRepository {

	private static final String GET_EVENT = "FROM  Event event WHERE  event.gooruOid=:id";
	private static final String GET_EVENT_BY_NAME = "FROM  Event event WHERE  event.name=:name";
	private static final String GET_EVENTS = "FROM  Event event";
	private static final String GET_EVENT_MAPPING = "FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.template.gooruOid=:templateUid and eventMapping.event.gooruOid=:eventUid";
	private static final String GET_TEMPLATE_BY_EVENT = "FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.event.gooruOid=:eventUid";
	private static final String GET_EVENT_MAPPING_TYPE = "FROM  EventMapping eventMapping WHERE " + generateOrgAuthQuery("eventMapping.template.") + " and eventMapping.event.displayName=:eventType and eventMapping.status.value='active'";
	
	@Override
	public Event getEvent(String id) {
		Query query = getSessionReadOnly().createQuery(GET_EVENT);
		query.setParameter("id", id);
		return (Event) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public Event getEventByName(String name) {
		Query query = getSessionReadOnly().createQuery(GET_EVENT_BY_NAME);
		query.setParameter("name", name);
		return (Event) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<Event> getEvents(Integer limit, Integer offset) {
		Query query = getSessionReadOnly().createQuery(GET_EVENTS);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	@Override
	public EventMapping getEventMapping(String eventUid, String templateUid) {
		Query query = getSessionReadOnly().createQuery(GET_EVENT_MAPPING);
		query.setParameter("eventUid", eventUid);
		query.setParameter("templateUid", templateUid);
		addOrgAuthParameters(query);
		return (EventMapping) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public List<EventMapping> getTemplatesByEvent(String eventUid) {
		Query query = getSessionReadOnly().createQuery(GET_TEMPLATE_BY_EVENT);
		query.setParameter("eventUid", eventUid);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public EventMapping getEventMappingByType(String eventType) {
		Query query = getSessionReadOnly().createQuery(GET_EVENT_MAPPING_TYPE);
		query.setParameter("eventType", eventType);
		addOrgAuthParameters(query);
		return (EventMapping) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);
	}

}
