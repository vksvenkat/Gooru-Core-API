/////////////////////////////////////////////////////////////
// CollectionService.java
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
/**
 * 
 */
package org.ednovo.gooru.domain.service;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;


/**
 * @author Search Team
 * 
 */
public interface CollectionService extends ScollectionService {

	ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(String collectionId, String data, User user, String questionImgSrc) throws Exception;
	
	ActionResponseDTO<CollectionItem> updateQuestionWithCollectionItem(String collectionItemId, String data, List<Integer> deleteAssets, User user, String questionImgSrc) throws Exception;

	ActionResponseDTO<CollectionItem> moveCollectionToFolder(String sourceId, String taregetId, User user) throws Exception;

	ActionResponseDTO<CollectionItem> createQuestionWithCollectionItem(String collectionId, AssessmentQuestion assessmentQuestion, User user, String questionImgSrc) throws Exception;

	Boolean resourceCopiedFrom(String gooruOid, String gooruUid);
	
	List<Map<String, Object>> getMyShelf(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, Integer itemLimit, boolean fetchChildItem, String topLevelCollectionType);
	
	List<Map<String, Object>> getFolderItem(String gooruOid, String sharing, String type,String collectionType, Integer itemLimit, boolean fetchChildItem);
	
	List<Map<String, Object>> getFolderItems(String gooruOid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, Integer itemLimit, boolean fetchChildItem);
	
	SearchResults<Code> getCollectionStandards(Integer codeId,String query, Integer limit, Integer offset, User user);
	
	Map<String, Object> getFolderList(Integer limit, Integer offset, String gooruOid, String title, String username );
	
	List<CollectionItem> createCollectionItems(List<String> collectionsIds, String resourceId, User user) throws Exception;
	
	List<CollectionItem> assignCollection(String classpageId, String collectionId, User user, String direction, String planedEndDate) throws Exception;	
	
	List<Collection> updateCollectionForPublish(List<Map<String,String>> collection,User user) throws Exception;
	
	List<Collection> updateCollectionForReject(List<Map<String,String>> collection,User user) throws Exception;
	
	SearchResults<Collection> getCollections(Integer offset, Integer limit, User user, String publishStatus);
	
	String getFolderItemsWithCache(String gooruOid, Integer limit, Integer offset, String sharing, String collectionType, String orderBy, Integer itemLimit, boolean fetchChildItem, boolean clearCache,User user);
	
	
}
