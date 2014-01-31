/////////////////////////////////////////////////////////////
// FeedbackService.java
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
package org.ednovo.gooru.domain.service;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.User;

public interface FeedbackService extends BaseService {
	Feedback createFeedback(Feedback feedback, User user);
	
	List<Feedback> createFeedbacks(Feedback feedback, User user);

	List<Feedback> updateFeedback(String feedbackId, Feedback newFeedback);

	Feedback getFeedback(String feedbackId);
	
	List<CustomTableValue> getCustomValues(String category, String type);

	void deleteFeedback(String feedbackId, String gooruUid);

	Feedback getContentFeedback(String type, String assocGooruOid, String gooruUid);

	Feedback getUserFeedback(String typeId, String assocUserUid, String gooruUid);

	List<Feedback> getContentFeedbacks(String feedbackCategory, String feedbackType, String assocGooruOid, String creatorUid, Integer limit, Integer offset,Boolean skipPagination);

	List<Feedback> getUserFeedbacks(String feedbackCategory, String feedbackType, String assocUserUid, String creatorUid, Integer limit, Integer offset);

	List<Feedback> getFeedbacks(String feedbackCategory, String feedbackTargetType, String feedbackType, String feedbackCreatorUid, Integer limit, Integer offset);

	Map<String, Object> getUserFeedbackStarRating(String assocUserUid);

	Map<String, Object> getContentFeedbackStarRating(String assocGooruOid);

	Map<Object, Object> getUserFeedbackThumbRating(String assocUserUid);

	Map<Object, Object> getContentFeedbackThumbRating(String assocGooruOid);

	Integer getContentFeedbackAggregateByType(String assocGooruOid, String feedbackType);

	Integer getUserFeedbackAggregateByType(String assocUserUid, String feedbackType);

	Map<Object, Object> getUserFeedbackAverage(String assocUserUid, String feedbackCategory);

	Map<Object, Object> getContentFeedbackAverage(String assocGooruUid, String feedbackCategory);
	
	List<Map<Object, Object>> getContentFeedbackAggregate(String assocGooruUid, String feedbackCategory);
	
	Map<String, Object> getFlags(Integer limit, Integer offset, Boolean skipPagination,String category,String type,String status,String reportedFlagType);
}
