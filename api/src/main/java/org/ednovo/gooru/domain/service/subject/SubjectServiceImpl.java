/////////////////////////////////////////////////////////////
// SubjectServiceImpl.java
// gooru-api
// Created by Gooru on 2015
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
package org.ednovo.gooru.domain.service.subject;

import java.util.Date;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService, ParameterProperties {

	@Autowired
	private SubjectRepository subjectRepository;

	@Override
	public ActionResponseDTO<Subject> createSubject(Subject subject, User user) {
		final Errors errors = validateSubject(subject);
		if (!errors.hasErrors()) {
			subject.setCreatedOn(new Date(System.currentTimeMillis()));
			subject.setLastModified(new Date(System.currentTimeMillis()));
			subject.setActiveFlag((short) 1);
			subject.setCreator(user);
			this.getSubjectRepository().save(subject);
		}
		return new ActionResponseDTO<Subject>(subject, errors);
	}

	@Override
	public Subject getSubject(Integer subjectId) {
		Subject subject = subjectRepository.getSubject(subjectId);
		rejectIfNull(subject, GL0056, 404, SUBJECT);
		reject((subject.getActiveFlag() == 1), GL0107, SUBJECT);
		return subject;
	}

	@Override
	public void deleteSubject(Integer subjectId) {
		Subject subject = subjectRepository.getSubject(subjectId);
		rejectIfNull(subject, GL0056, 404, SUBJECT);
		subject.setActiveFlag((short) 0);
		subject.setLastModified(new Date(System.currentTimeMillis()));
		this.getSubjectRepository().save(subject);
	}

	@Override
	public SearchResults<Subject> getSubjects(Integer limit, Integer offset) {
		SearchResults<Subject> result = new SearchResults<Subject>();
		result.setSearchResults(this.getSubjectRepository().getSubjects(limit, offset));
		result.setTotalHitCount(this.getSubjectRepository().getSubjectCount());
		return result;
	}

	@Override
	public Subject updateSubject(Subject newSubject, Integer subjectId) {
		Subject subject = this.getSubjectRepository().getSubject(subjectId);
		rejectIfNull(subject, GL0056, 404, SUBJECT);
		if (newSubject.getActiveFlag() != null) {
			reject((newSubject.getActiveFlag() == 0 || newSubject.getActiveFlag() == 1), GL0007, ACTIVE_FLAG);
			subject.setActiveFlag(newSubject.getActiveFlag());
	    }
		if (newSubject.getDescription() != null) {
			subject.setDescription(newSubject.getDescription());
		}
		if (newSubject.getImagePath() != null) {
			subject.setImagePath(newSubject.getImagePath());
		}
		if (newSubject.getName() != null) {
			subject.setName(newSubject.getName());
		}
		if (newSubject.getDisplaySequence() != null) {
			subject.setDisplaySequence(newSubject.getDisplaySequence());
		}
		subject.setLastModified(new Date(System.currentTimeMillis()));
		this.getSubjectRepository().save(subject);
		return subject;
	}

	private Errors validateSubject(Subject subject) {
		final Errors errors = new BindException(subject, SUBJECT);
		rejectIfNull(errors, subject.getName(), NAME, generateErrorMessage(GL0006, NAME));
		rejectIfNull(errors, subject.getDisplaySequence(), DISPLAY_SEQUENCE, generateErrorMessage(GL0006, DISPLAY_SEQUENCE));
		return errors;
	}

	public SubjectRepository getSubjectRepository() {
		return subjectRepository;
	}
}
