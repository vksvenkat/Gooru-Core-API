package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.domain.service.search.SearchResults;

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


}
