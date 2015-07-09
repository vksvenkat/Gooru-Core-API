package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionBoService extends AbstractCollectionService {
	ActionResponseDTO<Collection> createCollection(User user, Collection collection);

	ActionResponseDTO<Collection> createCollection(String courseId, String unitId, String lessonId, User user, Collection collection);

	void updateCollection(String collectionId, Collection newCollection, User user);

	void updateCollectionItem(String collectionId, String collectionItemId, CollectionItem newCollectionItem, User user);

	Map<String, Object> getCollection(String collectionId, String collectionType, User user, boolean includeItems);

	List<Map<String, Object>> getCollections(String lessonId, String collectionType, int limit, int offset);

	void deleteCollection(String courseUId, String unitUId, String lessonUId, String collectionId, User user);

	List<Map<String, Object>> getCollectionItems(String collectionId, int limit, int offset);

	ActionResponseDTO<CollectionItem> createResource(String collectionId, CollectionItem collectionItem, User user);

	void updateResource(String collectionId, String resourceId, CollectionItem newCollectionItem, User user);

	CollectionItem createQuestion(String collectionId, String data, User user);

	void updateQuestion(String collectionId, String collectionQuestionItemId, String data, User user);

	Map<String, Object> getCollectionItem(String collectionId, String collectionItemId);

	CollectionItem addResource(String collectionId, String resourceId, User user);

	CollectionItem addQuestion(String collectionId, String questionId, User user);

	void moveCollectionToLesson(String courseId, String unitId, String lessonId, String collectionId, User user);
}
