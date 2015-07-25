package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.eventlogs.CourseEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CourseServiceImpl extends AbstractCollectionServiceImpl implements CourseService {

	@Autowired
	private CourseEventLog courseEventLog;
	
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
			CollectionItem course = createCollection(collection, parentCollection, user);
			getCourseEventLog().courseEventLogs(parentCollection.getGooruOid(), course, user, collection, ADD );
			Map<String, Object> data = generateCourseMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.COURSE_SUMMARY);
			createContentMeta(collection, data);
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCourse(String courseId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(courseId);
		Collection parentCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
		rejectIfNull(collection, GL0056, COURSE);
		Map<String, Object> data = generateCourseMetaData(collection, newCollection, user);
		if (data != null && data.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collection.getContentId());
			updateContentMeta(contentMeta, data);
		}
		this.updateCollection(collection, newCollection, user);
		if (newCollection.getPosition() != null) {
			this.resetSequence(parentCollection, collection.getGooruOid(), newCollection.getPosition(), user.getPartyUid(), COURSE);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getCourse(String courseId) {
		return this.getCollection(courseId, CollectionType.COURSE.getCollectionType());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getCourses(int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_TYPE, COURSE_TYPE);
		return getCourses(filters, limit, offset);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getCourses(String gooruUid, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(COLLECTION_TYPE, COURSE_TYPE);
		filters.put(PARENT_COLLECTION_TYPE, SHELF);
		filters.put(GOORU_UID, gooruUid);
		return getCourses(filters, limit, offset);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCourse(String courseUId, User user) {
		CollectionItem course = getCollectionDao().getCollectionItemById(courseUId, user);
		rejectIfNull(course, GL0056, COURSE);
		reject(this.getOperationAuthorizer().hasUnrestrictedContentAccess(courseUId, user), GL0099, 403, COURSE);
		Collection parentCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
		getCourseEventLog().courseEventLogs(course.getCollection().getGooruOid(), course, user, null, DELETE );
		this.getCollectionDao().updateClassByCourse(course.getCollection().getContentId());
		this.resetSequence(parentCollection.getGooruOid(), course.getContent().getGooruOid(), user.getPartyUid(), COURSE);
		course.getContent().setIsDeleted((short) 1);
		this.getCollectionDao().save(course);
	}

	private List<Map<String, Object>> getCourses(Map<String, Object> filters, int limit, int offset) {
		List<Map<String, Object>> results = this.getCollections(filters, limit, offset);
		List<Map<String, Object>> courses = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> course : results) {
			courses.add(mergeMetaData(course));
		}
		return courses;
	}

	private Map<String, Object> generateCourseMetaData(Collection collection, Collection newCollection, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (newCollection.getTaxonomyCourseIds() != null) {
			List<Map<String, Object>> taxonomyCourse = updateTaxonomyCourse(collection, newCollection.getTaxonomyCourseIds());
			data.put(TAXONOMY_COURSE, taxonomyCourse);
		}
		if (newCollection.getAudienceIds() != null) {
			List<Map<String, Object>> audiences = updateContentMetaAssoc(collection, user, AUDIENCE, newCollection.getAudienceIds());
			data.put(AUDIENCE, audiences);
		}
		return data;
	}

	private Errors validateCourse(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	public CourseEventLog getCourseEventLog() {
		return courseEventLog;
	}
}
