package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface CountryRepository extends BaseRepository {

	Country getCountry(String countryId);

	List<Country> getCountries(Integer limit, Integer offset);

	Long getCountryCount();
	
	Province getState(String countryId,String stateId);

	List<Province> getStates(String countryId ,Integer limit, Integer offset);

	Long getStateCount();


}
