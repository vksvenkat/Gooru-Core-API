package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;

public interface CollectionDao extends BaseRepository {
	Collection getCollection(String collectionId);

	Collection getCollection(String userUid, String collectionType);

	Collection getCollectionByUser(String collectionId, String userUid);

	List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset);
	
	List<Map<String, Object>> getCollectionItem(String collectionId, String[] sharing, int limit, int offset);
	
	int getCollectionItemMaxSequence(Long contentId);
	
	int getCollectionItemCount(Long contentId, String collectionType);
}
