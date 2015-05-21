package org.ednovo.gooru.domain.service.subdomain;

import java.util.Date;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubdomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SubdomainServiceImpl extends BaseServiceImpl implements SubdomainService,ParameterProperties{

	@Autowired
	private SubdomainRepository subDomainRepository;

	@Override
    public ActionResponseDTO<Subdomain> createSubdomain(Subdomain subDomain, User user) {
		final Errors errors = validateSubdomain(subDomain);
		if (!errors.hasErrors()) {
			subDomain.setCreatedOn(new Date(System.currentTimeMillis()));
			this.getSubdomainRepository().save(subDomain);
		}
		return new ActionResponseDTO<Subdomain>(subDomain, errors);
    }

	@Override
    public Subdomain getSubdomain(String subDomainId) {
		Subdomain subDomain = (Subdomain) subDomainRepository.getSubdomain(subDomainId);
		if(subDomain == null){
			throw new NotFoundException(generateErrorMessage(GL0056, SUBDOMAIN), GL0056);
		}
		return subDomain;
    }

	@Override
    public SearchResults<Subdomain> getSubdomain(Integer limit, Integer offset) {
		SearchResults<Subdomain> result = new SearchResults<Subdomain>();
		Long count = this.getSubdomainRepository().getSubdomainCount();
		System.out.println(count);
		result.setSearchResults(this.getSubdomainRepository().getSubdomains(limit, offset));
		result.setTotalHitCount(count);
		return result;
    }

	@Override
    public void deleteSubdomain(String subDomainId) {
		Subdomain subDomain = subDomainRepository.getSubdomain(subDomainId);
		rejectIfNull(subDomain, GL0056, 404, generateErrorMessage(GL0056, SUBDOMAIN));
		this.subDomainRepository.remove(subDomain);	    
    }

	@Override
    public Subdomain updateSubdomain(Subdomain subdomain, User user, String subDomainId) {
		Subdomain oldSubdomain = subDomainRepository.getSubdomain(subDomainId);
	    rejectIfNull(oldSubdomain, GL0056, 404, SUBDOMAIN);
	    	if(subdomain.getCourseId()!=null)
	    		oldSubdomain.setCourseId(subdomain.getCourseId());
		    if(subdomain.getDomainId() !=null)
		    	oldSubdomain.setDomainId(subdomain.getDomainId());
		    subDomainRepository.save(oldSubdomain);	
	    	return oldSubdomain;
    }

	
	private Errors validateSubdomain(Subdomain subDomain) {
		final Errors errors = new BindException(subDomain, SUBDOMAIN);
		return errors;
	}
	
	public SubdomainRepository getSubdomainRepository() {
		return subDomainRepository;
	}

	
}
