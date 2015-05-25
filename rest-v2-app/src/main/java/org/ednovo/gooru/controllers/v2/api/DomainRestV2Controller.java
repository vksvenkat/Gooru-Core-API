package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.DomainService;
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


@Controller
@RequestMapping(value= {"/v2/domain"})
public class DomainRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	public DomainService domainService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = {" "} ,method = RequestMethod.POST)
	public ModelAndView createDomain(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final User user = (User) request.getAttribute(Constants.USER);
		ActionResponseDTO<Domain> responseDTO = getDomainService().createDomain(buildDomainFromInputParameters(data),user);
		String includes[] = (String[]) ArrayUtils.addAll(DOMAIN, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateDomain(@PathVariable(value = ID) Short domainId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getDomainService().updateDomain(domainId,buildDomainFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, DOMAIN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getDomain(@PathVariable(value = ID) Short DomainId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getDomainService().getDomain(DomainId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, DOMAIN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.GET)
	public ModelAndView getDomains(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getDomainService().getDomains(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, DOMAIN);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_DOMAIN_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteDomain(@PathVariable(value = ID) Short DomainId, HttpServletRequest request, HttpServletResponse response) {
		getDomainService().deleteDomain(DomainId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	
	public DomainService getDomainService() {
	return domainService;
	}
	
	private Domain buildDomainFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Domain.class);
	}
}