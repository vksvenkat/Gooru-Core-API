package org.ednovo.gooru.domain.service.collection;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentClassification;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CopyCollectionServiceImpl extends AbstractResourceServiceImpl implements CopyCollectionService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@Autowired
	private QuestionService questionService;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Collection copyCollection(String courseId, String unitId, String lessonId, String collectionId, User user, Collection newCollection) {
		Collection sourceCollection = this.getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
		rejectIfNull(sourceCollection, GL0056, 404, _COLLECTION);
		final Collection lesson = this.getCollectionDao().getCollectionByType(lessonId, LESSON_TYPE);
		rejectIfNull(lesson, GL0056, 404, LESSON);
		Collection destCollection = copyCollection(sourceCollection, lesson, user, newCollection);
		final Collection unit = this.getCollectionDao().getCollectionByType(unitId, UNIT_TYPE);
		final Collection course = this.getCollectionDao().getCollectionByType(courseId, COURSE_TYPE);
		updateMetaDataSummary(course.getContentId(), unit.getContentId(), lesson.getContentId(), destCollection.getCollectionType(), LESSON);
		return destCollection;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Collection copyCollection(String folderId, String collectionId, User user, Collection newCollection) {
		Collection sourceCollection = this.getCollectionDao().getCollectionByType(collectionId, COLLECTION_TYPES);
		rejectIfNull(sourceCollection, GL0056, 404, _COLLECTION);
		Collection targetCollection = null;
		if (folderId != null) {
			targetCollection = this.getCollectionDao().getCollectionByType(folderId, FOLDER_TYPE);
			rejectIfNull(targetCollection, GL0056, 404, FOLDER);
		} else {
			targetCollection = getCollectionDao().getCollection(user.getPartyUid(), CollectionType.SHElf.getCollectionType());
			if (targetCollection == null) {
				targetCollection = new Collection();
				targetCollection.setCollectionType(CollectionType.SHElf.getCollectionType());
				targetCollection.setTitle(CollectionType.SHElf.getCollectionType());
				super.createCollection(targetCollection, user);
			}
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + targetCollection.getUser().getPartyUid() + "*");
		return copyCollection(sourceCollection, targetCollection, user, newCollection);
	}

	private void copyCollectionItems(Collection lesson, Collection sourceCollection, Collection destCollection, User user) {
		List<CollectionItem> collectionItems = this.getCollectionDao().getCollectionItems(sourceCollection.getGooruOid());
		for (CollectionItem sourceCollectionItem : collectionItems) {
			final CollectionItem destCollectionItem = new CollectionItem();
			if (sourceCollectionItem.getContent().getContentType().getName().equalsIgnoreCase(QUESTION)) {
				final AssessmentQuestion assessmentQuestion = this.getQuestionService().copyQuestion(sourceCollectionItem.getContent().getGooruOid(), user);
				destCollectionItem.setContent(assessmentQuestion);
			} else {
				destCollectionItem.setContent(sourceCollectionItem.getContent());
			}
			destCollectionItem.setItemType(sourceCollectionItem.getItemType());
			destCollectionItem.setItemSequence(sourceCollectionItem.getItemSequence());
			destCollectionItem.setNarration(sourceCollectionItem.getNarration());
			destCollectionItem.setNarrationType(sourceCollectionItem.getNarrationType());
			destCollectionItem.setStart(sourceCollectionItem.getStart());
			destCollectionItem.setAssociatedUser(user);
			destCollectionItem.setStop(sourceCollectionItem.getStop());
			destCollectionItem.setCollection(destCollection);
			destCollectionItem.setContent(sourceCollectionItem.getContent());
			this.getCollectionDao().save(destCollectionItem);
		}

	}

	private void copyContentClassification(Long sourceContentId, Collection desCollection) {
		List<ContentClassification> contentClassifications = this.getContentClassificationRepository().getContentClassification(sourceContentId);
		for (ContentClassification contentClassification : contentClassifications) {
			ContentClassification newContentClassification = new ContentClassification();
			newContentClassification.setCode(contentClassification.getCode());
			newContentClassification.setContent(desCollection);
			newContentClassification.setTypeId(contentClassification.getTypeId());
			this.getContentRepository().save(newContentClassification);
		}
	}

	private void copyContentMetaAssoc(Long sourceContentId, Collection destCollection) {
		List<ContentMetaAssociation> contentMetaAssocs = this.getContentRepository().getContentMetaAssoc(sourceContentId);
		for (ContentMetaAssociation contentMetaAssoc : contentMetaAssocs) {
			ContentMetaAssociation newContentMetaAssoc = new ContentMetaAssociation();
			newContentMetaAssoc.setContent(destCollection);
			newContentMetaAssoc.setCreatedOn(new Date(System.currentTimeMillis()));
			newContentMetaAssoc.setTypeId(contentMetaAssoc.getTypeId());
			newContentMetaAssoc.setUser(destCollection.getUser());
			this.getCollectionDao().save(newContentMetaAssoc);
		}
	}

	private void copyCollectionRepoStorage(Collection sourceCollection, Collection destCollection) {
		StringBuilder sourceFilepath = new StringBuilder(sourceCollection.getOrganization().getNfsStorageArea().getInternalPath());
		sourceFilepath.append(sourceCollection.getImagePath()).append(File.separator);
		StringBuilder targetFilepath = new StringBuilder(destCollection.getOrganization().getNfsStorageArea().getInternalPath());
		targetFilepath.append(destCollection.getImagePath()).append(File.separator);
		getAsyncExecutor().copyResourceFolder(sourceFilepath.toString(), targetFilepath.toString());
	}

	private Collection copyCollection(Collection sourceCollection, Collection targetCollection, User user, Collection newCollection) {
		Collection destCollection = new Collection();
		if (newCollection.getTitle() != null) {
			destCollection.setTitle(newCollection.getTitle());
		} else {
			destCollection.setTitle(sourceCollection.getTitle());
		}
		destCollection.setCopiedCollectionId(sourceCollection.getGooruOid());
		destCollection.setCollectionType(sourceCollection.getCollectionType());
		destCollection.setDescription(sourceCollection.getDescription());
		destCollection.setNotes(sourceCollection.getNotes());
		destCollection.setLanguage(sourceCollection.getLanguage());
		destCollection.setImagePath(sourceCollection.getImagePath());
		destCollection.setGooruOid(UUID.randomUUID().toString());
		destCollection.setContentType(sourceCollection.getContentType());
		destCollection.setLastModified(new Date(System.currentTimeMillis()));
		destCollection.setCreatedOn(new Date(System.currentTimeMillis()));
		if (newCollection != null && newCollection.getSharing() != null) {
			destCollection.setSharing(newCollection.getSharing());
		} else {
			destCollection.setSharing(sourceCollection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) ? Sharing.ANYONEWITHLINK.getSharing() : sourceCollection.getSharing());
		}
		destCollection.setUser(user);
		destCollection.setOrganization(sourceCollection.getOrganization());
		destCollection.setCreator(sourceCollection.getCreator());
		this.getCollectionDao().save(destCollection);
		// copy resource and question items to collection
		copyCollectionItems(targetCollection, sourceCollection, destCollection, user);
		copyContentMetaAssoc(sourceCollection.getContentId(), destCollection);
		copyContentClassification(sourceCollection.getContentId(), destCollection);
		copyCollectionRepoStorage(sourceCollection, destCollection);
		// copy content meta details
		ContentMeta contentMeta = this.getContentRepository().getContentMeta(sourceCollection.getContentId());
		ContentMeta newContentMeta = new ContentMeta();
		if (contentMeta != null) {
			newContentMeta.setContent(destCollection);
			newContentMeta.setMetaData(contentMeta.getMetaData());
			this.getContentRepository().save(newContentMeta);
		}
		// associating the copied collection to lesson
		CollectionItem newCollectionItem = new CollectionItem();
		newCollectionItem.setItemType(ADDED);
		createCollectionItem(newCollectionItem, targetCollection, destCollection, user);

		return destCollection;
	}

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public GooruImageUtil getGooruImageUtil() {
		return gooruImageUtil;
	}

	public QuestionService getQuestionService() {
		return questionService;
	}

}
