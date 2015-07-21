/////////////////////////////////////////////////////////////
// ClasspageService.java
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
package org.ednovo.gooru.domain.service.classpage;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface ClasspageService {

	Classpage getClasspage(String classpageId, User user, String merge);

	SearchResults<Classpage> getClasspages(Integer offset, Integer limit, User user, String title, String authorGooruUid, String gooruUid);

	Classpage getClasspage(String classpageCode, User user) throws Exception;

	List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy);

	Long getMyClasspageCount(String gooruUid);

	List<Map<String, Object>> getClassMemberList(String gooruOid, String filterBy);

	SearchResults<Map<String, Object>> getMemberList(String code, Integer offset, Integer limit, String filterBy);

	Map<String, List<Map<String, Object>>> getClassMemberListByGroup(String gooruOid, String filterBy);

	List<String> classMemberSuggest(String queryText, String gooruUid);

	SearchResults<Map<String, Object>> getMyStudy(User apiCaller, String orderBy, Integer offset, Integer limit, String type, String itemType);

	List<Map<String, Object>> setMyStudy(List<Object[]> results, String itemType);

	List<Map<String, Object>> getClasspageItems(String gooruOid, Integer limit, Integer offset, User user, String orderBy, boolean optimize, String status, String type);

	Map<String, Object> getClasspageAssoc(Integer offset, Integer limit, String classpageId, String collectionId, String title, String collectionTitle, String classCode, String collectionCreator, String collectionItemId);

	Map<String, Object> getParentDetails(String collectionItemId);

}
