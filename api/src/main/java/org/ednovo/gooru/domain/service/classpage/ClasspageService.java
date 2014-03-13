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

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface ClasspageService {

	ActionResponseDTO<Classpage> createClasspage(Classpage classpage, boolean addToUserClasspage, String assignmentId) throws Exception;

	ActionResponseDTO<Classpage> createClasspage(Classpage newClasspage, CollectionItem collectionItem, String gooruOid, User user) throws Exception;
	
	ActionResponseDTO<Classpage> createClasspage(Classpage classpage, String collectionId) throws Exception ;

	ActionResponseDTO<CollectionItem> createClasspageItem(String assignmentGooruOid, String collectionGooruOid, CollectionItem collectionItem, User user, String type) throws Exception;

	List<Classpage> getClasspage(Map<String, String> filters, User user);

	ActionResponseDTO<Classpage> updateClasspage(Classpage newClasspage, String classpageId, Boolean hasUnrestrictedContentAccess) throws Exception;

	Classpage getClasspage(String classpageId, User user, String merge);

	SearchResults<Classpage> getClasspages(Integer offset, Integer limit, Boolean skipPagination, User user, String title, String author, String userName);

	Classpage getClasspage(String classpageCode, User user) throws Exception;
	
	void deleteClasspage(String classpageId);

	ActionResponseDTO<Classpage> createClasspage(Classpage newclasspage, User user, boolean addToUserClasspage, String assignmentId) throws Exception;

	List<Classpage> getMyClasspage(Integer offset, Integer limit, User user, boolean skipPagination, String orderBy);

	Long getMyClasspageCount(String gooruUid);
	
	List<Map<String, Object>> classpageUserJoin(String code,List<String> gooruUid,User apiCaller) throws Exception;
	
	void classpageUserRemove(String code,List<String> gooruUid,User apiCaller) throws Exception;

}
