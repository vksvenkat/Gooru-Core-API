/////////////////////////////////////////////////////////////
// SubjectServiceImpl.java
// rest-v2-app
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
			reject(!(subject.getActiveFlag() > 1 || subject.getActiveFlag() < 0), generateErrorMessage(GL0007, ACTIVE_FLAG));
			subject.setCreatedOn(new Date(System.currentTimeMillis()));
			subject.setLastModified(new Date(System.currentTimeMillis()));
			subject.setActiveFlag((short) 1);
			subject.setCreator(user);
			subject.setOrganization(user.getOrganization());
			this.getSubjectRepository().save(subject);
		}
		return new ActionResponseDTO<Subject>(subject, errors);
	}

	@Override
	public Subject getSubject(String subjectId) {
		Subject subject = (Subject) subjectRepository.getSubject(subjectId);
		rejectIfNull(subject, GL0056, 404, generateErrorMessage(GL0056, SUBJECT));
		reject((subject.getActiveFlag() == 1), generateErrorMessage(DEPRICATED));
		return subjectRepository.getSubject(subjectId);
	}

	@Override
	public void deleteSubject(String subjectId) {
		Subject subject = subjectRepository.getSubject(subjectId);
		rejectIfNull(subject, GL0056, 404, generateErrorMessage(GL0056, SUBJECT));
		subject.setActiveFlag((short) 0);
		subject.setLastModified(new Date(System.currentTimeMillis()));
		subjectRepository.save(subject);
	}

	@Override
	public SearchResults<Subject> getSubjects(Integer limit, Integer offset) {
		SearchResults<Subject> result = new SearchResults<Subject>();
		result.setSearchResults(this.getSubjectRepository().getSubjects(limit, offset));
		result.setTotalHitCount(this.getSubjectRepository().getSubjectCount());
		return result;
	}

	@Override
	public Subject updateSubject(Subject subject, User user, String subjectId) {
		Subject oldSubject = subjectRepository.getSubject(subjectId);
		rejectIfNull(oldSubject, GL0056, 404, SUBJECT);
		if (subject.getDescription() != null)
			oldSubject.setDescription(subject.getDescription());
		if (subject.getImagePath() != null)
			oldSubject.setImagePath(subject.getImagePath());
		if (subject.getName() != null)
			oldSubject.setName(subject.getName());
		if (subject.getDisplaySequence() != null)
			oldSubject.setDisplaySequence(subject.getDisplaySequence());
		if (subject.getActiveFlag() >= 0)
			oldSubject.setActiveFlag(subject.getActiveFlag());
		oldSubject.setLastModified(new Date(System.currentTimeMillis()));
		subjectRepository.save(oldSubject);
		return oldSubject;
	}

	private Errors validateSubject(Subject subject) {
		final Errors errors = new BindException(subject, SUBJECT);
		rejectIfNullOrEmpty(errors, subject.getName(), NAME, generateErrorMessage(GL0006, NAME));
		rejectIfNull(errors, subject.getDisplaySequence(), DISPLAY_SEQUENCE, generateErrorMessage(GL0006, DISPLAY_SEQUENCE));
		return errors;
	}

	public SubjectRepository getSubjectRepository() {
		return subjectRepository;
	}
}
