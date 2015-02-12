/////////////////////////////////////////////////////////////
// RatingServiceImpl.java
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
package org.ednovo.gooru.domain.service.rating;

import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.FeedbackService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.FeedbackRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RatingServiceImpl extends BaseServiceImpl implements RatingService, ParameterProperties,ConstantProperties {

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private IndexProcessor indexerMessenger;

	@Autowired
	private CustomTableRepository customTableRepository;
	
	@Autowired
	private IndexHandler indexHandler;

	@Override
	public Rating findByContent(String gooruContentId) {
		Resource resource = resourceRepository.findResourceByContentGooruId(gooruContentId);
		Feedback feedback = new Feedback();
		return createRating(feedback, resource);
	}
	
	@Override
	public Rating findByContentObj(Resource resource) {
		Feedback feedback = new Feedback();
		return createRating(feedback, resource);
	}

	@Override
	public int getContentRatingForUser(String userId, String gooruOid) {
		Feedback feedback = this.getContentFeebackRating(userId, gooruOid);
		return feedback != null ? feedback.getScore() != null ? feedback.getScore() : 0 : 0;
	}

	@Override
	public boolean hasUserRatedContent(String userId, String gooruContentId) {
		Feedback feedback = this.getContentFeebackRating(userId, gooruContentId);
		return feedback != null ? true : false;
	}

	@Override
	public Rating createRating(String score, String gooruContentId, String type, User apiCaller) {
		Resource resource = resourceRepository.findResourceByContentGooruId(gooruContentId);
		rejectIfNull(resource, GL0056, RESOURCE);
		Feedback feedback = new Feedback();
		feedback.setAssocGooruOid(gooruContentId);
		CustomTableValue feedbackCategory = new CustomTableValue();
		feedbackCategory.setValue(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory());
		CustomTableValue feedbackType = new CustomTableValue();
		feedbackType.setValue(CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType());
		CustomTableValue feedbackTarget = new CustomTableValue();
		feedbackTarget.setValue(CustomProperties.Target.CONTENT.getTarget());
		feedback.setCategory(feedbackCategory);
		feedback.setTarget(feedbackTarget);
		feedback.setType(feedbackType);
		feedback.setScore(Integer.parseInt(score));
		feedback = this.getFeedbackService().createFeedback(feedback, apiCaller);
		indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);		
		return createRating(feedback, resource);
	}

	private Rating createRating(Feedback feedback, Resource resource) {
		Rating rating = new Rating();
		rating.setGooruOid(feedback.getGooruOid());
		rating.setUser(feedback.getCreator());
		rating.setResource(resource);
		Map<Object, Object> thumbs = this.getFeedbackRepository().getContentFeedbackThumbs(resource.getGooruOid(), CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType());
		rating.setVotesUp((Integer) thumbs.get(_THUMB_UP));
		rating.setVotesDown((Integer) thumbs.get(_THUMB_DOWN));
		rating.setCount(0);
		rating.setAverage(0.0);
		return rating;
	}

	public IndexProcessor getIndexerMessenger() {
		return indexerMessenger;
	}

	private Feedback getContentFeebackRating(String gooruUid, String gooruOid) {
		return this.getFeedbackRepository().getContentFeedback(CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType(), gooruOid, gooruUid);
	}

	public FeedbackRepository getFeedbackRepository() {
		return feedbackRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public FeedbackService getFeedbackService() {
		return feedbackService;
	}
}
