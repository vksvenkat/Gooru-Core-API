/////////////////////////////////////////////////////////////
// SubdomainServiceImpl.java
// gooru-api
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
import java.util.List;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.DomainRepository;
import org.ednovo.gooru.domain.service.TaxonomyCourseRepository;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubdomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class SubdomainServiceImpl extends BaseServiceImpl implements SubdomainService, ParameterProperties, ConstantProperties {

	@Autowired
	private SubdomainRepository subdomainRepository;

	@Autowired
	private TaxonomyCourseRepository TaxonomycourseRepository;
	
	@Autowired
	private DomainRepository domainRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Subdomain> createSubdomain(Subdomain subdomain, User user) {
		final Errors errors = validateSubdomain(subdomain);
		TaxonomyCourse Taxonomycourse = this.getTaxonomyCourseRepository().getCourse(subdomain.getCourseId());
         rejectIfNull(Taxonomycourse, GL0006, 404, COURSE);
         Domain domain = this.getDomainRepository().getDomain(subdomain.getDomainId());
         rejectIfNull(domain, GL0006, 404, DOMAIN_);
		if (!errors.hasErrors()) {
			subdomain.setCreatedOn(new Date(System.currentTimeMillis()));
			this.getSubdomainRepository().save(subdomain);
		}
		return new ActionResponseDTO<Subdomain>(subdomain, errors);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Subdomain getSubdomain(Integer subdomainId) {
		Subdomain subdomain = this.getSubdomainRepository().getSubdomain(subdomainId);
		rejectIfNull(subdomain, GL0056, 404, SUBDOMAIN);
		return subdomain;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Subdomain> getSubdomains(Integer limit, Integer offset) {
		List<Subdomain> result = this.getSubdomainRepository().getSubdomains(limit, offset);
		rejectIfNull(result, GL0056, 404, SUBDOMAIN);
		return result;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteSubdomain(Integer subdomainId) {
		Subdomain subdomain = this.getSubdomainRepository().getSubdomain(subdomainId);
		rejectIfNull(subdomain, GL0056, 404,SUBDOMAIN);
		this.getSubdomainRepository().remove(subdomain);
	}

	private Errors validateSubdomain(Subdomain subdomain) {
		final Errors errors = new BindException(subdomain, SUBDOMAIN);
		rejectIfNull(errors, subdomain.getCourseId(), COURSE_ID, generateErrorMessage(GL0006, COURSE_ID));
		rejectIfNull(errors, subdomain.getDomainId(), DOMAIN_ID, generateErrorMessage(GL0006, DOMAIN_ID));
		return errors;
	}

	public SubdomainRepository getSubdomainRepository() {
		return subdomainRepository;
	}
	
	public TaxonomyCourseRepository getTaxonomyCourseRepository() {
		return TaxonomycourseRepository;
	}
	
	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

}
