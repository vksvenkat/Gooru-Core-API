/*
*LearnguideService.java
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

package org.ednovo.gooru.domain.service.classplan;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.formatter.CollectionFo;
import org.ednovo.gooru.core.application.util.formatter.SegmentFo;
import org.json.JSONException;
import org.json.JSONObject;

public interface LearnguideService {

	List<Object> findByUser(User user, ResourceType.Type type);

	List<Object> findAllLearnguides(ResourceType.Type type);

	List<Learnguide> findByResource(String gooruResourceContentId, String String);

	Learnguide findByContent(String gooruContentId);

	List<User> findCollaborators(String gooruContentId);

	List<Learnguide> findRecentLearnguideByUser(User user, String sharing);

	List<Learnguide> listLearnguides(Map<String, String> filters);

	List<ResourceInstance> listCollectionResourceInstances(Map<String, String> filters);

	List<Learnguide> listPublishedCollections(String userGooruId);

	List<ResourceInstance> listCollectionResourceInstance(Map<String, String> filters);

	List<Resource> listCollectionResources(Map<String, String> filters);

	List<String> getResourceInstanceIds(String gooruContentId);

	String findCollectionNameByGooruOid(String gooruOid);

	List<Segment> listCollectionSegments(Map<String, String> filters);

	String updateCollectionImage(String gooruContentId, String mediaFileName) throws IOException;

	void deleteCollectionBulk(String collectionGooruOIds);

	JSONObject updateContentSharingPermission(User user, String contentGooruOid, String sharing, String type) throws Exception;

	JSONObject resetRequestPending(User user, String contentGooruOid, Integer pendingStatus) throws Exception;

	JSONObject sendRequestForPublishCollection(User user, String contentGooruOid, String message, HttpServletRequest request) throws Exception;

	JSONObject publishCollection(String action, User user, String contentGooruOid) throws Exception;

	Learnguide createNewCollection(String lesson, String grade, String[] taxonomyCode, User user, String type, Map<String, String> customFieldAndValueMap, String lessonObjectives);

	void deleteCollectionThumbnail(String gooruContentId) throws Exception;

	String updateCollectionThumbnail(String gooruContentId, String fileName, String imageURL, Map<String, Object> formField) throws Exception;

	Learnguide copyCollection(String gooruContentId, String collectionTitle, User user, boolean isClassplan, String segmentIds, String targetCollectionId) throws Exception;

	void updateImages(String numberOfImages) throws Exception;

	void saveCollection(Learnguide collection);

	JSONObject getContentSessionActivity(String gooruContentId, String gooruUid) throws JSONException;

	List<Learnguide> getCollectionsOfResource(String resourceId);

	List<CollectionFo> getUserCollectionInfo(String gooruUId, Map<String, String> filters);

	Map<String, Integer> getCollectionPageCount(List<SegmentFo> segments);

	List<String> sendRequestForGetCollaborators(String gooruUId, String searchText);
}
