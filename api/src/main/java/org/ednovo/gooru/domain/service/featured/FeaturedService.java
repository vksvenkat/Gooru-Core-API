/////////////////////////////////////////////////////////////
// FeaturedService.java
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
package org.ednovo.gooru.domain.service.featured;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.FeaturedSet;
import org.ednovo.gooru.core.api.model.FeaturedSetItems;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface FeaturedService {

	List<FeaturedSet> getFeaturedList(int limit, boolean ramdom, String featuredSetName, String themeCode) throws Exception;

	void getFeaturedResource(List<FeaturedSet> featuredSet) throws Exception;

	FeaturedSet saveOrUpdateFeaturedSet(Integer featuredSetId, String name, Boolean activeFlag, Integer sequence, String themeCode) throws Exception;

	FeaturedSetItems saveOrUpdateFeaturedSetItems(FeaturedSet featuredSet, String gooruContentId, Integer featuredSetItemId, String parentGooruContentId, Integer sequence) throws Exception;

	List<FeaturedSet> getFeaturedTheme(int limit) throws Exception;

	List<FeaturedSet> getFeaturedList(int limit, boolean random, String featuredSetName, String themeCode, String themetype) throws Exception;

	FeaturedSetItems updateFeaturedContent(String type, Integer featuredSetItemId, FeaturedSetItems featuredSetItems);
	
	Map<Object, Object> getLibrary(String type, String libraryName);
	
	List<Map<Object, Object>> getLibraryContributor(String libraryName);
	
	List<Map<String, Object>> getLibraryTopic(String topicId, Integer limit, Integer offset, String type, String libraryName, String rootNode);
	
	List<Map<String, Object>> getLibraryUnit(String unitId, String type, Integer offset, Integer limit, String libraryName, String rootNode);
	
	List<Map<String, Object>> getLibraryCollection(Integer id, String type,Integer offset, Integer limit, boolean skipPagination, String libraryName,String rootNodeId);
	
	List<Map<String, Object>> getAllLibraryCollections(Integer limit, Integer offset, boolean skipPagination, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId);
	
	SearchResults<Map<String, Object>> getLibraryCollections(Integer limit, Integer offset, boolean skipPagination, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId);
	
	List<Map<String, Object>> getPopularLibrary(String courseId,  Integer offset, Integer limit,  String libraryName);

	List<Map<String, Object>> getLibraryCourse(String code,String ChildCode, String libraryName, String rootNode);
	
	List<Map<String,Object>> getCommunityLibraryResource(String type, Integer offset, Integer limit, boolean skipPagination,String libraryName);

	SearchResults<Map<String, Object>> getLibraryResource(String type, Integer offset, Integer limit, boolean skipPagination, String libraryName);
}
