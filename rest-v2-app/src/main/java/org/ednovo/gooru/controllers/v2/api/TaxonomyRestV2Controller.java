/////////////////////////////////////////////////////////////
//TaxonomyRestV2Controller.java
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/v2/taxonomy")
public class TaxonomyRestV2Controller extends BaseController implements ConstantProperties {
	
	@Autowired
	private TaxonomyService taxonomyService;
	
	@Autowired
	private RedisService redisService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(value =  "/{id}", method = RequestMethod.GET)
	public ModelAndView getTaxonomyByCode(@PathVariable(value = ID) Integer codeId,  HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getTaxonomyService().findTaxonomyCodeById(codeId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true,  TAXONOMY_CODE_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(value =  "/parent/{id}", method = RequestMethod.GET)
	public ModelAndView getTaxonomyByParentCode(@PathVariable(value = ID) String parentCodeId,@RequestParam(value= CLEAR_CACHE,required=false,defaultValue=FALSE) boolean clearCache ,@RequestParam(value= GROUP_BY_CODE,required=false,defaultValue=FALSE) boolean groupByCode, @RequestParam(value= CREATOR_UID,required=false) String creatorUid, @RequestParam(value= FETCH_TYPE,required=false, defaultValue=LIBRARY) String fetchType, HttpServletRequest request, HttpServletResponse response) {
		Map<String, List<Code>> code = null;
		final String cacheKey = "v2-taxonomy-code-"+parentCodeId+"-"+groupByCode + '-' + creatorUid + '-' + fetchType;
		String data = null;
		if (!clearCache) {
			data = getRedisService().getValue(cacheKey);
		}
		if (data == null) {
			code = this.getTaxonomyService().findCodeByParentCodeId(parentCodeId, groupByCode, creatorUid, fetchType, fetchType);
			data = serialize((groupByCode) ? code : code.get(TAXONOMY_CODES), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, true, TAXONOMY_CODE_INCLUDES);
			getRedisService().putValue(cacheKey, data, RedisService.DEFAULT_FEATURED_EXP);
		}
		
		return toModelAndView(data);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/course")
	public ModelAndView getCourse(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = CODE_ID, required = false) Integer codeId, @RequestParam(value = CLEAR_CACHE, required = false) boolean clearCache, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "4") Integer maxLessonLimit) throws Exception {
		request.setAttribute(PREDICATE, LIBRARY_CODE_CONTENT);
		final String cacheKey = LIBRARY_CODE_JSON;
		String libraryCodeList = null;
		if (!clearCache) {
			libraryCodeList = (String) getRedisService().getValue(cacheKey);
		}
		if (codeId == null || codeId == 0) {
			User user = (User) request.getAttribute(Constants.USER);
			String organizationUid = user.getOrganization().getPartyUid();
			codeId = TaxonomyUtil.getTaxonomyRootId(organizationUid);
		}
		if (libraryCodeList == null) {
			libraryCodeList = serializeToJsonWithExcludes(this.getTaxonomyService().getCourseBySubject(codeId, maxLessonLimit), COURSE_EXCLUDES, true, COURSE_INCLUDES);
			getRedisService().putValue(cacheKey, libraryCodeList, RedisService.DEFAULT_PROFILE_EXP);
		}
		return toModelAndView(libraryCodeList);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/curriculum")
	public ModelAndView getCurriculum(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = CLEAR_CACHE, required = false) boolean clearCache, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "4") Integer maxLessonLimit) throws Exception {
		final String cacheKey = CURRICULAM_CODE_JSON;
		String curriculumCodeList = null;
		if (!clearCache) {
			curriculumCodeList = (String) getRedisService().getValue(cacheKey);
		}
		if (curriculumCodeList == null) {
			curriculumCodeList = serializeToJsonWithExcludes(this.getTaxonomyService().getCurriculum(), CURRICULUM_EXCLUDES, true, CURRICULUM_INCLUDES);
			getRedisService().putValue(cacheKey, curriculumCodeList, RedisService.DEFAULT_PROFILE_EXP);
		}
		return toModelAndView(curriculumCodeList);
	}
	public TaxonomyService getTaxonomyService() {
		return taxonomyService;
	}


	public RedisService getRedisService() {
		return redisService;
	}
	
}
