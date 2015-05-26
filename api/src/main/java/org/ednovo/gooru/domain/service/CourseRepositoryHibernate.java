/////////////////////////////////////////////////////////////
// CourseRepositoryHibernate.java
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

import java.util.List;

import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CourseRepositoryHibernate extends BaseRepositoryHibernate implements CourseRepository, ParameterProperties, ConstantProperties {

	@Override
	public Course getCourse(Integer courseId) {
		Query query = getSession().createQuery("FROM Course c  WHERE c.courseId=:courseId").setParameter("courseId", courseId);
		return (Course) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public Course getCourseCode(String courseCode) {
		Query query = getSession().createQuery("FROM Course c  WHERE c.courseCode=:courseCode").setParameter("courseCode", courseCode);
		return (Course) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Course> getCourses(Integer limit, Integer offset) {
		Query query = getSession().createQuery("FROM Course");
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return list(query);
	}

	@Override
	public Long getCourseCount() {
		Query query = getSession().createQuery("SELECT COUNT(*) FROM Course");
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}

}
