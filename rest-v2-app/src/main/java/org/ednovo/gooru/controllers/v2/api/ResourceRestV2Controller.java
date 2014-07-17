/////////////////////////////////////////////////////////////
//ResourceRestV2Controller.java
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

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.UpdateViewsDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.resource.ResourceService;
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

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping(value = { "/v2/resource" })
public class ResourceRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private ResourceService resourceService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED, noRollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "")
	public ModelAndView createResource(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "resource.create_resource");
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Resource> responseDTO = getResourceService().createResource(this.buildResourceFromInputParameters(getValue(RESOURCE, json), user), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateResource(@RequestBody String data, @PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "resource.update_resource");
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Resource> responseDTO = getResourceService().updateResource(resourceId, this.buildResourceFromInputParameters(getValue(RESOURCE, json)), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? RESOURCE_INCLUDE_FIELDS : includeFields, ERROR_INCLUDE);

		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getResource(HttpServletRequest request, @PathVariable(ID) String resourceId, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(this.getResourceService().getResource(resourceId), RESPONSE_FORMAT_JSON,EXCLUDE_ALL, true, RESOURCE_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/resource/list")
	public ModelAndView listResourcesUsedInCollections(@RequestParam(value = DATA_OBJECT, required = true) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		return toModelAndView(serialize(this.getResourceService().listResourcesUsedInCollections(getValue(LIMIT_FIELD, json), getValue(OFFSET_FIELD, json), user), RESPONSE_FORMAT_JSON));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public void deleteResource(@PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "resource.delete_resource");
		User user = (User) request.getAttribute(Constants.USER);
		Resource resource = (Resource) request.getAttribute(Constants.SEC_CONTENT);
		resourceService.deleteResource(resource, resourceId, user);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/collaborators/{id}", method = { RequestMethod.PUT })
	public ModelAndView addCollborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "collaborator", required = true) String collaboratorId) {
		User user = (User) request.getAttribute(Constants.USER);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, "add-collaborators");
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		return toModelAndViewWithIoFilter(this.getResourceService().addCollaborator(collectionId, user, collaboratorId, "add"), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = "/collaborators/{id}", method = RequestMethod.GET)
	public ModelAndView getCollaborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, "get-collaborators");
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		return toModelAndViewWithIoFilter(this.getResourceService().getCollaborators(collectionId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@RequestMapping(value = "/collaborators/{id}", method = RequestMethod.DELETE)
	public ModelAndView deleteCollaborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "collaborator", required = true) String collaboratorId) {
		User user = (User) request.getAttribute(Constants.USER);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, "delete-collaborators");
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());

		return toModelAndViewWithIoFilter(this.getResourceService().addCollaborator(collectionId, user, collaboratorId, DELETE), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = {RequestMethod.DELETE,RequestMethod.PUT}, value = "/{id}/taxonomy")
	public void deleteTaxonomyResource(@RequestBody String data, @PathVariable(value = ID) String resourceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "resource.delete_resource_taxonomy");
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		getResourceService().deleteTaxonomyResource(resourceId, this.buildResourceFromInputParameters(getValue(RESOURCE, json)), user);

		SessionContextSupport.putLogParameter(EVENT_NAME, "taxonomy-resource-delete");
		SessionContextSupport.putLogParameter(GOORU_OID, resourceId);
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());

		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/suggest/meta/info")
	public ModelAndView suggestResourceMetaData(@RequestParam(value = URL) String url, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = "fetchThumbnail", required = false, defaultValue = "false") boolean fetchThumbnail, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toModelAndView(serializeToJson(getResourceService().getSuggestedResourceMetaData(url, title, fetchThumbnail), true));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/media")
	public ModelAndView updateResourceImage(HttpServletRequest request, @PathVariable(ID) String resourceId, @RequestBody String data, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		return toModelAndView(serializeToJson(getResourceService().updateResourceImage(resourceId, getValue(FILENAME, json)), true));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = { "/{id}/play" })
	public ModelAndView getResourceSource(HttpServletRequest request, @PathVariable(value = ID) String gooruContentId, HttpServletResponse response, @RequestParam(value = "includeBrokenPdf", required = false, defaultValue = TRUE) Boolean includeBrokenPdf,
			@RequestParam(value = "more", required = false, defaultValue = TRUE) boolean more) throws Exception {
		request.setAttribute(PREDICATE, "resourceSource.get");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndView(serialize(getResourceService().resourcePlay(gooruContentId, apiCaller, more), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, RESOURCE_INCLUDE_FIELDS));

	}
	
	//@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE_VIEW })
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_BULK_UPDATE_VIEW})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/update/views")
	public void updateResourceViews(HttpServletRequest request, HttpServletResponse response, @RequestBody String data) throws Exception {
		List<UpdateViewsDTO> updateViewsDTOs = this.buildUpdatesViewFromInputParameters(data);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		this.getResourceService().updateViewsBulk(updateViewsDTOs, apiCaller);
	}
	
	private Resource buildResourceFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Resource.class);
	}
	
	private  List<UpdateViewsDTO> buildUpdatesViewFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<UpdateViewsDTO>>(){});
	}

	private Resource buildResourceFromInputParameters(String data, User user) {
		Resource resource = JsonDeserializer.deserialize(data, Resource.class);
		resource.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getResourceService().getContentType(ContentType.RESOURCE);
		resource.setContentType(contentType);
		resource.setLastModified(new Date(System.currentTimeMillis()));
		resource.setCreatedOn(new Date(System.currentTimeMillis()));
		if (!hasUnrestrictedContentAccess()) {
			resource.setSharing(Sharing.PUBLIC.getSharing());
		} else {
			resource.setSharing(resource.getSharing() != null && (resource.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? resource
					.getSharing() : Sharing.PUBLIC.getSharing());
		}
		resource.setUser(user);
		resource.setOrganization(user.getPrimaryOrganization());
		resource.setCreator(user);
		resource.setDistinguish(Short.valueOf("0"));
		resource.setRecordSource(NOT_ADDED);
		resource.setIsFeatured(0);
		resource.setLastUpdatedUserUid(user.getGooruUId());

		return resource;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}
	

}
