package org.ednovo.gooru.domain.service.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentDomainAssoc;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.DomainRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class UnitServiceImpl extends AbstractCollectionServiceImpl implements UnitService, ConstantProperties, ParameterProperties {

	private static final String[] UNIT_TYPE = { "unit" };

	@Autowired
	private DomainRepository domainRepository;

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
			Map<String, Object> data = generateUnitMetaData(collection, collection, user);
			data.put(SUMMARY, MetaConstants.UNIT_SUMMARY);
			createContentMeta(collection, data);
			updateCourseMetaData(parentCollection.getContentId());
		}
		return new ActionResponseDTO<Collection>(collection, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateUnit(String unitId, Collection newCollection, User user) {
		Collection collection = this.getCollectionDao().getCollection(unitId);
		rejectIfNull(collection, GL0056, UNIT);
		this.updateCollection(collection, newCollection, user);
		Map<String, Object> data = generateUnitMetaData(collection, newCollection, user);
		if (data != null && data.size() > 0) {
			ContentMeta contentMeta = this.getContentRepository().getContentMeta(collection.getContentId());
			updateContentMeta(contentMeta, data);
		}
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
		List<Map<String, Object>> results = this.getCollections(filters, limit, offset);
		List<Map<String, Object>> units = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> unit : results) {
			units.add(mergeMetaData(unit));
		}
		return units;
	}

	private void updateCourseMetaData(Long courseId) {
		ContentMeta contentMeta = this.getContentRepository().getContentMeta(courseId);
		if (contentMeta != null) {
			int unitCount = this.getCollectionDao().getCollectionItemCount(courseId, CollectionType.UNIT.getCollectionType());
			Map<String, Object> metaData = JsonDeserializer.deserialize(contentMeta.getMetaData(), new TypeReference<Map<String, Object>>() {
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> summary = (Map<String, Object>) metaData.get(SUMMARY);
			summary.put(MetaConstants.UNIT_COUNT, unitCount);
			metaData.put(SUMMARY, summary);
			updateContentMeta(contentMeta, metaData);
		}
	}

	private Map<String, Object> generateUnitMetaData(Collection collection, Collection newCollection, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (newCollection.getDomainIds() != null) {
			List<Map<String, Object>> domain = updateUnitDomain(collection, newCollection.getDomainIds());
			data.put(DOMAIN, domain);
		}
		return data;
	}

	private List<Map<String, Object>> updateUnitDomain(Content content, List<Integer> domainIds) {
		this.getContentRepository().deleteContentDomainAssoc(content.getContentId());
		List<Map<String, Object>> unitDomains = null;
		if (domainIds != null && domainIds.size() > 0) {
			List<Domain> domains = this.getDomainRepository().getDomains(domainIds);
			unitDomains = new ArrayList<Map<String, Object>>();
			List<ContentDomainAssoc> contentDomainAssocs = new ArrayList<ContentDomainAssoc>();
			for (Domain domain : domains) {
				ContentDomainAssoc contentDomainAssoc = new ContentDomainAssoc();
				contentDomainAssoc.setContent(content);
				contentDomainAssoc.setDomain(domain);
				contentDomainAssocs.add(contentDomainAssoc);
				Map<String, Object> unitDomain = new HashMap<String, Object>();
				unitDomain.put(ID, contentDomainAssoc.getDomain().getDomainId());
				unitDomain.put(NAME, contentDomainAssoc.getDomain().getName());
				unitDomains.add(unitDomain);
			}
			this.getContentRepository().saveAll(contentDomainAssocs);
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

	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

}
