/////////////////////////////////////////////////////////////
// SubdomainServiceImpl.java
// rest-v2-app
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.subdomain;

import java.util.Date;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubdomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SubdomainServiceImpl extends BaseServiceImpl implements SubdomainService, ParameterProperties {

	@Autowired
	private SubdomainRepository subdomainRepository;

	@Override
	public ActionResponseDTO<Subdomain> createSubdomain(Subdomain subdomain, User user) {
		final Errors errors = validateNullFields(subdomain);
		if (!errors.hasErrors()) {
			subdomain.setCreatedOn(new Date(System.currentTimeMillis()));
			this.getSubdomainRepository().save(subdomain);
		}
		return new ActionResponseDTO<Subdomain>(subdomain, errors);
	}

	@Override
	public Subdomain getSubdomain(Integer subdomainId) {
		Subdomain subdomain = subdomainRepository.getSubdomain(subdomainId);
		rejectIfNull(subdomain, GL0056, 404, generateErrorMessage(GL0056, SUBDOMAIN));
		return subdomain;
	}

	@Override
	public SearchResults<Subdomain> getSubdomain(Integer limit, Integer offset) {
		SearchResults<Subdomain> result = new SearchResults<Subdomain>();
		rejectIfNull(result, GL0056, 404, SUBDOMAIN);
		result.setSearchResults(this.getSubdomainRepository().getSubdomains(limit, offset));
		result.setTotalHitCount(this.getSubdomainRepository().getSubdomainCount());
		return result;
	}

	@Override
	public void deleteSubdomain(Integer subdomainId) {
		Subdomain subdomain = subdomainRepository.getSubdomain(subdomainId);
		rejectIfNull(subdomain, GL0056, 404, generateErrorMessage(GL0056, SUBDOMAIN));
		this.subdomainRepository.remove(subdomain);
	}

	@Override
	public Subdomain updateSubdomain(Subdomain subdomain, User user, Integer subdomainId) {
		Subdomain oldSubdomain = subdomainRepository.getSubdomain(subdomainId);
		rejectIfNull(oldSubdomain, GL0056, 404, SUBDOMAIN);
		if (subdomain.getCourseId() != null) {
			oldSubdomain.setCourseId(subdomain.getCourseId());
		}
		if (subdomain.getDomainId() != null) {
			oldSubdomain.setDomainId(subdomain.getDomainId());
		}
		subdomainRepository.save(oldSubdomain);
		return oldSubdomain;
	}

	private Errors validateNullFields(Subdomain subdomain) {
		final Errors errors = new BindException(subdomain, SUBDOMAIN);
		rejectIfNull(errors, subdomain.getCourseId(), COURSE_ID, generateErrorMessage(GL0006, COURSE_ID));
		rejectIfNull(errors, subdomain.getDomainId(), DOMAIN_ID, generateErrorMessage(GL0006, DOMAIN_ID));
		return errors;
	}

	public SubdomainRepository getSubdomainRepository() {
		return subdomainRepository;
	}

}
