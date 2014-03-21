/////////////////////////////////////////////////////////////
// EventServiceImpl.java
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
package org.ednovo.gooru.domain.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Event;
import org.ednovo.gooru.core.api.model.EventMapping;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.Template;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.infrastructure.jira.SOAPClient;
import org.ednovo.gooru.infrastructure.jira.SOAPSession;
import org.ednovo.gooru.infrastructure.persistence.hibernate.EventRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.TemplateRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl extends BaseServiceImpl implements EventService, ParameterProperties, ConstantProperties {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private SOAPClient soapClient;

	@Autowired
	private SOAPSession soapSession;

	@Autowired
	private FeedbackService feedbackService;

	private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

	@Override
	public Event createEvent(Event event, User user) {
		rejectIfNull(event.getName(), GL0006, EVENT__NAME);
		Event eventData = this.getEventRepository().getEventByName(event.getName());
		if (eventData != null) {
			throw new UnauthorizedException("Event name already exists.");
		}
		event.setCreator(user);
		event.setCreatedDate(new Date(System.currentTimeMillis()));
		event.setDisplayName(event.getName().replace(" ", "_"));
		this.getEventRepository().save(event);
		return event;
	}

	@Override
	public Event updateEvent(String id, Event newEvent) {
		Event event = this.getEventRepository().getEvent(id);
		rejectIfNull(event, GL0056, EVENT);
		if (newEvent.getName() != null) {
			event.setName(newEvent.getName());
			event.setDisplayName(newEvent.getName().replace(" ", "_"));
		}
		this.getEventRepository().save(event);
		return event;
	}

	@Override
	public Event getEvent(String id) {
		return this.getEventRepository().getEvent(id);
	}

	@Override
	public void deleteEvent(String id) {
		Event event = this.getEventRepository().getEvent(id);
		if (event != null) {
			this.getEventRepository().remove(event);
		}
	}

	@Override
	public EventMapping createEventMapping(EventMapping eventMapping, User user) {
		rejectIfNull(eventMapping.getEvent(),GL0006, EVENT_ID);
		rejectIfNull(eventMapping.getEvent().getGooruOid(), GL0006,EVENT_ID);
		rejectIfNull(eventMapping.getTemplate(), GL0006, TEMPLATE_ID);
		rejectIfNull(eventMapping.getTemplate().getGooruOid(),GL0006, TEMPLATE_ID);
		Event event = this.getEvent(eventMapping.getEvent().getGooruOid());
		rejectIfNull(event, GL0056, EVENT);
		Template template = this.getTemplateRepository().getTemplate(eventMapping.getTemplate().getGooruOid());
		rejectIfNull(template, GL0056, TEMPLATE);
		eventMapping.setAssociatedBy(user);
		eventMapping.setCreatedDate(new Date(System.currentTimeMillis()));
		eventMapping.setTemplate(template);
		eventMapping.setEvent(event);
		CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.EVENT_STATUS.getTable(),
				(eventMapping.getStatus() != null && eventMapping.getStatus().getValue() != null) ? eventMapping.getStatus().getValue() : CustomProperties.EventStatus.IN_ACTIVE.getStatus());
		rejectIfNull(customTableValue, GL0056, EVENT_STATUS);
		eventMapping.setStatus(customTableValue);
		this.getEventRepository().save(eventMapping);
		return eventMapping;
	}

	@Override
	public EventMapping updateEventMapping(EventMapping newEventMapping, User user) {
		rejectIfNull(newEventMapping.getEvent(),GL0006, EVENT_ID);
		rejectIfNull(newEventMapping.getEvent().getGooruOid(), GL0006,EVENT_ID);
		rejectIfNull(newEventMapping.getTemplate(), GL0006, TEMPLATE_ID);
		rejectIfNull(newEventMapping.getTemplate().getGooruOid(), GL0006,TEMPLATE_ID);
		Event event = this.getEvent(newEventMapping.getEvent().getGooruOid());
		rejectIfNull(event, GL0056, EVENT);
		Template template = this.getTemplateRepository().getTemplate(newEventMapping.getTemplate().getGooruOid());
		rejectIfNull(template, GL0056, TEMPLATE);
		EventMapping eventMapping = this.getEventMapping(newEventMapping.getEvent().getGooruOid(), newEventMapping.getTemplate().getGooruOid());
		rejectIfNull(eventMapping, GL0056, EVENT_MAPPING);
		if (newEventMapping.getData() != null) {
			eventMapping.setData(newEventMapping.getData());
		}
		if (newEventMapping.getStatus() != null && newEventMapping.getStatus().getValue() != null) {
			CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.EVENT_STATUS.getTable(), newEventMapping.getStatus().getValue());
			rejectIfNull(customTableValue,GL0056, EVENT_STATUS);
			eventMapping.setStatus(customTableValue);
		}
		return eventMapping;
	}

	@Override
	public void deleteEventMapping(String eventUid, String templateUid) {
		EventMapping eventMapping = this.getEventMapping(eventUid, templateUid);
		if (eventMapping != null) {
			this.getEventRepository().remove(eventMapping);
		}
	}

	@Override
	public EventMapping getEventMapping(String eventUid, String templateUid) {
		return this.getEventRepository().getEventMapping(eventUid, templateUid);
	}

	@Override
	public List<Event> getEvents() {
		return this.getEventRepository().getEvents();
	}

	@Override
	public List<EventMapping> getTemplatesByEvent(String id) {
		return this.getEventRepository().getTemplatesByEvent(id);
	}

	@Override
	public EventMapping getTemplatesByEventName(String name) {
		return this.getEventRepository().getEventMappingByType(name);
	}

	@Override
	public void handleJiraEvent(Map<String, String> fields) {
		String issueKey = sendMessageToJira(fields);
		if (issueKey != null && fields != null && fields.get(EVENT_TYPE) != null) {
			if (fields.get(EVENT_TYPE).equals(FEEDBACK)) {
				Feedback newFeedback = new Feedback();
				newFeedback.setReferenceKey(issueKey);
				this.getFeedbackService().updateFeedback(fields.get(IDS), newFeedback,null);			
			}
		}
	}

	private String sendMessageToJira(Map<String, String> standardJiraFields) {
		String issueKey = null;
		try {
			issueKey = this.getSoapClient().createIssue(this.getSoapSession(), null, standardJiraFields);
		} catch (Exception e) {
			logger.debug("Error while connecting JIRA", e);
		}
		return issueKey;
	}

	public EventRepository getEventRepository() {
		return eventRepository;
	}

	public TemplateRepository getTemplateRepository() {
		return templateRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public SOAPClient getSoapClient() {
		return soapClient;
	}

	public SOAPSession getSoapSession() {
		return soapSession;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}
}
