/////////////////////////////////////////////////////////////
// FeaturedRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.featured;

import java.util.List;

import org.ednovo.gooru.core.api.model.FeaturedSet;
import org.ednovo.gooru.core.api.model.FeaturedSetItems;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;


public interface FeaturedRepository extends BaseRepository {

	List<FeaturedSet> getFeaturedList(Integer codeId, int limit, String featuredSetName, String themeCode, String themeType);
	
	List<Object[]> getLibraryCollectionsList(Integer limit, Integer offset, String themeCode, String themeType);
	
	List<Object[]> getLibraryCollectionsListByFilter(Integer limit, Integer offset, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId, String gooruOid, String codeId);
	
	Long getLibraryCollectionCount(String themeCode, String themeType, String gooruOid, String codeId);

	List<Integer> getFeaturedThemeIds();

	FeaturedSet getFeaturedSetById(Integer featuredSetId);

	FeaturedSetItems getFeaturedSetItem(Integer featuredSetId, Integer sequence);

	FeaturedSet getFeaturedSetByThemeNameAndCode(String featuresSetThemeName, String featuresSetThemeCode);

	FeaturedSetItems getFeaturedSetItemsByFeatureSetId(Integer featureSetId);

	List<FeaturedSet> getFeaturedTheme(int limit);

	FeaturedSetItems getFeaturedItemByIdAndType(Integer featuredSetItemId, String type);
	
	List<Object[]> getLibrary(String code, boolean fetchAll, String libraryName);
	
	List<Object[]> getLibraryCollection(String codeId, String featuredSetId, Integer limit, Integer offset, String contentId);
	
	Integer getFeaturedSetId(String type);
	
	List<Object[]> getCommunityLibraryResource(String type, Integer offset, Integer limit, String libraryName);

	Long getLibraryResourceCount(String type, String libraryName);
	
	List<Object[]> getLibrary(String libraryName);
	
	void deleteLibraryCollectionAssoc(String featuredSetId, String codeId, String contentId);
	
	FeaturedSet getFeaturedSetByIds(Integer featuredSetId);
}
