package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SubDomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.subdomain.SubDomainService;
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
@RequestMapping(value = {"/v2/sub-domain"})
public class SubDomainRestV2Controller  extends BaseController implements ConstantProperties{
	
	@Autowired
	private SubDomainService subDomainService;
	
		@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBDOMAIN_ADD})
		@RequestMapping(method = RequestMethod.POST)
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ModelAndView createSubDomain(HttpServletRequest request, HttpServletResponse response, @RequestBody String data)throws Exception{
			JSONObject json = requestData(data);
			User user = (User) request.getAttribute(Constants.USER);
			ActionResponseDTO<SubDomain> subDomain = this.getSubDomainService().createSubDomain(buildSubDomainFromInputParameters(getValue(SUBDOMAIN, json)),user);
			return toModelAndViewWithIoFilter(subDomain.getModelData(), FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDES);
		}
		
		@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBDOMAIN_READ})
		@RequestMapping(method = RequestMethod.GET, value = " ")
		@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ModelAndView getSubDomains(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletResponse response, HttpServletRequest request)throws Exception{
			return toModelAndViewWithIoFilter(this.getSubDomainService().getSubDomain(limit, offset), FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDES);
		}
		
		@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBDOMAIN_READ})
		@RequestMapping(method = RequestMethod.GET, value = "/{id}")
		@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ModelAndView getSubDomain(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) String SubDomainId)throws Exception{
			SubDomain subDomainObj = this.getSubDomainService().getSubDomain(SubDomainId);
			return toModelAndViewWithIoFilter(subDomainObj, FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDES);
		}
		
		@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBDOMAIN_UPDATE})
		@RequestMapping(method = RequestMethod.PUT, value ="/{id}")
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public ModelAndView updateSubDomain(HttpServletResponse response, HttpServletRequest request,@RequestBody String data, @PathVariable(ID) String subDomainId)throws Exception{
			JSONObject json = requestData(data);
			User user = (User) request.getAttribute(Constants.USER);
			return toModelAndViewWithIoFilter(this.getSubDomainService().updateSubDomain(buildSubDomainFromInputParameters(getValue(SUBDOMAIN, json)), user, subDomainId), FORMAT_JSON, EXCLUDE_ALL, true, SUBDOMAIN_INCLUDES);
		}
			
		@AuthorizeOperations(operations = {GooruOperationConstants.OPERATION_SUBDOMAIN_DELETE})
		@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
		public void deleteSubDomain(HttpServletResponse response, HttpServletRequest request, @PathVariable(ID) String subDomaintId)throws Exception{
			this.getSubDomainService().deleteSubDomain(subDomaintId);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
		
		private SubDomain buildSubDomainFromInputParameters(String data) {
			return JsonDeserializer.deserialize(data, SubDomain.class);
		}

		public SubDomainService getSubDomainService() {
	        return subDomainService;
        }
}
