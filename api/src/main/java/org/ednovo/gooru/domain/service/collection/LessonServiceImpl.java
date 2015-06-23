package org.ednovo.gooru.domain.service.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class LessonServiceImpl extends AbstractCollectionServiceImpl implements LessonService {

	private static final String[] LESSON_TYPE = { "lesson" };
	
	@Override
	public ActionResponseDTO<Collection> createLesson(String unitId, Collection collection, User user) {
		final Errors errors = validateLesson(collection);
		if (!errors.hasErrors()) {
			Collection parentCollection = getCollectionDao().getCollection(unitId);
			rejectIfNull(collection, GL0056, UNIT);
			collection.setSharing(Sharing.PRIVATE.getSharing());
			collection.setCollectionType(CollectionType.LESSON.getCollectionType());
			createCollection(collection, parentCollection, user);
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateLesson(String lessonId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(lessonId);
		rejectIfNull(collection, GL0056, LESSON);
		this.updateCollection(collection, newCollection, user);
	}

	@Override
	public Map<String, Object> getLesson(String lessonId) {
		return this.getCollection(lessonId, CollectionType.UNIT.getCollectionType());
	}

	@Override
	public List<Map<String, Object>> getLessons(String unitId, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_TYPE, LESSON_TYPE);
		filters.put(PARENT_GOORU_OID, unitId);
		return this.getCollections(filters, limit, offset);
	}
	
	private Errors validateLesson(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

}
