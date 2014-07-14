/////////////////////////////////////////////////////////////
//CollaboratorRestV2Controller.java
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

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollaboratorService;
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
@RequestMapping(value = "v2/collaborator")
public class CollaboratorRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private CollaboratorService collaboratorService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/content/{id}" }, method = RequestMethod.POST)
	public ModelAndView addCollaborator(@PathVariable(ID) String gooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toJsonModelAndView(this.getCollaboratorService().addCollaborator(JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}), gooruOid, user, true), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/suggest" }, method = RequestMethod.GET)
	public ModelAndView collaboratorSuggest(@RequestParam(value = "query") String query, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getCollaboratorService().collaboratorSuggest(query, user.getGooruUId()), RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/content/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCollaborators(@PathVariable(ID) String gooruOid, @RequestParam(value = "groupByStatus", defaultValue = "false", required = false) Boolean groupByStatus, @RequestParam(value = "filterBy", required = false) String filterBy, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toJsonModelAndView(groupByStatus ? this.getCollaboratorService().getCollaboratorsByGroup(gooruOid, filterBy) : this.getCollaboratorService().getCollaborators(gooruOid, filterBy), true);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/content/{id}" }, method = RequestMethod.DELETE)
	public void deleteCollaborator(@PathVariable(ID) String gooruOid, @RequestParam String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getCollaboratorService().deleteCollaborator(gooruOid, JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		}));
	}

	public CollaboratorService getCollaboratorService() {
		return collaboratorService;
	}

}
