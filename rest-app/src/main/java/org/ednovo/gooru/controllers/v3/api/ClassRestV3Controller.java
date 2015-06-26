package org.ednovo.gooru.controllers.v3.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.ClassService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
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

@RequestMapping(value = { RequestMappingUri.V3_CLASS })
@Controller
public class ClassRestV3Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private ClassService classService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createClass(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response)  {
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<UserClass> responseDTO = this.getClassService().createClass(buildClass(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseDTO.getModel().setUri(RequestMappingUri.V3_CLASS + RequestMappingUri.SEPARATOR + responseDTO.getModel().getPartyUid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(CREATE_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_UPDATE })
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public void updateClass(@PathVariable(value = ID) final String classUId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		this.getClassService().updateClass(classUId, this.buildClass(data), user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClass(@PathVariable(value = ID) final String classUId, final HttpServletRequest request, final HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(this.getClassService().getClass(classUId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CLASS_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getClasses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") final int offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") final int limit, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getClassService().getClasses(null, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CLASS_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@RequestMapping(value = "/teach", method = RequestMethod.GET)
	public ModelAndView getTeachClasses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getClassService().getClasses(user.getPartyUid(), limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CLASS_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/study", method = RequestMethod.GET)
	public ModelAndView getStudyClasses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		return toModelAndViewWithIoFilter(this.getClassService().getStudyClasses(user.getPartyUid(), limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CLASS_INCLUDES);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteClass(@PathVariable(value = ID) final String classId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@RequestMapping(value = RequestMappingUri.DELETE_USER_FROM_CLASS, method = RequestMethod.DELETE)
	public void removeFromClass(@PathVariable(value = ID) final String classUid, @PathVariable(value = USER_UID) final String userUid, final HttpServletRequest request, final HttpServletResponse response) {
		this.getClassService().deleteUserFromClass(classUid, userUid);
	}
	
	private UserClass buildClass(final String data) {
		return JsonDeserializer.deserialize(data, UserClass.class);
	}

	public ClassService getClassService() {
		return classService;
	}
}
