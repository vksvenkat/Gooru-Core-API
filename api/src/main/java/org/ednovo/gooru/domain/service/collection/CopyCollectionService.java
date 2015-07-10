package org.ednovo.gooru.domain.service.collection;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface CopyCollectionService extends AbstractCollectionService  {
	
	Collection copyCollection(String courseId,String unitId,String lessonId,String collectionId,User user,Collection newCollection) throws Exception;

}
