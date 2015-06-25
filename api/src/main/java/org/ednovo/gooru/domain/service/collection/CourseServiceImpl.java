package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentTaxonomyCourseAssoc;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.TaxonomyCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CourseServiceImpl extends AbstractCollectionServiceImpl implements CourseService {

	private static final String[] COURSE_TYPE = { "course" };

	private final static String TAXONOMY_COURSE = "taxonomyCourse";

	@Autowired
	private TaxonomyCourseRepository taxonomyCourseRepository;

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
			Map<String, Object> data = generateContentMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.COURSE_SUMMARY);
			createContentMeta(collection, data);
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateCourse(String courseId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(courseId);
		rejectIfNull(collection, GL0056, COURSE);
		Map<String, Object> data = generateContentMetaData(collection, newCollection, user);
		if (data != null && data.size() > 0) {
			updateContentMeta(collection.getContentId(), data);
		}
		this.updateCollection(collection, newCollection, user);
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
		List<Map<String, Object>> results = this.getCollections(filters, limit, offset);
		List<Map<String, Object>> courses = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> course : results) {
			courses.add(mergeMetaData(course));
		}
		return courses;
	}

	private List<Map<String, Object>> updateTaxonomyCourse(Content content, List<Integer> taxonomyCourseIds) {
		this.getContentRepository().deleteContentTaxonomyCourseAssoc(content.getContentId());
		List<Map<String, Object>> courses = null;
		if (taxonomyCourseIds != null && taxonomyCourseIds.size() > 0) {
			List<TaxonomyCourse> taxonomyCourses = this.getTaxonomyCourseRepository().getTaxonomyCourses(taxonomyCourseIds);
			courses = new ArrayList<Map<String, Object>>();
			List<ContentTaxonomyCourseAssoc> contentTaxonomyCourseAssocs = new ArrayList<ContentTaxonomyCourseAssoc>();
			for (TaxonomyCourse taxonomyCourse : taxonomyCourses) {
				ContentTaxonomyCourseAssoc contentTaxonomyCourseAssoc = new ContentTaxonomyCourseAssoc();
				contentTaxonomyCourseAssoc.setContent(content);
				contentTaxonomyCourseAssoc.setTaxonomyCourse(taxonomyCourse);
				contentTaxonomyCourseAssocs.add(contentTaxonomyCourseAssoc);
				Map<String, Object> course = new HashMap<String, Object>();
				course.put(ID, contentTaxonomyCourseAssoc.getTaxonomyCourse().getCourseId());
				course.put(NAME, contentTaxonomyCourseAssoc.getTaxonomyCourse().getName());
				courses.add(course);
			}
			this.getContentRepository().saveAll(contentTaxonomyCourseAssocs);
		}
		return courses;
	}

	private Map<String, Object> generateContentMetaData(Collection collection, Collection newCollection, User user) {
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

	public TaxonomyCourseRepository getTaxonomyCourseRepository() {
		return taxonomyCourseRepository;
	}
}
