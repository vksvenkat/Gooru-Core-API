/////////////////////////////////////////////////////////////
//CourseRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2015 Gooru. All rights reserved.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CourseService;
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
@RequestMapping(value = { "/v2/course" })
public class CourseRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	public CourseService courseService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COURSE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = {" "},method = RequestMethod.POST)
	public ModelAndView createCourse(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Course> responseDTO = getCourseService().createCourse(buildCourseFromInputParameters(data), user);
		String includes[] = (String[]) ArrayUtils.addAll(COURSE_, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COURSE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateCourse(@PathVariable(value = ID) Integer courseId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(getCourseService().updateCourse(courseId, buildCourseFromInputParameters(data), user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COURSE_);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COURSE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCourse(@PathVariable(value = ID) Integer courseId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCourseService().getCourse(courseId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COURSE_);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COURSE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getCourses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCourseService().getCourses(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COURSE_);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COURSE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCourse(@PathVariable(value = ID) Integer courseId, HttpServletRequest request, HttpServletResponse response) {
		getCourseService().deleteCourse(courseId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	public CourseService getCourseService() {
		return courseService;
	}

	private Course buildCourseFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Course.class);
	}
}