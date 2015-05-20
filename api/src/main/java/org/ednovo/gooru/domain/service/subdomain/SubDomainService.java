package org.ednovo.gooru.domain.service.subdomain;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SubDomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface SubDomainService {

	ActionResponseDTO<SubDomain> createSubDomain(SubDomain subDomain, User user);
	
	SubDomain getSubDomain(String subjectId);
	
	SearchResults<SubDomain> getSubDomain(Integer limit, Integer offset);

	void deleteSubDomain(String subDomainId);

	SubDomain updateSubDomain(SubDomain subdomain, User user, String subdomainId);
	
}
