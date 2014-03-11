/*******************************************************************************
 * QuizRestV2Controller.java
 *  gooru-v2-app
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/

/**
 * 
 */
package org.ednovo.gooru.controllers.v2.api;

import java.sql.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.quiz.QuizService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
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
@RequestMapping(value = { "/v2/quiz" })
public class QuizRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private QuizService quizSerivce;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createQuiz(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Quiz> responseDTO = this.getQuizSerivce().createQuiz(this.buildQuizFromInputParameters(getValue(QUIZ, json), user), true, this.quizSerivce.buildOptionsParameter(getValue("options", json)));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includesDefault[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_INCLUDE_FIELDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ModelAndView updateQuiz(@RequestBody String data, @PathVariable(value = ID) String quizId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<Quiz> responseDTO = getQuizSerivce().updateQuiz(quizId, this.buildQuizFromInputParameters(getValue(QUIZ, json), user), this.quizSerivce.buildOptionsParameter(getValue("options", json)));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includesDefault[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_INCLUDE_FIELDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/copy", method = RequestMethod.PUT)
	public ModelAndView copyQuiz(@RequestBody String data, @PathVariable(value = ID) String quizId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		Quiz quiz = getQuizSerivce().copyQuiz(quizId, this.buildCopyQuizFromInputParameters(getValue(QUIZ, json), user), false, user);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		return toModelAndViewWithIoFilter(quiz, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getQuiz(@PathVariable(value = ID) String quizId, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includesDefault = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_INCLUDE_FIELDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(this.getQuizSerivce().getQuizList(quizId, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getQuizzes(@RequestParam(value = LIMIT_FIELD, required = true) Integer limit, @RequestParam(value = OFFSET_FIELD, required = true) Integer offset, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_INCLUDE_FIELDS);
		return toModelAndView(serialize(this.getQuizSerivce().getQuizzes(limit, offset, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView getMyQuizzes(@RequestParam(value = DATA_OBJECT, required = true) String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includesDefault[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_INCLUDE_FIELDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(this.getQuizSerivce().getMyQuizzes(getValue(LIMIT_FIELD, json), getValue(OFFSET_FIELD, json), user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteQuiz(@PathVariable(value = ID) String quizId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		this.getQuizSerivce().deleteQuiz(quizId, user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{qid}/item", method = RequestMethod.POST)
	public ModelAndView createQuizItem(@PathVariable(value = QUESTION_ID) String quizId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = this.getQuizSerivce().createQuizItem(getValue(RESOURCE_ID, json), quizId, this.buildQuizItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), user, CollectionType.USER_QUIZ.getCollectionType());
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includesDefault[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_ITEM_INCLUDE_FILEDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{qid}/item/{id}", method = RequestMethod.PUT)
	public ModelAndView updateQuizItem(@PathVariable(value = QUESTION_ID) String quizId, @RequestBody String data, @PathVariable(value = ID) String quizItemId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject json = requestData(data);
		ActionResponseDTO<CollectionItem> responseDTO = getQuizSerivce().updateQuizItem(this.buildQuizItemFromInputParameters(getValue(COLLECTION_ITEM, json), user), quizItemId);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		String includesDefault[] = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_ITEM_INCLUDE_FILEDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{qid}/item/{id}/reorder/{sequence}", method = RequestMethod.PUT)
	public ModelAndView reorderQuizItem(@PathVariable(value = QUESTION_ID) String quizId, @PathVariable(value = ID) String quizItemId, @PathVariable(value = SEQUENCE) int newSequence, @RequestParam(value = DATA_OBJECT, required = false) String data, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = getQuizSerivce().reorderQuizItem(quizItemId, newSequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		String[] includeFields = null;
		if (data != null && !data.isEmpty()) {
			JSONObject json = requestData(data);
			includeFields = getValue(FIELDS, json) != null ? getFields(getValue(FIELDS, json)) : null;
		}
		String[] includesDefault = (String[]) ArrayUtils.addAll(QUIZ_INCLUDES, COLLECTION_ITEM_INCLUDE_FILEDS);
		String includes[] = (String[]) ArrayUtils.addAll(includeFields == null ? includesDefault : includeFields, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_QUIZ_V2_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{qid}/item/{id}", method = RequestMethod.DELETE)
	public void deleteQuizItem(@PathVariable(value = QUESTION_ID) String quizId, @PathVariable(value = ID) String quizItemId, HttpServletRequest request, HttpServletResponse response) {
		getQuizSerivce().deleteQuizItem(quizItemId);
	}

	private Quiz buildCopyQuizFromInputParameters(String data, User user) {
		Quiz quiz = JsonDeserializer.deserialize(data, Quiz.class);
		return quiz;
	}

	private CollectionItem buildQuizItemFromInputParameters(String data, User user) {
		CollectionItem collectionItem = JsonDeserializer.deserialize(data, CollectionItem.class);
		return collectionItem;
	}

	private Quiz buildQuizFromInputParameters(String data, User user) {
		Quiz quiz = JsonDeserializer.deserialize(data, Quiz.class);
		quiz.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getQuizSerivce().getContentType(ContentType.RESOURCE);
		quiz.setContentType(contentType);
		ResourceType resourceType = getQuizSerivce().getResourceType(ResourceType.Type.QUIZ.getType());
		quiz.setResourceType(resourceType);
		quiz.setLastModified(new Date(System.currentTimeMillis()));
		quiz.setCreatedOn(new Date(System.currentTimeMillis()));
		quiz.setCollectionType(CollectionType.Quiz.getCollectionType());
		if (!hasUnrestrictedContentAccess()) {
			quiz.setSharing(Sharing.PUBLIC.getSharing());
		} else {
			quiz.setSharing(quiz.getSharing() != null && (quiz.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || quiz.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || quiz.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? quiz.getSharing()
					: Sharing.PUBLIC.getSharing());
		}
		quiz.setUser(user);
		quiz.setOrganization(user.getPrimaryOrganization());
		quiz.setCreator(user);
		quiz.setDistinguish(Short.valueOf("0"));
		quiz.setRecordSource(NOT_ADDED);
		quiz.setIsFeatured(0);
		quiz.setLastUpdatedUserUid(user.getGooruUId());
		return quiz;
	}

	public QuizService getQuizSerivce() {
		return quizSerivce;
	}

}
