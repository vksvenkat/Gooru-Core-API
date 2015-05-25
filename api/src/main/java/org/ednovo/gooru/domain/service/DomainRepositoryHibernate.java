
package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class DomainRepositoryHibernate extends BaseRepositoryHibernate implements DomainRepository, ParameterProperties, ConstantProperties {

	@Override
	public Domain getDomain(Short domainId) {
		Query query = getSession().createQuery("FROM Domain d  WHERE d.domainId=:domainId").setParameter("domainId", domainId);
		return (Domain) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public List<Domain> getDomains(Integer limit, Integer offset) {
		Query query = getSession().createQuery("FROM Domain");
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return list(query);
	}

	@Override
	public Long getDomainCount() {

		Query query = getSession().createQuery("SELECT COUNT(*) FROM Course");
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);		
	}
	
}
