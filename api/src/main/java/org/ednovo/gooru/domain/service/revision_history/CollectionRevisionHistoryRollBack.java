/*
*CollectionRevisionHistoryRollBack.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.revision_history;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionRevisionHistoryRollBack extends RevisionHistoryRollBack<Collection> implements ParameterProperties{

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private IndexProcessor indexProcessor;

	private static final Logger logger = LoggerFactory.getLogger(CollectionRevisionHistoryRollBack.class);
	
	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.COLLECTION;
	}

	@Override
	protected Collection rollback(Collection entity, RevisionHistory history) {
		Collection collection = collectionRepository.getCollectionByGooruOid(entity.getGooruOid(), null);
		String revisionImagePath="";
		String revisionFileNamePrefix="";
		if (collection != null) {
			this.merge(entity, collection);
		} else {
			entity.setContentId(null);
			revisionImagePath=entity.getOrganization().getNfsStorageArea().getInternalPath()+entity.getFolder()+entity.getThumbnail();
			revisionFileNamePrefix = StringUtils.substringBeforeLast(entity.getThumbnail(), ".");
			collection = entity;
			collection.setFolder(null);
		}
		entity.setRevisionHistoryUid(history.getRevisionHistoryUid());

		collectionRepository.save(collection);
		for (CollectionItem collectionItem : collection.getCollectionItems()) {
			collectionItem.setCollection(collection);
			collectionRepository.save(collectionItem);
		}
		
		collectionRepository.flush();
		moveCollectionImage(collection,revisionImagePath,revisionFileNamePrefix);
		this.resourceImageUtil.setDefaultThumbnailImageIfFileNotExist((Resource) collection);

		collectionUtil.deleteCollectionFromCache(collection.getGooruOid(), COLLECTION);
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

		return collection;
	}

	@Override
	protected Collection merge(Collection revisionEntity,
			Collection existingEntity) {
		existingEntity.setCollectionType(revisionEntity.getCollectionType());
		existingEntity.setNarrationLink(revisionEntity.getNarrationLink());
		existingEntity.setNotes(revisionEntity.getNotes());
		existingEntity.setKeyPoints(revisionEntity.getKeyPoints());
		existingEntity.setLanguage(revisionEntity.getLanguage());
		existingEntity.setEstimatedTime(revisionEntity.getEstimatedTime());
		existingEntity.setGoals(revisionEntity.getGoals());
		existingEntity.setGrade(revisionEntity.getGrade());
		existingEntity.setNotes(revisionEntity.getNotes());
		existingEntity.setVocabulary(revisionEntity.getVocabulary());
		existingEntity.setTaxonomySet(revisionEntity.getTaxonomySet());
		existingEntity.setCollaborators(revisionEntity.getCollaborators());
		existingEntity.setContentAssociation(revisionEntity.getContentAssociation());
		mergeCollectionItem(existingEntity.getCollectionItems(), revisionEntity.getCollectionItems());
		mergeResource(revisionEntity, existingEntity);
		return existingEntity;
	}

	private Collection moveCollectionImage(Collection collection,String revisionImagePath,String revisionFileNamePrefix) {
		
		String destFolderPath = collection.getOrganization().getNfsStorageArea().getInternalPath()+collection.getFolder();
		try {
			GooruImageUtil.copyImage(revisionImagePath, destFolderPath, revisionFileNamePrefix);
			resourceImageUtil.sendMsgToGenerateThumbnails(collection);
		} catch (IOException e) {
			logger.error("Image move : " + e);
		}
		return collection;
	}
	
	
	private void mergeCollectionItem(Set<CollectionItem> existingCollectionItems, Set<CollectionItem> revisionCollectionItems) {
		SortedSet<CollectionItem> removeExistsCollectionItems = new TreeSet<CollectionItem>();
		
		for(CollectionItem existCollectionItem : existingCollectionItems){
			boolean CollectionItemExists = false;
			for(CollectionItem revisionCollectionItem : revisionCollectionItems){
				if(revisionCollectionItem.getCollectionItemId().equalsIgnoreCase(existCollectionItem.getCollectionItemId())){
					//update exists 
					existCollectionItem.setResource(revisionCollectionItem.getResource());
					existCollectionItem.setCollection(revisionCollectionItem.getCollection());
					existCollectionItem.setItemType(revisionCollectionItem.getItemType());
					existCollectionItem.setNarration(revisionCollectionItem.getNarration());
					existCollectionItem.setNarrationType(revisionCollectionItem.getNarrationType());
					existCollectionItem.setItemSequence(revisionCollectionItem.getItemSequence());
					existCollectionItem.setStart(revisionCollectionItem.getStart());
					existCollectionItem.setStop(revisionCollectionItem.getStop());
					existCollectionItem.setDocumentid(revisionCollectionItem.getDocumentid());

					existCollectionItem.setDocumentkey(revisionCollectionItem.getDocumentkey());
					CollectionItemExists = true;
					revisionCollectionItems.remove(revisionCollectionItem);
					break;
				}
			}
			if(!CollectionItemExists){
				removeExistsCollectionItems.add(existCollectionItem);
			}
		}
		if(removeExistsCollectionItems.size() > 0){
			existingCollectionItems.removeAll(removeExistsCollectionItems);
			collectionRepository.removeAll(removeExistsCollectionItems);
		}
		if(revisionCollectionItems != null && revisionCollectionItems.size() > 0){
			existingCollectionItems.addAll(revisionCollectionItems);
		}
			
	}
	
}
