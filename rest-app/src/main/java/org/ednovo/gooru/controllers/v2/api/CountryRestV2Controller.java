package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.core.api.model.City;
import org.ednovo.gooru.core.application.util.CustomProperties;
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

import scala.collection.CustomParallelizable;

@Controller
@RequestMapping(value = { "/v2/country" })
public class CountryRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties{

	@Autowired
	public CountryService countryService;

	@Autowired
	public OrganizationService organizationService;
	
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_ADD })
	@RequestMapping(value = { " " }, method = RequestMethod.POST)
	public ModelAndView createCountry(@RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
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
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateCountry(@PathVariable(value = ID) String countryUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().updateCountry(countryUid, buildCountryFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_READ })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getCountry(@PathVariable(value = ID) String countryUid, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getCountry(countryUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_READ })
	@RequestMapping(value = { " " }, method = RequestMethod.GET)
	public ModelAndView getCountries(@RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getCountries(limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, COUNTRY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COUNTRY_DELETE })
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.DELETE)
	public void deleteCountry(@PathVariable(value = ID) String countryUid, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteCountry(countryUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_ADD })
	@RequestMapping(value = { "/{id}/state " }, method = RequestMethod.POST)
	public ModelAndView createState(@PathVariable(value = ID) String countryUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
		ActionResponseDTO<Province> responseDTO = getCountryService().createState(buildProvinceFromInputParameters(data), countryUid);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(STATE, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_UPDATE })
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.PUT)
	public ModelAndView updateState(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().updateState(countryUid, stateUid, buildProvinceFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_READ })
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.GET)
	public ModelAndView getState(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getState(countryUid, stateUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_READ })
	@RequestMapping(value = { "{id}/state" }, method = RequestMethod.GET)
	public ModelAndView getStates(@PathVariable(value = ID) String countryUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request,
			HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getStates(countryUid, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, STATE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_STATE_DELETE })
	@RequestMapping(value = { "/{id}/state/{sid}" }, method = RequestMethod.DELETE)
	public void deleteState(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteState(countryUid, stateUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_ADD })
	@RequestMapping(value = { "/{id}/state/{sid}/city" }, method = RequestMethod.POST)
	public ModelAndView createCity(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
		ActionResponseDTO<City> responseDTO = getCountryService().createCity(buildCityFromInputParameters(data), countryUid, stateUid);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(CITY, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_UPDATE })
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.PUT)
	public ModelAndView updateCity(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @PathVariable(value = CID) String cityUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().updateCity(countryUid, stateUid, cityUid, buildCityFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_READ })
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.GET)
	public ModelAndView getCity(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @PathVariable(value = CID) String cityUid, HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getCity(countryUid, stateUid, cityUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_READ })
	@RequestMapping(value = { "{id}/state/{sid}/city" }, method = RequestMethod.GET)
	public ModelAndView getCities(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit,
			HttpServletRequest request, HttpServletResponse response)  {
		return toModelAndViewWithIoFilter(getCountryService().getCities(countryUid, stateUid, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, CITY);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CITY_DELETE })
	@RequestMapping(value = { "/{id}/state/{sid}/city/{cid}" }, method = RequestMethod.DELETE)
	public void deleteCity(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @PathVariable(value = CID) String cityUid, HttpServletRequest request, HttpServletResponse response) {
		getCountryService().deleteCity(countryUid, stateUid, cityUid);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCHOOL_DISTRICT_READ })
	@RequestMapping(value = { "/{id}/state/{sid}/school-district" }, method = RequestMethod.GET)
	public ModelAndView getStateSchoolDistricts(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getOrganizationService().getOrganizations(CustomProperties.InstitutionType.SCHOOL_DISTRICT.getInstitutionType(), null, stateUid, offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, INSTITUTION_INCLUDES_ADD);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCHOOL_READ })
	@RequestMapping(value = { "/{id}/state/{sid}/school-district/{schoolDistrictId}/school" }, method = RequestMethod.GET)
	public ModelAndView getStateSchoolDistrictSchools(@PathVariable(value = ID) String countryUid, @PathVariable(value = SID) String stateUid, @PathVariable(value = "schoolDistrictId") String schoolDistrictId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") Integer offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") Integer limit, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getOrganizationService().getOrganizations(CustomProperties.InstitutionType.SCHOOL.getInstitutionType(), schoolDistrictId, stateUid, offset, limit), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, INSTITUTION_INCLUDES_ADD);
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
