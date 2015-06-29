package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface CourseService extends AbstractCollectionService {
	ActionResponseDTO<Collection> createCourse(Collection collection, User user);

	void updateCourse(String courseId, Collection newCollection, User user);

	Map<String, Object> getCourse(String courseId);
	
	List<Map<String, Object>> getCourses(int limit, int offset);
	
	List<Map<String, Object>> getCourses(String gooruUId, int limit, int offset);

}
