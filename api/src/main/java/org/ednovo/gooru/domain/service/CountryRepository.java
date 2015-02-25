package org.ednovo.gooru.domain.service;

import java.util.List;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.core.api.model.City;

public interface CountryRepository extends BaseRepository {

	Country getCountry(String countryUid);

	List<Country> getCountries(Integer limit, Integer offset);

	Long getCountryCount();

	Province getState(String countryUid, String stateUid);

	List<Province> getStates(String countryUid, Integer limit, Integer offset);

	Long getStateCount(String countryUid);

	City getCity(String countryUid, String stateUid, String cityUid);

	List<City> getCities(String countryUid, String stateUid, Integer limit, Integer offset);

	Long getCityCount(String countryUid, String stateUid);

}
