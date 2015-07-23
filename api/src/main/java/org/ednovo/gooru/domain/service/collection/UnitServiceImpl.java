package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.ContentSubdomainAssoc;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubdomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class UnitServiceImpl extends AbstractCollectionServiceImpl implements UnitService, ConstantProperties, ParameterProperties {

	@Autowired
	private SubdomainRepository subdomainRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Collection> createUnit(String courseId, Collection collection, User user) {
		final Errors errors = validateUnit(collection);
		if (!errors.hasErrors()) {
			Collection parentCollection = getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
			rejectIfNull(parentCollection, GL0056, COURSE);
			collection.setSharing(Sharing.PRIVATE.getSharing());
			collection.setCollectionType(CollectionType.UNIT.getCollectionType());
			createCollection(collection, parentCollection, user);
			Map<String, Object> data = generateUnitMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.UNIT_SUMMARY);
			createContentMeta(collection, data);
			updateContentMetaDataSummary(parentCollection.getContentId(), UNIT, ADD);
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateUnit(String courseId, String unitId, Collection newCollection, User user) {
		Collection collection = getCollectionDao().getCollectionByType(unitId, UNIT_TYPE);
		rejectIfNull(collection, GL0056, UNIT);
		Collection parentCollection = getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		rejectIfNull(collection, GL0056, COURSE);
		if (newCollection.getPosition() != null) {
			this.resetSequence(parentCollection, collection.getGooruOid(), newCollection.getPosition(), user.getPartyUid(), UNIT);
		}
		this.updateCollection(collection, newCollection, user);
		Map<String, Object> data = generateUnitMetaData(collection, newCollection, user);
		if (data != null && data.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collection.getContentId());
			updateContentMeta(contentMeta, data);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getUnit(String courseId,String unitId) {
		Collection course = this.getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		rejectIfNull(course, GL0056, COURSE);
		return this.getCollection(unitId, CollectionType.UNIT.getCollectionType());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getUnits(String courseId, int limit, int offset) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(PARENT_GOORU_OID, courseId);
		filters.put(COLLECTION_TYPE, UNIT_TYPE);
		List<Map<String, Object>> results = this.getCollections(filters, limit, offset);
		List<Map<String, Object>> units = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> unit : results) {
			units.add(mergeMetaData(unit));
		}
		return units;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteUnit(String courseId, String unitId, User user) {
		CollectionItem unit = getCollectionDao().getCollectionItem(courseId, unitId, user.getPartyUid());
		rejectIfNull(unit, GL0056, UNIT);
		reject(this.getOperationAuthorizer().hasUnrestrictedContentAccess(unitId, user), GL0099, 403, UNIT);
		Collection course = getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		rejectIfNull(course, GL0056, COURSE);
		this.resetSequence(courseId, unit.getContent().getGooruOid(), user.getPartyUid(), UNIT);
		updateContentMetaDataSummary(course.getContentId(), UNIT, DELETE);
		unit.getContent().setIsDeleted((short) 1);
		this.getCollectionDao().save(unit);
	}

	private Map<String, Object> generateUnitMetaData(Collection collection, Collection newCollection, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (newCollection.getSubdomainIds() != null) {
			List<Map<String, Object>> subdomain = updateUnitDomain(collection, newCollection.getSubdomainIds());
			data.put(SUBDOMAIN, subdomain);
		}
		if (newCollection.getTaxonomyCourseIds() != null) {
			List<Map<String, Object>> taxonomyCourse = updateTaxonomyCourse(collection, newCollection.getTaxonomyCourseIds());
			data.put(TAXONOMY_COURSE, taxonomyCourse);
		}
		return data;
	}

	private List<Map<String, Object>> updateUnitDomain(Content content, List<Integer> subdomainIds) {
		this.getContentRepository().deleteContentSubdomainAssoc(content.getContentId());
		List<Map<String, Object>> unitDomains = null;
		if (subdomainIds != null && subdomainIds.size() > 0) {
			List<Subdomain> domains = this.getSubdomainRepository().getSubdomains(subdomainIds);
			if (domains != null && domains.size() > 0) {
				unitDomains = new ArrayList<Map<String, Object>>();
				List<ContentSubdomainAssoc> contentDomainAssocs = new ArrayList<ContentSubdomainAssoc>();
				for (Subdomain subdomain : domains) {
					ContentSubdomainAssoc contentDomainAssoc = new ContentSubdomainAssoc();
					contentDomainAssoc.setContent(content);
					contentDomainAssoc.setSubdomain(subdomain);
					contentDomainAssocs.add(contentDomainAssoc);
					Map<String, Object> unitDomain = new HashMap<String, Object>();
					unitDomain.put(ID, subdomain.getSubdomainId());
					unitDomain.put(SUBJECT_ID, subdomain.getTaxonomyCourse().getSubjectId());
					unitDomain.put(COURSE_ID, subdomain.getTaxonomyCourse().getCourseId());
					unitDomain.put(NAME, subdomain.getDomain().getName());
					unitDomains.add(unitDomain);
				}
				this.getContentRepository().saveAll(contentDomainAssocs);
			}
		}
		return unitDomains;
	}

	private Errors validateUnit(final Collection collection) {
		final Errors errors = new BindException(collection, COLLECTION);
		if (collection != null) {
			rejectIfNullOrEmpty(errors, collection.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	public SubdomainRepository getSubdomainRepository() {
		return subdomainRepository;
	}
}
