
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
	public Course getCourse(Short courseId) {
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
