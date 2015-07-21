/////////////////////////////////////////////////////////////
// FeedbackServiceImpl.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.Feedback;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSummary;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotAllowedException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.eventlogs.FeedbackEventLog;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.FeedbackRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackServiceImpl extends BaseServiceImpl implements FeedbackService, ParameterProperties, ConstantProperties {

	@Autowired
	private FeedbackRepository feedbackRepository;
	
	@Autowired
	private FeedbackEventLog feedbackEventLog;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private AsyncExecutor asyncExecutor;
	
	@Autowired
	private CollectionRepository collectionRepository;


	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private IndexHandler indexHandler;
	

	@Override
	public Feedback createFeedback(Feedback feedback, User user) {
		List<Feedback> feedbacks = setFeedbackData(feedback, user);
		return feedbacks.get(0);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Feedback> updateFeedback(String feedbackId, Feedback newFeedback, User user) {
		List<Feedback> feedbacks = this.getFeedbackRepository().getFeedbacks(feedbackId, null);
		List<Feedback> feedbackList = new ArrayList<Feedback>();
		if (feedbacks.size() == 0) {  
			throw new NotFoundException("Feedback not found", GL0056);
		}
		StringBuilder feedbackValue = new StringBuilder();
		if (feedbacks != null && user != null) {
			for (Feedback feedback : feedbacks) {
				if (this.getUserManagementService().isContentAdmin(user) || feedback.getCreator().getPartyUid().equals(user.getPartyUid())) {
					if (newFeedback.getReferenceKey() != null) {
						feedback.setReferenceKey(newFeedback.getReferenceKey());
					}
					if (newFeedback.getFreeText() != null) {
						feedback.setFreeText(newFeedback.getFreeText());
					}
					if (newFeedback.getNotes() != null) {
						feedback.setNotes(newFeedback.getNotes());
					}
					if (newFeedback.getScore() != null) {
						if (feedback.getCategory().getValue().equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && feedback.getType().getValue().equalsIgnoreCase(CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType())) {
							if (newFeedback.getScore() > MAX_RATING_POINT) {
								throw new BadRequestException(generateErrorMessage(GL0044, RATING_POINTS, MAX_RATING_POINT.toString()));
							} else if (newFeedback.getScore() < MIN_RATING_POINT) {
								throw new BadRequestException(generateErrorMessage(GL0044, RATING_POINTS, MIN_RATING_POINT.toString()));
							}
							feedback.setScore(newFeedback.getScore());
						} else if (feedback.getCategory().getValue().equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()) && feedback.getType().getValue().equalsIgnoreCase(CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType())) {
							if (newFeedback.getScore() != THUMB_UP && newFeedback.getScore() != THUMB_DOWN && newFeedback.getScore() != THUMB_NETURAL) {
								throw new BadRequestException(generateErrorMessage(GL0007, THUMB_SCORE));
							}
							feedback.setScore(newFeedback.getScore());
						}
					}
					feedback.setLastModifiedOn(new Date());
					feedbackList.add(feedback);
					feedbackValue.append(feedback.getType().getValue());	
				} else {
					throw new UnauthorizedException(generateErrorMessage(GL0058, USER, UPDATE));
				}
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, FEEDBACK));
		}
		this.getFeedbackRepository().saveAll(feedbackList);
		for (Feedback feedback : feedbacks) {
			ResourceSummary resourceSummary = updateResourceSummary(feedback.getAssocGooruOid());
			this.getFeedbackRepository().save(resourceSummary);
			feedback.setRatings(this.collectionService.setRatingsObj(this.getResourceRepository().getResourceSummaryById(feedback.getAssocGooruOid())));
			Resource resource = this.getResourceRepository().findResourceByContentGooruId(feedback.getAssocGooruOid());
			if (resource != null && resource.getContentId() != null) {
				if (resource.getResourceType() != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
					indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);						
				} else {
					indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);						
				}
				this.getAsyncExecutor().clearCache(resource.getGooruOid());
			}
		}
		return feedbacks;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteFeedback(String feedbackId, User user) throws Exception {
		Feedback feedback = this.getFeedbackRepository().getFeedback(feedbackId);
		
		if(feedback == null){
			throw new NotFoundException(generateErrorMessage(GL0056, FEEDBACK), GL0056);
		} else {
			
			if (this.getUserManagementService().isContentAdmin(user) || (feedback.getCreator() != null && feedback.getCreator().getPartyUid().equals(user.getPartyUid()))) {
				this.getFeedbackRepository().remove(feedback);
			} else {
				throw new UnauthorizedException(generateErrorMessage(GL0057, FEEDBACK), GL0057);
			}
		}
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(feedback.getAssocGooruOid());
		if (resource != null && resource.getContentId() != null) {
			updateResourceSummary(resource.getGooruOid());
			if (resource.getResourceType() != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
				indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);						
			} else {
				indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);						
			}
			this.getAsyncExecutor().clearCache(resource.getGooruOid());
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Feedback getFeedback(String feedbackId) {
		return this.getFeedbackRepository().getFeedback(feedbackId);
	}

	@Override
	public Feedback getContentFeedback(String type, String assocGooruOid, String gooruUid) {
		return this.getFeedbackRepository().getContentFeedback(type, assocGooruOid, gooruUid);
	}

	@Override
	public Feedback getUserFeedback(String type, String assocUserUid, String gooruUid) {
		return this.getFeedbackRepository().getUserFeedback(type, assocUserUid, gooruUid);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public SearchResults<Feedback> getContentFeedbacks(String feedbackCategory, String feedbackType, String assocGooruOid, String creatorUid, Integer limit, Integer offset, String orderBy) {
		String type = null;
		String category = null;
		
		if (feedbackType == null){
			category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + feedbackCategory;
		} else {
			type = getTableNameByFeedbackCategory(feedbackCategory, CustomProperties.Target.CONTENT.getTarget()) + "_" + feedbackType;
		}

		SearchResults<Feedback> result = new SearchResults<Feedback>();
		result.setSearchResults(this.getFeedbackRepository().getContentFeedbacks(type, assocGooruOid, creatorUid, category, limit, offset, orderBy));
		result.setTotalHitCount(this.getFeedbackRepository().getContentFeedbacksCount(type, assocGooruOid, creatorUid, category));
		return result;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Feedback> getUserFeedbacks(String feedbackCategory, String feedbackType, String assocUserUid, String creatorUid, Integer limit, Integer offset) {
		String type = null;
		String category = null;
		
		if (feedbackType == null) {
			category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + feedbackCategory;
		} else {
			type = getTableNameByFeedbackCategory(feedbackCategory, CustomProperties.Target.USER.getTarget()) + "_" + feedbackType;
		}
		rejectIfNull(this.getUserRepository().findByGooruId(assocUserUid), GL0056, _USER);
		return this.getFeedbackRepository().getUserFeedbacks(type, assocUserUid, creatorUid, category, limit, offset);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Feedback> getFeedbacks(String feedbackCategory, String feedbackTargetType, String feedbackType, String feedbackCreatorUid, Integer limit, Integer offset) {
		String feedbackTarget = CustomProperties.Table.TARGET.getTable() + "_" + feedbackTargetType;
		String type = getTableNameByFeedbackCategory(feedbackCategory, feedbackTargetType) + "_" + feedbackType;
		rejectIfNull(type, GL0007, feedbackType + TYPE);
		return this.getFeedbackRepository().getFeedbacks(feedbackTarget, type, feedbackCreatorUid, limit, offset);
	}

	private Feedback validateFeedbackData(Feedback feedback) {
		rejectIfNull(feedback.getCategory(), GL0006, _CATEGORY);
		rejectIfNull(feedback.getCategory().getValue(), GL0006, _CATEGORY);
		rejectIfNull(feedback.getTarget(), GL0006, feedback.getCategory().getValue() + TARGET);
		rejectIfNull(feedback.getTarget().getValue(), GL0006, feedback.getCategory().getValue() + TARGET);
		
		if(feedback.getTypes() == null || feedback.getTypes() != null && (feedback.getCategory().getValue().equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory()))){
			rejectIfNull(feedback.getType(), GL0006, feedback.getCategory().getValue() + TYPE);
			rejectIfNull(feedback.getType().getValue(), GL0006, feedback.getCategory().getValue() + TYPE);
		} else {
			rejectIfNull(feedback.getTypes(), GL0006, ATLEAST_ONE + feedback.getCategory().getValue() + TYPE);
		}
		
		if (feedback.getTypes() == null) {
			List<CustomTableValue> types = new ArrayList<CustomTableValue>();
			types.add(feedback.getType());
			feedback.setTypes(types);
		}
		if (feedback.getProduct() != null && feedback.getProduct().getValue() != null) {
			CustomTableValue product = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.PRODUCT_TYPE.getTable(), feedback.getProduct().getValue());
			feedback.setProduct(product);
		}
		CustomTableValue feedbackTarget = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.TARGET.getTable(), feedback.getTarget().getValue());
		rejectIfNull(feedbackTarget, GL0007, feedback.getCategory().getValue() + TARGET_TYPE);
		feedback.setTarget(feedbackTarget);
		CustomTableValue feedbackCategory = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.FEEDBACK_CATEGORY.getTable(), feedback.getCategory().getValue());
		rejectIfNull(feedbackCategory, GL0007, CATEGORY);
		feedback.setCategory(feedbackCategory);
		return feedback;
	}

	private List<Feedback> setFeedbackData(Feedback feedback, User creator) {
		List<Feedback> feedbackList = new ArrayList<Feedback>();
		List<Feedback> feedbackSetList = new ArrayList<Feedback>();
		feedback = validateFeedbackData(feedback);
		User user = null;
		Content content = null;
		if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.USER.getTarget())) {
			rejectIfNull(feedback.getAssocUserUid(), GL0006, _USER);
			user = this.getUserRepository().findByGooruId(feedback.getAssocUserUid());
			rejectIfNull(user, GL0056, _USER);
		}
		if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.CONTENT.getTarget())) {
			rejectIfNull(feedback.getAssocGooruOid(), GL0006, CONTENT);
			content = this.getContentRepository().findContentByGooruId(feedback.getAssocGooruOid());
			rejectIfNull(content, GL0056, _CONTENT);
		}
		StringBuilder feedbackValue = new StringBuilder();
		for (CustomTableValue feedbackTypes : feedback.getTypes()) {
			Feedback copyFeedback = new Feedback(feedback);
			copyFeedback.setCreator(creator);
			copyFeedback.setCreatedDate(new Date(System.currentTimeMillis()));
			copyFeedback.setOrganization(creator.getPrimaryOrganization());
			CustomTableValue feedbackType = this.getCustomTableRepository().getCustomTableValue(getTableNameByFeedbackCategory(feedback.getCategory().getValue(), feedback.getTarget().getValue()), feedbackTypes.getValue());
			rejectIfNull(feedbackType, GL0007, feedback.getCategory().getValue() + TYPE);
			copyFeedback.setType(feedbackType);
			copyFeedback = handleRating(copyFeedback);
			if (!handleFlag(copyFeedback, feedbackType)) {
				feedbackList.add(copyFeedback);
			} else {
				feedbackSetList.add(copyFeedback);
			}
			feedbackValue.append(feedbackTypes.getValue());
		}
		
		this.getFeedbackRepository().saveAll(feedbackList);
		if(feedbackSetList.size() > 0) {
			feedbackList.addAll(feedbackSetList);
		}
		if (content != null) {
			CustomTableValue statusType = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.CONTENT_STATUS_TYPE.getTable(), CustomProperties.ContentStatusType.OPEN.getContentStatusType());
			content.setStatusType(statusType);
			this.getCustomTableRepository().save(content);
		}
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(feedback.getAssocGooruOid());
		if (resource != null && resource.getContentId() != null) {
			if (resource.getResourceType() != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
				indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION, null, false, false);						
			} else {
				indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);						
			}
			this.getAsyncExecutor().clearCache(resource.getGooruOid());
		}

		return feedbackList;

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Feedback> createFeedbacks(Feedback feedback, User user) {
		return setFeedbackData(feedback, user);
	}

	private boolean  handleFlag(Feedback feedback, CustomTableValue feedbackType) {
	boolean alreadyExist = false;
		if (!feedback.getCategory().getValue().equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory())) {
			if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.USER.getTarget())) {
				Feedback userFeedback = this.getFeedbackRepository().getUserFeedback(feedbackType.getKeyValue(), feedback.getAssocUserUid(), feedback.getCreator().getGooruUId());
				if (userFeedback != null) {
					throw new NotAllowedException(generateErrorMessage("GL0092", feedbackType.getDisplayName()));
				}
			}

			if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.CONTENT.getTarget())) {
				Feedback contentFeedback = this.getFeedbackRepository().getContentFeedback(feedbackType.getKeyValue(), feedback.getAssocGooruOid(), feedback.getCreator().getGooruUId());
				if (contentFeedback != null) {
					alreadyExist = true;
				}
			}
		}
		return alreadyExist;
	}

	private Feedback handleRating(Feedback feedback) {
		if (feedback.getCategory().getValue().equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory())) {
			if (feedback.getType().getValue().equalsIgnoreCase(CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType())) {
				rejectIfNull(feedback.getScore(), GL0006, _RATING_POINTS);
				if (feedback.getScore() > MAX_RATING_POINT) {
					throw new BadRequestException(generateErrorMessage(GL0044, RATING_POINTS, MAX_RATING_POINT.toString()));
				}
				if (feedback.getScore() < MIN_RATING_POINT) {
					throw new BadRequestException(generateErrorMessage(GL0044, RATING_POINTS, MIN_RATING_POINT.toString()));
				}
				return setScore(feedback, feedback.getType());
			}
			if (feedback.getType().getValue().equalsIgnoreCase(CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType())) {
				rejectIfNull(feedback.getScore(), GL0006, _RATING_POINTS);
				if (feedback.getScore() != THUMB_UP && feedback.getScore() != THUMB_DOWN && feedback.getScore() != THUMB_NETURAL) {
					throw new BadRequestException(generateErrorMessage(GL0007, THUMB_SCORE));
				}
				return setScore(feedback, feedback.getType());
			}
		}
		return feedback;
	}

	private Feedback setScore(Feedback feedback, CustomTableValue feedbackType) {
		if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.USER.getTarget())) {
			Feedback userFeeback = this.getFeedbackRepository().getUserFeedback(feedbackType.getKeyValue(), feedback.getAssocUserUid(), feedback.getCreator().getGooruUId());
			if (userFeeback != null) {
				userFeeback.setScore(feedback.getScore());
				if (feedback.getFreeText() != null) {
					userFeeback.setFreeText(feedback.getFreeText());
				}
				this.getFeedbackRepository().save(userFeeback);
				return userFeeback;
			}
		}
		if (feedback.getTarget().getValue().equalsIgnoreCase(CustomProperties.Target.CONTENT.getTarget())) {
			Feedback contentFeedback = this.getFeedbackRepository().getContentFeedback(feedbackType.getKeyValue(), feedback.getAssocGooruOid(), feedback.getCreator().getGooruUId());
			if (contentFeedback != null) {
				contentFeedback.setScore(feedback.getScore());
				if (feedback.getFreeText() != null) {
					contentFeedback.setFreeText(feedback.getFreeText());
				}
				feedback = contentFeedback;
			}
			this.getFeedbackRepository().save(feedback);
			this.getFeedbackRepository().flush();
			ResourceSummary resourceSummary = updateResourceSummary(feedback.getAssocGooruOid());
			this.getFeedbackRepository().save(resourceSummary);
			Map<String, Object> summary = this.getContentFeedbackStarRating(feedback.getAssocGooruOid());
			summary.put(REVIEW_COUNT, resourceSummary.getReviewCount());
			feedback.setRatings(summary);
		}
		return feedback;
	}

	@Override
	public ResourceSummary updateResourceSummary(String assocGooruOid) {
		ResourceSummary resourceSummary = this.getResourceRepository().getResourceSummaryById(assocGooruOid);
		Map<String, Object> summary = this.getContentFeedbackStarRating(assocGooruOid);
		Long reviewSummary = this.getContentFeedbackReviewCount(assocGooruOid);
		if (resourceSummary == null) {
			resourceSummary = new ResourceSummary();
			resourceSummary.setResourceGooruOid(assocGooruOid);
		}
		resourceSummary.setRatingStarCount((Double) summary.get(COUNT));
		resourceSummary.setRatingStarAvg((Long) summary.get(AVERAGE));
		resourceSummary.setReviewCount(reviewSummary);
		this.getFeedbackRepository().save(resourceSummary);
		
		return resourceSummary;
	}

	
	private String getTableNameByFeedbackCategory(String category, String target) {
		String feedbackTypeTableName = null;
		if (category != null && category.equalsIgnoreCase(CustomProperties.FeedbackCategory.RATING.getFeedbackCategory())) {
			feedbackTypeTableName = CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable();
		} else if (category != null && category.equalsIgnoreCase(CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory()) && target != null && target.equalsIgnoreCase(CustomProperties.Target.CONTENT.getTarget())) {
			feedbackTypeTableName = CustomProperties.Table.FEEDBACK_REPORT_CONTENT_TYPE.getTable();
		} else if (category != null && category.equalsIgnoreCase(CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory()) && target != null && target.equalsIgnoreCase(CustomProperties.Target.USER.getTarget())) {
			feedbackTypeTableName = CustomProperties.Table.FEEDBACK_REPORT_USER_TYPE.getTable();
		} else if (category != null && category.equalsIgnoreCase(CustomProperties.FeedbackCategory.FLAG.getFeedbackCategory())) {
			feedbackTypeTableName = CustomProperties.Table.FEEDBACK_OTHER_TYPE.getTable();
		} else if (category != null && category.equalsIgnoreCase(CustomProperties.FeedbackCategory.REACTION.getFeedbackCategory())) {
			feedbackTypeTableName = CustomProperties.Table.FEEDBACK_REACTION.getTable();
		}
		return feedbackTypeTableName;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getUserFeedbackStarRating(String assocUserUid) {
		String feedbackType = CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType();
		rejectIfNull(this.getUserRepository().findByGooruId(assocUserUid), GL0056, _USER);
		return this.getFeedbackRepository().getUserFeedbackRating(assocUserUid, feedbackType);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getFlags(Integer limit, Integer offset, String category, String type, String status, String reportedFlagType, String startDate, String endDate, String searchQuery, String description, String reportQuery) throws Exception {
		return this.getFeedbackRepository().getContentFlags(limit, offset, getTableNameByFeedbackCategory(CustomProperties.FeedbackCategory.REPORT.getFeedbackCategory(), CustomProperties.Target.CONTENT.getTarget()), type, status, reportedFlagType,
				BaseUtil.dateFormat(startDate, "/", "-"), BaseUtil.dateFormat(endDate, "/", "-"), searchQuery, description, reportQuery);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getContentFeedbackStarRating(String assocGooruOid) {
		String feedbackType = CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType();
		return this.getFeedbackRepository().getContentFeedbackRating(assocGooruOid, feedbackType);
	}
	
	@Override
	public Long getContentFeedbackReviewCount(
			String assocGooruOid) {
		String feedbackType = CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.STAR.getFeedbackRatingType();
		return this.getFeedbackRepository().getContentFeedbackReviewCount(assocGooruOid, feedbackType);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<Object, Object> getUserFeedbackThumbRating(String assocUserUid) {
		String feedbackType = CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType();
		rejectIfNull(feedbackType, GL0006, CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType() + TYPE);
		rejectIfNull(this.getUserRepository().findByGooruId(assocUserUid), GL0056, _USER);
		return this.getFeedbackRepository().getUserFeedbackThumbs(assocUserUid, feedbackType);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<Object, Object> getContentFeedbackThumbRating(String assocGooruOid) {
		rejectIfNull(this.getContentRepository().findContentByGooruId(assocGooruOid), GL0056, _CONTENT);
		return this.getFeedbackRepository().getContentFeedbackThumbs(assocGooruOid, CustomProperties.Table.FEEDBACK_RATING_TYPE.getTable() + "_" + CustomProperties.FeedbackRatingType.THUMB.getFeedbackRatingType());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Integer getContentFeedbackAggregateByType(String assocGooruOid, String feedbackType) {
		String type = CustomProperties.Table.FEEDBACK_OTHER_TYPE.getTable() + "_" + feedbackType;
		rejectIfNull(this.getContentRepository().findContentByGooruId(assocGooruOid), GL0056, _CONTENT);
		return this.getFeedbackRepository().getContentFeedbackAggregateByType(assocGooruOid, type);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Integer getUserFeedbackAggregateByType(String assocUserUid, String feedbackType) {
		String type = CustomProperties.Table.FEEDBACK_OTHER_TYPE.getTable() + "_" + feedbackType;
		rejectIfNull(type, GL0006, feedbackType + TYPE);
		rejectIfNull(this.getUserRepository().findByGooruId(assocUserUid), GL0056, _USER);
		return this.getFeedbackRepository().getUserFeedbackAggregateByType(assocUserUid, type);
	}

	@Override
	public Map<Object, Object> getUserFeedbackAverage(String assocUserUid, String feedbackCategory) {
		String category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + feedbackCategory;
		return this.getFeedbackRepository().getUserFeedbackAverage(assocUserUid, category);
	}

	@Override
	public Map<Object, Object> getContentFeedbackAverage(String assocGooruUid, String feedbackCategory) {
		String category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + feedbackCategory;
		rejectIfNull(category, GL0006, feedbackCategory + CATEGORY);
		return this.getFeedbackRepository().getContentFeedbackAverage(assocGooruUid, category);
	}

	@Override
	public List<Map<Object, Object>> getContentFeedbackAggregate(String assocGooruUid, String feedbackCategory) {
		String category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + feedbackCategory;
		rejectIfNull(category, GL0006, feedbackCategory + CATEGORY);
		return this.getFeedbackRepository().getContentFeedbackAggregate(assocGooruUid, category, false);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<CustomTableValue> getCustomValues(String category, String type) {
		return this.getCustomTableRepository().getCustomValues(getTableNameByFeedbackCategory(category, type));
	}

	public FeedbackRepository getFeedbackRepository() {
		return feedbackRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}
	
	public FeedbackEventLog getFeedbackEventLog() {
		return feedbackEventLog;
	}
	
	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

}
