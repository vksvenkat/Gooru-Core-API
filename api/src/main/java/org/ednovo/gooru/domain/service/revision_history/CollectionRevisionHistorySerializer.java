/////////////////////////////////////////////////////////////
// CollectionRevisionHistorySerializer.java
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

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CollectionRevisionHistorySerializer extends RevisionHistorySerializer<Collection> implements CollectionSerializerConstants {

	@Override
	protected String serialize(Collection entity) {
		JSONObject collectionJsonObject = new JSONObject();
		try {
			collectionJsonObject.put(COLLECTION_TITLE, entity.getTitle());
			collectionJsonObject.put(COLLECTION_TYPE, entity.getCollectionType());
			collectionJsonObject.put(COLLECTION_ESTIMATED_TIME, entity.getEstimatedTime());
			collectionJsonObject.put(COLLECTION_GOALS, entity.getGoals());
			collectionJsonObject.put(COLLECTION_KEY_POINTS, entity.getKeyPoints());
			collectionJsonObject.put(COLLECTION_LANGUAGE, entity.getLanguage());
			collectionJsonObject.put(COLLECTION_NOTES, entity.getNotes());
			collectionJsonObject.put(COLLECTION_NARRATION_LINK, entity.getNarrationLink());
			
			collectionJsonObject.put(COLLABORATORS, getCollaboratorJsonArray(entity.getCollaborators()));
			
			putResourceObject(collectionJsonObject, entity);
			
			if(entity.getCollectionItems() != null && entity.getCollectionItems().size() > 0){
				Iterator<CollectionItem> collectionItems = entity.getCollectionItems().iterator();
				JSONArray collectionItemsJsonArray = new JSONArray();
				while (collectionItems.hasNext()) {
					CollectionItem collectionItem = collectionItems.next();
					JSONObject collectionItemJson = new JSONObject();
					collectionItemJson.put(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());
					collectionItemJson.put(COLLECTION_ITEM_DOCUMENT_ID, collectionItem.getDocumentid());
					collectionItemJson.put(COLLECTION_ITEM_DOCUMENT_KEY, collectionItem.getDocumentkey());
					collectionItemJson.put(COLLECTION_ITEM_NARRATION, collectionItem.getNarration());
					collectionItemJson.put(COLLECTION_ITEM_NARRATION_TYPE, collectionItem.getNarrationType());
					collectionItemJson.put(COLLECTION_ITEM_SEQUENCE, collectionItem.getItemSequence());
					collectionItemJson.put(COLLECTION_ITEM_START, collectionItem.getStart());
					collectionItemJson.put(COLLECTION_ITEM_STOP, collectionItem.getStop());
					collectionItemJson.put(COLLECTION_ITEM_TYPE, collectionItem.getItemType());
					putResourceObject(collectionItemJson, collectionItem.getResource());
					collectionItemsJsonArray.put(collectionItemJson);
				}
				collectionJsonObject.put(COLLECTION_ITEM, collectionItemsJsonArray);
			}
			
			return collectionJsonObject.toString();
		}  catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.COLLECTION;
	}

}
