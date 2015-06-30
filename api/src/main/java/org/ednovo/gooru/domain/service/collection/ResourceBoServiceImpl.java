package org.ednovo.gooru.domain.service.collection;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ResourceBoServiceImpl extends AbstractResourceServiceImpl implements ResourceBoService, ParameterProperties, ConstantProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBoServiceImpl.class);

	@Override
	public Resource createResource(Resource newResource, User user) {
		Resource resource = null;
		if (newResource.getUrl() != null && !newResource.getUrl().isEmpty() && newResource.getAttach() == null) {
			resource = this.getResourceRepository().findResourceByUrl(newResource.getUrl(), Sharing.PUBLIC.getSharing(), null);
		}
		if (this.getOperationAuthorizer().hasUnrestrictedContentAccess() && resource != null && resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
			throw new AccessDeniedException(generateErrorMessage(GL0012));
		}

		final String title = newResource.getTitle().length() > 1000 ? newResource.getTitle().substring(0, 1000) : newResource.getTitle();
		if (resource == null) {
			resource = new Resource();
			resource.setGooruOid(UUID.randomUUID().toString());
			resource.setUser(user);
			resource.setTitle(title);
			if (newResource.getResourceFormat() != null) {
				CustomTableValue resourcetype = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				resource.setResourceFormat(resourcetype);
			}
			resource.setDescription(newResource.getDescription());
			License license = new License();
			license.setName(OTHER);
			if (resource.getRecordSource() == null) {
				resource.setRecordSource(Resource.RecordSource.COLLECTION.getRecordSource());
			}
			final ResourceType resourceTypeDo = new ResourceType();
			resource.setResourceType(resourceTypeDo);
			String fileExtension = null;
			if (newResource.getAttach() != null && newResource.getAttach().getFilename() != null) {
				fileExtension = StringUtils.substringAfterLast(newResource.getAttach().getFilename(), ".");
				if (fileExtension.contains(PDF) || BaseUtil.supportedDocument().containsKey(fileExtension)) {
					resourceTypeDo.setName(ResourceType.Type.HANDOUTS.getType());
				} else {
					resourceTypeDo.setName(ResourceType.Type.IMAGE.getType());
				}
				resource.setUrl(newResource.getAttach().getFilename());
				resource.setIsOer(1);
				license.setName(CREATIVE_COMMONS);
			} else {
				resource.setUrl(newResource.getUrl());
				if (ResourceImageUtil.getYoutubeVideoId(newResource.getUrl()) != null) {
					resourceTypeDo.setName(ResourceType.Type.VIDEO.getType());
				} else if (newResource.getUrl() != null && newResource.getUrl().contains("vimeo.com")) {
					final String id = StringUtils.substringAfterLast(newResource.getUrl(), "/");
					if (StringUtils.isNumeric(id)) {
						final ResourceMetadataCo resourceMetadataCo = ResourceImageUtil.getMetaDataFromVimeoVideo(newResource.getUrl());
						resourceTypeDo.setName(ResourceType.Type.VIMEO_VIDEO.getType());
						newResource.setThumbnail(resourceMetadataCo != null ? resourceMetadataCo.getThumbnail() : null);
					} else {
						resourceTypeDo.setName(ResourceType.Type.RESOURCE.getType());
					}

				} else {
					resourceTypeDo.setName(ResourceType.Type.RESOURCE.getType());
				}

			}
			resource.setLicense(license);
			if (newResource.getSharing() != null) {
				resource.setSharing(Sharing.PRIVATE.getSharing());
			} else {
				resource.setSharing(newResource.getSharing());
			}
			String domainName = BaseUtil.getDomainName(newResource.getUrl());
			ResourceSource resourceSource = null;
			if (domainName != null) {
				resourceSource = this.getResourceRepository().findResourceSource(domainName);
			}
			if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
				resource.setHasFrameBreaker(true);
			} else if ((newResource.getUrl() != null && newResource.getUrl().contains(YOUTUBE_URL) && ResourceImageUtil.getYoutubeVideoId(newResource.getUrl()) == null)) {
				resource.setHasFrameBreaker(true);
			} else {
				resource.setHasFrameBreaker(false);
			}

			getResourceRepository().saveOrUpdate(resource);
			updateYoutubeResourceFeeds(resource, false);
			getResourceRepository().save(resource);
			mapSourceToResource(resource);
			if (newResource.getHost() != null && newResource.getHost().size() > 0) {
				resource.setHost(updateContentProvider(resource.getGooruOid(), newResource.getHost(), user, HOST));
			}
			try {
				if (newResource.getThumbnail() != null || fileExtension != null && fileExtension.contains(PDF)) {
					this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
				}
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
			if (resource != null) {
				getIndexHandler().setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
			}
			if (newResource.getAttach() != null) {
				this.getResourceImageUtil().moveAttachment(newResource, resource);
			}
		}

		return resource;

	}

	@Override
	public void updateResource(String resourceId, Resource newResource, User user) {
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceId);
		rejectIfNull(resource, GL0056, RESOURCE);
		if (newResource.getTitle() != null) {
			resource.setTitle(newResource.getTitle());
		}
		if (newResource.getDescription() != null) {
			resource.setDescription(newResource.getDescription());
		}
		if (newResource.getCategory() != null) {
			resource.setCategory(newResource.getCategory().toLowerCase());
		}
		if (newResource.getInstructional() != null) {
			final CustomTableValue resourceCategory = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, newResource.getInstructional().getValue());
			resource.setInstructional(resourceCategory);
		}
		if (newResource.getResourceFormat() != null) {
			final CustomTableValue resourcetype = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
			resource.setResourceFormat(resourcetype);
		}

		if (newResource.getMediaType() != null) {
			resource.setMediaType(newResource.getMediaType());
		}

		if (newResource.getSharing() != null) {
			resource.setSharing(newResource.getSharing());
		}

		String fileExtension = null;
		if (newResource.getAttach() != null && newResource.getAttach().getFilename() != null) {
			fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(newResource.getAttach().getFilename(), ".");
			final ResourceType resourceTypeDo = new ResourceType();
			resource.setResourceType(resourceTypeDo);
			if (fileExtension.contains(PDF)) {
				resourceTypeDo.setName(ResourceType.Type.HANDOUTS.getType());
			} else {
				resourceTypeDo.setName(ResourceType.Type.IMAGE.getType());
			}
			resource.setUrl(newResource.getAttach().getFilename());
		}
		if (newResource.getS3UploadFlag() != null) {
			resource.setS3UploadFlag(newResource.getS3UploadFlag());
		}

		if (newResource.getThumbnail() != null && newResource.getThumbnail().length() > 0) {
			try {
				this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
		}
		if (newResource.getAttach() != null) {
			this.getResourceImageUtil().moveAttachment(newResource, resource);
		}
		this.getResourceRepository().save(resource);

	}

}
