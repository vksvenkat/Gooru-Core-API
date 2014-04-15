/////////////////////////////////////////////////////////////
// CollectionUtil.java
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
package org.ednovo.gooru.application.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.StandardFo;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.rating.RatingService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.search.SearchResult;
import org.ednovo.gooru.domain.service.search.SearchResultContainer;
import org.ednovo.gooru.domain.service.shelf.ShelfService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.shelf.ShelfRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyStoredProcedure;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollectionUtil implements ParameterProperties {

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private RedisService redisService;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private TaxonomyStoredProcedure procedureExecutor;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private ShelfService shelfService;

	@Autowired
	private ShelfRepositoryHibernate shelfRepositoryHibernate;

	@Autowired
	private OperationAuthorizer operationAuthorizer;


	private final static Logger logger = LoggerFactory.getLogger(CollectionUtil.class);

	public void enrichCollectionWithTaxonomyMap(SearchResultContainer collectionResultContainer) {
		if (collectionResultContainer.getSearchResults() != null) {
			for (SearchResult srContainer : collectionResultContainer.getSearchResults()) {
				Content findByContent = this.getContentRepository().findContentByGooruId(srContainer.getId());
				if (findByContent != null) {
					Map<Integer, List<Code>> taxonomyMapByCode = TaxonomyUtil.getTaxonomyMapByCode(findByContent.getTaxonomySet(), taxonomyService);
					srContainer.setTaxonomyMapByCode(taxonomyMapByCode);
					Integer numberOfQuestions = assessmentRepository.getAssessmentQuestionsCount(findByContent.getContentId());
					srContainer.setNumberOfResources(numberOfQuestions);
				}

			}
		}
	}

	public void updateCollaborators(Resource resource, List<String> collaboratorsList, User apiCaller, String predicate, Set<Segment> collectionSegments, Set<AssessmentSegment> quizSegments) {
		if (collaboratorsList != null && collaboratorsList.size() > 0 && !collaboratorsList.isEmpty()) {
			List<User> userList = this.getUserRepository().findByIdentities(collaboratorsList);
			updateCollaborators(resource, userList, apiCaller, predicate, true);
			if (collectionSegments != null && collectionSegments.size() > 0) {
				for (Segment segment : collectionSegments) {
					for (ResourceInstance resourceInstance : segment.getResourceInstances()) {
						if (resourceInstance != null) {
							updateCollaborators(resourceInstance.getResource(), userList, apiCaller, predicate, false);
						}
					}
				}
			} else if (quizSegments != null && quizSegments.size() > 0) {
				for (AssessmentSegment quizSegment : quizSegments) {
					for (AssessmentSegmentQuestionAssoc quizSegmentQuestionAssoc : quizSegment.getSegmentQuestions()) {
						if (quizSegmentQuestionAssoc != null) {
							updateCollaborators(quizSegmentQuestionAssoc.getQuestion(), userList, apiCaller, predicate, false);
						}
					}
				}
			}

		}

	}

	public void updateCollaborators(Resource resource, List<User> userList, User apiCaller, String predicate, boolean addToShelf) {

		Set<ContentPermission> contentPermissions = resource.getContentPermissions();
		List<User> newUsers = new ArrayList<User>();
		if (contentPermissions == null) {
			contentPermissions = new HashSet<ContentPermission>();
		}
		if (userList != null && userList.size() > 0) {
			Date date = new Date();
			for (User user : userList) {
				if (!user.getGooruUId().equals(resource.getUser().getGooruUId())) {
					boolean newFlag = true;
					for (ContentPermission contentPermission : contentPermissions) {
						if (contentPermission.getParty().getPartyUid().equals(user.getPartyUid())) {
							newFlag = false;
							break;
						}
					}
					if (newFlag) {
						ContentPermission contentPerm = new ContentPermission();
						contentPerm.setParty(user);
						contentPerm.setContent(resource);
						contentPerm.setPermission(EDIT);
						contentPerm.setValidFrom(date);
						contentPermissions.add(contentPerm);
						if (!newUsers.contains(user)) {
							newUsers.add(user);
						}
						if (addToShelf) {
							this.getShelfService().addCollaboratorShelf(contentPerm);
						}
					}
				}
			}
			if (addToShelf) {
				if (newUsers.size() > 0) {
					sendMailToCollabrators(newUsers, resource, apiCaller);
				}
			}
		}
		Set<ContentPermission> removePermissions = new HashSet<ContentPermission>();
		for (ContentPermission contentPermission : contentPermissions) {
			boolean remove = true;
			if (userList != null) {
				for (User user : userList) {
					if (user.getPartyUid().equals(contentPermission.getParty().getPartyUid())) {
						remove = false;
						break;
					}
				}
			}
			if (remove) {
				removePermissions.add(contentPermission);
			}
		}
		if (removePermissions.size() > 0) {
			this.getShelfService().removeCollaboratorShelf(removePermissions);
			contentPermissions.removeAll(removePermissions);
			this.getBaseRepository().removeAll(removePermissions);
		}

		this.getBaseRepository().save(resource);
	}

	public List<User> updateNewCollaborator(Content content, List<String> collaboratorsList, User apiCaller, String predicate, String collaboratorOperation) {
		List<User> userList = null;
		if (collaboratorsList != null && collaboratorsList.size() > 0 && !collaboratorsList.isEmpty()) {
			userList = this.getUserRepository().findByIdentities(collaboratorsList);
			if (collaboratorOperation.equals(DELETE)) {
				deleteCollaborators(content, userList, apiCaller, predicate);
			} else {
				addNewCollaborators(content, userList, apiCaller, predicate, false);
			}
		}
		if (userList.size() > 0) {
			for (User user : userList) {
				user.setEmailId(user.getIdentities().iterator().next().getExternalId());
			}
			return userList;
		}
		return null;
	}

	public User updateNewCollaborators(Collection collection, List<String> collaboratorsList, User apiCaller, String predicate, String collaboratorOperation) {
		List<User> userList = null;
		if (collaboratorsList != null && collaboratorsList.size() > 0 && !collaboratorsList.isEmpty()) {
			userList = this.getUserRepository().findByIdentities(collaboratorsList);
			if (collaboratorOperation.equals(DELETE)) {
				deleteCollaborators(collection, userList, apiCaller, predicate);
			} else {
				addNewCollaborators(collection, userList, apiCaller, predicate, false);
			}
		}
		if (userList.size() > 0) {
			User user = userList.get(0);
			user.setEmailId(user.getIdentities().iterator().next().getExternalId());
			return user;
		}
		return null;
	}

	public void deleteCollaborators(Content content, List<User> userList, User apiCaller, String predicate) {

		Set<ContentPermission> contentPermissions = content.getContentPermissions();
		Set<ContentPermission> removePermissions = new HashSet<ContentPermission>();

		for (ContentPermission contentPermission : contentPermissions) {
			for (User user : userList) {
				if (user.getPartyUid().equalsIgnoreCase(contentPermission.getParty().getPartyUid())) {
					removePermissions.add(contentPermission);
					break;
				}
			}

		}
		if (removePermissions.size() > 0) {
			contentPermissions.removeAll(removePermissions);
			this.getBaseRepository().removeAll(removePermissions);
		}

		this.getBaseRepository().saveAll(contentPermissions);
		this.getBaseRepository().flush();

	}

	public void addNewCollaborators(Content content, List<User> userList, User apiCaller, String predicate, boolean addToShelf) {

		Set<ContentPermission> contentPermissions = content.getContentPermissions();

		if (contentPermissions == null) {
			contentPermissions = new HashSet<ContentPermission>();
		}

		if (userList != null && userList.size() > 0) {
			Date date = new Date();
			for (User user : userList) {
				if (!user.getGooruUId().equals(content.getUser().getGooruUId())) {
					boolean newFlag = true;
					for (ContentPermission contentPermission : contentPermissions) {
						if (contentPermission.getParty().getPartyUid().equals(user.getPartyUid())) {
							newFlag = false;
							break;
						}
					}
					if (newFlag) {
						ContentPermission contentPerm = new ContentPermission();
						contentPerm.setParty(user);
						contentPerm.setContent(content);
						contentPerm.setPermission(EDIT);
						contentPerm.setValidFrom(date);
						contentPermissions.add(contentPerm);
					}
				}
			}
		}
		if (contentPermissions != null && contentPermissions.size() > 0) {
			content.setContentPermissions(contentPermissions);
		}
	}

	public ShelfRepositoryHibernate getShelfRepositoryHibernate() {
		return shelfRepositoryHibernate;
	}

	public void setShelfRepositoryHibernate(ShelfRepositoryHibernate shelfRepositoryHibernate) {
		this.shelfRepositoryHibernate = shelfRepositoryHibernate;
	}

	public ShelfService getShelfService() {
		return shelfService;
	}

	public void setShelfService(ShelfService shelfService) {
		this.shelfService = shelfService;
	}

	public List<AssessmentQuestion> getAllDataOfQuestions(SearchResultContainer collectionResultContainer) {
		if (collectionResultContainer.getSearchResults() != null) {
			String assessmentGooruOid = "";
			int count = 0;
			for (SearchResult srContainer : collectionResultContainer.getSearchResults()) {
				if (count > 0) {
					assessmentGooruOid += ",";
				}
				assessmentGooruOid += "'" + srContainer.getId().toString() + "'";
				count++;
			}
			return assessmentRepository.getAssessmentQuestionsByAssessmentGooruOids(assessmentGooruOid);
		}
		return null;
	}

	public boolean hasCollaboratorPermission(Learnguide learnguide, User user) {
		List<User> userList = learnguideRepository.findCollaborators(learnguide.getGooruOid(), user.getPartyUid());
		return userList.size() > 0 ? true : false;
	}

	public boolean hasRelatedContentPlayPermission(Learnguide learnguide, User user) {
		if (learnguide == null) {
			// To an empty collection people don't have access!
			return false;
		}

		boolean hasCollaboratorPermission = hasCollaboratorPermission(learnguide, user);

		boolean hasUnrestrictedContentAccess = getOperationAuthorizer().hasUnrestrictedContentAccess();

		if (hasUnrestrictedContentAccess || learnguide.getSharing().equalsIgnoreCase(PUBLIC) || hasCollaboratorPermission || learnguide.getUser().getUserId() == user.getUserId() || hasSubOrgPermission(learnguide.getOrganization().getPartyUid())) {
			return true;
		}
		return false;
	}

	public boolean hasSubOrgPermission(String contentOrganizationId) {
		String[] subOrgUids = UserGroupSupport.getUserOrganizationUids();
		if (subOrgUids != null && subOrgUids.length > 0) {
			for (String userSuborganizationId : subOrgUids) {
				if (contentOrganizationId.equals(userSuborganizationId)) {
					return true;
				}
			}
		}
		return false;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public void setContentRepository(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public void setTaxonomyRepository(TaxonomyRespository taxonomyRepository) {
		this.taxonomyRepository = taxonomyRepository;
	}

	public JSONObject getContentSocialData(User user, String contentGooruOid) throws JSONException {
		JSONObject socialDataJSON = new JSONObject();
		Integer contentUserRating = ratingService.getContentRatingForUser(user.getPartyUid(), contentGooruOid);
		boolean isContentAlreadySubscribed = this.getShelfService().hasContentSubscribed(user, contentGooruOid);
		List<HashMap<String, String>> subscriptions = this.getSubscribtionUserList(contentGooruOid);
		Rating rating = ratingService.findByContent(contentGooruOid);
		socialDataJSON.put(CONTENT_USER_RATING, contentUserRating);
		socialDataJSON.put(IS_CONTENT_ALREADY_SUBSCRIBED, isContentAlreadySubscribed);
		socialDataJSON.put(CONTENT_RATING, new JSONObject(rating).put(VOTE_UP, rating.getVotesUp()));
		socialDataJSON.put(SUBSCRIPTION_COUNT, subscriptions.size());
		socialDataJSON.put(SUBSCRIPTION_LIST, new JSONArray(SerializerUtil.serializeToJson(subscriptions)));
		return socialDataJSON;
	}

	public JSONObject getContentTaxonomyData(Set<Code> taxonomySet, String contentGooruOid)  {
		JSONObject collectionTaxonomy = new JSONObject();
		try {
		Iterator<Code> iter = taxonomySet.iterator();
		Set<String> subject = new HashSet<String>();
		Set<String> course = new HashSet<String>();
		Set<String> unit = new HashSet<String>();
		Set<String> topic = new HashSet<String>();
		Set<String> lesson = new HashSet<String>();
		List<String> curriculumCode = new ArrayList<String>();
		List<String> curriculumDesc = new ArrayList<String>();
		List<String> curriculumName = new ArrayList<String>();
		while (iter.hasNext()) {
			Code code = iter.next();
			try {
				this.getProcedureExecutor().setCode(code);
				Map codeMap = this.getProcedureExecutor().execute();

				String codeLabel = (String) codeMap.get(CODE_LABEL);
				String[] taxonomy = codeLabel.split("\\$\\@");

				int length = taxonomy.length;
				if (length > 1) {
					subject.add(taxonomy[length - 2]);
				}
				if (length > 2 && code.getRootNodeId() != null && code.getRootNodeId().toString().equalsIgnoreCase(Code.GOORU_TAXONOMY_CODE_ID)) {
					course.add(taxonomy[length - 3]);
				}
				if (length > 3) {
					unit.add(taxonomy[length - 4]);
				}
				if (length > 4) {
					topic.add(taxonomy[length - 5]);
				}
				if (length > 5) {
					lesson.add(taxonomy[length - 6]);
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}

		if (taxonomySet != null) {
			for (Code code : taxonomySet) {
				if (code.getRootNodeId() != null && UserGroupSupport.getTaxonomyPreference() != null && UserGroupSupport.getTaxonomyPreference().contains(code.getRootNodeId().toString())) {
					String codeOrDisplayCode = "";
					if (code.getCommonCoreDotNotation() != null && !code.getCommonCoreDotNotation().equals("")) {
						codeOrDisplayCode = code.getCommonCoreDotNotation().replace(".--", " ");
					} else if (code.getdisplayCode() != null && !code.getdisplayCode().equals("")) {
						codeOrDisplayCode = code.getdisplayCode().replace(".--", " ");
					}
					if (!curriculumCode.contains(codeOrDisplayCode)) {
						// string replace has been added to fix the ".--" issue
						// code
						// in USCCM (US Common Core Math - Curriculum)
						curriculumCode.add(codeOrDisplayCode);
						if (code.getLabel() != null && !code.getLabel().equals("")) {
							curriculumDesc.add(code.getLabel());
						} else {
							curriculumDesc.add(BLANK + codeOrDisplayCode);
						}
						Code rootNode = this.getTaxonomyRepository().findCodeByCodeId(code.getRootNodeId());
						if (rootNode == null) {
							logger.error("FIXME: Taxonomy root was found null for code id" + code.getRootNodeId());
							continue;
						}
						String curriculumLabel = this.getTaxonomyRepository().findRootLevelTaxonomy(rootNode);
						curriculumName.add(curriculumLabel);
					}
				}
			}
		}
		JSONObject curriculumTaxonomy = new JSONObject();
		curriculumTaxonomy.put(CURRICULUM_CODE, curriculumCode).put(CURRICULUM_DESC, curriculumDesc).put(CURRICULUM_NAME, curriculumName);
		collectionTaxonomy.put(SUBJECT, subject);
		collectionTaxonomy.put(COURSE, course);
		collectionTaxonomy.put(TOPIC, topic);
		collectionTaxonomy.put(UNIT, unit);
		collectionTaxonomy.put(LESSON, lesson);
		collectionTaxonomy.put(CURRICULUM, curriculumTaxonomy);
		} catch (Exception e) { 
			logger.error("failed to fetch ");
		}
		return collectionTaxonomy;
	}

	// Form the custom fieldname and value map
	public Map<String, String> getCustomFieldNameAndValueAsMap(HttpServletRequest request) {
		Map<String, String> customFieldsAndValues = new HashMap<String, String>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if ((paramName.startsWith("cf.")) && (request.getParameter(paramName) != null)) {
				customFieldsAndValues.put(paramName.replace("cf.", ""), request.getParameter(paramName));

			}
		}
		return customFieldsAndValues;
	}

	public Segment setSegmentImageAbsolutePath(Resource resource, Segment segment) {
		if (resource != null && segment != null && segment.getSegmentImage() != null) {
			segment.setSegmentImage(resource.getAssetURI() + resource.getFolder() + Constants.SEGMENT_FOLDER + "/" + segment.getSegmentImage());
		}
		return segment;
	}

	public List<HashMap<String, String>> getSubscribtionUserList(String gooruOid) {
		List<ShelfItem> shelfItemList = shelfService.getShelfSubscribeUserList(gooruOid);
		List<HashMap<String, String>> subscriptions = new ArrayList<HashMap<String, String>>();
		if (shelfItemList != null) {
			for (ShelfItem shelfItem : shelfItemList) {
				User user = this.getUserRepository().findByGooruId(shelfItem.getShelf().getUserId());
				if (user != null) {
					if (shelfItem.getResource().getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId())) {
						continue;
					}
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put(SUBSCRIBED_ON, shelfItem.getCreatedOn().toString());
					hMap.put(CONT_USER_ID, shelfItem.getResource().getUser().getGooruUId());
					hMap.put(CONT_FIRSTNAME, user.getFirstName());
					hMap.put(CONT_LASTNAME, user.getLastName());
					hMap.put(SCB_USER_ID, shelfItem.getShelf().getUserId());
					subscriptions.add(hMap);
				}
			}
		}
		return subscriptions;
	}

	public Set<StandardFo> getContentStandards(Set<Code> taxonomySet, String contentGooruOid) {
		Set<StandardFo> standards = new HashSet<StandardFo>();
		if (taxonomySet != null) {
			for (Code code : taxonomySet) {
				if (code.getRootNodeId() != null && UserGroupSupport.getTaxonomyPreference() != null && UserGroupSupport.getTaxonomyPreference().contains(code.getRootNodeId().toString())) {
					StandardFo standardFo = new StandardFo();
					if (code.getLabel() != null && !code.getLabel().equals("")) {
						standardFo.setDescription(code.getLabel());
					}
					if (code.getCommonCoreDotNotation() != null && !code.getCommonCoreDotNotation().equals("")) {
						standardFo.setCode(code.getCommonCoreDotNotation().replace(".--", " "));
					} else if (code.getdisplayCode() != null && !code.getdisplayCode().equals("")) {
						standardFo.setCode(code.getdisplayCode().replace(".--", " "));
					}
					standards.add(standardFo);
				}
			}
		}
		return standards;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public void setBaseRepository(BaseRepository baseRepository) {
		this.baseRepository = baseRepository;
	}

	public AssessmentRepository getAssessmentRepository() {
		return assessmentRepository;
	}

	public void setAssessmentRepository(AssessmentRepository assessmentRepository) {
		this.assessmentRepository = assessmentRepository;
	}

	public void deleteCollectionFromCache(String collectionId, String prefix) {
		// Remove the collection from cache
		final String cacheKey = prefix + "-" + collectionId;
		getRedisService().deleteKey(cacheKey);
	}

	public TaxonomyStoredProcedure getProcedureExecutor() {
		return procedureExecutor;
	}

	public void setProcedureExecutor(TaxonomyStoredProcedure procedureExecutor) {
		this.procedureExecutor = procedureExecutor;
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public void setOperationAuthorizer(OperationAuthorizer operationAuthorizer) {
		this.operationAuthorizer = operationAuthorizer;
	}

	private void sendMailToCollabrators(List<User> users, Resource resource, User apiCaller) {
		try {
			String flag = "";
			String collectionOrQuizTitle = "";
			Learnguide learnguide = learnguideRepository.findByContent(resource.getGooruOid());
			Assessment assessment = assessmentRepository.findQuizContent(resource.getGooruOid());
			if (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType())) {
				collectionOrQuizTitle = learnguide.getLesson();
				if (collectionOrQuizTitle == null) {
					collectionOrQuizTitle = learnguide.getTitle();
				}
				flag = COLLECTION;
			} else if (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
				collectionOrQuizTitle = assessment.getName();
				if (collectionOrQuizTitle == null) {
					collectionOrQuizTitle = assessment.getTitle();
				}
				flag = "quiz";
			}
			for (User user : users) {

				mailHandler.sendMailForCollaborator(user.getPartyUid(), apiCaller.getUsername(), resource.getGooruOid(), collectionOrQuizTitle, flag);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}

	public RedisService getRedisService() {
		return redisService;
	}
	
	
}
