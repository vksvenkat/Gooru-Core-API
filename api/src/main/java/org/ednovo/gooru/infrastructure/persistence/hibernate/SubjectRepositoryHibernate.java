/////////////////////////////////////////////////////////////
// SubjectRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectRepositoryHibernate extends BaseRepositoryHibernate
		implements SubjectRepository, ParameterProperties, ConstantProperties {

	private static final String SUBJECT_MAX = "SELECT MAX(subject.displaySequence) FROM Subject subject";
	private static final String SUBJECTS = "FROM Subject subject where subject.activeFlag=1";
	private static final String SUBJECT = "FROM Subject subject WHERE subject.subjectId=:subjectId";

	@Override
	public Subject getSubject(Integer subjectId) {
		Query query = getSession().createQuery(SUBJECT).setParameter(SUBJECT_ID, subjectId);
		return (Subject) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Subject> getSubjects(Integer classificationTypeId,Integer limit, Integer offset) {
		String hql = SUBJECTS;
		if(classificationTypeId != null){
			hql += "and subject.classificationTypeId=:classificationTypeId";
		}
		Query query = getSession().createQuery(hql);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT: limit) : limit);
		query.setFirstResult(offset);
		if(classificationTypeId != null){
			query.setParameter(CLASSIFICATION_TYPE_ID, classificationTypeId);
		}
		return list(query);
	}

	@Override
	public Integer getMaxSequence() {
		Query query = getSession().createQuery(SUBJECT_MAX);
		return ((Number)query.list().get(0)).intValue();
	}

}
