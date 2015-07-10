package org.ednovo.gooru.domain.service.collection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.ContentProviderAssociation;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.MetaConstants;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.domain.cassandra.service.DashboardCassandraService;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractResourceServiceImpl extends AbstractCollectionServiceImpl implements AbstractResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@Autowired
	private MongoQuestionsService mongoQuestionsService;

	@Autowired
	private DashboardCassandraService dashboardCassandraService;
	
	@Autowired
	private AsyncExecutor asyncExecutor;

	protected static final String HINTS = "hints";

	protected static final String EDUCATIONALUSE = "educationalUse";

	@Override
	public List<String> updateContentProvider(String gooruOid, List<String> providerList, User user, String providerType) {
		final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(_CONTENT_PROVIDER_TYPE, providerType);
		final List<ContentProviderAssociation> contentProviderAssociationList = this.getContentRepository().getContentProviderByGooruOid(gooruOid, null, providerType);

		if (contentProviderAssociationList.size() > 0) {
			this.getContentRepository().removeAll(contentProviderAssociationList);
		}
		for (final String provider : providerList) {
			ContentProvider contentProvider = this.getContentRepository().getContentProviderByName(provider, CONTENT_PROVIDER_TYPE + providerType);
			if (contentProvider == null) {
				contentProvider = new ContentProvider();
				contentProvider.setName(provider);
				contentProvider.setActiveFlag(true);
				contentProvider.setType(customTableValue);
				this.getContentRepository().save(contentProvider);
			}

			final ContentProviderAssociation contentProviderAssociation = new ContentProviderAssociation();
			contentProviderAssociation.setContentProvider(contentProvider);
			final ResourceSource resourceSource = new ResourceSource();
			resourceSource.setDomainName(provider);
			resourceSource.setActiveStatus(0);
			this.getResourceRepository().save(resourceSource);
			contentProviderAssociation.setResourceSource(resourceSource);
			contentProviderAssociation.setGooruOid(gooruOid);
			contentProviderAssociation.setAssociatedDate(new Date(System.currentTimeMillis()));
			contentProviderAssociation.setAssociatedBy(user);
			this.getContentRepository().save(contentProviderAssociation);
		}
		return providerList;
	}

	protected void mapSourceToResource(final Resource resource) {
		if (resource != null && resource.getResourceSource() == null) {
			if (ResourceType.Type.RESOURCE.getType().equalsIgnoreCase(resource.getResourceType().getName()) || ResourceType.Type.VIDEO.getType().equalsIgnoreCase(resource.getResourceType().getName())) {
				final String domainName = BaseUtil.getDomainName(resource.getUrl());
				if (domainName != null) {
					final ResourceSource resourceSource = this.getResourceRepository().findResourceSource(domainName);
					if (resourceSource != null) {
						resource.setResourceSource(resourceSource);
					} else {
						resource.setResourceSource(createResourcesourceAttribution(domainName, StringUtils.substringBeforeLast(domainName, ".")));
					}
					this.getResourceRepository().save(resource);
				}
			}
		}
	}

	protected ResourceSource createResourcesourceAttribution(final String domainName, final String attribution) {
		final ResourceSource resourceSource = new ResourceSource();
		resourceSource.setDomainName(domainName);
		resourceSource.setAttribution(attribution);
		resourceSource.setActiveStatus(1);
		if (BaseUtil.checkUrlHasHttpSupport(domainName)) {
			resourceSource.setHasHttpsSupport(1);
		} else {
			resourceSource.setHasHttpsSupport(0);
		}
		getResourceRepository().save(resourceSource);
		return resourceSource;
	}

	public ResourceMetadataCo updateYoutubeResourceFeeds(final Resource resource, final boolean isUpdate) {
		ResourceMetadataCo resourceFeeds = null;
		final ResourceCio resourceCio = getResourceCassandraService().read(resource.getGooruOid());
		if (resourceCio != null) {
			resourceFeeds = resourceCio.getResourceMetadata();
		}
		if (resource.getResourceType().getName().equals(ResourceType.Type.VIDEO.getType())) {

			resourceFeeds = ResourceImageUtil.getYoutubeResourceFeeds(resource.getUrl(), resourceFeeds);
			if (resourceFeeds != null) {
				resourceFeeds.setId(resource.getGooruOid());
				if (resourceCio != null) {
					resourceCio.setResourceMetadata(resourceFeeds);
					getResourceCassandraService().save(resourceCio);
				}
				return resourceFeeds;
			}
		}
		return resourceFeeds;
	}

	protected Map<String, Object> generateResourceMetaData(Content content, Resource newResource, User user) {
		Map<String, Object> data = new HashMap<String, Object>();
		if (newResource.getStandardIds() != null) {
			List<Map<String, Object>> standards = updateContentCode(content, newResource.getStandardIds(), MetaConstants.CONTENT_CLASSIFICATION_STANDARD_TYPE_ID);
			data.put(STANDARDS, standards);
		}
		if (newResource.getSkillIds() != null) {
			List<Map<String, Object>> skills = updateContentCode(content, newResource.getSkillIds(), MetaConstants.CONTENT_CLASSIFICATION_SKILLS_TYPE_ID);
			data.put(SKILLS, skills);
		}
		if (newResource.getDepthOfKnowledgeIds() != null) {
			List<Map<String, Object>> depthOfKnowledge = updateContentMetaAssoc(content, user, DEPTH_OF_KNOWLEDGE, newResource.getDepthOfKnowledgeIds());
			data.put(DEPTHOF_KNOWLEDGE, depthOfKnowledge);
		}

		if (newResource.getMomentsOfLearningIds() != null) {
			List<Map<String, Object>> momentsOfLearning = updateContentMetaAssoc(content, user, MOMENTS_OF_LEARNING, newResource.getMomentsOfLearningIds());
			data.put(MOMENTSOFLEARNING, momentsOfLearning);
		}
		
		if (newResource.getEducationalUseIds() != null) {
			List<Map<String, Object>> educationalUse = updateContentMetaAssoc(content, user, EDUCATIONAL_USE, newResource.getEducationalUseIds());
			data.put(EDUCATIONALUSE, educationalUse);
		}

		return data;
	}

	protected Map<String, Object> generateQuestionMetaData(Content content, AssessmentQuestion question, User user) {
		Map<String, Object> data = generateResourceMetaData(content, question, user);
		if (question.isQuestionNewGen()) {
			if (question.getAnswers() != null) {
				data.put(ANSWERS, question.getAnswers());
			}
			if (question.getHints() != null) {
				data.put(HINTS, question.getHints());
			}
		}
		return data;
	}

	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	public MongoQuestionsService getMongoQuestionsService() {
		return mongoQuestionsService;
	}

	public DashboardCassandraService getDashboardCassandraService() {
		return dashboardCassandraService;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
