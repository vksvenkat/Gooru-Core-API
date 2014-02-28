/////////////////////////////////////////////////////////////
// CollectionRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assignment;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;

public interface CollectionRepository extends BaseRepository {

	List<Collection> getCollections(Map<String, String> filters, User user);
	
	List<Classpage> getClasspage(Map<String, String> filters, User user);

	List<Assignment> getAssignments(Map<String, String> filters, User user);

	List<CollectionItem> getCollectionItems(String collectionId, Map<String, String> filters);

	Collection getCollectionByGooruOid(String gooruOid, String gooruUid);

	CollectionItem getCollectionItemById(String collectionItemId);

	List<Collection> getCollectionsByResourceId(String resourceGooruOid);

	Collection getUserShelfByGooruUid(String gooruUid, String type);

	Assignment getAssignmentByGooruOid(String gooruOid, String gooruUid);

	Classpage getClasspageByGooruOid(String gooruOid, String gooruUid);

	List<Classpage> getClasspages(Integer offset, Integer limit, Boolean skipPagination, String title, String author, String userName);

	Classpage getClasspageByCode(String classpageCode);

	Assignment getAssignmentUserShelfByGooruUid(String gooruUid, String type);

	Classpage getUserShelfByClasspageGooruUid(String gooruUid, String type);

	List<Collection> getMyCollection(Map<String, String> filters, User user);

	List<Collection> getMyCollection(String offset, String limit, String type, String filter, User user);

	List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy);

	List<String> getCollectionGooruOidsByResourceId(long contentId);

	Quiz getQuiz(String gooruOid, String gooruUid, String type);

	List<Quiz> getQuizList(String gooruOid, String gooruUid, String type);

	List<Quiz> getQuizzes(Integer limit, Integer offset);

	List<Quiz> getMyQuizzes(Integer limit, Integer offset, String gooruUid, boolean skipPagination, String orderBy);

	List<CollectionItem> getCollectionItemByResourceId(Long resourceId);

	List<Collection> getMyCollection(Integer limit, Integer offset, String orderBy, String fetchType, String resourceType, boolean skipPagination, User user);

	List<CollectionItem> getMyCollectionItems(Map<String, String> filters, User user);

	List<CollectionItem> getCollectionItems(String collectionId, Integer offset, Integer limit, boolean skipPagination, String orderBy);

	Resource findResourceCopiedFrom(String gooruOid, String gooruUid);

	Long getClasspageCount(String title, String author, String userName);

	Long getMyClasspageCount(String gooruUid);
	
	List<Object[]> getMyFolder(String gooruUid, Integer limit, Integer offset, String sharing);
	
	Long getMyShelfCount(String gooruUid, String sharing);
	
	List<Object[]> getCollectionItem(String gooruOid, Integer limit, Integer offset, boolean SkipPagination, String sharing,String orderBy);
	
	Long getCollectionItemCount(String gooruOid, String sharing);
	
	List<CollectionItem> findCollectionByResource(String gooruOid, String gooruUid, String type);
	
	Long getClasspageCollectionCount(String classpageGooruOid);
	
	List<CollectionItem> getCollectionItemByAssociation(String resourceGooruOid, String gooruUid);
	
	CollectionItem findCollectionItemByGooruOid(String gooruOid, String gooruUid);
	
	String getParentCollection(String collectionGooruOid, String gooruUid);
	
	Long getPublicCollectionCount(String gooruOid);
	
}
