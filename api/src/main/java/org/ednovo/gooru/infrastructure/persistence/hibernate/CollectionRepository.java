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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.ContentMetaAssociation;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserCollectionItemAssoc;

public interface CollectionRepository extends BaseRepository {

	List<Collection> getCollections(Map<String, String> filters, User user);

	List<Classpage> getClasspage(Map<String, String> filters, User user);

	List<CollectionItem> getCollectionItems(String collectionId, Map<String, String> filters);

	Collection getCollectionByGooruOid(String gooruOid, String gooruUid);

	CollectionItem getCollectionItemById(String collectionItemId);

	List<Collection> getCollectionsByResourceId(String resourceGooruOid);

	Collection getUserShelfByGooruUid(String gooruUid, String type);

	Classpage getClasspageByGooruOid(String gooruOid, String gooruUid);

	List<Classpage> getClasspages(Integer offset, Integer limit, String title, String author, String userName);

	Classpage getClasspageByCode(String classpageCode);

	Classpage getUserShelfByClasspageGooruUid(String gooruUid, String type);

	List<Collection> getMyCollection(Map<String, String> filters, User user);

	List<Collection> getMyCollection(String offset, String limit, String type, String filter, User user);

	List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy);

	List<String> getCollectionGooruOidsByResourceId(long contentId);

	List<CollectionItem> getCollectionItemByResourceId(Long resourceId);

	List<Collection> getMyCollection(Integer limit, Integer offset, String orderBy, String fetchType, String resourceType, User user);

	List<CollectionItem> getMyCollectionItems(Map<String, String> filters, User user);

	List<CollectionItem> getCollectionItems(String collectionId, Integer offset, Integer limit, String orderBy, String type);

	Long getCollectionItemsCount(String collectionId, String orderBy, String type);

	Resource findResourceCopiedFrom(String gooruOid, String gooruUid);

	Long getClasspageCount(String title, String author, String userName);

	Long getMyClasspageCount(String gooruUid);

	List<Object[]> getMyFolder(String gooruUid, Integer limit, Integer offset, String sharing, String collectionType, boolean fetchChildItem, String orderBy, String excludeType);

	Long getMyShelfCount(String gooruUid, String sharing, String collectionType, String excludeType);

	List<Object[]> getCollectionItem(String gooruOid, Integer limit, Integer offset, String sharing, String orderBy, String collectionType, boolean fetchChildItem, String sequenceOrder, boolean fetchAll, String excludeType);

	Long getCollectionItemCount(String gooruOid, String sharing, String collectionType, String excludeType);

	List<CollectionItem> findCollectionByResource(String gooruOid, String gooruUid, String type);

	Long getClasspageCollectionCount(String classpageGooruOid, String status, String userUid, String orderBy, String type);

	List<CollectionItem> getCollectionItemByAssociation(String resourceGooruOid, String gooruUid, String collectionType);

	List<CollectionItem> getCollectionItemByParentId(String collectionGooruOid, String gooruUid, String collectionType);

	CollectionItem findCollectionItemByGooruOid(String gooruOid, String gooruUid, String type);

	UserCollectionItemAssoc getUserCollectionItemAssoc(String collectionItemId, String userUid);

	Object[] getParentCollection(String collectionGooruOid, String gooruUid);

	CollectionItem getNextCollectionItemResource(String collectionId, int sequence, String excludeType, String sharing, boolean excludeCollaboratorCollection);

	Long getPublicCollectionCount(String gooruOid, String sharing);

	List<Collection> getCollectionListByIds(List<String> collectionIds);

	List<Object[]> getFolderList(Integer limit, Integer offset, String gooruOid, String title, String gooruUid);

	Long getFolderListCount(String gooruOid, String title, String username);

	List<ContentMetaAssociation> getContentMetaById(String gooruOid, String type);

	ContentMetaAssociation getContentMetaByValue(String value, String collectionId);

	List<Object[]> getClasspageItems(String gooruOid, Integer limit, Integer offset, String userUid, String orderBy, String status, String type);

	List<Collection> getCollectionsList(User user, Integer limit, Integer offset, String publishStatus);

	Long getCollectionCount(String publishStatus);

	Collection getCollectionByIdWithType(String gooruOid, String type);

	List<Object[]> getClasspageAssoc(Integer offset, Integer limit, String classpageId, String collectionId, String gooruUid, String title, String collectionTitle, String classCode, String collectionItemId);

	BigInteger getClasspageAssocCount(String classpageId, String collectionId, String gooruUid, String title, String collectionTitle, String classCode, String collectionItemId);

	Long getClasspageCount(String gooruOid, String type);

	List<Object[]> getParentDetails(String collectionItemId);

	CollectionItem getCollectionItemByResourceOid(String collectionId, String resourceId);

	List<Collection> getCollectionByResourceOid(String resourceId);

	CollectionItem getCollectionItemByResource(String resourceId);

	List<CollectionItem> getCollectionItemsByResource(String resourceId);

	List<CollectionItem> getResetSequenceCollectionItems(String collectionId, int sequence);
}
