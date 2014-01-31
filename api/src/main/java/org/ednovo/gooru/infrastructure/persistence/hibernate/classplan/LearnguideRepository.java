/////////////////////////////////////////////////////////////
// LearnguideRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.classplan;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;


public interface LearnguideRepository extends BaseRepository {

	/*
	 * Classplan level methods
	 */

	List<Object> findByUser(User user, ResourceType.Type type);

	List<Object> findAllLearnguides(ResourceType.Type type);

	List<Learnguide> findRecentlyModifiedLearnguides();

	/**
	 * Returns list of learnguides giiven the resource id
	 * 
	 * @param gooruResourceContentId
	 * @return
	 */
	List<Learnguide> findByResource(String gooruResourceContentId, String sharing);

	Learnguide findByContent(String gooruContentId);

	List<User> findCollaborators(String gooruContentId, String userId);

	List<Learnguide> findAllClassplans();

	List<Learnguide> findRecentLearnguideByUser(User user, String sharing);

	List<Learnguide> listLearnguides(Map<String, String> filters);

	List<ResourceInstance> listCollectionResourceInstances(Map<String, String> filters);

	List<Learnguide> listPublishedCollections(String userGooruId);

	/*
	 * Resource level methods
	 */

	List<ResourceInstance> listCollectionResourceInstance(Map<String, String> filters);

	List<Resource> listCollectionResources(Map<String, String> filters);

	List<String> getAssessmentQuestionConcept(String keyword);

	List<String> getResourceInstanceIds(String gooruContentId);

	String findCollectionNameByGooruOid(String gooruOid);

	List<Segment> listCollectionSegments(Map<String, String> filters);

	List<Learnguide> listAllCollectionsWithoutGroups(Map<String, String> filters);

	List<Learnguide> findAllCollectionByResourceID(String resourceId);

	List<Learnguide> getUserCollectionInfo(String gooruUId, Map<String, String> filters);

	List<String> findAllCollaboratorByResourceID(String userId, String searchText);

}
