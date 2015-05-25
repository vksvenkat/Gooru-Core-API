package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface CourseService {
	
	ActionResponseDTO<Course> createCourse(Course course,User user);
	
	Course updateCourse(Short courseId, Course course,User user);

	Course getCourse(Short courseId);

	SearchResults<Course> getCourses(Integer limit, Integer offset);	
	
    void deleteCourse(Short courseId);

}
