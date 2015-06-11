package org.ednovo.gooru.controllers.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ResponseFieldSet;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.responseFieldSet.ResponseFieldSetService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/responseFieldSet" })
public class ResponseFieldSetRestController extends BaseController implements ParameterProperties {

	@Autowired
	private ResponseFieldSetService responseFieldSetService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESPONSE_FIELD_SET_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createResponseFieldSet(HttpServletRequest request, @RequestParam(value = FIELD_SET) String fieldSet, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response)
			throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ResponseFieldSet responseFieldSet = responseFieldSetService.addResponseFieldSet(fieldSet, apiCaller);
		return toModelAndView(new JSONObject(serializeToJson(responseFieldSet)));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESPONSE_FIELD_SET_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{fieldSetId}")
	public ModelAndView updateResponseFieldSet(HttpServletRequest request, @PathVariable(FIELD_SET_ID ) String fieldSetId, @RequestParam(value = FIELD_SET) String fieldSet, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ResponseFieldSet responseFieldSet = responseFieldSetService.updateResponseFieldSet(fieldSetId, fieldSet, apiCaller);
		return toModelAndView(serializeToJson(responseFieldSet));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESPONSE_FIELD_SET_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/{fieldSetId}")
	public ModelAndView deleteResponseFieldSet(HttpServletRequest request, @PathVariable(FIELD_SET_ID) String fieldSetId, @RequestParam(value = SESSIONTOKEN, required= false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response)
			throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		String value = responseFieldSetService.deleteResponseFieldSet(fieldSetId, apiCaller);
		return toModelAndView(value);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESPONSE_FIELD_SET_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{fieldSetId}")
	public ModelAndView getResponseFieldSet(HttpServletRequest request, @PathVariable(FIELD_SET_ID) String fieldSetId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ResponseFieldSet responseFieldSet = responseFieldSetService.getResponseFieldSet(fieldSetId, apiCaller.getGooruUId());
		return toModelAndView(serializeToJson(responseFieldSet));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESPONSE_FIELD_SET_LIST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET })
	public ModelAndView getResponseFieldsSet(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		List<ResponseFieldSet> responseFieldSet = responseFieldSetService.getResponseFieldSet();
		return toModelAndView(serializeToJson(responseFieldSet));
	}

}
