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
public class CourseServiceImpl extends AbstractCollectionServiceImpl implements CourseService {

	private static final String[] COURSE_TYPE = { "course" };

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createCourse(Collection collection, User user) {
		final Errors errors = validateCourse(collection);
		if (!errors.hasErrors()) {
			Collection parentCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
			if (parentCollection == null) {
				parentCollection = new Collection();
				parentCollection.setCollectionType(CollectionType.SHElf.getCollectionType());
				parentCollection.setTitle(CollectionType.SHElf.getCollectionType());
				parentCollection = super.createCollection(parentCollection, user);
			}
			collection.setSharing(Sharing.PRIVATE.getSharing());
			collection.setCollectionType(CollectionType.COURSE.getCollectionType());
			createCollection(collection, parentCollection, user);

		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCourse(String courseId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(courseId);
		rejectIfNull(collection, GL0056, COURSE);
		this.updateCollection(collection, newCollection, user);
	}

	@Override
	public Map<String, Object> getCourse(String courseId) {
		return this.getCollection(courseId, CollectionType.UNIT.getCollectionType());
	}

	@Override
	public List<Map<String, Object>> getCourses(int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_TYPE, COURSE_TYPE);
		return this.getCollections(filters, limit, offset);
	}

	private Errors validateCourse(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}
}
