package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionBoService {
	ActionResponseDTO<Collection> createCollection(String lessonId, Collection collection, User user);

	void updateCollection(String collectionId, Collection newCollection, User user);

	Map<String, Object> getCollection(String collectionId, String collectionType);

	List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset);

}
