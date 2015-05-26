package org.ednovo.gooru.domain.service.subdomain;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface SubdomainService {

	ActionResponseDTO<Subdomain> createSubdomain(Subdomain subDomain, User user);
	
	Subdomain getSubdomain(Integer subdomainId);
	
	SearchResults<Subdomain> getSubdomain(Integer limit, Integer offset);

	void deleteSubdomain(Integer subdomainId);

	Subdomain updateSubdomain(Subdomain subdomain, User user, Integer subdomainId);
	
}
