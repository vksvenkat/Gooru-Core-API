package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class CountryServiceImpl extends BaseServiceImpl implements CountryService, ParameterProperties, ConstantProperties {

	@Autowired
	private CountryRepository countryRepository;

	@Override
	public ActionResponseDTO<Country> createCountry(Country country) {
		final Errors errors = validateCountry(country);
		if (!errors.hasErrors()) {
			this.getCountryRepository().save(country);
		}
		return new ActionResponseDTO<Country>(country, errors);
	}

	@Override
	public Country updateCountry(String countryId, Country newCountry) {
		Country country = this.getCountryRepository().getCountry(countryId);
		rejectIfNull(country, GL0056, 404, "Country");
		if (newCountry.getName() != null) {
			country.setName(newCountry.getName());
		}
		this.getCountryRepository().save(country);
		return country;
	}

	@Override
	public Country getCountry(String countryId) {
		Country country = this.getCountryRepository().getCountry(countryId);
		rejectIfNull(country, GL0056, 404, "Country");
		return country;
	}

	@Override
	public SearchResults<Country> getCountries(Integer limit, Integer offset) {
		SearchResults<Country> result = new SearchResults<Country>();
		result.setSearchResults(this.getCountryRepository().getCountries(limit, offset));
		result.setTotalHitCount(this.getCountryRepository().getCountryCount());
		return result;
	}

	
	private Errors validateCountry(Country country) {
		final Errors errors = new BindException(country, "country");
		rejectIfNullOrEmpty(errors, country.getName(), NAME, GL0006, generateErrorMessage(GL0006, "Country name"));
		rejectIfNullOrEmpty(errors, country.getCountryId(), "countryId", GL0006, generateErrorMessage(GL0006, "Country"));
		return errors;
	}
		
	@Override
	public void deleteCountry(String countryId) {
		Country country = this.getCountryRepository().getCountry(countryId);
		rejectIfNull(country, GL0056, 404, "Country");
		this.getCountryRepository().remove(country);
		
	}

	public CountryRepository getCountryRepository() {
		return countryRepository;
	}

	
	@Override
	public ActionResponseDTO<Province> createState(Province province, String countryId) {
		final Errors errors = validateProvince(province);
		if (!errors.hasErrors()) {
			Country country = this.getCountryRepository().getCountry(countryId);
			rejectIfNull(country, GL0056, 404, "Country");
			province.setCountry(country);
			this.getCountryRepository().save(province);
		}

		return new ActionResponseDTO<Province>(province, errors);
	}

    @Override
	public Province updateState(String countryId, String stateId, Province newState) {
    	Country country = this.getCountryRepository().getCountry(countryId);
    	rejectIfNull(country, GL0056, 404, "Country");
		Province province = this.getCountryRepository().getState(countryId,stateId);
		rejectIfNull(province, GL0056, 404, "Province");
		if (newState.getName() != null) {
			province.setName(newState.getName());
		}
		this.getCountryRepository().save(province);
		return province;
	}

	@Override
	public Province getState(String countryId, String stateId) {
		Province province = this.getCountryRepository().getState(countryId,stateId);
		rejectIfNull(province, GL0056, 404, "Province");
		return province;
	}

	@Override
	public SearchResults<Province> getStates(String countryId,Integer limit, Integer offset) {
		SearchResults<Province> result = new SearchResults<Province>();
		result.setSearchResults(this.getCountryRepository().getStates(countryId,limit, offset));
		result.setTotalHitCount(this.getCountryRepository().getStateCount());
		return result;
	}

	@Override
	public void deleteState(String countryId, String stateId) {
		Province province = this.getCountryRepository().getState(countryId,stateId);
		rejectIfNull(province, GL0056, 404, "Province");
		this.getCountryRepository().remove(province);
		
	}
	private Errors validateProvince(Province province) {
		final Errors errors = new BindException(province, "Province");
		rejectIfNullOrEmpty(errors, province.getName(), NAME, GL0006, generateErrorMessage(GL0006, "State name"));
		rejectIfNullOrEmpty(errors, province.getStateId(), "stateId", GL0006, generateErrorMessage(GL0006, "State"));
		return errors;
	}

	

	

	
	
}
