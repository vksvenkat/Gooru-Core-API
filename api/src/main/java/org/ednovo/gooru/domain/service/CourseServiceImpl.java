/////////////////////////////////////////////////////////////
// CourseServiceImpl.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
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
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CourseServiceImpl extends BaseServiceImpl implements CourseService, ParameterProperties, ConstantProperties {

	@Autowired
	private CourseRepository courseRepository;

	@Override
	public ActionResponseDTO<Course> createCourse(Course course, User user) {

		final Errors errors = validateCourse(course);
		if (!errors.hasErrors()) {
			course.setCreatorUid(user);
			course.setOrganization(user.getOrganization());
			course.setCreatedOn(new Date(System.currentTimeMillis()));
			course.setLastModified(new Date(System.currentTimeMillis()));
			course.setActiveFlag((short) 1);
			Course courseCode = this.getCourseRepository().getCourseCode(course.getCourseCode());
			rejectIfAlreadyExist(courseCode, GL0101, COURSE);
			courseRepository.save(course);
		}
		return new ActionResponseDTO<Course>(course, errors);
	}

	@Override
	public Course updateCourse(Integer courseId, Course newCourse, User user) {
		Course course = this.getCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0006, 404, COURSE);
		if (newCourse.getName() != null) {
			course.setName(newCourse.getName());
		}
		if (newCourse.getDescription() != null) {
			course.setDescription(newCourse.getDescription());
		}
		if (newCourse.getGrades() != null) {
			course.setGrades(newCourse.getGrades());
		}
		if (newCourse.getImagePath() != null) {
			course.setImagePath(newCourse.getImagePath());
		}
		if (newCourse.getDisplaySequence() != null) {
			course.setDisplaySequence(newCourse.getDisplaySequence());
		}
		if(newCourse.getActiveFlag() != null){
			course.setActiveFlag(newCourse.getActiveFlag());
		}
		course.setLastModified(new Date(System.currentTimeMillis()));
		this.getCourseRepository().save(course);
		return course;
	}

	@Override
	public Course getCourse(Integer courseId) {
		Course course = this.getCourseRepository().getCourse(courseId);
		if (course.getActiveFlag() == 0) {
			throw new BadRequestException("course id:" + course.getCourseId() + " is deactivated");
		}
		rejectIfNull(course, GL0056, 404, COURSE);
		return course;
	}

	@Override
	public SearchResults<Course> getCourses(Integer limit, Integer offset) {
		SearchResults<Course> result = new SearchResults<Course>();
		result.setSearchResults(this.getCourseRepository().getCourses(limit, offset));
		result.setTotalHitCount(this.getCourseRepository().getCourseCount());
		return result;
	}

	@Override
	public void deleteCourse(Integer courseId) {
		Course course = this.getCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		course.setActiveFlag((short) 0);
		course.setLastModified(new Date(System.currentTimeMillis()));
		courseRepository.save(course);
	}

	private Errors validateCourse(Course course) {
		final Errors error = new BindException(course, COURSE);
		rejectIfNull(error,course.getSubjectId(), GL0006, generateErrorMessage(GL0006, SUBJECT_ID));
		rejectIfNull(error,course.getCourseCode(), GL0006,  generateErrorMessage(GL0006, COURSE_CODE));
		rejectIfNull(error,course.getDisplaySequence(), GL0006,  generateErrorMessage(GL0006, DISPLAY_SEQUENCE));
		return error;
	}

	public CourseRepository getCourseRepository() {
		return courseRepository;
	}
}
