
package org.ednovo.gooru.domain.service;

import java.util.List;

import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface DomainRepository extends BaseRepository{
	
	Domain getDomain(Short domainId);
	
	List<Domain> getDomains(Integer limit, Integer offset);

	Long getDomainCount();


}
