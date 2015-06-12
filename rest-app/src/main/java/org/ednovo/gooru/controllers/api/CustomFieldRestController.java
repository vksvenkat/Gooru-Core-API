package org.ednovo.gooru.controllers.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.CustomField;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.party.OrganizationService;
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
@RequestMapping(value = { "/customField", "" })
public class CustomFieldRestController extends BaseController implements ParameterProperties {

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private OrganizationService organizationService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED, noRollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/customfield.{format}")
	public ModelAndView saveCustomFields(@PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false)String sessionToken, @RequestParam(value = CUSTOM_FIELD_ID, required = false) String customFieldId, @RequestParam(value = ACCOUNT_UID) String organizationUid,
			@RequestParam(value = FIELD_NAME) String fieldName, @RequestParam(value = FIELD_DISP_NAME) String fieldDisplayName, @RequestParam(value = TYPE) String type, @RequestParam(value = LENGTH) double length, @RequestParam(value = DATA_COLUMN_NAME) String dataColumnName,
			@RequestParam(value = ADD_TO_SEARCH, defaultValue = ZERO) boolean addTosearch, @RequestParam(value = IS_REQUIRED, defaultValue = ONE) boolean isRequired, @RequestParam(value = GROUPNAME, defaultValue = DEFAULT) String groupName,
			@RequestParam(value = SEARCH_ALIAS_NAME) String searchAliasName, @RequestParam(value = SHOW_IN_RESPONSE, defaultValue = ZERO) Integer showInResponse, @RequestParam(value = ADD_SEARCH_INDEX, defaultValue = ZERO) Integer addToSearchIndex,
			@RequestParam(value = ADD_TO_FILTERS, defaultValue = ZERO) Integer addToFilters, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setAttribute(PREDICATE, CUSTOM_FIELD_ADD);

		CustomField customField = null;
		if (customFieldId != null) {
			customField = customFieldService.findCustomFieldIfExists(customFieldId);
		} else {
			customField = new CustomField();
			customField.setOrganization(organizationService.getOrganizationById(organizationUid));
		}

		customField.setFieldName(fieldName);
		customField.setFieldDisplayName(fieldDisplayName);
		customField.setType(type);
		customField.setLength(length);
		customField.setDataColumnName(dataColumnName);
		customField.setAddTosearch(addTosearch);
		customField.setIsRequired(isRequired);
		customField.setGroupName(groupName);
		customField.setSearchAliasName(searchAliasName);
		customField.setShowInResponse(showInResponse);
		customField.setAddToSearchIndex(addToSearchIndex);
		customField.setAddToFilters(addToFilters);

		customFieldService.saveCustomField(customField);

		return toModelAndView(customField, FORMAT_JSON);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/customField/{customFieldId}/delete")
	public ModelAndView deleteCustomField(@PathVariable(CUSTOM_FIELD_ID) String customFieldId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(PREDICATE, CUSTOM_FIELD_DEL);

		if (customFieldId != null) {
			customFieldService.deleteCustomField(customFieldId);
		}

		return toModelAndView("custom field is deleted ");
	}
}
