package org.ednovo.gooru.domain.service;

import java.util.Date;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Course;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class DomainServiceImpl extends BaseServiceImpl implements DomainService, ParameterProperties, ConstantProperties {

		@Autowired
		private DomainRepository domainRepository;

		@Override
		public ActionResponseDTO<Domain> createDomain(Domain domain,User user) {
			
			final Errors error = validateDomain(domain);
			if (!error.hasErrors()) {

			domain.setOrganization(domain.getOrganization());
			domain.setCreatedOn(new Date(System.currentTimeMillis()));
			domain.setLastModified(new Date(System.currentTimeMillis()));
			domainRepository.save(domain);
			}
			return new ActionResponseDTO<Domain>(domain,error);
		}
		
		@Override
		public Domain updateDomain(Short domainId,Domain domain) {
			Domain newDomain = this.getDomainRepository().getDomain(domainId);
			if (domain.getName() != null) {
				newDomain.setName(domain.getName());
			}
			if (domain.getDescription() != null) {
				newDomain.setDescription(domain.getDescription());
			}
			if (domain.getImagePath() != null) {
				newDomain.setImagePath(domain.getImagePath());
			}
			if (domain.getDisplaySequence() != null) {
				newDomain.setDisplaySequence(domain.getDisplaySequence());
			}
			if (domain.getActiveFlag()!= null) {
				newDomain.setActiveFlag(domain.getActiveFlag());
			}
	        newDomain.setLastModified(new Date(System.currentTimeMillis()));
			this.getDomainRepository().save(newDomain);
			return domain;
		}

		@Override
		public Domain getDomain(Short domainId) {
			Domain domain = this.getDomainRepository().getDomain(domainId);
			 if(domain.getActiveFlag() == 0){
					throw new BadRequestException(generateErrorMessage(GL0107, DEACTIVATE_DOMAIN), GL0107);
			 }
			rejectIfNull(domain, GL0056, 404, DOMAIN_);
			return domain;
		}
		@Override
		public SearchResults<Domain> getDomains(Integer limit, Integer offset) {
			SearchResults<Domain> result = new SearchResults<Domain>();
			result.setSearchResults(this.getDomainRepository().getDomains(limit, offset));
			result.setTotalHitCount(this.getDomainRepository().getDomainCount());
			return result;
		}
		
		@Override
		public void deleteDomain(Short domainId) {
			Domain Domain = this.getDomainRepository().getDomain(domainId);
			rejectIfNull(Domain, GL0056, 404, DOMAIN_);
			this.getDomainRepository().remove(Domain);
		}
		private Errors validateDomain(Domain domain) {
			final Errors error = new BindException(domain, DOMAIN_);
			rejectIfNull(domain.getName(),GL0006, NAME);
			rejectIfNull(domain.getActiveFlag(),GL0006,ACTIVE_FLAG);
			rejectIfNull(domain.getDisplaySequence(),GL0006,DISPLAY_SEQUENCE);
			return error;
		}
	
		public DomainRepository getDomainRepository () {
			return domainRepository;
		}


   }
