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

	Province getState(String countryUid, String stateId);

	List<Province> getStates(String countryUid, Integer limit, Integer offset);

	Long getStateCount(String countryUid);

	City getCity(String countryUid, String stateId, String cityId);

	List<City> getCities(String countryUid, String stateId, Integer limit, Integer offset);

	Long getCityCount(String countryUid, String stateId);

}
