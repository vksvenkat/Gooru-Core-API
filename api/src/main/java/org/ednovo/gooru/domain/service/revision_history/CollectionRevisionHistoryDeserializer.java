/////////////////////////////////////////////////////////////
// CollectionRevisionHistoryDeserializer.java
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
package org.ednovo.gooru.domain.service.revision_history;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CollectionRevisionHistoryDeserializer extends RevisionHistoryDeserializer<Collection> implements CollectionSerializerConstants {

	@Autowired
	private TaxonomyRespository taxonomyRespository;
	
	@Autowired ResourceService resourceService;

	private static final Logger logger = LoggerFactory.getLogger(CollectionRevisionHistoryDeserializer.class);
	
	@Override
	protected Collection deserialize(String data) {
		try {
			JSONObject collectionJsonObject = new JSONObject(data);
			Collection collection = new Collection();
			collection.setTitle((String) get(collectionJsonObject, COLLECTION_TITLE));
			collection.setKeyPoints((String) get(collectionJsonObject, COLLECTION_KEY_POINTS));
			collection.setCollectionType((String) get(collectionJsonObject, COLLECTION_TYPE));
			collection.setEstimatedTime((String) get(collectionJsonObject, COLLECTION_ESTIMATED_TIME));
			collection.setGoals((String) get(collectionJsonObject, COLLECTION_GOALS));
			collection.setKeyPoints((String) get(collectionJsonObject, COLLECTION_KEY_POINTS));
			collection.setLanguage((String) get(collectionJsonObject, COLLECTION_LANGUAGE));
			collection.setNotes((String) get(collectionJsonObject, COLLECTION_LANGUAGE));
			collection.setNarrationLink((String) get(collectionJsonObject, COLLECTION_NARRATION_LINK));
			
			getDeserializedResource(collection, collectionJsonObject);
			
			
			JSONArray collectionItemsJsonArray = (JSONArray) get(collectionJsonObject, COLLECTION_ITEM);
			Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
			if(collectionItemsJsonArray != null && collectionItemsJsonArray.length() > 0){
				for (int i = 0; i < collectionItemsJsonArray.length(); i++) {
					JSONObject collectionItemJsonObject = (JSONObject) collectionItemsJsonArray.get(i);
					CollectionItem collectionItem = new CollectionItem();
					collectionItem.setCollectionItemId((String) get(collectionItemJsonObject, COLLECTION_ITEM_ID));
					collectionItem.setItemType((String) get(collectionItemJsonObject, COLLECTION_ITEM_TYPE));
					collectionItem.setItemSequence((Integer) get(collectionItemJsonObject, COLLECTION_ITEM_SEQUENCE));
					collectionItem.setNarration((String) get(collectionItemJsonObject, COLLECTION_ITEM_NARRATION));
					collectionItem.setNarrationType((String) get(collectionItemJsonObject, COLLECTION_ITEM_NARRATION_TYPE));
					collectionItem.setStart((String) get(collectionItemJsonObject, COLLECTION_ITEM_START));
					collectionItem.setStop((String) get(collectionItemJsonObject, COLLECTION_ITEM_STOP));
					collectionItem.setDocumentid((String) get(collectionItemJsonObject, COLLECTION_ITEM_DOCUMENT_ID));
					collectionItem.setDocumentkey((String) get(collectionItemJsonObject, COLLECTION_ITEM_DOCUMENT_KEY));
				
					JSONObject contentJsonObject = (JSONObject) get(collectionItemJsonObject, CONTENT);
					collectionItem.setResource(resourceService.findResourceByContentGooruId((String) get(contentJsonObject, GOORU_OID)));
					
					collectionItems.add(collectionItem);
			}
				collection.setCollectionItems(collectionItems);	
			}
				
			JSONArray contentClassificationsJsonArray = (JSONArray) get(collectionJsonObject, CONTENT_CLASSIFICATIONS);
			if (contentClassificationsJsonArray != null) {
				Set<Code> taxonomySet = new HashSet<Code>();
				for (int i = 0; i < contentClassificationsJsonArray.length(); i++) {
					JSONObject classificationJsonObject = (JSONObject) contentClassificationsJsonArray.get(i);
					Integer codeId = Integer.parseInt((String) get(classificationJsonObject, CONTENT_CLASSIFICATION_CODE_ID));
					Code code = taxonomyRespository.findCodeByCodeId(codeId);
					taxonomySet.add(code);
				}
				collection.setTaxonomySet(taxonomySet);
			}


			JSONArray collaborators = (JSONArray) get(collectionJsonObject, COLLABORATORS);
			List<User> collaboratorsList = new ArrayList<User>();
			for (int j = 0; j < collaborators.length(); j++) {
				User collaborator = getUserRepository().findByGooruId((String) collaborators.get(j));
				if (collaborator != null) {
					collaboratorsList.add(collaborator);
				} else {
					logger.warn("collaborator does not found: " + (String) collaborators.get(j));
				}
			}
			if(collaboratorsList.size() > 0){
				collection.setCollaborators(collaboratorsList);
			}
			
			return collection;
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.COLLECTION;
	}

}
