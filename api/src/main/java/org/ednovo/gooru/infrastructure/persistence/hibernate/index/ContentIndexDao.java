/////////////////////////////////////////////////////////////
// ContentIndexDao.java
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
/**
 * 
 */
package org.ednovo.gooru.infrastructure.persistence.hibernate.index;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.Resource;

public interface ContentIndexDao {
	
	Resource findResourceByContentGooruId(String gooruOid);

	List<Object[]> getResourceCollections(Long contentId);

	List<Object[]> getResourceSCollections(Long contentId);

	List<Integer> getCollectionTaxonomyIds(Long contentId);

	List<Object[]> getCollectionItems(Long contentId);

	List<String> getQuizSegments(Long contentId);

	String getCollectionTitle(String contentUid);

	List<String> getQuestionQuiz(String questionGooruOid);

	String getQuizTitle(String gooruOid);

	List<Object[]> getCollectionSegments(Long contentId);

	List<Object[]> getResourceInstances(Set<String> segmentIds);
	
	List<Object[]> getResourceInstances(Long contentId);
	
	Object[] getResourceInfo(Long contentId);
	
	Object[] getCollectionInfo(long contentId);
	
	Object[] getSCollectionInfo(long contentId);
	
	Map<String,Object> getQuizInfo(long contentId);
	
	Object[] getStorageArea(String organizationUid,Boolean s3UploadFlag);

	Map<String, Object> getAnswerAndHint(long contentId);

	List<Object[]> getAssets(long contentId);

	Object[] getRatingByContentId(long contentId);

	Long getSubscriptionCountByContentId(long contentId);
	
	List<String> getCollectionItemIdsByResourceId(Long collectionId);
	
	List<Object[]> getContentProviderAssoc(long contentId);
	
	ContentProvider  getContentProviderlist(String contentProviderId);
	
}
