/////////////////////////////////////////////////////////////
//QuestionRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/question" })
public class QuestionRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private CustomFieldsService customFieldService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createQuestion(@RequestBody String data, @RequestParam(value = INDEX_FLAG, required = false, defaultValue = FALSE) Boolean indexFlag, @RequestParam(value = COLLECTION_ID, required = false) String collectionId, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_CREATE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		AssessmentQuestion question = getAssessmentService().buildQuestionFromInputParameters(data, apiCaller, true);
		ActionResponseDTO<AssessmentQuestion> responseDTO = getAssessmentService().createQuestion(question, indexFlag);
		setActionResponseStatus(response, responseDTO);
		Map<String, String> customFieldAndValueMap = collectionUtil.getCustomFieldNameAndValueAsMap(request);
		if (customFieldAndValueMap.size() > 0) {
			customFieldService.saveCustomFieldInfo(question.getGooruOid(), customFieldAndValueMap);
		}
		SessionContextSupport.putLogParameter(EVENT_NAME, QUESTION_CREATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		String[] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, QUESTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, USER_INCLUDES);
		includes = (String[]) ArrayUtils.addAll(includes, QUESTION_EXCLUDES);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ModelAndView updateQuestion(@RequestBody String data, @PathVariable(ID) String questionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_UPDATE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		JSONObject json = requestData(data);
		AssessmentQuestion question = this.getAssessmentService().buildQuestionFromInputParameters(getValue(QUESTION, json), apiCaller, false);

		ActionResponseDTO<AssessmentQuestion> responseDTO = getAssessmentService().updateQuestion(question, this.buildAssetsInputForm(getValue(DELETE_ASSETS, json)), questionId, true, true);

		setUpdateActionResponseStatus(response, responseDTO);

		SessionContextSupport.putLogParameter(EVENT_NAME, QUESTION_UPDATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(ASSESSMENT_GOORU_ID, question.getAssessmentGooruId());
		String[] includes = (String[]) ArrayUtils.addAll(ERROR_INCLUDE, QUESTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, USER_INCLUDES);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ModelAndView deleteQuestion(@PathVariable(ID) String questionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_DELETE_QUESTION);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		setDeleteResponseStatus(response, getAssessmentService().deleteQuestion(questionId, apiCaller));
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(IS_DELETE_SUCCESS, 1);
		jsonmodel.addObject(MODEL, jsonObj);

		SessionContextSupport.putLogParameter(EVENT_NAME, QUESTION_DELETE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GOORU_OID, questionId);

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUESTION_COPY })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{id}/copy")
	public ModelAndView copyQuestion(@PathVariable(ID) String questionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESS_QUES_COPY);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithInFilter(getAssessmentService().copyAssessmentQuestion(apiCaller, questionId), RESPONSE_FORMAT_JSON, QUESTION_INCLUDES);
	}

	private void setActionResponseStatus(HttpServletResponse response, ActionResponseDTO<?> responseData) {
		response.setStatus((responseData.getErrors().hasErrors()) ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_CREATED);
	}

	private void setUpdateActionResponseStatus(HttpServletResponse response, ActionResponseDTO<?> responseData) {
		response.setStatus((responseData.getErrors().hasErrors()) ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_OK);
	}

	private void setDeleteResponseStatus(HttpServletResponse response, int status) {
		if (status == 0) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else if (status == 2) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private List<Integer> buildAssetsInputForm(String data) {
		List<Integer> deleteAsseList = new ArrayList<Integer>();
		if (data != null && !data.isEmpty()) {
			String[] asset = data.split(",");
			for (int i = 0; i < asset.length; i++) {
				deleteAsseList.add(Integer.parseInt(asset[i]));
			}
		}
		return deleteAsseList;
	}

	public AssessmentService getAssessmentService() {
		return assessmentService;
	}
}
