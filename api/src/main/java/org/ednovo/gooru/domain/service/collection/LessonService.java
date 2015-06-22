package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface LessonService extends AbstractCollectionService {
	ActionResponseDTO<Collection> createLesson(String lessonId, Collection collection, User user);

	public void updateLesson(String lessonId, Collection newCollection, User user);

	Map<String, Object> getLesson(String lessonId);

	List<Map<String, Object>> getLessons(String unitId, int limit, int offset);

}
