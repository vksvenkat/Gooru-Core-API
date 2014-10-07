package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.core.api.model.City;

public interface CountryService {
	
	ActionResponseDTO<Country> createCountry(Country country);

    Country updateCountry(String countryId, Country newCountry);

	Country getCountry(String countryId);

	SearchResults<Country> getCountries(Integer limit, Integer offset);	
	
    void deleteCountry(String countryId);
    
    ActionResponseDTO<Province> createState(Province province , String countryId);

    Province updateState(String stateId,String countryId, Province newProvince);

	Province getState(String countryId ,String stateId);

	SearchResults<Province> getStates(String countryId,Integer limit, Integer offset);	
	
    void deleteState(String countryId, String stateId);

	ActionResponseDTO<City> createCity(City city,String stateId, String countryId);

    City updateCity(String stateId, String countryId, String cityId, City newcity);

	City getCity(String countryId ,String stateId ,String cityId);

	SearchResults<City> getCities(String countryId, String stateId, Integer limit, Integer offset);	
	
    void deleteCity(String countryId, String stateId ,String cityId);

}
