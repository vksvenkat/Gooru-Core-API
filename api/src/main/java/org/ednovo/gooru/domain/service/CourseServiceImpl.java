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
			course.setOrganization(course.getOrganization());
			course.setCreatedOn(new Date(System.currentTimeMillis()));
			course.setLastModified(new Date(System.currentTimeMillis()));
			Course courseCode=this.getCourseRepository().getCourseCode(course.getCourseCode());
			rejectIfAlreadyExist(courseCode,GL0101,COURSE);
			courseRepository.save(course);
	  }
		return new ActionResponseDTO<Course>(course,errors);
	}

	@Override
	public Course updateCourse(Short courseId, Course course, User user) {
		Course newCourse = this.getCourseRepository().getCourse(courseId);
		rejectIfNull(course,GL0006,404,SUBJECT_ID);
		if(course.getName()!=null){
		    newCourse.setName(course.getName());
	    }
		if(course.getDescription()!=null){
			newCourse.setDescription(course.getDescription());
		}
		if(course.getGrades()!=null){
			newCourse.setGrades(course.getGrades());
		}
		if(course.getImagePath()!=null){
			newCourse.setImagePath(course.getImagePath());
		}
		if (course.getDisplaySequence() != null) {
			newCourse.setDisplaySequence(course.getDisplaySequence());
		}
		if ( course.getActiveFlag() == null) {
		newCourse.setActiveFlag(course.getActiveFlag());
		}
		newCourse.setLastModified(new Date(System.currentTimeMillis()));
		this.getCourseRepository().save(newCourse);
		return course;
	}

	@Override
	public Course getCourse(Short courseId) {
		Course course = this.getCourseRepository().getCourse(courseId);
	 if(course.getActiveFlag() == 0){
			//throw new BadRequestException(generateErrorMessage(GL0107, DEACTIVATE_COURSE), GL0107);
			throw new BadRequestException("course id:"+course.getCourseId()+" is deactivated");
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
	public void deleteCourse(Short courseId) {
		Course course = this.getCourseRepository().getCourse(courseId);
		rejectIfNull(course, GL0056, 404, COURSE);
		this.getCourseRepository().remove(course);
	}
	
	private Errors validateCourse(Course course) {
		final Errors error = new BindException(course, COURSE);
		rejectIfNull(course.getSubjectId(),GL0006,SUBJECT_ID);
		rejectIfNull(course.getCourseCode(),GL0006,COURSE_CODE);
		rejectIfNull(course.getDisplaySequence(),GL0006,DISPLAY_SEQUENCE);
		rejectIfNull(course.getActiveFlag(),GL0006,ACTIVE_FLAG);
		return error;
	}

	public CourseRepository getCourseRepository() {
		return courseRepository;
	}


}
