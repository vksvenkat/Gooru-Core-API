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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentHint;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.Asset;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.ContentMetaDTO;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.ErrorMessage;
import org.ednovo.gooru.core.application.util.ResourceMetaInfo;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.content.ContentService;
import org.ednovo.gooru.domain.service.resource.AssetManager;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.sessionActivity.SessionActivityService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
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

	@Autowired
	private IndexHandler indexHandler;

	@Override
	public AssessmentQuestion getQuestion(String gooruOQuestionId) {
		return (AssessmentQuestion) assessmentRepository.getByGooruOId(AssessmentQuestion.class, gooruOQuestionId);
	}

	@Override
	public ActionResponseDTO<AssessmentQuestion> createQuestion(AssessmentQuestion question, boolean index) throws Exception {
		Set<Code> taxonomy = question.getTaxonomySet();
		question = initQuestion(question, null, true);
		question.setIsOer(1);
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

			if (index) {
				try {
					indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
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
			assessmentRepository.save(question);

			if (depth != null && depth.size() > 0) {
				question.setDepthOfKnowledges(this.collectionService.updateContentMeta(depth, question.getGooruOid(), question.getUser(), DEPTH_OF_KNOWLEDGE));
			} else {
				question.setDepthOfKnowledges(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), question.getGooruOid(), DEPTH_OF_KNOWLEDGE));
			}
			if (educational != null && educational.size() > 0) {
				question.setEducationalUse(this.collectionService.updateContentMeta(educational, question.getGooruOid(), question.getUser(), EDUCATIONAL_USE));
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
				indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
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
				License license = (License) baseRepository.get(License.class, CREATIVE_COMMONS);
				question.setLicense(license);
				ContentType contentType = (ContentType) baseRepository.get(ContentType.class, ContentType.RESOURCE);
				question.setContentType(contentType);
				if (question.getGooruOid() == null) {
					question.setGooruOid(UUID.randomUUID().toString());
				}
				// Explicitly set to null to reset any content id sent by
				// clients
				question.setContentId(null);
				question.setCreatedOn(new java.util.Date());
				question.setUrl("");
				if (question.getSharing() == null) {
					question.setSharing(PUBLIC);
				}
				if (question.getDistinguish() == null) {
					question.setDistinguish(Short.valueOf("0"));
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
				ResourceType resourceType = null;
				if (question.getSourceReference() != null && question.getSourceReference().equalsIgnoreCase(ASSESSMENT)) {
					resourceType = (ResourceType) baseRepository.get(ResourceType.class, ResourceType.Type.AM_ASSESSMENT_QUESTION.getType());
				} else {
					resourceType = (ResourceType) baseRepository.get(ResourceType.class, ResourceType.Type.ASSESSMENT_QUESTION.getType());
				}
				question.setResourceType(resourceType);
				question.setTypeName(question.getTypeName());
				question.setCategory(QUESTION);
				question.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, QUESTION));
			} else {
				AssessmentQuestion existingQuestion = getQuestion(gooruOQuestionId);
				if (existingQuestion == null) {
					throw new NotFoundException(ServerValidationUtils.generateErrorMessage(GL0056, RESOURCE), GL0056);
				}

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
			question.getLicense().setName(CREATIVE_COMMONS);
		}
		if (question.getUrl() == null) {
			question.setUrl("");
		}

		if (question.getQuestionText() == null) {
			throw new BadRequestException("Question Text is mandatory");

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
	public void updateQuetionInfo(String gooruOQuestionId, Integer segmentId) {
		updateQuestionTime(getQuestion(gooruOQuestionId));
	}

	private void updateQuestionTime(AssessmentQuestion question) {
		if (question != null) {
			assessmentRepository.updateTimeForSegments(question.getContentId());
			assessmentRepository.updateTimeForAssessments(question.getContentId());
		}
	}

	@Override
	public boolean getAttemptAnswerStatus(Integer answerId) {
		return assessmentRepository.getAttemptAnswerStatus(answerId);
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
			if (question.getTypeName().equals(AssessmentQuestion.TYPE.SHORT_ANSWER.getName()) && question.getTypeName().equals(AssessmentQuestion.TYPE.OPEN_ENDED.getName())) {
				ServerValidationUtils.rejectIfNullOrEmpty(errors, question.getAnswers(), ANSWERS, ErrorMessage.REQUIRED_FIELD);
			}
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
			License license = (License) baseRepository.get(License.class, OTHER);
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
				indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
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
					copyQuestion.setThumbnail(questionAssetAssoc.getAsset().getName());
					copyQuestionAssetAssoc.setQuestion(copyQuestion);
					questionAssets.add(copyQuestionAssetAssoc);
					assessmentRepository.save(copyQuestionAssetAssoc);
				}
				assessmentRepository.save(copyQuestion);
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

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@Override
	public String updateQuizQuestionImage(String gooruContentId, String fileName, Resource resource, String assetKey) throws Exception {
		if (fileName != null && ResourceImageUtil.getYoutubeVideoId(fileName) != null || fileName.contains(YOUTUBE_URL)) {
			return fileName;
		} else {
			final String mediaFolderPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + Constants.UPLOADED_MEDIA_FOLDER;
			String resourceImageFile = mediaFolderPath + "/" + fileName;
			String newImageFile = mediaFolderPath + "/" + assetKey + "_" + fileName;
			File mediaImage = new File(resourceImageFile);
			File newImage = new File(newImageFile);
			mediaImage.renameTo(newImage);
			fileName = newImage.getName();
			return newImageFile;
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
			if (!mediaImage.isFile()) {
				throw new BadRequestException("file not found");
			}
			String assetKey = StringUtils.left(assetKeyArr[i], assetKeyArr[i].indexOf("_"));
			String fileName = assetKeyArr[i].split("_")[1];
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
				question.setThumbnail(fileName);
				this.getBaseRepository().save(question);
				Set<AssessmentQuestionAssetAssoc> assets = new HashSet<AssessmentQuestionAssetAssoc>();
				assets.add(uploadQuestionAsset(gooruQuestionId, questionAsset, true));
				question.setAssets(assets);
				mediaImage.delete();
			}
		}
		indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		return question;
	}

	@Override
	public AssessmentQuestion updateQuestionVideoAssest(String gooruQuestionId, String assetKeys) throws Exception {
		AssessmentQuestion question = getQuestion(gooruQuestionId);
		String[] assetKeyArr = assetKeys.split("\\s*,\\s*");
		for (int i = 0; i < assetKeyArr.length; i++) {
			String assetKey = assetKeyArr[i];
			// String fileName = assetKeyArr[i].split("_")[0];
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
		indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
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
				indexHandler.setReIndexRequest(removeContentIds, IndexProcessor.DELETE, QUIZ, null, false, false);
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
				indexHandler.setReIndexRequest(removeContentIds, IndexProcessor.INDEX, RESOURCE, null, false, false);
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
		AssessmentQuestion question = null;
		try {
			question = (AssessmentQuestion) xstream.fromXML(jsonData);
		} catch (Exception e) {
			throw new BadRequestException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		}
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
	public int deleteQuestion(String gooruOQuestionId, User caller) {
		AssessmentQuestion question = getQuestion(gooruOQuestionId);
		if (question != null) {
			assessmentRepository.remove(AssessmentQuestion.class, question.getContentId());
			indexHandler.setReIndexRequest(question.getGooruOid(), IndexProcessor.DELETE, RESOURCE, null, false, false);
          return 1;
		}
		return 0;
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

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

}
