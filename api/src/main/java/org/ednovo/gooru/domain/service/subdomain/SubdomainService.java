package org.ednovo.gooru.domain.service.subdomain;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface SubdomainService {

	ActionResponseDTO<Subdomain> createSubdomain(Subdomain subDomain, User user);
	
	Subdomain getSubdomain(String subjectId);
	
	SearchResults<Subdomain> getSubdomain(Integer limit, Integer offset);

	void deleteSubdomain(String subdomainId);

	Subdomain updateSubdomain(Subdomain subdomain, User user, String subdomainId);
	
}
