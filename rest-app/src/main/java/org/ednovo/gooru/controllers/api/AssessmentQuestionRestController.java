package org.ednovo.gooru.controllers.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.AssessmentQuestionAssetAssoc;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.Asset;
import org.ednovo.gooru.core.api.model.QuestionSet;
import org.ednovo.gooru.core.api.model.QuestionSetQuestionAssoc;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

@Controller
@RequestMapping(value = { "/assessmentQuestion", "" })
public class AssessmentQuestionRestController extends BaseController implements ConstantProperties {

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	@javax.annotation.Resource(name = "classplanConstants")
	private Properties classPlanConstants;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment-question.{format}")
	public ModelAndView createQuestion(@PathVariable(FORMAT) String format, @RequestParam(value = DATA_OBJECT) String data, @RequestParam(value = INDEX_FLAG, required = false, defaultValue = FALSE) Boolean indexFlag, @RequestParam(value = COLLECTION_ID, required = false) String collectionId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_CREATE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		AssessmentQuestion question = assessmentService.buildQuestionFromInputParameters(data, apiCaller, true);
		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.createQuestion(question, indexFlag);
		setActionResponseStatus(response, responseDTO);
		Map<String, String> customFieldAndValueMap = collectionUtil.getCustomFieldNameAndValueAsMap(request);
		if (customFieldAndValueMap.size() > 0) {
			customFieldService.saveCustomFieldInfo(question.getGooruOid(), customFieldAndValueMap);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, QUESTION_CREATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());

		return toModelAndViewWithInFilter(responseDTO.getModelData(), format, QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/assessment-question/{gooruOQuestionId}.{format}")
	public ModelAndView updateQuestion(@PathVariable(FORMAT) String format, @RequestBody MultiValueMap<String, String> body, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_UPDATE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		AssessmentQuestion question = assessmentService.buildQuestionFromInputParameters(body.getFirst(DATA_OBJECT), apiCaller, false);

		ActionResponseDTO<AssessmentQuestion> responseDTO = assessmentService.updateQuestion(question, parseJSONArray(body.getFirst(DELETE_ASSETS)), gooruOQuestionId, true, true);

		setUpdateActionResponseStatus(response, responseDTO);

		SessionContextSupport.putLogParameter(EVENT_NAME, "question-update");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(ASSESSMENT_GOORU_ID, question.getAssessmentGooruId());

		return toModelAndViewWithInFilter(responseDTO.getModelData(), format, QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment-question.{format}")
	public ModelAndView listQuestions(@PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_LIST_QUESTIONS);
		return toModelAndViewWithInFilter(assessmentService.listQuestions(null), format, QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment-question/{gooruOQuestionId}.{format}")
	public ModelAndView getQuestion(@PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, @PathVariable(GOORUO_QUESTION_ID) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setAttribute(PREDICATE, ASSESSMENT_GET_QUESTION);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		AssessmentQuestion question = assessmentService.getQuestion(gooruOQuestionId);

		JSONObject questionObject = serializeToJsonObject(question, QUESTION_INCLUDES);

		JSONObject socialData = collectionUtil.getContentSocialData(apiCaller, question.getGooruOid());
		questionObject.put(SOCIAL, socialData);
		jsonmodel.addObject(MODEL, questionObject);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/assessment-question/{gooruOQuestionId}")
	public ModelAndView deleteQuestion(@PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_DELETE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		setDeleteResponseStatus(response, assessmentService.deleteQuestion(gooruOQuestionId, apiCaller));
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(IS_DELETE_SUCCESS, 1);
		jsonmodel.addObject(MODEL, jsonObj);

		SessionContextSupport.putLogParameter(EVENT_NAME, QUESTION_DELETE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GET_GOORU_OID, gooruOQuestionId);

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment/{gooruOAssessmentId}/resources.{format}")
	public ModelAndView getAssessmentQuestions(@PathVariable(GOORUO_ASSESSMENT_ID) String gooruOAssessmentId, @PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_GET_ASSESS_QUESTIONS);
		return toModelAndViewWithInFilter(assessmentService.getAssessmentQuestions(gooruOAssessmentId), FORMAT_JSON, ASSETS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/assessment/{gooruOAssessmentId}/segment/question/{segmentId}/{gooruOQuestionId}")
	public void deleteSegmentQuestion(@PathVariable(SEGMENT_ID) Integer segmentId, @PathVariable(GOORUO_ASSESSMENT_ID) String gooruOAssessmentId, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_DEL_SEG_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		int result = assessmentService.deleteSegmentQuestion(segmentId, gooruOAssessmentId, gooruOQuestionId, apiCaller);

		setDeleteResponseStatus(response, result);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment/segment/{segmentId}/{gooruOQuestionId}/{sequence}.{format}")
	public ModelAndView createSegmentQuestion(@PathVariable(SEGMENT_ID) Integer segmentId, @PathVariable(SEQUENCE) Integer sequence, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, @PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, ASSESS_CREATE_SEG_QUESTION);
		AssessmentSegmentQuestionAssoc existingSegmentQuestion = assessmentService.findSegmentQuestion(segmentId, gooruOQuestionId);
		if (existingSegmentQuestion == null) {
			AssessmentSegmentQuestionAssoc segmentQuestion = new AssessmentSegmentQuestionAssoc();
			segmentQuestion.setQuestion(assessmentService.getQuestion(gooruOQuestionId));
			segmentQuestion.getSegment().setSegmentId(segmentId);
			segmentQuestion.setSequence(sequence);
			assessmentService.createSegmentQuestion(segmentQuestion);
			return toModelAndViewWithInFilter(segmentQuestion, format, SEGMENT_QUESTION_INCLUDES);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return toModelAndViewWithInFilter(existingSegmentQuestion, format, SEGMENT_QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/assessment/segment/first/{gooruOQuestionId}/{assessmentId}.{format}")
	public void groupSegmentQuestionAdmin(@PathVariable(ASSESSMENT_ID) String assessmentId, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, @PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_GRP_SEG_QUES_ADMIN);
		List<String> assessmentIds = Arrays.asList(assessmentId.split("\\s*,\\s*"));
		List<Assessment> assessmentList = assessmentService.getAssessmentList(assessmentIds);

		for (Assessment assessment : assessmentList) {
			AssessmentSegmentQuestionAssoc segmentQuestion = new AssessmentSegmentQuestionAssoc();
			segmentQuestion.setQuestion(assessmentService.getQuestion(gooruOQuestionId));
			Iterator<AssessmentSegment> iterator = assessment.getSegments().iterator();
			boolean firstSegment = true;
			while (iterator.hasNext() && firstSegment) {
				firstSegment = false;
				AssessmentSegment assessmentSegment = iterator.next();
				Integer segmentId = assessmentSegment.getSegmentId().intValue();
				AssessmentSegmentQuestionAssoc existingSegmentQuestion = assessmentService.findSegmentQuestion(segmentId, gooruOQuestionId);
				if (existingSegmentQuestion == null) {
					segmentQuestion.getSegment().setSegmentId(assessmentSegment.getSegmentId());
					Integer sequence = assessmentSegment.getSegmentQuestions().size() + 1;
					segmentQuestion.setSequence(sequence);
					assessmentService.createSegmentQuestion(segmentQuestion);
				}
			}
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment/questionSet/{gooruOQuestionSetId}.{format}")
	public ModelAndView getQuestionSet(@PathVariable(FORMAT) String format, @PathVariable(GOORUO_QUESTION_SET_ID) String gooruOQuestionSetId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_GET_QUES_SET);
		QuestionSet questionSet = assessmentService.getQuestionSet(gooruOQuestionSetId);
		setGetResponseStatus(response, questionSet);

		return toModelAndViewWithInFilter(questionSet, format, QUESTION_SET_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_LIST })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment/questionSet.{format}")
	public ModelAndView getQuestionSets(@PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_GET_QUES_SETS);
		return toModelAndViewWithInFilter(assessmentService.listQuestionSets(null), format, QUESTION_SET_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/assessment/questionSet/{gooruOQuestionSetId}/{gooruOQuestionId}")
	public void deleteQuestionSetQuestion(@PathVariable(GOORUO_QUESTION_SET_ID) String gooruOQuestionSetId, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_GET_DEL_QUES_SET_QUES);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		setDeleteResponseStatus(response, assessmentService.deleteQuestionSetQuestion(gooruOQuestionSetId, gooruOQuestionId, apiCaller));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/assessment/questionSet/{gooruOQuestionSetId}")
	public void deleteQuestionSet(@PathVariable(GOORUO_QUESTION_SET_ID) String gooruOQuestionSetId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_DEL_QUES_SET);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		setDeleteResponseStatus(response, assessmentService.deleteQuestionSet(gooruOQuestionSetId, apiCaller));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment/questionSet.{format}")
	public ModelAndView createQuestionSet(@PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = DATA_OBJECT) String data) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_CREATE_QUES_SET);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<QuestionSet> responseDTO = assessmentService.createQuestionSet(buildQuestionSetFromInputParameters(data, apiCaller, true));

		setActionResponseStatus(response, responseDTO);

		return toModelAndViewWithInFilter(responseDTO.getModelData(), format, QUESTION_SET_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/assessment/questionSet/{gooruOQuestionSetId}.{format}")
	public ModelAndView createQuestionSet(@PathVariable(GOORUO_QUESTION_SET_ID) String gooruOQuestionSetId, @PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = DATA_OBJECT) String data) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_CREATE_QUES_SET);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<QuestionSet> responseDTO = assessmentService.updateQuestionSet(buildQuestionSetFromInputParameters(data, apiCaller, false), gooruOQuestionSetId);

		setActionResponseStatus(response, responseDTO);

		return toModelAndViewWithInFilter(responseDTO.getModelData(), format, QUESTION_SET_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/assessment/questionSet/{gooruOQuestionSetId}/{sequence}/{gooruOQuestionId}.{format}")
	public ModelAndView createQuestionSetQuestion(@PathVariable(GOORUO_QUESTION_SET_ID) String gooruOQuestionSetId, @PathVariable(GOORUO_QUESTION_ID) String gooruOQuestionId, @PathVariable(SEQUENCE) Integer sequence, @PathVariable(FORMAT) String format, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_CREATE_QUES_SET_QUES);
		QuestionSetQuestionAssoc questionSetQuestion = new QuestionSetQuestionAssoc();
		questionSetQuestion.setQuestion(assessmentService.getQuestion(gooruOQuestionId));
		questionSetQuestion.setQuestionSet(assessmentService.getQuestionSet(gooruOQuestionSetId));
		questionSetQuestion.setSequence(sequence);
		assessmentService.createQuestionSetQuestion(questionSetQuestion);

		return toModelAndViewWithInFilter(questionSetQuestion, format, SEGMENT_QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment-question/{questionGooruId}/assets/assign")
	public void assignAssetToQuestionImage(HttpServletRequest request, @PathVariable(QUESTION_GOORU_ID) String questionGooruId, @RequestParam String assetIds, @RequestParam String assetKeys, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_SAVE_QUES_IMG);
		String[] assetKeyList = assetKeys.split(",");
		String[] assetIdList = assetIds.split(",");
		for (int sequence = 0; sequence < assetIdList.length; sequence++) {
			assessmentService.assignAsset(questionGooruId, Integer.parseInt(assetIdList[sequence]), assetKeyList[sequence]);
		}
	}

	/**
	 * Add an image resource to a segment based on the gooru content id and
	 * 
	 * Sample Request : POST
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9
	 * /segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/image.json?sessionToken
	 * =07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruQuestionId
	 *            - content id representing the classplan
	 * @param propName
	 *            - title of the resource
	 * @param propDesc
	 *            - description of the resource
	 * @param propURL
	 *            - url of the resource
	 * @param instruction
	 *            - instructions for a resource
	 * @param sessionToken
	 *            - session token for resource authorization
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment-question/{gooruQuestionId}/resource/image.{format}")
	public ModelAndView saveQuestionImage(HttpServletRequest request, @PathVariable(GOORU_QUESTION_ID) String gooruQuestionId, @PathVariable(FORMAT) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_SAVE_QUES_IMG);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request, true);

		Map<String, Map<String, Object>> files = (Map<String, Map<String, Object>>) formField.get(RequestUtil.UPLOADED_FILE_KEY);
		for (Entry<String, Map<String, Object>> fileEntry : files.entrySet()) {
			Map<String, Object> fileInfo = fileEntry.getValue();
			byte[] fileContent = (byte[]) fileInfo.get(FILE_DATA);
			String assetKey = fileEntry.getKey();
			if (fileContent.length > 0) {
				AssessmentQuestionAssetAssoc questionAsset = null;
				if (assetKey != null && assetKey.length() > 0) {
					questionAsset = assessmentService.getQuestionAsset(assetKey, gooruQuestionId);
				}
				Asset asset = null;
				if (questionAsset == null) {
					asset = new Asset();
					asset.setHasUniqueName(true);
					questionAsset = new AssessmentQuestionAssetAssoc();
					questionAsset.setQuestion(assessmentService.getQuestion(gooruQuestionId));
					questionAsset.setAsset(asset);
					questionAsset.setAssetKey(assetKey);
				} else {
					asset = questionAsset.getAsset();
				}
				asset.setFileData(fileContent);
				asset.setName((String) fileInfo.get(FILENAME));
				assessmentService.uploadQuestionAsset(gooruQuestionId, questionAsset, true);
			}
		}
		ActionResponseDTO<String> result = new ActionResponseDTO<String>();
		result.setModel(OK );
		ModelAndView modelAndView = new ModelAndView(REST_MODEL);
		modelAndView.addObject(MODEL, OK );
		return modelAndView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_COPY })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment-question/{gooruQuestionId}/copy.{format}")
	public ModelAndView copyQuestion(@PathVariable(FORMAT) String format, @PathVariable(GOORU_QUESTION_ID) String gooruQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_QUES_COPY);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithInFilter(assessmentService.copyAssessmentQuestion(apiCaller, gooruQuestionId), format, QUESTION_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment/{gooruAssessmentId}/segment/{segmentId}/question/reorder")
	public ModelAndView reorderQuestions(@RequestParam String reOrdered, @PathVariable(SEGMENT_ID) Integer segmentId, @PathVariable(GOORU_ASSESS_ID) String gooruAssessmentId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_REORDER_QUES);
		response.setStatus(assessmentService.reorderQuestions(segmentId, reOrdered));
		return toModelAndView(SUCCESS, BaseController.FORMAT_JSON);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/assessment/segment/{segmentId}/question/{gooruQuestionId}/info")
	public void updateQuestionInfo(@PathVariable(SEGMENT_ID) Integer segmentId, @PathVariable(GOORU_QUESTION_ID) String gooruQuestionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_UPDATE_QUES_INFO);
		assessmentService.updateQuetionInfo(gooruQuestionId, segmentId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/quiz-question/{gooruContentId}/media")
	public ModelAndView updateQuizQuestionImage(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = MEDIA_FILE_NAME) String fileName, @RequestParam(value = ASSET_KEY) String assetKey, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletResponse response) throws Exception {
		AssessmentQuestion question = this.assessmentService.getQuestion(gooruContentId);
		String filePath = assessmentService.updateQuizQuestionImage(gooruContentId, fileName, question, assetKey);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, filePath);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/quiz-question/{gooruContentId}/asset")
	public ModelAndView updateQuizQuestionAssets(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = FILE_NAMES) String fileNames, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		return toModelAndView(assessmentService.updateQuestionAssest(gooruContentId, fileNames).getAssets(), FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/quiz-question/{gooruContentId}/asset")
	public void deleteQuizQuestionAssets(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		assessmentService.deleteQuestionAssest(gooruContentId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/question/bulk/{gooruContentIds}")
	public void deleteQuestionBulk(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentIds, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		assessmentService.deleteQuestionBulk(gooruContentIds);
	}

	private QuestionSet buildQuestionSetFromInputParameters(String jsonData, User user, boolean addFlag) {

		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias(QUESTION_SET, QuestionSet.class);

		QuestionSet questionSet = (QuestionSet) xstream.fromXML(jsonData);
		if (addFlag) {
			questionSet.setUser(user);
		}
		return questionSet;
	}

	private void setActionResponseStatus(HttpServletResponse response, ActionResponseDTO<?> responseData) {
		response.setStatus((responseData.getErrors().hasErrors()) ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_CREATED);
	}

	private void setUpdateActionResponseStatus(HttpServletResponse response, ActionResponseDTO<?> responseData) {
		response.setStatus((responseData.getErrors().hasErrors()) ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_OK);
	}

	private void setGetResponseStatus(HttpServletResponse response, Serializable model) {
		response.setStatus((model != null) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
	}

	private List<Integer> parseJSONArray(String arrayData) throws Exception {

		List<Integer> list = new ArrayList<Integer>();
		if (arrayData != null && arrayData.length() > 2) {
			JSONArray jsonArray = new JSONArray(arrayData);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add((Integer) jsonArray.get(i));
			}
		}
		return list;
	}

	private void setDeleteResponseStatus(HttpServletResponse response, int status) {
		if (status == 0) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (status == 2) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
