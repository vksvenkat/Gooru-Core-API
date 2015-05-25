package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface DomainService {
	
	ActionResponseDTO<Domain> createDomain(Domain domain,User user);
	
	Domain updateDomain(Short domainId, Domain domain);

	Domain getDomain(Short domainId);

	SearchResults<Domain> getDomains(Integer limit, Integer offset);	
	
    void deleteDomain(Short domainId);

}
