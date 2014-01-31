/////////////////////////////////////////////////////////////
//TagRestV2Controller.java
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
import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Tag;
import org.ednovo.gooru.core.api.model.TagSynonyms;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserTagAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.tag.TagService;
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

@Controller
@RequestMapping(value = { "/v2/tag" })
public class TagRestV2Controller extends BaseController implements ParameterProperties, ConstantProperties {

	@Autowired
	private TagService tagService;

	@Autowired
	private CollectionService collectionService;

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public void setCollectionService(CollectionService collectionService) {
		this.collectionService = collectionService;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createTag(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.add_resource");
		User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Tag> tag = getTagService().createTag(this.buildTagFromInputParameters(data), user);
		if (tag.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "create-tag");
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		String includes[] = (String[]) ArrayUtils.addAll(TAG_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(tag.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateTag(@PathVariable(ID) String gooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.update");

		User user = (User) request.getAttribute(Constants.USER);
		Tag tag = this.getTagService().updateTag(gooruOid, this.buildTagFromInputParameters(data), user);
		if (tag == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, "update-tag");
		SessionContextSupport.putLogParameter("tagUid", gooruOid);
		return toModelAndViewWithIoFilter(tag, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public ModelAndView getTag(@PathVariable(ID) String gooruOid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		List<Tag> tag = this.getTagService().getTag(gooruOid);
		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using tagUid");
		SessionContextSupport.putLogParameter(TAG_ID, gooruOid);
		return toModelAndViewWithIoFilter(tag, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getTags(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");

		return toModelAndViewWithIoFilter(this.getTagService().getTags(offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteTag(HttpServletRequest request, HttpServletResponse response, @PathVariable(ID) String gooruOid) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");

		this.getTagService().deleteTag(gooruOid);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/content")
	public ModelAndView getTagContentAssoc(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @PathVariable(ID) String tagGooruOid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		List<ContentTagAssoc> contentTagAssocs = this.getTagService().getTagContentAssoc(tagGooruOid, limit, offset);

		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using gooruOid");
		SessionContextSupport.putLogParameter(TAG_GOORU_OID, tagGooruOid);
		return toModelAndViewWithIoFilter(contentTagAssocs, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CONTENT_ASSOC_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/user")
	public ModelAndView getTagAssocUser(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @PathVariable(ID) String tagGooruOid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		List<UserTagAssoc> userTagAssocs = this.getTagService().getTagAssocUser(tagGooruOid, limit, offset);

		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using gooruOid");
		SessionContextSupport.putLogParameter(TAG_GOORU_OID, tagGooruOid);
		String[] includes = (String[]) ArrayUtils.addAll(USER_INCLUDES, USER_ASSOC_INCLUDES);
		return toModelAndViewWithIoFilter(userTagAssocs, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/synonyms")
	public ModelAndView createTagSynonyms(@PathVariable(ID) String tagGooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		User user = (User) request.getAttribute(Constants.USER);
		TagSynonyms tagSynonyms = this.getTagService().createTagSynonyms(this.buildTagSynonymsInputParameters(data), tagGooruOid, user);
		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using gooruOid");
		SessionContextSupport.putLogParameter(GOORU_OID, tagGooruOid);
		return toModelAndViewWithIoFilter(tagSynonyms, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_SYNONYM_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/synonyms/{sid}")
	public ModelAndView updateTagSynonyms(@PathVariable(ID) String tagGooruOid, @PathVariable(SID) Integer tagSynonymsId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		User user = (User) request.getAttribute(Constants.USER);
		TagSynonyms tagSynonyms = this.getTagService().updateTagSynonyms(this.buildTagSynonymsInputParameters(data), tagGooruOid, tagSynonymsId, user);
		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using gooruOid");
		SessionContextSupport.putLogParameter(GOORU_OID, tagGooruOid);
		return toModelAndViewWithIoFilter(tagSynonyms, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_SYNONYM_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/synonyms")
	public ModelAndView getTagSynonyms(@PathVariable(ID) String tagGooruOid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");
		List<TagSynonyms> tagSynonyms = this.getTagService().getTagSynonyms(tagGooruOid);
		SessionContextSupport.putLogParameter(EVENT_NAME, "get-tag using tagUid");
		SessionContextSupport.putLogParameter(TAG_ID, tagGooruOid);
		return toModelAndViewWithIoFilter(tagSynonyms, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, TAG_SYNONYM_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAG_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/synonyms/{sid}", method = RequestMethod.DELETE)
	public void deleteTagSynonyms(HttpServletRequest request, HttpServletResponse response, @PathVariable(ID) String tagGooruOid, @PathVariable(SID) Integer synonymsId) throws Exception {
		request.setAttribute(PREDICATE, "tag.read");

		this.getTagService().deleteTagSynonyms(tagGooruOid, synonymsId);
	}

	private Tag buildTagFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Tag.class);
	}

	private TagSynonyms buildTagSynonymsInputParameters(String data) {
		return JsonDeserializer.deserialize(data, TagSynonyms.class);
	}

	public void setTagService(TagService tagService) {
		this.tagService = tagService;
	}

	public TagService getTagService() {
		return tagService;
	}

}
