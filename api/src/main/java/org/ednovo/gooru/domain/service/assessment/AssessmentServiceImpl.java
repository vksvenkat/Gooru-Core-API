/////////////////////////////////////////////////////////////
// AssessmentServiceImpl.java
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
package org.ednovo.gooru.domain.service.assessment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.LogUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentAttempt;
import org.ednovo.gooru.core.api.model.AssessmentAttemptItem;
import org.ednovo.gooru.core.api.model.AssessmentAttemptSummaryDTO;
import org.ednovo.gooru.core.api.model.AssessmentAttemptTry;
import org.ednovo.gooru.core.api.model.AssessmentHint;
import org.ednovo.gooru.core.api.model.AssessmentMetaDataDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Asset;
import org.ednovo.gooru.core.api.model.AttemptQuestionDTO;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentMetaDTO;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionActivityType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.core.application.util.ErrorMessage;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.application.util.ResourceMetaInfo;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.content.ContentService;
import org.ednovo.gooru.domain.service.resource.AssetManager;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.sessionActivity.SessionActivityService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyStoredProcedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

@Service
public class AssessmentServiceImpl implements ConstantProperties, AssessmentService, ParameterProperties {

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	ResourceImageUtil resourceImageUtil;

	@Autowired
	private TaxonomyStoredProcedure procedureExecutor;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	@javax.annotation.Resource(name = "assetManager")
	private AssetManager assetManager;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	CollectionUtil collectionUtil;

	@Autowired
	private IndexProcessor indexProcessor;

