package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.City;
import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository
public class CountryRepositoryHibernate extends BaseRepositoryHibernate implements CountryRepository, ConstantProperties, ParameterProperties {
	
	private static final String GET_COUNTRY = "FROM Country c  WHERE c.countryUid=:countryUid"; 
	 
	private static final String COUNTRYS = "FROM Country";
	
	private static final String COUNTRY_COUNT = "SELECT COUNT(*) FROM Country";

    private static final  String STATE = "FROM Province c  WHERE c.stateUid=:stateUid";

    private static final  String GET_STATE = "FROM Province c  WHERE c.stateUid=:stateUid and c.country.countryUid=:countryUid";
    
    private static final String STATES = "FROM Province c where c.country.countryUid=:countryUid";
    
    private static final String STATE_COUNT = "SELECT COUNT(*) FROM Province p where p.country.countryUid=:countryUid";
    
    private static final String GET_CITY = "FROM City c  WHERE c.cityUid=:cityUid and c.country.countryUid=:countryUid and c.province.stateUid=:stateUid";

    private static final String CITYS = "FROM City c  where c.country.countryUid=:countryUid and c.province.stateUid=:stateUid";
    
    private static final String CITY_COUNT = "SELECT COUNT(*) FROM City c where c.country.countryUid=:countryUid and c.province.stateUid=:stateUid";


    
	@Override
	public Country getCountry(String countryUid) {
		Query query = getSession().createQuery(GET_COUNTRY).setParameter(COUNTRY_UID, countryUid);
		return (Country) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Country> getCountries(Integer limit, Integer offset) {
		Query query = getSession().createQuery(COUNTRYS);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return query.list();
	}

	@Override
	public Long getCountryCount() {
		Query query = getSession().createQuery(COUNTRY_COUNT);
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}

	@Override
	public Province getState(String countryUid, String stateUid) {
		Query query = getSession().createQuery(GET_STATE);
		query.setParameter(STATE_UID, stateUid);
		query.setParameter(COUNTRY_UID, countryUid);
		return (Province) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public Province getState(String stateUid) {
		Query query = getSession().createQuery(STATE);
		query.setParameter(STATE_UID, stateUid);
		return (Province) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Province> getStates(String countryUid, Integer limit, Integer offset) {
		Query query = getSession().createQuery(STATES);
		if (countryUid != null) {
			query.setParameter(COUNTRY_UID, countryUid);
		}
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return query.list();
	}

	@Override
	public Long getStateCount(String countryUid) {
		Query query = getSession().createQuery(STATE_COUNT);
		query.setParameter(COUNTRY_UID, countryUid);
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}

	@Override
	public City getCity(String countryUid, String stateUid, String cityUid) {
		Query query = getSession().createQuery(GET_CITY);
		query.setParameter(CITY_UID, cityUid);
		if (countryUid != null && stateUid != null) {
			query.setParameter(STATE_UID, stateUid);
			query.setParameter(COUNTRY_UID, countryUid);
		}
		return (City) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<City> getCities(String countryUid, String stateUid, Integer limit, Integer offset) {
		Query query = getSession().createQuery(CITYS);
		query.setParameter(COUNTRY_UID, countryUid);
		query.setParameter(STATE_UID, stateUid);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return query.list();
	}

	@Override
	public Long getCityCount(String countryUid, String stateUid) {
		Query query = getSession().createQuery(CITY_COUNT);
		query.setParameter(COUNTRY_UID, countryUid);
		query.setParameter(STATE_UID, stateUid);
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}
}
