package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Province;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepositoryHibernate extends BaseRepositoryHibernate implements CountryRepository {

	@Override
	public Country getCountry(String countryId) {
		Query query = getSession().createQuery("FROM Country c  WHERE c.countryId=:countryId").setParameter("countryId", countryId);
		return (Country) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Country> getCountries(Integer limit, Integer offset) {
		Query query = getSession().createQuery("FROM Country");
		query.setMaxResults(limit);
		query.setFirstResult(offset);
		return query.list();
	}

	@Override
	public Long getCountryCount() {
		Query query = getSession().createQuery("SELECT COUNT(*) FROM Country");
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}

	@Override
	public Province getState(String countryId, String stateId) {
		String hql = "FROM Province c  WHERE c.stateId=:stateId ";
		if (countryId != null) {
			hql += " and c.country.countryId=:countryId";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("stateId", stateId);
		if (countryId != null) {
			query.setParameter("countryId", countryId);
		}
		return (Province) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<Province> getStates(String countryId, Integer limit, Integer offset) {
		String hql = "FROM Province c ";
		if(countryId != null) {
			hql += " where c.country.countryId=:countryId";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("countryId", countryId);
		query.setMaxResults(limit);
		query.setFirstResult(offset);
		return query.list();
	}

	@Override
	public Long getStateCount() {
		Query query = getSession().createQuery("SELECT COUNT(*) FROM Province");
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}

}
