package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.User;

public interface AbstractCollectionService {

	Collection createCollection(Collection collection, Collection parentCollection, User user);

	Collection updateCollection(Collection collection, Collection newCollection, User user);

	List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset);

	Map<String, Object> getCollection(String collectionId, String collectionType);

	List<Map<String, Object>> getCollectionItem(String collectionId, String[] sharing, int limit, int offset);

	void deleteCollection(String collectionId);

	CollectionItem createCollectionItem(CollectionItem collectionItem, Collection parentContent, Content content, User user);
	
	void createContentMeta(Content content, Map<String, Object> data);
	
	void updateContentMeta(ContentMeta  contentMeta, Map<String, Object> data);
	
	List<Map<String, Object>> updateContentMetaAssoc(Content content, User user, String key, List<Integer> metaIds);
}
