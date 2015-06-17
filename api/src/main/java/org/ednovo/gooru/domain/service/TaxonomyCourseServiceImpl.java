/////////////////////////////////////////////////////////////
// TaxonomyCourseServiceImpl.java
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
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class TaxonomyCourseServiceImpl extends BaseServiceImpl implements TaxonomyCourseService, ParameterProperties, ConstantProperties {

	@Autowired
	private TaxonomyCourseRepository TaxonomycourseRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;

	@Override
	public ActionResponseDTO<TaxonomyCourse> createTaxonomyCourse(TaxonomyCourse course, User user) {
 
		final Errors errors = validateCourse(course);
		if (!errors.hasErrors()) {
			Subject subject = this.getSubjectRepository().getSubject(course.getSubjectId());
			rejectIfNull(subject, GL0056, 404, SUBJECT);
			TaxonomyCourse courseCode = this.getTaxonomyCourseRepository().getCourseCode(course.getCourseCode());
			rejectIfAlreadyExist(courseCode, GL0101, COURSE);
			course.setCreatorUid(user);
			course.setCreatedOn(new Date(System.currentTimeMillis()));
			course.setLastModified(new Date(System.currentTimeMillis()));
			course.setActiveFlag((short) 1);
			this.getTaxonomyCourseRepository().save(course);
		}
		return new ActionResponseDTO<TaxonomyCourse>(course, errors);
	}

	@Override
	public TaxonomyCourse updateTaxonomyCourse(Integer courseId, TaxonomyCourse newCourse) {
		TaxonomyCourse course = this.getTaxonomyCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		if (newCourse.getActiveFlag() != null) {
				reject((newCourse.getActiveFlag() == 0 || newCourse.getActiveFlag() == 1), GL0007, ACTIVE_FLAG);
				course.setActiveFlag(newCourse.getActiveFlag());
		}
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
		course.setLastModified(new Date(System.currentTimeMillis()));
		this.getTaxonomyCourseRepository().save(course);
		return course;
	}

	@Override
	public TaxonomyCourse getTaxonomyCourse(Integer courseId) {
		TaxonomyCourse course = this.getTaxonomyCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		reject((course.getActiveFlag() == 1), GL0107, COURSE);
		return course;
	}

	@Override
	public SearchResults<TaxonomyCourse> getTaxonomyCourses(Integer limit, Integer offset) {
		SearchResults<TaxonomyCourse> result = new SearchResults<TaxonomyCourse>();
		result.setSearchResults(this.getTaxonomyCourseRepository().getCourses(limit, offset));
		result.setTotalHitCount(this.getTaxonomyCourseRepository().getCourseCount());
		return result;
	}

	@Override
	public void deleteTaxonomyCourse(Integer courseId) {
		TaxonomyCourse course = this.getTaxonomyCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		course.setActiveFlag((short) 0);
		course.setLastModified(new Date(System.currentTimeMillis()));
		this.getTaxonomyCourseRepository().save(course);
	}

	private Errors validateCourse(TaxonomyCourse course) {
		final Errors error = new BindException(course, COURSE);
    	rejectIfNull(error,course.getSubjectId(), SUBJECT_ID,  generateErrorMessage(GL0006, SUBJECT_ID));
		rejectIfNull(error,course.getCourseCode(), COURSE_CODE,  generateErrorMessage(GL0006, COURSE_CODE));
		rejectIfNull(error,course.getDisplaySequence(), DISPLAY_SEQUENCE,  generateErrorMessage(GL0006, DISPLAY_SEQUENCE));
		return error;
	}

	public TaxonomyCourseRepository getTaxonomyCourseRepository() {
		return TaxonomycourseRepository;
	}
	
	public SubjectRepository getSubjectRepository() {
		return subjectRepository;
	}
}