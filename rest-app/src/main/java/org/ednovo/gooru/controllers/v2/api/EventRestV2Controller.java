/////////////////////////////////////////////////////////////
//EventRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Event;
import org.ednovo.gooru.core.api.model.EventMapping;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.EventService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping(value = { "/v2/event" })
public class EventRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private EventService eventService;

	@Autowired
	private MailHandler mailHandler;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "")
	public ModelAndView createEvent(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Event event = getEventService().createEvent(this.buildEventFromInputParameters(data), user);
		response.setStatus(HttpServletResponse.SC_CREATED);
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(event, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{id}")
	public ModelAndView updateEvent(@PathVariable(value = ID) String id, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Event event = getEventService().updateEvent(id, this.buildEventFromInputParameters(data));
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(event, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getEvent(@PathVariable(value = ID) String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getEventService().getEvent(id), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "")
	public ModelAndView getEvents(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, 
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getEventService().getEvents(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAPPING_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/{id}/template-mapping")
	public ModelAndView createEventMapping(@PathVariable(value = ID) String id, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		EventMapping eventMapping = getEventService().createEventMapping(this.buildEventMappingFromInputParameters(data, id), user);
		response.setStatus(HttpServletResponse.SC_CREATED);
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_MAPPING_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(eventMapping, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAPPING_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/{id}/template-mapping")
	public ModelAndView updateEventMapping(@PathVariable(value = ID) String id, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		EventMapping eventMapping = getEventService().updateEventMapping(this.buildEventMappingFromInputParameters(data, id), user);
		response.setStatus(HttpServletResponse.SC_CREATED);
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_MAPPING_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(eventMapping, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAPPING_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/{id}/template-mapping")
	public void deleteEventMapping(@PathVariable(value = ID) String eventUid, @RequestParam(value = TEMPLATE_UID) String templateUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getEventService().deleteEventMapping(eventUid, templateUid);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAPPING_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/template-mapping")
	public ModelAndView getEventMapping(@PathVariable(value = ID) String eventUid, @RequestParam(value = TEMPLATE_UID) String templateUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(EVENT_MAPPING_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(this.getEventService().getEventMapping(eventUid, templateUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAIL_TRIGGER })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/trigger/mail")
	public void triggerMailEvent(@RequestParam(value = EVENT_TYPE, required = true) String eventType, @RequestParam(value = GOORU_UID, required = false) String gooruUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (gooruUid != null) {
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put(EVENT_TYPE, eventType);
			dataMap.put(GOORU_UID, gooruUid);
			getMailHandler().handleMailEvent(dataMap);
		} else {
			getMailHandler().handleMailEvent(eventType);
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_EVENT_MAIL_TRIGGER })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/trigger/jira")
	public void triggerMailEvent(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getEventService().handleJiraEvent(buildJiraEventFromInputParameters(data));
	}

	private Event buildEventFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Event.class);
	}

	private Map<String, String> buildJiraEventFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<Map<String, String>>() {
		});
	}

	private EventMapping buildEventMappingFromInputParameters(String data, String eventUid) {
		EventMapping eventMapping = JsonDeserializer.deserialize(data, EventMapping.class);
		Event event = new Event();
		event.setGooruOid(eventUid);
		eventMapping.setEvent(event);
		return eventMapping;
	}

	public EventService getEventService() {
		return eventService;
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}

}
