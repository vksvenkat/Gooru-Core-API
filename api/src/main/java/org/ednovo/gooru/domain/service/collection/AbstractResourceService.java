package org.ednovo.gooru.domain.service.collection;

import java.util.List;

import org.ednovo.gooru.core.api.model.User;

public interface AbstractResourceService extends AbstractCollectionService {
	public List<String> updateContentProvider(final String gooruOid, final List<String> providerList, final User user, final String providerType);
}
