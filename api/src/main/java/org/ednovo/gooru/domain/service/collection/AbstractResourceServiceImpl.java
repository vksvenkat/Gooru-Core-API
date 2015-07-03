package org.ednovo.gooru.domain.service.collection;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.ContentProviderAssociation;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.content.ContentService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractResourceServiceImpl extends AbstractCollectionServiceImpl implements AbstractResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private ResourceCassandraService resourceCassandraService;

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
	
	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	public ContentService getContentService() {
		return contentService;
	}

}
