/////////////////////////////////////////////////////////////
// ResourceRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.resource;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceSummary;
import org.ednovo.gooru.core.api.model.ResourceUrlStatus;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface ResourceRepository extends BaseRepository {
	Resource findWebResource(String url);

	Resource findResourceByContentGooruId(String gooruContentId);

	ResourceSource findResourceByresourceSourceId(Integer resourceSourceId);

	List<Resource> findAllResourceBySourceId(Integer resourceSourceId);

	List<Resource> findWebResourcesForBlacklisting();

	void updateWebResource(Long contentId, Integer status);

	Textbook findTextbookByContentGooruId(String gooruContentId);

	void saveOrUpdate(Resource resource);

	Resource findByFileHash(String fileHash, String typeName, String url, String category);

	ResourceSource findResourceSource(String domainName);

	void updateResourceSourceId(Long contentId, Integer resourceSourceId);

	Resource getResourceByUrl(String url);

	List<ResourceSource> getSuggestAttribution(String keyword);

	ResourceSource getAttribution(String attribution);

	Map<String, Object> findAllResourcesSource(Map<String, String> filters);

	ResourceInfo findResourceInfo(String resourceGooruOid);

	void deleteResourceBulk(String contentIds);

	List<Resource> findAllResourcesByGooruOId(String resourceGooruOIds);

	Resource findResourceByUrl(String resourceUrl, String sharing, String userUid);

	ResourceInfo getResourcePageCount(String resourceId);

	String shortenedUrlResourceCheck(String domainName, String domainType);

	List<Resource> listResourcesUsedInCollections(Map<String, String> filters);

	ResourceMetadataCo findResourceFeeds(String resourceGooruOid);

	List getResourceFlatten(List<Long> contentIds);

	List<Long> findResources(Map<String, String> filters);

	List getResourceFieldValueById(String field, String contentIds);

	List<Resource> listResourcesUsedInCollections(Integer limit, Integer offset);

	Map<String, Object> getContentSubscription(long contentId);

	List<Map<String, Object>> getPartyPermissions(long contentId);

	void saveTextBook(Long contentId, String documentId, String documentKey);

	Textbook findTextbookByContentGooruIdWithNewSession(String gooruOid);

	License getLicenseByLicenseName(String licenseName);

	Resource findResourceByContent(String gooruContentId);

	Resource findLtiResourceByContentGooruId(String gooruContentId);

	List<ContentProvider> getResourceContentProvierList();

	ResourceSummary getResourceSummaryById(String gooruOid);

	List<Collection> getCollectionsByResourceId(String resourceId, String sharing, Integer limit, Integer offset);

	Long getContentId(String contentGooruOid);

	List<Object[]> getContentIds(String gooruOids);
}
