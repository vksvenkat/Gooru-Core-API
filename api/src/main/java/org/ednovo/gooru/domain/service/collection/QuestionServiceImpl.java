package org.ednovo.gooru.domain.service.collection;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.AssessmentAnswer;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.Asset;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.QuestionRepository;
import org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl extends AbstractResourceServiceImpl implements QuestionService, ParameterProperties, ConstantProperties {

	@Autowired
	private MongoQuestionsService mongoQuestionsService;

	@Autowired
	private QuestionRepository questionRepository;

	@Override
	public AssessmentQuestion createQuestion(AssessmentQuestion question, User user) {
		License license = (License) getBaseRepository().get(License.class, CREATIVE_COMMONS);
		question.setLicense(license);
		ContentType contentType = (ContentType) getBaseRepository().get(ContentType.class, ContentType.RESOURCE);
		question.setContentType(contentType);
		question.setGooruOid(UUID.randomUUID().toString());
		question.setCreatedOn(new Date(System.currentTimeMillis()));
		question.setLastModified(new Date(System.currentTimeMillis()));
		question.setDistinguish((short) 0);
		question.setIsFeatured(0);
		if (question.getSharing() == null) {
			question.setSharing(PUBLIC);
		}
		if (question.getTypeName().equalsIgnoreCase(AssessmentQuestion.TYPE.MATCH_THE_FOLLOWING.getName()) && question.getAnswers().size() > 0) {
			for (AssessmentAnswer assessmentAnswer : question.getAnswers()) {
				for (AssessmentAnswer matchingAnswer : question.getAnswers()) {
					if (assessmentAnswer.getMatchingSequence() != null && assessmentAnswer.getMatchingSequence().equals(matchingAnswer.getMatchingSequence()) && !assessmentAnswer.getSequence().equals(matchingAnswer.getSequence())) {
						matchingAnswer.setMatchingAnswer(assessmentAnswer);
					}
				}
			}
		}
		ResourceType resourceType = null;
		if (question.getSourceReference() != null && question.getSourceReference().equalsIgnoreCase(ASSESSMENT)) {
			resourceType = (ResourceType) getBaseRepository().get(ResourceType.class, ResourceType.Type.AM_ASSESSMENT_QUESTION.getType());
		} else {
			resourceType = (ResourceType) getBaseRepository().get(ResourceType.class, ResourceType.Type.ASSESSMENT_QUESTION.getType());
		}
		question.setResourceType(resourceType);
		question.setTypeName(question.getTypeName());
		question.setCategory(QUESTION);
		question.setResourceFormat(this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, QUESTION));
		question.setOrganization(user.getOrganization());
		question.setCreator(user);
		question.setUser(user);
		question.setIsOer(1);
		this.getQuestionRepository().save(question);
		getIndexHandler().setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		return question;
	}

	@Override
	public void updateQuestion(String questionId, AssessmentQuestion assessmentQuestion, User user) {
		AssessmentQuestion question = this.getQuestionRepository().getQuestion(questionId);
		rejectIfNull(question, GL0056, QUESTION);

	}

	public AssessmentQuestion updateQuestionAssest(String gooruQuestionId, String fileNames) throws Exception {
		return null;
	/*	AssessmentQuestion question = getQuestion(gooruQuestionId);
	//	final String repositoryPath = question.getOrganization().getNfsStorageArea().getInternalPath();
		//final String mediaFolderPath = repositoryPath + "/" + Constants.UPLOADED_MEDIA_FOLDER;
		String[] assetKeyArr = fileNames.split("\\s*,\\s*");
		for (int i = 0; i < assetKeyArr.length; i++) {
		//	String resourceImageFile = mediaFolderPath + "/" + assetKeyArr[i];
		//	File mediaImage = new File(resourceImageFile);
		//	if (!mediaImage.isFile()) {
				throw new BadRequestException("file not found");
			}
			String assetKey = StringUtils.left(assetKeyArr[i], assetKeyArr[i].indexOf("_"));
			String fileName = assetKeyArr[i].split("_")[1];
			byte[] fileContent = FileUtils.readFileToByteArray(mediaImage);
			if (fileContent.length > 0) {
				AssessmentQuestionAssetAssoc questionAsset = null;
				if (assetKey != null && assetKey.length() > 0) {
					//questionAsset = getQuestionAsset(assetKey, gooruQuestionId);
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
				//assets.add(uploadQuestionAsset(gooruQuestionId, questionAsset, true));
				question.setAssets(assets);
				mediaImage.delete();
			}
		}
		getIndexHandler().setReIndexRequest(question.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		return question;*/
	}

	public MongoQuestionsService getMongoQuestionsService() {
		return mongoQuestionsService;
	}

	public QuestionRepository getQuestionRepository() {
		return questionRepository;
	}

}
