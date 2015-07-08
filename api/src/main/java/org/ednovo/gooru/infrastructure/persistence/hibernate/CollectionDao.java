package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionDao extends BaseRepository {
	Collection getCollection(String collectionId);
	
	Collection getCollectionByType(String collectionId, String collectionType);

	Collection getCollection(String userUid, String collectionType);

	Collection getCollectionByUser(String collectionId, String userUid);

	List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset);
	
	List<Map<String, Object>> getCollectionItem(Map<String, Object> filters, int limit, int offset);
	
	int getCollectionItemMaxSequence(Long contentId);
	
	int getCollectionItemCount(Long contentId, String collectionType);
	
	List<CollectionItem> getCollectionItems(String gooruOid, int parameterOne, int parameterTwo);
	
	CollectionItem getCollectionItem(String parentGooruOid, String gooruOid);
	
	List<CollectionItem> getCollectionItems(String gooruOid, int sequence);
	
	void deleteCollectionItem(Long contentId);
	
	CollectionItem getCollectionItemById(String gooruOid, User user);
	
	CollectionItem getParentCollection(Long contentId);
}
