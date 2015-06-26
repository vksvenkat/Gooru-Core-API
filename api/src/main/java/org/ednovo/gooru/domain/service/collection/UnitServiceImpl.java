package org.ednovo.gooru.domain.service.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class UnitServiceImpl extends AbstractCollectionServiceImpl implements UnitService, ConstantProperties, ParameterProperties {

	private static final String[] UNIT_TYPE = { "unit" };

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createUnit(String courseId, Collection collection, User user) {
		final Errors errors = validateUnit(collection);
		if (!errors.hasErrors()) {
			Collection parentCollection = getCollectionDao().getCollection(courseId);
			rejectIfNull(collection, GL0056, COURSE);
			collection.setSharing(Sharing.PRIVATE.getSharing());
			collection.setCollectionType(CollectionType.UNIT.getCollectionType());
			createCollection(collection, parentCollection, user);
			// TO DO
			/*Map<String, Object> data = generateCourseMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.UNIT_SUMMARY);
			createContentMeta(collection, data);
			updateCourseMetaData(parentCollection, CollectionType.UNIT.getCollectionType());*/
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateUnit(String unitId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(unitId);
		rejectIfNull(collection, GL0056, UNIT);
		this.updateCollection(collection, newCollection, user);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getUnit(String unitId) {
		return this.getCollection(unitId, CollectionType.UNIT.getCollectionType());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getUnits(String courseId, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PARENT_GOORU_OID, courseId);
		filters.put(COLLECTION_TYPE, UNIT_TYPE);
		return this.getCollections(filters, limit, offset);
	}

	private void updateCourseMetaData(Collection collection, String collectionType) {
		ContentMeta contentMeta = this.getContentRepository().getContentMeta(collection.getContentId());
		if (contentMeta != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(contentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
			summary.put(MetaConstants.UNIT_COUNT, this.getCollectionDao().getCollectionItemCount(collection.getContentId(), CollectionType.UNIT.getCollectionType()));
			metaData.put(SUMMARY, summary);
			updateContentMeta(contentMeta, metaData);
		}
	}
	
	private Map<String, Object> generateUnitMetaData(Collection collection, Collection newCollection, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		return data;
	}

	private Errors validateUnit(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

}
