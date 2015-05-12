/////////////////////////////////////////////////////////////
// TagServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;

import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ContentProviderAssociation;
import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.Tag;
import org.ednovo.gooru.core.api.model.TagSynonyms;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserTagAssoc;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.cassandra.service.BlackListWordCassandraService;
import org.ednovo.gooru.domain.cassandra.service.DashboardCassandraService;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.FeedbackService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.PostRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;


@Service
public class TagServiceImpl extends BaseServiceImpl implements TagService, ParameterProperties,ConstantProperties {

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private BlackListWordCassandraService blackListWordCassandraService;
	
	@Autowired
	private StorageRepository storageRepository;
	
	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private ResourceCassandraService resourceCassandraService;
	
	@Autowired
	private DashboardCassandraService dashboardCassandraService;
	
	@Autowired
	private IndexHandler indexHandler;

	@Autowired
	@Resource(name = "userService")
	private UserService userService;

	@Override
	public ActionResponseDTO<Tag> createTag(Tag newTag, User user) {
		Tag tag = this.getTagRepository().findTagByLabel(newTag.getLabel());
		
		if (tag != null) {
			throw new BadRequestException(generateErrorMessage(GL0041, LABEL));
		}
		Errors errors = this.createTagValidation(newTag, tag);
		if (!errors.hasErrors()) {
			if (newTag.getWikiPostGooruOid() != null) {
				newTag.setWikiPostGooruOid(this.getPostRepository().getPost(newTag.getWikiPostGooruOid()) != null ? newTag.getWikiPostGooruOid() : null);
			}
			if (newTag.getExcerptPostGooruOid() != null) {
				newTag.setExcerptPostGooruOid(this.getPostRepository().getPost(newTag.getExcerptPostGooruOid()) != null ? newTag.getExcerptPostGooruOid() : null);
			}
			newTag.setTagType(customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_TYPE.getTable(), USER));
			CustomTableValue customTableValue = this.getBlackListWordCassandraService().validate(newTag.getLabel()) ? getCustomTableRepository().getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), CustomProperties.TagStatus.ABUSE.getTagStatus()) : getCustomTableRepository()
					.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), CustomProperties.TagStatus.ACTIVE.getTagStatus());
			newTag.setStatus(customTableValue);
			newTag.setGooruOid(UUID.randomUUID().toString());
			newTag.setSharing(Sharing.PRIVATE.getSharing());
			ContentType contentType = getCollectionService().getContentType(ContentType.TAG);
			newTag.setContentType(contentType);
			newTag.setCreator(user);
			newTag.setLastModified(new Date(System.currentTimeMillis()));
			newTag.setCreatedOn(new Date(System.currentTimeMillis()));
			newTag.setUser(user);
			newTag.setOrganization(user.getPrimaryOrganization());
			newTag.setLastUpdatedUserUid(user.getGooruUId());
			newTag.setCreatedOn(new Date());
			this.getTagRepository().save(newTag);
		}
		return new ActionResponseDTO<Tag>(newTag, errors);
	}

	@Override
	public Tag updateTag(String gooruOid, Tag newTag, User apiCaller) {
		Tag tag = this.getTagRepository().findTagByTagId(gooruOid);
		if (tag == null) {
			throw new NotFoundException("Tag Not Found!!", GL0056);
		}
		if (newTag.getLabel() != null) {
			if (getTagRepository().findTagByLabel(newTag.getLabel()) != null) {
				throw new EntityExistsException(generateErrorMessage(GL0041, LABEL));
			}
			if (newTag.getLabel().length() > 25) {
				throw new BadRequestException("Label should be with in 25 character!!!");
			}
			tag.setLabel(newTag.getLabel());
		}
		if (newTag.getWikiPostGooruOid() != null && this.getPostRepository().getPost(newTag.getWikiPostGooruOid()) != null) {
			tag.setWikiPostGooruOid(newTag.getWikiPostGooruOid());
		}
		if (newTag.getExcerptPostGooruOid() != null && this.getPostRepository().getPost(newTag.getExcerptPostGooruOid()) != null) {
			tag.setExcerptPostGooruOid(newTag.getExcerptPostGooruOid());
		}
		if (newTag.getStatus() != null && customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), newTag.getStatus().getValue()) != null) {
			tag.setStatus(customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), newTag.getStatus().getValue()));
		}
		if (userService.isContentAdmin(apiCaller)) {
			if (tag.getCreator() != null && tag.getCreator().getPartyUid() != null) {
				User user = getUserService().findByGooruId(tag.getCreator().getPartyUid());
				tag.setCreator(user);
			}
			if (tag.getUser() != null && tag.getUser().getPartyUid() != null) {
				User user = getUserService().findByGooruId(tag.getUser().getPartyUid());
				tag.setUser(user);
			}
		}
		this.getTagRepository().save(tag);
		return tag;
	}

	@Override
	public List<Tag> getTags(Integer offset, Integer limit) {
		List<Tag> tag = this.getTagRepository().getTags(offset, limit);
		if (tag.size() <= 0) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG), GL0056);
		}
		return tag;
	}

	@Override
	public List<Tag> getTag(String gooruOid) {
		List<Tag> tag = this.getTagRepository().getTag(gooruOid);
		if (tag.size() <= 0) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG), GL0056);
		}
		return tag;
	}

	@Override
	public void deleteTag(String gooruOid) {
		List<Tag> tags = this.getTagRepository().getTag(gooruOid);
		if (tags.size() <= 0) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG), GL0056);
		}
		this.getTagRepository().removeAll(tags);
	}

	@Override
	public List<ContentTagAssoc> getTagContentAssoc(String tagGooruOid, Integer limit, Integer offset) {
		return this.getTagRepository().getTagContentAssoc(tagGooruOid, limit, offset);
	}
	
	@Override
	public Map<String, Object> getResourceByLabel(String label, Integer limit, Integer offset, String gooruUid) {
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(NFS);
		List<Object[]> results = this.getTagRepository().getResourceByLabel(label, limit, offset,  gooruUid);
		Map<String, Object> content = new HashMap<String, Object>();
		List<Map<String, Object>> resource = new ArrayList<Map<String,Object>>();
		for (Object[] object : results) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(TITLE, object[0]);
			result.put(GOORU_OID, object[1]);
			result.put(TYPE, object[2]);
			String typeName = object[2].toString();
			Map<String, Object> thumbnails = new HashMap<String, Object>();
			if (typeName != null && typeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
				if (object[8] != null) {
					thumbnails.put(URL, ResourceImageUtil.getYoutubeVideoId(object[8].toString()) == null ? null : "http://img.youtube.com/vi/" + ResourceImageUtil.getYoutubeVideoId(object[8].toString()) + "/1.jpg");
				}
			} else {
				if (object[4] != null) {
					thumbnails.put(URL, storageArea.getCdnDirectPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
				} else {
					thumbnails.put(URL, "");
				}
			}
			result.put(THUMBNAILS, thumbnails);
			if (object[5] != null) {
				Map<String, Object> resourceFormat = new HashMap<String, Object>();
				resourceFormat.put(VALUE, object[5]);
				resourceFormat.put(DISPLAY_NAME, object[6]);
				result.put(RESOURCEFORMAT, resourceFormat);
			}
			
			List<ContentProviderAssociation> contentProviderAssociations = this.getContentRepository().getContentProviderByGooruOid(String.valueOf(object[1]),null,null);
 			if (contentProviderAssociations != null) {
 				List<String> aggregator = new ArrayList<String>();
 				List<String> publisher = new ArrayList<String>();
 				for (ContentProviderAssociation contentProviderAssociation : contentProviderAssociations) {
 					if (contentProviderAssociation.getContentProvider() != null && contentProviderAssociation.getContentProvider().getType() != null
 							&& contentProviderAssociation.getContentProvider().getType().getValue().equalsIgnoreCase(CustomProperties.ContentProviderType.PUBLISHER.getContentProviderType())) {
 						publisher.add(contentProviderAssociation.getContentProvider().getName());
 					} else if (contentProviderAssociation.getContentProvider() != null && contentProviderAssociation.getContentProvider().getType() != null
 							&& contentProviderAssociation.getContentProvider().getType().getValue().equalsIgnoreCase(CustomProperties.ContentProviderType.AGGREGATOR.getContentProviderType())) {
 						aggregator.add(contentProviderAssociation.getContentProvider().getName());
 					}
 				}
 				result.put(PUBLISHER, publisher);
 				result.put(AGGREGATOR, aggregator);
 			}
 			
			result.put(VIEWS, this.dashboardCassandraService.readAsLong(object[1].toString(),COUNT_VIEWS));
			result.put(RATINGS, this.collectionService.setRatingsObj(this.getResourceRepository().getResourceSummaryById(String.valueOf(object[1]))));
			resource.add(result);
		}
		content.put(SEARCH_RESULT,resource);
		content.put(TOTAL_HIT_COUNT,this.getTagRepository().getResourceByLabelCount(label, gooruUid));
		return content;
	}

	@Override
	public UserTagAssoc createUserTagAssoc(String gooruUid, String tagGooruOid) {
		User user = this.getUserService().findByGooruId(gooruUid);
		Tag tag = this.tagRepository.findTagByTagId(tagGooruOid);
		if (tag == null || user == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _CONTENT), GL0056);
		}
		UserTagAssoc userTagAssocDb = this.tagRepository.getUserTagassocById(gooruUid, tagGooruOid);
		if (userTagAssocDb != null) {
			throw new BadRequestException("Tag already associated by same user");
		}
		UserTagAssoc userTagAssoc = new UserTagAssoc();
		userTagAssoc.setUser(user);
		userTagAssoc.setTagGooruOid(tagGooruOid);
		this.getTagRepository().save(userTagAssoc);
		tag.setUserCount(tag.getUserCount() != null ? tag.getUserCount() : 0 + 1);
		this.getTagRepository().save(tag);
		return userTagAssoc;
	}

	@Override
	public void deleteUserTagAssoc(String gooruUid, String tagGooruOid) {
		UserTagAssoc userTagAssoc = this.tagRepository.getUserTagassocById(gooruUid, tagGooruOid);
		if (userTagAssoc != null) {
			this.getTagRepository().remove(userTagAssoc);
			Tag tag = this.tagRepository.findTagByTagId(tagGooruOid);
			tag.setUserCount(tag.getUserCount() - 1);
			this.getTagRepository().save(tag);
			indexHandler.setReIndexRequest(userTagAssoc.getUser().getPartyUid(), IndexProcessor.INDEX, USER, null, false, false);					
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, _CONTENT), GL0056);
		}

	}

	@Override
	public List<UserTagAssoc> getUserTagAssoc(String gooruUid, Integer limit, Integer offset) {
		return this.getTagRepository().getContentTagByUser(gooruUid, limit, offset);
	}

	@Override
	public List<UserTagAssoc> getTagAssocUser(String tagGooruOid, Integer limit, Integer offset) {
		return this.getTagRepository().getTagAssocUser(tagGooruOid, limit, offset);
	}

	@Override
	public TagSynonyms createTagSynonyms(TagSynonyms tagSynonyms, String tagGooruOid, User user) {
		Tag tag = this.getTagRepository().findTagByTagId(tagGooruOid);
		if (tag == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG), GL0056);
		}
		rejectIfNull(tagSynonyms.getTargetTagName(), GL0006, SYNONYM);
		if (getTagRepository().findSynonymByName(tagSynonyms.getTargetTagName()) != null) {
			throw new BadRequestException(generateErrorMessage(GL0041, SYNONYM));
		}
		if (tagSynonyms.getTargetTagName().length() > 25) {
			throw new BadRequestException("Synonym must with in 25 character");
		}
		tagSynonyms.setCreator(user);
		tagSynonyms.setCreatedOn(new Date());
		tagSynonyms.setTagContentGooruOid(tagGooruOid);
		tagSynonyms.setStatus(customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), NEW));
		this.getTagRepository().save(tagSynonyms);
		tag.setSynonymsCount(tag.getSynonymsCount() != null ? tag.getSynonymsCount() : 0 + 1);
		this.getTagRepository().save(tag);
		return tagSynonyms;
	}

	@Override
	public TagSynonyms updateTagSynonyms(TagSynonyms newTagSynonyms, String tagGooruOid, Integer tagSynonymsId, User user) {
		Tag tag = this.getTagRepository().findTagByTagId(tagGooruOid);
		TagSynonyms tagSynonyms = this.getTagRepository().findTagSynonymById(tagSynonymsId);
		if (tag == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG), GL0056);
		}
		if (tagSynonyms == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, TAG_SYNONYMS), GL0056);
		}
		if (newTagSynonyms.getTargetTagName() != null) {
			if (getTagRepository().findSynonymByName(newTagSynonyms.getTargetTagName()) != null) {
				throw new BadRequestException(generateErrorMessage(GL0041, SYNONYM));
			}
			if (newTagSynonyms.getTargetTagName().length() > 25) {
				throw new BadRequestException("Synonym must with in 25 character");
			}
			tagSynonyms.setTargetTagName(newTagSynonyms.getTargetTagName());
		}
		if (newTagSynonyms.getStatus() != null && customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), newTagSynonyms.getStatus().getValue()) != null) {
			tagSynonyms.setStatus(customTableRepository.getCustomTableValue(CustomProperties.Table.TAG_STATUS.getTable(), newTagSynonyms.getStatus().getValue()));
		}
		tagSynonyms.setApprover(user);
		tagSynonyms.setApprovalOn(new Date());
		getTagRepository().save(tagSynonyms);
		return tagSynonyms;
	}

	@Override
	public List<TagSynonyms> getTagSynonyms(String tagGooruOid) {

		return this.getTagRepository().getTagSynonyms(tagGooruOid);
	}

	@Override
	public void deleteTagSynonyms(String tagGooruOid, Integer synonymsId) {
		TagSynonyms tagSynonyms = this.getTagRepository().getSynonymByTagAndSynonymId(tagGooruOid, synonymsId);
		if (tagSynonyms == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, SYNONYMS), GL0056);
		}
		Tag tag = this.getTagRepository().findTagByTagId(tagGooruOid);
		this.getTagRepository().remove(tagSynonyms);
		tag.setSynonymsCount(tag.getSynonymsCount() - 1);
		this.getTagRepository().save(tag);
	}

	private Errors createTagValidation(Tag newTag, Tag tag) {
		Errors errors = new BindException(newTag, TAG);
		rejectIfNullOrEmpty(errors, newTag.getLabel(), LABEL, generateErrorMessage(GL0007, LABEL));
		return errors;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public TagRepository getTagRepository() {
		return tagRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public PostRepository getPostRepository() {
		return postRepository;
	}

	public BlackListWordCassandraService getBlackListWordCassandraService() {
		return blackListWordCassandraService;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public void setStorageRepository(StorageRepository storageRepository) {
		this.storageRepository = storageRepository;
	}

	public StorageRepository getStorageRepository() {
		return storageRepository;
	}

	public void setFeedbackService(FeedbackService feedbackService) {
		this.feedbackService = feedbackService;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}

}
