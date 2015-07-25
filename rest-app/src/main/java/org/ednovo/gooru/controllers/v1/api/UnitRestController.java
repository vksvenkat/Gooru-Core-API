package org.ednovo.gooru.controllers.v1.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.collection.UnitService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { RequestMappingUri.UNIT })
@Controller
public class UnitRestController extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private UnitService unitService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createUnit(@PathVariable(value = COURSE_ID) final String courseId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<Collection> responseDTO = this.getUnitService().createUnit(courseId, buildUnit(data), user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseDTO.getModel().setUri(generateUri(request.getRequestURI(), responseDTO.getModel().getGooruOid()));
		}
		String includes[] = (String[]) ArrayUtils.addAll(CREATE_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.PUT)
	public void updateUnit(@PathVariable(value = COURSE_ID) final String courseId, @PathVariable(value = ID) final String unitId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		this.getUnitService().updateUnit(courseId, unitId, buildUnit(data), user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.GET)
	public ModelAndView getUnit(@PathVariable(value = COURSE_ID) final String courseId, @PathVariable(value = ID) final String unitId, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(getUnitService().getUnit(unitId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, "*");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getUnits(@PathVariable(value = COURSE_ID) final String courseId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") int offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") int limit, final HttpServletRequest request,
			final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(getUnitService().getUnits(courseId, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, "*");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.DELETE)
	public void deleteUnit(@PathVariable(value = COURSE_ID) final String courseId, @PathVariable(value = ID) final String unitId, final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		this.getUnitService().deleteUnit(courseId, unitId, user);
	}

	private Collection buildUnit(final String data) {
		return JsonDeserializer.deserialize(data, Collection.class);
	}

	public UnitService getUnitService() {
		return unitService;
	}

}
