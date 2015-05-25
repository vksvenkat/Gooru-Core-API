
package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface CourseRepository extends BaseRepository{
	
	Course getCourse(Short courseId);
	
	Course getCourseCode(String courseCode);
	
	List<Course> getCourses(Integer limit, Integer offset);

	Long getCourseCount();


}
