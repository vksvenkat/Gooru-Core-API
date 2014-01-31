/////////////////////////////////////////////////////////////
//PartyRestV2Controller.java
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
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

@Controller
@RequestMapping(value = { "/v2/party" })
public class PartyRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private PartyService partyservice;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/custom-field")
	public ModelAndView createPartyCustomField(@RequestBody String data, @PathVariable(value = ID) String partyId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<PartyCustomField> responseDTO = getPartyservice().createPartyCustomField(partyId, buildPartyCustomFieldFromInputParameters(getValue(PARTY_CUSTOM_FIELD, json)), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? PARTY_CUSTOM_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/default/{id}/custom-field")
	public ModelAndView createDefaultPartyCustomField(@RequestParam(value = TYPE, required = true, defaultValue = USER_TYPE) String type, @PathVariable(value = ID) String partyId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(PARTY_CUSTOM_INCLUDES, ERROR_INCLUDE);
		List<PartyCustomField> partyCustomFields = this.getPartyservice().createPartyDefaultCustomAttributes(partyId, user, type);
		if (partyCustomFields != null) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		return toModelAndViewWithIoFilter(partyCustomFields, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/custom-field")
	public ModelAndView updatePartyCustomField(@RequestBody String data, @PathVariable(value = ID) String partyId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<PartyCustomField> responseDTO = getPartyservice().updatePartyCustomField(partyId, buildPartyCustomFieldFromInputParameters(getValue(PARTY_CUSTOM_FIELD, json)), user);
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? PARTY_CUSTOM_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/custom-field/{optionalkey}")
	public ModelAndView getPartyCustomField(@PathVariable(value = ID) String partyId, @PathVariable(value = OPTIONAL_KEY) String optionalKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(getPartyservice().getPartyCustomeField(partyId, optionalKey, user), FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/custom-field")
	public ModelAndView getPartyCustomFields(@RequestParam(value = DATA_OBJECT, required = false) String data, @PathVariable(value = ID) String partyId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		List<PartyCustomField> partyCustomFields = getPartyservice().getPartyCustomFields(partyId, buildPartyCustomFieldFromInputParameters(getValue(PARTY_CUSTOM_FIELD, json)), user);
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? PARTY_CUSTOM_INCLUDES : includeFields, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(partyCustomFields, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_PARTY_CUSTOM_FIELD_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}/custom-field")
	public void deletePartyCustomField(@RequestBody String data, @PathVariable(value = ID) String partyId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		getPartyservice().deleteCustomField(partyId, buildPartyCustomFieldFromInputParameters(data), user);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private PartyCustomField buildPartyCustomFieldFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, PartyCustomField.class);
	}

	public PartyService getPartyservice() {
		return partyservice;
	}
}
