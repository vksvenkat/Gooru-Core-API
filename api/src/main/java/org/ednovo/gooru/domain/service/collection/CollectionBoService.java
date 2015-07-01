package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionBoService extends AbstractCollectionService {
	ActionResponseDTO<Collection> createCollection(User user, Collection collection);

	ActionResponseDTO<Collection> createCollection(String courseId, String unitId, String lessonId, User user, Collection collection);

	void updateCollection(String collectionId, Collection newCollection, User user);

	Map<String, Object> getCollection(String collectionId, String collectionType);

	List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset);
	
	void deleteCollection(String courseUId, String unitUId, String lessonUId, String collectionId);

}
