package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.SubDomain;

public interface SubDomainRepository extends BaseRepository{

	SubDomain getSubDomain(String subDomainId);

	List<SubDomain> getSubDomains(Integer limit, Integer offset);

	Long getSubDomainCount();

}
