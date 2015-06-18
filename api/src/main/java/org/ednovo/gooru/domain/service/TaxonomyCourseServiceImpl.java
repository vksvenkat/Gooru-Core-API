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
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class TaxonomyCourseServiceImpl extends BaseServiceImpl implements TaxonomyCourseService, ParameterProperties, ConstantProperties {

	@Autowired
	private TaxonomyCourseRepository TaxonomycourseRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<TaxonomyCourse> createTaxonomyCourse(TaxonomyCourse course, User user) {
		final Errors errors = validateCourse(course);
		if (!errors.hasErrors()) {
			Subject subject = this.getSubjectRepository().getSubject(course.getSubjectId());
			rejectIfNull(subject, GL0056, 404, SUBJECT);
			reject((subject.getActiveFlag() == 1), GL0107, SUBJECT);
			TaxonomyCourse courseCode = this.getTaxonomyCourseRepository().getCourseCode(course.getCourseCode());
			rejectIfAlreadyExist(courseCode, GL0101, COURSE);
			course.setCreatorUid(user);
			course.setCreatedOn(new Date(System.currentTimeMillis()));
			course.setLastModified(new Date(System.currentTimeMillis()));
			course.setActiveFlag((short) 1);
			course.setDisplaySequence(this.getTaxonomyCourseRepository().getMaxSequence() + 1);
			this.getTaxonomyCourseRepository().save(course);
		}
		return new ActionResponseDTO<TaxonomyCourse>(course, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateTaxonomyCourse(Integer courseId, TaxonomyCourse newCourse) {
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
		course.setLastModified(new Date(System.currentTimeMillis()));
		this.getTaxonomyCourseRepository().save(course);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public TaxonomyCourse getTaxonomyCourse(Integer courseId) {
		TaxonomyCourse course = this.getTaxonomyCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		reject((course.getActiveFlag() == 1), GL0107, COURSE);
		return course;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<TaxonomyCourse> getTaxonomyCourses(Integer limit, Integer offset) {
		List<TaxonomyCourse> result = this.getTaxonomyCourseRepository().getCourses(limit, offset);
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteTaxonomyCourse(Integer courseId) {
		TaxonomyCourse course = this.getTaxonomyCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		course.setActiveFlag((short) 0);
		course.setLastModified(new Date(System.currentTimeMillis()));
		this.getTaxonomyCourseRepository().save(course);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getDomains(Integer courseId) {
		List<Map<String, Object>> result = this.getTaxonomyCourseRepository().getDomains(courseId);
		rejectIfNull(result, GL0056, 404, DOMAIN);
		return result;
	}

	private Errors validateCourse(TaxonomyCourse course) {
		final Errors error = new BindException(course, COURSE);
    	rejectIfNull(error,course.getSubjectId(), SUBJECT_ID,  generateErrorMessage(GL0006, SUBJECT_ID));
		rejectIfNull(error,course.getCourseCode(), COURSE_CODE,  generateErrorMessage(GL0006, COURSE_CODE));
		return error;
	}

	public TaxonomyCourseRepository getTaxonomyCourseRepository() {
		return TaxonomycourseRepository;
	}
	
	public SubjectRepository getSubjectRepository() {
		return subjectRepository;
	}
}
