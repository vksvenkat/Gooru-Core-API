/////////////////////////////////////////////////////////////
//ClasspageRestV2Controller.java
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
/**
 * 
 */
package org.ednovo.gooru.controllers.v2.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.classpage.ClasspageService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/classpage", "/v2/class" })
public class ClasspageRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClasspageService classpageService;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private RedisService redisService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspage(@PathVariable(value = ID) final String classpageId, @RequestParam(value = DATA_OBJECT, required = false) final String data, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) final boolean includeCollectionItem,
			@RequestParam(value = MERGE, required = false) final String merge, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageId, user, merge), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getClasspages(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = TITLE, required = false) final String title,
			@RequestParam(value = AUTHOR, required = false) final String author, @RequestParam(value = USERNAME, required = false) final String userName, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		return toModelAndView(serialize(getClasspageService().getClasspages(offset, limit, user, title, author, userName), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
	public ModelAndView getClasspageByCode(@PathVariable(value = CLASSPAGE_CODE) final String classpageCode, @RequestParam(value = DATA_OBJECT, required = false) final String data,
			@RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = FALSE) final boolean includeCollectionItem, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_TAGS);
		if (includeCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		}
		return toModelAndViewWithIoFilter(getClasspageService().getClasspage(classpageCode, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "item/{id}", method = RequestMethod.GET)
	public ModelAndView getClasspageItem(@PathVariable(value = ID) final String collectionItemId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, false, user, null), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ITEM_READ })
	@RequestMapping(value = "/{cid}/item", method = RequestMethod.GET)
	public ModelAndView getClasspageItems(@PathVariable(value = COLLECTIONID) final String classpageId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = ORDER_BY, defaultValue = PLANNED_END_DATE, required = false) final String orderBy, @RequestParam(value = CLEAR_CACHE, required = false, defaultValue = "false") final Boolean clearCache,
			@RequestParam(value = OPTIMIZE, required = false, defaultValue = FALSE) final Boolean optimize, @RequestParam(value = STATUS, required = false) final String status, @RequestParam(value = TYPE, required = false) final String type, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		final User user = (User) request.getAttribute(Constants.USER);
		final String cacheKey = "v2-class-data-" + classpageId + "-" + offset + "-" + limit + "-" + optimize + "-" + orderBy + "-" + status + "-" + type;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			final List<Map<String, Object>> collectionItems = this.getClasspageService().getClasspageItems(classpageId, limit != null ? limit : (optimize ? limit : 5), offset, user, orderBy, optimize, status, type);
			final SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
			result.setSearchResults(collectionItems);
			result.setTotalHitCount(this.getCollectionRepository().getClasspageCollectionCount(classpageId, status, user.getPartyUid(), orderBy, type));
			data = serialize(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, includes);
			getRedisService().putValue(cacheKey, data, Constants.CACHE_EXPIRY_TIME_IN_SEC);
		}
		return toModelAndView(data);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = { "/{id}/member" }, method = RequestMethod.GET)
	public ModelAndView getClassMemberList(@PathVariable(ID) final String code, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			@RequestParam(value = GROUP_BY_STATUS, defaultValue = "false", required = false) final Boolean groupByStatus, @RequestParam(value = FILTER_BY, required = false) final String filterBy, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		return toModelAndView(serialize(this.getClasspageService().getMemberList(code, offset, limit, filterBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, CLASS_MEMBER_FIELDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = { "/my/{type}" }, method = RequestMethod.GET)
	public ModelAndView getMyTeachAndStudy(@PathVariable(value = TYPE) final String type, final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = ITEM_TYPE, required = false) final String itemType, @RequestParam(value = ORDER_BY, defaultValue = "desc", required = false) final String orderBy) throws Exception {
		final User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndView(serialize(this.getClasspageService().getMyStudy(apiCaller, orderBy, offset, limit, type, itemType), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, STUDY_RESOURCE_FIELDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView getMyClasspage(final HttpServletRequest request, @RequestParam(value = DATA_OBJECT, required = false) final String data, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") final boolean skipPagination,
			@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, @RequestParam(value = ORDER_BY, required = false, defaultValue = DESC) final String orderBy,
			final HttpServletResponse resHttpServletResponse) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		final List<Classpage> classpage = this.getClasspageService().getMyClasspage(offset, limit, user, skipPagination, orderBy);
		String[] includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, CLASSPAGE_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, CLASSPAGE_ITEM_INCLUDE_FIELDS);
		if (!skipPagination) {
			final SearchResults<Classpage> result = new SearchResults<Classpage>();
			result.setSearchResults(classpage);
			result.setTotalHitCount(this.getClasspageService().getMyClasspageCount(user.getGooruUId()));
			return toModelAndViewWithIoFilter(result, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		} else {
			return toModelAndViewWithIoFilter(getClasspageService().getMyClasspage(offset, limit, user, true, orderBy), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = { "/member/suggest" }, method = RequestMethod.GET)
	public ModelAndView classMemberSuggest(@RequestParam(value = QUERY) final String queryText, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getClasspageService().classMemberSuggest(queryText, user.getPartyUid()), RESPONSE_FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@RequestMapping(value = { "/assignment/{id}" }, method = RequestMethod.GET)
	public ModelAndView getParentDetails(@PathVariable(value = ID) final String collectionItemId, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		return toModelAndView(serializeToJson(this.getClasspageService().getParentDetails(collectionItemId), false, true));
	}

	public ClasspageService getClasspageService() {
		return classpageService;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public RedisService getRedisService() {
		return redisService;
	}

}
