/////////////////////////////////////////////////////////////
// DomainServiceImpl.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service;

import java.util.Date;
import java.util.List;

import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.Subject;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class DomainServiceImpl extends BaseServiceImpl implements DomainService, ParameterProperties, ConstantProperties {

	@Autowired
	private DomainRepository domainRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Domain> createDomain(Domain domain, User user) {
		final Errors error = validateDomain(domain);
		if (!error.hasErrors()) {
			domain.setCreatedOn(new Date(System.currentTimeMillis()));
			domain.setLastModified(new Date(System.currentTimeMillis()));
			domain.setActiveFlag((short) 1);
			domain.setDisplaySequence(this.getDomainRepository().getMaxSequence() + 1);
			this.getDomainRepository().save(domain);
			domain.setUri(RequestMappingUri.DOMAIN + domain.getDomainId());
		}
		return new ActionResponseDTO<Domain>(domain, error);
	}
		
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateDomain(Integer domainId, Domain newDomain) {
		Domain domain = this.getDomainRepository().getDomain(domainId);
		rejectIfNull(domain, GL0006, 404, DOMAIN_);
		if (newDomain.getActiveFlag() != null) {
			reject((newDomain.getActiveFlag() == 0 || newDomain.getActiveFlag() == 1), GL0007, ACTIVE_FLAG);
			domain.setActiveFlag(newDomain.getActiveFlag());
	    }
		if (newDomain.getName() != null) {
			domain.setName(domain.getName());
		}
		if (newDomain.getDescription() != null) {
			domain.setDescription(domain.getDescription());
		}
		if (newDomain.getImagePath() != null) {
			domain.setImagePath(domain.getImagePath());
		}
		if (newDomain.getDisplaySequence() != null) {
			domain.setDisplaySequence(domain.getDisplaySequence());
		}
		domain.setLastModified(new Date(System.currentTimeMillis()));
		this.getDomainRepository().save(domain);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Domain getDomain(Integer domainId) {
		Domain domain = this.getDomainRepository().getDomain(domainId);
		reject((domain.getActiveFlag() == 1), GL0107, DOMAIN);
		rejectIfNull(domain, GL0056, 404, DOMAIN_);
		if(domain.getImagePath() != null){
			domain.setThumbnail(GooruImageUtil.getThumbnail(domain.getImagePath()));
		}
		return domain;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Domain> getDomains(Integer limit, Integer offset) {
		List<Domain> domains = this.getDomainRepository().getDomains(limit, offset);
		for(Domain domain: domains){
			if(domain.getImagePath() != null){
				domain.setThumbnail(GooruImageUtil.getThumbnail(domain.getImagePath()));
			}
		}
		return domains;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteDomain(Integer domainId) {
		Domain domain = this.getDomainRepository().getDomain(domainId);
		rejectIfNull(domain, GL0056, 404, DOMAIN_);
		domain.setActiveFlag((short) 0);
		domain.setLastModified(new Date(System.currentTimeMillis()));
		this.getDomainRepository().save(domain);
	}

	private Errors validateDomain(Domain domain) {
		final Errors error = new BindException(domain, DOMAIN_);
		rejectIfNull(domain.getName(), GL0006, NAME);
		return error;
	}

	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

}
