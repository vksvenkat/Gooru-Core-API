package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionBoService extends AbstractCollectionService {
	ActionResponseDTO<Collection> createCollection(User user, Collection collection);

	ActionResponseDTO<Collection> createCollection(String courseId, String unitId, String lessonId, User user, Collection collection);

	void updateCollection(String collectionId, Collection newCollection, User user);
	
	void updateCollectionItem(String collectionItemId,String collectionId, CollectionItem newCollectionItem, User user);

	Map<String, Object> getCollection(String collectionId, String collectionType);

	List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset);

	List<Map<String, Object>> getCollectionItem(String collectionId, int limit, int offset);

	ActionResponseDTO<Resource> createResource(String collectionId, Resource resource, User user);

	void updateResource(String collectionId, String resourceId, Resource newResource, User user);
	
	ActionResponseDTO<AssessmentQuestion> createQuestion(String collectionId, AssessmentQuestion assessmentQuestion, User user);
	
	void updateQuestion(String collectionId, String resourceId, AssessmentQuestion assessmentQuestion, User user);
}
