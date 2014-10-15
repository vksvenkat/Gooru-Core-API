/////////////////////////////////////////////////////////////
// ApplicationServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
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
package org.ednovo.gooru.domain.service.apikey;

import java.sql.Date;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.party.OrganizationService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apikey.ApplicationRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class ApplicationServiceImpl extends BaseServiceImpl implements ApplicationService, ParameterProperties, ConstantProperties {

	@Autowired
	private ApplicationRepository applicatioRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public ActionResponseDTO<Application> createApplication(Application application, User apiCaller) {
		final Errors errors = validateCreateApplication(application);
		if (!errors.hasErrors()) {
			application.setGooruOid(UUID.randomUUID().toString());
			application.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
			application.setApiKey(UUID.randomUUID().toString().replaceAll("-", ""));
			if (application.getStatus() != null && application.getStatus().getValue() != null) {
				CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), application.getStatus().getValue());
				rejectIfNull(status, GL0007, " application status ");
				application.setStatus(status);
			} else { 
				CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), CustomProperties.ApplicationStatus.ACTIVE.getApplicationStatus());
				application.setStatus(status);
			}
			rejectIfNull(application.getOrganization(), GL0006, "Organization ");
			rejectIfNull(application.getOrganization().getPartyUid(), GL0006, "Organization ");
			rejectIfNull(this.getOrganizationService().getOrganizationById(application.getOrganization().getPartyUid()), GL0007, "Organization ");
			application.setContentType((ContentType) this.getApplicationRepository().get(ContentType.class, RESOURCE));
			application.setResourceType((ResourceType) this.getApplicationRepository().get(ResourceType.class, ResourceType.Type.APPLICATION.getType()));
			application.setLastModified(new Date(System.currentTimeMillis()));
			application.setCreatedOn(new Date(System.currentTimeMillis()));
			application.setUser(apiCaller);
			application.setOrganization(apiCaller.getPrimaryOrganization());
			application.setIsFeatured(0);
			application.setCreator(apiCaller);
			application.setRecordSource(NOT_ADDED);
			application.setLastUpdatedUserUid(apiCaller.getGooruUId());
			application.setSharing(Sharing.PRIVATE.getSharing());
			this.getApplicationRepository().save(application);
		}
		return new ActionResponseDTO<Application>(application, errors);
	}

	@Override
	public Application updateApplication(Application newapplication, String apiKey) {
		Application application = this.getApplicationRepository().getApplication(apiKey);
		rejectIfNull(application, GL0056, 404, "Application ");
		if (newapplication.getTitle() != null) {
			application.setTitle(newapplication.getTitle());
		}
		if (newapplication.getDescription() != null) {
			application.setDescription(newapplication.getDescription());
		}
		if (newapplication.getUrl() != null) {
			application.setUrl(newapplication.getUrl());
		}
		if (newapplication.getComment() != null) {
			application.setComment(newapplication.getComment());
		}

		if (newapplication.getStatus() != null && newapplication.getStatus().getValue() != null) {
			CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(CustomProperties.Table.APPLICATION_STATUS.getTable(), application.getStatus().getValue());
			rejectIfNull(status, GL0007, " application status ");
			application.setStatus(status);
		}
		this.getApplicationRepository().save(application);
		return application;
	}

	@Override
	public Application getApplication(String apiKey) {
		return this.getApplicationRepository().getApplication(apiKey);
	}

	@Override
	public SearchResults<Application> getApplications(String organizationUid, Integer limit, Integer offset) {
		SearchResults<Application> result = new SearchResults<Application>();
		result.setSearchResults(this.getApplicationRepository().getApplications(organizationUid, offset, limit));
		result.setTotalHitCount(this.getApplicationRepository().getApplicationCount(organizationUid));
		return result;
	}

	@Override
	public void deleteApplication(String apiKey) {
		Application application = this.getApplicationRepository().getApplication(apiKey);
		rejectIfNull(application, GL0056, 404, "Application ");
		this.getApplicationRepository().remove(application);
	}

	private Errors validateCreateApplication(Application application) {
		final Errors errors = new BindException(application, "application");
		rejectIfNull(errors, application, TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		return errors;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public ApplicationRepository getApplicationRepository() {
		return applicatioRepository;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

}
