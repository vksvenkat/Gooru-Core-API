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

import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.CsvCrawler;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
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

	int findViews(String contentGooruId);

	void incrementViews(String contentGooruId);

	List<Resource> findWebResourcesForBlacklisting();

	void updateWebResource(Long contentId, Integer status);

	List<Resource> listResources(Map<String, String> filters);

	Textbook findTextbookByContentGooruId(String gooruContentId);

	void saveOrUpdate(Resource resource);

	Resource findByFileHash(String fileHash, String typeName, String url, String category);

	void retriveAndSetInstances(Resource resource);

	ResourceSource findResourceSource(String domainName);

	void updateResourceSourceId(Long contentId, Integer resourceSourceId);

	ResourceInstance findResourceInstanceByContentGooruId(String gooruOid);

	Resource getResourceByUrl(String url);

	List<ResourceSource> getSuggestAttribution(String keyword);

	ResourceSource getAttribution(String attribution);

	Map<String, Object> findAllResourcesSource(Map<String, String> filters);

	List<Resource> findByContentIds(List<Long> contentIds);

	List<String> getUnorderedInstanceSegments();

	List<ResourceInstance> getUnorderedInstances(String segmentId);

	ResourceInfo findResourceInfo(String resourceGooruOid);

	void insertResourceUrlStatus();

	void deleteResourceBulk(String contentIds);

	String getContentIdsByGooruOIds(String resourceGooruOIds);

	List<Resource> findAllResourcesByGooruOId(String resourceGooruOIds);

	ResourceUrlStatus findResourceUrlStatusByGooruOId(String resourceGooruOId);

	List<ResourceInstance> findResourceInstances(String gooruOid, String userUid);

	Resource findResourceByUrl(String resourceUrl, String sharing, String userUid);

	List<Resource> getResourceListByUrl(String resourceUrl, String sharing, String userUid);

	List<Resource> listAllResourceWithoutGroups(Map<String, String> filters);

	Resource getResourceByResourceInstanceId(String resourceInstanceId);

	List<String> findAllPublicResourceGooruOIds(Map<String, String> filters);

	ResourceInfo getResourcePageCount(String resourceId);

	String getResourceInstanceNarration(String resourceInstanceId);

	CsvCrawler getCsvCrawler(String url, String type);

	void saveCsvCrawler(CsvCrawler csvCrawler);

	boolean findIdIsValid(Class<?> modelClass, String ids);

	String shortenedUrlResourceCheck(String domainName, String domainType);

	List<Resource> listResourcesUsedInCollections(Map<String, String> filters);

	ResourceMetadataCo findResourceFeeds(String resourceGooruOid);

	List getResourceFlatten(List<Long> contentIds);

	List getResourceRatingSubscription(long contentId);

	List<Long> findResources(Map<String, String> filters);

	List getResourceFieldValueById(String field, String contentIds);

	List<Resource> listResourcesUsedInCollections(Integer limit, Integer offset);

	Map<String, Object> getResourceCollectionInfo(long contentId);

	Map<String, Object> getContentSubscription(long contentId);

	List<Map<String, Object>> getPartyPermissions(long contentId);

	List<Long> findValidContentIds(Class<?> modelClass, String ids);

	void saveTextBook(Long contentId, String documentId, String documentKey);

	Textbook findTextbookByContentGooruIdWithNewSession(String gooruOid);
	
	License getLicenseByLicenseName(String licenseName);
	
	Resource findResourceByContent(String gooruContentId);
	
	Resource findLtiResourceByContentGooruId(String gooruContentId);

	List<String> getResourceSourceAttribution();
	
	List<ContentProvider> getResourceContentProvierList();
	
	ResourceSummary getResourceSummaryById(String gooruOid);

}
