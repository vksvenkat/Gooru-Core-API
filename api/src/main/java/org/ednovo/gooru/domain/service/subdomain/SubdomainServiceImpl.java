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
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Domain;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.TaxonomyCourse;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.DomainRepository;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.TaxonomyCourseRepository;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.SubdomainRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
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
	
	@Autowired
	private PartyService partyService; 
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<Subdomain> createSubdomain(Subdomain subdomain, User user) {
		final Errors errors = validateSubdomain(subdomain);
		TaxonomyCourse Taxonomycourse = this.getTaxonomyCourseRepository().getCourse(subdomain.getTaxonomyCourseId());
		rejectIfNull(Taxonomycourse, GL0006, 404, COURSE);
		Domain domain = this.getDomainRepository().getDomain(subdomain.getDomainId());
		rejectIfNull(domain, GL0006, 404, DOMAIN_);
		if (!errors.hasErrors()) {
			subdomain.setTaxonomyCourse(Taxonomycourse);
			subdomain.setDomain(domain);
			subdomain.setCreatedOn(new Date(System.currentTimeMillis()));
			this.getSubdomainRepository().save(subdomain);
		}
		return new ActionResponseDTO<Subdomain>(subdomain, errors);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getSubdomain(Integer subdomainId) {
		Map<String, Object>  subdomain = this.getSubdomainRepository().getSubdomain(subdomainId);
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
		Map<String, Object>   subdomain = this.getSubdomainRepository().getSubdomain(subdomainId);
		rejectIfNull(subdomain, GL0056, 404, SUBDOMAIN);
		this.getSubdomainRepository().remove(subdomain);
	}

	@Override
	public List<Map<String, Object>> getSubdomainStandards(Integer subdomainId, User user) {
		//Need to check it with userGroupService to get rootNodeId
		String root =  getRootNodeId(user);
		String[] rootNodeId = root.split(COMMA);
		List<Map<String, Object>> codes = this.getSubdomainRepository().getSubdomainStandards(subdomainId, rootNodeId);
		if (codes != null) {
			for (Map<String, Object> code : codes) {
				code.put(NODE, getStandards(((Number) code.get(CODE_ID)).intValue(), rootNodeId));
			}
		}
		return codes;
	}
	
	private String getRootNodeId(User user){
		PartyCustomField partyCustomFieldTax = partyService.getPartyCustomeField(user.getPartyUid(), USER_TAXONOMY_ROOT_CODE, null);
		if (partyCustomFieldTax != null) {
			return partyCustomFieldTax.getOptionalValue();
		}  else  {
			return this.taxonomyRespository.getFindTaxonomyList(settingService.getConfigSetting(ConfigConstants.GOORU_EXCLUDE_TAXONOMY_PREFERENCE,0, user.getOrganization().getPartyUid()));
 		}
	}

	private List<Map<String, Object>> getStandards(Integer codeId, String[] rootNodeId) {
		List<Map<String, Object>> codes = this.getSubdomainRepository().getStandards(codeId , rootNodeId);
		for (Map<String, Object> code : codes) {
			code.put(NODE, this.getSubdomainRepository().getStandards(((Number) code.get(CODE_ID)).intValue(), rootNodeId));
		}
		return codes;
	}

	private Errors validateSubdomain(Subdomain subdomain) {
		final Errors errors = new BindException(subdomain, SUBDOMAIN);
		rejectIfNull(errors, subdomain.getTaxonomyCourseId(), COURSE_ID, generateErrorMessage(GL0006, COURSE_ID));
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
