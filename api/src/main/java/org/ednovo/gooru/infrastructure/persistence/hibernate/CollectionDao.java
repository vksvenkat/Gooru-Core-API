package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;

public interface CollectionDao extends BaseRepository {
	Collection getCollection(String collectionId);

	Collection getCollectionByType(String collectionId, String[] collectionType);

	Collection getCollection(String userUid, String collectionType);

	Collection getCollectionByUser(String collectionId, String userUid);

	List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset);

	List<Map<String, Object>> getCollectionItem(Map<String, Object> filters, int limit, int offset);

	int getCollectionItemMaxSequence(Long contentId);

	int getCollectionItemCount(Long contentId, String collectionType);

	List<CollectionItem> getCollectionItems(String gooruOid, int parameterOne, int parameterTwo, String userUid, String collectionType);

	CollectionItem getCollectionItem(String parentGooruOid, String gooruOid, String userUid);

	List<CollectionItem> getCollectionItems(String parentId, int sequence, String userUid, String collectionType);

	CollectionItem getCollectionItemById(String gooruOid, User user);

	CollectionItem getParentCollection(Long contentId);

	CollectionItem getCollectionItem(String collectionItemId);

	List<CollectionItem> getCollectionItems(String collectionId);
	
	List<Map<String, Object>> getCollectionItemById(String collectionId);
	
	List<Collection> getCollections(List<String> collectionIds);
	
	void updateClassByCourse(Long contentId);

}
