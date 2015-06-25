package org.ednovo.gooru.domain.service.collection;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.User;

public interface UnitService extends AbstractCollectionService {

	ActionResponseDTO<Collection> createUnit(String courseId, Collection collection, User user);
	
	void updateUnit(String unitId, Collection newCollection, User user);
	
	Map<String, Object> getUnit(String unitId);
	
	List<Map<String, Object>> getUnits(String courseId, int limit, int offset);
}
