package org.ednovo.gooru.domain.service.subdomain;

import java.util.Date;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SubDomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SubDomainServiceImpl extends BaseServiceImpl implements SubDomainService,ParameterProperties{

	@Autowired
	private SubDomainRepository subDomainRepository;

	@Override
    public ActionResponseDTO<SubDomain> createSubDomain(SubDomain subDomain, User user) {
		final Errors errors = validateSubDomain(subDomain);
		if (!errors.hasErrors()) {
			subDomain.setCreatedOn(new Date(System.currentTimeMillis()));
			this.getSubDomainRepository().save(subDomain);
		}
		return new ActionResponseDTO<SubDomain>(subDomain, errors);
    }

	@Override
    public SubDomain getSubDomain(String subDomainId) {
		SubDomain subDomain = (SubDomain) subDomainRepository.getSubDomain(subDomainId);
		if(subDomain == null){
			throw new NotFoundException(generateErrorMessage(GL0056, SUBDOMAIN), GL0056);
		}
		return subDomain;
    }

	@Override
    public SearchResults<SubDomain> getSubDomain(Integer limit, Integer offset) {
		SearchResults<SubDomain> result = new SearchResults<SubDomain>();
		Long count = this.getSubDomainRepository().getSubDomainCount();
		System.out.println(count);
		result.setSearchResults(this.getSubDomainRepository().getSubDomains(limit, offset));
		result.setTotalHitCount(count);
		return result;
    }

	@Override
    public void deleteSubDomain(String subDomainId) {
		SubDomain subDomain = subDomainRepository.getSubDomain(subDomainId);
		rejectIfNull(subDomain, GL0056, 404, generateErrorMessage(GL0056, SUBDOMAIN));
		this.subDomainRepository.remove(subDomain);	    
    }

	@Override
    public SubDomain updateSubDomain(SubDomain subdomain, User user, String subDomainId) {
		SubDomain oldSubDomain = subDomainRepository.getSubDomain(subDomainId);
	    rejectIfNull(oldSubDomain, GL0056, 404, SUBDOMAIN);
	    	if(subdomain.getCourseId()!=null)
	    		oldSubDomain.setCourseId(subdomain.getCourseId());
		    if(subdomain.getDomainId() !=null)
		    	oldSubDomain.setDomainId(subdomain.getDomainId());
		    subDomainRepository.save(oldSubDomain);	
	    	return oldSubDomain;
    }

	
	private Errors validateSubDomain(SubDomain subDomain) {
		final Errors errors = new BindException(subDomain, SUBDOMAIN);
		return errors;
	}
	
	public SubDomainRepository getSubDomainRepository() {
		return subDomainRepository;
	}

	
}
