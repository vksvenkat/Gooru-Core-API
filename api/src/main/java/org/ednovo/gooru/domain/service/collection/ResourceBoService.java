package org.ednovo.gooru.domain.service.collection;

import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;

public interface ResourceBoService extends AbstractCollectionService {
	Resource createResource(Resource newResource, User user);

	void updateResource(String resourceId, Resource newResource, User user);
}
