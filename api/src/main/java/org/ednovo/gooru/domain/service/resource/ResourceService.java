/////////////////////////////////////////////////////////////
// ResourceService.java
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
package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.ConverterDTO;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.StatisticsDTO;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.UpdateViewsDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.domain.service.BaseService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.Errors;


public interface ResourceService extends BaseService {

	ResourceInstance saveResourceInstance(ResourceInstance resourceInstance) throws Exception;

	Resource saveResource(Resource resource, Errors errors, boolean findByURL);

	List<Resource> listResources(Map<String, String> filters);

	Resource findResourceByContentGooruId(String gooruContentId);

	Textbook findTextbookByContentGooruId(String gooruContentId);

	void deleteResource(Long contentId);

	Segment reorderResourceInstace(Resource resource, String segmentId, String resourceInstanceId, String newResourceInstancePos, String newSegmentId) throws Exception;

	ResourceInstance getResourceInstance(String resourceInstanceId);

	void deleteSegmentResourceInstance(String resourceInstanceId);

	void deleteSegment(String segmentId, Learnguide collection);

	Segment getSegment(String segmentId);

	Long reorderSegments(String reorder, String gooruResourceId) throws Exception;

	List<ResourceInstance> listResourceInstances(String gooruContentId, String type);

	List<ResourceInstance> listSegmentResourceInstances(String segmentId);

	ResourceInstance getFirstResourceInstanceOfResource(String gooruContentId);

	Resource findWebResource(String url);

	int findViews(String contentGooruId);

	void enrichAndAddOrUpdate(Resource resource);

	List<Resource> splitToChaptersResources(Resource resource);

	Resource handleNewResource(Resource resource, String resourceTypeForPdf, String thumbnail);

	void saveNewResource(Resource resource, boolean downloadResource) throws IOException;

	void updateResourceSource(String resourceTypeString);

	ResourceInstance findResourceInstanceByContentGooruId(String gooruOid);

	void orderCollectionResourceInstances();

	void deleteResourceFromGAT(String gooruContentId, boolean isThirdPartyUser, User apiCaller, boolean isMycontent);

	String updateResourceImage(String gooruContentId, String fileName) throws IOException;

	void deleteResourceImage(String gooruContentId);

	void deleteResourceBulk(String contentIds);
	
	void deleteBulkResource(String contentIds);

	void updateResourceInstanceMetaData(Resource resource, User user);

	ResourceInstance buildResourceInstance(Map<String, Object> resourceParam, Map<String, Object> formField) throws Exception;

	Resource buildMyContent(Map<String, Object> resourceParam, Map<String, Object> formField) throws Exception;

	void replaceDuplicatePrivateResourceWithPublicResource(Resource resource);

	void setDefaultThumbnail(String contentType, int batchSize, int pageSize);

	void incrementViews(String contentGooruId);

	ResourceSource updateSuggestAttribution(String gooruContentId, String attribution);

	void deleteResource(Resource resource, String gooruContentId, User apiCaller);

	void deleteAttribution(Resource resource, String gooruAttributionId, User apiCaller);

	Job saveJob(File sourceFile, ConverterDTO converterDTO, User user);

	Job getResourceTaskJob(String taskId);

	ResourceSource findResourceSource(String domainName);

	ResourceInfo findResourceInfo(String resourceGooruOid);

	Resource saveCrawledResource(String url, String title, String text, String parentUrl, String thumbnail, String attribution, String typeForPdf, String category, String siteName, String tags, String description);

	Resource findResourceByUrl(String resourceUrl, String sharing, String userUid);

	List<Resource> findWebResourcesForBlacklisting();

	Resource addNewResource(String url, String title, String text, String category, String sharing, String type_name, String licenseName, Integer brokenStatus, Boolean hasFrameBreaker, String description, Integer isFeatured, String tags, boolean isReturnJson, User apiCaller, String mediaType, String resource_format, String resource_instructional);

	List<ResourceSource> getSuggestAttribution(String keyword);

	Map<String, Object> findAllResourcesSource(Map<String, String> filters);

	Resource updateResource(String resourceGooruOid, String title, String description, String mediaFilename, String mediaType) throws IOException;

	void saveOrUpdate(Resource resource);

	Resource updateResourceByGooruContentId(String gooruContentId, String resourceTitle, String distinguish, Integer isFeatured, String description, Boolean hasFrameBreaker, String tags, String sharing, Integer resourceSourceId, User user, String mediaType, String attribution, String category,
			String mediaFileName, Boolean isBlacklisted, String grade, String resource_format, String licenseName, String url);

	ActionResponseDTO<Resource> updateResource(String resourceId, Resource newResource, User user) throws Exception;

	void updateResourceSourceAttribution(Integer resourceSourceId, String domainName, String attribution, Integer frameBreaker, User user, Boolean isBlacklisted) throws Exception;

	ResourceSource createResourcesourceAttribution(String domainName, String attribution);

	Resource updateResourceThumbnail(String gooruContentId, String fileName, Map<String, Object> formField) throws FileNotFoundException, IOException;

	Resource getResourceByResourceInstanceId(String resourceInstanceId);

	JSONObject getResourceAnalyticData(String gooruOid, String contentType, User user) throws JSONException;

	ResourceInfo getResourcePageCount(String resourceId);

	String getResourceInstanceNarration(String resourceInstanceId);

	boolean shortenedUrlResourceCheck(String domain);

	List<Resource> listResourcesUsedInCollections(Map<String, String> filters);

	ResourceMetadataCo updateYoutubeResourceFeeds(Resource resource);

	public ResourceMetadataCo updateYoutubeResourceFeeds(Resource resource, boolean isUpdate);

	public Resource updateResourceInfo(Resource resource);

	void mapSourceToResource(Resource resource);

	Map<String, Object> getSuggestedResourceMetaData(String url, String title, boolean fetchThumbnail);

	void saveOrUpdateResourceTaxonomy(Resource resource, Set<Code> taxonomySet);

	void deleteResourceTaxonomy(Resource resource, Set<Code> taxonomySet);

	Resource deleteTaxonomyResource(String resourceId, Resource newResource, User user);

	List<Resource> listResourcesUsedInCollections(String limit, String offset, User user);

	ActionResponseDTO<Resource> createResource(Resource newResource, User user) throws Exception;

	void saveOrUpdateGrade(Resource resource, Resource newResource);

	List<Map<String, Object>> getPartyPermissions(long contentId);

	List<User> addCollaborator(String colletionId, User user, String collaboratorId, String collaboratorOperation);

	List<User> getCollaborators(String collectionId);

	Resource resourcePlay(String gooruContentId, User apiCaller, boolean more) throws Exception;
	
	void updateViewsBulk(List<UpdateViewsDTO> updateViewsDTOs, User apiCaller);
	
	Resource findLtiResourceByContentGooruId(String gooruContentId);
	
	Map<String, Object> getResource(String gooruOid);
	
	Resource setContentProvider(Resource resource);
	
	void updateStatisticsData(List<StatisticsDTO> statisticsList, boolean skipReindex);
	
	List<String> updateContentProvider(String gooruOid, List<String> providerList, User user, String providerType);

	void deleteContentProvider(String gooruOid, String providerType, String name);
	
	ResourceInstance checkResourceUrlExists(String url, boolean checkShortenedUrl) throws Exception;
	
	


}
