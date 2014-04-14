/////////////////////////////////////////////////////////////
// FeedbackRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Feedback;


public interface FeedbackRepository extends BaseRepository {
	Feedback getFeedback(String feedbackId);

	List<Feedback> getFeedbacks(String feedbackId, String gooruUid);
	
	Feedback getFeedback(String feedbackId, String gooruUid);

	Feedback getContentFeedback(String  type, String assocGooruOid, String gooruUid);

	Feedback getUserFeedback(String type, String assocUserUid, String gooruUid);

	List<Feedback> getContentFeedbacks(String type, String assocGooruOid, String creatorUid, String category, Integer limit, Integer offset,Boolean skipPagination);
	
	Long getContentFeedbacksCount(String type, String assocGooruOid, String creatorUid, String category);

	List<Feedback> getUserFeedbacks(String type, String assocUserUid, String creatorUid, String category, Integer limit, Integer offset);

	List<Feedback> getFeedbacks(String feedbackTargetType, String feedbackType, String feedbackCreatorUid, Integer limit, Integer offset);

	Map<String, Object> getUserFeedbackRating(String assocUserUid, String feedbackRatingType);
	
	Map<String, Object> getContentFeedbackRating(String assocGooruOid,  String feedbackRatingType);
	
	Map<Object, Object> getContentFeedbackThumbs(String assocGooruOid,  String feedbackRatingType);
	
	Map<Object, Object> getUserFeedbackThumbs(String assocUserUid,  String feedbackRatingTypeId);
	
	Integer getContentFeedbackAggregateByType(String assocGooruOid,  String feedbackTypeId);
	
	Integer getUserFeedbackAggregateByType(String assocUserUid,  String feedbackTypeId);
	
	Map<Object, Object> getUserFeedbackAverage(String assocUserUid,   String feedbackCategoryId);
	
	Map<Object, Object> getContentFeedbackAverage(String assocGooruOid,   String feedbackCategoryId);
	
	List<Map<Object, Object>> getContentFeedbackAggregate(String assocGooruOid,   String feedbackCategoryId, Boolean flag);
	
	List<CustomTableValue> getCustomValues(String type);
	
	Map<String, Object> getContentFlags(Integer limit,Integer offset,Boolean skipPagination,String category,String type,String status,String getContentFlags, String startDate, String endDate, String searchQuery, String description, String reportQuery);

}
