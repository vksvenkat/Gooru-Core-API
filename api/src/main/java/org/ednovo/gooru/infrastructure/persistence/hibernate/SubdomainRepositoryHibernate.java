package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SubdomainRepositoryHibernate extends BaseRepositoryHibernate implements SubdomainRepository, ParameterProperties {

	private static final String SUBDOMAIN_COUNT = "SELECT COUNT(*) FROM Subdomain";
	private static final String SUBDOMAINS = "FROM Subdomain";	
	@Override
    public Subdomain getSubdomain(String subdomainId) {
		String hql = "FROM Subdomain subdomain WHERE subdomain.subdomainId = '" + subdomainId + "'";
		return get(hql);
    }

	@Override
    public List<Subdomain> getSubdomains(Integer limit, Integer offset) {
		Query query = getSession().createQuery(SUBDOMAINS);
        query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
        query.setFirstResult(offset);
	    return list(query);
    }
	
	@Override
	public Long getSubdomainCount() {
		Query query = getSession().createQuery(SUBDOMAIN_COUNT);
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}
	
}
