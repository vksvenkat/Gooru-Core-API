package org.ednovo.gooru.controllers.v3.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { "/v3/class" })
public class ClassRestV3Controller extends BaseController implements ConstantProperties, ParameterProperties {
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createClass(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public ModelAndView updateClass(@PathVariable(value = ID) final String classId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return null;
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getClass(@PathVariable(value = ID) final String classId,  final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		return null;
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/teach", method = RequestMethod.GET)
	public ModelAndView getTeachClasses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
		     final HttpServletRequest request, final HttpServletResponse response) throws Exception {
				return null;
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/study", method = RequestMethod.GET)
	public ModelAndView getStudyClasses(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
		     final HttpServletRequest request, final HttpServletResponse response) throws Exception {
				return null;
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CLASSPAGE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteClass(@PathVariable(value = ID) final String classId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
	}
}