	private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentServiceImpl.class);

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	private SessionActivityService sessionActivityService;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private UserService userService;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private AsyncExecutor asyncExecutor;
	
	@Autowired
	private CollectionService collectionService;
	
	@Autowired
	private ResourceService resourceService;
	
	@Override
	public AssessmentQuestion getQuestion(String gooruOQuestionId) {
		return (AssessmentQuestion) assessmentRepository.getByGooruOId(AssessmentQuestion.class, gooruOQuestionId);
	}

	@Override
	public int deleteQuestion(String gooruOQuestionId, User caller) {
		AssessmentQuestion question = getQuestion(gooruOQuestionId);
		if (question != null) {
			if (!assessmentRepository.isQuestionUsedInAttemptItem(gooruOQuestionId) && !assessmentRepository.isQuestionUsedInSegmentQuestion(gooruOQuestionId)) {
				assessmentRepository.remove(AssessmentQuestion.class, question.getContentId());
				indexProcessor.index(question.getGooruOid(), IndexProcessor.DELETE, RESOURCE);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(LogUtil.getActivityLogStream(QUESTION, caller.toString(), question.toString(), LogUtil.QUESTION_DELETE, ""));
				}

				return 1;
			} else {
				return 2;
			}
		}
		return 0;
	}

	@Override
	public List<AssessmentQuestion> listQuestions(Map<String, String> filters) {
		return assessmentRepository.listQuestions(filters);
	}

	@Override
	public ActionResponseDTO<Assessment> createAssessment(Assessment assessment) throws Exception {
		assessment = initAssessment(assessment, null, true, null);

		Errors errors = validateAssessment(assessment);
		if (!errors.hasErrors()) {
			assessmentRepository.save(assessment);

			// this.createRevisionHistoryEntry(assessment.getGooruOid(),
			// "AssessmentCreate");
			this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist((Resource) assessment);

			/*
			 * Commenting this line of code. Organization already saved in
			 * resource level in base class(saveOrUpdate)
			 */

			// s3ResourceApiHandler.updateOrganization(assessment);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(LogUtil.getActivityLogStream(ASSESSMENT, assessment.getUser().toString(), assessment.toString(), LogUtil.ASSESSMENT_CREATE, assessment.getName()));
			}

		}
		return new ActionResponseDTO<Assessment>(assessment, errors);
	}

	@Override
	public ActionResponseDTO<Assessment> updateAssessment(Assessment assessment, String gooruOAssessmentId, boolean copyToOriginal, User apiCaller, boolean shareQuestions) throws Exception {

		assessment = initAssessment(assessment, gooruOAssessmentId, copyToOriginal, apiCaller);

		/*
		 * Errors errors = validateAssessment(assessment); if
		 * (!errors.hasErrors()) {
		 */assessmentRepository.save(assessment);

		this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_UPDATE);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(LogUtil.getActivityLogStream(ASSESSMENT, assessment.getUser().toString(), assessment.toString(), LogUtil.ASSESSMENT_EDIT, assessment.getName()));
		}
		indexProcessor.index(assessment.getGooruOid(), IndexProcessor.INDEX, QUIZ);
		/* } */

		return new ActionResponseDTO<Assessment>(assessment, new BindException(assessment, ASSESSMENT));
	}

	private Assessment initAssessment(Assessment assessment, String gooruOAssessmentId, boolean copyToOriginal, User apiCaller) throws Exception {
		if (copyToOriginal) {
			if (gooruOAssessmentId == null) {
				License license = (License) baseRepository.get(License.class, License.OTHER);
				assessment.setLicense(license);
				assessment.setGooruOid(UUID.randomUUID().toString());
				assessment.setContentId(null);
				assessment.setCreatedOn(new java.util.Date());
				assessment.setUrl("");
				ContentType contentType = (ContentType) baseRepository.get(ContentType.class, ContentType.RESOURCE);
				assessment.setContentType(contentType);
				if (assessment.getDistinguish() == null) {
					assessment.setDistinguish((short) 0);
				}
				if (assessment.getIsFeatured() == null) {
					assessment.setIsFeatured(0);
				}
				if (assessment.getTimeToCompleteInSecs() == null) {
					assessment.setTimeToCompleteInSecs(0);
				}
				if (assessment.getTitle() == null) {
					assessment.setTitle("");
				}
				if (assessment.getDescription() == null) {
					assessment.setDescription("");
				}
				if (assessment.getLearningObjectives() == null) {
					assessment.setLearningObjectives("");
				}
				if (assessment.getShowCorrectAnswer() == null) {
					assessment.setShowCorrectAnswer(true);
				}
				if (assessment.getShowHints() == null) {
					assessment.setShowHints(true);
				}
				if (assessment.getShowScore() == null) {
					assessment.setShowScore(true);
				}
				if (assessment.getIsChoiceRandom() == null) {
					assessment.setIsChoiceRandom(true);
				}
				if (assessment.getIsRandom() == null) {
					assessment.setIsRandom(true);
				}
				if (assessment.getSharing() == null) {
					assessment.setSharing("public");
				}
				if (assessment.getLicense() == null) {
					assessment.setLicense(new License());
					assessment.getLicense().setName(License.OTHER);
				}
				if (assessment.getUrl() == null) {
					assessment.setUrl("");
				}
				if (assessment.getSource() == null) {
					assessment.setSource("");
				}

				if (assessment.getVocabulary() == null) {
					assessment.setVocabulary("");
				}
				if (assessment.getCollectionGooruOid() == null) {
					assessment.setCollectionGooruOid("");
				}
				if (assessment.getRecordSource() == null) {
					assessment.setRecordSource(Resource.RecordSource.DEFAULT.getRecordSource());
				}

			} else {
				Assessment existingAssessment = getAssessment(gooruOAssessmentId);
				if (assessment.getAttempts() != null) {
					existingAssessment.setAttempts(assessment.getAttempts());
				}
				if (assessment.getTaxonomySet() != null) {
					existingAssessment.setTaxonomySet(assessment.getTaxonomySet());
				}
				if (assessment.getIsRandom() != null) {
					existingAssessment.setIsRandom(assessment.getIsRandom());
				}
				if (assessment.getIsChoiceRandom() != null) {
					existingAssessment.setIsChoiceRandom(assessment.getIsChoiceRandom());
				}
				if (assessment.getShowHints() != null) {
					existingAssessment.setShowHints(assessment.getShowHints());
				}
				if (assessment.getShowScore() != null) {
					existingAssessment.setShowScore(assessment.getShowScore());
				}
				if (assessment.getShowCorrectAnswer() != null) {
					existingAssessment.setShowCorrectAnswer(assessment.getShowCorrectAnswer());
				}
				if (assessment.getName() != null) {
					existingAssessment.setName(assessment.getName());
				}
				if (assessment.getIsFeatured() != null) {
					existingAssessment.setIsFeatured(assessment.getIsFeatured());
				} else {
					existingAssessment.setIsFeatured(existingAssessment.getIsFeatured());
				}
				if (assessment.getDistinguish() != null) {
					existingAssessment.setDistinguish(assessment.getDistinguish());
				} else {
					existingAssessment.setDistinguish(existingAssessment.getDistinguish());
				}
				if (assessment.getResourceType() != null) {
					existingAssessment.setResourceType(assessment.getResourceType());
				}
				if (assessment.getSharing() != null) {
					existingAssessment.setSharing(assessment.getSharing());
				}
				if (assessment.getSource() != null) {
					existingAssessment.setSource(assessment.getSource());
				}
				if (assessment.getVocabulary() != null) {
					existingAssessment.setVocabulary(assessment.getVocabulary());
				}
				if (assessment.getCollectionGooruOid() != null) {
					existingAssessment.setCollectionGooruOid(assessment.getCollectionGooruOid());
				}
				if (assessment.getQuizGooruOid() != null) {
					existingAssessment.setQuizGooruOid(assessment.getQuizGooruOid());
				}

				if (apiCaller != null) {
					List<String> collaboratorList = null;
					String collaboratorsStr = assessment.getCollaborators();
					if (collaboratorsStr != null && collaboratorsStr.length() > 0) {
						collaboratorList = Arrays.asList(collaboratorsStr.split("\\s*,\\s*"));
						for (User collaborator : userService.findByIdentities(collaboratorList)) {
							if (userService.checkCollaboratorsPermission(gooruOAssessmentId, collaborator, ASSESSMENT)) {
								existingAssessment.setCollaborators(collaboratorsStr);
							} else {
								throw new Exception("Invalid collaborators!");
							}
						}
					}
				}

				if (assessment.getDescription() != null) {
					existingAssessment.setDescription(assessment.getDescription());
				}
				if (assessment.getTitle() != null) {
					existingAssessment.setTitle(assessment.getTitle());
				}
				if (assessment.getTimeToCompleteInSecs() != null) {
					existingAssessment.setTimeToCompleteInSecs(assessment.getTimeToCompleteInSecs());
				}
				if (assessment.getLearningObjectives() != null) {
					existingAssessment.setLearningObjectives(assessment.getLearningObjectives());
				}
				if (assessment.getMedium() != null) {
					existingAssessment.setMedium(assessment.getMedium());
				}
				if (assessment.getGrade() != null) {
					existingAssessment.setGrade(assessment.getGrade());
				}
				if (assessment.getCreatorGooruUserId() != null || assessment.getOwnerGooruUserId() != null) {

					if (assessment.getCreatorGooruUserId() != null) {
						User user = userRepository.findByGooruId(assessment.getCreatorGooruUserId());
						existingAssessment.setCreator(user);
						if (assessment.getCreatorGooruUserId().equalsIgnoreCase("")) {
							existingAssessment.setCreator(null);
						}
					}
					if (assessment.getOwnerGooruUserId() != null) {
						User user = userRepository.findByGooruId(assessment.getOwnerGooruUserId());
						existingAssessment.setUser(user);
						if (assessment.getOwnerGooruUserId().equalsIgnoreCase("")) {
							existingAssessment.setUser(null);
						}
					}
				}

				assessment = existingAssessment;
			}
			Set<Code> taxonomySet = new HashSet<Code>();
			if (assessment.getTaxonomySet() != null) {
				for (Code code : assessment.getTaxonomySet()) {
					if (code.getCode() != null) {
						Code taxonomy = (Code) taxonomyRepository.findCodeByTaxCode(code.getCode());
						if (taxonomy != null) {
							taxonomySet.add(taxonomy);
						}
					}
				}
				assessment.setTaxonomySet(taxonomySet);
			} else {
				assessment.setTaxonomySet(null);
			}
		}

		ResourceType resourceType = (ResourceType) baseRepository.get(ResourceType.class, assessment.getResourceType().getName());
		assessment.setResourceType(resourceType);
		assessment.setLastModified(new java.util.Date());
		if (assessment.getName() != null) {
			assessment.setTitle(assessment.getName());
		}
		return assessment;
	}

	@Override
	public Assessment getAssessment(String gooruOAssessmentId) {
		Assessment assessment = assessmentRepository.getByGooruOId(Assessment.class, gooruOAssessmentId);
		if (assessment != null) {
			getAssessmentMetaData(assessment);
		}
		return assessment;
	}

	@Override
	public int deleteAssessment(String gooruOAssessmentId, User caller) {
		Assessment assessment = getAssessment(gooruOAssessmentId);
		if (assessment != null) {
			this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_DELETE);
			indexProcessor.index(assessment.getGooruOid(), IndexProcessor.DELETE, QUIZ);
			assessmentRepository.remove(Assessment.class, assessment.getContentId());
			// redisService.deleteEntry(gooruOAssessmentId);
			this.getSessionActivityService().updateSessionActivityByContent(assessment.getGooruOid(), SessionActivityType.Status.ARCHIVE.getStatus());
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(LogUtil.getActivityLogStream(ASSESSMENT, caller.toString(), assessment.toString(), LogUtil.ASSESSMENT_DELETE, ""));
			}
			return 1;
		}
		return 0;
	}

	private void createRevisionHistoryEntry(String assessmentGooruUid, String eventType) {
		Content content = contentService.findContentByGooruId(assessmentGooruUid, false);
		if (content != null && content instanceof Versionable) {
			try {
				getRevisionHistoryService().createVersion((Versionable) content, eventType);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public List<Assessment> listAssessments(Map<String, String> filters) {
		List<Assessment> assessments = new ArrayList<Assessment>();
		for (Assessment assessment : assessmentRepository.listAssessments(filters)) {
			assessments.add(getAssessmentMetaData(assessment));
		}
		return assessments;
	}

	@Override
	public AssessmentAttempt getAttempt(Integer attemptId) {
		return (AssessmentAttempt) assessmentRepository.getModel(AssessmentAttempt.class, attemptId);
	}

	@Override
	public ActionResponseDTO<AssessmentQuestion> createQuestion(AssessmentQuestion question, boolean index) throws Exception {
		Set<Code> taxonomy = question.getTaxonomySet();
		question = initQuestion(question, null, true);
		question.setIsOer(true);
		question.setTaxonomySet(null);
		Errors errors = validateQuestion(question);
		if (!errors.hasErrors()) {
			// To Save Folder
			question.setOrganization(question.getCreator().getOrganization());
			assessmentRepository.save(question);
			resourceService.saveOrUpdateResourceTaxonomy(question, taxonomy);
			if (question.getResourceInfo() != null) {
				resourceRepository.save(question.getResourceInfo());
			}
			s3ResourceApiHandler.updateOrganization(question);

			Assessment assessment = assessmentRepository.getAssessmentQuestion(question.getGooruOid());
			if (assessment != null) {
				this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_QUESTION_CREATE);
				this.getSessionActivityService().updateSessionActivityByContent(assessment.getGooruOid(), SessionActivityType.Status.ARCHIVE.getStatus());
			}
			if (index) {
				try {
					indexProcessor.index(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
				} catch (Exception e) {
					LOGGER.info(e.getMessage());
				}
			}
		}
		return new ActionResponseDTO<AssessmentQuestion>(question, errors);
	}

	@Override
	public ActionResponseDTO<AssessmentQuestion> updateQuestion(AssessmentQuestion question, List<Integer> deleteAssets, String gooruOQuestionId, boolean copyToOriginal, boolean index) throws Exception {
		List<ContentMetaDTO> depth = question.getDepthOfKnowledges();
		List<ContentMetaDTO> educational = question.getEducationalUse();
		
		question = initQuestion(question, gooruOQuestionId, copyToOriginal);
		Errors errors = validateQuestion(question);
		List<Asset> assets = buildQuestionAssets(deleteAssets, errors);

		if (!errors.hasErrors()) {
			Assessment assessment = assessmentRepository.getAssessmentQuestion(question.getGooruOid());
			if (assessment != null) {
				this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_QUESTION_UPDATE);
			}
			assessmentRepository.save(question);

			if(depth != null && depth.size() > 0) {
				question.setDepthOfKnowledges(this.collectionService.updateContentMeta(depth,question.getGooruOid(), question.getUser(), DEPTH_OF_KNOWLEDGE));
			} else {
				question.setDepthOfKnowledges(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), question.getGooruOid(), DEPTH_OF_KNOWLEDGE));
			}
			if(educational != null && educational.size() > 0) {
				question.setEducationalUse(this.collectionService.updateContentMeta(educational,question.getGooruOid(), question.getUser(), EDUCATIONAL_USE));
			} else {
				question.setEducationalUse(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(EDUCATIONAL_USE), question.getGooruOid(), EDUCATIONAL_USE));
			}

			if (question.getResourceInfo() != null) {
				resourceRepository.save(question.getResourceInfo());
			}

			for (Asset asset : assets) {
				assessmentRepository.deleteQuestionAssets(asset.getAssetId());
				assetManager.deletePathIfExist(asset.getOrganization().getNfsStorageArea().getInternalPath() + question.getFolder() + asset.getName());
			}
			if (assets.size() > 0) {
				assessmentRepository.removeAll(assets);
			}
			ResourceMetaInfo resourceMetaInfo = new ResourceMetaInfo();
			resourceMetaInfo.setStandards(collectionService.getStandards(question.getTaxonomySet(), false, null));
			question.setMetaInfo(resourceMetaInfo);
			updateQuestionTime(question);
			if (index) {
				indexProcessor.index(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
			}
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(LogUtil.getActivityLogStream(QUESTION, question.getUser().toString(), question.toString(), LogUtil.QUESTION_EDIT, question.getTitle()));
			}

		}

		return new ActionResponseDTO<AssessmentQuestion>(question, errors);
	}

	private List<Asset> buildQuestionAssets(List<Integer> assets, Errors errors) {
		List<Asset> assetList = new ArrayList<Asset>();
		if (assets != null) {
			for (Integer assetId : assets) {
				Asset asset = (Asset) assessmentRepository.getModel(Asset.class, assetId);
				if (asset != null) {
					assetList.add(asset);
				}
			}
		}
		return assetList;
	}

	private AssessmentQuestion initQuestion(AssessmentQuestion question, String gooruOQuestionId, boolean copyToOriginal) {
		if (copyToOriginal) {
			if (gooruOQuestionId == null) {
				License license = (License) baseRepository.get(License.class, License.OTHER);
				question.setLicense(license);
				ContentType contentType = (ContentType) baseRepository.get(ContentType.class, ContentType.RESOURCE);
				question.setContentType(contentType);
				question.setGooruOid(UUID.randomUUID().toString());
				// Explicitly set to null to reset any content id sent by
				// clients
				question.setContentId(null);
				question.setCreatedOn(new java.util.Date());
				question.setUrl("");
				if (question.getSharing() == null) {
					question.setSharing(PUBLIC);
				}
				if (question.getDistinguish() == null) {
					question.setDistinguish((short) 0);
				}
				if (question.getIsFeatured() == null) {
					question.setIsFeatured(0);
				}
				if (question.getTypeName() == null) {
					question.setTypeName(AssessmentQuestion.TYPE.MULTIPLE_CHOICE.getName());
				}

				if (question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.MATCH_THE_FOLLOWING.getName()) && question.getAnswers().size() > 0) {
					for (AssessmentAnswer assessmentAnswer : question.getAnswers()) {
						for (AssessmentAnswer matchingAnswer : question.getAnswers()) {
							if (assessmentAnswer.getMatchingSequence() != null && assessmentAnswer.getMatchingSequence().equals(matchingAnswer.getMatchingSequence()) && !assessmentAnswer.getSequence().equals(matchingAnswer.getSequence())) {
								// assessmentAnswer.setMatchingAnswer(matchingAnswer);
								matchingAnswer.setMatchingAnswer(assessmentAnswer);
							}
						}
					}
				}
				ResourceType resourceType = (ResourceType) baseRepository.get(ResourceType.class, ResourceType.Type.ASSESSMENT_QUESTION.getType());
				question.setResourceType(resourceType);
				question.setTypeName(question.getTypeName());
				question.setCategory(QUESTION);
				question.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT,QUESTION));
			} else {
				AssessmentQuestion existingQuestion = getQuestion(gooruOQuestionId);
				if (question.getQuestionText() != null) {
					existingQuestion.setQuestionText(question.getQuestionText());
				}

				if (question.getDescription() != null) {
					existingQuestion.setDescription(question.getDescription());
				}
				if (question.getExplanation() != null) {
					existingQuestion.setExplanation(question.getExplanation());
				}
				if (question.getConcept() != null) {
					existingQuestion.setConcept(question.getConcept());
				}
				if (question.getImportCode() != null) {
					existingQuestion.setImportCode(question.getImportCode());
				}
				if (question.getContentType() != null) {
					existingQuestion.setContentType(question.getContentType());
				}
				if (question.getTags() != null) {
					existingQuestion.setTags(question.getTags());
				}
				if (question.getResourceSource() != null && existingQuestion.getResourceSource() != null) {
						ResourceSource resourceSource = existingQuestion.getResourceSource();
						resourceSource.setAttribution(question.getResourceSource().getAttribution());
						existingQuestion.setResourceSource(resourceSource);
				}
				existingQuestion.setDifficultyLevel(question.getDifficultyLevel());
				existingQuestion.setTitle(question.getTitle());
				existingQuestion.setTimeToCompleteInSecs(question.getTimeToCompleteInSecs());
				if (question.getTypeName() != null) {
					existingQuestion.setTypeName(question.getTypeName());
				}
				if (question.getCategory() != null) {
					existingQuestion.setCategory(question.getCategory());
				}

				if (question.getSharing() != null) {
					existingQuestion.setSharing(question.getSharing());
				}
				if (question.getAnswers() != null) {
					updateAnswerList(question.getAnswers(), existingQuestion.getAnswers());
				}
				if (question.getHints() != null) {
					updateHintList(question.getHints(), existingQuestion.getHints());
				}
			
				resourceService.saveOrUpdateResourceTaxonomy(existingQuestion, question.getTaxonomySet());

				if (question.getRecordSource() != null) {
					existingQuestion.setRecordSource(question.getRecordSource());
				}

				question = existingQuestion;
			}
		}
		question.setLastModified(new java.util.Date());

		if (question.getConcept() == null) {
			question.setConcept("");
		}

		if (question.getRecordSource() == null) {
			question.setRecordSource(Resource.RecordSource.DEFAULT.getRecordSource());
		}

		if (question.getTimeToCompleteInSecs() == null) {
			question.setTimeToCompleteInSecs(0);
		}
		if (question.getTitle() == null) {
			question.setTitle("");
		}
		if (question.getExplanation() == null) {
			question.setExplanation("");
		}
		if (question.getDescription() == null) {
			question.setDescription("");
		}
		if (question.getSharing() == null) {
			question.setSharing(PUBLIC);
		}
		if (question.getLicense() == null) {
			question.setLicense(new License());
			question.getLicense().setName(License.OTHER);
		}
		if (question.getUrl() == null) {
			question.setUrl("");
		}

		if (question.getQuestionText() == null) {
			question.setQuestionText("");
		}

		return question;
	}

	private void updateAnswerList(Set<AssessmentAnswer> sourceList, Set<AssessmentAnswer> existingList) {
		Set<AssessmentAnswer> addList = new TreeSet<AssessmentAnswer>();
		if (sourceList != null && sourceList.size() > 0) {
			if (existingList != null && existingList.size() > 0) {
				for (AssessmentAnswer srcObj : sourceList) {
					for (AssessmentAnswer chkObj : existingList) {
						if (srcObj.getAnswerId() != null) {
							if (srcObj.getAnswerId().equals(chkObj.getAnswerId())) {
								chkObj.setAnswerText(srcObj.getAnswerText());
								chkObj.setAnswerType(srcObj.getAnswerType());
								chkObj.setIsCorrect(srcObj.getIsCorrect());
								chkObj.setUnit(srcObj.getUnit());
								addList.add(chkObj);
								break;
							}
						} else if (!addList.contains(srcObj)) {
							addList.add(srcObj);
							if (srcObj.getIsCorrect() == null) {
								srcObj.setIsCorrect(false);
							}
						}
					}
				}
			} else {
				addList.addAll(sourceList);
			}
		}
		existingList.clear();
		existingList.addAll(addList);
	}

	private void updateHintList(Set<AssessmentHint> sourceList, Set<AssessmentHint> existingList) {
		Set<AssessmentHint> addList = new TreeSet<AssessmentHint>();
		if (sourceList != null && sourceList.size() > 0) {
			if (existingList != null && existingList.size() > 0) {
				for (AssessmentHint srcObj : sourceList) {
					for (AssessmentHint chkObj : existingList) {
						if (srcObj.getHintId() != null) {
							if (srcObj.getHintId().equals(chkObj.getHintId())) {
								chkObj.setHintText(srcObj.getHintText());
								addList.add(chkObj);
								break;
							}
						} else if (!addList.contains(srcObj)) {
							addList.add(srcObj);
						}
					}
				}
			} else {
				addList.addAll(sourceList);
			}
		}
		existingList.clear();
		existingList.addAll(addList);
	}

	@Override
	public AssessmentSegment getSegment(Integer segmentId) {
		return (AssessmentSegment) assessmentRepository.getModel(AssessmentSegment.class, segmentId);
	}

	@Override
	public Set<AssessmentSegment> listAssessmentSegments(String gooruOAssessmentId) {
		Assessment assessment = getAssessment(gooruOAssessmentId);
		return assessment.getSegments();
	}

	@Override
	public int deleteSegment(Integer segmentId, String gooruOAssessmentId, User caller) {
		AssessmentSegment segment = getSegment(segmentId);
		Assessment assessment = getAssessment(gooruOAssessmentId);
		if (assessment != null) {
			this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_SEGMENT_CREATE);
		}
		try {
			getRevisionHistoryService().createVersion(segment.getAssessment(), SEGMENT_DELETE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (segment != null) {
			assessmentRepository.remove(AssessmentSegment.class, segmentId);
			this.getSessionActivityService().updateSessionActivityByContent(gooruOAssessmentId, SessionActivityType.Status.ARCHIVE.getStatus());
			return 1;
		}
		return 0;
	}

	@Override
	public AssessmentAttemptItem getNextAttemptQuestion(String gooruOAssessmentId, Integer attemptId, User user) {
		AssessmentAttempt attempt = getAttempt(attemptId);
		AssessmentQuestion question = assessmentRepository.getNextUnansweredQuestion(gooruOAssessmentId, attemptId);
		if (attempt != null && question != null) {
			attempt.setAttemptItems(new HashSet<AssessmentAttemptItem>());
			AssessmentAttemptItem attemptItem = new AssessmentAttemptItem();
			attemptItem.getQuestion().setContentId(question.getContentId());
			attemptItem.setPresentedAtTime(new java.util.Date());
			attemptItem.setAttemptStatus(0);// Not Attempted
			attempt.getAttemptItems().add(attemptItem);
			assessmentRepository.saveAndFlush(attempt);
			attemptItem.setQuestion(question);
			return attemptItem;
		}
		return null;
	}

	@Override
	public AssessmentSegmentQuestionAssoc createSegmentQuestion(AssessmentSegmentQuestionAssoc assessmentSegmentQuestion) {
		if (assessmentSegmentQuestion.getSequence() == null) {
			assessmentSegmentQuestion.setSequence(0);
		}
		assessmentRepository.saveAndFlush(assessmentSegmentQuestion);
		updateQuestionTime(assessmentSegmentQuestion.getQuestion());
		updateQuestionCount(assessmentSegmentQuestion.getSegment().getSegmentId());
		return assessmentSegmentQuestion;
	}

	@Override
	public boolean saveAttemptAnswerTry(Integer attemptId, Integer attemptItemId, Integer answerId, String answerText) {
		AssessmentAttemptItem assessmentAttemptItem = getAttemptItem(attemptItemId);
		if (answerId != null && !answerId.equals("")) {
			AssessmentAttempt attempt = (AssessmentAttempt) assessmentRepository.getModel(AssessmentAttempt.class, attemptId);
			AssessmentAnswer answer = (AssessmentAnswer) assessmentRepository.getModel(AssessmentAnswer.class, answerId);
			if (assessmentAttemptItem != null && attempt != null && answer != null) {
				if (answerId != null) {
					AssessmentAttemptTry assessmentAttemptTry = new AssessmentAttemptTry();
					assessmentAttemptTry.setAssessmentAttemptItem(new AssessmentAttemptItem());
					assessmentAttemptTry.getAssessmentAttemptItem().setAttemptItemId(attemptItemId);
					assessmentAttemptTry.setAnswer(new AssessmentAnswer());
					assessmentAttemptTry.getAnswer().setAnswerId(answerId);
					assessmentAttemptTry.setAnsweredAtTime(new java.util.Date());
					assessmentAttemptTry.setAnswerText(answerText);
					Integer trySequence = getCurrentTrySequence(attemptItemId);
					assessmentAttemptTry.setTrySequence(trySequence);

					if (getAttemptAnswerStatus(answerId)) {
						assessmentAttemptTry.setAttemptTryStatus(1);
						assessmentAttemptItem.setAttemptStatus(1);
						assessmentAttemptItem.setCorrectTryId(trySequence);
					} else {
						assessmentAttemptTry.setAttemptTryStatus(0);
						assessmentAttemptItem.setAttemptStatus(0);
					}
					assessmentRepository.save(assessmentAttemptTry);
				}
				if (answer.getIsCorrect()) {
					attempt.setScore(attempt.getScore() + 1);
					assessmentRepository.save(attempt);
				}
				return true;
			}
		} else {
			assessmentAttemptItem.setAttemptStatus(0);
		}
		assessmentRepository.save(assessmentAttemptItem);
		return false;
	}

	private Integer getCurrentTrySequence(Integer attemptItemId) {
		return assessmentRepository.getCurrentTrySequence(attemptItemId);
	}

	@Override
	public AssessmentAttemptItem getAttemptItem(Integer attemptItemId) {
		return (AssessmentAttemptItem) assessmentRepository.getModel(AssessmentAttemptItem.class, attemptItemId);
	}

	@Override
	public Integer createAttempt(User user, String gooruOAssessmentId, String mode) {
		Assessment assessment = getAssessment(gooruOAssessmentId);
		AssessmentAttempt attempt = new AssessmentAttempt();
		attempt.setScore(0);
		attempt.setStudent(user);
		attempt.setModeName(mode);
		attempt.setStatus(AssessmentAttempt.STATUS.INPROGRESS.getId());
		attempt.setStartTime(new java.util.Date());
		assessment.setAttempts(new HashSet<AssessmentAttempt>());
		assessment.getAttempts().add(attempt);
		assessmentRepository.saveAndFlush(assessment);
		resourceRepository.incrementViews(gooruOAssessmentId);
		// redisService.updateCount(gooruOAssessmentId, Constants.REDIS_VIEWS);
		return attempt.getAttemptId();
	}

	@Override
	public AssessmentSegment createSegment(AssessmentSegment segment, String gooruOAssessmentId) {
		Assessment assessment = getAssessment(gooruOAssessmentId);
		assessment.setSegments(new HashSet<AssessmentSegment>());
		segment.setSegmentUId(UUID.randomUUID().toString());
		assessment.getSegments().add(segment);
		assessmentRepository.saveAndFlush(assessment);
		if (assessment != null) {
			this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_SEGMENT_CREATE);
		}
		return segment;
	}

	@Override
	public AssessmentSegment updateSegment(AssessmentSegment segment) {
		Assessment assessment = getAssessment(segment.getAssessment().getGooruOid());
		AssessmentSegment existingSegment = getSegment(segment.getSegmentId());
		existingSegment.setName(segment.getName());
		existingSegment.setSequence(segment.getSequence());
		assessmentRepository.saveAndFlush(existingSegment);
		if (assessment != null) {
			this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_SEGMENT_UPDATE);
		}
		return segment;
	}

	@Override
	public int deleteSegmentQuestion(Integer segmentId, String gooruOAssessmentId, String gooruOQuestionId, User caller) {
		AssessmentSegmentQuestionAssoc segmentQuestionAssoc = new AssessmentSegmentQuestionAssoc();
		this.getSessionActivityService().updateSessionActivityByContent(gooruOAssessmentId, SessionActivityType.Status.ARCHIVE.getStatus());
		Assessment assessment = getAssessment(gooruOAssessmentId);
		if (segmentId == 0) {
			Iterator<AssessmentSegment> iterator = assessment.getSegments().iterator();
			boolean firstSegment = true;
			while (iterator.hasNext() && firstSegment) {
				firstSegment = false;
				AssessmentSegment assessmentSegment = iterator.next();
				segmentQuestionAssoc.getSegment().setSegmentId(assessmentSegment.getSegmentId().intValue());
			}
		} else {
			segmentQuestionAssoc.getSegment().setSegmentId(segmentId);
		}

		segmentQuestionAssoc.setQuestion(getQuestion(gooruOQuestionId));
		segmentQuestionAssoc = (AssessmentSegmentQuestionAssoc) assessmentRepository.getModel(AssessmentSegmentQuestionAssoc.class, segmentQuestionAssoc);
		if (segmentQuestionAssoc != null) {
			if (assessment != null) {
				this.createRevisionHistoryEntry(assessment.getGooruOid(), ASSESSMENT_QUESTION_DELETE);
			}
			assessmentRepository.deleteSegmentQuestion(segmentQuestionAssoc);
			assessmentRepository.flush();
			updateQuestionTime(getQuestion(gooruOQuestionId));
			updateQuestionCount(segmentId);
			return 1;
		}
		return 0;
	}

	@Override
	public void updateQuetionInfo(String gooruOQuestionId, Integer segmentId) {
		updateQuestionTime(getQuestion(gooruOQuestionId));
		updateQuestionCount(segmentId);
	}

	private void updateQuestionTime(AssessmentQuestion question) {
		if (question != null) {
			assessmentRepository.updateTimeForSegments(question.getContentId());
			assessmentRepository.updateTimeForAssessments(question.getContentId());
		}
	}

	private void updateQuestionCount(Integer segmentId) {
		Assessment assessment = assessmentRepository.getAssessmentForSegment(segmentId);
		if (assessment != null) {
			int currentCount = assessmentRepository.getAssessmentQuestions(assessment.getGooruOid()).size();
			assessment.setQuestionCount(currentCount);
			assessmentRepository.save(assessment);
		}

	}

	/**
	 * To get the Assessment Details with its Metadata(Taxonomy,Skills,etc)
	 * 
	 * @param model
	 * @return
	 */
	public Assessment getAssessmentMetaData(Assessment model) {

		AssessmentMetaDataDTO metaData = new AssessmentMetaDataDTO();

		List<User> users = this.learnguideRepository.findCollaborators(model.getGooruOid(), null);

		Set<Code> taxonomySet = model.getTaxonomySet();
		Iterator<Code> iter = taxonomySet.iterator();
		while (iter.hasNext()) {
			Code code = iter.next();
			this.procedureExecutor.setCode(code);
			Map codeMap = this.procedureExecutor.execute();
			String codeLabel = (String) codeMap.get(CODE_LABEL);
			String[] taxonomy = codeLabel.split("\\$\\@");

			int length = taxonomy.length;
			if (length > 0) {
				metaData.getGrades().add(taxonomy[length - 1]);
			}
			if (length > 1) {
				metaData.getSubjects().add(taxonomy[length - 2]);
			}
			if (length > 2) {
				metaData.getUnits().add(taxonomy[length - 3]);
			}
			if (length > 3) {
				metaData.getTopics().add(taxonomy[length - 4]);
			}
			if (length > 4) {
				metaData.getLessons().add(taxonomy[length - 5]);
			}
		}

		if (taxonomySet != null) {
			for (Code code : taxonomySet) {
				if (code.getRootNodeId() != null && UserGroupSupport.getTaxonomyPreference() != null && UserGroupSupport.getTaxonomyPreference().contains(code.getRootNodeId().toString()) && (!metaData.getCurriculumCodes().contains(code.getCode()))) {
						metaData.getCurriculumCodes().add(code.getCode());
						if (code.getDescription() != null && !code.getDescription().equals("")) {
							metaData.getCurriculumDescs().add(code.getDescription());
						} else {
							metaData.getCurriculumCodes().add(BLANK + code.getCode());
						}
				}
			}
		}
		Map<Integer, List<Code>> taxonomyMapByCode = TaxonomyUtil.getTaxonomyMapByCode(model.getTaxonomySet(), taxonomyService);
		Code rootCode = (Code) this.taxonomyRepository.get(Code.class, TaxonomyUtil.getTaxonomyRootId(model.getOrganization().getPartyUid()));
		if (rootCode != null) {
			List<CodeType> findTaxonomyLevels = this.taxonomyRepository.findTaxonomyLevels(rootCode);
			metaData.setTaxonomyLevels(findTaxonomyLevels);
		}
		metaData.setTaxonomyMapByCode(taxonomyMapByCode);
		metaData.setCollaboratorsString(model.collaboratorsInAString());
		metaData.setCollaborators(users);

		model.setMetaData(metaData);
		return model;
	}

	@Override
	public boolean getAttemptAnswerStatus(Integer answerId) {
		return assessmentRepository.getAttemptAnswerStatus(answerId);
	}

	@Override
	public AssessmentAttemptSummaryDTO getAssessmentAttemptSummary(Integer attemptId, String gooruOAssessmentId, Integer userId) {
		Integer correctAnswerCountWithoutShortAnswer = 0;
		Integer totalQuestionsExculdeShortAnswer = 0;
		AssessmentAttemptSummaryDTO assessmentAttemptSummary = new AssessmentAttemptSummaryDTO();
		List<AttemptQuestionDTO> questions = new ArrayList<AttemptQuestionDTO>();

		List<Object[]> attemptItems = assessmentRepository.getAssessmentAttemptQuestionSummary(attemptId);

		Map<String, Integer> conceptMap = new HashMap<String, Integer>();

		if (attemptItems != null) {
			for (Object[] attemptItem : attemptItems) {

				AttemptQuestionDTO question = new AttemptQuestionDTO();
				question.setQuestionText((String) attemptItem[0]);
				/*
				 * question.setAnswer((String) attemptItem[1]);
				 * question.setCorrectAnswer((String) attemptItem[2]);
				 * question.setIsCorrect((Integer) attemptItem[3]);
				 */question.setConcept((String) attemptItem[1]);
				question.setQuestionStatus((String) attemptItem[2]);
				Integer questionId = (Integer) attemptItem[3];
				List<AssessmentAnswer> answers = assessmentRepository.findAnswerByAssessmentQuestionId(questionId);
				question.setAnswers(answers);
				List<AssessmentQuestionAssetAssoc> assets = assessmentRepository.getQuestionAssetByQuestionId(questionId);
				for (AssessmentQuestionAssetAssoc asset : assets) {
					if (asset.getAssetKey().equalsIgnoreCase(ASSET_QUESTION)) {
						question.setAsset(asset);
					}
				}
				question.setExplanation((String) attemptItem[4]);
				question.setType((Integer) attemptItem[5]);
				question.setFolder((String) attemptItem[6]);
				question.setAssetURI((String) attemptItem[7]);
				question.setGooruOid((String) attemptItem[8]);
				List<AssessmentAttemptTry> attemptsTry = assessmentRepository.findAssessmentAttemptsTryByAttemptItemId((Integer) attemptItem[9]);
				question.setAssessmentAttemptsTry(attemptsTry);
				Integer correctTrySequence = (Integer) attemptItem[10];
				question.setCorrectTrySequence(correctTrySequence);

				if (correctTrySequence != null && correctTrySequence == 1) {
					question.setIsCorrect(1);
				} else {
					question.setIsCorrect(0);
				}

				/*
				 * Map<String, String> filter = new HashMap<String, String>();
				 * filter.put(AssessmentRepository.ATTEMPT_ID, attemptId + "");
				 * filter.put(AssessmentRepository.QUESTION_GOORU_ID,
				 * gooruOAssessmentId);
				 * filter.put(AssessmentRepository.QUESTION_TYPE_SA, "2");
				 * 
				 * Integer total =
				 * assessmentRepository.getDistinctAttemptQuestionCount(filter);
				 * filter.put(AssessmentRepository.IS_CORRECT, "1"); Integer
				 * correctlyAnswered =
				 * assessmentRepository.getDistinctAttemptQuestionCount(filter);
				 * question.setCorrectlyAnsweredPercentage(getPercentageValue(
				 * correctlyAnswered, total) + "");
				 */
				if (question.getIsCorrect() == 1) {
					correctAnswerCountWithoutShortAnswer++;
				}
				if (question.getType() != 2) {
					totalQuestionsExculdeShortAnswer++;
				}

				// build concept-wise map for attempt

				String concept = question.getConcept();
				if (concept == null || concept.equalsIgnoreCase("")) {
					concept = NO_CONCEPT;
				}

				Integer conceptCorrectCount = conceptMap.get(concept);
				if (conceptCorrectCount == null) {
					conceptCorrectCount = 0;
				}
				if (question.getIsCorrect() == 1) {
					conceptMap.put(concept, conceptCorrectCount + 1);
				} else {
					Integer existingConceptCount = conceptMap.get(concept);
					if (existingConceptCount != null) {
						if (existingConceptCount == 0) {
							conceptMap.put(concept, 0);
						}
					} else {
						conceptMap.put(concept, 0);
					}

				}

				questions.add(question);

			}

			Map<String, Integer> conceptsScore = new HashMap<String, Integer>();

			for (String conceptKey : conceptMap.keySet()) {
				Integer correctCount = conceptMap.get(conceptKey);
				Integer conceptQuestionCount = 0;

				for (AttemptQuestionDTO aquestion : questions) {
					String conceptCheck = "";
					if (!conceptKey.equalsIgnoreCase(NO_CONCEPT) && conceptKey != null) {
						conceptCheck = conceptKey;
					}

					if (aquestion.getConcept().equalsIgnoreCase(conceptCheck)) {
						conceptQuestionCount++;
					}
				}

				Double conceptsPercentage = (correctCount / (conceptQuestionCount * 1.0)) * 100;
				conceptsScore.put(conceptKey, conceptsPercentage.intValue());
			}

			assessmentAttemptSummary.setScore(correctAnswerCountWithoutShortAnswer);
			assessmentAttemptSummary.setTotalQuestions(totalQuestionsExculdeShortAnswer);
			Double correctAnswerPercentageDouble = (correctAnswerCountWithoutShortAnswer / (totalQuestionsExculdeShortAnswer * 1.0)) * 100;
			assessmentAttemptSummary.setCorrectAnswersPercentage(new Integer(correctAnswerPercentageDouble.intValue()));
			assessmentAttemptSummary.setConceptsScore(conceptsScore);
		}
		assessmentAttemptSummary.setQuestionData(questions);
		Map<String, Object> summary = assessmentRepository.getAssessmentAttemptsInfo(attemptId, gooruOAssessmentId, userId);
		summary.put(SUMMARY, getAttempt(attemptId));
		assessmentAttemptSummary.setSocialAttemptScore(summary);
		return assessmentAttemptSummary;
	}

	private Errors validateQuestion(AssessmentQuestion question) throws Exception {
		final Errors errors = new BindException(question, QUESTION);
		if (question != null) {
			boolean imageExist = false;
			if (question.getAssets() != null) {
				for (AssessmentQuestionAssetAssoc assetAssoc : question.getAssets()) {
					if (assetAssoc.getAssetKey().equals(ASSET_QUESTION)) {
						imageExist = true;
					}
				}
			}
			if (!imageExist) {
				ServerValidationUtils.rejectIfNullOrEmpty(errors, question.getQuestionText(), QUESTION_TEXT, ErrorMessage.REQUIRED_FIELD);
			}
			if (!question.getTypeName().equals(AssessmentQuestion.TYPE.SHORT_ANSWER.getName()) && !question.getTypeName().equals(AssessmentQuestion.TYPE.OPEN_ENDED.getName())) {
				ServerValidationUtils.rejectIfNullOrEmpty(errors, question.getAnswers(), ANSWERS, ErrorMessage.REQUIRED_FIELD);
			}
		}
		return errors;
	}

	private Errors validateAssessment(Assessment assessment) throws Exception {
		final Errors errors = new BindException(assessment, ASSESSMENT);
		if (assessment != null) {
			ServerValidationUtils.rejectIfNullOrEmpty(errors, assessment.getName(), NAME, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNullOrEmpty(errors, assessment.getSharing(), SHARING, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getTimeToCompleteInSecs(), TIME_TO_COMPLETE_IN_SECS, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getIsChoiceRandom(), IS_CHOICE_RANDOM, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getIsRandom(), IS_RANDOM, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getShowScore(), SHOW_SCORE, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getShowHints(), SHOW_HINTS, ErrorMessage.REQUIRED_FIELD);
			ServerValidationUtils.rejectIfNull(errors, assessment.getShowCorrectAnswer(), SHOW_CORRECT_ANSWER, ErrorMessage.REQUIRED_FIELD);
		}
		return errors;
	}

	@Override
	public QuestionSet getQuestionSet(String gooruOQuestionSetId) {
		return assessmentRepository.getByGooruOId(QuestionSet.class, gooruOQuestionSetId);
	}

	@Override
	public ActionResponseDTO<QuestionSet> createQuestionSet(QuestionSet questionSet) throws Exception {

		questionSet = initQuestionSet(questionSet, null);

		Errors errors = validateQuestionSet(questionSet);

		if (!errors.hasErrors()) {
			assessmentRepository.save(questionSet);
		}

		return new ActionResponseDTO<QuestionSet>(questionSet, errors);
	}

	@Override
	public ActionResponseDTO<QuestionSet> updateQuestionSet(QuestionSet questionSet, String gooruOQuestionSetId) throws Exception {
		questionSet = initQuestionSet(questionSet, gooruOQuestionSetId);

		Errors errors = validateQuestionSet(questionSet);

		if (!errors.hasErrors()) {
			assessmentRepository.save(questionSet);
		}

		return new ActionResponseDTO<QuestionSet>(questionSet, errors);
	}

	private QuestionSet initQuestionSet(QuestionSet questionSet, String gooruOQuestionSetId) {
		if (gooruOQuestionSetId == null) {
			License license = (License) baseRepository.get(License.class, License.OTHER);
			questionSet.setLicense(license);
			questionSet.setGooruOid(UUID.randomUUID().toString());
			questionSet.setContentId(null);
			questionSet.setCreatedOn(new java.util.Date());
			questionSet.setUrl("");
			ContentType contentType = (ContentType) baseRepository.get(ContentType.class, ContentType.RESOURCE);
			questionSet.setContentType(contentType);
		} else {
			QuestionSet existingQuestionSet = getQuestionSet(gooruOQuestionSetId);
			existingQuestionSet.setTitle(questionSet.getTitle());

			questionSet = existingQuestionSet;
		}
		ResourceType resourceType = (ResourceType) baseRepository.get(ResourceType.class, questionSet.getResourceType().getName());
		questionSet.setResourceType(resourceType);
		questionSet.setLastModified(new java.util.Date());
		return questionSet;
	}

	private Errors validateQuestionSet(QuestionSet questionSet) throws Exception {
		final Errors errors = new BindException(questionSet, QUESTION_SET);
		if (questionSet != null) {
			ServerValidationUtils.rejectIfNullOrEmpty(errors, questionSet.getTitle(), TITLE, ErrorMessage.REQUIRED_FIELD);
		}
		return errors;
	}

	@Override
	public List<QuestionSet> listQuestionSets(Map<String, String> filters) {
		return assessmentRepository.listQuestionSets(filters);
	}

	@Override
	public int deleteQuestionSetQuestion(String questionSetGooruOId, String gooruOQuestionId, User caller) {
		QuestionSetQuestionAssoc questionSetQuestionAssoc = new QuestionSetQuestionAssoc();
		questionSetQuestionAssoc.setQuestionSet(getQuestionSet(questionSetGooruOId));
		questionSetQuestionAssoc.setQuestion(getQuestion(gooruOQuestionId));
		questionSetQuestionAssoc = (QuestionSetQuestionAssoc) assessmentRepository.getModel(QuestionSetQuestionAssoc.class, questionSetQuestionAssoc);
		if (questionSetQuestionAssoc != null) {
			assessmentRepository.deleteQuestionSetQuestion(questionSetQuestionAssoc);
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int deleteQuestionSet(String gooruOQuestionSetId, User caller) {
		QuestionSet questionSet = getQuestionSet(gooruOQuestionSetId);
		if (questionSet != null) {
			assessmentRepository.remove(QuestionSet.class, questionSet.getContentId());
			return 1;
		}
		return 0;
	}

	@Override
	public QuestionSetQuestionAssoc createQuestionSetQuestion(QuestionSetQuestionAssoc questionSetQuestion) {
		assessmentRepository.saveAndFlush(questionSetQuestion);
		return questionSetQuestion;
	}

	@Override
	public List<AssessmentQuestion> getAssessmentQuestions(String gooruOAssessmentId) {
		return assessmentRepository.getAssessmentQuestions(gooruOAssessmentId);
	}

	@Override
	public AssessmentQuestionAssetAssoc uploadQuestionAsset(String gooruQuestionId, AssessmentQuestionAssetAssoc questionAsset, boolean index) throws Exception {

		if (questionAsset.getAsset().getFileData() != null && questionAsset.getAsset().getFileData().length > 2) {
			AssessmentQuestion question = getQuestion(gooruQuestionId);
			Asset asset = questionAsset.getAsset();
			asset.setHasUniqueName(true);

			assessmentRepository.save(asset);

			String realPath = asset.getOrganization().getNfsStorageArea().getInternalPath() + question.getFolder();
			this.getResourceImageUtil().sendMsgToGenerateThumbnails(question, asset.getName());
			this.assetManager.saveAssetResource(asset, realPath);

			s3ResourceApiHandler.uploadResourceFileWithNewSession(question, asset.getName());

			if (index) {
				indexProcessor.index(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
			}

			assessmentRepository.save(asset);

			assessmentRepository.saveAndFlush(questionAsset);

			return questionAsset;
		}
		return null;
	}

	@Override
	public AssessmentQuestionAssetAssoc getQuestionAsset(final String assetKey, String gooruOAssessmentId) {
		return assessmentRepository.getQuestionAsset(assetKey, gooruOAssessmentId);
	}

	@Override
	public AssessmentSegmentQuestionAssoc findSegmentQuestion(Integer segmentId, String gooruOQuestionId) {
		return assessmentRepository.findSegmentQuestion(segmentId, gooruOQuestionId);
	}

	@Override
	public Assessment copyAssessment(User user, String gooruAssessmentId, String quizTitle) throws Exception {
		Assessment assessment = getAssessment(gooruAssessmentId);
		Assessment copyAssessment = new Assessment();
		if (assessment != null) {
			copyAssessment.setDescription(assessment.getDescription());
			copyAssessment.setContentType(assessment.getContentType());
			copyAssessment.setIsChoiceRandom(assessment.getIsChoiceRandom());
			copyAssessment.setIsRandom(assessment.getIsRandom());
			copyAssessment.setLicense(assessment.getLicense());
			if (quizTitle == null) {
				quizTitle = "Copy - " + assessment.getName();
			}
			copyAssessment.setCopiedResourceId(gooruAssessmentId);
			copyAssessment.setName(quizTitle);
			copyAssessment.setTitle(assessment.getTitle());
			copyAssessment.setQuestionCount(assessment.getQuestionCount());
			copyAssessment.setResourceType(assessment.getResourceType());
			copyAssessment.setGrade(assessment.getGrade());
			copyAssessment.setSharing(Sharing.PRIVATE.getSharing());
			copyAssessment.setShowCorrectAnswer(assessment.getShowCorrectAnswer());
			copyAssessment.setShowHints(assessment.getShowHints());
			copyAssessment.setShowScore(assessment.getShowScore());
			copyAssessment.setTimeToCompleteInSecs(assessment.getTimeToCompleteInSecs());
			copyAssessment.setCreator(assessment.getCreator());
			copyAssessment.setUser(user);
			copyAssessment.setMetaData(assessment.getMetaData());
			copyAssessment.setThumbnail(assessment.getThumbnail());
			Set<Code> taxonomy = new HashSet<Code>();
			taxonomy.addAll(assessment.getTaxonomySet());
			copyAssessment.setTaxonomySet(taxonomy);
			createAssessment(copyAssessment);
			this.getResourceManager().copyResourceRepository(assessment, copyAssessment);

			if (assessment.getSegments() != null) {
				Set<AssessmentSegment> copyAssessmentSegments = new HashSet<AssessmentSegment>();
				for (AssessmentSegment segment : assessment.getSegments()) {
					AssessmentSegment copySegment = new AssessmentSegment();
					copySegment.setName("Copy - " + segment.getName());
					copySegment.setSequence(segment.getSequence());
					copySegment.setTimeToCompleteInSecs(segment.getTimeToCompleteInSecs());
					copySegment.setSegmentUId(UUID.randomUUID().toString());
					createSegment(copySegment, copyAssessment.getGooruOid());
					Set<AssessmentSegmentQuestionAssoc> segmentQuestionAssocs = segment.getSegmentQuestions();
					if (segmentQuestionAssocs != null && segmentQuestionAssocs.size() > 0) {
						Set<AssessmentSegmentQuestionAssoc> copyAssessmentSegmentQuestionAssocs = new HashSet<AssessmentSegmentQuestionAssoc>();
						for (AssessmentSegmentQuestionAssoc segmentQuestionAssoc : segmentQuestionAssocs) {
							AssessmentSegmentQuestionAssoc copySegmentQuestionAssoc = new AssessmentSegmentQuestionAssoc();
							copySegmentQuestionAssoc.setQuestion(segmentQuestionAssoc.getQuestion());
							copySegmentQuestionAssoc.setSegment(copySegment);
							createSegmentQuestion(copySegmentQuestionAssoc);
							copyAssessmentSegmentQuestionAssocs.add(copySegmentQuestionAssoc);
						}
						copySegment.setSegmentQuestions(copyAssessmentSegmentQuestionAssocs);
					}
					copyAssessmentSegments.add(copySegment);
				}
				copyAssessment.setSegments(copyAssessmentSegments);
			}
			this.s3ResourceApiHandler.uploadS3Resource(copyAssessment);
		}
		return copyAssessment;
	}

	@Override
	public int reorderSegments(String reOrdered) throws Exception {
		if (reOrdered == null || reOrdered.length() < 1) {
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		String[] reOrderedArray = reOrdered.split(",");
		for (int sequence = 0; sequence < reOrderedArray.length; sequence++) {
			Integer segmentId = Integer.parseInt(reOrderedArray[sequence]);
			AssessmentSegment segment = getSegment(segmentId);
			segment.setSequence(sequence + 1);
			assessmentRepository.save(segment);
		}
		return HttpServletResponse.SC_OK;
	}

	@Override
	public int reorderQuestions(Integer segmentId, String reOrdered) throws Exception {
		if (reOrdered == null || reOrdered.length() < 1) {
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		AssessmentSegment segment = getSegment(segmentId);

		String[] reOrderedArray = reOrdered.split(",");
		for (int sequence = 0; sequence < reOrderedArray.length; sequence++) {
			String gooruQuestionId = reOrderedArray[sequence];
			AssessmentSegmentQuestionAssoc segmentQuestionAssoc = new AssessmentSegmentQuestionAssoc();
			segmentQuestionAssoc.setSegment(segment);
			segmentQuestionAssoc.setQuestion(getQuestion(gooruQuestionId));
			segmentQuestionAssoc.setSequence(sequence + 1);
			assessmentRepository.save(segmentQuestionAssoc);
		}
		assessmentRepository.flush();
		updateQuestionCount(segmentId);
		return HttpServletResponse.SC_OK;
	}

	@Override
	public AssessmentQuestion copyAssessmentQuestion(User user, String gooruQuestionId) throws Exception {
		AssessmentQuestion question = getQuestion(gooruQuestionId);
		AssessmentQuestion copyQuestion = new AssessmentQuestion();
		if (question != null) {
			copyQuestion.setDescription(question.getDescription());
			copyQuestion.setContentType(question.getContentType());
			copyQuestion.setConcept(question.getConcept());
			copyQuestion.setLicense(question.getLicense());
			copyQuestion.setCopiedResourceId(question.getAssessmentGooruId());
			copyQuestion.setLabel(question.getLabel());
			copyQuestion.setTitle(question.getTitle());
			copyQuestion.setResourceType(question.getResourceType());
			copyQuestion.setSharing(question.getSharing());
			copyQuestion.setTimeToCompleteInSecs(question.getTimeToCompleteInSecs());
			copyQuestion.setDifficultyLevel(question.getDifficultyLevel());
			copyQuestion.setQuestionText(question.getQuestionText());
			copyQuestion.setExplanation(question.getExplanation());
			copyQuestion.setHelpContentLink(question.getHelpContentLink());
			copyQuestion.setInstruction(question.getInstruction());
			copyQuestion.setScorePoints(question.getScorePoints());
			copyQuestion.setUser(user);
			copyQuestion.setCreator(question.getCreator());
			copyQuestion.setType(question.getType());
			if (question.getHints() != null) {
				Set<AssessmentHint> copyHints = new TreeSet<AssessmentHint>();
				for (AssessmentHint hint : question.getHints()) {
					AssessmentHint copyHint = new AssessmentHint();
					copyHint.setHintText(hint.getHintText());
					copyHint.setSequence(hint.getSequence());
					copyHints.add(copyHint);
				}
				copyQuestion.setHints(copyHints);
			}
			if (question.getAnswers() != null) {
				Set<AssessmentAnswer> copyAnswers = new TreeSet<AssessmentAnswer>();
				for (AssessmentAnswer answer : question.getAnswers()) {
					AssessmentAnswer copyAnswer = new AssessmentAnswer();
					copyAnswer.setAnswerText(answer.getAnswerText());
					copyAnswer.setSequence(answer.getSequence());
					copyAnswer.setIsCorrect(answer.getIsCorrect());
					copyAnswer.setMatchingAnswer(answer.getMatchingAnswer());
					copyAnswer.setUnit(answer.getUnit());
					copyAnswers.add(copyAnswer);
				}
				copyQuestion.setAnswers(copyAnswers);
			}
			createQuestion(copyQuestion, true);
			if (question.getAssets() != null && question.getAssets().size() > 0) {
				Set<AssessmentQuestionAssetAssoc> questionAssets = new HashSet<AssessmentQuestionAssetAssoc>();
				for (AssessmentQuestionAssetAssoc questionAssetAssoc : question.getAssets()) {
					AssessmentQuestionAssetAssoc copyQuestionAssetAssoc = new AssessmentQuestionAssetAssoc();
					Asset asset = new Asset();
					asset.setDescription(questionAssetAssoc.getAsset().getDescription());
					asset.setName(questionAssetAssoc.getAsset().getName());
					asset.setHasUniqueName(questionAssetAssoc.getAsset().getHasUniqueName());
					assessmentRepository.save(asset);
					copyQuestionAssetAssoc.setAsset(asset);
					copyQuestionAssetAssoc.setAssetKey(questionAssetAssoc.getAssetKey());
					copyQuestionAssetAssoc.setQuestion(copyQuestion);
					questionAssets.add(copyQuestionAssetAssoc);
					assessmentRepository.save(copyQuestionAssetAssoc);
				}
				copyQuestion.setAssets(questionAssets);
			}
			this.getAsyncExecutor().copyResourceFolder(question, copyQuestion);

		}
		return copyQuestion;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public void setContentRepository(ContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public void setBaseRepository(BaseRepository baseRepository) {
		this.baseRepository = baseRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean assignAsset(String questionGooruOid, Integer assetId, String assetKey) {
		AssessmentQuestionAssetAssoc questionAssetAssoc = assessmentRepository.findQuestionAsset(questionGooruOid, assetId);
		if (questionAssetAssoc != null) {
			questionAssetAssoc.setAssetKey(assetKey);
			assessmentRepository.save(questionAssetAssoc);
			return true;
		}
		return false;
	}

	@Override
	public String updateAssessmentThumbnail(String gooruAssessmentId, String fileName, Map<String, Object> formField) throws Exception {
		Assessment assessment = getAssessment(gooruAssessmentId);

		File classplanDir = new File(assessment.getOrganization().getNfsStorageArea().getInternalPath() + assessment.getFolder());

		if (!classplanDir.exists()) {
			classplanDir.mkdirs();
		}

		Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

		byte[] fileData = null;

		// expecting only one file in the request right now
		for (byte[] fileContent : files.values()) {
			fileData = fileContent;
		}
		if (fileData != null && fileData.length > 0) {

			String prevFileName = assessment.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(assessment.getOrganization().getNfsStorageArea().getInternalPath() + assessment.getFolder() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
				}
			}

			File file = new File(assessment.getOrganization().getNfsStorageArea().getInternalPath() + assessment.getFolder() + "/" + fileName);

			OutputStream out = new FileOutputStream(file);
			out.write(fileData);
			out.close();

			assessment.setThumbnail(fileName);
		}

		assessmentRepository.saveAndFlush(assessment);
		String thumbnailPath = assessment.getAssetURI() + assessment.getFolder() + fileName;
		indexProcessor.index(assessment.getGooruOid(), IndexProcessor.INDEX, QUIZ);
		return thumbnailPath;
	}

	@Override
	public List<String> suggestConcept(String keyword) {
		return learnguideRepository.getAssessmentQuestionConcept(keyword);
	}

	@Override
	public void deleteAssessmentThumbnail(String gooruAssessmentId) {

		Assessment assessment = getAssessment(gooruAssessmentId);
		File classplanDir = new File(assessment.getOrganization().getNfsStorageArea().getInternalPath() + assessment.getFolder());

		if (classplanDir.exists()) {

			String prevFileName = assessment.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(classplanDir.getPath() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
				}
			}

			assessment.setThumbnail(null);
			assessmentRepository.save(assessment);
			indexProcessor.index(assessment.getGooruOid(), IndexProcessor.INDEX, QUIZ);
		}
	}

	@Override
	public List<Assessment> getAssessmentList(List<String> assessmentIds) {
		return assessmentRepository.getAssessmentsListByAssessmentGooruOids(assessmentIds);
	}

	@Override
	public List<Assessment> getAssessmenOfQuestion(String questionGooruOid) {
		return assessmentRepository.getAssessmentOfQuestion(questionGooruOid);
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public String updateQuizImage(String gooruContentId, String fileName) throws IOException {
		Assessment assessment = this.assessmentRepository.findQuizContent(gooruContentId);

		resourceImageUtil.moveFileAndSendMsgToGenerateThumbnails(assessment, fileName, false);
		indexProcessor.index(assessment.getGooruOid(), IndexProcessor.INDEX, QUIZ);

		// Remove the collection from cache
		// collectionUtil.deleteCollectionFromCache(gooruContentId,
		// "collection");
		return assessment.getOrganization().getNfsStorageArea().getAreaPath() + assessment.getFolder() + "/" + assessment.getThumbnail();
	}

	@Override
	public String updateQuizQuestionImage(String gooruContentId, String fileName, Resource resource, String assetKey) throws Exception {
		if(fileName != null && ResourceImageUtil.getYoutubeVideoId(fileName) != null || fileName.contains(YOUTUBE_URL)) {
			return fileName;
		} else {
			final String mediaFolderPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + Constants.UPLOADED_MEDIA_FOLDER;
			String resourceImageFile = mediaFolderPath + "/" + fileName;
			String newImageFile = mediaFolderPath + "/" + assetKey + "_" + fileName;
			File mediaImage = new File(resourceImageFile);
			File newImage = new File(newImageFile);
			mediaImage.renameTo(newImage);
			fileName = newImage.getName();
			return resource.getOrganization().getNfsStorageArea().getAreaPath() + Constants.UPLOADED_MEDIA_FOLDER + "/" + fileName;
		}
	}

	@Override
	public AssessmentQuestion updateQuestionAssest(String gooruQuestionId, String fileNames) throws Exception {
		AssessmentQuestion question = getQuestion(gooruQuestionId);
		final String repositoryPath = question.getOrganization().getNfsStorageArea().getInternalPath();
		final String mediaFolderPath = repositoryPath + "/" + Constants.UPLOADED_MEDIA_FOLDER;
		String[] assetKeyArr = fileNames.split("\\s*,\\s*");
		for (int i = 0; i < assetKeyArr.length; i++) {
			String resourceImageFile = mediaFolderPath + "/" + assetKeyArr[i];
			File mediaImage = new File(resourceImageFile);

			String assetKey = StringUtils.left(assetKeyArr[i], assetKeyArr[i].indexOf("_"));
			String fileName = assetKeyArr[i].split("_")[0];
			byte[] fileContent = FileUtils.readFileToByteArray(mediaImage);
			if (fileContent.length > 0) {
				AssessmentQuestionAssetAssoc questionAsset = null;
				if (assetKey != null && assetKey.length() > 0) {
					questionAsset = getQuestionAsset(assetKey, gooruQuestionId);
				}
				Asset asset = null;
				if (questionAsset == null) {
					asset = new Asset();
					asset.setHasUniqueName(true);
					questionAsset = new AssessmentQuestionAssetAssoc();
					questionAsset.setQuestion(question);
					questionAsset.setAsset(asset);
					questionAsset.setAssetKey(assetKey);
				} else {
					asset = questionAsset.getAsset();
				}
				asset.setFileData(fileContent);
				asset.setName(fileName);
				Set<AssessmentQuestionAssetAssoc> assets = new HashSet<AssessmentQuestionAssetAssoc>();
				assets.add(uploadQuestionAsset(gooruQuestionId, questionAsset, true));
				question.setAssets(assets);
				mediaImage.delete();
			}
		}
		indexProcessor.index(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		return question;
	}
	
	@Override
	public AssessmentQuestion updateQuestionVideoAssest(String gooruQuestionId, String assetKeys) throws Exception {
		AssessmentQuestion question = getQuestion(gooruQuestionId);
		String[] assetKeyArr = assetKeys.split("\\s*,\\s*");
		for (int i = 0; i < assetKeyArr.length; i++) {
			String assetKey = assetKeyArr[i];
			//String fileName = assetKeyArr[i].split("_")[0];
			if (resourceImageUtil.getYoutubeVideoId(assetKey) != null || assetKey.contains(YOUTUBE_URL)) {
				AssessmentQuestionAssetAssoc questionAsset = null;
				if (assetKey != null && assetKey.length() > 0) {
					questionAsset = getQuestionAsset(assetKey, gooruQuestionId);
				}
				Asset asset = null;
				if (questionAsset == null) {
					asset = new Asset();
					asset.setHasUniqueName(true);
					questionAsset = new AssessmentQuestionAssetAssoc();
					questionAsset.setQuestion(question);
					questionAsset.setAsset(asset);
					questionAsset.setAssetKey(assetKey);
				} else {
					asset = questionAsset.getAsset();
				}
				asset.setName(assetKey);
				asset.setUrl(assetKey);
				
				assessmentRepository.save(asset);

				assessmentRepository.saveAndFlush(questionAsset);
				
				Set<AssessmentQuestionAssetAssoc> assets = new HashSet<AssessmentQuestionAssetAssoc>();
				assets.add(questionAsset);
				question.setAssets(assets);
				
			}
		}
		indexProcessor.index(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		return question;
	}

	@Override
	public void deleteQuestionAssest(String gooruQuestionId) throws Exception {
		this.assessmentRepository.deleteQuestionAssoc(gooruQuestionId);
	}

	@Override
	public void deleteQuizBulk(String gooruContentIds) {
		List<Resource> quizResources = resourceRepository.findAllResourcesByGooruOId(gooruContentIds);
		List<Resource> removeQuizList = new ArrayList<Resource>();
		if (quizResources.size() > 0) {
			String removeContentIds = "";
			int count = 0;
			for (Resource resource : quizResources) {
				if (count > 0) {
					removeContentIds += ",";
				}
				if (resource.getResourceType().getName().equals(ResourceType.Type.ASSESSMENT_EXAM.getType()) || resource.getResourceType().getName().equals(ResourceType.Type.ASSESSMENT_QUIZ.getType())) {
					removeContentIds += resource.getGooruOid();
					removeQuizList.add(resource);
					count++;
				}
			}
			if (removeQuizList.size() > 0) {
				this.baseRepository.removeAll(removeQuizList);
				indexProcessor.index(removeContentIds, IndexProcessor.DELETE, QUIZ);
			}
		}
	}

	@Override
	public void deleteQuestionBulk(String gooruQuestionIds) {
		List<Resource> questionResources = resourceRepository.findAllResourcesByGooruOId(gooruQuestionIds);
		List<Resource> removeQuestionList = new ArrayList<Resource>();
		if (questionResources.size() > 0) {
			String removeContentIds = "";
			int count = 0;
			for (Resource resource : questionResources) {
				if (count > 0) {
					removeContentIds += ",";
				}
				if (resource.getResourceType().getName().equals(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
					removeContentIds += resource.getGooruOid();
					removeQuestionList.add(resource);
					count++;
				}
			}
			if (removeQuestionList.size() > 0) {
				this.baseRepository.removeAll(removeQuestionList);
				indexProcessor.index(removeContentIds, IndexProcessor.INDEX, RESOURCE);
			}
		}
	}

	@Override
	public AssessmentQuestion buildQuestionFromInputParameters(String jsonData, User user, boolean addFlag) {

		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias(QUESTION, AssessmentQuestion.class);
		xstream.alias(ANSWER, AssessmentAnswer.class);
		xstream.alias(HINT, AssessmentHint.class);
		xstream.alias(TAXONOMY_CODE, Code.class);
		xstream.alias(_DEPTH_OF_KNOWLEDGE, ContentMetaDTO.class);
		xstream.alias(_EDUCATIONAL_USE, ContentMetaDTO.class);
		AssessmentQuestion question = (AssessmentQuestion) xstream.fromXML(jsonData);
		if (addFlag) {
			question.setUser(user);
		}
		if (question.getAnswers() != null) {
			question.setAnswers(new TreeSet<AssessmentAnswer>(question.getAnswers()));
		}
		if (question.getHints() != null) {
			question.setHints(new TreeSet<AssessmentHint>(question.getHints()));
		}

		return question;
	}

	@Override
	public String findAssessmentNameByGooruOId(String gooruOId) {
		return assessmentRepository.findAssessmentNameByGooruOid(gooruOId);
	}

	public SessionActivityService getSessionActivityService() {
		return sessionActivityService;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	@Override
	public Assessment findQuizContent(String quizGooruOid) {
		return assessmentRepository.findQuizContent(quizGooruOid);
	}

	public RevisionHistoryService getRevisionHistoryService() {
		return revisionHistoryService;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}
	

}
