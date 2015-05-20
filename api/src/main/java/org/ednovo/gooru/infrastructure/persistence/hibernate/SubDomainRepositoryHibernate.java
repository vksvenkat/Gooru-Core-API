package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.SubDomain;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SubDomainRepositoryHibernate extends BaseRepositoryHibernate implements SubDomainRepository, ParameterProperties {

	private static final String SUBDOMAIN_COUNT = "SELECT COUNT(*) FROM SubDomain";
	private static final String SUBDOMAINS = "FROM SubDomain";	
	@Override
    public SubDomain getSubDomain(String subDomainId) {
		String hql = "FROM SubDomain subdomain WHERE subdomain.subDomainId = '" + subDomainId + "'";
		return get(hql);
    }

	@Override
    public List<SubDomain> getSubDomains(Integer limit, Integer offset) {
		Query query = getSession().createQuery(SUBDOMAINS);
        query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
        query.setFirstResult(offset);
	    return list(query);
    }
	
	@Override
	public Long getSubDomainCount() {
		Query query = getSession().createQuery(SUBDOMAIN_COUNT);
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}
	
}
