package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.core.api.model.City;

public interface CountryService {
	
	ActionResponseDTO<Country> createCountry(Country country);

    Country updateCountry(String countryUid, Country newCountry);

	Country getCountry(String countryUid);

	SearchResults<Country> getCountries(Integer limit, Integer offset);	
	
    void deleteCountry(String countryUid);
    
    ActionResponseDTO<Province> createState(Province province , String countryUid);

    Province updateState(String stateId,String countryUid, Province newProvince);

	Province getState(String countryUid ,String stateId);

	SearchResults<Province> getStates(String countryUid,Integer limit, Integer offset);	
	
    void deleteState(String countryUid, String stateId);

	ActionResponseDTO<City> createCity(City city, String countryUid, String stateId);

    City updateCity(String countryUid, String stateId, String cityId, City newcity);

	City getCity(String countryUid ,String stateId ,String cityId);

	SearchResults<City> getCities(String countryUid, String stateId, Integer limit, Integer offset);	
	
    void deleteCity(String countryUid, String stateId ,String cityId);

}
