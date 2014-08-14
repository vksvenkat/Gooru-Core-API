/////////////////////////////////////////////////////////////
// OrganizationService.java
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
package org.ednovo.gooru.domain.service.party;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationSetting;
import org.ednovo.gooru.core.api.model.User;

public interface OrganizationService {

	Organization getOrganizationById(String organizationUid);

	Organization getOrganizationByCode(String organizationCode);

	List<Organization> listAllOrganizations();

	Organization getOrganizationByName(String partyName);
	
	ActionResponseDTO<Organization> saveOrganization(Organization organization, User user, HttpServletRequest request);

	ActionResponseDTO<Organization> updateOrganization(Organization organization, String existingOrganizationUid, User apiCaller) throws Exception;
	
	ActionResponseDTO<OrganizationSetting> saveOrUpdateOrganizationSetting(String organizationUid, OrganizationSetting organizationSetting) throws Exception;
	
	User updateUserOrganization(String orgnaizationUid, String gooruUid) throws Exception;

	OrganizationSetting getOrganizationSetting(String organizationUid, String key) throws Exception;
	
	Organization getOrganizationByIdpName(String idpDomainName);
}
