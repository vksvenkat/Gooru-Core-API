package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.core.api.model.City;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CountryService;
import org.ednovo.gooru.domain.service.party.OrganizationService;
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
@RequestMapping(value = { "/v2/country" })
public class CountryRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	public CountryService countryService;

	@Autowired
	public OrganizationService organizationService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createCountry(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<Country> responseDTO = getCountryService().createCountry(buildCountryFromInputParameters(data));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(COUNTRY, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateCountry(@PathVariable(value = ID) String countryId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().updateCountry(countryId, buildCountryFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCountry(@PathVariable(value = ID) String countryId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getCountry(countryId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { " " }, method = RequestMethod.GET)
	public ModelAndView getCountries(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getCountries(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCountry(@PathVariable(value = ID) String countryId, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteCountry(countryId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state " }, method = RequestMethod.POST)
	public ModelAndView createState(@PathVariable(value = ID) String countryId, String stateId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<Province> responseDTO = getCountryService().createState(buildProvinceFromInputParameters(data), countryId);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(STATE, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.PUT)
	public ModelAndView updateState(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().updateState(countryId, stateId, buildProvinceFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.GET)
	public ModelAndView getState(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getState(countryId, stateId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "{id}/state" }, method = RequestMethod.GET)
	public ModelAndView getStates(@PathVariable(value = ID) String countryId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getStates(countryId, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.DELETE)
	public void deleteState(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteState(countryId, stateId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/city " }, method = RequestMethod.POST)
	public ModelAndView createCity(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, String cityId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<City> responseDTO = getCountryService().createCity(buildCityFromInputParameters(data), countryId, cityId);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(COUNTRY, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.PUT)
	public ModelAndView updateCity(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @PathVariable(value = CID) String cityId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().updateCity(countryId, stateId, cityId, buildCityFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.GET)
	public ModelAndView getCity(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @PathVariable(value = CID) String cityId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getCity(countryId, stateId, cityId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "{id}/state/{sid}/city" }, method = RequestMethod.GET)
	public ModelAndView getCities(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getCountryService().getCities(countryId, stateId, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.DELETE)
	public void deleteCity(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @PathVariable(value = CID) String cityId, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteCity(countryId, stateId, cityId);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_SCHOOL_DISTRICTS_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/school-district" }, method = RequestMethod.GET)
	public ModelAndView getStateSchoolDistricts(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndView(this.getOrganizationService().getOrganizations("school_district", null, stateId, offset, limit), RESPONSE_FORMAT_JSON);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_SCHOOL_DISTRICTS_SCHOOLS_READ})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}/state/{sid}/school-district/{schoolDistrictId}/school" }, method = RequestMethod.GET)
	public ModelAndView getStateSchoolDistrictSchools(@PathVariable(value = ID) String countryId, @PathVariable(value = SID) String stateId, @PathVariable(value = "schoolDistrictId") String schoolDistrictId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndView(this.getOrganizationService().getOrganizations("school", schoolDistrictId, stateId, offset, limit), RESPONSE_FORMAT_JSON);
	}

	private Country buildCountryFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Country.class);
	}

	private Province buildProvinceFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Province.class);
	}

	private City buildCityFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, City.class);
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

}
