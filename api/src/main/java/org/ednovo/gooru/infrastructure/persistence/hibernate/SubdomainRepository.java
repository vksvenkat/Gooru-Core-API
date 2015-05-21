package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Subdomain;

public interface SubdomainRepository extends BaseRepository{

	List<Subdomain> getSubdomains(Integer limit, Integer offset);

	Long getSubdomainCount();

	Subdomain getSubdomain(String subdomainId);

}
