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
import java.util.List;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.ApplicationItem;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.OAuthClient;
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
import org.ednovo.gooru.infrastructure.persistence.hibernate.auth.OAuthRepository;
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

	@Autowired
	private OAuthRepository oAuthRepository;
	
	@Override
	public ActionResponseDTO<Application> createApplication(Application application, User apiCaller) {
		final Errors errors = validateCreateApplication(application);
		if (!errors.hasErrors()) {
			application.setGooruOid(UUID.randomUUID().toString());
			application.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
			application.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
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
		if (newapplication.getContactEmailId() != null) {
			application.setContactEmailId(newapplication.getContactEmailId());
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
		Application application = this.getApplicationRepository().getApplication(apiKey);
		application.setApplicationItems(this.getApplicationRepository().getApplicationItemByApiKey(apiKey));
		application.setOauthClients(oAuthRepository.findOAuthClientByApplicationKey(apiKey));
		return application;
	}

	@Override
	public SearchResults<Application> getApplications(String organizationUid,String gooruUid, Integer limit, Integer offset) {
		SearchResults<Application> result = new SearchResults<Application>();
		result.setSearchResults(this.getApplicationRepository().getApplications(organizationUid,gooruUid, offset, limit));
		result.setTotalHitCount(this.getApplicationRepository().getApplicationCount(organizationUid, gooruUid));
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
	@Override
	public ActionResponseDTO<ApplicationItem> createApplicationItem(ApplicationItem applicationItem,String apiKey, User apiCaller) {
		final Errors errors = validateCreateApplicationItem(applicationItem);
		if (!errors.hasErrors()) {
			rejectIfNull(applicationItem.getApplication(), GL0006, "Application key ");
			rejectIfNull(applicationItem.getApplication().getKey(), GL0006, "Application key ");
			Application application = this.getApplicationRepository().getApplication(apiKey);
			rejectIfNull(application, GL0007, "Application key ");
			applicationItem.setApplication(application);
			this.getApplicationRepository().save(applicationItem);
		}
		return new ActionResponseDTO<ApplicationItem>(applicationItem, errors);
	}
	
	@Override
	public ActionResponseDTO<ApplicationItem>  updateApplicationItem(ApplicationItem newApplicationItem, String applicationItemId, User apiCaller) throws Exception {
		ApplicationItem applicationItem = this.getApplicationRepository().getApplicationItem(applicationItemId);
		final Errors errors = validateUpdateApplicationItem(applicationItem);
		rejectIfNull(applicationItem, GL0056, 404, "ApplicationItem ");
		if (newApplicationItem.getUrl() != null) {
			applicationItem.setUrl(newApplicationItem.getUrl());
		}
		if (newApplicationItem.getDisplayName() != null) {
			applicationItem.setDisplayName(newApplicationItem.getDisplayName());
		}
		if (newApplicationItem.getDisplaySequence() != null) {
			applicationItem.setDisplaySequence(newApplicationItem.getDisplaySequence());
		}
		this.getApplicationRepository().save(applicationItem);
		return new ActionResponseDTO<ApplicationItem>(applicationItem, errors);
	}
	
	private Errors validateUpdateApplicationItem(ApplicationItem applicationItem) throws Exception {
		final Errors errors = new BindException(applicationItem, APPLICATION_ITEM);
		rejectIfNull(errors, applicationItem, APPLICATION_ITEM, GL0056, generateErrorMessage(GL0056, APPLICATION_ITEM));
		return errors;
	}
	
	@Override
	public ApplicationItem getApplicationItem(String applicationItemId) {
		return this.getApplicationRepository().getApplicationItem(applicationItemId);
	}
	
	private Errors validateCreateApplicationItem(ApplicationItem applicationItem) {
		final Errors errors = new BindException(applicationItem, "applicationItem");
		rejectIfNull(errors, applicationItem, APPLICATION_URL, GL0006, generateErrorMessage(GL0006, APPLICATION_URL));
		return errors;
	}
	

	@Override
	public List<ApplicationItem> getApplicationItemByApiKey(String apiKey) throws Exception {
		return this.getApplicationRepository().getApplicationItemByApiKey(apiKey);
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
	
	public OAuthRepository getOAuthRepository() {
		return oAuthRepository;
	}

	

}
